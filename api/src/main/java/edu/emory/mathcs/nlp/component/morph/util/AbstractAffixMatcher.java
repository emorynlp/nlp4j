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
package edu.emory.mathcs.nlp.component.morph.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractAffixMatcher
{
	protected String                      s_affixCanonicalForm;
	protected String                      s_affixPOS;
	protected Pattern                     p_originalPOS;
	protected List<AbstractAffixReplacer> l_replacers;
	
	public AbstractAffixMatcher(String affixCanonicalForm, String affixPOS, Pattern originalPOS)
	{
		init(affixCanonicalForm, affixPOS, originalPOS);
	}
	
	private void init(String affixCanonicalForm, String affixPOS, Pattern originalPOS)
	{
		s_affixCanonicalForm = affixCanonicalForm;
		s_affixPOS           = affixPOS;
		p_originalPOS        = originalPOS;
		l_replacers          = new ArrayList<>();
	}
	
	public boolean matchesOriginalPOS(String pos)
	{
		return p_originalPOS == null || p_originalPOS.matcher(pos).find();
	}
	
	public void addReplacer(AbstractAffixReplacer replacer)
	{
		l_replacers.add(replacer);
	}
	
	/** 
	 * Returns (BaseMorpheme, SuffixMorphem) if exists; otherwise, {@code null}.
	 * @param form the word-form in lower-case.
	 */
	abstract public String getBaseForm(Map<String,Set<String>> baseMap, String form, String pos);

	/** 
	 * Returns (BaseMorpheme, SuffixMorphem) if exists; otherwise, {@code null}.
	 * @param form the word-form in lower-case.
	 */
	abstract public String getBaseForm(Set<String> baseSet, String form, String pos);

	abstract public String getBaseForm(Set<String> baseSet, String lemma);
}
