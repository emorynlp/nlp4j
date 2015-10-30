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
package edu.emory.mathcs.nlp.component.dep;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.component.util.eval.Eval;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPEval implements Eval
{
	private int las, uas;
	private int total;
	
	public DEPEval()
	{
		clear();
	}
	
	public void add(int las, int uas, int total)
	{
		this.las   += las;
		this.uas   += uas;
		this.total += total;
	}
	
	public void clear()
	{
		las = uas = total = 0;
	}
	
	public int total()
	{
		return total;
	}
	
	public double getLAS()
	{
		return MathUtils.accuracy(las, total);
	}
	
	public double getUAS()
	{
		return MathUtils.accuracy(uas, total);
	}
	
	@Override
	public double score()
	{
		return getLAS();
	}
	
	@Override
	public String toString()
	{
		return String.format("LAS = %5.2f, UAS = %5.2f", getLAS(), getUAS());
	}
}
