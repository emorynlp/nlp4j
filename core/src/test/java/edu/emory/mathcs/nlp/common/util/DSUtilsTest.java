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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DSUtilsTest
{
	@Test
	public void testIncrement()
	{
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		
		FastUtils.increment(map, "A", 1);
		FastUtils.increment(map, "B", 1);
		FastUtils.increment(map, "A", 1);
	}
	
	@Test
	public void testListUtils()
	{
		List<String> list = DSUtils.toList("B","A","E","C","D");
		
		DSUtils.sortReverseOrder(list);
		assertEquals("[E, D, C, B, A]", list.toString());
		
		DSUtils.swap(list, 1, 3);
		assertEquals("[E, B, C, D, A]", list.toString());
		
		Random rand = new Random(5);
		DSUtils.shuffle(list, rand);
		assertEquals("[D, E, B, A, C]", list.toString());
		
		DSUtils.shuffle(list, rand, 3);
		assertEquals("[E, B, D, A, C]", list.toString());
	}
	
	@Test
	public void testHasIntersection()
	{
		Set<String> s1 = DSUtils.toHashSet("A","B");
		Set<String> s2 = DSUtils.toHashSet("C");
		
		assertFalse(DSUtils.hasIntersection(s1, s2));
		
		s2.add("A");
		assertTrue(DSUtils.hasIntersection(s1, s2));
	}
	
	@Test
	public void testRange()
	{
		assertEquals("[0, 1, 2, 3]"	, Arrays.toString(DSUtils.range(0, 4, 1)));
		assertEquals("[0, 2]"		, Arrays.toString(DSUtils.range(0, 4, 2)));
		assertEquals("[0, 3]"		, Arrays.toString(DSUtils.range(0, 4, 3)));
		assertEquals("[0]"			, Arrays.toString(DSUtils.range(0, 4, 4)));
		assertEquals("[0]"			, Arrays.toString(DSUtils.range(0, 4, 5)));
		
		assertEquals("[]", Arrays.toString(DSUtils.range(0,  4, -1)));
		assertEquals("[]", Arrays.toString(DSUtils.range(0, -4,  1)));
		
		assertEquals("[0, -1, -2, -3]"	, Arrays.toString(DSUtils.range(0, -4, -1)));
		assertEquals("[4, 3, 2, 1]"		, Arrays.toString(DSUtils.range(4,  0, -1)));
		assertEquals("[4, 2]"			, Arrays.toString(DSUtils.range(4,  0, -2)));
		assertEquals("[4]"				, Arrays.toString(DSUtils.range(4,  0, -5)));
	}
	
	@Test
	public void testTop()
	{
		double[] array = {3, 1, 2, 0, 4};
		Pair<DoubleIntPair,DoubleIntPair> ps = DSUtils.top2(array);
		
		DoubleIntPair p = ps.o1;
		assertEquals(p.i, 4);
		assertEquals(p.d, 4, 0);
		
		p = ps.o2;
		assertEquals(p.i, 0);
		assertEquals(p.d, 3, 0);
		
		ps = DSUtils.top2(array, new int[]{1,2,4});
		
		p = ps.o1;
		assertEquals(p.i, 4);
		assertEquals(p.d, 4, 0);
		
		p = ps.o2;
		assertEquals(p.i, 2);
		assertEquals(p.d, 2, 0);
	}
	
	@Test
	public void testBagOfWords()
	{
		String[] t = {"A","B","C","D","E","F","G"};
		
		List<String> l = new ArrayList<>(DSUtils.getBagOfWords(t, 0, "_")); Collections.sort(l);
		assertEquals("[A, B, C, D, E, F, G]", l.toString());
		
		l = new ArrayList<>(DSUtils.getBagOfWords(t, 1, "_")); Collections.sort(l);
		assertEquals("[A, A_B, B, B_C, C, C_D, D, D_E, E, E_F, F, F_G, G]", l.toString());
		
		l = new ArrayList<>(DSUtils.getBagOfWords(t, 2, "_")); Collections.sort(l);
		assertEquals("[A, A_B, A_B_C, B, B_C, B_C_D, C, C_D, C_D_E, D, D_E, D_E_F, E, E_F, E_F_G, F, F_G, G]", l.toString());
	}
}