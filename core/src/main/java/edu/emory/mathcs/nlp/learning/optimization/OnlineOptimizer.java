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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class OnlineOptimizer implements Serializable
{
	private static final long serialVersionUID = -7750497048585331648L;
	
	// model to be saved
	protected WeightVector weight_vector;
	protected LabelMap     label_map;
	protected float        bias;
	
	// for training
	protected Regularizer  l1_regularizer;
	protected float        learning_rate;
	protected int          steps;
	
//	=================================== CONSTRUCTORS ===================================
	
	public OnlineOptimizer(WeightVector vector, float learningRate, float bias)
	{
		this(vector, learningRate, bias, null);
	}
	
	public OnlineOptimizer(WeightVector vector, float learningRate, float bias, Regularizer l1)
	{
		label_map = new LabelMap();
		setWeightVector(vector);
		setBias(bias);

		setLearningRate(learningRate);
		setL1Regularizer(l1);
		steps = 1;
	}
	
	public void adapt(HyperParameter hp)
	{
		setL1Regularizer(hp.getL1Regularizer());
		setLearningRate(hp.getLearningRate());
	}
	
//	=================================== SERIALIZATION ===================================
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		weight_vector = (WeightVector)in.readObject();
		label_map     = (LabelMap)in.readObject();
		bias          = in.readFloat();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(weight_vector);
		out.writeObject(label_map);
		out.writeFloat(bias);
	}
	
//	=================================== GETTERS & SETTERS ===================================
	
	public WeightVector getWeightVector()
	{
		return weight_vector;
	}
	
	public void setWeightVector(WeightVector vector)
	{
		weight_vector = vector;
	}
	
	public float getLearningRate()
	{
		return learning_rate;
	}
	
	public void setLearningRate(float rate)
	{
		learning_rate = rate;
	}
	
	public float getBias()
	{
		return bias;
	}
	
	public void setBias(float bias)
	{
		this.bias = bias;
	}
	
	public Regularizer getL1Regularizer()
	{
		return l1_regularizer;
	}
	
	public void setL1Regularizer(Regularizer l1)
	{
		l1_regularizer = l1;
		if (isL1Regularization()) l1_regularizer.setWeightVector(weight_vector);
	}
	
	public boolean isL1Regularization()
	{
		return l1_regularizer != null;
	}
	
//	=================================== LABEL & FEATURE ===================================

	public LabelMap getLabelMap()
	{
		return label_map;
	}
	
	public String getLabel(int index)
	{
		return label_map.getLabel(index);
	}
	
	public int getLabelIndex(String label)
	{
		return label_map.index(label);
	}
	
	public int[] getLabelIndexArray(Collection<String> labels)
	{
		return labels.stream().mapToInt(s -> getLabelIndex(s)).toArray();
	}
	
	public int getLabelSize()
	{
		return label_map.size();
	}
	
	public int addLabel(String label)
	{
		return label_map.add(label);
	}
	
	public void addLabels(Collection<String> labels)
	{
		for (String label : labels) addLabel(label);
	}
	
//	=================================== TRAIN ===================================

	/** @param instance consists of string label and features. */
	public void train(Instance instance)
	{
		train(instance, true);
	}
	
	public void train(Instance instance, boolean augment)
	{
		if (augment) augment(instance);
		expand(instance.getFeatureVector());
		if (instance.hasScores()) addScores(instance.getFeatureVector(), instance.getScores());
		else instance.setScores(scores(instance.getFeatureVector()));
		int yhat = getPredictedLabel(instance);
		instance.setPredictedLabel(yhat);
		if (!instance.isGoldLabel(yhat)) trainAux(instance);
		steps++;
	}
	
	/**
	 * Adds string values to maps, converts them to sparse indices, and expands the weight vector.
	 * Called by {@link #train(Instance)}.
	 */
	public void augment(Instance instance)
	{
		// add label
		if (instance.hasStringLabel())
		{
			int label = addLabel(instance.getStringLabel());
			instance.setGoldLabel(label);
		}
		
		// add features
		augment(instance.getFeatureVector());
	}
	
	public void augment(FeatureVector x)
	{
		if (x.hasSparseVector())
		{
			x.getSparseVector().addBias(bias);
//			x.getSparseVector().sort();
		}
		else
			x.setSparseVector(new SparseVector(bias));
	}
	
	protected void expand(FeatureVector x)
	{
		int sparseFeatureSize = x.hasSparseVector() ? x.getSparseVector().maxIndex()+1 : 0;
		int denseFeatureSize  = x.hasDenseVector()  ? x.getDenseVector().length : 0;
		int labelSize = getLabelSize();
		expand(sparseFeatureSize, denseFeatureSize, labelSize);
	}
	
	protected boolean expand(int sparseFeatureSize, int denseFeatureSize, int labelSize)
	{
		boolean b = weight_vector.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		if (b && isL1Regularization()) l1_regularizer.expand(sparseFeatureSize, denseFeatureSize, labelSize);
		return b;
	}
	
	protected abstract void trainAux(Instance instance);
	
	/** Update batch learning (override if necessary). */
	public abstract void updateMiniBatch();
	
//	=================================== HELPERS ===================================
	
	protected abstract int getPredictedLabel(Instance instance);
	
	protected int getPredictedLabelHingeLoss(Instance instance)
	{
		float[] scores = instance.getScores();
		int y = instance.getGoldLabel();
		
		scores[y] -= 1;
		int yhat = argmax(scores);
		return yhat;
	}
	
	protected int getPredictedLabelRegression(Instance instance)
 	{
 		float[] scores = instance.getScores();
 		int y = instance.getGoldLabel();
 		return (1 <= scores[y]) ? y : argmax(scores);
 	}
 	
 	protected float[] getGradientsRegression(Instance instance)
 	{
		float[] gradients = Arrays.copyOf(instance.getScores(), getLabelSize());
		MathUtils.multiply(gradients, -1);
		gradients[instance.getGoldLabel()] += 1;
		return gradients;
 	}
 	
//	=================================== UTILITIES ===================================
	
 	protected abstract float getLearningRate(int index, boolean sparse);
 	
	protected int argmax(float[] scores)
 	{
 		int yhat = MLUtils.argmax(scores, getLabelSize());
 		return (scores[yhat] == 0 && yhat > 0) ? MLUtils.argmax(scores, yhat) : yhat;
 	}
 	
	public String toString(String type, String... args)
	{
		StringJoiner join = new StringJoiner(", ");
		join.add("learning rate = "+learning_rate);
		join.add("bias = "+bias);
		if (isL1Regularization()) join.add("l1 = "+l1_regularizer.getRate());
		for (String arg : args) if (arg != null) join.add(arg);
		return type+": "+join.toString();
	}
	
//	=================================== PREDICT ===================================
	
	public float[] scores(FeatureVector x)
	{
		return scores(x, true);
	}
	
	public float[] scores(FeatureVector x, boolean augment)
	{
		if (augment) augment(x);
		return weight_vector.scores(x);
	}
	
	public void addScores(FeatureVector x, float[] scores)
	{
		weight_vector.addScores(x, scores);
	}
}
