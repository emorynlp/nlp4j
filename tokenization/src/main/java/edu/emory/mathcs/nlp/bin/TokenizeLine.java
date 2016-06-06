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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TokenizeLine
{
	Tokenizer tok;
	
	public TokenizeLine(String inputDir, String outputDir, String inputExt, int flag, int threads)
	{
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		tok = new EnglishTokenizer();
		
		for (String inputFile : FileUtils.getFileList(inputDir, inputExt))
		{
			String outputFile = outputDir+"/"+FileUtils.getBaseName(inputFile);
			executor.submit(new Task(inputFile, outputFile, flag));
		}
		
		executor.shutdown();
	}
	
	class Task implements Runnable
	{
		String input_file;
		String output_file;
		int flag;
		
		public Task(String inputFile, String outputFile, int flag)
		{
			input_file  = inputFile;
			output_file = outputFile;
			this.flag   = flag;
		}

		@Override
		public void run()
		{
			PrintStream fout = IOUtils.createBufferedPrintStream(output_file);
			InputStream fin  = IOUtils.createFileInputStream(input_file);
			System.out.println(FileUtils.getBaseName(output_file));

			tok.tokenizeLine(fin, fout, " ", flag);
			try
			{
				fin.close();
				fout.close();
			}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		final String inputDir  = args[0];
		final String outputDir = args[1];
		final String inputExt  = args[2];
		final int flag    = Integer.parseInt(args[3]);
		final int threads = Integer.parseInt(args[4]);
		
		new TokenizeLine(inputDir, outputDir, inputExt, flag, threads);
	}
}
