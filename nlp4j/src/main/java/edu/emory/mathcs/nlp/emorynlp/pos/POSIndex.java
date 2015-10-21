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
package edu.emory.mathcs.nlp.emorynlp.pos;

import java.util.List;

import edu.emory.mathcs.nlp.emorynlp.utils.node.FeatMap;
import edu.emory.mathcs.nlp.emorynlp.utils.reader.TSVIndex;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSIndex implements TSVIndex<POSNode>
{
	public int form;
	public int lemma;
	public int pos;
	public int feats;
	
	public POSIndex(int form, int pos)
	{
		set(form, -1, pos, -1);
	}
	
	public POSIndex(int form, int lemma, int pos, int feats)
	{
		set(form, lemma, pos, feats);
	}
	
	public void set(int form, int lemma, int pos, int feats)
	{
		this.form  = form;
		this.lemma = lemma;
		this.pos   = pos;
		this.feats = feats;
	}

	@Override
	public POSNode[] toNodeList(List<String[]> values)
	{
		int i, size = values.size();
		POSNode[] nodes = new POSNode[size];
		
		for (i=0; i<size; i++)
			nodes[i] = create(values.get(i), i+1);
		
		return nodes;
	}
	
	private POSNode create(String[] values, int id)
	{
		String  f = (form  >= 0) ? values[form]  : null;
		String  l = (lemma >= 0) ? values[lemma] : null;
		String  p = (pos   >= 0) ? values[pos]   : null;
		FeatMap m = (feats >= 0) ? new FeatMap(values[feats]) : new FeatMap();
		return new POSNode(id, f, l, p, m);
	}
}
