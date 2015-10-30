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
package edu.emory.mathcs.nlp.component.util.node;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface Orthographic
{
	String HYPERLINK			= "0";
	String ALL_UPPER			= "1";
	String ALL_LOWER			= "2";
	String ALL_DIGIT			= "3";
	String ALL_PUNCT			= "4";
	String ALL_DIGIT_OR_PUNCT	= "5";
	String HAS_DIGIT			= "6";
	String HAS_PERIOD			= "7";
	String HAS_HYPHEN			= "8";
	String HAS_OTHER_PUNCT		= "9";
	String NO_LOWER				= "10";
	String FST_UPPER			= "11";
	String UPPER_1				= "12";
	String UPPER_2				= "13";
}