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
package edu.emory.mathcs.nlp.emorynlp.component.train;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.emorynlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.model.StringModelHash;
import edu.emory.mathcs.nlp.machine_learning.model.StringModelMap;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVector;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVectorDynamic;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVectorStatic;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPOnlineTrain<N extends NLPNode,S extends NLPState<N>>
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	public String train_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	public String train_ext = "*";
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	public String develop_path;
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	public String develop_ext = "*";
	@Option(name="-f", usage="feature template ID (default: 0)", required=false, metaVar="integer")
	public int feature_template = 0;
	@Option(name="-m", usage="model file (optional)", required=false, metaVar="<filename>")
	public String model_file = null;
	
	public NLPOnlineTrain() {};
	
	public NLPOnlineTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
	}
	
	protected void initComponentSingleModel(NLPOnlineComponent<N,S> component, List<String> inputFiles)
	{
		NLPConfig<N> configuration = component.getConfiguration();
		TrainInfo info = configuration.getTrainInfo();
		WeightVector vector;
		StringModel  model;
		
		if (info.featureHash())
		{
			vector = new WeightVectorStatic(info.getLabelSize(), info.getFeatureSize());
			model  = new StringModelHash(vector, info.getFeatureSize(), info.getBias());
		}
		else
		{
			vector = new WeightVectorDynamic();
			model  = new StringModelMap(vector, info.getBias());
		}
		
		OnlineOptimizer optimizer = configuration.getOptimizer(model);
		FeatureTemplate<N,S> template = createFeatureTemplate();
		
		component.setModel(model);
		component.setTrainInfo(info);
		component.setOptimizer(optimizer);
		component.setFeatureTemplate(template);
	}
	
	protected abstract void initComponent(NLPOnlineComponent<N,S> component, List<String> inputFiles);
	protected abstract NLPOnlineComponent<N,S> createComponent(InputStream config);
	protected abstract FeatureTemplate<N,S> createFeatureTemplate();
	protected abstract N createNode();
	
	public void train()
	{
		List<String> trainFiles   = FileUtils.getFileList(train_path  , train_ext);
		List<String> developFiles = FileUtils.getFileList(develop_path, develop_ext);
		NLPOnlineComponent<N,S> component = createComponent(IOUtils.createFileInputStream(configuration_file));
		initComponent(component, trainFiles);
		train(trainFiles, developFiles, component);
		if (model_file != null) save(component);
	}
	
	public void train(List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<N,S> component)
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		OnlineOptimizer[] optimizers = component.getOptimizers();
		StringModel[] models = component.getModels();
		TrainInfo[] info = component.getTrainInfos();
		
		for (int i=0; i<optimizers.length; i++)
			train(trainFiles, developFiles, component, reader, optimizers[i], models[i], info[i]);
	}
	
	/** Called by {@link #train(NLPConfig, TSVReader, List, List, NLPOnlineComponent)}. */
	protected double train(List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<N,?> component, TSVReader reader, OnlineOptimizer optimizer, StringModel model, TrainInfo info)
	{
		int bestEpoch = -1, bestNZW = -1, nzw, epoch;
		Random rand = new XORShiftRandom(9);
		double bestScore = 0, score;
		byte[] bestModel = null;
		
		BinUtils.LOG.info("\n"+optimizer.toString()+", bias = "+model.getBias()+", batch = "+info.getBatchSize()+"\n");
		
		for (epoch=1; epoch<=info.getMaxEpochs(); epoch++)
		{
			component.setFlag(NLPFlag.TRAIN);
			Collections.shuffle(trainFiles, rand);
			iterate(reader, trainFiles, component::process, optimizer, info);
			score = evaluate(developFiles, component, reader);
			nzw = optimizer.getWeightVector().countNonZeroWeights();
			
			BinUtils.LOG.info(String.format("%5d: %s, RollIn = %5.4f, NZW = %d\n", epoch, component.getEval().toString(), info.getRollInProbability(), nzw));
			info.updateRollInProbability();
			
			if (bestScore < score || (bestScore == score && nzw < bestNZW))
			{
				bestNZW   = nzw;
				bestEpoch = epoch;
				bestScore = score;
				bestModel = model.toByteArray();
			}
		}
		
		BinUtils.LOG.info(String.format(" Best: %5.2f, epoch: %d, nzw: %d\n", bestScore, bestEpoch, bestNZW));
		model.fromByteArray(bestModel);
		return bestScore; 
	}
	
	protected double evaluate(List<String> developFiles, NLPOnlineComponent<N,?> component, TSVReader reader)
	{
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		iterate(reader, developFiles, component::process);
		return eval.score();
	}
	
//	=================================== HELPERS ===================================
	
	protected void iterate(TSVReader reader, List<String> inputFiles, Consumer<N[]> f)
	{
		iterate(reader, inputFiles, f, null, null);
	}
	
	protected void iterate(TSVReader reader, List<String> inputFiles, Consumer<N[]> f, OnlineOptimizer optimizer, TrainInfo info)
	{
		int count = 0;
		N[] nodes;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next(this::createNode)) != null)
				{
					f.accept(nodes);
					
					if (optimizer != null && ++count == info.getBatchSize())
					{
						optimizer.update();
						count = 0;
					}
				}
			}
			catch (IOException e) {e.printStackTrace();}
			reader.close();
		}
		
		if (optimizer != null && count > 0)
			optimizer.update();
	}
	
	public void save(NLPOnlineComponent<N,S> component)
	{
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(model_file);
		
		try
		{
			out.writeObject(component);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
}
