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

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.headrule.HeadRuleMap;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultC2DConverterTest
{
//	@Test
	public void test()
	{
		final String headrule_file = "src/test/resources/conversion/default_headrules.txt";
		final String parse_file    = "src/test/resources/conversion/default.parse";
		
		HeadRuleMap map = new HeadRuleMap(IOUtils.createFileInputStream(headrule_file));
		C2DConverter c2d = new DefaultC2DConverter(map);
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parse_file));
		CTTree tree = reader.next();
		reader.close();
		
		NLPGraph graph = c2d.toDependencyGraph(tree);
		System.out.println(graph.toString());
	}
}
