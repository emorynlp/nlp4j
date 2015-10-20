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
package edu.emory.mathcs.nlp.corenlp.component.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.corenlp.component.reader.TSVIndex;
import edu.emory.mathcs.nlp.corenlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.corenlp.component.train.Aggregation;
import edu.emory.mathcs.nlp.corenlp.learn.model.StringModel;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.Optimizer;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.liblinear.LiblinearL2SVC;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.minibatch.AdaDeltaMiniBatch;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.minibatch.AdaGradMiniBatch;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.sgd.AdaGrad;
import edu.emory.mathcs.nlp.corenlp.learn.optimization.sgd.Perceptron;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPConfig<N> implements ConfigXML
{
	protected Element xml;
	
//	=================================== CONSTRUCTORS ===================================
	
	public NLPConfig() {}
	
	public NLPConfig(InputStream in)
	{
		xml = XMLUtils.getDocumentElement(in);
	}
	
//	=================================== GETTERS & SETTERS ===================================  
	
	public Language getLanguage()
	{
		String language = XMLUtils.getTextContentFromFirstElementByTagName(xml, LANGUAGE);
		return Language.getType(language);
	}
	
	public Aggregation getAggregation()
	{
		Element e = XMLUtils.getFirstElementByTagName(xml, AGGREGATE);
		
		if (e != null)
		{
			double delta = XMLUtils.getDoubleAttribute (e, TOLERANCE_DELTA);
			int    max   = XMLUtils.getIntegerAttribute(e, MAX_TOLERANCE);
			return new Aggregation(delta, max);
		}
		
		return null;
	}
	
	public TSVReader<N> getTSVReader()
	{
		return new TSVReader<>(getTSVIndex());
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
	
	protected abstract TSVIndex<N> getTSVIndex();
	
//	=================================== TRAINER ===================================
	
	public Optimizer[] getOptimizers(StringModel[] models)
	{
		Optimizer[] trainers = new Optimizer[models.length];
		
		for (int i=0; i<models.length; i++)
			trainers[i] = getOptimizer(models[i], i);
		
		return trainers;
	}
	
	private Optimizer getOptimizer(StringModel model, int index)
	{
		Element eOptimizer = XMLUtils.getElementByTagName(xml, OPTIMIZER, index);
		String  algorithm  = XMLUtils.getTextContentFromFirstElementByTagName(eOptimizer, ALGORITHM);
		initOptimizer(eOptimizer, model);
		
		switch (algorithm)
		{
		case PERCEPTRON         : return getPerceptron       (eOptimizer, model);
		case ADAGRAD            : return getAdaGrad          (eOptimizer, model);
		case ADAGRAD_MINI_BATCH : return getAdaGradMiniBatch (eOptimizer, model);
		case ADADELTA_MINI_BATCH: return getAdaDeltaMiniBatch(eOptimizer, model);
		case LIBLINEAR_L2_SVC   : return getLiblinearL2SVC   (eOptimizer, model);
		}
		
		throw new IllegalArgumentException(algorithm+" is not a valid algorithm name.");
	}
	
	private void initOptimizer(Element eOptimizer, StringModel model)
	{
		int labelCutoff   = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, LABEL_CUTOFF);
		int featureCutoff = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, FEATURE_CUTOFF);
		float bias        = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eOptimizer, BIAS);
		boolean reset     = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, RESET_WEIGHTS);
		
		model.setBias(bias);
		model.vectorize(labelCutoff, featureCutoff, reset);
	}
	
	private Perceptron getPerceptron(Element eOptimizer, StringModel model)
	{
		boolean average      = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, AVERAGE);
		double  learningRate = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, LEARNING_RATE);
		
		return new Perceptron(model.getWeightVector(), average, learningRate);
	}
	
	private AdaGrad getAdaGrad(Element eOptimizer, StringModel model)
	{
		boolean average      = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, AVERAGE);
		double  learningRate = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, LEARNING_RATE);
		
		return new AdaGrad(model.getWeightVector(), average, learningRate);
	}
	
	private AdaGradMiniBatch getAdaGradMiniBatch(Element eOptimizer, StringModel model)
	{
		double  batchRatio   = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, BATCH_RATIO);
		boolean average      = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, AVERAGE);
		double  learningRate = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, LEARNING_RATE);
		
		return new AdaGradMiniBatch(model.getWeightVector(), batchRatio, average, learningRate);
	}
	
	private AdaDeltaMiniBatch getAdaDeltaMiniBatch(Element eOptimizer, StringModel model)
	{
		double  batchRatio   = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, BATCH_RATIO);
		boolean average      = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eOptimizer, AVERAGE);
		double  learningRate = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, LEARNING_RATE);
		double  decayingRate = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, DECAYING_RATE);

		return new AdaDeltaMiniBatch(model.getWeightVector(), batchRatio, average, learningRate, decayingRate);
	}
	
	private LiblinearL2SVC getLiblinearL2SVC(Element eOptimizer, StringModel model)
	{
		int    threadSize = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eOptimizer, THREAD_SIZE);
		String lossType   = XMLUtils.getTextContentFromFirstElementByTagName       (eOptimizer, LOSS_TYPE);
		double cost       = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, COST);
		double tolerance  = XMLUtils.getDoubleTextContentFromFirstElementByTagName (eOptimizer, TOLERANCE_DELTA);
		
		return new LiblinearL2SVC(model.getWeightVector(), threadSize, lossType, cost, tolerance);
	}
}
