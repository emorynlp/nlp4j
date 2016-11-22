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
package edu.emory.mathcs.nlp.zzz;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.activation.SigmoidFunction;
import edu.emory.mathcs.nlp.learning.initialization.RandomWeightGenerator;
import edu.emory.mathcs.nlp.learning.neural.FeedForwardNeuralNetwork;
import edu.emory.mathcs.nlp.learning.neural.FeedForwardNeuralNetworkSoftmax;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CSVSpectra
{
	public CSVSpectra(String trnFile, String tstFile) throws Exception
	{
		List<Instance> trnData = getData(trnFile);
		List<Instance> tstData = getData(tstFile);
		
//		print(trnData, trnFile+".txt");
//		print(tstData, tstFile+".txt");
		validate(trnData, tstData);
		
//		for (int i=0; i<10; i++)
//		{
//			List<Instance> instances = getInstances(TSV_FILE, COL, stopwords);
//			avg += bestScore;
//		}
//		
//		System.out.printf("Avg: %5.2f\n", avg/10);
	}
	
	public void print(List<Instance> data, String outputFile)
	{
		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
		
		for (Instance instance : data)
		{
			StringJoiner join = new StringJoiner(" ");
			join.add(instance.getStringLabel());
			int i=0;
			for (float f : instance.getFeatureVector().getDenseVector())
				join.add(i+++":"+f);
			out.println(join.toString());
		}
		
		out.close();
	}
	
	public double validate(List<Instance> trnData, List<Instance> tstData)
	{
//		WeightVector w = new WeightVector();
//		OnlineOptimizer optimizer = new AdaGrad(w, 0.001f, 0f);
		FeedForwardNeuralNetwork optimizer = new FeedForwardNeuralNetworkSoftmax(new int[]{100,100,100}, new ActivationFunction[]{new SigmoidFunction(),new SigmoidFunction(),new SigmoidFunction()}, 0.01f, 0f, new RandomWeightGenerator(-2f, 2f), new float[]{1f,1f,1f});
		double bestScore = 0, score, bestSen = 0, bestSpc = 0;
		int tp, tn, fp, fn, bestIdx = 0;
		String outFile = "/Users/jdchoi/Desktop/tmp/spectra-trn.csv.ann";
		PrintStream fout = IOUtils.createBufferedPrintStream(outFile);
				
		for (int j=0; j<100; j++)
		{
			for (Instance instance : trnData)
				optimizer.train(instance);
			
			tp = tn = fp = fn = 0;
			
			for (Instance instance : trnData)
			{
				float[] scores = optimizer.scores(instance.getFeatureVector());
				String y = optimizer.getLabel(MLUtils.argmax(scores));
				if (j==32) fout.println(y);
				
				if (instance.isStringLabel("1"))
				{
					if (y.equals("1")) tp++;
					else fp++;
				}
				else
				{
					if (y.equals("1")) fn++;
					else tn++;
				}
			}
			
			score = MathUtils.accuracy(tp+tn, tp+tn+fp+fn);
			double sen = 100d * tp / (tp+fn);
			double spc = 100d * tn / (tn+fp);
			System.out.printf("%3d: ACC = %5.2f, SEN = %5.2f, SPC = %5.2f, tp = %d, tn = %d, fp = %d, fn = %d\n", j, score, sen, spc, tp, tn, fp, fn);
			if (score > bestScore)
			{
				bestScore = score;
				bestSen = sen;
				bestSpc = spc;
				bestIdx = j;
			}
		}
		
		fout.close();
		System.out.printf("ACC = %5.2f, SEN = %5.2f, SPC = %5.2f, IDX = %d\n\n", bestScore, bestSen, bestSpc, bestIdx);
		return bestScore;
	}
	
	public List<Instance> getData(String filename) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(filename), CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		List<Instance> instances = new ArrayList<>();
		CSVRecord record;
		
		for (int i=0; i<records.size(); i++)
		{
			record = records.get(i);

			float[] f = new float[record.size()-1];
			
			for (int j=1; j<record.size(); j++)
				f[j-1] = Float.parseFloat(record.get(j));
			
			instances.add(new Instance(record.get(0), f));
		}
		
		parser.close();
		return instances;
	}
		
	static public void main(String[] args)
	{
		try
		{
			String trnFile = "/Users/jdchoi/Desktop/tmp/spectra-trn.csv";
			String tstFile = "/Users/jdchoi/Desktop/tmp/spectra-tst.csv";
			new CSVSpectra(trnFile, tstFile);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
