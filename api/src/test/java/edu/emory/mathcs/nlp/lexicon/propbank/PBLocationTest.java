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
package edu.emory.mathcs.nlp.lexicon.propbank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.lexicon.propbank.PBLocation;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBLocationTest
{
	@Test
	public void test()
	{
		PBLocation loc1 = new PBLocation(0, 1);
		PBLocation loc2 = new PBLocation(0, 1, "*");
		
		assertEquals( "0:1", loc1.toString());
		assertEquals("*0:1", loc2.toString());
		assertTrue(loc1.matches(loc2.getTerminalID(), loc2.getHeight()));
		assertFalse(loc1.equals(loc2));
		
		loc1.set(0, 2);
		assertFalse(loc1.matches(loc2.getTerminalID(), loc2.getHeight()));
		
		loc2 = new PBLocation("0:3", ",");
		assertEquals(",0:3", loc2.toString());
	}
}