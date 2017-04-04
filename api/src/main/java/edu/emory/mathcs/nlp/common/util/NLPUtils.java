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
package edu.emory.mathcs.nlp.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.Tokenizer;
import edu.emory.mathcs.nlp.structure.conversion.C2DConverter;
import edu.emory.mathcs.nlp.structure.conversion.EnglishC2DConverter;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;
import edu.emory.mathcs.nlp.structure.dependency.NLPArc;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	public static final Logger LOG = LoggerFactory.getLogger(NLPUtils.class);
	static public String FEAT_POS_2ND   = "pos2";
	static public String FEAT_PREDICATE = "pred";
	
	/** The feat-key of semantic function tags. */
	static public final String FEAT_SEM	= "sem";
	/** The feat-key of syntactic function tags. */
	static public final String FEAT_SYN	= "syn";
	/** The feat-key of sentence types. */
	static public final String FEAT_SNT	= "snt";
	/** The feat-key of PropBank rolesets. */
	static public final String FEAT_PB	= "pb";
	/** The feat-key of VerbNet classes. */
	static public final String FEAT_VN	= "vn";
	/** The feat-key of word senses. */
	static public final String FEAT_WS	= "ws";
	/** The feat-key of 2nd pos tag. */
	static public final String FEAT_POS2 = "p2";
	/** The feat-key of 2nd ner tag. */
	static public final String FEAT_NER2 = "n2";
	/** The feat-key of sentiments (for root). */
	static public final String FEAT_FUTURE = "fut";
	
	static public C2DConverter getC2DConverter(Language language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishC2DConverter();
		default: new IllegalArgumentException("Invalid language: "+language);
		}
		
		return null;
	}
	
	static public <N extends AbstractNLPNode<N>>String join(N[] nodes, String delim, Function<N,String> f)
	{
		return Joiner.join(nodes, delim, 1, nodes.length, f);
	}

	static public <N>Iterator<N> iterator(N[] nodes)
	{
		Iterator<N> it = new Iterator<N>()
		{
			private int current_index = 1;
			
			@Override
			public boolean hasNext()
			{
				return current_index < nodes.length;
			}
			
			@Override
			public N next()
			{
				return nodes[current_index++];
			}
			
			@Override
			public void remove() {}
		};
		
		return it;
	}



	
	
	
	
	
	
	






	@SuppressWarnings("unchecked")
	static public <N extends AbstractNLPNode<N>,S extends NLPState<N>>NLPComponent<N> getComponent(String pathname)
	{
		try (ObjectInputStream oin = IOUtils.createArtifactObjectInputStream(pathname)) {
			OnlineComponent<N,S> component;
			component = (OnlineComponent<N,S>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			return component;
		} catch (Exception e) {
			NLPUtils.LOG.error("Failed to read component " + pathname, e);
			throw new RuntimeException(e);
		}
	}






	@SuppressWarnings("unchecked")
	static public <N extends AbstractNLPNode<N>,S extends NLPState<N>>NLPComponent<N> getComponent(InputStream in)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		OnlineComponent<N,S> component = null;
		
		try
		{
			component = (OnlineComponent<N,S>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}
	
		return component;
	}






	static public Tokenizer createTokenizer(Language language)
	{
		return new EnglishTokenizer();
	}






	static public String toStringLine(NLPNode[] nodes, String delim, Field field)
	{
		return Joiner.join(nodes, delim, 1, nodes.length, n -> n.get(field));
	}
	
	
	
	
	
	
	
	
//	================================= Dependency Graph =================================
	
	/**
	 * @return the list of semantic arguments in the dependency graph (e.g., list.get(1) = semantic arguments of nodes[1]).
	 * @param nodes dependency graph.
	 */
	static public <N extends AbstractNLPNode<N>>List<List<NLPArc<N>>> getSemanticArgumentList(N[] nodes)
	{
		List<List<NLPArc<N>>> list = new ArrayList<>();
		List<NLPArc<N>> args;
		
		for (int i=0; i<nodes.length; i++)
			list.add(new ArrayList<>());
		
		for (int i=1; i<nodes.length; i++)
		{
			N node = nodes[i];
			
			for (NLPArc<N> arc : node.getSecondaryHeads())
			{
				args = list.get(arc.getNode().getTokenID());
				args.add(new NLPArc<>(node, arc.getLabel()));
			}
		}
		
		return list;
	}
	
	/**
	 * @return {@code true} if the dependency graph contains a cycle.
	 * @param tree dependency graph.
	 */
	static public <N extends AbstractNLPNode<N>>boolean containsCycle(N[] tree)
	{
		for (int i=1; i<tree.length; i++)
		{
			N node = tree[i];
			
			if (node.hasParent() && node.getParent().isDescendantOf(node))
				return true;
		}
		
		return false;
	}
	
	/**
	 * @return the dependency graph whose 1st node is the root, and the rest is filled with the input nodes.
	 * @param sup returns an empty node of type {@code N}.
	 */
	@SuppressWarnings("unchecked")
	static public <N extends AbstractNLPNode<N>>N[] toDependencyTree(List<N> nodes, Supplier<N> sup)
	{
		N[] graph = (N[])Array.newInstance(nodes.get(0).getClass(), nodes.size()+1);
		graph[0] = sup.get().toRoot();
		
		for (int i=1; i<graph.length; i++)
			graph[i] = nodes.get(i-1);
		
		return graph;
	}

    /**
     * Create a component from an object in a file system. Throw exceptions
     * for errors reading the data.
     * @param pathname the object's location.
     * @param <N> The type of NLP node that this component processes.
     * @param <S> State manager type.
     * @return the object.
     */
    @SuppressWarnings("unchecked")
    static public <N extends AbstractNLPNode<N>, S extends NLPState<N>> NLPComponent<N> getComponent(Path pathname) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oin = IOUtils.createArtifactObjectInputStream(pathname)) {
            OnlineComponent<N, S> component;
            component = (OnlineComponent<N, S>) oin.readObject();
            component.setFlag(NLPFlag.DECODE);
            return component;
        }
    }

    /**
     * Create a component from an object found in the classpath. Throw exceptions
     * for errors reading the data.
     * @param classpath the object's location in the classpath.
     * @param <N> The type of NLP node that this component processes.
     * @param <S> State manager type.
     * @return the object.
     */
    @SuppressWarnings("unchecked")
    static public <N extends AbstractNLPNode<N>, S extends NLPState<N>> NLPComponent<N>
    getComponentFromClasspath(String classpath, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oin = new ObjectInputStream(IOUtils.createArtifactInputStreamForClasspath(classpath, classLoader))) {
            OnlineComponent<N, S> component;
            component = (OnlineComponent<N, S>) oin.readObject();
            component.setFlag(NLPFlag.DECODE);
            return component;
        }
    }

    /**
     * Create a component from an object read from a stream. It is the caller's responsibility
     * to arrange for any decompression.
     * @param <N> The type of NLP node that this component processes.
     * @param <S> State manager type.
     * @param inputStream the stream containing the object.
     * @return the object.
     */
    @SuppressWarnings("unchecked")
    static public <N extends AbstractNLPNode<N>, S extends NLPState<N>> NLPComponent<N>
    getComponentFromRawStream(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oin = new ObjectInputStream(inputStream)) {
            OnlineComponent<N, S> component;
            component = (OnlineComponent<N, S>) oin.readObject();
            component.setFlag(NLPFlag.DECODE);
            return component;
        }
    }

}
