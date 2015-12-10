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

import java.util.Arrays;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class FeedForwardNeuralNetwork extends OnlineOptimizer
{
	private static final long serialVersionUID = -6902794736542104875L;
	
	protected int[]           hidden_dimensions;
	protected WeightVector    w_h2o;
	protected WeightVector[]  w_h2h;
	protected WeightGenerator generator;
	
//	============================== CONSTRUCTORS ==============================
	
	public FeedForwardNeuralNetwork(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator generator)
	{
		this(hiddenDimensions, functions, learningRate, bias, generator, null);
	}
	
	/**
	 * @param hiddenDimensions dimensions of the hidden layers; {@code hiddenDimensions.length} must be greater than 0.
	 * @param functions        activation functions for hidden layers; {@code functions.length} must be equal to {@code hiddenDimensions.length}.
	 * @param learningRate     learning rate used to train all layers.
	 * @param bias             bias used to train all layers.
	 * @param initializer      initializes the weights between the input and the first hidden layers.
	 */
	public FeedForwardNeuralNetwork(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator generator, Regularizer l1)
	{
		super(new WeightVector(functions[0]), learningRate, bias, l1);
		
		// dimensions
		hidden_dimensions = hiddenDimensions;
	
		// weights
		w_h2h = new WeightVector[hiddenDimensions.length - 1];
		
		for (int i=1; i<hiddenDimensions.length; i++)
		{
			w_h2h[i-1] = new WeightVector(functions[i]);
			w_h2h[i-1].expand(0, hiddenDimensions[i-1], hiddenDimensions[i]);
		}
		
		w_h2o = new WeightVector(createActivationFunctionH2O());
		this.generator = generator;
	}
	
	/** @return the activation function between the last hidden layer to the output layer. */
	protected abstract ActivationFunction createActivationFunctionH2O();
	
//	=================================== OVERRIDE ===================================

	@Override
	public void train(Instance instance)
	{
		augment(instance);
		expand(instance.getFeatureVector());
		float[][] layers = forwardPropagation(instance.getFeatureVector());
		instance.setScores(layers[layers.length-1]);
		int yhat = getPredictedLabel(instance);
		instance.setPredictedLabel(yhat);
		if (!instance.isGoldLabel(yhat)) backwardPropagation(instance, layers);
		steps++;
	}
	
	@Override
	protected void expand(FeatureVector x)
	{
		// input -> hidden
		int sparseDimension = x.hasSparseVector() ? x.getSparseVector().maxIndex()+1 : 0;
		int denseDimension  = x.hasDenseVector()  ? x.getDenseVector().length : 0;
		int labelSize       = hidden_dimensions[0];
		
		boolean b = weight_vector.expand(sparseDimension, denseDimension, labelSize, generator);
		if (b && isL1Regularization()) l1_regularizer.expand(sparseDimension, denseDimension, labelSize);
		
		// hidden -> output
		denseDimension = hidden_dimensions[hidden_dimensions.length-1];
		labelSize      = getLabelSize();
		w_h2o.expand(0, denseDimension, labelSize);
	}
	
	@Override
	protected void trainAux(Instance instance) {}
	
//	=================================== PREDICT ===================================

	@Override
	public float[] scores(FeatureVector x)
	{
		return forwardPropagation(x)[hidden_dimensions.length];
	}

//	============================== PROPAGATION ==============================
	
	/** @return [1st hidden layer(, next hidden layer)*, output_layer] from forward propagation. */
	public float[][] forwardPropagation(FeatureVector x)
	{
		float[][] layers = new float[hidden_dimensions.length+1][];
		int i;
		
		// input -> hidden
		layers[0] = weight_vector.scores(x);
		
		// hidden -> hidden
		for (i=1; i<hidden_dimensions.length; i++)
			layers[i] = w_h2h[i-1].scores(new FeatureVector(layers[i-1]));
			
		// hidden -> output
		layers[i] = w_h2o.scores(new FeatureVector(layers[i-1]));
		return layers;
	}

	// back-propagation
	public void backwardPropagation(Instance instance, float[][] layers)
	{
		int i = layers.length - 2;
		float[] errors;
		
		// output -> hidden
		errors = backwardPropagationO2H(instance, layers[i]);
		
		// hidden -> hidden
		for (i--; i>=0; i--)
			errors = backwardPropagationH2H(w_h2h[i].getDenseWeightVector(), errors, layers[i], i);

		// hidden -> input
		backwardPropagationH2I(instance.getFeatureVector(), errors);
	}
	
	protected abstract float[] backwardPropagationO2H(Instance instance, float[] input);
	protected abstract float[] backwardPropagationH2H(MajorVector weights, float[] gradients, float[] input, int layer);
	protected abstract void    backwardPropagationH2I(FeatureVector input, float[] gradients);
	
	@Override
	public String toString()
	{
		return toString("FeedForward-Softmax", "hidden = "+Arrays.toString(hidden_dimensions));
	}
}