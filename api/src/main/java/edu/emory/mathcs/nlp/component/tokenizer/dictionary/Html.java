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

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Html
{
	private Map<String,String> m_tags;
	
	public Html()
	{
		init(IOUtils.getInputStreamsFromResource(Dictionary.ROOT+"html-tags.txt"));
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	public Html(InputStream in)
	{
		init(in);
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	public void init(InputStream in)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String[] t;
		String line;
		
		m_tags = new HashMap<>();
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				t = Splitter.splitTabs(line);
				m_tags.put(t[0], Character.toString((char)Integer.parseInt(t[1])));
			}			
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public String replace(String s)
	{
		Matcher m = PatternConst.HTML_TAG.matcher(s);
		StringBuffer sb = null;
		
		while (m.find())
		{
			if (sb == null) sb = new StringBuffer();
			appendReplacement(sb, m);
		}
		
		if (sb == null)
			return s;
		else
		{
			m.appendTail(sb);
			return sb.toString();
		}
	}
	
	private void appendReplacement(StringBuffer sb, Matcher m)
	{
		String key = m.group(1), value;
		int ascii;
		
		if ((value = m_tags.get(key.toLowerCase())) != null)
			m.appendReplacement(sb, value);
		else if ((ascii = getASCII(key)) != -1)
			m.appendReplacement(sb, Character.toString((char)ascii));
		else
			m.appendReplacement(sb, m.group());
	}
	
	private int getASCII(String s)
	{
		if (s.startsWith(StringConst.POUND))
		{
			s = s.substring(1);
			
			if (StringUtils.containsDigitOnly(s))
			{
				int i = Integer.parseInt(s);
				
				if (32 <= i && i <= 917631)
					return i;
			}
		}
		
		return -1;
	}
}
