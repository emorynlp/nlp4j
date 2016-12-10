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
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
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
//		EnglishC2DConverter c2d = new EnglishC2DConverter();
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		Set<String> set = Sets.newHashSet();
		final String tag = "WHPP";
		CTTree tree;
		
		
		
		while ((tree = reader.next()) != null)
		{
			tree.flatten().filter(n -> n.getChildrenSize() > 1 && (n.isSyntacticTag(PTBTag.C_CONJP))).forEach(n -> FastUtils.increment(map, StringUtils.toLowerCase(n.toForms())));
//			process(tree.getRoot(), tag, set, map);
			
//			PTBLib.preprocess(tree);
			
//			if (tree.flatten().filter(C -> (C.isSyntacticTag(PTBTag.C_ADVP) || PTBLib.isAdverb(C)) && C.hasParent(n -> n.isSyntacticTag("PP")) && !C.hasRightSibling() && C.getLeftNearestSibling().isSyntacticTag("IN"))
//			.findAny().isPresent()) System.out.println(tree);
			
//			for (CTNode node : tree.getTerminals())
//			{
//				if (PTBLib.isTrace(node) && node.hasCoIndex() && isMulti(tree, node))
//				{
//					System.out.println(node.getTerminalID());
//					System.out.println(tree.toString());
//					break;
//				}
//			}
		}
			
		reader.close();
		
		List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, Entry.comparingByValue(Collections.reverseOrder()));
		
		for (Entry<String,Integer> e : list)
			System.out.println(e.getKey()+" "+e.getValue());
	}
	
	void process(CTNode node, String tag, Set<String> set, Object2IntMap<String> map)
	{
		if (node.isSyntacticTag(tag))
		{
			if (node.getChildrenSize() > 1 && !node.containsChild(n -> n.isSyntacticTag(set)))
				FastUtils.increment(map, Joiner.join(node.getChildren(), " ", CTNode::getSyntacticTag));
		}
		
		node.getChildren().forEach(n -> process(n, tag, set, map));
	}
	
	boolean isMulti(CTTree tree, CTNode node)
	{
//		return tree.getEmptyCategories(node.getCoIndex()).size() > 1;
		List<CTNode> list = tree.getEmptyCategories(node.getCoIndex());
		return list.stream().filter(n -> !PTBLib.isPRO(n) && !PTBLib.isTrace(n)).findAny().isPresent();
	}
	
//	@Test
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
	
//	@Test
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
		
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		EnglishC2DConverter c2d = new EnglishC2DConverter();
		MorphAnalyzer morph = new EnglishMorphAnalyzer();
		CTReader reader = new CTReader();
		CTTree tree;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream(filepath+filename));
		
			while ((tree = reader.next()) != null)
			{
				c2d.lemmatize(tree);
				tree.getRoot().flatten().forEach(n -> findPredicate(n, map, c2d, morph));
			}
			
			reader.close();
		}
		
		List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, Entry.comparingByValue(Collections.reverseOrder()));
		
		for (Entry<String,Integer> e : list)
			System.out.println(e.getKey()+"\t"+e.getValue());
	}

	void findPredicate(CTNode node, Object2IntMap<String> map, EnglishC2DConverter c2d, MorphAnalyzer morph)
	{
		CTNode modal = c2d.preprocessModalVerb(node);
		if (modal != null) FastUtils.increment(map, morph.setLemma(modal));
	}
}
