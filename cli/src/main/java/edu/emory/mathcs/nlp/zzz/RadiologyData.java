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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.Tokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RadiologyData
{
	final List<Pattern> TITLES;
	Tokenizer tokenizer;
	
	public RadiologyData()
	{
		final String[] T = {"CT HEAD WITHOUT CONTRAST :", "HEAD CT WITHOUT IV CONTRAST", "NONCONTRAST CT SCAN :", "NONCONTRAST CT SCAN", "NONCONTRAST HEAD CT :", "NONCONTRAST HEAD CT",
							"PATIENT NAME :", "DOB :", "SEX :", "ORDERING PHYSICIAN :", "EXAM :", "CLINICAL INDICATION :", "CLINICAL HISTORY :", "HISTORY :", "PROCEDURE :", "TECHNIQUE :", "COMPARISON :", "FINDINGS :", "IMPRESSION :", "VENTRICLES :", "RECOMMENDATIONS :", "COMMUNICATION :", "BONES / SINUSES :"};
		
		TITLES = Arrays.stream(T).map(s -> Pattern.compile("(?i)"+s)).collect(Collectors.toList());
		tokenizer = new EnglishTokenizer();
	}

	public void tokenize(String inputFile, int threads) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(inputFile), CSVFormat.DEFAULT);
		PrintStream fout = IOUtils.createBufferedPrintStream(inputFile+".tok");
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<String>> list = new ArrayList<>();
		List<CSVRecord> records = parser.getRecords();
		
		for (int i=1; i<records.size(); i++)
			list.add(executor.submit(new TokenizeTask(records.get(i).get(0))));
		
		for (Future<String> f : list)
			fout.println(f.get().trim());
			
		fout.close();
		parser.close();
		executor.shutdown();
	}
	
	public void tokenizeTSV(String inputFile) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(inputFile), CSVFormat.DEFAULT);
		PrintStream fout = IOUtils.createBufferedPrintStream(inputFile+".tsv");
		List<CSVRecord> records = parser.getRecords();
		StringJoiner join;
		CSVRecord record;
		final int K = 5;

		for (int j=1; j<records.size(); j++)
		{
			record = records.get(j);
			join = new StringJoiner("\t");
			join.add(record.get(0));
			join.add(tokenize(record.get(1)));
			for (int i=0; i<K; i++) join.add(record.get(i+2));
			fout.println(join.toString());
		}
		
		parser.close();
		fout.close();
	}
	
	class TokenizeTask implements Callable<String>
	{
		String report;
		
		public TokenizeTask(String report)
		{
			this.report = report;
		}

		@Override
		public String call()
		{
			return tokenize(report);
		}
	}
	
	String tokenize(String report)
	{
		return Joiner.join(tokenizer.tokenize(report), " ");
	}
	
	String segment(String report, String delim)
	{
		StringJoiner join = new StringJoiner(delim);
		
		for (List<Token> tokens : tokenizer.segmentize(report))
		{
			String s = Joiner.join(tokens, " ");
			
			for (Pattern p : TITLES)
			{
				Matcher m = p.matcher(s);
				if (m.find()) s = m.replaceAll("\n"+p.pattern().substring(4)+"\n");
			}
			
			for (String t : Splitter.splitNewLines(s))
			{
				t = t.trim();
				if (!t.isEmpty()) join.add(t);
			}			
		}
		
		return join.toString();
	}
	
	static public void main(String[] args)
	{
		try
		{
			RadiologyData dat = new RadiologyData();
			String inputFile = args[0];
			int threads = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
			
			if (threads > 0)
				dat.tokenize(inputFile, threads);
			else
				dat.tokenizeTSV(inputFile);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
