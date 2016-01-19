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

import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class Regularizer
{
	protected float rate;

	public Regularizer(float rate)
	{
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
	
	public abstract void setWeightVector(WeightVector vector);
	
	/** Expands the dimension of necessary vectors with respect to the weight vector. */
	public abstract void expand(int sparseFeatureSize, int denseFeatureSize, int labelSize);
	
	/** Updates the index'th weight of the weight vector with respect to the regularization. */
	public abstract void updateWeight(int index, float gradient, float learningRate, int steps, boolean sparse);
}
