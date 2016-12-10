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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ExtractPBAliases
{
	public void extract(String input_path)
	{
		Map<String,String> map = new HashMap<>();
		
		for (String filename : FileUtils.getFileList(input_path, "xml"))
		{
			Element doc = XMLUtils.getDocumentElement(IOUtils.createFileInputStream(filename));
			NodeList list = doc.getElementsByTagName("alias");
			String nn = null, vb = null;
			
			for (int i=0; i<list.getLength(); i++)
			{
				Element e = (Element)list.item(i);
				String pos = XMLUtils.getTrimmedAttribute(e, "pos");
				
				if ("n".equals(pos))
					nn = getTextContent(e);
				else if ("v".equals(pos))
					vb = getTextContent(e);
			}
			
			if (nn != null && vb != null)
				map.put(nn, vb);
		}
		
		List<Entry<String,String>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, Entry.comparingByKey());
		
		for (Entry<String,String> e : list)
			System.out.println(e.getKey());
	}
	
	String getTextContent(Element e)
	{
		String s = XMLUtils.getTrimmedTextContent(e);
		return Splitter.splitUnderscore(s)[0];
	}
	
	static public void main(String[] args)
	{
		final String filepath = "/Users/jdchoi/Downloads/propbank-frames-3.1/frames";
		new ExtractPBAliases().extract(filepath);
	}
}
