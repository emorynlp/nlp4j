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
package edu.emory.mathcs.nlp.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.CharTokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharTokenizerTest
{
	@Test
	public void test()
	{
		CharTokenizer t;
		String s;
		
		t = new CharTokenizer(',');
		s = "a,b,c";
		assertEquals("[a, b, c]", Arrays.toString(t.tokenize(s)));
		
		t = new CharTokenizer(';');
		s = ";abc;def;;ghi;";
		assertEquals("[abc, def, ghi]", Arrays.toString(t.tokenize(s)));
	}
}
