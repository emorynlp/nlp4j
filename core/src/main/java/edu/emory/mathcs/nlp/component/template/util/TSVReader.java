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
package edu.emory.mathcs.nlp.component.template.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVReader
{
	static public String BLANK = StringConst.UNDERSCORE;
	private BufferedReader reader;
	
	public int form   = -1;
	public int lemma  = -1;
	public int pos    = -1;
	public int nament = -1;
	public int feats  = -1;
	public int dhead  = -1;
	public int deprel = -1;
	public int sheads = -1;
	
	public void open(InputStream in)
	{
		reader = IOUtils.createBufferedReader(in);		
	}
	
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public NLPNode[] next() throws IOException
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
	
	public NLPNode[] toNodeList(List<String[]> values)
	{
		int i, size = values.size();
		NLPNode[] nodes = (NLPNode[])Array.newInstance(NLPNode.class, size+1);
		
		nodes[0] = new NLPNode();
		nodes[0].setToRoot();
		
		for (i=1; i<=size; i++)
			nodes[i] = create(values.get(i-1), i);
		
		if (dhead >= 0)
		{
			for (i=1; i<=size; i++)
				initDependencyHead(i, nodes, values.get(i-1));
			
			if (sheads >= 0)
			{
				for (i=1; i<=size; i++)
					initSemanticHeads(i, nodes, values.get(i-1)[sheads]);
			}
		}
		
		return nodes;
	}
	
	protected NLPNode create(String[] values, int id)
	{
		String  f = (form   >= 0) ? values[form]   : null;
		String  l = (lemma  >= 0) ? values[lemma]  : null;
		String  p = (pos    >= 0) ? values[pos]    : null;
		String  n = (nament >= 0) ? values[nament] : null;
		FeatMap t = (feats  >= 0) ? new FeatMap(values[feats]) : new FeatMap();
		return new NLPNode(id, f, l, p, n, t, null, null);
	}
	
	private void initDependencyHead(int id, NLPNode[] nodes, String[] values)
	{
		int headID = Integer.parseInt(values[dhead]);
		nodes[id].setDependencyHead(nodes[headID], values[deprel]);
	}
	
	private void initSemanticHeads(int id, NLPNode[] nodes, String value)
	{
		NLPNode node = nodes[id];
		int headID;
		String[] t;
		
		if (!value.equals(StringConst.UNDERSCORE))
		{
			for (String arg : Splitter.splitSemiColons(value))
			{
				t = Splitter.splitColons(arg);
				headID = Integer.parseInt(t[0]);
				node.addSemanticHead(nodes[headID], t[1]);
			}			
		}
	}
}
