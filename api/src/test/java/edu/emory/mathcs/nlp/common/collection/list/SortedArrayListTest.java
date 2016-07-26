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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SortedArrayListTest
{
	@Test
	public void testAscending()
	{
		SortedArrayList<String> list = new SortedArrayList<String>();
		String s;
		
		s = "C";	assertEquals(0, list.getInsertIndex(s));	list.add(s);
		s = "A";	assertEquals(0, list.getInsertIndex(s));	list.add(s);
		s = "B";	assertEquals(1, list.getInsertIndex(s));	list.add(s);
		s = "E";	assertEquals(3, list.getInsertIndex(s));	list.add(s);
		s = "D";	assertEquals(3, list.getInsertIndex(s));	list.add(s);
		
		assertEquals("[A, B, C, D, E]", list.toString());
		
		assertEquals( 1, list.remove("B"));
		assertEquals( 2, list.remove("D"));
		assertEquals(-3, list.remove("D"));
		
		assertEquals(   1, list.indexOf ("C"));
		assertTrue(list.contains("C"));
		assertEquals( "A", list.get(0));
		assertEquals( "E", list.get(2));
	}
	
	@Test
	public void testDescending()
	{
		SortedArrayList<String> list = new SortedArrayList<String>(false);
		String s;
		
		s = "C";	assertEquals(0, list.getInsertIndex(s));	list.add(s);
		s = "A";	assertEquals(1, list.getInsertIndex(s));	list.add(s);
		s = "B";	assertEquals(1, list.getInsertIndex(s));	list.add(s);
		s = "E";	assertEquals(0, list.getInsertIndex(s));	list.add(s);
		s = "D";	assertEquals(1, list.getInsertIndex(s));	list.add(s);

		assertEquals("[E, D, C, B, A]", list.toString());
		
		assertEquals( 1, list.remove("D"));
		assertEquals( 2, list.remove("B"));
		assertEquals(-3, list.remove("B"));
		
		assertEquals(   1, list.indexOf ("C"));
		assertTrue(list.contains("C"));
		assertEquals( "E", list.get(0));
		assertEquals( "A", list.get(2));
	}
}