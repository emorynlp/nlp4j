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
package edu.emory.mathcs.nlp.common.constituent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.propbank.PBLocation;
import edu.emory.mathcs.nlp.common.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTLibEnTest
{
	@Test
	public void testFixFunctionTags()
	{
		String filename = "src/test/resources/constituent/functionTags.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree tree;
		String[] parses = {
				"(TOP (S (NP-SBJ (NP (CC both) (NNP Bush) (CC and) (NNP Rice))) (VP (VBP have) (VP (VBN delivered) (NP (NP (NNS speeches)) (, ,) (SBAR (WHNP-1 (WDT which)) (S (NP-SBJ (-NONE- *T*-1)) (VP (VBP are) (ADJP-PRD (RB very) (JJ clear))))))))))",
				"(TOP (S (NP-SBJ-1 (NNP Mr.) (NNP Clinton)) (VP (VBD was) (VP (VBN joined) (NP (-NONE- *-1)) (PP-LGS (IN by) (NP (JJ several) (JJ key) (NN republican) (NNS leaders))))) (. .)))",
				"(TOP (SBARQ (WHNP-1 (WP Who)) (SQ (VBZ is) (NP-SBJ (PRP it)) (NP-PRD (-NONE- *T*-1)) (SBAR-CLF (WHNP-2 (WDT that)) (S (NP-SBJ-3 (-NONE- *T*-2)) (NP-TMP (NN today)) (VP (VBZ wants) (S (NP-SBJ (-NONE- *PRO*-3)) (VP (TO to) (VP (VB blow) (NP (NNS things)) (PRT (RP up)) (PP-LOC (IN in) (NP (NNP Lebanon))))))))) (, ,) (NP-VOC (NNP Doctor))) (. ?)))"};
		int i, size = parses.length;
		
		for (i=0; i<size; i++)
		{
			tree = reader.nextTree();
			CTLibEn.fixFunctionTags(tree);
			assertEquals(parses[i], tree.toStringLine());
		}
				
		reader.close();
	}
	
	@Test
	public void testLinks()
	{
		String filename = "src/test/resources/constituent/links.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree tree;
		
		PBLocation[] antes = {
				new PBLocation(3, 1), new PBLocation( 9, 0), new PBLocation(6, 1), new PBLocation(33, 1), new PBLocation(2, 1), new PBLocation(6, 4), new PBLocation(8, 1), new PBLocation(11, 1),
				new PBLocation(9, 1), new PBLocation(22, 1), new PBLocation(6, 1), new PBLocation( 0, 1), new PBLocation(8, 1), new PBLocation(6, 1), new PBLocation(10, 1)};

		PBLocation[] nulls = {
				new PBLocation( 7, 0), new PBLocation(11, 0), new PBLocation(11, 0), new PBLocation(36, 0), new PBLocation( 5, 0), new PBLocation(1, 0), new PBLocation(20, 0), new PBLocation(13, 0), 
				new PBLocation(11, 0), new PBLocation(23, 0), new PBLocation( 8, 0), new PBLocation( 3, 0), new PBLocation(11, 0), new PBLocation(9, 0), new PBLocation(14, 0)};
		int i, size = antes.length;
		
		for (i=0; i<size; i++)
		{
			tree = reader.nextTree();
			CTLibEn.preprocess(tree);
			assertEquals(tree.getNode(antes[i]), tree.getNode(nulls[i]).getAntecedent());	
		}
				
		reader.close();
	}
	
	@Test
	public void testPassiveNullPattern()
	{
		assertTrue(CTLibEn.P_PASSIVE_NULL.matcher("*").find());
		assertTrue(CTLibEn.P_PASSIVE_NULL.matcher("*-12").find());
		assertFalse(CTLibEn.P_PASSIVE_NULL.matcher("*-a").find());
		assertFalse(CTLibEn.P_PASSIVE_NULL.matcher("*T*").find());
		assertFalse(CTLibEn.P_PASSIVE_NULL.matcher("-*").find());
	}
}