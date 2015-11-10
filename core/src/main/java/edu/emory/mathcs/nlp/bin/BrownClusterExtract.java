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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BrownClusterExtract
{
	static public Map<String,Set<String>> getBrownClusters(InputStream in) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Map<String,Set<String>> map = new HashMap<>();
		Set<String> v;
		String line;
		String[] t;
		int i, len;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitTabs(line);
			v = new HashSet<String>();
			len = t[0].length();
			
			for (i=2; ; i+=2)
			{
				if (len <= i)
				{
					v.add(t[0].substring(0, len));
					break;
				}
				else
					v.add(t[0].substring(0, i));
				
			}
			
			map.put(t[1], v);
		}
		
		return map;
	}
	
	static public void main(String[] args)
	{
		final String INPUT_FILE  = args[0];
		final String OUTPUT_FILE = args[1];
		
		try
		{
			Map<String,Set<String>> tree = getBrownClusters(new FileInputStream(INPUT_FILE));
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(OUTPUT_FILE);
			out.writeObject(tree);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}