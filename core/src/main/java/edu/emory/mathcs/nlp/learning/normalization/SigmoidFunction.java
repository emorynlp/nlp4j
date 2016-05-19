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
 * Well the its just application of sigmoid function<br>
 * Someone can actually make the sum = 1 if needed<br>
 *
 */
public class SigmoidFunction implements NormalizationFunction {

	private static final long serialVersionUID = 873532059178086953L;
	private edu.emory.mathcs.nlp.learning.activation.SigmoidFunction f;

	public SigmoidFunction() {
		f = new edu.emory.mathcs.nlp.learning.activation.SigmoidFunction();
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.normalization.NormalizationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		f.apply(scores);
	}

}
