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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Bigram<T1,T2> extends HashMap<T1,Unigram<T2>>
{
	private static final long serialVersionUID = 4856975632981517711L;
	
	public void add(T1 key1, T2 key2)
	{
		add(key1, key2, 1);
	}
	
	public void add(T1 key1, T2 key2, int inc)
	{
		computeIfAbsent(key1, k -> new Unigram<>()).add(key2, inc);
	}
	
	public ObjectDoublePair<T2> getBest(T1 key1)
	{
		Unigram<T2> map = get(key1);
		return (map != null) ? map.getBest() : null;
	}
	
	public Set<T2> keySet(T1 key1)
	{
		Unigram<T2> map = get(key1);
		return (map != null) ? map.keySet() : null;
	}
	
	public List<ObjectIntPair<T2>> toList(T1 key1, int cutoff)
	{
		Unigram<T2> map = get(key1);
		return (map != null) ? map.toList(cutoff) : null;
	}
	
	public List<ObjectDoublePair<T2>> toList(T1 key1, double threshold)
	{
		Unigram<T2> map = get(key1);
		return (map != null) ? map.toList(threshold) : null;
	}
}
