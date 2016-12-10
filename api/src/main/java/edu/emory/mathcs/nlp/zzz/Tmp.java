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

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.dep.DEPArc;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.C2DConverter;
import edu.emory.mathcs.nlp.structure.conversion.EnglishC2DConverter;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp() throws Exception
	{
//		String[] filenames = {"ontonotes.tb","web.tb","question.tb","mipacq.tb","sharp.tb","thyme.tb","craft.tb"};
		String[] filenames = {"ontonotes.tb","web.tb","bolt.tb","question.tb"};
		CTReader reader = new CTReader();
		List<CTNode> tokens;
		CTTree tree;
		int sc, wc;
		
		int tsc = 0, twc = 0;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/english/"+filename));
			sc = wc = 0;
			
			while ((tree = reader.next()) != null)
			{
				 tokens = tree.getTokens();

				 if (!tokens.isEmpty())
				 {
					 sc++;
					 wc += tokens.size();					 
				 }
			}
			
			reader.close();
			System.out.printf("%15s%10d%10d\n", filename, sc, wc);
			tsc += sc;
			twc += wc;
		}
		
		System.out.printf("%15s%10d%10d\n", "total", tsc, twc);
	}
	
	
	
	public Tmp(String filename) throws Exception
	{
		final String headrules = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-headrules.txt";
		final String transferVerbs = "src/main/resources/edu/emory/mathcs/nlp/conversion/en-transfer-verbs.txt";
		
		HeadRuleMap headruleMap = new HeadRuleMap(IOUtils.createFileInputStream(headrules));
		Set<String> transferVerbSet = IOUtils.readSet(IOUtils.createFileInputStream(transferVerbs));
		C2DConverter conv = new EnglishC2DConverter(headruleMap);

		CTReader reader = new CTReader();
		reader.open(IOUtils.createFileInputStream(filename));
		NLPNode[] nodes;
		CTTree cTree;
		
//		PrintStream fout = IOUtils.createBufferedPrintStream(filename+".ddg");
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		int snd = 0;
		
		while ((cTree = reader.next()) != null)
		{
			nodes = conv.toDependencyGraph(cTree);
			if (nodes == null) continue;
			for (int i=1; i<nodes.length; i++)
			{
				for (DEPArc<NLPNode> n : nodes[i].getSecondaryHeads())
					FastUtils.increment(map, n.getLabel());
				
				snd += nodes[i].getSecondaryHeads().size();
			}
//			fout.println(Joiner.join(nodes, "\n", 1)+"\n");
		}
		
//		fout.close();
		
		
		System.out.println(snd);
		for (Entry<String,Integer> e : map.entrySet())
			System.out.println(e.getKey()+" "+e.getValue());

		
	}
	
	static public void main(String[] args) throws Exception
	{
		String filename = "/Users/jdchoi/Documents/Data/english/thyme.tb";
//		new Tmp(filename);
		new Tmp();
	}
}

