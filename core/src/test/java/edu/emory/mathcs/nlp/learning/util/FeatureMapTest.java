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
public class FeatureMapTest
{
	@Test
	public void test()
	{
		FeatureMap map = new FeatureMap();
		assertEquals(1, map.size());
		
		assertEquals(1, map.add(0, "A"));
		assertEquals(1, map.add(0, "A"));
		assertEquals(2, map.add(0, "B"));
		assertEquals(3, map.add(0, "C"));
		assertEquals(3, map.add(0, "C"));
		assertEquals(4, map.add(1, "A"));
		assertEquals(5, map.add(1, "B"));
		assertEquals(5, map.add(1, "B"));
		assertEquals(6, map.add(1, "C"));
		assertEquals(6, map.add(1, "C"));
		
		assertEquals(1, map.index(0, "A"));
		assertEquals(2, map.index(0, "B"));
		assertEquals(3, map.index(0, "C"));
		assertEquals(4, map.index(1, "A"));
		assertEquals(5, map.index(1, "B"));
		assertEquals(6, map.index(1, "C"));
		
		assertEquals(7, map.size());
		
		assertEquals(-1, map.index(0, "D"));
		assertEquals(-1, map.index(2, "A"));
	}
}
