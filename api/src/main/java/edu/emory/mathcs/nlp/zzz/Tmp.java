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

import java.util.List;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp() throws Exception
	{
//		String[] filenames = {"ontonotes.tb","web.tb","question.tb","mipacq.tb","sharp.tb","thyme.tb","craft.tb"};
		String[] filenames = {"ontonotes.tb","web.tb","bolt.tb","question.tb"};
		CTReader reader = new CTReader();
		List<CTNode> tokens;
		CTTree tree;
		int sc, wc;
		
		int tsc = 0, twc = 0;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/english/"+filename));
			sc = wc = 0;
			
			while ((tree = reader.next()) != null)
			{
				 tokens = tree.getTokens();

				 if (!tokens.isEmpty())
				 {
					 sc++;
					 wc += tokens.size();					 
				 }
			}
			
			reader.close();
			System.out.printf("%15s%10d%10d\n", filename, sc, wc);
			tsc += sc;
			twc += wc;
		}
		
		System.out.printf("%15s%10d%10d\n", "total", tsc, twc);
	}
	
	static public void main(String[] args) throws Exception
	{
		new Tmp();
	}
}

