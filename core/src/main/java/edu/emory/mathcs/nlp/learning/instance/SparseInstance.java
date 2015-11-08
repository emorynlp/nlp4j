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
package edu.emory.mathcs.nlp.learning.instance;

import java.io.Serializable;
import java.util.Arrays;

import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.vector.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseInstance implements Serializable
{
	private static final long serialVersionUID = 8175869181443119424L;
	private int[]        zero_cost_labels;
	private int          predicted_label;
	private int          gold_label;
	private float[]      scores;
	private SparseVector vector;
	
	public SparseInstance(int zeroCostLabels, SparseVector vector)
	{
		this(new int[]{zeroCostLabels}, vector);
	}
	
	public SparseInstance(int[] zeroCostLabels, SparseVector vector)
	{
		this(zeroCostLabels, vector, null);
	}
	
	public SparseInstance(int[] zeroCostLabels, SparseVector vector, float[] scores)
	{
		this(zeroCostLabels, vector, scores, -1, -1);
	}
	
	public SparseInstance(int[] zeroCostLabels, SparseVector vector, float[] scores, int goldLabel, int predictedLabel)
	{
		setZeroCostLabels(zeroCostLabels);
		setPredictedLabel(predictedLabel);
		setGoldLabel(goldLabel);
		setVector(vector);
		setScores(scores);
	}
	
	public int[] getZeroCostLabels()
	{
		return zero_cost_labels;
	}
	
	public int getGoldLabel()
	{
		if (!hasGoldLabel() && hasScores())
			gold_label = (zero_cost_labels.length == 1) ? zero_cost_labels[0] : MLUtils.argmax(scores, zero_cost_labels);

		return gold_label;
	}
	
	public int getPredictedLabel()
	{
		return predicted_label;
	}
	
	public float[] getScores()
	{
		return scores;
	}
	
	public SparseVector getVector()
	{
		return vector;
	}
	
	/** @param labels labels are internally sorted in ascending order. */
	public void setZeroCostLabels(int[] labels)
	{
		Arrays.sort(labels);
		zero_cost_labels = labels;
	}
	
	public void setGoldLabel(int label)
	{
		gold_label = label;
	}
	
	public void setPredictedLabel(int label)
	{
		predicted_label = label;
	}
	
	public void setScores(float[] scores)
	{
		this.scores = scores;
	}
	
	public void setVector(SparseVector vector)
	{
		this.vector = vector;
	}
	
	public boolean isZeroCostLabel(int label)
	{
		return Arrays.binarySearch(zero_cost_labels, label) >= 0;
	}
	
	public boolean hasGoldLabel()
	{
		return gold_label >= 0;
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
