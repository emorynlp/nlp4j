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

import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 * @author amit-deshmane ({@code amitad87@gmail.com})
 */
public abstract class FeedForwardNeuralNetwork extends OnlineOptimizer
{
	private static final long serialVersionUID = -6902794736542104875L;
	
	protected int[]           hidden_dimensions;
	/**
	 * it is actually the probability with which a unit is retained.<br>
	 */
	protected float[]		  dropout_prob;
	/**
	 * while training on an instance or mini_batch, a thinned network is sampled for dropout<br>
	 */
	protected boolean[][]	  sampled_thinned_network;
	protected WeightVector    w_h2o;
	protected WeightVector[]  w_h2h;
	protected WeightGenerator generator;
	
//	============================== CONSTRUCTORS ==============================
	
	public FeedForwardNeuralNetwork(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator generator, float[] dropout_prob)
	{
		this(hiddenDimensions, functions, learningRate, bias, generator, null, dropout_prob);
	}
	
	public FeedForwardNeuralNetwork(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator generator)
	{
		this(hiddenDimensions, functions, learningRate, bias, generator, null, null);
	}
	
	/**
	 * @param hiddenDimensions dimensions of the hidden layers; {@code hiddenDimensions.length} must be greater than 0.
	 * @param functions        activation functions for hidden layers; {@code functions.length} must be equal to {@code hiddenDimensions.length}.
	 * @param learningRate     learning rate used to train all layers.
	 * @param bias             bias used to train all layers.
	 * @param initializer      initializes the weights between the input and the first hidden layers.
	 * @param dropout_prob	   dropout: unit retain probabilities for each layer including input layer.
	 */
	public FeedForwardNeuralNetwork(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator generator, Regularizer l1, float[] dropout_prob)
	{
		super(new WeightVector(functions[0]), learningRate, bias, l1);
		
		// dimensions
		hidden_dimensions = hiddenDimensions;
	
		// dropout: unit retain probabilities for each layer, including input
		this.dropout_prob = dropout_prob;
		// weights
		w_h2h = new WeightVector[hiddenDimensions.length - 1];
		
		// for bias
		int sparseFeatureSize = 1;
		
		for (int i=1; i<hiddenDimensions.length; i++)
		{
			w_h2h[i-1] = new WeightVector(functions[i]);
			w_h2h[i-1].expand(sparseFeatureSize, hiddenDimensions[i-1], hiddenDimensions[i], generator);
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
		sampleThinnedNetwork(instance);
		expand(instance.getFeatureVector());
		float[][] layers = forwardPropagation(instance.getFeatureVector(), NLPFlag.TRAIN);
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
		w_h2o.expand(sparseDimension, denseDimension, labelSize, generator);
	}
	
	@Override
	protected void trainAux(Instance instance) {}
	
//	=================================== PREDICT ===================================

	@Override
	public float[] scores(FeatureVector x)
	{
		return forwardPropagation(x, NLPFlag.EVALUATE)[hidden_dimensions.length];
	}

//	============================== PROPAGATION ==============================
	
	/** @param flag 
	 * @return [1st hidden layer(, next hidden layer)*, output_layer] from forward propagation. */
	public float[][] forwardPropagation(FeatureVector x, NLPFlag flag)
	{
		float[][] layers = new float[hidden_dimensions.length+1][];
		int i;
		
		switch (flag) {
		case TRAIN:
			// input -> hidden
			FeatureVector r_x = applyDropout(x, 0, flag);
			layers[0] = weight_vector.scores(r_x);
			
			// hidden -> hidden
			for (i=1; i<hidden_dimensions.length; i++){
				FeatureVector local_input = new FeatureVector(layers[i-1]);
				augment(local_input);
				FeatureVector r_local_input = applyDropout(local_input, i, flag);
				layers[i] = w_h2h[i-1].scores(r_local_input);
			}	
			// hidden -> output
			FeatureVector local_input = new FeatureVector(layers[hidden_dimensions.length-1]);
			augment(local_input);
			FeatureVector r_local_input = applyDropout(local_input, hidden_dimensions.length, flag);
			layers[hidden_dimensions.length] = w_h2o.scores(r_local_input);
			
			break;
		case EVALUATE:
			
		default:
			// input -> hidden
			layers[0] = weight_vector.scores(x);
			
			// hidden -> hidden
			for (i=1; i<hidden_dimensions.length; i++){
				FeatureVector local_input3 = new FeatureVector(layers[i-1]);
				augment(local_input3);
				layers[i] = w_h2h[i-1].scores(local_input3);
			}	
			// hidden -> output
			FeatureVector local_input3 = new FeatureVector(layers[i-1]);
			augment(local_input3);
			layers[i] = w_h2o.scores(local_input3);
			break;
		}
		
		
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
			errors = backwardPropagationH2H(w_h2h[i].getDenseWeightVector(), errors, layers[i], layers[i+1], i);

		// hidden -> input
		backwardPropagationH2I(instance.getFeatureVector(), errors, layers[i+1]);
	}
	
	protected abstract float[] backwardPropagationO2H(Instance instance, float[] input);
	protected abstract float[] backwardPropagationH2H(MajorVector weights, float[] gradients, float[] input, float[] output, int layer);
	protected abstract void    backwardPropagationH2I(FeatureVector input, float[] gradients, float[] output);
	
	@Override
	public String toString()
	{
		return toString("FeedForward-Softmax", "hidden = "+Arrays.toString(hidden_dimensions));
	}
	
	// dropout: sampling a thinned network
	public void sampleThinnedNetwork(Instance instance){
		sampled_thinned_network = new boolean[hidden_dimensions.length + 1][];
		sampled_thinned_network[0] = new boolean[instance.getFeatureVector().getSparseVector().maxIndex() + 1 + instance.getFeatureVector().getDenseVector().length];
		for(int index = 0; index < hidden_dimensions.length; index++){
			sampled_thinned_network[index + 1] = new boolean[1 + hidden_dimensions[index]];
		}
		for(int index = 0; index < hidden_dimensions.length + 1; index++){
			for(int unitIndex = 0; unitIndex < sampled_thinned_network[index].length; unitIndex++){
				if(dropout_prob == null || index >= dropout_prob.length || Math.random() <= dropout_prob[index]){
					sampled_thinned_network[index][unitIndex] = true;
				}
				else{
					sampled_thinned_network[index][unitIndex] = false;
				}
				
			}
		}
	}
	
	// dropout: applying the thinned network to input at each layer
	private FeatureVector applyDropout(FeatureVector x, int layerIndex, NLPFlag flag) {
		SparseVector srx = new SparseVector(x.getSparseVector());
		float[] drx = x.getDenseVector().clone();
		FeatureVector rx = new FeatureVector(srx, drx);
		int index;
		float[] denseVector;
		switch (flag) {
		case TRAIN:// considering inputs only from sampled thinned network
			for(SparseItem si : rx.getSparseVector().getVector()){
				int itemIndex = si.getIndex();
				if(!sampled_thinned_network[layerIndex][itemIndex]){
					si.setValue(0f);
				}
			}
			index = rx.getSparseVector().maxIndex() + 1;
			denseVector = rx.getDenseVector();
			for(int subIndex = 0; subIndex < denseVector.length; subIndex++){
				if(!sampled_thinned_network[layerIndex][index]){
					denseVector[subIndex] = 0f;
				}
				index++;
			}
			break;
		case EVALUATE:// average over all thinned networks by simply considering all units and multiplying the input with the dropout probability
			for(SparseItem si : rx.getSparseVector().getVector()){
				si.setValue(dropout_prob[layerIndex] * si.getValue());
			}
			index = rx.getSparseVector().maxIndex() + 1;
			denseVector = rx.getDenseVector();
			for(int subIndex = 0; subIndex < denseVector.length; subIndex++){
				denseVector[subIndex] = dropout_prob[layerIndex] * denseVector[subIndex];
				index++;
			}
			break;

		default: // same as evaluate in case you want to change anything ***
			for(SparseItem si : rx.getSparseVector().getVector()){
				si.setValue(dropout_prob[layerIndex] * si.getValue());
			}
			index = rx.getSparseVector().maxIndex() + 1;
			denseVector = rx.getDenseVector();
			for(int subIndex = 0; subIndex < denseVector.length; subIndex++){
				denseVector[subIndex] = dropout_prob[layerIndex] * denseVector[subIndex];
				index++;
			}
			break;
		}
		
		
		return rx;
	}
}