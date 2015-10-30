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
package edu.emory.mathcs.nlp.component.util.feature;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureItem<T> implements Serializable
{
	private static final long serialVersionUID = 7297765746466162241L;
	public Source    source;
	public Relation  relation;
	public int       window;
	public Field     field;
	public T         value;
	
	public FeatureItem(int window, Field field)
	{
		this(null, null, window, field, null);
	}
	
	public FeatureItem(int window, Field field, T value)
	{
		this(null, null, window, field, value);
	}
	
	public FeatureItem(Source source, int window, Field field)
	{
		this(source, null, window, field, null);
	}
	
	public FeatureItem(Source source, int window, Field field, T value)
	{
		this(source, null, window, field, value);
	}
	
	public FeatureItem(Source source, Relation relation, int window, Field field)
	{
		this(source, relation, window, field, null);
	}
	
	public FeatureItem(Source source, Relation relation, int window, Field field, T value)
	{
		this.source   = source;
		this.relation = relation;
		this.window   = window;
		this.field    = field;
		this.value    = value;
	}
}
