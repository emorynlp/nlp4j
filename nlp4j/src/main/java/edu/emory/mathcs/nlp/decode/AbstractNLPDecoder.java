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
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.component.morph.MorphologicalAnalyzer;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.lexicon.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.tokenization.Token;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNLPDecoder<N extends AbstractNLPNode<N>>
{
	static final public String FORMAT_RAW  = "raw";
	static final public String FORMAT_LINE = "line";
	static final public String FORMAT_TSV  = "tsv";
	
	volatile private List<NLPComponent<N>> components;
	volatile private Tokenizer tokenizer;
	private DecodeConfig decode_config;

//	======================================== CONSTRUCTORS ========================================
	
	public AbstractNLPDecoder() {}
	
	public AbstractNLPDecoder(DecodeConfig config)
	{
		init(config);
	}
	
	public AbstractNLPDecoder(InputStream configuration)
	{
		init(new DecodeConfig(configuration));
	}
	
	public void init(DecodeConfig config)
	{
		List<NLPComponent<N>> components = new ArrayList<>();
		Language language = config.getLanguage();
		decode_config = config;
		
		components.add(new GlobalLexica<>(decode_config.getDocumentElement()));
		
		BinUtils.LOG.info("Loading tokenizer\n");
		setTokenizer(NLPUtils.createTokenizer(language));
		
		if (decode_config.getPartOfSpeechTagging() != null)
		{
			BinUtils.LOG.info("Loading part-of-speech tagger\n");
			components.add(NLPUtils.getComponent(IOUtils.getInputStream(decode_config.getPartOfSpeechTagging())));
			
			BinUtils.LOG.info("Loading morphological analyzer\n");
			components.add(new MorphologicalAnalyzer<>(language));
		}
		
		if (decode_config.getNamedEntityRecognition() != null)
		{
			BinUtils.LOG.info("Loading named entity recognizer\n");
			components.add(NLPUtils.getComponent(IOUtils.getInputStream(decode_config.getNamedEntityRecognition())));
		}
		
		if (decode_config.getDependencyParsing() != null)
		{
			BinUtils.LOG.info("Loading dependency parser\n");
			components.add(NLPUtils.getComponent(IOUtils.getInputStream(decode_config.getDependencyParsing())));
		}
		
//		if (decode_config.getSemanticRoleLabeling() != null)
//		{
//			BinUtils.LOG.info("Loading semantic role labeler\n");
//			add(compoinent, , );
//			components.add(NLPUtils.getComponent(IOUtils.getInputStream(decode_config.getSemanticRoleLabeling())));		
//		}

		setComponents(components);
		BinUtils.LOG.info("\n");
	}
	
//	======================================== GETTERS/SETTERS ========================================
	
	public Tokenizer getTokenizer()
	{
		return tokenizer;
	}
	
	public List<NLPComponent<N>> getComponents()
	{
		return components;
	}
	
	public void setTokenizer(Tokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}
	
	public void setComponents(List<NLPComponent<N>> components)
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
			case FORMAT_TSV : decodeTSV (createTSVReader(), in, out); break;
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public List<N[]> decodeDocument(String s) throws IOException
	{
		return decodeDocument(new ByteArrayInputStream(s.getBytes()));
	}
	
	public List<N[]> decodeDocument(InputStream in) throws IOException
	{
		List<N[]> document = new ArrayList<>();
		N[] nodes;
		
		for (List<Token> tokens : tokenizer.segmentize(in))
		{
			nodes = toNodeArray(tokens);
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
		N[] nodes;
		
		for (List<Token> tokens : tokenizer.segmentize(in))
		{
			nodes = toNodeArray(tokens);
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
		N[] nodes;
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			nodes = decode(line);
			fout.println(toString(nodes)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public void decodeTSV(TSVReader<N> reader, InputStream in, OutputStream out) throws IOException
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		N[] nodes;
		
		reader.open(in);
		
		while ((nodes = reader.next()) != null)
		{
			decode(nodes);
			fout.println(toString(nodes)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public N[] decode(String sentence)
	{
		List<Token> tokens = tokenizer.tokenize(sentence);
		return decode(toNodeArray(tokens));
	}
	
	public N[] decode(N[] nodes)
	{
		for (NLPComponent<N> component : components)
			component.process(nodes);
		
		return nodes;
	}
	
	public N[] toNodeArray(List<Token> tokens)
	{
		return toNodeArray(tokens, t -> create(t));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Token>N[] toNodeArray(List<T> tokens, Function<T,N> f)
	{
		N node = create(); node.toRoot();
		N[] nodes = (N[])Array.newInstance(node.getClass(), tokens.size() + 1);
		nodes[0] = node;	// root
		
		for (int i=0,j=1; i<tokens.size(); i++,j++)
		{
			nodes[j] = f.apply(tokens.get(i));
			nodes[j].setID(j);
		}
			
		return nodes;
	}
	
	public abstract N create();
	
	public N create(Token token)
	{
		N node = create();
		node.setWordForm   (token.getWordForm());
		node.setStartOffset(token.getStartOffset());
		node.setEndOffset  (token.getEndOffset());
		return node;
	}
	
	public TSVReader<N> createTSVReader()
	{
		return new TSVReader<N>(decode_config.getReaderFieldMap())
		{
			@Override
			protected N create() {return create();}
		};
	}
	
	public String toString(N[] nodes)
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
