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
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CSVIt
{
	final Pattern IT = Pattern.compile("\\[\\[(?i)(it)_(\\d+)\\]\\]");
	
	public void toTSV(String csvFile) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(csvFile), CSVFormat.DEFAULT);
		PrintStream fout = IOUtils.createBufferedPrintStream(csvFile+".tsv");
		List<CSVRecord> records = parser.getRecords();
		StringJoiner join;
		CSVRecord record;
		int count;
		
		for (int i=0; i<records.size(); i++)
		{
			record = records.get(i);
			join = new StringJoiner("\t");
			
			join.add("Yahoo Answer");
			join.add(getGenre(record.get(1)));
			join.add(getDocumentSize(record.get(2)));
			join.add(record.get(3));
			count = Integer.parseInt(record.get(3));
			join.add(getContent(record.get(4), count));
			join.add(getClases(record, count));
			fout.println(join.toString());
		}
		
		parser.close();
		fout.close();
	}
	
	String getGenre(String s)
	{
		return s.substring(11, s.length()-9);
	}
	
	String getDocumentSize(String s)
	{
		int i = Integer.parseInt(s);
		return "<"+(i+1)*20;
	}
	
	int getCount(String s)
	{
		return Integer.parseInt(s);
	}
	
	String getContent(String s, int count)
	{
		Matcher m = IT.matcher(s);
		
		while (m.find()) count--;
		if (count != 0) System.err.println("Count mismatch");
		
		m = IT.matcher(s);
		return m.replaceAll("[[$1]]").trim();
	}
	
	String getClases(CSVRecord record, int count)
	{
		StringJoiner join = new StringJoiner(",");
		
		for (int i=0; i<count; i++)
		{
			join.add(record.get(i+5));
			
			if (!StringUtils.containsDigitOnly(record.get(i+5)))
				System.out.println("Digit mismatch");
		}
		
		return join.toString();
	}
	
	static public void main(String[] args)
	{
		try
		{
			String csvFile = "/Users/jdchoi/Documents/EmoryNLP/qa-it/qa_it.csv";
			new CSVIt().toTSV(csvFile);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
