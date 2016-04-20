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
public class ExpFunction implements GridFunction {

	/**
	 * 
	 */
	public float min;
	public float max;
	public float base;
	public int[] subgrid = {0, 2, 5};
	public int expIndex = 1;
	public int subIndex = -1;
	
	public int markExpIndex = 1;
	public int markSubIndex = -1;
	
	public ExpFunction(float min, float max, float base) {
		this.min = min;
		this.max = max;
		this.base = base;
	}
	
	@Override
	public boolean next(){
		if(subIndex == subgrid.length - 1){
			subIndex = 0;
			expIndex = expIndex + 1;
		}
		else{
			subIndex = subIndex + 1;
		}
		if(getVal() < min || getVal() > max){
			return false;
		}
		else return true;
	}
	@Override
	public boolean previous(){
		if(subIndex == 0){
			subIndex = subgrid.length - 1;
			expIndex = expIndex - 1;
		}
		else{
			subIndex = subIndex - 1;
		}
		if(getVal() < min || getVal() > max){
			return false;
		}
		else return true;
	}
	public void set(int valIndex, int expIndex){
		this.subIndex = valIndex;
		this.expIndex = expIndex;
	}
	@Override
	public void reset(){
		subIndex = 0;
		expIndex = 0;
	}
	@Override
	public float getVal(){
		double next = Math.pow(base, expIndex);
		double prev = Math.pow(base, expIndex - 1);
		return (float)(min*(prev + subgrid[subIndex]/10.0*(next-prev)));
	}

	public void resetToMark() {
		expIndex = markExpIndex;
		subIndex = markSubIndex;
	}

	public void mark() {
		markExpIndex = expIndex;
		markSubIndex = subIndex;
	}
}
