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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringUtilsTest
{
	@Test
	public void testToLowerUpperCase()
	{
		StringBuilder build = new StringBuilder();
		String ascii;
		int i;
		
		for (i=0; i<128; i++) build.append((char)i);
		ascii = build.toString();

		assertEquals(ascii.toLowerCase(), StringUtils.toLowerCase(ascii));
		assertEquals(ascii.toUpperCase(), StringUtils.toUpperCase(ascii));

		build = new StringBuilder();
		for (i=128; i<256; i++) build.append((char)i);
		ascii = build.toString();
		
		assertEquals(StringUtils.toLowerCase(ascii), StringUtils.toLowerCase(StringUtils.toUpperCase(ascii)));
		assertEquals(StringUtils.toUpperCase(ascii), StringUtils.toUpperCase(StringUtils.toLowerCase(ascii)));
	}
	
	@Test
	public void testStartsWithAny()
	{
		String[] suffixes = {"ab","cd","ef"};

		assertTrue(StringUtils.startsWithAny("ab", suffixes));
		assertTrue(StringUtils.startsWithAny("cd", suffixes));
		assertTrue(StringUtils.startsWithAny("ef", suffixes));
		
		assertTrue(StringUtils.startsWithAny("ab0", suffixes));
		assertTrue(StringUtils.startsWithAny("cd0", suffixes));
		assertTrue(StringUtils.startsWithAny("ef0", suffixes));
		
		assertFalse(StringUtils.startsWithAny("0ab", suffixes));
		assertFalse(StringUtils.startsWithAny("0cd", suffixes));
		assertFalse(StringUtils.startsWithAny("0ef", suffixes));
		
		assertFalse(StringUtils.startsWithAny("a", suffixes));
		assertFalse(StringUtils.startsWithAny("c", suffixes));
		assertFalse(StringUtils.startsWithAny("e", suffixes));
	}
	
	@Test
	public void testEndsWithAny()
	{
		String[] suffixes = {"ab","cd","ef"};

		assertTrue(StringUtils.endsWithAny("ab", suffixes));
		assertTrue(StringUtils.endsWithAny("cd", suffixes));
		assertTrue(StringUtils.endsWithAny("ef", suffixes));
		
		assertTrue(StringUtils.endsWithAny("0ab", suffixes));
		assertTrue(StringUtils.endsWithAny("0cd", suffixes));
		assertTrue(StringUtils.endsWithAny("0ef", suffixes));
		
		assertFalse(StringUtils.endsWithAny("ab0", suffixes));
		assertFalse(StringUtils.endsWithAny("cd0", suffixes));
		assertFalse(StringUtils.endsWithAny("ef0", suffixes));
		
		assertFalse(StringUtils.endsWithAny("b", suffixes));
		assertFalse(StringUtils.endsWithAny("d", suffixes));
		assertFalse(StringUtils.endsWithAny("f", suffixes));
	}
	
	@Test
	public void testRegionMatches()
	{
		char[] c = "abcd".toCharArray();
		char[] d = "bc".toCharArray();
		
		assertFalse(CharUtils.regionMatches(c, d, 0));
		assertTrue (CharUtils.regionMatches(c, d, 1));
		assertFalse(CharUtils.regionMatches(c, d, 2));
		assertFalse(CharUtils.regionMatches(c, d, 3));	
	}
	
	@Test
	public void testCollapseDigits()
	{
		String[] arr0 = {"10%","$10",".01","97.33","1,000,000","10:30","10-20","10/20","$12.34,56:78-90/12%",".1%-$2,#3+4-5=6/7.8:9,0"};
		
		for (String s : arr0)
			assertEquals("0", StringUtils.collapseDigits(s));
		
		assertEquals("A.0", StringUtils.collapseDigits("A.12"));
		assertEquals("A:0", StringUtils.collapseDigits("A:12"));
		assertEquals("$A0", StringUtils.collapseDigits("$A12"));
		assertEquals("A0$", StringUtils.collapseDigits("A12$"));
		assertEquals("A0" , StringUtils.collapseDigits("A12%"));
		assertEquals("%A0", StringUtils.collapseDigits("%A12"));
	}
	
	@Test
	public void testCollapsePunctuation()
	{
		String[] s = {"...","!!!","???","---","***","===","~~~",",,,",".!?-*=~,","..!!??--**==~~,,","....!!!!????----****====~~~~,,,,"};
		String[] t = {"..","!!","??","--","**","==","~~",",,",".!?-*=~,","..!!??--**==~~,,","..!!??--**==~~,,"};
		int i, size = s.length;
		
		for (i=0; i<size; i++)
			assertEquals(t[i], StringUtils.collapsePunctuation(s[i]));
	}
	
	public void testAffix()
	{
		
	}
}