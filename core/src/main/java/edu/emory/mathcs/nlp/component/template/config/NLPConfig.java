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
package edu.emory.mathcs.nlp.component.template.config;

import java.io.InputStream;
import java.util.Arrays;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.train.RollIn;
import edu.emory.mathcs.nlp.component.template.train.TrainInfo;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.activation.SigmoidFunction;
import edu.emory.mathcs.nlp.learning.activation.SoftmaxFunction;
import edu.emory.mathcs.nlp.learning.initialization.RandomWeightGenerator;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.neural.FeedForwardNeuralNetworkSoftmax;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaDeltaMiniBatch;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGrad;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGradMiniBatch;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGradRegression;
import edu.emory.mathcs.nlp.learning.optimization.method.Perceptron;
import edu.emory.mathcs.nlp.learning.optimization.method.SoftmaxRegression;
import edu.emory.mathcs.nlp.learning.optimization.reguralization.RegularizedDualAveraging;
import edu.emory.mathcs.nlp.learning.util.WeightVector;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPConfig implements ConfigXML
{
	protected Element xml;
	
//	=================================== CONSTRUCTORS ===================================
	
	public NLPConfig() {}
	
	public NLPConfig(InputStream in)
	{
		xml = XMLUtils.getDocumentElement(in);
		GlobalLexica.init(xml);
	}
	
//	=================================== GETTERS & SETTERS ===================================  
	
	public Language getLanguage()
	{
		String language = XMLUtils.getTextContentFromFirstElementByTagName(xml, LANGUAGE);
		return Language.getType(language);
	}
	
	public TSVReader getTSVReader()
	{
		Element eReader = XMLUtils.getFirstElementByTagName(xml, TSV);
		Object2IntMap<String> map = getFieldMap(eReader);
		TSVReader reader = new TSVReader();
		
		reader.form   = map.getOrDefault(FIELD_FORM  , -1);
		reader.lemma  = map.getOrDefault(FIELD_LEMMA , -1);
		reader.pos    = map.getOrDefault(FIELD_POS   , -1);
		reader.nament = map.getOrDefault(FIELD_NAMENT, -1);
		reader.feats  = map.getOrDefault(FIELD_FEATS , -1);
		reader.dhead  = map.getOrDefault(FIELD_DHEAD , -1);
		reader.deprel = map.getOrDefault(FIELD_DEPREL, -1);
		
		return reader;
	}
	
	/** Called by {@link #getTSVReader()}. */
	protected Object2IntMap<String> getFieldMap(Element eTSV)
	{
		NodeList list = eTSV.getElementsByTagName(COLUMN);
		int i, index, size = list.getLength();
		Element element;
		String field;
		
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		
		for (i=0; i<size; i++)
		{
			element = (Element)list.item(i);
			field   = XMLUtils.getTrimmedAttribute(element, FIELD);
			index   = XMLUtils.getIntegerAttribute(element, INDEX);
			
			map.put(field, index);
		}
		
		return map;
	}
	
//	=================================== FEATURE ===================================
	
	public Element getFeatureElement()
	{
		return XMLUtils.getFirstElementByTagName(xml, FEATURES);
	}
	
//	=================================== OPTIMIZER ===================================
	
	public TrainInfo getTrainInfo()
	{
		Element eOptimizer = XMLUtils.getFirstElementByTagName(xml, OPTIMIZER);
		Element eRollIn = XMLUtils.getFirstElementByTagName(eOptimizer, ROLL_IN);

		int maxEpochs = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, MAX_EPOCHS);
		int batchSize = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, BATCH_SIZE);
		int fixed = XMLUtils.getIntegerAttribute(eRollIn, "fixed");
		double decaying = XMLUtils.getDoubleAttribute(eRollIn, "decaying");
		RollIn rollin = new RollIn(fixed, decaying);
		boolean saveLast = XMLUtils.getBooleanAttribute(eOptimizer, SAVE_LAST);
		
		return new TrainInfo(maxEpochs, batchSize, rollin, saveLast);
	}
	
	public OnlineOptimizer getOnlineOptimizer()
	{
		Element eOptimizer = XMLUtils.getFirstElementByTagName(xml, OPTIMIZER);
		String  algorithm  = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, ALGORITHM);
		OnlineOptimizer optimizer = null;
		
		switch (algorithm)
		{
		case PERCEPTRON         : optimizer = getPerceptron       (eOptimizer); break;
		case SOFTMAX_REGRESSION : optimizer = getSoftmaxRegression(eOptimizer); break;
		case ADAGRAD            : optimizer = getAdaGrad          (eOptimizer); break;
		case ADAGRAD_REGRESSION : optimizer = getAdaGradRegression(eOptimizer); break;
		case ADAGRAD_MINI_BATCH : optimizer = getAdaGradMiniBatch (eOptimizer); break;
		case ADADELTA_MINI_BATCH: optimizer = getAdaDeltaMiniBatch(eOptimizer); break;
		case FFNN_SOFTMAX       : optimizer = getFeedForwardNeuralNetworkSoftmax(eOptimizer); break;
		default: throw new IllegalArgumentException(algorithm+" is not a valid algorithm name."); 
		}
		
		return optimizer;
	}
	
	private Perceptron getPerceptron(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		WeightVector w     = new WeightVector();
		return new Perceptron(w, learningRate, bias);
	}
	
	private SoftmaxRegression getSoftmaxRegression(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		WeightVector w     = new WeightVector();
		return new SoftmaxRegression(w, learningRate, bias);
	}
	
	private AdaGradRegression getAdaGradRegression(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		WeightVector w     = new WeightVector();
		return new AdaGradRegression(w, learningRate, bias);
	}
	
	private AdaGrad getAdaGrad(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		float l1           = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		WeightVector w     = new WeightVector();
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(w, l1) : null;
		return new AdaGrad(w, learningRate, bias, rda);
	}
	
	private AdaGradMiniBatch getAdaGradMiniBatch(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		float l1           = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		WeightVector w     = new WeightVector();
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(w, l1) : null;
		return new AdaGradMiniBatch(w, learningRate, bias, rda);
	}
	
	private AdaDeltaMiniBatch getAdaDeltaMiniBatch(Element eOptimizer)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float decayingRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, DECAYING_RATE);
		float bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		float l1           = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		WeightVector w     = new WeightVector();
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(w, l1) : null;
		return new AdaDeltaMiniBatch(w, learningRate, decayingRate, bias, rda);
	}
	
	private FeedForwardNeuralNetworkSoftmax getFeedForwardNeuralNetworkSoftmax(Element eOptimizer)
	{
		float  learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float  bias         = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, BIAS);
		String hidden       = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, HIDDEN);
		String activation   = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, ACTIVATION);
		String bound        = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, RANDOM_BOUND);
		
		String[] t = Splitter.splitCommas(hidden);
		int[] hiddenDimensions = Arrays.stream(t).mapToInt(Integer::parseInt).toArray();
		
		t = Splitter.splitCommas(activation);
		ActivationFunction[] functions = toActivationFunction(t);
		
		t = Splitter.splitCommas(bound);
		double[] bounds = Arrays.stream(t).mapToDouble(Double::parseDouble).toArray();
		WeightGenerator generator = new RandomWeightGenerator((float)bounds[0], (float)bounds[1]);
		
		return new FeedForwardNeuralNetworkSoftmax(hiddenDimensions, functions, learningRate, bias, generator);
	}
	
	private ActivationFunction[] toActivationFunction(String[] t)
	{
		ActivationFunction[] functions = new ActivationFunction[t.length];
		
		for (int i=0; i<t.length; i++)
		{
			switch (t[i])
			{
			case SIGMOID: functions[i] = new SigmoidFunction(); break;
			case SOFTMAX: functions[i] = new SoftmaxFunction(); break;
			}
		}
		
		return functions;
	}
}
