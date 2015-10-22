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
public class F1Eval implements Eval
{
	private int total_system;
	private int total_gold;
	private int correct;
	
	public F1Eval()
	{
		clear();
	}
	
	public void add(int correct, int totalSystem, int totalGold)
	{
		this.correct += correct;
		total_system += totalSystem;
		total_gold   += totalGold;
	}
	
	public double getPrecision()
	{
		return MathUtils.accuracy(correct, total_system);
	}
	
	public double getRecall()
	{
		return MathUtils.accuracy(correct, total_gold);
	}
	
	public double getF1()
	{
		return MathUtils.getF1(getPrecision(), getRecall());
	}
	
	@Override
	public void clear()
	{
		correct = total_system = total_gold = 0;
	}
	
	@Override
	public double score()
	{
		return getF1();
	}
	
	@Override
	public String toString()
	{
		double p  = getPrecision();
		double r  = getRecall();
		double f1 = MathUtils.getF1(p, r);
		
		return String.format("F1: %5.2f, P: %5.2f, R: %5.2f", f1, p, r);
	}
}
