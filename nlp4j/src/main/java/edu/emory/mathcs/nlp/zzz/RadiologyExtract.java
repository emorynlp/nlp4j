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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RadiologyExtract
{
	public RadiologyExtract(InputStream in, OutputStream out) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		Tokenizer tokenizer = new EnglishTokenizer();
		List<String> tokens;
		StringBuilder build;
		FeatMap map;
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			build = new StringBuilder();
			t = Splitter.splitTabs(line);
			tokens = tokenizer.tokenize(t[0]);
			
			for (int i=0; i<tokens.size(); i++)
			{
				map = new FeatMap();
				
				if (i == 0)
				{
					for (int j=1; j<t.length; j++)
						map.put("r"+j, t[j]);
				}
				
				build.append(tokens.get(i));
				build.append(StringConst.TAB);
				build.append(map.toString());
				build.append(StringConst.NEW_LINE);
			}
			
			fout.println(build.toString());
		}
		
		reader.close();
		fout.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		new RadiologyExtract(IOUtils.createFileInputStream(args[0]), IOUtils.createFileOutputStream(args[1]));
	}
}
