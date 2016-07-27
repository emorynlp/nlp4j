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
package edu.emory.mathcs.nlp.learning.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LabelMapTest
{
	@Test
	public void test()
	{
		LabelMap map = new LabelMap();
		
		assertEquals(0, map.add("A"));
		assertEquals(1, map.add("B"));
		assertEquals(2, map.add("C"));
		assertEquals(0, map.add("A"));
		assertEquals(0, map.add("A"));
		assertEquals(2, map.add("C"));
		
		assertEquals( 0, map.index("A"));
		assertEquals( 1, map.index("B"));
		assertEquals( 2, map.index("C"));
		assertEquals(-1, map.index("D"));
		
		assertEquals(3, map.size());
		
		assertEquals("A", map.getLabel(map.index("A")));
		assertEquals("B", map.getLabel(map.index("B")));
		assertEquals("C", map.getLabel(map.index("C")));
	}
}
