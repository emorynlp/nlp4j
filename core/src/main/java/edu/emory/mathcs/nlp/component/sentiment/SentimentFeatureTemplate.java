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

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.component.template.feature.DocumentFeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentFeatureTemplate extends DocumentFeatureTemplate<SentimentState>
{
	private static final long serialVersionUID = -5155132966568754061L;

	public SentimentFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}

	@Override
	public float[] createDenseVector(SentimentState state)
	{
		float[] sum = state.getDocument().get(0)[1].getSentimentScores();
		if (sum == null) return null;
		int[] count = new int[sum.length];
		sum = new float[sum.length];
		boolean first = true;
		float[] scores;
		
		for (NLPNode[] nodes : state.getDocument())
		{
			for (int i=1; i<nodes.length; i++)
			{
				if (first) {first = false; continue;}
				scores = nodes[i].getSentimentScores();
				
				for (int j=0; j<scores.length; j++)
				{
					if (scores[j] != 0)
					{
						sum[j] += scores[j];
						count[j]++;
					}
				}
			}
		}
		
		for (int j=0; j<sum.length; j++)
			sum[j] = count[j] == 0 ? 0 : sum[j] / count[j];
		
		float[] v = getEmbeddingFeaturess(state);
		scores = new float[v != null ? sum.length+v.length : sum.length];
		
		int des = 0;
		System.arraycopy(sum, 0, scores, des, sum.length);
		
		if (v != null)
		{
			des += sum.length;
			System.arraycopy(v, 0, scores, des, v.length);
		}
		
		return scores;
	}
	
	public float[] getEmbeddingFeaturess(SentimentState state)
	{
		float[] w, v = null;
		int count = 0;
		NLPNode node;
		
		for (NLPNode[] nodes : state.getDocument())
		{
			for (int i=1; i<nodes.length; i++)
			{
				node = nodes[i];
				
				if (!node.isStopWord() && node.hasWordEmbedding())
				{
					w = node.getWordEmbedding();
					if (v == null) v = new float[w.length];
					MathUtils.add(v, w);
					count++;
				}
			}
		}
		
		if (v != null)
		{
			for (int i=0; i<v.length; i++)
				v[i] /= count;
		}

		return v;
	}
}
