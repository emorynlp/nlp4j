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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.treebank.CTReader;
import edu.emory.mathcs.nlp.common.treebank.CTTree;
import edu.emory.mathcs.nlp.common.util.CharTokenizer;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CT2CoNLL
{
	public void transform(String inputFile, String outputFile) throws Exception
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(inputFile));
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		CharTokenizer tok = new CharTokenizer('\n');
		List<String[]> ts = new ArrayList<>();
		int[] max = {0,0,0,0};
		int tokenID;
		CTTree tree;
		
		while ((tree = reader.nextTree()) != null)
		{
			tokenID = 0;
			
			for (String line : tok.tokenize(tree.toString()))
			{
				String[] t = transform(line, tokenID++);
				ts.add(t);
			
				for (int i=0; i<t.length; i++)
					max[i] = Math.max(max[i], t[i].length());
			}
			
			ts.add(null);
		}
		
		for (String[] t : ts)
		{
			if (t == null) fout.println();
			else fout.printf(format(max)+"\n", t[0], t[1], t[2], t[3]);
		}
		
		reader.close();
		fout.close();
	}
	
	public String[] transform(String line, int tokenID)
	{
		int beginIndex = line.lastIndexOf('(');
		int endIndex = line.indexOf(')', beginIndex);
		
		String s = line.substring(beginIndex+1, endIndex);
		String[] t = Splitter.splitSpace(s);
		line = line.substring(0, beginIndex)+"*"+line.substring(endIndex+1, line.length());
		line = line.replace(" ", "");
		return new String[]{Integer.toString(tokenID), t[1], t[0], line};
	}
	
	public String format(int[] max)
	{
		StringBuilder build = new StringBuilder();
		for (int m : max) build.append("%"+(m+2)+"s");
		return build.toString();
	}
	
	static public void main(String[] args) throws Exception
	{
		final String inputFile  = args[0];
		final String outputFile = inputFile+".conll";
		
		CT2CoNLL c2n = new CT2CoNLL();
		c2n.transform(inputFile, outputFile);
	}
}
