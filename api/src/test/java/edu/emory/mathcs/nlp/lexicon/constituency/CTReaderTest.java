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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.lexicon.constituency.CTReader;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTReaderTest
{
	@Test
	public void testCTReader() throws Exception
	{
		String filename = "src/test/resources/constituent/constituent.parse"; 
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
}