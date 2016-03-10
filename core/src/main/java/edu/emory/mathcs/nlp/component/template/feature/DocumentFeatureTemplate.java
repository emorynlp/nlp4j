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
package edu.emory.mathcs.nlp.component.template.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectFloatPair;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.DocumentState;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DocumentFeatureTemplate<S extends DocumentState> extends FeatureTemplate<S>
{
	private static final long serialVersionUID = 8581842859392646419L;
	private List<Field> feature_list_type;
	
	public DocumentFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}
	
	@Override
	protected void initFeatureItems(Element element)
	{
		FeatureItem[] items = createFeatureItems(element);
		if (feature_list_type == null) feature_list_type = new ArrayList<>();
		
		if (XMLUtils.getBooleanAttribute(element, "set"))
			addSet(items[0]);
		else if (items.length > 0 && items[0].field == Field.word_embedding)
			addWordEmbedding(items[0]);
		else
		{
			add(items);
			feature_list_type.add(Field.valueOf(XMLUtils.getTrimmedAttribute(element, "t")));
		}
	}
	
	@Override
	public SparseVector createSparseVector(S state, boolean isTrain)
	{
		Collection<ObjectFloatPair<String>> t;
		SparseVector x = new SparseVector();
		int i, type = 0;
		
		for (i=0; i<feature_list.size(); i++,type++)
		{
			t = getWeightedFeatures(state, feature_list.get(i), feature_list_type.get(i));
			if (t != null) for (ObjectFloatPair<String> s : t) add(x, type, s.o, s.f, isTrain);
		}
		
		return x;
	}
	
	protected Collection<ObjectFloatPair<String>> getWeightedFeatures(S state, FeatureItem[] items, Field type)
	{
		Object2FloatMap<String> map = getBagOfLexicons(state, items, type);
		return (map == null || map.isEmpty()) ? null : getBagOfLexicons(map, type);
	}
	
	protected Object2FloatMap<String> getBagOfLexicons(S state, FeatureItem[] items, Field type)
	{
		switch (type)
		{
		case bag_of_words:
		case bag_of_words_norm:
		case bag_of_words_count:
			return getBagOfWords(state, items, false);
		case bag_of_words_stopwords:
		case bag_of_words_stopwords_norm:
		case bag_of_words_stopwords_count:
			return getBagOfWords(state, items, true);
		case bag_of_clusters:
		case bag_of_clusters_norm:
		case bag_of_clusters_count:
			return getBagOfClusters(state, false);
		case bag_of_clusters_stopwords:
		case bag_of_clusters_stopwords_norm:
		case bag_of_clusters_stopwords_count:
			return getBagOfClusters(state, true);
		default: return null;
		}
	}
	
	protected Collection<ObjectFloatPair<String>> getBagOfLexicons(Object2FloatMap<String> map, Field type)
	{
		switch (type)
		{
		case bag_of_words:
		case bag_of_clusters:
		case bag_of_words_stopwords:
		case bag_of_clusters_stopwords:
			return map.entrySet().stream().map(e -> new ObjectFloatPair<>(e.getKey(), 1f)).collect(Collectors.toList());
		case bag_of_words_count:
		case bag_of_clusters_count:
		case bag_of_words_stopwords_count:
		case bag_of_clusters_stopwords_count:
			return map.entrySet().stream().map(e -> new ObjectFloatPair<>(e.getKey(), e.getValue())).collect(Collectors.toList());
		case bag_of_words_norm:
		case bag_of_clusters_norm:
		case bag_of_words_stopwords_norm:
		case bag_of_clusters_stopwords_norm:
			float total = (float)map.entrySet().stream().mapToDouble(e -> e.getValue()).sum();
			return map.entrySet().stream().map(e -> new ObjectFloatPair<>(e.getKey(), e.getValue()/total)).collect(Collectors.toList());
		default: return null;
		}
	}
	
	protected Object2FloatMap<String> getBagOfWords(S state, FeatureItem[] items, boolean stopwords)
	{
		Object2FloatMap<String> map = new Object2FloatOpenHashMap<>();
		NLPNode node;
		int index;
		String f;
		
		for (NLPNode[] nodes : state.getDocument(stopwords))
		{
			outer: for (int i=1; i<nodes.length; i++)
			{
				StringJoiner join = new StringJoiner("_");
				
				for (FeatureItem item : items)
				{
					index = i + item.window;
					if (index < 1 || index >= nodes.length) continue outer;
					node = state.getRelativeNode(nodes[index], item.relation);
					if (node == null) continue outer;
					f = getFeature(state, item, node);
					if (f == null) continue outer;
					join.add(f);
				}
				
				map.merge(join.toString(), 1f, (oldCount, newCount) -> oldCount + newCount);
			}
		}
		
		return map;
	}
	
	protected Object2FloatMap<String> getBagOfClusters(S state, boolean stopwords)
	{
		Object2FloatMap<String> map = new Object2FloatOpenHashMap<>();
		Set<String> clusters;
		
		for (NLPNode[] nodes : state.getDocument(stopwords))
		{
			for (int i=1; i<nodes.length; i++)
			{
				clusters = nodes[i].getWordClusters();
				if (clusters == null) continue;
				
				for (String f : clusters)
					map.merge(f, 1f, (oldCount, newCount) -> oldCount + newCount);
			}
		}
		
		return map;
	}
	
	@Override
	public float[] createDenseVector(S state)
	{
		return null;
	}
}
