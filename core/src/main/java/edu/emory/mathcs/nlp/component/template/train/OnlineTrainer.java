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

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.dep.DEPParser;
import edu.emory.mathcs.nlp.component.ner.NERTagger;
import edu.emory.mathcs.nlp.component.pleonastic.PleonasticClassifier;
import edu.emory.mathcs.nlp.component.pos.POSTagger;
import edu.emory.mathcs.nlp.component.sentiment.SentimentAnalyzer;
import edu.emory.mathcs.nlp.component.srl.SRLParser;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;
import edu.emory.mathcs.nlp.component.template.util.TSVReader;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class OnlineTrainer<S extends NLPState>
{
	public OnlineTrainer() {};
	
//	=================================== COMPONENT ===================================

	@SuppressWarnings("unchecked")
	public OnlineComponent<S> initComponent(NLPMode mode, InputStream configStream, InputStream previousModelStream)
	{
		OnlineComponent<S> component = null;
		NLPConfig configuration = null;
		
		if (previousModelStream != null)
		{
			BinUtils.LOG.info("Loading the previous model\n");
			ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(previousModelStream);
			
			try
			{
				component = (OnlineComponent<S>)oin.readObject();
				configuration = component.setConfiguration(configStream);
				oin.close();
			}
			catch (Exception e) {e.printStackTrace();}
		}
		else
		{
			component = createComponent(mode, configStream);
			configuration = component.getConfiguration();
		}
		
		HyperParameter hp = configuration.getHyperParameter();
		component.setHyperParameter(hp);
		
		if (component.getOptimizer() != null)
		{
			component.getOptimizer().adapt(hp);			
		}
		else
		{
			component.setOptimizer(configuration.getOnlineOptimizer(hp));
			component.initFeatureTemplate();
		}

		return component;
	}
	
	@SuppressWarnings("unchecked")
	protected OnlineComponent<S> createComponent(NLPMode mode, InputStream config)
	{
		switch (mode)
		{
		case pos: return (OnlineComponent<S>)new POSTagger(config);
		case ner: return (OnlineComponent<S>)new NERTagger(config);
		case dep: return (OnlineComponent<S>)new DEPParser(config);
		case srl: return (OnlineComponent<S>)new SRLParser(config);
		case sentiment : return (OnlineComponent<S>)new SentimentAnalyzer(config);
		case pleonastic: return (OnlineComponent<S>)new PleonasticClassifier(config);
		default : throw new IllegalArgumentException("Unsupported mode: "+mode);
		}
	}
	
//	=================================== TRAIN ===================================
	
	public void train(NLPMode mode, List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile)
	{
		InputStream configStream = IOUtils.createFileInputStream(configurationFile);
		InputStream previousModelStream = (previousModelFile != null) ? IOUtils.createFileInputStream(previousModelFile) : null;
		GlobalLexica lexica = new GlobalLexica(IOUtils.createFileInputStream(configurationFile));
		OnlineComponent<S> component = initComponent(mode, configStream, previousModelStream);
		
		try
		{
			train(trainFiles, developFiles, modelFile, component, lexica);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public void train(List<String> trainFiles, List<String> developFiles, String modelFile, OnlineComponent<S> component, GlobalLexica lexica) throws Exception
	{
		OnlineOptimizer optimizer = component.getOptimizer();
		HyperParameter hp = component.getHyperParameter();
		NLPConfig config = component.getConfiguration();
		TSVReader reader = config.getTSVReader();
		
		int bestEpoch = -1, bestNZW = -1, NZW, L, SF;
		Random rand = new XORShiftRandom(9);
		double bestScore = 0, score;
		DoubleIntPair p;
		String eval;
		
		BinUtils.LOG.info(optimizer.toString()+"\n"+hp.toString("- ")+"\n");
		BinUtils.LOG.info("Training:\n");
		
		for (int epoch=1; epoch<=hp.getMaxEpochs(); epoch++)
		{
			// train
			component.setFlag(NLPFlag.TRAIN);
			Collections.shuffle(trainFiles, rand);
			hp.getLOLS().updateGoldProbability();
			iterate(reader, trainFiles, component, lexica, false);

			// info
			L   = optimizer.getLabelSize();
			SF  = component.getFeatureTemplate().getSparseFeatureSize();
			NZW = optimizer.getWeightVector().countNonZeroWeights();
			component.getFeatureTemplate().initFeatureCount();
			
			if (developFiles == null)
				BinUtils.LOG.info(String.format("%5d: L = %3d, SF = %7d, NZW = %9d\n", epoch, L, SF, NZW));
			else
			{
				p = evaluate(developFiles, component, lexica, reader);
				score = p.d;
				eval = component.getEval().toString();
				BinUtils.LOG.info(String.format("%5d: %s, L = %3d, SF = %7d, NZW = %8d, N/S = %6d\n", epoch, eval, L, SF, NZW, p.i));
				
				if (bestScore < score || (bestScore == score && NZW < bestNZW))
				{
					bestNZW   = NZW;
					bestEpoch = epoch;
					bestScore = score;
				}
			}
		}
		
		if (developFiles != null)
			BinUtils.LOG.info(String.format(" Best: %5.2f, epoch = %d\n\n", bestScore, bestEpoch));
		if (modelFile != null)
			saveModel(component, IOUtils.createFileOutputStream(modelFile));
	}
	
	protected DoubleIntPair evaluate(List<String> developFiles, OnlineComponent<S> component, GlobalLexica lexica, TSVReader reader)
	{
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		double time = iterate(reader, developFiles, component, lexica, true);
		return new DoubleIntPair(eval.score(), (int)Math.round(time));
	}
	
//	=================================== HELPERS ===================================
	
	protected double iterate(TSVReader reader, List<String> inputFiles, OnlineComponent<S> component, GlobalLexica lexica, boolean evaluate)
	{
		long st, et, time = 0, unit = 0;
		List<NLPNode[]> document;
		NLPNode[] nodes;
		int count = 0;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				if (component.isDocumentBased())
				{
					document = reader.readDocument();
					lexica.process(document);
					st = System.currentTimeMillis();
					component.process(document);
					et = System.currentTimeMillis();
					if (!evaluate) count = update(component, count, false);
					time += et - st;
					unit++;
				}
				else
				{
					while ((nodes = reader.next()) != null)
					{
						lexica.process(nodes);
						st = System.currentTimeMillis();
						component.process(nodes);
						et = System.currentTimeMillis();
						if (!evaluate) count = update(component, count, false);
						time += et - st;
						unit += nodes.length - 1;
					}					
				}
			}
			catch (Exception e) {e.printStackTrace();}
			reader.close();
		}
		
		if (!evaluate) update(component, count, true);
		return 1000d * unit / time;
	}
	
	protected int update(OnlineComponent<S> component, int count, boolean last)
	{
		OnlineOptimizer optimizer = component.getOptimizer();
		HyperParameter hp = component.getHyperParameter();
		
		if ((last && count > 0) || ++count == hp.getBatchSize())
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
