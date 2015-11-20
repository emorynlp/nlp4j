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
package edu.emory.mathcs.nlp.decode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDecoder
{
	static final public String FORMAT_RAW = "raw";
	static final public String FORMAT_SEN = "sen";
	static final public String FORMAT_TSV = "tsv";
	
	volatile private NLPComponent[] components;
	volatile private Tokenizer tokenizer;
	private DecodeConfig config;

//	======================================== CONSTRUCTORS ========================================
	
	public NLPDecoder() {}
	
	public NLPDecoder(InputStream configuration)
	{
		init(configuration);
	}
	
	public void init(InputStream configuration)
	{
		NLPComponent[] components = new NLPComponent[2];
		config = new DecodeConfig(configuration);
		setComponents(components);
		
		BinUtils.LOG.info("Loading tokenizer\n");
		setTokenizer(new EnglishTokenizer());
		
		BinUtils.LOG.info("Loading part-of-speech tagger\n");
		components[0] = getComponent(IOUtils.createFileInputStream(config.getPartOfSpeechTagging()));
		
		BinUtils.LOG.info("Loading morphological analyzer\n");
		components[1] = new EnglishMorphAnalyzer();
		
		BinUtils.LOG.info("Finished loading\n");
	}
	
//	======================================== GETTERS/SETTERS ========================================
	
	public Tokenizer getTokenizer()
	{
		return tokenizer;
	}
	
	public NLPComponent[] getComponents()
	{
		return components;
	}
	
	public void setTokenizer(Tokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}
	
	public void setComponents(NLPComponent[] components)
	{
		this.components = components;
	}
	
//	======================================== DECODE ========================================

	public void decode(List<String> inputFiles, String outputExt, String format, int threads)
	{
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		String outputFile;
		
		for (String inputFile : inputFiles)
		{
			outputFile = inputFile + StringConst.PERIOD + outputExt;
			executor.submit(new NLPTask(inputFile, outputFile, format));
		}
		
		executor.shutdown();
	}
	
	public void decode(InputStream in, OutputStream out, String format)
	{
		try
		{
			switch (format)
			{
			case FORMAT_RAW: decodeRaw (in, out);
			case FORMAT_SEN: decodeLine(in, out);
			case FORMAT_TSV: decodeTSV (config.getTSVReader(), in, out);
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public void decodeRaw(InputStream in, OutputStream out) throws IOException
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		NLPNode[] nodes;
		
		for (List<String> tokens : tokenizer.segmentize(in))
		{
			nodes = decode(tokens);
			fout.println(Joiner.join(nodes, "\n", 1)+"\n");
		}
		
		in.close();
		fout.close();
	}
	
	public void decodeLine(InputStream in, OutputStream out) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		NLPNode[] nodes;
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			nodes = decode(line);
			fout.println(Joiner.join(nodes, "\n", 1)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public void decodeTSV(TSVReader reader, InputStream in, OutputStream out) throws IOException
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		NLPNode[] nodes;
		
		reader.open(in);
		
		while ((nodes = reader.next()) != null)
		{
			decode(nodes);
			fout.println(Joiner.join(nodes, "\n", 1)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public NLPNode[] decode(String sentence)
	{
		List<String> tokens = tokenizer.tokenize(sentence);
		return decode(tokens);
	}
	
	public NLPNode[] decode(List<String> tokens)
	{
		NLPNode[] nodes = toNodes(tokens);
		return decode(nodes);
	}
	
	public NLPNode[] decode(NLPNode[] nodes)
	{
		GlobalLexica.assignGlobalLexica(nodes);
		
		for (NLPComponent component : components)
			component.process(nodes);
		
		return nodes;
	}
	
	public NLPNode[] toNodes(List<String> tokens)
	{
		NLPNode[] nodes = new NLPNode[tokens.size()+1];
		nodes[0] = new NLPNode(); nodes[0].setToRoot();
		
		for (int i=1; i<nodes.length; i++)
			nodes[i] = new NLPNode(i, tokens.get(i-1));
		
		return nodes;
	}
	
	public NLPComponent getComponent(InputStream in)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		NLPOnlineComponent<?> component = null;
		
		try
		{
			component = (NLPOnlineComponent<?>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}

		return component;
	}
	
	class NLPTask implements Runnable
	{
		private String input_file;
		private String output_file;
		private String format;
		
		public NLPTask(String inputFile, String outputFile, String format)
		{
			this.input_file  = inputFile;
			this.output_file = outputFile;
			this.format      = format;
		}
		
		@Override
		public void run()
		{
			BinUtils.LOG.info(FileUtils.getBaseName(input_file)+"\n");
			InputStream  in  = IOUtils.createFileInputStream (input_file);
			OutputStream out = IOUtils.createFileOutputStream(output_file);
			decode(in, out, format);
		}
	}
}
