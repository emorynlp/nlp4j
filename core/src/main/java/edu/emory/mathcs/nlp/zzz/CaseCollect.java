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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CaseCollect
{
	public Map<String,int[]> createMap(InputStream in) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Map<String,int[]> map = new HashMap<>();
		String line, l;
		int[] count;
		
		while ((line = reader.readLine()) != null)
		{
			for (String s : Splitter.splitSpace(line))
			{
				s = StringUtils.toSimplifiedForm(s);
				l = StringUtils.toLowerCase(s);
				count = map.computeIfAbsent(l, k -> new int[]{0,0});
				if (s.equals(l)) count[0]++;
				count[1]++;
			}
		}
		
		reader.close();
		return map;
	}
	
	public Set<String> shrink(Map<String,int[]> map, int cutoff, double threshold) throws Exception
	{
		Set<String> set = new HashSet<>();
		int[] count;
		
		for (Entry<String,int[]> e : map.entrySet())
		{
			count = e.getValue();
			
			if (count[1] > cutoff && MathUtils.divide(count[0], count[1]) > threshold && isAlphaNum(e.getKey()))
				set.add(e.getKey());
		}
		
		return set;
	}
	
	public boolean isAlphaNum(String s)
	{
		char[] cs = s.toCharArray();
		boolean alpha = false;
		char c;
		
		if (cs[0] == '#' || cs[0] == '%' || cs[0] == '@' || cs[0] == '(')
			return false;
		
		for (int i=0; i<cs.length; i++)
		{
			c = cs[i];
			alpha = alpha || CharUtils.isAlphabet(c);
			
			if (!CharUtils.isAlphabet(c) && !CharUtils.isPunctuation(c))
				return false;
		}
		
		return alpha;
	}
	
	@SuppressWarnings("unchecked")
	static public void main(String[] args) throws Exception
	{
		final String INPUT_FILE  = args[0];
		final String OUTPUT_FILE = args[0]+"."+args[1]+"."+args[2]+".xz";
		
		ObjectInputStream  in  = IOUtils.createObjectXZBufferedInputStream (INPUT_FILE);
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(OUTPUT_FILE);
		int cutoff = Integer.parseInt(args[1]);
		double threshold = Double.parseDouble(args[2]);
		
		CaseCollect collect = new CaseCollect();
		Map<String,int[]> map = (Map<String,int[]>)in.readObject();
		Set<String> set = collect.shrink(map, cutoff, threshold);
		System.out.println(map.size()+" -> "+set.size());
		
		out.writeObject(set);
		out.close();
		
		PrintStream fout = IOUtils.createBufferedPrintStream(OUTPUT_FILE+".txt");
		List<String> list = new ArrayList<>(set);
		Collections.sort(list);
		for (String s : list) fout.println(s);
		fout.close();
	}
}
