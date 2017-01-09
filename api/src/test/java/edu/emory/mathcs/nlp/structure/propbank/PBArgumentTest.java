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
package edu.emory.mathcs.nlp.structure.propbank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.propbank.PBLocation;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBArgumentTest
{
	@Test
	public void test()
	{
		PBLocation[] locations = {new PBLocation(2, 1), new PBLocation(2, 1, ";"), new PBLocation(0, 1, "*"), new PBLocation(0, 1, "&"), new PBLocation(0, 0, ",")};
		PBArgument arg = new PBArgument();
		String gold;
		
		for (PBLocation loc : locations) arg.addLocation(loc);
		arg.setLabel("ARGM-TMP");
		gold = "2:1;2:1*0:1&0:1,0:0-ARGM-TMP";
		assertEquals(gold, arg.toString());
		
		arg.sortLocations();
		gold = "0:0*0:1&0:1,2:1;2:1-ARGM-TMP";
		assertEquals(gold, arg.toString());
		
		arg = new PBArgument(gold);
		assertEquals(gold, arg.toString());
		assertTrue(arg.isLabel("ARGM-TMP"));
		assertTrue(arg.containsOperator("*"));
		assertFalse(arg.isLabel("ARGM-LOC"));
		assertFalse(arg.containsOperator(":"));
		
		assertEquals( "0:0", arg.getLocation(0).toString());
		assertEquals("*0:1", arg.getLocation(1).toString());
		assertEquals(null  , arg.getLocation(-1));
		assertEquals(arg.getLocation(3), arg.getLocation(2, 1));
		
		arg.removeLocations(2, 1);
		assertEquals(arg.getLocation(3), arg.getLocation(2, 1));
		
		arg.removeLocations(2, 1);
		assertEquals(null, arg.getLocation(2, 1));
	}
}