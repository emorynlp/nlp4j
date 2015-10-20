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
package edu.emory.mathcs.nlp.corenlp.component.state;

import java.util.Arrays;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.corenlp.component.eval.AccuracyEval;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class L2RState<N> extends NLPState<N>
{
	protected String[] oracle;
	protected int input = 0;
	
	public L2RState(N[] nodes)
	{
		super(nodes);
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public void saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> setLabel(n, null)).toArray(String[]::new);
	}
	
	@Override
	public Set<String> getZeroCost()
	{
		return DSUtils.toHashSet(oracle[input]);
	}
	
	protected abstract String setLabel(N node, String label);
	protected abstract String getLabel(N node);
	
//	============================== TRANSITION ==============================

	@Override
	public void next(StringPrediction prediction)
	{
		setLabel(nodes[input++], prediction.getLabel());
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	public N getNode(int window)
	{
		return getNode(input, window);
	}
	
//	============================== EVALUATION ==============================
	
	public void evaluateTokens(AccuracyEval eval)
	{
		int correct = 0;
		
		for (int i=0; i<nodes.length; i++)
			if (oracle[i].equals(getLabel(nodes[i])))
				correct++;
		
		eval.add(correct, nodes.length);
	}
	
//	============================== UTILITIES ==============================
	
	public boolean isFirst(N node)
	{
		return nodes[0] == node;
	}
	
	public boolean isLast(N node)
	{
		return nodes[nodes.length-1] == node;
	}
}
