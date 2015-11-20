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

import java.util.function.Function;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
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
		Int2ObjectMap<ObjectIntIntTriple<String>> map = new Int2ObjectOpenHashMap<>();
		int i, beginChunk = -1, size = nodes.length;
		String tag;
		
		for (i=beginIndex; i<endIndex; i++)
		{
			tag = f.apply(nodes[i]);
			if (tag == null) continue;
			
			switch (toBILOU(tag))
			{
			case U: putNamedEntity(map, tag, i, i, size); beginChunk = -1; break;
			case B: beginChunk = i; break;
			case L: if (beginIndex <= beginChunk&&beginChunk < i) putNamedEntity(map, tag, beginChunk, i, size); beginChunk = -1; break;
			case O: beginChunk = -1; break;
			case I: break;
			}
		}
	
		return map;
	}

	private static void putNamedEntity(Int2ObjectMap<ObjectIntIntTriple<String>> map, String tag, int beginIndex, int endIndex, int size)
	{
		int key = beginIndex * size + endIndex;
		map.put(key, new ObjectIntIntTriple<>(toTag(tag), beginIndex, endIndex));
	}
	
//	============================== POST-PROCESS ==============================
	
	public static void postProcess(NLPNode[] nodes)
	{
		ObjectIntPair<String> begin = new ObjectIntPair<>();
		NLPNode curr;
		
		for (int i=1; i<nodes.length; i++)
		{
			curr = nodes[i];
			
			switch (toBILOU(curr.getNamedEntityTag()))
			{
			case B: postProcessB(nodes, curr, begin); break;
			case I: postProcessI(nodes, curr, begin); break;
			case L: postProcessL(nodes, curr, begin); break;
			case U: postProcessU(nodes, curr, begin); break;
			case O: postProcessO(nodes, curr, begin); break;
			}
		}
	}
	
	private static void postProcessB(NLPNode[] nodes, NLPNode curr, ObjectIntPair<String> begin)
	{
		if (curr.getID()+1 == nodes.length)		// B$
		{
			curr.setNamedEntityTag(changeChunkType(U, curr.getNamedEntityTag()));
			begin.set(null, -1);
			return;
		}
		else if (begin.o != null)
		{
			if (begin.i+1 == curr.getID())		// BB; TODO: BB -> BI?
				nodes[begin.i].setNamedEntityTag(changeChunkType(U, begin.o));
			else								// B(I)+B
				postProcessL(nodes, nodes[curr.getID()-1], begin);
		}
		
		begin.set(curr.getNamedEntityTag(), curr.getID());
		curr.setNamedEntityTag(O.toString());
	}
	
	private static void postProcessI(NLPNode[] nodes, NLPNode curr, ObjectIntPair<String> begin)
	{
		if (begin.o == null)	// (^|O|U|L)I; TODO: UI -> BI?
			begin.set(curr.getNamedEntityTag(), curr.getID());
		
		curr.setNamedEntityTag(O.toString());
	}
	
	private static void postProcessL(NLPNode[] nodes, NLPNode curr, ObjectIntPair<String> begin)
	{
		if (begin.o == null)	// (^|O|U|L)L; TODO: UL -> BL or LL -> IL?
			curr.setNamedEntityTag(changeChunkType(U, curr.getNamedEntityTag()));
		else
		{
			String tag = toTag(curr.getNamedEntityTag());
			nodes[begin.i].setNamedEntityTag(toBILOUTag(B, tag));
			
			for (int i=begin.i+1; i<curr.getID(); i++)
				nodes[i].setNamedEntityTag(toBILOUTag(I, tag));
			
			nodes[curr.getID()].setNamedEntityTag(toBILOUTag(L, tag));
		}
		
		begin.set(null, -1);
	}
	
	private static void postProcessU(NLPNode[] nodes, NLPNode curr, ObjectIntPair<String> begin)
	{
		if (begin.o != null)	// (B|I)U; TODO:
		{
			
		}
		
		begin.set(null, -1);
	}
	
	private static void postProcessO(NLPNode[] nodes, NLPNode curr, ObjectIntPair<String> begin)
	{
		begin.set(null, -1);
	}
}