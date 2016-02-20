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

import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IssuesTest
{
//	@Test
	public void test()
	{
		Tokenizer t = new EnglishTokenizer();
		String s = "  !((a)).  dsjkds sdf;l s  ";
		
		for (NLPNode node : t.tokenize(s))
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
