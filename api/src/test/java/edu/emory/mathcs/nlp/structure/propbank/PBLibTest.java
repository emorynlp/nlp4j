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
package edu.emory.mathcs.nlp.structure.propbank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.util.PBLib;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBLibTest
{
	@Test
	public void testGetTreeList()
	{
		InputStream treebank = IOUtils.createFileInputStream("src/test/resources/propbank/sample.parse");
		InputStream propbank = IOUtils.createFileInputStream("src/test/resources/propbank/sample.prop");
		List<CTTree> trees = PBLib.getTreeList(treebank, propbank);
		CTNode pNode, aNode;
		CTTree tree;
		
		tree = trees.get(0);
		pNode = tree.getPredicates().get(2);
		assertEquals(tree.getNode(25,0), pNode);
		assertEquals("show.02", pNode.getFrameID());
		
		aNode = tree.getNode(22, 1);
		assertEquals("25-ARG1", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(23, 1);
		assertEquals("25-ARG1", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(24, 1);
		assertEquals("25-ARG1", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(26, 1);
		assertEquals("25-rel", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(27, 2);
		assertEquals("25-ARGM-TMP", aNode.getSemanticHeads().get(0).toString());
		
		tree = trees.get(1);
		pNode = tree.getPredicates().get(1);
		assertEquals(tree.getNode(21,0), pNode);
		assertEquals("be.01", pNode.getFrameID());
		
		aNode = tree.getNode(19, 1);
		assertEquals("21-ARG1", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(22, 2);
		assertEquals("21-ARG2", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(0, 2);
		assertEquals("18-ARG1", aNode.getSemanticHeads().get(0).toString());
		
		aNode = tree.getNode(0, 2);
		assertEquals("21-ARG1", aNode.getSemanticHeads().get(1).toString());
	}
	
	@Test
	public void testGetNumber()
	{
		assertEquals("0", PBLib.getNumber("A0"));
		assertEquals("A", PBLib.getNumber("AA"));
		assertEquals("0", PBLib.getNumber("C-A0"));
		assertEquals("0", PBLib.getNumber("R-A0"));
		assertEquals("1", PBLib.getNumber("A1-DSP"));
		
		assertEquals("0", PBLib.getNumber("ARG0"));
		assertEquals("A", PBLib.getNumber("ARGA"));
		assertEquals("1", PBLib.getNumber("ARG1-DSP"));
	}
	
	@Test
	public void testGetLinkType()
	{
		assertEquals("SLC", PBLib.getLinkType("LINK-SLC"));
		assertEquals(null , PBLib.getLinkType("ARGM-SLC"));
	}
	
	@Test
	public void testGetModifierType()
	{
		assertEquals("TMP", PBLib.getModifierType("ARGM-TMP"));
		assertEquals(null , PBLib.getModifierType("LINK-TMP"));
	}
	
	@Test
	public void testIsNumberedArgument()
	{
		String label;
		
		label = "ARG0";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "ARGA";
		assertTrue(PBLib.isNumberedArgument(label));

		label = "ARG1-DSP";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "ARG";
		assertFalse(PBLib.isNumberedArgument(label));
		
		label = "ARGM-LOC";
		assertFalse(PBLib.isNumberedArgument(label));

		label = "A0";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "C-A0";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "R-A0";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "AA";
		assertTrue(PBLib.isNumberedArgument(label));

		label = "A1-DSP";
		assertTrue(PBLib.isNumberedArgument(label));
		
		label = "AM-LOC";
		assertFalse(PBLib.isNumberedArgument(label));
	}
	
	@Test
	public void testIsCoreNumberedArgument()
	{
		String label;
		
		label = "ARG0";
		assertTrue(PBLib.isCoreNumberedArgument(label));
		
		label = "ARGA";
		assertTrue(PBLib.isCoreNumberedArgument(label));

		label = "ARG1-DSP";
		assertTrue(PBLib.isCoreNumberedArgument(label));
		
		label = "ARG";
		assertFalse(PBLib.isCoreNumberedArgument(label));
		
		label = "ARGM-LOC";
		assertFalse(PBLib.isCoreNumberedArgument(label));

		label = "A0";
		assertTrue(PBLib.isCoreNumberedArgument(label));
		
		label = "AA";
		assertTrue(PBLib.isCoreNumberedArgument(label));

		label = "A1-DSP";
		assertTrue(PBLib.isCoreNumberedArgument(label));
		
		label = "C-A0";
		assertFalse(PBLib.isCoreNumberedArgument(label));
		
		label = "R-A0";
		assertFalse(PBLib.isCoreNumberedArgument(label));
		
		label = "AM-LOC";
		assertFalse(PBLib.isCoreNumberedArgument(label));
	}
	
	@Test
	public void testIsModifier()
	{
		String label;
		
		label = "ARG0";
		assertFalse(PBLib.isModifier(label));
		
		label = "ARGA";
		assertFalse(PBLib.isModifier(label));

		label = "ARG1-DSP";
		assertFalse(PBLib.isModifier(label));
		
		label = "ARGM-LOC";
		assertTrue(PBLib.isModifier(label));
	}
}