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
package edu.emory.mathcs.nlp.emorynlp.component.config;

import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.component.train.TrainInfo;
import edu.emory.mathcs.nlp.emorynlp.component.util.GlobalLexica;
import edu.emory.mathcs.nlp.machine_learning.activation.SoftmaxFunction;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaDeltaMiniBatch;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaGrad;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaGradMiniBatch;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaGradRegression;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.Perceptron;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.SoftmaxRegression;
import edu.emory.mathcs.nlp.machine_learning.optimization.reguralization.RegularizedDualAveraging;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPConfig<N extends NLPNode> implements ConfigXML
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
	
//	=================================== TRAIN INFO ===================================
	
	public TrainInfo getTrainInfo()
	{
		return getTrainInfoAux(0);
	}
	
	public TrainInfo[] getTrainInfo(int size)
	{
		TrainInfo[] info = new TrainInfo[size];
		
		for (int i=0; i<size; i++)
			info[i] = getTrainInfoAux(i);
		
		return info;
	}
	
	private TrainInfo getTrainInfoAux(int index)
	{
		Element eOptimizer  = XMLUtils.getElementByTagName(xml, OPTIMIZER, index);
		int     maxEpochs   = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, MAX_EPOCHS); 
		int     batchSize   = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, BATCH_SIZE);
		int     labelSize   = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, LABEL_SIZE);
		int     featureSize = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, FEATURE_SIZE);
		boolean featureHash = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, FEATURE_HASH);
		float   bias        = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eOptimizer, BIAS);
		float   rollIn      = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eOptimizer, ROLL_IN);
		return new TrainInfo(maxEpochs, batchSize, labelSize, featureSize, featureHash, bias, rollIn);
	}
	
//	=================================== OPTIMIZERS ===================================
	
	public OnlineOptimizer getOptimizer(StringModel model)
	{
		return getOnlineOptimizer(model, 0);
	}
	
	public OnlineOptimizer[] getOptimizers(StringModel[] models)
	{
		OnlineOptimizer[] trainers = new OnlineOptimizer[models.length];
		
		for (int i=0; i<models.length; i++)
			trainers[i] = getOnlineOptimizer(models[i], i);
		
		return trainers;
	}
	
	private OnlineOptimizer getOnlineOptimizer(StringModel model, int index)
	{
		Element eOptimizer = XMLUtils.getElementByTagName(xml, OPTIMIZER, index);
		String  algorithm  = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, ALGORITHM);
		OnlineOptimizer optimizer = null;
		
		switch (algorithm)
		{
		case PERCEPTRON         : optimizer = getPerceptron       (eOptimizer, model); break;
		case SOFTMAX_REGRESSION : optimizer = getSoftmaxRegression(eOptimizer, model); break;
		case ADAGRAD            : optimizer = getAdaGrad          (eOptimizer, model); break;
		case ADAGRAD_REGRESSION : optimizer = getAdaGradRegression(eOptimizer, model); break;
		case ADAGRAD_MINI_BATCH : optimizer = getAdaGradMiniBatch (eOptimizer, model); break;
		case ADADELTA_MINI_BATCH: optimizer = getAdaDeltaMiniBatch(eOptimizer, model); break;
		default: throw new IllegalArgumentException(algorithm+" is not a valid algorithm name."); 
		}
		
		return optimizer;
	}
	
	private Perceptron getPerceptron(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		return new Perceptron(model.getWeightVector(), learningRate);
	}
	
	private SoftmaxRegression getSoftmaxRegression(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		model.getWeightVector().setActivationFunction(new SoftmaxFunction());
		return new SoftmaxRegression(model.getWeightVector(), learningRate);
	}
	
	private AdaGradRegression getAdaGradRegression(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float l1 = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(model.getWeightVector(), l1) : null;
		model.getWeightVector().setActivationFunction(new SoftmaxFunction());
		return new AdaGradRegression(model.getWeightVector(), learningRate, rda);
	}
	
	private AdaGrad getAdaGrad(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float l1 = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(model.getWeightVector(), l1) : null;
		return new AdaGrad(model.getWeightVector(), learningRate, rda);
	}
	
	private AdaGradMiniBatch getAdaGradMiniBatch(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float l1 = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(model.getWeightVector(), l1) : null;
		return new AdaGradMiniBatch(model.getWeightVector(), learningRate, rda);
	}
	
	private AdaDeltaMiniBatch getAdaDeltaMiniBatch(Element eOptimizer, StringModel model)
	{
		float learningRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, LEARNING_RATE);
		float decayingRate = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, DECAYING_RATE);
		float l1 = XMLUtils.getFloatTextContentFromFirstElementByTagName(eOptimizer, L1_REGULARIZATION);
		RegularizedDualAveraging rda = (l1 > 0) ? new RegularizedDualAveraging(model.getWeightVector(), l1) : null;
		return new AdaDeltaMiniBatch(model.getWeightVector(), learningRate, decayingRate, rda);
	}
}
