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

import edu.emory.mathcs.nlp.learning.gridsearch.ExpFunction;
import edu.emory.mathcs.nlp.learning.gridsearch.GridFunction;
import edu.emory.mathcs.nlp.learning.gridsearch.LinearFunction;

/**
 * @author Amit_Deshmane
 *
 */
public class GridFunctionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GridFunction f = new ExpFunction(1E-6f, 1f, 10f);
		while(f.next()){
			System.out.println(f.getVal());
		}
		System.out.println("*************");
		f = new LinearFunction(0.1f, 5f, 10);
		while(f.next()){
			System.out.println(f.getVal());
		}
	}

}
