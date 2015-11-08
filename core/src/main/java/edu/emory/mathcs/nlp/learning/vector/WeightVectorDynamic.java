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

import org.magicwerk.brownies.collections.primitive.FloatGapList;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WeightVectorDynamic extends WeightVector
{
	private static final long serialVersionUID = 8705317710858367422L;
	private FloatGapList weights;
	
//	=================================== CONSTRUCTORS ===================================

	public WeightVectorDynamic()
	{
		this(null);
	}
	
	public WeightVectorDynamic(ActivationFunction function)
	{
		this(0, 0, function);
	}
	
	public WeightVectorDynamic(int labelSize, int featureSize)
	{
		this(labelSize, featureSize, null);
	}
	
	public WeightVectorDynamic(int labelSize, int featureSize, ActivationFunction function)
	{
		super(labelSize, featureSize, function);
		int size = labelSize * featureSize;
		weights = new FloatGapList();
		for (int i=0; i<size; i++) weights.add(0);
	}

	@Override
	public WeightVectorDynamic createEmptyVector()
	{
		return new WeightVectorDynamic(label_size, feature_size, activation_function);
	}
	
//	=================================== OPERATIONS ===================================

	@Override
	public int size()
	{
		return weights.size();
	}
	
	@Override
	public float get(int index)
	{
		return weights.get(index);
	}
	
	@Override
	public void set(int index, float value)
	{
		weights.set(index, value);
	}
	
//	=================================== INITIALIZATION ===================================
	
	@Override
	public void expand(int labelSize, int featureSize)
	{
		expandLabels  (labelSize);
		expandFeatures(featureSize);
	}
	
	/** Expands the size of this weight vector using the new label size. */
	public void expandLabels(int labelSize)
	{
		if (labelSize <= label_size) return;
		int i, j, diff = labelSize - label_size, size = labelSize * feature_size;
		
		for (i=label_size; i<size; i+=labelSize)
			for (j=0; j<diff; j++)
				weights.add(i+j, 0);
		
		label_size = labelSize;
	}
	
	/** Expands the size of this weight vector using the new feature size. */
	public void expandFeatures(int featureSize) 
	{
		if (featureSize <= feature_size) return;
		int i, size = label_size * (featureSize - feature_size);
		for (i=0; i<size; i++) weights.add(0);
		feature_size = featureSize;
	}
	
	@Override
	public String toString()
	{
		return weights.toString();
	}
}
