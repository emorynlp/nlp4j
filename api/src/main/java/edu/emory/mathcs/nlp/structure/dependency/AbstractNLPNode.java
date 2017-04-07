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
package edu.emory.mathcs.nlp.structure.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.feature.Direction;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.structure.node.AbstractNode;
import edu.emory.mathcs.nlp.structure.util.Arc;
import edu.emory.mathcs.nlp.structure.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNLPNode<N extends AbstractNLPNode<N>> extends AbstractNode<N> implements Comparable<N>
{
	private static final long serialVersionUID = -6890831718184647451L;
	static final String ROOT_TAG = "@#r$%";
	
	// fields
	protected String dependency_label;
	protected List<NLPArc<N>> secondary_parents;
    
	// offsets
	protected int start_offset;
	protected int end_offset;
    
	// lexicons
	protected Map<Field, Object> lexicons;
	
//	============================== Constructors ==============================
	
	public AbstractNLPNode()
	{
		this(-1, null);
	}
	
	public AbstractNLPNode(int id, String form)
	{
		this(id, form, null);
	}
	
	public AbstractNLPNode(int token_id, String form, String syntactic_tag)
	{
		this(token_id, form, null, syntactic_tag, new FeatMap());
	}
	
	public AbstractNLPNode(int token_id, String form, String lemma, String syntactic_tag, FeatMap feat_map)
	{
		this(token_id, form, lemma, syntactic_tag, feat_map, null, null);
	}
	
	public AbstractNLPNode(int token_id, String word_form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		this(token_id, word_form, lemma, syntactic_tag, named_entity_tag, feat_map, null, null);
	}
	
	public AbstractNLPNode(int token_id, String form, String lemma, String syntactic_tag, FeatMap feat_map, N parent, String dependency_label)
	{
		this(token_id, form, lemma, syntactic_tag, null, feat_map, parent, dependency_label);
	}
	
	public AbstractNLPNode(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map, N parent, String dependency_label)
	{
		init(token_id, form, lemma, syntactic_tag, named_entity_tag, feat_map, parent, dependency_label);
	}
	
	public void init(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map, N parent, String dependency_label)
	{
		init(token_id, form, lemma, syntactic_tag, named_entity_tag, feat_map);
		init(parent, dependency_label);
	}
	
	protected void init(N parent, String dependency_label)
	{
		setParent(parent, dependency_label);
		secondary_parents = new ArrayList<>();
		lexicons = new HashMap<>();
	}
	
	public N toRoot()
	{
		init(0, ROOT_TAG, ROOT_TAG, ROOT_TAG, ROOT_TAG, new FeatMap(), null, null);
		return self();
	}
	
//	============================== Abstract Methods ==============================
	
	@Override
	public int getChildIndex(N node)
	{
		return Collections.binarySearch(children, node);
	}
	
	@Override
	protected int getDefaultIndex(List<N> list, N node)
	{
		return DSUtils.binarySearch(list, node);
	}
	
//	============================== Fields ==============================

	@Override
	public String get(Field field)
	{
		String value = super.get(field);
		if (value != null) return value;
		
		switch (field)
		{
		case dependency_label: return getDependencyLabel();
		default: return null;
		}
	}
	
	public String getDependencyLabel()
	{
		return dependency_label;
	}
	
	public void setDependencyLabel(String label)
	{
		dependency_label = label;
	}

	public Set<String> getDependentFields(Field field)
	{
		return children.stream().map(n -> n.get(field)).collect(Collectors.toSet());
	}
	
	/** @return true if the dependency label of this node equals to the specific label; otherwise, false. */
	public boolean isDependencyLabel(String label)
	{
		return label.equals(dependency_label);
	}
	
	/** @return true if the dependency label of this node equals to any of the specific labels; otherwise, false. */
	public boolean isDependencyLabel(String... labels)
	{
		for (String label : labels)
		{
			if (isDependencyLabel(label))
				return true;
		}
		
		return false;
	}
	
	/** @return true if the dependency label of this node matches the specific pattern; otherwise, false. */
	public boolean isDependencyLabel(Pattern pattern)
	{
		return pattern.matcher(dependency_label).find();
	}
	
	/** {@link #isChildOf(N)} && {@link #isDependencyLabel(String)}. */
	public boolean isChildOf(N node, String label)
	{
		return isChildOf(node) && isDependencyLabel(label);
	}
	
//	============================== Offsets ==============================
	
	public int getStartOffset()
    {
        return start_offset;
    }
	
	public int getEndOffset()
    {
        return end_offset;
    }
	
	public void setStartOffset(int offset)
    {
        start_offset = offset;
    }

    public void setEndOffset(int offset)
    {
        end_offset = offset;
    }
	
//	============================== Lexicons ==============================
	
    @SuppressWarnings("unchecked")
	public <T>T getLexicon(Field field)
    {
    	return (T)lexicons.get(field);
    }
    
    public <T>void addLexicon(Field field, T lexicon)
    {
    	lexicons.put(field, lexicon);
    }
	
//	============================== Descendents ==============================
	
	/** Adds a child to the appropriate position with the specific label. */
	public void addChild(N child, String label)
	{
		addChild(child);
		setDependencyLabel(label);
	}
	
	/** @return {@link #getLeftMostChild(int)}, where {@code order = 0}. */
	public N getLeftMostChild()
	{
		return getLeftMostChild(0);
	}

	/**
	 * @param order displacement (0: leftmost, 1: 2nd leftmost, etc.)
	 * @return the order'th leftmost dependent of this node if exists; otherwise, null.
	 * The leftmost dependent must be on the left-hand side of this node.
	 */
	public N getLeftMostChild(int order)
	{
		N node = getFirstChild(order);
		return node != null && compareTo(node) < 0 ? node : null;
	}
	
	/** @return {@link #getRightMostChild(int)}, where {@code order = 0}. */
	public N getRightMostChild()
	{
		return getRightMostChild(0);
	}
	
	/**
	 * @param order displacement (0: rightmost, 1: 2nd rightmost, etc.)
	 * @return the order'th rightmost dependent of this node if exists; otherwise, null.
	 * The rightmost dependent must be on the right-hand side of this node.
	 */
	public N getRightMostChild(int order)
	{
		N node = getLastChild(order);
		return node != null && compareTo(node) > 0 ? node : null;
	}
	
	/** @return {@link #getLeftNearestChild(int)}, where {@code order = 0}. */
	public N getLeftNearestChild()
	{
		return getLeftNearestChild(0);
	}
	
	/**
	 * @param order displacement (0: left-nearest, 1: 2nd left-nearest, etc.).
	 * @return the order'th left-nearest dependent of this node if exists; otherwise, null.
	 * The left-nearest dependent must be on the left-hand side of this node.
	 */
	public N getLeftNearestChild(int order)
	{
		return getChild(getDefaultIndex(children, self()) - order - 1);
	}
	
	/** @return {@link #getRightNearestChild(int)}, where {@code order = 0}. */
	public N getRightNearestChild()
	{
		return getRightNearestChild(0);
	}
	
	/**
	 * @param order displacement (0: right-nearest, 1: 2nd right-nearest, etc.).
	 * @return the order'th right-nearest dependent of this node if exists; otherwise, null.
	 * The right-nearest dependent must be on the right-hand side of this node.
	 */
	public N getRightNearestChild(int order)
	{
		return getChild(getDefaultIndex(children, self()) + order);
	}

	/** @return the list of dependents on the left-hand side of this node. */
	public List<N> getLeftChildren()
	{
		int index = getDefaultIndex(children, self());
		return children.subList(0, index);
	}

	/** @return the list of dependents on the right-hand side of this node. */
	public List<N> getRightChildren()
	{
		int index = getDefaultIndex(children, self());
		return children.subList(index, children.size());
	}
	
	/** @return the list of all descendents (excluding this node). */
	public List<N> getDescendants()
	{
		return flatten().collect(Collectors.toList());
	}
	
	/**
	 * @param depth the level of the descendents to be retrieved (1: children, 2: children + grand-children, etc.).
	 * @return the list of descendents (excluding this node).
	 */
	public List<N> getDescendants(int depth)
	{
		List<N> list = new ArrayList<>();
		return depth > 0 ? getDescendantListAux(depth-1, self(), list) : list;
	}
	
	private List<N> getDescendantListAux(int depth, N node, List<N> list)
	{
		list.addAll(node.getChildren());
		
		if (depth-- > 0)
		{
			for (N dep : node.getChildren())
				getDescendantListAux(depth, dep, list);
		}
		
		return list;
	}
	
	/**
	 * @see #getLeftValency()
	 * @see #getRightValency()
	 * @see #getAllValency() 
	 */
	public String getValency(Direction direction)
	{
		switch (direction)
		{
		case left : return getLeftValency();
		case right: return getRightValency();
		case all  : return getLeftValency()+"-"+getRightValency();
		default: return null;
		}
	}
	
	/**
	 * @return "<"  if there is only one child on the left-hand side,
	 *         "<<" if there are more than one child on the left-hand side,
	 *         null if there is no child on the left-hand side.
	 */
	public String getLeftValency()
	{
		if (getLeftMostChild(1) != null) return "<<";
		if (getLeftMostChild()  != null) return "<";
		return null;
	}
	
	/**
	 * @return ">"  if there is only one child on the right-hand side,
	 *         "<<" if there are more than one child on the right-hand side,
	 *         null if there is no child on the right-hand side.
	 */
	public String getRightValency()
	{
		if (getRightMostChild(1) != null) return ">>";
		if (getRightMostChild()  != null) return ">";
		return null;
	}
	
	/** @return {@link #getLeftValency()} + {@link #getRightValency()} if exists; otherwise, null. */
	public String getAllValency()
	{
		String l = getLeftValency();
		String r = getRightValency();
		if (l == null) return r;
		if (r == null) return l;
		return l+r;
	}
	
	public void adaptChildren(N from)
	{
		for (N d : new ArrayList<>(from.children))
			d.setParent(self());
	}
	
//	============================== Ancestors ==============================

	/** Sets the parent of this node with the specific label. */
	public void setParent(N parent, String label)
	{
		setParent(parent);
		setDependencyLabel(label);
	}
	
//	============================== SEMANTICS ==============================

	/** @return a list of all semantic head arc of the node. */
	public List<NLPArc<N>> getSecondaryParents()
	{
		return secondary_parents;
	}
	
	/** @return a list of all semantic head arc of the node with the given label. */
	public List<NLPArc<N>> getSecondaryParents(String label)
	{
		return secondary_parents.stream().filter(arc -> arc.isLabel(label)).collect(Collectors.toList());
	}
	
	/** @return semantic arc relationship between the node and another given node. */
	public NLPArc<N> getSecondaryParent(N node)
	{
		return secondary_parents.stream().filter(arc -> arc.isNode(node)).findAny().orElse(null);
	}
	
	/** @return the semantic arc relationship between the node and another given node with a given label. */
	public NLPArc<N> getSecondaryParent(N node, String label)
	{
		return secondary_parents.stream().filter(arc -> arc.equals(node, label)).findAny().orElse(null);
	}
	
	/** @return the semantic arc relationship between the node and another given node with a given pattern. */
	public NLPArc<N> getSecondaryParent(N node, Pattern pattern)
	{
		return secondary_parents.stream().filter(arc -> arc.equals(node, pattern)).findAny().orElse(null);
	}
	
	/** @return the first node that is found to have the semantic head of the given label from the node. */
	public N getSecondaryParentAny(String label)
	{
		NLPArc<N> a = secondary_parents.stream().filter(arc -> arc.isLabel(label)).findAny().orElse(null);
		return a != null ? a.getNode() : null;
	}
	
	/** @return the first node that is found to have the semantic head of the given pattern from the node. */
	public N getSecondaryParentAny(Pattern pattern)
	{
		NLPArc<N> a = secondary_parents.stream().filter(arc -> arc.isLabel(pattern)).findAny().orElse(null);
		return a != null ? a.getNode() : null;
	}
	
	/** @return the semantic label of the given in relation to the node. */
	public String getSecondaryLabel(N node)
	{
		NLPArc<N> arc = getSecondaryParent(node);
		return arc != null ? arc.getLabel() : null;
	}
	
	/** @param arcs {@code Collection<DEPArc>} of the semantic heads. */
	public void addSecondaryParents(Collection<NLPArc<N>> arcs)
	{
		secondary_parents.addAll(arcs);
	}
	
	/** Adds a node a give the given semantic label to the node. */
	public void addSecondaryParent(N head, String label)
	{
		addSecondaryParent(new NLPArc<>(head, label));
	}
	
	/** Adds a semantic arc to the node. */
	public void addSecondaryParent(NLPArc<N> arc)
	{
		secondary_parents.add(arc);
	}
	
	/** Sets semantic heads of the node. */
	public void setSecondaryParents(List<NLPArc<N>> arcs)
	{
		secondary_parents = arcs;
	}
	
	/** Removes all semantic heads of the node in relation to a given node.
	 * @return {@code true}, else {@code false} if nothing gets removed. 
	 */
	public boolean removeSecondaryParent(N node)
	{
		for (NLPArc<N> arc : secondary_parents)
		{
			if (arc.isNode(node))
				return secondary_parents.remove(arc);
		}
		
		return false;
	}
	
	/** Removes a specific semantic head of the node. */
	public boolean removeSecondaryParent(NLPArc<N> arc)
	{
		return secondary_parents.remove(arc);
	}
	
	/** Removes a collection of specific semantic heads of the node. */
	public void removeSecondaryParents(Collection<NLPArc<N>> arcs)
	{
		secondary_parents.removeAll(arcs);
	}
	
	/** Removes all semantic heads of the node that have the given label. */
	public void removeSecondaryParents(String label)
	{
		secondary_parents.removeAll(getSecondaryParents(label));
	}
	
	/** Removes all semantic heads of the node. */
	public List<NLPArc<N>> clearSecondaryParents()
	{
		List<NLPArc<N>> backup = new ArrayList<>(secondary_parents);
		secondary_parents.clear();
		return backup;
	}
	
	/** @return {@code true}, else {@code false} if there is no DEPArc between the two nodes. */
	public boolean isSecondaryChildOf(N node)
	{
		return getSecondaryParent(node) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no DEPArc with the given label. */
	public boolean isSecondaryChildOf(String label)
	{
		return getSecondaryParentAny(label) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no DEPArc with the given pattern. */
	public boolean isSecondaryChildOf(Pattern pattern)
	{
		return getSecondaryParentAny(pattern) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no DEPArc with the given label between the two node. */
	public boolean isSecondaryChildOf(N node, String label)
	{
		return getSecondaryParent(node, label) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no DEPArc with the given pattern between the two node. */
	public boolean isSecondaryChildOf(N node, Pattern pattern)
	{
		return getSecondaryParent(node, pattern) != null;
	}

	/**
	 * Consider this node as a predicate.
	 * @param maxDepth  > 0.
	 * @param maxHeight > 0.
	 * @return list of (argument, lowest common ancestor) pairs.
	 */
	public List<Pair<N,N>> getArgumentCandidateList(int maxDepth, int maxHeight)
	{
		List<Pair<N,N>> list = new ArrayList<>();
		int i, j, beginIndex, endIndex = 0;
		N lca = self(), prev;
		
		// descendents
		for (N node : lca.getChildren())
			list.add(new Pair<>(node, lca));
		
		for (i=1; i<maxDepth; i++)
		{
			if (endIndex == list.size()) break;
			beginIndex = endIndex;
			endIndex   = list.size();
			
			for (j=beginIndex; j<endIndex; j++)
			{
				for (N node : list.get(j).o1.getChildren())
					list.add(new Pair<>(node, lca));
			}
		}
		
		// ancestors
		for (i=0; i<maxHeight; i++)
		{
			prev = lca;
			lca  = lca.getParent();
			if (lca == null || !lca.hasParent()) break;
			list.add(new Pair<>(lca, lca));
			
			for (N node : lca.getChildren())
				if (node != prev) list.add(new Pair<>(node, lca));
		}
		
		return list;
	}
	
//	============================== Helpers ==============================

	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(StringConst.TAB);
		
		join.add(Integer.toString(token_id));
		join.add(toString(form));
		join.add(toString(lemma));
		join.add(toString(syntactic_tag));
		join.add(feat_map.toString());
		toStringDependency(join);
		join.add(toString(secondary_parents));
		join.add(toString(named_entity_tag));
		
		return join.toString();
	}
	
	private String toString(String s)
	{
		return (s == null) ? TSVReader.BLANK : s;
	}
	
	private void toStringDependency(StringJoiner join)
	{
		if (hasParent())
		{
			join.add(Integer.toString(parent.token_id));
			join.add(toString(dependency_label));
		}
		else
		{
			join.add(TSVReader.BLANK);
			join.add(TSVReader.BLANK);
		}
	}
	
	private <T extends Arc<N>>String toString(List<T> arcs)
	{
		if (arcs == null || arcs.isEmpty())
			return TSVReader.BLANK;
		
		Collections.sort(arcs);
		return Joiner.join(arcs, Arc.ARC_DELIM);
	}

	
	@Override
	public int compareTo(N o)
	{
		return token_id - o.token_id;
	}
}
