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
package edu.emory.mathcs.nlp.learning.optimization;

import java.util.Arrays;

import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.vector.SparseItem;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AdaptiveGradientDescentMiniBatch extends AdaptiveGradientDescent
{
	protected WeightVector gradients;
	protected IntSet updated_indices;
	protected int batch_steps;
	
	public AdaptiveGradientDescentMiniBatch(WeightVector vector, float learningRate)
	{
		this(vector, learningRate, null);
		batch_steps = 1;
	}
	
	public AdaptiveGradientDescentMiniBatch(WeightVector vector, float learningRate, Regularizer rda)
	{
		super(vector, learningRate, rda);
		gradients = vector.createEmptyVector();
		updated_indices = new IntOpenHashSet();
	}
	
	@Override
	public void expand(int labelSize, int featureSize)
	{
		super.expand(labelSize, featureSize);
		gradients.expand(labelSize, featureSize);
	}
	
	@Override
 	protected void updateWeight(int y, SparseItem xi, float gradient)
 	{
		int index = gradients.indexOf(y, xi.getIndex());
 		gradients.add(index, xi.getValue() * gradient);
 		updated_indices.add(index);
 	}
	
	@Override
	public void update()
	{
		int[] indices = updated_indices.toIntArray();
		Arrays.sort(indices);
		updateDiagonals(indices);
		updateWeights (indices);
		clearGraidents(indices);
		updated_indices.clear();
		batch_steps++;
	}
	
	protected void updateDiagonals(int[] indices)
	{
		for (int index : indices)
			diagonals.set(index, getDiagonal(diagonals.get(index), gradients.get(index)));
	}
	
	protected void updateWeights(int[] indices)
	{
		for (int index : indices)
		{
			if (isL1Regularization())
				l1_regularizer.updateWeight(index, gradients.get(index), getLearningRate(index), batch_steps);
			else
				updateWeight(index);
		}
	}
	
	protected void updateWeight(int index)
	{
		weight_vector.add(index, getLearningRate(index) * gradients.get(index));
	}
	
	protected void clearGraidents(int[] indices)
	{
		for (int index : indices)
			gradients.set(index, 0);
	}
	
	protected abstract float getDiagonal(float previousDiagonal, float gradient);
}
