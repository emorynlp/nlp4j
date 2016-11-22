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
package edu.emory.mathcs.nlp.lexicon.constituency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTTreeTest
{
	@Test
	public void test() throws Exception
	{
		String filename = "src/test/resources/constituent/constituent.parse";
		CTReader reader = new CTReader(new BufferedInputStream(new FileInputStream(filename)));
		List<CTTree> trees = reader.readTrees();
		CTTree tree = trees.get(2);
		String s;
		
		reader.close();
		s = "You are some of those who have been chosen to belong to Jesus Christ .";
		assertEquals(s, tree.toForms());
		
		s = "You are some of those who *T* have been chosen * *PRO* to belong to Jesus Christ .";
		assertEquals(s, tree.toForms(" ", true));
		
		assertTrue(tree.getTerminal( 6).isEmptyCategory());
		assertTrue(tree.getTerminal(10).isEmptyCategory());
		assertTrue(tree.getTerminal(11).isEmptyCategory());
		
		assertFalse(tree.getTerminal( 5).isEmptyCategory());
		assertFalse(tree.getTerminal(12).isEmptyCategory());
		
		assertEquals(tree.getTerminal( 7), tree.getToken(6));
		assertEquals(tree.getTerminal(12), tree.getToken(9));
		
		assertEquals(tree.getRoot(), tree.getNode(0, 3));
		assertEquals(tree.getTerminal( 6).getAntecedent(), tree.getNode( 5, 1));
		assertEquals(tree.getTerminal(10).getAntecedent(), tree.getNode( 6, 1));
		assertEquals(tree.getTerminal(11).getAntecedent(), tree.getNode(10, 1));
		
		tree = trees.get(5);
		assertEquals(Lists.newArrayList(tree.getTerminal(4), tree.getTerminal(10)), tree.getEmptyCategories(1));
		assertEquals(tree.getTerminal(11), tree.getTerminal( 4).getAntecedent());
		assertEquals(tree.getTerminal(11), tree.getTerminal(10).getAntecedent());
	}

	@Test
	public void testNormalizeIndices() throws Exception
	{
		String filename = "src/test/resources/constituent/normalize.parse";
		CTReader reader = new CTReader(new BufferedInputStream(new FileInputStream(filename)));
		CTTree tree;
		
		String[] parses = {
				"(TOP (S (PP (IN In) (NP (NN order) (S (NP-SBJ (-NONE- *PRO*)) (VP (TO to) (VP (VB determine) (NP (NP (DT the) (NN sequence)) (PP (IN of) (NP (DT the) (JJ entire) (NN transcript))))))))) (, ,) (S (S (NP-SBJ-1=3 (NP (NN RT) (HYPH -) (NN PCR)) (VP (VBG using) (NP (NP (NP (NNS primers)) (PP-LOC (IN in) (NP (NNS exons) (NML (CD 10) (CC and) (CD 11))))) (VP (VBN paired) (NP (-NONE- *)) (PP (IN with) (NP (NP (DT a) (NN primer)) (PP-LOC (IN in) (NP (NN intron) (CD 12))))))))) (VP (VBD was) (VP=4 (VBN performed) (NP (-NONE- *-1)) (S-MNR (NP-SBJ (-NONE- *PRO*)) (VP (VBG using) (NP (NML (NML (NML (NN BALB) (HYPH /) (NN c)) (NN mouse)) (NN brain)) (JJ total) (NN RNA))))))) (CC and) (S (NP-SBJ-2=3 (DT the) (VBG resulting) (NNS products)) (VP=4 (VBN sequenced) (NP (-NONE- *-2))))) (. .)))",
		        "(TOP (S (NP-SBJ (NN Figure) (CD 1)) (VP (VBZ shows) (NP (NP (DT the) (JJ average) (NN IOP)) (PP (IN of) (NP (NP (NP (DT a) (NN number)) (PP (IN of) (NP (JJ inbred) (NN mouse) (NNS strains)))) (SBAR (WHNP-2 (WDT that)) (S (NP-SBJ-1 (-NONE- *T*-2)) (VP (VBD were) (VP (VBN housed) (NP (-NONE- *-1)) (PP (IN in) (NP (DT the) (JJ same) (JJ environmental) (NNS conditions))))))))))) (. .)))",
		        "(TOP (S (S (NP-SBJ (NP (PRP It)) (SBAR (-NONE- *EXP*-1))) (VP (VBZ is) (VP (VBG becoming) (ADJP-PRD (RB increasingly) (JJ clear)) (SBAR-1 (IN that) (S (NP-SBJ (NP (JJ many) (NNS forms)) (PP (IN of) (NP (NN glaucoma)))) (VP (VBP have) (NP (DT a) (JJ genetic) (NN component))))) (PRN (-LRB- [) (NP (CD 6) (, ,) (CD 7)) (-RRB- ]))))) (, ,) (CC and) (S (NP-SBJ-3 (JJ much) (JJ current) (NN research)) (VP (VBZ is) (VP (VBN focused) (NP (-NONE- *-3)) (PP (IN on) (S-NOM (NP-SBJ (-NONE- *PRO*)) (VP (VBG identifying) (NP (NP (NP (JJ chromosomal) (NNS regions)) (CC and) (NP (NNS genes))) (SBAR (WHNP-2 (WDT that)) (S (NP-SBJ (-NONE- *T*-2)) (VP (VBP contribute) (PP (IN to) (NP (NN glaucoma)))))))))) (PRN (-LRB- [) (NP (NP (CD 8)) (PP (SYM -) (NP (CD 10)))) (-RRB- ]))))) (. .)))"};
		int i, size = parses.length;
		
		for (i=0; i<size; i++)
		{
			tree = reader.next();
			tree.normalizeIndices();
			assertEquals(parses[i], tree.toStringLine());	
		}
		
		reader.close();
	}
}