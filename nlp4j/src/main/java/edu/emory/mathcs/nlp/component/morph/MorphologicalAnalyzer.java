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
package edu.emory.mathcs.nlp.component.morph;

import java.util.List;

import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MorphologicalAnalyzer<N extends AbstractNLPNode<N>> implements NLPComponent<N>
{
	private MorphAnalyzer analyzer;
	
	public MorphologicalAnalyzer(Language language)
	{
		analyzer = new EnglishMorphAnalyzer();
	}

	@Override
	public void process(N[] nodes)
	{
		N node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			node.setLemma(analyzer.lemmatize(node.getWordFormSimplified(), node.getPartOfSpeechTag()));
		}
	}

	@Override
	public void process(List<N[]> document)
	{
		for (N[] nodes : document)
			process(nodes);
	}
}
