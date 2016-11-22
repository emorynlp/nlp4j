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
package edu.emory.mathcs.nlp.component.template.lexicon;

import edu.emory.mathcs.nlp.common.collection.tree.PrefixTree;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import edu.emory.mathcs.nlp.lexicon.dependency.AbstractNLPNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a holder for data resources shared across multiple components.
 * Each of these items is usually stored as a {@link java.io.Serializable} object
 * written with {@link java.io.ObjectOutputStream}, but an application might choose
 * to make other arrangements. If the caller does not choose to initialize from an XML
 * descriptor, but rather to call the various {@code set} methods, the caller must call
 * {@link #getGlobalLexicon(InputStream, Field, String)} to wrap the object in a
 * {@link GlobalLexicon}.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica<N extends AbstractNLPNode<N>> implements NLPComponent<N>
{
	private static final Logger LOG = LoggerFactory.getLogger(GlobalLexica.class);
	static final public String LEXICA = "lexica";
	static final public String FIELD  = "field";
	static final public String NAME   = "name";
	
//	protected Pair<Map<String,List<String>>,Field>       ambiguity_classes;
//	protected Pair<Map<String,Set<String>>,Field>        word_clusters;	
//	protected Pair<Map<String,float[]>,Field>            word_embeddings;
//	protected Pair<PrefixTree<String,Set<String>>,Field> named_entity_gazetteers;
//	protected Pair<Set<String>,Field>                    stop_words;
	
	protected GlobalLexicon<Map<String,List<String>>>       ambiguity_classes;
	protected GlobalLexicon<Map<String,Set<String>>>        word_clusters;	
	protected GlobalLexicon<Map<String,float[]>>            word_embeddings;
	protected GlobalLexicon<PrefixTree<String,Set<String>>> named_entity_gazetteers;
	protected GlobalLexicon<Set<String>>                    stop_words;
	
//	=================================== CONSTRUCTOR ===================================

	/**
	 * Initialize the lexica with no contents.
	 * Call this if you plan to call the various 'set' methods for yourself.
	 */
	public GlobalLexica() {
		//
	}
	
	/**
	 * Initialize from an XML decoder descriptor read from a stream.
	 * @param in configuration xml. */
	public GlobalLexica(InputStream in)
	{
		this(XMLUtils.getDocumentElement(in));
	}

	/**
	 * Initialize from an XML decoder descriptor represented as an XML DOM.
	 * @param doc the document object of the DOM.
	 */
	public GlobalLexica(Element doc)
	{
		Element eLexica = XMLUtils.getFirstElementByTagName(doc, LEXICA);
		if (eLexica == null) return;
		
		setAmbiguityClasses     (getGlobalLexicon(eLexica, "ambiguity_classes"      , "Loading ambiguity classes"));
		setWordClusters         (getGlobalLexicon(eLexica, "word_clusters"          , "Loading word clusters"));
		setWordEmbeddings       (getGlobalLexicon(eLexica, "word_embeddings"        , "Loading word embeddings"));
		setNamedEntityGazetteers(getGlobalLexicon(eLexica, "named_entity_gazetteers", "Loading named entity gazetteers"));
		setStopWords            (getGlobalLexicon(eLexica, "stop_words"             , "Loading stop words"));
	}
	
	protected <T>GlobalLexicon<T> getGlobalLexicon(Element eLexica, String tag, String message)
	{
		return getGlobalLexicon(XMLUtils.getFirstElementByTagName(eLexica, tag), message);
	}
	
	@SuppressWarnings("unchecked")
	protected <T>GlobalLexicon<T> getGlobalLexicon(Element element, String message)
	{
		if (element == null) return null;
		LOG.info(message);
		
		String path = XMLUtils.getTrimmedTextContent(element);

		Field field = Field.valueOf(XMLUtils.getTrimmedAttribute(element, FIELD));
		String name = XMLUtils.getTrimmedAttribute(element, NAME);
		T lexicon = null;
		
		try(ObjectInputStream oin = IOUtils.createArtifactObjectInputStream(path))
		{
			lexicon = (T)oin.readObject();
		}
		catch (Exception e) {e.printStackTrace();}

		return new GlobalLexicon<>(lexicon, field, name);
	}

	/**
	 * Read a global lexicon resource from a stream in Java serialized object format.
	 * The caller is responsible for decompression if appropriate.
	 * @param objectInput the input data.
	 * @param field the {@link Field} that identifies the resource.
	 * @param name a name used in diagnostics.
	 * @param <T> the underlying type of the item. See {@link GlobalLexica} for these.
	 * @return the lexicon item.
	 */
	@SuppressWarnings("unchecked")
	public static <T> GlobalLexicon<T> getGlobalLexicon(InputStream objectInput, Field field, String name) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(objectInput)) {
			T lexicon = (T) ois.readObject();
			return new GlobalLexicon<T>(lexicon, field, name);
		}
	}
	
//	=================================== GETTERS/SETTERS ===================================
	
	public GlobalLexicon<Map<String,List<String>>> getAmbiguityClasses()
	{
		return ambiguity_classes;
	}
	
	public void setAmbiguityClasses(GlobalLexicon<Map<String,List<String>>> classes)
	{
		ambiguity_classes = classes;
	}
	
	public GlobalLexicon<Map<String,Set<String>>> getWordClusters()
	{
		return word_clusters;
	}
	
	public void setWordClusters(GlobalLexicon<Map<String,Set<String>>> p)
	{
		word_clusters = p;
	}
	
	public GlobalLexicon<Map<String,float[]>> getWordEmbeddings() 
	{
		return word_embeddings;
	}
	
	public void setWordEmbeddings(GlobalLexicon<Map<String,float[]>> embeddings) 
	{
		word_embeddings = embeddings;
	}
	
	public GlobalLexicon<PrefixTree<String,Set<String>>> getNamedEntityGazetteers()
	{
		return named_entity_gazetteers;
	}
	
	public void setNamedEntityGazetteers(GlobalLexicon<PrefixTree<String,Set<String>>> gazetteers)
	{
		named_entity_gazetteers = gazetteers;
	}
	
	public GlobalLexicon<Set<String>> getStopWords()
	{
		return stop_words;
	}
	
	public void setStopWords(GlobalLexicon<Set<String>> stopwords)
	{
		stop_words = stopwords;
	}
	
//	=================================== PROCESS ===================================
	
	@Override
	public void process(List<N[]> document)
	{
		for (N[] nodes : document)
			process(nodes);
	}
	
	@Override
	public void process(N[] nodes)
	{
		processAmbiguityClasses(nodes);
		processWordClusters(nodes);
		processWordEmbeddings(nodes);
		processNamedEntityGazetteers(nodes);
		processStopWords(nodes);
	}
	
	public void processAmbiguityClasses(N[] nodes)
	{
		if (ambiguity_classes == null) return;
		List<String> list;
		N node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			list = ambiguity_classes.getLexicon().get(getKey(node, ambiguity_classes.getField()));
			node.setAmbiguityClasses(list);
		}
	}
	
	public void processWordClusters(N[] nodes)
	{
		if (word_clusters == null) return;
		Set<String> set;
		N node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			set  = word_clusters.getLexicon().get(getKey(node, word_clusters.getField()));
			node.setWordClusters(set);
		}
	}
	
	public void processWordEmbeddings(N[] nodes)
	{
		if (word_embeddings == null) return;
		float[] embedding;
		N node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			embedding = word_embeddings.getLexicon().get(getKey(node, word_embeddings.getField()));
			node.setWordEmbedding(embedding);
		}
	}
	
	public void processNamedEntityGazetteers(N[] nodes)
	{
		if (named_entity_gazetteers == null) return;
		List<ObjectIntIntTriple<Set<String>>> list = named_entity_gazetteers.getLexicon().getAll(nodes, 1, n -> getKey(n, named_entity_gazetteers.getField()), false, false);
		
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
	
	public void processStopWords(N[] nodes)
	{
		if (stop_words == null) return;
		N node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			node.setStopWord(stop_words.getLexicon().contains(getKey(node, stop_words.getField())));
		}
	}
	
	protected String getKey(N node, Field field)
	{
		return node.get(field);
	}
}
