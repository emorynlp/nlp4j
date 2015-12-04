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
package edu.emory.mathcs.nlp.learning.initialization;

import java.util.Random;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RandomInitializer implements WeightInitializer
{
	private float  lower_bound;	// inclusive
	private float  upper_bound;	// exclusive
	private Random rand;
	
	public RandomInitializer(Random rand, float lowerBound, float upperBound)
	{
		lower_bound = lowerBound;
		upper_bound = upperBound;
		this.rand   = rand;
	}
	
	@Override
	public void init(float[] weights)
	{
		float gap = upper_bound - lower_bound;
		
		for (int i=0; i<weights.length; i++)
			weights[i] = lower_bound + gap * rand.nextFloat(); 
	}
}
