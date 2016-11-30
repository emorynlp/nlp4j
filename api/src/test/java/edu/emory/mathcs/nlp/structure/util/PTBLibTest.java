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
package edu.emory.mathcs.nlp.structure.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.IntIntPair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PTBLibTest
{
	@Test
	public void testFixFunctionTags()
	{
		String filename = "src/test/resources/constituency/function_tags.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTNode n0, n1;
		CTTree tree;
		
		// fix SBJ
		tree = reader.next();
		n0 = tree.getNode(0, 2);
		n1 = tree.getNode(0, 1);
		assertEquals("S", n0.getTags());
		assertEquals("NP-SBJ", n1.getTags());
		PTBLib.fixFunctionTags(tree);
		assertEquals("NP-SBJ", n0.getTags());
		assertEquals("NP", n1.getTags());
		
		// fix LGS
		tree = reader.next();
		n0 = tree.getNode(5, 1);
		n1 = tree.getNode(6, 1);
		assertEquals("PP", n0.getTags());
		assertEquals("NP-LGS", n1.getTags());
		PTBLib.fixFunctionTags(tree);
		assertEquals("PP-LGS", n0.getTags());
		assertEquals("NP", n1.getTags());
		
		// fix CLF
		tree = reader.next();
		n0 = tree.getNode(1, 1);
		n1 = tree.getNode(4, 2);
		assertEquals("SQ-CLF", n0.getTags());
		assertEquals("SBAR", n1.getTags());
		PTBLib.fixFunctionTags(tree);
		assertEquals("SQ", n0.getTags());
		assertEquals("SBAR-CLF", n1.getTags());
		
		// fix PRD
		tree = reader.next();
		n0 = tree.getNode(4, 1);
		assertEquals("ADVP-PRD", n0.getTags());
		PTBLib.fixFunctionTags(tree);
		assertEquals("ADVP", n0.getTags());
		
		reader.close();
	}
	
	@Test
	public void testLinkReducedPassiveNulls()
	{
		String filename = "src/test/resources/constituency/reduced_passive_nulls.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTNode node;
		CTTree tree;
		
		tree = reader.next();
		node = tree.getTerminal(5);
		assertFalse(node.hasAntecedent());
		PTBLib.linkReducedPassiveNulls(tree);
		assertFalse(node.hasAntecedent());

		tree = reader.next();
		node = tree.getTerminal(10);
		assertFalse(node.hasAntecedent());
		PTBLib.linkReducedPassiveNulls(tree);
		assertEquals(tree.getNode(2, 1), node.getAntecedent());
		
		tree = reader.next();
		node = tree.getTerminal(12);
		assertFalse(node.hasAntecedent());
		PTBLib.linkReducedPassiveNulls(tree);
		assertEquals(tree.getNode(6, 2), node.getAntecedent());

		tree = reader.next();
		node = tree.getTerminal(7);
		assertFalse(node.hasAntecedent());
		PTBLib.linkReducedPassiveNulls(tree);
		assertEquals(tree.getNode(0, 2), node.getAntecedent());

		tree = reader.next();
		node = tree.getTerminal(1);
		assertFalse(node.hasAntecedent());
		PTBLib.linkReducedPassiveNulls(tree);
		assertEquals(tree.getNode(5, 1), node.getAntecedent());
	}
	
	@Test
	public void testLinks()
	{
		String filename = "src/test/resources/constituency/links.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree tree;
		
		IntIntPair[] antes = {
				new IntIntPair(3, 1), new IntIntPair( 9, 0), new IntIntPair(6, 1), new IntIntPair(33, 1), new IntIntPair(2, 1), new IntIntPair(6, 4), new IntIntPair(8, 1), new IntIntPair(11, 1),
				new IntIntPair(9, 1), new IntIntPair(22, 1), new IntIntPair(6, 1), new IntIntPair( 0, 1), new IntIntPair(8, 1), new IntIntPair(6, 1), new IntIntPair(10, 1)};

		IntIntPair[] nulls = {
				new IntIntPair( 7, 0), new IntIntPair(11, 0), new IntIntPair(11, 0), new IntIntPair(36, 0), new IntIntPair( 5, 0), new IntIntPair(1, 0), new IntIntPair(20, 0), new IntIntPair(13, 0), 
				new IntIntPair(11, 0), new IntIntPair(23, 0), new IntIntPair( 8, 0), new IntIntPair( 3, 0), new IntIntPair(11, 0), new IntIntPair(9, 0), new IntIntPair(14, 0)};
		int i, size = antes.length;
		
		for (i=0; i<size; i++)
		{
			tree = reader.next();
			PTBLib.preprocess(tree);
			CTNode ante = tree.getNode(antes[i].i1, antes[i].i2);
			CTNode ec   = tree.getNode(nulls[i].i1, nulls[i].i2);
			assertEquals(ante, ec.getAntecedent());	
		}
				
		reader.close();
	}
	
	@Test
	public void testApostropheS()
	{
		String filename = "src/test/resources/constituency/apostrophe_s.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree tree;
		
		tree = reader.next();
		assertEquals("be", PTBLib.getLemmaOfApostropheS(tree.getTerminal(1)));
		
		tree = reader.next();
		assertEquals("be", PTBLib.getLemmaOfApostropheS(tree.getTerminal(1)));
		
		tree = reader.next();
		assertEquals("be", PTBLib.getLemmaOfApostropheS(tree.getTerminal(1)));
		
		tree = reader.next();
		assertEquals("be", PTBLib.getLemmaOfApostropheS(tree.getTerminal(1)));
		
		tree = reader.next();
		assertEquals("have", PTBLib.getLemmaOfApostropheS(tree.getTerminal(1)));
		
		reader.close();
	}
}