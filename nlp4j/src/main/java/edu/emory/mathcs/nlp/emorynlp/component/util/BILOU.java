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
package edu.emory.mathcs.nlp.emorynlp.component.util;

import java.util.function.Function;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public enum BILOU
{
	B,
	I,
	L,
	U,
	O;
	
	public static BILOU toBILOU(String tag)
	{
		return BILOU.valueOf(tag.substring(0,1));
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

	public static <N>Int2ObjectMap<String> collectNamedEntityMap(N[] nodes, Function<N,String> f)
	{
		Int2ObjectMap<String> map = new Int2ObjectOpenHashMap<>();
		int i, beginIndex = -1, size = nodes.length;
		String tag;
		
		for (i=0; i<size; i++)
		{
			tag = f.apply(nodes[i]);
			if (tag == null || tag.length() < 3) continue;
			
			switch (toBILOU(tag))
			{
			case U: map.put(getKey(i,i,size), toTag(tag)); beginIndex = -1; break;
			case B: beginIndex = i; break;
			case L: if (0 <= beginIndex&&beginIndex < i) map.put(getKey(beginIndex,i,size), toTag(tag)); beginIndex = -1; break;
			case O: beginIndex = -1; break;
			case I: break;
			}
		}
	
		return map;
	}

	private static int getKey(int beginIndex, int endIndex, int size)
	{
		return beginIndex * size + endIndex;
	}
}