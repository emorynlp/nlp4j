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
package edu.emory.mathcs.nlp.learning.util;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringItem implements Serializable, Comparable<StringItem>
{
	private static final long serialVersionUID = 4909247545825639480L;
	private int    type;
	private String value;
	private float  weight;
	
	public StringItem(int type, String value)
	{
		this(type, value, 1f);
	}
	
	public StringItem(int type, String value, float weight)
	{
		set(type, value, weight);
	}
	
	public int getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public void set(int type, String value, float weight)
	{
		setType(type);
		setValue(value);
		setWeight(weight);
	}
	
	@Override
	public int compareTo(StringItem o)
	{
		int sign = type - o.type;
		return (sign == 0) ? value.compareTo(o.value) : sign;
	}

	@Override
	public String toString()
	{
		return type+":"+value+":"+weight;
	}
}
