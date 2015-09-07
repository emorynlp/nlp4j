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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishTokenizerTest
{
	@Test
	public void testPeriods()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		
		s = "500 million of 1986.[11]";
		r = "[500, million, of, 1986, ., [, 11, ]]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = "injury-related deaths worldwide.[6]";
		r = "[injury, -, related, deaths, worldwide, ., [, 6, ]]";
		assertEquals(r, t.tokenize(s).toString());
	}
	
	@Test
	public void test()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		
		// white-spaces
		s = " \n\t\n\r\f";
		r = "[]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = " A B  C\n D \t\nE\r\f ";
		r = "[A, B, C, D, E]";
		assertEquals(r, t.tokenize(s).toString());
		
		// hyperlinks
		s = "|http://www.clearnlp.com|www.clearnlp.com|mailto:support@clearnlp.com|jinho_choi@clearnlp.com|";
		r = "[|, http://www.clearnlp.com, |, www.clearnlp.com, |, mailto:support@clearnlp.com, |, jinho_choi@clearnlp.com, |]";
		assertEquals(r, t.tokenize(s).toString());

		// emoticons
		s = ":-))) :---( Hi:).";
		r = "[:-))), :---(, Hi, :), .]";
		assertEquals(r, t.tokenize(s).toString());
		
		// surrounding symbols
		s = "---\"((``@#$Choi%&*''))\".?!===";
		r = "[---, \", ((, ``, @#$, Choi, %&*, '', )), \", .?!, ===]";
		assertEquals(r, t.tokenize(s).toString());
		
		// in-between symbols
		s = ",,A---C**D~~~~E==F,G,,H..I.J-1.--2-K||L-#3";
		r = "[,,, A, ---, C**D, ~~~~, E, ==, F, ,, G, ,,, H, .., I.J-1., --, 2, -, K, ||, L, -, #, 3]";
		assertEquals(r, t.tokenize(s).toString());

		// brackets
		s = "(1){2}[3]<4>";
		r = "[(1), {, 2, }, [, 3, ], <, 4, >]";
		assertEquals(r, t.tokenize(s).toString());
		
		// twitter tags
		s = "@UserID #HashTag";
		r = "[@UserID, #HashTag]";
		assertEquals(r, t.tokenize(s).toString());

		// abbreviations
		s = "Dr. ph.d. w.r.t. 1.2. A-1. a.1 (e.g., bcd. BCD. and. T. T.. T.";
		r = "[Dr., ph.d., w.r.t., 1.2., A-1., a.1, (, e.g., ,, bcd., BCD., and, ., T., T, .., T.]";
		assertEquals(r, t.tokenize(s).toString());
		
		// symbols in numbers
		s = ".1,-2.3,+4,567,8:9\"0\" -1+2=1 +82-2-000-0000 12/25/2014";
		r = "[.1, ,, -2.3, ,, +4,567, ,, 8:9, \", 0, \", -1, +2, =, 1, +82-2-000-0000, 12/25/2014]";
		assertEquals(r, t.tokenize(s).toString());		
		
		// currency
		s = "$1 E2 L3 USD1 2KPW $1 USD1 us$ US$ ub$";
		r = "[$, 1, E2, L3, USD, 1, 2, KPW, $, 1, USD, 1, us$, US$, ub, $]";
		assertEquals(r, t.tokenize(s).toString());
		
		// unit
		s = "1m 2mm 3kg 4oz 1D 2nM 3CM 4LB";
		r = "[1, m, 2, mm, 3, kg, 4, oz, 1, D, 2, nM, 3, CM, 4, LB]";
		assertEquals(r, t.tokenize(s).toString());

		// apostrophe
		s = "he's we'd I'm you'll they're I've didn't did'nt he'S DON'T gue'ss he'mm 90's";
		r = "[he, 's, we, 'd, I, 'm, you, 'll, they, 're, I, 've, did, n't, did, 'nt, he, 'S, DO, N'T, gue'ss, he'mm, 90's]";
		assertEquals(r, t.tokenize(s).toString());

		// compounds
		s = "aint cannot don'cha d'ye i'mma dunno lemme LEMME";
		r = "[ai, nt, can, not, do, n', cha, d', ye, i, 'm, ma, du, n, no, lem, me, LEM, ME]";
		assertEquals(r, t.tokenize(s).toString());
		
		// hyphens
		s = "dis-able cross-validation o-kay art-o-torium s-e-e art-work DIS-ABLE CROSS-VALIDATION";
		r = "[dis-able, cross-validation, o-kay, art-o-torium, s-e-e, art, -, work, DIS-ABLE, CROSS-VALIDATION]";
		assertEquals(r, t.tokenize(s).toString());

		// years
		s = "'90 '90s '90's '100's";
		r = "['90, '90s, '90's, ', 100's]";
		assertEquals(r, t.tokenize(s).toString());
		
		// ampersand
		s = "AT&T at&t A&1";
		r = "[AT&T, at&t, A, &, 1]";
		assertEquals(r, t.tokenize(s).toString());
		
		// no.
		s = "No. 5 No.";
		r = "[No., 5, No, .]";
		assertEquals(r, t.tokenize(s).toString());

		// more examples
		s = "\"John & Mary's dog,\" Jane thought (to herself).\n" + "\"What a #$%!\n" + "a- ``I like AT&T''.\"";
		r = "[\", John, &, Mary, 's, dog, ,, \", Jane, thought, (, to, herself, ), ., \", What, a, #$%, !, a, -, ``, I, like, AT&T, '', ., \"]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = "I said at 4:45pm.";
		r = "[I, said, at, 4:45, pm, .]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = "I can't believe they wanna keep 40% of that. ``Whatcha think?'' \"I don't --- think so...,\"";
		r = "[I, ca, n't, believe, they, wan, na, keep, 40, %, of, that, ., ``, What, cha, think, ?, '', \", I, do, n't, ---, think, so, ..., ,, \"]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = "You `paid' US$170,000?!\nYou should've paid only $16.75.";
		r = "[You, `, paid, ', US$, 170,000, ?!, You, should, 've, paid, only, $, 16.75, .]";
		assertEquals(r, t.tokenize(s).toString());
		
		s = " 1. Buy a new Chevrolet (37%-owned in the U.S.) . 15%";
		r = "[1., Buy, a, new, Chevrolet, (, 37, %, -, owned, in, the, U.S., ), ., 15, %]";
		assertEquals(r, t.tokenize(s).toString());
	}
}
