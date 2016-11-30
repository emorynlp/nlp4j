/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.structure.conversion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
import edu.emory.mathcs.nlp.structure.util.PTBTag;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class C2DConverterTest
{
	@Test
	public void test()
	{
		final String filename = "/Users/jdchoi/Documents/Data/english/ontonotes.tb";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		EnglishC2DConverter c2d = new EnglishC2DConverter();
		CTTree tree;
		
		while ((tree = reader.next()) != null)
		{
			PTBLib.preprocess(tree);
			
			for (CTNode node : tree.getTerminals())
			{
				if (PTBLib.isPRO(node) && node.hasAntecedent() && PTBLib.isWhPhrase(node.getAntecedent()) && node.hasCoIndex() && tree.getEmptyCategories(node.getCoIndex()).size() == 1)
				{
					System.out.println(node.getTerminalID());
					System.out.println(tree.toString());
					break;
				}
			}
		}
			
		reader.close();
	}
	
	@Test
	public void testConjunction()
	{
		final String filename = "src/test/resources/conversion/conj.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		EnglishC2DConverter c2d = new EnglishC2DConverter();
		CTTree tree;
		CTNode node;
		
		tree = reader.next();
		node = tree.getTerminal(2);
		assertFalse(PTBLib.isConjunction(node));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessQuantifierPhrase(n));
		assertTrue(PTBLib.isConjunction(node));
		
		tree = reader.next();
		node = tree.getTerminal(1);
		assertFalse(PTBLib.isConjunction(node));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessQuantifierPhrase(n));
		assertTrue(PTBLib.isConjunction(node));
		
		tree = reader.next();
		node = tree.getTerminal(1);
		assertFalse(PTBLib.isConjunction(node));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessQuantifierPhrase(n));
		assertTrue(PTBLib.isConjunction(node));
		
		tree = reader.next();
		node = tree.getTerminal(1);
		assertFalse(PTBLib.isConjunction(node));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessQuantifierPhrase(n));
		assertFalse(PTBLib.isConjunction(node));
		
		reader.close();
	}
	
	@Test
	public void testAdjectiveModals()
	{
		final String filename = "src/test/resources/conversion/modal.parse";
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		EnglishC2DConverter c2d = new EnglishC2DConverter();
		CTTree tree;
		CTNode node;
		
		tree = reader.next();
		node = tree.getNode(3, 2);
		assertFalse(node.isFunctionTag(PTBTag.F_PRD));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessModalAdjective(n));
		assertTrue(node.isFunctionTag(PTBTag.F_PRD));
		
		tree = reader.next();
		node = tree.getNode(4, 2);
		assertFalse(node.isFunctionTag(PTBTag.F_PRD));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessModalAdjective(n));
		assertTrue(node.isFunctionTag(PTBTag.F_PRD));
		
		tree = reader.next();
		node = tree.getNode(3, 2);
		assertFalse(node.isFunctionTag(PTBTag.F_PRD));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessModalAdjective(n));
		assertTrue(node.isFunctionTag(PTBTag.F_PRD));
		
		tree = reader.next();
		node = tree.getNode(3, 2);
		assertFalse(node.isFunctionTag(PTBTag.F_PRD));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessModalAdjective(n));
		assertTrue(node.isFunctionTag(PTBTag.F_PRD));
		
		tree = reader.next();
		node = tree.getNode(4, 2);
		assertFalse(node.isFunctionTag(PTBTag.F_PRD));
		tree.getRoot().flatten().forEach(n -> c2d.preprocessModalAdjective(n));
		assertFalse(tree.getNode(4, 2).isFunctionTag(PTBTag.F_PRD));

		reader.close();
	}
	
	
//	@Test
	public void testModals()
	{
		final String[] filenames = {"ontonotes.tb","web.tb","question.tb","mipacq.tb","sharp.tb","thyme.tb"};
		final String filepath = "/Users/jdchoi/Documents/Data/english/";
		final String phrase = "ADJP";
		final Predicate<CTNode> pos = PTBLib::isAdjective;
//		final String phrase = "ADVP";
//		final Predicate<CTNode> pos = PTBLib::isAdverb;
		
		Object2IntMap<String>map = new Object2IntOpenHashMap<>();
		CTReader reader = new CTReader();
		CTTree tree;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream(filepath+filename));
		
			while ((tree = reader.next()) != null)
				tree.getRoot().flatten().forEach(n -> findPredicate(n, map, phrase, pos));
			
			reader.close();
		}
		
		List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, Entry.comparingByValue(Collections.reverseOrder()));
		
		for (Entry<String,Integer> e : list)
			System.out.println(e.getKey()+"\t"+e.getValue());
	}

	void findPredicate(CTNode node, Object2IntMap<String> map, String phrase, Predicate<CTNode> pos)
	{
		boolean is_vp = node.hasParent(n -> n.isSyntacticTag(PTBTag.C_VP));
		boolean is_sq = node.hasParent(n -> n.isSyntacticTag(PTBTag.C_SQ));
	
		if (node.andSF(phrase, PTBTag.F_PRD) && (is_vp || is_sq))
		{
			CTNode s = node.getFirstChild(n -> n.isSyntacticTag(PTBTag.C_S));
			
			if (s != null)
			{
				CTNode np = s.getFirstChild(PTBLib::isNominalSubject);
				
				if (np != null && np.isEmptyCategoryPhrase())
				{
					CTNode sbj = np.getFirstTerminal().getAntecedent();
					
					if (sbj != null)
					{
						CTNode vp = node.getHighestChainedAncestor(n -> n.isSyntacticTag(PTBTag.C_VP));
						if (is_sq && vp == null) vp = node;
						
						if (sbj == vp.getLeftNearestSibling(PTBLib::isNominalSubject))
						{
							CTNode prd = node.getFirstChild(pos);
							if (prd != null) FastUtils.increment(map, prd.getFormLowercase());
						}
					}
				}
			}
		}
	}
}
