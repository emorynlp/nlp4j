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
package edu.emory.mathcs.nlp.common.collection.ngram;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BigramTest
{
	@Test
	public void test()
	{
		Bigram<String,String> map = new Bigram<>();
		
		map.add("A", "a1");
		map.add("A", "a2");
		map.add("A", "a1");
		map.add("A", "a3");
		
		map.add("B", "b1");
		map.add("B", "b2", 2);
		map.add("B", "b3");
		
		ObjectDoublePair<String> p = map.getBest("A");
		assertEquals("a1", p.o);
		assertEquals(0.5, p.d, 0);
		
		p = map.getBest("B");
		assertEquals("b2", p.o);
		assertEquals(0.5, p.d, 0);

		assertEquals("[(a1,2)]"  , map.toList("A", 1).toString());
		assertEquals("[(b2,0.5)]", map.toList("B", 0.4).toString());
		
	}
}