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
package edu.emory.mathcs.nlp.component.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.magicwerk.brownies.collections.GapList;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.MetaUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Currency;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Dictionary;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Unit;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;
import edu.emory.mathcs.nlp.component.tokenizer.token.TokenIndex;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class Tokenizer
{
	protected final CharSet S_SYMBOL_IN_BETWEEN = new CharOpenHashSet(new char[]{CharConst.SEMICOLON, CharConst.COMMA, CharConst.TILDA, CharConst.EQUAL, CharConst.PLUS, CharConst.AMPERSAND, CharConst.PIPE, CharConst.FW_SLASH});
	protected final Pattern P_ABBREVIATION = PatternUtils.createClosedPattern("\\p{Alnum}([\\.|-]\\p{Alnum})*");
	protected final Pattern P_YEAR = PatternUtils.createClosedPattern("\\d\\d['\u2019]?[sS]?");
	protected final Pattern P_YEAR_YEAR = PatternUtils.createClosedPattern("(\\d{2}|\\d{4})(-)(\\d{2}|\\d{4}|\\d{2}[sS])");

	protected Emoticon    d_emoticon;
	protected Currency    d_currency;
	protected Unit        d_unit;
	protected Set<String> d_preserve;

	public Tokenizer()
	{
		d_emoticon = new Emoticon();
		d_currency = new Currency();
		d_unit     = new Unit();
		d_preserve = initPreserve();
	}

	private Set<String> initPreserve()
	{
		BufferedReader reader = IOUtils.createBufferedReader(IOUtils.getInputStreamsFromResource(Dictionary.ROOT+"preserve.txt"));
		Set<String> set = new HashSet<>();
		String line;

		try
		{
			while ((line = reader.readLine()) != null)
				set.add(line.trim());
		}
		catch (IOException e) {e.printStackTrace();}

		return set;
	}

//	============================== Public ==============================

	/** @return a list of tokens in the specific input stream. */
	public List<Token> tokenize(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		ArrayList<Token> tokens = new ArrayList<>();
		List<Token> t;
		String line;
		int start = 0;
        int end = 0;
        boolean flag = false;

		try
		{
			while ((line = reader.readLine()) != null)
			{
			    if (flag)
                {
			    	// assigning the start and end offset to all the lines except first line
                    start = end + System.getProperty("line.separator").length();
                    end = start + line.length();
                }
                else
                {
                    start = 0;
                    end = line.length();
                    flag = true;
                }

			    t = tokenizeWhiteSpaces(line, start);
				if (!t.isEmpty()) tokens.addAll(t);
			}

			reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
		tokens.trimToSize();

		return tokens;
	}

	/** @return a list of tokens in the specific string. */
	public List<Token> tokenize(String s)
	{
        return tokenizeWhiteSpaces(s, 0);
    }

	public List<List<Token>> segmentize(InputStream in)
	{
		return segmentize(tokenize(in));
	}

	public List<List<Token>> segmentize(String s)
	{
		return segmentize(tokenize(s));
	}
	
	/** @param flag 0: word-form, 1: simplified word-form, 2: decapitalized simplified word-form. */
	public void tokenizeLine(InputStream in, PrintStream out, String delim, int flag)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				List<Token> tokens = tokenize(line);
				
				if (flag > 0)
				{
					for (Token token : tokens)
					{
						String s = token.getWordForm();
						
						switch (flag)
						{
						case 1: s = StringUtils.toSimplifiedForm(s, false); break;
						case 2: s = StringUtils.toSimplifiedForm(s, true);  break;
						}
						
						token.setWordForm(s);
					}	
				}
				
				line = Joiner.join(tokens, delim);
				out.println(line);
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}

	abstract public <T extends Token>List<List<T>> segmentize(List<T> tokens);

//	============================== Tokenize ==============================

	/**
	 * Tokenizes white spaces.
	 * Called by {@link #tokenize(InputStream)} and {@link #tokenize(String)}.
	 */
	private List<Token> tokenizeWhiteSpaces(String s, int start)
	{
	    List<Token> tokens = new GapList<>();
	    int i, len = s.length(), bIndex = start;
		char[] cs = s.toCharArray();

		for (i = start; i < start + len; i++)
		{
			if (CharUtils.isWhiteSpace(cs[i - start]))
			{
				if (bIndex < i) tokenizeMetaInfo(tokens, s.substring(bIndex - start, i - start), bIndex, i);
				bIndex = i + 1;
			}
		}

		if (bIndex < start + len) tokenizeMetaInfo(tokens, s.substring(bIndex - start), bIndex, len - bIndex + start);
		if (!tokens.isEmpty()) finalize(tokens, s);

		return tokens;
	}

	/**
	 * Tokenizes hyperlinks, emoticons.
	 * Called by {@link #tokenizeAux(String)}.
	 */
	private void tokenizeMetaInfo(List<Token> tokens, String s, int bIndex2, int i)
	{
		int[] ps;
		TokenIndex bIndex3 = new TokenIndex(bIndex2);
		if ((ps = getMetaRange(s)) != null)
		{
			int bIndex = ps[0], eIndex = ps[1], len = s.length();

			if (0 < bIndex) tokenizeSymbols(tokens, s.substring(0, bIndex), bIndex3);
            Token Token = new Token(s.substring(bIndex, eIndex), bIndex3.getVal(), bIndex3.getVal() + eIndex - bIndex);
            tokens.add(Token);
            bIndex3.setVal(bIndex3.getVal() + eIndex - bIndex);
			if (eIndex < len) tokenizeSymbols(tokens, s.substring(eIndex), bIndex3);
		}
		else
			tokenizeSymbols(tokens, s, bIndex3);
	}

	/** Called by {@link #tokenizeMetaInfo(List, String)}. */
	private int[] getMetaRange(String s)
	{
		if (MetaUtils.startsWithNetworkProtocol(s) || d_preserve.contains(s))
			return new int[]{0, s.length()};

		int[] ps;

		if ((ps = d_emoticon.getEmoticonRange(s)) != null)
			return ps;

		Matcher m = MetaUtils.HYPERLINK.matcher(s);

		if (m.find())
			return new int[]{m.start(), m.end()};

		return null;
	}

	/** Called by {@link #tokenizeMetaInfo(List, String)}. */
	private void tokenizeSymbols(List<Token> tokens, String s, TokenIndex bIndex2)
	{
		char[] cs = s.toCharArray();
		int len = s.length();

		int bIndex = getFirstNonSymbolIndex(cs);

		if (bIndex == len)
		{
			addSymbols(tokens, s, bIndex2);
			return;
		}

		int eIndex = getLastSymbolSequenceIndex(cs);
		List<int[]> indices = new ArrayList<>();

		indices.add(new int[]{0, bIndex});
		addNextSymbolSequenceIndices(indices, cs, bIndex+1, eIndex-1);
		indices.add(new int[]{eIndex, len});

		tokenizeSymbolsAux(tokens, s, cs, indices, bIndex2);
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
	private void tokenizeSymbolsAux(List<Token> tokens, String s, char[] cs, List<int[]> indices, TokenIndex bIndex2)
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
                if (i == 0)
                    bIndex2.setVal(addSymbols(tokens, t, bIndex2));
                else
                {
                    Token Token = new Token(t, bIndex2.getVal(), bIndex2.getVal() + t.length());
                    tokens.add(Token);
                    bIndex2.setVal(bIndex2.getVal() + t.length());
                }
            }

			bIndex = pi[1];
			eIndex = ni[0];

			if (bIndex < eIndex)
			{
                t = s.substring(bIndex, eIndex);
                bIndex2.setVal(addMorphemes(tokens, t, bIndex2));
            }
		}

		ni = indices.get(size);
		bIndex = ni[0];
		eIndex = ni[1];

		if (bIndex < eIndex)
		    bIndex2.setVal(addSymbols(tokens, s.substring(bIndex, eIndex), bIndex2));
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
	private int addSymbols(List<Token> tokens, String s, TokenIndex bIndex2)
	{
		if (s.length() == 1)
		{
            Token Token = new Token(s, bIndex2.getVal(), bIndex2.getVal() + 1);
            tokens.add(Token);
            bIndex2.setVal(bIndex2.getVal() + 1);
            return bIndex2.getVal();
        }

		int i, j, flag, len = s.length(), bIndex = 0;
		char[] cs = s.toCharArray();

		for (i=0; i<len; i=j)
		{
			flag = getSymbolFlag(cs[i]);
			j = getSpanIndex(cs, i, len, flag == 1);

			if (0 < flag || i+1 < j)
			{
				if (bIndex < i)
				{
					Token Token = new Token(s.substring(bIndex, i), bIndex2.getVal(), bIndex2.getVal() + i - bIndex);
					tokens.add(Token);
					bIndex2.setVal(bIndex2.getVal() + i - bIndex);
				}

				Token Token = new Token(s.substring(i, j), bIndex2.getVal(), bIndex2.getVal() + j - i);
                tokens.add(Token);
                bIndex2.setVal(bIndex2.getVal() + j - i);
                bIndex = j;
			}
		}

		if (bIndex < len)
		{
			Token Token = new Token(s.substring(bIndex), bIndex2.getVal(), bIndex2.getVal() + len - bIndex);
			tokens.add(Token);
			bIndex2.setVal(bIndex2.getVal() + len - bIndex);
    }
		return bIndex2.getVal();
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
	private int addMorphemes(List<Token> tokens, String s, TokenIndex bIndex2)
	{
		if (s.length() == 1)
		{
            Token Token = new Token(s, bIndex2.getVal(), bIndex2.getVal() + 1);
            tokens.add(Token);
            bIndex2.setVal(bIndex2.getVal() + 1);
            return bIndex2.getVal();
        }

		char[] lcs = s.toCharArray();
		String lower = CharUtils.toLowerCase(lcs) ? new String(lcs) : s;

		if (!tokenize(tokens, s, lower, lcs, d_currency, bIndex2) && !tokenize(tokens, s, lower, lcs, d_unit, bIndex2) && !tokenizeDigit(tokens, s, lcs, bIndex2) && !tokenizeWordsMore(tokens, s, lower, lcs, bIndex2))
		{
            Token Token = new Token(s, bIndex2.getVal(), bIndex2.getVal() + s.length());
            tokens.add(Token);
            bIndex2.setVal(bIndex2.getVal() + s.length());
            return bIndex2.getVal();
        }

		return bIndex2.getVal();
	}

	/** Called by {@link #addMorphemes(List, String)}. */
	protected boolean tokenize(List<Token> tokens, String original, String lower, char[] lcs, Dictionary tokenizer, TokenIndex bIndex2)
	{
		String[] t = tokenizer.tokenize(original, lower, lcs);

		if (t != null)
		{
			bIndex2.setVal(addAll(tokens, t, bIndex2.getVal()));
			return true;
		}

		return false;
	}
	
	public int addAll(List<Token> tokens, String[] array, int bIndex2)
    {
        for (String item : array)
        {
            Token interval = new Token(item, bIndex2, bIndex2 + item.length());
            tokens.add(interval);
            bIndex2 = bIndex2 + item.length();
        }
        
        return bIndex2;
    }

	/** Called by {@link #addMorphemes(List, String)}. */
	private boolean tokenizeDigit(List<Token> tokens, String original, char[] lcs, TokenIndex bIndex2)
	{
		int len = lcs.length;
		if (len < 2) return false;
		
		if (tokenizeDigitAux(lcs[0]) && CharUtils.containsDigitPunctuationOnly(lcs, 1, len))
		{
            Token Token = new Token(original.substring(0, 1), bIndex2.getVal(), bIndex2.getVal() + 1);
            tokens.add(Token);
            bIndex2.setVal(bIndex2.getVal() + 1);
            Token newinterval = new Token(original.substring(1), bIndex2.getVal(), bIndex2.getVal()
									        + original.length() - 1);
            tokens.add(newinterval);
            bIndex2.setVal(bIndex2.getVal() + original.length() - 1);
            return true;
        }

		len--;

		if (tokenizeDigitAux(lcs[len]) && CharUtils.containsDigitPunctuationOnly(lcs, 0, len))
		{
            Token Token = new Token(original.substring(0, len), bIndex2.getVal(), bIndex2.getVal() + len);
            tokens.add(Token);
            bIndex2.setVal(bIndex2.getVal() + len);
            Token newinterval = new Token(original.substring(len), bIndex2.getVal(), bIndex2.getVal() + original.length() - len);
            tokens.add(newinterval);
            bIndex2.setVal(bIndex2.getVal() + original.length() - len);
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
	abstract protected boolean tokenizeWordsMore(List<Token> tokens, String original, String lower, char[] lcs, TokenIndex bIndex2);

//	----------------------------------- Finalize -----------------------------------

	/** Called by {@link #tokenize(String)}. */
	private void finalize(List<Token> tokens, String input)
	{
		int i, j, size = tokens.size();
		String token, lower;
		
		for (i=0; i<size; i++)
		{
		    token = tokens.get(i).getWordForm();
			lower = StringUtils.toLowerCase(token);

			if ((j = tokenizeNo(tokens, token, lower, i)) != 0 || (mergeBrackets(tokens, token, i, input)) != 0 || (j = tokenizeYears(tokens, token, i)) != 0 || (j = tokenizeMiddleSymbol(tokens, token, lower, i)) != 0)
			{
				size = tokens.size();
				i += j;
			}
		}

		if (tokens.size() == 1) tokenizeLastPeriod(tokens);
	}

	/** Called by {@link #finalize()}. */
	private int tokenizeNo(List<Token> tokens, String token, String lower, int index)
	{
		if (lower.equals("no.") && (index+1 == tokens.size() || !CharUtils.isDigit(tokens.get(index + 1).getWordForm().charAt(0))))
		{
            Token currToken = tokens.get(index);
            Token Token = new Token(StringUtils.trim(
			        currToken.getWordForm(), 1),
                    currToken.getStartOffset(), currToken.getEndOffset() - 1);
            tokens.set(index, Token);
            Token nextInterval = new Token(StringConst.PERIOD,
                    currToken.getEndOffset() - 1, currToken.getEndOffset());
            tokens.add(index + 1, nextInterval);
            return 1;
        }

		return 0;
	}

	/** Called by {@link #finalize()}. */
	private int mergeBrackets(List<Token> tokens, String token, int index, String input)
	{
		if ((token.length() == 1 || StringUtils.containsDigitOnly(token)) && 0 <= index-1 && index+1 < tokens.size())
		{
			Token prevToken = tokens.get(index - 1);
			Token nextToken = tokens.get(index + 1);
			
			if (CharUtils.isLeftBracket(prevToken.getWordForm().charAt(0)) && CharUtils.isRightgBracket(nextToken.getWordForm().charAt(0)))
			{
				Token currToken = tokens.get(index);
				Token Token = new Token(prevToken.getWordForm()+currToken.getWordForm()+nextToken.getWordForm(), prevToken.getStartOffset(), nextToken.getEndOffset());
				tokens.set(index - 1, Token);
				tokens.remove(index);
				tokens.remove(index);
				return -1;
			}
		}
		
		return 0;
	}
	
	private int tokenizeYears(List<Token> tokens, String token, int index)
	{
		Matcher m = P_YEAR_YEAR.matcher(token);
		return m.find() ? addTokens(m, tokens, index, 2, 3, 4) : 0;
	}
	
	protected int addTokens(Matcher m, List<Token> tokens, int index, int... ids)
	{
		Token prev, curr;
		curr = tokens.get(index);
		curr.setWordForm(m.group(ids[0]));
		curr.resetEndOffset();
		
		for (int i=1; i<ids.length; i++)
		{
			prev = curr;
			curr = new Token(m.group(ids[i]));
			curr.setStartOffset(prev.getEndOffset());
			curr.resetEndOffset();
			tokens.add(index+i, curr);
		}
		
		return ids.length-1;
	}
	
	protected abstract int tokenizeMiddleSymbol(List<Token> tokens, String token, String lower, int index);

	/** Called by {@link #finalize()}. */
	private void tokenizeLastPeriod(List<Token> tokens)
	{
	    int lastIndex = tokens.size() - 1;
        Token lastInterval = tokens.get(lastIndex);
        String lastToken = lastInterval.getWordForm();
        char[] ca = lastToken.toCharArray();
        int leng = lastToken.length();
        if (1 < leng && ca[leng - 1] == CharConst.PERIOD && !CharUtils.isFinalMark(ca[leng - 2]))
        {
            Token Token = new Token(StringUtils.trim(lastToken, 1), lastInterval.getStartOffset(), lastInterval.getEndOffset() - 1);
            tokens.set(lastIndex, Token);
            Token nextInterval = new Token(StringConst.PERIOD, lastInterval.getEndOffset() - 1, lastInterval.getEndOffset());
            tokens.add(lastIndex + 1, nextInterval);
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
	protected boolean preservePeriod(char[] cs, int endIndex, String t)
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
		if (CharUtils.isFinalMark(cs[index]) && index+1 < cs.length)
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
