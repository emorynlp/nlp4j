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
package edu.emory.mathcs.nlp.component.common.util;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.common.feature.Field;
import edu.emory.mathcs.nlp.component.common.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica
{
	static public Map<String,Set<String>> word_clusters;
	static private Field word_clusters_field;
	
	static public void init(Element doc)
	{
		Element eGlobal = XMLUtils.getFirstElementByTagName(doc, "global");
		if (eGlobal == null) return;
		initWordCluster(eGlobal);
	}
	
	@SuppressWarnings("unchecked")
	static public void initWordCluster(Element eGlobal)
	{
		Element element = XMLUtils.getFirstElementByTagName(eGlobal, "word_clusters");
		if (element == null) return;
		
		String path = XMLUtils.getTrimmedTextContent(element);
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(path);
		BinUtils.LOG.info("Loading word clusters: ");
		
		try
		{
			word_clusters = (HashMap<String,Set<String>>)in.readObject();
			word_clusters_field = Field.valueOf(XMLUtils.getTrimmedAttribute(element, "field"));
		}
		catch (Exception e) {e.printStackTrace();}
		
		BinUtils.LOG.info(word_clusters.size()+"\n");
	}
	
	static public void assignGlobalLexica(NLPNode[] nodes)
	{
		assignWordClusters(nodes);
	}
	
	static public void assignWordClusters(NLPNode[] nodes)
	{
		if (nodes[0].hasWordClusters() || word_clusters == null) return;
		nodes[0].setWordClusters(new String[]{});
		Set<String> set;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			set  = word_clusters.get(node.getValue(word_clusters_field));
			node.setWordClusters(DSUtils.toArray(set));
		}
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