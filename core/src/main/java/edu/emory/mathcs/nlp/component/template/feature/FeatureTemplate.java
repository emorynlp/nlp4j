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
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.node.Orthographic;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.learning.util.FeatureMap;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.StringPrediction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.openhft.hashing.LongHashFunction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class FeatureTemplate<S extends NLPState> implements Serializable
{
	private static final long serialVersionUID = -6755594173767815098L;
	protected FeatureMap             feature_map;
	protected List<FeatureItem<?>[]> feature_list;
	protected List<FeatureItem<?>>   feature_set;
	protected List<FeatureItem<?>>   feature_set_weighted;
	
	// dynamic feature induction
	protected LongHashFunction dynamic_feature_hash;
	protected final int        dynamic_feature_size;
	protected boolean[]        dynamic_feature_switch;
	protected Random           dynamic_feature_gap;
	
	public FeatureTemplate(int dynamicFeatureSize)
	{
		feature_map          = new FeatureMap();
		feature_list         = new ArrayList<>();
		feature_set          = new ArrayList<>();
		feature_set_weighted = new ArrayList<>();
		dynamic_feature_size = dynamicFeatureSize;
		
		// dynamic feature induction
		if (useDynamicFeatureInduction())
		{
			dynamic_feature_hash   = LongHashFunction.xx_r39(serialVersionUID);
			dynamic_feature_switch = new boolean[dynamic_feature_size];
			dynamic_feature_gap    = new XORShiftRandom(9);	
		}

		// feature templates
		init();
	}

//	============================== INITIALIZATION ==============================
	
	protected abstract void init();

	public void add(FeatureItem<?>... items)
	{
		feature_list.add(items);
	}
	
	public void addSet(FeatureItem<?> items)
	{
		feature_set.add(items);
	}
	
	public void addSetWeighted(FeatureItem<?> items)
	{
		feature_set_weighted.add(items);
	}
	
	public int getSparseFeatureSize()
	{
		return feature_map.size();
	}
	
	public int getTemplateSize()
	{
		return feature_list.size() + feature_set.size() + feature_set_weighted.size();
	}
	
//	============================== DYNAMIC FEATURE INDUCTION ==============================
	
	public void addDynamicFeature(String feature)
	{
		dynamic_feature_switch[getDynamicFeatureIndex(feature)] = true;
	}
	
	public void addDynamicFeatures(Collection<String> features)
	{
		for (String feature : features)	addDynamicFeature(feature);
	}	
	
	public boolean useDynamicFeatureInduction()
	{
		return dynamic_feature_size > 0; 
	}
	
	private int getRandomGap()
	{
		return dynamic_feature_gap.nextInt(2) + 1;
	}
	
	static public String getFeatureCombination(int i, int j)
	{
		return (i < j) ? i+"_"+j : j+"_"+i;
	}
	
//	static public String getFeatureCombination(int... indices)
//	{
//		Arrays.sort(indices);
//		StringJoiner join = new StringJoiner("_");
//		for (int index : indices) join.add(Integer.toString(index));
//		return join.toString();
//	}

	protected int getDynamicFeatureIndex(String feature)
	{
		return MathUtils.modulus(dynamic_feature_hash.hashChars(feature), dynamic_feature_size);
	}
	
	private void appendDynamicFeatures(SparseVector x, int type, boolean add)
	{
		List<SparseItem> ov = new ArrayList<>(x.getVector());
		SparseItem oi, oj;
		String f;
		int i, j;
		
		for (i=0; i<ov.size(); i+=getRandomGap())
		{
			oi = ov.get(i);
			
			for (j=i+1; j<ov.size(); j+=getRandomGap())
			{
				oj = ov.get(j);
				f  = getFeatureCombination(oi.getIndex(), oj.getIndex());
				if (dynamic_feature_switch[getDynamicFeatureIndex(f)]) add(x, type, f, 1, add);
			}
		}
	}
	
//	private class InductionTask implements Callable<List<String>>
//	{
//		private List<SparseItem> ov;
//		private int i;
//		
//		public InductionTask(List<SparseItem> ov, int i)
//		{
//			this.ov = ov;
//			this.i  = i;
//		}
//
//		@Override
//		public List<String> call()
//		{
//			List<String> list = new ArrayList<>();
//			SparseItem oj, oi = ov.get(i);
//			String f;
//			
//			for (int j=i+1; j<ov.size(); j+=getRandomGap())
//			{
//				oj = ov.get(j);
//				f  = getFeatureCombination(oi.getIndex(), oj.getIndex());
//				if (dynamic_feature_switch[getDynamicFeatureIndex(f)]) list.add(f);
//			}
//			
//			return list;
//		}
//	}
	
//	============================== EXTRACTOR ==============================
	
	public FeatureVector createFeatureVector(S state, boolean add)
	{
		return new FeatureVector(createSparseVector(state,add), createDenseVector(state));
	}
	
	public SparseVector createSparseVector(S state, boolean add)
	{
		SparseVector x = new SparseVector();
		Collection<StringPrediction> w;
		Collection<String> t;
		int i, type = 0;
		String f;
		
		for (i=0; i<feature_list.size(); i++,type++)
		{
			f = getFeature(state, feature_list.get(i));
			add(x, type, f, 1, add);
		}
		
		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getFeatures(state, feature_set.get(i));
			if (t != null) for (String s : t) add(x, type, s, 1, add);
		}
		
		for (i=0; i<feature_set_weighted.size(); i++,type++)
		{
			w = getFeaturesWeighted(state, feature_set_weighted.get(i));
			if (w != null) for (StringPrediction s : w) add(x, type, s.getLabel(), s.getScore(), add);
		}
		
		if (useDynamicFeatureInduction())
			appendDynamicFeatures(x, type, add);
		
		return x;
	}
	
	private void add(SparseVector x, int type, String value, float weight, boolean add)
	{
		if (value != null)
		{
			int index = add ? feature_map.add(type, value) : feature_map.index(type, value);
			if (index > 0) x.add(index, weight);
		}
	}
	
	public abstract float[] createDenseVector(S state);
	
//	============================== SINGLE FEATURES ==============================
	
	/** Called by {@link #extractFeatures()}. */
	protected String getFeature(S state, FeatureItem<?>... items)
	{
		String f;
		
		if (items.length == 1)
			return getFeature(state, items[0]);
		else
		{
			StringJoiner join = new StringJoiner("_");
			
			for (FeatureItem<?> item : items)
			{
				f = getFeature(state, item);
				if (f == null) return null;
				join.add(f);
			}
			
			return join.toString();
		}
	}
	
	protected String getFeature(S state, FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeature(item, node);
	}
	
	protected String getFeature(FeatureItem<?> item, NLPNode node)
	{
		String f = node.getValue(item.field);
		if (f != null) return f;
		
		switch (item.field)
		{
		case word_shape: return StringUtils.getShape(node.getSimplifiedWordForm(), (Integer)item.value);
		case uncapitalized_word_shape: return StringUtils.getShape(StringUtils.toLowerCase(node.getSimplifiedWordForm()), (Integer)item.value);
		case prefix: return getPrefix(node, (Integer)item.value);
		case suffix: return getSuffix(node, (Integer)item.value);
		case feats: return node.getFeat((String)item.value);
		case valency: return node.getValency((Direction)item.value);
		case named_entity_gazetteers: return getNamedEntityGazetteers(node);
		default: return null;
		}
	}
	
	protected String getNamedEntityGazetteers(NLPNode node)
	{
		Set<String> set = node.getNamedEntityGazetteers();
		return set == null || set.isEmpty() || set.size() > 1 ? null : Joiner.join(set, StringConst.UNDERSCORE);
	}
	
	/** The prefix cannot be the entire word (e.g., getPrefix("abc", 3) -> null). */
	protected String getPrefix(NLPNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(0, n)) : null;
	}
	
	/** The suffix cannot be the entire word (e.g., getSuffix("abc", 3) -> null). */
	protected String getSuffix(NLPNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(s.length()-n)) : null;
	}
	
//	============================== SET FEATURES ==============================
	
	protected Collection<String> getFeatures(S state, FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeatures(state, item, node);
	}
	
	protected Collection<String> getFeatures(S state, FeatureItem<?> item, NLPNode node)
	{
		switch (item.field)
		{
		case position: return getPositionFeatures(state, node);
		case orthographic: return getOrthographicFeatures(state, node, true);
		case orthographic_uncapitalized: return getOrthographicFeatures(state, node, false);
		case word_clusters: return node.getWordClusters();
		case named_entity_gazetteers: return node.getNamedEntityGazetteers();
		case bag_of_words: return getBagOfWords(state, item);
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
		
		if (MetaConst.HYPERLINK.equals(node.getSimplifiedWordForm()))
			list.add(Orthographic.HYPERLINK);
		else
		{
			char[] cs = node.getSimplifiedWordForm().toCharArray();
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
	
	protected Set<String> getBagOfWords(S state, FeatureItem<?> item)
	{
		Boolean includePunct = (Boolean)item.value;
		NLPNode[] nodes = state.getNodes();
		Set<String> set = new HashSet<>();
		String key;
		
		for (int i=1; i<nodes.length; i++)
		{
			key = GlobalLexica.getNonStopWord(nodes[i]);
			if (key != null && (includePunct || StringUtils.containsPunctuationOnly(key))) set.add(key);
		}
		
		return set;
	}
	
//	============================== SET FEATURES WEIGHTED ==============================
	
	protected Collection<StringPrediction> getFeaturesWeighted(S state, FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeaturesWeighted(state, item, node);
	}
	
	protected Collection<StringPrediction> getFeaturesWeighted(S state, FeatureItem<?> item, NLPNode node)
	{
		switch (item.field)
		{
		case bag_of_words_count: return getBagOfWordsCount(state, item);
		default: return null;
		}
	}
	
	protected Set<StringPrediction> getBagOfWordsCount(S state, FeatureItem<?> item)
	{
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		Boolean includePunct = (Boolean)item.value;
		NLPNode[] nodes = state.getNodes();
		String key;
		
		for (int i=1; i<nodes.length; i++)
		{
			key = GlobalLexica.getNonStopWord(nodes[i]);
			if (key != null && (includePunct || StringUtils.containsPunctuationOnly(key))) FastUtils.increment(map, key);
		}
		
		return toSet(map);
	}
	
	protected Set<StringPrediction> toSet(Object2IntMap<String> map)
	{
		Set<StringPrediction> set = new HashSet<>();
		
		for (Entry<String> e : map.object2IntEntrySet())
			set.add(new StringPrediction(e.getKey(), e.getValue()));
		
		return set;
	}
}
