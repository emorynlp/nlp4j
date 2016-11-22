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

import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import edu.emory.mathcs.nlp.lexicon.dependency.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class JSONProcess
{
	NLPDecoder decoder;
	
	public JSONProcess() {}
	
	public JSONProcess(String configFile)
	{
		decoder = new NLPDecoder(IOUtils.createFileInputStream(configFile));
	}
	
	public void process(String inputPath, String inputExt, int threads) 
	{
		List<String> inputFiles = FileUtils.getFileList(inputPath, inputExt, true);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		
		for (String inputFile : inputFiles)
		{
			System.out.println(inputFile);
			executor.submit(new Task(inputFile, s -> mergeParagraphs(s)));
		}
		
		executor.shutdown();
	}
	
	class Task implements Runnable
	{
		String input_file;
		Consumer<String> processor;
		
		public Task(String inputFile, Consumer<String> processor)
		{
			input_file = inputFile;
			this.processor = processor;
		}

		@Override
		public void run()
		{
			try
			{
				processor.accept(input_file);
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void process(String inputFile)
	{
		JSONParser parser = new JSONParser();
		JSONArray articles, sections, paragraphs, paragraphs_tokenized, paragraphs_lemmatized;
		JSONObject article, section;
		Pair<String,String> tok_lem;
		String paragraph;
		
		try
		{
			Reader reader = new FileReader(inputFile);
			articles = (JSONArray)parser.parse(reader);
			
			for (int i=0; i<articles.size(); i++)
			{
				article = (JSONObject)articles.get(i);
				sections = (JSONArray)article.get("sections");
				
				for (int j=0; j<sections.size(); j++)
				{
					section = (JSONObject)sections.get(j);
					paragraphs = (JSONArray)section.get("paragraphs");
					paragraphs_tokenized  = new JSONArray();
					paragraphs_lemmatized = new JSONArray();
					
					section.put("paragraphs_tokenized" , paragraphs_tokenized);
					section.put("paragraphs_lemmatized", paragraphs_lemmatized);
					
					for (int k=0; k<paragraphs.size(); k++)
					{
						paragraph = (String)paragraphs.get(k);
						tok_lem = getNLP(paragraph);
						paragraphs_tokenized .add(tok_lem.o1);
						paragraphs_lemmatized.add(tok_lem.o2);
					}
				}
			}
			
			reader.close();
			
			String outputFile = inputFile.substring(0, inputFile.length()-4)+"tm.json";
			PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
			out.println(articles.toJSONString());
			out.close();			
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	Pair<String,String> getNLP(String paragraph) throws Exception
	{
		List<NLPNode[]> document = decoder.decodeDocument(paragraph);
		StringJoiner tok = new StringJoiner(" ");
		StringJoiner lem = new StringJoiner(" ");
		NLPNode node;
		
		for (NLPNode[] nodes : document)
		{
			for (int i=1; i<nodes.length; i++)
			{
				node = nodes[i];
				tok.add(node.getForm());
				
				if (node.isLemma("0") || node.isLemma(MetaConst.CARDINAL) || node.isLemma(MetaConst.ORDINAL))
					lem.add(node.getFormLowercase());
				else if (!node.isLemma(MetaConst.HYPERLINK))
					lem.add(node.getLemma());
			}
		}
		
		return new Pair<>(tok.toString(), lem.toString());
	}
	
	public void mergeParagraphs(String inputFile)
	{
		JSONParser parser = new JSONParser();
		JSONArray articles, sections, paragraphs, paragraphs_tokenized, paragraphs_lemmatized;
		JSONObject article, section;
		String paragraph;
		
		try
		{
			Reader reader = new FileReader(inputFile);
			articles = (JSONArray)parser.parse(reader);
			
			for (int i=0; i<articles.size(); i++)
			{
				article = (JSONObject)articles.get(i);
				sections = (JSONArray)article.get("sections");
				
				for (int j=0; j<sections.size(); j++)
				{
					section = (JSONObject)sections.get(j);
					paragraphs = (JSONArray)section.get("paragraphs");
					paragraphs_tokenized  = (JSONArray)section.get("paragraphs_tokenized");
					paragraphs_lemmatized = (JSONArray)section.get("paragraphs_lemmatized");
					
					for (int k=1; k<paragraphs.size(); k++)
					{
						paragraph = (String)paragraphs.get(k);
						
						if (Character.isLowerCase(paragraph.charAt(0)))
						{
							mergeParagraphs(paragraphs, k);
							mergeParagraphs(paragraphs_tokenized , k);
							mergeParagraphs(paragraphs_lemmatized, k);
							k--;
						}
					}
				}
			}
			
			reader.close();
			
			String outputFile = inputFile.substring(0, inputFile.length()-4)+"merged.json";
			PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
			out.println(articles.toJSONString());
			out.close();			
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("unchecked")
	void mergeParagraphs(JSONArray paragraphs, int k)
	{
		paragraphs.set(k-1, paragraphs.get(k-1)+" "+paragraphs.get(k));
		paragraphs.remove(k);
	}
	
	public static void main(String[] args)
	{
		final String configFile = args[0];
		final String inputPath  = args[1];
		final String inputExt   = args[2];
		final int threads = Integer.parseInt(args[3]);
		new JSONProcess().process(inputPath, inputExt, threads);
	}
}
