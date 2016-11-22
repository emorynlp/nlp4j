/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.lexicon.node;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.magicwerk.brownies.collections.GapList;

import com.google.common.base.Predicate;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.lexicon.util.DefaultFields;
import edu.emory.mathcs.nlp.lexicon.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNode<N extends AbstractNode<N>> extends DefaultFields
{
	private static final long serialVersionUID = -7778214978518194244L;
	
	protected N parent;
	protected N left_sibling;
	protected N right_sibling;
	protected List<N> children;
	
//	============================== Constructors ==============================
	
	public AbstractNode()
	{
		this(-1, null, null, null, null, new FeatMap());
	}
	
	public AbstractNode(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		super(token_id, form, lemma, syntactic_tag, named_entity_tag, feat_map);
		
		parent        = null;
		left_sibling  = null;
		right_sibling = null;
		children      = new GapList<>();
	}
	
//	============================== Abstract Methods ==============================
	
	/** @return this node. */
	public abstract N self();
	
	/** @return the index of the child. */
	public abstract int getChildIndex(N node);
	
	/** @return the default index for add. */
	protected abstract int getDefaultIndex(List<N> list, N node);
	
//	============================== Child ==============================
	
	/** @return the index'th child of this node if exists; otherwise, null. */
	public N getChild(int index)
	{
		return DSUtils.isRange(children, index) ? children.get(index) : null;
	}
	
	/** @return the first child of this node if exists; otherwise, {@code null}. */
	public N getFirstChild()
	{
		return getFirstChild(0);
	}
	
	/**
	 * @param order displacement (0: 1st, 1: 2nd, etc.).
	 * @return the order'th child of this node if exists; otherwise, null.
	 */
	public N getFirstChild(int order)
	{
		return getChild(order);
	}
	
	/**
	 * @see #getFirst(List, Object, BiPredicate).
	 * @return the first child matching the condition.
	 */
	public <T>N getFirstChild(T condition, BiPredicate<N,T> matcher)
	{
		return getFirst(children, condition, matcher);
	}
	
	/** @return the last child of this node if exists; otherwise, null. */
	public N getLastChild()
	{
		return getLastChild(0);
	}
	
	/**
	 * @param order displacement (0: last, 1: 2nd to the last, etc.).
	 * @return the order'th last child of this node if exists; otherwise, null.
	 */
	public N getLastChild(int order)
	{
		return getChild(children.size() - order - 1);
	}
	
	/**
	 * @see #getLast(List, Object, BiPredicate).
	 * @return the last child matching the condition.
	 */
	public <T>N getLastChild(T condition, BiPredicate<N,T> matcher)
	{
		return getLast(children, condition, matcher);
	}
	
	/**
	 * Adds a child to the default location.
	 * @return true if the child is added; otherwise, false.
	 */
	public boolean addChild(N node)
	{
		return addChild(getDefaultIndex(children, node), node);
	}

	/**
	 * Adds a node as the index'th child of this node if it is not already a child of this node.
	 * @return true if the specific node is added; otherwise, false.
	 */
	public boolean addChild(int index, N node)
	{
		if (!isParentOf(node))
		{
			if (node.hasParent())
				node.parent.removeChild(node);
			
			node.parent = self();
			children.add(index, node);
			setSiblings(getChild(index-1), node);
			setSiblings(node, getChild(index+1));
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets a node as the index'th child of this node if it is not already a child of this node.
	 * @return the previously index'th node if added; otherwise, null.
	 */
	public N setChild(int index, N node)
	{
		if (!isParentOf(node))
		{
			if (node.hasParent())
				node.parent.removeChild(node);
			
			node.parent = self();
			N old = children.set(index, node);
			setSiblings(getChild(index-1), node);
			setSiblings(node, getChild(index+1));
			old.isolate();
			return old;	
		}
		
		return null;
	}
	
	/**
	 * Removes a child from this node.
	 * @return the removed child if exists; otherwise, null.
	 */
	public N removeChild(N node)
	{
		return removeChild(getChildIndex(node));
	}
	
	/**
	 * Removes the index'th child of this node.
	 * @return the removed child if exists; otherwise, null.
	 */
	public N removeChild(int index)
	{
		if (DSUtils.isRange(children, index))
		{
			setSiblings(getChild(index-1), getChild(index+1));
			N node = children.remove(index);
			node.isolate();
			return node;
		}
		
		return null;
	}
	
	/** Replaces the old child with the new child. */
	public boolean replaceChild(N old_child, N new_child)
	{
		int index = getChildIndex(old_child);
		
		if (index >= 0)
		{
			if (new_child.hasParent())
				new_child.parent.removeChild(new_child);
			
			setChild(index, new_child);
			return true;
		}
		
		return false;
	}
	
	/** @return true if this node has any child; otherwise, false. */
	public boolean hasChild()
	{
		return !children.isEmpty();
	}

	/** @return true if this node is a child of the specific node; otherwise, false. */
	public boolean isChildOf(N node)
	{
		return node != null && parent == node;
	}

	/**
	 * @see #contains(List, Object, BiPredicate).
	 * @return true if this node contains a child matching the specific condition.
	 */
	public <T>boolean containsChild(T supplement, BiPredicate<N,T> matcher)
	{
		return contains(children, supplement, matcher);
	}
	
//	============================== Descendants ==============================
	
	/** @return the number of children. */
	public int getChildrenSize()
	{
		return children.size();
	}
	
	/** @return the list of children. */
	public List<N> getChildren()
	{
		return children;
	}

	/**
     * The sublist begins at the specific position and extends to the end.
     * @param fst_id the ID of the first child (inclusive).
     * @return an immutable list of sub-children.
     */
	public List<N> getChildren(int fst_id)
	{
		return children.subList(fst_id, getChildrenSize());
	}

	/**
     * The sublist begins and ends at the specific positions.
     * @param fst_id the ID of the first child (inclusive).
     * @param lst_id the ID of the last child (exclusive)
     * @return an immutable list of sub-children.
     */
	public List<N> getChildren(int fst_id, int lst_id)
	{
		return children.subList(fst_id, lst_id);
	}
	
	/**
	 * @see #getMatchedList(List, Object, BiPredicate).
	 * @return the list of children matching the specific condition.
	 */
	public <T>List<N> getChildren(T supplement, BiPredicate<N,T> matcher)
	{
		return getMatchedList(children, supplement, matcher);
	}
	
	/** @return the list of grand-children. */
	public List<N> getGrandChildren()
	{
		return getSecondOrder(N::getChildren);
	}

	/**
	 * @see #getFirst(List, Object, BiPredicate).
	 * @return the first descendant matching the specific condition.
	 */
	public <T>N getFirstDescendant(T supplement, BiPredicate<N,T> matcher)
	{
		return getFirstDescendantAux(children, supplement, matcher);
	}
	
	private <T>N getFirstDescendantAux(Collection<N> nodes, T supplement, BiPredicate<N,T> matcher)
	{
		for (N node : nodes)
		{
			if (matcher.test(node, supplement))
				return node;
			
			if ((node = getFirstDescendantAux(node.children, supplement, matcher)) != null)
				return node;
		}
		
		return null;
	}
	
	/**
	 * @see #getFirst(List, Object, BiPredicate).
	 * @return the first lowest descendant whose intermediate ancestors to this node all match the specific conditoin.
	 */
	public <T>N getFirstLowestChainedDescendant(T supplement, BiPredicate<N,T> matcher)
	{
		N node = getFirstChild(supplement, matcher), descendant = null;
		
		while (node != null)
		{
			descendant = node;
			node = node.getFirstChild(supplement, matcher);
		}

		return descendant;
	}
	
	/** @return true if the node is a descendant of the specific node. */
	public boolean isDescendantOf(N node)
	{
		return getNearestNode(node, (n,s) -> n == s, N::getParent) != null;
	}
	
//	======================== Ancestors ========================
	
	/** @return the parent of this node if exists; otherwise, null. */
	public N getParent()
	{
		return parent;
	}
	
	/** @return the grandparent of this node if exists; otherwise, null. */
	public N getGrandParent()
	{
		return getAncestor(2);
	}
	
	/**
	 * @param height height of the ancestor from this node (1: parent, 2: grandparent, etc.).
	 * @return the height'th nearest ancestor of this node if exists; otherwise, null.
	 */
	public N getAncestor(int height)
	{
		return getNode(height, n -> n.parent);
	}
	
	/**
	 * @see #getNode(Object, BiPredicate, Function).
	 * @return the lowest ancestor matching the specific condition.
	 */
	public <T>N getLowestAncestor(T supplement, BiPredicate<N,T> matcher)
	{
		return getNearestNode(supplement, matcher, N::getParent);
	}
	
	public <T>N getHighestChainedAncestor(T supplement, BiPredicate<N,T> matcher)
	{
		N node = parent, ancestor = null;
		
		while (node != null)
		{
			if (matcher.test(node, supplement))	ancestor = node;
			else break;

			node = node.parent;
		}
		
		return ancestor;
	}
	
	/** @return the set of all ancestors of this node. */
	public Set<N> getAncestorSet()
	{
		Set<N> set = new HashSet<>();
		N node = getParent();
		
		while (node != null)
		{
			set.add(node);
			node = node.getParent();
		}
		
		return set;
	}
	
	/** @return the lowest common ancestor of this node and the specified node. */
	public N getLowestCommonAncestor(N node)
	{
		Set<N> set = getAncestorSet();
		set.add(self());
		
		while (node != null)
		{
			if (set.contains(node)) return node;
			node = node.getParent();
		}
		
		return null;
	}
	
	/** Sets the parent of this node and its siblings. */
	public void setParent(N parent)
	{
		parent.addChild(self());
	}
	
	/** @return true if this node is the parent of the specific node. */
	public boolean isParentOf(N node)
	{
		return node.isChildOf(self());
	}
	
	/** @return true if the node is a descendant of the specific node. */
	public boolean isAncestorOf(N node)
	{
		return node.isDescendantOf(self());
	}
	
	/** @return true if this node has a parent; otherwise, false. */
	public boolean hasParent()
	{
		return parent != null;
	}
	
//	============================== Siblings ==============================
	
	/** @return the left-nearest sibling of this node if exists; otherwise, null. */
	public N getLeftSibling()
	{
		return left_sibling;
	}
	
	/**
	 * @param order left displacement (1: left-nearest, 2: 2nd left-nearest, etc.).
	 * @return the order'th left-nearest sibling of this node if exists; otherwise, null.
	 */
	public N getLeftSibling(int order)
	{
		return order > 0 ? getNode(order, N::getLeftSibling) : null;
	}
	
	/** @return the right-nearest sibling of this node if exists; otherwise, null. */
	public N getRightSibling()
	{
		return right_sibling;
	}
	
	/**
	 * @param order right displacement (1: right-nearest, 2: 2nd right-nearest, etc.).
	 * @return the order'th right-nearest sibling of this node if exists; otherwise, null.
	 */
	public N getRightSibling(int order)
	{
		return order > 0 ? getNode(order, N::getRightSibling) : null;
	}
	
	/** @return true if this node has a left sibling. */
	public boolean hasLeftSibling()
	{
		return left_sibling != null;
	}
	
	/** @return true if this node has a right sibling. */
	public boolean hasRightSibling()
	{
		return right_sibling != null;
	}
	
	/** @return true if this node is a sibling of the specific node. */
	public boolean isSiblingOf(N node)
	{
		return node.isChildOf(parent);
	}
	
	/** @return true if this node is a left sibling of the specific node. */
	public boolean isLeftSiblingOf(N node)
	{
		return node != null && parent == node.parent && getNearestNode(node, (n,s) -> n == s, N::getRightSibling) != null;
	}
	
	/** @return true if this node is a right sibling of the specific node. */
	public boolean isRightSiblingOf(N node)
	{
		return node.isLeftSiblingOf(self());
	}
	
//	============================== Helpers ==============================
	
	public Stream<N> flatten()
	{
		return Stream.concat(Stream.of(self()), children.stream().flatMap(N::flatten));
    }
	
	/**
	 * @param order 1: nearest, 2: second nearest, etc.
	 * @param getter takes a node and returns a node.
	 * @return the order'th node with respect to the getter.
	 */
	public N getNode(int order, Function<N,N> getter)
	{
		N node = self();
		
		for (int i=0; i<order; i++)
		{
			if (node == null) return node;
			node = getter.apply(node);
		}
		
		return node;
	}
	
	public N getNearestNode(Predicate<N> matcher, Function<N,N> getter)
	{
		N node = getter.apply(self());
		
		while (node != null)
		{
			if (matcher.apply(node)) return node;
			node = getter.apply(node);
		}
		
		return null;
	}
	
	/**
	 * @param supplement the supplement for matching (e.g., set of string to match).
	 * @param matcher takes a node and the supplement, and returns true if its field matches to the specific condition.
	 * @param getter takes a node and returns a node.
	 * @return the first node matching the specific condition.
	 */
	public <T>N getNearestNode(T supplement, BiPredicate<N,T> matcher, Function<N,N> getter)
	{
		N node = getter.apply(self());
		
		while (node != null)
		{
			if (matcher.test(node, supplement)) return node;
			node = getter.apply(node);
		}
		
		return null;
	}
	
	public int distanceToTop()
	{
		N node = parent;
		int dist;
		
		for (dist=0; node != null; dist++)
			node = node.parent;
		
		return dist;
	}
	
	/** Isolates this node from its parent, children, and siblings. */
	protected void isolate()
	{
		parent        = null;
		left_sibling  = null;
		right_sibling = null;
		children.clear();
	}
	
	/** Sets two nodes siblings of each other. */
	protected void setSiblings(N left, N right)
	{
		if (left  != null)	left.right_sibling = right;
		if (right != null)	right.left_sibling = left;
	}
	
	/**
	 * @param getter takes a node and returns a list of nodes.
	 * @return the list of second order elements according to the getter. 
	 */
	protected List<N> getSecondOrder(Function<N,List<N>> getter)
	{
		return getter.apply(self()).stream().flatMap(n -> getter.apply(n).stream()).filter(n -> n != self()).collect(Collectors.toList());
	}
	
	/**
	 * e.g., getFirst(list, "VB", (n,s) -> AbstractNode::isSyntacticTag);
	 * @param supplement the supplement for matching (e.g., set of string to match).
	 * @param matcher takes a node and the supplement, and returns true if its field matches to the specific condition.
	 * @return the first node in the list matching the condition.
	 */
	static public <N,T>N getFirst(List<N> list, T supplement, BiPredicate<N,T> matcher)
	{
		return list.stream().filter(n -> matcher.test(n, supplement)).findFirst().orElse(null);
	}
	
	/**
	 * e.g., getLast(list, "VB", (n,s) -> AbstractNode::isSyntacticTag);
	 * @param supplement the supplement for matching (e.g., set of string to match).
	 * @param matcher takes a node and the supplement, and returns true if its field matches to the specific condition.
	 * @return the last node in the list matching the condition.
	 */
	static public <N,T>N getLast(List<N> list, T supplement, BiPredicate<N,T> matcher)
	{
		return list.stream().filter(n -> matcher.test(n, supplement)).reduce((a, b) -> b).orElse(null);
	}
	
	/**
	 * e.g., getMatchedList(list, "VB", (n,s) -> AbstractNode::isSyntacticTag);
	 * @param supplement the supplement for matching (e.g., set of string to match).
	 * @param matcher takes a node and the supplement, and returns true if its field matches to the specific condition.
	 * @return the sublist of the original list containing only matched items.
	 */
	static public <N,T>List<N> getMatchedList(List<N> list, T supplement, BiPredicate<N,T> matcher)
	{
		return list.stream().filter(n -> matcher.test(n, supplement)).collect(Collectors.toList());
	}
	
	/**
	 * e.g., contains(list, "VB", (n,s) -> AbstractNode::isSyntacticTag);
	 * @param supplement the supplement for matching (e.g., set of string to match).
	 * @param matcher takes a node and the supplement, and returns true if its field matches to the specific condition.
	 * @return true if the list contains any item matching the condition.
	 */
	static public <N,T>boolean contains(List<N> list, T supplement, BiPredicate<N,T> matcher)
	{
		return list.stream().anyMatch(n -> matcher.test(n, supplement));
	}
}
