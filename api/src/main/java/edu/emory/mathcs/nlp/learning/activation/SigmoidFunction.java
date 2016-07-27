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
package edu.emory.mathcs.nlp.learning.activation;

import edu.emory.mathcs.nlp.common.util.Sigmoid;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SigmoidFunction implements ActivationFunction
{
	private static final long serialVersionUID = 242731926367876732L;
	private Sigmoid table;
	
	/** Calls {@link #SigmoidFunction(int, float, float)}, where size = 3500, floor = -6, ceiling = 6. */
	public SigmoidFunction()
	{
		table = new Sigmoid();
	}
	
	/**
	 * @param size the size of the sigmoid table (10,000 being the highest recommendation).
	 * @param floor the lower convergence bound.
	 * @param ceiling the upper convergence bound.
	 */
	public SigmoidFunction(int size, float floor, float ceiling)
	{
		table = new Sigmoid(size, floor, ceiling);
	}
	
	@Override
	public void apply(float[] scores)
	{
		for (int i=0; i<scores.length; i++)
			scores[i] = table.get(scores[i]);
	}
	
	@Override
	public String toString()
	{
		return "Sigmoid";
	}
}
