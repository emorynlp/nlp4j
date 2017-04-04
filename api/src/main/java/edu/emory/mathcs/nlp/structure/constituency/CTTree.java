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
package edu.emory.mathcs.nlp.structure.constituency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.propbank.PBInstance;
import edu.emory.mathcs.nlp.structure.propbank.PBLocation;
import edu.emory.mathcs.nlp.structure.util.PBLib;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
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
	private CTNode root;
	private List<CTNode> tokens;
	private List<CTNode> terminals;
	private Int2ObjectMap<List<CTNode>> empty_category_map;
	
//	======================== Constructors ========================
	
	public CTTree(CTNode root)
	{
		this.root = root;
		init();
	}
	
	private void init()
	{
		empty_category_map = new Int2ObjectOpenHashMap<List<CTNode>>();
		terminals = root.getTerminals();
		tokens = new ArrayList<>();
		
		for (int terminal_id=0; terminal_id < terminals.size(); terminal_id++)
		{
			CTNode node = terminals.get(terminal_id);
			
			if (node.isEmptyCategory())
			{
				String form = node.getForm();
				int idx;
				
				if ((idx = form.lastIndexOf(CTNode.DELIM_CO_INDEX)) >= 0)
				{
					int index = Integer.parseInt(form.substring(idx+1));
					node.setCoIndex(index);
					node.setForm(form.substring(0, idx));
					empty_category_map.computeIfAbsent(index, i -> new ArrayList<>()).add(node);
				}
			}
			else
			{
				node.setTokenID(tokens.size());
				tokens.add(node);
			}
			
			int height = 0;
			node.setLocation(terminal_id, height);

			while (node.hasParent())
			{
				node = node.getParent();
				if (node.getHeight() > 0) break;
				node.setLocation(terminal_id, ++height);
			}
		}
		
		root.flatten().forEach(n -> initAntecedent(n));
	}
	
	private void initAntecedent(CTNode node)
	{
		if (!node.isEmptyCategory() && node.hasCoIndex())
		{
			List<CTNode> es;
			
			if ((es = empty_category_map.get(node.getCoIndex())) != null)
			{
				es.forEach(e -> e.setAntecedent(node));
			}
			// for error cases where co-index is annotated as gap-index
			else if ((es = empty_category_map.get(node.getGapIndex())) != null)
			{
				int t = node.getCoIndex();
				node.setCoIndex(node.getGapIndex());
				node.setGapIndex(t);
				es.forEach(e -> e.setAntecedent(node));
			}
		}
	}
	
//	======================== Getters ========================

	/** @return the root of this tree. */
	public CTNode getRoot()
	{
		return root;
	}
	
	/**
	 * @return the node in this tree with the specific terminal ID and height if exists; otherwise, null.
	 * @param terminal_id the ID of the first terminal node in the subtree of the target node.
	 * @param height the height (starting at 0) of the target node from its first terminal node.
	 */
	public CTNode getNode(int terminal_id, int height)
	{
		return DSUtils.isRange(terminals, terminal_id) ? getTerminal(terminal_id).getAncestor(height) : null; 
	}
	
	public CTNode getNode(PBLocation loc)
	{
		return getNode(loc.getTerminalID(), loc.getHeight());
	}
	
	/** @return a terminal node in this tree with the specific ID. */
	public CTNode getTerminal(int id)
	{
		return terminals.get(id);
	}
	
	/** @return the list of all terminal nodes. */
	public List<CTNode> getTerminals()
	{
		return terminals;
	}
	
	/** @return a terminal node in this tree with respect to its token ID. */
	public CTNode getToken(int tokenID)
	{
		return tokens.get(tokenID);
	}
	
	/** @return the list of all terminal nodes discarding empty categories. */
	public List<CTNode> getTokens()
	{
		return tokens;
	}
	
	public Int2ObjectMap<List<CTNode>> getEmptyCategoryMap()
	{
		return empty_category_map;
	}
	
	/** @return a list of empty categories with he specific co-index if exists; otherwise, {@code null}. */
	public List<CTNode> getEmptyCategories(int index)
	{
		return empty_category_map.get(index);
	}
	
	public List<CTNode> getPredicates()
	{
		return tokens.stream().filter(n -> n.isPredicate()).collect(Collectors.toList());
	}
	
	public boolean containsOnlyEmptyCategories()
	{
		return tokens.isEmpty();
	}
	
	/**
	 * Removes the first node from the tree, substitutes the second node with the first node, and adds all function tags in the second node to the first node. 
	 * @param fst the first node.
	 * @param snd the second node.
	 */
	protected void substitute(CTNode fst, CTNode snd)
	{
		fst.removeSelf();
		snd.getParent().replaceChild(snd, fst);
		fst.addFunctionTags(snd.getFunctionTags());
	}
	
	public Stream<CTNode> flatten()
	{
		return root.flatten();
	}
	
//	/** @return {@code true} if both the specific terminal ID and height are within the range of this tree. */
//	public boolean isRange(int terminal_id, int height)
//	{
//		return getNode(terminal_id, height) != null;
//	}
//	
//	public boolean compareBrackets(CTTree tree)
//	{
//		int i, size = terminals.size();
//		
//		if (size != tree.getTerminals().size())
//			return false;
//		
//		CTNode node1, node2;
//		
//		for (i=0; i<size; i++)
//		{
//			node1 = getTerminal(i);
//			node2 = tree.getTerminal(i);
//			
//			if (node1.distanceToTop() != node2.distanceToTop())
//				return false;
//			
//			if (!node1.isForm(node2.getForm()))
//				return false;
//		}
//		
//		return true;
//	}
	
//	======================== Normalization ========================
	
	/** Normalizes empty category indices and gapping relation indices of the specific tree. */
	public void normalizeIndices()
	{
		// retrieve all co-indexes
		Int2ObjectMap<List<CTNode>> m_org = getCoIndexMap(root, new Int2ObjectOpenHashMap<List<CTNode>>());
		if (m_org.isEmpty())	return;
		
		List<Entry<Integer,List<CTNode>>> ps = new ArrayList<>(m_org.entrySet());
		Collections.sort(ps, Entry.comparingByKey());
		
		Int2IntMap m_new = new Int2IntOpenHashMap();
		int coIndex = 1, last, i;
		boolean ante_found;
		List<CTNode> list;
		CTNode curr, ec;
		
		for (Entry<Integer,List<CTNode>> p : ps)
		{
			list = p.getValue();
			last = list.size() - 1;
			ante_found = false;
			
			for (i=last; i>=0; i--)
			{
				curr = list.get(i);
				
				if (curr.isEmptyCategoryPhrase())
				{
					ec = curr.getTerminals().get(0);
					
					if (i == last || ante_found || PTBLib.isDiscontinuousConstituent(ec) || PTBLib.containsCoordination(curr.getLowestCommonAncestor(list.get(i+1))))
						curr.setCoIndex(-1);
					else
						curr.setCoIndex(coIndex++);

					if (ante_found || i > 0)
						ec.setCoIndex(coIndex);
				}
				else if (ante_found)
				{
					curr.setCoIndex(-1);
				}
				else
				{
					curr.setCoIndex(coIndex);
					m_new.put(p.getKey().intValue(), coIndex);
					ante_found  = true;
				}
			}
			
			coIndex++;
		}
		
		int[] lastIndex = {coIndex};
		remapGapIndices(m_new, lastIndex, root);
	}
	
	/** Called by {@link #normalizeIndices()}. */
	private Int2ObjectMap<List<CTNode>> getCoIndexMap(CTNode curr, Int2ObjectMap<List<CTNode>> map)
	{
		if (curr.isEmptyCategory())
		{
			if (curr.isForm("*0*"))
				curr.setForm("0");
		}
		else
		{
			if (curr.hasCoIndex())
				map.computeIfAbsent(curr.getCoIndex(), i -> new ArrayList<>()).add(curr);
			
			for (CTNode child : curr.getChildren())
				getCoIndexMap(child, map);
		}
		
		return map;
	}
	
	/** Called by {@link #normalizeIndices()}. */
	private void remapGapIndices(Int2IntMap map, int[] lastIndex, CTNode curr)
	{
		int gapIndex = curr.getGapIndex();
		
		if (map.containsKey(gapIndex))
		{
			curr.setGapIndex(map.get(gapIndex));
		}
		else if (gapIndex != -1)
		{
			curr.setGapIndex(lastIndex[0]);
			map.put(gapIndex, lastIndex[0]++);
		}
		
		for (CTNode child : curr.getChildren())
			remapGapIndices(map, lastIndex, child);
	}
	
	public void set(PBInstance instance)
	{
		CTNode pred = getTerminal(instance.getPredicateID()), node;
		pred.setFrameID(instance.getFrameID());
		instance.setTree(this);
		
		for (PBArgument arg : instance.getArguments())
		{
			String label = arg.getLabel();
			if (PBLib.isLinkArgument(label))	continue;
			if (PBLib.isUndefinedLabel(label))	continue;
			
			for (PBLocation loc : arg.getLocations())
			{
				node = getNode(loc);
				if (node != pred) node.addSemanticHead(new CTArc(pred, label));
			}
		}
	}

//	======================== Strings ========================
	
	/** @return {@link #toForms(String, boolean)}, where {@code includeEmptyCategories=false, delim=" "}. */
	public String toForms()
	{
		return toForms(StringConst.SPACE, false);
	}
	
	/**
	 * @param empty_category if {@code true}, include forms of empty categories.
	 * @return the string containing ordered word-forms of this tree.
	 */
	public String toForms(String delim, boolean empty_category)
	{
		return Joiner.join(empty_category ? terminals : tokens, delim, CTNode::getForm); 
	}
	
	@Override
	/** @see CTNode#toString(). */
	public String toString()
	{
		return root.toString();
	}
	
	/** @see CTNode#toStringLine(). */
	public String toStringLine()
	{
		return root.toStringLine();
	}
	
	/** @see CTNode#toString(boolean, boolean, String). */
	public String toString(boolean line_number, boolean antecedent, String delim)
	{
		return root.toString(line_number, antecedent, delim);
	}
}