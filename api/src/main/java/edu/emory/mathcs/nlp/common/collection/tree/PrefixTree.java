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
package edu.emory.mathcs.nlp.common.collection.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;
import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PrefixTree<K extends Comparable<K>,V> implements Serializable
{
	private static final long serialVersionUID = 6471355272521434323L;
	private PrefixNode<K,V> n_root;
	
	public PrefixTree()
	{
		n_root = new PrefixNode<K,V>();
	}
	
	public PrefixNode<K,V> getRoot()
	{
		return n_root;
	}
	
	public void setRoot(PrefixNode<K,V> node)
	{
		n_root = node;
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public <A>PrefixNode<K,V> add(A[] keys, int beginIndex, int endIndex, Function<A,K> f)
	{
		PrefixNode<K,V> next, curr = n_root;
		
		for (int i=beginIndex; i<endIndex; i++)
		{
			next = curr.get(keys[i]);
			
			if (next == null)
			{
				next = new PrefixNode<K,V>();
				curr.put(f.apply(keys[i]), next);
			}
			
			curr = next;
		}
	
		return curr;
	}
	
	public <A>void set(A[] keys, V value, Function<A,K> f)
	{
		add(keys, 0, keys.length, f).setValue(value);
	}
	
	public <A>ObjectIntPair<V> get(A[] keys, int beginIndex, Function<A,K> f)
	{
		ObjectIntPair<V> p = new ObjectIntPair<>();
		PrefixNode<K,V> curr = n_root;
		int i, len = keys.length;
		
		for (i=beginIndex; i<len; i++)
		{
			curr = curr.get(f.apply(keys[i]));
			if (curr == null) break;
			if (curr.hasValue()) p.set(curr.getValue(), i);
		}
		
		return p.o != null ? p : null;
	}
	
	public <A>PrefixNode<K,V> get(A[] keys, int beginIndex, int endIndex, Function<A,K> f)
	{
		PrefixNode<K,V> curr = n_root;
		
		for (int i=beginIndex; i<endIndex; i++)
		{
			curr = curr.get(f.apply(keys[i]));
			if (curr == null) return null;
		}
		
		return curr;
	}
	
	public <A>List<ObjectIntIntTriple<V>> getAll(A[] array, int beginIndex, Function<A,K> f, boolean removeSubset, boolean removeOverlap)
	{
		List<ObjectIntIntTriple<V>> list = new ArrayList<>();
		int i, size = array.length;
		for (i=beginIndex; i<size; i++) getAllAux(array, i, f, list, removeSubset, removeOverlap);
		return list;
	}
	
	private <A>void getAllAux(A[] keys, int beginIndex, Function<A,K> f, List<ObjectIntIntTriple<V>> list, boolean removeSubset, boolean removeOverlap)
	{
		ObjectIntPair<V> v = get(keys, beginIndex, f);
		if (v == null) return;
		ObjectIntIntTriple<V> t = DSUtils.getLast(list);
		if (removeSubset  && t != null && t.i2 >= v.i) return;
		
		if (removeOverlap && t != null && t.i2 >= beginIndex)
		{
			if (t.i2 - t.i1 < v.i - beginIndex)
				DSUtils.removeLast(list);
			else
				return;
		}
		
		list.add(new ObjectIntIntTriple<V>(v.o, beginIndex, v.i));
	}
}
