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

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureVector implements Serializable
{
	private static final long serialVersionUID = -5213368916106070872L;
	private SparseVector sparse_vector;
	private float[]      dense_vector;
	
//	=================================== CONSTRUCTORS ===================================

	public FeatureVector() {}

	public FeatureVector(SparseVector sparseVector, float[] denseVector)
	{
		setSparseVector(sparseVector);
		setDenseVector (denseVector);
	}
	
	public FeatureVector(float[] denseVector, float bias)
	{
		setDenseVector(denseVector);
		setSparseVector(new SparseVector(bias));
	}
	
	public FeatureVector(SparseVector vector)
	{
		setSparseVector(vector);
	}
	
	public FeatureVector(float[] vector)
	{
		setDenseVector(vector);
	}
	
//	=================================== GETTERS & SETTERS ===================================

	public SparseVector getSparseVector()
	{
		return sparse_vector;
	}
	
	public float[] getDenseVector()
	{
		return dense_vector;
	}
	
	public void setSparseVector(SparseVector vector)
	{
		sparse_vector = vector;
	}
	
	public void setDenseVector(float[] vector)
	{
		dense_vector = vector;
	}
	
	public boolean hasSparseVector()
	{
		return sparse_vector != null;
	}
	
	public boolean hasDenseVector()
	{
		return dense_vector != null;
	}
	
//	=================================== STRING VECTOR ===================================	
	
//	private StringVector string_vector;
//	
//	public FeatureVector(StringVector stringVector, float[] denseVector)
//	{
//		setStringVector(stringVector);
//		setDenseVector (denseVector);
//	}
//	
//	public FeatureVector(StringVector vector)
//	{
//		setStringVector(vector);
//	}
//	
//	public StringVector getStringVector()
//	{
//		return string_vector;
//	}
//	
//	public void setStringVector(StringVector vector)
//	{
//		string_vector = vector;
//	}
//	
//	public boolean hasStringVector()
//	{
//		return string_vector != null;
//	}
}
