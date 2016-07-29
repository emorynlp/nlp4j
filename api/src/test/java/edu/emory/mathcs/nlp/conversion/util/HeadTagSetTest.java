/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.conversion.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.conversion.util.HeadTagSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HeadTagSetTest
{
	@Test
	public void testHeadTagSet()
	{
		String tags = "NN.*|NP|-SBJ|-TPC";
		HeadTagSet set = new HeadTagSet(tags);
		CTNode node;
		
		node = new CTNode("NN", null);
		assertTrue(set.matches(node));
		
		node.setConstituentTag("NNS");
		assertTrue(set.matches(node));
		
		node.setConstituentTag("NP");
		assertTrue(set.matches(node));
		
		node.setConstituentTag("S");
		assertFalse(set.matches(node));
		
		node.addFunctionTag("SBJ");
		assertTrue(set.matches(node));
		
		assertEquals(tags, "NN.*|NP|-SBJ|-TPC");
	}
}