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

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MathUtilsTest
{
	@Test
	public void testPow()
	{
		int i, j;
		
		for (j=-5; j<5; j++)
		{
			if (j == 0) continue;
			
			for (i=-5; i<5; i++)
			{
				assertEquals(Math.pow( 2, i), MathUtils.pow( 2, i), 0);
				assertEquals(Math.pow(-2, i), MathUtils.pow(-2, i), 0);
			}
		}
	}
}