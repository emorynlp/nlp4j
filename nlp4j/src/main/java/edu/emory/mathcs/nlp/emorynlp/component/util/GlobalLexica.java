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
package edu.emory.mathcs.nlp.emorynlp.component.util;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica
{
	static public List<Map<String,Set<String>>> clusters;
	
	static public void init(Element doc)
	{
		Element eGlobal = XMLUtils.getFirstElementByTagName(doc, "global");
		if (eGlobal == null) return;
		
		NodeList nodes = eGlobal.getElementsByTagName("clusters");
		List<String> paths = new ArrayList<>();
		
		for (int i=0; i<nodes.getLength(); i++)
			paths.add(XMLUtils.getTrimmedTextContent((Element)nodes.item(i)));
		
		initClusters(paths);
	}
	
	static public void initClusters(List<String> paths)
	{
		BinUtils.LOG.info("Loading clusters:");
		clusters = paths.stream().map(path -> getClusters(IOUtils.createObjectXZBufferedInputStream(path))).collect(Collectors.toList());
		BinUtils.LOG.info("\n");
	}
	
	@SuppressWarnings("unchecked")
	static public Map<String,Set<String>> getClusters(ObjectInputStream in)
	{
		Map<String,Set<String>> map = null;
		BinUtils.LOG.info(".");
		
		try
		{
			map = (HashMap<String,Set<String>>)in.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return map;
	}
	
	static public String[] getClusterFeatures(String word, int index)
	{
		if (clusters == null || !DSUtils.isRange(clusters, index)) return null;
		Set<String> set = clusters.get(index).get(word);
		if (set == null) return null;
		String[] t = new String[set.size()];
		set.toArray(t);
		return t;
	}
	
	
	
	
	
	
//	static public void initNamedEntityDictionary(String path)
//	{
//		if (path != null && !path.isEmpty())
//			named_entity_dictionary = NLPUtils.getNERDictionary(path);
//	}
//	
//	
//	static public PrefixTree<String,NERInfoSet> getNamedEntityDictionary()
//	{
//		return named_entity_dictionary;
//	}
}