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
package edu.emory.mathcs.nlp.common.propbank;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.util.IOUtils;


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
		assertEquals("Pierre Vinken , 61 years old ,", node.toForms());
		assertEquals(PBTag.PB_ARG0, argument.getLabel());
		
		argument = instance.getArgument(5);
		node = tree.getNode(argument.getLocation(0));
		assertEquals("Nov. 29", node.toForms());
		assertEquals(PBTag.PB_ARGM_TMP, argument.getLabel());
		
		instance = instances.get(2);
		tree = instance.getTree();
		
		argument = instance.getArgument(0);
		node = tree.getNode(argument.getLocation(0));
		assertEquals("publishing", node.toForms());
		assertEquals(PBTag.PB_REL, argument.getLabel());
		
		instance = instances.get(3);
		tree = instance.getTree();
		
		argument = instance.getArgument(0);
		node = tree.getNode(argument.getLocation(1));
		assertEquals("*-1", node.toWordForms(true,""));
		assertEquals(PBTag.PB_ARG1, argument.getLabel());
	}
}