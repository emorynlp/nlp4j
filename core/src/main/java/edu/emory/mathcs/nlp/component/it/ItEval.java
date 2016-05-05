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
package edu.emory.mathcs.nlp.component.it;

import java.util.Arrays;
import java.util.stream.IntStream;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ItEval extends AccuracyEval
{
	int[] precisions, recalls, corrects;
	
	public ItEval(int size)
	{
		super();
		precisions = new int[size];
		corrects   = new int[size];
		recalls    = new int[size];
	}
	
	public void add(String auto, String gold)
	{
		int aindex = Integer.parseInt(auto) - 1;
		int gindex = Integer.parseInt(gold) - 1;
		
		if (aindex == gindex) corrects[aindex]++;
		precisions[gindex]++;
		recalls[aindex]++;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		if (corrects != null)
		{
			Arrays.fill(precisions, 0);
			Arrays.fill(recalls   , 0);
			Arrays.fill(corrects  , 0);
		}
	}
	
	@Override
	public String toString()
	{
		double[] p = IntStream.range(0, corrects.length).mapToDouble(i -> 100d *MathUtils.divide(corrects[i], precisions[i])).toArray();
		double[] r = IntStream.range(0, corrects.length).mapToDouble(i -> 100d *MathUtils.divide(corrects[i], recalls   [i])).toArray();
		double[] f = IntStream.range(0, corrects.length).mapToDouble(i -> MathUtils.getF1(p[i], r[i])).toArray();
		
		
		StringBuilder join = new StringBuilder();
		join.append(super.toString());
		
		for (int i=0; i<f.length; i++)
		{
			join.append(" ");
			join.append(String.format("[%d: P = %5.2f, R = %5.2f, F1 = %5.2f]", i+1, p[i], r[i], f[i]));
		}
		
		return join.toString();
	}
}
