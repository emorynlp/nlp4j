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
package edu.emory.mathcs.nlp.component.template.lexicon;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;

/**
 * This class is a holder for data resources shared across multiple components.
 * Each of these items is usually stored as a {@link java.io.Serializable} object
 * written with {@link java.io.ObjectOutputStream}, but an application might choose
 * to make other arrangements. If the caller does not choose to initialize from an XML
 * descriptor, but rather to call the various {@code set} methods, the caller must call
 * {@link #getGlobalLexicon(InputStream, Field, String)} to wrap the object in a
 * {@link NLPLexicon}.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPLexiconMapper<N extends AbstractNLPNode<N>> implements NLPComponent<N>, Serializable
{
	private static final long serialVersionUID = -8225747739145834063L;
	private static final Logger LOG = LoggerFactory.getLogger(NLPLexiconMapper.class);
	static final public String LEXICA = "lexica";
	static final public String FIELD  = "field";
	protected Map<Field, Object> lexicons;
	
//	=================================== INITIALIZATION ===================================

	/**
	 * Initialize the lexica with no contents.
	 * Call this if you plan to call the various 'set' methods for yourself.
	 */
	public NLPLexiconMapper()
	{
		lexicons = new HashMap<>();
	}
	
	/**
	 * Initialize from an XML decoder descriptor read from a stream.
	 * @param in configuration xml.
	 */
	public NLPLexiconMapper(InputStream in)
	{
		this(XMLUtils.getDocumentElement(in));
	}

	/**
	 * Initialize from an XML decoder descriptor represented as an XML DOM.
	 * @param doc the document object of the DOM.
	 */
	public NLPLexiconMapper(Element doc)
	{
		Element eLexica = XMLUtils.getFirstElementByTagName(doc, LEXICA);
		setLexicons(eLexica);
	}
	
	protected void setLexicons(Element eLexica)
	{
		lexicons = new HashMap<>();
		
		if (eLexica != null)
		{
			putLexicon(getLexicon(eLexica, "ambiguity_classes"      , "Loading ambiguity classes"));
			putLexicon(getLexicon(eLexica, "word_clusters"          , "Loading word clusters"));
			putLexicon(getLexicon(eLexica, "named_entity_gazetteers", "Loading named entity gazetteers"));
		}
	}
	
	protected <T>Pair<Field, T> getLexicon(Element eLexica, String tag, String message)
	{
		return getLexicon(XMLUtils.getFirstElementByTagName(eLexica, tag), message);
	}
	
	@SuppressWarnings("unchecked")
	protected <T>Pair<Field, T> getLexicon(Element element, String message)
	{
		if (element == null) return null;
		LOG.info(message);
		
		String path = XMLUtils.getTrimmedTextContent(element);

		Field field = Field.valueOf(XMLUtils.getTrimmedAttribute(element, FIELD));
		T lexicon = null;
		
		try (ObjectInputStream oin = IOUtils.createArtifactObjectInputStream(path))
		{
			lexicon = (T)oin.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new Pair<>(field, lexicon);
	}

//	=================================== GETTER/SETTER ===================================
	
	@SuppressWarnings("unchecked")
	public <T>T getLexicon(Field field)
	{
		return (T)lexicons.get(field);
	}
	
	public <T>void putLexicon(Field field, T lexicon)
	{
		lexicons.put(field, lexicon);
	}
	
	protected <T>void putLexicon(Pair<Field, T> p)
	{
		lexicons.put(p.o1, p.o2);
	}
	
//	=================================== PROCESS ===================================
	
	@Override
	public void process(List<N> nodes)
	{
		for (int i=1; i<nodes.size(); i++)
		{
			N node = nodes.get(i);
			
			for (Entry<Field, Object> e : lexicons.entrySet())
				node.addLexicon(e.getKey(), e.getValue());
		}
	}
}
