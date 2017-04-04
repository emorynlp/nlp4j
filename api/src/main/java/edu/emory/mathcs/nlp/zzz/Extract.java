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

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Extract
{
	static public void main(String[] args) throws Exception
	{
		final String INPUT_PATH  = args[0];
		final String OUTPUT_PATH = args[1];
		
		List<String> filenames = FileUtils.getFileList(INPUT_PATH, "txt", true);
		Map<String,List<String>> map = new HashMap<>();
		
		for (String filename : filenames)
		{
			System.out.println(filename);
			BufferedReader reader = IOUtils.createBufferedReader(filename);
			StringJoiner join = new StringJoiner(" ");
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				for (String token : Splitter.splitSpace(line.trim()))
					join.add(token);
			}

			reader.close();
			String label = FileUtils.getBaseName(filename).substring(0, 2);
			map.computeIfAbsent(label, k -> new ArrayList<>()).add(join.toString().trim());
		}
		
		PrintStream ftrn = IOUtils.createBufferedPrintStream(OUTPUT_PATH+".trn");
		PrintStream fdev = IOUtils.createBufferedPrintStream(OUTPUT_PATH+".dev");
		PrintStream ftst = IOUtils.createBufferedPrintStream(OUTPUT_PATH+".tst");
		List<String> labels = new ArrayList<>(map.keySet());
		Collections.sort(labels);
		
		for (String label : labels)
		{
			List<String> docs = map.get(label);
			
			for (int i=0; i<docs.size(); i++)
			{
				String doc = label+"\t"+docs.get(i);
				
				switch (i%10)
				{
				case 0 : ftst.println(doc); break;
				case 1 : fdev.println(doc); break;
				default: ftrn.println(doc); break;
				}
			}
		}
		
		ftrn.close();
		fdev.close();
		ftst.close();
	}
}
