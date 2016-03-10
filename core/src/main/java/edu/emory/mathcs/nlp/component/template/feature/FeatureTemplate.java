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
package edu.emory.mathcs.nlp.component.template.feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.node.Orthographic;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.learning.util.ColumnMajorVector;
import edu.emory.mathcs.nlp.learning.util.FeatureMap;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.StringPrediction;
import edu.emory.mathcs.nlp.learning.util.WeightVector;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureTemplate<S extends NLPState> implements Serializable
{
	private static final long serialVersionUID = -6755594173767815098L;

	protected List<FeatureItem[]>   feature_list;
	protected List<FeatureItem>     feature_set;
	protected List<FeatureItem>     word_embeddings;
	
	protected Object2IntMap<String> feature_count;
	protected FeatureMap            feature_map;
	protected int                   cutoff;
	
	public FeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		feature_list    = new ArrayList<>();
		feature_set     = new ArrayList<>();
		word_embeddings = new ArrayList<>();

		feature_count   = new Object2IntOpenHashMap<String>();
		feature_map     = new FeatureMap();
		
		setCutoff(hp.getFeature_cutoff());
		init(eFeatures);
	}
	
//	============================== IINTIALIZATION ==============================
	
	protected void init(Element eFeatures)
	{
		if (eFeatures == null) return;
		NodeList nodes = eFeatures.getElementsByTagName("feature");
		Element element;
		
		for (int i=0; i<nodes.getLength(); i++)
		{
			element = (Element)nodes.item(i);
			initFeatureItems(element);
		}
	}
	
	protected void initFeatureItems(Element element)
	{
		FeatureItem[] items = createFeatureItems(element);
		
		if (XMLUtils.getBooleanAttribute(element, "set"))
			addSet(items[0]);
		else if (items[0].field == Field.word_embedding)
			addWordEmbedding(items[0]);
		else
			add(items);
	}
	
	protected FeatureItem[] createFeatureItems(Element element)
	{
		List<String> list = new ArrayList<>();
		String s;
		
		for (int i=0; ;i++)
		{
			s = element.getAttribute("f"+i);
			if (s.isEmpty()) break;
			list.add(s);
		}
		
		FeatureItem[] items = new FeatureItem[list.size()];
		
		for (int i=0; i<items.length; i++)
			items[i] = createFeatureItem(list.get(i));

		return items;
	}
	
	private FeatureItem createFeatureItem(String s)
	{
		String[] t = Splitter.splitColons(s);
		Source   source;
		Relation relation;
		int      window;
		Field    field;
		Object   attribute;
		
		// source
		s = t[0];
		source = Source.valueOf(s.substring(0, 1));
		
		// window
		int endIdx = s.indexOf('_');
		if (endIdx < 0) endIdx = s.length();
		window = endIdx == 1 ? 0 : Integer.parseInt(s.substring(1, endIdx));
		
		// relation
		relation = endIdx != s.length() ? Relation.valueOf(s.substring(endIdx+1)) : null;

		// field
		field = Field.valueOf(t[1]);
		
		// attribute
		attribute = t.length > 2 ? createAttribute(field, t[2]) : null;
		return new FeatureItem(source, relation, window, field, attribute);
	}
	
	@SuppressWarnings({ "incomplete-switch" })
	protected Object createAttribute(Field field, String attribute)
	{
		switch (field)
		{
		case prefix : return new Integer(Integer.parseInt(attribute));
		case suffix : return new Integer(Integer.parseInt(attribute));
		case feats  : return attribute;
		case valency: return Direction.valueOf(attribute);
		}
		
		return null;
	}

//	============================== GETTERS / SETTERS ==============================

	public void add(FeatureItem... items)
	{
		feature_list.add(items);
	}
	
	public void addSet(FeatureItem items)
	{
		feature_set.add(items);
	}
	
	public void addWordEmbedding(FeatureItem item)
	{
		word_embeddings.add(item);
	}
	
	public int getSparseFeatureSize()
	{
		return feature_map.size();
	}
	
	public int getTemplateSize()
	{
		return feature_list.size() + feature_set.size() + word_embeddings.size();
	}
	
	public int getCutoff()
	{
		return cutoff;
	}
	
	public void setCutoff(int cutoff) 
	{
		this.cutoff = cutoff;
	}
	
	public void clearFeatureCount()
	{
		feature_count.clear();
	}
	
	public void initFeatureCount()
	{
		feature_count = new Object2IntOpenHashMap<String>();
	}
	
//	============================== EXTRACTOR ==============================
	
	public FeatureVector createFeatureVector(S state, boolean isTrain)
	{
		return new FeatureVector(createSparseVector(state, isTrain), createDenseVector(state));
	}
	
	public SparseVector createSparseVector(S state, boolean isTrain)
	{
		SparseVector x = new SparseVector();
		Collection<String> t;
		int i, type = 0;
		String f;
		
		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getFeatures(state, feature_set.get(i));
			if (t != null) for (String s : t) add(x, type, s, 1, isTrain);
		}
		
		for (i=0; i<feature_list.size(); i++,type++)
		{
			f = getFeature(state, feature_list.get(i));
			add(x, type, f, 1, isTrain);
		}
		
		return x;
	}
	
	protected void add(SparseVector x, int type, String value, float weight, boolean isTrain)
	{
		if (value != null)
		{
			int index;
			
			if (isTrain)
				index = FastUtils.increment(feature_count, type+value) > cutoff ? feature_map.add(type, value) : -1;
			else
				index = feature_map.index(type, value);
			
			if (index > 0) x.add(index, weight);
		}
	}
	
//	============================== SINGLE FEATURES ==============================
	
	/** Called by {@link #extractFeatures()}. */
	protected String getFeature(S state, FeatureItem... items)
	{
		String f;
		
		if (items.length == 1)
			return getFeature(state, items[0]);
		else
		{
			StringJoiner join = new StringJoiner("_");
			
			for (FeatureItem item : items)
			{
				f = getFeature(state, item);
				if (f == null) return null;
				join.add(f);
			}
			
			return join.toString();
		}
	}
	
	protected String getFeature(S state, FeatureItem item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeature(state, item, node);
	}
	
	protected String getFeature(S state, FeatureItem item, NLPNode node)
	{
		String f = node.getValue(item.field);
		if (f != null) return f;
		
		switch (item.field)
		{
		case prefix : return getPrefix(node, (Integer)item.attribute);
		case suffix : return getSuffix(node, (Integer)item.attribute);
		case feats  : return node.getFeat((String)item.attribute);
		case valency: return node.getValency((Direction)item.attribute);
		default: return null;
		}
	}
	
	/** The prefix cannot be the entire word (e.g., getPrefix("abc", 3) -> null). */
	protected String getPrefix(NLPNode node, int n)
	{
		String s = node.getWordFormSimplifiedLowercase();
		return (n < s.length()) ? s.substring(0, n) : null;
	}
	
	/** The suffix cannot be the entire word (e.g., getSuffix("abc", 3) -> null). */
	protected String getSuffix(NLPNode node, int n)
	{
		String s = node.getWordFormSimplifiedLowercase();
		return (n < s.length()) ? s.substring(s.length()-n) : null;
	}
	
//	============================== SET FEATURES ==============================
	
	protected Collection<String> getFeatures(S state, FeatureItem item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeatures(state, item, node);
	}
	
	protected Collection<String> getFeatures(S state, FeatureItem item, NLPNode node)
	{
		switch (item.field)
		{
		case positional: return getPositionFeatures(state, node);
		case orthographic: return getOrthographicFeatures(state, node, true);
		case orthographic_lowercase: return getOrthographicFeatures(state, node, false);
		case ambiguity_classes: return node.getAmbiguityClasseList();
		case named_entity_gazetteers: return node.getNamedEntityGazetteerSet();
		case word_clusters: return node.getWordClusters();
		default: return null;
		}
	}
	
	protected List<String> getPositionFeatures(S state, NLPNode node)
	{
		List<String> values = new ArrayList<>();
		
		if      (state.isFirst(node)) values.add("0");
		else if (state.isLast (node)) values.add("1");
		
		return values.isEmpty() ? null : values;
	}
	
	protected List<String> getOrthographicFeatures(S state, NLPNode node, boolean caseSensitive)
	{
		List<String> list = new ArrayList<>();
		
		if (MetaConst.HYPERLINK.equals(node.getWordFormSimplified()))
			list.add(Orthographic.HYPERLINK);
		else
		{
			char[] cs = node.getWordFormSimplified().toCharArray();
			getOrthographicFeauturesAux(list, cs, state.isFirst(node), caseSensitive);
		}
		
		return list.isEmpty() ? null : list;
	}
	
	/** Called by {@link #getOrthographic(boolean)}. */
	protected void getOrthographicFeauturesAux(List<String> list, char[] cs, boolean isFirst, boolean caseSensitive)
	{
		boolean hasDigit  = false;
		boolean hasPeriod = false;
		boolean hasHyphen = false;
		boolean hasPunct  = false;
		boolean fstUpper  = false;
		boolean allDigit  = true;
		boolean allPunct  = true;
		boolean allUpper  = true;
		boolean allLower  = true;
		boolean noLower   = true;
		boolean allDigitOrPunct = true;
		int     countUpper = 0;
		
		boolean upper, lower, punct, digit;
		int i, size = cs.length;
		char c;
		
		for (i=0; i<size; i++)
		{
			c = cs[i];
			
			upper = CharUtils.isUpperCase(c);
			lower = CharUtils.isLowerCase(c);
			digit = CharUtils.isDigit(c);
			punct = CharUtils.isPunctuation(c);
			
			if (upper)
			{
				if (i == 0)	fstUpper = true;
				else		countUpper++;
			}
			else
				allUpper = false;
			
			if (lower)	noLower  = false;	
			else		allLower = false;
			
			if (digit)	hasDigit = true;
			else		allDigit = false;

			if (punct)
			{
				hasPunct = true;
				if (c == CharConst.PERIOD) hasPeriod = true;
				if (c == CharConst.HYPHEN) hasHyphen = true;
			}
			else
				allPunct = false;
			
			if (!digit && !punct)
				allDigitOrPunct = false;
		}
		
		if (allUpper)
			{if (caseSensitive) list.add(Orthographic.ALL_UPPER);}
		else if (allLower)
			{if (caseSensitive) list.add(Orthographic.ALL_LOWER);}
		else if (allDigit)
			list.add(Orthographic.ALL_DIGIT);
		else if (allPunct)
			list.add(Orthographic.ALL_PUNCT);
		else if (allDigitOrPunct)
			list.add(Orthographic.ALL_DIGIT_OR_PUNCT);
		else if (noLower)
			{if (caseSensitive) list.add(Orthographic.NO_LOWER);}
		
		if (caseSensitive && !allUpper)
		{
			if (fstUpper && !isFirst)
				list.add(Orthographic.FST_UPPER);
			if (countUpper == 1)
				list.add(Orthographic.UPPER_1);
			else if (countUpper > 1)
				list.add(Orthographic.UPPER_2);
		}
		
		if (!allDigit && hasDigit)
			list.add(Orthographic.HAS_DIGIT);
		
		if (hasPeriod)	list.add(Orthographic.HAS_PERIOD);
		if (hasHyphen)	list.add(Orthographic.HAS_HYPHEN);
		
		if (!allPunct && !hasPeriod && !hasHyphen && hasPunct)
			list.add(Orthographic.HAS_OTHER_PUNCT);
	}
	
//	============================== SET FEATURES WEIGHTED ==============================
	
	protected Set<StringPrediction> toSet(Object2IntMap<String> map)
	{
		Set<StringPrediction> set = new HashSet<>();
		
		for (Entry<String> e : map.object2IntEntrySet())
			set.add(new StringPrediction(e.getKey(), e.getValue()));
		
		return set;
	}
	
//	============================== DENSE FEATURES ==============================

	public float[] createDenseVector(S state)
	{
		return getEmbeddingFeatures(state);
	}
	
	public float[] getEmbeddingFeatures(S state)
	{
		if (word_embeddings == null || word_embeddings.isEmpty()) return null;
		float[] w, v = null;
		NLPNode node;
		int i = -1;
		
		for (FeatureItem item : word_embeddings)
		{
			node = state.getNode(item);
			i++;
			
			if (node != null && node.hasWordEmbedding())
			{
				w = node.getWordEmbedding();
				if (v == null) v = new float[w.length * word_embeddings.size()];
				System.arraycopy(w, 0, v, w.length*i, w.length);
			}
		}
		
		return v;
	}
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (FeatureItem[] t : feature_list)
			build.append("["+Joiner.join(t, "],[")+"]\n");
		
		for (FeatureItem t : feature_set)
			build.append(t+"\n");

		if (word_embeddings != null)
			build.append(Joiner.join(word_embeddings, ",")+"\n");
		
		return build.toString();
	}
	
//	============================== REDUCTION ==============================
	
	public int reduce(WeightVector weights, float threshold)
	{
		final int L = weights.getLabelSize();
		final int F = weights.getSparseWeightVector().getFeatureSize();
		
		MajorVector oldSparse = weights.getSparseWeightVector();
		int[] indexMap = new int[F];
		int i, j, k, l, count = 1;	// bias
		float max, min;
		
		for (i=1; i<F; i++)
		{
			k = i * L;
			max = oldSparse.get(k);
			min = oldSparse.get(k);
			
			for (j=1; j<L; j++)
			{
				max = Math.max(max, oldSparse.get(k+j));
				min = Math.min(min, oldSparse.get(k+j));
			}
			
			if (Math.abs(max - min) >= threshold)
				indexMap[i] = count++;
		}
		
		MajorVector newSparse = new ColumnMajorVector();
		ObjectIterator<Entry<String>> it;
		int oldIndex, newIndex;
		Entry<String> e;
		
		newSparse.expand(L, count);
		
		// bias weights
		for (j=0; j<L; j++)
			newSparse.set(j, oldSparse.get(j));
		
		for (Object2IntMap<String> map : feature_map.getIndexMaps())
		{
			it = map.object2IntEntrySet().iterator();
			
			while (it.hasNext())
			{
				e = it.next();
				oldIndex = e.getValue();
				newIndex = (oldIndex < indexMap.length) ? indexMap[oldIndex] : -1;
				
				if (newIndex > 0)
				{
					e.setValue(newIndex);
					k = oldIndex * L;
					l = newIndex * L;
					
					for (j=0; j<L; j++)
						newSparse.set(l+j, oldSparse.get(k+j));
				}
				else
					it.remove();
			}
		}
		
		weights.setSparseWeightVector(newSparse);
		feature_map.setSize(count);
		return count;
	}
}
