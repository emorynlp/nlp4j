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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.model.StringModel;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPOnlineTrainer<S extends NLPState>
{
	public NLPOnlineTrainer() {};
	
//	=================================== ABSTRACT ===================================

	protected abstract void collect(NLPOnlineComponent<S> component, List<String> inputFiles);
	protected abstract NLPOnlineComponent<S> createComponent(InputStream config);
	protected abstract FeatureTemplate<S> createFeatureTemplate(int id);
	
//	=================================== COMPONENT ===================================
	
	@SuppressWarnings("unchecked")
	public NLPOnlineComponent<S> initComponent(InputStream configStream, InputStream previousModelStream, int featureID)
	{
		NLPOnlineComponent<S> component = null;
		NLPConfig configuration = null;
		StringModel[] models = null;
		TrainInfo[] info;
		
		if (previousModelStream != null)
		{
			BinUtils.LOG.info("Loading the previous model:\n");
			ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(previousModelStream);
			
			try
			{
				component = (NLPOnlineComponent<S>)oin.readObject();
				configuration = component.setConfiguration(configStream);
				models = component.getModels();
				oin.close();
			}
			catch (Exception e) {e.printStackTrace();}
		}
		else
		{
			component = createComponent(configStream);
			configuration = component.getConfiguration();
			component.setFeatureTemplate(createFeatureTemplate(featureID));
		}
	
		component.setTrainInfos(info = configuration.getTrainInfos());
		if (models == null) component.setModels(models = configuration.getStringModels(info));
		component.setOptimizers(configuration.getOptimizers(models));

		return component;
	}
	
	@SuppressWarnings("unchecked")
	public NLPOnlineComponent<S> initComponent(InputStream configStream, ObjectInputStream componentStream)
	{
		NLPOnlineComponent<S> component = null;
		
		try
		{
			component = (NLPOnlineComponent<S>)componentStream.readObject();
		}
		catch (Exception e) {e.printStackTrace();}

		NLPConfig config = component.setConfiguration(configStream);
		StringModel[] models = component.getModels();
		component.setTrainInfos(config.getTrainInfos());
		component.setOptimizers(config.getOptimizers(models));
		
		return component;
	}
	
//	=================================== TRAIN ===================================
	
	public void train(List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile, int featureType)
	{
		InputStream configStream = IOUtils.createFileInputStream(configurationFile);
		InputStream previousModelStream = (previousModelFile != null) ? IOUtils.createFileInputStream(previousModelFile) : null;
		NLPOnlineComponent<S> component = initComponent(configStream, previousModelStream, featureType);
		train(trainFiles, developFiles, component);
		if (modelFile != null) saveModel(component, IOUtils.createFileOutputStream(modelFile));
	}
	
	public void train(List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<S> component)
	{
		NLPConfig config = component.getConfiguration();
		TSVReader reader = config.getTSVReader();
		OnlineOptimizer[] optimizers = component.getOptimizers();
		StringModel[] models = component.getModels();
		TrainInfo[] info = component.getTrainInfos();
		
		int bestEpoch = -1, bestNZW = -1, maxEpochs = config.getMaxEpochs(), nzw;
		byte[][] bestModels = new byte[models.length][];
		Random rand = new XORShiftRandom(9);
		double bestScore = 0, score;
		
		collect(component, trainFiles);
		
		for (int i=0; i<optimizers.length; i++)
			BinUtils.LOG.info(optimizers[i].toString()+", bias = "+models[i].getBias()+", batch = "+info[i].getBatchSize()+", rollIn = "+info[i].getRollInProbability()+"\n");
		
		for (int epoch=1; epoch<=maxEpochs; epoch++)
		{
			component.setFlag(NLPFlag.TRAIN);
			Collections.shuffle(trainFiles, rand);
			iterate(reader, trainFiles, component::process, optimizers, info);
			score = evaluate(developFiles, component, reader);
			for (TrainInfo in : info) in.updateRollInProbability();
			nzw = Arrays.stream(optimizers).mapToInt(o -> o.getWeightVector().countNonZeroWeights()).sum();
			BinUtils.LOG.info(String.format("%5d: %s, NZW = %d\n", epoch, component.getEval().toString(), nzw));
			
			if (bestScore < score || (bestScore == score && nzw < bestNZW))
			{
				bestNZW    = nzw;
				bestEpoch  = epoch;
				bestScore  = score;
				for (int i=0; i<models.length; i++) bestModels[i] = models[i].toByteArray();
			}
		}
		
		BinUtils.LOG.info(String.format(" Best: %5.2f, epoch: %d, nzw: %d\n\n", bestScore, bestEpoch, bestNZW));
		for (int i=0; i<models.length; i++) models[i].fromByteArray(bestModels[i]);
	}
	
	protected double evaluate(List<String> developFiles, NLPOnlineComponent<?> component, TSVReader reader)
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
	
	protected void iterate(TSVReader reader, List<String> inputFiles, Consumer<NLPNode[]> f, OnlineOptimizer[] optimizers, TrainInfo[] info)
	{
		int[] counts = (optimizers != null) ? new int[optimizers.length] : null;
		NLPNode[] nodes;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next()) != null)
				{
					GlobalLexica.assignGlobalLexica(nodes);
					f.accept(nodes);
					update(optimizers, info, counts, false);
				}
			}
			catch (IOException e) {e.printStackTrace();}
			reader.close();
		}
		
		update(optimizers, info, counts, true);
	}
	
	protected void update(OnlineOptimizer[] optimizers, TrainInfo[] info, int[] counts, boolean last)
	{
		if (optimizers == null) return;
		
		for (int i=0; i<counts.length; i++)
		{
			if ((last && counts[i] > 0) || ++counts[i] == info[i].getBatchSize())
			{
				optimizers[i].update();
				counts[i] = 0;
			}		
		}
	}
	
	public void saveModel(NLPOnlineComponent<S> component, OutputStream stream)
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
