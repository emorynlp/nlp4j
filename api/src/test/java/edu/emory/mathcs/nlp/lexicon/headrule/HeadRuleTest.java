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
package edu.emory.mathcs.nlp.lexicon.headrule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.lexicon.constituency.CTNode;
import edu.emory.mathcs.nlp.lexicon.headrule.HeadRule;
import edu.emory.mathcs.nlp.lexicon.headrule.HeadTagSet;


/** @author Jinho D. Choi ({@code jinho.choi@emory.edu}) */
public class HeadRuleTest
{
	@Test
	public void testHeadRule()
	{
		String tags = "NN.*|NP;VB.*|VP"; 
		HeadRule rule = new HeadRule(HeadRule.DIR_LEFT_TO_RIGHT, tags);
		CTNode  node1 = new CTNode("NNS", null);
		CTNode  node2 = new CTNode("VBN", null);
		
		assertFalse(rule.isRightToLeft());

		HeadTagSet[] headTags = rule.getHeadTags();
		
		HeadTagSet headTag = headTags[0];
		assertTrue(headTag.matches(node1));
		assertFalse(headTag.matches(node2));
		
		headTag = headTags[1];
		assertFalse(headTag.matches(node1));
		assertTrue(headTag.matches(node2));
		
		assertEquals(tags, rule.toString());
	}
}