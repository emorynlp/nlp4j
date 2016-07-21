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
package edu.emory.mathcs.nlp.learning.util;

import java.io.Serializable;

import org.magicwerk.brownies.collections.primitive.FloatGapList;

import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class MajorVector implements Serializable
{
	private static final long serialVersionUID = 4837958224356746566L;
	protected FloatGapList weights;
	protected int feature_size;
	protected int label_size;	
	
//	=================================== CONSTRUCTORS ===================================

	public MajorVector()
	{
		weights = new FloatGapList();
		setSizes(0, 0);
	}
	
//	=================================== GETTERS & SETTERS ===================================

	public int getFeatureSize()
	{
		return feature_size;
	}
	
	public int getLabelSize()
	{
		return label_size;
	}
	
//	=================================== EXPAND ===================================
	
	/**
	 * Expands the size of this weight vector using the new feature and label sizes.
	 * @return true if the size of this weight vector is expanded.
	 */
	public boolean expand(int labelSize, int featureSize)
	{
		return expand(labelSize, featureSize, null);
	}
	
	public abstract boolean expand(int labelSize, int featureSize, WeightGenerator generator);
	
	protected boolean expand(int oldRowSize, int oldColumnSize, int newRowSize, int newColumnSize, WeightGenerator generator)
	{
		if (newRowSize    < oldRowSize)    newRowSize    = oldRowSize;
		if (newColumnSize < oldColumnSize) newColumnSize = oldColumnSize;
		boolean expanded = false;
		
		// expand columns
		if (oldColumnSize < newColumnSize)
		{
			int i, j, diff = newColumnSize - oldColumnSize, size = oldRowSize * newColumnSize;
			
			for (i=oldColumnSize; i<size; i+=newColumnSize)
				for (j=0; j<diff; j++) weights.add(i+j, generator == null ? 0 : generator.next());
			
			setColumnSize(newColumnSize);
			expanded = true;
		}
		
		// expand label dimension
		if (oldRowSize < newRowSize)
		{
			int i, size = newRowSize * newColumnSize;
			for (i=weights.size(); i<size; i++) weights.add(generator == null ? 0 : generator.next());
			
			setRowSize(newRowSize);
			expanded = true;
		}
		
		return expanded;
	}
	
	protected abstract void setRowSize   (int size);
	protected abstract void setColumnSize(int size);
	
//	=================================== VECTOR OPERATIONS ===================================

	public float get(int index)
	{
		return weights.get(index);
	}

	public void set(int index, float value)
	{
		weights.set(index, value);
	}
	
	public void add(int index, float value)
	{
		set(index, get(index) + value);
	}
	
	public void add(float value)
	{
		for (int i=0; i<size(); i++) add(i, value);
	}
	
	public void multiply(int index, float value)
	{
		set(index, get(index) * value);
	}
	
	public void multiply(float value)
	{
		for (int i=0; i<size(); i++) multiply(i, value);
	}
	
	public void fill(float value)
	{
		for (int i=0; i<size(); i++) set(i, value);
	}
	
	public int size()
	{
		return weights.size();
	}
	
//	=================================== X/Y OPERATIONS ===================================

	/**
	 * @param y  the index of the label.
	 * @param xi the index of the feature.
	 * @return the weight index of the specific label and feature.
	 */
	public abstract int indexOf(int y, int xi);
	
	public float get(int y, int xi)
	{
		return get(indexOf(y, xi));
	}
	
	public void set(int y, int xi, float value)
	{
		set(indexOf(y, xi), value);
	}
	
	public void add(int y, int xi, float value)
	{
		add(indexOf(y, xi), value);
	}
	
	public void multiply(int y, int xi, float value)
	{
		multiply(indexOf(y, xi), value);
	}
	
//	=================================== SCORES ===================================

	public abstract void addScores(SparseVector x, float[] scores);
	public abstract void addScores(float[] x, float[] scores);
	
//	=================================== UTILITIES ===================================
	
	protected abstract MajorVector createInstance();
	
	/** @return a copy of this vector where all the values are initialized to 0. */
	public MajorVector createZeroVector()
	{
		MajorVector vector = createInstance();

		vector.setSizes(label_size, feature_size);
		vector.weights = new FloatGapList();

		for (int i=0; i<weights.size(); i++)
			vector.weights.add(0);
		
		return vector;
	}
	
	/** @return the number of non-zero weights in this vector. */
	public int countNonZeroWeights()
	{
		int count = 0;
		
		for (int i=0; i<size(); i++)
			if (get(i) != 0) count++;
		
		return count;
	}
	
	protected void setSizes(int labelSize, int featureSize)
	{
		feature_size = featureSize;
		label_size   = labelSize;
	}
	
	@Override
	public String toString()
	{
		return weights.toString();
	}
}
