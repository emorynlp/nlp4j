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
package edu.emory.mathcs.nlp.learning.zzz;

import java.io.Serializable;

import edu.emory.mathcs.nlp.learning.util.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseInstance implements Serializable
{
	private static final long serialVersionUID = 8175869181443119424L;
	private int          predicted_label;
	private int          gold_label;
	private float[]      scores;
	private SparseVector vector;
	
	public SparseInstance(int goldLabel, SparseVector vector)
	{
		this(goldLabel, -1, vector, null);
	}
	
	public SparseInstance(int goldLabel, int predictedLabel, SparseVector vector, float[] scores)
	{
		setPredictedLabel(predictedLabel);
		setGoldLabel(goldLabel);
		setVector(vector);
		setScores(scores);
	}
	
	public int getPredictedLabel()
	{
		return predicted_label;
	}
	
	public int getGoldLabel()
	{
		return gold_label;
	}
	
	public float[] getScores()
	{
		return scores;
	}
	
	public SparseVector getVector()
	{
		return vector;
	}
	
	public void setPredictedLabel(int label)
	{
		predicted_label = label;
	}
	
	public void setGoldLabel(int label)
	{
		gold_label = label;
	}
	
	public void setScores(float[] scores)
	{
		this.scores = scores;
	}
	
	public void setVector(SparseVector vector)
	{
		this.vector = vector;
	}
	
	public boolean isGoldLabel(int label)
	{
		return gold_label == label;
	}
	
	public boolean hasPredictedLabel()
	{
		return predicted_label >= 0;
	}
	
	public boolean hasScores()
	{
		return scores != null;
	}
	
	@Override
	public String toString()
	{
		return getGoldLabel()+" "+vector.toString();
	}
}
