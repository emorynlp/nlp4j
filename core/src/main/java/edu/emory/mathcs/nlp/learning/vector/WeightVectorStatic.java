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
package edu.emory.mathcs.nlp.learning.vector;

import java.util.Arrays;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WeightVectorStatic extends WeightVector
{
	private static final long serialVersionUID = -841242941248961003L;
	private float[] weights;
	
//	=================================== CONSTRUCTORS ===================================

	public WeightVectorStatic(int labelSize, int featureSize)
	{
		this(labelSize, featureSize, null);
	}
	
	public WeightVectorStatic(int labelSize, int featureSize, ActivationFunction function)
	{
		super(labelSize, featureSize, function);
		int size = labelSize * featureSize;
		weights = new float[size];
	}

	@Override
	public WeightVectorStatic createEmptyVector()
	{
		return new WeightVectorStatic(label_size, feature_size, activation_function);
	}
	
//	=================================== OPERATIONS ===================================

	@Override
	public int size()
	{
		return weights.length;
	}
	
	@Override
	public float get(int index)
	{
		return weights[index];
	}
	
	@Override
	public void set(int index, float value)
	{
		weights[index] = value;
	}
	
//	=================================== INITIALIZATION ===================================
	
	@Override
	public void expand(int labelSize, int featureSize) {}
	
	@Override
	public String toString()
	{
		return Arrays.toString(weights);
	}
}
