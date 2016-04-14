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

import java.util.stream.Collectors;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.Joiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IssuesTest
{
	@Test
	public void issues()
	{
		Tokenizer t = new EnglishTokenizer();
		
		// https://github.com/emorynlp/nlp4j-tokenization/pull/3
		String s = "I did it my way. Definitely not worth stopping by.";
		String r = "I did it my way . Definitely not worth stopping by .";
		assertEquals(r, Joiner.join(t.tokenize(s), " ", Token::getWordForm));
	}
	
//	@Test
	public void test1()
	{
		Tokenizer t = new EnglishTokenizer();
		String s = "There 's Mother 's Day , there 's Father 's Day , there 's no . .. Lesbian Lover Day .";

		for (Token node : t.tokenize(s))
			System.out.print(node.getWordForm()+" ");	
	}
	
//	@Test
	public void test()
	{
		Tokenizer t = new EnglishTokenizer();
		String s = "  !((a)).  dsjkds sdf;l s  ";
		
		for (Token node : t.tokenize(s))
			System.out.println(node.getWordForm()+" "+node.getStartOffset()+" "+node.getEndOffset());
	}
	
//	@Test
	public void testEmoticons()
	{
		Tokenizer t = new EnglishTokenizer();
		String s = "Hello ^u^.";
		System.out.println(t.tokenize(s).stream().map(n -> n.getWordForm()).collect(Collectors.toList()).toString());
	}
}
