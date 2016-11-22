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
package edu.emory.mathcs.nlp.zzz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiTokenize
{
	Tokenizer tokenizer;
	
	public WikiTokenize()
	{
		tokenizer = new EnglishTokenizer();
	}
	
	public void tokenize(String inputPath, int threads) throws Exception
	{
		List<String> inputFiles = FileUtils.getFileList(inputPath, "*", true);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		
		for (String inputFile : inputFiles)
		{
			System.out.println(inputFile);
			executor.submit(new Task(inputFile));
		}
		
		executor.shutdown();
	}
	
	boolean isMeta(String line)
	{
		return line.startsWith("<doc") || line.startsWith("<section_name") || line.startsWith("</doc");
	}
	
	class Task implements Runnable
	{
		String input_file;
		
		public Task(String inputFile)
		{
			input_file = inputFile;
		}

		@Override
		public void run()
		{
			BufferedReader in = IOUtils.createBufferedReader(input_file);
			PrintStream out = IOUtils.createBufferedPrintStream(input_file+".tok");
			String line;
			
			try
			{
				while ((line = in.readLine()) != null)
				{
					line = line.trim();
					
					if (isMeta(line))
						out.println(line);
					else
						out.println(Joiner.join(tokenizer.tokenize(line), " ", t -> t.getWordForm()));
				}				
				
				in.close();
				out.close();
			}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		String inputPath = args[0];
		int threads = Integer.parseInt(args[1]);
		new WikiTokenize().tokenize(inputPath, threads);
	}
}
