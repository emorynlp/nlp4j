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

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.feature.Relation;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * This class consists of processing states 
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPState
{
	protected List<NLPNode[]> document;
	protected NLPNode[] nodes;

	/**
	 * For sentence-based NLP components.
	 * @param nodes {@code node[0]} is reserved for the artificial root, {@code node[1]} represents the first token, and so on.
	 */
	public NLPState(NLPNode[] nodes)
	{
		setNodes(nodes);
	}
	
	/**
	 * For document-based NLP components.
	 * @param document each node array represents a sentence.
	 */
	public NLPState(List<NLPNode[]> document)
	{
		setDocument(document);
	}
	
	/**
	 * Saves and removes the gold labels from {@link #nodes} or {@link #document}.
	 * @return {@code true} if any gold label was saved; otherwise, {@code false}. 
	 */
	public abstract boolean saveOracle();
	
	/** @return the gold label given the current state. */
	public abstract String getOracle();
	
	/** Resets the oracle with gold information. */
	public abstract void resetOracle();
	
	/**
	 * Applies the predictions to the current state, and moves onto the next state.
	 * @param map to retrieve the string label from its index. 
	 * @param top2 indices of the top 2 predications, where {@code top2[0]} is the best prediction and {@code top2[1]} is the 2nd best prediction.
	 * @param scores scores of all labels.
	 */
	public abstract void next(LabelMap map, int[] top2, float[] scores);
	
	/** @return {@code true} if no more state can be processed; otherwise, {@code false}. */
	public abstract boolean isTerminate();
	
	/**
	 * @return the node with respect to the feature item if exists; otherwise, {@code null}.
	 * @see {@link FeatureTemplate}.
	 */
	public abstract NLPNode getNode(FeatureItem item);
	
	/**
	 * Evaluates all predictions made for either {@link #nodes} or {@link #document} using the specific evaluator.
	 * @param eval e.g., {@link AccuracyEval}, {@link F1Eval}.
	 */
	public abstract void evaluate(Eval eval);
	
	public NLPNode[] getNodes()
	{
		return nodes;
	}
	
	public void setNodes(NLPNode[] nodes)
	{
		this.nodes = nodes;
	}
	
	public List<NLPNode[]> getDocument()
	{
		return document;
	}
	
	public void setDocument(List<NLPNode[]> document)
	{
		this.document = document;
	}
	
	public NLPNode getNode(int index)
	{
		return getNode(index, 0, false);
	}
	
	/** @return {@link #getNode(int, int, boolean)}, where {@code includeRoot = false}. */
	public NLPNode getNode(int index, int window)
	{
		return getNode(index, window, false);
	}
	
	public NLPNode getNode(int index, int window, Relation relation)
	{
		return getRelativeNode(getNode(index, window), relation);
	}
	
	/**
	 * @return the {@code index+window}'th node in {@link #nodes} if exists; otherwise, {@code null}.
	 * @param index the index of the input node.
	 * @param window context window.
	 * @param includeRoot if {@code true}, the artificial root node is considered a part of context.
	 */
	public NLPNode getNode(int index, int window, boolean includeRoot)
	{
		index += window;
		int begin = includeRoot ? 0 : 1;
		return begin <= index && index < nodes.length ? nodes[index] : null;
	}
	
	public NLPNode getRelativeNode(NLPNode node, Relation relation)
	{
		if (node == null || relation == null)
			return node;
		
		switch (relation)
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
	
	/**
	 * @return the relative node with respect to the feature template and the input node.
	 * @param item the feature template.
	 * @param node the input node.
	 */
	public NLPNode getRelativeNode(FeatureItem item, NLPNode node)
	{
		return getRelativeNode(node, item.relation);
	}
	
	/** @return {@code true} if the node is the first node in the sentence. */
	public boolean isFirst(NLPNode node)
	{
		return nodes[1] == node; 
	}
	
	/** @return {@code true} if the node is the first node in the sentence. */
	public boolean isLast(NLPNode node)
	{
		return nodes[nodes.length-1] == node;
	}
}
