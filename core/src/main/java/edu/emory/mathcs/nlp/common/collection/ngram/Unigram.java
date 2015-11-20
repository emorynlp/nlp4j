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
package edu.emory.mathcs.nlp.common.collection.ngram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Unigram<T> implements Serializable
{
	private static final long serialVersionUID = 2431106431004828434L;
	private Object2IntMap<T> count_map;
	private int total_count;
	private T   best;

	public Unigram()
	{
		count_map = new Object2IntOpenHashMap<>();
		total_count = 0;
		best = null;
	}
	
	public void add(T key)
	{
		add(key, 1);
	}
	
	public void add(T key, int inc)
	{
		int c = FastUtils.increment(count_map, key, inc);
		if (best == null || get(best) < c) best = key;
		total_count += inc;
	}
	
	public int get(T key)
	{
		return count_map.get(key);
	}
	
	public ObjectDoublePair<T> getBest()
	{
		 return (best != null) ? new ObjectDoublePair<T>(best, MathUtils.divide(get(best), total_count)) : null;
	}
	
	public boolean contains(T key)
	{
		return count_map.containsKey(key);
	}
	
	public double getProbability(T key)
	{
		return MathUtils.divide(get(key), total_count);
	}
	
	public List<ObjectIntPair<T>> toList(int cutoff)
	{
		List<ObjectIntPair<T>> list = new ArrayList<>();
		
		for (Entry<T> p : count_map.object2IntEntrySet())
		{
			if (p.getValue() > cutoff)
				list.add(new ObjectIntPair<>(p.getKey(), p.getValue()));
		}
		
		return list;
	}
	
	public List<ObjectDoublePair<T>> toList(double threshold)
	{
		List<ObjectDoublePair<T>> list = new ArrayList<>();
		double d;
		
		for (Entry<T> p : count_map.object2IntEntrySet())
		{
			d = MathUtils.divide(p.getValue(), total_count);
			if (d > threshold) list.add(new ObjectDoublePair<T>(p.getKey(), d));
		}
		
		return list;
	}
	
	public Set<T> keySet()
	{
		return keySet(0);
	}
	
	/** @return a set of keys whose values are greater than the specific cutoff. */
	public Set<T> keySet(int cutoff)
	{
		Set<T> set = new HashSet<>();
		
		for (Entry<T> p : count_map.object2IntEntrySet())
		{
			if (p.getValue() > cutoff) set.add(p.getKey());
		}
		
		return set;
	}
	
	public Set<T> keySet(double threshold)
	{
		Set<T> set = new HashSet<>();
		double d;
		
		for (Entry<T> p : count_map.object2IntEntrySet())
		{
			d = MathUtils.divide(p.getValue(), total_count);
			if (d > threshold) set.add(p.getKey());
		}
		
		return set;
	}
}
