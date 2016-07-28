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
import edu.emory.mathcs.nlp.common.constituent.CTLibEn;
import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTTagEn;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.propbank.PBLib;
import edu.emory.mathcs.nlp.common.treebank.DEPLibEn;
import edu.emory.mathcs.nlp.common.treebank.DEPTagEn;
import edu.emory.mathcs.nlp.common.treebank.PBArc;
import edu.emory.mathcs.nlp.common.treebank.POSLibEn;
import edu.emory.mathcs.nlp.common.treebank.POSTagEn;
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
import edu.emory.mathcs.nlp.conversion.headrule.HeadRule;
import edu.emory.mathcs.nlp.conversion.headrule.HeadRuleMap;


/**
 * Constituent to dependency converter for English.
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishC2DConverter extends C2DConverter
{
	
	private final Set<String> S_NPADVMOD	= DSUtils.toHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_QP);
	private final Set<String> S_ADVCL		= DSUtils.toHashSet(CTTagEn.C_S, CTTagEn.C_SBAR, CTTagEn.C_SINV);
	private final Set<String> S_NFMOD		= DSUtils.toHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_WHNP);
	private final Set<String> S_CCOMP		= DSUtils.toHashSet(CTTagEn.C_S, CTTagEn.C_SQ, CTTagEn.C_SINV, CTTagEn.C_SBARQ);
	private final Set<String> S_META		= DSUtils.toHashSet(CTTagEn.C_EDITED, CTTagEn.C_EMBED, CTTagEn.C_LST, CTTagEn.C_META, CTLibEn.POS_CODE, CTTagEn.C_CAPTION, CTTagEn.C_CIT, CTTagEn.C_HEADING, CTTagEn.C_TITLE);
	private final Set<String> S_MARK		= DSUtils.toHashSet(CTLibEn.POS_IN, CTLibEn.POS_TO, CTLibEn.POS_DT);
	private final Set<String> S_POSS		= DSUtils.toHashSet(CTLibEn.POS_PRPS, CTLibEn.POS_WPS);
	private final Set<String> S_INTJ		= DSUtils.toHashSet(CTTagEn.C_INTJ, CTLibEn.POS_UH);
	private final Set<String> S_PRT 		= DSUtils.toHashSet(CTTagEn.C_PRT, CTLibEn.POS_RP);
//	private final Set<String> S_NUM			= DSUtils.toHashSet(CTLibEn.POS_CD, CTTagEn.C_QP);
	private final Set<String> S_DET			= DSUtils.toHashSet(CTLibEn.POS_DT, CTLibEn.POS_WDT, CTLibEn.POS_WP);
	private final Set<String> S_AUX			= DSUtils.toHashSet(CTLibEn.POS_MD, CTLibEn.POS_TO);
//	private final Set<String> S_NN			= DSUtils.toHashSet(CTTagEn.C_NML, CTTagEn.C_NP);

//	private final Set<String> S_ADJT_PHRASE	= DSUtils.toHashSet(CTTagEn.C_ADJP, CTTagEn.C_WHADJP);
	private final Set<String> S_NOUN_PHRASE	= DSUtils.toHashSet(CTTagEn.C_NP, CTTagEn.C_NML);
	private final Set<String> S_PREP_PHRASE	= DSUtils.toHashSet(CTTagEn.C_PP, CTTagEn.C_WHPP);
	private final Set<String> S_ADVB_PHRASE	= DSUtils.toHashSet(CTTagEn.C_ADJP, CTTagEn.C_ADVP, CTTagEn.C_PP);
	private final Set<String> S_PREPOSITION	= DSUtils.toHashSet(CTLibEn.POS_IN, CTLibEn.POS_TO);
//	private final Set<String> S_PARTICIPIAL	= DSUtils.toHashSet(CTLibEn.POS_VBG, CTLibEn.POS_VBN);
	private final Set<String> S_PREP_DET	= DSUtils.toHashSet(CTLibEn.POS_IN, CTLibEn.POS_DT);
	
	private final Set<String> S_COMP_PARENT_S = DSUtils.toHashSet(CTTagEn.C_VP, CTTagEn.C_SINV, CTTagEn.C_SQ);
	private final Set<String> S_COMP_PARENT_A = DSUtils.toHashSet(CTTagEn.C_ADJP, CTTagEn.C_ADVP);
	private final Set<String> S_NMOD_PARENT	  = DSUtils.toHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_NX, CTTagEn.C_WHNP);
	private final Set<String> S_POSS_PARENT	  = DSUtils.toHashSet(CTTagEn.C_NP, CTTagEn.C_NML, CTTagEn.C_WHNP, CTTagEn.C_QP, CTTagEn.C_ADJP);
	
	private final Set<String> S_COMPLM = DSUtils.toHashSet("that", "if", "whether");
	private final int SIZE_HEAD_FLAGS = 4;
	
	private Set<String> s_semTags;
	private Set<String> s_synTags;
	
	private Map<CTNode,Deque<CTNode>> m_rnr;
	private Map<CTNode,Deque<CTNode>> m_xsubj;
	private Map<String,Pattern>       m_coord;
	
	private Predicate<CTNode> mt_s;
	private Predicate<CTNode> mt_to;
	private Predicate<CTNode> mt_pos;
	private Predicate<CTNode> mt_sbj;
	private Predicate<CTNode> mt_prd;
	private Predicate<CTNode> mt_none;
	private Predicate<CTNode> mt_in_dt;
	private Predicate<CTNode> mt_np_prd;
	
	private Emoticon emoticon;
	
	public EnglishC2DConverter(HeadRuleMap headrules)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));
		
		initBasic();
		initCoord();
		initMatchers();
		emoticon = new Emoticon();
	}
	
	@Override
	public NLPNode[] toDEPTree(CTTree cTree)
	{
		NLPNode[] tree = null;
		
		try
		{
			CTLibEn.preprocess(cTree);
			clearMaps();
			if (!mapEmtpyCategories(cTree))	return null;
			setHeads(cTree.getRoot());
			tree = getDEPTree(cTree);	
		}
		catch (Exception e) {e.printStackTrace();}
		
		if (tree != null) finalize(tree);
		return tree;
	}
	
// ============================= Initialization ============================= 
	
	private void initBasic()
	{
		s_semTags = DSUtils.toHashSet(CTTagEn.F_BNF, CTTagEn.F_DIR, CTTagEn.F_EXT, CTTagEn.F_LOC, CTTagEn.F_MNR, CTTagEn.F_PRP, CTTagEn.F_TMP, CTTagEn.F_VOC);
		s_synTags = DSUtils.toHashSet(CTTagEn.F_ADV, CTTagEn.F_CLF, CTTagEn.F_CLR, CTTagEn.F_DTV, CTTagEn.F_NOM, CTTagEn.F_PUT, CTTagEn.F_PRD, CTTagEn.F_TPC);
		m_rnr     = new HashMap<>();
		m_xsubj   = new HashMap<>();
	}
	
	private void initCoord()
	{
		m_coord = new HashMap<>();
		
		m_coord.put(CTTagEn.C_ADJP	, PatternUtils.createClosedORPattern("ADJP","JJ.*","VBN","VBG"));
		m_coord.put(CTTagEn.C_ADVP	, PatternUtils.createClosedORPattern("ADVP","RB.*"));
		m_coord.put(CTTagEn.C_INTJ	, PatternUtils.createClosedORPattern("INTJ","UH"));
		m_coord.put(CTTagEn.C_PP  	, PatternUtils.createClosedORPattern("PP","IN","VBG"));
		m_coord.put(CTTagEn.C_PRT 	, PatternUtils.createClosedORPattern("PRT","RP"));
		m_coord.put(CTTagEn.C_NAC 	, PatternUtils.createClosedORPattern("NP"));
		m_coord.put(CTTagEn.C_NML 	, PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		m_coord.put(CTTagEn.C_NP  	, PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		m_coord.put(CTTagEn.C_NX  	, PatternUtils.createClosedORPattern("NX"));
		m_coord.put(CTTagEn.C_VP  	, PatternUtils.createClosedORPattern("VP","VB.*"));
		m_coord.put(CTTagEn.C_S   	, PatternUtils.createClosedORPattern("S","SINV","SQ","SBARQ"));
		m_coord.put(CTTagEn.C_SBAR	, PatternUtils.createClosedORPattern("SBAR.*"));
		m_coord.put(CTTagEn.C_SBARQ	, PatternUtils.createClosedORPattern("SBAR.*"));
		m_coord.put(CTTagEn.C_SINV	, PatternUtils.createClosedORPattern("S","SINV"));
		m_coord.put(CTTagEn.C_SQ	, PatternUtils.createClosedORPattern("S","SQ","SBARQ"));
		m_coord.put(CTTagEn.C_WHNP	, PatternUtils.createClosedORPattern("NN.*","WP"));
		m_coord.put(CTTagEn.C_WHADJP, PatternUtils.createClosedORPattern("JJ.*","VBN","VBG"));
		m_coord.put(CTTagEn.C_WHADVP, PatternUtils.createClosedORPattern("RB.*","WRB","IN"));
	}
	
	private void initMatchers()
	{
		mt_s		= CTLib.matchC(CTTagEn.C_S);
		mt_to		= CTLib.matchC(POSTagEn.POS_TO);
		mt_pos		= CTLib.matchC(POSTagEn.POS_POS);
		mt_none		= CTLib.matchC(CTLibEn.NONE);
		
		mt_sbj  	= CTLib.matchF(CTTagEn.F_SBJ);
		mt_prd  	= CTLib.matchF(CTTagEn.F_PRD);
		mt_np_prd	= CTLib.matchCF(CTTagEn.C_NP, CTTagEn.F_PRD);
		mt_in_dt	= CTLib.matchCo(DSUtils.toHashSet(POSTagEn.POS_IN, POSTagEn.POS_DT));
	}

	private void clearMaps()
	{
		m_rnr.clear();
		m_xsubj.clear();
	}
	
// ============================= Empty categories ============================= 
	
	/**
	 * Removes, relocates empty categories in the specific tree. 
	 * @param cTree the constituent tree to be processed.
	 * @return {@true} if the constituent tree contains nodes after relocating empty categories.
	 */
	private boolean mapEmtpyCategories(CTTree cTree)
	{
		for (CTNode node : cTree.getTerminalList())
		{
			if (!node.isEmptyCategory())	continue;
			if (node.getParent() == null)	continue;
			
			if      (node.wordFormStartsWith(CTTagEn.E_PRO))
				mapPRO(cTree, node);
			else if (node.wordFormStartsWith(CTTagEn.E_TRACE))
				mapTrace(cTree, node);
			else if (node.matchesWordForm(CTLibEn.P_PASSIVE_NULL))
				mapPassiveNull(cTree, node);
			else if (node.isWordForm(CTTagEn.E_ZERO))
				continue;
			else if (CTLibEn.isDiscontinuousConstituent(node))
				mapDiscontinuousConstituent(cTree, node);
//			else if (node.wordFormStartsWith(CTTagEn.E_EXP))
//				reloateEXP(cTree, node);
			else
				removeCTNode(node);
		}
		
		return cTree.getRoot().getChildrenSize() > 0;
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPRO(CTTree cTree, CTNode ec)
	{
		CTNode np = ec.getParent();
		CTNode vp = np.getParent().getFirstLowestChainedDescendant(CTLibEn.M_VP);
		
		if (vp == null)		// small clauses
			relocatePRD(np, ec);
		else
		{
			CTNode ante;
			
			if ((ante = ec.getAntecedent()) != null && CTLibEn.isWhPhrase(ante))	// relative clauses
			{
				if (cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex()).size() == 1)
					mapTrace(cTree, ec);
			}
			
			addXSubject(ec, m_xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapTrace(CTTree cTree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			removeCTNode(ec);
		else if (ante.hasFunctionTag(CTTagEn.F_TPC))
		{
			if (!ante.hasFunctionTag(CTTagEn.F_SBJ))
			{
				CTNode parent = ec.getParent();
				parent.removeChild(ec);
				replaceEC(parent, ante);
			}
			else
				removeCTNode(ec);
		}
		else	// relative clauses
		{
			CTNode parent = ante.getHighestChainedAncestor(CTLibEn.M_SBAR);
			if (parent != null) parent.addFunctionTag(DEPTagEn.DEP_RELCL);
			replaceEC(ec, ante);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPassiveNull(CTTree cTree, CTNode ec)
	{
		CTNode np = ec.getParent();
		
		if (np.hasFunctionTag(CTTagEn.F_SBJ))
		{
			// small clauses
			if (np.getRightNearestSibling(CTLibEn.M_VP) == null)
				relocatePRD(np, ec);
			else
				addXSubject(ec, m_xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapDiscontinuousConstituent(CTTree cTree, CTNode ec)
	{
		CTNode parent = ec.getParent();
		CTNode ante   = ec.getAntecedent();
		
		if (ec.wordFormStartsWith(CTTagEn.E_ICH) && parent.getLeftNearestSibling(CTLibEn.M_WHx) != null)
			removeCTNode(ec);
		else if (ante == null || ec.isDescendantOf(ante))
			removeCTNode(ec);
		else
		{
			List<CTNode> list = cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex());
			boolean isRNR = CTLibEn.isRNR(ec);
			int i, size = list.size();
			CTNode node;
			
			Deque<CTNode> dq = isRNR ? new ArrayDeque<CTNode>() : null;
			
			if (ec.getTerminalID() < ante.getFirstTerminal().getTerminalID())
			{		
				for (i=0; i<size-1; i++)
				{
					node = list.get(i);
					if (isRNR)	dq.addLast(node.getParent().getParent());
					removeCTNode(node);
				}
				
				ec = list.get(size-1);
			}
			else
			{
				for (i=size-1; i>0; i--)
				{
					node = list.get(i);
					if (isRNR)	dq.addFirst(node.getParent().getParent());
					removeCTNode(node);
				}
				
				ec = list.get(0);
			}
			
			if (isRNR && !dq.isEmpty())
				m_rnr.put(ante, dq);
			
			parent = ec.getParent();
			parent.removeChild(ec);
			replaceEC(parent, ante);
		}
	}
	
	/** Called by {@link #mapPRO(CTTree, CTNode)} and {@link #mapPassiveNull(CTTree, CTNode)}. */
	private void relocatePRD(CTNode np, CTNode ec)
	{
		CTNode s   = np.getParent();
		CTNode prd = s.getFirstChild(mt_prd);
		Set<String> fTags = s.getFunctionTagSet();
		
		if (prd != null && (fTags.isEmpty() || fTags.contains(CTTagEn.F_CLR)))
		{
			fTags.clear();
			fTags.add(DEPTagEn.DEP_OPRD);
		}

		removeCTNode(ec);
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
	 * @param map key: antecedent, value: list of clauses containing empty subjects.
	 */
	private void addXSubject(CTNode ec, Map<CTNode, Deque<CTNode>> map)
	{
		CTNode ante = ec.getAntecedent();
		
		while (ante != null && ante.isEmptyCategoryTerminal())
		{
			if (CTLibEn.isWhPhrase(ante)) return;
			ante = ante.getFirstTerminal().getAntecedent();
		}
		
		if (ante != null)
		{
			CTNode s = ec.getNearestAncestor(mt_s);
			
			if (s != null)
			{
				Deque<CTNode> dq = map.get(ante);
				if (dq == null)	dq = new ArrayDeque<CTNode>();
				
				dq.add(s);
				map.put(ante, dq);
			}
		}
	}
	
	private void removeCTNode(CTNode node)
	{
		CTNode parent = node.getParent();
	
		if (parent != null)
		{
			parent.removeChild(node);
			
			if (parent.getChildrenSize() == 0)
				removeCTNode(parent);			
		}
	}
	
	private void replaceEC(CTNode ec, CTNode ante)
	{
		removeCTNode(ante);
		ec.getParent().replaceChild(ec, ante);
	}
	
// ============================= Find heads =============================
	
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
			
			if (!POSLibEn.isPunctuation(node.getConstituentTag()) && !CTLibEn.isConjunction(node) && !node.isEmptyCategoryTerminal())
				break;
		}
		
		if (!CTLibEn.containsCoordination(curr, curr.getChildrenList(sId)))
			return false;
		
		// find conjuncts
		Pattern rTags = getConjunctPattern(curr, sId, size);
		CTNode prevHead = null, mainHead = null;
		boolean isFound = false;
		int bId = 0, eId = sId;
		
		for (; eId<size; eId++)
		{
			node = curr.getChild(eId);
			
			if (CTLibEn.isCoordinator(node))
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
		Pattern rTags = m_coord.get(curr.getConstituentTag());
		
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
		if (P.isConstituentTag(CTTagEn.C_SBAR) && C.isConstituentTagAny(S_PREP_DET))
			return false;
		else if (rTags.pattern().equals(".*"))
			return getSpecialLabel(C) == null;
		else if (rTags.matcher(C.getConstituentTag()).find())
		{
			if (P.isConstituentTag(CTTagEn.C_VP) && getAuxLabel(C) != null)
				return false;
			
			if (CTLibEn.isMainClause(P) && C.isConstituentTag(CTTagEn.C_S) && hasAdverbialTag(C))
				return false;
			
			return true;
		}
		else if (P.isConstituentTag(CTTagEn.C_NP))
		{
			return C.hasFunctionTag(CTTagEn.F_NOM);
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
			else if (POSLibEn.isPunctuation(currHead.getConstituentTag()))	label = DEPTagEn.DEP_PUNCT;

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
		boolean isVP = node.isConstituentTag(CTTagEn.C_VP);
		
		for (i=0; i<size-2; i++)
		{
			prev = node.getChild(i);
			hyph = node.getChild(i+1);
			next = node.getChild(i+2);
			
			if (hyph.isConstituentTag(CTLibEn.POS_HYPH))
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
		if (!curr.isConstituentTagAny(S_NOUN_PHRASE) || curr.containsChild(CTLibEn.M_NNx))
			return false;
		
		CTNode fst = curr.getFirstChild(CTLibEn.M_NP_NML);
		while (fst != null && fst.containsChild(mt_pos))
			fst = fst.getRightNearestSibling(CTLibEn.M_NP_NML);
		
		if (fst == null || fst.getC2DInfo().hasHead())	return false;

		boolean hasAppo = false;
		CTNode snd = fst;
		
		while ((snd = snd.getRightSibling()) != null)
		{
			if (snd.getC2DInfo().hasHead())	continue;
			
			if ((snd.isConstituentTagAny(S_NOUN_PHRASE) && !hasAdverbialTag(snd)) ||
				(snd.hasFunctionTagAny(CTTagEn.F_HLN, CTTagEn.F_TTL)) ||
				(snd.isConstituentTag(CTTagEn.C_RRC) && snd.containsChild(mt_np_prd)))
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
		
		if (node.isConstituentTag(CTTagEn.C_S) && !node.containsChild(CTLibEn.M_VP))
		{
			CTNode sbj = node.getFirstChild(mt_sbj);
			CTNode prd = node.getFirstChild(mt_prd);
			
			if (sbj != null && prd != null)
			{
				if (parent.isConstituentTag(CTTagEn.C_SQ))
				{
					CTNode vb = parent.getFirstChild(CTLibEn.M_VBx);
					
					if (vb != null)
					{
						sbj.getC2DInfo().setHead(vb, getDEPLabel(sbj, parent, vb));
						node.setConstituentTag(prd.getConstituentTag());
						node.addFunctionTag(CTTagEn.F_PRD);
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
		
		if (child.isEmptyCategoryTerminal() || POSLibEn.isPunctuation(child.getConstituentTag()))
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
		if (C.isConstituentTag(CTTagEn.C_UCP))
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
		
		if (C.isConstituentTag(CTTagEn.C_SBAR) || isXcomp(C) || (P.isConstituentTag(CTTagEn.C_PP) && CTLibEn.isClause(C)))
			return DEPTagEn.DEP_ADVCL;
		
		if (C.isConstituentTagAny(S_CCOMP))
			return DEPTagEn.DEP_CCOMP;
		
		if (P.isConstituentTag(CTTagEn.C_QP))
		{
//			if (C.isConstituentTag(CTLibEn.POS_CD) && p.isConstituentTag(CTLibEn.POS_CD))
//				return DEPTagEn.DEP_COMPOUND;
//			else
			return DEPTagEn.DEP_QMOD;
		}
		
		if (P.isConstituentTagAny(S_NMOD_PARENT) || POSLibEn.isNoun(p.getConstituentTag()))
			return getNmodLabel(C, d);
		
		if (c != null)
		{
			if ((label = getSimpleLabel(c)) != null)
				return label;
			
			if (d.isConstituentTag(CTLibEn.POS_IN))
				return DEPTagEn.DEP_PREP;
			
			if (POSLibEn.isAdverb(d.getConstituentTag()))
				return DEPTagEn.DEP_ADVMOD;
		}
		
		if ((P.isConstituentTagAny(S_ADVB_PHRASE) || POSLibEn.isAdjective(p.getConstituentTag()) || POSLibEn.isAdverb(p.getConstituentTag())))
		{
			if (C.isConstituentTagAny(S_NPADVMOD) || POSLibEn.isNoun(C.getConstituentTag()))
				return DEPTagEn.DEP_NPADVMOD;
			
			return DEPTagEn.DEP_ADVMOD;
		}
		
		if (d.hasC2DInfo() && (label = d.getC2DInfo().getLabel()) != null)
			return label;
		
		return DEPTagEn.DEP_DEP;
	}
	
	private boolean hasAdverbialTag(CTNode node)
	{
		return node.hasFunctionTag(CTTagEn.F_ADV) || DSUtils.hasIntersection(node.getFunctionTagSet(), s_semTags);
	}
	
	private String getObjectLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_NOUN_PHRASE))
		{
			if (node.hasFunctionTag(CTTagEn.F_PRD))
				return DEPTagEn.DEP_ATTR;
			else
				return DEPTagEn.DEP_DOBJ;
		}
		
		return null;
	}
	
	private String getSubjectLabel(CTNode C, CTNode d)
	{
		if (C.hasFunctionTag(CTTagEn.F_SBJ))
		{
			if (CTLibEn.isClause(C))
				return DEPTagEn.DEP_CSUBJ;
			else if (d.isConstituentTag(CTLibEn.POS_EX) || d.isWordFormIgnoreCase("there"))
				return DEPTagEn.DEP_EXPL;
			else
				return DEPTagEn.DEP_NSUBJ;
		}
		else if (C.hasFunctionTag(CTTagEn.F_LGS))
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
		
		if (CTLibEn.isCorrelativeConjunction(C))
			return DEPTagEn.DEP_PRECONJ;
		
		if (CTLibEn.isConjunction(C))
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
		
		if (POSLibEn.isPunctuation(C.getConstituentTag()) || POSLibEn.isPunctuation(d.getConstituentTag()))
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
		
		if (POSLibEn.isVerb(node.getConstituentTag()) && (vp = node.getRightNearestSibling(CTLibEn.M_VP)) != null)
		{
			if (ENUtils.isPassiveAuxiliaryVerb(node.getWordForm()))
			{
				if (vp.containsChild(CTLibEn.M_VBD_VBN))
					return DEPTagEn.DEP_AUXPASS;
				
				if (!vp.containsChild(CTLibEn.M_VBx) && (vp = vp.getFirstChild(CTLibEn.M_VP)) != null && vp.containsChild(CTLibEn.M_VBD_VBN))
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
		
//		if (C.isConstituentTagAny(S_NN) || (C.matches(CTLibEn.M_NNx) || C.isConstituentTag(CTLibEn.POS_FW)))
//			return DEPTagEn.DEP_COMPOUND;
		
//		if (C.isConstituentTagAny(S_NUM) || d.isConstituentTag(CTLibEn.POS_CD))
//			return DEPTagEn.DEP_NUMMOD;

		if (C.isConstituentTag(CTLibEn.POS_POS))
			return DEPTagEn.DEP_CASE;
		
		if (C.isConstituentTag(CTLibEn.POS_PDT))
			return DEPTagEn.DEP_PREDET;
		
		return DEPTagEn.DEP_NMOD;
	}
	
	private String getPmodLabel(CTNode C, CTNode d)
	{
		if (C.isConstituentTagAny(S_NOUN_PHRASE) || POSLibEn.isRelativizer(d.getConstituentTag()))
			return DEPTagEn.DEP_POBJ;
		else
			return DEPTagEn.DEP_PCOMP;	
	}
	
	private boolean isHyph(CTNode node)
	{
		return node.isConstituentTag(CTLibEn.POS_HYPH);
	}
	
//	private boolean isAmod(CTNode node)
//	{
//		return node.isConstituentTagAny(S_ADJT_PHRASE) || CTLibEn.isAdjective(node);
//	}
	
	private boolean isAdv(CTNode C)
	{
		if (C.isConstituentTag(CTTagEn.C_ADVP) || POSLibEn.isAdverb(C.getConstituentTag()))
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
		return node.isConstituentTag(CTTagEn.C_PRN);
	}
	
	private boolean isPrt(CTNode curr)
	{
		return curr.isConstituentTagAny(S_PRT);
	}
	
	private boolean isAcomp(CTNode node)
	{
		return node.isConstituentTag(CTTagEn.C_ADJP);
	}
	
	private boolean isOprd(CTNode curr)
	{
		if (curr.hasFunctionTag(DEPTagEn.DEP_OPRD))
			return true;
		
		if (curr.isConstituentTag(CTTagEn.C_S) && !curr.containsChild(CTLibEn.M_VP) && curr.containsChild(mt_prd))
		{
			CTNode sbj = curr.getFirstChild(mt_sbj);
			return sbj != null && sbj.isEmptyCategoryTerminal();
		}
		
		return false;
	}
	
	private boolean isPoss(CTNode curr, CTNode parent)
	{
		if (curr.isConstituentTagAny(S_POSS))
			return true;
		
		if (parent.isConstituentTagAny(S_POSS_PARENT))
			return curr.containsChild(mt_pos);
		
		return false;
	}
	
	private boolean isXcomp(CTNode node)
	{
		if (node.isConstituentTag(CTTagEn.C_S))
		{
			CTNode sbj = node.getFirstChild(mt_sbj);
			
			if (node.containsChild(CTLibEn.M_VP) && (sbj == null || sbj.isEmptyCategoryTerminal()))
				return true;
		}
		else if (node.hasFunctionTag(DEPTagEn.DEP_RELCL))
		{
			CTNode s = node.getFirstChild(mt_s);
			if (s != null)	return isXcomp(s);
		}

		return false;
	}
	
	private boolean isCcomp(CTNode node)
	{
		if (node.isConstituentTagAny(S_CCOMP))
			return true;
		
		if (node.isConstituentTag(CTTagEn.C_SBAR))
		{
			CTNode comp;
			
			if ((comp = node.getFirstChild(mt_none)) != null && comp.isWordForm(CTTagEn.E_ZERO))
				return true;
			
			if ((comp = node.getFirstChild(mt_in_dt)) != null)
			{
				if (isComplm(comp))
				{
//					comp.getC2DInfo().setLabel(DEPTagEn.DEP_COMPLM);
					comp.getC2DInfo().setLabel(DEPTagEn.DEP_MARK);
					return true;
				}
			}
			
			if (node.hasFunctionTag(DEPTagEn.DEP_RELCL) || node.containsChild(CTLibEn.M_WHx))
				return true;
		}
		
		return false;
	}
	
	private boolean isNfmod(CTNode curr)
	{
		return isXcomp(curr) || curr.isConstituentTag(CTTagEn.C_VP);
	}
	
	protected boolean isInfMod(CTNode curr)
	{
		CTNode vp = curr.isConstituentTag(CTTagEn.C_VP) ? curr : curr.getFirstDescendant(CTLibEn.M_VP);
		
		if (vp != null)
		{
			CTNode vc = vp.getFirstChild(CTLibEn.M_VP);
			
			while (vc != null)
			{
				vp = vc;
				
				if (vp.getLeftNearestSibling(mt_to) != null)
					return true;
				
				vc = vp.getFirstChild(CTLibEn.M_VP);
			}
			
			return vp.containsChild(mt_to);
		}
		
		return false;
	}
	
	private boolean isRcmod(CTNode curr)
	{
		return curr.isConstituentTag(CTTagEn.C_RRC) || curr.hasFunctionTag(DEPTagEn.DEP_RELCL) || (curr.isConstituentTag(CTTagEn.C_SBAR) && curr.containsChild(CTLibEn.M_WHx));
	}
	
	private boolean isComplm(CTNode curr)
	{
		return S_COMPLM.contains(StringUtils.toLowerCase(curr.getWordForm()));
	}
	
	// ============================= Get a dependency tree =============================
	
	private NLPNode[] getDEPTree(CTTree cTree)
	{
		NLPNode[] dTree = initDEPTree(cTree);
		addDEPHeads(dTree, cTree);
		
		if (NLPUtils.containsCycle(dTree))
			throw new UnknownFormatConversionException("Cyclic depedency relation.");

		DEPLibEn.enrichLabels(dTree);
		addSecondaryHeads(dTree);
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
				
				if (cNode.isConstituentTagAny(S_MARK) && cNode.getParent().isConstituentTag(CTTagEn.C_SBAR))// && !label.equals(DEPTagEn.DEP_COMPLM))
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
	private void addSecondaryHeads(NLPNode[] dTree)
	{
		for (CTNode curr : m_xsubj.keySet())
		{
			if (curr.hasC2DInfo())
				addSecondaryHeadsAux(dTree, curr, m_xsubj.get(curr), DEPTagEn.DEP2_XSUBJ);
		}
		
		for (CTNode curr : m_rnr.keySet())
		{
			if (curr.getParent() == null)
				continue;
			
			if (curr.getParent().getC2DInfo().getNonTerminalHead() != curr)
				addSecondaryHeadsAux(dTree, curr, m_rnr.get(curr), DEPTagEn.DEP2_RNR);
			else
				addSecondaryChildren(dTree, curr, m_rnr.get(curr), DEPTagEn.DEP2_RNR);
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
		
		if ((feat = getFunctionTags(cNode, s_semTags)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);
		
		if ((feat = getFunctionTags(cNode, s_synTags)) != null)
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
		if (cNode.isConstituentTag(CTTagEn.TOP)) return null;
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
			if (CTLibEn.isEditedPhrase(child))
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
					if ((c = CTLibEn.getRelativizer(c)) != null && (c = c.getAntecedent()) != null)
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
		
		if ((ref = CTLibEn.getWhPhrase(node)) != null)
			return ref;
		
		if (node.isConstituentTag(CTTagEn.C_PP))
		{
			for (CTNode child : node.getChildrenList()) 
			{
				if ((ref = CTLibEn.getWhPhrase(child)) != null)
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
		finalizeCompound(tree, POSTagEn.POS_NN, DEPTagEn.DEP_NMOD , n -> n.getPartOfSpeechTag().startsWith(POSTagEn.POS_NNP) || n.isDependencyLabel(DEPTagEn.DEP_NMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
		finalizeCompound(tree, POSTagEn.POS_CD, DEPTagEn.DEP_QMOD, n -> n.isDependencyLabel(DEPTagEn.DEP_QMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
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
		if (!POSLibEn.isVerb(node.getDependencyHead().getPartOfSpeechTag())) return false;
//		if (node.isDependencyLabel(DEPTagEn.DEP_IOBJ)) return true;
		String feat;
		
		if ((feat = node.getFeat(NLPUtils.FEAT_SYN)) != null && DSUtils.toHashSet(Splitter.splitCommas(feat)).contains(CTTagEn.F_DTV)) return true;
		if (CTTagEn.F_BNF.equals(node.getFeat(NLPUtils.FEAT_SEM))) return true;
		
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
		return (feat = node.getFeat(NLPUtils.FEAT_SEM)) != null && feat.equals(CTLibEn.F_VOC);
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
					else if (node.isPartOfSpeechTag(POSTagEn.POS_HYPH))
						continue;
					else
						break;
				}
			}
		}
	}
}