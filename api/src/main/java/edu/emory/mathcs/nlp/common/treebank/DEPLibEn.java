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
package edu.emory.mathcs.nlp.common.treebank;

import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.propbank.PBLib;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.component.dep.DEPArc;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLibEn implements DEPTagEn
{
	private DEPLibEn() {}
	
	static public boolean isNoun(NLPNode node)
	{
		return PTBLib.isNoun(node.getPartOfSpeechTag());
	}
	
	static public boolean isVerb(NLPNode node)
	{
		return PTBLib.isVerb(node.getPartOfSpeechTag());
	}
	
	/** Enriches certain dependency labels into finer-grained labels. */
	static public void enrichLabels(NLPNode[] tree)
	{
		Set<String> subj = DSUtils.toHashSet(DEP_CSUBJ, DEP_NSUBJ);
		List<NLPNode> list;

		for (NLPNode node : tree)
		{
			if (node.isDependencyLabel(DEP_ADVMOD) && ENUtils.isNegation(node.getWordForm()))
				node.setDependencyLabel(DEP_NEG);
			
			if (node.containsDependentByLabel(DEP_AUXPASS))
			{
				for (NLPNode child : node.getDependentListByLabel(subj))
					child.setDependencyLabel(child.getDependencyLabel()+DEP_PASS);
			}
			
			if ((list = node.getDependentListByLabel(DEP_DOBJ)).size() > 1)
				list.get(0).setDependencyLabel(DEP_DATIVE);
		}
	}
	
	static public void postLabel(NLPNode[] tree)
	{
		postSemanticLabel(tree);
	}
	
	static public void postSemanticLabel(NLPNode[] tree)
	{
		List<List<DEPArc<NLPNode>>> argLists;
		int i, size = tree.length;
		List<DEPArc<NLPNode>> list;
		NLPNode node = tree[0];
		
		if (node.getSemanticHeadList() == null) return;
		argLists = NLPUtils.getSemanticArgumentList(tree);
		
		for (i=1; i<size; i++)
		{
			node = tree[i];
			list = argLists.get(i);
			
			if (node.isDependencyLabel(DEP_PREP))
			{
				relinkPreposition(node);
			}
			else if (PTBLib.isVerb(node.getPartOfSpeechTag()))
			{
				labelReferentOfRelativeClause(node, list);
			}
		}
	}

	/**
	 * Re-links PP to a verb predicate.
	 * Called by {@link #postLabel(DEPTree)}.
	 * @param prep the dependency label of this node is {@link DEPTagEn#DEP_PREP}.
	 */
	static private void relinkPreposition(NLPNode prep)
	{
		NLPNode head = prep.getDependencyHead();
		
		if (head.isDependencyLabel(DEPLibEn.DEP_POBJ))
			head = head.getDependencyHead();
		
		if (isNoun(head) || head.isPartOfSpeechTag(PTBTag.P_IN) || head.isPartOfSpeechTag(PTBTag.P_RP))
		{
			NLPNode gHead = head.getDependencyHead();		// verb predicate	
			DEPArc<NLPNode>  sp;
			
			if (gHead != null && (sp = prep.getSemanticHeadArc(gHead)) != null && PBLib.isNumberedArgument(sp.getLabel()))
			{
				if (head.getSemanticHeadArc(gHead) == null)
				{
					prep.removeSemanticHead(sp);
					head.addSemanticHead(gHead, PBLib.getBaseLabel(sp.getLabel()));
				}
			}
		}
	}
	
	/**
	 * Called by {@link #postLabel(DEPTree)}.
	 * Add the argument label to the head of a referent.
	 * @param verb the POS tag of this node is a verb.
	 */
	static private void labelReferentOfRelativeClause(NLPNode verb, List<DEPArc<NLPNode>> argList)
	{
		NLPNode top  = getHeightVerbInChain(verb);
		NLPNode head = top.getDependencyHead();
		
		if (top.isDependencyLabel(DEP_RELCL) && !head.isArgumentOf(verb))
		{
			for (DEPArc<NLPNode> arc : argList)
			{
				if (PBLib.isReferentArgument(arc.getLabel()) && isReferentArgument(arc.getNode()))
				{
					head.addSemanticHead(verb, PBLib.getBaseLabel(arc.getLabel()));
					return;
				}
			}
		}
	}
	
	/** Called by {@link #labelReferentOfRelativeClause(NLPNode, List)}. */
	static private boolean isReferentArgument(NLPNode node)
	{
		return node.getFirstDependent(DEP_POBJ, (n,l) -> n.isDependencyLabel(l)) != null || node.isLemma("that") || node.isLemma("which");
	}
	
	/**
	 * @return get the highest verb in the chain.
	 * @param verb the POS tag of this node is a verb. 
	 */
	static public NLPNode getHeightVerbInChain(NLPNode verb)
	{
		while (isVerb(verb.getDependencyHead()) && (verb.isDependencyLabel(DEP_CONJ) || verb.isDependencyLabel(DEP_XCOMP)))
			verb = verb.getDependencyHead();
			
		return verb;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param node the POS tag of this node is a noun.
	 * @return the trimmed sequence of lemmas for the specific node.
	 */
	static public String getSubLemmasForNP(NLPNode node, String delim)
	{
		StringBuilder build = new StringBuilder();
		boolean add = true;
		
		for (NLPNode dep : node.getDependentList())
		{
			// insert the node's lemma at the right position.
			if (add && dep.getID() > node.getID())
			{
				build.append(delim);
				build.append(node.getLemma());
				add = false;
			}
			
			if (dep.isDependencyLabel(DEP_COMPOUND) || dep.isPartOfSpeechTag(PTBTag.P_PRPS))
			{
				build.append(delim);
				build.append(dep.getLemma());
			}
		}
		
		if (add)
		{
			build.append(delim);
			build.append(node.getLemma());
		}
		
		return build.substring(delim.length());
	}
	
	/**
	 * @param node the POS tag of this node is a preposition.
	 * @return the trimmed sequence of lemmas for the specific node.
	 */
	static public String getSubLemmasForPP(NLPNode node, String delim)
	{
		StringBuilder build = new StringBuilder();
		build.append(node.getLemma());

		NLPNode pobj = node.getFirstDependent(DEP_POBJ, (n,l) -> n.isDependencyLabel(l));
		
		if (pobj != null)
		{
			build.append(delim);
			
			if (PTBLib.isNoun(pobj.getPartOfSpeechTag()))
				build.append(getSubLemmasForNP(pobj, delim));
			else
				build.append(pobj.getLemma());
		}
		
		return build.toString();
	}
}