/**
 * Copyright 2014, Emory University
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

import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishApostrophe extends Dictionary
{
	private final String[] APOSTROPHE_SUFFIXES = {"d","m","s","t","z","ll","nt","re","ve"};

	public String[] tokenize(String original, String lower, char[] lcs)
	{
		int i;
		
		for (String suffix : APOSTROPHE_SUFFIXES)
		{
			i = isApostropheSuffix(lower, lcs, suffix);
			if (i > 0) return Splitter.split(original, i);
		}
		
		return null;
	}
	
	private int isApostropheSuffix(String lower, char[] lcs, String suffix)
	{
		if (lower.endsWith(suffix))
		{
			if (suffix.equals("t"))		// n't
			{
				int i = lower.length() - suffix.length() - 2;
				
				if (0 < i && lcs[i] == 'n' && CharUtils.isApostrophe(lcs[i+1]))
					return i;
			}
			else
			{
				int i = lower.length() - suffix.length() - 1;
				
				if (0 < i && CharUtils.isApostrophe(lcs[i]))
					return (suffix.equals("s") && CharUtils.isDigit(lcs[i-1])) ? -1 : i;
			}
		}
		
		return -1;
	}
}
