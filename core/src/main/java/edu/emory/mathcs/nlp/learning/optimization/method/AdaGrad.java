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

import edu.emory.mathcs.nlp.learning.optimization.AdaptiveGradientDescent;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaGrad extends AdaptiveGradientDescent
{
	private static final long serialVersionUID = -943369544382849727L;

	public AdaGrad(WeightVector vector, float learningRate, float bias)
	{
		this(vector, learningRate, bias, null);
	}
	
	public AdaGrad(WeightVector vector, float learningRate, float bias, Regularizer l1)
	{
		super(vector, learningRate, bias, l1);
	}
	
	private void updateDiagonals(Instance instance)
	{
		FeatureVector x = instance.getFeatureVector();
		int gold = instance.getGoldLabel();
		int yhat = instance.getPredictedLabel();
		
		for (SparseItem xi : x.getSparseVector())
		{
			updateDiagonal(gold, xi.getIndex(), xi.getValue(), true);
			updateDiagonal(yhat, xi.getIndex(), xi.getValue(), true);
		}
		
		if (x.hasDenseVector())
		{
			float[] d = x.getDenseVector();
			for (int xi=0; xi<d.length; xi++) updateDiagonal(gold, xi, d[xi], false);
			for (int xi=0; xi<d.length; xi++) updateDiagonal(yhat, xi, d[xi], false);	
		}
	}
	
	@Override
	public void trainAux(Instance instance)
	{
		updateDiagonals(instance);
		trainClassification(instance);
	}
	
	@Override
	protected int getPredictedLabel(Instance instance)
	{
		return getPredictedLabelHingeLoss(instance);
	}
	
	@Override
	public void updateMiniBatch() {}
	
	@Override
	public String toString()
	{
		return "AdaGrad";
	}
}
