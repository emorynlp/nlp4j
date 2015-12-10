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
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LabelMap implements Serializable
{
	private static final long serialVersionUID = 6353276311284514969L;
	private Object2IntMap<String> index_map;
	private List<String> labels;
	
	public LabelMap()
	{
		index_map = new Object2IntOpenHashMap<>();
		labels    = new ArrayList<>();
	}
	
	/** @return the index of the specific label. */
	public int add(String label)
	{
		int index = index(label);
		
		if (index < 0)
		{
			index = labels.size();
			index_map.put(label, index);
			labels.add(label);
		}
		
		return index;
	}
	
	/** @return the index of the specific label if exists; otherwise, {@code -1}. */
	public int index(String label)
	{
		return index_map.getOrDefault(label, -1);
	}
	
	/** @return the index'th label. */
	public String getLabel(int index)
	{
		return labels.get(index);
	}
	
	/** @return the list of all labels. */
	public List<String> getLabelList()
	{
		return labels;
	}
	
	/** @return the total number of labels. */
	public int size()
	{
		return labels.size();
	}
	
	@Override
	public String toString()
	{
		return labels.toString();
	}
}