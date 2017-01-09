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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.structure.node.AbstractNode;
import edu.emory.mathcs.nlp.structure.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTNode extends AbstractNode<CTNode> implements Comparable<CTNode>
{
	private static final long serialVersionUID = 8550646818628570193L;
	
	static public final String DELIM_FUNCTION_TAG = StringConst.HYPHEN;
	static public final String DELIM_CO_INDEX     = StringConst.HYPHEN;
	static public final String DELIM_GAP_INDEX    = StringConst.EQUAL;
	
	// fields
	private Set<String>	function_tags;
	private int         co_index = -1;
	private int         gap_index = -1;
	private int         terminal_id = -1;
	private int	        height = -1;
	
	// referents
	private CTNode antecedent = null;
	private CTNode gapping = null;
	
	// dependency structures
	private CTNode phrase_head = null;
	private CTNode terminal_head = null;
	private CTArc  primary_head;
	private List<CTArc> secondary_heads;
	
	// predicate argument structures
	private String		frame_id = null;
	private List<CTArc> semantic_heads;
	
//	======================== Constructors ========================
	
	/** @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public CTNode(String tags)
	{
		this(tags, null);
	}
	
	/**
	 * @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2").
	 * @param form word form.
	 */
	public CTNode(String tags, String form)
	{
		super(-1, form, null, null, null, new FeatMap());
		primary_head = new CTArc(null, null);
		secondary_heads = new ArrayList<>();
		semantic_heads = new ArrayList<>();
		setTags(tags);
	}
	
//	============================== Abstract Methods ==============================
	
	@Override
	public CTNode self() { return this; }
	
	@Override
	public int getChildIndex(CTNode node) { return children.indexOf(node); }
	
	@Override
	protected int getDefaultIndex(List<CTNode> list, CTNode node) { return list.size(); }
	
//	======================== Fields ========================
	
	/** @return all tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public String getTags()
	{
		List<String> fTags = new ArrayList<>(function_tags);
		StringBuilder build = new StringBuilder();
		Collections.sort(fTags);
		
		build.append(syntactic_tag);
		
		for (String fTag : fTags)
		{
			build.append(DELIM_FUNCTION_TAG);
			build.append(fTag);
		}
		
		if (!isEmptyCategory())
		{
			if (co_index != -1)
			{
				build.append(DELIM_CO_INDEX);
				build.append(co_index);
			}
			
			if (gap_index != -1)
			{
				build.append(DELIM_GAP_INDEX);
				build.append(gap_index);
			}			
		}
		
		return build.toString();
	}
	
	/** @return the set of function tags. */
	public Set<String> getFunctionTags()
	{
		return function_tags;
	}
	
	/** @return the index of an empty category if this node is the antecedent of the empty category (e.g., NP-1); otherwise, -1. */
	public int getCoIndex()
	{
		return co_index;
	}
	
	/** @return the index of a gapping relation if this node is in ellipsis (e.g., NP=1); otherwise, -1. */
	public int getGapIndex()
	{
		return gap_index;
	}
	
	/** @return the ID (starting at 0) of this node among other terminal nodes in the tree if this is a terminal; otherwise -1. */
	public int getTerminalID()
	{
		return terminal_id;
	}
	
	/** @return the semantic frame ID if exists; otherwise, null. */
	public String getFrameID()
	{
		return frame_id;
	}
	
	/** @return the height from its first terminal. */
	public int getHeight()
	{
		return height;
	}
	
	/** @param tags tags in the Penn Treebank format (e.g., "NP-SBJ-TMP-1=2"). */
	public void setTags(String tags)
	{
		function_tags = new HashSet<>();
		
		if (tags.charAt(0) == '-')
		{
			setSyntacticTag(tags);
			return;
		}
		
		StringTokenizer tok = new StringTokenizer(tags, DELIM_FUNCTION_TAG+DELIM_GAP_INDEX, true);
		String delim, tag;
		
		setSyntacticTag(tok.nextToken());
		
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
					if (co_index == -1)
						setCoIndex(Integer.parseInt(tag));
					else
						setGapIndex(Integer.parseInt(tag));
				}
				else
					addFunctionTag(tag);
			}
			else // if (delim.equals(DELIM_GAPPING_RELATION))
				setGapIndex(Integer.parseInt(tag));
		}
	}
	
	public void addFunctionTag(String tag)
	{
		function_tags.add(tag);
	}
	
	public void addFunctionTags(Collection<String> tags)
	{
		function_tags.addAll(tags);
	}
	
	public void removeFunctionTag(String tag)
	{
		function_tags.remove(tag);
	}
	
	public void clearFunctionTags()
	{
		function_tags.clear();
	}
	
	public void setCoIndex(int index)
	{
		co_index = index;
	}
	
	public void setGapIndex(int index)
	{
		gap_index = index;
	}
	
	public void setTerminalID(int id)
	{
		terminal_id = id;
	}
	
	/** @param height the height from the first terminal. */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void setLocation(int terminal_id, int height)
	{
		setTerminalID(terminal_id);
		setHeight(height);
	}
	
	/** @return true if this node has any function tag; otherwise, false. */
	public boolean hasFunctionTag()
	{
		return !function_tags.isEmpty();
	}
	
	/** @return true if this node contains the specific function tag; otherwise, false. */
	public boolean isFunctionTag(String tag)
	{
		return function_tags.contains(tag);
	}
	
	/** @return true if this node has any of the specific function tags; otherwise, false. */
	public boolean isFunctionTag(Collection<String> tags)
	{
		return DSUtils.hasIntersection(function_tags, tags);
	}
	
	/** @return true if this node has any of the specific function tags; otherwise, false. */
	public boolean isFunctionTag(String... tags)
	{
		return Arrays.stream(tags).anyMatch(tag -> isFunctionTag(tag));
	}

	/** @return true if this node has all of the specific function tags; otherwise, false. */
	public boolean isFunctionTagAll(String... tags)
	{
		return Arrays.stream(tags).allMatch(tag -> isFunctionTag(tag));
	}
	
	/** @return true if this node matches both the syntactic tag and the function tag. */
	public boolean andSF(String syntactic_tag, String function_tag)
	{
		return isSyntacticTag(syntactic_tag) && isFunctionTag(function_tag);
	}
	
	public boolean hasCoIndex()
	{
		return co_index >= 0;
	}
	
	public boolean hasGapIndex()
	{
		return gap_index >= 0;
	}
	
//	======================== Referents ========================
	
	/** @return the antecedent of this node if exists; otherwise, null. */
	public CTNode getAntecedent()
	{
		return antecedent;
	}
	
	/** Sets the antecedent of this node to the specific node. */
	public void setAntecedent(CTNode node)
	{
		antecedent = node;
	}
	
	/** @return true if this node has an antecedent; otherwise, false. */
	public boolean hasAntecedent()
	{
		return antecedent != null;
	}
	
	/** @return the gapping of this node if exists; otherwise, null. */
	public CTNode getGapping()
	{
		return gapping;
	}
	
	/** Sets the gapping of this node to the specific node. */
	public void setGapping(CTNode node)
	{
		gapping = node;
	}
	
	/** @return true if this node has a gapping relation; otherwise, false. */
	public boolean hasGapping()
	{
		return gapping != null;
	}
	
//	======================== Dependency Structures ========================
	
	public CTArc getPrimaryHead()
	{
		return primary_head;
	}
	
	public void setPrimaryHead(CTArc arc)
	{
		setPrimaryHead(arc.getNode(), arc.getLabel());
	}
	
	public void setPrimaryHead(CTNode node, String label)
	{
		primary_head.set(node, label);
	}
	
	public void setPrimaryHead(CTNode node)
	{
		primary_head.setNode(node);
	}
	
	public boolean hasPrimaryHead()
	{
		return primary_head.getNode() != null;
	}
	
	public String getPrimaryLabel()
	{
		return primary_head.getLabel();
	}
	
	public void setPrimaryLabel(String label)
	{
		primary_head.setLabel(label);
	}
	
	public boolean hasPrimaryLabel()
	{
		return primary_head.getLabel() != null;
	}
	
	public boolean hasPrimaryHead(CTNode node)
	{
		return primary_head.isNode(node);
	}
	
	public boolean isPrimaryLabel(String label)
	{
		return primary_head.isLabel(label);
	}
	
	public List<CTArc> getSecondaryHeads()
	{
		return secondary_heads;
	}
	
	public CTArc getSecondaryHeads(CTNode head)
	{
		return secondary_heads.stream().filter(n -> n.isNode(head)).findFirst().orElse(null);
	}
	
	public void addSecondaryHead(CTNode node)
	{
		addSecondaryHead(node, null);
	}
	
	public void addSecondaryHead(CTArc arc)
	{
		addSecondaryHead(arc.getNode(), arc.getLabel());
	}
	
	public void addSecondaryHead(CTNode node, String label)
	{
		if (node == this) return;
		CTArc arc = secondary_heads.stream().filter(n -> n.isNode(node)).findAny().orElse(null);
		if (arc == null) secondary_heads.add(new CTArc(node, label));
		else arc.setLabel(label);
	}
	
	public void addSecondaryHeads(Collection<CTNode> nodes)
	{
		nodes.stream().forEach(n -> addSecondaryHead(n));
	}
	
	public CTNode getPhraseHead()
	{
		return phrase_head;
	}
	
	public void setPhraseHead(CTNode head)
	{
		phrase_head = head;
		
		if (head != null && !isTerminal())
			terminal_head = head.getTerminalHead();
	}
	
	public boolean hasPhraseHead()
	{
		return phrase_head != null;
	}
	
	public CTNode getTerminalHead()
	{
		return isTerminal() ? self() : terminal_head;
	}
	
//	======================== Predicate Argument Structures ========================

	public void setFrameID(String id)
	{
		frame_id = id;
	}
	
	/** @return true if the specific id matches this node's predicate id. */
	public boolean isFrameID(String id)
	{
		return id.equals(frame_id);
	}
	
	public boolean isPredicate()
	{
		return frame_id != null;
	}
	
	/** @return the set of predicates of this node. */
	public List<CTArc> getSemanticHeads()
	{
		return semantic_heads;
	}
	
	/** Adds a predicate of this node with a relation. */
	public void addSemanticHead(CTArc arc)
	{
		semantic_heads.add(arc);
	}
	
//	======================== Terminals ========================
	
	/** @return the first terminal of this node's subtree. */
	public CTNode getFirstTerminal()
	{
		return isTerminal() ? this : getNearestNode(CTNode::isTerminal, CTNode::getFirstChild);
	}
	
	/** @return the last terminal of this node's subtree. */
	public CTNode getLastTerminal()
	{
		return isTerminal() ? this : getNearestNode(CTNode::isTerminal, CTNode::getLastChild);
	}

	/** @return the list of terminals in this node's subtree. */
	public List<CTNode> getTerminals()
	{
		return flatten().filter(CTNode::isTerminal).collect(Collectors.toList());
	}
	
	/** @return the list of tokens in this node's subtree. */
	public List<CTNode> getTokens()
	{
		return flatten().filter(n -> n.isTerminal() && !n.isEmptyCategory()).collect(Collectors.toList());
	}
	
	/** @return the list of empty categories in this node's subtree. */
	public List<CTNode> getEmptyCategories(Pattern pattern)
	{
		return flatten().filter(n -> n.isEmptyCategory() && n.isForm(pattern)).collect(Collectors.toList());
	}
	
	/** @return true if this node is terminal; otherwise, false. */
	public boolean isTerminal()
	{
		return children.isEmpty();
	}
	
	/** @return true if this node is an empty category; otherwise, false. */
	public boolean isEmptyCategory()
	{
		return isSyntacticTag(CTTag.NONE);
	}
	
	/** @return true if this node is single branched and its terminal node is an empty category; otherwise, false. */
	public boolean isEmptyCategoryPhrase()
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
	
	public boolean isEmptyCategoryBranch()
	{
		return !getTerminals().stream().filter(n -> !n.isEmptyCategory()).findAny().isPresent();
	}
	
	/** @param forms in lower case. */
	public boolean matchesForms(String... forms)
	{
		List<CTNode> tokens = getTokens();
		if (tokens.size() != forms.length) return false;
		
		for (int i=0; i<tokens.size(); i++)
		{
			if (!tokens.get(i).isFormLowercase(forms[i]))
				return false;
		}
		
		return true;
	}
	
//	======================== Strings ========================

	/** Calls {@link #toForms(String, boolean)}, where {@code includeEmptyCategories=false, delim=" "}. */
	public String toForms()
	{
		return toForms(StringConst.SPACE, false);
	}
	
	/**
	 * @param empty_category if true, include forms of empty categories.
	 * @return the string containing ordered word-forms of the subtree of this node.
	 */
	public String toForms(String delim, boolean empty_category)
	{
		List<String> forms = getTerminals().stream().filter(n -> empty_category || !n.isEmptyCategory()).map(CTNode::getForm).collect(Collectors.toList());
		return Joiner.join(forms, delim);
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
	
	public String toString(boolean line_number, boolean antecedent, String delim)
	{
		List<String> parse = new ArrayList<>();
		toStringAux(parse, this, StringConst.EMPTY, antecedent, delim.equals(StringConst.SPACE));
		
		StringBuilder build = new StringBuilder();
		int i, size = parse.size();
		
		for (i=0; i<size; i++)
		{
			build.append(delim);
			if (line_number) build.append(String.format("%3d: ", i));
			build.append(parse.get(i));
		}
			
		return build.substring(delim.length());
	}
	
	private void toStringAux(List<String> parse, CTNode curr, String tags, boolean antecedent, boolean space)
	{
		if (curr.isTerminal())
		{
			StringBuilder build = new StringBuilder();
			
			build.append(tags);
			build.append(StringConst.LRB);
			build.append(curr.getTags());
			build.append(StringConst.SPACE);
			build.append(curr.form);
			if (curr.isEmptyCategory() && curr.hasCoIndex())
			{
				build.append(DELIM_CO_INDEX);
				build.append(curr.co_index);
			}
			build.append(StringConst.RRB);
			
			if (antecedent && curr.hasAntecedent())
			{
				build.append(StringConst.LSB);
				build.append(curr.antecedent.getTags());
				build.append(StringConst.RSB);
			}
			
			parse.add(build.toString());
		}
		else
		{
			tags += StringConst.LRB + curr.getTags() + StringConst.SPACE;

			for (CTNode child : curr.children)
			{
				toStringAux(parse, child, tags, antecedent, space);
				if (child.left_sibling == null) tags = space ? StringConst.EMPTY : StringUtils.spaces(tags.length());	// indent	
			}

			int last = parse.size() - 1;
			parse.set(last, parse.get(last)+StringConst.RRB);
		}
	}
	
	@Override
	public int compareTo(CTNode n)
	{
		return terminal_id == n.terminal_id ? height - n.height : terminal_id - n.terminal_id;
	}
}