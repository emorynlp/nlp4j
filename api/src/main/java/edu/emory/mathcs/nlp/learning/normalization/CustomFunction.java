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
package edu.emory.mathcs.nlp.learning.normalization;

/**
 * @author amit-deshmane
 * 
 * Jasper's Normalization<br>
 * Normalize input. New value is sum of entries divided by sum over all<br>
 * values. Adds smallest value to scores if it is negative.<br>
 */
public class CustomFunction implements NormalizationFunction {

	private static final long serialVersionUID = 3113580872545506521L;

	public CustomFunction() {
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.normalization.NormalizationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		float sum = 0;
		float minVal = Float.MAX_VALUE;
		for (float tempScore : scores) {
			if(tempScore < minVal){
				minVal = tempScore;
			}
		}
		if(minVal>0){
			minVal=0;
		}
		for (float tempScore : scores) {
			sum += tempScore - minVal;
		}
		if (sum == 0) {
			sum = 1;
		}
		for (int i =0; i < scores.length; i ++) {
			scores[i] = (scores[i]-minVal) / sum;
		}
	}

}
