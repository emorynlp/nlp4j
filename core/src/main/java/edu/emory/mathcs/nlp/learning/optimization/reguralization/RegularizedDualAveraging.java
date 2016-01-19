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

import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RegularizedDualAveraging extends Regularizer
{
	private WeightVector weight_vector;
	private WeightVector cumulative_penalty;
	
	public RegularizedDualAveraging(float rate)
	{
		super(rate);
	}
	
	@Override
	public void setWeightVector(WeightVector vector)
	{
		weight_vector = vector;
		cumulative_penalty = vector.createZeroVector();
	}
	
	@Override
	public void expand(int sparseFeatureSize, int denseFeatureSize, int labelSize)
	{
		cumulative_penalty.expand(sparseFeatureSize, denseFeatureSize, labelSize);
	}
	
	@Override
	public void updateWeight(int index, float gradient, float learningRate, int steps, boolean sparse)
 	{
		MajorVector cum = cumulative_penalty.getMajorVector(sparse);
		cum.add(index, gradient);
		
		float penalty = cum.get(index);
		float l1 = rate * steps;
		float value;
		
		if (Math.abs(penalty) <= l1)
			value = 0;
		else
			value = learningRate * (penalty - Math.signum(penalty) * l1);
		
		weight_vector.getMajorVector(sparse).set(index, value);
 	}
	
	@Override
	public String toString()
	{
		return String.format("RDA: %s", rate);
	}
}
