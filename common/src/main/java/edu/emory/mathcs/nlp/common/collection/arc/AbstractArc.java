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
package edu.emory.mathcs.nlp.common.collection.arc;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractArc<N> implements Comparable<AbstractArc<N>>, Serializable
{
	private static final long serialVersionUID = -2230309327619045746L;
	/** The delimiter between node and label. */
	static public final String LABEL_DELIM  = ":";
	/** The delimiter between arcs. */
	static public final String ARC_DELIM  = ";";
	protected N node;
	protected String label;
	
	public AbstractArc(N node, String label)
	{
		set(node, label);
	}
	
	public N getNode()
	{
		return node;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setNode(N node)
	{
		this.node = node;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void clear()
	{
		set(null, null);
	}
	
	public void set(N node, String label)
	{
		setNode (node);
		setLabel(label);
	}
	
	public boolean isNode(N node)
	{
		return this.node == node;
	}
	
	public boolean isLabel(String label)
	{
		return this.label.equals(label);
	}
	
	public boolean isLabel(Pattern pattern)
	{
		return label != null && pattern.matcher(label).find();
	}
	
	public boolean equals(N node, String label)
	{
		return isNode(node) && isLabel(label);
	}
	
	public boolean equals(N node, Pattern pattern)
	{
		return isNode(node) && isLabel(pattern);
	}
}