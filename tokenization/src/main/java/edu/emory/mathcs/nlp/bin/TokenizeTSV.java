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

import java.io.BufferedReader;
import java.io.PrintStream;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TokenizeTSV
{
	Tokenizer tokenizer;
	
	public TokenizeTSV()
	{
		tokenizer = new EnglishTokenizer();
	}
	
	public void tokenize(String inputFile, String outputFile) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		String line;
		
		while ((line = reader.readLine()) != null)
			fout.println(tokenize(line));
		
		reader.close();
		fout.close();
	}
	
	public String tokenize(String line)
	{
		String[] t = Splitter.splitTabs(line);
		
		for (int i=0; i<t.length; i++)
			t[i] = Joiner.join(tokenizer.tokenize(t[i]), " ");
		
		return Joiner.join(t, "\t");
	}
	
	static public void main(String[] args) throws Exception
	{
		final String inputFile  = args[0];
		final String outputFile = args[1];
		
		TokenizeTSV tsv = new TokenizeTSV();
		tsv.tokenize(inputFile, outputFile);
	}
}
