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
package edu.emory.mathcs.nlp.emorynlp.utils.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVReader<N>
{
	static public String BLANK = StringConst.UNDERSCORE;
	private BufferedReader reader;
	private TSVIndex<N>    index;
	
	public TSVReader(TSVIndex<N> index)
	{
		setIndex(index);
	}
	
	public TSVIndex<N> getIndex()
	{
		return index;
	}
	
	public void setIndex(TSVIndex<N> index)
	{
		this.index = index;
	}
	
	public void open(InputStream in)
	{
		reader = IOUtils.createBufferedReader(in);		
	}
	
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public N[] next() throws IOException
	{
		List<String[]> list = new ArrayList<>();
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			if (line.isEmpty())
			{
				if (list.isEmpty()) continue;
				break;
			}
			
			list.add(Splitter.splitTabs(line));
		}
		
		return list.isEmpty() ? null : index.toNodeList(list);
	}
}
