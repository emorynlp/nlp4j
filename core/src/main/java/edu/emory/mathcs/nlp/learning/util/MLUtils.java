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
package edu.emory.mathcs.nlp.learning.util;

import org.apache.commons.math3.util.FastMath;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MLUtils
{
	/** Transform the scores into softmax regression. */
	static public void softmax(float[] scores)
	{
		float sum = 0;
		
		for (int i=0; i<scores.length; i++)
		{
			scores[i] = (float)FastMath.exp(scores[i]);
			sum += scores[i];
		}
		
		for (int i=0; i<scores.length; i++)
			scores[i] /= sum;
	}
	
	static public int argmax(float[] scores)
	{
		return argmax(scores, scores.length);
	}
	
	static public int argmax(float[] scores, int size)
	{
		int i, maxIndex = 0;
		double maxValue = scores[maxIndex];
		
		for (i=1; i<size; i++)
		{
			if (maxValue < scores[i])
			{
				maxIndex = i;
				maxValue = scores[maxIndex];
			}
		}
		
		return maxIndex;
	}
	
	static public int argmax(float[] scores, int[] labels)
	{
		int i, j, size = labels.length, maxIndex = labels[0];
		float maxValue = scores[maxIndex];
		
		for (j=1; j<size; j++)
		{
			i = labels[j];
			
			if (maxValue < scores[i])
			{
				maxIndex = i;
				maxValue = scores[i];
			}
		}
		
		return maxIndex;
	}
	
	static public int[] argmax2(float[] array)
	{
		return argmax2(array, array.length);
	}
	
	static public int[] argmax2(float[] array, int size)
	{
		if (size < 2) return new int[]{0,-1};
		int[] max = {0,1};
		
		if (array[0] < array[1])
		{
			max[0] = 1;
			max[1] = 0;
		}
		
		for (int i=2; i<size; i++)
		{
			if (array[max[0]] < array[i])
			{
				max[1] = max[0];
				max[0] = i;
			}
			else if (array[max[1]] < array[i])
				max[1] = i;
		}
		
		return max;
	}
}
