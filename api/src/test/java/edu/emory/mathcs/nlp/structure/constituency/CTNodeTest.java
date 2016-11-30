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
package edu.emory.mathcs.nlp.structure.constituency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.structure.constituency.CTNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTNodeTest
{
	@Test
	public void testFields()
	{
		CTNode node = new CTNode("NP-PRD-1-LOC=2");
		
		assertEquals("NP-LOC-PRD-1=2", node.getTags());
		assertEquals(Sets.newHashSet("LOC", "PRD"), node.getFunctionTags());
		assertEquals(1, node.getCoIndex());
		assertEquals(2, node.getGapIndex());
		assertEquals(-1, node.getTerminalID());
		assertEquals(-1, node.getTokenID());
		assertEquals(-1, node.getHeight());
		
		node.removeFunctionTag("LOC");
		assertEquals(Sets.newHashSet("PRD"), node.getFunctionTags());
		node.addFunctionTags(Lists.newArrayList("TMP","CLF"));
		assertEquals(Sets.newHashSet("PRD","TMP","CLF"), node.getFunctionTags());
		assertTrue(node.hasFunctionTag());
		assertTrue(node.isFunctionTag("PRD"));
		assertTrue(node.isFunctionTag("PRD","LOC"));
		assertTrue(node.isFunctionTag(Sets.newHashSet("PRD","LOC")));
		assertTrue(node.isFunctionTagAll("PRD","TMP"));
		
		assertFalse(node.isFunctionTag("LOC"));
		assertFalse(node.isFunctionTag("TOC","LOC"));
		assertFalse(node.isFunctionTag(Sets.newHashSet("TOC","LOC")));
		assertFalse(node.isFunctionTagAll("PRD","LOC"));
		
		node.clearFunctionTags();
		assertFalse(node.hasFunctionTag());
		
		node = new CTNode("VB", "take");
		assertEquals("VB", node.getSyntacticTag());
		assertEquals("take", node.getForm());
	}
	
	@Test
	public void testTerminals()
	{
		CTNode np = new CTNode("NP-PRD-1-LOC=2");
		
		CTNode child0 = new CTNode("NP-LOC");
		CTNode child1 = new CTNode("CC", "and");
		CTNode child2 = new CTNode("NP-TMP");
		CTNode child3 = new CTNode("PP-LOC-PRD");
		
		np.addChild(child0);
		np.addChild(child1);
		np.addChild(child2);
		np.addChild(child3);
		
		CTNode gChild0 = new CTNode("-NONE-", "*");
		CTNode gChild1 = new CTNode("NNP", "Jinho");
		CTNode gChild2 = new CTNode("-NONE-", "*ICH*");
		CTNode gChild3 = new CTNode("PP");

		child0.addChild(gChild0);
		child2.addChild(gChild1);
		child2.addChild(gChild2);
		child3.addChild(gChild3);
		
		assertEquals(gChild0, np.getFirstTerminal());
		assertEquals(gChild3, np.getLastTerminal());
		assertEquals(Lists.newArrayList(gChild0, child1, gChild1, gChild2, gChild3), np.getTerminals());
		assertEquals(Lists.newArrayList(child1, gChild1, gChild3), np.getTokens());
		assertEquals(Lists.newArrayList(gChild2), np.getEmptyCategories(Pattern.compile("\\*ICH\\*")));
		assertTrue(gChild0.isTerminal());
		assertTrue(gChild0.isEmptyCategory());
		assertTrue(child0.isEmptyCategoryPhrase());
		
		assertFalse(child2.isTerminal());
		assertFalse(gChild1.isEmptyCategory());
		assertFalse(child2.isEmptyCategoryPhrase());

		assertEquals(2, gChild2.distanceToTop());
	}
	
	@Test
	public void testChanges()
	{
		CTNode[] nodes = new CTNode[5];
		for (int i=0; i<nodes.length; i++) nodes[i] = new CTNode("N"+i);

		nodes[0].addChild(nodes[1]);
		nodes[0].addChild(nodes[2]);
		nodes[1].addChild(nodes[3]);
		nodes[3].addChild(nodes[4]);
		
		nodes[4].removeSelf();
		assertEquals(Lists.newArrayList(nodes[2]), nodes[0].getChildren());
	}
}