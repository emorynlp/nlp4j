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
public class RegularizedDualAveraging extends Regularizer
{
	private WeightVector cumulative_penalty;
	
	public RegularizedDualAveraging(WeightVector vector, float rate)
	{
		super(vector, rate);
		cumulative_penalty = vector.createEmptyVector();
	}
	
	@Override
	public void expand()
	{
		cumulative_penalty.expand(weight_vector.getLabelSize(), weight_vector.getFeatureSize());
	}
	
	@Override
	public void updateWeight(int index, float gradient, float learningRate, int steps)
 	{
		cumulative_penalty.add(index, gradient);
		
		float penalty = cumulative_penalty.get(index);
		float l1 = rate * steps;
		float value;
		
		if (Math.abs(penalty) <= l1)
			value = 0;
		else
			value = learningRate * (penalty - Math.signum(penalty) * l1);
		
		weight_vector.set(index, value);
 	}
}
