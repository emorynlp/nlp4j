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
package edu.emory.mathcs.nlp.learning.prediction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparsePrediction extends Prediction
{
	private static final long serialVersionUID = -2873195048974695284L;
	private int label;
	
	public SparsePrediction(int label, float score)
	{
		super(score);
		setLabel(label);
	}
	
	public int getLabel()
	{
		return label;
	}

	public void setLabel(int label)
	{
		this.label = label;
	}

	public void copy(SparsePrediction p)
	{
		set(p.label, p.score);
	}
	
	public void set(int label, float score)
	{
		setLabel(label);
		setScore(score);
	}
	
	@Override
	public String toString()
	{
		return label+":"+score;
	}
}
