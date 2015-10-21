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
package edu.emory.mathcs.nlp.emorynlp.utils.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.Field;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPNode implements Serializable
{
	private static final long serialVersionUID = 5522467283393796925L;
	protected String  simplified_word_form;
	protected String  word_form;
	protected FeatMap feat_map;
	protected int     id;
	
	public NLPNode() {}
	
	public NLPNode(String form)
	{
		setWordForm(form);
		setFeatMap(new FeatMap());
	}
	
	public NLPNode(int id, String form, FeatMap map)
	{
		setID(id);
		setWordForm(form);
		setFeatMap(map);
	}
	
//	============================== ID ==============================
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
//	============================== FEATURE MAP ==============================
	
	public FeatMap getFeatMap()
	{
		return feat_map;
	}
	
	public void setFeatMap(FeatMap map)
	{
		feat_map = map;
	}
	
	public String getFeat(String key)
	{
		return feat_map.get(key);
	}
	
	public String putFeat(String key, String value)
	{
		return feat_map.put(key, value);
	}
	
	public String removeFeat(String key)
	{
		return feat_map.remove(key);
	}
	
//	============================== WORD FORM ==============================
	
	public String getWordForm()
	{
		return word_form;
	}
	
	/** @return the previous word-form. */
	public String setWordForm(String form)
	{
		simplified_word_form = StringUtils.toSimplifiedForm(form);
		String t = word_form;
		word_form = form;
		return t;
	}

	public boolean isWordForm(String form)
	{
		return form.equals(word_form);
	}
	
//	============================== WORD FORM VARIATIONS ==============================
	
	/** @see StringUtils#toSimplifiedForm(String). */
	public String getSimplifiedWordForm()
	{
		return simplified_word_form;
	}
	
	public boolean isSimplifiedWordForm(String form)
	{
		return form.equals(simplified_word_form);
	}
	
	/**
	 * @return the word shape of the word-form.
	 * @param maxRepetitions the max count of repetition of a word shape in sequence.
	 * @see {@link StringUtils#getShape(String, int)}.
	 */
	public String getWordShape(int maxRepetitions)
	{
		return StringUtils.getShape(word_form, maxRepetitions);
	}
	
	/**
	 * @return the orthographic features of the word form.
	 * @param isFirst true if this node is the first node in sequence.
	 */
	public String[] getOrthographic(boolean isFirst)
	{
		List<String> list = new ArrayList<>();
		
		if (MetaConst.HYPERLINK.equals(simplified_word_form))
			list.add(Orthographic.HYPERLINK);
		else
		{
			char[] cs = word_form.toCharArray();
			getOrthographicFeautureAux(list, cs, isFirst);
		}
		
		return list.toArray(new String[list.size()]);
	}
	
	/** Called by {@link #getOrthographic(boolean)}. */
	private void getOrthographicFeautureAux(List<String> list, char[] cs, boolean isFirst)
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
			list.add(Orthographic.ALL_UPPER);
		else if (allLower)
			list.add(Orthographic.ALL_LOWER);
		else if (allDigit)
			list.add(Orthographic.ALL_DIGIT);
		else if (allPunct)
			list.add(Orthographic.ALL_PUNCT);
		else if (allDigitOrPunct)
			list.add(Orthographic.ALL_DIGIT_OR_PUNCT);
		else if (noLower)
			list.add(Orthographic.NO_LOWER);
		
		if (!allUpper)
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
	
	/** @return the value of the specific field. */
	public String getValue(Field field)
	{
		switch (field)
		{
		case word_form: return getWordForm();
		case simplified_word_form: return getSimplifiedWordForm();
		case uncapitalized_simplified_word_form: return StringUtils.toLowerCase(getSimplifiedWordForm());
		default: return null;
		}
	}

	@Override
	public String toString()
	{
		return word_form;
	}
}