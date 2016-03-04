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

import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPState
{
	protected NLPNode[] nodes;

	public NLPState(NLPNode[] nodes)
	{
		this.nodes = nodes;
	}
	
	/** Clears and saves the gold-standard labels in the input nodes if available. */
	public abstract boolean saveOracle();
	
	/** @return the gold label given the current state. */
	public abstract String getOracle();
	
	/**
	 * Applies the prediction and moves onto the next state.
	 * @param map retrieves the string label from its index. 
	 * @param yhat index of the top predicated label.
	 * @param scores scores of all labels.
	 */
	public abstract void next(LabelMap map, int yhat, float[] scores);
	
	/** @return true if no more state can be processed; otherwise, false. */
	public abstract boolean isTerminate();
	
	/** @return the node with respect to the feature item if exists; otherwise, null. */
	public abstract NLPNode getNode(FeatureItem item);
	
	/** Evaluates all predictions given the current input and the evaluator. */
	public abstract void evaluate(Eval eval);
	
	public NLPNode[] getNodes()
	{
		return nodes;
	}
	
	public NLPNode getNode(int index)
	{
		return getNode(index, 0, false);
	}
	
	/** @return the node in the (index+window) position of {@link #nodes} if exists; otherwise, null. */
	public NLPNode getNode(int index, int window)
	{
		return getNode(index, window, false);
	}
	
	public NLPNode getNode(int index, int window, boolean includeRoot)
	{
		index += window;
		int begin = includeRoot ? 0 : 1;
		return begin <= index && index < nodes.length ? nodes[index] : null;
	}
	
	public boolean isFirst(NLPNode node)
	{
		return nodes[1] == node; 
	}
	
	public boolean isLast(NLPNode node)
	{
		return nodes[nodes.length-1] == node;
	}
	
	protected NLPNode getRelativeNode(FeatureItem item, NLPNode node)
	{
		if (node == null || item.relation == null)
			return node;
		
		switch (item.relation)
		{
		case h   : return node.getDependencyHead();
		case h2  : return node.getGrandDependencyHead();
		case lmd : return node.getLeftMostDependent();
		case lmd2: return node.getLeftMostDependent(1);
		case lnd : return node.getLeftNearestDependent();
		case lnd2: return node.getLeftNearestDependent(1);
		case lns : return node.getLeftNearestSibling();
		case lns2: return node.getLeftNearestSibling(1);
		case rmd : return node.getRightMostDependent();
		case rmd2: return node.getRightMostDependent(1);
		case rnd : return node.getRightNearestDependent();
		case rnd2: return node.getRightNearestDependent(1);
		case rns : return node.getRightNearestSibling();
		case rns2: return node.getRightNearestSibling(1);
		}
		
		return null;
	}
}
