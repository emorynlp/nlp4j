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
package edu.emory.mathcs.nlp.tokenization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishTokenizerTest
{
	@Test
	public void testPostProcessing()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		
		s = "1234-56 12-3456 12-34 12-34s 123-4567 1234-567";
		r = "[1234, -, 56, 12, -, 3456, 12, -, 34, 12, -, 34s, 123-4567, 1234-567]";
		test(t, s, r, new int[]{0, 4, 4, 5, 5, 7, 8, 10, 10, 11, 11, 15, 16, 18, 18, 19, 19, 21, 22, 24, 24, 25, 25, 28, 29, 37, 38, 46});
		
		s = "A.B.C";
		r = "[A.B.C]";
		test(t, s, r);
		
		s = "Mbaaah!hello prize.Please";
		r = "[Mbaaah, !, hello, prize, ., Please]";
		test(t, s, r, new int[]{0, 6, 6, 7, 7, 12, 13, 18, 18, 19, 19, 25});
	}
	
	@Test
	public void testPeriods()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		
		s = "500 million of 1986.[11]";
		r = "[500, million, of, 1986, ., [, 11, ]]";
		test(t, s, r);
		
		s = "injury-related deaths worldwide.[6]";
		r = "[injury, -, related, deaths, worldwide, ., [, 6, ]]";
		test(t, s, r);
	}

	@Test
	public void test()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		
		// white-spaces
		s = " \n\t\n\r\f";
		r = "[]";
		test(t, s, r);
		
		s = " A B  C\n D \t\nE\r\f ";
		r = "[A, B, C, D, E]";
		test(t, s, r);
		
		// hyperlinks
		s = "|http://www.clearnlp.com|www.clearnlp.com|mailto:support@clearnlp.com|jinho_choi@clearnlp.com|";
		r = "[|, http://www.clearnlp.com, |, www.clearnlp.com, |, mailto:support@clearnlp.com, |, jinho_choi@clearnlp.com, |]";
		test(t, s, r);

		// emoticons
		s = ":-))) :---( Hi:).";
		r = "[:-))), :---(, Hi, :), .]";
		test(t, s, r);
		
		// surrounding symbols
		s = "---\"((``@#$Choi%&*''))\".?!===";
		r = "[---, \", ((, ``, @#$, Choi, %&*, '', )), \", .?!, ===]";
		test(t, s, r);
		
		// in-between symbols
		s = ",,A---C**D~~~~E==F,G,,H..I.J-1.--2-K||L-#3";
		r = "[,,, A, ---, C**D, ~~~~, E, ==, F, ,, G, ,,, H, .., I.J-1., --, 2, -, K, ||, L, -, #, 3]";
		test(t, s, r);

		// brackets
		s = "(1){2}[3]<4>";
		r = "[(1), {, 2, }, [, 3, ], <, 4, >]";
		test(t, s, r);
		
		// twitter tags
		s = "@UserID #HashTag";
		r = "[@UserID, #HashTag]";
		test(t, s, r);

		// abbreviations
		s = "Dr. ph.d. w.r.t. 1.2. A-1. a.1 (e.g., bcd. BCD. and. T. T.. T.";
		r = "[Dr., ph.d., w.r.t., 1.2., A-1., a.1, (, e.g., ,, bcd., BCD., and, ., T., T, .., T.]";
		test(t, s, r);
		
		// symbols in numbers
		s = ".1,-2.3,+4,567,8:9\"0\" -1+2=1 +82-2-000-0000 12/25/2014";
		r = "[.1, ,, -2.3, ,, +4,567, ,, 8:9, \", 0, \", -1, +2, =, 1, +82-2-000-0000, 12/25/2014]";
		test(t, s, r);		
		
		// currency
		s = "$1 E2 L3 USD1 2KPW $1 USD1 us$ US$ ub$";
		r = "[$, 1, E2, L3, USD, 1, 2, KPW, $, 1, USD, 1, us$, US$, ub, $]";
		test(t, s, r);
		
		// unit
		s = "1m 2mm 3kg 4oz 1D 2nM 3CM 4LB";
		r = "[1, m, 2, mm, 3, kg, 4, oz, 1, D, 2, nM, 3, CM, 4, LB]";
		test(t, s, r);

		// apostrophe
		s = "he's we'd I'm you'll they're I've didn't did'nt he'S DON'T gue'ss he'mm 90's";
		r = "[he, 's, we, 'd, I, 'm, you, 'll, they, 're, I, 've, did, n't, did, 'nt, he, 'S, DO, N'T, gue'ss, he'mm, 90's]";
		test(t, s, r);

		// compounds
		s = "aint cannot don'cha d'ye i'mma dunno lemme LEMME";
		r = "[ai, nt, can, not, do, n', cha, d', ye, i, 'm, ma, du, n, no, lem, me, LEM, ME]";
		test(t, s, r);
		
		// hyphens
		s = "dis-able cross-validation o-kay art-o-torium s-e-e art-work DIS-ABLE CROSS-VALIDATION";
		r = "[dis-able, cross-validation, o-kay, art-o-torium, s-e-e, art, -, work, DIS-ABLE, CROSS-VALIDATION]";
		test(t, s, r);

		// years
		s = "'90 '90s '90's '100's";
		r = "['90, '90s, '90's, ', 100's]";
		test(t, s, r);
		
		// ampersand
		s = "AT&T at&t A&1";
		r = "[AT&T, at&t, A, &, 1]";
		test(t, s, r);
		
		// no.
		s = "No. 5 No.";
		r = "[No., 5, No, .]";
		test(t, s, r);

		// more examples
		s = "\"John & Mary's dog,\" Jane thought (to herself).\n" + "\"What a #$%!\n" + "a- ``I like AT&T''.\"";
		r = "[\", John, &, Mary, 's, dog, ,, \", Jane, thought, (, to, herself, ), ., \", What, a, #$%, !, a, -, ``, I, like, AT&T, '', ., \"]";
		test(t, s, r);
		
		s = "I said at 4:45pm.";
		r = "[I, said, at, 4:45, pm, .]";
		test(t, s, r);
		
		s = "I can't believe they wanna keep 40% of that. ``Whatcha think?'' \"I don't --- think so...,\"";
		r = "[I, ca, n't, believe, they, wan, na, keep, 40, %, of, that, ., ``, What, cha, think, ?, '', \", I, do, n't, ---, think, so, ..., ,, \"]";
		test(t, s, r);
		
		s = "You `paid' US$170,000?!\nYou should've paid only $16.75.";
		r = "[You, `, paid, ', US$, 170,000, ?!, You, should, 've, paid, only, $, 16.75, .]";
		test(t, s, r);
		
		s = " 1. Buy a new Chevrolet (37%-owned in the U.S.) . 15%";
		r = "[1., Buy, a, new, Chevrolet, (, 37, %, -, owned, in, the, U.S., ), ., 15, %]";
		test(t, s, r);
	}
	
	private List<String> getTokenStrings(List<Token> tokens)
	{
		List<String> tokenStrings = new ArrayList<String>();
		for(int index = 0; index < tokens.size(); index++)
		{
			tokenStrings.add(tokens.get(index).getWordForm());
		}
		return tokenStrings;
	}
	
	private List<Token> test(Tokenizer t, String s, String r)
	{
		List<Token> tokens = t.tokenize(s);
		List<String> tokenStrings = getTokenStrings(tokens);
		assertEquals(r, tokenStrings.toString());
		return tokens;
	}
	
	private void test(Tokenizer t, String s, String r, int[] offsets)
	{
		List<Token> tokens = test(t, s, r);
		Token token;
		
		for (int i=0; i<tokens.size(); i++)
		{
			token = tokens.get(i);
//			System.out.println(token.getWordForm()+" "+token.getStartOffset()+" "+token.getEndOffset());
			assertEquals(offsets[i*2], token.getStartOffset());
			assertEquals(offsets[i*2+1], token.getEndOffset());
		}
	}
}
