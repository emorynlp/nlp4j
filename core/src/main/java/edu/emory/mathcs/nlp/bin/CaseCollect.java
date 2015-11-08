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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
			
			if (count[1] > cutoff && MathUtils.divide(count[0], count[1]) > threshold)
				set.add(e.getKey());
		}
		
		return set;
	}
	
	@SuppressWarnings("unchecked")
	static public void main(String[] args) throws Exception
	{
		ObjectInputStream  in  = IOUtils.createObjectXZBufferedInputStream (args[0]);
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(args[0]+"."+args[1]+"."+args[2]+".xz");
		int cutoff = Integer.parseInt(args[1]);
		double threshold = Double.parseDouble(args[2]);
		
		CaseCollect collect = new CaseCollect();
		Map<String,int[]> map = (Map<String,int[]>)in.readObject();
		Set<String> set = collect.shrink(map, cutoff, threshold);
		System.out.println(map.size()+" -> "+set.size());
		
		out.writeObject(set);
		out.close();
	}
}
