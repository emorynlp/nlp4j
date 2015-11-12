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
package edu.emory.mathcs.nlp.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Splitter implements PatternConst
{
	static public CharTokenizer T_UNDERSCORE  = new CharTokenizer('_');
	static public CharTokenizer T_HYPHEN      = new CharTokenizer('-');
	static public CharTokenizer T_SPACE       = new CharTokenizer(' ');
	static public CharTokenizer T_COMMA       = new CharTokenizer(',');
	static public CharTokenizer T_COLON       = new CharTokenizer(':');
	static public CharTokenizer T_PIPE        = new CharTokenizer('|');
	static public CharTokenizer T_TAB         = new CharTokenizer('\t');
	
	static public String[] split(String s, Pattern p)
	{
		return p.split(s);
	}
	
	static public String[] splitSpace(String s)
	{
		return T_SPACE.tokenize(s);
	}
	
	static public List<String> splitTabsToList(String s)
	{
		return T_TAB.tokenizeToList(s);
	}
	
	static public String[] splitTabs(String s)
	{
		return T_TAB.tokenize(s);
	}
	
	static public String[] splitUnderscore(String s)
	{
		return T_UNDERSCORE.tokenize(s);
	}

	static public String[] splitHyphens(String s)
	{
		return T_HYPHEN.tokenize(s);
	}
	
	static public String[] splitCommas(String s)
	{
		return T_COMMA.tokenize(s);
	}
	
	static public String[] splitCommas(String s, boolean includeEmpty)
	{
		return T_COMMA.tokenize(s, includeEmpty);
	}
	
	static public String[] splitColons(String s)
	{
		return T_COLON.tokenize(s);
	}
	
	static public String[] splitPipes(String s)
	{
		return T_PIPE.tokenize(s);
	}
	
	static public List<String> splitIncludingMatches(Pattern p, String s)
	{
		ArrayList<String> list = new ArrayList<>();
		Matcher m = p.matcher(s);
		int last = 0, curr;
		
		while (m.find())
		{
			curr = m.start();
			
			if (last < curr)
				list.add(s.substring(last, curr));
			
			last = m.end();
			list.add(m.group());
		}
		
		if (last < s.length())
			list.add(s.substring(last));
		
		list.trimToSize();
		return list;
	}
	
	static public String[] split(String s, int... splitIndices)
	{
		int i, len = splitIndices.length;
		String[] t = new String[len+1];
		int beginIndex = 0;
		
		for (i=0; i<len; i++)
		{
			t[i] = s.substring(beginIndex, splitIndices[i]);
			beginIndex = splitIndices[i];
		}
		
		t[i] = s.substring(beginIndex);
		return t;
	}
}