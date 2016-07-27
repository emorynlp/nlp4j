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
package edu.emory.mathcs.nlp.common.propbank.frameset;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PBFXml
{
	String E_FRAMESET	= "frameset";
	String E_PREDICATE	= "predicate";
	String E_ROLESET	= "roleset";
	String E_ROLE		= "role";
	String E_VNROLE		= "vnrole";
	
	String A_LEMMA		= "lemma";
	String A_ID			= "id";
	String A_DESCR		= "descr";
	String A_NAME		= "name";
	String A_N			= "n";
	String A_F			= "f";
	String A_VNCLS		= "vncls";
	String A_VNTHETA	= "vntheta";
}