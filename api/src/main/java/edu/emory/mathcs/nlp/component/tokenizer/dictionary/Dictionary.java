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

import edu.emory.mathcs.nlp.common.util.CharUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class Dictionary
{
	static public String ROOT = "edu/emory/mathcs/nlp/component/tokenizer/dictionary/";
	
	public String[] tokenize(String s)
	{
		char[] lcs = s.toCharArray();
		String lower = CharUtils.toLowerCase(lcs) ? new String(lcs) : s;
		return tokenize(s, lower, lcs);
	}
	
	/**
	 * @param original the original string.
	 * @param lower the lowercase of the original string.
	 * @param lcs the lowercase character array of the original string.
	 */
	abstract public String[] tokenize(String original, String lower, char[] lcs);
}
