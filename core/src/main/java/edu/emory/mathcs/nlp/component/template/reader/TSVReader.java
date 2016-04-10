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
package edu.emory.mathcs.nlp.component.template.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class TSVReader<N extends AbstractNLPNode<N>>
{
	static public String BLANK = StringConst.UNDERSCORE;
	protected BufferedReader reader;
	
	public int form   = -1;
	public int lemma  = -1;
	public int pos    = -1;
	public int nament = -1;
	public int feats  = -1;
	public int dhead  = -1;
	public int deprel = -1;
	public int sheads = -1;
	
	public TSVReader() {}
	
	public TSVReader(Object2IntMap<String> map)
	{
		form   = map.getOrDefault("form"  , -1);
		lemma  = map.getOrDefault("lemma" , -1);
		pos    = map.getOrDefault("pos"   , -1);
		nament = map.getOrDefault("nament", -1);
		feats  = map.getOrDefault("feats" , -1);
		dhead  = map.getOrDefault("dhead" , -1);
		deprel = map.getOrDefault("deprel", -1);
		sheads = map.getOrDefault("sheads", -1);
	}
	
	public void open(InputStream in)
	{
		reader = IOUtils.createBufferedReader(in);		
	}
	
	public void close()
	{
		try
		{
			if (reader != null)
				reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public List<N[]> readDocument() throws Exception
	{
		List<N[]> document = new ArrayList<>();
		N[] nodes;
		
		while ((nodes = next()) != null)
			document.add(nodes);
		
		return document;
	}
	
	public N[] next() throws IOException
	{
		List<String[]> list = new ArrayList<>();
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			
			if (line.isEmpty())
			{
				if (list.isEmpty()) continue;
				break;
			}
			
			list.add(Splitter.splitTabs(line));
		}
		
		return list.isEmpty() ? null : toNodeList(list);
	}
	
	@SuppressWarnings("unchecked")
	public N[] toNodeList(List<String[]> list)
	{
		int i, size = list.size();
		N node = create();
		N[] nodes = (N[])Array.newInstance(node.getClass(), size+1);
		
		node.toRoot();
		nodes[0] = node;
		
		for (i=1; i<=size; i++)
			nodes[i] = create(i, list.get(i-1));
		
		if (dhead >= 0)
		{
			for (i=1; i<=size; i++)
				initDependencyHead(i, list.get(i-1), nodes);
			
			if (sheads >= 0)
			{
				for (i=1; i<=size; i++)
					initSemanticHeads(i, list.get(i-1)[sheads], nodes);
			}
		}
		
		return nodes;
	}
	
	protected N create(int id, String[] values)
	{
		String  f = (form   >= 0) ? values[form]   : null;
		String  l = (lemma  >= 0) ? values[lemma]  : null;
		String  p = (pos    >= 0) ? values[pos]    : null;
		String  n = (nament >= 0) ? values[nament] : null;
		FeatMap t = (feats  >= 0) ? new FeatMap(values[feats]) : new FeatMap();
		
		N node = create();
		node.set(id, f, l, p, n, t, null, null);
		return node;
	}
	
	protected abstract N create();
	
	protected String getValue(String[] values, int index, boolean tag)
	{
		if (index < 0 || values.length <= index) return null;
		String s = values[index];
		return tag && BLANK.equals(s) ? null : s; 
	}
	
	protected void initDependencyHead(int id, String[] values, N[] nodes)
	{
		if (BLANK.equals(values[dhead])) return;
		int headID = Integer.parseInt(values[dhead]);
		nodes[id].setDependencyHead(nodes[headID], values[deprel]);
	}
	
	protected void initSemanticHeads(int id, String value, N[] nodes)
	{
		if (BLANK.equals(value)) return;
		N node = nodes[id];
		int headID;
		String[] t;
		
		for (String arg : Splitter.splitSemiColons(value))
		{
			t = Splitter.splitColons(arg);
			headID = Integer.parseInt(t[0]);
			node.addSemanticHead(nodes[headID], t[1]);
		}			
	}
}
