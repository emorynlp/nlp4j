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
package edu.emory.mathcs.nlp.bin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.util.PTBTag;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class QTGenerate
{
	MorphAnalyzer analyzer;
	
	public QTGenerate()
	{
		analyzer = new EnglishMorphAnalyzer();
	}
	
	public void extract(String input_file)
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(input_file));
		CTTree tree;
		CTNode node;
		
		ObjectIntPair<PrintStream> sbarq = new ObjectIntPair<>(IOUtils.createBufferedPrintStream(input_file+".sbarq"), 0);
		ObjectIntPair<PrintStream> sq = new ObjectIntPair<>(IOUtils.createBufferedPrintStream(input_file+".sq"), 0);
		ObjectIntPair<PrintStream> imp = new ObjectIntPair<>(IOUtils.createBufferedPrintStream(input_file+".imp"), 0);
		
		while ((tree = reader.next()) != null)
		{
			if (tree.getRoot().getChildrenSize() > 1) continue;
			node = tree.getRoot().getFirstChild();
			
			if (node.isSyntacticTag(PTBTag.C_SBARQ))
				print(sbarq, tree);
			else if (node.isSyntacticTag(PTBTag.C_SQ))
				print(sq, tree);
			else if (node.isFunctionTag(PTBTag.F_IMP))
				print(imp, tree);
		}
		
		System.out.printf("SBARQ: %4d\n", sbarq.i);
		System.out.printf("SQ   : %4d\n", sq.i);
		System.out.printf("IMP  : %4d\n", imp.i);
		
		reader.close();
		sbarq.o.close();
		sq.o.close();
		imp.o.close();
	}
	
	void print(ObjectIntPair<PrintStream> p, CTTree tree)
	{
		p.o.println(tree.toStringLine()+"\n");
		p.i++;	
	}
	
	public void count(String input_file)
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(input_file));
		Map<String,List<String>> map = new HashMap<>();
		CTTree tree;
		
		while ((tree = reader.next()) != null)
		{
			CTNode node = tree.getRoot().getFirstChild();
			String key = "OTHERS";
			
			if (node.isFunctionTag("IMP"))
				key = "IMP";
			else if (node.isSyntacticTag("SBARQ"))
				key = "SBARQ";
			else if (node.isSyntacticTag("SQ"))
				key = "SQ";
			else if (node.isSyntacticTag("FRAG"))
				key = "FRAG";
//			else
//				System.out.println(tree.toString());
			
			map.computeIfAbsent(key, k -> new ArrayList<>()).add(tree.toStringLine());
		}

		for (Entry<String,List<String>> e : map.entrySet())
		{
			System.out.println(e.getKey()+": "+e.getValue().size());
			List<String> list = e.getValue();
		}
	}
	
	static public void main(String[] args) throws Exception
	{
//		String parse_file = "/Users/jdchoi/Documents/Data/english/ontonotes.parse";
//		parse_file = "/Users/jdchoi/Documents/Data/english/question.parse";
//		
//		QTGenerate qt = new QTGenerate();
////		qt.extract(parse_file);
//		qt.count(parse_file);
		
		CTReader reader = new CTReader(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/english/genia.ptb"));
		CTTree tree;
		int sc = 0, wc = 0;
		
		while ((tree = reader.next()) != null)
		{
			sc++;
			wc += tree.getTokens().size();
		}
		
		reader.close();
		System.out.println(wc+" "+sc);
	}

}
