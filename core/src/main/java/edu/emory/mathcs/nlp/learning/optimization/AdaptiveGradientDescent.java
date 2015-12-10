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

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AdaptiveGradientDescent extends StochasticGradientDescent
{
	private static final long serialVersionUID = 9194316873258304736L;
	protected final float EPSILON = 0.00001f;
	protected WeightVector diagonals;
	
	public AdaptiveGradientDescent(WeightVector vector, float learningRate, float bias)
	{
		this(vector, learningRate, bias, null);
	}
	
	public AdaptiveGradientDescent(WeightVector vector, float learningRate, float bias, Regularizer rda)
	{
		super(vector, learningRate, bias, rda);
		diagonals = weight_vector.createZeroVector();
	}
	
	@Override
	protected boolean expand(int sparseFeatureSize, int denseFeatureSize, int labelSize)
	{
		boolean b = super.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		if (b) diagonals.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		return b;
	}
	
	@Override
	protected float getLearningRate(int index, boolean sparse)
	{
		MajorVector d = diagonals.getMajorVector(sparse);
		return learning_rate / (EPSILON + (float)Math.sqrt(d.get(index)));
	}
	
	protected void updateDiagonal(int y, int xi, float gradient, boolean sparse)
	{
		MajorVector d = diagonals.getMajorVector(sparse);
		d.add(y, xi, MathUtils.sq(gradient));
	}
}
