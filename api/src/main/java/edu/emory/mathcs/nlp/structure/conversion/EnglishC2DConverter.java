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
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.grammar.Aspect;
import edu.emory.mathcs.nlp.common.grammar.TVerb;
import edu.emory.mathcs.nlp.common.grammar.Tense;
import edu.emory.mathcs.nlp.common.grammar.Voice;
import edu.emory.mathcs.nlp.common.treebank.CTTag;
import edu.emory.mathcs.nlp.common.treebank.DSRTag;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.dep.DEPArc;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.structure.constituency.CTArc;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRule;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;
import edu.emory.mathcs.nlp.structure.propbank.PBLib;
import edu.emory.mathcs.nlp.structure.util.Arc;
import edu.emory.mathcs.nlp.structure.util.FeatMap;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
import edu.emory.mathcs.nlp.structure.util.PTBTag;
import edu.emory.mathcs.nlp.zzz.C2DInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishC2DConverter extends C2DConverter
{
	private final Set<String> CCOMP       = Sets.newHashSet(PTBTag.C_S, PTBTag.C_SQ, PTBTag.C_SINV, PTBTag.C_SBARQ);
	private final Set<String> COMPP       = Sets.newHashSet(PTBTag.C_VP, PTBTag.C_SINV, PTBTag.C_SQ);
	private final Set<String> ADVCL       = Sets.newHashSet(PTBTag.C_S, PTBTag.C_SBAR, PTBTag.C_SINV);
	private final Set<String> NPMOD       = Sets.newHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_QP);
	private final Set<String> META        = Sets.newHashSet(PTBTag.C_EDITED, PTBTag.C_EMBED, PTBTag.C_LST, PTBTag.C_META, PTBTag.P_CODE, PTBTag.C_CAPTION, PTBTag.C_CIT, PTBTag.C_HEADING, PTBTag.C_TITLE, PTBTag.P_DOLLAR);
	private final Set<String> POSS        = Sets.newHashSet(PTBTag.P_PRPS, PTBTag.P_WPS);
	private final Set<String> INTJ        = Sets.newHashSet(PTBTag.C_INTJ, PTBTag.P_UH);
	private final Set<String> PRT         = Sets.newHashSet(PTBTag.C_PRT, PTBTag.P_RP);
	private final Set<String> DET         = Sets.newHashSet(PTBTag.P_DT, PTBTag.P_WDT, PTBTag.P_WP, PTBTag.P_PDT);
	private final Set<String> NP          = Sets.newHashSet(PTBTag.C_NP, PTBTag.C_NML);
	private final Set<String> PP          = Sets.newHashSet(PTBTag.C_PP, PTBTag.C_WHPP);
	private final Set<String> ADVP        = Sets.newHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP, PTBTag.C_PP);
	private final Set<String> PREP        = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_TO);
	private final Set<String> ACOMP       = Sets.newHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP);
	private final Set<String> PREP_DET    = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_DT);
	private final Set<String> NMOD_PARENT = Sets.newHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_NX, PTBTag.C_WHNP);
	private final Set<String> POSS_PARENT = Sets.newHashSet(PTBTag.C_NP, PTBTag.C_NML, PTBTag.C_WHNP, PTBTag.C_QP, PTBTag.C_ADJP);

	/** Syntactic function tags. */
	private final Set<String> SYN_TAGS = Sets.newHashSet(PTBTag.F_ADV, PTBTag.F_CLF, PTBTag.F_CLR, PTBTag.F_DTV, PTBTag.F_NOM, PTBTag.F_PUT, PTBTag.F_PRD, PTBTag.F_TPC);
	/** Semantic function tags. */
	private final Set<String> SEM_TAGS = Sets.newHashSet(PTBTag.F_BNF, PTBTag.F_DIR, PTBTag.F_EXT, PTBTag.F_LOC, PTBTag.F_MNR, PTBTag.F_PRP, PTBTag.F_TMP);
	/** Mappings between phrasal/clausal tags and phrasal/pos tags for coordination. */
	private final Map<String,Pattern> COORD_MAP = initCoordMap();
	
	// lexicons
	private final MorphAnalyzer analyzer;
	private final Emoticon      emoticon;
	private final Set<String>   transfer_verbs;
	
//	private final String AUXP  = "AUXP";
//	private final String NLGS  = "NLGS";
//	private final String CLGS  = "CLGS";
//	private final String XSUBJ = "XSUBJ";
	
//	======================== Constructors ========================

	public EnglishC2DConverter()
	{
		analyzer = null;
		emoticon = null;
		transfer_verbs = null;
	}
	
	public EnglishC2DConverter(HeadRuleMap headrules, Set<String> transfer_verbs)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));
		this.analyzer = new EnglishMorphAnalyzer();
		this.emoticon = new Emoticon();
		this.transfer_verbs = transfer_verbs;
	}
	
	private Map<String,Pattern> initCoordMap()
	{
		Map<String,Pattern> map = new HashMap<>();	
		
		map.put(PTBTag.C_S     , PatternUtils.createClosedORPattern("S","SINV","SQ","SBARQ"));
		map.put(PTBTag.C_SBAR  , PatternUtils.createClosedORPattern("SBAR.*"));
		map.put(PTBTag.C_SBARQ , PatternUtils.createClosedORPattern("SBAR.*"));
		map.put(PTBTag.C_SINV  , PatternUtils.createClosedORPattern("S","SINV"));
		map.put(PTBTag.C_SQ    , PatternUtils.createClosedORPattern("S","SQ","SBARQ"));
		map.put(PTBTag.C_ADJP  , PatternUtils.createClosedORPattern("ADJP","JJ.*","VBN","VBG"));
		map.put(PTBTag.C_ADVP  , PatternUtils.createClosedORPattern("ADVP","RB.*"));
		map.put(PTBTag.C_INTJ  , PatternUtils.createClosedORPattern("INTJ","UH"));
		map.put(PTBTag.C_NAC   , PatternUtils.createClosedORPattern("NP"));
		map.put(PTBTag.C_NML   , PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		map.put(PTBTag.C_NP    , PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		map.put(PTBTag.C_NX    , PatternUtils.createClosedORPattern("NX"));
		map.put(PTBTag.C_PP    , PatternUtils.createClosedORPattern("PP","IN","VBG"));
		map.put(PTBTag.C_PRT   , PatternUtils.createClosedORPattern("PRT","RP"));
		map.put(PTBTag.C_QP    , PatternUtils.createClosedORPattern("QP","CD"));
		map.put(PTBTag.C_VP    , PatternUtils.createClosedORPattern("VP","VB.*"));
		map.put(PTBTag.C_WHADJP, PatternUtils.createClosedORPattern("JJ.*","VBN","VBG"));
		map.put(PTBTag.C_WHADVP, PatternUtils.createClosedORPattern("RB.*","WRB","IN"));
		map.put(PTBTag.C_WHNP  , PatternUtils.createClosedORPattern("NN.*","WP"));
		
		return map;
	}
	
//	============================= Abstract Methods =============================

	@Override
	public NLPGraph toDependencyGraph(CTTree tree)
	{
		if (tree.containsOnlyEmptyCategories()) return null;
		preprocess(tree);
		mapEmtpyCategories(tree);
		setHead(tree.getRoot());
		Map<CTNode,CTNode> terminal_map = getTerminalMap(tree.getRoot(), new HashMap<>());
		finalizeDependencies(tree.getRoot(), terminal_map);
		return createDependencyGraph(tree);
	}
	
	@Override
	protected void setHead(CTNode curr, HeadRule rule)
	{
		if (findHeadCoordination(curr, rule)) return;
		findHeadHyphen(curr);
		findHeadApposition(curr);
		findHeadSmallClause(curr);
		findHeadQuantifierPhrase(curr);

		CTNode head = getHead(curr.getChildren(), rule, SIZE_HEAD_FLAGS);
		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null); 
		curr.setC2DInfo(new C2DInfo(head));
	}
	
	@Override
	protected int getHeadFlag(CTNode child)
	{
		C2DInfo info = child.getC2DInfo();
		
		if (info.hasHead())// && info.getTerminalHead() != info.getNonTerminalHead())
			return -1;
		
		if (child.isFunctionTag(PTBTag.F_PRD))
			return 0;
		
		if (hasAdverbialTag(child))
			return 1;
		
		if (isMeta(child))
			return 2;
		
		if (child.isEmptyCategoryPhrase() || PTBLib.isPunctuation(child.getSyntacticTag()))
			return 3;
		
		return 0;
	}

//	============================= Pre-process =============================
	
	public void preprocess(CTTree tree)
	{
		lemmatize(tree);
		PTBLib.preprocess(tree);
		tree.flatten().forEach(n -> preprocess(n));
	}
	
	private void lemmatize(CTTree tree)
	{
		for (CTNode token : tree.getTokens())
		{
			analyzer.setLemma(token);
			
			if (token.isLemma("'s"))
			{
				String lemma = PTBLib.getLemmaOfApostropheS(token);
				if (lemma != null) token.setLemma(lemma);
			}
		}
	}
	
	private void preprocess(CTNode node)
	{
		preprocessModalAdjective(node);
		preprocessQuantifierPhrase(node);
	}
	
	public CTNode preprocessModalAdjective(CTNode node)
	{
		boolean is_vp = node.hasParent(n -> n.isSyntacticTag(PTBTag.C_VP));
		boolean is_sq = node.hasParent(n -> n.isSyntacticTag(PTBTag.C_SQ));
		
		if (node.andSF(PTBTag.C_ADJP, PTBTag.F_PRD) && (is_vp || is_sq))
		{
			CTNode s = node.getFirstChild(n -> n.isSyntacticTag(PTBTag.C_S));
			
			if (s != null)
			{
				CTNode np = s.getFirstChild(PTBLib::isNominalSubject);
				
				if (np != null && np.isEmptyCategoryPhrase())
				{
					CTNode sbj = np.getFirstTerminal().getAntecedent();
					
					if (sbj != null)
					{
						CTNode vp = node.getHighestChainedAncestor(n -> n.isSyntacticTag(PTBTag.C_VP));
						if (is_sq && vp == null) vp = node;
						
						if (sbj == vp.getLeftNearestSibling(PTBLib::isNominalSubject))
						{
							CTNode prd = node.getFirstChild(PTBLib::isAdjective);
							
							if (prd != null)
							{
								s.addFunctionTag(PTBTag.F_PRD);
								return prd;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public boolean preprocessQuantifierPhrase(CTNode node)
	{
		if (!node.isSyntacticTag(PTBTag.C_QP)) return false;
		
		for (CTNode child : node.getChildren())
		{
			if (child.isTerminal() && MetaConst.CARDINAL.equals(child.getLemma()))
				child.setSyntacticTag(PTBTag.P_CD);
		}
		
		outer: for (int i=0; i<node.getChildrenSize(); i++)
		{
			CTNode child = node.getChild(i);
			
			if (isQuantifierConjunction(child))
			{
				for (int j=i-1; j>=0; j--)
				{
					CTNode prev = node.getChild(j);
					if (prev.isSyntacticTag(PTBTag.P_CC) || isQuantifierConjunction(prev)) continue outer;
							
					if (prev.isSyntacticTag(PTBTag.P_CD))
					{
						for (i++; i<node.getChildrenSize(); i++)
						{
							CTNode next = node.getChild(i);
							
							if (next.isSyntacticTag(PTBTag.P_CC) || isQuantifierConjunction(next))
								continue outer;
							
							if (next.isSyntacticTag(PTBTag.P_CD))
							{
								child.setPrimaryLabel(DSRTag.CC);
								continue outer;
							}
						}
						
						break;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean isQuantifierConjunction(CTNode node)
	{
		return node.isSyntacticTag(PTBTag.P_TO) || node.isSyntacticTag(PTBTag.P_SYM);
	}
	
//	============================= Empty Categories ============================= 
	
	/**
	 * Removes, relocates empty categories in the specific tree. 
	 * @param tree the constituent tree to be processed.
	 * @return {@true} if the constituent tree contains nodes after relocating empty categories.
	 */
	private boolean mapEmtpyCategories(CTTree tree)
	{
		for (CTNode node : tree.getTerminals())
		{
			if (node.isEmptyCategory() && node.hasParent())
			{
				if      (PTBLib.isPRO(node))
					mapPRO(tree, node, xsubj);
				else if (PTBLib.isTrace(node))
					mapTrace(tree, node);
				else if (PTBLib.isPassiveNull(node))
					mapPassiveNull(tree, node, xsubj);
				else if (PTBLib.isDiscontinuousConstituent(node))
					mapDiscontinuousConstituent(tree, node, rnr);
				else if (PTBLib.isNullComplementizer(node))
					continue;
				else if (PTBLib.isExpletive(node))
					reloateEXP(tree, node);
				else
					removeNode(node);				
			}
		}
		
		return tree.getRoot().getChildrenSize() > 0;
	}
	
	/**
	 * (TOP (S (NP-SBJ-1 (NNP John))
     *         (VP (VBD bought)
     *             (NP (DT a)
     *                 (NN book))
     *             (S-PRP (NP-SBJ (-NONE- *PRO*-1))
     *                    (VP (TO to)
     *                        (VP (VB teach)
     *                            (NP (DT the)
     *                                (NN course))))))))
     *
     * (TOP (S (NP-SBJ-1 (NNP John))
     *         (VP (VBD had)
     *             (S (NP-SBJ-2 (-NONE- *-1))
     *                (VP (TO to)
     *                    (VP (VB buy)
     *                        (NP (DT a)
     *                            (NN book))
     *                        (S-PRP (NP-SBJ (-NONE- *PRO*-2))
     *                               (VP (TO to)
     *                                   (VP (VB teach)
     *                                       (NP (DT the)
     *                                           (NN course)))))))))))
	 */
	private void mapPRO(CTTree cTree, CTNode ec, Map<CTNode,Deque<CTNode>> xsubj)
	{
		CTNode np = ec.getParent();
		CTNode vp = np.getParent().getFirstLowestChainedDescendant(PTBLib.M_VP);
		
		if (vp == null)
			handleSmallClause(np, ec);
		else
		{
			CTNode ante;
			
			if ((ante = ec.getAntecedent()) != null && PTBLib.isWhPhrase(ante))	// relative clauses
			{
				if (cTree.getEmptyCategories(ante.getCoIndex()).size() == 1)
					mapTrace(cTree, ec);
			}
			
			addXSubject(ec, xsubj);
		}
	}
	
	/**
	 * (TOP (SINV (`` ")
     *            (S-TPC-1 (NP-SBJ (PRP I))
     *                     (VP (VBP am)
     *                         (ADJP (JJ smart))))
     *            ('' ")
     *            (VP (VBZ says)
     *                (S (-NONE- *T*-1)))
     *            (NP-SBJ (NNP John))))
     *        
     * (TOP (NP (NP (NNP John))
     *          (SBAR (WHNP-2 (WP who))
     *                (S (NP-SBJ-1 (PRP I))
     *                   (VP (VBD wanted)
     *                       (S (NP-SBJ (-NONE- *PRO*-1))
     *                          (VP (TO to)
     *                              (VP (VB meet)
     *                                  (NP (-NONE- *T*-2)))))))))
     *       (VP (VBZ is)
     *           (ADVP-LOC (RB here))))
     *           
     * (TOP (NP (NP (NNP John))
     *          (SBAR (WHNP-1 (WP who))
     *                (S (NP-SBJ (PRP I))
     *                   (VP (VBD bought)
     *                       (NP (DT a)
     *                           (NN book))
     *                       (PP (IN for)
     *                           (NP (-NONE- *T*-1)))))))
     *       (VP (VBZ is)
     *           (ADVP-LOC (RB here))))
	 */
	private void mapTrace(CTTree cTree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			removeNode(ec);
		else if (ante.isFunctionTag(PTBTag.F_TPC))
		{
			if (!ante.isFunctionTag(PTBTag.F_SBJ))
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
			if (parent != null) parent.addFunctionTag(DSRTag.RELCL);
			replaceEmptyCategory(ec, ante);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPassiveNull(CTTree cTree, CTNode ec, Map<CTNode,Deque<CTNode>> xsubj)
	{
		CTNode np = ec.getParent();
		
		if (np.isFunctionTag(PTBTag.F_SBJ))
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
		
		if (ec.formStartsWith(PTBTag.E_ICH) && parent.getLeftNearestSibling(PTBLib.M_WHx) != null)
			removeNode(ec);
		else if (ante == null || ec.isDescendantOf(ante))
			removeNode(ec);
		else
		{
			List<CTNode> list = cTree.getEmptyCategories(ante.getCoIndex());
			boolean isRNR = PTBLib.isRightNodeRaising(ec);
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
	
	private void reloateEXP(CTTree cTree, CTNode ec)
	{
		CTNode s = ec.getParent();
		
		if (s != null && PTBLib.isClause(s))
		{
			CTNode np = s.getParent();
			
			if (np != null && np.matches(PTBLib.M_NP_SBJ))
			{
				np.addFunctionTag(DSRTag.EXPL);
				if (ec.hasAntecedent())
					ec.getAntecedent().addFunctionTag(PTBTag.F_SBJ);
			}
		}
		
		removeNode(ec);
	}
	
	private void handleSmallClause(CTNode np, CTNode ec)
	{
		handleSmallClause(np, ec, false);
	}
	
	/**
     * (TOP (S (NP-SBJ (PRP I))
     *         (VP (VBP call)
     *             (NP-1 (NNP John))
     *             (S-CLR (NP-SBJ (-NONE- *PRO*-1))
     *                    (NP-PRD (DT the)
     *                            (NN genius))))))
     *
     * (TOP (S (NP-SBJ (PRP I))
     *         (VP (VBP consider)
     *             (S (NP-SBJ (NNP John))
     *                (ADJP-PRD (JJ smart))))))
	 */
	private void handleSmallClause(CTNode np, CTNode ec, boolean replace)
	{
		CTNode s   = np.getParent();
		CTNode prd = s.getFirstChild(PTBLib.M_PRD);
		
		if (prd != null && (!s.hasFunctionTag() || s.isFunctionTag(PTBTag.F_CLR)))
		{
			s.clearFunctionTags();
			s.addFunctionTag(DSRTag.OPRD);
		}
		
		if (replace)
		{
			if (ec.hasAntecedent())
				replaceEmptyCategory(ec, ec.getAntecedent());
		}
		else
			removeNode(ec);
	}
	
	/**
	 * @param ec empty subject.
	 * @param xsubj key: antecedent, value: list of clauses containing empty subjects.
	 */
	private void addXSubject(CTNode ec, Map<CTNode, Deque<CTNode>> xsubj)
	{
		CTNode ante = ec.getAntecedent();
		
		while (ante != null && ante.isEmptyCategoryPhrase())
		{
			if (PTBLib.isWhPhrase(ante)) return; // TODO: why?
			ante = ante.getFirstTerminal().getAntecedent();
		}
		
		if (ante != null)
		{
			CTNode s = ec.getNearestAncestor(PTBLib.M_S);
			if (s != null) xsubj.computeIfAbsent(ante, n -> new ArrayDeque<>()).add(s);
		}
	}
	
//	============================= Coordination =============================

	/**
	 * If the specific node contains a coordination structure, find the head of each coordination.
	 * @param curr the specific node to be compared. 
	 * @return {@code true} if this node contains a coordination structure.
	 */
	private boolean findHeadCoordination(CTNode curr, HeadRule rule)
	{
		// skip pre-conjunctions and punctuation
		int i, sId, size = curr.getChildrenSize();
		CTNode node;
		
		for (sId=0; sId<size; sId++)
		{
			node = curr.getChild(sId);
			
			if (!PTBLib.isConjunction(node) && !PTBLib.isPunctuation(node) && !node.isEmptyCategoryPhrase())
				break;
		}
		
		// not a coordination construction
		if (!PTBLib.containsCoordination(curr, curr.getChildren(sId)))
			return false;
		
		// find conjuncts
		Pattern rTags = getConjunctPattern(curr, sId, size);
		List<CTNode> heads = new ArrayList<>();
		CTNode head = null, main_head = null;
		boolean isConjunctFound = false;
		int bId = 0, eId = sId;
		
		for (; eId<size; eId++)
		{
			node = curr.getChild(eId);
			
			if (PTBLib.isCoordinator(node))
			{
				if (isConjunctFound)
				{
					head = findHeadCoord(curr, rule, bId, eId, main_head);
					if (main_head == null) main_head = head;
					setPrimaryHead(node, head);
					isConjunctFound = false;
					heads.add(head);
					bId = eId + 1;
				}
				else if (!heads.isEmpty())
				{
					head = DSUtils.getLast(heads);
					
					for (i=bId; i<=eId; i++)
						setPrimaryHead(curr.getChild(i), head);
					
					bId = eId + 1;
				}
			}
			else if (isConjunct(node, curr, rTags))
				isConjunctFound = true;
		}
		
		if (heads.isEmpty()) return false;
		
		if (eId - bId > 0)
		{
			if (isConjunctFound)
			{
				head = findHeadCoord(curr, rule, bId, eId, main_head);
				heads.add(head);
			}
			else
			{
				head = DSUtils.getLast(heads);
				
				for (i=bId; i<eId; i++)
					setPrimaryHead(curr.getChild(i), head);
			}
		}
		
		handleArgumentsInCoordination(curr, heads);
		return true;
	}
	
	/** Called by {@link #findHeadCoordination(CTNode, HeadRule)}. */
	private Pattern getConjunctPattern(CTNode curr, int sId, int size)
	{
		Pattern rTags = COORD_MAP.get(curr.getSyntacticTag());
		
		if (rTags != null)
		{
			boolean b = false;
			int i;
			
			for (i=sId; i<size; i++)
			{
				if (curr.getChild(i).isSyntacticTag(rTags))
				{
					b = true;
					break;
				}
			}
			
			if (!b)	rTags = PatternConst.ANY;
		}
		else
			rTags = PatternConst.ANY;
		
		return rTags;
	}
	
	/** Called by {@link #findHeadCoordination(CTNode, HeadRule)}. */
	private boolean isConjunct(CTNode C, CTNode P, Pattern rTags)
	{
		if (P.isSyntacticTag(PTBTag.C_SBAR) && C.isSyntacticTag(PREP_DET))
			return false;
		
		if (P.isSyntacticTag(PTBTag.C_UCP))
		{
			CTNode s = C.getRightNearestSibling();
			return s == null || PTBLib.isCoordinator(s);
		}
		
		if (rTags == PatternConst.ANY)
			return getSpecialLabel(C) == null;
		
		if (rTags.matcher(C.getSyntacticTag()).find())
		{
			if (P.isSyntacticTag(PTBTag.C_VP) && getAuxiliaryLabel(C) != null)
				return false;
			
			if (PTBLib.isMainClause(P) && C.isSyntacticTag(PTBTag.C_S) && hasAdverbialTag(C))
				return false;
			
			return true;
		}
		
		if (P.isSyntacticTag(PTBTag.C_NP))
			return C.isFunctionTag(PTBTag.F_NOM);
		
		return false;
	}
	
	/** Called by {@link #findHeadCoordination(CTNode, HeadRule)}. */
	private CTNode findHeadCoord(CTNode curr, HeadRule rule, int bId, int eId, CTNode prevHead)
	{
		CTNode currHead = (eId - bId == 1) ? curr.getChild(bId) : findHeadDefault(curr.getChildren(bId, eId), rule);
		
		if (prevHead != null)
		{
			String label = DSRTag.CONJ;
			if (isDiscourse(currHead)) label = DSRTag.DISC;
			else if (PTBLib.isPunctuation(currHead)) label = DSRTag.PUNCT;
			currHead.setPrimaryHead(prevHead, label);
		}
		
		return currHead;
	}
	
	private boolean handleArgumentsInCoordination(CTNode curr, List<CTNode> heads)
	{
		if (heads.size() <= 1) return false;
		if (!curr.isSyntacticTag(PTBTag.C_VP) && !curr.isFunctionTag(PTBTag.F_PRD)) return false;
		CTNode head = DSUtils.getLast(heads);

		for (CTNode child : curr.getChildren())
		{
			if (child.getPrimaryHead().isNode(head) && isCoordArgument(child))
			{
				for (int i=0; i<heads.size()-1; i++)
					child.addSecondaryHead(heads.get(i), child.getPrimaryLabel());	
			}
		}
		
		return true;
	}
	
	private boolean isCoordArgument(CTNode node)
	{
		return !(node.isPrimaryLabel(DSRTag.PUNCT));
	}
	
//	============================= Find Heads =============================

	private boolean findHeadHyphen(CTNode node)
	{
		int i, size = node.getChildrenSize();
		CTNode prev, hyph, next;
		boolean isFound = false;
		boolean isVP = node.isSyntacticTag(PTBTag.C_VP);
		
		for (i=0; i<size-2; i++)
		{
			prev = node.getChild(i);
			hyph = node.getChild(i+1);
			next = node.getChild(i+2);
			
			if (hyph.isSyntacticTag(PTBTag.P_HYPH))
			{
				prev.setPrimaryHead(next, DSRTag.COM);
				hyph.setPrimaryHead(next, DSRTag.PUNCT);
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
	private boolean findHeadApposition(CTNode curr)
	{
		if (!curr.isSyntacticTag(NP) || curr.containsChild(PTBLib::isNoun))
			return false;
		
		CTNode fst = curr.getFirstChild(n -> n.isSyntacticTag(PTBLib.NP_NML_WHNP));
		
		while (fst != null && fst.containsChild(n -> n.isSyntacticTag(PTBTag.P_POS)))
			fst = fst.getRightNearestSibling(n -> n.isSyntacticTag(PTBLib.NP_NML_WHNP));
		
		if (fst == null || fst.hasPrimaryHead()) return false;

		boolean hasAppo = false;
		CTNode snd = fst;
		
		while ((snd = snd.getRightNearestSibling()) != null)
		{
			if (snd.hasPrimaryHead()) continue;
			
			if ((snd.isSyntacticTag(NP) && !hasAdverbialTag(snd)) ||
				(snd.isFunctionTag(PTBTag.F_HLN, PTBTag.F_TTL)) ||
				(snd.isSyntacticTag(PTBTag.C_RRC) && snd.containsChild(PTBLib::isNominalPredicate)))
			{
				snd.setPrimaryHead(fst, DSRTag.APPOS);
				hasAppo = true;
			}
		}
		
		return hasAppo;
	}

	/**
	 * (SQ (VBD was)
     *     (S (NP-SBJ (PRP it))
     *        (NP-PRD (NP (DT the)
     *                    (NN reason))
     *                (PP-TMP (IN in)
     *                        (NP (DT the)
     *                            (NN past))))))
	 */
	public boolean findHeadSmallClause(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.C_S) && !node.containsChild(n -> n.isSyntacticTag(PTBTag.C_VP)))
		{
			CTNode sbj = node.getFirstChild(n -> n.isFunctionTag(PTBTag.F_SBJ));
			CTNode prd = node.getFirstChild(n -> n.isFunctionTag(PTBTag.F_PRD));
			
			if (sbj != null && prd != null)
			{
				CTNode parent = node.getParent();
				
				if (parent.isSyntacticTag(PTBTag.C_SQ))
				{
					CTNode vb = parent.getFirstChild(PTBLib::isVerb);
					
					// see the example above
					if (vb != null && !sbj.hasPrimaryHead())
						node.addFunctionTag(PTBTag.F_PRD);
				}

				node.addFunctionTag(DSRTag.OPRD);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean findHeadQuantifierPhrase(CTNode node)
	{
		if (!node.isSyntacticTag(PTBTag.C_QP)) return false;
		
		for (int i=1; i<node.getChildrenSize(); i++)
		{
			CTNode next = node.getChild(i);
			if (next.hasPrimaryHead()) continue;
			CTNode curr = node.getChild(i-1);
			
			if (curr.isSyntacticTag(PTBTag.P_JJR, PTBTag.P_RBR))
			{
				if (next != null && (next.isFormLowercase("than")))
					next.setPrimaryHead(curr, DSRTag.MWE);
			}
			else if (curr.isSyntacticTag(PTBTag.P_JJ))
			{
				if (next != null && (next.isFormLowercase("to")))
					next.setPrimaryHead(curr, DSRTag.MWE);
			}
			else if (curr.isSyntacticTag(PTBTag.P_NN))
			{
				if (next != null && (next.isFormLowercase("like")))
					next.setPrimaryHead(curr, DSRTag.MWE);
			}
			else if (curr.isSyntacticTag(PTBTag.P_IN))
			{
				if (next != null && next.isSyntacticTag(PTBTag.P_TO))
					next.setPrimaryHead(curr, DSRTag.MWE);
			}
		}
		
		return true;
	}
	
//	============================= Arrange Modals =============================
	
//	private void arrangeModals(CTTree tree)
//	{
//		arrangeModalsRec(tree.getRoot());
//	}
//	
//	private void arrangeModalsRec(CTNode node)
//	{
//		if (node.isSyntacticTag(PTBTag.C_VP))
//		{
//			CTNode vb = node.getFirstChild(PTBLib.M_MD_TO_VBx);
//			
//			if (vb != null && isModal(vb))
//			{
//				CTNode s = vb.getRightNearestSibling(PTBLib.M_S);
//				
//				if (s != null && s.getChildrenSize() == 2)
//				{
//					CTNode np = s.getChild(0);
//					CTNode vp = s.getChild(1);
//					
//					if (np.isEmptyCategoryPhrase() && vp.isSyntacticTag(PTBTag.C_VP))
//						node.replaceChild(s, vp);
//				}
//			}
//		}
//		
//		for (CTNode child : node.getChildren())
//			arrangeModalsRec(child);
//	}
//	
//	private boolean isModal(CTNode vb)
//	{
//		String lower = StringUtils.toLowerCase(vb.getForm());
//		if (vb.isLemma("have") || vb.isLemma("need") || vb.isLemma("dare") || vb.isLemma("ought") || lower.equals("used")) return true;
//		
//		if (lower.equals("going"))
//		{
//			CTNode vp = vb.getParent();
//			CTNode be = vp.getLeftNearestSibling(PTBLib.M_VBx);
//			return be != null && be.isLemma("be");
//		}
//		
//		return false;
//	}
//	
////	============================= Predicate Argument Structures =============================
//	
//	private Map<CTNode,Set<CTNode>> findArguments(CTTree tree)
//	{
//		Map<CTNode,Set<CTNode>> map = new HashMap<>();
//		findArgumentsRec(tree.getRoot(), map);
//		return map;
//	}
//	
//	/** Called by {@link #findArguments(CTTree)}. */
//	private void findArgumentsRec(CTNode node, Map<CTNode,Set<CTNode>> map)
//	{
//		if (node.isSyntacticTag(PTBTag.C_VP))
//			findArgumentsAux(node, map);
//		else
//			node.getChildren().forEach(n -> findArgumentsRec(n, map));
//	}
//	
//	/**
//	 * Called by {@link #findArgumentsRec(CTNode, Int2ObjectMap)}.
//	 * @param vp the highest verb phrase.
//	 */
//	private void findArgumentsAux(CTNode vp, Map<CTNode,Set<CTNode>> map)
//	{
//		List<CTNode> args = new ArrayList<>();
//		List<CTNode> auxs = new ArrayList<>();
//		CTNode p = vp.getParent();
//		
//		if (p.matches(PTBLib.M_Sx))
//		{
//			for (CTNode n : p.getChildren())
//			{
//				if (n == vp || PTBLib.isPunctuation(n.getSyntacticTag())) continue;
//				if (n.matches(PTBLib.M_MD_TO_VBx)) auxs.add(n);
//				else args.add(n);
//			}
//		}
//		
//		findArgumentsAux(vp, map, args, auxs);
//	}
//	
//	/** Called by {@link #findArgumentsAux(CTNode, Map)}. */
//	private void findArgumentsAux(CTNode vp, Map<CTNode,Set<CTNode>> map, List<CTNode> args, List<CTNode> auxs)
//	{
//		List<CTNode> vps = new ArrayList<>();
//		List<CTNode> vbs = new ArrayList<>();
//		List<CTNode> tmp = new ArrayList<>();
//		
//		for (CTNode n : vp.getChildren())
//		{
//			if (n.isSyntacticTag(PTBTag.C_VP))
//				vps.add(n);
//			else if (n.matches(PTBLib.M_MD_TO_VBx))
//				vbs.add(n);
//			else
//				tmp.add(n);
//		}
//		
//		if (vps.isEmpty())
//			vbs.forEach(vb -> initArguments(vb, map, args, auxs));
//		else
//		{
//			args.addAll(tmp);
//			auxs.addAll(vbs);
//		
//			if (vps.size() == 1) findArgumentsAux(DSUtils.getFirst(vps), map, args, auxs);
//			else for (CTNode n : vps) findArgumentsAux(n, map, new ArrayList<>(args), new ArrayList<>(auxs));
//		}
//	}
//	
//	private TVerb initArguments(CTNode vb, Map<CTNode,Set<CTNode>> map, List<CTNode> args, List<CTNode> auxs)
//	{
//		TVerb vt = new TVerb(vb.getLemma());
//		vt.setTense(getTense(vb, auxs));
//		
//		Pair<Aspect,Voice> p = getAspectAndVoice(vb, auxs);
//		vt.setAspect(p.o1);
//		vt.setVoice (p.o2);
//		
//		vt.setModals(getModals(auxs));
//		vt.setNegation(neg);
//		
//		return vt;
//	}
//	
//	private void initModals(List<CTNode> auxs)
//	{
//		CTNode curr, next;
//		
//		for (int i=0; i<auxs.size()-1; i++)
//		{
//			curr = auxs.get(i);
//			next = auxs.get(i+1);
//			
//			if (curr.isLemma("go"))
//			{
//				if (0 < i)
//				{
//					CTNode prev = auxs.get(i-1);
//					
//					if (prev.isLemma("be") && next.isLemma("to"))
//					{
//						prev.addFunctionTag(DSRTag.MWE);
//						next.addFunctionTag(DSRTag.MWE);
//						curr.addFunctionTag(DSRTag.MODAL);
//					}
//				}
//			}
//			else if (isSemiModal(curr) && next.isLemma("to"))
//			{
//				next.addFunctionTag(DSRTag.MWE);
//				curr.addFunctionTag(DSRTag.MODAL);
//			}
//		}
//	}
//	
//	private Tense getTense(CTNode vb, List<CTNode> auxs)
//	{
//		if (auxs.isEmpty()) return getTense(vb);
//		CTNode aux = DSUtils.getFirst(auxs);
//		
//		if (aux.isLemma("will") || aux.isLemma("shall"))
//			return Tense.future;
//		
//		for (int i=1; i<auxs.size()-1; i++)
//		{
//			aux = auxs.get(i);
//			CTNode prev = auxs.get(i-1);
//			CTNode next = auxs.get(i+1);
//
//			if (aux.isLemma("go") && prev.isLemma("be") && next.isLemma("to"))
//			{
//				prev.addFunctionTag(DSRTag.MWE);
//				next.addFunctionTag(DSRTag.MWE);
//				aux .addFunctionTag(DSRTag.MODAL);
//				return Tense.future;	
//			}
//		}
//		
//		return getTense(DSUtils.getFirst(auxs));
//	}
//	
//	private Tense getTense(CTNode vb)
//	{
//		switch (vb.getSyntacticTag())
//		{
//		case PTBTag.P_VBD: return Tense.past;
//		case PTBTag.P_VBP:
//		case PTBTag.P_VBZ: return Tense.present;
//		}
//		
//		return Tense.none;
//	}
//	
//	private Pair<Aspect,Voice> getAspectAndVoice(CTNode vb, List<CTNode> auxs)
//	{
//		if (auxs.isEmpty()) return new Pair<>(Aspect.none, Voice.active);
//		CTNode prog = null, perf = null, pass = null;
//		
//		if (vb.isSyntacticTag(PTBTag.P_VBG))
//		{
//			CTNode a = DSUtils.getLast(auxs);
//			
//			if (a.isLemma("be"))
//			{
//				prog = a;
//				
//				if (a.matches(PTBLib.M_VBD_VBN))
//				{
//					a = DSUtils.getLast(auxs, 1);
//					if (a != null && a.isLemma("have")) perf = a;
//				}
//			}
//		}
//		else if (vb.matches(PTBLib.M_VBD_VBN))
//		{
//			CTNode a = DSUtils.getLast(auxs);
//			
//			if (a.isLemma("have"))
//				perf = a;
//			else if (a.isLemma("be") || a.isLemma("become") || a.isLemma("get"))
//				pass = a;
//		}
//		
//		Aspect aspect = Aspect.none;
//		
//		if (perf != null && prog != null)
//			aspect = Aspect.perfect_progressive;
//		else if (perf != null)
//			aspect = Aspect.perfect;
//		else if (prog != null)
//			aspect = Aspect.progressive;
//		
//		Voice voice = (pass != null) ? Voice.passive : Voice.active;
//		return new Pair<>(aspect, voice);
//	}
//	
//	private void findModals(CTNode vb, List<CTNode> auxs)
//	{
//		Set<String> modals = new HashSet<>();
//		
//		for (int i=0; i<auxs.size(); i++)
//		{
//			CTNode aux = auxs.get(i);
//			
//			if (aux.isSyntacticTag(PTBTag.P_MD))
//			{
//				if (!aux.isLemma("will") && !aux.isLemma("shall"))
//					modals.add(aux.getLemma());
//			}
//			else if (isSemiModal(aux))
//			{
//				if (i+1 < auxs.size() && auxs.get(i+1).isLemma("to"))
//					modals.add(aux.getLemma());
//			}
//		}
//		
//		return modals;
//	}
//	
//	private boolean isSemiModal(CTNode vb)
//	{
//		return vb.isLemma("have") || vb.isLemma("need") || vb.isLemma("dare") || vb.isLemma("use") || vb.isLemma("go");
//	}
//	
//	private void addDependent(CTNode head, CTNode node, String label)
//	{
//		node.addDependencyHead(new CTArc(head, label));
//	}
	

// ============================= Get Labels =============================
	
	@Override
	protected String getDependencyLabel(CTNode C, CTNode p)
	{
		CTNode c = C.getC2DInfo().getNonTerminalHead();
		CTNode d = C.getC2DInfo().getTerminalHead();
		String label;

		// vocative
		if (C.isFunctionTag(PTBLib.F_VOC))
			return DSRTag.VOC;
		
		// dative
		if (C.isFunctionTag(PTBTag.F_DTV))
			return DSRTag.DATV;
				
		// adverbial clause/phrase
		if (hasAdverbialTag(C))
		{
			if (C.isSyntacticTag(ADVCL))
				return DSRTag.ADVCL;
			
			if (C.isSyntacticTag(NPMOD))
				return DSRTag.ADVNP;
		}
		
		// subject
		if ((label = getSubjectLabel(C, d)) != null)
			return label;
		
		// coordination
		if (C.isSyntacticTag(PTBTag.C_UCP))
		{
			c.addFunctionTags(C.getFunctionTags());
			return getDependencyLabel(c, p);
		}
		
		// complements of verbal predicates
		if (P.isSyntacticTag(COMPP))
		{
			if ((label = getObjectLabel(C))    != null)	return label;
			if ((label = getAuxiliaryLabel(C)) != null)	return label;
			if (isObjectPredicate(C))					return DSRTag.OPRD;
			if (isOpenClausalComplement(C))				return DSRTag.XCOMP;
			if (isClausalComplement(C))					return DSRTag.CCOMP;
			
		}
		
		// complements of adjectival/adverbial predicates
		if (P.isSyntacticTag(ACOMP))
		{
			if (isOpenClausalComplement(C))	return DSRTag.XCOMP;
			if (isClausalComplement(C))		return DSRTag.CCOMP;
		}

		// complements of nouns
		if (P.matches(PTBLib.M_NP_NML_WHNP))
		{
			if (isRelativeClause(C)) return DSRTag.RELCL;
			if (isNonFiniteClause(C) || isClausalComplement(C)) return DSRTag.ACL;
		}
		
		// possessive
		if (isPossive(C, P))
			return DSRTag.POSS;
		
		// simple labels
		if ((label = getSimpleLabel(C)) != null)
			return label;
			
		// default
		if (P.isSyntacticTag(PP))
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
		
		if (C.isSyntacticTag(PTBTag.C_SBAR) || isOpenClausalComplement(C) || (P.isSyntacticTag(PTBTag.C_PP) && PTBLib.isClause(C)))
			return DSRTag.ADVCL;
		
		if (C.isSyntacticTag(CCOMP))
			return DSRTag.CCOMP;
		
		if (P.isSyntacticTag(PTBTag.C_QP))
			return getQmodLabel(C);	// TODO: check with c
		
		if (P.isSyntacticTag(NMOD_PARENT) || PTBLib.isNoun(p.getSyntacticTag()))
			return getNmodLabel(C, d);
		
		if (c != null)
		{
			if ((label = getSimpleLabel(c)) != null)
				return label;
			
			if (d.isSyntacticTag(PTBTag.P_IN))
				return DSRTag.CASE;
			
			if (PTBLib.isAdverb(d.getSyntacticTag()))
				return DSRTag.ADV;
		}
		
		if ((P.isSyntacticTag(ADVP) || PTBLib.isAdjective(p.getSyntacticTag()) || PTBLib.isAdverb(p.getSyntacticTag())))
		{
			if (C.isSyntacticTag(NPMOD) || PTBLib.isNoun(C.getSyntacticTag()))
				return DSRTag.ADVNP;
			
			return DSRTag.ADV;
		}
		
		if (d.hasC2DInfo() && (label = d.getC2DInfo().getLabel()) != null)
			return label;
		
		return DSRTag.DEP;
	}
	
	private boolean hasAdverbialTag(CTNode node)
	{
		return node.isFunctionTag(PTBTag.F_ADV) || DSUtils.hasIntersection(node.getFunctionTags(), SEM_TAGS);
	}
	
	private String getSubjectLabel(CTNode C, CTNode d)
	{
		if (C.isFunctionTag(PTBTag.F_SBJ))
		{
			if (C.isFunctionTag(DSRTag.EXPL) ||  d.isSyntacticTag(PTBTag.P_EX) || d.isFormIgnoreCase("there"))
				return DSRTag.EXPL;
			else if (PTBLib.isClause(C))
				return isOpenClausalComplement(C) ? XSUBJ : DSRTag.CSUBJ;
			else
				return DSRTag.NSUBJ;
		}
		else if (C.isFunctionTag(PTBTag.F_LGS))
		{
			if (C.containsChild(PTBLib.M_Sx))
				return CLGS;
			else
				return NLGS;
		}
		
		return null;
	}
	
	private String getObjectLabel(CTNode node)
	{
		return node.isSyntacticTag(NP) ? DSRTag.DOBJ : null;
	}
	
	private String getAuxiliaryLabel(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.P_MD))
			return DSRTag.AUX;
		
		if (node.isSyntacticTag(PTBTag.P_TO))
			return DSRTag.MARK;

		if (PTBLib.isVerb(node.getSyntacticTag()))
		{
			if (node.getRightNearestSibling(PTBLib.M_PRD) != null)
				return DSRTag.COP;
			
			CTNode vp;
			
			if ((vp = node.getRightNearestSibling(PTBLib.M_VP)) != null)
			{
				if (ENUtils.isPassiveAuxiliaryVerb(node.getForm()))
				{
					if (vp.containsChild(PTBLib.M_VBD_VBN))
						return AUXP;
					
					if (!vp.containsChild(PTBLib.M_VBx) && (vp = vp.getFirstChild(PTBLib.M_VP)) != null && vp.containsChild(PTBLib.M_VBD_VBN))
						return AUXP;
				}
				
				return DSRTag.AUX;	
			}
		}
		
		return null;
	}
	
	private boolean isObjectPredicate(CTNode curr)
	{
		if (curr.isFunctionTag(DSRTag.OPRD))
			return true;
		
		if (curr.isSyntacticTag(PTBTag.C_S) && !curr.containsChild(PTBLib.M_VP) && curr.containsChild(PTBLib.M_PRD))
		{
			CTNode sbj = curr.getFirstChild(PTBLib.M_SBJ);
			return sbj != null && sbj.isEmptyCategoryPhrase();
		}
		
		return false;
	}
	
	private boolean isOpenClausalComplement(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.C_S))
		{
			CTNode sbj = node.getFirstChild(PTBLib.M_SBJ);
			
			if (node.containsChild(PTBLib.M_VP) && (sbj == null || sbj.isEmptyCategoryPhrase()))
				return true;
		}
		else if (node.isFunctionTag(DSRTag.RELCL))
		{
			CTNode s = node.getFirstChild(PTBLib.M_S);
			if (s != null)	return isOpenClausalComplement(s);
		}

		return false;
	}
	
	private boolean isClausalComplement(CTNode node)
	{
		if (node.isSyntacticTag(CCOMP))
			return true;
		
		if (node.isSyntacticTag(PTBTag.C_SBAR))
		{
			CTNode mark;
			
			if ((mark = node.getFirstChild(PTBLib.C_NONE)) != null && mark.isForm(PTBTag.E_ZERO))
				return true;
			
			if ((mark = node.getFirstChild(PTBLib.M_IN_DT_TO)) != null)
			{
				mark.getC2DInfo().setLabel(DSRTag.MARK);
				return true;
			}
			
			if (node.isFunctionTag(DSRTag.RELCL) || node.containsChild(PTBLib.M_WHx))
				return true;
		}
		
		return false;
	}
	
	private boolean isRelativeClause(CTNode curr)
	{
		return curr.isSyntacticTag(PTBTag.C_RRC) || curr.isFunctionTag(DSRTag.RELCL) || (curr.isSyntacticTag(PTBTag.C_SBAR) && curr.containsChild(PTBLib.M_WHx));
	}
	
	private boolean isNonFiniteClause(CTNode curr)
	{
		return isOpenClausalComplement(curr) || curr.isSyntacticTag(PTBTag.C_VP);
	}
	
	private boolean isDeterminer(CTNode curr)
	{
		return curr.isSyntacticTag(DET);
	}
	
	private boolean isNum(CTNode curr)
	{
		return curr.isSyntacticTag(PTBTag.P_CD) || "0".equals(curr.getLemma());
	}
	
	private String getSimpleLabel(CTNode C)
	{
		String label;
		
		if (C.isSyntacticTag(PP))
			return DSRTag.CASE;
		
		if (PTBLib.isCorrelativeConjunction(C) || PTBLib.isConjunction(C))
			return DSRTag.CC;
		
		if (isPrt(C))
			return DSRTag.PRT;

		if ((label = getSpecialLabel(C)) != null)
			return label;
		
		return null;
	}
	
	private String getSpecialLabel(CTNode C)
	{
		CTNode d = C.getC2DInfo().getTerminalHead();
		
		if (PTBLib.isPunctuation(C.getSyntacticTag()) || PTBLib.isPunctuation(d.getSyntacticTag()))
			return DSRTag.PUNCT;
		
		if (isDiscourse(C))
			return DSRTag.DISC;
		
		if (C.isSyntacticTag(PTBTag.C_QP) || isNum(d))
			return DSRTag.NUM;
		
		if (isMeta(C))
			return DSRTag.META;
		
		if (isPrn(C))
			return DSRTag.PARAT;

		if (isAdverb(C))
			return DSRTag.ADV;
		
		return null;
	}
	
	private String getNmodLabel(CTNode C, CTNode d)
	{
		if (isDeterminer(C))
			return DSRTag.DET;

		if (C.isSyntacticTag(PTBTag.P_POS))
			return DSRTag.CASE;
		
		return DSRTag.NDEP;
	}
	
	private String getPmodLabel(CTNode C, CTNode d)
	{
		if (C.isSyntacticTag(NP) || PTBLib.isRelativizer(d.getSyntacticTag()))
			return DSRTag.POBJ;
		else
			return DSRTag.PCOMP;	
	}
	
	private String getQmodLabel(CTNode C)
	{
		if (isDeterminer(C))
			return DSRTag.DET;
		
		if (isMeta(C))
			return DSRTag.META;
		
		return DSRTag.QDEP;
	}
	
	private boolean isAdverb(CTNode C)
	{
		if (C.isSyntacticTag(PTBTag.C_ADVP) || PTBLib.isAdverb(C.getSyntacticTag()))
		{
			CTNode P = C.getParent();
			
			if (P.isSyntacticTag(PP) && C.getRightNearestSibling() == null && C.getLeftNearestSibling().isSyntacticTag(PREP))
				return false;

			return true;
		}
		
		return false;
	}
	
	private boolean isDiscourse(CTNode C)
	{
		CTNode d = C.getC2DInfo().getTerminalHead();
		return isInterjection(C) || isInterjection(d) || emoticon.isEmoticon(d.getForm());
	}
	
	private boolean isInterjection(CTNode node)
	{
		return node.isSyntacticTag(INTJ);
	}
	
	private boolean isMeta(CTNode node)
	{
		return node.isSyntacticTag(META) || node.isForm("%");
	}
	
	private boolean isPrn(CTNode node)
	{
		return node.isSyntacticTag(PTBTag.C_PRN);
	}
	
	private boolean isPrt(CTNode curr)
	{
		return curr.isSyntacticTag(PRT);
	}
	
	private boolean isPossive(CTNode curr, CTNode parent)
	{
		if (curr.isSyntacticTag(POSS))
			return true;
		
		if (parent.isSyntacticTag(POSS_PARENT))
			return curr.containsChild(PTBLib.M_POS);
		
		return false;
	}
	
	private boolean isNegation(CTNode node)	// TODO: use
	{
		if (node.isTerminal() && node.getForm() != null && ENUtils.isNegation(node.getForm()))
			return true;
		
		if (node.getChildrenSize() == 2 && "no longer".equals(StringUtils.toLowerCase(node.toForms())))
			return true;
		
		return false;
	}
	
//	private boolean isAmod(CTNode node)
//	{
//		return node.isConstituentTagAny(S_ADJT_PHRASE) || CTLibEn.isAdjective(node);
//	}
//
//	protected boolean isInfMod(CTNode curr)
//	{
//		CTNode vp = curr.isConstituentTag(PTBTag.C_VP) ? curr : curr.getFirstDescendant(PTBLib.M_VP);
//		
//		if (vp != null)
//		{
//			CTNode vc = vp.getFirstChild(PTBLib.M_VP);
//			
//			while (vc != null)
//			{
//				vp = vc;
//				
//				if (vp.getLeftNearestSibling(PTBLib.M_TO) != null)
//					return true;
//				
//				vc = vp.getFirstChild(PTBLib.M_VP);
//			}
//			
//			return vp.containsChild(PTBLib.M_TO);
//		}
//		
//		return false;
//	}
//	
//	private final Set<String> MARK  = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_TO, PTBTag.P_DT);
//	private final Set<String> MARK_WORDS  = Sets.newHashSet("that", "if", "whether");
//	
//	private boolean isMark(CTNode curr)
//	{
//		return MARK_WORDS.contains(StringUtils.toLowerCase(curr.getWordForm()));
//	}
	
// ============================= Get a dependency graph =============================
	
	private NLPNode[] getDependencyGraph(CTTree cTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
	{
		NLPNode[] dTree = initDependencyGraph(cTree);
		addDEPHeads(dTree, cTree);
		
		if (NLPUtils.containsCycle(dTree))
			throw new UnknownFormatConversionException("Cyclic depedency relation.");

		addSecondaryCoord(dTree);
		deepLabel(dTree);
		addSecondaryHeads(dTree, xsubj, rnr);
		addFeats(dTree, cTree, cTree.getRoot());
		labelCompounds(dTree);
		postProcess(dTree);
		addCoordArguments(dTree, cTree);
		
		if (cTree.hasPropBank())
			addSemanticHeads(dTree, cTree);
		
		if (cTree.hasNamedEntity())
			addNamedEntities(dTree, cTree);
		
		return getDEPTreeWithoutEdited(cTree, dTree);
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
				dNode.setParent(dTree[0], DSRTag.ROOT);
				rootCount++;
			}
			else
			{
				label = cNode.getC2DInfo().getLabel();
				dNode.setParent(dTree[headId], label);
			}
			
			if ((ante = cNode.getAntecedent()) != null)
				dNode.addSecondaryHead(getNLPNode(dTree, ante), DSRTag.DEP2_REF);
		}
		
		return rootCount;
//		if (rootCount > 1)	System.err.println("Warning: multiple roots exist");
	}
	
	private void addCoordArguments(NLPNode[] dTree, CTTree cTree)
	{
		int currId, size = dTree.length;
		NLPNode dNode;
		CTNode  cNode;
		
		for (currId=1; currId<size; currId++)
		{
			dNode = dTree[currId];
			cNode = cTree.getToken(currId-1);
			
			if (cNode.getParent().isSyntacticTag(PTBLib.C_PP))
				dNode = dNode.getParent();
			
			for (CTNode cHead : cNode.getC2DInfo().getSecondaryHeads())
			{
				int headId = cHead.getTokenID()+1;
				
				if (headId > 0)
				{
					NLPNode dHead = dTree[headId];
					if (!dNode.isChildOf(dHead))
						dNode.addSecondaryHead(dHead, dNode.getDependencyLabel());
				}
			}
		}
	}
	
	private void deepLabel(NLPNode[] tree)
	{
		for (int i=1; i<tree.length; i++)
		{
			NLPNode node = tree[i];
			
			if (node.isDependencyLabel(DSRTag.ADV))
			{
				if (ENUtils.isNegation(node.getForm()))
					changeLabel(node, DSRTag.NEG);
			}
			else if (node.isDependencyLabel(AUXP))
			{
				NLPNode head = node.getParent();
				changeLabel(node, DSRTag.AUX);
				
				for (NLPNode sib : head.getChildren())
				{
					switch (sib.getDependencyLabel())
					{
					case DSRTag.NSUBJ: changeLabel(sib, DSRTag.DOBJ);  break;
					case DSRTag.CSUBJ: changeLabel(sib, DSRTag.CCOMP); break;
					case        XSUBJ: changeLabel(sib, DSRTag.XCOMP); break;
					}
				}
			}
			else if (node.isDependencyLabel(DSRTag.EXPL))
			{
				NLPNode head = node.getParent();
				NLPNode d = head.getFirstChildByDependencyLabel(DSRTag.DOBJ);
				
				if (d != null && node.getFormLowercase().equals("there"))
					changeLabel(d, DSRTag.NSUBJ);
				
				d = head.getFirstChildByDependencyLabel(DSRTag.CCOMP);
				if (d == null) d = head.getFirstChildByDependencyLabel(DSRTag.XCOMP);
				if (d != null) changeLabel(d, DSRTag.CSUBJ);
			}
			else if (node.isDependencyLabel(DSRTag.ACL))
			{
				addDOBJinRRC(node, node.getParent());
			}
			else if (node.isDependencyLabel(DSRTag.OPRD))
			{
				NLPNode nsubj = node.getFirstChildByDependencyLabel(DSRTag.NSUBJ);
				if (nsubj != null) nsubj.setParent(node.getParent(), DSRTag.DOBJ);
			}
			
			List<NLPNode> list = node.getChildrenByDependencyLabel(DSRTag.DOBJ);
			
			if (list.size() > 1 || (list.size() == 1 && node.hasChildByDepenencyLabel(DSRTag.CCOMP)))
				changeLabel(list.get(0), DSRTag.DATV);
		}
		
		for (int i=1; i<tree.length; i++)
		{
			NLPNode node = tree[i];
			
			switch (node.getDependencyLabel())
			{
			case XSUBJ: changeLabel(node, DSRTag.CSUBJ); break;
			case NLGS : changeLabel(node, DSRTag.NSUBJ); break;
			case CLGS : changeLabel(node, DSRTag.CSUBJ); break;
			}
		}
	}
	
	private void changeLabel(NLPNode sib, String label)
	{
		for (DEPArc<NLPNode> d : sib.getSecondaryHeadList())
			if (d.isLabel(sib.getDependencyLabel())) d.setLabel(label);
		
		sib.setDependencyLabel(label);
	}
	
	private void addDOBJinRRC(NLPNode node, NLPNode head)
	{
		if (node != null && node.isSyntacticTag(PTBLib.P_VBN))
		{
			head.addSecondaryHead(node, DSRTag.DOBJ);
			addDOBJinRRC(node.getFirstChildByDependencyLabel(DSRTag.CONJ), head);			
		}
	}
	
	private void postProcess(NLPNode[] nodes)
	{
		for (int i=1; i<nodes.length; i++) postProcess(nodes[i]);
	}
	
	private boolean postProcess(NLPNode node)
	{
		// flip PP
		if (node.isSyntacticTag(PTBTag.P_IN) && !(node.hasChildByDepenencyLabel(DSRTag.COP) || node.isDependencyLabel(DSRTag.OPRD)))
		{
			NLPNode pdep = node.getFirstChildByDependencyLabel(DSRTag.POBJ);
			if (pdep == null) pdep = node.getFirstChildByDependencyLabel(DSRTag.PCOMP);
			
			if (pdep == null)
			{
				NLPNode in = node.getLeftMostChild(PTBTag.P_IN, (n,s) -> n.isSyntacticTag(s));
				if (in == null || in.getChildren().isEmpty()) return false;
				node.adaptDependents(in);
				return postProcess(node);
			}
			
			String label = (node.isDependencyLabel(DSRTag.CASE)) ? DSRTag.PPMOD : node.getDependencyLabel();
			pdep.setParent(node.getParent(), label);
			pdep.setFeatMap(node.getFeatMap());
			pdep.adaptDependents(node);
			node.setParent(pdep, DSRTag.CASE);
			node.setFeatMap(new FeatMap());
			return true;
		}
		// dative
		else if (PTBTag.F_BNF.equals(node.getFeat(NLPUtils.FEAT_SEM)))
		{
			NLPNode head = node.getParent();
			if (PTBLib.isVerb(head.getSyntacticTag()) && transfer_verbs.contains(head.getLemma()))
				node.setDependencyLabel(DSRTag.DATV);
		}

		return false;
	}
	
// ============================= Compounds =============================

	private void labelCompounds(NLPNode[] tree)
	{
		labelCompounds(tree,
				head -> head.getSyntacticTag().startsWith(PTBTag.P_NN) && (!head.getParent().getSyntacticTag().startsWith(PTBTag.P_NN) || head.isDependencyLabel(DSRTag.CONJ)),
				node -> node.getSyntacticTag().startsWith(PTBTag.P_NN) && (node.isDependencyLabel(DSRTag.NDEP) || node.isDependencyLabel(DSRTag.DEP) || node.getSyntacticTag().startsWith(PTBTag.P_NNP)));
		
		labelCompounds(tree, 
				head -> isNumber(head) && (!isNumber(head.getParent()) || head.isDependencyLabel(DSRTag.CONJ)),
				node -> isNumber(node) && (node.isDependencyLabel(DSRTag.QDEP) || node.isDependencyLabel(DSRTag.NUM) || node.isDependencyLabel(DSRTag.DEP)));
		
		for (int i=1; i<tree.length; i++)
		{
			NLPNode node = tree[i];
			if (node.isDependencyLabel(DSRTag.QDEP) && node.isLemma(MetaConst.ORDINAL) && node.getParent().isSyntacticTag(PTBTag.P_CD))
				node.setDependencyLabel(DSRTag.COM);
		}
	}
	
	private void labelCompounds(NLPNode[] tree, Predicate<NLPNode> p1, Predicate<NLPNode> p2)
	{
		NLPNode node, head;
		int i, j;
		
		for (i=tree.length-1; i>0; i--)
		{
			head = tree[i];
			
			if (p1.test(head))
			{
				for (j=i-1; j>0; j--)
				{
					node = tree[j];
					
					if (node.isDescendantOf(head) && node.getParent().getTokenID() > node.getTokenID() && p2.test(node))
					{
						node.setDependencyLabel(DSRTag.COM);
						i = j;
					}
					else if (node.isSyntacticTag(PTBTag.P_HYPH) && !node.isDependencyLabel(DSRTag.CC))
						continue;
					else
						break;
				}
			}
		}
	}
	
	private boolean isNumber(NLPNode node)
	{
		return node.isSyntacticTag(PTBLib.P_CD) || node.isLemma("0") || node.isLemma(MetaConst.CARDINAL) || node.isLemma(MetaConst.ORDINAL);
	}
	
// ============================= Secondary Dependencies =============================

	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addSecondaryHeads(NLPNode[] dTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
	{
		for (CTNode curr : xsubj.keySet())
		{
			if (curr.hasC2DInfo())
				addSecondaryHeadsAux(dTree, curr, xsubj.get(curr), DSRTag.DEP2_XSUBJ);
		}
		
		for (CTNode curr : rnr.keySet())
		{
			if (curr.getParent() == null)
				continue;
			
			if (curr.getParent().getC2DInfo().getNonTerminalHead() != curr)
				addSecondaryHeadsAux(dTree, curr, rnr.get(curr), DSRTag.DEP2_RNR);
			else
				addSecondaryChildren(dTree, curr, rnr.get(curr), DSRTag.DEP2_RNR);
		}
	}
	
	private void addSecondaryCoord(NLPNode[] dTree)
	{
		for (int i=1; i<dTree.length; i++)
		{
			NLPNode node = dTree[i];
			
			if (isPredicate(node))
			{
				NLPNode sbj = getSubject(node);
				if (sbj != null) addSecondaryCoord(node.getFirstChildByDependencyLabel(DSRTag.CONJ), sbj);
			}
		}
	}
	
	private void addSecondaryCoord(NLPNode conj, NLPNode sbj)
	{
		if (conj != null && isPredicate(conj))
		{
			if (getSubject(conj) == null)
				sbj.addSecondaryHead(conj, sbj.getDependencyLabel());
			
			addSecondaryCoord(conj.getFirstChildByDependencyLabel(DSRTag.CONJ), sbj);
		}
	}
	
	private boolean isPredicate(NLPNode node)
	{
		return PTBLib.isVerb(node.getSyntacticTag()) || PTBTag.F_PRD.equals(node.getFeat(NLPUtils.FEAT_SYN));
	}
	
	private NLPNode getSubject(NLPNode node)
	{
		NLPNode sbj = node.getFirstChildByDependencyLabel(DSRTag.NSUBJ);
		return (sbj != null) ? sbj : node.getFirstChildByDependencyLabel(DSRTag.CSUBJ);	
	}
	
	/** Called by {@link #addSecondaryHeads(DEPTree)}. */
	private void addSecondaryHeadsAux(NLPNode[] dTree, CTNode cNode, Deque<CTNode> dq, String label)
	{
		if (cNode.isEmptyCategoryPhrase()) return;
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
			
			if (!node.isChildOf(head)) node.addSecondaryHead(head, label);
			
			if (label.equals(DSRTag.DEP2_XSUBJ) && head.isDependencyLabel(DSRTag.CCOMP))
				head.setDependencyLabel(DSRTag.XCOMP);
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
			
			if (node == null || node.getTokenID() == 0)
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
		
		if (!cNode.isEmptyCategoryPhrase() && cNode.getGapIndex() != -1 && cNode.getParent().getGapIndex() == -1 && (ante = cTree.getAntecedent(cNode.getGapIndex())) != null)
		{
			NLPNode dNode = getNLPNode(dTree, cNode);
			dNode.addSecondaryHead(getNLPNode(dTree, ante), DSRTag.DEP2_GAP);
		}
		
		if ((feat = getFunctionTags(cNode, SEM_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);
		
		if ((feat = getFunctionTags(cNode, SYN_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SYN, feat);

		for (CTNode child : cNode.getChildren())
			addFeats(dTree, cTree, child);
	}
	
	/** Called by {@link #addFeats(DEPTree, CTTree, CTNode)}. */
	private String getFunctionTags(CTNode node, Set<String> sTags)
	{
		List<String> tags = new ArrayList<>();
		
		for (String tag : node.getFunctionTags())
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
		if (cNode.isSyntacticTag(CTTag.TOP)) return null;
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
			
		for (int i=1; i<dTree.length; i++)
		{
			NLPNode node = dTree[i];
			
			if (!set.contains(node.getTokenID()))
			{
				removeEditedHeads(node.getSecondaryHeadList(), set);
				removeEditedHeads(node.getSemanticHeadList() , set);
				node.setTokenID(id++);
				nodes.add(node);
			}
		}
		
		return (nodes.size() > 0) ? NLPUtils.toDependencyTree(nodes, NLPNode::new) : null;
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private void addEditedTokensAux(CTNode curr, Set<Integer> set)
	{
		for (CTNode child : curr.getChildren())
		{
			if (PTBLib.isEditedPhrase(child))
			{
				for (CTNode sub : child.getTokens())
					set.add(sub.getTokenID()+1);
			}
			else if (!child.isTerminal())
				addEditedTokensAux(child, set);
		}
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private <T extends Arc<NLPNode>>void removeEditedHeads(List<T> heads, Set<Integer> set)
	{
		if (heads == null) return;
		List<T> remove = new ArrayList<>();
		
		for (T arc : heads)
		{
			if (arc.getNode() == null || set.contains(arc.getNode().getTokenID()))
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
			if (cNode.isPredicate())
				dNode.putFeat(NLPUtils.FEAT_PREDICATE, cNode.getFrameID());
			
			NLPNode sHead, d;
			String  label;
			CTNode  c;
			
			for (CTArc p : cNode.getSemanticHeads())
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
		
		for (CTNode child : cNode.getChildren())
			initPropBank(dTree, child);
	}
	
	/** Called by {@link #initPropBank(DEPTree, CTNode)}. */
	private CTNode getReferentArgument(CTNode node)
	{
		CTNode ref;
		
		if ((ref = PTBLib.getWhPhrase(node)) != null)
			return ref;
		
		if (node.isSyntacticTag(PTBTag.C_PP))
		{
			for (CTNode child : node.getChildren()) 
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
		NLPNode dHead = dNode.getParent();
		
		while (dHead.getTokenID() != 0)
		{
			if (dHead.isArgumentOf(sHead, label))
				return true;
			
			dHead = dHead.getParent();
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
		
	public void addNamedEntities(NLPNode[] dTree, CTTree cTree)
	{
		for (CTNode node : cTree.getTokens())
			dTree[node.getTokenID()+1].setNamedEntityTag(node.getNamedEntityTag());
	}
}