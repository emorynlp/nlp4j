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
package edu.emory.mathcs.nlp.component.ner;

import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NERFeatureTemplate extends FeatureTemplate<NERState>
{
	private static final long serialVersionUID = 2750773840515707758L;
	
	public NERFeatureTemplate(int dynamicFeatureSize, int embeddingWindowLeft, int embeddingWindowRight)
	{
		super(dynamicFeatureSize, embeddingWindowLeft, embeddingWindowRight);
	}
	
	@Override
	public float[] createDenseVector(NERState state)
	{
		final int beginIndex = -4, endIndex = 4;
		float[] w, v = null;
		NLPNode node;
		
		for (int i=beginIndex,j=0; i<=endIndex; i++,j++)
		{
			node = state.getNode(state.getInputIndex(), i);
			
			if (node != null && node.hasWordEmbedding())
			{
				w = node.getWordEmbedding();
				if (v == null) v = new float[w.length * (endIndex-beginIndex+1)];
				System.arraycopy(w, 0, v, w.length*j, w.length);
			}
		}
		
		return v;
	}
}
