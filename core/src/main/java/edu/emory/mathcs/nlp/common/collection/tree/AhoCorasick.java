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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AhoCorasick<V> implements Serializable
{
	private static final long serialVersionUID = -7636571150961289534L;
	private AhoCorasickNode root_node;
	
	public AhoCorasick()
	{
		root_node = new AhoCorasickNode();
		root_node.setSuffix(root_node);
	}
	
	public AhoCorasickNode getRoot()
	{
		return root_node;
	}
	
	public void setRoot(AhoCorasickNode node)
	{
		root_node = node;
	}
	
	public void add(List<String> tokens, V value)
	{
		AhoCorasickNode node = root_node, t;
		
		for (String token : tokens)
		{
			t = node.getChild(token);
			
			if (t == null)
			{
				t = new AhoCorasickNode();
				t.setParent(node);
				t.setToken(token);
				node.putChild(token, t);
			}

			node = t;
		}
		
		node.setChunkLength(tokens.size());
		node.setValue(value);
	}
	
	public List<ObjectIntIntTriple<V>> get(List<String> tokens)
	{
		List<ObjectIntIntTriple<V>> list = new ArrayList<>();
		int i, j, i1, size = tokens.size();
		AhoCorasickNode node = root_node;
		
		for (i=0; i<size; i++)
		{
			node = get(tokens.get(i), node);
			
			if (node.hasValue())
			{
				i1 = i - node.getChunkLength() + 1;
				
				for (j=list.size()-1; j>=0; j--)
				{
					if (list.get(j).i1 >= i1)	list.remove(j);
					else						break;
				}
				
				list.add(new ObjectIntIntTriple<>(node.getValue(), i1, i+1));
			}
		}
		
		return list;
	}
	
	public AhoCorasickNode get(String token)
	{
		return get(token, root_node);
	}
	
	private AhoCorasickNode get(String token, AhoCorasickNode node)
	{
		AhoCorasickNode t = node.getTransition(token);
		if (t != null) return t;
		
		AhoCorasickNode child = node.getChild(token);
		
		if (child != null)
			t = child;
		else if (node == root_node)
			t = root_node;
		else
			t = get(token, getSuffix(node));
		
		node.putTransition(token, t);
		return t;
	}
	
	private AhoCorasickNode getSuffix(AhoCorasickNode node)
	{
		AhoCorasickNode suffix = node.getSuffix();
		if (suffix != null) return suffix;
		
		suffix = (node.getParent() == root_node) ? root_node : get(node.getToken(), getSuffix(node.getParent()));
		node.setSuffix(suffix);
		return suffix;
	}
	
	private class AhoCorasickNode implements Serializable
	{
		private static final long serialVersionUID = -77952757562106452L;
		private Map<String,Pair<AhoCorasickNode,AhoCorasickNode>> child_transition_map;
		private AhoCorasickNode parent;
		private AhoCorasickNode suffix;
		private int chunk_length;
		private String token;
		private V value;
		
		public AhoCorasickNode()
		{
			child_transition_map = new HashMap<>();
			chunk_length = 0;
			parent = null;
			suffix = null;
			token  = null;
			value  = null;
		}
		
		public AhoCorasickNode getChild(String token)
		{
			Pair<AhoCorasickNode,AhoCorasickNode> p = child_transition_map.get(token);
			return (p != null) ? p.o1 : null;
		}
		
		public AhoCorasickNode getTransition(String token)
		{
			Pair<AhoCorasickNode,AhoCorasickNode> p = child_transition_map.get(token);
			return (p != null) ? p.o2 : null;
		}
		
		public AhoCorasickNode getParent()
		{
			return parent;
		}
		
		public AhoCorasickNode getSuffix()
		{
			return suffix;
		}

		public String getToken()
		{
			return token;
		}
		
		public int getChunkLength()
		{
			return chunk_length;
		}
		
		public V getValue()
		{
			return value;
		}
		
		public void putChild(String token, AhoCorasickNode node)
		{
			child_transition_map.computeIfAbsent(token, k -> new Pair<>()).o1 = node;
		}
		
		public void putTransition(String token, AhoCorasickNode node)
		{
			child_transition_map.computeIfAbsent(token, k -> new Pair<>()).o2 = node;
		}
		
		public void setParent(AhoCorasickNode node)
		{
			parent = node;
		}
		
		public void setSuffix(AhoCorasickNode node)
		{
			suffix = node;
		}
		
		public void setToken(String token)
		{
			this.token = token;
		}
		
		public void setChunkLength(int length)
		{
			chunk_length = length;
		}
		
		public void setValue(V value)
		{
			this.value = value;
		}
		
		public boolean hasValue()
		{
			return value != null;
		}
	}
	
	public static void main(String[] args)
	{
		AhoCorasick<String> ahoCorasick = new AhoCorasick<>();
		ahoCorasick.add(DSUtils.toList("a","b"),"A");
		ahoCorasick.add(DSUtils.toList("a","b","c"),"B");
		ahoCorasick.add(DSUtils.toList("b","c","d"),"C");
		ahoCorasick.add(DSUtils.toList("c","d"),"D");

		List<String> tokens = DSUtils.toList("a","b","c","d","c","d","a","b","c");
		
		for (ObjectIntIntTriple<String> t : ahoCorasick.get(tokens))
			System.out.println(Joiner.join(tokens, " ", t.i1, t.i2)+" - "+t.o);
	}
}