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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import edu.emory.mathcs.nlp.common.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp(String[] args) throws Exception
	{
		InputStream in = new ByteArrayInputStream("SDK\nLFS\nFSFS".getBytes());
		BufferedReader reader = IOUtils.createBufferedReader(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		String line;
		
		while ((line = reader.readLine()) != null)
			fout.println(line);
		
		fout.close();
		System.out.println(new String(out.toByteArray()));
	}

	static public void main(String[] args)
	{
		try {
			new Tmp(args);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
