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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;
import edu.emory.mathcs.nlp.component.template.util.TSVReader;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDecoder
{
	static final public String FORMAT_RAW  = "raw";
	static final public String FORMAT_LINE = "line";
	static final public String FORMAT_TSV  = "tsv";
	
	volatile private NLPComponent[] components;
	volatile private Tokenizer tokenizer;
	private DecodeConfig decode_config;

//	======================================== CONSTRUCTORS ========================================
	
	public NLPDecoder() {}
	
	public NLPDecoder(DecodeConfig config)
	{
		init(config);
	}
	
	public NLPDecoder(InputStream configuration)
	{
		init(new DecodeConfig(configuration));
	}
	
	public void init(DecodeConfig config)
	{
		List<NLPComponent> components = new ArrayList<>();
		Language language = config.getLanguage();
		decode_config = config;
		
		components.add(new GlobalLexica(decode_config.getDocumentElement()));
		
		BinUtils.LOG.info("Loading tokenizer\n");
		setTokenizer(createTokenizer(language));
		
		if (decode_config.getPartOfSpeechTagging() != null)
		{
			BinUtils.LOG.info("Loading part-of-speech tagger\n");
			components.add(getComponent(IOUtils.getInputStream(decode_config.getPartOfSpeechTagging())));
			
			BinUtils.LOG.info("Loading morphological analyzer\n");
			components.add(createMorphologicalAnalyzer(language));
		}
		
		if (decode_config.getNamedEntityRecognition() != null)
		{
			BinUtils.LOG.info("Loading named entity recognizer\n");
			components.add(getComponent(IOUtils.getInputStream(decode_config.getNamedEntityRecognition())));		
		}
		
		if (decode_config.getDependencyParsing() != null)
		{
			BinUtils.LOG.info("Loading dependency parser\n");
			components.add(getComponent(IOUtils.getInputStream(decode_config.getDependencyParsing())));
		}
		
		if (decode_config.getSemanticRoleLabeling() != null)
		{
			BinUtils.LOG.info("Loading semantic role labeler\n");
			components.add(getComponent(IOUtils.getInputStream(decode_config.getSemanticRoleLabeling())));		
		}

		this.components = new NLPComponent[components.size()];
		components.toArray(this.components);
		BinUtils.LOG.info("\n");
	}
	
	static public Tokenizer createTokenizer(Language language)
	{
		return new EnglishTokenizer();
	}
	
	static public NLPComponent createMorphologicalAnalyzer(Language language)
	{
		return new EnglishMorphAnalyzer();
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
	
	public String decode(String s, String format)
	{
		return new String(decodeByteArray(s, format));
	}
	
	public byte[] decodeByteArray(String s, String format)
	{
		InputStream bin = new ByteArrayInputStream(s.getBytes());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		decode(bin, bout, format);
		
		try
		{
			bin .close();
			bout.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return bout.toByteArray();
	}
	
	public void decode(InputStream in, OutputStream out, String format)
	{
		try
		{
			switch (format)
			{
			case FORMAT_RAW : decodeRaw (in, out); break;
			case FORMAT_LINE: decodeLine(in, out); break;
			case FORMAT_TSV : decodeTSV (decode_config.getTSVReader(), in, out); break;
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public List<NLPNode[]> decodeDocument(String s) throws IOException
	{
		return decodeDocument(new ByteArrayInputStream(s.getBytes()));
	}
	
	public List<NLPNode[]> decodeDocument(InputStream in) throws IOException
	{
		List<NLPNode[]> document = new ArrayList<>();
		
		for (NLPNode[] nodes : tokenizer.segmentize(in))
		{
			decode(nodes);
			document.add(nodes);
		}
		
		in.close();
		return document;
	}
	
	public void decodeRaw(String s, OutputStream out) throws IOException
	{
		decodeRaw(new ByteArrayInputStream(s.getBytes()), out);
	}
	
	public void decodeRaw(InputStream in, OutputStream out) throws IOException
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		
		for (NLPNode[] nodes : tokenizer.segmentize(in))
		{
			decode(nodes);
			fout.println(toString(nodes)+"\n");
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
			fout.println(toString(nodes)+"\n");
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
			fout.println(toString(nodes)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public NLPNode[] decode(String sentence)
	{
		List<NLPNode> tokens = tokenizer.tokenize(sentence);
		return decode(NLPUtils.toNodeArray(tokens));
	}
	
	public NLPNode[] decode(NLPNode[] nodes)
	{
		for (NLPComponent component : components)
			component.process(nodes);
		
		return nodes;
	}
	
	static public NLPComponent getComponent(InputStream in)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		OnlineComponent<?> component = null;
		
		try
		{
			component = (OnlineComponent<?>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}

		return component;
	}
	
	public String toString(NLPNode[] nodes)
	{
		return Joiner.join(nodes, "\n", 1);
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
