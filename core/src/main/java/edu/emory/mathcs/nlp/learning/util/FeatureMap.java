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

import edu.emory.mathcs.nlp.common.util.DSUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureMap implements Serializable
{
	private static final long serialVersionUID = 6496256881514652478L;
	private List<Object2IntMap<String>> index_map;
	private int feature_size;
	
	public FeatureMap()
	{
		this(1);
	}
	
	public FeatureMap(int beginIndex)
	{
		index_map = new ArrayList<>();
		feature_size = beginIndex;
	}
	
	/**
	 * Adds the specific feature to this map.
	 * @param type the type of the feature (starting at 0).
	 * @param value the value of the feature.
	 * @return the index of the specific feature.
	 */
	public int add(int type, String value)
	{
		// expand types
		for (int i=index_map.size(); i<=type; i++)
			index_map.add(new Object2IntOpenHashMap<>());
		
		Object2IntMap<String> map = index_map.get(type);
		int index = map.getOrDefault(value, -1);
		
		if (index < 0)
		{
			index = feature_size++;
			map.put(value, index);
		}
		
		return index;
	}
	
	/** @return the index of the specific feature if exists; otherwise, {@code -1}. */
	public int index(int type, String value)
	{
		return DSUtils.isRange(index_map, type) ? index_map.get(type).getOrDefault(value, -1) : -1;
	}
	
	/** @return the total number of features. */
	public int size()
	{
		return feature_size;
	}
	
	public List<Object2IntMap<String>> getIndexMaps()
	{
		return index_map;
	}
	
	/** Do not use this method unless you know what you are doing. */
	public void setSize(int size)
	{
		feature_size = size;
	}
	
	@Override
	public String toString()
	{
		return index_map.toString();
	}
}