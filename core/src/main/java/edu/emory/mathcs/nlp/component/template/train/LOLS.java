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
package edu.emory.mathcs.nlp.component.template.train;

import java.util.Random;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LOLS
{
	private int    fixed_stage;
	private double decaying_rate;
	private double gold_probability;
	private Random random;
	
	public LOLS(int fixedStage, double decayingRate)
	{
		init(fixedStage, decayingRate);
	}
	
	private void init(int fixedStage, double decayingRate)
	{
		fixed_stage      = fixedStage;
		decaying_rate    = decayingRate;
		gold_probability = 1d;
		random           = new XORShiftRandom(9);
	}
	
	public void updateGoldProbability()
	{
		if (fixed_stage <= 0)
			gold_probability *= decaying_rate;
		else
			fixed_stage--;
	}
	
	public double getGoldProbability()
	{
		return gold_probability;
	}
	
	public boolean chooseGold()
	{
		return (gold_probability > 0) && (gold_probability >= 1 || gold_probability > random.nextDouble());
	}
	
	@Override
	public String toString()
	{
		return String.format("LOLS: fixed = %d, decaying rate = %s", fixed_stage, decaying_rate);
	}
}
