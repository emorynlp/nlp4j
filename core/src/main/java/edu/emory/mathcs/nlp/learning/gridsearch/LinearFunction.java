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
package edu.emory.mathcs.nlp.learning.gridsearch;

/**
 * @author Amit_Deshmane
 *
 */
public class LinearFunction implements GridFunction {

	/**
	 * 
	 */
	public float min;
	public float max;
	public int steps;
	public int index = -1;
	public int markIndex = -1;
	
	public LinearFunction(float min, float max, int steps) {
		this.min = min;
		this.max = max;
		this.steps = steps;
	}

	public float getVal() {
		return min + index * (max - min)/steps;
	}

	public void reset() {
		index = 0;
	}

	public boolean previous() {
		index--;
		if(getVal() < min || getVal() > max){
			return false;
		}
		else return true;
	}

	public boolean next() {
		index++;
		if(getVal() < min || getVal() > max){
			return false;
		}
		else return true;
	}
	
	@Override
	public void mark(){
		markIndex = index;
	}
	
	@Override
	public void resetToMark(){
		index = markIndex;
	}
}
