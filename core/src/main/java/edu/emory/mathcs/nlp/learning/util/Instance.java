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
public class Instance implements Serializable
{
	private static final long serialVersionUID = 7998185354380065988L;
	private FeatureVector vector;
	private float[]       scores;
	private String        string_label;
	private int           gold_label;
	private int           predicted_label;
	
//	=================================== CONSTRUCTORS ===================================

	public Instance(String label, FeatureVector vector)
	{
		setStringLabel(label);
		setFeatureVector(vector);
	}
	
	public Instance(String label, SparseVector sparseVector, float[] denseVector)
	{
		setStringLabel(label);
		setFeatureVector(new FeatureVector(sparseVector, denseVector));
	}
	
	public Instance(String label, SparseVector vector)
	{
		setStringLabel(label);
		setFeatureVector(new FeatureVector(vector));
	}
	
	public Instance(String label, float[] vector)
	{
		setStringLabel(label);
		setFeatureVector(new FeatureVector(vector));
	}
	
	public Instance(int label, FeatureVector vector)
	{
		setGoldLabel(label);
		setFeatureVector(vector);
	}
	
	public Instance(int label, SparseVector sparseVector, float[] denseVector)
	{
		setGoldLabel(label);
		setFeatureVector(new FeatureVector(sparseVector, denseVector));
	}
	
	public Instance(int label, SparseVector vector)
	{
		setGoldLabel(label);
		setFeatureVector(new FeatureVector(vector));
	}
	
	public Instance(int label, float[] vector)
	{
		setGoldLabel(label);
		setFeatureVector(new FeatureVector(vector));
	}
	
//	=================================== GETTERS & SETTERS ===================================
	
	public String getStringLabel()
	{
		return string_label;
	}
	
	public void setStringLabel(String label)
	{
		this.string_label = label;
	}
	
	public boolean hasStringLabel()
	{
		return string_label != null;
	}
	
	public boolean isStringLabel(String label)
	{
		return label.equals(string_label);
	}
	
	public int getGoldLabel()
	{
		return gold_label;
	}
	
	public void setGoldLabel(int label)
	{
		gold_label = label;
	}
	
	public boolean isGoldLabel(int label)
	{
		return label == gold_label;
	}
	
	public int getPredictedLabel()
	{
		return predicted_label;
	}
	
	public void setPredictedLabel(int label)
	{
		predicted_label = label;
	}
	
	public FeatureVector getFeatureVector()
	{
		return vector;
	}
	
	public void setFeatureVector(FeatureVector vector)
	{
		this.vector = vector;
	}
	
	public float[] getScores()
	{
		return scores;
	}
	
	public void setScores(float[] scores)
	{
		this.scores = scores;
	}
	
//	=================================== STRING VECTOR ===================================

//	public Instance(String label, StringVector stringVector, float[] denseVector)
//	{
//		setStringLabel(label);
//		setFeatureVector(new FeatureVector(stringVector, denseVector));
//	}
//	
//	public Instance(int label, StringVector stringVector, float[] denseVector)
//	{
//		setGoldLabel(label);
//		setFeatureVector(new FeatureVector(stringVector, denseVector));
//	}
//	
//	public Instance(String label, StringVector vector)
//	{
//		setStringLabel(label);
//		setFeatureVector(new FeatureVector(vector));
//	}
//	
//	public Instance(int label, StringVector vector)
//	{
//		setGoldLabel(label);
//		setFeatureVector(new FeatureVector(vector));
//	}
}
