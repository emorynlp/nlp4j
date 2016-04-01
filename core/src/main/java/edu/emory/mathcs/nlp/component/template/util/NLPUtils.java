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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	static public String FEAT_POS_2ND   = "pos2";
	static public String FEAT_PREDICATE = "pred";

	static public String toStringLine(NLPNode[] nodes, String delim, Field field)
	{
		return Joiner.join(nodes, delim, 1, nodes.length, n -> n.getValue(field));
	}
	
	static public NLPNode[] toNodeArray(List<NLPNode> tokens)
	{
		return toNodeArray(tokens, 0, tokens.size());
	}
	
	static public NLPNode[] toNodeArray(List<NLPNode> tokens, int beginIndex, int endIndex)
	{
		NLPNode[] nodes = new NLPNode[endIndex - beginIndex +1];
		int id = 0;
		
		nodes[id] = new NLPNode();
		nodes[id++].toRoot();
		
		for (int i=beginIndex; i<endIndex; i++)
		{
			nodes[id] = tokens.get(i);
			nodes[id].setID(id++);
		}
			
		return nodes;
	}
	
	static public NLPComponent getComponent(InputStream in)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		OnlineComponent<?> component = null;
		
		try
		{
			component = (OnlineComponent<?>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}

		return component;
	}
	
	static public List<NLPNode[]> getNonStopWords(List<NLPNode[]> document)
	{
		List<NLPNode[]> nonstop = new ArrayList<>();
		NLPNode node;
		
		for (NLPNode[] nodes : document)
		{
			List<NLPNode> sen = new ArrayList<>();
			
			for (int i=1; i<nodes.length; i++)
			{
				node = nodes[i];
				if (!node.isStopWord() && !StringUtils.containsPunctuationOrDigitsOrWhiteSpacesOnly(node.getWordFormSimplified()))
					sen.add(node);
			}
			
			if (!sen.isEmpty())
			{
				NLPNode[] snodes = new NLPNode[sen.size()+1];
				snodes[0] = nodes[0];
				
				for (int i=1; i<snodes.length; i++)
					snodes[i] = sen.get(i-1);
				
				nonstop.add(snodes);
			}
		}
		
		return nonstop;
	}
}
