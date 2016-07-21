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

import edu.emory.mathcs.nlp.common.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ObjectDoublePair<T> implements Serializable, Comparable<ObjectDoublePair<T>>
{
	private static final long serialVersionUID = -5228607179375724504L;
	
	public T      o;
	public double d;
	
	public ObjectDoublePair(T o, double d)
	{
		set(o, d);
	}
	
	public void set(T o, double d)
	{
		this.o = o;
		this.d = d;
	}
	
	public T getObject()
	{
		return o;
	}
	
	public double getDouble()
	{
		return d;
	}

	@Override
	public int compareTo(ObjectDoublePair<T> p)
	{
		return MathUtils.signum(d - p.d);
	}
	
	@Override
	public String toString()
	{
		return "("+o.toString()+","+d+")";
	}
}