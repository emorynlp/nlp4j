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
package edu.emory.mathcs.nlp.common.verbnet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface VNXml
{
	String E_VNSUBCLASS	= "VNSUBCLASS";
	String E_SEMANTICS	= "SEMANTICS";
	String E_SYNRESTR	= "SYNRESTR";
	String E_SYNTAX		= "SYNTAX";
	String E_FRAMES		= "FRAMES";
	String E_FRAME		= "FRAME";
	String E_PRED		= "PRED";
	String E_ARG		= "ARG";
	
	String A_ID			= "ID";
	String A_TYPE		= "type";
	String A_VALUE		= "value";
	String A_VALUE_CAP	= "Value";
	String A_BOOL		= "bool";
	
	String ARG_TYPE_EVENT			= "Event";
	String ARG_TYPE_THEM_ROLE		= "ThemRole";
	String ARG_TYPE_VERB_SPECIFIC	= "VerbSpecific";
	String ARG_TYPE_CONSTANT		= "Constant";
	
	String SYNRESTR_TYPE_PLURAL		= "plural";
}