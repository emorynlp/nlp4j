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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntPair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class UnigramTest
{
	@Test
	public void test()
	{
		Unigram<String> map = new Unigram<>();
		
		map.add("A");
		map.add("B", 2);
		map.add("C");
		map.add("A");
		map.add("B", 2);
		map.add("D");
		
		assertEquals(2, map.get("A"));
		assertEquals(4, map.get("B"));
		assertEquals(1, map.get("C"));
		assertEquals(1, map.get("D"));
		assertFalse(map.contains("E"));
		
		ObjectDoublePair<String> best = map.getBest();
		assertEquals("B", best.o);
		assertEquals(0.5, best.d, 0);
		assertEquals(0.25, map.getProbability("A"), 0);

		List<ObjectIntPair<String>> li = map.toList(1);
		Collections.sort(li);
		assertEquals("[(A,2), (B,4)]", li.toString());
		
		List<ObjectDoublePair<String>> ld = map.toList(0.2);
		Collections.sort(ld);
		assertEquals("[(A,0.25), (B,0.5)]", ld.toString());
		
		Set<String> set = map.keySet(1);
		assertTrue (set.contains("A"));
		assertTrue (set.contains("B"));
		assertFalse(set.contains("C"));
		assertFalse(set.contains("D"));
		
		set = map.keySet(0.2);
		assertTrue (set.contains("A"));
		assertTrue (set.contains("B"));
		assertFalse(set.contains("C"));
		assertFalse(set.contains("D"));
	}
}
