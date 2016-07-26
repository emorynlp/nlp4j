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
package edu.emory.mathcs.nlp.common.collection.tree;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import java.util.Collection;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharAffixTree
{
	private CharAffixNode n_root;
	private boolean b_prefix;
	
	public CharAffixTree(boolean prefix)
	{
		init(prefix);
	}
	
	public CharAffixTree(boolean prefix, Collection<String> col)
	{
		init(prefix);
		addAll(col);
	}
	
	public CharAffixTree(boolean prefix, String[] array)
	{
		init(prefix);
		addAll(array);
	}
	
	private void init(boolean prefix)
	{
		n_root = new CharAffixNode();
		b_prefix = prefix;
	}
	
	public void addAll(Collection<String> col)
	{
		for (String s : col)
			add(s);
	}
	
	public void addAll(String[] array)
	{
		for (String s : array)
			add(s);
	}
	
	public void add(String s)
	{
		int i, beginIndex, direction, len = s.length();
		char[] cs = s.toCharArray();
		CharAffixNode curr, next;
		
		if (b_prefix)
		{
			beginIndex = 0;
			direction  = 1;
		}
		else
		{
			beginIndex = len - 1;
			direction  = -1;
		}
		
		curr = n_root;
		
		for (i=beginIndex; 0<=i&&i<len; i+=direction)
		{
			next = curr.get(cs[i]);
			
			if (next == null)
			{
				next = new CharAffixNode();
				curr.put(cs[i], next);
			}
			
			curr = next;
		}
		
		curr.setEndState(true);
	}
	
	public int getAffixIndex(String s, boolean minimum)
	{
		int i, beginIndex, direction, index = -1, len = s.length();
		char[] cs = s.toCharArray();
		CharAffixNode curr = n_root;
		
		if (b_prefix)
		{
			beginIndex = 0;
			direction  = 1;
		}
		else
		{
			beginIndex = len - 1;
			direction  = -1;
		}
		
		for (i=beginIndex; 0<=i&&i<len; i+=direction)
		{
			curr = curr.get(cs[i]);
			if (curr == null) break;
			
			if (curr.isEndState())
			{
				index = i;
				if (minimum) break;
			}
		}
		
		return index;
	}
	
	private class CharAffixNode extends Char2ObjectOpenHashMap<CharAffixNode>
	{
		private static final long serialVersionUID = 1566684742873455351L;
		private boolean b_endState;
		
		public CharAffixNode()
		{
			b_endState = false;
		}
		
		public boolean isEndState()
		{
			return b_endState;
		}
		
		public void setEndState(boolean endState)
		{
			b_endState = endState;
		}
	}
}
