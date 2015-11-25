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
package edu.emory.mathcs.nlp.component.template.util;

import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.tukaani.xz.XZInputStream;
import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.collection.tree.PrefixTree;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica
{
	static public PrefixTree<String,Set<String>> named_entity_gazetteers;
	static public Map<String,Set<String>> word_clusters;
	static public Set<String> stop_words;
	
	static private Field named_entity_gazetteers_field;
	static private Field word_clusters_field;
	static private Field stop_words_field; 
	
	static private boolean initialized = false;
	
//	=================================== INITIALIZATION ===================================
	
	static public void init(Element doc)
	{
		Element eLexica = XMLUtils.getFirstElementByTagName(doc, "lexica");
		if (initialized || eLexica == null) return;
		initialized = true;
		
		initLexica(eLexica, "word_clusters", GlobalLexica::initWordClusters);
		initLexica(eLexica, "named_entity_gazetteers", GlobalLexica::initNamedEntityGazetteers);
		initLexica(eLexica, "stop_words", GlobalLexica::initStopWords);
	}
	
	static private void initLexica(Element eLexica, String tag, BiConsumer<XZInputStream,Field> f)
	{
		Element element = XMLUtils.getFirstElementByTagName(eLexica, tag);
		if (element == null) return;
		
		String path = XMLUtils.getTrimmedTextContent(element);
		XZInputStream in = IOUtils.createXZBufferedInputStream(IOUtils.getInputStream(path));
		Field field = Field.valueOf(XMLUtils.getTrimmedAttribute(element, "field"));
		f.accept(in, field);
	}
	
	@SuppressWarnings("unchecked")
	static public void initWordClusters(XZInputStream in, Field field)
	{
		BinUtils.LOG.info("Loading word clusters: ");
		
		try
		{
			ObjectInputStream oin = new ObjectInputStream(in);
			word_clusters = (Map<String,Set<String>>)oin.readObject();
			word_clusters_field = field;
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		BinUtils.LOG.info(word_clusters.size()+"\n");
	}
	
	@SuppressWarnings("unchecked")
	static public void initNamedEntityGazetteers(XZInputStream in, Field field)
	{
		BinUtils.LOG.info("Loading named entity gazetteers\n");
		
		try
		{
			ObjectInputStream oin = new ObjectInputStream(in);
			named_entity_gazetteers = (PrefixTree<String,Set<String>>)oin.readObject();
			named_entity_gazetteers_field = field;
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("unchecked")
	static public void initStopWords(XZInputStream in, Field field)
	{
		BinUtils.LOG.info("Loading stop words: ");
		
		try
		{
			ObjectInputStream oin = new ObjectInputStream(in);
			stop_words = (Set<String>)oin.readObject();
			stop_words_field = field;
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		BinUtils.LOG.info(stop_words.size()+"\n");
	}
	
//	=================================== ASSIGNMENTS ===================================
	
	static public void assignGlobalLexica(NLPNode[] nodes)
	{
		if (nodes[0].hasWordClusters()) return;
		nodes[0].setWordClusters(new HashSet<>());
		
		assignWordClusters(nodes);
		assignNamedEntityGazetteers(nodes);
	}
	
	static public void assignWordClusters(NLPNode[] nodes)
	{
		if (word_clusters == null) return;
		Set<String> set;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			set  = word_clusters.get(getKey(node, word_clusters_field));
			node.setWordClusters(set);
		}
	}
	
	static public void assignNamedEntityGazetteers(NLPNode[] nodes)
	{
		if (named_entity_gazetteers == null) return;
		List<ObjectIntIntTriple<Set<String>>> list = named_entity_gazetteers.getAll(nodes, 1, n -> getKey(n, named_entity_gazetteers_field), false, false);
		
		for (ObjectIntIntTriple<Set<String>> t : list)
		{
			for (String tag : t.o) 
			{
				if (t.i1 == t.i2)
					nodes[t.i1].addNamedEntityGazetteer(BILOU.toBILOUTag(BILOU.U, tag));
				else
				{
					nodes[t.i1].addNamedEntityGazetteer(BILOU.toBILOUTag(BILOU.B, tag));
					nodes[t.i2].addNamedEntityGazetteer(BILOU.toBILOUTag(BILOU.L, tag));
					
					for (int j=t.i1+1; j<t.i2; j++)
						nodes[j].addNamedEntityGazetteer(BILOU.toBILOUTag(BILOU.I, tag));
				}	
			}
		}
	}
	
	static public String getNonStopWord(NLPNode node)
	{
		if (stop_words != null)
		{
			String key = node.getValue(stop_words_field);
			return stop_words.contains(key) ? null : key;
		}
		
		return null;
	}
	
	static private String getKey(NLPNode node, Field field)
	{
		return node.getValue(field);
	}
}