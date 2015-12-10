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

import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class StochasticGradientDescent extends OnlineOptimizer
{
	private static final long serialVersionUID = -531288798885524823L;

	public StochasticGradientDescent(WeightVector vector, float learningRate, float bias)
	{
		this(vector, learningRate, bias, null);
	}
	
	public StochasticGradientDescent(WeightVector vector, float learningRate, float bias, Regularizer l1)
	{
		super(vector, learningRate, bias, l1);
	}
	
	protected void trainClassification(Instance instance)
	{
		FeatureVector x = instance.getFeatureVector();
		int gold = instance.getGoldLabel();
		int yhat = instance.getPredictedLabel();
		
		for (SparseItem xi : x.getSparseVector())
		{
			updateWeight(gold, xi.getIndex(),  xi.getValue(), true);
			updateWeight(yhat, xi.getIndex(), -xi.getValue(), true);
		}
		
		if (x.hasDenseVector())
		{
			float[] d = x.getDenseVector();
			for (int xi=0; xi<d.length; xi++) updateWeight(gold, xi,  d[xi], false);
			for (int xi=0; xi<d.length; xi++) updateWeight(yhat, xi, -d[xi], false);
		}
	}
 	
 	protected void trainRegression(Instance instance, float[] gradients)
	{
 		FeatureVector x = instance.getFeatureVector();
 		
		for (SparseItem xi : x.getSparseVector())
			for (int y=0; y<gradients.length; y++)
				updateWeight(y, xi.getIndex(), gradients[y] * xi.getValue(), true);
		
		if (x.hasDenseVector())
		{
			float[] d = x.getDenseVector();
			
			for (int y=0; y<gradients.length; y++)
				for (int xi=0; xi<d.length; xi++)
					updateWeight(y, xi, gradients[y] * d[xi], false);
		}
	}
 	
 	protected void updateWeight(int y, int xi, float gradient, boolean sparse)
 	{
 		MajorVector weights = weight_vector.getMajorVector(sparse);
 		int index = weights.indexOf(y, xi);
 		float learningRate = getLearningRate(index, sparse);
 		
		if (isL1Regularization())
			l1_regularizer.updateWeight(index, gradient, learningRate, steps, sparse);
		else
			weights.add(index, gradient * learningRate);
 	}
}
