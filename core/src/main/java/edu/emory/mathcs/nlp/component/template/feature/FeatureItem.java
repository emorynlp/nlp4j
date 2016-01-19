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
package edu.emory.mathcs.nlp.component.template.feature;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureItem implements Serializable
{
	private static final long serialVersionUID = 7297765746466162241L;
	public Source    source;
	public Relation  relation;
	public int       window;
	public Field     field;
	public Object    attribute;
	
	public FeatureItem(Source source, Relation relation, int window, Field field, Object attribute)
	{
		this.source    = source;
		this.relation  = relation;
		this.window    = window;
		this.field     = field;
		this.attribute = attribute;
	}
	
	public Source getSource()
	{
		return source;
	}

	public void setSource(Source source)
	{
		this.source = source;
	}

	public Relation getRelation()
	{
		return relation;
	}

	public void setRelation(Relation relation)
	{
		this.relation = relation;
	}

	public int getWindow()
	{
		return window;
	}

	public void setWindow(int window)
	{
		this.window = window;
	}

	public Field getField()
	{
		return field;
	}

	public void setField(Field field)
	{
		this.field = field;
	}

	public Object getAttribute()
	{
		return attribute;
	}

	public void setAttribute(Object attribute)
	{
		this.attribute = attribute;
	}
	
	public String toString()
	{
		return String.format("%s: source=%s, relation=%s, attribute=%s, window=%d", field, source, relation, attribute, window);
	}
}
