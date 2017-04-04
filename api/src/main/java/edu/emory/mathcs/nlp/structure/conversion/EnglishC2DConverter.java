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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.ENUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.structure.constituency.CTArc;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRule;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.dependency.NLPArc;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;
import edu.emory.mathcs.nlp.structure.util.DDGTag;
import edu.emory.mathcs.nlp.structure.util.FeatMap;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
import edu.emory.mathcs.nlp.structure.util.PTBTag;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishC2DConverter extends C2DConverter
{
	private final Set<String> COMPP       = Sets.newHashSet(PTBTag.C_VP, PTBTag.C_SINV, PTBTag.C_SQ, PTBTag.C_PP, PTBTag.C_WHPP);
	private final Set<String> CCOMP       = Sets.newHashSet(PTBTag.C_S, PTBTag.C_SQ, PTBTag.C_SINV, PTBTag.C_SBARQ);
	private final Set<String> ADVCL       = Sets.newHashSet(PTBTag.C_S, PTBTag.C_SBAR, PTBTag.C_SINV);
	private final Set<String> ADVNP       = Sets.newHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_QP);
	private final Set<String> META        = Sets.newHashSet(PTBTag.C_EDITED, PTBTag.C_EMBED, PTBTag.C_LST, PTBTag.C_META, PTBTag.P_CODE, PTBTag.C_CAPTION, PTBTag.C_CIT, PTBTag.C_HEADING, PTBTag.C_TITLE, PTBTag.P_DOLLAR);
	private final Set<String> POSS        = Sets.newHashSet(PTBTag.P_PRPS, PTBTag.P_WPS);
	private final Set<String> INTJ        = Sets.newHashSet(PTBTag.C_INTJ, PTBTag.P_UH);
	private final Set<String> PRT         = Sets.newHashSet(PTBTag.C_PRT, PTBTag.P_RP);
	private final Set<String> DET         = Sets.newHashSet(PTBTag.P_DT, PTBTag.P_WDT, PTBTag.P_WP, PTBTag.P_PDT);
	private final Set<String> NP          = Sets.newHashSet(PTBTag.C_NP, PTBTag.C_NML);
	private final Set<String> PP          = Sets.newHashSet(PTBTag.C_PP, PTBTag.C_WHPP);
	private final Set<String> ADVP        = Sets.newHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP, PTBTag.C_PP);
	private final Set<String> PREP        = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_TO);
	private final Set<String> ACOMPP      = Sets.newHashSet(PTBTag.C_ADJP, PTBTag.C_ADVP);
	private final Set<String> PREP_DET    = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_DT);
	private final Set<String> NMOD_PARENT = Sets.newHashSet(PTBTag.C_NML, PTBTag.C_NP, PTBTag.C_NX, PTBTag.C_WHNP);
	private final Set<String> POSS_PARENT = Sets.newHashSet(PTBTag.C_NP, PTBTag.C_NML, PTBTag.C_WHNP, PTBTag.C_QP, PTBTag.C_ADJP);
	private final Set<String> MARK        = Sets.newHashSet(PTBTag.P_IN, PTBTag.P_DT, PTBTag.P_TO);
	private final Set<String> MD_VBx      = Sets.newHashSet(PTBTag.P_MD, PTBTag.P_VB, PTBTag.P_VBP, PTBTag.P_VBZ, PTBTag.P_VBD, PTBTag.P_VBG, PTBTag.P_VBN);

//	/** Syntactic function tags. */
//	private final Set<String> SYN_TAGS = Sets.newHashSet(PTBTag.F_ADV, PTBTag.F_CLF, PTBTag.F_CLR, PTBTag.F_DTV, PTBTag.F_NOM, PTBTag.F_PUT, PTBTag.F_PRD, PTBTag.F_TPC);
	/** Semantic function tags. */
	private final Set<String> SEM_TAGS = Sets.newHashSet(PTBTag.F_BNF, PTBTag.F_DIR, PTBTag.F_EXT, PTBTag.F_LOC, PTBTag.F_MNR, PTBTag.F_PRP, PTBTag.F_TMP);
	/** Mappings between phrasal/clausal tags and phrasal/pos tags for coordination. */
	private final Map<String,Pattern> COORD_MAP = initCoordMap();
	/** Feats */
	private final Set<String> FEATS = Sets.newHashSet(PTBTag.F_CLR, PTBTag.F_DIR, PTBTag.F_EXT, PTBTag.F_LOC, PTBTag.F_MNR, PTBTag.F_PRP, PTBTag.F_TMP);
	
	// lexicons
	private final MorphAnalyzer analyzer;
	private final Emoticon      emoticon;
	private final Set<String>   eventive_nouns;
	private final Set<String>   light_verbs = Sets.newHashSet("make", "take", "have", "do", "give", "keep");
	
	private final String NLGS  = "NLGS";
	private final String CLGS  = "CLGS";
	
//	======================== Constructors ========================

	public EnglishC2DConverter()
	{
		this(new HeadRuleMap(IOUtils.getInputStreamsFromResource("edu/emory/mathcs/nlp/conversion/en-headrules.txt")),
			IOUtils.readSet(IOUtils.getInputStreamsFromResource("edu/emory/mathcs/nlp/conversion/en-eventive-nouns.txt")));
	}
	
	public EnglishC2DConverter(HeadRuleMap headrules, Set<String> eventive_nouns)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));
		this.analyzer = new EnglishMorphAnalyzer();
		this.emoticon = new Emoticon();
		this.eventive_nouns = eventive_nouns;
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
		setHead(tree.getRoot());
		postprocess(tree);
		finalizeDependencies(tree.getRoot());
		NLPGraph graph = createDependencyGraph(tree);
		relabel(tree, graph);
		addFeats(tree, graph);
		validate(tree, graph);
		return graph;
	}
	
	@Override
	protected void findHead(CTNode node, HeadRule rule)
	{
		if (PTBLib.isSecondaryPredicate(node))
		{
			if (node.isSyntacticTag(PTBTag.C_PP))// && !node.isFunctionTag(DDGTag.OPRD))
				rule = headrule_map.get("PPP");
		}
		
		CTNode head = findHeadCoordination(node, rule);
		
		if (head == null)
		{
			findHeadApposition(node);
			findHeadQuantifierPhrase(node);
			head = findHeadDefault(node.getChildren(), rule);
		}
		
		node.setPhraseHead(head);
	}
	
	@Override
	protected int getHeadFlag(CTNode node)
	{
		if (node.hasPrimaryHead())
			return -1;
		
		if (node.isEmptyCategoryBranch() || PTBLib.isPunctuation(node) || node.isFunctionTag(PTBTag.F_VOC))
			return 3;
		
		if (isMeta(node) || node.isSyntacticTag(INTJ) || node.isSyntacticTag(PTBTag.C_PRN))
			return 2;
		
		if (node.isFunctionTag(PTBTag.F_PRD))
			return 0;
		
		if (hasAdverbialTag(node))
			return 1;
		
		return 0;
	}

//	============================= Pre-process =============================
	
	public void preprocess(CTTree tree)
	{
		lemmatize(tree);
		tree.flatten().forEach(node -> preprocess(tree, node));
		preprocessDuplicates(tree);
		tree.getTerminals().stream().forEach(n -> preprocessEmptyCategory(tree, n));
		tree.getRoot().getTerminals().stream().filter(n -> removeEmptyCategoriesAux(n)).forEach(n -> n.removeSelf());
	}
	
	private boolean removeEmptyCategoriesAux(CTNode node)
	{
		return node.isEmptyCategory() && !(node.hasAntecedent() || PTBLib.isNullComplementizer(node));
	}
	
	public void lemmatize(CTTree tree)
	{
		for (CTNode token : tree.getTokens())
			analyzer.setLemma(token);
	}
	
	private void preprocess(CTTree tree, CTNode node)
	{
		preprocessSQ(node);
		preprocessModalVerb(node);
		preprocessModalAdjective(node);
		preprocessQuantifierPhrase(node);
	}
	
	public CTNode preprocessModalVerb(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.C_VP))
		{
			CTNode vb = node.getFirstChild(n -> n.isSyntacticTag(MD_VBx));
			
			if (vb != null && !isPassiveVerb(vb))
			{
				CTNode s = vb.getRightNearestSibling(n -> n.isSyntacticTag(PTBTag.C_S));
				
				if (s != null && s.getChildrenSize() == 2)
				{
					CTNode np = s.getChild(0);
					CTNode vp = s.getChild(1);
					
					if (PTBLib.isNominalSubject(np) && np.isEmptyCategoryPhrase() && (PTBLib.isVerbPhrase(vp) || PTBLib.isSecondaryPredicate(vp)))
					{
						CTNode ec = np.getFirstTerminal();
						
						if (PTBLib.isPassiveNull(ec) && ec.hasAntecedent())
						{
							CTNode sbj = ec.getAntecedent();
							CTNode tmp = node.getHighestChainedAncestor(PTBLib::isVerbPhrase);
							if (tmp == null) tmp = node;
							
							if (sbj == tmp.getLeftNearestSibling(PTBLib::isNominalSubject))
							{
								s.addFunctionTag(PTBTag.F_PRD);
								vb.setPrimaryLabel(DDGTag.RAISE);
								return vb;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public boolean isPassiveVerb(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.P_VBN) && node.hasParent(PTBLib::isVerbPhrase))
		{
			CTNode vp = node.getParent();
			
			while (vp.hasParent(PTBLib::isVerbPhrase) && PTBLib.containsCoordination(vp.getParent()))
				vp = vp.getParent();
			
			CTNode vb = vp.getLeftNearestSibling(PTBLib::isVerb);
			return (vb != null && (vb.isLemma("be") || vb.isLemma("become") || vb.isLemma("get")));
		}

		return false;
	}
	
	public CTNode preprocessModalAdjective(CTNode node)
	{
		CTNode prd = node.getFirstChild(n -> PTBLib.isAdjective(n) || n.isSyntacticTag(PTBTag.P_VBG) || n.isSyntacticTag(PTBTag.P_VBN));
		CTNode s = node.getFirstChild(n -> n.isSyntacticTag(PTBTag.C_S));
		
		if (node.andSF(PTBTag.C_ADJP, PTBTag.F_PRD) && prd != null && s != null)
		{
			if (node.getParent().isSyntacticTag(node.getSyntacticTag())) node = node.getParent();
			CTNode np = s.getFirstChild(PTBLib::isNominalSubject);
			
			if (np != null && np.isEmptyCategoryPhrase())
			{
				CTNode sbj = np.getFirstTerminal().getAntecedent();
				
				if (sbj != null)
				{
					CTNode vp = node.getHighestChainedAncestor(PTBLib::isVerbPhrase);
					if (vp == null) vp = node;
					
					if (sbj == vp.getLeftNearestSibling(PTBLib::isNominalSubject))
					{
						s.addFunctionTag(PTBTag.F_PRD);
						prd.setPrimaryLabel(DDGTag.MODAL);
						return prd;
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
			if (child.isTerminal() && (MetaConst.CARDINAL.equals(child.getLemma()) || MetaConst.ORDINAL.equals(child.getLemma())))
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
								child.addFunctionTag(DDGTag.CC);
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
	
	private boolean preprocessSQ(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.C_S) && 
			node.hasParent(n -> n.isSyntacticTag(PTBTag.C_SQ)) && 
		   !node.containsChild(PTBLib::isVerbPhrase) && 
			node.containsChild(PTBLib::isSubject) && 
			node.containsChild(PTBLib::isSecondaryPredicate))
		{
			node.addFunctionTag(PTBTag.F_PRD);
			return true;
		}
		
		return false;
	}
	
//	============================= Pre-process: Empty Categories ============================= 
	
	private void preprocessDuplicates(CTTree tree)
	{
		for (Entry<Integer,List<CTNode>> e : tree.getEmptyCategoryMap().entrySet())
		{
			List<CTNode> list = e.getValue().stream().filter(PTBLib::isTrace).collect(Collectors.toList());
			
			for (int i=1; i<list.size(); i++)
				list.get(i).setForm(PTBTag.E_PRO);
			
			list = e.getValue().stream().filter(n -> PTBLib.isPassiveNull(n) && n.hasCoIndex()).collect(Collectors.toList());
			if (list.isEmpty()) continue;
			
			CTNode node = DSUtils.getFirst(list);
			CTNode ante = node.getAntecedent();
			
			if (ante != null)
			{
				CTNode np = node.getParent();
				
				if (PTBLib.isSubject(np) && !PTBLib.isSubject(ante))
					node.setForm(PTBTag.E_PRO);
			}
		}
	}
	
	private void preprocessEmptyCategory(CTTree tree, CTNode node)
	{
		if (node.isEmptyCategory())
		{
			switch (node.getForm())
			{
			case PTBTag.E_ZERO : break;
			case PTBTag.E_NULL : preprocessPassiveNull(tree, node); break;
			case PTBTag.E_TRACE: preprocessTrace(node); break;
			case PTBTag.E_PRO  : preprocessPRO(node); break;
			case PTBTag.E_EXP  : preprocessEXP(tree, node); break;
			case PTBTag.E_ICH  :
			case PTBTag.E_PPA  :
			case PTBTag.E_RNR  : preprocessDiscontinuousConstituent(tree, node); break;
			default: node.removeSelf();
			}
		}
	}
	
	private void preprocessPassiveNull(CTTree tree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			ec.removeSelf();
		else if (ec.hasCoIndex())
		{
			List<CTNode> list = tree.getEmptyCategories(ec.getCoIndex());
			CTNode np = ec.getParent();
			
			if (ec == DSUtils.getFirst(list) && !(PTBLib.isSubject(np) && !PTBLib.isSubject(ante)))
			{
				ec.getParent().replaceChild(ec, ante);

				if (PTBLib.isClause(ante))
				{
					np.getParent().replaceChild(np, ante);
					ante.removeFunctionTag(PTBTag.F_SBJ);
				}
			}
		}
	}
	
	private void preprocessPRO(CTNode ec)
	{
		CTNode np = ec.getParent();
		
		// small clause
		if (!np.hasRightSibling(PTBLib::isVerbPhrase))
		{
			CTNode prd = np.getRightNearestSibling(PTBLib::isSecondaryPredicate);
			
			if (prd != null)
			{
				CTNode s = np.getParent();
				
				if (s.isSyntacticTag(PTBTag.C_S))
				{
					prd.addFunctionTag(DDGTag.OPRD);
					s.setPrimaryLabel(DDGTag.COMP);
				}				
			}
		}
	}

	private void preprocessTrace(CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			ec.removeSelf();
		else if (ante.isFunctionTag(PTBTag.F_TPC))
		{
			ec.getParent().replaceChild(ec, ante);
		}
		else	// relative clauses
		{
			CTNode parent = ante.getHighestChainedAncestor(n -> n.isSyntacticTag(PTBTag.C_SBAR));
			if (parent != null) parent.addFunctionTag(DDGTag.RELCL);
			ec.getParent().replaceChild(ec, ante);
		}
	}
	
	private void preprocessDiscontinuousConstituent(CTTree tree, CTNode ec)
	{
		if (!ec.hasGrandParent()) return;
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			ec.removeSelf();
		else if (PTBLib.isInterpretConstituentHere(ec) && ec.getParent().hasLeftSibling(PTBLib::isWhPhrase))
			ec.removeSelf(); 
		else
		{
			List<CTNode> list = tree.getEmptyCategories(ante.getCoIndex());
			ec = list.get(list.size()-1);
			
			if (!ec.isFunctionTag(PTBTag.E_RNR))
			{
				CTNode gp = ec.getGrandParent();
				gp.replaceChild(ec.getParent(), ante);
				ec.addFunctionTag(PTBTag.E_RNR);
			}
		}
	}
	
	private void preprocessEXP(CTTree cTree, CTNode ec)
	{
		CTNode s = ec.getParent();
		
		if (s != null && PTBLib.isClause(s))
		{
			CTNode np = s.getParent();
			
			if (np != null && PTBLib.isNominalSubject(np))
			{
				np.addFunctionTag(DDGTag.EXPL);
				
				if (ec.hasAntecedent())
					ec.getAntecedent().addFunctionTag(PTBTag.F_SBJ);
			}
		}
		
		ec.removeSelf();
	}
	
//	============================= Coordination =============================

	/**
	 * If the specific node contains a coordination structure, find the head of each coordination.
	 * @param curr the specific node to be compared. 
	 */
	private CTNode findHeadCoordination(CTNode curr, HeadRule rule)
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
			return null;
		
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
		
		if (heads.isEmpty()) return null;
		
		if (eId - bId > 0)
		{
			head = findHeadCoord(curr, rule, bId, eId, main_head);
			heads.add(head);
		}
		
		handleArgumentsInCoordination(curr, heads);
		return main_head;
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
			return getSpecialLabel(C, C.getTerminalHead()) == null;
		
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
			String label = DDGTag.CONJ;
			if (isDiscourse(currHead, currHead.getTerminalHead())) label = DDGTag.DISC;
			else if (PTBLib.isPunctuation(currHead)) label = DDGTag.P;
			else if (currHead.isSyntacticTag(ADVCL) && hasAdverbialTag(currHead)) label = DDGTag.ADVCL;
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
					child.addSecondaryHead(heads.get(i));	
			}
		}
		
		final List<CTNode> sub = heads.subList(1, heads.size());
		
		while (curr.hasParent(PTBLib::isVerbPhrase) || curr.hasParent(n -> n.isSyntacticTag(PTBTag.C_S)))
		{
			curr.getSiblings().stream().filter(n -> isCoordArgument(n)).forEach(n -> n.addSecondaryHeads(sub));
			curr = curr.getParent();
			if (curr.isSyntacticTag(PTBTag.C_S)) break;
		}
		
		return true;
	}
	
	private boolean isCoordArgument(CTNode node)
	{
		CTNode d = node.getTerminalHead();
		
		return node.isPrimaryLabel(DDGTag.OBJ) || node.isPrimaryLabel(DDGTag.DAT) || node.isPrimaryLabel(DDGTag.COMP) ||
			   (node.isFunctionTag(PTBTag.F_SBJ) && !isEXPL(node, d)) || node.isFunctionTag(PTBTag.F_LGS) || node.isFunctionTag(PTBTag.F_DTV) || hasAdverbialTag(node) ||
			   PTBLib.isAdverb(node); 
	}
	
//	============================= Find Heads =============================

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
		
		if (fst == null || fst.isEmptyCategoryPhrase() || fst.hasPrimaryHead()) return false;

		boolean hasAppo = false;
		CTNode snd = fst;
		
		while ((snd = snd.getRightNearestSibling()) != null)
		{
			if (snd.hasPrimaryHead()) continue;
			
			if ((snd.isSyntacticTag(NP) && !hasAdverbialTag(snd)) ||
				(snd.isFunctionTag(PTBTag.F_HLN, PTBTag.F_TTL)) ||
				(snd.isSyntacticTag(PTBTag.C_RRC) && snd.containsChild(PTBLib::isNominalPredicate)))
			{
				snd.setPrimaryHead(fst, DDGTag.APPO);
				hasAppo = true;
			}
		}
		
		return hasAppo;
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
					next.setPrimaryHead(curr, DDGTag.COM);
			}
			else if (curr.isSyntacticTag(PTBTag.P_JJ))
			{
				if (next != null && (next.isFormLowercase("to")))
					next.setPrimaryHead(curr, DDGTag.COM);
			}
			else if (curr.isSyntacticTag(PTBTag.P_NN))
			{
				if (next != null && (next.isFormLowercase("like")))
					next.setPrimaryHead(curr, DDGTag.COM);
			}
			else if (curr.isSyntacticTag(PTBTag.P_IN))
			{
				if (next != null && next.isSyntacticTag(PTBTag.P_TO))
					next.setPrimaryHead(curr, DDGTag.COM);
			}
		}
		
		return true;
	}
	
// ============================= Get Labels =============================
	
	@Override
	protected String getDependencyLabel(CTNode node, CTNode head)
	{
		CTNode parent = node.getParent();
		CTNode c = node.getPhraseHead();
		CTNode d = node.getTerminalHead();
		String label;
		
		if (node.hasPrimaryLabel())
			return node.getPrimaryLabel();
		
		if (node.hasParent(n -> n.isSyntacticTag(PTBTag.C_CONJP)) || d.isSyntacticTag(PTBTag.P_GW))
			return DDGTag.COM;

		// vocative
		if (node.isFunctionTag(PTBLib.F_VOC))
			return DDGTag.VOC;
		
		// dative
		if (node.isFunctionTag(PTBTag.F_DTV) || node.isFunctionTag(PTBTag.F_BNF))
			return DDGTag.DAT;
				
		// adverbial clause/phrase
		if (hasAdverbialTag(node))
		{
			if (node.isSyntacticTag(ADVCL))
				return DDGTag.ADVCL;
			
			if (node.isSyntacticTag(ADVNP))
				return DDGTag.ADVNP;
		}
		
		// subject
		if ((label = getSubjectLabel(node, d)) != null)
			return label;
		
		// coordination
		if (node.isSyntacticTag(PTBTag.C_UCP))
		{
			c.addFunctionTags(node.getFunctionTags());
			return getDependencyLabel(c, head);
		}
		
		// complements of verbal predicates
		if (parent.isSyntacticTag(COMPP))
		{
			if ((label = getObjectLabel(node, d)) != null) return label;
			if ((label = getAuxiliaryLabel(node)) != null) return label;
			if (isClausalComplement(node)) return DDGTag.COMP;
		}
		
		// complements of adjectival/adverbial predicates
		if (parent.isSyntacticTag(ACOMPP))
		{
			if (isClausalComplement(node)) return DDGTag.COMP;
		}

		// complements of nouns
		if (parent.isSyntacticTag(PTBLib.NP_NML_WHNP))
		{
			if (isRelativeClause(node)) return DDGTag.RELCL;
			if (isNonFiniteClause(node) || isClausalComplement(node)) return DDGTag.ACL;
		}
		
		// possessive
		if (isPossive(node, parent))
			return DDGTag.POSS;
		
		// simple labels
		if ((label = getSimpleLabel(node, d)) != null)
			return label;
			
		// default
		if (node.isSyntacticTag(PTBTag.C_SBAR) || isOpenClausalComplement(node) || (parent.isSyntacticTag(PTBTag.C_PP) && PTBLib.isClause(node)))
			return DDGTag.ADVCL;
		
		if (node.isSyntacticTag(CCOMP))
			return DDGTag.COMP;
		
		if (parent.isSyntacticTag(PTBTag.C_QP) || isNum(head) || head.isSyntacticTag(PTBTag.C_QP))
			return DDGTag.ATTR;
		
		if (parent.isSyntacticTag(NMOD_PARENT) || PTBLib.isNoun(head))
			return DDGTag.ATTR;
		
		if ((parent.isSyntacticTag(ADVP) || PTBLib.isAdjective(head) || PTBLib.isAdverb(head)))
		{
			if (node.isSyntacticTag(ADVNP) || PTBLib.isNoun(node))
				return DDGTag.ADVNP;
			
			return DDGTag.ADV;
		}
		
		return DDGTag.DEP;
	}
	
	private boolean hasAdverbialTag(CTNode node)
	{
		return node.isFunctionTag(PTBTag.F_ADV) || DSUtils.hasIntersection(node.getFunctionTags(), SEM_TAGS);
	}
	
	private String getSubjectLabel(CTNode node, CTNode d)
	{
		if (node.isFunctionTag(PTBTag.F_SBJ))
		{
			if (isEXPL(node, d))
				return DDGTag.EXPL;
			else if (PTBLib.isClause(node))
				return DDGTag.CSBJ;
			else
				return DDGTag.NSBJ;
		}
		else if (node.isFunctionTag(PTBTag.F_LGS))
		{
			if (node.containsChild(PTBLib::isClause))
				return CLGS;
			else
				return NLGS;
		}
		
		return null;
	}
	
	private boolean isEXPL(CTNode node, CTNode d)
	{
		return node.isFunctionTag(DDGTag.EXPL) || node.getFirstTerminal().isSyntacticTag(PTBTag.P_EX) || (d != null && d.isFormLowercase("there"));
	}
	
	private String getObjectLabel(CTNode node, CTNode d)
	{
		if (node.isSyntacticTag(NP))
			return node.hasRightSibling(n -> !hasAdverbialTag(n) && !n.isFunctionTag(PTBTag.F_VOC) && (n.isSyntacticTag(NP) || n.isFunctionTag(PTBTag.F_NOM))) ? DDGTag.DAT : DDGTag.OBJ;
		
		return null;
	}
	
	private String getAuxiliaryLabel(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.P_MD))
			return DDGTag.MODAL;
		
		if (node.isSyntacticTag(PTBTag.P_TO))
			return DDGTag.AUX;

		if (PTBLib.isVerb(node))
		{
			if (node.hasRightSibling(PTBLib::isSecondaryPredicate))
				return DDGTag.COP;
			
			if (node.hasRightSibling(PTBLib::isVerbPhrase))
				return DDGTag.AUX;	
		}
		
		return null;
	}
	
	private boolean isClausalComplement(CTNode node)
	{
		if (node.isSyntacticTag(CCOMP))
			return true;
		
		if (node.isSyntacticTag(PTBTag.C_SBAR))
		{
			if (node.containsChild(PTBLib::isNullComplementizer))
				return true;
			
			if (node.containsChild(n -> n.isSyntacticTag(MARK)))
				return true;
			
			if (node.isFunctionTag(DDGTag.RELCL) || node.containsChild(PTBLib::isWhPhrase))
				return true;
		}
		
		return false;
	}
	
	private boolean isOpenClausalComplement(CTNode node)
	{
		if (node.isSyntacticTag(PTBTag.C_S))
		{
			CTNode sbj = node.getFirstChild(PTBLib::isSubject);
			
			if (node.containsChild(PTBLib::isVerbPhrase) && (sbj == null || sbj.isEmptyCategoryPhrase()))
				return true;
		}
		else if (node.isFunctionTag(DDGTag.RELCL))
		{
			CTNode s = node.getFirstChild(n -> n.isSyntacticTag(PTBTag.C_S));
			if (s != null)	return isOpenClausalComplement(s);
		}

		return false;
	}
	
	private boolean isRelativeClause(CTNode node)
	{
		return node.isSyntacticTag(PTBTag.C_RRC) || node.isFunctionTag(DDGTag.RELCL) || (node.isSyntacticTag(PTBTag.C_SBAR) && node.containsChild(PTBLib::isWhPhrase));
	}
	
	private boolean isNonFiniteClause(CTNode node)
	{
		return isOpenClausalComplement(node) || node.isSyntacticTag(PTBTag.C_VP);
	}
	
	private boolean isPossive(CTNode node, CTNode parent)
	{
		if (node.isSyntacticTag(POSS))
			return true;
		
		if (parent.isSyntacticTag(POSS_PARENT))
			return node.containsChild(n -> n.isSyntacticTag(PTBTag.P_POS));
		
		return false;
	}
	
	private String getSimpleLabel(CTNode node, CTNode d)
	{
		if (PTBLib.isCorrelativeConjunction(node) || PTBLib.isConjunction(node))
			return DDGTag.CC;

		if (node.isSyntacticTag(PP))
			return DDGTag.PPMOD;
		
		if (node.isSyntacticTag(PTBTag.C_QP) || isNum(d))
			return DDGTag.NUM;
		
		if (isMark(node))
			return DDGTag.MARK;
		
		if (isCaseMarker(node))
			return DDGTag.CASE;
		
		return getSpecialLabel(node, d);
	}
	
	private String getSpecialLabel(CTNode node, CTNode d)
	{
		if (isDiscourse(node, d))
			return DDGTag.DISC;
		
		if (PTBLib.isPunctuation(node) || PTBLib.isPunctuation(d))
			return DDGTag.P;
		
		if (isNegation(node) || isNegation(d))
			return DDGTag.NEG;
		
		if (isMeta(node))
			return DDGTag.META;
		
		if (isPrn(node))
			return DDGTag.PRN;

		if (isAdverb(node, d))
			return DDGTag.ADV;
		
		if (isPrt(node))
			return DDGTag.PRT;
		
		if (isDeterminer(node))
			return DDGTag.DET;
		
		return null;
	}
	
	private boolean isMark(CTNode node)
	{
		return node.isSyntacticTag(MARK) && node.hasParent(n -> n.isSyntacticTag(PTBTag.C_SBAR));
	}
	
	private boolean isDiscourse(CTNode node, CTNode d)
	{
		return node.isSyntacticTag(INTJ) || d.isSyntacticTag(INTJ) || d.isSyntacticTag(PTBTag.P_EMO) || emoticon.isEmoticon(d.getForm());
	}
	
	private boolean isNum(CTNode node)
	{
		return node.isSyntacticTag(PTBTag.P_CD) || "0".equals(node.getLemma());
	}
	
	private boolean isMeta(CTNode node)
	{
		return node.isSyntacticTag(META) || node.isForm("%");
	}
	
	private boolean isPrn(CTNode node)
	{
		return node.isSyntacticTag(PTBTag.C_PRN);
	}
	
	private boolean isAdverb(CTNode node, CTNode d)
	{
		if (node.isSyntacticTag(PTBTag.C_ADVP) || PTBLib.isAdverb(node) || PTBLib.isAdverb(d))
		{
			if (node.hasParent(n -> n.isSyntacticTag(PP)) && !node.hasRightSibling() && node.getLeftNearestSibling().isSyntacticTag(PREP))
				return false;

			return true;
		}
		
		return false;
	}
	
	private boolean isPrt(CTNode node)
	{
		return node.isSyntacticTag(PRT);
	}
	
	private boolean isDeterminer(CTNode node)
	{
		return node.isSyntacticTag(DET);
	}
	
	private boolean isCaseMarker(CTNode node)
	{	
		return node.isSyntacticTag(PTBTag.P_IN) || node.isSyntacticTag(PTBTag.P_POS);
	}
	
	private boolean isNegation(CTNode node)
	{
		if (PTBLib.isAdverb(node) && node.getForm() != null && ENUtils.isNegation(node.getForm()))
			return true;
		
		if (node.isSyntacticTag(PTBTag.C_ADVP) && node.matchesForms("no","longer"))
			return true;
		
		return false;
	}

// ============================= Post-process =============================
	
	private void postprocess(CTTree tree)
	{
		tree.flatten().forEach(n -> postprocess(tree, n));
	}
	
	private void postprocess(CTTree tree, CTNode node)
	{
		postprocessLabelSecondaryDependency(node);
		postprocessEmptyCategory(tree, node);
	}
	
	private void postprocessLabelSecondaryDependency(CTNode node)
	{
		for (CTArc arc : node.getSecondaryHeads())
		{
			if (arc.getLabel() == null)
				arc.setLabel(getDependencyLabel(node, arc.getNode()));
		}
	}
	
	private void postprocessEmptyCategory(CTTree tree, CTNode node)
	{
		CTNode ante = getAntecedent(node);
		
		if (ante != null)
		{
			if (PTBLib.isPassiveNull(node))
				postprocessNull(tree, node, ante);
			else if (PTBLib.isPRO(node))
				postprocessPRO(node, ante);
			else if (PTBLib.isNullComplementizer(node) || PTBLib.isRelativizer(node))
				postprocessRelativizer(tree, node, ante);
			else if (PTBLib.isRightNodeRaising(node))
				postprocessRightNodeRaising(tree, node, ante);
		}
	}
	
	private void postprocessNull(CTTree tree, CTNode node, CTNode ante)
	{
		CTNode np  = node.getParent();
		CTArc  arc = getPrimaryArc(np, ante);
		CTNode p   = arc.getNode();
		
		if (p != null && !(PTBLib.isSecondaryPredicate(p) && p.isEmptyCategoryPhrase()))
			ante.addSecondaryHead(arc);
//		{
//			if (node.hasCoIndex())
//			{
//				List<CTNode> list = tree.getEmptyCategories(node.getCoIndex());
//				
//				if (node == DSUtils.getFirst(list))
//				{
//					if (PTBLib.isSubject(np) && !PTBLib.isSubject(ante))
//						ante.addSecondaryHead(arc);
//					else if (!node.isDescendantOf(ante) && !isPrimaryAncestorOf(ante, arc.getNode()))
//						ante.setPrimaryHead(arc);
//				}
//				else
//					ante.addSecondaryHead(arc);
//			}
//			else
//				ante.addSecondaryHead(arc);			
//		}
	}
	
	private void postprocessPRO(CTNode node, CTNode ante)
	{
		CTNode np = node.getParent();
		CTArc arc = np.getPrimaryHead();
		
		if (arc.getNode() != null)
			ante.addSecondaryHead(arc.getNode(), arc.getLabel());
	}

	private void postprocessRelativizer(CTTree tree, CTNode node, CTNode ante)
	{
		CTNode wh = node.getParent();
		
		while (!wh.hasCoIndex() && wh.hasParent(PTBLib::isWhPhrase))
			wh = wh.getParent();
		
		if (wh.hasCoIndex())
		{
			CTArc arc = getPrimaryArc(wh.getParent(), ante);
			
			if (arc.getNode() != null && !arc.isLabel(DDGTag.RELCL) && !arc.isLabel(DDGTag.CONJ))
			{
				ante.addSecondaryHead(arc);
				arc.setLabel(DDGTag.R+arc.getLabel());
			}
		}
	}
	
	private void postprocessRightNodeRaising(CTTree tree, CTNode node, CTNode ante)
	{
		CTNode p = node.getParent();
		CTArc arc = getPrimaryArc(p, ante);

		if (arc.getNode() != null)
		{
			if (!isPrimaryAncestorOf(ante, arc.getNode()) && !arc.isLabel(DDGTag.DEP) && !arc.isLabel(DDGTag.ATTR))
				ante.addSecondaryHead(arc);
		}
	}
	
	private CTNode getAntecedent(CTNode node)
	{
		CTNode ante = node.getAntecedent();
		
		while (ante != null && ante.isEmptyCategoryPhrase())
			ante = ante.getFirstTerminal().getAntecedent();

		return ante;
	}
	
	private CTArc getPrimaryArc(CTNode node, CTNode ante)
	{
		if (node.hasParent(n -> n.isSyntacticTag(PP)) && node.getParent().hasPrimaryHead())
		{
			ante.addFunctionTags(node.getParent().getFunctionTags());
			node.getParent().clearFunctionTags();
			return node.getParent().getPrimaryHead();
		}
		
		return node.getPrimaryHead();
	}
	
	/** n1 is a primary ancestor of n2. */
	private boolean isPrimaryAncestorOf(CTNode n1, CTNode n2)
	{
		while (n2 != null)
		{
			if (n2.hasPrimaryHead(n1)) return true;
			n2 = n2.getParent();
		}
		
		return false;
	}
	
// ============================= relabel =============================
	
	private void relabel(CTTree tree, NLPGraph graph)
	{
		graph.forEach(n -> relabel(tree, n));
		labelCompounds(graph);
		labelLightVerbs(tree, graph);
	}
	
	private void relabel(CTTree tree, NLPNode node)
	{
		if (PTBLib.isVerb(node))
			relabelEXPL(node);
		else if (node.isSyntacticTag(PTBTag.P_IN))
		{
			if (!node.hasChild() && !node.isDependencyLabel(DDGTag.COM) && !node.isDependencyLabel(DDGTag.MARK))// && !node.getDependencyLabel().startsWith(DDGTag.R))
				node.setDependencyLabel(DDGTag.CASE);
		}
		
		String label = relabelAux(node.getParent(), node.getDependencyLabel());
		if (label != null) node.setDependencyLabel(label);
		
		Iterator<NLPArc<NLPNode>> it = node.getSecondaryHeads().iterator();
		
		while (it.hasNext())
		{
			NLPArc<NLPNode> snd = it.next();
			
			if (snd.isLabel(DDGTag.DEP))
			{
				if (node.isDependencyLabel(DDGTag.NSBJ) || node.isDependencyLabel(DDGTag.CSBJ))
					snd.setLabel(node.getDependencyLabel());
				else
					it.remove();
			}
			else if (snd.isLabel(DDGTag.CONJ))
				it.remove();
			else
			{
				label = relabelAux(snd.getNode(), snd.getLabel());
				if (label != null) snd.setLabel(label);
			}
		}
	}
	
	private String relabelAux(NLPNode head, String label)
	{
		switch (label)
		{
		case CLGS:
		case DDGTag.R+CLGS: return DDGTag.CSBJ;
		case NLGS:
		case DDGTag.R+NLGS: return DDGTag.NSBJ;
		}
		
		if (label.equals(DDGTag.COP) && PTBLib.isVerb(head))
			return DDGTag.AUX;
		
		return null;
	}
	
	private void relabelEXPL(NLPNode node)
	{
		NLPNode expl = node.getFirstChild(n -> n.isDependencyLabel(DDGTag.EXPL));
		
		if (expl != null && expl.isLemma("there"))
		{
			NLPNode obj = node.getFirstChild(n -> n.isDependencyLabel(DDGTag.OBJ));
			if (obj != null) obj.setDependencyLabel(DDGTag.NSBJ);
		}
	}
	
	private void labelCompounds(NLPGraph graph)
	{
		labelCompounds(graph,
				head -> PTBLib.isCommonOrProperNoun(head),
				node -> node.isDependencyLabel(DDGTag.ATTR) || node.isDependencyLabel(DDGTag.DEP) || node.getSyntacticTag().startsWith(PTBTag.P_NNP));
		
		labelCompounds(graph, 
				head -> isNumber(head),
				node -> node.isDependencyLabel(DDGTag.ATTR) || node.isDependencyLabel(DDGTag.DEP) || node.isDependencyLabel(DDGTag.NUM));
		
		labelCompounds(graph, 
				head -> PTBLib.isAdverb(head),
				node -> node.isDependencyLabel(DDGTag.ADV) || node.isDependencyLabel(DDGTag.DEP) || node.isDependencyLabel(DDGTag.NEG));
	}
	
	private void labelCompounds(NLPGraph graph, Predicate<NLPNode> main_condition, Predicate<NLPNode> node_condition)
	{
		NLPNode node, head;
		int i, j;
		
		for (i=graph.size()-1; i>0; i--)
		{
			head = graph.get(i);
			
			if (main_condition.test(head))
			{
				for (j=i-1; j>0; j--)
				{
					node = graph.get(j);
					
					if (node.isDescendantOf(head) && node.getParent().compareTo(node) > 0 && main_condition.test(node) && node_condition.test(node))
					{
						node.setDependencyLabel(DDGTag.COM);
						i = j;
					}
					else if (node.isSyntacticTag(PTBTag.P_HYPH) && !node.isDependencyLabel(DDGTag.CC))
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
	
	private void labelLightVerbs(CTTree tree, NLPGraph graph)
	{
		for (NLPNode node : graph)
		{
			if (PTBLib.isVerb(node) && light_verbs.contains(node.getLemma()))
//			if (tree.getToken(node.getTokenID()-1).isFunctionTag(DDGTag.LV))
			{
				NLPNode obj = node.getFirstChild(n -> n.isDependencyLabel(DDGTag.OBJ));
				
				if (obj != null && eventive_nouns.contains(obj.getLemma()) && !node.containsChild(n -> n.isDependencyLabel(DDGTag.DAT)) && !node.containsChild(n -> n.isDependencyLabel(DDGTag.OPRD)))
				{
					// heads
					obj.setParent(node.getParent(), node.getDependencyLabel());
					obj.addSecondaryHeads(node.getSecondaryHeads());
					node.clearSecondaryHeads();
					node.setParent(obj, DDGTag.LV);
					
					for (NLPNode d : node.getChildren())
					{
						if (d.isDependencyLabel(DDGTag.ATTR))
							d.setDependencyLabel(DDGTag.ADV);
					}
					
					// children 
					for (NLPNode d : graph)
					{
						if (d.isChildOf(node)) d.setParent(obj);
						d.getSecondaryHeads().stream().filter(a -> a.isNode(node)).forEach(a -> a.setNode(obj));
					}
				}
			}
		}
	}
	
	// ============================= validate =============================
	
	private void addFeats(CTTree tree, NLPGraph graph)
	{
		tree.flatten().forEach(n -> addFeats(n, graph));
	}
	
	private void addFeats(CTNode node, NLPGraph graph)
	{
		CTNode term = node.getTerminalHead();
		if (term == null || term.isEmptyCategory()) return;
		
		Set<String> tags = new TreeSet<>(node.getFunctionTags());
		tags.retainAll(FEATS);
		
		if (tags.size() > 1)
			tags.remove(PTBTag.F_CLR);
		
		if (!tags.isEmpty())
		{
			String feat = StringUtils.toLowerCase(Joiner.join(tags, FeatMap.DELIM_VALUES));
			NLPNode n = graph.get(term.getTokenID()+1);
			if (n.isDependencyLabel(DDGTag.LV))
			{
				n = n.getParent();
				n.removeFeat(DDGTag.FEAT_SEM);
			}
			n.putFeat(DDGTag.FEAT_SEM, feat);
		}
	}

	// ============================= validate =============================
	
	public void validate(CTTree tree, NLPGraph graph)
	{
		String message = null;
		
		if (graph.getRoot().getChildrenSize() != tree.getRoot().getChildrenSize())
			message = "Root mistach";
		
		for (NLPNode node : graph)
		{
			if (!node.hasParent() || node.getDependencyLabel() == null)
				message = "Primary head error: "+node.getTokenID();
			
			for (NLPArc<NLPNode> arc : node.getSecondaryHeads())
			{
				if (arc.getNode() == null || arc.getLabel() == null)
					message = "Secondary head error: "+node.getTokenID();
			}
			
			if (node.isAncestorOf(node.getParent()))
				message = "Cyclic relation: "+node.getTokenID();
			
//			if (node.getChildren(n -> n.isDependencyLabel(DDGTag.OBJ)).size() > 1)
//				message = "Multiple objects: "+node.getTokenID();
		}
		
		if (message != null)
			System.err.println(message+"\n"+tree.toString()+"\n"+graph.toString());
	}
	
////============================= Predicate Argument Structures =============================
//
//private Map<CTNode,Set<CTNode>> findArguments(CTTree tree)
//{
//	Map<CTNode,Set<CTNode>> map = new HashMap<>();
//	findArgumentsRec(tree.getRoot(), map);
//	return map;
//}
//
///** Called by {@link #findArguments(CTTree)}. */
//private void findArgumentsRec(CTNode node, Map<CTNode,Set<CTNode>> map)
//{
//	if (node.isSyntacticTag(PTBTag.C_VP))
//		findArgumentsAux(node, map);
//	else
//		node.getChildren().forEach(n -> findArgumentsRec(n, map));
//}
//
///**
// * Called by {@link #findArgumentsRec(CTNode, Int2ObjectMap)}.
// * @param vp the highest verb phrase.
// */
//private void findArgumentsAux(CTNode vp, Map<CTNode,Set<CTNode>> map)
//{
//	List<CTNode> args = new ArrayList<>();
//	List<CTNode> auxs = new ArrayList<>();
//	CTNode p = vp.getParent();
//	
//	if (p.matches(PTBLib.M_Sx))
//	{
//		for (CTNode n : p.getChildren())
//		{
//			if (n == vp || PTBLib.isPunctuation(n.getSyntacticTag())) continue;
//			if (n.matches(PTBLib.M_MD_TO_VBx)) auxs.add(n);
//			else args.add(n);
//		}
//	}
//	
//	findArgumentsAux(vp, map, args, auxs);
//}
//
///** Called by {@link #findArgumentsAux(CTNode, Map)}. */
//private void findArgumentsAux(CTNode vp, Map<CTNode,Set<CTNode>> map, List<CTNode> args, List<CTNode> auxs)
//{
//	List<CTNode> vps = new ArrayList<>();
//	List<CTNode> vbs = new ArrayList<>();
//	List<CTNode> tmp = new ArrayList<>();
//	
//	for (CTNode n : vp.getChildren())
//	{
//		if (n.isSyntacticTag(PTBTag.C_VP))
//			vps.add(n);
//		else if (n.matches(PTBLib.M_MD_TO_VBx))
//			vbs.add(n);
//		else
//			tmp.add(n);
//	}
//	
//	if (vps.isEmpty())
//		vbs.forEach(vb -> initArguments(vb, map, args, auxs));
//	else
//	{
//		args.addAll(tmp);
//		auxs.addAll(vbs);
//	
//		if (vps.size() == 1) findArgumentsAux(DSUtils.getFirst(vps), map, args, auxs);
//		else for (CTNode n : vps) findArgumentsAux(n, map, new ArrayList<>(args), new ArrayList<>(auxs));
//	}
//}
//
//private TVerb initArguments(CTNode vb, Map<CTNode,Set<CTNode>> map, List<CTNode> args, List<CTNode> auxs)
//{
//	TVerb vt = new TVerb(vb.getLemma());
//	vt.setTense(getTense(vb, auxs));
//	
//	Pair<Aspect,Voice> p = getAspectAndVoice(vb, auxs);
//	vt.setAspect(p.o1);
//	vt.setVoice (p.o2);
//	
//	vt.setModals(getModals(auxs));
//	vt.setNegation(neg);
//	
//	return vt;
//}
//
//private void initModals(List<CTNode> auxs)
//{
//	CTNode curr, next;
//	
//	for (int i=0; i<auxs.size()-1; i++)
//	{
//		curr = auxs.get(i);
//		next = auxs.get(i+1);
//		
//		if (curr.isLemma("go"))
//		{
//			if (0 < i)
//			{
//				CTNode prev = auxs.get(i-1);
//				
//				if (prev.isLemma("be") && next.isLemma("to"))
//				{
//					prev.addFunctionTag(DSRTag.MWE);
//					next.addFunctionTag(DSRTag.MWE);
//					curr.addFunctionTag(DSRTag.MODAL);
//				}
//			}
//		}
//		else if (isSemiModal(curr) && next.isLemma("to"))
//		{
//			next.addFunctionTag(DSRTag.MWE);
//			curr.addFunctionTag(DSRTag.MODAL);
//		}
//	}
//}
//
//private Tense getTense(CTNode vb, List<CTNode> auxs)
//{
//	if (auxs.isEmpty()) return getTense(vb);
//	CTNode aux = DSUtils.getFirst(auxs);
//	
//	if (aux.isLemma("will") || aux.isLemma("shall"))
//		return Tense.future;
//	
//	for (int i=1; i<auxs.size()-1; i++)
//	{
//		aux = auxs.get(i);
//		CTNode prev = auxs.get(i-1);
//		CTNode next = auxs.get(i+1);
//
//		if (aux.isLemma("go") && prev.isLemma("be") && next.isLemma("to"))
//		{
//			prev.addFunctionTag(DSRTag.MWE);
//			next.addFunctionTag(DSRTag.MWE);
//			aux .addFunctionTag(DSRTag.MODAL);
//			return Tense.future;	
//		}
//	}
//	
//	return getTense(DSUtils.getFirst(auxs));
//}
//
//private Tense getTense(CTNode vb)
//{
//	switch (vb.getSyntacticTag())
//	{
//	case PTBTag.P_VBD: return Tense.past;
//	case PTBTag.P_VBP:
//	case PTBTag.P_VBZ: return Tense.present;
//	}
//	
//	return Tense.none;
//}
//
//private Pair<Aspect,Voice> getAspectAndVoice(CTNode vb, List<CTNode> auxs)
//{
//	if (auxs.isEmpty()) return new Pair<>(Aspect.none, Voice.active);
//	CTNode prog = null, perf = null, pass = null;
//	
//	if (vb.isSyntacticTag(PTBTag.P_VBG))
//	{
//		CTNode a = DSUtils.getLast(auxs);
//		
//		if (a.isLemma("be"))
//		{
//			prog = a;
//			
//			if (a.matches(PTBLib.M_VBD_VBN))
//			{
//				a = DSUtils.getLast(auxs, 1);
//				if (a != null && a.isLemma("have")) perf = a;
//			}
//		}
//	}
//	else if (vb.matches(PTBLib.M_VBD_VBN))
//	{
//		CTNode a = DSUtils.getLast(auxs);
//		
//		if (a.isLemma("have"))
//			perf = a;
//		else if (a.isLemma("be") || a.isLemma("become") || a.isLemma("get"))
//			pass = a;
//	}
//	
//	Aspect aspect = Aspect.none;
//	
//	if (perf != null && prog != null)
//		aspect = Aspect.perfect_progressive;
//	else if (perf != null)
//		aspect = Aspect.perfect;
//	else if (prog != null)
//		aspect = Aspect.progressive;
//	
//	Voice voice = (pass != null) ? Voice.passive : Voice.active;
//	return new Pair<>(aspect, voice);
//}
//
//private void findModals(CTNode vb, List<CTNode> auxs)
//{
//	Set<String> modals = new HashSet<>();
//	
//	for (int i=0; i<auxs.size(); i++)
//	{
//		CTNode aux = auxs.get(i);
//		
//		if (aux.isSyntacticTag(PTBTag.P_MD))
//		{
//			if (!aux.isLemma("will") && !aux.isLemma("shall"))
//				modals.add(aux.getLemma());
//		}
//		else if (isSemiModal(aux))
//		{
//			if (i+1 < auxs.size() && auxs.get(i+1).isLemma("to"))
//				modals.add(aux.getLemma());
//		}
//	}
//	
//	return modals;
//}
//
//private boolean isSemiModal(CTNode vb)
//{
//	return vb.isLemma("have") || vb.isLemma("need") || vb.isLemma("dare") || vb.isLemma("use") || vb.isLemma("go");
//}
//
//private void addDependent(CTNode head, CTNode node, String label)
//{
//	node.addDependencyHead(new CTArc(head, label));
//}
//	
//	private void deepLabel(NLPNode[] tree)
//	{
//		for (int i=1; i<tree.length; i++)
//		{
//			NLPNode node = tree[i];
//			
//			if (node.isDependencyLabel(DDGTag.ADV))
//			{
//				if (ENUtils.isNegation(node.getForm()))
//					changeLabel(node, DDGTag.NEG);
//			}
//			else if (node.isDependencyLabel(AUXP))
//			{
//				NLPNode head = node.getParent();
//				changeLabel(node, DDGTag.AUX);
//				
//				for (NLPNode sib : head.getChildren())
//				{
//					switch (sib.getDependencyLabel())
//					{
//					case DDGTag.NSBJ: changeLabel(sib, DDGTag.OBJ);  break;
//					case DDGTag.CSBJ: changeLabel(sib, DDGTag.COMP); break;
//					case        XSUBJ: changeLabel(sib, DDGTag.XCOMP); break;
//					}
//				}
//			}
//			else if (node.isDependencyLabel(DDGTag.EXPL))
//			{
//				NLPNode head = node.getParent();
//				NLPNode d = head.getFirstChildByDependencyLabel(DDGTag.OBJ);
//				
//				if (d != null && node.getFormLowercase().equals("there"))
//					changeLabel(d, DDGTag.NSBJ);
//				
//				d = head.getFirstChildByDependencyLabel(DDGTag.COMP);
//				if (d == null) d = head.getFirstChildByDependencyLabel(DDGTag.XCOMP);
//				if (d != null) changeLabel(d, DDGTag.CSBJ);
//			}
//			else if (node.isDependencyLabel(DDGTag.ACL))
//			{
//				addDOBJinRRC(node, node.getParent());
//			}
//			else if (node.isDependencyLabel(DDGTag.SCOMP))
//			{
//				NLPNode nsubj = node.getFirstChildByDependencyLabel(DDGTag.NSBJ);
//				if (nsubj != null) nsubj.setParent(node.getParent(), DDGTag.OBJ);
//			}
//			
//			List<NLPNode> list = node.getChildrenByDependencyLabel(DDGTag.OBJ);
//			
//			if (list.size() > 1 || (list.size() == 1 && node.hasChildByDepenencyLabel(DDGTag.COMP)))
//				changeLabel(list.get(0), DDGTag.DAT);
//		}
//		
//		for (int i=1; i<tree.length; i++)
//		{
//			NLPNode node = tree[i];
//			
//			switch (node.getDependencyLabel())
//			{
//			case XSUBJ: changeLabel(node, DDGTag.CSBJ); break;
//			case NLGS : changeLabel(node, DDGTag.NSBJ); break;
//			case CLGS : changeLabel(node, DDGTag.CSBJ); break;
//			}
//		}
//	}
//	
//	private boolean postProcess(NLPNode node)
//	{
//		// flip PP
//		if (node.isSyntacticTag(PTBTag.P_IN) && !(node.hasChildByDepenencyLabel(DDGTag.COP) || node.isDependencyLabel(DDGTag.SCOMP)))
//		{
//			NLPNode pdep = node.getFirstChildByDependencyLabel(DDGTag.POBJ);
//			if (pdep == null) pdep = node.getFirstChildByDependencyLabel(DDGTag.PCOMP);
//			
//			if (pdep == null)
//			{
//				NLPNode in = node.getLeftMostChild(PTBTag.P_IN, (n,s) -> n.isSyntacticTag(s));
//				if (in == null || in.getChildren().isEmpty()) return false;
//				node.adaptDependents(in);
//				return postProcess(node);
//			}
//			
//			String label = (node.isDependencyLabel(DDGTag.CASE)) ? DDGTag.PREP : node.getDependencyLabel();
//			pdep.setParent(node.getParent(), label);
//			pdep.setFeatMap(node.getFeatMap());
//			pdep.adaptDependents(node);
//			node.setParent(pdep, DDGTag.CASE);
//			node.setFeatMap(new FeatMap());
//			return true;
//		}
//		// dative
//		else if (PTBTag.F_BNF.equals(node.getFeat(NLPUtils.FEAT_SEM)))
//		{
//			NLPNode head = node.getParent();
//			if (PTBLib.isVerb(head.getSyntacticTag()) && transfer_verbs.contains(head.getLemma()))
//				node.setDependencyLabel(DDGTag.DAT);
//		}
//
//		return false;
//	}
//	

//	
//// ============================= Get a dependency graph =============================
//	
//	private NLPNode[] getDependencyGraph(CTTree cTree, Map<CTNode,Deque<CTNode>> xsubj, Map<CTNode,Deque<CTNode>> rnr)
//	{
//		NLPNode[] dTree = initDependencyGraph(cTree);
//		addDEPHeads(dTree, cTree);
//		
//		if (NLPUtils.containsCycle(dTree))
//			throw new UnknownFormatConversionException("Cyclic depedency relation.");
//
//		addSecondaryCoord(dTree);
//		deepLabel(dTree);
//		addSecondaryHeads(dTree, xsubj, rnr);
//		addFeats(dTree, cTree, cTree.getRoot());
//		labelCompounds(dTree);
//		postProcess(dTree);
//		addCoordArguments(dTree, cTree);
//		
//		if (cTree.hasPropBank())
//			addSemanticHeads(dTree, cTree);
//		
//		if (cTree.hasNamedEntity())
//			addNamedEntities(dTree, cTree);
//		
//		return getDEPTreeWithoutEdited(cTree, dTree);
//	}
//
//// ============================= Compounds =============================
//
//	private boolean findHeadHyphen(CTNode node)
//	{
//		int i, size = node.getChildrenSize();
//		CTNode prev, hyph, next;
//		boolean isFound = false;
//		boolean isVP = node.isSyntacticTag(PTBTag.C_VP);
//		
//		for (i=0; i<size-2; i++)
//		{
//			prev = node.getChild(i);
//			hyph = node.getChild(i+1);
//			next = node.getChild(i+2);
//			
//			if (hyph.isSyntacticTag(PTBTag.P_HYPH))
//			{
//				prev.setPrimaryHead(next, DDGTag.COM);
//				hyph.setPrimaryHead(next, DDGTag.P);
//				isFound = true;
//				i++;
//			}
//		}
//		
//		return isFound;
//	}
//	
//// ============================= Secondary Dependencies =============================
//	
//	/** Called by {@link #getDEPTree(CTTree)}. */
//	private void addFeats(NLPNode[] dTree, CTTree cTree, CTNode cNode)
//	{
//		CTNode ante;
//		String feat;
//		
//		if (!cNode.isEmptyCategoryPhrase() && cNode.getGapIndex() != -1 && cNode.getParent().getGapIndex() == -1 && (ante = cTree.getAntecedent(cNode.getGapIndex())) != null)
//		{
//			NLPNode dNode = getNLPNode(dTree, cNode);
//			dNode.addSecondaryHead(getNLPNode(dTree, ante), DDGTag.DEP2_GAP);
//		}
//		
//		if ((feat = getFunctionTags(cNode, SEM_TAGS)) != null)
//			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);
//		
//		if ((feat = getFunctionTags(cNode, SYN_TAGS)) != null)
//			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SYN, feat);
//
//		for (CTNode child : cNode.getChildren())
//			addFeats(dTree, cTree, child);
//	}
//	
//	/** Called by {@link #addFeats(DEPTree, CTTree, CTNode)}. */
//	private String getFunctionTags(CTNode node, Set<String> sTags)
//	{
//		List<String> tags = new ArrayList<>();
//		
//		for (String tag : node.getFunctionTags())
//		{
//			if (sTags.contains(tag))
//				tags.add(tag);
//		}
//		
//		if (tags.isEmpty())	return null;
//		Collections.sort(tags);
//		return Joiner.join(tags, FeatMap.DELIM_VALUES);
//	}
//	
//	private NLPNode getNLPNode(NLPNode[] dTree, CTNode cNode)
//	{
//		if (cNode.isSyntacticTag(CTTag.TOP)) return null;
//		CTNode cHead = cNode.isTerminal() ? cNode : cNode.getC2DInfo().getTerminalHead();
//		return cHead.isEmptyCategory() ? null : dTree[cHead.getTokenID()+1];
////		return cNode.isTerminal() ? dTree.get(cNode.getTokenID()+1) : dTree.get(cNode.getC2DInfo().getTerminalHead().getTokenID()+1);
//	}
//	
//// ============================= Edited phrases =============================
//	
//	public NLPNode[] getDEPTreeWithoutEdited(CTTree cTree, NLPNode[] dTree)
//	{
//		List<NLPNode> nodes = new ArrayList<>();
//		Set<Integer> set = new HashSet<>();
//		int id = 1;
//			
//		addEditedTokensAux(cTree.getRoot(), set);
//			
//		for (int i=1; i<dTree.length; i++)
//		{
//			NLPNode node = dTree[i];
//			
//			if (!set.contains(node.getTokenID()))
//			{
//				removeEditedHeads(node.getSecondaryHeadList(), set);
//				removeEditedHeads(node.getSemanticHeadList() , set);
//				node.setTokenID(id++);
//				nodes.add(node);
//			}
//		}
//		
//		return (nodes.size() > 0) ? NLPUtils.toDependencyTree(nodes, NLPNode::new) : null;
//	}
//		
//	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
//	private void addEditedTokensAux(CTNode curr, Set<Integer> set)
//	{
//		for (CTNode child : curr.getChildren())
//		{
//			if (PTBLib.isEditedPhrase(child))
//			{
//				for (CTNode sub : child.getTokens())
//					set.add(sub.getTokenID()+1);
//			}
//			else if (!child.isTerminal())
//				addEditedTokensAux(child, set);
//		}
//	}
//		
//	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
//	private <T extends Arc<NLPNode>>void removeEditedHeads(List<T> heads, Set<Integer> set)
//	{
//		if (heads == null) return;
//		List<T> remove = new ArrayList<>();
//		
//		for (T arc : heads)
//		{
//			if (arc.getNode() == null || set.contains(arc.getNode().getTokenID()))
//				remove.add(arc);
//		}
//		
//		heads.removeAll(remove);
//	}	
	
	// ============================= Add PropBank arguments =============================
	
//	private void addSemanticHeads(NLPNode[] dTree, CTTree cTree)
//	{
//		initPropBank(dTree, cTree.getRoot());
//		arrangePropBank(dTree);
//		relabelNumberedArguments(dTree);
//	}
//	
//	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
//	private void initPropBank(NLPNode[] dTree, CTNode cNode)
//	{
//		NLPNode dNode = getNLPNode(dTree, cNode);
//		
//		if (dNode != null)
//		{
//			if (cNode.isPredicate())
//				dNode.putFeat(NLPUtils.FEAT_PREDICATE, cNode.getFrameID());
//			
//			NLPNode sHead, d;
//			String  label;
//			CTNode  c;
//			
//			for (CTArc p : cNode.getSemanticHeads())
//			{
//				sHead = getNLPNode(dTree, p.getNode());
//				label = PBLib.getShortLabel(p.getLabel());
//				
//				if ((c = getReferentArgument(cNode)) != null)
//				{
//					if ((c = PTBLib.getRelativizer(c)) != null && (c = c.getAntecedent()) != null)
//					{
//						d = getNLPNode(dTree, c);
//						
//						if (d != null && d.getSemanticHeadArc(sHead) == null)
//							d.addSemanticHead(new DEPArc<>(sHead, label));
//					}
//					
//					label = PBLib.PREFIX_REFERENT + label;
//				}
//				
//				if (!dNode.isArgumentOf(sHead) && dNode != sHead)
//					dNode.addSemanticHead(sHead, label);
//			}	
//		}
//		
//		for (CTNode child : cNode.getChildren())
//			initPropBank(dTree, child);
//	}
//	
//	/** Called by {@link #initPropBank(DEPTree, CTNode)}. */
//	private CTNode getReferentArgument(CTNode node)
//	{
//		CTNode ref;
//		
//		if ((ref = PTBLib.getWhPhrase(node)) != null)
//			return ref;
//		
//		if (node.isSyntacticTag(PTBTag.C_PP))
//		{
//			for (CTNode child : node.getChildren()) 
//			{
//				if ((ref = PTBLib.getWhPhrase(child)) != null)
//					return ref;
//			}
//		}
//
//		return null;
//	}
//	
//	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
//	private void arrangePropBank(NLPNode[] tree)
//	{
//		List<DEPArc<NLPNode>> remove;
//		NLPNode head;
//		String label;
//		
//		for (NLPNode node : tree)
//		{
//			remove = new ArrayList<>();
//			
//			for (DEPArc<NLPNode> arc : node.getSemanticHeadList())
//			{
//				head  = arc.getNode();
//				label = arc.getLabel();
//				
//				if (ancestorHasSemanticHead(node, head, label))
//					remove.add(arc);
//			//	else if (rnrHasSHead(node, head, label))
//			//		remove.add(arc);
//			}
//			
//			node.removeSemanticHeads(remove);
//		}
//	}
//	
//	/** Called by {@link #arrangePropBank(DEPTree)}. */
//	private boolean ancestorHasSemanticHead(NLPNode dNode, NLPNode sHead, String label)
//	{
//		NLPNode dHead = dNode.getParent();
//		
//		while (dHead.getTokenID() != 0)
//		{
//			if (dHead.isArgumentOf(sHead, label))
//				return true;
//			
//			dHead = dHead.getParent();
//		}
//		
//		return false;
//	}
//	
////	private boolean rnrHasSHead(NLPNode dNode, NLPNode sHead, String label)
////	{
////		for (DEPArc rnr : dNode.getSecondaryHeadList(DEPTagEn.DEP2_RNR))
////		{
////			if (rnr.getNode().isArgumentOf(sHead, label))
////				return true;
////		}
////		
////		return false;
////	}
//	
//	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
//	private void relabelNumberedArguments(NLPNode[] tree)
//	{
//		Map<String,NLPNode> map = new HashMap<>();
//		String key;
//		
//		for (NLPNode node : tree)
//		{
//			for (DEPArc<NLPNode> arc : node.getSemanticHeadList())
//			{
//				if (PBLib.isReferentArgument(arc.getLabel()))
//					continue;
//								
//				if (PBLib.isModifier(arc.getLabel()))
//					continue;
//				
//				key = arc.toString();
//				
//				if (map.containsKey(key))
//					arc.setLabel(PBLib.PREFIX_CONCATENATION + arc.getLabel());
//				else
//					map.put(key, node);
//			}
//		}
//	}
//		
//	public void addNamedEntities(NLPNode[] dTree, CTTree cTree)
//	{
//		for (CTNode node : cTree.getTokens())
//			dTree[node.getTokenID()+1].setNamedEntityTag(node.getNamedEntityTag());
//	}
}