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

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCState extends NLPState
{
	private final String feat_key; 
	private boolean terminate;
	private NLPNode key_node;
	private String gold;
	
	public DOCState(NLPNode[] nodes, String key)
	{
		super(nodes);
		feat_key  = key;
		key_node  = nodes[1];
		terminate = false;
	}

	@Override
	public void saveOracle()
	{
		gold = key_node.removeFeat(feat_key);
	}

	@Override
	public String getOracle()
	{
		return gold;
	}

	@Override
	public void next(LabelMap map, int yhat, float[] scores)
	{
		key_node.putFeat(feat_key, map.getLabel(yhat));
		terminate = true;
	}

	@Override
	public boolean isTerminate()
	{
		return terminate;
	}

	@Override
	public NLPNode getNode(FeatureItem item)
	{
		return null;
	}

	@Override
	public void evaluate(Eval eval)
	{
		int correct = gold.equals(key_node.getFeat(feat_key)) ? 1 : 0;
		((AccuracyEval)eval).add(correct, 1);
	}
}
