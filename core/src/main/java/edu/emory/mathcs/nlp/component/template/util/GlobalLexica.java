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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.collection.tree.PrefixTree;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica implements NLPComponent
{
	static final public String LEXICA = "lexica";
	static final public String FIELD  = "field";
	
	protected Pair<Map<String,List<String>>,Field>       ambiguity_classes;
	protected Pair<Map<String,Set<String>>,Field>        word_clusters;	
	protected Pair<Map<String,float[]>,Field>            word_embeddings;
	protected Pair<PrefixTree<String,Set<String>>,Field> named_entity_gazetteers;
	protected Pair<Set<String>,Field>                    stop_words;
	protected List<Pair<Object2FloatMap<String>,Field>>  sentiment_lexica;
	
//	=================================== CONSTRUCTOR ===================================
	
	/** @param in configuration xml. */
	public GlobalLexica(InputStream in)
	{
		this(XMLUtils.getDocumentElement(in));
	}
	
	public GlobalLexica(Element doc)
	{
		Element eLexica = XMLUtils.getFirstElementByTagName(doc, LEXICA);
		
		setAmbiguityClasses     (getLexiconFieldPair(eLexica, "ambiguity_classes"      , "Loading ambiguity classes\n"));
		setWordClusters         (getLexiconFieldPair(eLexica, "word_clusters"          , "Loading word clusters\n"));
		setWordEmbeddings       (getLexiconFieldPair(eLexica, "word_embeddings"        , "Loading word embeddings\n"));
		setNamedEntityGazetteers(getLexiconFieldPair(eLexica, "named_entity_gazetteers", "Loading named entity gazetteers\n"));
		setStopWords            (getLexiconFieldPair(eLexica, "stop_words"             , "Loading stop words\n"));
		setSentimentLexica      (getSentimentLexica (eLexica));
	}
	
	private <T>Pair<T,Field> getLexiconFieldPair(Element eLexica, String tag, String message)
	{
		return getLexiconFieldPair(XMLUtils.getFirstElementByTagName(eLexica, tag), message);
	}
	
	@SuppressWarnings("unchecked")
	private <T>Pair<T,Field> getLexiconFieldPair(Element element, String message)
	{
		if (element == null) return null;
		BinUtils.LOG.info(message);
		
		String path = XMLUtils.getTrimmedTextContent(element);
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(path);
		Field field = Field.valueOf(XMLUtils.getTrimmedAttribute(element, FIELD));
		T lexicon = null;
		
		try
		{
			lexicon = (T)oin.readObject();
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}

		return new Pair<>(lexicon, field);
	}
	
	private List<Pair<Object2FloatMap<String>,Field>> getSentimentLexica(Element eLexica) 
	{
		List<Pair<Object2FloatMap<String>,Field>> lexica = new ArrayList<>();
		NodeList nodes = eLexica.getElementsByTagName("sentiment_lexicon");
		
		for (int i=0; i<nodes.getLength(); i++)
			lexica.add(getLexiconFieldPair((Element)nodes.item(i), "Loading sentiment lexicon: "+i+"\n"));
			
		return lexica.isEmpty() ? null : lexica;
	}
	
//	=================================== GETTERS/SETTERS ===================================
	
	public Pair<Map<String,List<String>>,Field> getAmbiguityClasses()
	{
		return ambiguity_classes;
	}
	
	public void setAmbiguityClasses(Pair<Map<String,List<String>>,Field> classes)
	{
		ambiguity_classes = classes;
	}
	
	public Pair<Map<String,Set<String>>,Field> getWordClusters()
	{
		return word_clusters;
	}
	
	public void setWordClusters(Pair<Map<String,Set<String>>,Field> p)
	{
		word_clusters = p;
	}
	
	public Pair<Map<String,float[]>,Field> getWordEmbeddings() 
	{
		return word_embeddings;
	}
	
	public void setWordEmbeddings(Pair<Map<String,float[]>,Field> embeddings) 
	{
		word_embeddings = embeddings;
	}
	
	public Pair<PrefixTree<String,Set<String>>,Field> getNamedEntityGazetteers()
	{
		return named_entity_gazetteers;
	}
	
	public void setNamedEntityGazetteers(Pair<PrefixTree<String,Set<String>>,Field> gazetteers)
	{
		named_entity_gazetteers = gazetteers;
	}
	
	public Pair<Set<String>,Field> getStopWords()
	{
		return stop_words;
	}
	
	public void setStopWords(Pair<Set<String>,Field> stopwords)
	{
		stop_words = stopwords;
	}
	
	public List<Pair<Object2FloatMap<String>,Field>> getSentimentLexica()
	{
		return sentiment_lexica;
	}
	
	public void setSentimentLexica(List<Pair<Object2FloatMap<String>,Field>> lexica)
	{
		sentiment_lexica = lexica;
	}
	
//	=================================== PROCESS ===================================
	
	@Override
	public void process(List<NLPNode[]> document)
	{
		for (NLPNode[] nodes : document)
			process(nodes);
	}
	
	@Override
	public void process(NLPNode[] nodes)
	{
		processAmbiguityClasses(nodes);
		processWordClusters(nodes);
		processWordEmbeddings(nodes);
		processNamedEntityGazetteers(nodes);
		processStopWords(nodes);
		processSentimentScores(nodes);
	}
	
	public void processAmbiguityClasses(NLPNode[] nodes)
	{
		if (ambiguity_classes == null) return;
		List<String> list;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			list = ambiguity_classes.o1.get(getKey(node, ambiguity_classes.o2));
			node.setAmbiguityClasses(list);
		}
	}
	
	public void processWordClusters(NLPNode[] nodes)
	{
		if (word_clusters == null) return;
		Set<String> set;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			set  = word_clusters.o1.get(getKey(node, word_clusters.o2));
			node.setWordClusters(set);
		}
	}
	
	public void processWordEmbeddings(NLPNode[] nodes)
	{
		if (word_embeddings == null) return;
		float[] embedding;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			embedding = word_embeddings.o1.get(getKey(node, word_embeddings.o2));
			node.setWordEmbedding(embedding);
		}
	}
	
	public void processNamedEntityGazetteers(NLPNode[] nodes)
	{
		if (named_entity_gazetteers == null) return;
		List<ObjectIntIntTriple<Set<String>>> list = named_entity_gazetteers.o1.getAll(nodes, 1, n -> getKey(n, named_entity_gazetteers.o2), false, false);
		
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
	
	public void processStopWords(NLPNode[] nodes)
	{
		if (stop_words == null) return;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			node.setStopWord(stop_words.o1.contains(getKey(node, stop_words.o2)));
		}
	}
	
	public void processSentimentScores(NLPNode[] nodes)
	{
		if (sentiment_lexica == null) return;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			node.setSentimentScores(getSentimentScores(node));
		}
	}
	
	public float[] getSentimentScores(NLPNode node)
	{
		float[] scores = new float[sentiment_lexica.size()];
		Pair<Object2FloatMap<String>,Field> p;
		
		for (int i=0; i<scores.length; i++)
		{
			p = sentiment_lexica.get(i);
			scores[i] = p.o1.getOrDefault(getKey(node, p.o2), 0f);
		}
		
		return scores;
	}
	
	protected String getKey(NLPNode node, Field field)
	{
		return node.getValue(field);
	}
}