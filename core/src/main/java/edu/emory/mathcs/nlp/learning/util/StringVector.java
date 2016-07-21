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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.Joiner;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringVector implements Serializable, Iterable<StringItem>
{
	private static final long serialVersionUID = -4051551027968492079L;
	private List<StringItem> vector;
	
	public StringVector()
	{
		vector = new ArrayList<>();
	}
	
	public StringItem get(int index)
	{
		return vector.get(index);
	}
	
	public void add(int type, String value)
	{
		add(new StringItem(type, value));
	}
	
	public void add(int type, String value, float weight)
	{
		add(new StringItem(type, value, weight));
	}
	
	public void add(StringItem item)
	{
		vector.add(item);
	}
	
	public int size()
	{
		return vector.size();
	}
	
	@Override
	public Iterator<StringItem> iterator()
	{
		return vector.iterator(); 
	}
	
	@Override
	public String toString()
	{
		return Joiner.join(vector, StringConst.SPACE);
	}
}