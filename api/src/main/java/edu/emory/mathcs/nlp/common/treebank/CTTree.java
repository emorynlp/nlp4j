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
package edu.emory.mathcs.nlp.common.treebank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.propbank.PBArgument;
import edu.emory.mathcs.nlp.common.propbank.PBInstance;
import edu.emory.mathcs.nlp.common.propbank.PBLib;
import edu.emory.mathcs.nlp.common.propbank.PBLocation;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;


/**
 * Constituent tree.
 * @see CTReader
 * @see CTNode 
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTTree
{
	private CTNode                      n_root;
	private List<CTNode>                n_termainals;
	private List<CTNode>                n_tokens;
	private Int2ObjectMap<List<CTNode>> m_nulls;
	
	/**
	 * Constructs a constituent tree using the specific root node.
	 * @param root the root node of this tree.
	 */
	public CTTree(CTNode root)
	{
		n_root = root;
		initTerminals();
		linkEmtpyCategories();
	}
	
	/** Called by {@link #CTTree(CTNode)}. */
	private void initTerminals()
	{
		List<CTNode> terminals = new ArrayList<>();
		List<CTNode> tokens    = new ArrayList<>();

		initTerminalsAux(n_root, terminals, tokens);
		n_termainals = terminals;
		n_tokens     = tokens;
	}

	/** Called by {@link #initTerminals()}. */
	private void initTerminalsAux(CTNode curr, List<CTNode> terminals, List<CTNode> tokens)
	{
		if (curr.isTerminal())
		{
			curr.setTerminalID(terminals.size());
			terminals.add(curr);
			
			if (!curr.isEmptyCategory())
			{
				curr.setTokenID(tokens.size());
				tokens.add(curr);
			}
		}
		else
		{
			for (CTNode child : curr.getChildrenList())
				initTerminalsAux(child, terminals, tokens);			
		}
	}
	
	/** Called by {@link #CTTree(CTNode)}. */
	private void linkEmtpyCategories()
	{
		m_nulls = new Int2ObjectOpenHashMap<List<CTNode>>();
		List<CTNode> list;
		int idx, coIndex;
		String form;
		
		for (CTNode node : n_termainals)
		{
			form = node.getWordForm();
			
			if (node.isEmptyCategory() && (idx = form.lastIndexOf("-")) >= 0)
			{
				coIndex = Integer.parseInt(form.substring(idx+1));
				node.setAntecedent(getAntecedent(coIndex));
				
				if (node.hasAntecedent())
				{
					if (m_nulls.containsKey(coIndex))
						list = m_nulls.get(coIndex);
					else
					{
						list = new ArrayList<>();
						m_nulls.put(coIndex, list);
					}
		
					list.add(node);
				}
			}
		}
	}
	
//	======================== Getters ========================

	/** @return the root of this tree. */
	public CTNode getRoot()
	{
		return n_root;
	}
	
	/**
	 * @return a node in this tree with the specific terminal ID and height.
	 * @param terminalID {@link CTNode#i_terminalID}.
	 * @param height the height (starting at 0) of the node from its first terminal node.
	 */
	public CTNode getNode(int terminalID, int height)
	{
		CTNode node = getTerminal(terminalID);
		
		for (int i=height; i>0; i--)
			node = node.getParent();
		
		return node;
	}
	
	public CTNode getNode(PBLocation location)
	{
		return getNode(location.getTerminalID(), location.getHeight());
	}
	
	/** @return a terminal node in this tree with the specific ID. */
	public CTNode getTerminal(int terminalID)
	{
		return n_termainals.get(terminalID);
	}
	
	/** @return the list of all terminal nodes. */
	public List<CTNode> getTerminalList()
	{
		return n_termainals;
	}
	
	/** @return a terminal node in this tree with respect to its token ID. */
	public CTNode getToken(int tokenID)
	{
		return n_tokens.get(tokenID);
	}
	
	/** @return the list of all terminal nodes discarding empty categories. */
	public List<CTNode> getTokenList()
	{
		return n_tokens;
	}
	
	/** @return the antecedent corresponding to the specific index if exists; otherwise, {@code null}. */
	public CTNode getAntecedent(int index)
	{
		return getAntecedentAux(index, n_root);
	}
	
	/** Called by {@link CTTree#getAntecedent(int)}. */
	private CTNode getAntecedentAux(int index, CTNode curr)
	{
		if (curr.getEmptyCategoryIndex() == index)
			return curr;
		else if (curr.getGappingRelationIndex() == index)
		{
			int t = curr.getEmptyCategoryIndex();
			curr.setEmptyCategoryIndex(curr.getGappingRelationIndex());
			curr.setGappingRelationIndex(t);
			
			return curr;
		}
		
		CTNode ante;
		
		for (CTNode child : curr.getChildrenList())
		{
			if ((ante = getAntecedentAux(index, child)) != null)
				return ante;
		}
		
		return null;
	}
	
	/** @return a list of empty categories with he specific co-index if exists; otherwise, {@code null}. */
	public List<CTNode> getEmptyCategoryList(int index)
	{
		return m_nulls.get(index);
	}
	
//	======================== Boolean ========================
	
	/** @return {@code true} if both the specific terminal ID and height are within the range of this tree. */
	public boolean isRange(int terminalId, int height)
	{
		if (terminalId < 0 || terminalId >= n_termainals.size())
			return false;
		
		CTNode node = n_termainals.get(terminalId);
		
		for (int i=height; i>0; i--)
		{
			if (!node.hasParent())
				return false;
			
			node = node.getParent();
		}
		
		return true;
	}
	
	public boolean isRange(PBLocation loc)
	{
		return isRange(loc.getTerminalID(), loc.getHeight());
	}
	
	public boolean compareBrackets(CTTree tree)
	{
		int i, size = n_termainals.size();
		
		if (size != tree.getTerminalList().size())
			return false;
		
		CTNode node1, node2;
		
		for (i=0; i<size; i++)
		{
			node1 = getTerminal(i);
			node2 = tree.getTerminal(i);
			
			if (node1.getDistanceToTop() != node2.getDistanceToTop())
				return false;
			
			if (!node1.isWordForm(node2.getWordForm()))
				return false;
		}
		
		return true;
	}
	
//	======================== Normalize ========================
	
	/** Normalizes empty category indices and gapping relation indices of the specific tree. */
	public void normalizeIndices()
	{
		// retrieve all co-indexes
		Int2ObjectMap<List<CTNode>> mOrg = new Int2ObjectOpenHashMap<List<CTNode>>();
		getCoIndexMap(n_root, mOrg);
		if (mOrg.isEmpty())	return;
		
		List<Entry<Integer,List<CTNode>>> ps = new ArrayList<>(mOrg.entrySet());
		Collections.sort(ps, Entry.comparingByKey());
		
		Int2IntMap mNew = new Int2IntOpenHashMap();
		int coIndex = 1, last, i;
		boolean isAnteFound;
		List<CTNode> list;
		CTNode curr, ec;
		
		for (Entry<Integer,List<CTNode>> p : ps)
		{
			list = p.getValue();
			last = list.size() - 1;
			isAnteFound = false;
			
			for (i=last; i>=0; i--)
			{
				curr = list.get(i);
				
				if (curr.isEmptyCategoryTerminal())
				{
					ec = curr.getTerminalList().get(0);
					
					if (i == last || isAnteFound || CTLibEn.isDiscontinuousConstituent(ec) || CTLibEn.containsCoordination(curr.getLowestCommonAncestor(list.get(i+1))))
						curr.setEmptyCategoryIndex(-1);
					else
						curr.setEmptyCategoryIndex(coIndex++);

					if (isAnteFound || i > 0)
						ec.appendWordForm("-"+coIndex);
				}
				else if (isAnteFound)
				{
					curr.setEmptyCategoryIndex(-1);
				}
				else
				{
					curr.setEmptyCategoryIndex(coIndex);
					mNew.put(p.getKey().intValue(), coIndex);
					isAnteFound  = true;
				}
			}
			
			coIndex++;
		}
		
		int[] lastIndex = {coIndex};
		remapGapIndices(mNew, lastIndex, n_root);
	}
	
	/** Called by {@link #normalizeIndices()}. */
	private void getCoIndexMap(CTNode curr, Int2ObjectMap<List<CTNode>> map)
	{
		if (!curr.isTerminal())
		{
			if (curr.getEmptyCategoryIndex() != -1)
			{
				int key = curr.getEmptyCategoryIndex();
				List<CTNode> list;
				
				if (map.containsKey(key))
					list = map.get(key);
				else
				{
					list = new ArrayList<CTNode>();
					map.put(key, list);
				}
				
				list.add(curr);
			}
			
			for (CTNode child : curr.getChildrenList())
				getCoIndexMap(child, map);
		}
		else if (curr.isEmptyCategory())
		{
			if (curr.isWordForm("*0*"))
				curr.setWordForm("0");
		}
	}
	
	/** Called by {@link #normalizeIndices()}. */
	private void remapGapIndices(Int2IntMap map, int[] lastIndex, CTNode curr)
	{
		int gapIndex = curr.getGappingRelationIndex();
		
		if (map.containsKey(gapIndex))
		{
			curr.setGappingRelationIndex(map.get(gapIndex));
		}
		else if (gapIndex != -1)
		{
			curr.setGappingRelationIndex(lastIndex[0]);
			map.put(gapIndex, lastIndex[0]++);
		}
		
		for (CTNode child : curr.getChildrenList())
			remapGapIndices(map, lastIndex, child);
	}

//	======================== Strings ========================
	
	/** @return {@link #toForms(boolean, String)}, where {@code includeEmptyCategories=false, delim=" "}. */
	public String toForms()
	{
		return toForms(false, StringConst.SPACE);
	}
	
	/**
	 * @return the string containing ordered word-forms of this tree.
	 * @param includeEmptyCategories if {@code true}, include forms of empty categories.
	 */
	public String toForms(boolean includeEmptyCategories, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		if (includeEmptyCategories)
		{
			for (CTNode node : n_termainals)
			{
				build.append(delim);
				build.append(node.getWordForm());
			}	
		}
		else
		{
			for (CTNode node : n_tokens)
			{
				build.append(delim);
				build.append(node.getWordForm());
			}
		}
		
		return build.length() == 0 ? StringConst.EMPTY : build.substring(delim.length());
	}
	
	@Override
	/** @see CTNode#toString(). */
	public String toString()
	{
		return n_root.toString();
	}
	
	/** @see CTNode#toStringLine(). */
	public String toStringLine()
	{
		return n_root.toStringLine();
	}
	
	/** @see CTNode#toString(boolean, boolean, String). */
	public String toString(boolean includeLineNumbers, boolean includeAntecedentPointers, String delim)
	{
		return n_root.toString(includeLineNumbers, includeAntecedentPointers, delim);
	}
	
	public String toColumnPOS(boolean includeEmptyCategories, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (CTNode node : n_tokens)
		{
			build.append(node.getWordForm());
			build.append(delim);
			build.append(node.getConstituentTag());
			build.append("\n");
		}
		
		return build.toString();
	}
	
//	======================== PropBank ========================
	
	/** Assigns PropBank locations to all nodes. */
	public void initPBLocations()
	{
		int terminalID, height;
		
		for (CTNode node : n_termainals)
		{
			terminalID = node.getTerminalID();
			height = 0;
			node.setPBLocation(terminalID, height);
			
			while (node.hasParent() && node.getParent().getPBLocation() == null)
			{
				node = node.getParent();
				node.setPBLocation(terminalID, ++height);
			}
		}
	}
	
	public void initPropBank()
	{
		initPropBankAux(n_root);
	}
	
	private void initPropBankAux(CTNode node)
	{
		node.initPropBank();
		
		for (CTNode child : node.getChildrenList())
			initPropBankAux(child);
	}
	
	/** PRE: {@link #initPBLocations()} and {@link #initPropBank()} must be called. */
	public void initPBInstance(PBInstance instance)
	{
		CTNode pNode = getTerminal(instance.getPredicateID()), aNode;
		String label;
		
		pNode.setPBRolesetID(instance.getRolesetID());
		if (!hasPropBank()) initPropBank();
		
		for (PBArgument arg : instance.getArgumentList())
		{
			label = arg.getLabel();
			
			if (PBLib.isLinkArgument(label))	continue;
			if (PBLib.isUndefinedLabel(label))	continue;
			
			for (PBLocation loc : arg.getLocationList())
			{
				aNode = getNode(loc);
				
				if (aNode != pNode)
					aNode.addPBHead(new PBArc(pNode, label));
			}
		}
	}
	
	public List<CTNode> getPBHeadList()
	{
		List<CTNode> predicates = new ArrayList<>();
		
		for (CTNode node : n_tokens)
		{
			if (node.isPBHead())
				predicates.add(node);
		}
		
		return predicates;
	}
	
	public boolean hasPropBank()
	{
		return n_root.getPBHeads() != null;
	}
	
	public boolean hasNamedEntity()
	{
		return getTerminal(0).getNamedEntityTag() != null;
	}
}