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

import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.propbank.PBInstance;
import edu.emory.mathcs.nlp.structure.propbank.PBReader;
import edu.emory.mathcs.nlp.structure.util.PBTag;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBReaderTest
{
	@Test
	public void test()
	{
		PBReader reader = new PBReader(IOUtils.createFileInputStream("src/test/resources/propbank/wsj.prop"));
		List<PBInstance> instances = reader.getSortedInstanceList("src/test/resources");
		PBInstance instance;
		PBArgument argument;
		CTTree tree;
		CTNode node;
		
		instance = instances.get(0);
		tree = instance.getTree();
		
		argument = instance.getArgument(0);
		node = tree.getNode(argument.getLocation(0));
		assertEquals(node, tree.getNode(0, 2));
		assertEquals(PBTag.ARG0, argument.getLabel());
		
		argument = instance.getArgument(5);
		node = tree.getNode(argument.getLocation(0));
		assertEquals(node, tree.getNode(15, 1));
		assertEquals(PBTag.ARGM_TMP, argument.getLabel());
		
		instance = instances.get(2);
		tree = instance.getTree();
		
		argument = instance.getArgument(0);
		node = tree.getNode(argument.getLocation(0));
		assertEquals(node, tree.getNode(10, 0));
		assertEquals(PBTag.REL, argument.getLabel());
		
		instance = instances.get(3);
		tree = instance.getTree();
		
		argument = instance.getArgument(0);
		node = tree.getNode(argument.getLocation(1));
		assertEquals(node, tree.getNode(17,1));
		assertEquals(PBTag.ARG1, argument.getLabel());
	}
}