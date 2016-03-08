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
package edu.emory.mathcs.nlp.component.sentiment;

import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentEval implements Eval
{
	private final String positive_label;
	private final String negative_label;
	private F1Eval       positive_eval;
	private F1Eval       negative_eval;
	
	public SentimentEval(String positiveLabel, String negativeLabel)
	{
		positive_label = positiveLabel;
		negative_label = negativeLabel;
		positive_eval  = new F1Eval();
		negative_eval  = new F1Eval();
	}
	
	@Override
	public void clear()
	{
		positive_eval.clear();
		negative_eval.clear();
	}

	@Override
	public double score()
	{
		return (positive_eval.getF1() + negative_eval.getF1()) / 2;
	}

	public boolean add(String oracle, String system)
	{
		if (positive_label == null || negative_label == null)
			return false;
		
		int[] pos = count(oracle, system, positive_label);
		int[] neg = count(oracle, system, negative_label);
		
		positive_eval.add(pos[0], pos[1], pos[2]);
		negative_eval.add(neg[0], neg[1], neg[2]);
		
		return true;
	}
	
	private int[] count(String oracle, String system, String label)
	{
		int totalSystem = label.equals(system) ? 1 : 0;
		int totalOracle = label.equals(oracle) ? 1 : 0;
		int correct = totalSystem * totalOracle;
		return new int[]{correct, totalSystem, totalOracle};
	}
	
	@Override
	public String toString()
	{
		return String.format("AVG = %5.2f, POS = %5.2f, NEG = %5.2f", score(), positive_eval.getF1(), negative_eval.getF1());		
	}
}
