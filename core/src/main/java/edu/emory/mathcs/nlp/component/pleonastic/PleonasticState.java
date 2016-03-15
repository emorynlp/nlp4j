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

import java.util.Arrays;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PleonasticState extends NLPState
{
	static public final String FEAT_KEY = "it"; 
	private String[] oracle;
	private int input;
	
	public PleonasticState(NLPNode[] nodes)
	{
		super(nodes);
		input = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================
	
	@Override
	public boolean saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> n.removeFeat(FEAT_KEY)).toArray(String[]::new);
		return Arrays.stream(oracle).filter(o -> o != null).findFirst().isPresent();
	}

	@Override
	public String getOracle()
	{
		return oracle[input];
	}
	
	public void resetOracle()
	{
		// TODO:
	}

//	====================================== TRANSITION ======================================
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		String label = map.getLabel(top2[0]);
		nodes[input].putFeat(FEAT_KEY, label);
		shift();
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	private void shift()
	{
		for (input++; input<nodes.length; input++)
		{
			NLPNode node = nodes[input];
			if (node.isLemma("it")) break;
		}
	}

	@Override
	public NLPNode getNode(FeatureItem item)
	{
		NLPNode node = getNode(input, item.window);
		return getRelativeNode(item, node);
	}

	@Override
	public void evaluate(Eval eval)
	{
		int correct = 0, total = 0;
		
		for (int i=1; i<oracle.length; i++)
		{
			NLPNode n = nodes[i];
			String o = oracle[i];
			
			if (o != null)
			{
				if (o.equals(n.getFeat(FEAT_KEY))) correct++;
				total++;
			}
		}

		((AccuracyEval)eval).add(correct, total);
	}
}
