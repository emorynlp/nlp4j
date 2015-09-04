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
package edu.emory.mathcs.nlp.tokenizer.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishApostropheTest
{
	@Test
	public void test()
	{
		EnglishApostrophe dt = new EnglishApostrophe();
		
		assertEquals("[he, 's]" , Arrays.toString(dt.tokenize("he's")));
		assertEquals("[he, 'S]" , Arrays.toString(dt.tokenize("he'S")));
		assertEquals("[do, n't]", Arrays.toString(dt.tokenize("don't")));
		assertEquals("[do, 'nt]", Arrays.toString(dt.tokenize("do'nt")));
		
		assertTrue(dt.tokenize("he'dd") == null);
		assertTrue(dt.tokenize("dont")  == null);
	}
}
