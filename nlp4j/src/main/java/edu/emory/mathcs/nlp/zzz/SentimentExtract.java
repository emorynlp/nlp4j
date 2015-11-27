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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentExtract
{
	public SentimentExtract(InputStream in, OutputStream out) throws Exception
	{
		CSVParser parser = new CSVParser(IOUtils.createBufferedReader(in), CSVFormat.DEFAULT);
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		Tokenizer tokenizer = new EnglishTokenizer();
		List<String> tokens;
		StringJoiner join;
		
		for (CSVRecord rec : parser)
		{
			tokens = tokenizer.tokenize(rec.get(5));
			join = new StringJoiner("\n");
			
			join.add(tokens.get(0)+"\tsentiment="+rec.get(0));
			
			for (int i=1; i<tokens.size(); i++)
				join.add(tokens.get(i));
			
			fout.println(join.toString()+"\n");
		}
		
		parser.close();
		fout.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		new SentimentExtract(IOUtils.createFileInputStream(args[0]), IOUtils.createFileOutputStream(args[1]));
	}
}
