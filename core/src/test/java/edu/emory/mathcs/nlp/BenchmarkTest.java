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
package edu.emory.mathcs.nlp;

import java.util.Random;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BenchmarkTest
{

	@Test
	public void test() throws Exception
	{
		float[] f = new float[100000000];
		Random rand = new Random(1);
		
		for (int i=0; i<f.length; i++)
			f[i] = rand.nextFloat();
		
		float[] g = new float[100];
		int r, k = f.length - g.length;
		long st, et;
		
		st = System.currentTimeMillis();
		for (int j=0; j<1000000; j++)
		{
			r = rand.nextInt(k);
			
			for (int i=0; i<g.length; i++)
				g[i] += f[r+i];
		}
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}

}
