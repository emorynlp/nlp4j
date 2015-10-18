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
package edu.emory.mathcs.nlp.corenlp.benchmark;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.corenlp.component.NLPFlag;
import edu.emory.mathcs.nlp.corenlp.component.eval.AccuracyEval;
import edu.emory.mathcs.nlp.corenlp.component.eval.Eval;
import edu.emory.mathcs.nlp.corenlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.corenlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.corenlp.pos.AmbiguityClassMap;
import edu.emory.mathcs.nlp.corenlp.pos.POSIndex;
import edu.emory.mathcs.nlp.corenlp.pos.POSNode;
import edu.emory.mathcs.nlp.corenlp.pos.POSState;
import edu.emory.mathcs.nlp.corenlp.pos.POSTagger;
import edu.emory.mathcs.nlp.corenlp.pos.feature.POSFeatureTemplate1;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.optimization.method.AdaGradMiniBatch;
import edu.emory.mathcs.nlp.machine_learning.optimization.reguralization.RegularizedDualAveraging;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVector;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSBenchmark
{
	@Test
	public void benchmark() throws Exception
	{
		FeatureTemplate<POSNode,POSState<POSNode>> template = new POSFeatureTemplate1();
		WeightVector weights = new WeightVector();
		StringModel model = new StringModel(weights);
//		OnlineOptimizer optimizer = new AdaGrad(weights, 0.02f);
//		OnlineOptimizer optimizer = new AdaGrad(weights, 0.02f, new RegularizedDualAveraging(weights, 0.000002f));
		OnlineOptimizer optimizer = new AdaGradMiniBatch(weights, 0.02f, new RegularizedDualAveraging(weights, 0.000002f));
		run(model, optimizer, template);
	}
	
	void run(StringModel model, OnlineOptimizer optimizer, FeatureTemplate<POSNode,POSState<POSNode>> template) throws Exception
	{
		final String root = "/Users/jdchoi/Documents/Data/experiments/wsj/wsj-pos/";
		TSVReader<POSNode> reader = new TSVReader<>(new POSIndex(0,1));
		List<String> trnFiles = FileUtils.getFileList(root+"trn/", "pos");
		List<String> devFiles = FileUtils.getFileList(root+"tst/", "pos");
		Collections.sort(trnFiles);
		Collections.sort(devFiles);
		
		// collect ambiguity classes
		AmbiguityClassMap map = new AmbiguityClassMap();
		iterate(reader, trnFiles, nodes -> map.add(nodes));
		map.expand(0.4);
		
		// collect training instances
		Eval eval = new AccuracyEval();
		POSTagger<POSNode> tagger = new POSTagger<>(model, optimizer, template, eval, map);

		DoubleIntPair best = new DoubleIntPair(-1, -1);
		int epochs = 200;
		double currScore;
		double ref = 1, alpha = 0.95;
		
		for (int i=0; i<epochs; i++)
		{
			tagger.setGoldDynamicProbability(ref);
			tagger.setFlag(NLPFlag.TRAIN);
			iterate(reader, trnFiles, tagger::process, optimizer);
			
			tagger.setFlag(NLPFlag.EVALUATE);
			eval.clear();
			iterate(reader, devFiles, tagger::process);
			currScore = eval.score();
			System.out.printf("%4d: %5.2f%10d\n", i, currScore, optimizer.getWeightVector().countNonZeroWeights());
			if (best.d < currScore) best.set(currScore, i);
			ref *= alpha;
		}
		
		System.out.printf("Best: %d - %5.2f\n", best.i, best.d);
	}
	
	void iterate(TSVReader<POSNode> reader, List<String> filenames,  Consumer<POSNode[]> f, OnlineOptimizer optimizer) throws Exception
	{
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream(filename));
			POSNode[] nodes;
			
			while ((nodes = reader.next()) != null)
			{
				f.accept(nodes);
				optimizer.update();
			}
			
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
