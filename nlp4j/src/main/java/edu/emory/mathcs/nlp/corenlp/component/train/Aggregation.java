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
package edu.emory.mathcs.nlp.corenlp.component.train;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Aggregation
{
	private double tolerance_delta;
	private int    max_tolerance;
	
	public Aggregation(double delta, int max)
	{
		setToleranceDelta(delta);
		setMaxTolerance(max);
	}

	public double getToleranceDelta()
	{
		return tolerance_delta;
	}

	public void setToleranceDelta(double delta)
	{
		this.tolerance_delta = delta;
	}

	public int getMaxTolerance()
	{
		return max_tolerance;
	}

	public void setMaxTolerance(int max)
	{
		this.max_tolerance = max;
	}
}
