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
package edu.emory.mathcs.nlp.structure.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTArc;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTag;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRule;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadTagSet;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;
import edu.emory.mathcs.nlp.structure.util.DDGTag;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class C2DConverter
{
	protected HeadRuleMap headrule_map;
	protected HeadRule    default_rule;
	
	public C2DConverter() {}
	
	/** The default rule takes the rightmost constituency as the head. */
	public C2DConverter(HeadRuleMap headrule_map)
	{
		this(headrule_map, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));
	}
	
	/** @param default_rule use this rule when no specified headrule matches. */
	public C2DConverter(HeadRuleMap headrule_map, HeadRule default_rule)
	{
		this.headrule_map = headrule_map;
		this.default_rule = default_rule;
	}
	
//	============================= Abstract Methods =============================

	/**
	 * @return the dependency graph converted from the constituency tree.
	 * If the constituent tree contains only empty categories, returns {@code null}.
	 */
	public abstract NLPGraph toDependencyGraph(CTTree tree);
	
	/**
	 * Sets the head of the specific constituent node using the specific headrule.
	 * @return the head of the node list.
	 */
	protected abstract void findHead(CTNode node, HeadRule rule);
	
	/**
	 * @return the head flag of the specific constituent node.
	 * @see EnglishC2DConverter#getHeadFlag(CTNode).
	 */
	protected abstract int getHeadFlag(CTNode node);
	
	/**
	 * Returns a dependency label given the specific phrase structure.
	 * @param node the current node.
	 * @param head the head of the current node.
	 * @return a dependency label given the specific phrase structure.
	 */
	protected abstract String getDependencyLabel(CTNode node, CTNode head);
	
//	============================= Set Heads ============================= 
	
	/**
	 * Sets the head of the specific node and all its sub-nodes.
	 * Calls {@link C2DConverter#findHeads(CTNode)}.
	 */
	protected void setHead(CTNode node)
	{
		if (node.isTerminal())
			return;
		
		// set the heads of the children first
		for (CTNode child : node.getChildren())
			setHead(child);
		
		// stop traversing if it is the top node
		if (node.isSyntacticTag(CTTag.TOP))
			return;
		
		// trivial case of one child
		if (node.getChildrenSize() == 1)
		{
			node.setPhraseHead(node.getFirstChild());
			return;
		}
		
		// find the headrule of the current node
		HeadRule rule = headrule_map.get(node.getSyntacticTag());
				
		if (rule == null)
		{
			System.err.println("Error: headrules not found for \""+node.getSyntacticTag()+"\"");
			rule = default_rule;
		}
		
		// abstract method
		findHead(node, rule);
	}
	
	/**
	 * Every other node in the list becomes the dependent of the head node.
	 * @param nodes the list of nodes.
	 * @param rule the headrule to be consulted.
	 * @return the head of the input node-list according to the headrule.
	 */
	protected CTNode findHeadDefault(List<CTNode> nodes, HeadRule rule)
	{
		CTNode head = getDefaultHead(nodes);
		
		if (head == null)
		{
			if (rule.isRightToLeft())
			{
				nodes = new ArrayList<>(nodes);
				Collections.reverse(nodes);
			}
			
			int[] flags = nodes.stream().mapToInt(n -> getHeadFlag(n)).toArray();
			int flag_size = DSUtils.max(flags);
			
			outer: for (int flag=0; flag<=flag_size; flag++)
			{
				for (HeadTagSet tagset : rule.getHeadTags())
				{
					for (int i=0; i<nodes.size(); i++)
					{
						CTNode node = nodes.get(i);
						
						if (flags[i] == flag && tagset.matches(node))
						{
							head = node;
							break outer;
						}
					}
				}
			}
		}
		
		if (head == null)
			throw new IllegalStateException("Head not found");
		
		for (CTNode node : nodes)
		{
			if (node != head && !node.hasPrimaryHead())
				setPrimaryHead(node, head);
		}
		
		return head;
	}
	
	protected void setPrimaryHead(CTNode node, CTNode head)
	{
		node.setPrimaryHead(head, getDependencyLabel(node, head));
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
	
//	============================= Get Dependency Graph =============================
	
	protected void finalizeDependencies(CTNode node)
	{
		if (node.hasPrimaryHead())
		{
			CTArc  arc  = node.getPrimaryHead();
			CTNode head = arc.getNode().getTerminalHead();
			CTNode dep  = node.getTerminalHead();
			dep.setPrimaryHead(head, arc.getLabel());	
		}
		
		for (CTArc arc : node.getSecondaryHeads())
		{
			if (arc.getNode() == null) continue;
			CTNode head = arc.getNode().getTerminalHead();
			CTNode dep  = getTerminalHead(node);
			
			if (head != null && dep != null && dep != node)
				dep.addSecondaryHead(head, arc.getLabel());
			else
				arc.setNode(head);
		}
		
		for (CTNode child : node.getChildren())
			finalizeDependencies(child);
	}
	
	private CTNode getTerminalHead(CTNode node)
	{
		CTNode t = node.getTerminalHead();
		
		while (t.hasAntecedent())
			t = t.getAntecedent().getTerminalHead();
		
		return t;
	}
	
	/** @return the dependency graph converted from the specific constituent tree without head information. */
	protected NLPGraph createDependencyGraph(CTTree tree)
	{
		List<CTNode> tokens = tree.getTokens();
		NLPGraph graph = new NLPGraph();
		String form, pos, lemma, nament;
		NLPNode node, head;
		int id;
		
		for (CTNode token : tokens)
		{
			id     = token.getTokenID() + 1;
			form   = PatternUtils.revertSymbols(token.getForm());
			lemma  = token.getLemma();
			pos    = token.getSyntacticTag();
			nament = token.getNamedEntityTag();
			
			graph.add(new NLPNode(id, form, lemma, pos, nament, token.getFeatMap()));
		}
		
		for (CTNode token : tokens)
		{
			node = graph.get(token.getTokenID() + 1);
			
			if (token.hasPrimaryHead())
			{
				CTArc arc = token.getPrimaryHead();
				head = graph.get(arc.getNode().getTokenID() + 1);
				node.setParent(head, arc.getLabel());	
			}
			else
				node.setParent(graph.getRoot(), DDGTag.ROOT);
			
			for (CTArc arc : token.getSecondaryHeads())
			{
				head = graph.get(arc.getNode().getTokenID() + 1);
				if (!node.isChildOf(head)) node.addSecondaryHead(head, arc.getLabel());
			}
		}

		return graph;
	}
}