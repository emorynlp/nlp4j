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
package edu.emory.mathcs.nlp.common.collection.tuple;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ObjectCharPair<T> implements Serializable, Comparable<ObjectCharPair<T>>
{
	private static final long serialVersionUID = -5228607179375724504L;
	
	public T    o;
	public char c;
	
	public ObjectCharPair(T o, char c)
	{
		set(o, c);
	}
	
	public void set(T o, char c)
	{
		this.o = o;
		this.c = c;
	}

	@Override
	public int compareTo(ObjectCharPair<T> p)
	{
		return c - p.c;
	}
}