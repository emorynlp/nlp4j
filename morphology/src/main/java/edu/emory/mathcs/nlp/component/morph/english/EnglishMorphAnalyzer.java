/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.emory.mathcs.nlp.component.morph.english;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.util.AbstractAffixMatcher;
import edu.emory.mathcs.nlp.component.util.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMorphAnalyzer<N extends NLPNode> extends MorphAnalyzer<N>
{
	final String ROOT = "edu/emory/mathcs/nlp/component/morph/english/";
	
	final String INFLECTION_SUFFIX		= ROOT + "inflection_suffix.xml";
	final String ABBREVIATOIN_RULE		= ROOT + "abbreviation.rule";
	final String CARDINAL_BASE			= ROOT + "cardinal.base";
	final String ORDINAL_BASE      		= ROOT + "ordinal.base";
	final String DERIVATION_SUFFIX_N2V	= ROOT + "derivation_suffix_n2v.xml";
	
	final String FIELD_DELIM   = StringConst.UNDERSCORE;
	final String VERB          = "verb";
	final String NOUN          = "noun";
	final String ADJECTIVE     = "adjective";
	final String ADVERB        = "adverb";
	final String EXT_BASE      = ".base";
	final String EXT_EXCEPTION = ".exc";
	
	final String VERB_POS      = "VB";
	final String NOUN_POS      = "NN";
	final String ADJECTIVE_POS = "JJ";
	final String ADVERB_POS    = "RB";

	private EnglishInflection inf_verb;
	private EnglishInflection inf_noun;
	private EnglishInflection inf_adjective;
	private EnglishInflection inf_adverb;
	private EnglishDerivation der_n2v;
	
	/** Abbreviation replacement rules */
	private Map<String,String> rule_abbreviation;
	private Set<String> base_cardinal;
	/** Ordinal base-forms */
	private Set<String> base_ordinal;
	
//	====================================== CONSTRUCTORS ======================================
	
	/** Constructs an English morphological analyzer from the dictionary in resource. */
	public EnglishMorphAnalyzer()
	{
		Element inflection = XMLUtils.getDocumentElement(IOUtils.getInputStreamsFromResource(INFLECTION_SUFFIX));
		Element derivationN2V = XMLUtils.getDocumentElement(IOUtils.getInputStreamsFromResource(DERIVATION_SUFFIX_N2V));
		
		try
		{
			inf_verb      = getInflectionRules(inflection, VERB     , VERB_POS);
			inf_noun      = getInflectionRules(inflection, NOUN     , NOUN_POS);
			inf_adjective = getInflectionRules(inflection, ADJECTIVE, ADJECTIVE_POS);
			inf_adverb    = getInflectionRules(inflection, ADVERB   , ADVERB_POS);
			
			der_n2v = getDerivationalRules(derivationN2V, NOUN);
			
			base_cardinal     = DSUtils.createStringHashSet(IOUtils.getInputStreamsFromResource(CARDINAL_BASE));
			base_ordinal      = DSUtils.createStringHashSet(IOUtils.getInputStreamsFromResource(ORDINAL_BASE));
			rule_abbreviation = getAbbreviationMap(IOUtils.getInputStreamsFromResource(ABBREVIATOIN_RULE));
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	/** Called by {@link #EnglishLemmatizer()}. */
	private EnglishInflection getInflectionRules(Element eInflection, String type, String basePOS) throws IOException
	{
		Element     eAffixes        = XMLUtils.getFirstElementByTagName(eInflection, type);
		InputStream baseStream      = IOUtils.getInputStreamsFromResource(ROOT + type + EXT_BASE);
		InputStream exceptionStream = IOUtils.getInputStreamsFromResource(ROOT + type + EXT_EXCEPTION);
		
		return getInflection(baseStream, exceptionStream, eAffixes, basePOS);
	}
	
	private EnglishInflection getInflection(InputStream baseStream, InputStream exceptionStream, Element eAffixes, String basePOS) throws IOException
	{
		Map<String,String> exceptionMap = (exceptionStream != null) ? DSUtils.createStringHashMap(exceptionStream, Splitter.T_SPACE) : null;
		List<AbstractAffixMatcher> affixMatchers = new EnglishAffixMatcherFactory().createAffixMatchers(eAffixes);
		Set<String> baseSet = DSUtils.createStringHashSet(baseStream);
		return new EnglishInflection(basePOS, baseSet, exceptionMap, affixMatchers);
	}
	
	/** Called by {@link #EnglishMPAnalyzer(ZipFile)}. */
	private EnglishDerivation getDerivationalRules(Element eDerivation, String type) throws IOException
	{
		Element eAffixes = XMLUtils.getFirstElementByTagName(eDerivation, type);
		return new EnglishDerivation(new EnglishAffixMatcherFactory().createAffixMatchers(eAffixes));
	}
	
	private Map<String,String> getAbbreviationMap(InputStream stream) throws IOException
	{
		BufferedReader fin = new BufferedReader(new InputStreamReader(stream));
		Map<String,String> map = new HashMap<>();
		String line, abbr, pos, key, base;
		String[] tmp;
		
		while ((line = fin.readLine()) != null)
		{
			tmp  = Splitter.splitSpace(line.trim());
			abbr = tmp[0];
			pos  = tmp[1];
			base = tmp[2];
			key  = abbr + FIELD_DELIM + pos;
			
			map.put(key, base);
		}
			
		return map;
	}
	
	@Override
	public String lemmatize(String simplifiedWordForm, String pos)
	{
		String lemma = StringUtils.toLowerCase(simplifiedWordForm), t;
		
		if ((t = getAbbreviation(lemma, pos)) != null || (t = getBaseFormFromInflection(lemma, pos)) != null)
			lemma = t;
		
		if      (isCardinal(lemma))	return MetaConst.CARDINAL;
		else if (isOrdinal (lemma))	return MetaConst.ORDINAL;
		
		return lemma;
	}
	
	/** Called by {@link #analyze(DEPNode)}. */
	private String getAbbreviation(String form, String pos)
	{
		String key = form + FIELD_DELIM + pos;
		return rule_abbreviation.get(key);
	}
	
	/** @param form the lower simplified word-form. */
	private String getBaseFormFromInflection(String form, String pos)
	{
		if (pos.startsWith(VERB_POS))
			return inf_verb.getBaseForm(form, pos);
			
		if (pos.startsWith(NOUN_POS))
			return inf_noun.getBaseForm(form, pos);
		
		if (pos.startsWith(ADJECTIVE_POS))
			return inf_adjective.getBaseForm(form, pos);
		
		if (pos.startsWith(ADVERB_POS))
			return inf_adverb.getBaseForm(form, pos);
			
		return null;
	}
	
	private boolean isCardinal(String lower)
	{
		return base_cardinal.contains(lower);
	}
	
	private boolean isOrdinal(String lower)
	{
		return lower.equals("0st") || lower.equals("0nd") || lower.equals("0rd") || lower.equals("0th") || base_ordinal.contains(lower);
	}
	
	public String toVerb(String lemma)
	{
		Set<String> verbSet = inf_verb.getBaseSet();
		return verbSet.contains(lemma) ? lemma : der_n2v.getBaseForm(null, null);
	}
}
