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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AmbiguityClassGenerator
{
	public AmbiguityClassGenerator(String inputFile, String outputFile, double threshold) throws Exception
	{
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(outputFile);
		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
		Map<String,List<String>> map = new HashMap<>();
		List<ObjectDoublePair<String>> list;
		String line, s;
		String[] t;
		double d;
		int idx;
		
		outer: while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitSpace(line);
			list = new ArrayList<>();
			
			for (int i=1; i<t.length; i++)
			{
				s = t[i];
				idx = s.lastIndexOf(':');
				d = Double.parseDouble(s.substring(idx+1));
				
				if (d > threshold)
					list.add(new ObjectDoublePair<>(s.substring(0, idx), d));

				Collections.sort(list, Collections.reverseOrder());
			}
			
			if (list.size() == 1 && (list.get(0).o.equals("NNP") || list.get(0).o.equals("NNPS"))) continue;
			char[] cs = t[0].toCharArray();
			
			for (int i=0; i<cs.length; i++)
			{
				if (cs[i] == '_') continue outer;
				if (cs[i] >= 128) continue outer;
			}
			
			if (!list.isEmpty())
				map.put(t[0], list.stream().map(p -> p.o).collect(Collectors.toList()));
		}

		System.out.println(map.size());
		out.writeObject(map);
		out.close();
		reader.close();
	}
	
	static public void main(String[] args)
	{
		try
		{
			new AmbiguityClassGenerator(args[0], args[1], Double.parseDouble(args[2]));
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
