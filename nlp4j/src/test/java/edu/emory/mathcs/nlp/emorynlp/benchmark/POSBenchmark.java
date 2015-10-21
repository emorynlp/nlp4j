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
package edu.emory.mathcs.nlp.emorynlp.benchmark;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.emorynlp.pos.AmbiguityClassMap;
import edu.emory.mathcs.nlp.emorynlp.pos.POSIndex;
import edu.emory.mathcs.nlp.emorynlp.pos.POSNode;
import edu.emory.mathcs.nlp.emorynlp.pos.POSState;
import edu.emory.mathcs.nlp.emorynlp.pos.POSTagger;
import edu.emory.mathcs.nlp.emorynlp.pos.feature.POSFeatureTemplate0;
import edu.emory.mathcs.nlp.emorynlp.utils.component.NLPFlag;
import edu.emory.mathcs.nlp.emorynlp.utils.eval.AccuracyEval;
import edu.emory.mathcs.nlp.emorynlp.utils.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.utils.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.utils.train.TrainInfo;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaGradMiniBatch;
import edu.emory.mathcs.nlp.machine_learning.optimization.reguralization.RegularizedDualAveraging;
import edu.emory.mathcs.nlp.machine_learning.optimization.reguralization.Regularizer;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVector;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSBenchmark
{
	@Test
	public void benchmark() throws Exception
	{
		FeatureTemplate<POSNode,POSState<POSNode>> template = new POSFeatureTemplate0();
		WeightVector weights = new WeightVector();
		StringModel model = new StringModel(weights);
		TrainInfo info = new TrainInfo(100, 10, 0.95);
//		OnlineOptimizer optimizer = new AdaGradMiniBatch(weights, 0.04f);
//		OnlineOptimizer optimizer = new AdaGrad(weights, 0.04f, new RegularizedDualAveraging(weights, 0.000001f));
		Regularizer rda = new RegularizedDualAveraging(weights, 0.000002f);
		OnlineOptimizer optimizer = new AdaGradMiniBatch(weights, 0.02f, rda);
		run(model, optimizer, rda, info, template);
	}
	
	void run(StringModel model, OnlineOptimizer optimizer, Regularizer rda, TrainInfo info, FeatureTemplate<POSNode,POSState<POSNode>> template) throws Exception
	{
		final String root = "/Users/jdchoi/Documents/Data/experiments/wsj/wsj-pos/";
		TSVReader<POSNode> reader = new TSVReader<>(new POSIndex(0,1));
		List<String> trnFiles = FileUtils.getFileList(root+"trn/", "pos");
		List<String> devFiles = FileUtils.getFileList(root+"dev/", "pos");
		Collections.sort(trnFiles);
		Collections.sort(devFiles);
		
		// collect ambiguity classes
		AmbiguityClassMap map = new AmbiguityClassMap();
		iterate(reader, trnFiles, nodes -> map.add(nodes));
		map.expand(0.4);
		
		// collect training instances
		Eval eval = new AccuracyEval();
		POSTagger<POSNode> tagger = new POSTagger<>(optimizer, model, null, template, eval, map);
		tagger.setTrainInfo(new TrainInfo[]{info});

		DoubleIntPair best = new DoubleIntPair(-1, -1);
		double currScore;
		
		for (int i=0; i<info.getMaxEpochs(); i++)
		{
			tagger.setFlag(NLPFlag.TRAIN);
			iterate(reader, trnFiles, tagger::process, optimizer);
			
			tagger.setFlag(NLPFlag.EVALUATE);
			eval.clear();
			iterate(reader, devFiles, tagger::process);
			currScore = eval.score();
			System.out.printf("%4d: %5.2f%10d%10d\n", i, currScore, optimizer.getWeightVector().countNonZeroWeights(), optimizer.getWeightVector().size());
			if (best.d < currScore) best.set(currScore, i);
			info.updateRollInProbability();
		}
		
		System.out.printf("Best: %d - %5.2f\n", best.i, best.d);
	}
	
	void iterate(TSVReader<POSNode> reader, List<String> filenames,  Consumer<POSNode[]> f, OnlineOptimizer optimizer) throws Exception
	{
		int count = 0;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream(filename));
			POSNode[] nodes;
			
			while ((nodes = reader.next()) != null)
			{
				f.accept(nodes);
				count++;
				if (count == 1000)
				{
					optimizer.update();
					count = 0;
				}
			}
			
			if (count > 0) optimizer.update();
			reader.close();	
		}
	}
	
	void iterate(TSVReader<POSNode> reader, List<String> filenames, Consumer<POSNode[]> f) throws Exception
	{
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream(filename));
			POSNode[] nodes;
			
			while ((nodes = reader.next()) != null)
				f.accept(nodes);
			
			reader.close();	
		}
	}
}
