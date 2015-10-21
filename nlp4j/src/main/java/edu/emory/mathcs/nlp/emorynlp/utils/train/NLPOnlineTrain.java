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
package edu.emory.mathcs.nlp.emorynlp.utils.train;

import java.io.IOException;
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
import edu.emory.mathcs.nlp.emorynlp.utils.component.NLPFlag;
import edu.emory.mathcs.nlp.emorynlp.utils.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.utils.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.utils.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.utils.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.utils.state.NLPState;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVector;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPOnlineTrain<N,S extends NLPState<N>>
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
	
	/** Collects necessary lexicons for the component before training. */
	protected abstract NLPOnlineComponent<N,S> createComponent(NLPConfig<N> configuration, List<String> inputFiles);
	protected abstract NLPConfig<N> createConfiguration(String filename);
	
	public void train()
	{
		List<String>        trainFiles    = FileUtils.getFileList(train_path  , train_ext);
		List<String>        developFiles  = FileUtils.getFileList(develop_path, develop_ext);
		NLPConfig<N>        configuration = createConfiguration(configuration_file);
		NLPOnlineComponent<N,S> component = createComponent(configuration, trainFiles);
		
		train(configuration, trainFiles, developFiles, component);
		if (model_file != null) save(component);
	}
	
	public void train(NLPConfig<N> configuration, List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<N,S> component)
	{
		OnlineOptimizer[] optimizers = component.getOptimizers();
		TSVReader<N> reader = configuration.getTSVReader();
		StringModel[] models = component.getModels();
		TrainInfo[] info = component.getTrainInfo();
		
		for (int i=0; i<optimizers.length; i++)
		{
			BinUtils.LOG.info("\n"+optimizers[i].toString()+", bias = "+models[i].getBias()+"\n");
			train(trainFiles, developFiles, component, reader, optimizers[i], models[i], info[i]);
		}
	}
	
	/** Called by {@link #train(NLPConfig, TSVReader, List, List, NLPOnlineComponent)}. */
	protected double train(List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<N,?> component, TSVReader<N> reader, OnlineOptimizer optimizer, StringModel model, TrainInfo info)
	{
		WeightVector weightVector = model.getWeightVector();
		Random rand = new XORShiftRandom(9);
		Eval eval = component.getEval();
		double bestScore = 0, score;
		float[] bestWeights = null;
		int bestEpoch = -1;
		
		for (int epoch=0; epoch<info.getMaxEpochs(); epoch++)
		{
			component.setFlag(NLPFlag.TRAIN);
			Collections.shuffle(trainFiles, rand);
			iterate(reader, trainFiles, component::process, optimizer, info);
			
			component.setFlag(NLPFlag.EVALUATE);
			eval.clear();
			iterate(reader, developFiles, component::process, optimizer, info);
			score = eval.score();
			
			if (bestScore <= score)
			{
				bestEpoch   = epoch;
				bestScore   = score;
				bestWeights = weightVector.getWeights().toArray();
			}
			
			BinUtils.LOG.info(String.format("%4d: %s, RollIn = %5.4f, Batch = %d, NZW = %d\n", epoch, eval.toString(), info.getRollInProbability(), info.getBatchSize(), optimizer.getWeightVector().countNonZeroWeights()));
			info.updateRollInProbability();
		}
		
		BinUtils.LOG.info(String.format("Best: %5.2f at epoch d\n", bestScore, bestEpoch));
		weightVector.setWeights(bestWeights);
		
		BinUtils.LOG.info(String.format("\nFeature reduction: %d -> ", model.getFeatureSize()));
		BinUtils.LOG.info(String.format("%d\n", model.shrink(0.00005f)));
		
		return bestScore; 
	}
	
//	=================================== HELPERS ===================================
	
	protected void iterate(TSVReader<N> reader, List<String> inputFiles, Consumer<N[]> f)
	{
		iterate(reader, inputFiles, f, null, null);
	}
	
	protected void iterate(TSVReader<N> reader, List<String> inputFiles, Consumer<N[]> f, OnlineOptimizer optimizer, TrainInfo info)
	{
		int count = 0;
		N[] nodes;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next()) != null)
				{
					f.accept(nodes);
					
					if (optimizer != null)
					{
						count++;
						
						if (count == info.getBatchSize())
						{
							optimizer.update();
							count = 0;
						}
					}
						
				}
			}
			catch (IOException e) {e.printStackTrace();}
			
			if (optimizer != null && count > 0)
				optimizer.update();
			
			reader.close();
		}
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
