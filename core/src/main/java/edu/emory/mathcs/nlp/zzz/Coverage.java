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
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.StringJoiner;

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
public class Coverage
{
	public void readTrain(Bigram<String,String> bigram, String dirpath) throws Exception
	{
		BufferedReader reader;
		String line;
		String f, p;
		String[] t;
		
		for (String filename : FileUtils.getFileList(dirpath, "*"))
		{
			System.out.println(filename);
			reader = IOUtils.createBufferedReader(filename);
			
			while ((line = reader.readLine()) != null)
			{
				if (line.trim().isEmpty()) continue;
				t = Splitter.splitTabs(line);
				f = StringUtils.toSimplifiedForm(t[1]);
				p = t[3];
				bigram.add(f, p);
			}
			
			reader.close();
		}
	}
	
	public void printVocab(Bigram<String,String> bigram, String outputFile, int cutoff)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		Unigram<String> unigram;
		int count = 0, tags = 0;
		StringJoiner build;
		
		for (Entry<String, Unigram<String>> e : bigram.entrySet())
		{
			unigram = e.getValue();
			if (unigram.getTotalCount() < cutoff) continue;
			build = new StringJoiner(" ");
			build.add(e.getKey());
			
			for (ObjectDoublePair<String> v : unigram.toList(0d))
			{
				build.add(v.o+":"+v.d);
				tags++;
			}
			
			fout.println(build.toString());
			count++;
		}
		
		fout.close();
		System.out.println("Avg tags: "+((double)tags/count));
		System.out.println("Words: "+count);
	}
	
	static public void main(String[] args) throws Exception
	{
		Coverage cov = new Coverage();
		Bigram<String,String> bigram = new Bigram<>();
		cov.readTrain (bigram, "/mnt/ainos-research/henryyhc/dat/nytimes/tree");
		cov.readTrain (bigram, "/mnt/ainos-research/henryyhc/dat/wikipedia2015/tree");
//		cov.readTrain (bigram, "/home/jdchoi/dat/general-en/trn-pos");
		cov.printVocab(bigram, "/mnt/ainos-research/data/word_classes/nytimes-wiki-ambiguity-classes.txt", 0);
	}
}
