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
package edu.emory.mathcs.nlp.component.template.util;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.NLPReader;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVReaderTest
{
//	@Test
	public void test() throws Exception
	{
		final String filename = "src/test/resources/dat/sample-dev.tsv";
		TSVReader<NLPNode> reader = new NLPReader();
		reader.open(IOUtils.createFileInputStream(filename));
		NLPNode[] nodes;
		
		reader.form   = 1;
		reader.lemma  = 2;
		reader.pos    = 3;
		reader.feats  = 4;
		reader.dhead  = 5;
		reader.deprel = 6;
		reader.sheads = 7;
		reader.nament = 8;
		
		nodes = reader.next();
		
		for (int i=1; i<nodes.length; i++)
		{
			System.out.println("----------- "+i+" "+nodes[i].dependent_id+" "+nodes[i].getWordForm()+"\n"+nodes[i].getLeftNearestSibling()+"\n"+nodes[i].getRightNearestSibling());
		}
				
		reader.close();
	}
}
