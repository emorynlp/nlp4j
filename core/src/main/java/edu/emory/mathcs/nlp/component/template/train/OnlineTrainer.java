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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class OnlineTrainer<S extends NLPState>
{
	public OnlineTrainer() {};
	
//	=================================== ABSTRACT ===================================

	protected abstract void collect(OnlineComponent<S> component, List<String> inputFiles);
	protected abstract OnlineComponent<S> createComponent(InputStream config);
	protected abstract FeatureTemplate<S> createFeatureTemplate(Element eFeatures);
	
//	=================================== COMPONENT ===================================
	
	@SuppressWarnings("unchecked")
	public OnlineComponent<S> initComponent(InputStream configStream, InputStream previousModelStream)
	{
		OnlineComponent<S> component = null;
		OnlineOptimizer optimizer = null;
		NLPConfig configuration = null;
		
		if (previousModelStream != null)
		{
			BinUtils.LOG.info("Loading the previous model:\n");
			ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(previousModelStream);
			
			try
			{
				component = (OnlineComponent<S>)oin.readObject();
				configuration = component.setConfiguration(configStream);
				optimizer = component.getOptimizer();
				oin.close();
			}
			catch (Exception e) {e.printStackTrace();}
		}
		else
		{
			component = createComponent(configStream);
			configuration = component.getConfiguration();
			component.setFeatureTemplate(createFeatureTemplate(configuration.getFeatureElement()));
		}
	
		component.setTrainInfo(configuration.getTrainInfo());
		component.setOptimizer(configuration.getOnlineOptimizer());
		if (optimizer != null) component.getOptimizer().adapt(optimizer);
		
		return component;
	}
	
//	=================================== TRAIN ===================================
	
	public void train(List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile)
	{
		InputStream configStream = IOUtils.createFileInputStream(configurationFile);
		InputStream previousModelStream = (previousModelFile != null) ? IOUtils.createFileInputStream(previousModelFile) : null;
		OnlineComponent<S> component = initComponent(configStream, previousModelStream);
		
		try
		{
			train(trainFiles, developFiles, modelFile, component);
			if (modelFile != null) saveModel(component, IOUtils.createFileOutputStream(modelFile));
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("unchecked")
	public void train(List<String> trainFiles, List<String> developFiles, String modelFile, OnlineComponent<S> component) throws Exception
	{
		FeatureTemplate<S> feature = component.getFeatureTemplate();
		OnlineOptimizer optimizer = component.getOptimizer();
		NLPConfig config = component.getConfiguration();
		TSVReader reader = config.getTSVReader();
		TrainInfo info = component.getTrainInfo();
		
		int bestEpoch = -1, bestNZW = -1, nzw, labels, features;
		byte[] template = null, bestTemplate = null;
		Random rand = new XORShiftRandom(9);
		byte[] bestOptimizer = null;
		double bestScore = 0, score;
		double rollin;
		String eval;
		
		collect(component, trainFiles);
		BinUtils.LOG.info(optimizer.toString()+", batch = "+info.getBatchSize()+", dynamic = "+feature.getDynamicFeatureSize()+"\n");
		
		for (int epoch=1; epoch<=info.getMaxEpochs(); epoch++)
		{
			// train
			component.setFlag(NLPFlag.TRAIN);
			Collections.shuffle(trainFiles, rand);
			info.getRollIn().updateProbability();
			iterate(reader, trainFiles, component::process, optimizer, info);

			// info
			labels = optimizer.getLabelSize();
			features = component.getFeatureTemplate().getSparseFeatureSize();
			nzw = optimizer.getWeightVector().countNonZeroWeights();
			rollin = info.getRollIn().getProbability();
			
			if (info.isSaveLast())
				BinUtils.LOG.info(String.format("%5d: labels = %3d, features = %7d, nzw = %8d, rollin = %4.2f\n", epoch, labels, features, nzw, rollin));
			else
			{
				if (modelFile != null) template = IOUtils.toByteArray(component.getFeatureTemplate());
				score = evaluate(developFiles, component, reader);
				eval = component.getEval().toString();
				BinUtils.LOG.info(String.format("%5d: %s, labels = %3d, features = %7d, nzw = %8d, rollin = %4.2f\n", epoch, eval, labels, features, nzw, rollin));
				
				if (bestScore < score || (bestScore == score && nzw < bestNZW))
				{
					bestNZW   = nzw;
					bestEpoch = epoch;
					bestScore = score;
					 
					if (modelFile != null)
					{
						bestOptimizer = IOUtils.toByteArray(optimizer);
						bestTemplate  = template;
					}
				}				
			}
		}
		
		if (!info.isSaveLast())
		{
			if (modelFile != null)
			{
				component.setOptimizer((OnlineOptimizer)IOUtils.fromByteArray(bestOptimizer));
				component.setFeatureTemplate((FeatureTemplate<S>)IOUtils.fromByteArray(bestTemplate));
			}
			
			BinUtils.LOG.info(String.format(" Best: %5.2f, epoch: %d, nzw: %d\n\n", bestScore, bestEpoch, bestNZW));			
		}
	}
	
	protected double evaluate(List<String> developFiles, OnlineComponent<?> component, TSVReader reader)
	{
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		iterate(reader, developFiles, component::process);
		return eval.score();
	}
	
//	=================================== HELPERS ===================================
	
	protected void iterate(TSVReader reader, List<String> inputFiles, Consumer<NLPNode[]> f)
	{
		iterate(reader, inputFiles, f, null, null);
	}
	
	protected void iterate(TSVReader reader, List<String> inputFiles, Consumer<NLPNode[]> f, OnlineOptimizer optimizer, TrainInfo info)
	{
		NLPNode[] nodes;
		int count = 0;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next()) != null)
				{
					GlobalLexica.assignGlobalLexica(nodes);
					f.accept(nodes);
					count = update(optimizer, info, count, false);
				}
			}
			catch (IOException e) {e.printStackTrace();}
			reader.close();
		}
		
		update(optimizer, info, count, true);
	}
	
	protected int update(OnlineOptimizer optimizer, TrainInfo info, int count, boolean last)
	{
		if (optimizer == null) return count;
		
		if ((last && count > 0) || ++count == info.getBatchSize())
		{
			optimizer.updateMiniBatch();
			count = 0;
		}
		
		return count;
	}
	
	public void saveModel(OnlineComponent<S> component, OutputStream stream)
	{
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(stream);
		
		try
		{
			out.writeObject(component);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
}
