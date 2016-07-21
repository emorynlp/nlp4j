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

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentLexicon
{
	static public Object2FloatMap<String> getLexicons(InputStream in) throws IOException
	{
		Object2FloatMap<String> map = new Object2FloatOpenHashMap<>();
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitTabs(line);
			if (skip(t[0])) continue;
			map.put(t[0], Float.parseFloat(t[1]));
		}
		
		return map;
	}
	
	static boolean skip(String form)
	{
		char[] cs = form.toCharArray();
		
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
			Object2FloatMap<String> tree = getLexicons(new FileInputStream(INPUT_FILE));
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(OUTPUT_FILE);
			out.writeObject(tree);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}