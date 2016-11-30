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

import org.junit.Test;

import edu.emory.mathcs.nlp.structure.propbank.PBInstance;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBInstanceTest
{
	@Test
	public void test()
	{
		String gold = "wsj_2100.parse 8 20 gold get-v get.04 ----- 21:2-ARG1 20:0-rel 18:0-ARG0 17:1-ARGM-MNR 18:0*11:1-LINK-PCR 17:1*15:1-LINK-SLC";
		PBInstance instance = new PBInstance(gold);
		
		assertEquals("20:0-rel", instance.getArgument(1).toString());
		
		gold = "wsj_2100.parse 8 20 gold get-v get.04 ----- 11:1*18:0-LINK-PCR 15:1*17:1-LINK-SLC 17:1-ARGM-MNR 18:0-ARG0 20:0-rel 21:2-ARG1";
		instance.sortArguments();
		assertEquals(gold, instance.toString());
		assertEquals(instance.getArgument(3), instance.getFirstArgument("ARG0"));
		
		gold = "wsj_2100.parse 8 20 gold get-v get.04 ----- 11:1*18:0-LINK-PCR 15:1*17:1-LINK-SLC 17:1-ARGM-MNR 20:0-rel 21:2-ARG1";
		instance.removeArguments("ARG0");
		assertEquals(gold, instance.toString());
		
		gold = "wsj_2100.parse 8 20 gold get-v get.04 ----- 11:1*18:0-LINK-PCR 15:1*17:1-LINK-SLC 17:1-ARGM-MNR 20:0-rel 21:2-ARG1";
		instance.removeArguments("ARG0");
		assertEquals(gold, instance.toString());
	}
}