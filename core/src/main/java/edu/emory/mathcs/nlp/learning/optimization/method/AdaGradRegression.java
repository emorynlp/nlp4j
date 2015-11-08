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

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.learning.optimization.AdaptiveGradientDescent;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.vector.SparseItem;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaGradRegression extends AdaptiveGradientDescent
{
	public AdaGradRegression(WeightVector vector, float learningRate)
	{
		this(vector, learningRate, null);
	}
	
	public AdaGradRegression(WeightVector vector, float learningRate, Regularizer l1)
	{
		super(vector, learningRate, l1);
	}
	
	private void updateDiagonals(SparseInstance instance, float[] gradients)
	{
		float g;
		
		for (SparseItem xi : instance.getVector())
		{
			for (int y=0; y<gradients.length; y++)
			{
				g = MathUtils.sq(xi.getValue() * gradients[y]);
				diagonals.add(y, xi.getIndex(), g);
			}
		}
	}

	@Override
	public void trainAux(SparseInstance instance)
	{
		float[] gradients = getGradientsRegression(instance);
		updateDiagonals(instance, gradients);
		trainRegression(instance, gradients);
	}
	
	@Override
	protected int getPredictedLabel(SparseInstance instance)
	{
		return getPredictedLabelRegression(instance);
	}
	
	@Override
	public String toString()
	{
		return toString("AdaGrad Regression");
	}
}
