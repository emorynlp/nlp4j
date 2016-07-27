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

import edu.emory.mathcs.nlp.learning.optimization.StochasticGradientDescent;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Perceptron extends StochasticGradientDescent
{
	private static final long serialVersionUID = 4996609767585176672L;

	public Perceptron(WeightVector vector, float learningRate, float bias)
	{
		super(vector, learningRate, bias);
	}
	
	@Override
	public void trainAux(Instance instance)
	{
		trainClassification(instance);
	}
	
	@Override
	protected int getPredictedLabel(Instance instance)
	{
		float[] scores = instance.getScores();
		return argmax(scores);
	}
	
	@Override
	protected float getLearningRate(int index, boolean sparse)
	{
		return learning_rate;
	}
	
	@Override
	public void updateMiniBatch() {}

	@Override
	public String toString()
	{
		return "Perceptron";
	}
}
