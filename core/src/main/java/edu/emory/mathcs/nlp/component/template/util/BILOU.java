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
package edu.emory.mathcs.nlp.component.template.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public enum BILOU
{
	B,I,L,U,O;
	
	public static BILOU toBILOU(String tag)
	{
		return valueOf(tag.substring(0,1));
	}

	public static String toBILOUTag(BILOU bilou, String tag)
	{
		return bilou+"-"+tag;
	}

	public static String toTag(String bilouTag)
	{
		return bilouTag.substring(2);
	}

	public static String changeChunkType(BILOU newBILOU, String tag)
	{
		return toBILOUTag(newBILOU, toTag(tag));
	}
	
//	============================== COLLECT ==============================

	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public static <N>Int2ObjectMap<ObjectIntIntTriple<String>> collectNamedEntityMap(N[] nodes, Function<N,String> f, int beginIndex, int endIndex)
	{
		List<ObjectIntIntTriple<String>> list = collectNamedEntityList(nodes, f, beginIndex, endIndex);
		Int2ObjectMap<ObjectIntIntTriple<String>> map = new Int2ObjectOpenHashMap<>();
		int key, size = nodes.length;
		
		for (ObjectIntIntTriple<String> t : list)
		{
			key = t.i1 * size + t.i2;
			map.put(key, t);
		}

		return map;
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public static <N>List<ObjectIntIntTriple<String>> collectNamedEntityList(N[] nodes, Function<N,String> f, int beginIndex, int endIndex)
	{
		List<ObjectIntIntTriple<String>> list = new ArrayList<>();
		int i, beginChunk = -1;
		String tag;
		
		for (i=beginIndex; i<endIndex; i++)
		{
			tag = f.apply(nodes[i]);
			if (tag == null) continue;
			
			switch (toBILOU(tag))
			{
			case U: putNamedEntity(list, tag, i, i); beginChunk = -1; break;
			case B: beginChunk = i; break;
			case L: if (beginIndex <= beginChunk&&beginChunk < i) putNamedEntity(list, tag, beginChunk, i); beginChunk = -1; break;
			case O: beginChunk = -1; break;
			case I: break;
			}
		}
	
		return list;
	}

	private static void putNamedEntity(List<ObjectIntIntTriple<String>> list, String tag, int beginIndex, int endIndex)
	{
		list.add(new ObjectIntIntTriple<>(toTag(tag), beginIndex, endIndex));
	}
}