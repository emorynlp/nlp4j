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

import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishTokenizerOffsetTest
{
	@Test
	public void testPeriods()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		List<NLPNode> tokens;
		List<Integer> tokenStartEnd;
		
//		System.out.println(t.tokenize("This building included: 30 toilets."));
		
		s = "500 million of 1986.[11]";
		r = "[0, 3, 4, 11, 12, 14, 15, 19, 19, 20, 20, 21, 21, 23, 23, 24]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		
		s = "injury-related deaths worldwide.[6]";
		r = "[0, 6, 6, 7, 7, 14, 15, 21, 22, 31, 31, 32, 32, 33, 33, 34, 34, 35]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		
	}

	List<String> getTokenStrings(List<NLPNode> tokens) {
		List<String> tokenStrings = new ArrayList<String>();
		for(int index = 0; index < tokens.size(); index++){
			tokenStrings.add(tokens.get(index).getWordForm());
		}
		return tokenStrings;
	}
	
	List<Integer> getTokenStartEnd(List<NLPNode> tokens) {
		List<Integer> tokenStartEnd = new ArrayList<Integer>();
		for(int index = 0; index < tokens.size(); index++){
			tokenStartEnd.add(tokens.get(index).getStartOffset());
			tokenStartEnd.add(tokens.get(index).getEndOffset());
		}
		return tokenStartEnd;
	}
	
	@Test
	public void test()
	{
		Tokenizer t = new EnglishTokenizer();
		String s, r;
		List<NLPNode> tokens;
		List<Integer> tokenStartEnd;
		
		
		// white-spaces
		s = " \n\t\n\r\f";
		r = "[]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = " A B  C\n D \t\nE\r\f ";
		r = "[1, 2, 3, 4, 6, 7, 9, 10, 13, 14]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// hyperlinks
		s = "|http://www.clearnlp.com|www.clearnlp.com|mailto:support@clearnlp.com|jinho_choi@clearnlp.com|";
		r = "[0, 1, 1, 24, 24, 25, 25, 41, 41, 42, 42, 69, 69, 70, 70, 93, 93, 94]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// emoticons
		s = ":-))) :---( Hi:).";
		r = "[0, 5, 6, 11, 12, 14, 14, 16, 16, 17]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// surrounding symbols
		s = "---\"((``@#$Choi%&*''))\".?!===";
		r = "[0, 3, 3, 4, 4, 6, 6, 8, 8, 11, 11, 15, 15, 18, 18, 20, 20, 22, 22, 23, 23, 26, 26, 29]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// in-between symbols
		s = ",,A---C**D~~~~E==F,G,,H..I.J-1.--2-K||L-#3";
		r = "[0, 2, 2, 3, 3, 6, 6, 10, 10, 14, 14, 15, 15, 17, 17, 18, 18, 19, 19, 20, 20, 22, 22, 23, 23, 25, 25, 31, 31, 33, 33, 34, 34, 35, 35, 36, 36, 38, 38, 39, 39, 40, 40, 41, 41, 42]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// brackets
		s = "(1){2}[3]<4>";
		r = "[0, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// twitter tags
		s = "@UserID #HashTag";
		r = "[0, 7, 8, 16]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// abbreviations
		s = "Dr. ph.d. w.r.t. 1.2. A-1. a.1 (e.g., bcd. BCD. and. T. T.. T.";
		r = "[0, 3, 4, 9, 10, 16, 17, 21, 22, 26, 27, 30, 31, 32, 32, 36, 36, 37, 38, 42, 43, 47, 48, 51, 51, 52, 53, 55, 56, 57, 57, 59, 60, 62]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// symbols in numbers
		s = ".1,-2.3,+4,567,8:9\"0\" -1+2=1 +82-2-000-0000 12/25/2014";
		r = "[0, 2, 2, 3, 3, 7, 7, 8, 8, 14, 14, 15, 15, 18, 18, 19, 19, 20, 20, 21, 22, 24, 24, 26, 26, 27, 27, 28, 29, 43, 44, 54]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// currency
		s = "$1 E2 L3 USD1 2KPW $1 USD1 us$ US$ ub$";
		r = "[0, 1, 1, 2, 3, 5, 6, 8, 9, 12, 12, 13, 14, 15, 15, 18, 19, 20, 20, 21, 22, 25, 25, 26, 27, 30, 31, 34, 35, 37, 37, 38]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// unit
		s = "1m 2mm 3kg 4oz 1D 2nM 3CM 4LB";
		r = "[0, 1, 1, 2, 3, 4, 4, 6, 7, 8, 8, 10, 11, 12, 12, 14, 15, 16, 16, 17, 18, 19, 19, 21, 22, 23, 23, 25, 26, 27, 27, 29]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// apostrophe
		s = "he's we'd I'm you'll they're I've didn't did'nt he'S DON'T gue'ss he'mm 90's";
		r = "[0, 2, 2, 4, 5, 7, 7, 9, 10, 11, 11, 13, 14, 17, 17, 20, 21, 25, 25, 28, 29, 30, 30, 33, 34, 37, 37, 40, 41, 44, 44, 47, 48, 50, 50, 52, 53, 55, 55, 58, 59, 65, 66, 71, 72, 76]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// compounds
		s = "aint cannot don'cha d'ye i'mma dunno lemme LEMME";
		r = "[0, 2, 2, 4, 5, 8, 8, 11, 12, 14, 14, 16, 16, 19, 20, 22, 22, 24, 25, 26, 26, 28, 28, 30, 31, 33, 33, 34, 34, 36, 37, 40, 40, 42, 43, 46, 46, 48]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// hyphens
		s = "dis-able cross-validation o-kay art-o-torium s-e-e art-work DIS-ABLE CROSS-VALIDATION";
		r = "[0, 8, 9, 25, 26, 31, 32, 44, 45, 50, 51, 54, 54, 55, 55, 59, 60, 68, 69, 85]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// years
		s = "'90 '90s '90's '100's";
		r = "[0, 3, 4, 8, 9, 14, 15, 16, 16, 21]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// ampersand
		s = "AT&T at&t A&1";
		r = "[0, 4, 5, 9, 10, 11, 11, 12, 12, 13]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// no.
		s = "No. 5 No.";
		r = "[0, 3, 4, 5, 6, 8, 8, 9]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		// more examples
		s = "\"John & Mary's dog,\" Jane thought (to herself).\n" + "\"What a #$%!\n" + "a- ``I like AT&T''.\"";
		r = "[0, 1, 1, 5, 6, 7, 8, 12, 12, 14, 15, 18, 18, 19, 19, 20, 21, 25, 26, 33, 34, 35, 35, 37, 38, 45, 45, 46, 46, 47, 48, 49, 49, 53, 54, 55, 56, 59, 59, 60, 61, 62, 62, 63, 64, 66, 66, 67, 68, 72, 73, 77, 77, 79, 79, 80, 80, 81]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "I said at 4:45pm.";
		r = "[0, 1, 2, 6, 7, 9, 10, 14, 14, 16, 16, 17]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "I can't believe they wanna keep 40% of that. ``Whatcha think?'' \"I don't --- think so...,\"";
		r = "[0, 1, 2, 4, 4, 7, 8, 15, 16, 20, 21, 24, 24, 26, 27, 31, 32, 34, 34, 35, 36, 38, 39, 43, 43, 44, 45, 47, 47, 51, 51, 54, 55, 60, 60, 61, 61, 63, 64, 65, 65, 66, 67, 69, 69, 72, 73, 76, 77, 82, 83, 85, 85, 88, 88, 89, 89, 90]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "You `paid' US$170,000?!\nYou should've paid only $16.75.";
		r = "[0, 3, 4, 5, 5, 9, 9, 10, 11, 14, 14, 21, 21, 23, 24, 27, 28, 34, 34, 37, 38, 42, 43, 47, 48, 49, 49, 54, 54, 55]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = " 1. Buy a new Chevrolet (37%-owned in the U.S.) . 15%";
		r = "[1, 3, 4, 7, 8, 9, 10, 13, 14, 23, 24, 25, 25, 27, 27, 28, 28, 29, 29, 34, 35, 37, 38, 41, 42, 46, 46, 47, 48, 49, 50, 52, 52, 53]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "A slashed up white leather jacket for $3000???That's unbelievable!";
		r = "[0, 1, 2, 9, 10, 12, 13, 18, 19, 26, 27, 33, 34, 37, 38, 39, 39, 43, 43, 46, 46, 50, 50, 52, 53, 65, 65, 66]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "He loves Acne (! ) disgusting";
		r = "[0, 2, 3, 8, 9, 13, 14, 18, 19, 29]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
		
		s = "It should be suffixed as follows %_of_fat";
		r = "[0, 2, 3, 9, 10, 12, 13, 21, 22, 24, 25, 32, 33, 35, 35, 41]";
		tokens = t.tokenize(s);
		tokenStartEnd = getTokenStartEnd(tokens);
		assertEquals(r, tokenStartEnd.toString());
		assertWordFormsEquals(s, tokens);
	}
	
	private void assertWordFormsEquals(final String tokenizedString, final List<NLPNode> tokens) {
		tokens.forEach(token -> assertEquals(tokenizedString.substring(token.getStartOffset(), token.getEndOffset()), token.getWordForm()));
	}
}
