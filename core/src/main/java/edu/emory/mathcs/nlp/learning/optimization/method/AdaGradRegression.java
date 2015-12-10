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
package edu.emory.mathcs.nlp.learning.optimization.method;

import edu.emory.mathcs.nlp.learning.activation.SoftmaxFunction;
import edu.emory.mathcs.nlp.learning.optimization.AdaptiveGradientDescent;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaGradRegression extends AdaptiveGradientDescent
{
	private static final long serialVersionUID = 6397042389113367031L;

	public AdaGradRegression(WeightVector vector, float learningRate, float bias)
	{
		super(vector, learningRate, bias, null);
		if (!vector.hasActivationFunction()) vector.setActivationFunction(new SoftmaxFunction());
	}
	
	private void updateDiagonals(Instance instance, float[] gradients)
	{
		FeatureVector x = instance.getFeatureVector();
		
		for (SparseItem xi : x.getSparseVector())
			for (int y=0; y<gradients.length; y++)
				updateDiagonal(y, xi.getIndex(), gradients[y] * xi.getValue(), true);
		
		if (x.hasDenseVector())
		{
			float[] d = x.getDenseVector();
			
			for (int y=0; y<gradients.length; y++)
				for (int xi=0; xi<d.length; xi++)
					updateDiagonal(y, xi, gradients[y] * d[xi], false);
		}
	}
	
	@Override
	public void trainAux(Instance instance)
	{
		float[] gradients = getGradientsRegression(instance);
		updateDiagonals(instance, gradients);
		trainRegression(instance, gradients);
	}
	
	@Override
	protected int getPredictedLabel(Instance instance)
	{
		return getPredictedLabelRegression(instance);
	}
	
	@Override
	public void updateMiniBatch() {}
	
	@Override
	public String toString()
	{
		return toString("AdaGrad Regression");
	}
}
