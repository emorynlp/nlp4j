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
package edu.emory.mathcs.nlp.emorynlp.component.eval;

import edu.emory.mathcs.nlp.common.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AccuracyEval implements Eval
{
	private int correct;
	private int total;
	
	public AccuracyEval()
	{
		clear();
	}
	
	public void add(int correct, int total)
	{
		this.correct += correct;
		this.total   += total;
	}
	
	public int correct()
	{
		return correct;
	}
	
	public int total()
	{
		return total;
	}
	
	@Override
	public void clear()
	{
		correct = total = 0;
	}
	
	@Override
	public double score()
	{
		return MathUtils.accuracy(correct, total);
	}
	
	@Override
	public String toString()
	{
		return String.format("ACC = %5.2f", score());
	}
}
