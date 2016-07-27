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
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishHyphen
{
	private Set<String> s_prefix;
	private Set<String> s_suffix;
	
	public EnglishHyphen()
	{
		InputStream prefix = IOUtils.getInputStreamsFromResource(Dictionary.ROOT+"english-hyphen-prefix.txt");
		InputStream suffix = IOUtils.getInputStreamsFromResource(Dictionary.ROOT+"english-hyphen-suffix.txt");
		init(prefix, suffix);
	}
	
	public EnglishHyphen(InputStream prefix, InputStream suffix)
	{
		init(prefix, suffix);
	}
	
	public void init(InputStream prefix, InputStream suffix)
	{
		s_prefix = DSUtils.createStringHashSet(prefix, true, true);
		s_suffix = DSUtils.createStringHashSet(suffix, true, true);
	}
	
	public boolean isPrefix(String lower)
	{
		return s_prefix.contains(lower);
	}
	
	public boolean isSuffix(String lower)
	{
		return s_suffix.contains(lower);
	}
	
	public boolean preserveHyphen(char[] cs, int index)
	{
		if (CharUtils.isHyphen(cs[index]) && (index+1 == cs.length || CharUtils.isAlphabet(cs[index+1])))
		{
			int len = cs.length;
			char[] tmp;
			
			if (index > 0)
			{
				tmp = Arrays.copyOfRange(cs, 0, index);
				CharUtils.toLowerCase(tmp);
				
				if (isPrefix(new String(tmp)))
					return true;	
			}
			
			if (index+1 < len)
			{
				tmp = Arrays.copyOfRange(cs, index+1, len);
				CharUtils.toLowerCase(tmp);
				
				if (isSuffix(new String(tmp)))
					return true;	
			}
			
			if (index+2 < len)
			{
				if (CharUtils.isVowel(cs[index+1]) && CharUtils.isHyphen(cs[index+2]))
					return true;
			}
			
			if (0 <= index-2)
			{
				if (CharUtils.isVowel(cs[index-1]) && CharUtils.isHyphen(cs[index-2]))
					return true;
			}
		}
		
		return false;
	}
}
