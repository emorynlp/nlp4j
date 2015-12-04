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
package edu.emory.mathcs.nlp.component.template.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BILOUTest
{
	@Test
	public void test()
	{
		String[] s = {"O","B-A","O","I-B","O","L-C","O","U-D",
				      "O","B-E","L-E","O","B-F","I-F","L-F","O","B-G","B-G","L-G","O","B-H","L-H","L-H","O","B-I","U-I","L-I",
				      "O","I-J","L-J","O","B-K","I-K","O","U-L","I-L",
				      "O","U-M","B-N","I-N","L-N","B-P","L-P"};
		
		Int2ObjectMap<ObjectIntIntTriple<String>> map = BILOU.collectNamedEntityMap(s, String::toString, 1, s.length);

		assertEquals(10, map.size());
		assertEquals(map.get( 308).o, "D");
		assertEquals(map.get( 397).o, "E");
		assertEquals(map.get( 530).o, "F");
		assertEquals(map.get( 749).o, "G");
		assertEquals(map.get( 881).o, "H");
		assertEquals(map.get(1100).o, "I");
		assertEquals(map.get(1496).o, "L");
		assertEquals(map.get(1628).o, "M");
		assertEquals(map.get(1674).o, "N");
		assertEquals(map.get(1805).o, "P");
	}
}
