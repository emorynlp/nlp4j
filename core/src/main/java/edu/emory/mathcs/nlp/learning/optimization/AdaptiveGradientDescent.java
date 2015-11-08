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
package edu.emory.mathcs.nlp.learning.optimization;

import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AdaptiveGradientDescent extends StochasticGradientDescent
{
	protected final float epsilon = 0.00001f;
	protected WeightVector diagonals;
	
	public AdaptiveGradientDescent(WeightVector vector, float learningRate)
	{
		this(vector, learningRate, null);
	}
	
	public AdaptiveGradientDescent(WeightVector vector, float learningRate, Regularizer rda)
	{
		super(vector, learningRate, rda);
		diagonals = weight_vector.createEmptyVector();
	}
	
	@Override
	public void expand(int labelSize, int featureSize)
	{
		super.expand(labelSize, featureSize);
		diagonals.expand(labelSize, featureSize);
	}
	
	@Override
	protected float getLearningRate(int index)
	{
		return learning_rate / (epsilon + (float)Math.sqrt(diagonals.get(index)));
	}
}
