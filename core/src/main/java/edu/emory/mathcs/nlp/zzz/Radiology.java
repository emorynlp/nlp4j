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

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGrad;
import edu.emory.mathcs.nlp.learning.util.FeatureMap;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Radiology
{
	FeatureMap feature_map;
	
	public Radiology(String[] args) throws Exception
	{
		final String STOP_WORDS = "/Users/jdchoi/Documents/EmoryNLP/english-models/src/main/resources/edu/emory/mathcs/nlp/english/lexica/en-stop-words-simplified-uncapitalized.xz";
		final String TSV_FILE   = "/Users/jdchoi/Emory/radiology/dat/radiology_report_151112_falgun.tsv";
		final int COL = 6;
		
		Set<String> stopwords = getStopWordSet(STOP_WORDS);
		List<Instance> trn, dev;
		double bestScore = 0, score, avg;
		int correct = 0;
		
		avg = 0;
		
		for (int i=0; i<10; i++)
		{
			feature_map = new FeatureMap();
			List<Instance> instances = getInstances(TSV_FILE, COL, stopwords);
			WeightVector w = new WeightVector();
			OnlineOptimizer optimizer = new AdaGrad(w, 0.001f, 0f);
			
			Collections.shuffle(instances);
			trn = instances.subList(0, 400);
			dev = instances.subList(400, 500);
			bestScore = 0;
			
			for (int j=0; j<20; j++)
			{
				for (Instance instance : trn)
					optimizer.train(instance);
				
				correct = 0;
				
				for (Instance instance : dev)
				{
					float[] scores = optimizer.scores(instance.getFeatureVector());
					String y = optimizer.getLabel(MLUtils.argmax(scores));
					if (instance.isStringLabel(y)) correct++;
				}
				
				score = MathUtils.accuracy(correct, dev.size());
				if (score > bestScore) bestScore = score;
			}
			
			System.out.printf("%5.2f\n", bestScore);
			avg += bestScore;
		}
		
		System.out.printf("Avg: %5.2f\n", avg/10);
	}
	
	@SuppressWarnings("unchecked")
	Set<String> getStopWordSet(String filename) throws Exception
	{
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(filename);
		Set<String> set = (Set<String>)in.readObject();
		return set;
	}
	
	List<Instance> getInstances(String filename, int index, Set<String> stopwords) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(filename);
		List<Instance> instances = new ArrayList<>();
		Set<String> set;
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitTabs(line);
			set = toSet(t[1], stopwords);
			instances.add(new Instance(t[index], toSparseVector(set)));
		}
		
		return instances;
	}
	
	SparseVector toSparseVector(Set<String> set)
	{
		SparseVector vector = new SparseVector();
		
		for (String s : set)
			vector.add(feature_map.add(0, s));

		vector.sort();
		return vector;
	}
	
	Set<String> toSet(String s, Set<String> stopwords)
	{
		Set<String> set = new HashSet<>();

		for (String t : Splitter.splitSpace(s))
		{
			t = toWord(t);
			
			if (!t.isEmpty() && !stopwords.contains(t))
				set.add(t);
		}
		
		return set;
	}
	
	String toWord(String t)
	{
		StringBuilder build = new StringBuilder();
		
		for (char c : t.toCharArray())
		{
			if (!CharUtils.isPunctuation(c))
				build.append(c);
		}
		
		return StringUtils.toLowerCaseSimplifiedForm(build.toString());
	}
	
	static public void main(String[] args) throws Exception
	{
		new Radiology(args);
	}
}
