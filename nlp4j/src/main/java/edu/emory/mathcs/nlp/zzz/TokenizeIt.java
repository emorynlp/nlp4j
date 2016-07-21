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
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Token;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TokenizeIt
{
	NLPDecoder decoder;
	Tokenizer  tokenizer;
	
	public TokenizeIt(String configFile)
	{
		decoder   = new NLPDecoder(IOUtils.createFileInputStream(configFile));
		tokenizer = new EnglishTokenizer();
	}
	
	public void convert(String inputDir, String outputDir)
	{
		for (String inputFile : FileUtils.getFileList(inputDir, "tsv"))
		{
			String outputFile = outputDir+"/"+FileUtils.getBaseName(inputFile);
			System.out.println(FileUtils.getBaseName(inputFile));
			
			try
			{
				convert(IOUtils.createFileInputStream(inputFile), IOUtils.createFileOutputStream(outputFile));
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public void convert(InputStream in, OutputStream out) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		List<ItToken> list = new ArrayList<>();
		NLPNode[] nodes;
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null && !(line = line.trim()).isEmpty())
		{
			t = Splitter.splitTabs(line);
			list.add(new ItToken(t[0], t[1]));
		}
		
		for (List<ItToken> tokens : tokenizer.segmentize(list))
		{
			nodes = decoder.toNodeArray(tokens, token -> create(token));
			decoder.decode(nodes);
			check(nodes);
			fout.println(decoder.toString(nodes)+"\n");
		}
		
		reader.close();
		fout.close();
	}
	
	public void check(NLPNode[] nodes)
	{
		for (int i=1; i<nodes.length; i++)
		{
			NLPNode node = nodes[i];
			
			if (((node.isLemma("it") || node.isLemma("its")) && node.getFeat("it") == null) ||
				(node.getFeat("it") != null && !node.isLemma("it") && !node.isLemma("its")))
			{
				System.out.println(decoder.toString(nodes)+"\n");
				break;
			}
		}
	}
	
	public NLPNode create(ItToken token)
	{
		NLPNode node = decoder.create(token);
		node.setFeatMap(token.feat);
		return node;
	}
	
	class ItToken extends Token
	{
		FeatMap feat;
		
		public ItToken(String form, String feats)
		{
			super(form, 0, 0);
			feat = new FeatMap(feats);
		}
	}
	
	static public void main(String[] args)
	{
		String configFile = "src/main/resources/edu/emory/mathcs/nlp/configuration/config-decode-en.xml";
		String inputDir   = "/Users/jdchoi/Documents/Data/it/output";
		String outputDir  = "/Users/jdchoi/Documents/Data/it/it-dat";
		new TokenizeIt(configFile).convert(inputDir, outputDir);
	}
}
