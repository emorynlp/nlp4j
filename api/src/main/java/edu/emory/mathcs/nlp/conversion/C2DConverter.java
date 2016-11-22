/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.emory.mathcs.nlp.common.treebank.CTTag;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.conversion.util.C2DInfo;
import edu.emory.mathcs.nlp.lexicon.constituency.CTNode;
import edu.emory.mathcs.nlp.lexicon.constituency.CTTree;
import edu.emory.mathcs.nlp.lexicon.dependency.NLPNode;
import edu.emory.mathcs.nlp.lexicon.headrule.HeadRule;
import edu.emory.mathcs.nlp.lexicon.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.lexicon.headrule.HeadTagSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class C2DConverter
{
	protected HeadRuleMap headrule_map;
	protected HeadRule    default_rule;
	
	/** @param defaultRule use this rule when no specified headrule matches. */
	public C2DConverter(HeadRuleMap headruleMap, HeadRule defaultRule)
	{
		headrule_map = headruleMap;
		default_rule = defaultRule;
	}
	
	/**
	 * @return the dependency graph converted from the constituent tree.
	 * If the constituent tree contains only empty categories, returns {@code null}.
	 * @param cTree the constituent tree to be converted.
	 */
	abstract public NLPNode[] toDependencyGraph(CTTree cTree);
	
//	============================= Empty categories ============================= 
	
	protected void removeNode(CTNode node)
	{
		CTNode parent = node.getParent();
	
		if (parent != null)
		{
			parent.removeChild(node);
			
			if (parent.getChildrenSize() == 0)
				removeNode(parent);			
		}
	}
	
	protected void replaceEmptyCategory(CTNode ec, CTNode ante)
	{
		removeNode(ante);
		ec.getParent().replaceChild(ec, ante);
		ante.addFunctionTags(ec.getFunctionTags());
	}
	
	
	/**
	 * Sets the head of the specific node and all its sub-nodes.
	 * Calls {@link C2DConverter#findHeads(CTNode)}.
	 */
	protected void setHeads(CTNode curr)
	{
		// terminal nodes become the heads of themselves
		if (curr.isTerminal())
		{
			curr.setC2DInfo(new C2DInfo(curr));
			return;
		}
		
		// set the heads of all children
		for (CTNode child : curr.getChildren())
			setHeads(child);
		
		// stop traversing if it is the top node
		if (curr.isSyntacticTag(CTTag.TOP))
			return;
		
		// only one child
		if (curr.getChildrenSize() == 1)
		{
			curr.setC2DInfo(new C2DInfo(curr.getChild(0)));
			return;
		}
		
		// find the headrule of the current node
		HeadRule rule = headrule_map.get(curr.getSyntacticTag());
				
		if (rule == null)
		{
			System.err.println("Error: headrules not found for \""+curr.getSyntacticTag()+"\"");
			rule = default_rule;
		}
		
		// abstract method
		setHeads(curr, rule);
	}
	
	/**
	 * @return the head of the input node-list according to the headrule.
	 * Every other node in the list becomes the dependent of the head node.
	 * @param rule the headrule to be consulted.
	 * @param nodes the list of nodes.
	 * @param flagSize the number of head flags.
	 */
	protected CTNode getHead(HeadRule rule, List<CTNode> nodes, int flagSize)
	{
		CTNode head = getDefaultHead(nodes);
		
		if (head == null)
		{
			nodes = new ArrayList<>(nodes);
			if (rule.isRightToLeft()) Collections.reverse(nodes);
			
			int i, size = nodes.size(), flag;
			int[] flags = new int[size];
			CTNode child;
			
			for (i=0; i<size; i++)
				flags[i] = getHeadFlag(nodes.get(i));
			
			outer: for (flag=0; flag<flagSize; flag++)
			{
				for (HeadTagSet tagset : rule.getHeadTags())
				{
					for (i=0; i<size; i++)
					{
						child = nodes.get(i);
						
						if (flags[i] == flag && tagset.matches(child))
						{
							head = child;
							break outer;
						}
					}
				}
			}
			
			outer: for (flag=0; flag<flagSize; flag++)
			{
				for (HeadTagSet tagset : rule.getHeadTags())
				{
					for (i=0; i<size; i++)
					{
						child = nodes.get(i);
						
						if (flags[i] == flag && tagset.matches(child))
						{
							head = child;
							break outer;
						}
					}
				}
			}
		}
		
		if (head == null)
			throw new IllegalStateException("Head not found");
		
		CTNode parent = head.getParent();
		
		for (CTNode node : nodes)
		{
			if (node != head && !node.getC2DInfo().hasHead())
				node.getC2DInfo().setHead(head, getDEPLabel(node, parent, head));
		}
		
		return head;
	}
	
	/** @return the default head if it is the only node in the list that is not an empty category. */
	private CTNode getDefaultHead(List<CTNode> nodes)
	{
		CTNode head = null;
		
		for (CTNode node : nodes)
		{
			if (!node.isEmptyCategoryPhrase())
			{
				if (head != null) return null;
				head = node;
			}
		}

		return head;
	}
	
	/** @return the dependency tree converted from the specific constituent tree without head information. */
	protected NLPNode[] initDEPTree(CTTree cTree)
	{
		List<CTNode>  cNodes = cTree.getTokens();
		NLPNode[]     dNodes = new NLPNode[cNodes.size()+1];
		String form, pos, lemma;
		NLPNode dNode;
		int id;
		
		dNodes[0] = new NLPNode().toRoot();
		
		for (CTNode cNode : cNodes)
		{
			id   = cNode.getTokenID() + 1;
			form = PatternUtils.revertSymbols(cNode.getForm());
			lemma = cNode.getLemma();
			pos  = cNode.getSyntacticTag();
			dNode = new NLPNode(id, form, lemma, pos, cNode.getC2DInfo().getFeatMap());
			dNode.setSecondaryHeads(new ArrayList<>());
			dNodes[id] = dNode;
		}

		return dNodes;
	}
	
	/**
	 * Sets the head of the specific constituent node using the specific headrule.
	 * Called by {@link #setHeads(CTNode)}.
	 */
	abstract protected void setHeads(CTNode curr, HeadRule rule);
	
	/**
	 * @return the head flag of the specific constituent node.
	 * @see EnglishC2DConverter#getHeadFlag(CTNode).
	 */
	abstract protected int getHeadFlag(CTNode child);
	
	/**
	 * Returns a dependency label given the specific phrase structure.
	 * @param C the current node.
	 * @param P the parent of {@code C}.
	 * @param p the head of {@code P}.
	 * @return a dependency label given the specific phrase structure.
	 */
	abstract protected String getDEPLabel(CTNode C, CTNode P, CTNode p);
}