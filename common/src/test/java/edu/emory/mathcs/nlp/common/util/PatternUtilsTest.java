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

import java.util.regex.Pattern;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.PatternUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PatternUtilsTest
{
	@Test
	public void testGetORPattern()
	{
		Pattern p; 
		
		p = PatternUtils.createORPattern("A", "B");
		assertTrue(p.matcher("A").find());
		assertTrue(p.matcher("B").find());
		assertTrue(p.matcher("aA").find());
		assertTrue(p.matcher("Bb").find());
		
		p = PatternUtils.createClosedORPattern("A", "B");
		assertTrue(p.matcher("A").find());
		assertTrue(p.matcher("B").find());
		assertFalse(p.matcher("aA").find());
		assertFalse(p.matcher("Bb").find());
	}
	
	@Test
	public void testContainsPunctuation()
	{
		assertFalse(PatternUtils.containsPunctuation("ab"));
		assertTrue(PatternUtils.containsPunctuation("$ab"));
		assertTrue(PatternUtils.containsPunctuation("a-b"));
		assertTrue(PatternUtils.containsPunctuation("ab#"));
		assertTrue(PatternUtils.containsPunctuation("$-#"));
	}
	
	@Test
	public void testRevertBrackets()
	{
		String[] org = {"-LRB-","-RRB-","-LCB-","-RCB-","-LSB-","-RSB-","-LRB--RRB-","-LCB--RCB-","-LSB--RSB-"};
		String[] rep = {"(",")","{","}","[","]","()","{}","[]"};
		int i, size = org.length;
		
		for (i=0; i<size; i++)
			assertEquals(rep[i], PatternUtils.revertBrackets(org[i]));
	}
}
