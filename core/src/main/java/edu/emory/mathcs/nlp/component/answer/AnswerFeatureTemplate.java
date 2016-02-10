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

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
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
//		NLPNode[] answers  = state.getNodes();
//		NLPNode[] question = state.getQuestion();
		SparseVector x = new SparseVector();
//		int input = state.getInputIndex();
//		int type;
//		
//		type = 0;
//		add(x, type, s, 1, isTrain);

		return x;
	}
	
	
	@Override
	public float[] createDenseVector(AnswerState state)
	{
		return null;
	}
}
