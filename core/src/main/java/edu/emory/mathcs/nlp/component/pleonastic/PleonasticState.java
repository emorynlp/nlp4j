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
package edu.emory.mathcs.nlp.component.pleonastic;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PleonasticState<N extends AbstractNLPNode<N>> extends NLPState<N>
{
	static public final String FEAT_KEY = "it"; 
	private List<ObjectIntIntTriple<String>> oracle;
	private int tree_id, node_id;
	
	public PleonasticState(List<N[]> document)
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
		N[] nodes;
		String o;
		
		for (int i=0; i<document.size(); i++)
		{
			nodes = document.get(i);
			
			for (int j=1; j<nodes.length; j++)
			{
				o = nodes[j].removeFeat(FEAT_KEY);
				if (o != null) oracle.add(new ObjectIntIntTriple<>(o, i, j));
			}
		}

		return !oracle.isEmpty();
	}

	@Override
	public String getOracle()
	{
		return null;
//		return oracle.get(tree_id)[node_id];
	}
	
	public void resetOracle()
	{
//		N[] nodes;
//		String[] o;
//		
//		for (int i=0; i<oracle.size(); i++)
//		{
//			nodes = document.get(i);
//			o = oracle.get(i);
//
//			for (int j=1; j<o.length; j++)
//			{
//				if (o[j] != null)
//					nodes[j].putFeat(FEAT_KEY, o[j]);
//			}
//		}
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
//		for (input++; input<nodes.length; input++)
//		{
//			NLPNode node = nodes[input];
//			if (node.isLemma("it")) break;
//		}
	}

	@Override
	public N getNode(FeatureItem item)
	{
//		NLPNode node = getNode(input, item.window);
//		return getRelativeNode(item, node);
		return null;
	}

	@Override
	public void evaluate(Eval eval)
	{
//		int correct = 0, total = 0;
//		
//		for (int i=1; i<oracle.length; i++)
//		{
//			NLPNode n = nodes[i];
//			String o = oracle[i];
//			
//			if (o != null)
//			{
//				if (o.equals(n.getFeat(FEAT_KEY))) correct++;
//				total++;
//			}
//		}
//
//		((AccuracyEval)eval).add(correct, total);
	}
}
