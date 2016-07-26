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
package edu.emory.mathcs.nlp.learning.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FeatureVectorTest
{
	@Test
	public void testSparseVector()
	{
		SparseVector x = new SparseVector();
		
		x.add(2);
		x.add(1, 0.2f);
		x.add(4, 0.3f);
		x.add(3);
		
		assertEquals("2:1.0 1:0.2 4:0.3 3:1.0", x.toString());
		x.sort();
		assertEquals("1:0.2 2:1.0 3:1.0 4:0.3", x.toString());
	}
	
	@Test
	public void testStringVector()
	{
		StringVector vector = new StringVector();

		vector.add((short)2, "B");
		vector.add((short)4, "A", 0.2f);
		vector.add((short)1, "A", 0.3f);
		vector.add((short)3, "C");
		
		assertEquals("2:B:1.0 4:A:0.2 1:A:0.3 3:C:1.0", vector.toString());
	}
}
