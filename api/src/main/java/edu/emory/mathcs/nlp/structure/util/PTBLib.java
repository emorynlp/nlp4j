package edu.emory.mathcs.nlp.structure.util;
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

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.node.AbstractNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PTBLib implements PTBTag
{
//	static final public Predicate<CTNode> M_S_SBAR      = matchCo(Sets.newHashSet(C_S, C_SBAR));
//	static final public Predicate<CTNode> M_IN_DT_TO    = matchCo(Sets.newHashSet(PTBTag.P_IN, PTBTag.P_DT, PTBTag.P_TO));
//	static final public Predicate<CTNode> M_VP_OR_PRD   = matchCoF(C_VP, F_PRD);
	
	// clause-level
	static final public Set<String> MAIN_CLAUSE    = Sets.newHashSet(C_S, C_SQ, C_SINV);

	// phrase-level
	static final public Set<String> LGS_PHRASE     = Sets.newHashSet(C_PP, C_SBAR);
	static final public Set<String> EDITED_PHRASE  = Sets.newHashSet(C_EDITED, C_EMBED);
	static final public Set<String> NOMINAL_PHRASE = Sets.newHashSet(C_NP, C_NML, C_NX, C_NAC);
	static final public Set<String> WH_LINK        = Sets.newHashSet(C_WHNP, C_WHPP, C_WHADVP);
	static final public Set<String> SEPARATOR      = Sets.newHashSet(P_COMMA, P_COLON);
	static final public Set<String> CONJUNCTION	   = Sets.newHashSet(P_CC, C_CONJP);
	static final public Set<String> PUNCTUATION    = Sets.newHashSet(P_COLON, P_COMMA, P_PERIOD, P_LQ, P_RQ, P_LRB, P_RRB, P_HYPH, P_NFP, P_SYM, P_PUNC);
	static final public Set<String> RELATIVIZER    = Sets.newHashSet(P_WDT, P_WP, P_WPS, P_WRB);
	
	// more
	static final public Set<String> VBD_VBN        = Sets.newHashSet(P_VBD, P_VBN);
	static final public Set<String> VP_RRC_UCP     = Sets.newHashSet(C_VP, C_RRC, C_UCP);
	static final public Set<String> NP_NML_WHNP    = Sets.newHashSet(C_NP, C_NML, C_WHNP);
	
	private PTBLib() {}
	
//	========================= Pre-process =========================
	
	/**
	 * Fixes inconsistent function tags.
	 * Links antecedents of reduced passive nulls ({@code *}) and relativizers.
	 * @see #fixFunctionTags(CTTree)
	 * @see #linkReducedPassiveNulls(CTTree)
	 * @see #linkRelativizers(CTTree)	 
	 */
	static public void preprocess(CTTree tree)
	{
		fixFunctionTags(tree);
		linkReducedPassiveNulls(tree);
		linkRelativizers(tree);
		fixEmptyCategories(tree);
	}
	
//	============================== Fix Function Tags ==============================

	/**
	 * Fixes inconsistent function tags in the specific tree.
	 * @see #fixSBJ(CTNode)
	 * @see #fixLGS(CTNode)
	 * @see #fixCLF(CTNode)
	 * @see #fixPRD(CTNode)
	 */
	static public void fixFunctionTags(CTTree tree)
	{
		tree.flatten().forEach(node -> fixFunctionTags(node));
	}
	
	/** Called by {@link PTBLib#fixFunctionTags(CTTree)}. */
	static public void fixFunctionTags(CTNode node)
	{
		if (node.isFunctionTag(F_SBJ))
			fixSBJ(node);
		else if (node.isFunctionTag(F_LGS))
			fixLGS(node);
		else if (node.isFunctionTag(F_CLF))
			fixCLF(node);
		else if (node.isFunctionTag(F_PRD))
			fixPRD(node);
	}
	
	/** If the specific node contains the function tag {@link PTBTag#F_SBJ} and it is the only child of its parent, moves the tag to its parent. */
	static private boolean fixSBJ(CTNode node)
	{
		CTNode parent = node.getParent();
		
		if (parent.getChildrenSize() == 1 && !parent.isSyntacticTag(EDITED_PHRASE) && !parent.hasFunctionTag())
		{
			node.removeFunctionTag(F_SBJ);
			parent.addFunctionTag(F_SBJ);
			parent.setSyntacticTag(node.getSyntacticTag());
			return true;
		}
		
		if (isClause(node) && (!isClause(parent) || parent.containsChild(PTBLib::isNominalSubject)))
		{
			node.removeFunctionTag(F_SBJ);
			node.addFunctionTag(F_ADV);
			return true;
		}
		
		return false;
	}
	
	/** If the specific node contains the function tag {@link PTBTag#F_LGS} and it is not a prepositional phrase, moves the tag to its parent. */
	static private boolean fixLGS(CTNode node)
	{
		if (!node.isSyntacticTag(C_PP))
		{
			CTNode parent = node.getParent();
			
			if (parent.isSyntacticTag(LGS_PHRASE))
			{
				node.removeFunctionTag(F_LGS);
				parent.addFunctionTag(F_LGS);
				return true;
			}
		}
		
		return false;
	}
	
	/** If the specific node contains the function tag {@link PTBTag#F_CLF} and it is not a subordinate clause, moves the tag to the subordinate clause. */
	static private boolean fixCLF(CTNode node)
	{
		if (isMainClause(node))
		{
			CTNode desc = node.getFirstDescendant(PTBLib::isSubordinateClause);
			node.removeFunctionTag(F_CLF);
			
			if (desc != null)
			{
				desc.addFunctionTag(F_CLF);
				return true;
			}
		}
		
		return false;
	}
	
	static private boolean fixPRD(CTNode node)
	{
		if (isClause(node) || (node.isSyntacticTag(C_ADVP) && node.getChildrenSize() == 1 && node.getFirstChild().isFormLowercase("so")))
		{
			node.removeFunctionTag(F_PRD);
			return true;	
		}
		
		if (node.isSyntacticTag(C_ADJP) && containsCoordination(node))
		{
			for (CTNode n : node.getChildren(n -> n.isSyntacticTag(C_ADJP)))
				n.addFunctionTag(F_PRD);
		}
		
		return false;
	}
	
//	============================== Fix Empty Categories ==============================
	
	static public void fixEmptyCategories(CTTree tree)
	{
		tree.flatten().forEach(node -> fixEmptyCategories(tree, node));
	}
	
	static private void fixEmptyCategories(CTTree tree, CTNode node)
	{
		if (node.isEmptyCategory())
		{
			if (isPRO(node)) fixPRO(tree, node);
			else if (isTrace(node)) fixTrace(tree, node);
			else if (isInterpretConstituentHere(node) || isPermanentPredictableAmbiguity(node)) fixICHnPPA(tree, node);
			else if (isPassiveNull(node)) fixPassiveNull(tree, node);
		}
	}
	
	static private void fixPRO(CTTree tree, CTNode node)
	{
		if (node.hasAntecedent() && PTBLib.isWhPhrase(node.getAntecedent()) && node.hasCoIndex() && tree.getEmptyCategories(node.getCoIndex()).size() == 1)
			node.setForm(PTBTag.E_TRACE);
	}
	
	static public void fixTrace(CTTree tree, CTNode node)
	{
		CTNode ante = node.getAntecedent();
		
		if (ante != null && ante.isFunctionTagAll(F_SBJ, F_TPC))
		{
			CTNode np = node.getParent();
			
			if (np.isFunctionTag(F_SBJ))
				node.setForm(E_PRO);
			else
			{
				node.setForm(E_NULL);
				
				if (!isPassiveEmptyCategory(node))
					node.setForm(E_TRACE);
			}
		}
	}
	
	static private void fixICHnPPA(CTTree tree, CTNode node)
	{
		if (node.hasCoIndex())
		{
			List<CTNode> list = tree.getEmptyCategories(node.getCoIndex());
			if (list.size() > 1) list.stream().forEach(n -> n.setForm(E_RNR));
		}
	}
	
	static private void fixPassiveNull(CTTree tree, CTNode node)
	{
		if (node.hasCoIndex())
		{
			if (!node.hasParent(n -> n.isSyntacticTag(C_NP)))
				node.setForm(E_ICH);
		}
	}
	
//	============================== Passive Nulls ==============================
	
	/**
	 * Finds reduced passive empty category ({@code *}) and links them to their antecedents in the specific tree.
	 * This method links most but not all antecedents; especially ones related to parenthetical phrases and topicalization.
	 * @see PTBLib#isPassiveEmptyCategory(CTNode)
	 */
	static public void linkReducedPassiveNulls(CTTree tree)
	{
		linkReducedPassiveNullsAux(tree, tree.getRoot());
	}
	
	/** Called by {@link #linkReducedPassiveNulls(CTTree)}. */
	static private void linkReducedPassiveNullsAux(CTTree tree, CTNode node)
	{
		if (isPassiveEmptyCategory(node) && !node.hasCoIndex())
		{
			CTNode parent = node.getParent();	// NP
			int index = parent.getParent().getCoIndex();
			
			if (index != -1)	// VP
			{
				List<CTNode> list = tree.getEmptyCategories(index);
				if (list != null) parent = list.get(0);
			}
			
			CTNode vp = parent.getHighestChainedAncestor(n -> n.isSyntacticTag(VP_RRC_UCP));

			if (vp.hasParent(n -> n.isSyntacticTag(NP_NML_WHNP)) || vp.hasParent(n -> n.isFunctionTag(F_NOM)))
			{
				node.setAntecedent(vp.getLeftNearestSibling(n -> n.isSyntacticTag(NP_NML_WHNP)));
				
				if (!node.hasAntecedent())
					node.setAntecedent(vp.getLeftNearestSibling(PTBLib::isCommonOrProperNoun));
				
				if (!node.hasAntecedent())
					node.setAntecedent(vp.getLeftNearestSibling(n -> n.isSyntacticTag(C_QP)));
				
				if (!node.hasAntecedent())
					node.setAntecedent(vp.getLeftNearestSibling(n -> n.isFunctionTag(F_NOM)));
			}
			else if (isClause(vp.getParent()))
			{
				node.setAntecedent(vp.getLeftNearestSibling(PTBLib::isNominalSubject));
				
				if (!node.hasAntecedent())	// VP-TPC
					node.setAntecedent(vp.getRightNearestSibling(PTBLib::isNominalSubject));
			}
		}
		
		for (CTNode child : node.getChildren())
			linkReducedPassiveNullsAux(tree, child);
	}
	
	/** @return {@code true} if the specific node represents a passive null ({@code *|*-\d}). */
	static public boolean isPassiveEmptyCategory(CTNode node)
	{
		if (isPassiveNull(node) && node.hasParent())
		{
			node = node.getParent();
			
			if (node.isSyntacticTag(C_NP) && !node.hasFunctionTag() &&
				node.hasParent() && node.getParent().isSyntacticTag(C_VP) &&
				node.hasLeftSibling(n -> n.isSyntacticTag(VBD_VBN)))
				return true;
		}
		
		return false;
	}
	
//	============================== Complementizers ==============================
	
	/**
	 * Finds relativizers and links them to their antecedents in the specific tree.
	 * This method links most but not all antecedents; especially when the relativizers are under {@code *-PRD} phrases.
	 */
	static public void linkRelativizers(CTTree tree)
	{
		linkRelativizersAux(tree, tree.getRoot());
	}
	
	/** Called by {@link #linkRelativizers(CTTree)}. */
	static private void linkRelativizersAux(CTTree tree, CTNode curr)
	{
		if (isWhLinkPhrase(curr))
		{
			CTNode comp = getRelativizer(curr);
			CTNode sbar = curr.getHighestChainedAncestor(n -> n.isSyntacticTag(C_SBAR));
			
			if (comp != null && sbar != null && !sbar.isFunctionTag(F_NOM) && ENUtils.isLinkingRelativizer(comp.getForm()))
			{
				if (sbar.getCoIndex() != -1)
				{
					List<CTNode> ecs = tree.getEmptyCategories(sbar.getCoIndex());
					
					if (ecs != null)
					{
						for (CTNode ec : ecs)
						{
							if (ec.getForm().startsWith(E_ICH) && ec.getParent().isSyntacticTag(C_SBAR))
							{
								sbar = ec.getParent();
								break;
							}
						}						
					}
				}
				else if (sbar.hasParent() && sbar.getParent().isSyntacticTag(C_UCP))
					sbar = sbar.getParent();
				
				CTNode p = sbar.getParent(), ante;
				if (p == null)	return;
				
				if (p.isSyntacticTag(C_NP))
				{
					if ((ante = sbar.getLeftNearestSibling(n -> n.isSyntacticTag(C_NP))) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isSyntacticTag(C_ADVP))
				{
					if ((ante = sbar.getLeftNearestSibling(n -> n.isSyntacticTag(C_ADVP))) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isSyntacticTag(C_VP))
				{
					if ((ante = sbar.getLeftNearestSibling(n -> n.isFunctionTag(F_PRD))) != null)
					{
						if (sbar.isFunctionTag(F_CLF) ||
						   (curr.isSyntacticTag(C_WHNP)   && ante.isSyntacticTag(C_NP)) ||
						   (curr.isSyntacticTag(C_WHPP)   && ante.isSyntacticTag(C_PP)) ||
						   (curr.isSyntacticTag(C_WHADVP) && ante.isSyntacticTag(C_ADVP)))
							comp.setAntecedent(ante);
					}
				}
				
				ante = comp.getAntecedent();
				
				while (ante != null && ante.isEmptyCategoryPhrase())
					ante = ante.getFirstTerminal().getAntecedent();
				
				comp.setAntecedent(ante);
			}
		}
		else
		{
			for (CTNode child : curr.getChildren())
				linkRelativizersAux(tree, child);
		}
	}
	
	/**
	 * @return the first relativizer under the specific node if exists; otherwise, {@code null}.
	 * The specific node must be a wh-phrase.
	 */
	static public CTNode getRelativizer(CTNode node)
	{
		if (!isWhPhrase(node)) return null;
		List<CTNode> terminals = node.getTerminals();
		
		if (node.isEmptyCategoryPhrase())
			return terminals.get(0);
		
		CTNode rel = terminals.stream().filter(PTBLib::isRelativizer).findAny().orElse(null);
		return rel != null ? rel : terminals.stream().filter(n -> ENUtils.isRelativizer(n.getForm())).findAny().orElse(null);
	}
	
//	============================== Coordination ==============================

	/** @return {@code true} if the specific node contains coordination. */
	static public boolean containsCoordination(CTNode node)
	{
		return containsCoordination(node, node.getChildren());
	}
	
	/** @return {@code true} if the specific list of children contains coordination. */
	static public boolean containsCoordination(CTNode parent, List<CTNode> children)
	{
		boolean sbj = false, prd = false;
		
		for (CTNode child : children)
		{
			if (child.isSyntacticTag(C_VP) || isSecondaryPredicate(child))
				prd = true;
			else if (isSubject(child))
				sbj = true;
		}
		
		if (sbj && prd) return false;
		
		if (parent.isSyntacticTag(C_UCP))
			return true;
		
		if (parent.isSyntacticTag(NP_NML_WHNP) && containsEtc(children))
			return true;
		
		return children.stream().anyMatch(PTBLib::isConjunction);
	}
	
	/** Called by {@link PTBLib#containsCoordination(CTNode, List)}. */
	static private boolean containsEtc(List<CTNode> children)
	{
		int i, size = children.size();
		CTNode child;
		
		for (i=size-1; i>0; i--)
		{
			child = children.get(i);
			
			if (isPunctuation(child)) continue;
			if (isEtc(child)) return true;
			break;
		}
		
		return false;
	}
	
	/** @return {@code true} if the specific node is et cetera (e.g., etc). */
	static public boolean isEtc(CTNode node)
	{
		return node.isFunctionTag(F_ETC) || node.getFirstTerminal().isFormLowercase("etc.");
	}
	
	/**
	 * @return {@code true} if this node is a conjunction.
	 * @see PTBLib#isConjunction(CTNode)
	 * @see PTBLib#isSeparator(CTNode)
	 */
	static public boolean isCoordinator(CTNode node)
	{
		return isConjunction(node) || isSeparator(node);
	}
	
	/** @return {@code true} if this node is a conjunction. */
	static public boolean isConjunction(CTNode node)
	{
		return node.isSyntacticTag(CONJUNCTION) || node.isFunctionTag(DDGTag.CC);
	}
	
	/** @return {@code true} if this node is a separator. */
	static public boolean isSeparator(CTNode node)
	{
		return node.isSyntacticTag(SEPARATOR);
	}
	
	/** @return {@code true} if this node is a correlative conjunction. */
	static public boolean isCorrelativeConjunction(CTNode node)
	{
		if (node.isSyntacticTag(P_CC))
		{
			return ENUtils.isCorrelativeConjunction(node.getForm());
		}
		else if (node.isSyntacticTag(C_CONJP))
		{
			String form = StringUtils.toLowerCase(node.toForms(StringConst.SPACE, false));
			return form.equals("not only");
		}
		
		return false;
	}
	
//	============================== Clausal/Phrasal Tags ==============================
	
	static public boolean isVerbPhrase(CTNode node)
	{
		return node.isSyntacticTag(C_VP);
	}
	
	static public boolean isSubject(CTNode node)
	{
		return node.isFunctionTag(F_SBJ);
	}
	
	static public boolean isSecondaryPredicate(CTNode node)
	{
		return node.isFunctionTag(F_PRD);
	}
	
	static public boolean isNominalSubject(CTNode node)
	{
		return node.andSF(C_NP, F_SBJ);
	}
	
	static public boolean isNominalPredicate(CTNode node)
	{
		return node.andSF(C_NP, F_PRD);
	}
	
	static public boolean isClause(CTNode node)
	{
		return isMainClause(node) || isSubordinateClause(node);
	}
	
	/** @return {@code true} if "S|SQ|SINV". */
	static public boolean isMainClause(CTNode node)
	{
		return node.isSyntacticTag(MAIN_CLAUSE);
	}
	
	static public boolean isSubordinateClause(CTNode node)
	{
		return node.getSyntacticTag().startsWith(C_SBAR);
	}

	static public boolean isNominalPhrase(CTNode node)
	{
		return node.isSyntacticTag(NOMINAL_PHRASE);
	}

	static public boolean isWhPhrase(CTNode node)
	{
		return node.getSyntacticTag().startsWith("WH");
	}
	
	static public boolean isWhLinkPhrase(CTNode node)
	{
		return node.isSyntacticTag(WH_LINK);
	}
	
	static public boolean isEditedPhrase(CTNode node)
	{
		return node.getSingleChained(n -> n.isSyntacticTag(C_EDITED)) != null;
	}
	
	static public String getLemmaOfApostropheS(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.P_VBZ) && node.isFormLowercase("'s"))
		{
			CTNode vp = node.getRightNearestSibling(n -> n.isSyntacticTag(PTBTag.C_VP));
			
			if (vp != null && vp.containsChild(n -> n.isSyntacticTag(P_VBN)) && (vp.getChildrenSize() < 2 || !isPassiveNull(vp.getFirstChild(1).getFirstTerminal())))
				return "have";
						
			return "be";
		}
		
		return null;
	}
	
//	============================== Empty Categories ============================== 
	
	static public boolean isPRO(CTNode node)
	{
		return node.isEmptyCategory() && node.isForm(E_PRO);
	}
	
	static public boolean isTrace(CTNode node)
	{
		return node.isEmptyCategory() &&node.isForm(E_TRACE);
	}
	
	static public boolean isPassiveNull(CTNode node)
	{
		return node.isEmptyCategory() &&node.isForm(E_NULL);
	}
	
	static public boolean isNullComplementizer(CTNode node)
	{
		return node.isEmptyCategory() &&node.isForm(E_ZERO);
	}
	
	static public boolean isExpletive(CTNode node)
	{
		return node.isEmptyCategory() &&node.isForm(E_EXP);
	}
	
	static public boolean isRightNodeRaising(CTNode node)
	{
		return node.isEmptyCategory() &&node.isForm(E_RNR);
	}
	
	static public boolean isInterpretConstituentHere(CTNode node)
	{
		return node.isEmptyCategory() && node.isForm(E_ICH);
	}
	
	static public boolean isPermanentPredictableAmbiguity(CTNode node)
	{
		return node.isEmptyCategory() && node.isForm(E_PPA);
	}
	
	static public boolean isDiscontinuousConstituent(CTNode node)
	{
		return isRightNodeRaising(node) || isInterpretConstituentHere(node) || isPermanentPredictableAmbiguity(node);
	}
	
//	============================== Part-of-Speech Tags ============================== 

	static public <N extends AbstractNode<N>>boolean isNoun(N node)
	{
		String tag = node.getSyntacticTag();
		return tag.startsWith(P_NN) || tag.equals(P_PRP) || tag.equals(P_WP);
	}

	static public <N extends AbstractNode<N>>boolean isCommonOrProperNoun(N node)
	{
		return node.getSyntacticTag().startsWith(P_NN);
	}

	static public <N extends AbstractNode<N>>boolean isVerb(N node)
	{
		return node.getSyntacticTag().startsWith(P_VB);
	}

	static public <N extends AbstractNode<N>>boolean isAdjective(N node)
	{
		return node.getSyntacticTag().startsWith(P_JJ);
	}

	static public <N extends AbstractNode<N>>boolean isAdverb(N node)
	{
		String tag = node.getSyntacticTag();
		return tag.startsWith(P_RB) || tag.equals(P_WRB);
	}

	static public <N extends AbstractNode<N>>boolean isRelativizer(N node)
	{
		return RELATIVIZER.contains(node.getSyntacticTag());
	}

	static public <N extends AbstractNode<N>>boolean isPunctuation(N node)
	{
		return PUNCTUATION.contains(node.getSyntacticTag());
	}
}