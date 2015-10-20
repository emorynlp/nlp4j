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
package edu.emory.mathcs.nlp.corenlp.pos;

import java.util.Arrays;

import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.corenlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.corenlp.component.feature.FeatureTemplate;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class POSFeatureTemplate extends FeatureTemplate<POSNode,POSState<POSNode>>
{
	private static final long serialVersionUID = -243334323533999837L;
	
	public POSFeatureTemplate()	
	{
		init();
	}
	
	protected abstract void init();
	
//	========================= FEATURE EXTRACTORS =========================
	
	@Override
	protected String getFeature(FeatureItem<?> item)
	{
		POSNode node = state.getNode(item.window);
		if (node == null) return null;
		
		switch (item.field)
		{
		case word_form: return node.getWordForm();
		case simplified_word_form: return node.getSimplifiedWordForm();
		case uncapitalized_simplified_word_form: return StringUtils.toLowerCase(node.getSimplifiedWordForm());
		case word_shape: return node.getWordShape((Integer)item.value);
		case lemma: return node.getLemma();
		case feats: return node.getFeat((String)item.value);
		case pos_tag: return node.getPOSTag();
		case ambiguity_class: return state.getAmbiguityClass(node);
		case prefix: return getPrefix(node, (Integer)item.value);
		case suffix: return getSuffix(node, (Integer)item.value);
		default: throw new IllegalArgumentException("Unsupported feature: "+item.field);
		}
	}
	
	@Override
	protected String[] getFeatures(FeatureItem<?> item)
	{
		POSNode node = state.getNode(item.window);
		if (node == null) return null;
		
		switch (item.field)
		{
		case orthographic: return getOrthographicFeatures(node);
		case binary: return getBinaryFeatures(node);
		default: throw new IllegalArgumentException("Unsupported feature: "+item.field);
		}
	}
	
	/** The prefix cannot be the entire word (e.g., getPrefix("abc", 3) -> null). */
	protected String getPrefix(POSNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(0, n)) : null;
	}
	
	/** The suffix cannot be the entire word (e.g., getSuffix("abc", 3) -> null). */
	protected String getSuffix(POSNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(s.length()-n)) : null;
	}
	
	protected String[] getOrthographicFeatures(POSNode node)
	{
		String[] t = node.getOrthographic(state.isFirst(node));
		return t.length == 0 ? null : t;
	}
	
	protected String[] getBinaryFeatures(POSNode node)
	{
		String[] values = new String[2];
		int index = 0;
		
		if (state.isFirst(node)) values[index++] = "0";
		if (state.isLast (node)) values[index++] = "1";
		
		return (index == 0) ? null : (index == values.length) ? values : Arrays.copyOf(values, index);
	}
}
