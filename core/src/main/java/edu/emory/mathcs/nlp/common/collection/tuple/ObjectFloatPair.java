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
public class ObjectFloatPair<T> implements Serializable, Comparable<ObjectFloatPair<T>>
{
	private static final long serialVersionUID = -4442614450903889259L;

	public T     o;
	public float f;
	
	public ObjectFloatPair(T o, float f)
	{
		set(o, f);
	}
	
	public void set(T o, float f)
	{
		this.o = o;
		this.f = f;
	}
	
	public T getObject()
	{
		return o;
	}
	
	public float getFloat()
	{
		return f;
	}

	@Override
	public int compareTo(ObjectFloatPair<T> p)
	{
		return MathUtils.signum(f - p.f);
	}
	
	@Override
	public String toString()
	{
		return "("+o.toString()+","+f+")";
	}
}