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

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BenchmarkTest
{
	AtomicDouble t = new AtomicDouble(0);
	
	@Test
	public void test() throws Exception
	{
		System.out.println(FastMath.exp(174.80113f));
	}
	
	boolean hello()
	{
		System.out.println("hello");
		return true;
	}
	
	boolean world()
	{
		System.out.println("world");
		return false;
	}
	
	class Task implements Runnable
	{
		FloatGapList weights;
		int begin_index;
		float[] x;
		
		public Task(FloatGapList weights, float[] x, int beginIndex)
		{
			this.weights = weights;
			this.x       = x;
			begin_index  = beginIndex;
		}

		@Override
		public void run()
		{
			for (int i=0; i<x.length; i++)
				t.addAndGet(x[i] * weights.get(begin_index++));
		}
	}
}
