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
package edu.emory.mathcs.nlp.common.random;

import java.util.Random;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class XORShiftRandom extends Random
{
	private static final long serialVersionUID = -6971555410750547741L;
	private long seed;
	
	public XORShiftRandom(long seed)
	{
		this.seed = seed;
	}

	@Override
	protected int next(int nbits)
	{
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		x &= ((1L << nbits) - 1);
		return (int)x;
	}
}