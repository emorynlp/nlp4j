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
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CSVTokenize
{
	final String[] BEFORE = {"Patient Name :", "DOB :", "SEX :", "Ordering Physician :", "Exam :", "HEAD CT", "CLINICAL", "TECHNIQUE :", "COMPARISON :", "FINDINGS :", "IMPRESSION :"};
	final String[] AFTER  = {"INDICATION :", "TECHNIQUE :", "COMPARISON :", "FINDINGS :", "IMPRESSION :"};
	List<Pair<Pattern,String>> P_BEFORE, P_AFTER;
	Pattern NEW_LINE = Pattern.compile("\n");

	public void categorize(String inputFile) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(inputFile), CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		StringJoiner join;
		CSVRecord record;
		
		for (int i=0; i<=500; i++)
		{
			if (i == 0) continue;
			record = records.get(i);
			join = new StringJoiner(" ");
			
			for (int j=2; j<7; j++)
				join.add(record.get(j));
			
			System.out.println(join.toString());
		}
		
		parser.close();
	}
	
	public void tokenize(String inputFile) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(inputFile), CSVFormat.DEFAULT);
		String inputPath = FileUtils.getPath(inputFile)+"/";
		List<CSVRecord> records = parser.getRecords();
		Tokenizer tokenizer = new EnglishTokenizer();
		
		P_BEFORE = new ArrayList<>();
		P_AFTER  = new ArrayList<>();
		for (String s : BEFORE) P_BEFORE.add(new Pair<>(Pattern.compile(s), "\n"+s));
		for (String s : AFTER ) P_AFTER .add(new Pair<>(Pattern.compile(s), s+"\n"));
		
		for (int i=0; i<records.size(); i++)
		{
			PrintStream fout = IOUtils.createBufferedPrintStream(getOuputFilename(inputPath, i));
			
			for (NLPNode[] nodes : tokenizer.segmentize(records.get(i).get(0)))
				print(fout, nodes);
			
			fout.close();
		}
		
		parser.close();
	}
	
	String getOuputFilename(String inputPath, int index)
	{
		StringBuilder build = new StringBuilder();
		
		build.append(inputPath);
		if (index < 1000) build.append(0);
		if (index < 100)  build.append(0);
		if (index < 10)   build.append(0);
		build.append(index);
		build.append(".txt");
		
		return build.toString();
	}
	
	void print(PrintStream fout, NLPNode[] nodes)
	{
		String s = Joiner.join(nodes, " ", 1, nodes.length, NLPNode::getWordForm);
		
		for (Pair<Pattern,String> p : P_BEFORE)
		{
			Matcher m = p.o1.matcher(s);
			if (m.find()) s = m.replaceAll(p.o2);
		}
		
		for (Pair<Pattern,String> p : P_AFTER)
		{
			Matcher m = p.o1.matcher(s);
			if (m.find()) s = m.replaceAll(p.o2);
		}
		
		for (String t : NEW_LINE.split(s))
		{
			t = t.trim();
			if (!t.isEmpty()) fout.println(t.trim());
		}
	}
	
	static public void main(String[] args)
	{
//		String inputFile = "/Users/jdchoi/Emory/radiology/tools/500/500-original.csv";
		String inputFile = "/Users/jdchoi/Emory/radiology/dat/radiology_report_151112_lemmon.csv";
		
		try
		{
			CSVTokenize cvs = new CSVTokenize();
//			cvs.tokenize(inputFile);
			cvs.categorize(inputFile);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
