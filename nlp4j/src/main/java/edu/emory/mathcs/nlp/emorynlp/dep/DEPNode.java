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
package edu.emory.mathcs.nlp.emorynlp.dep;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.collection.list.SortedArrayList;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.emorynlp.pos.POSNode;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.Direction;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.Field;
import edu.emory.mathcs.nlp.emorynlp.utils.node.FeatMap;
import edu.emory.mathcs.nlp.emorynlp.utils.reader.TSVReader;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPNode extends POSNode implements Comparable<DEPNode>
{
	private static final long serialVersionUID = 3794720014142939766L;
	static final String ROOT_TAG = "@#r$%";

	/** The dependency label of this node. */
	protected String dependency_label;
	/** The dependency head of this node. */
	protected DEPNode dependency_head;
	/** The sorted list of all dependents of this node (default: empty). */
	protected SortedArrayList<DEPNode> dependent_list;
	
//	====================================== Constructors ======================================
	
	/** Creates an artificial root node. */
	public DEPNode()
	{
		this(0, ROOT_TAG, ROOT_TAG, ROOT_TAG, new FeatMap());
	}
	
	public DEPNode(int id, String form)
	{
		super(id, form, null, null, new FeatMap());
		dependent_list = new SortedArrayList<>();
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, FeatMap feats)
	{
		super(id, form, lemma, posTag, feats);
		dependent_list = new SortedArrayList<>();
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, FeatMap feats, DEPNode head, String label)
	{
		super(id, form, lemma, posTag, feats);
		dependent_list = new SortedArrayList<>();
		setHead(head);
		setLabel(label);
	}
	
//	====================================== GETTERS ======================================
	
	/** @return the dependency label of this node if exists; otherwise, null. */
	public String getLabel()
	{
		return dependency_label;
	}
	
	/** @return the dependency head of this node if exists; otherwise, null. */
	public DEPNode getHead()
	{
		return dependency_head;
	}

	/** @return the dependency grand-head of the node if exists; otherwise, null. */
	public DEPNode getGrandHead()
	{
		DEPNode head = getHead();
		return (head == null) ? null : head.getHead();
	}
	
	/** Calls {@link #getLeftNearestSibling(int)}, where {@code order=0}. */
	public DEPNode getLeftNearestSibling()
	{
		return getLeftNearestSibling(0);
	}
	
	private int getSiblingID()
	{
		return Collections.binarySearch(dependency_head.getDependentList(), this);
	}
	
	/**
	 * @return the left sibling node with input displacement.
	 * @param order left displacement (0 - left-nearest, 1 - second left-nearest, etc.).
	 */
	public DEPNode getLeftNearestSibling(int order)
	{
		if (dependency_head != null)
		{
			order = getSiblingID() - order - 1;
			if (order >= 0) return dependency_head.getDependent(order);
		}
		
		return null;
	}
	
	public DEPNode getLeftNearestSibling(String label)
	{
		if (dependency_head != null)
		{
			DEPNode node;
			
			for (int i=getSiblingID()-1; i>=0; i--)
			{	
				node = dependency_head.getDependent(i);
				if (node.isLabel(label)) return node;
			}
		}
		
		return null;
	}

	/**
	 * Get the right nearest sibling node of the node.
	 * Calls {@link #getRightNearestSibling(int)}, where {@code order=0}.
	 * @return the right nearest sibling node
	 */
	public DEPNode getRightNearestSibling()
	{
		return getRightNearestSibling(0);
	}
	
	/**
	 * Get the right sibling node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * @param order right displacement
	 * @return the right sibling node with input displacement
	 */
	public DEPNode getRightNearestSibling(int order)
	{
		if (dependency_head != null)
		{
			order = getSiblingID() + order + 1;
			if (order < dependency_head.getDependentSize()) return dependency_head.getDependent(order);
		}
		
		return null;
	}
	
	public DEPNode getRightNearestSibling(String label)
	{
		if (dependency_head != null)
		{
			int i, size = dependency_head.getDependentSize();
			DEPNode node;
			
			for (i=getSiblingID()+1; i<size; i++)
			{	
				node = dependency_head.getDependent(i);
				if (node.isLabel(label)) return node;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the left most dependency node of the node.
	 * Calls {@link #getLeftMostDependent(int)}, where {@code order=0}
	 * @return the left most dependency node of the node
	 */
	public DEPNode getLeftMostDependent()
	{
		return getLeftMostDependent(0);
	}
	
	/**
	 * Get the left dependency node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * The leftmost dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the leftmost dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getLeftMostDependent(int order)
	{
		if (DSUtils.isRange(dependent_list, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.id < id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the right most dependency node of the node.
	 * Calls {@link #getRightMostDependent(int)}, where {@code order=0}. 
	 * @return the right most dependency node of the node
	 */
	public DEPNode getRightMostDependent()
	{
		return getRightMostDependent(0);
	}
	
	/**
	 * Get the right dependency node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * The rightmost dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the rightmost dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getRightMostDependent(int order)
	{
		order = getDependentSize() - 1 - order;
		
		if (DSUtils.isRange(dependent_list, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.id > id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the left nearest dependency node.
	 * Calls {@link #getLeftNearestDependent(int)}, where {@code order=0}.
	 * @return the left nearest dependency node
	 */
	public DEPNode getLeftNearestDependent()
	{
		return getLeftNearestDependent(0);
	}
	
	/**
	 * Get the left nearest dependency node with input displacement (0 - left-nearest, 1 - second left-nearest, etc.).
	 * The left nearest dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the left-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getLeftNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) - order - 1;
		return (index >= 0) ? getDependent(index) : null;
	}
	
	/**
	 * Get the right nearest dependency node.
	 * Calls {@link #getRightNearestDependent(int)}, where {@code order=0}. 
	 * @return the right nearest dependency node
	 */
	public DEPNode getRightNearestDependent()
	{
		return getRightNearestDependent(0);
	}
	
	/**
	 * Get the right nearest dependency node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * The right-nearest dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the right-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getRightNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) + order;
		return (index < getDependentSize()) ? getDependent(index) : null;
	}

	/**
	 * @param predicate takes a dependency node and compares the specific tag with the referenced function.
	 * @return the first-dependent with the specific label.
	 */
	public DEPNode getFirstDependent(String label, BiPredicate<DEPNode,String> predicate)
	{
		for (DEPNode node : dependent_list)
		{
			if (predicate.test(node, label))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the first dependency node of the node by label.
	 * @param pattern pattern label of the first-dependency node
	 * @return the first-dependency node of the specific label
	 */
	public DEPNode getFirstDependentByLabel(Pattern pattern)
	{
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(pattern))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node.
	 * @return list of all the dependency nodes of the node
	 */
	public List<DEPNode> getDependentList()
	{
		return dependent_list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label.
	 * @param label string label
	 * @return list of all the dependency nodes of the node by label
	 */
	public List<DEPNode> getDependentListByLabel(String label)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(label))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by labels set.
	 * @param y labels set
	 * @return list of all the dependency nodes of the node by labels set
	 */
	public List<DEPNode> getDependentListByLabel(Set<String> labels)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (labels.contains(node.getLabel()))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label pattern.
	 * @param y label pattern
	 * @return list of all the dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(pattern))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the left dependency nodes of the node.
	 * @return list of all the left dependency nodes of the node
	 */
	public List<DEPNode> getLeftDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id > id) break;
			list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the left dependency nodes of the node by label pattern.
	 * @param y label pattern
	 * @return list of all the left dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getLeftDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id > id) break;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the right dependency nodes of the node.
	 * @return list of all the right dependency nodes of the node
	 */
	public List<DEPNode> getRightDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id < id) continue;
			list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the right dependency nodes of the node by label pattern.
	 * @param y label pattern
	 * @return list of all the right dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getRightDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id < id) continue;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all grand-dependents of the node. 
	 * @return an unsorted list of grand-dependents of the node
	 */
	public List<DEPNode> getGrandDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
			list.addAll(node.getDependentList());
	
		return list;
	}
	
	/**
	 * Get the list of all descendant nodes of the node with specified height.
	 * If {@code height == 1}, return {@link #getDependentList()}.
	 * If {@code height > 1} , return all descendants within the depth.
	 * If {@code height < 1} , return an empty list.
	 * @param height height level of the descendant nodes
	 * @return an unsorted list of descendants.
	 */
	public List<DEPNode> getDescendantList(int height)
	{
		List<DEPNode> list = new ArrayList<>();
	
		if (height > 0)
			getDescendantListAux(this, list, height-1);
		
		return list;
	}
	
	private void getDescendantListAux(DEPNode node, List<DEPNode> list, int height)
	{
		list.addAll(node.getDependentList());
		
		if (height > 0)
		{
			for (DEPNode dep : node.getDependentList())
				getDescendantListAux(dep, list, height-1);
		}
	}
	
	/**
	 * Get any descendant node with POS tag.
	 * @param tag POS tag
	 * @return s descendant node with the POS tag
	 */
	public DEPNode getAnyDescendantByPOSTag(String tag)
	{
		return getAnyDescendantByPOSTagAux(this, tag);
	}
	
	private DEPNode getAnyDescendantByPOSTagAux(DEPNode node, String tag)
	{
		for (DEPNode dep : node.getDependentList())
		{
			if (dep.isPOSTag(tag)) return dep;
			
			dep = getAnyDescendantByPOSTagAux(dep, tag);
			if (dep != null) return dep;
		}
		
		return null;
	}

	/**
	 * Get the sorted list of all the nodes in the subtree of the node.
	 * @return a sorted list of nodes in the subtree of this node (inclusive)
	  */
	public List<DEPNode> getSubNodeList()
	{
		List<DEPNode> list = new ArrayList<>();
		getSubNodeCollectionAux(list, this);
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Get a set of all the nodes is the subtree of the node.
	 * @return a set of nodes in the subtree of this node (inclusive)
	 */
	public Set<DEPNode> getSubNodeSet()
	{
		Set<DEPNode> set = new HashSet<>();
		getSubNodeCollectionAux(set, this);
		return set;
	}
	
	private void getSubNodeCollectionAux(Collection<DEPNode> col, DEPNode node)
	{
		col.add(node);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeCollectionAux(col, dep);
	}
	
	/**
	 * Get the IntHashSet of all the nodes in the subtree (Node ID -> DEPNode).
	 * @return the ntHashSet of all the nodes in the subtree (inclusive)
	 */
	public IntSet getSubNodeIDSet()
	{
		IntSet set = new IntOpenHashSet();
		getSubNodeIDSetAux(set, this);
		return set;
	}

	private void getSubNodeIDSetAux(IntSet set, DEPNode node)
	{
		set.add(node.id);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeIDSetAux(set, dep);
	}
	
	/** 
	 * Get a sorted array of IDs of all the nodes in the subtree of the node.
	 * @return a sorted array of IDs from the subtree of the node (inclusive) 
	 */
	public int[] getSubNodeIDSortedArray()
	{
		IntSet set = getSubNodeIDSet();
		int[] list = set.toIntArray();
		Arrays.sort(list);
		return list;
	}
	
	/**
	 * Get the dependency node with specific index.
	 * @return the dependency node of the node with the specific index if exists; otherwise, {@code null}.
	 * @throws IndexOutOfBoundsException
	 */
	public DEPNode getDependent(int index)
	{
		return dependent_list.get(index);
	}
	
	/**
	 * Get the index of the dependency node of a specified DEPNode.
	 * If the specific node is not a dependent of this node, returns a negative number.
	 * @return the index of the dependent node among other siblings (starting with 0).
	 */
	public int getDependentIndex(DEPNode node)
	{
		return dependent_list.indexOf(node);
	}
	
	/**
	 * Get the size of the dependents of the node.
	 * @return the number of dependents of the node 
	 */
	public int getDependentSize()
	{
		return dependent_list.size();
	}
	
	/**
	 * Get the the valency of the node.
	 * @param direction DirectionType of l, r, a 
	 * @return "0" - no dependents, "<" - left dependents, ">" - right dependents, "<>" - left and right dependents. 
	 */
	public String getValency(Direction direction)
	{
		switch (direction)
		{
		case  left: return getLeftValency();
		case  right: return getRightValency();
		case  all: return getLeftValency()+"-"+getRightValency();
		default: return null;
		}
	}
	
	/**
	 * Get the left valency of the node.
	 * @return "<" - left dependents
	 */
	public String getLeftValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getLeftMostDependent() != null)
		{
			build.append(StringConst.LESS_THAN);
			
			if (getLeftMostDependent(1) != null)
				build.append(StringConst.LESS_THAN);
		}
		
		return build.toString();
	}
	
	/**
	 * Get the right valency of the node.
	 * @return ">" - right dependents
	 */
	public String getRightValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getRightMostDependent() != null)
		{
			build.append(StringConst.GREATER_THAN);
			
			if (getRightMostDependent(1) != null)
				build.append(StringConst.GREATER_THAN);
		}
		
		return build.toString();
	}
	
	/**
	 * Get sub-categorization of the node.
	 * @param direction direction DirectionType of l, r, a
	 * @param field Field of tag feature
	 * @return "< {@code TagFeature}" for left sub-categorization, "> {@code TagFeature}" for right-categorization, and {@code null} if not exist
	 */
	public String getSubcategorization(Direction direction, Field field)
	{
		switch (direction)
		{
		case left: return getLeftSubcategorization (field);
		case right: return getRightSubcategorization(field);
		case all:
			String left = getLeftSubcategorization(field);
			if (left == null) return getRightSubcategorization(field);
			String right = getRightSubcategorization(field);
			return  (right == null) ? left : left+right;
		default: return null; 
		}
	}
	
	/**
	 * Get left sub-categorization of the node.
	 * @param field Field of tag feature 
	 * @return "< {@code TagFeature}" for left sub-categorization, {@code null} if not exist. 
	 */
	public String getLeftSubcategorization(Field field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=0; i<size; i++)
		{
			node = getDependent(i);
			if (node.getID() > id) break;
			build.append(StringConst.LESS_THAN);
			build.append(node.getValue(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	/**
	 * Get right sub-categorization of the node.
	 * @param field Field of tag feature 
	 * @return "> {@code TagFeature}" for right sub-categorization, {@code null} if not exist. 
	 */
	public String getRightSubcategorization(Field field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=size-1; i>=0; i--)
		{
			node = getDependent(i);
			if (node.getID() < id) break;
			build.append(StringConst.GREATER_THAN);
			build.append(node.getValue(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	
	/**
	 * Find the path of between this nodes and the input DEPNode.
	 * @param node the node that you want to find the path from this node
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(DEPNode node, Field field)
	{
		DEPNode lca = getLowestCommonAncestor(node);
		return (lca != null) ? getPath(node, lca, field) : null;
	}
	
	/**
	 * Find the path of between this nodes and the input DEPNode with the lowest common ancestor specified.
	 * @param node the node that you want to find the path from this node
	 * @param lca the lowest common ancestor DEPNode that you specified for the path
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(DEPNode node, DEPNode lca, Field field)
	{
		if (node == lca)
			return getPathAux(lca, this, field, "^", true);
		
		if (this == lca)
			return getPathAux(lca, node, field, "|", true);
		
		return getPathAux(lca, this, field, "^", true) + getPathAux(lca, node, field, "|", false);
	}
	
	private String getPathAux(DEPNode top, DEPNode bottom, Field field, String delim, boolean includeTop)
	{
		StringBuilder build = new StringBuilder();
		DEPNode node = bottom;
		int dist = 0;
		String s;
		
		do
		{
			s = node.getValue(field);
			
			if (s != null)
			{
				build.append(delim);
				build.append(s);
			}
			else
			{
				dist++;
			}
		
			node = node.getHead();
		}
		while (node != top && node != null);
		
		if (field == Field.distance)
		{
			build.append(delim);
			build.append(dist);
		}
		else if (field != Field.dependency_label && includeTop)
		{
			build.append(delim);
			build.append(top.getValue(field));
		}
		
		return build.length() == 0 ? null : build.toString();
	}
	
	/**
	 * Get a set of all the ancestor nodes of the node (ie. Parent node, Grandparent node, etc.).
	 * @return set of all the ancestor nodes
	 */
	public Set<DEPNode> getAncestorSet()
	{
		Set<DEPNode> set = new HashSet<>();
		DEPNode node = getHead();
		
		while (node != null)
		{
			set.add(node);
			node = node.getHead();
		}
		
		return set;
	}
	
	/**
	 * Get the first/lowest common ancestor of the two given nodes (this node and the input DEPNode).
	 * @param node the node that you want to find the lowest common ancestor with the node with
	 * @return the lowest common ancestor of the node and the specified node
	 */
	public DEPNode getLowestCommonAncestor(DEPNode node)
	{
		Set<DEPNode> set = getAncestorSet();
		set.add(this);
		
		while (node != null)
		{
			if (set.contains(node)) return node;
			node = node.getHead();
		}
		
		return null;
	}
	
//	====================================== Setters ======================================

	/** Sets the dependency label. */
	public void setLabel(String label)
	{
		dependency_label = label;
	}
	
	/** Sets the dependency head. */
	public void setHead(DEPNode node)
	{
		if (hasHead())
			dependency_head.dependent_list.remove(this);
		
		if (node != null)
			node.dependent_list.addItem(this);
		
		dependency_head = node;
	}
	
	/** Sets the dependency head of this node with the specific label. */
	public void setHead(DEPNode node, String label)
	{
		setHead (node);
		setLabel(label);
	}
	
	/** Add the specific node as a dependent. */
	public void addDependent(DEPNode node)
	{
		node.setHead(this);
	}
	
	/** Add the specific node as a dependent with the specific label. */
	public void addDependent(DEPNode node, String label)
	{
		node.setHead(this, label);
	}
	
	/**
	 * Clear out all dependencies (head, label, and sibling relations) of the node.
	 * @param the previous head information.
	 */
	public DEPArc clearDependencies()
	{
		DEPArc arc = new DEPArc(dependency_head, dependency_label);
		dependency_head  = null;
		dependency_label = null;
		dependent_list.clear();
		return arc;
	}
	
//	====================================== Booleans ======================================
	
	/** @return true if this node has the dependency head; otherwise, false. */
	public boolean hasHead()
	{
		return dependency_head != null;
	}
	
	/** @return true if the node has the specific node as a dependent. */
	public boolean containsDependent(DEPNode node)
	{
		return dependent_list.contains(node);
	}
	
	/**
	 * @return true if this node has a dependent with the specific label.
	 * @see #getFirstDependent(String, BiPredicate).
	 */
	public boolean containsDependent(String label, BiPredicate<DEPNode,String> predicate)
	{
		return getFirstDependent(label, predicate) != null;
	}
	
	/**
	 * @return true if this node has a dependent with the specific pattern.
	 * @see #getFirstDependentByLabel(Pattern).
	 */
	public boolean containsDependentByLabel(Pattern pattern)
	{
		return getFirstDependentByLabel(pattern) != null;
	}
	
	/** @return true if the dependency label of this node equals to the specific label. */
	public boolean isLabel(String label)
	{
		return label.equals(dependency_label);
	}
	
	/** @return true if the dependency label of this node equals to any of the specific labels. */
	public boolean isLabelAny(String... labels)
	{
		for (String label : labels)
		{
			if (isLabel(label))
				return true;
		}
		
		return false;
	}
	
	/** @return true if the dependency label of this node matches the specific pattern. */
	public boolean isLabel(Pattern pattern)
	{
		return pattern.matcher(dependency_label).find();
	}
	
	/** @return true if this node is a dependent of the specific node. */
	public boolean isDependentOf(DEPNode node)
	{
		return dependency_head == node;
	}
	
	/** {@link #isDependentOf(DEPNode)} && {@link #isLabel(String)}. */
	public boolean isDependentOf(DEPNode node, String label)
	{
		return isDependentOf(node) && isLabel(label);
	}
	
	/** @return true if the node is a descendant of the specific node. */
	public boolean isDescendantOf(DEPNode node)
	{
		DEPNode head = getHead();
		
		while (head != null)
		{
			if (head == node) return true;
			head = head.getHead();
		}
		
		return false;
	}
	
	/** @return true if this node is a sibling of the specific node. */
	public boolean isSiblingOf(DEPNode node)
	{
		return hasHead() && node.isDependentOf(dependency_head);
	}

//	====================================== Helpers ======================================
	
	@Override
	public String getValue(Field field)
	{
		switch (field)
		{
		case dependency_label: return getLabel();
		default: return super.getValue(field);
		}
	}
	
	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(StringConst.TAB);
		
		join.add(Integer.toString(id));
		join.add(word_form);
		join.add(lemma);
		join.add(pos_tag);
		join.add(feat_map.toString());
		
		if (hasHead())
		{
			join.add(Integer.toString(dependency_head.id));
			join.add(dependency_label);
		}
		else
		{
			join.add(TSVReader.BLANK);
			join.add(TSVReader.BLANK);
		}
		
		return join.toString();
	}
	
	@Override
	public int compareTo(DEPNode node)
	{
		return id - node.id;
	}
}