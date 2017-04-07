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

import java.util.List;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.feature.Relation;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;

/**
 * This class consists of processing states 
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPState<N extends AbstractNLPNode<N>>
{
	protected List<N> nodes;

	/**
	 * @param nodes the 0'th node is the root, 1st node is the 1st token, etc.
	 * @param save_gold if true, save the gold tags (see {@link #saveGold()}.
	 */
	public NLPState(List<N> nodes, boolean save_gold)
	{
		if (save_gold) saveGold(nodes);
		setNodes(nodes);
	}
	
//	============================== ORACLE ==============================
	
	/** Saves the clears the gold labels from {@link #nodes}. */
	public abstract void saveGold(List<N> nodes);
	
	/** @return the gold label given the current state. */
	public abstract String getGoldLabel();
	
//	============================== TRANSITION ==============================
	
	/**
	 * Applies the predictions to the current state, and moves onto the next state.
	 * @param map to retrieve the string label from its index. 
	 * @param scores scores of all labels.
	 */
	public abstract void next(LabelMap map, float[] scores);
	
	/** @return {@code true} if no more state can be processed; otherwise, {@code false}. */
	public abstract boolean isTerminate();
	
	/**
	 * Evaluates all predictions made for either {@link #nodes} or {@link #document} using the specific evaluator.
	 * @param eval e.g., {@link AccuracyEval}, {@link F1Eval}.
	 */
	public abstract void evaluate(Eval eval);
	
//	============================== NODES ==============================
	
	/**
	 * @return the node with respect to the feature item if exists; otherwise, {@code null}.
	 * @see {@link FeatureTemplate}.
	 */
	public abstract N getNode(FeatureItem item);
	
	public List<N> getNodes()
	{
		return nodes;
	}
	
	public void setNodes(List<N> nodes)
	{
		this.nodes = nodes;
	}
	
	public N getNode(int index)
	{
		return getNode(index, 0, false);
	}
	
	/** @return {@link #getNode(int, int, boolean)}, where {@code includeRoot = false}. */
	public N getNode(int index, int window)
	{
		return getNode(index, window, false);
	}
	
	public N getNode(int index, int window, Relation relation)
	{
		return getRelativeNode(getNode(index, window), relation);
	}
	
	/**
	 * @return the {@code index+window}'th node in {@link #nodes} if exists; otherwise, {@code null}.
	 * @param index the index of the input node.
	 * @param window context window.
	 * @param includeRoot if {@code true}, the artificial root node is considered a part of context.
	 */
	public N getNode(int index, int window, boolean includeRoot)
	{
		index += window;
		int begin = includeRoot ? 0 : 1;
		return begin <= index && index < nodes.size() ? nodes.get(index) : null;
	}
	
	public N getRelativeNode(N node, Relation relation)
	{
		if (node == null || relation == null)
			return node;
		
		switch (relation)
		{
		case h   : return node.getParent();
		case h2  : return node.getGrandParent();
		case lmd : return node.getLeftMostChild();
		case lmd2: return node.getLeftMostChild(1);
		case lnd : return node.getLeftNearestChild();
		case lnd2: return node.getLeftNearestChild(1);
		case lns : return node.getLeftNearestSibling();
		case lns2: return node.getLeftNearestSibling(1);
		case rmd : return node.getRightMostChild();
		case rmd2: return node.getRightMostChild(1);
		case rnd : return node.getRightNearestChild();
		case rnd2: return node.getRightNearestChild(1);
		case rns : return node.getRightNearestSibling();
		case rns2: return node.getRightNearestSibling(1);
		}
		
		return null;
	}
	
	/**
	 * @return the relative node with respect to the feature template and the input node.
	 * @param item the feature template.
	 * @param node the input node.
	 */
	public N getRelativeNode(FeatureItem item, N node)
	{
		return getRelativeNode(node, item.relation);
	}
	
	/** @return {@code true} if the node is the first node in the sentence. */
	public boolean isFirst(N node)
	{
		return nodes.get(1) == node; 
	}
	
	/** @return {@code true} if the node is the first node in the sentence. */
	public boolean isLast(N node)
	{
		return DSUtils.getLast(nodes) == node;
	}
}
