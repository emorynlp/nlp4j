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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.propbank.PBLocation;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTNode implements Comparable<CTNode>
{
	static public final String DELIM_FUNCTION_TAG     = StringConst.HYPHEN;
	static public final String DELIM_EMPTY_CATEGORY   = StringConst.HYPHEN;
	static public final String DELIM_GAPPING_RELATION = StringConst.EQUAL;
	
	// basic information
	private String		s_wordForm = null;
	private String		s_constituentTag;
	private Set<String>	s_functionTags;
	private int			i_emptyCategoryIndex   = -1;
	private int			i_gappingRelationIndex = -1;
	
	// constituent information
	private CTNode		n_parent       = null;
	private CTNode		n_antecedent   = null;
	private CTNode		n_leftSibling  = null;
	private CTNode		n_rightSibling = null;
	private List<CTNode>n_children;
	private int			i_terminalID = -1;
	private int			i_tokenID    = -1;

	// propbank
	private List<PBArc>	pb_heads = null;
	private PBLocation	pb_location   = null;
	private String		pb_rolesetID  = null;
	
	// conversion
	private String named_entity_tag = null;
	
//	======================== Constructors ========================
	
	/** @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public CTNode(String tags)
	{
		n_children = new ArrayList<>();
		setTags(tags);
	}
	
	/** @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public CTNode(String tags, String form)
	{
		this(tags);
		setWordForm(form);
	}
	
//	======================== Getters ========================
	
	public String getNamedEntityTag()
	{
		return named_entity_tag;
	}

	/** @return all tags of this node in the Penn Treebank format. */
	public String getTags()
	{
		StringBuilder build = new StringBuilder();
		List<String> fTags = new ArrayList<>(s_functionTags);
		Collections.sort(fTags);
		
		build.append(s_constituentTag);
		
		for (String fTag : fTags)
		{
			build.append(DELIM_FUNCTION_TAG);
			build.append(fTag);
		}
		
		if (i_emptyCategoryIndex != -1)
		{
			build.append(DELIM_EMPTY_CATEGORY);
			build.append(i_emptyCategoryIndex);
		}
		
		if (i_gappingRelationIndex != -1)
		{
			build.append(DELIM_GAPPING_RELATION);
			build.append(i_gappingRelationIndex);
		}
		
		return build.toString();
	}
	
	/**
	 * @return the word-form of this node.
	 * If this is not a terminal node, returns {@code null}.
	 */
	public String getWordForm()
	{
		return s_wordForm;
	}
	
	/** @return the pos/phrase/clause tag of this node. */
	public String getConstituentTag()
	{
		return s_constituentTag;
	}
	
	/** @return a set of function tags of this node. */
	public Set<String> getFunctionTagSet()
	{
		return s_functionTags;
	}
	
	/** @return the index of empty category if this node is the antecedent of the corresponding node (e.g., NP-1); otherwise, {@code -1}. */
	public int getEmptyCategoryIndex()
	{
		return i_emptyCategoryIndex;
	}
	
	/** @return the index of gapping relation if this node is in ellipsis (e.g., NP=1); otherwise, {@code -1}. */
	public int getGappingRelationIndex()
	{
		return i_gappingRelationIndex;
	}
	
	/** @return a list of children of this node. */
	public List<CTNode> getChildrenList()
	{
		return n_children;
	}

	/**
	 * @return an immutable list of sub-children of this node.
     * The sublist begins at the specific position and extends to the end.
     * @param fstId the ID of the first child (inclusive).
     * @throws IndexOutOfBoundsException for an illegal ID.
     */
	public List<CTNode> getChildrenList(int fstId)
	{
		return n_children.subList(fstId, getChildrenSize());
	}

	/**
	 * @return an immutable list of sub-children of this node.
     * The sublist begins and ends at the specific positions.
     * @param fstId the ID of the first child (inclusive).
     * @param lstId the ID of the last child (exclusive)
     * @throws IndexOutOfBoundsException for an illegal ID.
     */
	public List<CTNode> getChildrenList(int fstId, int lstId)
	{
		return n_children.subList(fstId, lstId);
	}
	
	public List<CTNode> getChildrenList(Predicate<CTNode> matcher)
	{
		List<CTNode> list = new ArrayList<>();
		
		for (CTNode child : n_children)
		{
			if (matcher.test(child))
				list.add(child);
		}
		
		return list;
	}
	
	public int getChildrenSize()
	{
		return n_children.size();
	}
	
	/**
	 * @return the index'th child of this node if exists; otherwise, {@code null}.
	 * @param index starting with 0.
	 */
	public CTNode getChild(int index)
	{
		return DSUtils.isRange(n_children, index) ? n_children.get(index) : null;
	}
	
	/** @return the first child of this node if exists; otherwise, {@code null}. */
	public CTNode getFirstChild()
	{
		return getChild(0);
	}
	
	public CTNode getLastChild()
	{
		return getChild(n_children.size()-1);
	}
	
	public CTNode getFirstChild(Predicate<CTNode> matcher)
	{
		for (CTNode child : n_children)
		{
			if (matcher.test(child))
				return child;
		}
		
		return null;
	}
	
	public CTNode getLastChild(Predicate<CTNode> matcher)
	{
		CTNode child;	int i;
		
		for (i=n_children.size()-1; i>=0; i--)
		{
			child = n_children.get(i);
			
			if (matcher.test(child))
				return child;
		}
		
		return null;
	}
	
	/** @return the parent of this node if exists; otherwise, {@code null}. */
	public CTNode getParent()
	{
		return n_parent;
	}
	
	public CTNode getNearestAncestor(Predicate<CTNode> matcher)
	{
		CTNode node = n_parent;
		
		while (node != null)
		{
			if (matcher.test(node)) return node;
			node = node.n_parent;
		}
		
		return null;
	}
	
	public CTNode getHighestChainedAncestor(Predicate<CTNode> matcher)
	{
		CTNode node = n_parent, ancestor = null;
		
		while (node != null)
		{
			if (matcher.test(node))	ancestor = node;
			else						break;

			node = node.n_parent;
		}
		
		return ancestor;
	}
	
	public CTNode getLowestCommonAncestor(CTNode node)
	{
		if (this.isDescendantOf(node))	return node;
		if (node.isDescendantOf(this))	return this;
		
		CTNode ancestor = n_parent;
		
		while (ancestor != null)
		{
			if (node.isDescendantOf(ancestor))
				return ancestor;
			
			ancestor = ancestor.n_parent;
		}
		
		return null;
	}
	
	public CTNode getFirstDescendant(Predicate<CTNode> matcher)
	{
		return getFirstDescendantAux(n_children, matcher);
	}
	
	private CTNode getFirstDescendantAux(Collection<CTNode> nodes, Predicate<CTNode> matcher)
	{
		for (CTNode node : nodes)
		{
			if (matcher.test(node))
				return node;
			
			if ((node = getFirstDescendantAux(node.n_children, matcher)) != null)
				return node;
		}
		
		return null;
	}
	
	public CTNode getFirstLowestChainedDescendant(Predicate<CTNode> matcher)
	{
		CTNode node = getFirstChild(matcher), descendant = null;
		
		while (node != null)
		{
			descendant = node;
			node = node.getFirstChild(matcher);
		}

		return descendant;
	}
	
	/** @return the left sibling of this node if exists; otherwise, {@code null}. */
	public CTNode getLeftSibling()
	{
		return n_leftSibling;
	}
	
	public CTNode getLeftNearestSibling(Predicate<CTNode> matcher)
	{
		CTNode node = n_leftSibling;
		
		while (node != null)
		{
			if (matcher.test(node))
				return node;
			
			node = node.n_leftSibling;
		}
		
		return null;
	}
	
	/** @return the right sibling of this node if exists; otherwise, {@code null}. */
	public CTNode getRightSibling()
	{
		return n_rightSibling;
	}
	
	public CTNode getRightNearestSibling(Predicate<CTNode> matcher)
	{
		CTNode node = n_rightSibling;
		
		while (node != null)
		{
			if (matcher.test(node))
				return node;
			
			node = node.n_rightSibling;
		}
		
		return null;
	}
	
	/** @return the antecedent of this node if exists; otherwise, {@code null}. */
	public CTNode getAntecedent()
	{
		return n_antecedent;
	}
	
	/**
	 * @return the ID (starting at 0) of this node among other terminal nodes in the tree.
	 * If this is not a terminal node, returns {@code -1}.
	 */
	public int getTerminalID()
	{
		return i_terminalID;
	}
	
	public CTNode getFirstTerminal()
	{
		return getFirstTerminalAux(this);
	}
	
	private CTNode getFirstTerminalAux(CTNode node)
	{
		return node.isTerminal() ? node : getFirstTerminalAux(node.getFirstChild());
	}
	
	public CTNode getLastTerminal()
	{
		return getLastTerminalAux(this);
	}
	
	private CTNode getLastTerminalAux(CTNode node)
	{
		return node.isTerminal() ? node : getLastTerminalAux(node.getLastChild());
	}
	
	public Set<Integer> getTerminalIDSet()
	{
		return getTerminalList().stream().map(node -> node.getTerminalID()).collect(Collectors.toCollection(HashSet::new));
	}
	
	/** @return a list of terminal nodes in the subtree of this node. */
	public List<CTNode> getTerminalList()
	{
		List<CTNode> terminals = new ArrayList<>();
		getTerminalListAux(terminals, this);
		
		return terminals;
	}
	
	private void getTerminalListAux(Collection<CTNode> terminals, CTNode node)
	{
		if (node.isTerminal())
			terminals.add(node);		
		else
		{
			for (CTNode child : node.n_children)
				getTerminalListAux(terminals, child);
		}
	}
	
	/**
	 * @return the ID (starting at 0) of this node among other terminal nodes (disregarding empty categories) in the tree.
	 * If this is not a terminal node, returns {@code -1}.
	 */
	public int getTokenID()
	{
		return i_tokenID;
	}
	
	/** @return a list of terminal nodes in the subtree of this node, disregarding empty categories. */
	public List<CTNode> getTokenList()
	{
		List<CTNode> tokens = new ArrayList<>();
		getSubTokens(tokens, this);
		
		return tokens;
	}
	
	private void getSubTokens(Collection<CTNode> tokens, CTNode node)
	{
		if (node.isTerminal())
		{
			if (!node.isEmptyCategory())
				tokens.add(node);
		}
		else
		{
			for (CTNode child : node.n_children)
				getSubTokens(tokens, child);
		}
	}
	
	public List<CTNode> getEmptyCategoryListInSubtree(Pattern wordFormPattern)
	{
		List<CTNode> list = new ArrayList<>();
		getEmptyCategoryListInSubtreeAux(list, this, wordFormPattern);
		
		return list;
	}
	
	private void getEmptyCategoryListInSubtreeAux(Collection<CTNode> collection, CTNode node, Pattern wordFormPattern)
	{
		if (node.isEmptyCategory() && node.matchesWordForm(wordFormPattern))
			collection.add(node);
		
		for (CTNode child : node.n_children)
			getEmptyCategoryListInSubtreeAux(collection, child, wordFormPattern);
	}
	
	public int getDistanceToTop()
	{
		CTNode node = n_parent;
		int dist;
		
		for (dist=0; node != null; dist++)
			node = node.n_parent;
		
		return dist;
	}

//	======================== Setters ========================
	
	public void setNamedEntityTag(String tag)
	{
		named_entity_tag = tag;
	}
	
	/** @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public void setTags(String tags)
	{
		s_functionTags = new HashSet<>();
		
		if (tags.charAt(0) == '-')
		{
			setConstituentTag(tags);
			return;
		}
		
		StringTokenizer tok = new StringTokenizer(tags, DELIM_FUNCTION_TAG+DELIM_GAPPING_RELATION, true);
		String delim, tag;
		
		setConstituentTag(tok.nextToken());
		
		while (tok.hasMoreTokens())
		{
			delim = tok.nextToken();
			
			if (!tok.hasMoreTokens())
			{
				System.err.println("Error: illegal tag \""+tags+"\"");
				break;
			}
			
			tag = tok.nextToken();
			
			if (delim.equals(DELIM_FUNCTION_TAG))
			{
				if (StringUtils.containsDigitOnly(tag))
				{
					if (i_emptyCategoryIndex == -1)
						setEmptyCategoryIndex(Integer.parseInt(tag));
					else
						setGappingRelationIndex(Integer.parseInt(tag));
				}
				else
					addFunctionTag(tag);
			}
			else // if (delim.equals(DELIM_GAPPING_RELATION))
				setGappingRelationIndex(Integer.parseInt(tag));
		}
	}
	
	public void appendWordForm(String wordForm)
	{
		s_wordForm += wordForm;
	}
	
	public void setWordForm(String wordForm)
	{
		s_wordForm = wordForm;
	}
	
	public void setConstituentTag(String tag)
	{
		s_constituentTag = tag;
	}
	public void addFunctionTag(String tag)
	{
		s_functionTags.add(tag);
	}
	
	public void addFunctionTags(Collection<String> tags)
	{
		s_functionTags.addAll(tags);
	}
	
	public void removeFunctionTag(String tag)
	{
		s_functionTags.remove(tag);
	}
	
	public void clearFunctionTags()
	{
		s_functionTags.clear();
	}
	
	public void setEmptyCategoryIndex(int index)
	{
		i_emptyCategoryIndex = index;
	}
	
	public void setGappingRelationIndex(int index)
	{
		i_gappingRelationIndex = index;
	}
	
	public void setParent(CTNode node)
	{
		n_parent = node;
	}
	
	public void setAntecedent(CTNode node)
	{
		n_antecedent = node;
	}
	
	public void addChild(CTNode child)
	{
		setSiblings(getLastChild(), child);
		n_children.add(child);
		child.setParent(this);
	}
	
	/**
	 * @throws IndexOutOfBoundsException
	 */
	public void addChild(int index, CTNode child)
	{
		if (index < 0 || index > n_children.size())
			throw new IndexOutOfBoundsException();
		
		setSiblings(getChild(index-1), child);
		setSiblings(child, getChild(index));
		n_children.add(index, child);
		child.setParent(this);
	}
	
	public void setChild(int index, CTNode child)
	{
		if (index < 0 || index > n_children.size())
			throw new IndexOutOfBoundsException();
		
		setSiblings(getChild(index-1), child);
		setSiblings(child, getChild(index+1));
		n_children.set(index, child);
		child.setParent(this);
	}
	
	public void removeChild(int index)
	{
		if (!DSUtils.isRange(n_children, index))
			throw new IndexOutOfBoundsException(Integer.toString(index));
		
		setSiblings(getChild(index-1), getChild(index+1));
		n_children.remove(index).n_parent = null;
	}
	
	public void removeChild(CTNode child)
	{
		removeChild(n_children.indexOf(child));
	}
	
	public void replaceChild(CTNode oldChild, CTNode newChild)
	{
		setChild(n_children.indexOf(oldChild), newChild);
	}
	
	private void setSiblings(CTNode leftSibling, CTNode rightSibling)
	{
		if (leftSibling != null)
			leftSibling.n_rightSibling = rightSibling;
		
		if (rightSibling != null)
			rightSibling.n_leftSibling = leftSibling;
	}
	
	public void setTerminalID(int id)
	{
		i_terminalID = id;
	}
	
	public void setTokenID(int id)
	{
		i_tokenID = id;
	}

//	======================== Booleans ========================
	
	/** @return {@code true} if the word form of this node is the specific word form. */
	public boolean isWordForm(String wordForm)
	{
		return wordForm.equals(s_wordForm);
	}
	
	public boolean isWordFormIgnoreCase(String wordForm)
	{
		return wordForm.equalsIgnoreCase(s_wordForm);
	}
	
	public boolean matchesWordForm(Pattern pattern)
	{
		return pattern.matcher(s_wordForm).find();
	}
	
	/** @return {@code true} if the constituent tag of this node is the specific tags. */
	public boolean isConstituentTag(String tag)
	{
		return s_constituentTag.equals(tag);
	}
	
	public boolean isConstituentTagAny(Set<String> tags)
	{
		return tags.contains(s_constituentTag);
	}
	
	/**
	 * @return {@code true} if the constituent tag of this node is any of the specific tags.
	 * If the length of {@code tags} is long, use {@link #isConstituentTagAny(Set)} instead.
	 */
	public boolean isConstituentTagAny(String... tags)
	{
		for (String tag : tags)
		{
			if (isConstituentTag(tag))
				return true;
		}
		
		return false;
	}
	
	public boolean matches(Predicate<CTNode> matcher)
	{
		return matcher.test(this);
	}
	
	/** @return {@code true} if this node matches the specific pattern of constituent tags. */
	public boolean matchesConstituentTag(Pattern pattern)
	{
		return pattern.matcher(s_constituentTag).find();
	}
	
	/** @return {@code true} if this node has the specific function tag. */
	public boolean hasFunctionTag(String tag)
	{
		return s_functionTags.contains(tag);
	}

	/** @return {@code true} if this node has all of the specific function tags. */
	public boolean hasFunctionTagAll(String... tags)
	{
		for (String tag : tags)
		{
			if (!hasFunctionTag(tag))
				return false;
		}
		
		return true;
	}
	
	/** @return {@code true} if this node has any of the specific function tags. */
	public boolean hasFunctionTagAny(String... tags)
	{
		for (String tag : tags)
		{
			if (hasFunctionTag(tag))
				return true;
		}
		
		return false;
	}
	
	/** @return {@code true} if this node has any of the function tags in the specific set. */
	public boolean hasFunctionTagAny(Set<String> tags)
	{
		return DSUtils.hasIntersection(tags, s_functionTags);
	}
	
	/** @return {@code true} if this node is non-terminal. */
	public boolean isTerminal()
	{
		return n_children.isEmpty();
	}
	
	/** @return {@code true} if this node is an empty category. */
	public boolean isEmptyCategory()
	{
		return isConstituentTag(CTTagEn.NONE);
	}
	
	/** @return {@code true} if this node is single branched and its terminal node is an empty category. */
	public boolean isEmptyCategoryTerminal()
	{
		CTNode node = this;
		
		while (!node.isTerminal())
		{
			if (node.getChildrenSize() > 1)
				return false;
			else
				node = node.getFirstChild();
		}
		
		return node.isEmptyCategory();
	}
	
	/** @return {@code true} if this node is a descendant of the specific node. */
	public boolean isDescendantOf(CTNode node)
	{
		CTNode ancestor = getParent();

		while (ancestor != null)
		{
			if (ancestor == node)
				return true;
			
			ancestor = ancestor.getParent();
		}
		
		return false;
	}
	
	/** @return {@code true} if this node is a left-sibling of the specific node. */
	public boolean isLeftSiblingOf(CTNode node)
	{
		if (n_parent != node.n_parent) return false;
		CTNode left = node.n_leftSibling;
		
		while (left != null)
		{
			if (left == this) return true;
			left = left.n_leftSibling;
		}
		
		return false;
	}
	
	/** @return {@code true} if this node is a right-sibling of the specific node. */
	public boolean isRightSiblingOf(CTNode node)
	{
		if (n_parent != node.n_parent) return false;
		CTNode right = node.n_rightSibling;
		
		while (right != null)
		{
			if (right == this) return true;
			right = right.n_rightSibling;
		}
		
		return false;
	}
	
	public boolean hasParent()
	{
		return n_parent != null;
	}
	
	public boolean hasAntecedent()
	{
		return n_antecedent != null;
	}
	
	public boolean hasLeftSibling()
	{
		return n_leftSibling != null;
	}
	
	public boolean hasRightSibling()
	{
		return n_rightSibling != null;
	}
	
	public boolean hasNoFunctionTag()
	{
		return s_functionTags.isEmpty();
	}
	
	public boolean wordFormStartsWith(String prefix)
	{
		return s_wordForm.startsWith(prefix);
	}
	
	public boolean containsChild(Predicate<CTNode> matcher)
	{
		return getFirstChild(matcher) != null;
	}
	
//	======================== Strings ========================

	/** Calls {@link #toWordForms(boolean, String)}, where {@code includeEmptyCategories=false, delim=" "}. */
	public String toForms()
	{
		return toWordForms(false, StringConst.SPACE);
	}
	
	/**
	 * @return the string containing ordered word-forms of the subtree of this node.
	 * @param includeEmptyCategories if {@code true}, include forms of empty categories.
	 */
	public String toWordForms(boolean includeEmptyCategories, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (CTNode node : getTerminalList())
		{
			if (includeEmptyCategories || !node.isEmptyCategory())
			{
				build.append(delim);
				build.append(node.s_wordForm);
			}
		}
		
		return build.length() == 0 ? "" : build.substring(delim.length());
	}
	
	@Override
	public String toString()
	{
		return toString(false, false, StringConst.NEW_LINE);
	}
	
	public String toStringLine()
	{
		return toString(false, false, StringConst.SPACE);
	}
	
	public String toString(boolean includeLineNumbers, boolean includeAntecedentPointers, String delim)
	{
		List<String> lTree = new ArrayList<>();
		toStringAux(lTree, this, StringConst.EMPTY, includeAntecedentPointers, delim.equals(StringConst.SPACE));
		
		StringBuilder build = new StringBuilder();
		int i, size = lTree.size();
		
		for (i=0; i<size; i++)
		{
			build.append(delim);
			if (includeLineNumbers) build.append(String.format("%3d: ", i));
			build.append(lTree.get(i));
		}
			
		return build.substring(delim.length());
	}
	
	private void toStringAux(List<String> lTree, CTNode curr, String sTags, boolean includeAntecedentPointers, boolean isSpace)
	{
		if (curr.isTerminal())
		{
			StringBuilder build = new StringBuilder();
			
			build.append(sTags);
			build.append(StringConst.LRB);
			build.append(curr.getTags());
			build.append(StringConst.SPACE);
			build.append(curr.s_wordForm);
			build.append(StringConst.RRB);
			
			if (includeAntecedentPointers)
			{
				if (curr.n_antecedent != null)
				{
					build.append(StringConst.LSB);
					build.append(curr.n_antecedent.getTags());
					build.append(StringConst.RSB);
				}
			}
			
			lTree.add(build.toString());
		}
		else
		{
			sTags += StringConst.LRB + curr.getTags() + StringConst.SPACE;

			for (CTNode child : curr.n_children)
			{
				toStringAux(lTree, child, sTags, includeAntecedentPointers, isSpace);
				if (child.n_leftSibling == null) sTags = isSpace ? StringConst.EMPTY : StringUtils.spaces(sTags.length());	// indent	
			}

			int last = lTree.size() - 1;
			lTree.set(last, lTree.get(last)+StringConst.RRB);
		}
	}
	
	@Override
	public int compareTo(CTNode node)
	{
		return i_terminalID - node.i_terminalID;
	}
	
//	======================== Semantic role labeling ========================
	
	/** @return the PropBank location of this node if exists; otherwise, {@code null}. */
	public PBLocation getPBLocation()
	{
		return pb_location;
	}
	
	public void setPBLocation(int terminalID, int height)
	{
		pb_location = new PBLocation(terminalID, height);
	}
	
	public void initPropBank()
	{
		pb_heads = new ArrayList<>();
	}
	
	/** @return the graph structure for semantic role labeling if exists; otherwise, {@code null}. */
	public List<PBArc> getPBHeads()
	{
		return pb_heads;
	}
	
	public void addPBHead(PBArc arc)
	{
		pb_heads.add(arc);
	}
	
	public boolean isPBHead()
	{
		return pb_rolesetID != null;
	}
	
	public String getPBRolesetID()
	{
		return pb_rolesetID;
	}
	
	public void setPBRolesetID(String rolesetID)
	{
		pb_rolesetID = rolesetID;
	}
}