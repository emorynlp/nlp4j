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

import java.util.regex.Pattern;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SplitterTest
{
	@Test
	public void testSplitIncludingMatches()
	{
		Pattern pd = Pattern.compile("\\d+");
		Pattern pa = Pattern.compile("\\p{Lower}+");
		String s, t;
		
		s = "ab12cd34ef56gh";
		t = "[ab, 12, cd, 34, ef, 56, gh]";
		assertEquals(t, Splitter.splitIncludingMatches(pd, s).toString());
		assertEquals(t, Splitter.splitIncludingMatches(pa, s).toString());
		
		s = "12cd34ef56";
		t = "[12, cd, 34, ef, 56]";
		assertEquals(t, Splitter.splitIncludingMatches(pd, s).toString());
		assertEquals(t, Splitter.splitIncludingMatches(pa, s).toString());
		
		s = "1234";
		t = "[1234]";
		assertEquals(t, Splitter.splitIncludingMatches(pd, s).toString());
		assertEquals(t, Splitter.splitIncludingMatches(pa, s).toString());
		
		s = "abcd";
		t = "[abcd]";
		assertEquals(t, Splitter.splitIncludingMatches(pd, s).toString());
		assertEquals(t, Splitter.splitIncludingMatches(pa, s).toString());
	}
}
