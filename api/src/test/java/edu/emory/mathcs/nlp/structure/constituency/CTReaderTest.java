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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.util.PTBLib;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTReaderTest
{
	@Test
	public void test() throws Exception
	{
		String filename = "src/test/resources/constituency/constituent.parse"; 
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree   tree;
		
		StringBuilder build = new StringBuilder();
		List<String>  trees = new ArrayList<>();
		String tmp;
		
		while ((tree = reader.next()) != null)
		{
			tmp = tree.toString();
			trees.add(tmp);
			build.append(tmp);
		}
		
		reader.close();
		
		reader = new CTReader(IOUtils.createByteArrayInputStream(build.toString()));
		int i;
		
		for (i=0; (tree = reader.next()) != null; i++)
			assertEquals(trees.get(i), tree.toString());
		
		reader.close();
	}
	
	@Test
	public void test1() throws Exception
	{
		String filename = "/Users/jdchoi/Documents/Data/english/ontonotes.tb"; 
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		CTTree   tree;
		
		while ((tree = reader.next()) != null)
		{
			PTBLib.linkReducedPassiveNulls(tree);
		}
	}
}