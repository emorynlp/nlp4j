/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.component.answer;

import java.util.Set;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;
import edu.emory.mathcs.nlp.learning.util.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AnswerFeatureTemplate extends FeatureTemplate<AnswerState>
{
	private static final long serialVersionUID = 7422000023884517980L;

	public AnswerFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}

	@Override
	public SparseVector createSparseVector(AnswerState state, boolean isTrain)
	{
		SparseVector x = new SparseVector();
		int type = 0;
		
		addOverlappingFeatures(state, x, type, isTrain);
		return x;
	}
	
	public void addOverlappingFeatures(AnswerState state, SparseVector x, int type, boolean isTrain)
	{
		// set of lemmas in the question
		Set<String> qlemmas = NLPUtils.getBagOfWords(state.getQuestion(), Field.lemma);
		int input = state.getInputIndex();
		NLPNode node;
		
		for (int i=-2; i<=2; i++)
		{
			node = state.getNode(input, i);
			
			if (node != null && qlemmas.contains(node.getLemma()))
				add(x, type, "o"+i, 1, isTrain);	
		}
	}
	
	@Override
	public float[] createDenseVector(AnswerState state)
	{
		return null;
	}
}
