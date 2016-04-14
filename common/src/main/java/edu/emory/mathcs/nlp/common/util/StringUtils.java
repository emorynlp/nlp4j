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

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringUtils
{
	private StringUtils() {}
	
	static public int getLCSLength(String[] x, String[] y)
	{
		final int M = x.length;
		final int N = y.length;
		
		int[][] counts = new int[M+1][N+1];
		int i, j;
		
		for (i=1; i<=M; i++)
			for (j=1; j<=N; j++)
				counts[i][j] = (x[i-1].equals(y[j-1])) ? counts[i-1][j-1] + 1 : Math.max(counts[i][j-1], counts[i-1][j]);

		return counts[M][N];
	}
	
	/** @return the specific number of spaces. */
	static public String spaces(int length)
	{
		StringBuilder build = new StringBuilder();
		int i;
		
		for (i=0; i<length; i++)
			build.append(StringConst.SPACE);
		
		return build.toString();
	}
	
	static public boolean startsWithAny(String str, String... suffixes)
	{
		for (String suffix : suffixes)
		{
			if (str.startsWith(suffix))
				return true;
		}
		
		return false;
	}
	
	static public boolean endsWithAny(String str, String... suffixes)
	{
		for (String suffix : suffixes)
		{
			if (str.endsWith(suffix))
				return true;
		}
		
		return false;
	}
	
	static public String trim(String s, int trimSize)
	{
		return s.substring(0, s.length()-trimSize);
	}
	
//	====================================== Conversion ======================================
	
	/**
	 * This method converts characters in [128, 256) correctly where {@link String#toUpperCase()} doesn't. 
	 * About 2+ times faster than {@link String#toUpperCase()}.
	 */
	static public String toUpperCase(String s)
	{
		if (s == null) return null;
		char[] array = s.toCharArray();
		boolean b = CharUtils.toUpperCase(array);
		return b ? new String(array) : s;
	}
	
	/**
	 * This method converts characters in [128, 256) correctly where {@link String#toUpperCase()} doesn't.
	 * About 2+ times faster than {@link String#toLowerCase()}.
	 */
	static public String toLowerCase(String s)
	{
		if (s == null) return null;
		char[] array = s.toCharArray();
		boolean b = CharUtils.toLowerCase(array);
		return b ? new String(array) : s;
	}
	
	static public String[] toUpperCase(String[] source)
	{
		int i, size = source.length;
		String[] target = new String[size];
		
		for (i=0; i<size; i++)
			target[i] = toUpperCase(source[i]);
		
		return target;
	}
	
	static public String[] toLowerCase(String[] source)
	{
		int i, size = source.length;
		String[] target = new String[size];
		
		for (i=0; i<size; i++)
			target[i] = toLowerCase(source[i]);
		
		return target;
	}
	
//	====================================== Simplify ======================================
	
	/**
	 * @return a simplified form of the specific word-form.
	 * @see MetaUtils#containsHyperlink(String)
	 * @see #collapseDigits(String)
	 * @see #collapsePunctuation(String)
	 */
	static public String toSimplifiedForm(String s, boolean uncapitalize)
	{
		if (MetaUtils.endsWithFileExtension(s) || MetaUtils.containsHyperlink(s))
			return MetaConst.HYPERLINK;
		
		if (s.length() == 1)
		{
			char c = s.charAt(0);
			if (CharUtils.isCurrency(c)) return StringConst.DOLLAR;
			if (CharUtils.isSingleQuotationMark(c)) return StringConst.SINGLE_QUOTE;
			if (CharUtils.isDoubleQuotationMark(c)) return StringConst.DOUBLE_QUOTE;
			if (CharUtils.isListMark(c) || CharUtils.isHyphen(c)) return StringConst.HYPHEN;
		}
		
		s = collapseDigits(s);
		s = collapsePunctuation(s);
		if (uncapitalize) s = toLowerCase(s);
		
		return s;
	}
	
	/** Calls {@link #toSimplifiedForm(String, boolean)}, where {@code lower = false}. */
	static public String toSimplifiedForm(String s)
	{
		return toSimplifiedForm(s, false);
	}
	
	static public String toUndigitalizedForm(String s)
	{
		return toUndigitalizedForm(s, false);
	}
	
	static public String toUndigitalizedForm(String s, boolean uncapitalize)
	{
		char[] cs = s.toCharArray();
		boolean b = false;
		
		for (int i=0; i<cs.length; i++)
		{
			if ('0' <= cs[i] && cs[i] <= '9')
			{
				cs[i] = '0';
				b = true;
			}
			else if (uncapitalize)
				cs[i] = CharUtils.toLowerCase(cs[i]);
		}
		
		return b || uncapitalize ? new String(cs) : s;
	}
	
	static public String toLowerCaseSimplifiedForm(String s)
	{
		return toLowerCase(toSimplifiedForm(s));
	}
	
//	====================================== Collapse ======================================
	
	static public String collapseDigits(String s)
	{
		StringBuilder build = new StringBuilder();
		char[] cs = s.toCharArray();
		int i, j, size = cs.length;
		char curr, prev = 0;
		
		for (i=0; i<size; i++)
		{
			i = collapseDigitsAux(cs, i);
			curr = cs[i];
			
			if (curr == CharConst.PERCENT)
			{
				if (CharUtils.isDigit(prev))
					continue;
			}
			else if (CharUtils.isPreDigitSymbol(curr) || curr == CharConst.COMMA || curr == CharConst.COLON || curr == CharConst.FW_SLASH || curr == CharConst.EQUAL)
			{
				if (i+1 < size && CharUtils.isDigit(cs[j = collapseDigitsAux(cs, i+1)]))
				{
					if (i == 0)
					{
						i = j;
						curr = cs[i];
					}
					else if (CharUtils.isDigit(prev))
					{
						i = j;
						continue;
					}
				}
			}
			
			if (CharUtils.isDigit(curr))
			{
				if (!CharUtils.isDigit(prev))
					build.append(CharConst.ZERO);
			}
			else
				build.append(curr);
			
			prev = curr;
		}
		
		return build.toString();
	}
	
	static private int collapseDigitsAux(char[] cs, int index)
	{
		char curr = cs[index];
		
		if (curr == CharConst.DOLLAR || curr == CharConst.POUND)
		{
			if (index+1 < cs.length && CharUtils.isDigit(cs[index+1]))
				return index + 1;
		}
		
		return index;
	}
	
	static public String collapsePunctuation(String s)
	{
		StringBuilder build = new StringBuilder();
		char[] cs = s.toCharArray();
		int i, size = cs.length;
		
		for (i=0; i<size; i++)
		{
			if (i > 1 && CharUtils.isPunctuation(cs[i]) && cs[i] == cs[i-1] && cs[i] == cs[i-2])
				continue;
			
			build.append(cs[i]);
		}
		
		return (build.length() < size) ? build.toString() : s;
	}
	
	static public List<String> stripPunctuation(List<String> tokens)
	{
		List<String> list = new ArrayList<>();
		
		for (String token : tokens)
		{
			if (!containsPunctuationOnly(token))
				list.add(token);
		}
		
		return list;
	}
	
	static public List<String> stripPunctuation(String[] tokens)
	{
		List<String> list = new ArrayList<>();
		
		for (String token : tokens)
		{
			if (!containsPunctuationOnly(token))
				list.add(token);
		}
		
		return list;
	}
	
//	====================================== Boolean ======================================
	
	/**
	 * @return {@code true} if the specific string includes only upper-case characters.
	 * @see CharUtils#isUpperCase(char).
	 */
	static public boolean containsUpperCaseOnly(String s)
	{
		char[] cs= s.toCharArray();
		int i, size = cs.length;
		
		for (i=0; i<size; i++)
		{
			if (!CharUtils.isUpperCase(cs[i]))
				return false;
		}
		
		return true;
	}
	
	/**
	 * @return {@code true} if the specific string includes only lower-case characters.
	 * @see CharUtils#isLowerCase(char). 
	 */
	static public boolean containsLowerCaseOnly(String s)
	{
		char[] cs= s.toCharArray();
		int i, size = cs.length;
		
		for (i=0; i<size; i++)
		{
			if (!CharUtils.isLowerCase(cs[i]))
				return false;
		}
		
		return true;
	}
	
	/**
	 * @return {@code true} if the specific string contains any digit.
	 * @see CharUtils#isDigit(char).
	 */
	static public boolean containsDigit(String s)
	{
		char[] cs= s.toCharArray();
		int i, size = cs.length;
		
		for (i=0; i<size; i++)
		{
			if (CharUtils.isDigit(cs[i]))
				return true;
		}
		
		return false;
	}
	
	public static boolean containsDigitOnly(String s)
	{
		return CharUtils.containsDigitOnly(s.toCharArray());
	}
	
	static public boolean containsPunctuation(String s)
	{
		char[] cs= s.toCharArray();
		int i, size = cs.length;
		
		for (i=0; i<size; i++)
		{
			if (CharUtils.isPunctuation(cs[i]))
				return true;
		}
		
		return false;
	}
	
	public static boolean containsPunctuationOnly(String s)
	{
		return CharUtils.containsPunctuationOnly(s.toCharArray());
	}
	
	public static boolean containsPunctuationOrWhiteSpacesOnly(String s)
	{
		return CharUtils.containsPunctuationOrWhiteSpacesOnly(s.toCharArray());
	}
	
	public static boolean containsPunctuationOrDigitsOrWhiteSpacesOnly(String s)
	{
		return CharUtils.containsPunctuationOrDigitsOrWhiteSpacesOnly(s.toCharArray());
	}
	
	public static boolean isDouble(String s)
	{
		for (char c : s.toCharArray())
		{
			if (!Character.isDigit(c) && c != '.' && c != '-' && c != '+')
				return false;
		}
		
		return true;
	}
	
//	====================================== Getters ======================================
	
	static public String[] getPrefixes(String form, int n)
	{
		int i, length = form.length() - 1;
		if (length < n)	n = length;	
		String[] prefixes = new String[n];
		
		for (i=0; i<n; i++)
			prefixes[i] = form.substring(0, i+1);
		
		return prefixes;
	}
	
	static public String[] getSuffixes(String form, int n)
	{
		int i, length = form.length() - 1;
		if (length < n)	n = length;	
		String[] suffixes = new String[n];
		
		for (i=0; i<n; i++)
			suffixes[i] = form.substring(length-i);
		
		return suffixes;
	}
	
	/**
	 * @param maxRepetitions the max count of repetition of a word shape in sequence.
	 * @return the word shape of the word-form.
	 */
	static public String getShape(String form, int maxRepetitions)
	{
		if (form == null) return null;
		StringBuilder build = new StringBuilder();
		char curr, prev = CharConst.EMPTY;
		char cs[] = form.toCharArray();
		int i, len = cs.length;
		int repetition = 0;
		
		for (i=0; i<len; i++)
		{
			curr = cs[i];
			
			if      (CharUtils.isUpperCase(curr)) 	curr = 'A';
			else if (CharUtils.isLowerCase(curr))	curr = 'a';
			else if (CharUtils.isDigit(curr))		curr = '1';
			else if (CharUtils.isPunctuation(curr))	curr = '.';
			else									curr = 'x';
			
			if (curr == prev)
				repetition++;
			else
			{
				prev = curr;
				repetition = 0;
			}
			
			if (repetition < maxRepetitions)
				build.append(curr);
		}
		
		return build.toString();
	}
}