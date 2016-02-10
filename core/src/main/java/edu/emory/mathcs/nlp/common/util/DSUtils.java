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
package edu.emory.mathcs.nlp.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DSUtils
{
	private DSUtils() {}
	
	static public Set<String> createStringHashSet(InputStream in)
	{
		return createStringHashSet(in, true, false);
	}
	
	/**
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}.
	 * The file that the input-stream is created from consists of one entry per line. 
	 */
	static public Set<String> createStringHashSet(InputStream in, boolean trim, boolean decap)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Set<String> set = new HashSet<>();
		String line;

		try
		{
			while ((line = reader.readLine()) != null)
			{
				if (trim)
				{
					line = line.trim();
					if (line.isEmpty()) continue;
				}
				
				if (decap)
					line = StringUtils.toLowerCase(line);
				
				set.add(line);
			}			
		}
		catch (IOException e) {e.printStackTrace();}
		
		return set;
	}
	
	static public Map<String,String> createStringHashMap(InputStream in, CharTokenizer tokenizer)
	{
		return createStringHashMap(in, tokenizer, true);
	}
	
	/**
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}.
	 * The file that the input-stream is created from consists of one entry per line ("key"<delim>"value").
	 */
	static public Map<String,String> createStringHashMap(InputStream in, CharTokenizer tokenizer, boolean trim)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Map<String,String> map = new HashMap<>();
		String[] t;
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				if (trim)
				{
					line = line.trim();
					if (line.isEmpty()) continue;
				}
				
				t = tokenizer.tokenize(line);
				map.put(t[0], t[1]);
			}			
		}
		catch (IOException e) {e.printStackTrace();}
		
		return map;
	}

	static public <T extends Comparable<? extends T>>void sortReverseOrder(List<T> list)
	{
		Collections.sort(list, Collections.reverseOrder());
	}
	
	static public <T extends Comparable<? extends T>>void sortReverseOrder(T[] array)
	{
		Arrays.sort(array, Collections.reverseOrder());
	}

	static public <T>boolean hasIntersection(Collection<T> col1, Collection<T> col2)
	{
		if (col2.size() < col1.size())
		{
			Collection<T> tmp = col1;
			col1 = col2;
			col2 = tmp;
		}
		
		for (T item : col1)
		{
			if (col2.contains(item))
				return true;
		}
		
		return false;
	}

	/** @return a set containing all field values of this class. */
	static public Set<String> getFieldSet(Class<?> cs)
	{
		Set<String> set = new HashSet<>();
		
		try
		{
			for (Field f : cs.getFields())
				set.add(f.get(cs).toString());
		}
		catch (IllegalArgumentException e) {e.printStackTrace();}
		catch (IllegalAccessException e)   {e.printStackTrace();}
		
		return set;
	}
	
	/** @return the index'th item if exists; otherwise, {@code null}. */
	static public <T>T get(List<T> list, int index)
	{
		return isRange(list, index) ? list.get(index) : null;
	}
	
	/** @return the index'th item if exists; otherwise, {@code null}. */
	static public <T>T get(T[] array, int index)
	{
		return isRange(array, index) ? array[index] : null;
	}
	
	/** @return the last item in the list if exists; otherwise, {@code null}. */
	static public <T>T getLast(List<T> list)
	{
		return list.isEmpty() ? null : list.get(list.size()-1);
	}

	static public <T>boolean isRange(List<T> list, int index)
	{
		return 0 <= index && index < list.size();
	}
	
	static public <T>boolean isRange(T[] array, int index)
	{
		return 0 <= index && index < array.length;
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	static public int[] range(int beginIndex, int endIndex, int gap)
	{
		double d = MathUtils.divide(endIndex-beginIndex, gap);
		if (d < 0) return new int[0];
		
		int[] array = new int[MathUtils.ceil(d)];
		int i, j;
		
		if (beginIndex < endIndex)
		{
			for (i=beginIndex,j=0; i<endIndex; i+=gap,j++)
				array[j] = i;
		}
		else
		{
			for (i=beginIndex,j=0; i>endIndex; i+=gap,j++)
				array[j] = i;
		}
	
		return array;
	}

	static public int[] range(int size)
	{
		return range(0, size, 1);
	}
	
	static public void swap(int[] array, int index0, int index1)
	{
		int tmp = array[index0];
		array[index0] = array[index1];
		array[index1] = tmp;
	}
	
	static public <T>void swap(List<T> list, int index0, int index1)
	{
		T tmp = list.get(index0);
		list.set(index0, list.get(index1));
		list.set(index1, tmp);
	}
	
	static public void shuffle(int[] array, Random rand)
	{
		shuffle(array, rand, array.length);
	}
	
	/** Calls {@link #shuffle(List, Random, int)}, where {@code lastIndex = list.size()}. */
	static public <T>void shuffle(List<T> list, Random rand)
	{
		shuffle(list, rand, list.size());
	}
	
	static public void shuffle(int[] array, Random rand, int lastIndex)
	{
		int i, j, size = lastIndex - 1;
		
		for (i=0; i<size; i++)
		{
			j = rand.nextInt(size - i) + i + 1;
			swap(array, i, j);
		}
	}
	
	/**
	 * A slightly modified version of Durstenfeld's shuffle algorithm.
	 * @param lastIndex shuffle up to this index (exclusive, cannot be greater than the list of the list).
	 */
	static public <T>void shuffle(List<T> list, Random rand, int lastIndex)
	{
		int i, j, size = lastIndex - 1;
		
		for (i=0; i<size; i++)
		{
			j = rand.nextInt(size - i) + i + 1;
			swap(list, i, j);
		}
	}
	
	/** Adds all items in the specific array to the specific list. */
	static public void addAll(List<String> list, String[] array)
	{
		for (String item : array)
			list.add(item);
	}
	
	static public int addAll(List<NLPNode> tokens, String[] array,
            int bIndex2)
    {
        for (String item : array)
        {
            NLPNode interval = new NLPNode(bIndex2, bIndex2 + item.length(),
                    item);
            tokens.add(interval);
            bIndex2 = bIndex2 + item.length();
        }
        return bIndex2;
    }
	
	static public <T>void removeLast(List<T> list)
	{
		if (!list.isEmpty()) list.remove(list.size()-1);
	}
	
	static public int max(int[] array)
	{
		int i, size = array.length;
		int m = array[0];
		
		for (i=1; i<size; i++)
			m = Math.max(m, array[i]);
		
		return m;
	}
	
	static public float max(float[] array)
	{
		int i, size = array.length;
		float m = array[0];
		
		for (i=1; i<size; i++)
			m = Math.max(m, array[i]);
		
		return m;
	}
	
	static public double max(double[] array)
	{
		int i, size = array.length;
		double m = array[0];
		
		for (i=1; i<size; i++)
			m = Math.max(m, array[i]);
		
		return m;
	}
	
	static public float min(float[] array)
	{
		int i, size = array.length;
		float m = array[0];
		
		for (i=1; i<size; i++)
			m = Math.min(m, array[i]);
		
		return m;
	}
	
	static public double min(double[] array)
	{
		int i, size = array.length;
		double m = array[0];
		
		for (i=1; i<size; i++)
			m = Math.min(m, array[i]);
		
		return m;
	}
	
	static public int maxIndex(double[] array)
	{
		int i, size = array.length, maxIndex = 0;
		double maxValue = array[maxIndex];
		
		for (i=1; i<size; i++)
		{
			if (maxValue < array[i])
			{
				maxIndex = i;
				maxValue = array[maxIndex];
			}
		}
		
		return maxIndex;
	}
	
	static public int maxIndex(double[] array, int[] indices)
	{
		int i, j, size = indices.length, maxIndex = indices[0];
		double maxValue = array[maxIndex];
		
		for (j=1; j<size; j++)
		{
			i = indices[j];
			
			if (maxValue < array[i])
			{
				maxIndex = i;
				maxValue = array[i];
			}
		}
		
		return maxIndex;
	}
	
	static public <T>List<?>[] createEmptyListArray(int size)
	{
		List<?>[] array = new ArrayList<?>[size];
		
		for (int i=0; i<size; i++)
			array[i] = new ArrayList<T>();
		
		return array;
	}
	
	static public <T>PriorityQueue<?>[] createEmptyPriorityQueueArray(int size, boolean ascending)
	{
		PriorityQueue<?>[] queue = new PriorityQueue<?>[size];
		
		for (int i=0; i<size; i++)
			queue[i] = ascending ? new PriorityQueue<>() : new PriorityQueue<>(Collections.reverseOrder());
		
		return queue;
	}
	
	@SuppressWarnings("unchecked")
	static public <T>List<T> toList(T... items)
	{
		return Arrays.stream(items).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	static public <T>Set<T> toHashSet(T... items)
	{
		return Arrays.stream(items).collect(Collectors.toSet());
	}
	
	static public <T>Set<T> merge(List<Set<T>> sets)
	{
		Set<T> merge = new HashSet<>();
		for (Set<T> set : sets) merge.addAll(set);
		return merge;
	}
	
	static public String[] toArray(Collection<String> col)
	{
		if (col == null) return null;
		String[] array = new String[col.size()];
		col.toArray(array);
		return array;
	}
	
	static public <T>List<T> removeAll(Collection<T> source, Collection<T> remove)
	{
		List<T> list = new ArrayList<>(source);
		list.removeAll(remove);
		return list;
	}
	
	/** @return true if s2 is a subset of s1. */
	static public <T>boolean isSubset(Collection<T> s1, Collection<T> s2)
	{
		for (T t : s2)
		{
			if (!s1.contains(t))
				return false;
		}
		
		return true;
	}

	static public Pair<DoubleIntPair,DoubleIntPair> top2(double[] array)
	{
		int i, size = array.length;
		DoubleIntPair fst, snd;
		
		if (array[0] < array[1])
		{
			fst = toDoubleIntPair(array, 1);
			snd = toDoubleIntPair(array, 0);
		}
		else
		{
			fst = toDoubleIntPair(array, 0);
			snd = toDoubleIntPair(array, 1);			
		}
		
		for (i=2; i<size; i++)
		{
			if (fst.d < array[i])
			{
				snd.set(fst.d, fst.i);
				fst.set(array[i], i);
			}
			else if (snd.d < array[i])
				snd.set(array[i], i);
		}
		
		return new Pair<DoubleIntPair,DoubleIntPair>(fst, snd);
	}
	
	static public Pair<DoubleIntPair,DoubleIntPair> top2(double[] array, int[] include)
	{
		int i, j, size = include.length;
		DoubleIntPair fst, snd;
		
		if (array[include[0]] < array[include[1]])
		{
			fst = toDoubleIntPair(array, include[1]);
			snd = toDoubleIntPair(array, include[0]);
		}
		else
		{
			fst = toDoubleIntPair(array, include[0]);
			snd = toDoubleIntPair(array, include[1]);
		}
		
		for (j=2; j<size; j++)
		{
			i = include[j];
			
			if (fst.d < array[i])
			{
				snd.set(fst.d, fst.i);
				fst.set(array[i], i);
			}
			else if (snd.d < array[i])
				snd.set(array[i], i);
		}
		
		return new Pair<DoubleIntPair,DoubleIntPair>(fst, snd);
	}
	
	static public DoubleIntPair toDoubleIntPair(double[] array, int index)
	{
		return new DoubleIntPair(array[index], index);
	}
	
	static public Set<String> getBagOfWords(String s, Pattern splitter)
	{
		Set<String> set = new HashSet<>();
		
		for (String t : splitter.split(s))
		{
			t = t.trim();
			if (!t.isEmpty()) set.add(t);
		}
		
		return set;
	}
	
	static public Set<String> getBagOfWords(InputStream in, Pattern splitter)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Set<String> set = new HashSet<>();
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null) 
				set.addAll(getBagOfWords(line, splitter));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return set;
	}
	
	static public Set<String> getBagOfWords(String[] document, int ngram, String delim)
	{
		Set<String> set = new HashSet<>();
		int n, i, len = document.length;
		
		for (n=0; n<=ngram; n++)
		{
			for (i=0; i<len-n; i++)
				set.add(Joiner.join(document, delim, i, i+n+1));
		}
		
		return set;
	}
	
	static public float[] toFloatArray(double[] array)
	{
		float[] f = new float[array.length];
		
		for (int i=0; i<array.length; i++)
			f[i] = (float)array[i];
		
		return f;
	}
	
	@SuppressWarnings("unchecked")
	static public <T>Set<T> createSet(T... array)
	{
		Set<T> set = new HashSet<>();
		for (T item : array) set.add(item);
		return set;
	}
	
	static public void normalize01(float[] array)
	{
		float min = min(array);
		float div = max(array) - min;
		
		for (int i=0; i<array.length; i++)
			array[i] = (array[i] - min) / div;
	}
}