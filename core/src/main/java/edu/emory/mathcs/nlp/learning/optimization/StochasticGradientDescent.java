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

import edu.emory.mathcs.nlp.learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.vector.SparseItem;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class StochasticGradientDescent extends OnlineOptimizer
{
	public StochasticGradientDescent(WeightVector vector, float learningRate)
	{
		this(vector, learningRate, null);
	}
	
	public StochasticGradientDescent(WeightVector vector, float learningRate, Regularizer l1)
	{
		super(vector, learningRate, l1);
	}
	
	protected void trainClassification(SparseInstance instance)
	{
		for (SparseItem xi : instance.getVector())
		{
			updateWeight(instance.getGoldLabel()     , xi,  xi.getValue());
			updateWeight(instance.getPredictedLabel(), xi, -xi.getValue());
		}
	}
 	
 	protected void trainRegression(SparseInstance instance, float[] gradients)
	{
		for (SparseItem xi : instance.getVector())
			for (int y=0; y<gradients.length; y++)
				updateWeight(y, xi, gradients[y] * xi.getValue());
	}
 	
 	protected void updateWeight(int y, SparseItem xi, float gradient)
 	{
 		int index = weight_vector.indexOf(y, xi.getIndex());
 		float learningRate = getLearningRate(index);
 		
		if (isL1Regularization())
			l1_regularizer.updateWeight(index, gradient, learningRate, steps);
		else
			weight_vector.add(index, learningRate * gradient * xi.getValue());
 	}

 	protected abstract float getLearningRate(int index);
}
