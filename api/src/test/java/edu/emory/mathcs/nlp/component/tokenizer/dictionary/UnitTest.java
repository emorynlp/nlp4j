/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.component.tokenizer.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class UnitTest
{
	@Test
	public void test()
	{
		Unit dt = new Unit();
		
		assertEquals("[1, mg]", Arrays.toString(dt.tokenize("1mg")));
		assertEquals("[1, cm]", Arrays.toString(dt.tokenize("1cm")));
		
		assertEquals("[10, MG]", Arrays.toString(dt.tokenize("10MG")));
		assertEquals("[10, CM]", Arrays.toString(dt.tokenize("10CM")));
		
		assertTrue(dt.tokenize("1ma") == null);
	}
}
