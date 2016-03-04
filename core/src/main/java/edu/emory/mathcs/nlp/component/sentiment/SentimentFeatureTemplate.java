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
package edu.emory.mathcs.nlp.component.sentiment;

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
public class SentimentFeatureTemplate extends FeatureTemplate<SentimentState>
{
	private static final long serialVersionUID = -5155132966568754061L;

	public SentimentFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}

	@Override
	public SparseVector createSparseVector(SentimentState state, boolean isTrain)
	{
		SparseVector x = new SparseVector();
		NLPNode[] nodes = state.getNodes();
		int type;
		
		type = 0;
		for (String s : NLPUtils.getUnigramSet(nodes, Field.lemma))
			add(x, type, s, 1, isTrain);
		
				
//		type++;
//		for (String s : NLPUtils.getBagOfWords(nodes, Field.lemma, Field.part_of_speech_tag))
//			add(x, type, s, 1, isTrain);

		return x;
	}
	
	@Override
	public float[] createDenseVector(SentimentState state)
	{
		return null;
	}
}
