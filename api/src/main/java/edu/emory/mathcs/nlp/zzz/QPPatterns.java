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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.lexicon.constituency.CTNode;
import edu.emory.mathcs.nlp.lexicon.constituency.CTReader;
import edu.emory.mathcs.nlp.lexicon.constituency.CTTree;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class QPPatterns
{
	Pattern CD = Pattern.compile("CD( CD)+");
	Pattern JJR_IN = Pattern.compile("(JJ|JJR|RBR) IN");
	Pattern REMOVE = Pattern.compile("(JJR|RBR|RB|ADVP|DT|PDT|\\$)");
	Pattern SPACE = Pattern.compile("( ){2,}");
	
	public QPPatterns(String[] args) throws Exception
	{
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		CTReader reader = new CTReader();
		String filename = "/Users/jdchoi/Documents/Data/english/onto.parse";
		
		reader.open(IOUtils.createFileInputStream(filename));
		CTTree tree;
		
		while ((tree = reader.next()) != null)
			count(map, tree.getRoot());
		
		List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, Entry.comparingByValue());
		Collections.reverse(list);
		
		int sum = list.stream().mapToInt(a -> a.getValue()).sum();
		int d = 0;
		
		for (Entry<String, Integer> p : list)
		{
			d += p.getValue();
			System.out.printf("%30s%5d%7.2f\n", p.getKey(), p.getValue(), MathUtils.accuracy(d, sum));
		}
	}
	
	void count(Object2IntMap<String> map, CTNode node)
	{
		if (node.isSyntacticTag("QP"))
		{
			String s = Joiner.join(node.getChildren(), " ", n -> n.getSyntacticTag());
			Matcher m = CD.matcher(s); s = m.replaceAll("CD");
			m = JJR_IN.matcher(s); s = m.replaceAll("");
			m = REMOVE.matcher(s); s = m.replaceAll("");
			m = SPACE.matcher(s); s = m.replaceAll(" ").trim();
			if (!s.contains(" ")) s = "";
			
			FastUtils.increment(map, s.trim());
		}
		else
		{
			for (CTNode child : node.getChildren())
				count(map, child);
		}
	}
		
	static public void main(String[] args) throws Exception
	{
		new QPPatterns(args);
	}
}

