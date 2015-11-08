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
package edu.emory.mathcs.nlp.learning.optimization.reguralization;

import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class Regularizer
{
	protected WeightVector weight_vector;
//	protected IntSet retired;
	protected float rate;

	public Regularizer(WeightVector vector, float rate)
	{
//		retired = new IntOpenHashSet();
		weight_vector = vector;
		setRate(rate);
	}
	
	public float getRate()
	{
		return rate;
	}

	public void setRate(float rate)
	{
		this.rate = rate;
	}
	
	/** Expands the dimension of necessary vectors with respect to the weight vector. */
	public abstract void expand();
	
	/** Updates the index'th weight of the weight vector with respect to the regularization. */
	public abstract void updateWeight(int index, float gradient, float learningRate, int steps);
	
//	public void retire(float threshold)
//	{
//		int i, j, k, labelSize = weight_vector.getLabelSize(), featureSize = weight_vector.getFeatureSize();
//		float min, max;
//		
//		for (i=1; i<featureSize; i++)
//		{
//			k = i * labelSize;
//			max = weight_vector.get(k);
//			min = weight_vector.get(k);
//			
//			for (j=1; j<labelSize; j++)
//			{
//				max = Math.max(max, weight_vector.get(k+j));
//				min = Math.min(min, weight_vector.get(k+j));
//			}
//			
//			if (Math.abs(max - min) < threshold)
//			{
//				for (j=0; j<labelSize; j++) weight_vector.set(k+j, 0);
//				retired.add(i);
//			}
//		}
//	}
}
