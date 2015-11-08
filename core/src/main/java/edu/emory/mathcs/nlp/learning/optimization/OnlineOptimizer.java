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
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class OnlineOptimizer
{
	protected WeightVector weight_vector;
	protected Regularizer l1_regularizer;
	protected float learning_rate;
	protected int   steps;
	
	public OnlineOptimizer(WeightVector vector, float learningRate)
	{
		this(vector, learningRate, null);
	}
	
	public OnlineOptimizer(WeightVector vector, float learningRate, Regularizer l1)
	{
		steps = 1;
		setL1Regularizer(l1);
		setWeightVector(vector);
		learning_rate = learningRate;
	}
	
//	=================================== GETTERS/SETTERS ===================================
	
	/** @return the weight vector. */
	public WeightVector getWeightVector()
	{
		return weight_vector;
	}
	
	public void setWeightVector(WeightVector vector)
	{
		weight_vector = vector;
	}
	
	public Regularizer getL1Regularizer()
	{
		return l1_regularizer;
	}
	
	public void setL1Regularizer(Regularizer l1)
	{
		l1_regularizer = l1;
	}
	
	public boolean isL1Regularization()
	{
		return l1_regularizer != null;
	}
	
//	=================================== TRAIN ===================================

	/** Trains the weight vector given the training instance. */
	public void train(SparseInstance instance)
	{
		if (!instance.hasScores()) instance.setScores(weight_vector.scores(instance.getVector()));
		int yhat = instance.hasPredictedLabel() ? instance.getPredictedLabel() : setPredictedLabel(instance); 
		if (!instance.isZeroCostLabel(yhat)) trainAux(instance);
		steps++;
	}
	
	/**
	 * Sets the predicted label to the instance with respect to the loss function defined by the optimizer.
	 * @return the predicted label of the instance with respect to the loss function defined by the optimizer.
	 * @param instance {@link SparseInstance#getZeroCostLabels()} != null && {@link SparseInstance#getVector()} != null.
	 */
	public int setPredictedLabel(SparseInstance instance)
	{
		int yhat = getPredictedLabel(instance);
		instance.setPredictedLabel(yhat);
		return yhat;
	}
	
	/** Update batch learning (to be overriden if necessary). */
	public void update() {}
	
	/** Called by {@link #train(SparseInstance)}. */
	protected abstract void trainAux(SparseInstance instance);

	/**
	 * Called by {@link #setPredictedLabel(SparseInstance)}.
	 * PRE: {@link SparseInstance#getScores()} != null.
	 */
	protected abstract int getPredictedLabel(SparseInstance instance);
 	
//	=================================== HELPERS ===================================
	
	protected int getPredictedLabelHinge(SparseInstance instance)
	{
		float[] scores = instance.getScores();
		for (int y : instance.getZeroCostLabels()) scores[y] -= 1;
		int yhat = argmax(scores);
		for (int y : instance.getZeroCostLabels()) scores[y] += 1;
		return yhat;
	}
	
	protected int getPredictedLabelRegression(SparseInstance instance)
 	{
 		float[] scores = instance.getScores();
		
		for (int y : instance.getZeroCostLabels())
			if (1 <= scores[y]) return y;

		return -1;
 	}
 	
 	protected int argmax(float[] scores)
 	{
 		int yhat = MLUtils.argmax(scores);
 		return (scores[yhat] == 0 && yhat > 0) ? MLUtils.argmax(scores, yhat) : yhat;
 	}

 	protected float[] getGradientsRegression(SparseInstance instance)
 	{
		float[] gradients = Arrays.copyOf(instance.getScores(), weight_vector.getLabelSize());
		MathUtils.multiply(gradients, -1);
		gradients[instance.getGoldLabel()] += 1;
		return gradients;
 	}
 	
 	public void expand(int labelSize, int featureSize)
	{
		weight_vector.expand(labelSize, featureSize);
		if (isL1Regularization()) l1_regularizer.expand();
	}
 	
	public String toString(String type, String... args)
	{
		StringJoiner join = new StringJoiner(", ");
		join.add("learning rate = "+learning_rate);
		if (isL1Regularization()) join.add("l1 = "+l1_regularizer.getRate());
		for (String arg : args) if (arg != null) join.add(arg);
		return type+": "+join.toString();
	}
}
