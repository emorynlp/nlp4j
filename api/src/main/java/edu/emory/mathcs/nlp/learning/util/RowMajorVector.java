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

import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RowMajorVector extends MajorVector
{
	private static final long serialVersionUID = -3004240061242740599L;

	@Override
	public boolean expand(int labelSize, int featureSize, WeightGenerator generator)
	{
		return expand(label_size, feature_size, labelSize, featureSize, generator);
	}
	
	@Override
	public int indexOf(int y, int xi)
	{
		return y * feature_size + xi;
	}
	
	@Override
	protected MajorVector createInstance()
	{
		return new RowMajorVector();
	}
	
	@Override
	public void addScores(SparseVector x, float[] scores)
	{
		int i, index;
		
		for (i=0; i<scores.length; i++)
		{
			index = i * feature_size;
			
			for (SparseItem p : x)
			{
				if (p.getIndex() < getFeatureSize())
					scores[i] += get(index+p.getIndex()) * p.getValue();
			}
		}
	}
	
	@Override
	public void addScores(float[] x, float[] scores)
	{
		int i, j, index = 0;
		
		for (i=0; i<scores.length; i++)
			for (j=0; j<x.length; j++)
				scores[i] += get(index++) * x[j];
	}
	
	@Override
	protected void setRowSize(int size)
	{
		label_size = size;
	}
	
	@Override
	protected void setColumnSize(int size)
	{
		feature_size = size;
	}
}
