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

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class L2RState extends NLPState
{
	protected String[] oracle;
	protected int input = 1;
	
	public L2RState(NLPNode[] nodes)
	{
		super(nodes);
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public boolean saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> setLabel(n, null)).toArray(String[]::new);
		return Arrays.stream(oracle).filter(o -> o != null).findFirst().isPresent();
	}
	
	@Override
	public String getOracle()
	{
		return oracle[input];
	}
	
	protected abstract String setLabel(NLPNode node, String label);
	protected abstract String getLabel(NLPNode node);
	
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
	public NLPNode getNode(FeatureItem item)
	{
		NLPNode node = getNode(input, item.window);
		return getRelativeNode(item, node);
	}
	
	public NLPNode getInput()
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
//			if (POSTrainer.train_words != null && POSTrainer.train_words.contains(nodes[i].getWordForm())) continue;
			if (oracle[i].equals(getLabel(nodes[i]))) correct++;
			total++;
		}
		
		((AccuracyEval)eval).add(correct, total);
	}
}
