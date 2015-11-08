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
package edu.emory.mathcs.nlp.learning.optimization.method;

import edu.emory.mathcs.nlp.learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.learning.optimization.StochasticGradientDescent;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SoftmaxRegression extends StochasticGradientDescent
{
	public SoftmaxRegression(WeightVector vector, float learningRate)
	{
		super(vector, learningRate);
	}
	
	@Override
	public void trainAux(SparseInstance instance)
	{
		float[] gradients = getGradientsRegression(instance);
		trainRegression(instance, gradients);
	}
	
	@Override
	protected int getPredictedLabel(SparseInstance instance)
	{
		return getPredictedLabelRegression(instance);
	}
	
	@Override
	protected float getLearningRate(int index)
	{
		return learning_rate;
	}
	
	@Override
	public String toString()
	{
		return toString("Logistic Regression");
	}
}
