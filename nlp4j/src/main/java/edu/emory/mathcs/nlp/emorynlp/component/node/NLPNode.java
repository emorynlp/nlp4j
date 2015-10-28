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
package edu.emory.mathcs.nlp.emorynlp.component.node;

import java.io.Serializable;
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

import edu.emory.mathcs.nlp.common.collection.arc.AbstractArc;
import edu.emory.mathcs.nlp.common.collection.list.SortedArrayList;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.emorynlp.component.feature.Direction;
import edu.emory.mathcs.nlp.emorynlp.component.feature.Field;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.dep.DEPArc;
import edu.emory.mathcs.nlp.emorynlp.srl.SRLArc;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPNode implements Serializable, Comparable<NLPNode>
{
	private static final long serialVersionUID = 5522467283393796925L;
	static final String ROOT_TAG = "@#r$%";
	
	// core fields
	protected String       word_form;
	protected String       lemma;
	protected String       pos_tag;
	protected String       nament_tag;
	protected FeatMap      feat_map;
	protected String       dependency_label;
	protected NLPNode      dependency_head;
	protected List<SRLArc> semantic_heads;

	// inferred fields
	protected int id;
	protected String simplified_word_form;
	protected SortedArrayList<NLPNode> dependent_list;
	
	public NLPNode() {}
	
	public NLPNode(int id, String form)
	{
		set(id, form, null, null, null, new FeatMap(), null, null);
	}
	
	public NLPNode(int id, String form, String tag)
	{
		set(id, form, null, null, null, new FeatMap(), null, null);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, FeatMap feats)
	{
		set(id, form, lemma, posTag, null, feats, null, null);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, String namentTag, FeatMap feats)
	{
		set(id, form, lemma, posTag, namentTag, feats, null, null);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, FeatMap feats, NLPNode dhead, String deprel)
	{
		set(id, form, lemma, posTag, null, feats, dhead, deprel);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, String namentTag, FeatMap feats, NLPNode dhead, String deprel)
	{
		set(id, form, lemma, posTag, namentTag, feats, dhead, deprel);
	}
	
	public void set(int id, String form, String lemma, String posTag, String namentTag, FeatMap feats, NLPNode dhead, String deprel)
	{
		setID(id);
		setWordForm(form);
		setLemma(lemma);
		setPartOfSpeechTag(posTag);
		setNamedEntityTag(namentTag);
		setFeatMap(feats);
		setDependencyHead(dhead);
		setDependencyLabel(deprel);
		
		dependent_list = new SortedArrayList<>();
		semantic_heads = new ArrayList<>();
	}
	
	public void setToRoot()
	{
		set(0, ROOT_TAG, ROOT_TAG, ROOT_TAG, ROOT_TAG, new FeatMap(), null, null);
	}
	
//	============================== GETTERS ==============================
	
	public int getID()
	{
		return id;
	}
	
	public String getWordForm()
	{
		return word_form;
	}
	
	/** @see StringUtils#toSimplifiedForm(String). */
	public String getSimplifiedWordForm()
	{
		return simplified_word_form;
	}
	
	public String getLemma()
	{
		return lemma;
	}
	
	public String getPartOfSpeechTag()
	{
		return pos_tag;
	}
	
	public String getNamedEntityTag()
	{
		return nament_tag;
	}
	
	public FeatMap getFeatMap()
	{
		return feat_map;
	}
	
	public String getFeat(String key)
	{
		return feat_map.get(key);
	}
	
	/** @return the value of the specific field. */
	public String getValue(Field field)
	{
		switch (field)
		{
		case word_form: return getWordForm();
		case simplified_word_form: return getSimplifiedWordForm();
		case uncapitalized_simplified_word_form: return StringUtils.toLowerCase(getSimplifiedWordForm());
		case lemma: return getLemma();
		case part_of_speech_tag: return getPartOfSpeechTag();
		case named_entity_tag: return getNamedEntityTag();
		case dependency_label: return getDependencyLabel();
		default: return null;
		}
	}
	
//	============================== SETTERS ==============================
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public void setWordForm(String form)
	{
		word_form = form;
		simplified_word_form = StringUtils.toSimplifiedForm(form);
	}
	
	public void setLemma(String lemma)
	{
		this.lemma = lemma;
	}
	
	public void setPartOfSpeechTag(String tag)
	{
		pos_tag = tag;
	}
	
	public void setFeatMap(FeatMap map)
	{
		feat_map = map;
	}
	
	public String putFeat(String key, String value)
	{
		return feat_map.put(key, value);
	}
	
	public String removeFeat(String key)
	{
		return feat_map.remove(key);
	}
	
	public void setNamedEntityTag(String tag)
	{
		nament_tag = tag;
	}
	
//	============================== BOOLEANS ==============================
	
	public boolean isID(int id)
	{
		return this.id == id;
	}
	
	public boolean isWordForm(String form)
	{
		return form.equals(word_form);
	}
	
	public boolean isSimplifiedWordForm(String form)
	{
		return form.equals(simplified_word_form);
	}
	
	public boolean isLemma(String lemma)
	{
		return lemma.equals(this.lemma);
	}
	
	public boolean isPartOfSpeechTag(String tag)
	{
		return tag.equals(pos_tag);
	}
	
	public boolean isPartOfSpeechTag(Pattern pattern)
	{
		return pattern.matcher(pos_tag).find();
	}
	
	public boolean isNamedEntityTag(String tag)
	{
		return tag.equals(nament_tag);
	}
	
//	============================== DEPENDENCY GETTERS ==============================
	
	/** @return the dependency label of this node if exists; otherwise, null. */
	public String getDependencyLabel()
	{
		return dependency_label;
	}
	
	/** @return the dependency head of this node if exists; otherwise, null. */
	public NLPNode getDependencyHead()
	{
		return dependency_head;
	}
	
	/** @return the dependency grand-head of the node if exists; otherwise, null. */
	public NLPNode getGrandDependencyHead()
	{
		NLPNode head = getDependencyHead();
		return (head == null) ? null : head.getDependencyHead();
	}
	
	/** Calls {@link #getLeftNearestSibling(int)}, where {@code order=0}. */
	public NLPNode getLeftNearestSibling()
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
	public NLPNode getLeftNearestSibling(int order)
	{
		if (dependency_head != null)
		{
			order = getSiblingID() - order - 1;
			if (order >= 0) return dependency_head.getDependent(order);
		}
		
		return null;
	}
	
	public NLPNode getLeftNearestSibling(String label)
	{
		if (dependency_head != null)
		{
			NLPNode node;
			
			for (int i=getSiblingID()-1; i>=0; i--)
			{	
				node = dependency_head.getDependent(i);
				if (node.isDependencyLabel(label)) return node;
			}
		}
		
		return null;
	}

	/**
	 * Get the right nearest sibling node of the node.
	 * Calls {@link #getRightNearestSibling(int)}, where {@code order=0}.
	 * @return the right nearest sibling node
	 */
	public NLPNode getRightNearestSibling()
	{
		return getRightNearestSibling(0);
	}
	
	/**
	 * Get the right sibling node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * @param order right displacement
	 * @return the right sibling node with input displacement
	 */
	public NLPNode getRightNearestSibling(int order)
	{
		if (dependency_head != null)
		{
			order = getSiblingID() + order + 1;
			if (order < dependency_head.getDependentSize()) return dependency_head.getDependent(order);
		}
		
		return null;
	}
	
	public NLPNode getRightNearestSibling(String label)
	{
		if (dependency_head != null)
		{
			int i, size = dependency_head.getDependentSize();
			NLPNode node;
			
			for (i=getSiblingID()+1; i<size; i++)
			{	
				node = dependency_head.getDependent(i);
				if (node.isDependencyLabel(label)) return node;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the left most dependency node of the node.
	 * Calls {@link #getLeftMostDependent(int)}, where {@code order=0}
	 * @return the left most dependency node of the node
	 */
	public NLPNode getLeftMostDependent()
	{
		return getLeftMostDependent(0);
	}
	
	/**
	 * Get the left dependency node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * The leftmost dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the leftmost dependent of this node if exists; otherwise, {@code null}
	 */
	public NLPNode getLeftMostDependent(int order)
	{
		if (DSUtils.isRange(dependent_list, order))
		{
			NLPNode dep = getDependent(order);
			if (dep.id < id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the right most dependency node of the node.
	 * Calls {@link #getRightMostDependent(int)}, where {@code order=0}. 
	 * @return the right most dependency node of the node
	 */
	public NLPNode getRightMostDependent()
	{
		return getRightMostDependent(0);
	}
	
	/**
	 * Get the right dependency node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * The rightmost dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the rightmost dependent of this node if exists; otherwise, {@code null}
	 */
	public NLPNode getRightMostDependent(int order)
	{
		order = getDependentSize() - 1 - order;
		
		if (DSUtils.isRange(dependent_list, order))
		{
			NLPNode dep = getDependent(order);
			if (dep.id > id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the left nearest dependency node.
	 * Calls {@link #getLeftNearestDependent(int)}, where {@code order=0}.
	 * @return the left nearest dependency node
	 */
	public NLPNode getLeftNearestDependent()
	{
		return getLeftNearestDependent(0);
	}
	
	/**
	 * Get the left nearest dependency node with input displacement (0 - left-nearest, 1 - second left-nearest, etc.).
	 * The left nearest dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the left-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public NLPNode getLeftNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) - order - 1;
		return (index >= 0) ? getDependent(index) : null;
	}
	
	/**
	 * Get the right nearest dependency node.
	 * Calls {@link #getRightNearestDependent(int)}, where {@code order=0}. 
	 * @return the right nearest dependency node
	 */
	public NLPNode getRightNearestDependent()
	{
		return getRightNearestDependent(0);
	}
	
	/**
	 * Get the right nearest dependency node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * The right-nearest dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the right-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public NLPNode getRightNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) + order;
		return (index < getDependentSize()) ? getDependent(index) : null;
	}

	/**
	 * @param predicate takes a dependency node and compares the specific tag with the referenced function.
	 * @return the first-dependent with the specific label.
	 */
	public NLPNode getFirstDependent(String label, BiPredicate<NLPNode,String> predicate)
	{
		for (NLPNode node : dependent_list)
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
	public NLPNode getFirstDependentByLabel(Pattern pattern)
	{
		for (NLPNode node : dependent_list)
		{
			if (node.isDependencyLabel(pattern))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node.
	 * @return list of all the dependency nodes of the node
	 */
	public List<NLPNode> getDependentList()
	{
		return dependent_list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label.
	 * @param label string label
	 * @return list of all the dependency nodes of the node by label
	 */
	public List<NLPNode> getDependentListByLabel(String label)
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
		{
			if (node.isDependencyLabel(label))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by labels set.
	 * @param y labels set
	 * @return list of all the dependency nodes of the node by labels set
	 */
	public List<NLPNode> getDependentListByLabel(Set<String> labels)
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
		{
			if (labels.contains(node.getDependencyLabel()))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label pattern.
	 * @param y label pattern
	 * @return list of all the dependency nodes of the node by label pattern
	 */
	public List<NLPNode> getDependentListByLabel(Pattern pattern)
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
		{
			if (node.isDependencyLabel(pattern))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the left dependency nodes of the node.
	 * @return list of all the left dependency nodes of the node
	 */
	public List<NLPNode> getLeftDependentList()
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
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
	public List<NLPNode> getLeftDependentListByLabel(Pattern pattern)
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
		{
			if (node.id > id) break;
			if (node.isDependencyLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the right dependency nodes of the node.
	 * @return list of all the right dependency nodes of the node
	 */
	public List<NLPNode> getRightDependentList()
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
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
	public List<NLPNode> getRightDependentListByLabel(Pattern pattern)
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
		{
			if (node.id < id) continue;
			if (node.isDependencyLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all grand-dependents of the node. 
	 * @return an unsorted list of grand-dependents of the node
	 */
	public List<NLPNode> getGrandDependentList()
	{
		List<NLPNode> list = new ArrayList<>();
		
		for (NLPNode node : dependent_list)
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
	public List<NLPNode> getDescendantList(int height)
	{
		List<NLPNode> list = new ArrayList<>();
	
		if (height > 0)
			getDescendantListAux(this, list, height-1);
		
		return list;
	}
	
	private void getDescendantListAux(NLPNode node, List<NLPNode> list, int height)
	{
		list.addAll(node.getDependentList());
		
		if (height > 0)
		{
			for (NLPNode dep : node.getDependentList())
				getDescendantListAux(dep, list, height-1);
		}
	}
	
	/**
	 * Get any descendant node with POS tag.
	 * @param tag POS tag
	 * @return s descendant node with the POS tag
	 */
	public NLPNode getAnyDescendantByPartOfSpeechTag(String tag)
	{
		return getAnyDescendantByPartOfSpeechTagAux(this, tag);
	}
	
	private NLPNode getAnyDescendantByPartOfSpeechTagAux(NLPNode node, String tag)
	{
		for (NLPNode dep : node.getDependentList())
		{
			if (dep.isPartOfSpeechTag(tag)) return dep;
			
			dep = getAnyDescendantByPartOfSpeechTagAux(dep, tag);
			if (dep != null) return dep;
		}
		
		return null;
	}

	/**
	 * Get the sorted list of all the nodes in the subtree of the node.
	 * @return a sorted list of nodes in the subtree of this node (inclusive)
	  */
	public List<NLPNode> getSubNodeList()
	{
		List<NLPNode> list = new ArrayList<>();
		getSubNodeCollectionAux(list, this);
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Get a set of all the nodes is the subtree of the node.
	 * @return a set of nodes in the subtree of this node (inclusive)
	 */
	public Set<NLPNode> getSubNodeSet()
	{
		Set<NLPNode> set = new HashSet<>();
		getSubNodeCollectionAux(set, this);
		return set;
	}
	
	private void getSubNodeCollectionAux(Collection<NLPNode> col, NLPNode node)
	{
		col.add(node);
		
		for (NLPNode dep : node.getDependentList())
			getSubNodeCollectionAux(col, dep);
	}
	
	/**
	 * Get the IntHashSet of all the nodes in the subtree (Node ID -> NLPNode).
	 * @return the ntHashSet of all the nodes in the subtree (inclusive)
	 */
	public IntSet getSubNodeIDSet()
	{
		IntSet set = new IntOpenHashSet();
		getSubNodeIDSetAux(set, this);
		return set;
	}

	private void getSubNodeIDSetAux(IntSet set, NLPNode node)
	{
		set.add(node.id);
		
		for (NLPNode dep : node.getDependentList())
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
	public NLPNode getDependent(int index)
	{
		return dependent_list.get(index);
	}
	
	/**
	 * Get the index of the dependency node of a specified NLPNode.
	 * If the specific node is not a dependent of this node, returns a negative number.
	 * @return the index of the dependent node among other siblings (starting with 0).
	 */
	public int getDependentIndex(NLPNode node)
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
		NLPNode node;
		
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
		NLPNode node;
		
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
	 * Find the path of between this nodes and the input NLPNode.
	 * @param node the node that you want to find the path from this node
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(NLPNode node, Field field)
	{
		NLPNode lca = getLowestCommonAncestor(node);
		return (lca != null) ? getPath(node, lca, field) : null;
	}
	
	/**
	 * Find the path of between this nodes and the input NLPNode with the lowest common ancestor specified.
	 * @param node the node that you want to find the path from this node
	 * @param lca the lowest common ancestor NLPNode that you specified for the path
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(NLPNode node, NLPNode lca, Field field)
	{
		if (node == lca)
			return getPathAux(lca, this, field, "^", true);
		
		if (this == lca)
			return getPathAux(lca, node, field, "|", true);
		
		return getPathAux(lca, this, field, "^", true) + getPathAux(lca, node, field, "|", false);
	}
	
	private String getPathAux(NLPNode top, NLPNode bottom, Field field, String delim, boolean includeTop)
	{
		StringBuilder build = new StringBuilder();
		NLPNode node = bottom;
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
		
			node = node.getDependencyHead();
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
	public Set<NLPNode> getAncestorSet()
	{
		Set<NLPNode> set = new HashSet<>();
		NLPNode node = getDependencyHead();
		
		while (node != null)
		{
			set.add(node);
			node = node.getDependencyHead();
		}
		
		return set;
	}
	
	/**
	 * Get the first/lowest common ancestor of the two given nodes (this node and the input NLPNode).
	 * @param node the node that you want to find the lowest common ancestor with the node with
	 * @return the lowest common ancestor of the node and the specified node
	 */
	public NLPNode getLowestCommonAncestor(NLPNode node)
	{
		Set<NLPNode> set = getAncestorSet();
		set.add(this);
		
		while (node != null)
		{
			if (set.contains(node)) return node;
			node = node.getDependencyHead();
		}
		
		return null;
	}
	
//	============================== DEPENDENCY SETTERS ==============================
	
	/** Sets the dependency label. */
	public void setDependencyLabel(String label)
	{
		dependency_label = label;
	}
	
	/** Sets the dependency head. */
	public void setDependencyHead(NLPNode node)
	{
		if (hasDependencyHead())
			dependency_head.dependent_list.remove(this);
		
		if (node != null)
			node.dependent_list.addItem(this);
		
		dependency_head = node;
	}
	
	/** Sets the dependency head of this node with the specific label. */
	public void setDependencyHead(NLPNode node, String label)
	{
		setDependencyHead (node);
		setDependencyLabel(label);
	}
	
	/** Add the specific node as a dependent. */
	public void addDependent(NLPNode node)
	{
		node.setDependencyHead(this);
	}
	
	/** Add the specific node as a dependent with the specific label. */
	public void addDependent(NLPNode node, String label)
	{
		node.setDependencyHead(this, label);
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
	
//	============================== DEPENDENCY BOOLEANS ==============================

	/** @return true if this node has the dependency head; otherwise, false. */
	public boolean hasDependencyHead()
	{
		return dependency_head != null;
	}
	
	/** @return true if the dependency label of this node equals to the specific label. */
	public boolean isDependencyLabel(String label)
	{
		return label.equals(dependency_label);
	}
	
	/** @return true if the dependency label of this node equals to any of the specific labels. */
	public boolean isDependencyLabelAny(String... labels)
	{
		for (String label : labels)
		{
			if (isDependencyLabel(label))
				return true;
		}
		
		return false;
	}
	
	/** @return true if the dependency label of this node matches the specific pattern. */
	public boolean isDependencyLabel(Pattern pattern)
	{
		return pattern.matcher(dependency_label).find();
	}
	
	/** @return true if this node is a dependent of the specific node. */
	public boolean isDependentOf(NLPNode node)
	{
		return dependency_head == node;
	}
	
	/** {@link #isDependentOf(NLPNode)} && {@link #isDependencyLabel(String)}. */
	public boolean isDependentOf(NLPNode node, String label)
	{
		return isDependentOf(node) && isDependencyLabel(label);
	}
	
	/** @return true if the node has the specific node as a dependent. */
	public boolean containsDependent(NLPNode node)
	{
		return dependent_list.contains(node);
	}
	
	/**
	 * @return true if this node has a dependent with the specific label.
	 * @see #getFirstDependent(String, BiPredicate).
	 */
	public boolean containsDependent(String label, BiPredicate<NLPNode,String> predicate)
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
	
	
	
	/** @return true if the node is a descendant of the specific node. */
	public boolean isDescendantOf(NLPNode node)
	{
		NLPNode head = getDependencyHead();
		
		while (head != null)
		{
			if (head == node) return true;
			head = head.getDependencyHead();
		}
		
		return false;
	}
	
	/** @return true if this node is a sibling of the specific node. */
	public boolean isSiblingOf(NLPNode node)
	{
		return hasDependencyHead() && node.isDependentOf(dependency_head);
	}
	
//	============================== SEMANTICS ==============================

	/** @return a list of all semantic head arc of the node. */
	public List<SRLArc> getSemanticHeadList()
	{
		return semantic_heads;
	}
	
	/** @return a list of all semantic head arc of the node with the given label. */
	public List<SRLArc> getSemanticHeadList(String label)
	{
		List<SRLArc> list = new ArrayList<>();
		
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isLabel(label))
				list.add(arc);
		}
		
		return list;
	}
	
	/** @return semantic arc relationship between the node and another given node. */
	public SRLArc getSemanticHeadArc(NLPNode node)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isNode(node))
				return arc;
		}
		
		return null;
	}
	
	/** @return the semantic arc relationship between the node and another given node with a given label. */
	public SRLArc getSemanticHeadArc(NLPNode node, String label)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.equals(node, label))
				return arc;
		}
		
		return null;
	}
	
	/** @return the semantic arc relationship between the node and another given node with a given pattern. */
	public SRLArc getSemanticHeadArc(NLPNode node, Pattern pattern)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.equals(node, pattern))
				return arc;
		}
		
		return null;
	}
	
	/** @return the semantic label of the given in relation to the node. */
	public String getSemanticLabel(NLPNode node)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isNode(node))
				return arc.getLabel();
		}
		
		return null;
	}
	
	/** @return the first node that is found to have the semantic head of the given label from the node. */
	public NLPNode getFirstSemanticHead(String label)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isLabel(label))
				return arc.getNode();
		}
		
		return null;
	}
	
	/** @return the first node that is found to have the semantic head of the given pattern from the node. */
	public NLPNode getFirstSemanticHead(Pattern pattern)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isLabel(pattern))
				return arc.getNode();
		}
		
		return null;
	}
	
	/** @param arcs {@code Collection<SRLArc>} of the semantic heads. */
	public void addSemanticHeads(Collection<SRLArc> arcs)
	{
		semantic_heads.addAll(arcs);
	}
	
	/** Adds a node a give the given semantic label to the node. */
	public void addSemanticHead(NLPNode head, String label)
	{
		addSemanticHead(new SRLArc(head, label));
	}
	
	/** Adds a semantic arc to the node. */
	public void addSemanticHead(SRLArc arc)
	{
		semantic_heads.add(arc);
	}
	
	/** Sets semantic heads of the node. */
	public void setSemanticHeads(List<SRLArc> arcs)
	{
		semantic_heads = arcs;
	}
	
	/** Removes all semantic heads of the node in relation to a given node.
	 * @return {@code true}, else {@code false} if nothing gets removed. 
	 */
	public boolean removeSemanticHead(NLPNode node)
	{
		for (SRLArc arc : semantic_heads)
		{
			if (arc.isNode(node))
				return semantic_heads.remove(arc);
		}
		
		return false;
	}
	
	/** Removes a specific semantic head of the node. */
	public boolean removeSemanticHead(SRLArc arc)
	{
		return semantic_heads.remove(arc);
	}
	
	/** Removes a collection of specific semantic heads of the node. */
	public void removeSemanticHeads(Collection<SRLArc> arcs)
	{
		semantic_heads.removeAll(arcs);
	}
	
	/** Removes all semantic heads of the node that have the given label. */
	public void removeSemanticHeads(String label)
	{
		semantic_heads.removeAll(getSemanticHeadList(label));
	}
	
	/** Removes all semantic heads of the node. */
	public void clearSemanticHeads()
	{
		semantic_heads.clear();
	}
	
	/** @return {@code true}, else {@code false} if there is no SRLArc between the two nodes. */
	public boolean isArgumentOf(NLPNode node)
	{
		return getSemanticHeadArc(node) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no SRLArc with the given label. */
	public boolean isArgumentOf(String label)
	{
		return getFirstSemanticHead(label) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no SRLArc with the given pattern. */
	public boolean isArgumentOf(Pattern pattern)
	{
		return getFirstSemanticHead(pattern) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no SRLArc with the given label between the two node. */
	public boolean isArgumentOf(NLPNode node, String label)
	{
		return getSemanticHeadArc(node, label) != null;
	}
	
	/** @return {@code true}, else {@code false} if there is no SRLArc with the given pattern between the two node. */
	public boolean isArgumentOf(NLPNode node, Pattern pattern)
	{
		return getSemanticHeadArc(node, pattern) != null;
	}

	/**
	 * Consider this node as a predicate.
	 * @param maxDepth  > 0.
	 * @param maxHeight > 0.
	 * @return list of (argument, lowest common ancestor) pairs.
	 */
	public List<Pair<NLPNode,NLPNode>> getArgumentCandidateList(int maxDepth, int maxHeight)
	{
		return null;
	}
	
	
//	============================== HELPERS ==============================
	
	@Override
	public int compareTo(NLPNode node)
	{
		return id - node.id;
	}

	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(StringConst.TAB);
		
		join.add(Integer.toString(id));
		join.add(toString(word_form));
		join.add(toString(lemma));
		join.add(toString(pos_tag));
		join.add(toString(nament_tag));
		join.add(feat_map.toString());
		toStringDependency(join);
		join.add(toStringSemantics(semantic_heads));
		
		return join.toString();
	}
	
	private String toString(String s)
	{
		return (s == null) ? TSVReader.BLANK : s;
	}
	
	private void toStringDependency(StringJoiner join)
	{
		if (hasDependencyHead())
		{
			join.add(Integer.toString(dependency_head.id));
			join.add(toString(dependency_label));
		}
		else
		{
			join.add(TSVReader.BLANK);
			join.add(TSVReader.BLANK);
		}
	}
	
	private <T extends AbstractArc<NLPNode>>String toStringSemantics(List<T> arcs)
	{
		if (arcs == null || arcs.isEmpty())
			return TSVReader.BLANK;
		
		Collections.sort(arcs);
		return Joiner.join(arcs, AbstractArc.ARC_DELIM);
	}
}