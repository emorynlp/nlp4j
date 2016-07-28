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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CurrencyTest
{
	@Test
	public void test()
	{
		Currency dt = new Currency();

		assertTrue(dt.isCurrencyDollar("c"));
		assertTrue(dt.isCurrencyDollar("us"));
		
		assertTrue(dt.isCurrency("usd"));
		assertTrue(dt.isCurrency("us$"));

		assertFalse(dt.isCurrencyDollar("US"));
		assertFalse(dt.isCurrencyDollar("a"));
		assertFalse(dt.isCurrency("usb"));
		
		assertEquals("[USD, 1]", Arrays.toString(dt.tokenize("USD1")));
		assertEquals("[us$, 1]", Arrays.toString(dt.tokenize("us$1")));
		assertTrue(dt.tokenize("u$1") == null);
	}
}
