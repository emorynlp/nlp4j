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
package edu.emory.mathcs.nlp.component.template.train;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HyperParameter
{
	private int         batch_size;
	private int         max_epoch;
	private float       learning_rate;
	private float       decaying_rate;
	private float       bias;
	private int         feature_cutoff;
	private Regularizer l1_regularizer;
	private LOLS        lols;
	
	// neural networks
	private int[] hidden_dimensions;
	private ActivationFunction[]  activation_functions;
	private WeightGenerator weightGenerator;

//	========================== FEATURE CUTOFF ==========================

	public int getFeature_cutoff()
	{
		return feature_cutoff;
	}

	public void setFeature_cutoff(int cutoff)
	{
		this.feature_cutoff = cutoff;
	}
	
//	========================== BATCH SIZE ==========================

	public int getBatchSize()
	{
		return batch_size;
	}

	public void setBatchSize(int size)
	{
		batch_size = size;
	}
	
//	========================== MAX EPOCH ==========================
	
	public int getMaxEpochs()
	{
		return max_epoch;
	}
	
	public void setMaxEpochs(int epochs)
	{
		max_epoch = epochs;
	}
	
//	========================== LEARNING RATE ==========================

	public float getLearningRate()
	{
		return learning_rate;
	}

	public void setLearningRate(float learningRate)
	{
		learning_rate = learningRate;
	}
	
//	========================== L1 REGULARIZATION ==========================

	public Regularizer getL1Regularizer()
	{
		return l1_regularizer;
	}

	public void setL1Regularizer(Regularizer regularizer)
	{
		l1_regularizer = regularizer;
	}
	
	public boolean hasL1Regularizer()
	{
		return l1_regularizer != null;
	}

//	========================== BIAS ==========================

	public float getBias()
	{
		return bias;
	}

	public void setBias(float bias)
	{
		this.bias = bias;
	}
	
//	========================== LOLS ==========================
	
	public void setLOLS(LOLS lols)
	{
		this.lols = lols;
	}
	
	public LOLS getLOLS()
	{
		return lols;
	}

//	========================== DECAYING RATE: ADA-DELTA ==========================

	public float getDecayingRate()
	{
		return decaying_rate;
	}

	public void setDecayingRate(float decayingRate)
	{
		decaying_rate = decayingRate;
	}

//	========================== HIDDEN DIMENSIONS ==========================

	public int[] getHiddenDimensions()
	{
		return hidden_dimensions;
	}

	public void setHiddenDimensions(int[] dimensions)
	{
		this.hidden_dimensions = dimensions;
	}

//	========================== ACTIVATION FUNCTIONS ==========================

	public ActivationFunction[] getActivationFunctions()
	{
		return activation_functions;
	}

	public void setActivationFunctions(ActivationFunction[] functions)
	{
		this.activation_functions = functions;
	}

	public WeightGenerator getWeightGenerator()
	{
		return weightGenerator;
	}

//	========================== WEIGHT GENERATOR ==========================
	
	public void setWeightGenerator(WeightGenerator weightGenerator)
	{
		this.weightGenerator = weightGenerator;
	}
	
	
//	========================== UTILITIES ==========================
	
	public String toString(String prefix)
	{
		StringBuilder build = new StringBuilder();
		
		build.append(String.format("%s%s: %d\n", prefix, "Max epoch", max_epoch));
		if (batch_size > 0) build.append(String.format("%s%s: %d\n", prefix, "Mini-batch", batch_size));
		if (feature_cutoff > 0) build.append(String.format("%s%s: %d\n", prefix, "Feature cutoff", feature_cutoff));
		build.append(String.format("%s%s: %s\n", prefix, "Learning rate", learning_rate));
		if (decaying_rate > 0) build.append(String.format("%s%s: %s\n", prefix, "Decaying rate", decaying_rate));
		if (bias > 0) build.append(String.format("%s%s: %s\n", prefix, "Bias", bias));
		if (lols != null) build.append(String.format("%s%s\n", prefix, lols.toString()));
		if (l1_regularizer != null) build.append(String.format("%s%s", prefix, l1_regularizer.toString()));
		
		return build.toString();
	}
}
