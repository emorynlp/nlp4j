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
package edu.emory.mathcs.nlp.common.collection.list;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.magicwerk.brownies.collections.GapList;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SortedArrayList<T extends Comparable<T>> extends GapList<T> implements Serializable, Iterable<T>
{
	private static final long serialVersionUID = 3219296829240273911L;
	private boolean b_ascending;
	
	public SortedArrayList()
	{
		super();
		setDirection(true);
	}
	
	public SortedArrayList(int initialCapacity)
	{
		super(initialCapacity);
		setDirection(true);
	}
	
	public SortedArrayList(boolean ascending)
	{
		super();
		setDirection(ascending);
	}
	
	public SortedArrayList(int initialCapacity, boolean ascending)
	{
		super(initialCapacity);
		setDirection(ascending);
	}
	
	private void setDirection(boolean ascending)
	{
		b_ascending = ascending;
	}
	
	/**
	 * Adds the specific item to this list in a sorted order.
	 * @return {@code true}.
	 */
	@Override
	public boolean add(T e)
	{
		int index = getInsertIndex(e);
		super.add(index, e);
		return true;
	}
	
	/** @return the index of the added item. */
	public int addItem(T e)
	{
		int index = getInsertIndex(e);
		super.add(index, e);
		return index;
	}
	
	/**
	 * Adds all items in the specific collection to this list in a sorted order.
	 * @return {@code true}.
	 */
	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		for (T t : c) add(t);
		return true;
	}
	
	/**
	 * Removes the first occurrence of the specific item from this list if exists.
	 * @return the index of the item's original position if exists; otherwise, return a negative number.
	 */
	public int remove(T item)
	{
		int index = indexOf(item);
		if (index >= 0) super.remove(index);
		return index;
	}
	
	/** @return {@code true} if the specific item is in the list. */
	public boolean contains(T item)
	{
		return indexOf(item) >= 0;
	}
	
	/** @return the index of the first occurrence of the specific item if exists; otherwise, a negative number. */
	public int indexOf(T item)
	{
		return b_ascending ? Collections.binarySearch(this, item) : Collections.binarySearch(this, item, Collections.reverseOrder());
	}
	
	/** @return the index of the specific item if it is added to this list. */
	public int getInsertIndex(T item)
	{
		int index = indexOf(item);
		return (index < 0) ? -(index+1) : index+1;
	}

	@Override @Deprecated
	public void add(int index, T element)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override @Deprecated
	public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override @Deprecated
	public T set(int index, T element)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #remove(Comparable)} instead. */
	@Override
	public boolean remove(Object o)
	{
		return super.remove(o);
	}
	
	/** @deprecated Use {@link #contains(Comparable)} instead. */
	@Override
	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #indexOf(Comparable)} instead. */
	@Override
	public int indexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated Use {@link #indexOf(Comparable)} instead. */
	@Override
	public int lastIndexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
}