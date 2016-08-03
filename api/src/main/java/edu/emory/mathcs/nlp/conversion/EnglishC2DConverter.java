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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UnknownFormatConversionException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.collection.arc.AbstractArc;
import edu.emory.mathcs.nlp.common.constituent.CTLib;
import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.propbank.PBLib;
import edu.emory.mathcs.nlp.common.treebank.PTBLib;
import edu.emory.mathcs.nlp.common.treebank.CTTag;
import edu.emory.mathcs.nlp.common.treebank.DEPLibEn;
import edu.emory.mathcs.nlp.common.treebank.DEPTagEn;
import edu.emory.mathcs.nlp.common.treebank.PBArc;
import edu.emory.mathcs.nlp.common.treebank.PTBTag;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.dep.DEPArc;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.conversion.util.C2DInfo;
import edu.emory.mathcs.nlp.conversion.util.HeadRule;
import edu.emory.mathcs.nlp.conversion.util.HeadRuleMap;


/**
 * Constituent to dependency converter for English.
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishC2DConverter extends C2DConverter
{
	
	private final Set<String> S_NPADVMOD	= DSUtils.toHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_QP);
	private final Set<String> S_ADVCL		= DSUtils.toHashSet(PTBTag.C_S, PTBTag.C_SBAR, PTBTag.C_SINV);
	private final Set<String> S_NFMOD		= DSUtils.toHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_WHNP);
	private final Set<String> S_CCOMP		= DSUtils.toHashSet(PTBTag.C_S, PTBTag.C_SQ, PTBTag.C_SINV, PTBTag.C_SBARQ);
	private final Set<String> S_META		= DSUtils.toHashSet(PTBTag.C_EDITED, PTBTag.C_EMBED, PTBTag.C_LST, PTBTag.C_META, PTBTag.P_CODE, PTBTag.C_CAPTION, PTBTag.C_CIT, PTBTag.C_HEADING, PTBTag.C_TITLE);
	private final Set<String> S_MARK		= DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_TO, PTBTag.P_DT);
	private final Set<String> S_POSS		= DSUtils.toHashSet(PTBTag.P_PRPS, PTBTag.P_WPS);
	private final Set<String> S_INTJ		= DSUtils.toHashSet(PTBTag.C_INTJ, PTBTag.P_UH);
	private final Set<String> S_PRT 		= DSUtils.toHashSet(PTBTag.C_PRT, PTBTag.P_RP);
//	private final Set<String> S_NUM			= DSUtils.toHashSet(CTLibEn.P_CD, PTBTag.C_QP);
	private final Set<String> S_DET			= DSUtils.toHashSet(PTBTag.P_DT, PTBTag.P_WDT, PTBTag.P_WP);
	private final Set<String> S_AUX			= DSUtils.toHashSet(PTBTag.P_MD, PTBTag.P_TO);
//	private final Set<String> S_NN			= DSUtils.toHashSet(PTBTag.C_NML, PTBTag.C_NP);

//	private final Set<String> S_ADJT_PHRASE	= DSUtils.toHashSet(PTBTag.C_ADJP, PTBTag.C_WHADJP);
	private final Set<String> S_NOUN_PHRASE	= DSUtils.toHashSet(PTBTag.C_NP, PTBTag.C_NML);
	private final Set<String> S_PREP_PHRASE	= DSUtils.toHashSet(PTBTag.C_PP, PTBTag.C_WHPP);
	private final Set<String> S_ADVB_PHRASE	= DSUtils.toHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP, PTBTag.C_PP);
	private final Set<String> S_PREPOSITION	= DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_TO);
//	private final Set<String> S_PARTICIPIAL	= DSUtils.toHashSet(CTLibEn.P_VBG, CTLibEn.P_VBN);
	private final Set<String> S_PREP_DET	= DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_DT);
	
	private final Set<String> S_COMP_PARENT_S = DSUtils.toHashSet(PTBTag.C_VP, PTBTag.C_SINV, PTBTag.C_SQ);
	private final Set<String> S_COMP_PARENT_A = DSUtils.toHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP);
	private final Set<String> S_NMOD_PARENT	  = DSUtils.toHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_NX, PTBTag.C_WHNP);
	private final Set<String> S_POSS_PARENT	  = DSUtils.toHashSet(PTBTag.C_NP, PTBTag.C_NML, PTBTag.C_WHNP, PTBTag.C_QP, PTBTag.C_ADJP);
	
	private final Set<String> S_COMPLM = DSUtils.toHashSet("that", "if", "whether");
	private final int SIZE_HEAD_FLAGS = 4;
	
	/** Syntactic function tags. */
	private final Set<String> SYN_TAGS = DSUtils.toHashSet(PTBTag.F_ADV, PTBTag.F_CLF, PTBTag.F_CLR, PTBTag.F_DTV, PTBTag.F_NOM, PTBTag.F_PUT, PTBTag.F_PRD, PTBTag.F_TPC);
	
	/** Semantic function tags. */
	private final Set<String> SEM_TAGS = DSUtils.toHashSet(PTBTag.F_BNF, PTBTag.F_DIR, PTBTag.F_EXT, PTBTag.F_LOC, PTBTag.F_MNR, PTBTag.F_PRP, PTBTag.F_TMP, PTBTag.F_VOC);
	
	/** Mappings between phrasal/clausal tags and phrasal/pos tags for coordination. */
	@SuppressWarnings("serial")
	private final Map<String,Pattern> COORD_MAP = new HashMap<String,Pattern>()
	{{
		COORD_MAP.put(PTBTag.C_S     , PatternUtils.createClosedORPattern("S","SINV","SQ","SBARQ"));
		COORD_MAP.put(PTBTag.C_SBAR  , PatternUtils.createClosedORPattern("SBAR.*"));
		COORD_MAP.put(PTBTag.C_SBARQ , PatternUtils.createClosedORPattern("SBAR.*"));
		COORD_MAP.put(PTBTag.C_SINV  , PatternUtils.createClosedORPattern("S","SINV"));
		COORD_MAP.put(PTBTag.C_SQ	   , PatternUtils.createClosedORPattern("S","SQ","SBARQ"));

		COORD_MAP.put(PTBTag.C_ADJP  , PatternUtils.createClosedORPattern("ADJP","JJ.*","VBN","VBG"));
		COORD_MAP.put(PTBTag.C_ADVP  , PatternUtils.createClosedORPattern("ADVP","RB.*"));
		COORD_MAP.put(PTBTag.C_INTJ  , PatternUtils.createClosedORPattern("INTJ","UH"));
		COORD_MAP.put(PTBTag.C_NAC   , PatternUtils.createClosedORPattern("NP"));
		COORD_MAP.put(PTBTag.C_NML   , PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		COORD_MAP.put(PTBTag.C_NP    , PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		COORD_MAP.put(PTBTag.C_NX    , PatternUtils.createClosedORPattern("NX"));
		COORD_MAP.put(PTBTag.C_PP    , PatternUtils.createClosedORPattern("PP","IN","VBG"));
		COORD_MAP.put(PTBTag.C_PRT   , PatternUtils.createClosedORPattern("PRT","RP"));
		COORD_MAP.put(PTBTag.C_VP    , PatternUtils.createClosedORPattern("VP","VB.*"));
		COORD_MAP.put(PTBTag.C_WHADJP, PatternUtils.createClosedORPattern("JJ.*","VBN","VBG"));
		COORD_MAP.put(PTBTag.C_WHADVP, PatternUtils.createClosedORPattern("RB.*","WRB","IN"));
		COORD_MAP.put(PTBTag.C_WHNP  , PatternUtils.createClosedORPattern("NN.*","WP"));
	}};
	
	/** {@code true} if the constituent tag is {@link PTBTag#C_S}. */
	private final Predicate<CTNode> MT_S      = CTLib.matchC(PTBTag.C_S);
	/** {@code true} if the constituent tag is {@link PTBTag#P_TO}. */
	private final Predicate<CTNode> MT_TO     = CTLib.matchC(PTBTag.P_TO);
	/** {@code true} if the constituent tag is {@link PTBTag#P_POS}. */
	private final Predicate<CTNode> MT_POS    = CTLib.matchC(PTBTag.P_POS);
	/** {@code true} if the constituent tag is {@link CTTag#NONE}. */
	private final Predicate<CTNode> MT_NONE   = CTLib.matchC(CTTag.NONE);
	/** {@code true} if the constituent tag is {@link PTBTag#C_NP} and the function tag is {@link PTBTag#F_PRD}. */
	private final Predicate<CTNode> MT_NP_PRD = CTLib.matchCF(PTBTag.C_NP, PTBTag.F_PRD);
	/** {@code true} if the constituent tag is {@link PTBTag#P_IN} or {@link PTBTag#P_DT}. */
	private final Predicate<CTNode> MT_IN_DT  = CTLib.matchCo(DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_DT));
	
	private final Emoticon emoticon = new Emoticon();
	
	public EnglishC2DConverter(HeadRuleMap headrules)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));
	}
	
	@Override
	public NLPNode[] toDependencyGraph(CTTree cTree)
	{
		Map<CTNode,Deque<CTNode>> xsubj = new HashMap<>();
		Map<CTNode,Deque<CTNode>> rnr   = new HashMap<>();
		
		PTBLib.preprocess(cTree);
		if (!mapEmtpyCategories(cTree, xsubj, rnr))	return null;
		setHeads(cTree.getRoot());
		NLPNode[] tree = getDEPTree(cTree, xsubj, rnr);
		
		if (tree != null) finalize(tree);
		return tree;
	}

//	============================= Empty Categories ============================= 
	
	/**
	 * Removes, relocates empty categories in the specific tree. 
	 * @param cTree the constituent tree to be processed.
	 * @return {@true} if the constituent tree contains nodes after relocating empty categories.
	 */
	private boolean mapEmtpyCategories(CTTree cTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
	{
		for (CTNode node : cTree.getTerminalList())
		{
			if (!node.isEmptyCategory())	continue;
			if (node.getParent() == null)	continue;
			
			if      (node.wordFormStartsWith(PTBTag.E_PRO))
				mapPRO(cTree, node, xsubj);
			else if (node.wordFormStartsWith(PTBTag.E_TRACE))
				mapTrace(cTree, node);
			else if (node.matchesWordForm(PTBLib.P_PASSIVE_NULL))
				mapPassiveNull(cTree, node, xsubj);
			else if (node.isWordForm(PTBTag.E_ZERO))
				continue;
			else if (PTBLib.isDiscontinuousConstituent(node))
				mapDiscontinuousConstituent(cTree, node, rnr);
//			else if (node.wordFormStartsWith(PTBTag.E_EXP))
//				reloateEXP(cTree, node);
			else
				removeNode(node);
		}
		
		return cTree.getRoot().getChildrenSize() > 0;
	}
	
	/**
	 * (TOP (S (NP-SBJ-1 (NNP John))
     *         (VP (VBD bought)
     *             (NP (DT a)
     *                 (NN book))
     *             (S-PRP (NP-SBJ (-NONE- *PRO*-1))
     *                    (VP (TO to)
     *                        (VP (VB teach)
     *                            (NP (PRP people))))))))
     *      
     * (TOP (S (NP-SBJ-1 (NNP John))
     *         (VP (VBD had)
     *             (S (NP-SBJ-2 (-NONE- *-1))
     *                (VP (TO to)
     *                    (VP (VB buy)
     *                        (NP (DT a)
     *                            (NN sugar))
     *                        (S-PRP (NP-SBJ (-NONE- *PRO*-2))
     *                               (VP (TO to)
     *                                   (VP (VB teach)
     *                                       (NP (NNS people)))))))))))
	 */
	private void mapPRO(CTTree cTree, CTNode ec, Map<CTNode,Deque<CTNode>> xsubj)
	{
		CTNode np = ec.getParent();
		CTNode vp = np.getParent().getFirstLowestChainedDescendant(PTBLib.M_VP);
		
		if (vp == null)		// small clauses
			handleSmallClause(np, ec);
		else
		{
			CTNode ante;
			
			if ((ante = ec.getAntecedent()) != null && PTBLib.isWhPhrase(ante))	// relative clauses
			{
				if (cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex()).size() == 1)
					mapTrace(cTree, ec);
			}
			
			addXSubject(ec, xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapTrace(CTTree cTree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			removeNode(ec);
		else if (ante.hasFunctionTag(PTBTag.F_TPC))
		{
			if (!ante.hasFunctionTag(PTBTag.F_SBJ))
			{
				CTNode parent = ec.getParent();
				parent.removeChild(ec);
				replaceEmptyCategory(parent, ante);
			}
			else
				removeNode(ec);
		}
		else	// relative clauses
		{
			CTNode parent = ante.getHighestChainedAncestor(PTBLib.M_SBAR);
			if (parent != null) parent.addFunctionTag(DEPTagEn.DEP_RELCL);
			replaceEmptyCategory(ec, ante);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPassiveNull(CTTree cTree, CTNode ec, Map<CTNode,Deque<CTNode>> xsubj)
	{
		CTNode np = ec.getParent();
		
		if (np.hasFunctionTag(PTBTag.F_SBJ))
		{
			// small clauses
			if (np.getRightNearestSibling(PTBLib.M_VP) == null)
				handleSmallClause(np, ec);
			else
				addXSubject(ec, xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapDiscontinuousConstituent(CTTree cTree, CTNode ec, Map<CTNode,Deque<CTNode>> rnr)
	{
		CTNode parent = ec.getParent();
		CTNode ante   = ec.getAntecedent();
		
		if (ec.wordFormStartsWith(PTBTag.E_ICH) && parent.getLeftNearestSibling(PTBLib.M_WHx) != null)
			removeNode(ec);
		else if (ante == null || ec.isDescendantOf(ante))
			removeNode(ec);
		else
		{
			List<CTNode> list = cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex());
			boolean isRNR = PTBLib.isRNR(ec);
			int i, size = list.size();
			CTNode node;
			
			Deque<CTNode> dq = isRNR ? new ArrayDeque<CTNode>() : null;
			
			if (ec.getTerminalID() < ante.getFirstTerminal().getTerminalID())
			{		
				for (i=0; i<size-1; i++)
				{
					node = list.get(i);
					if (isRNR)	dq.addLast(node.getParent().getParent());
					removeNode(node);
				}
				
				ec = list.get(size-1);
			}
			else
			{
				for (i=size-1; i>0; i--)
				{
					node = list.get(i);
					if (isRNR)	dq.addFirst(node.getParent().getParent());
					removeNode(node);
				}
				
				ec = list.get(0);
			}
			
			if (isRNR && !dq.isEmpty())
				rnr.put(ante, dq);
			
			parent = ec.getParent();
			parent.removeChild(ec);
			replaceEmptyCategory(parent, ante);
		}
	}
	
	/**
     * (TOP (S (NP-SBJ (PRP I))
     *         (VP (VBP call)
     *             (NP-1 (NNP John))
     *             (S-CLR (NP-SBJ (-NONE- *PRO*-1))
     *                    (NP-PRD (DT a)
     *                            (NN genius))))))
     * 
     * (TOP (S (NP-SBJ-1 (NNP John))
     *         (VP (VBZ is)
     *             (VP (VBN called)
     *                 (NP-2 (-NONE- *-1))
     *                 (S-CLR (NP-SBJ (-NONE- *PRO*-2))
     *                        (NP-PRD (DT a)
     *                                (NN genius)))))))
     * 
     * (TOP (S (NP (NP (NNP John))
     *             (SBAR (WHNP-1 (WP who))
     *                   (S (NP-SBJ (PRP I))
     *                      (VP (VBP call)
     *                          (NP-2 (-NONE- *T*-1))
     *                          (S-CLR (NP-SBJ (-NONE- *PRO*-2))
     *                                 (NP-PRD (DT a)
     *                                 (NN genius)))))))
     *         (VP (VBZ is)
     *             (ADVP-LOC (RB here)))))                       
	 */
	private void handleSmallClause(CTNode np, CTNode ec)
	{
		CTNode s   = np.getParent();
		CTNode prd = s.getFirstChild(PTBLib.M_PRD);
		
		if (prd != null && (!s.hasFunctionTag() || s.hasFunctionTag(PTBTag.F_CLR)))
		{
			s.clearFunctionTags();
			s.addFunctionTag(DEPTagEn.DEP_OPRD);
		}

		removeNode(ec);
	}
	
/*	private void reloateEXP(CTTree cTree, CTNode ec)
	{
		int idx = ec.form.lastIndexOf("-");
		
		if (idx != -1)
		{
			int coIndex = Integer.parseInt(ec.form.substring(idx+1));
			CTNode ante = cTree.getCoIndexedAntecedent(coIndex);
			if (ante != null)	ante.addFTag(DEPTagEn.CONLL_EXTR);
		}
		
		removeCTNode(ec);
	}*/
	
	/**
	 * @param ec empty subject.
	 * @param xsubj key: antecedent, value: list of clauses containing empty subjects.
	 */
	private void addXSubject(CTNode ec, Map<CTNode, Deque<CTNode>> xsubj)
	{
		CTNode ante = ec.getAntecedent();
		
		while (ante != null && ante.isEmptyCategoryTerminal())
		{
			if (PTBLib.isWhPhrase(ante)) return;
			ante = ante.getFirstTerminal().getAntecedent();
		}
		
		if (ante != null)
		{
			CTNode s = ec.getNearestAncestor(MT_S);
			if (s != null) xsubj.computeIfAbsent(ante, n -> new ArrayDeque<>()).add(s);
		}
	}
	
//	============================= Set Heads =============================
	
	@Override
	protected void setHeadsAux(HeadRule rule, CTNode curr)
	{
		if (findHeadsCoordination(rule, curr))	return;
		
//		findHyphens(curr);
		findHeadsApposition(curr);
		findHeadsSmallClause(curr);

		CTNode head = getHead(rule, curr.getChildrenList(), SIZE_HEAD_FLAGS);
		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null); 
		curr.setC2DInfo(new C2DInfo(head));
	}
	
	/**
	 * If the specific node contains a coordination structure, find the head of each coordination.
	 * @param curr the specific node to be compared. 
	 * @return {@code true} if this node contains a coordination structure.
	 */
	private boolean findHeadsCoordination(HeadRule rule, CTNode curr)
	{
		// skip pre-conjunctions and punctuation
		int i, sId, size = curr.getChildrenSize();
		CTNode node;
		
		for (sId=0; sId<size; sId++)
		{
			node = curr.getChild(sId);
			
			if (!PTBLib.isPunctuation(node.getConstituentTag()) && !PTBLib.isConjunction(node) && !node.isEmptyCategoryTerminal())
				break;
		}
		
		if (!PTBLib.containsCoordination(curr, curr.getChildrenList(sId)))
			return false;
		
		// find conjuncts
		Pattern rTags = getConjunctPattern(curr, sId, size);
		CTNode prevHead = null, mainHead = null;
		boolean isFound = false;
		int bId = 0, eId = sId;
		
		for (; eId<size; eId++)
		{
			node = curr.getChild(eId);
			
			if (PTBLib.isCoordinator(node))
			{
				if (isFound)
				{
					prevHead = findHeadsCoordinationAux(rule, curr, bId, eId, prevHead);
//					prevHead = findHeadsCoordinationAux(rule, curr, bId, eId, mainHead);
					if (mainHead == null) mainHead = prevHead;
					setHeadCoord(node, prevHead, getDEPLabel(node, curr, prevHead));
//					setHeadCoord(node, mainHead, getDEPLabel(node, curr, mainHead));
					isFound = false;
			
					bId = eId + 1;
				}
				else if (prevHead != null)
				{
					for (i=bId; i<=eId; i++)
					{
						node = curr.getChild(i);
						setHeadCoord(node, prevHead, getDEPLabel(node, curr, prevHead));
//						setHeadCoord(node, mainHead, getDEPLabel(node, curr, mainHead));
					}
					
					bId = eId + 1;
				}
			}
			else if (isConjunct(node, curr, rTags))
				isFound = true;
		}
		
		if (mainHead == null) return false;
		
		if (eId - bId > 0)
			findHeadsCoordinationAux(rule, curr, bId, eId, prevHead);
//			findHeadsCoordinationAux(rule, curr, bId, eId, mainHead);
		
		curr.setC2DInfo(new C2DInfo(mainHead));
		return true;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private Pattern getConjunctPattern(CTNode curr, int sId, int size)
	{
		Pattern rTags = COORD_MAP.get(curr.getConstituentTag());
		
		if (rTags != null)
		{
			boolean b = false;
			int i;
			
			for (i=sId; i<size; i++)
			{
				if (curr.getChild(i).matchesConstituentTag(rTags))
				{
					b = true;
					break;
				}
			}
			
			if (!b)	rTags = Pattern.compile(".*");
		}
		else
			rTags = Pattern.compile(".*");
		
		return rTags;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private boolean isConjunct(CTNode C, CTNode P, Pattern rTags)
	{
		if (P.isConstituentTag(PTBTag.C_SBAR) && C.isConstituentTagAny(S_PREP_DET))
			return false;
		else if (rTags.pattern().equals(".*"))
			return getSpecialLabel(C) == null;
		else if (rTags.matcher(C.getConstituentTag()).find())
		{
			if (P.isConstituentTag(PTBTag.C_VP) && getAuxLabel(C) != null)
				return false;
			
			if (PTBLib.isMainClause(P) && C.isConstituentTag(PTBTag.C_S) && hasAdverbialTag(C))
				return false;
			
			return true;
		}
		else if (P.isConstituentTag(PTBTag.C_NP))
		{
			return C.hasFunctionTag(PTBTag.F_NOM);
		}
		
		return false;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private CTNode findHeadsCoordinationAux(HeadRule rule, CTNode curr, int bId, int eId, CTNode lastHead)
	{
		CTNode currHead = (eId - bId == 1) ? curr.getChild(bId) : getHead(rule, curr.getChildrenList(bId, eId), SIZE_HEAD_FLAGS);
		
		if (lastHead != null)
		{
			String label = DEPTagEn.DEP_CONJ;
			
			if (isIntj(currHead))						label = DEPTagEn.DEP_DISCOURSE;
			else if (PTBLib.isPunctuation(currHead.getConstituentTag()))	label = DEPTagEn.DEP_PUNCT;

			setHeadCoord(currHead, lastHead, label);
		}
		
		return currHead;
	}
	
	private void setHeadCoord(CTNode node, CTNode head, String label)
	{
		node.getC2DInfo().setHead(head, label, head.isTerminal());
	}
	
	boolean findHyphens(CTNode node)
	{
		int i, size = node.getChildrenSize();
		CTNode prev, hyph, next;
		boolean isFound = false;
		boolean isVP = node.isConstituentTag(PTBTag.C_VP);
		
		for (i=0; i<size-2; i++)
		{
			prev = node.getChild(i);
			hyph = node.getChild(i+1);
			next = node.getChild(i+2);
			
			if (hyph.isConstituentTag(PTBTag.P_HYPH))
			{
				if (isVP)
				{
					prev.getC2DInfo().setLabel(DEPTagEn.DEP_COMPOUND);
					hyph.getC2DInfo().setLabel(DEPTagEn.DEP_PUNCT);
					next.getC2DInfo().setLabel(DEPTagEn.DEP_COMPOUND);
				}
				else
				{
					prev.getC2DInfo().setHead(next, DEPTagEn.DEP_COMPOUND);
					hyph.getC2DInfo().setHead(next, DEPTagEn.DEP_PUNCT);
				}
				
				isFound = true;
				i++;
			}
		}
		
		return isFound;
	}
	
	
	/**
	 * Finds the head of appositional modifiers.
	 * @param curr the constituent node to be processed.
	 * @return {@code true} if the specific node contains appositional modifiers. 
	 */
	private boolean findHeadsApposition(CTNode curr)
	{
		if (!curr.isConstituentTagAny(S_NOUN_PHRASE) || curr.containsChild(PTBLib.M_NNx))
			return false;
		
		CTNode fst = curr.getFirstChild(PTBLib.M_NP_NML);
		while (fst != null && fst.containsChild(MT_POS))
			fst = fst.getRightNearestSibling(PTBLib.M_NP_NML);
		
		if (fst == null || fst.getC2DInfo().hasHead())	return false;

		boolean hasAppo = false;
		CTNode snd = fst;
		
		while ((snd = snd.getRightSibling()) != null)
		{
			if (snd.getC2DInfo().hasHead())	continue;
			
			if ((snd.isConstituentTagAny(S_NOUN_PHRASE) && !hasAdverbialTag(snd)) ||
				(snd.hasFunctionTagAny(PTBTag.F_HLN, PTBTag.F_TTL)) ||
				(snd.isConstituentTag(PTBTag.C_RRC) && snd.containsChild(MT_NP_PRD)))
			{
				snd.getC2DInfo().setHead(fst, DEPTagEn.DEP_APPOS);
				hasAppo = true;
			}
		}
		
		return hasAppo;
	}

	private boolean findHeadsSmallClause(CTNode node)
	{
		CTNode parent = node.getParent();
		
		if (node.isConstituentTag(PTBTag.C_S) && !node.containsChild(PTBLib.M_VP))
		{
			CTNode sbj = node.getFirstChild(PTBLib.M_SBJ);
			CTNode prd = node.getFirstChild(PTBLib.M_PRD);
			
			if (sbj != null && prd != null)
			{
				if (parent.isConstituentTag(PTBTag.C_SQ))
				{
					CTNode vb = parent.getFirstChild(PTBLib.M_VBx);
					
					if (vb != null)
					{
						sbj.getC2DInfo().setHead(vb, getDEPLabel(sbj, parent, vb));
						node.setConstituentTag(prd.getConstituentTag());
						node.addFunctionTag(PTBTag.F_PRD);
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected int getHeadFlag(CTNode child)
	{
		C2DInfo info = child.getC2DInfo();
		
		if (info.hasHead())// && info.getTerminalHead() != info.getNonTerminalHead())
			return -1;
		
		if (hasAdverbialTag(child))
			return 1;
		
		if (isMeta(child))
			return 2;
		
		if (child.isEmptyCategoryTerminal() || PTBLib.isPunctuation(child.getConstituentTag()))
			return 3;
		
		return 0;
	}
	
	// ============================= Get labels ============================= 
	
	@Override
	protected String getDEPLabel(CTNode C, CTNode P, CTNode p)
	{
		CTNode c = C.getC2DInfo().getNonTerminalHead();
		CTNode d = C.getC2DInfo().getTerminalHead();
		String label;
		
		if (hasAdverbialTag(C))
		{
			if (C.isConstituentTagAny(S_ADVCL))
				return DEPTagEn.DEP_ADVCL;
			
			if (C.isConstituentTagAny(S_NPADVMOD))
				return DEPTagEn.DEP_NPADVMOD;
		}
		
		// function tags
		if ((label = getSubjectLabel(C, d)) != null)
			return label;
		
		// coordination
		if (C.isConstituentTag(PTBTag.C_UCP))
		{
			c.addFunctionTags(C.getFunctionTagSet());
			return getDEPLabel(c, P, p);
		}
		
		// complements
		if (P.isConstituentTagAny(S_COMP_PARENT_S))
		{
			if (isAcomp(C))	return DEPTagEn.DEP_ACOMP;
			if ((label = getObjectLabel(C)) != null) return label;
			if (isOprd(C))	return DEPTagEn.DEP_OPRD;
			if (isXcomp(C))	return DEPTagEn.DEP_XCOMP;
			if (isCcomp(C))	return DEPTagEn.DEP_CCOMP;
			if ((label = getAuxLabel(C)) != null) return label;
		}
		
		if (P.isConstituentTagAny(S_COMP_PARENT_A))
		{
			if (isXcomp(C))	return DEPTagEn.DEP_XCOMP;
			if (isCcomp(C))	return DEPTagEn.DEP_CCOMP;
		}
		
		if (P.isConstituentTagAny(S_NFMOD))
		{
			if (isRcmod(C))	return DEPTagEn.DEP_RELCL;
			if (isNfmod(C) || isCcomp(C)) return DEPTagEn.DEP_ACL;
//			if (isNfmod(C))	return isInfMod(C) ? DEPTagEn.DEP_INFMOD : DEPTagEn.DEP_PARTMOD;
//			if (isCcomp(C))	return DEPTagEn.DEP_CCOMP;
		}
		
		if (isPoss(C, P))
			return DEPTagEn.DEP_POSS;
		
		// simple labels
		if ((label = getSimpleLabel(C)) != null)
			return label;
			
		// default
		if (P.isConstituentTagAny(S_PREP_PHRASE))
		{
			if (p.getParent() == C.getParent())	// p and C are siblings
			{
				if (p.isLeftSiblingOf(C))
					return getPmodLabel(C, d);
			}
			else								// UCP
			{
				if (p.getFirstTerminal().getTerminalID() < C.getFirstTerminal().getTerminalID())
					return getPmodLabel(C, d);
			}
		}
		
		if (C.isConstituentTag(PTBTag.C_SBAR) || isXcomp(C) || (P.isConstituentTag(PTBTag.C_PP) && PTBLib.isClause(C)))
			return DEPTagEn.DEP_ADVCL;
		
		if (C.isConstituentTagAny(S_CCOMP))
			return DEPTagEn.DEP_CCOMP;
		
		if (P.isConstituentTag(PTBTag.C_QP))
		{
//			if (C.isConstituentTag(CTLibEn.P_CD) && p.isConstituentTag(CTLibEn.P_CD))
//				return DEPTagEn.DEP_COMPOUND;
//			else
			return DEPTagEn.DEP_QMOD;
		}
		
		if (P.isConstituentTagAny(S_NMOD_PARENT) || PTBLib.isNoun(p.getConstituentTag()))
			return getNmodLabel(C, d);
		
		if (c != null)
		{
			if ((label = getSimpleLabel(c)) != null)
				return label;
			
			if (d.isConstituentTag(PTBTag.P_IN))
				return DEPTagEn.DEP_PREP;
			
			if (PTBLib.isAdverb(d.getConstituentTag()))
				return DEPTagEn.DEP_ADVMOD;
		}
		
		if ((P.isConstituentTagAny(S_ADVB_PHRASE) || PTBLib.isAdjective(p.getConstituentTag()) || PTBLib.isAdverb(p.getConstituentTag())))
		{
			if (C.isConstituentTagAny(S_NPADVMOD) || PTBLib.isNoun(C.getConstituentTag()))
				return DEPTagEn.DEP_NPADVMOD;
			
			return DEPTagEn.DEP_ADVMOD;
		}
		
		if (d.hasC2DInfo() && (label = d.getC2DInfo().getLabel()) != null)
			return label;
		
		return DEPTagEn.DEP_DEP;
	}
	
	private boolean hasAdverbialTag(CTNode node)
	{
		return node.hasFunctionTag(PTBTag.F_ADV) || DSUtils.hasIntersection(node.getFunctionTagSet(), SEM_TAGS);
	}
	
	private String getObjectLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_NOUN_PHRASE))
		{
			if (node.hasFunctionTag(PTBTag.F_PRD))
				return DEPTagEn.DEP_ATTR;
			else
				return DEPTagEn.DEP_DOBJ;
		}
		
		return null;
	}
	
	private String getSubjectLabel(CTNode C, CTNode d)
	{
		if (C.hasFunctionTag(PTBTag.F_SBJ))
		{
			if (PTBLib.isClause(C))
				return DEPTagEn.DEP_CSUBJ;
			else if (d.isConstituentTag(PTBTag.P_EX) || d.isWordFormIgnoreCase("there"))
				return DEPTagEn.DEP_EXPL;
			else
				return DEPTagEn.DEP_NSUBJ;
		}
		else if (C.hasFunctionTag(PTBTag.F_LGS))
			return DEPTagEn.DEP_AGENT;
		
		return null;
	}
	
	private String getSimpleLabel(CTNode C)
	{
		String label;
		
		if (isHyph(C))
			return DEPTagEn.DEP_PUNCT;
		
//		if (isAmod(C))
//			return DEPTagEn.DEP_AMOD;
		
		if (C.isConstituentTagAny(S_PREP_PHRASE))
			return DEPTagEn.DEP_PREP;
		
		if (PTBLib.isCorrelativeConjunction(C))
			return DEPTagEn.DEP_PRECONJ;
		
		if (PTBLib.isConjunction(C))
			return DEPTagEn.DEP_CC;
		
		if (isPrt(C))
			return DEPTagEn.DEP_PRT;

		if ((label = getSpecialLabel(C)) != null)
			return label;
		
		return null;
	}
	
	private String getSpecialLabel(CTNode C)
	{
		CTNode d = C.getC2DInfo().getTerminalHead();
		
		if (PTBLib.isPunctuation(C.getConstituentTag()) || PTBLib.isPunctuation(d.getConstituentTag()))
			return DEPTagEn.DEP_PUNCT;
		
		if (isIntj(C) || isIntj(d))
			return DEPTagEn.DEP_DISCOURSE;
		
		if (isMeta(C))
			return DEPTagEn.DEP_META;
		
		if (isPrn(C))
			return DEPTagEn.DEP_PARATAXIS;

		if (isAdv(C))
			return DEPTagEn.DEP_ADVMOD;
		
		return null;
	}
	
	private String getAuxLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_AUX))
			return DEPTagEn.DEP_AUX;

		CTNode vp;
		
		if (PTBLib.isVerb(node.getConstituentTag()) && (vp = node.getRightNearestSibling(PTBLib.M_VP)) != null)
		{
			if (ENUtils.isPassiveAuxiliaryVerb(node.getWordForm()))
			{
				if (vp.containsChild(PTBLib.M_VBD_VBN))
					return DEPTagEn.DEP_AUXPASS;
				
				if (!vp.containsChild(PTBLib.M_VBx) && (vp = vp.getFirstChild(PTBLib.M_VP)) != null && vp.containsChild(PTBLib.M_VBD_VBN))
					return DEPTagEn.DEP_AUXPASS;
			}
			
			return DEPTagEn.DEP_AUX;
		}
		
		return null;
	}
	
	private String getNmodLabel(CTNode C, CTNode d)
	{
//		if (C.isConstituentTagAny(S_PARTICIPIAL))
//			return DEPTagEn.DEP_AMOD;
		
		if (C.isConstituentTagAny(S_DET))
			return DEPTagEn.DEP_DET;
		
//		if (C.isConstituentTagAny(S_NN) || (C.matches(CTLibEn.M_NNx) || C.isConstituentTag(CTLibEn.P_FW)))
//			return DEPTagEn.DEP_COMPOUND;
		
//		if (C.isConstituentTagAny(S_NUM) || d.isConstituentTag(CTLibEn.P_CD))
//			return DEPTagEn.DEP_NUMMOD;

		if (C.isConstituentTag(PTBTag.P_POS))
			return DEPTagEn.DEP_CASE;
		
		if (C.isConstituentTag(PTBTag.P_PDT))
			return DEPTagEn.DEP_PREDET;
		
		return DEPTagEn.DEP_NMOD;
	}
	
	private String getPmodLabel(CTNode C, CTNode d)
	{
		if (C.isConstituentTagAny(S_NOUN_PHRASE) || PTBLib.isRelativizer(d.getConstituentTag()))
			return DEPTagEn.DEP_POBJ;
		else
			return DEPTagEn.DEP_PCOMP;	
	}
	
	private boolean isHyph(CTNode node)
	{
		return node.isConstituentTag(PTBTag.P_HYPH);
	}
	
//	private boolean isAmod(CTNode node)
//	{
//		return node.isConstituentTagAny(S_ADJT_PHRASE) || CTLibEn.isAdjective(node);
//	}
	
	private boolean isAdv(CTNode C)
	{
		if (C.isConstituentTag(PTBTag.C_ADVP) || PTBLib.isAdverb(C.getConstituentTag()))
		{
			CTNode P = C.getParent();
			
			if (P.isConstituentTagAny(S_PREP_PHRASE) && C.getRightSibling() == null && C.getLeftSibling().isConstituentTagAny(S_PREPOSITION))
				return false;

			return true;
		}
		
		return false;
	}
	
	private boolean isIntj(CTNode node)
	{
		return node.isConstituentTagAny(S_INTJ);
	}
	
	private boolean isMeta(CTNode node)
	{
		return node.isConstituentTagAny(S_META);
	}
	
	private boolean isPrn(CTNode node)
	{
		return node.isConstituentTag(PTBTag.C_PRN);
	}
	
	private boolean isPrt(CTNode curr)
	{
		return curr.isConstituentTagAny(S_PRT);
	}
	
	private boolean isAcomp(CTNode node)
	{
		return node.isConstituentTag(PTBTag.C_ADJP);
	}
	
	private boolean isOprd(CTNode curr)
	{
		if (curr.hasFunctionTag(DEPTagEn.DEP_OPRD))
			return true;
		
		if (curr.isConstituentTag(PTBTag.C_S) && !curr.containsChild(PTBLib.M_VP) && curr.containsChild(PTBLib.M_PRD))
		{
			CTNode sbj = curr.getFirstChild(PTBLib.M_SBJ);
			return sbj != null && sbj.isEmptyCategoryTerminal();
		}
		
		return false;
	}
	
	private boolean isPoss(CTNode curr, CTNode parent)
	{
		if (curr.isConstituentTagAny(S_POSS))
			return true;
		
		if (parent.isConstituentTagAny(S_POSS_PARENT))
			return curr.containsChild(MT_POS);
		
		return false;
	}
	
	private boolean isXcomp(CTNode node)
	{
		if (node.isConstituentTag(PTBTag.C_S))
		{
			CTNode sbj = node.getFirstChild(PTBLib.M_SBJ);
			
			if (node.containsChild(PTBLib.M_VP) && (sbj == null || sbj.isEmptyCategoryTerminal()))
				return true;
		}
		else if (node.hasFunctionTag(DEPTagEn.DEP_RELCL))
		{
			CTNode s = node.getFirstChild(MT_S);
			if (s != null)	return isXcomp(s);
		}

		return false;
	}
	
	private boolean isCcomp(CTNode node)
	{
		if (node.isConstituentTagAny(S_CCOMP))
			return true;
		
		if (node.isConstituentTag(PTBTag.C_SBAR))
		{
			CTNode comp;
			
			if ((comp = node.getFirstChild(MT_NONE)) != null && comp.isWordForm(PTBTag.E_ZERO))
				return true;
			
			if ((comp = node.getFirstChild(MT_IN_DT)) != null)
			{
				if (isComplm(comp))
				{
//					comp.getC2DInfo().setLabel(DEPTagEn.DEP_COMPLM);
					comp.getC2DInfo().setLabel(DEPTagEn.DEP_MARK);
					return true;
				}
			}
			
			if (node.hasFunctionTag(DEPTagEn.DEP_RELCL) || node.containsChild(PTBLib.M_WHx))
				return true;
		}
		
		return false;
	}
	
	private boolean isNfmod(CTNode curr)
	{
		return isXcomp(curr) || curr.isConstituentTag(PTBTag.C_VP);
	}
	
	protected boolean isInfMod(CTNode curr)
	{
		CTNode vp = curr.isConstituentTag(PTBTag.C_VP) ? curr : curr.getFirstDescendant(PTBLib.M_VP);
		
		if (vp != null)
		{
			CTNode vc = vp.getFirstChild(PTBLib.M_VP);
			
			while (vc != null)
			{
				vp = vc;
				
				if (vp.getLeftNearestSibling(MT_TO) != null)
					return true;
				
				vc = vp.getFirstChild(PTBLib.M_VP);
			}
			
			return vp.containsChild(MT_TO);
		}
		
		return false;
	}
	
	private boolean isRcmod(CTNode curr)
	{
		return curr.isConstituentTag(PTBTag.C_RRC) || curr.hasFunctionTag(DEPTagEn.DEP_RELCL) || (curr.isConstituentTag(PTBTag.C_SBAR) && curr.containsChild(PTBLib.M_WHx));
	}
	
	private boolean isComplm(CTNode curr)
	{
		return S_COMPLM.contains(StringUtils.toLowerCase(curr.getWordForm()));
	}
	
	// ============================= Get a dependency tree =============================
	
	private NLPNode[] getDEPTree(CTTree cTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
	{
		NLPNode[] dTree = initDEPTree(cTree);
		addDEPHeads(dTree, cTree);
		
		if (NLPUtils.containsCycle(dTree))
			throw new UnknownFormatConversionException("Cyclic depedency relation.");

		DEPLibEn.enrichLabels(dTree);
		addSecondaryHeads(dTree, xsubj, rnr);
		addFeats(dTree, cTree, cTree.getRoot());
		
		if (cTree.hasPropBank())
			addSemanticHeads(dTree, cTree);
		
		if (cTree.hasNamedEntity())
			addNamedEntities(dTree, cTree);
		
		return getDEPTreeWithoutEdited(cTree, dTree);
	}

	public void addNamedEntities(NLPNode[] dTree, CTTree cTree)
	{
		for (CTNode node : cTree.getTokenList())
			dTree[node.getTokenID()+1].setNamedEntityTag(node.getNamedEntityTag());
	}
	
	/** Adds dependency heads. */
	private int addDEPHeads(NLPNode[] dTree, CTTree cTree)
	{
		int currId, headId, size = dTree.length, rootCount = 0;
		CTNode cNode, ante;
		NLPNode dNode;
		String label;
		
		for (currId=1; currId<size; currId++)
		{
			dNode  = dTree[currId];
			cNode  = cTree.getToken(currId-1);
			headId = cNode.getC2DInfo().getTerminalHead().getTokenID() + 1;
			
			if (currId == headId)	// root
			{
				dNode.setDependencyHead(dTree[0], DEPTagEn.DEP_ROOT);
				rootCount++;
			}
			else
			{
				label = cNode.getC2DInfo().getLabel();
				
				if (cNode.isConstituentTagAny(S_MARK) && cNode.getParent().isConstituentTag(PTBTag.C_SBAR))// && !label.equals(DEPTagEn.DEP_COMPLM))
					label = DEPTagEn.DEP_MARK;
				
				dNode.setDependencyHead(dTree[headId], label);
			}
			
			if ((ante = cNode.getAntecedent()) != null)
				dNode.addSecondaryHead(getNLPNode(dTree, ante), DEPTagEn.DEP2_REF);
		}
		
		return rootCount;
//		if (rootCount > 1)	System.err.println("Warning: multiple roots exist");
	}
	
	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addSecondaryHeads(NLPNode[] dTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
	{
		for (CTNode curr : xsubj.keySet())
		{
			if (curr.hasC2DInfo())
				addSecondaryHeadsAux(dTree, curr, xsubj.get(curr), DEPTagEn.DEP2_XSUBJ);
		}
		
		for (CTNode curr : rnr.keySet())
		{
			if (curr.getParent() == null)
				continue;
			
			if (curr.getParent().getC2DInfo().getNonTerminalHead() != curr)
				addSecondaryHeadsAux(dTree, curr, rnr.get(curr), DEPTagEn.DEP2_RNR);
			else
				addSecondaryChildren(dTree, curr, rnr.get(curr), DEPTagEn.DEP2_RNR);
		}
	}
	
	/** Called by {@link #addSecondaryHeads(DEPTree)}. */
	private void addSecondaryHeadsAux(NLPNode[] dTree, CTNode cNode, Deque<CTNode> dq, String label)
	{
		if (cNode.isEmptyCategoryTerminal()) return;
		NLPNode node = getNLPNode(dTree, cNode);
		NLPNode head;
		
		for (CTNode cHead : dq)
		{
			head = getNLPNode(dTree, cHead);
			
			if (head == null)
			{
				System.err.println("HEAD NOT EXIST: AUX");
				continue;
			}
			
			if (!node.isDependentOf(head)) node.addSecondaryHead(head, label);
			
			if (label.equals(DEPTagEn.DEP2_XSUBJ) && head.isDependencyLabel(DEPTagEn.DEP_CCOMP))
				head.setDependencyLabel(DEPTagEn.DEP_XCOMP);
		}
	}
	
	/** Called by {@link #addSecondaryHeads(DEPTree)}. */
	private void addSecondaryChildren(NLPNode[] dTree, CTNode cHead, Deque<CTNode> dq, String label)
	{
		NLPNode head = getNLPNode(dTree, cHead);
		NLPNode node;
		
		for (CTNode cNode : dq)
		{
			node = getNLPNode(dTree, cNode);
			
			if (node == null || node.getID() == 0)
			{
				System.err.println("HEAD NOT EXIST: CHILDREN");
				continue;
			}
			
			node.addSecondaryHead(head, label);			
		}
	}
	
	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addFeats(NLPNode[] dTree, CTTree cTree, CTNode cNode)
	{
		CTNode ante;
		String feat;
		
		if (!cNode.isEmptyCategoryTerminal() && cNode.getGappingRelationIndex() != -1 && cNode.getParent().getGappingRelationIndex() == -1 && (ante = cTree.getAntecedent(cNode.getGappingRelationIndex())) != null)
		{
			NLPNode dNode = getNLPNode(dTree, cNode);
			dNode.addSecondaryHead(getNLPNode(dTree, ante), DEPTagEn.DEP2_GAP);
		}
		
		if ((feat = getFunctionTags(cNode, SEM_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);
		
		if ((feat = getFunctionTags(cNode, SYN_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SYN, feat);

		for (CTNode child : cNode.getChildrenList())
			addFeats(dTree, cTree, child);
	}
	
	/** Called by {@link #addFeats(DEPTree, CTTree, CTNode)}. */
	private String getFunctionTags(CTNode node, Set<String> sTags)
	{
		List<String> tags = new ArrayList<>();
		
		for (String tag : node.getFunctionTagSet())
		{
			if (sTags.contains(tag))
				tags.add(tag);
		}
		
		if (tags.isEmpty())	return null;
		Collections.sort(tags);
		return Joiner.join(tags, FeatMap.DELIM_VALUES);
	}
	
	private NLPNode getNLPNode(NLPNode[] dTree, CTNode cNode)
	{
		if (cNode.isConstituentTag(CTTag.TOP)) return null;
		CTNode cHead = cNode.isTerminal() ? cNode : cNode.getC2DInfo().getTerminalHead();
		return cHead.isEmptyCategory() ? null : dTree[cHead.getTokenID()+1];
//		return cNode.isTerminal() ? dTree.get(cNode.getTokenID()+1) : dTree.get(cNode.getC2DInfo().getTerminalHead().getTokenID()+1);
	}
	
// ============================= Edited phrases =============================
	
	public NLPNode[] getDEPTreeWithoutEdited(CTTree cTree, NLPNode[] dTree)
	{
		List<NLPNode> nodes = new ArrayList<>();
		Set<Integer> set = new HashSet<>();
		int id = 1;
			
		addEditedTokensAux(cTree.getRoot(), set);
			
		for (NLPNode node : dTree)
		{
			if (!set.contains(node.getID()))
			{
				removeEditedHeads(node.getSecondaryHeadList(), set);
				removeEditedHeads(node.getSemanticHeadList() , set);
				node.setID(id++);
				nodes.add(node);
			}
		}
		
		return (nodes.size() > 0) ? NLPUtils.toDependencyTree(nodes, NLPNode::new) : null;
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private void addEditedTokensAux(CTNode curr, Set<Integer> set)
	{
		for (CTNode child : curr.getChildrenList())
		{
			if (PTBLib.isEditedPhrase(child))
			{
				for (CTNode sub : child.getTokenList())
					set.add(sub.getTokenID()+1);
			}
			else if (!child.isTerminal())
				addEditedTokensAux(child, set);
		}
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private <T extends AbstractArc<NLPNode>>void removeEditedHeads(List<T> heads, Set<Integer> set)
	{
		if (heads == null) return;
		List<T> remove = new ArrayList<>();
		
		for (T arc : heads)
		{
			if (arc.getNode() == null || set.contains(arc.getNode().getID()))
				remove.add(arc);
		}
		
		heads.removeAll(remove);
	}	
	
	// ============================= Add PropBank arguments =============================
	
	private void addSemanticHeads(NLPNode[] dTree, CTTree cTree)
	{
		initPropBank(dTree, cTree.getRoot());
		arrangePropBank(dTree);
		relabelNumberedArguments(dTree);
	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void initPropBank(NLPNode[] dTree, CTNode cNode)
	{
		NLPNode dNode = getNLPNode(dTree, cNode);
		
		if (dNode != null)
		{
			if (cNode.isPBHead())
				dNode.putFeat(NLPUtils.FEAT_PREDICATE, cNode.getPBRolesetID());
			
			NLPNode sHead, d;
			String  label;
			CTNode  c;
			
			for (PBArc p : cNode.getPBHeads())
			{
				sHead = getNLPNode(dTree, p.getNode());
				label = PBLib.getShortLabel(p.getLabel());
				
				if ((c = getReferentArgument(cNode)) != null)
				{
					if ((c = PTBLib.getRelativizer(c)) != null && (c = c.getAntecedent()) != null)
					{
						d = getNLPNode(dTree, c);
						
						if (d != null && d.getSemanticHeadArc(sHead) == null)
							d.addSemanticHead(new DEPArc<>(sHead, label));
					}
					
					label = PBLib.PREFIX_REFERENT + label;
				}
				
				if (!dNode.isArgumentOf(sHead) && dNode != sHead)
					dNode.addSemanticHead(sHead, label);
			}	
		}
		
		for (CTNode child : cNode.getChildrenList())
			initPropBank(dTree, child);
	}
	
	/** Called by {@link #initPropBank(DEPTree, CTNode)}. */
	private CTNode getReferentArgument(CTNode node)
	{
		CTNode ref;
		
		if ((ref = PTBLib.getWhPhrase(node)) != null)
			return ref;
		
		if (node.isConstituentTag(PTBTag.C_PP))
		{
			for (CTNode child : node.getChildrenList()) 
			{
				if ((ref = PTBLib.getWhPhrase(child)) != null)
					return ref;
			}
		}

		return null;
	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void arrangePropBank(NLPNode[] tree)
	{
		List<DEPArc<NLPNode>> remove;
		NLPNode head;
		String label;
		
		for (NLPNode node : tree)
		{
			remove = new ArrayList<>();
			
			for (DEPArc<NLPNode> arc : node.getSemanticHeadList())
			{
				head  = arc.getNode();
				label = arc.getLabel();
				
				if (ancestorHasSemanticHead(node, head, label))
					remove.add(arc);
			//	else if (rnrHasSHead(node, head, label))
			//		remove.add(arc);
			}
			
			node.removeSemanticHeads(remove);
		}
	}
	
	/** Called by {@link #arrangePropBank(DEPTree)}. */
	private boolean ancestorHasSemanticHead(NLPNode dNode, NLPNode sHead, String label)
	{
		NLPNode dHead = dNode.getDependencyHead();
		
		while (dHead.getID() != 0)
		{
			if (dHead.isArgumentOf(sHead, label))
				return true;
			
			dHead = dHead.getDependencyHead();
		}
		
		return false;
	}
	
//	private boolean rnrHasSHead(NLPNode dNode, NLPNode sHead, String label)
//	{
//		for (DEPArc rnr : dNode.getSecondaryHeadList(DEPTagEn.DEP2_RNR))
//		{
//			if (rnr.getNode().isArgumentOf(sHead, label))
//				return true;
//		}
//		
//		return false;
//	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void relabelNumberedArguments(NLPNode[] tree)
	{
		Map<String,NLPNode> map = new HashMap<>();
		String key;
		
		for (NLPNode node : tree)
		{
			for (DEPArc<NLPNode> arc : node.getSemanticHeadList())
			{
				if (PBLib.isReferentArgument(arc.getLabel()))
					continue;
								
				if (PBLib.isModifier(arc.getLabel()))
					continue;
				
				key = arc.toString();
				
				if (map.containsKey(key))
					arc.setLabel(PBLib.PREFIX_CONCATENATION + arc.getLabel());
				else
					map.put(key, node);
			}
		}
	}
	
	private void finalize(NLPNode[] tree)
	{
		finalizeLabels(tree);
		finalizeCompound(tree, PTBTag.P_NN, DEPTagEn.DEP_NMOD , n -> n.getPartOfSpeechTag().startsWith(PTBTag.P_NNP) || n.isDependencyLabel(DEPTagEn.DEP_NMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
		finalizeCompound(tree, PTBTag.P_CD, DEPTagEn.DEP_QMOD, n -> n.isDependencyLabel(DEPTagEn.DEP_QMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
	}
	
	private void finalizeLabels(NLPNode[] tree)
	{
		for (NLPNode node : tree)
		{
			if (isDative(node))
				node.setDependencyLabel(DEPTagEn.DEP_DATIVE);
			else if (isEmoticon(node))
				node.setDependencyLabel(DEPTagEn.DEP_DISCOURSE);
			else if (isVocative(node))
				node.setDependencyLabel(DEPTagEn.DEP_VOCATIVE);
		}
	}
	
	private boolean isDative(NLPNode node)
	{
		if (!PTBLib.isVerb(node.getDependencyHead().getPartOfSpeechTag())) return false;
//		if (node.isDependencyLabel(DEPTagEn.DEP_IOBJ)) return true;
		String feat;
		
		if ((feat = node.getFeat(NLPUtils.FEAT_SYN)) != null && DSUtils.toHashSet(Splitter.splitCommas(feat)).contains(PTBTag.F_DTV)) return true;
		if (PTBTag.F_BNF.equals(node.getFeat(NLPUtils.FEAT_SEM))) return true;
		
		return false;
	}
	
	private boolean isEmoticon(NLPNode node)
	{
		String s = node.getWordForm();
		int[] idx = emoticon.getEmoticonRange(s);
		return idx != null && idx[0] == 0 && idx[1] == s.length();
	}
	
	private boolean isVocative(NLPNode node)
	{
		String feat;
		return (feat = node.getFeat(NLPUtils.FEAT_SEM)) != null && feat.equals(PTBLib.F_VOC);
	}
	
	private void finalizeCompound(NLPNode[] tree, String pos, String label, Predicate<NLPNode> p)
	{
		NLPNode node, head;
		int i, j;
		
		for (i=tree.length-1; i>0; i--)
		{
			head = tree[i];
			
			if (head.getPartOfSpeechTag().startsWith(pos) && !head.isDependencyLabel(label))
			{
				for (j=i-1; j>0; j--)
				{
					node = tree[j];
					
					if (node.getPartOfSpeechTag().startsWith(pos) && node.isDescendantOf(head) && node.getDependencyHead().getID() > node.getID() && p.test(node))
					{
						node.setDependencyLabel(DEPTagEn.DEP_COMPOUND);
						i = j;
					}
					else if (node.isPartOfSpeechTag(PTBTag.P_HYPH))
						continue;
					else
						break;
				}
			}
		}
	}
}