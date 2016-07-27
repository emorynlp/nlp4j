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
package edu.emory.mathcs.nlp.component.tokenizer.dictionary;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Compound extends Dictionary
{
	private Map<String,int[]> m_compound;
	
	public Compound(Language language)
	{
		switch (language)
		{
		case ENGLISH: init(IOUtils.getInputStreamsFromResource(ROOT+"english-compounds.txt")); break;
		default: throw new IllegalArgumentException(language.toString());
		}
	}
	
	public Compound(InputStream in)
	{
		init(in);
	}
	
	public void init(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		m_compound = new HashMap<>();
		StringBuilder build;
		String line, token;
		String[] tokens;
		int i, size;
		int[] tmp;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				tokens = Splitter.splitSpace(line.trim());
				build  = new StringBuilder();
				size   = tokens.length - 1;
				tmp    = new int[size];
				
				for (i=0; i<size; i++)
				{
					token  = tokens[i];
					tmp[i] = build.length() + token.length();
					build.append(token);
				}
				
				build.append(tokens[size]);
				m_compound.put(StringUtils.toLowerCase(build.toString()), tmp);
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	@Override
	public String[] tokenize(String original, String lower, char[] lcs)
	{
		int[] indices = m_compound.get(lower);
		return (indices != null) ? Splitter.split(original, indices) : null;
	}
}
