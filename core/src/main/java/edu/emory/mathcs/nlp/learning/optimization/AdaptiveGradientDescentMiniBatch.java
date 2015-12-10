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
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AdaptiveGradientDescentMiniBatch extends AdaptiveGradientDescent
{
	private static final long serialVersionUID = -9070887527388228842L;
	protected WeightVector gradients;
	protected IntSet       sparse_updated_indices;
	protected IntSet       dense_updated_indices;
	protected int          batch_steps;
	
	public AdaptiveGradientDescentMiniBatch(WeightVector vector, float learningRate, float bias)
	{
		this(vector, learningRate, bias, null);
	}
	
	public AdaptiveGradientDescentMiniBatch(WeightVector vector, float learningRate, float bias, Regularizer rda)
	{
		super(vector, learningRate, bias, rda);

		batch_steps = 1;
		gradients = vector.createZeroVector();
		sparse_updated_indices = new IntOpenHashSet();
		dense_updated_indices  = new IntOpenHashSet();
	}
	
	@Override
	protected boolean expand(int sparseFeatureSize, int denseFeatureSize, int labelSize)
	{
		boolean b = super.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		if (b) gradients.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		return b;
	}
	
	@Override
	protected void updateWeight(int y, int xi, float gradient, boolean sparse)
 	{
		MajorVector g = gradients.getMajorVector(sparse);
		int index = g.indexOf(y, xi);

		g.add(index, gradient);
 		if (sparse)	sparse_updated_indices.add(index);
 		else		dense_updated_indices .add(index);
 	}
	
	@Override
	public void updateMiniBatch()
	{
		update(true);
		update(false);
		batch_steps++;
	}
	
	protected void update(boolean sparse)
	{
		IntSet s = sparse ? sparse_updated_indices : dense_updated_indices;
		MajorVector w = weight_vector.getMajorVector(sparse);
		MajorVector d = diagonals    .getMajorVector(sparse);
		MajorVector g = gradients    .getMajorVector(sparse);
		
		int[] indices = s.toIntArray();
		Arrays.sort(indices);
		
		updateDiagonals(d, g, indices);
		updateWeights  (w, g, indices, sparse);
		clearGraidents (g, indices);
		s.clear();
	}
	
	protected void updateDiagonals(MajorVector diagonals, MajorVector gradients, int[] indices)
	{
		for (int index : indices)
			diagonals.set(index, getDiagonal(diagonals.get(index), gradients.get(index)));
	}
	
	protected void updateWeights(MajorVector weights, MajorVector gradients, int[] indices, boolean sparse)
	{
		for (int index : indices)
		{
			if (isL1Regularization())
				l1_regularizer.updateWeight(index, gradients.get(index), getLearningRate(index, sparse), batch_steps, sparse);
			else
				weights.add(index, gradients.get(index) * getLearningRate(index, sparse));
		}
	}
	
	protected void clearGraidents(MajorVector gradients, int[] indices)
	{
		for (int index : indices)
			gradients.set(index, 0);
	}
	
	protected abstract float getDiagonal(float previousDiagonal, float gradient);
}
