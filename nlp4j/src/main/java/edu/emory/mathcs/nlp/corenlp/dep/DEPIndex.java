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
package edu.emory.mathcs.nlp.corenlp.dep;

import java.util.List;

import edu.emory.mathcs.nlp.corenlp.component.node.FeatMap;
import edu.emory.mathcs.nlp.corenlp.component.reader.TSVIndex;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPIndex implements TSVIndex<DEPNode>
{
	public int form;
	public int lemma;
	public int pos;
	public int feats;
	public int head_id;
	public int deprel;
	
	public DEPIndex(int form, int lemma, int pos, int feats)
	{
		set(form, lemma, pos, feats, -1, -1);
	}
	
	public DEPIndex(int form, int lemma, int pos, int feats, int headID, int deprel)
	{
		set(form, lemma, pos, feats, headID, deprel);
	}
	
	public void set(int form, int lemma, int pos, int feats, int headID, int deprel)
	{
		this.form    = form;
		this.lemma   = lemma;
		this.pos     = pos;
		this.feats   = feats;
		this.head_id = headID;
		this.deprel  = deprel;
	}

	@Override
	public DEPNode[] toNodeList(List<String[]> values)
	{
		int i, size = values.size();
		DEPNode[] nodes = new DEPNode[size+1];
		
		nodes[0] = new DEPNode();
		
		for (i=1; i<=size; i++)
			nodes[i] = create(values.get(i-1), i);
		
		if (head_id >= 0)
		{
			for (i=1; i<=size; i++)
				initHead(i, nodes, values.get(i-1));
		}
		
		return nodes;
	}
	
	private DEPNode create(String[] values, int id)
	{
		String  f = (form  >= 0) ? values[form]  : null;
		String  l = (lemma >= 0) ? values[lemma] : null;
		String  p = (pos   >= 0) ? values[pos]   : null;
		FeatMap m = (feats >= 0) ? new FeatMap(values[feats]) : new FeatMap();
		return new DEPNode(id, f, l, p, m);
	}
	
	private void initHead(int id, DEPNode[] nodes, String[] values)
	{
		int headID = Integer.parseInt(values[head_id]);
		nodes[id].setHead(nodes[headID], values[deprel]);
	}
}
