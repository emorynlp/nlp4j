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
package edu.emory.mathcs.nlp.tokenizer;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.MetaUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.tokenizer.dictionary.Currency;
import edu.emory.mathcs.nlp.tokenizer.dictionary.Dictionary;
import edu.emory.mathcs.nlp.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.tokenizer.dictionary.Unit;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class Tokenizer
{
	private final CharSet S_SYMBOL_IN_BETWEEN = new CharOpenHashSet(new char[]{CharConst.SEMICOLON, CharConst.COMMA, CharConst.TILDA, CharConst.EQUAL, CharConst.PLUS, CharConst.AMPERSAND, CharConst.PIPE, CharConst.FW_SLASH});
	private final Pattern P_ABBREVIATION = PatternUtils.createClosedPattern("\\p{Alnum}([\\.|-]\\p{Alnum})*");
	private final Pattern P_YEAR = PatternUtils.createClosedPattern("\\d\\d['\u2019]?[sS]?");

	private Emoticon d_emoticon;
	private Currency d_currency;
	private Unit     d_unit;
	
	public Tokenizer()
	{
		d_emoticon = new Emoticon();
		d_currency = new Currency();
		d_unit     = new Unit();
	}
	
//	----------------------------------- Public methods -----------------------------------
	
	abstract public List<List<String>> segmentize(InputStream in);
	
	/** @return a list of tokens in the specific input stream. */
	public List<String> tokenize(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		ArrayList<String> tokens = new ArrayList<>();
		List<String> t;
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				t = tokenizeWhiteSpaces(line);
				if (!t.isEmpty()) tokens.addAll(t);
			}
			
			reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
		tokens.trimToSize();
		return tokens;
	}
	
	/** @return a list of tokens in the specific string. */
	public List<String> tokenize(String s)
	{
		List<String> tokens = tokenizeWhiteSpaces(s);
		return tokens;
	}
	
//	----------------------------------- Tokenize -----------------------------------
	
	/**
	 * Tokenizes white spaces.
	 * Called by {@link #tokenize(InputStream)} and {@link #tokenize(String)}.
	 */
	private List<String> tokenizeWhiteSpaces(String s)
	{
		List<String> tokens = new ArrayList<>();
		int i, len = s.length(), bIndex = 0;
		char[] cs = s.toCharArray();
		
		for (i=0; i<len; i++)
		{
			if (CharUtils.isWhiteSpace(cs[i]))
			{
				if (bIndex < i) tokenizeMetaInfo(tokens, s.substring(bIndex, i));
				bIndex = i + 1;
			}
		}
		 
		if (bIndex < len) tokenizeMetaInfo(tokens, s.substring(bIndex));
		if (!tokens.isEmpty()) finalize(tokens);
		
		return tokens;
	}
	
	/**
	 * Tokenizes hyperlinks, emoticons.
	 * Called by {@link #tokenizeAux(String)}.
	 */
	private void tokenizeMetaInfo(List<String> tokens, String s)
	{
		int[] ps;
		
		if ((ps = getMetaRange(s)) != null)
		{
			int bIndex = ps[0], eIndex = ps[1], len = s.length();
			
			if (0 < bIndex)		tokenizeSymbols(tokens, s.substring(0, bIndex));
								tokens.add(s.substring(bIndex, eIndex));
			if (eIndex < len)	tokenizeSymbols(tokens, s.substring(eIndex));
		}
		else
			tokenizeSymbols(tokens, s);
	}
	
	/** Called by {@link #tokenizeMetaInfo(List, String)}. */
	private int[] getMetaRange(String s)
	{
		int[] ps;
		
		if ((ps = d_emoticon.getEmoticonRange(s)) != null)
			return ps;
		
		Matcher m = MetaUtils.HYPERLINK.matcher(s);
		
		if (m.find())
			return new int[]{m.start(), m.end()};
		
		return null;
	}
	
	/** Called by {@link #tokenizeMetaInfo(List, String)}. */
	private void tokenizeSymbols(List<String> tokens, String s)
	{
		char[] cs = s.toCharArray();
		int len = s.length();
		
		int bIndex = getFirstNonSymbolIndex(cs);
		
		if (bIndex == len)
		{
			addSymbols(tokens, s);
			return;
		}
		
		int eIndex = getLastSymbolSequenceIndex(cs);
		List<int[]> indices = new ArrayList<>();
		
		indices.add(new int[]{0, bIndex});
		addNextSymbolSequenceIndices(indices, cs, bIndex+1, eIndex-1);
		indices.add(new int[]{eIndex, len});
		
		tokenizeSymbolsAux(tokens, s, cs, indices);
	}
	
	/**
	 * @return {@code 0} if no character in {@code cs} is symbol.
	 * @return {@code cs.length} if all characters in {@code cs} are symbols.  
	 * Called by {@link #tokenizeSymbols(List, String)}.
	 */
	private int getFirstNonSymbolIndex(char[] cs)
	{
		int i, len = cs.length;
		
		for (i=0; i<len; i++)
		{
			if (!isSymbol(cs[i]))
				return i;
		}
		
		return i;
	}
	
	/**
	 * @return {@code cs.length} if no character in {@code cs} is symbol.
	 * @return {@code 0} if all characters in {@code cs} are symbols.  
	 * Called by {@link #tokenizeSymbols(List, String)}.
	 */
	private int getLastSymbolSequenceIndex(char[] cs)
	{
		int i;
		
		for (i=cs.length-1; i>=0; i--)
		{
			if (!isSymbol(cs[i]))
				return i+1;
		}
		
		return i+1;
	}
	
	/** Called by {@link #tokenizeSymbols(List, String)}. */
	private void addNextSymbolSequenceIndices(List<int[]> indices, char[] cs, int bIndex, int eIndex)
	{
		int i, j;
		
		for (i=bIndex; i<eIndex; i++)
		{
			if (preserveSymbolInBetween(cs, i) || preserveSymbolInDigits(cs, i) || preserveSymbolInAlphabets(cs, i))
				continue;
		
			if (isEllipsis(cs, i) || isSymbolInBetween(cs[i]) || (i+1<eIndex && isSymbolInBetween(cs[i+1]) && CharUtils.isFinalMark(cs[i])))
			{
				j = getSpanIndex(cs, i, eIndex, false);
				indices.add(new int[]{i, j});
				i = j - 1;
			}
		}
	}
	
	/** Called by {@link #tokenizeSymbols(List, String)}. */
	private void tokenizeSymbolsAux(List<String> tokens, String s, char[] cs, List<int[]> indices)
	{
		int i, pg, ng, bIndex, eIndex, size = indices.size() - 1;
		boolean pb, nb;
		int[] pi, ni;
		String t;
		
		for (i=0; i<size; i++)
		{
			pi = indices.get(i);
			ni = indices.get(i+1);
			
			bIndex = pi[1];
			eIndex = ni[0];
			
			if (bIndex < eIndex)
			{
				t  = s.substring(bIndex, eIndex);
				pg = pi[1] - pi[0];
				ng = ni[1] - ni[0];
				
				pb = (i == 0) ? pg > 0 : pg == 1;
				nb = (i+1 == size) ? ng > 0 : ng == 1;
				
				if (pb) pi[1] = adjustFirstNonSymbolIndex(cs, bIndex, t);
				if (nb) ni[0] = adjustLastSymbolSequenceIndex(cs, eIndex, t);
			}
		}
		
		for (i=0; i<size; i++)
		{
			pi = indices.get(i);
			ni = indices.get(i+1);
			
			bIndex = pi[0];
			eIndex = pi[1];
			
			if (bIndex < eIndex)
			{
				t = s.substring(bIndex, eIndex);
				if (i == 0) addSymbols(tokens, t);
				else		tokens.add(t);
			}
			
			bIndex = pi[1];
			eIndex = ni[0];
			
			if (bIndex < eIndex)
			{
				t = s.substring(bIndex, eIndex);
				addMorphemes(tokens, t);
			}
		}
		
		ni = indices.get(size);
		bIndex = ni[0];
		eIndex = ni[1];
		
		if (bIndex < eIndex)
			addSymbols(tokens, s.substring(bIndex, eIndex));
	}
	
	/** Called by {@link #tokenizeSymbolsAux(List, String, char[], List)}. */
	private int adjustFirstNonSymbolIndex(char[] cs, int beginIndex, String t)
	{
		char sym = cs[beginIndex-1], curr = cs[beginIndex];
		int gap;
		
		if ((gap = adjustFirstNonSymbolGap(cs, beginIndex, t)) > 0)
		{
			beginIndex -= gap;
		}
		else if (CharUtils.isPreDigitSymbol(sym))
		{
			if (CharUtils.isDigit(curr)) beginIndex--;		// -1, .1, +1
		}
		else if ((sym == CharConst.AT || sym == CharConst.POUND))
		{
			if (CharUtils.isAlphabet(curr)) beginIndex--;	// @A, #A
		}
		else if (CharUtils.isApostrophe(sym))
		{
			if (P_YEAR.matcher(t).find()) beginIndex--;
		}
			
		return beginIndex;
	}
	
	/** Called by {@link #tokenizeSymbolsAux(List, String, char[], List)}. */
	protected int adjustLastSymbolSequenceIndex(char[] cs, int endIndex, String t)
	{
		String lower = StringUtils.toLowerCase(t);
		char sym = cs[endIndex];
		int gap;
		
		if ((gap = adjustLastSymbolSequenceGap(cs, endIndex, t)) > 0)
		{
			endIndex += gap;
		}
		else if (sym == CharConst.DOLLAR)
		{
			if (d_currency.isCurrencyDollar(lower)) endIndex++;
		}
		else if (sym == CharConst.PERIOD)
		{
			if (preservePeriod(cs, endIndex, t)) endIndex++;
		}
		
		return endIndex;
	}
	
	/** Called by {@link #adjustFirstNonSymbolIndex(char[], int, String)}. */
	abstract protected int adjustFirstNonSymbolGap(char[] cs, int beginIndex, String t);
	/** Called by {@link #adjustLastSymbolSequenceIndex(char[], int, String)}. */
	abstract protected int adjustLastSymbolSequenceGap(char[] cs, int endIndex, String t);
	
//	----------------------------------- Add symbols -----------------------------------
	
	/** Called by {@link #tokenizeSymbols(List, String)}. */
	private void addSymbols(List<String> tokens, String s)
	{
		if (s.length() == 1)
		{
			tokens.add(s);
			return;
		}
		
		int i, j, flag, len = s.length(), bIndex = 0;
		char[] cs = s.toCharArray();
		
		for (i=0; i<len; i=j)
		{
			flag = getSymbolFlag(cs[i]);
			j = getSpanIndex(cs, i, len, flag == 1);
					
			if (0 < flag || i+1 < j)
			{
				if (bIndex < i) tokens.add(s.substring(bIndex, i));
				tokens.add(s.substring(i, j));
				bIndex = j;
			}
		}
		
		if (bIndex < len)
			tokens.add(s.substring(bIndex));
	}
	
	/**
	 * @return the right-most index in the span (exclusive).
	 * Called by {@link #addSymbols(List, String)}.
	 */
	private int getSpanIndex(char[] cs, int index, int rightBound, boolean finalMark)
	{
		char c = cs[index];
		int i;
		
		for (i=index+1; i<rightBound; i++)
		{
			if (!isConsecutive(cs, i, c, finalMark))
				return i;
		}
		
		return i;
	}
	
//	/**
//	 * @return the left-most index in the span (inclusive).  
//	 * Called by {@link #addSymbols(List, String)}.
//	 */
//	private int getSpanIndexRL(char[] cs, int index, int leftBound, boolean finalMark)
//	{
//		char c = cs[index];
//		int i;
//		
//		for (i=index-1; i>leftBound; i--)
//		{
//			if (!isConsecutive(cs, i, c, finalMark))
//				return i+1;
//		}
//		
//		return i+1;
//	}
	
//	----------------------------------- Add morphmes -----------------------------------
	
	/** Called by {@link #tokenizeSymbols(List, String)}. */
	private void addMorphemes(List<String> tokens, String s)
	{
		if (s.length() == 1)
		{
			tokens.add(s);
			return;
		}
		
		char[] lcs = s.toCharArray();
		String lower = CharUtils.toLowerCase(lcs) ? new String(lcs) : s;
		
		if (!tokenize(tokens, s, lower, lcs, d_currency) && !tokenize(tokens, s, lower, lcs, d_unit) && !tokenizeDigit(tokens, s, lcs) && !tokenizeWordsMore(tokens, s, lower, lcs))
			tokens.add(s);
	}
	
	/** Called by {@link #addMorphemes(List, String)}. */
	protected boolean tokenize(List<String> tokens, String original, String lower, char[] lcs, Dictionary tokenizer)
	{
		String[] t = tokenizer.tokenize(original, lower, lcs);
		
		if (t != null)
		{
			DSUtils.addAll(tokens, t);
			return true;
		}
		
		return false;
	}
	
	/** Called by {@link #addMorphemes(List, String)}. */
	private boolean tokenizeDigit(List<String> tokens, String original, char[] lcs)
	{
		int len = lcs.length;
		if (len < 2) return false;
		
		if (tokenizeDigitAux(lcs[0]) && CharUtils.containsDigitPunctuationOnly(lcs, 1, len))
		{
			tokens.add(original.substring(0, 1));
			tokens.add(original.substring(1));
			return true;
		}
		
		len--;
		
		if (tokenizeDigitAux(lcs[len]) && CharUtils.containsDigitPunctuationOnly(lcs, 0, len))
		{
			tokens.add(original.substring(0, len));
			tokens.add(original.substring(len));
			return true;
		}
		
		return false;
		
	}
	
	/** {@link #tokenizeDigit(List, String, char[])}. */
	private boolean tokenizeDigitAux(char c)
	{
		return c == CharConst.POUND || c == CharConst.DOLLAR || c == CharConst.PERCENT || c == CharConst.ASTERISK || c == CharConst.EQUAL;
	}
	
	/** Called by {@link #addMorphemes(List, String)}. */
	abstract protected boolean tokenizeWordsMore(List<String> tokens, String original, String lower, char[] lcs);
	
//	----------------------------------- Finalize -----------------------------------
	
	/** Called by {@link #tokenize(String)}. */
	private void finalize(List<String> tokens)
	{
		int i, j, size = tokens.size();
		String token, lower;
		
		for (i=0; i<size; i++)
		{
			token = tokens.get(i);
			lower = StringUtils.toLowerCase(token);
			
			if ((j = tokenizeNo(tokens, token, lower, i)) != 0 || (mergeParenthesis(tokens, token, i)) != 0)
			{
				size = tokens.size();
				i += j;
			}
		}
		
		if (tokens.size() == 1) tokenizeLastPeriod(tokens);
	}
	
	/** Called by {@link #finalize()}. */
	private int tokenizeNo(List<String> tokens, String token, String lower, int index)
	{
		if (lower.equals("no.") && (index+1 == tokens.size() || !CharUtils.isDigit(tokens.get(index+1).charAt(0))))
		{
			tokens.set(index  , StringUtils.trim(token, 1));
			tokens.add(index+1, StringConst.PERIOD);
			return 1;
		}
		
		return 0;
	}
	
	/** Called by {@link #finalize()}. */
	private int mergeParenthesis(List<String> tokens, String token, int index)
	{
		if (token.length() == 1 && 0 <= index-1 && index+1 < tokens.size())
		{
			String prev = tokens.get(index-1);
			String next = tokens.get(index+1);
			
			if (prev.equals(StringConst.LRB) && next.equals(StringConst.RRB))
			{
				tokens.set(index-1, prev+token+next);
				tokens.remove(index);
				tokens.remove(index);
				return -1;
			}
		}
		
		return 0;
	}
	
	/** Called by {@link #finalize()}. */
	private void tokenizeLastPeriod(List<String> tokens)
	{
		int last = tokens.size() - 1;
		String token = tokens.get(last);
		char[] cs = token.toCharArray();
		int len = token.length();
		
		if (1 < len && cs[len-1] == CharConst.PERIOD && !CharUtils.isFinalMark(cs[len-2]))
		{
			tokens.set(last, StringUtils.trim(token, 1));
			tokens.add(StringConst.PERIOD);
		}
	}
	
//	----------------------------------- Preserve -----------------------------------
	
	/** Called by {@link #addNextSymbolSequenceIndices(List, char[], int, int)}. */
	abstract protected boolean preserveSymbolInBetween(char[] cs, int index);
	
	/** Called by {@link #addMorphemes(List, String)}. */
	private boolean preserveSymbolInDigits(char[] cs, int index)
	{
		char c = cs[index];
		
		if (CharUtils.isHyphen(c))
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isAlnum(cs[index-1]) && CharUtils.isDigit(cs[index+1]);
		else if (c == CharConst.FW_SLASH)
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]);
		else if (cs[index] == CharConst.COMMA)
			return (0 <= index-1 && index+3 < cs.length) && (index+4 == cs.length || !CharUtils.isDigit(cs[index+4])) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]) && CharUtils.isDigit(cs[index+2]) && CharUtils.isDigit(cs[index+3]);
		
		return false;
	}
	
	/** Called by {@link #addMorphemes(List, String)}. */
	private boolean preserveSymbolInAlphabets(char[] cs, int index)
	{
		char c = cs[index];
		
		if (c == CharConst.AMPERSAND)
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isAlphabet(cs[index-1]) && CharUtils.isAlphabet(cs[index+1]);
		
		return false;
	}
	
	/** Called by {@link #adjustLastSymbolSequenceGap(char[], int, String)}. */
	private boolean preservePeriod(char[] cs, int endIndex, String t)
	{
		if (endIndex+1 < cs.length)
		{
			char c = cs[endIndex+1];
			
			if (CharUtils.isSeparatorMark(c))
				return true;
			
			if (CharUtils.isFinalMark(c) || CharUtils.isQuotationMark(c))
				return false;
		}
		
		if (P_ABBREVIATION.matcher(t).find())
			return true;
		
		int len = t.length();
		return (2 <= len && len <= 5) && CharUtils.containsOnlyConsonants(t);
	}
	
//	----------------------------------- Boolean -----------------------------------
	
	/** Called by {@link #getFirstNonSymbolIndex(char[])} and {@link #getLastSymbolSequenceIndex(char[])}. */
	private boolean isSymbol(char c)
	{
		return CharUtils.isPunctuation(c) ||
			   CharUtils.isGeneralPunctuation(c) ||
			   CharUtils.isCurrency(c) ||
			   CharUtils.isArrow(c);
	}
	
	/** Called by {@link #addNextSymbolSequenceIndices(List, char[], int, int)}. */
	private boolean isEllipsis(char[] cs, int index)
	{
		if (cs[index] == CharConst.PERIOD && index+1 < cs.length)
		{
			char c = cs[index+1];
			return CharUtils.isFinalMark(c) || CharUtils.isSeparatorMark(c) || CharUtils.isQuotationMark(c);
		}
		
		return false;
	}
	
	/** Called by {@link #addNextSymbolSequenceIndices(List, char[], int, int)}. */
	private boolean isSymbolInBetween(char c)
	{
		return CharUtils.isBracket(c) || CharUtils.isArrow(c) || CharUtils.isDoubleQuotationMark(c) || CharUtils.isHyphen(c) || S_SYMBOL_IN_BETWEEN.contains(c);
	}
	
	/** Called by {@link #getSpanIndex(char[], int, int, boolean)}. */
	private boolean isConsecutive(char[] cs, int index, char c, boolean finalMark)
	{
		return finalMark ? CharUtils.isFinalMark(cs[index]) : c == cs[index];
	}
	
	/** Called by {@link #addSymbols(List, String)}. */
	private int getSymbolFlag(char c)
	{
		if (CharUtils.isFinalMark(c))
			return 1;
		else if (CharUtils.isBracket(c) || CharUtils.isSeparatorMark(c) || CharUtils.isQuotationMark(c) || c == CharConst.PRIME)
			return 2;
		else
			return 0;
	}
	
	protected boolean isFinalMarksOnly(String s)
	{
		for (char c : s.toCharArray())
		{
			if (!CharUtils.isFinalMark(c))
				return false;
		}
		
		return true;
		
	}
}
