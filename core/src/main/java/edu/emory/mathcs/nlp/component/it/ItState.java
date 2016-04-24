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
package edu.emory.mathcs.nlp.component.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ItState<N extends AbstractNLPNode<N>> extends NLPState<N>
{
	static public final String FEAT_KEY = "it"; 
	private List<String[]> oracle;
	private int tree_id, node_id;
	
	public ItState(List<N[]> document)
	{
		super(document);
		tree_id = node_id = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================
	
	@Override
	public boolean saveOracle()
	{
		oracle = new ArrayList<>();
		boolean exist = false;
		
		for (N[] nodes : document)
		{
			String[] o = Arrays.stream(nodes).map(n -> n.removeFeat(FEAT_KEY)).toArray(String[]::new);
			oracle.add(o);
			if (!exist) exist = Arrays.stream(o).filter(s -> s != null).findAny().isPresent();
		}

		return exist;
	}

	@Override
	public String getOracle()
	{
		return oracle.get(tree_id)[node_id];
	}
	
	public void resetOracle()
	{
		for (int i=0; i<oracle.size(); i++)
		{
			N[] nodes = document.get(i);
			String[] o = oracle.get(i);

			for (int j=1; j<o.length; j++)
			{
				if (o[j] != null)
					nodes[j].putFeat(FEAT_KEY, o[j]);
			}
		}
	}

//	====================================== GETTERS/SETTERS ======================================
	
	public int getTreeID()
	{
		return tree_id;
	}
	
	public int getNodeID()
	{
		return node_id;
	}

//	====================================== TRANSITION ======================================
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		String label = map.getLabel(top2[0]);
		document.get(tree_id)[node_id].putFeat(FEAT_KEY, label);
		shift();
	}
	
	@Override
	public boolean isTerminate()
	{
		return tree_id >= document.size();
	}
	
	private void shift()
	{
		for (; tree_id < document.size(); tree_id++)
		{
			nodes = document.get(tree_id);
			
			for (node_id++; node_id < nodes.length; node_id++)
			{
				N node = nodes[node_id];
				if (node.isLemma("it") || node.isLemma("its")) return;
			}
			
			node_id = 0;
		}
	}

	@Override
	public N getNode(FeatureItem item)
	{
		N node = getNode(node_id, item.window);
		return getRelativeNode(item, node);
	}

	@Override
	public void evaluate(Eval eval)
	{
		int correct = 0, total = 0;
		
		for (int i=0; i<oracle.size(); i++)
		{
			N[] nodes = document.get(i);
			String[] o = oracle.get(i);

			for (int j=1; j<o.length; j++)
			{
				if (o[j] != null)
				{
					if (o[j].equals(nodes[j].getFeat(FEAT_KEY))) correct++;
					total++;
				}
			}
		}

		((AccuracyEval)eval).add(correct, total);
	}
}
