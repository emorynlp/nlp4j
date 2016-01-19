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
package edu.emory.mathcs.nlp.bin;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.ngram.Bigram;
import edu.emory.mathcs.nlp.common.collection.ngram.Unigram;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AmbiguityClassGenerator
{
	public void readTrain(Bigram<String,String> bigram, String dirpath, boolean uncaptialized) throws Exception
	{
		BufferedReader reader;
		String line, form;
		String[] t;
		
		for (String filename : FileUtils.getFileList(dirpath, "*"))
		{
			System.out.println(filename);
			reader = IOUtils.createBufferedReader(filename);
			
			while ((line = reader.readLine()) != null)
			{
				if (line.trim().isEmpty()) continue;
				t = Splitter.splitTabs(line);
				form = StringUtils.toSimplifiedForm(t[1], uncaptialized);
				if (!skip(form)) bigram.add(form, t[3]);
			}
			
			reader.close();
		}
	}
	
	private boolean skip(String form)
	{
		char[] cs = form.toCharArray();
		
		for (int i=0; i<cs.length; i++)
		{
			if (cs[i] == '_' || cs[i] >= 128)
				return true;
		}
		
		return false;
	}
	
	public void printVocab(Bigram<String,String> bigram, String outputFile, int cutoff, double threshold) throws Exception
	{
		Map<String,List<String>> map = new HashMap<>();
		List<ObjectDoublePair<String>> list;
		Unigram<String> unigram;
		int count = 0, tags = 0;
		
		for (Entry<String, Unigram<String>> e : bigram.entrySet())
		{
			unigram = e.getValue();
			if (unigram.getTotalCount() < cutoff) continue;
			list = unigram.toList(threshold);
			if (list.isEmpty()) continue;
			if (list.size() == 1 && (list.get(0).o.equals("NNP") || list.get(0).o.equals("NNPS"))) continue;
			
			Collections.sort(list, Collections.reverseOrder());
			map.put(e.getKey(), list.stream().map(p -> p.o).collect(Collectors.toList()));
			tags += list.size();
			count++;
		}
		
		System.out.println("Avg tags: "+((double)tags/count));
		System.out.println("Words: "+count);
		
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(outputFile);
		fout.writeObject(map);
		fout.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		Bigram<String,String> bigram = new Bigram<>();
		AmbiguityClassGenerator cov = new AmbiguityClassGenerator();
		
		final String outputFile = args[0];
		final int cutoff = Integer.parseInt(args[1]);
		final double threshold  = Double.parseDouble(args[2]);
		final boolean uncaptialized = Boolean.parseBoolean(args[3]);
		
		cov.readTrain (bigram, "/mnt/ainos-research/henryyhc/dat/nytimes/tree", uncaptialized);
		cov.readTrain (bigram, "/mnt/ainos-research/henryyhc/dat/wikipedia2015/tree", uncaptialized);
		cov.readTrain (bigram, "/home/jdchoi/dat/en-general/trn-pos", uncaptialized);
		cov.readTrain (bigram, "/home/jdchoi/dat/en-medical/trn-pos", uncaptialized);
		cov.readTrain (bigram, "/home/jdchoi/dat/en-bioinformatics/trn-pos", uncaptialized);
		cov.printVocab(bigram, outputFile, cutoff, threshold);
	}
}
