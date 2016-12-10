/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.structure.conversion;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.ngram.Bigram;
import edu.emory.mathcs.nlp.common.collection.ngram.Unigram;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.dep.DEPArc;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;
import edu.emory.mathcs.nlp.structure.util.DDGTag;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishC2DConverterTest
{
//	@Test
	public void test()
	{
		final String headrule_file = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-headrules.txt";
		final String eventive_noun_file = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-eventive-nouns.txt";
		HeadRuleMap headrule_map = new HeadRuleMap(IOUtils.createFileInputStream(headrule_file));
		Set<String> eventive_nouns = IOUtils.readSet(IOUtils.createFileInputStream(eventive_noun_file));
		
		C2DConverter c2d = new EnglishC2DConverter(headrule_map, eventive_nouns);
		CTReader reader = new CTReader();
		String input_file, output_file;
		PrintStream fout;
		NLPGraph graph;
		CTTree tree;
		int count = 0;
		
		String corpus = "/Users/jdchoi/Documents/Data/english/mipacq";
		corpus = "src/test/resources/conversion/tmp";
		input_file  = corpus+".parse";
		output_file = corpus+".ddg";
		reader.open(IOUtils.createFileInputStream(input_file));
		fout = IOUtils.createBufferedPrintStream(output_file);
		
		while ((tree = reader.next()) != null)
		{
			if (++count%1000 == 0) System.out.println(count);
			graph = c2d.toDependencyGraph(tree);
			if (graph != null) fout.println(graph.toString()+"\n");
			else System.err.println("NULL");
			System.out.println(graph.toString()+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	@Test
	public void printLabels()
	{
		final String headrule_file = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-headrules.txt";
		final String eventive_noun_file = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-eventive-nouns.txt";
		HeadRuleMap headrule_map = new HeadRuleMap(IOUtils.createFileInputStream(headrule_file));
		Set<String> eventive_nouns = IOUtils.readSet(IOUtils.createFileInputStream(eventive_noun_file));
		
		C2DConverter c2d = new EnglishC2DConverter(headrule_map, eventive_nouns);
		CTReader reader = new CTReader();
		NLPGraph graph;
		CTTree tree;
		
		final String path = "/Users/jdchoi/Documents/Data/english/";
		final String[] corpora = {"ontoNotes", "web", "question", "mipacq", "sharp", "thyme"};
		Object2IntMap<String> primary = new Object2IntOpenHashMap<>();
		Object2IntMap<String> secondary = new Object2IntOpenHashMap<>();
		Object2IntMap<String> semantic = new Object2IntOpenHashMap<>();
		Bigram<String,String> syn2sem = new Bigram<>();
		
		for (int i=0; i<corpora.length; i++)
		{
			reader.open(IOUtils.createFileInputStream(path+corpora[i]+".parse"));
			System.out.println(corpora[i]);
			
			while ((tree = reader.next()) != null)
			{
				graph = c2d.toDependencyGraph(tree);
				if (graph == null) continue;
				
				for (NLPNode node : graph)
				{
					FastUtils.increment(primary, node.getDependencyLabel());
					String feat = node.getFeat(DDGTag.FEAT_SEM);
					if (feat != null)
					{
						for (String f : Splitter.splitCommas(feat))
						{
							FastUtils.increment(semantic, f);
							syn2sem.add(node.getDependencyLabel(), f);
						}
					}
					
					for (DEPArc<NLPNode> snd : node.getSecondaryHeads())
						FastUtils.increment(secondary, snd.getLabel());
				}
			}
			
			reader.close();
		}
		
		reader.close();
		
		List<Entry<String,Integer>> list = new ArrayList<>(primary.entrySet());
		Collections.sort(list, Entry.comparingByKey());
		
		for (Entry<String,Integer> e : list)
		{
			String key = e.getKey();
			int val1 = e.getValue();
			int val2 = secondary.getOrDefault(key, 0);
			System.out.println(key+"\t"+val1+"\t"+val2);
		}
		
		list = new ArrayList<>(semantic.entrySet());
		Collections.sort(list, Entry.comparingByKey());
		
		System.out.println("=====");
		
		for (Entry<String,Integer> e : list)
			System.out.println(e.getKey()+"\t"+e.getValue());
		
		System.out.println("=====");
		
		
		List<String> keys = new ArrayList<>(syn2sem.keySet());
		Collections.sort(keys);
		
		List<String> sems = new ArrayList<>(semantic.keySet());
		Collections.sort(sems);
		System.out.println("syn\t"+Joiner.join(sems, "\t"));
		
		for (String key : keys)
		{
			Unigram<String> uni = syn2sem.get(key);
			if (uni == null) continue;
			
			StringJoiner join = new StringJoiner("\t");
			join.add(key);
			
			for (String sem : sems)
				join.add(Integer.toString(uni.get(sem)));
			
			System.out.println(join.toString());
		}
	}
}
