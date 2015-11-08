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

import java.io.Serializable;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class WeightVector implements Serializable
{
	private static final long serialVersionUID = 5876902100282177639L;
	protected ActivationFunction activation_function;
	protected int feature_size;
	protected int label_size;
	
//	=================================== CONSTRUCTORS ===================================

	public WeightVector(int labelSize, int featureSize, ActivationFunction function)
	{
		setSizes(labelSize, featureSize);
		setActivationFunction(function);
	}
	
	/** @return a vector whose size is the same as this vector but the values are initialized to 0. */
	public abstract WeightVector createEmptyVector();
	
//	=================================== INITIALIZATION ===================================
	
	/** Expands the size of this weight vector using the new label and feature sizes. */
	public abstract void expand(int labelSize, int featureSize);
	
//	=================================== GETTERS/SETTERS ===================================
	
	/** @return the total number of labels. */
	public int getLabelSize()
	{
		return label_size;
	}
	
	/** @return the total number of features. */
	public int getFeatureSize()
	{
		return feature_size;
	}
	
	public ActivationFunction getActivationFunction()
	{
		return activation_function;
	}
	
	public void setActivationFunction(ActivationFunction function)
	{
		activation_function = function;
	}
	
	protected void setSizes(int labelSize, int featureSize)
	{
		label_size   = labelSize;
		feature_size = featureSize;
	}
	
//	/** @return the weights of all labels and features. */
//	public FloatGapList getWeights()
//	{
//		return weight_list;
//	}
//	
//	/** Sets the weights of all labels and features. */
//	public void setWeights(FloatGapList weights)
//	{
//		weight_list = weights;
//	}
//	
//	public void setWeights(float[] weights)
//	{
//		if (weight_list.size() == weights.length)
//		{
//			for (int i=0; i<weights.length; i++)
//				set(i, weights[i]);	
//		}
//		else
//		{
//			weight_list.clear();
//			for (float f : weights) weight_list.add(f);
//		}
//	}
//	
//	/**
//	 * @return the weights for the specific label.
//	 * @param y the index of the label.
//	 */
//	public float[] getWeights(int y)
//	{
//		float[] w = new float[feature_size];
//		
//		for (int xi=0; xi<feature_size; xi++)
//			w[xi] = get(y, xi);
//		
//		return w;
//	}
//	
//	/**
//	 * Sets the weights of the specific label.
//	 * @param y the index of the lable.
//	 * @param weights the new weights.
//	 */
//	public void setWeights(int y, float[] weights)
//	{
//		for (int xi=0; xi<feature_size; xi++)
//			set(y, xi, weights[xi]);
//	}
	
//	=================================== OPERATIONS ===================================

	public abstract int size();
	public abstract float get(int index);
	public abstract void set(int index, float value);
	
	/**
	 * @return the weight of the specific label and feature.
	 * @param y the index of the label.
	 * @param xi the index of the feature.
	 */
	public float get(int y, int xi)
	{
		return get(indexOf(y, xi));
	}
	
	/**
	 * Sets the weight of the specific label and feature.
	 * @param y the index of the label.
	 * @param xi the index of the feature.
	 */
	public void set(int y, int xi, float value)
	{
		set(indexOf(y, xi), value);
	}
	
	public void add(int index, float value)
	{
		set(index, get(index) + value);
	}
	
	/**
	 * Adds the specific value to the weight of the specific label and feature.
	 * @param y the index of the label.
	 * @param xi the index of the feature.
	 */
	public void add(int y, int xi, float value)
	{
		add(indexOf(y, xi), value);
	}
	
	public void add(float value)
	{
		int i, size = size();
		for (i=0; i<size; i++) add(i, value);
	}
	
	public void multiply(int index, float value)
	{
		set(index, get(index) * value);
	}
	
	/**
	 * Multiplies the specific value to the weight of the specific label and feature.
	 * @param y the index of the label.
	 * @param xi the index of the feature.
	 */
	public void multiply(int y, int xi, float value)
	{
		multiply(indexOf(y, xi), value);
	}
	
	public void multiply(float value)
	{
		int i, size = size();
		for (i=0; i<size; i++) multiply(i, value);
	}
	
	/** Sets each dimension of this weight vector to the specific value. */
	public void fill(float value)
	{
		int i, size = size();
		for (i=0; i<size; i++) set(i, value);
	}
	
	public int countNonZeroWeights()
	{
		int count = 0, i, size = size();
		
		for (i=0; i<size; i++)
			if (get(i) != 0) count++;
		
		return count;
	}
	
//	=================================== SCORING ===================================
	
	/**
	 * @return the scores of all labels given the specific input.
	 * @param x the input vector.
	 */
	public float[] scores(SparseVector x)
	{
		float[] scores = new float[label_size];
		int i, index;
		
		for (SparseItem p : x)
		{
			if (p.getIndex() < feature_size)
			{
				index = indexOf(p.getIndex());
				
				for (i=0; i<label_size; i++)
					scores[i] += get(index+i) * p.getValue();	
			}
		}
		
		if (activation_function != null) activation_function.apply(scores);
		return scores;
	}
	
	public float[] scores(SparseVector x, int[] labels)
	{
		float[] scores = new float[label_size];
		int index;
		
		// for classification: make sure label scores are higher than non-label scores
		if (activation_function == null)
		{
			for (int i : labels)
				scores[i] = 10000f;
		}
		
		for (SparseItem p : x)
		{
			if (p.getIndex() < feature_size)
			{
				index = indexOf(p.getIndex());
				
				for (int i : labels)
					scores[i] += get(index+i) * p.getValue();	
			}
		}
		
		if (activation_function != null) activation_function.apply(scores);
		return scores;
	}
	
//	=================================== HELPERS ===================================

	/**
	 * @param y the index of the label.
	 * @param xi the index of the feature.
	 * @return the weight index of the specific label and feature.
	 */
	public int indexOf(int y, int xi)
	{
		return y + indexOf(xi);
	}
	
	private int indexOf(int xi)
	{
		return xi * label_size;
	}
}
