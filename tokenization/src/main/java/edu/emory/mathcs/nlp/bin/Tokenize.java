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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tokenize
{
	static public final String RAW  = "raw";
	static public final String LINE = "line";
	static public final String TSV  = "tsv";
	
	@Option(name="-l", usage="language (default: english)", required=false, metaVar="<language>")
	private String language = Language.ENGLISH.toString();
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<regex>")
	private String input_ext = "*";
	@Option(name="-oe", usage="output file extension (default: tok)", required=false, metaVar="<string>")
	private String output_ext = "tok";
	@Option(name="-input_format", usage="format of the input data (raw|line; default: raw)", required=false, metaVar="<string>")
	private String input_format = RAW;
	@Option(name="-output_format", usage="format of the output data (line|tsv; default: line)", required=false, metaVar="<string>")
	private String output_format = LINE;
	@Option(name="-threads", usage="number of threads (default: 2)", required=false, metaVar="<integer>")
	protected int thread_size = 2;
	
	public Tokenize() {}
	
	public Tokenize(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		Tokenizer tokenizer = Tokenizer.create(Language.getType(language));
		ExecutorService executor = Executors.newFixedThreadPool(thread_size);
		String outputFile;
		
		for (String inputFile : FileUtils.getFileList(input_path, input_ext, false))
		{
			outputFile = inputFile + StringConst.PERIOD + output_ext;
			executor.submit(new NLPTask(tokenizer, inputFile, outputFile));
		}
		
		executor.shutdown();
	}
	
	public void tokenizeRaw(Tokenizer tokenizer, String inputFile, String outputFile) throws IOException
	{
		InputStream in  = IOUtils.createFileInputStream(inputFile);
		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
		
		String tok_delim = output_format.equals(LINE) ? StringConst.SPACE : StringConst.NEW_LINE;
		String sen_delim = output_format.equals(LINE) ? StringConst.EMPTY : StringConst.NEW_LINE;
		
		for (NLPNode[] tokens : tokenizer.segmentize(in))
			out.println(Joiner.join(tokens, tok_delim, 1, tokens.length, n -> n.getValue(Field.word_form))+sen_delim);
		
		in.close();
		out.close();
	}
	
	public void tokenizeLine(Tokenizer tokenizer, String inputFile, String outputFile) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
		String line;
		
		String tok_delim = output_format.equals(LINE) ? StringConst.SPACE : StringConst.NEW_LINE;
		String sen_delim = output_format.equals(LINE) ? StringConst.EMPTY : StringConst.NEW_LINE;
		
		while ((line = reader.readLine()) != null)
		{
			List<NLPNode> tokens = tokenizer.tokenize(line);
			out.println(Joiner.join(tokens, tok_delim, 0, tokens.size(), n -> n.getValue(Field.word_form))+sen_delim);
		}
		
		reader.close();
		out.close();
	}
	
//	public void tokenizeTSV(Tokenizer tokenizer, String inputFile, String outputFile) throws IOException
//	{
//		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
//		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
//		String line;
//		String[] t;
//		
//		while ((line = reader.readLine()) != null)
//		{
//			t = Splitter.splitTabs(line);
//			
//			for (int i=0; i<t.length; i++)
//				t[i] = Joiner.join(tokenizer.tokenize(t[i]), StringConst.SPACE);
//			
//			out.println(Joiner.join(t, StringConst.TAB));
//		}
//		
//		reader.close();
//		out.close();
//	}
	
	class NLPTask implements Runnable
	{
		private Tokenizer tokenizer;
		private String    input_file;
		private String    output_file;
		
		public NLPTask(Tokenizer tokenizer, String inputFile, String outputFile)
		{
			this.tokenizer   = tokenizer;
			this.input_file  = inputFile;
			this.output_file = outputFile;
		}
		
		@Override
		public void run()
		{
			try
			{
				BinUtils.LOG.info(FileUtils.getBaseName(input_file)+"\n");
				
				switch (input_format)
				{
				case RAW : tokenizeRaw (tokenizer, input_file, output_file);
				case LINE: tokenizeLine(tokenizer, input_file, output_file);
//				case TSV : tokenizeTSV (tokenizer, input_file, output_file);
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	static public void main(String[] args)
	{
		new Tokenize(args);
	}
}
