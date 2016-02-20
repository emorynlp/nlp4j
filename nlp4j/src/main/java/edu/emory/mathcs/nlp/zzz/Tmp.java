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
package edu.emory.mathcs.nlp.zzz;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.util.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp(String[] args) throws Exception
	{
		TSVReader reader = new TSVReader();
		reader.form = 1;
		reader.lemma = 2;
		reader.pos = 3;
		reader.feats = 4;
		reader.dhead = 5;
		reader.deprel = 6;
		reader.sheads = 7;
		reader.nament = 9;
		
		NLPNode[] nodes;
		reader.open(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/experiments/general-en/dev/ontonotes_mz.dev"));
		
		while ((nodes = reader.next()) != null)
			System.out.println(Joiner.join(nodes, "\n", 1)+"\n");
	}

	static public void main(String[] args)
	{
		try
		{
			new Tmp(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
