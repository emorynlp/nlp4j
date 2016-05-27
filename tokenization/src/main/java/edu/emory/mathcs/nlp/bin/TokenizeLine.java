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

import java.io.InputStream;
import java.io.PrintStream;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TokenizeLine
{
	static public void main(String[] args) throws Exception
	{
		final String inputFile  = args[0];
		final String outputFile = args[1];
		final int flag = Integer.parseInt(args[2]);
		
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		InputStream fin  = IOUtils.createFileInputStream(inputFile);
		Tokenizer   tok  = new EnglishTokenizer();
		
		tok.tokenizeLine(fin, fout, " ", flag);
		fin.close(); fout.close();
	}
}
