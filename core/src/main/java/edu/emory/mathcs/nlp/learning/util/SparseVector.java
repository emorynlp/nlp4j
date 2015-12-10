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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.Joiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseVector implements Serializable, Iterable<SparseItem>
{
	private static final long serialVersionUID = -1427072719834760188L;
	private List<SparseItem> vector;
	private int max_index;
	
	public SparseVector()
	{
		this(0);
	}
	
	public SparseVector(float bias)
	{
		vector = new ArrayList<>();
		addBias(bias);
		max_index = 0;
	}
	
	public SparseItem get(int index)
	{
		return vector.get(index);
	}
	
	public void add(int index)
	{
		add(new SparseItem(index));
	}
	
	public void add(int index, float value)
	{
		add(new SparseItem(index, value));
	}
	
	public void add(SparseItem item)
	{
		vector.add(item);
		max_index = Math.max(max_index, item.getIndex());
	}
	
	public void addBias(float bias)
	{
		if (bias > 0) add(0, bias);
	}
	
	public boolean isEmpty()
	{
		return vector.isEmpty();
	}
	
	public int size()
	{
		return vector.size();
	}

	public void sort()
	{
		Collections.sort(vector);
	}
	
	public int maxIndex()
	{
		return max_index;
	}
	
	public List<SparseItem> getVector()
	{
		return vector;
	}
	
	@Override
	public Iterator<SparseItem> iterator()
	{
		return vector.iterator();
	}

	@Override
	public String toString()
	{
		return Joiner.join(vector, StringConst.SPACE);
	}
}
