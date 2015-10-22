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
package edu.emory.mathcs.nlp.emorynlp.component.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.emorynlp.component.node.FeatMap;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVReader
{
	static public String BLANK = StringConst.UNDERSCORE;
	private BufferedReader reader;
	
	public int form;
	public int lemma;
	public int pos;
	public int nament;
	public int feats;
	public int dhead;
	public int deprel;
	
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
	
	public <N extends NLPNode>N[] next(Supplier<N> supplier) throws IOException
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
		
		return list.isEmpty() ? null : toNodeList(supplier, list);
	}
	
	@SuppressWarnings("unchecked")
	public <N extends NLPNode>N[] toNodeList(Supplier<N> supplier, List<String[]> values)
	{
		int i, size = values.size();
		N[] nodes = (N[])Array.newInstance(supplier.get().getClass(), size+1);
		
		nodes[0] = supplier.get();
		nodes[0].setToRoot();
		
		for (i=1; i<=size; i++)
			nodes[i] = create(supplier, values.get(i-1), i);
		
		if (dhead >= 0)
		{
			for (i=1; i<=size; i++)
				initHead(i, nodes, values.get(i-1));
		}
		
		return nodes;
	}
	
	protected <N extends NLPNode>N create(Supplier<N> supplier, String[] values, int id)
	{
		String  f = (form   >= 0) ? values[form]   : null;
		String  l = (lemma  >= 0) ? values[lemma]  : null;
		String  p = (pos    >= 0) ? values[pos]    : null;
		String  n = (nament >= 0) ? values[nament] : null;
		FeatMap t = (feats  >= 0) ? new FeatMap(values[feats]) : new FeatMap();
		
		N node = supplier.get();
		node.set(id, f, l, p, n, t, null, null);
		return node;
	}
	
	private <N extends NLPNode>void initHead(int id, N[] nodes, String[] values)
	{
		int headID = Integer.parseInt(values[dhead]);
		nodes[id].setDependencyHead(nodes[headID], values[deprel]);
	}
}
