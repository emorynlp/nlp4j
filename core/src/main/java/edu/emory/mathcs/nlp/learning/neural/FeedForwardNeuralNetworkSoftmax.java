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
package edu.emory.mathcs.nlp.learning.neural;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.activation.SoftmaxFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseItem;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeedForwardNeuralNetworkSoftmax extends FeedForwardNeuralNetwork
{
	private static final long serialVersionUID = 7122005284712284931L;

	public FeedForwardNeuralNetworkSoftmax(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator initializer)
	{
		super(hiddenDimensions, functions, learningRate, bias, initializer);
	}
	
//	============================== OVERRIDE ==============================
	
	@Override
	protected ActivationFunction createActivationFunctionH2O()
	{
		return new SoftmaxFunction();
	}
	
	@Override
	protected int getPredictedLabel(Instance instance)
	{
		return getPredictedLabelRegression(instance);
	}
	
	@Override
	protected float getLearningRate(int index, boolean sparse)
	{
		return learning_rate;
	}
	
	@Override
	public void updateMiniBatch() {}
	
//	============================== BACKWARD PROPAGATION ==============================

	@Override
	protected float[] backwardPropagationO2H(Instance instance, float[] input)
	{
		float[] gradients = getGradientsRegression(instance);
		float[] errors = new float[input.length];
		float g; int index;
		
		MajorVector weights = w_h2o.getDenseWeightVector();
		
		for (int y=0; y<gradients.length; y++)
		{
			for (int xi=0; xi<input.length; xi++)
			{
				index = weights.indexOf(y, xi);
				g = gradients[y] * getLearningRate(index, false);
				errors[xi] += g * weights.get(index);
				weights.add(index, g * input[xi]);
			}
		}

		return errors;
	}
//	
	@Override
	protected float[] backwardPropagationH2H(MajorVector weights, float[] gradients, float[] input, int layer)
	{
		float[] errors = new float[input.length];
		int index;
		
		for (int y=0; y<gradients.length; y++)
		{
			for (int xi=0; xi<input.length; xi++)
			{
				index = weights.indexOf(y, xi);
				errors[xi] += gradients[y] * weights.get(index);
				weights.add(index, gradients[y] * input[xi]);
			}
		}
		
		return errors;
	}
	
	@Override
	protected void backwardPropagationH2I(FeatureVector input, float[] gradients)
	{
		MajorVector weights;
		int index;
		
		// sparse layer
		if (input.hasSparseVector())
		{
			weights = weight_vector.getSparseWeightVector();
			
			for (SparseItem p : input.getSparseVector())
			{
				for (int y=0; y<gradients.length; y++)
				{
					index = weights.indexOf(y, p.getIndex());
					weights.add(index, gradients[y] * p.getValue());
				}
			}
		}
		
		if (input.hasDenseVector())
		{
			weights = weight_vector.getDenseWeightVector();
			float[] x = input.getDenseVector();
			
			for (int y=0; y<gradients.length; y++)
			{
				for (int xi=0; xi<x.length; xi++)
				{
					index = weights.indexOf(y, xi);
					weights.add(index, gradients[y] * x[xi]);
				}
			}
		}
	}
}
