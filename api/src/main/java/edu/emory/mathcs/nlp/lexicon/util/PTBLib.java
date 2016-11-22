package edu.emory.mathcs.nlp.lexicon.util;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.DSRTag;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.lexicon.constituency.CTLib;
import edu.emory.mathcs.nlp.lexicon.constituency.CTNode;
import edu.emory.mathcs.nlp.lexicon.constituency.CTTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PTBLib extends CTLib implements PTBTag
{
	static final public Predicate<CTNode> M_S      = matchC(C_S);
	static final public Predicate<CTNode> M_NP     = matchC(C_NP);
	static final public Predicate<CTNode> M_VP     = matchC(C_VP);
	static final public Predicate<CTNode> M_QP     = matchC(C_QP);
	static final public Predicate<CTNode> M_ADVP   = matchC(C_ADVP);
	static final public Predicate<CTNode> M_SBAR   = matchC(C_SBAR);
	static final public Predicate<CTNode> M_EDITED = matchC(C_EDITED);
	static final public Predicate<CTNode> M_TO     = matchC(P_TO);
	static final public Predicate<CTNode> M_POS    = matchC(P_POS);
	
	static final public Predicate<CTNode> M_SBJ    = matchF(F_SBJ);
	static final public Predicate<CTNode> M_NOM    = matchF(F_NOM);
	static final public Predicate<CTNode> M_PRD    = matchF(F_PRD);
	
	static final public Predicate<CTNode> M_NP_SBJ = matchCF(C_NP, F_SBJ);
	static final public Predicate<CTNode> M_NP_PRD = matchCF(C_NP, F_PRD);
	
	static final public Predicate<CTNode> M_NNx    = matchCp(P_NN);
	static final public Predicate<CTNode> M_VBx    = matchCp(P_VB);
	static final public Predicate<CTNode> M_WHx    = matchCp("WH");
	static final public Predicate<CTNode> M_Sx     = matchCp(C_S);
	static final public Predicate<CTNode> M_SBARx  = matchCp(C_SBAR);
	
	static final public Predicate<CTNode> M_MD_TO_VBx   = matchCo(DSUtils.toHashSet(P_MD, P_TO, P_VB, P_VBP, P_VBZ, P_VBD, P_VBG, P_VBN));
	static final public Predicate<CTNode> M_S_SBAR      = matchCo(DSUtils.toHashSet(C_S, C_SBAR));
	static final public Predicate<CTNode> M_NP_NML_WHNP = matchCo(DSUtils.toHashSet(C_NP, C_NML, C_WHNP));
	static final public Predicate<CTNode> M_VBD_VBN     = matchCo(DSUtils.toHashSet(P_VBD, P_VBN));
	static final public Predicate<CTNode> M_VP_RRC_UCP  = matchCo(DSUtils.toHashSet(C_VP, C_RRC, C_UCP));
	static final public Predicate<CTNode> M_IN_DT_TO    = matchCo(DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_DT, PTBTag.P_TO));
	static final public Predicate<CTNode> M_VP_OR_PRD   = matchCoF(C_VP, F_PRD);
	
	static final public Set<String> LGS_PHRASE		= DSUtils.toHashSet(C_PP, C_SBAR);
	static final public Set<String> MAIN_CLAUSE		= DSUtils.toHashSet(C_S, C_SQ, C_SINV);
	static final public Set<String> EDITED_PHRASE	= DSUtils.toHashSet(C_EDITED, C_EMBED);
	static final public Set<String> NOMINAL_PHRASE	= DSUtils.toHashSet(C_NP, C_NML, C_NX, C_NAC);
	static final public Set<String> WH_LINK			= DSUtils.toHashSet(C_WHNP, C_WHPP, C_WHADVP);
	static final public Set<String> SEPARATOR		= DSUtils.toHashSet(P_COMMA, P_COLON);
	static final public Set<String> CONJUNCTION		= DSUtils.toHashSet(P_CC, C_CONJP);
	static final public Set<String> PUNCTUATION     = DSUtils.toHashSet(P_COLON, P_COMMA, P_PERIOD, P_LQ, P_RQ, P_LRB, P_RRB, P_HYPH, P_NFP, P_SYM, P_PUNC);
	static final public Set<String> RELATIVIZER     = DSUtils.toHashSet(P_WDT, P_WP, P_WPS, P_WRB);

	static final public Pattern P_PASSIVE_NULL = PatternUtils.createClosedORPattern("\\*","\\*-\\d+");

	
	
	
	static final public Set<String> NP_NML_WHNP = Sets.newHashSet(C_NP, C_NML, C_WHNP);
	
	
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
	}
	
//	============================== Fix Function Tags ==============================

	/**
	 * Fixes inconsistent function tags in the specific tree.
	 * @see PTBLib#fixSBJ(CTNode)
	 * @see PTBLib#fixLGS(CTNode)
	 * @see PTBLib#fixCLF(CTNode)
	 */
	static public void fixFunctionTags(CTTree tree)
	{
		fixFunctionTagsAux(tree.getRoot());
	}
	
	/** Called by {@link PTBLib#fixFunctionTags(CTTree)}. */
	static private void fixFunctionTagsAux(CTNode node)
	{
		if (!fixSBJ(node) && !fixLGS(node) && !fixCLF(node) && !fixPRD(node))
			;	// no error in this node
		
		for (CTNode child : node.getChildren())
			fixFunctionTagsAux(child);
	}
	
	/** If the specific node contains the function tag {@link PTBTag#F_SBJ} and it is the only child of its parent, moves the tag to its parent. */
	static private boolean fixSBJ(CTNode node)
	{
		if (node.isFunctionTag(F_SBJ))
		{
			CTNode parent = node.getParent();
			
			if (parent.getChildrenSize() == 1 && !parent.isSyntacticTag(EDITED_PHRASE) && parent.hasNoFunctionTag())
			{
				node.removeFunctionTag(F_SBJ);
				parent.addFunctionTag(F_SBJ);
				parent.setSyntacticTag(node.getSyntacticTag());
				return true;
			}
			
			if (isClause(node) && (!isClause(parent) || parent.containsChild(M_NP_SBJ)))
			{
				node.removeFunctionTag(F_SBJ);
				node.addFunctionTag(F_ADV);
				return true;
			}
		}
		
		return false;
	}
	
	/** If the specific node contains the function tag {@link PTBTag#F_LGS} and it is not a prepositional phrase, moves the tag to its parent. */
	static private boolean fixLGS(CTNode node)
	{
		if (node.isFunctionTag(F_LGS) && !node.isSyntacticTag(C_PP))
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
		if (node.isFunctionTag(F_CLF) && isMainClause(node))
		{
			CTNode desc = node.getFirstDescendant(M_SBARx);
			node.removeFunctionTag(F_CLF);
			
			if (desc != null)
			{
				desc.addFunctionTag(F_CLF);
				return true;
			}
		}
		
		return false;
	}
	
	static public boolean fixPRD(CTNode node)
	{
		if (node.isFunctionTag(F_PRD))
		{
			if (isClause(node) || (node.isSyntacticTag(C_ADVP) && node.getChildrenSize() == 1 && node.getFirstChild().isFormIgnoreCase("so")))
			{
				node.removeFunctionTag(F_PRD);
				return true;	
			}
		}
		
		return false;
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
	static private void linkReducedPassiveNullsAux(CTTree tree, CTNode curr)
	{
		if (isPassiveEmptyCategory(curr) && curr.isForm("*"))
		{
			CTNode parent = curr.getParent();	// NP
			int index = parent.getParent().getCoIndex();
			
			if (index != -1)	// VP
			{
				List<CTNode> list = tree.getEmptyCategories(index);
				if (list != null) parent = list.get(0);
			}
			
			CTNode vp = parent.getHighestChainedAncestor(M_VP_RRC_UCP);

			if (vp.getParent().matches(M_NP_NML_WHNP) || vp.getParent().isFunctionTag(F_NOM))
			{
				curr.setAntecedent(vp.getLeftNearestSibling(M_NP_NML_WHNP));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_NNx));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_QP));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_NOM));
			}
			else if (isClause(vp.getParent()))
			{
				curr.setAntecedent(vp.getLeftNearestSibling(M_NP_SBJ));
				
				if (!curr.hasAntecedent())	// VP-TPC
					curr.setAntecedent(vp.getRightNearestSibling(M_NP_SBJ));
			}
		}
		
		for (CTNode child : curr.getChildren())
			linkReducedPassiveNullsAux(tree, child);
	}
	
	/** @return {@code true} if the specific node represents a passive null ({@code *|*-\d}). */
	static public boolean isPassiveEmptyCategory(CTNode node)
	{
		if (node.isEmptyCategory() && node.isForm(P_PASSIVE_NULL) && node.hasParent())
		{
			node = node.getParent();
			
			if (node.isSyntacticTag(C_NP) && node.hasNoFunctionTag() &&
				node.hasParent() && node.getParent().isSyntacticTag(C_VP) &&
				node.hasLeftSibling() && node.getLeftSibling().matches(M_VBD_VBN))
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
		linkComlementizersAux(tree, tree.getRoot());
	}
	
	/** Called by {@link #linkRelativizers(CTTree)}. */
	static private void linkComlementizersAux(CTTree tree, CTNode curr)
	{
		if (isWhPhraseLink(curr))
		{
			CTNode comp = getRelativizer(curr);
			CTNode sbar = curr.getHighestChainedAncestor(M_SBAR);
			
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
					if ((ante = sbar.getLeftNearestSibling(M_NP)) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isSyntacticTag(C_ADVP))
				{
					if ((ante = sbar.getLeftNearestSibling(M_ADVP)) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isSyntacticTag(C_VP))
				{
					if ((ante = sbar.getLeftNearestSibling(M_PRD)) != null)
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
				linkComlementizersAux(tree, child);
		}
	}
	
	/**
	 * @return the first relativizer under the specific node if exists; otherwise, {@code null}.
	 * The specific node must be a wh-phrase.
	 */
	static public CTNode getRelativizer(CTNode node)
	{
		if (!isWhPhrase(node))
			return null;
		
		List<CTNode> terminals = node.getTerminals();
		
		if (node.isEmptyCategoryPhrase())
			return terminals.get(0);
		
		for (CTNode term : terminals)
		{
			if (isRelativizer(term.getSyntacticTag()))
				return term;
		}
		
		for (CTNode term : terminals)
		{
			if (ENUtils.isRelativizer(term.getForm()))
				return term;
		}
			
		return null;
	}
	
	static public CTNode getWhPhrase(CTNode node)
	{
		return getNode(node, M_WHx, true);
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
		if (parent.isSyntacticTag(C_UCP))
			return true;
		
		if (parent.isSyntacticTag(NP_NML_WHNP) && containsEtc(children))
			return true;
		
		for (CTNode child : children)
		{
			if (isConjunction(child))
				return true;
		}

		return false;
	}
	
	/** Called by {@link PTBLib#containsCoordination(CTNode, List)}. */
	static private boolean containsEtc(List<CTNode> children)
	{
		int i, size = children.size();
		CTNode child;
		
		for (i=size-1; i>0; i--)
		{
			child = children.get(i);
			
			if (isPunctuation(child.getSyntacticTag()))	continue;
			if (isEtc(child)) return true;
			break;
		}
		
		return false;
	}
	
	/** @return {@code true} if the specific node is et cetera (e.g., etc). */
	static public boolean isEtc(CTNode node)
	{
		return node.isFunctionTag(F_ETC) || node.getFirstTerminal().isFormIgnoreCase("etc.");
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
		return node.isSyntacticTag(CONJUNCTION) || node.isFunctionTag(DSRTag.CC);
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
	
	static public boolean isWhPhraseLink(CTNode node)
	{
		return node.isSyntacticTag(WH_LINK);
	}
	
	static public boolean isWhPhrase(CTNode node)
	{
		return M_WHx.test(node);
	}
	
	static public boolean isEditedPhrase(CTNode node)
	{
		return getNode(node, M_EDITED, true) != null;
	}
	
	static public CTNode getNode(CTNode node, Predicate<CTNode> matcher, boolean recursive)
	{
		if (matcher.test(node))
			return node;
		
		if (recursive && node.getChildrenSize() == 1)
			return getNode(node.getFirstChild(), matcher, recursive);
		
		return null;
	}
	
//	============================== Empty Categories ============================== 
	
	static public boolean isPRO(CTNode node)
	{
		return node.formStartsWith(E_PRO);
	}
	
	static public boolean isTrace(CTNode node)
	{
		return node.formStartsWith(E_TRACE);
	}
	
	static public boolean isPassiveNull(CTNode node)
	{
		return node.isForm(PTBLib.P_PASSIVE_NULL);
	}
	
	static public boolean isNullComplementizer(CTNode node)
	{
		return node.isForm(E_ZERO);
	}
	
	static public boolean isExpletive(CTNode node)
	{
		return node.formStartsWith(E_EXP);
	}
	
	static public boolean isDiscontinuousConstituent(CTNode node)
	{
		return isRightNodeRaising(node) || isInterpretConstituentHere(node) || isPermanentPredictableAmbiguity(node);
	}
	
	static public boolean isRightNodeRaising(CTNode node)
	{
		return node.getForm().startsWith(E_RNR);
	}
	
	static public boolean isInterpretConstituentHere(CTNode node)
	{
		return node.getForm().startsWith(E_ICH);
	}
	
	static public boolean isPermanentPredictableAmbiguity(CTNode node)
	{
		return node.getForm().startsWith(E_PPA);
	}
	
//	============================== Part-of-Speech Tags ============================== 

	static public boolean isNoun(String tag)
	{
		return tag.startsWith(P_NN) || tag.equals(P_PRP) || tag.equals(P_WP);
	}

	static public boolean isCommonOrProperNoun(String tag)
	{
		return tag.startsWith(P_NN);
	}

	static public boolean isPronoun(String posTag)
	{
		return posTag.equals(P_PRP) || posTag.equals(P_PRPS);
	}

	static public boolean isVerb(String posTag)
	{
		return posTag.startsWith(P_VB);
	}

	static public boolean isAdjective(String posTag)
	{
		return posTag.startsWith(P_JJ);
	}

	static public boolean isAdverb(String posTag)
	{
		return posTag.startsWith(P_RB) || posTag.equals(P_WRB);
	}

	static public boolean isRelativizer(String posTag)
	{
		return RELATIVIZER.contains(posTag);
	}

	static public boolean isPunctuation(String posTag)
	{
		return PUNCTUATION.contains(posTag);
	}
}