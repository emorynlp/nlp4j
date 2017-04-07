/**
 * Copyright 2017, Emory University
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
package edu.emory.mathcs.nlp.component.pos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.component.template.lexicon.NLPLexicon;
import edu.emory.mathcs.nlp.component.template.lexicon.NLPLexiconMapper;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSLexiconMapper<N extends AbstractNLPNode<N>> extends NLPLexiconMapper<N>
{
	protected NLPLexicon<Map<String, List<String>>> ambiguity_classes;
	protected NLPLexicon<Map<String, Set<String>>>  word_clusters;
	
	@Override
	public void init(Element eLexica)
	{
		setAmbiguityClasses(getLexicon(eLexica, "ambiguity_classes", "Loading ambiguity classes"));
		setWordClusters    (getLexicon(eLexica, "word_clusters"    , "Loading word clusters"));
	}
	
//	=================================== GETTERS/SETTERS ===================================
	
	public NLPLexicon<Map<String,List<String>>> getAmbiguityClasses()
	{
		return ambiguity_classes;
	}
	
	public void setAmbiguityClasses(NLPLexicon<Map<String,List<String>>> classes)
	{
		ambiguity_classes = classes;
	}
	
	public NLPLexicon<Map<String,Set<String>>> getWordClusters()
	{
		return word_clusters;
	}
	
	public void setWordClusters(NLPLexicon<Map<String,Set<String>>> p)
	{
		word_clusters = p;
	}
}
