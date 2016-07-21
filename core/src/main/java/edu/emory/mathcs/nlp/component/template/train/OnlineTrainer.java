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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.lexicon.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class OnlineTrainer<N extends AbstractNLPNode<N>, S extends NLPState<N>>
{
	public OnlineTrainer() {};
	
//	=================================== COMPONENT ===================================
	
	@SuppressWarnings("unchecked")
	public OnlineComponent<N,S> initComponent(NLPMode mode, InputStream configStream, InputStream previousModelStream, String name)
	{
		OnlineComponent<N,S> component = null;
		NLPConfig<N> configuration = null;
		
		if (previousModelStream != null)
		{
			BinUtils.LOG.info("Loading the previous model\n");
			ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(previousModelStream);
			
			try
			{
				component = (OnlineComponent<N,S>)oin.readObject();
				configuration = component.setConfiguration(configStream);
				oin.close();
			}
			catch (Exception e) {e.printStackTrace();}
		}
		else
		{
			component = createComponent(mode, configStream, name);
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
	
	public OnlineComponent<N,S> createComponent(NLPMode mode, InputStream config, String name)
	{
		if (name != null) BinUtils.LOG.warn("Name not implemented for OnlineComponent. Input name - " + name + " will be ignored.");
		return createComponent(mode, config);
	}
	
	public abstract OnlineComponent<N,S> createComponent(NLPMode mode, InputStream config);
	public abstract TSVReader<N> createTSVReader(Object2IntMap<String> map);
	public abstract GlobalLexica<N> createGlobalLexica(InputStream config);
	
//	=================================== TRAIN ===================================
	
	public double crossValidate(NLPMode mode, List<String> filenames, String configurationFile, String modelFile, String previousModelFile, int k)
	{
		ExecutorService executor = Executors.newFixedThreadPool(k);
		List<Future<Double>> future = new ArrayList<>();
		Pair<List<String>,List<String>> p;
		Future<Double> f;
		
		for (int i=0; i<k; i++)
		{
			p = split(filenames, k, i);
			f = executor.submit(new TrainTask(mode, p.o1, p.o2, configurationFile, modelFile+"."+i, previousModelFile));
			future.add(f);
		}
		
		executor.shutdown();
		double avg = 0;

		try
		{
			for (Future<Double> d : future)
				avg += d.get();
		}
		catch (Exception e) {e.printStackTrace();}
		
		avg /= k;
		BinUtils.LOG.info(String.format("Cross-validation score: %5.2f\n", avg));
		return avg;
	}
	
	static public Pair<List<String>,List<String>> split(List<String> filenames, int k, int index)
	{
		int g = filenames.size() / k;
		int beginIndex = index * g;
		int endIndex = (index+1 == k) ? filenames.size() : (index+1) * g;
		
		List<String> trnFiles = new ArrayList<>();
		List<String> devFiles = new ArrayList<>();
		
		for (int i=0; i<filenames.size(); i++)
		{
			if (beginIndex <= i && i < endIndex)
				devFiles.add(filenames.get(i));
			else
				trnFiles.add(filenames.get(i));
		}
		
		return new Pair<>(trnFiles, devFiles);
	}
	
	class TrainTask implements Callable<Double>
	{
		private NLPMode mode;
		private List<String> trainFiles;
		private List<String> developFiles;
		private String configurationFile;
		private String modelFile;
		private String previousModelFile;
		
		public TrainTask(NLPMode mode, List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile)
		{
			this.mode = mode;
			this.trainFiles = trainFiles;
			this.developFiles = developFiles;
			this.configurationFile = configurationFile;
			this.modelFile = modelFile;
			this.previousModelFile = previousModelFile;
		}
		
		@Override
		public Double call()
		{
			return train(mode, trainFiles, developFiles, configurationFile, modelFile, previousModelFile);
		}
	}
	
	public double train(NLPMode mode, List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile)
	{
		return train(mode, trainFiles, developFiles, configurationFile, modelFile, previousModelFile, 0);
	}
	
	public double train(NLPMode mode, List<String> trainFiles, List<String> developFiles, String configurationFile, String modelFile, String previousModelFile, int index)
	{
		InputStream previousModelStream = (previousModelFile != null) ? IOUtils.createFileInputStream(previousModelFile) : null;
		GlobalLexica<N> lexica = createGlobalLexica(IOUtils.createFileInputStream(configurationFile));
		String name = (modelFile != null) ? FileUtils.getBaseName(modelFile) : null;
		OnlineComponent<N,S> component = initComponent(mode, IOUtils.createFileInputStream(configurationFile), previousModelStream, name);
		TSVReader<N> reader = createTSVReader(component.getConfiguration().getReaderFieldMap());
		ObjectDoublePair<OnlineComponent<N,S>> p = null;
		
		try
		{
			p = train(reader, trainFiles, developFiles, component, lexica, index);
			if (modelFile != null) saveModel(p.o, IOUtils.createFileOutputStream(modelFile));
		}
		catch (Exception e) {e.printStackTrace();}
		
		return p.d;
	}
	
	@SuppressWarnings("unchecked")
	public ObjectDoublePair<OnlineComponent<N,S>> train(TSVReader<N> reader, List<String> trainFiles, List<String> developFiles, OnlineComponent<N,S> component, GlobalLexica<N> lexica, int index) throws Exception
	{
		OnlineOptimizer optimizer = component.getOptimizer();
		HyperParameter hp = component.getHyperParameter();
		int bestEpoch = -1, bestNZW = -1, NZW, L, SF;
		Random rand = new XORShiftRandom(9);
		byte[] bestComponent = null;
		double bestScore = 0, score;
		DoubleIntPair p;
		String eval;
		
		BinUtils.LOG.info(optimizer.toString()+"\n"+hp.toString("- ")+"\n");
		BinUtils.LOG.info("Training: "+index+"\n");
		
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
			
			p = evaluate(developFiles, component, lexica, reader);
			score = p.d;
			eval = component.getEval().toString();
			BinUtils.LOG.info(String.format("%2d:%5d: %s, L = %3d, SF = %7d, NZW = %8d, N/S = %6d\n", index, epoch, eval, L, SF, NZW, p.i));
			
			if (bestScore < score || (bestScore == score && NZW < bestNZW))
			{
				bestNZW   = NZW;
				bestEpoch = epoch;
				bestScore = score;
				bestComponent = IOUtils.toByteArray(component);
			}
		}
		
		if (bestComponent != null)
			component = (OnlineComponent<N,S>)IOUtils.fromByteArray(bestComponent);
		
		BinUtils.LOG.info(String.format("%2d: Best: %5.2f, epoch = %d\n", index, bestScore, bestEpoch));
		return new ObjectDoublePair<OnlineComponent<N,S>>(component, bestScore);
	}
	
	public DoubleIntPair evaluate(List<String> developFiles, OnlineComponent<N,S> component, GlobalLexica<N> lexica, TSVReader<N> reader)
	{
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		double time = iterate(reader, developFiles, component, lexica, true);
		return new DoubleIntPair(eval.score(), (int)Math.round(time));
	}
	
//	=================================== HELPERS ===================================
	
	protected double iterate(TSVReader<N> reader, List<String> inputFiles, OnlineComponent<N,S> component, GlobalLexica<N> lexica, boolean evaluate)
	{
		long st, et, time = 0, unit = 0;
		List<N[]> document;
		N[] nodes;
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
	
	protected int update(OnlineComponent<N,S> component, int count, boolean last)
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
	
	public void saveModel(OnlineComponent<N,S> component, OutputStream stream)
	{
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(stream);
		BinUtils.LOG.info("Saving the model\n");
		
		try
		{
			out.writeObject(component);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("unchecked")
	public void reduceModel(TSVReader<N> reader, List<String> filenames, OnlineComponent<N,S> component, GlobalLexica<N> lexica, String modelFile, String reducedModelFile)
	{
		BinUtils.LOG.info("Reducing:\n");
		float rate = 0f;
		
		DoubleIntPair p = evaluate(filenames, component, lexica, reader);
		BinUtils.LOG.info(String.format("%8.4f: %7d -> %s\n", rate, component.getFeatureTemplate().getSparseFeatureSize(), component.getEval().toString()));
		
		NLPConfig<N> config = component.getConfiguration();
		Element eReduce = XMLUtils.getFirstElementByTagName(config.getDocumentElement(), "reducer");
		double lowerBound = XMLUtils.getDoubleTextContentFromFirstElementByTagName(eReduce, "lower_bound");
		float start = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eReduce, "start");
		float inc   = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eReduce, "increment");
		float range = XMLUtils.getFloatTextContentFromFirstElementByTagName  (eReduce, "range");
		int   iter  = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eReduce, "iteration");
		
		byte[] backup;
		
		for (rate=start; ; rate+=inc)
		{
			backup = IOUtils.toByteArray(component);
			component.getFeatureTemplate().reduce(component.getOptimizer().getWeightVector(), rate);
			p = evaluate(filenames, component, lexica, reader);
			BinUtils.LOG.info(String.format("%8.4f: %7d -> %s, N/S = %6d\n", rate, component.getFeatureTemplate().getSparseFeatureSize(), component.getEval().toString(), p.i));

			if (iter <= 0 || Math.abs(lowerBound - p.d) <= range)
			{
				saveModel(component, IOUtils.createFileOutputStream(reducedModelFile));
				return;
			}
			else if (p.d < lowerBound)
			{
				component = (OnlineComponent<N,S>)IOUtils.fromByteArray(backup);
				rate -= inc;
				inc /= 2;
				iter--;
			}
		}
	}
}
