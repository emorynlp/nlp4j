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
package edu.emory.mathcs.nlp.learning.vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import edu.emory.mathcs.nlp.learning.util.ColumnMajorVector;
import edu.emory.mathcs.nlp.learning.util.RowMajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MajorVectorTest
{
	@Test
	public void testColumnMajorVector()
	{
		ColumnMajorVector v = new ColumnMajorVector();
		assertEquals(0, v.size());
		
		assertTrue(v.expand(2, 3));
		assertEquals(3, v.getFeatureSize());
		assertEquals(2, v.getLabelSize());
		assertEquals(6, v.size());
		
		assertFalse(v.expand(2, 2));
		assertFalse(v.expand(1, 3));
		
		v.fill(1);
		assertTrue(v.expand(2, 5));
		
		v.add(1);
		assertEquals(2, v.get(0, 2), 0);
		assertEquals(2, v.get(1, 2), 0);
		assertEquals(1, v.get(0, 3), 0);
		assertEquals(1, v.get(1, 3), 0);
		assertEquals(1, v.get(0, 4), 0);
		assertEquals(1, v.get(1, 4), 0);

		v.fill(1);
		assertTrue(v.expand(4, 5));
		assertEquals(0, v.get(2, 0), 0);
		assertEquals(0, v.get(3, 0), 0);
		assertEquals(0, v.get(2, 4), 0);
		assertEquals(0, v.get(3, 4), 0);
		
		for (int i=0; i<v.size(); i++) v.set(i, i+1);
		SparseVector x = new SparseVector();
		x.add(0); x.add(2); x.add(4);
		float[] scores = new float[v.getLabelSize()];
		v.addScores(x, scores);
		assertArrayEquals(new float[]{27f, 30f, 33f, 36f}, scores, 0);
		
		Arrays.fill(scores, 0);
		v.addScores(new float[]{1,1,1,1,1}, scores);
		assertArrayEquals(new float[]{45f, 50f, 55f, 60f}, scores, 0);
	}
	
	@Test
	public void testRowMajorVector()
	{
		RowMajorVector v = new RowMajorVector();
		assertEquals(0, v.size());
		
		assertTrue(v.expand(2, 3));
		assertEquals(3, v.getFeatureSize());
		assertEquals(2, v.getLabelSize());
		assertEquals(6, v.size());
		
		assertFalse(v.expand(2, 2));
		assertFalse(v.expand(1, 3));
		
		v.fill(1);
		assertTrue(v.expand(2, 5));
		
		v.add(1);
		assertEquals(2, v.get(0, 2), 0);
		assertEquals(2, v.get(1, 2), 0);
		assertEquals(1, v.get(0, 3), 0);
		assertEquals(1, v.get(1, 3), 0);
		assertEquals(1, v.get(0, 4), 0);
		assertEquals(1, v.get(1, 4), 0);

		v.fill(1);
		assertTrue(v.expand(4, 5));
		assertEquals(0, v.get(2, 0), 0);
		assertEquals(0, v.get(3, 0), 0);
		assertEquals(0, v.get(2, 4), 0);
		assertEquals(0, v.get(3, 4), 0);
		
		for (int i=0; i<v.size(); i++) v.set(i, i+1);
		SparseVector x = new SparseVector();
		x.add(0); x.add(2); x.add(4);
		float[] scores = new float[v.getLabelSize()];
		v.addScores(x, scores);
		assertArrayEquals(new float[]{9f, 24f, 39f, 54f}, scores, 0);
		
		Arrays.fill(scores, 0);
		v.addScores(new float[]{1,1,1,1,1}, scores);
		assertArrayEquals(new float[]{15f, 40f, 65f, 90f}, scores, 0);
	}
}
