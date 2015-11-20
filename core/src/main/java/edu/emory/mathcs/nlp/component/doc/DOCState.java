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
package edu.emory.mathcs.nlp.component.doc;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.model.StringModel;
import edu.emory.mathcs.nlp.learning.prediction.StringPrediction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCState extends NLPState
{
	public static final int KEY_NODE = 1; 
	protected String oracle;
	protected String feat_key;
	protected boolean assinged;
	
	public DOCState(NLPNode[] nodes, String featKey)
	{
		super(nodes);
		assinged = false;
		feat_key = featKey;
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public void saveOracle()
	{
		oracle = nodes[KEY_NODE].removeFeat(feat_key);
	}
	
	@Override
	public int[] getZeroCostLabels(StringModel model)
	{
		model.addLabel(oracle);
		return new int[]{model.getLabelIndex(oracle)};
	}
	
//	============================== TRANSITION ==============================

	@Override
	public void next(StringPrediction prediction)
	{
		nodes[KEY_NODE].putFeat(feat_key, prediction.getLabel());
		assinged = true;
	}
	
	@Override
	public boolean isTerminate()
	{
		return assinged;
	}
	
	@Override
	public NLPNode getNode(FeatureItem<?> item)
	{
		return nodes[0];
	}
	
//	============================== EVALUATION ==============================
	
	@Override
	public void evaluate(Eval eval)
	{
		int correct = oracle.equals(nodes[KEY_NODE].getFeat(feat_key)) ? 1 : 0;
		((AccuracyEval)eval).add(correct, 1);
	}
}
