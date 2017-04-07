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
package edu.emory.mathcs.nlp.component.template.state;

import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class L2RState<N extends AbstractNLPNode<N>> extends NLPState<N>
{
	protected String[] golds;
	protected int input = 1;
	
	public L2RState(List<N> nodes, boolean save_gold)
	{
		super(nodes, save_gold);
	}
	
	@Override
	public void reset()
	{
		input = 1;
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public boolean saveGold()
	{
		golds = Arrays.stream(nodes).map(n -> setLabel(n, null)).toArray(String[]::new);
		return Arrays.stream(golds).filter(o -> o != null).findFirst().isPresent();
	}
	
	@Override
	public String getGoldLabel()
	{
		return golds[input];
	}
	
	protected abstract String setLabel(N node, String label);
	protected abstract String getLabel(N node);
	
//	============================== TRANSITION ==============================

	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		setLabel(nodes[input++], map.getLabel(top2[0]));
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	@Override
	public N getNode(FeatureItem item)
	{
		N node = getNode(input, item.window);
		return getRelativeNode(item, node);
	}
	
	public N getInput()
	{
		return nodes[input];
	}
	
	public int getInputIndex()
	{
		return input;
	}
	
//	============================== EVALUATION ==============================
	
	@Override
	public void evaluate(Eval eval)
	{
		int correct = 0, total = 0;
		
		for (int i=1; i<nodes.length; i++)
		{
			if (golds[i].equals(getLabel(nodes[i]))) correct++;
			total++;
		}
		
		((AccuracyEval)eval).add(correct, total);
	}
}
