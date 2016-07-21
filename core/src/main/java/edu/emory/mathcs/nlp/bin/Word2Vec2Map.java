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
import java.util.Map;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Word2Vec2Map
{
	static public Map<String,float[]> getEmbeddings(InputStream in) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Map<String,float[]> map = new HashMap<>();
		String[] t;
		float[] v;
		
		t =  Splitter.splitSpace(reader.readLine());
		final int V = Integer.parseInt(t[0]);
		final int D = Integer.parseInt(t[1]);
		
		for (int i=0; i<V; i++)
		{
			t = Splitter.splitSpace(reader.readLine());
			if (skip(t[0])) continue;
			v = new float[D];
			
			for (int j=0; j<D; j++)
				v[j] = Float.parseFloat(t[j+1]);
			
			map.put(t[0], v);
		}
		
		return map;
	}
	
	static boolean skip(String form)
	{
		char[] cs = form.toCharArray();
		if (cs.length < 3 || cs.length > 20) return true;
		
		for (int i=0; i<cs.length; i++)
		{
			if (cs[i] == '_' || cs[i] >= 128)
				return true;
		}
		
		return false;
	}
	
	static public void main(String[] args)
	{
		final String INPUT_FILE  = args[0];
		final String OUTPUT_FILE = args[1];
		
		try
		{
			Map<String,float[]> map = getEmbeddings(new FileInputStream(INPUT_FILE));
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(OUTPUT_FILE);
			out.writeObject(map);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}