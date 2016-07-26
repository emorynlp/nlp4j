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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AffixTreeTest
{
	@Test
	public void testPrefix()
	{
		CharAffixTree tree = new CharAffixTree(true);
		
		tree.add("inter");
		tree.add("in");
		tree.add("re");
		
		assertEquals(1, tree.getAffixIndex("interconnect", true));
		assertEquals(1, tree.getAffixIndex("informal", true));
		
		assertEquals(4, tree.getAffixIndex("interconnect", false));
		assertEquals(1, tree.getAffixIndex("informal", false));
		
		assertEquals( 1, tree.getAffixIndex("reimplement", true));
		assertEquals(-1, tree.getAffixIndex("rare", true));
		assertEquals(-1, tree.getAffixIndex("", true));
	}
	
	@Test
	public void testMinimumSuffix()
	{
		CharAffixTree tree = new CharAffixTree(false);
		
		tree.add("iness");
		tree.add("ness");
		tree.add("ful");
		
		assertEquals(5, tree.getAffixIndex("happiness", true));
		assertEquals(7, tree.getAffixIndex("awesomeness", true));

		assertEquals(4, tree.getAffixIndex("happiness", false));
		assertEquals(7, tree.getAffixIndex("awesomeness", false));
		
		assertEquals( 6, tree.getAffixIndex("beautiful", true));
		assertEquals(-1, tree.getAffixIndex("rul", true));
		assertEquals(-1, tree.getAffixIndex("", true));
	}
}
