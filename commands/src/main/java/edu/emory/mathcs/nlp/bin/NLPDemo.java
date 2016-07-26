/**
 * Copyright 2015, Emory University
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
package edu.emory.mathcs.nlp.bin;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.AbstractNLPDecoder;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDemo
{
	static public void main(String[] args) throws Exception
	{
		final String configFile = "src/main/resources/edu/emory/mathcs/nlp/configuration/config-decode-en.xml";
		final String inputFile  = "src/test/resources/dat/nlp4j.txt";
		
		NLPDecoder nlp4j = new NLPDecoder(IOUtils.createFileInputStream(configFile));
		NLPNode[] nodes;

		String sentence = "John bought a car for Mary.";
		nodes = nlp4j.decode(sentence);
		System.out.println(Joiner.join(nodes, "\n", 1)+"\n");
		nlp4j.decode(IOUtils.createFileInputStream(inputFile), System.out, AbstractNLPDecoder.FORMAT_RAW);
	}
}
