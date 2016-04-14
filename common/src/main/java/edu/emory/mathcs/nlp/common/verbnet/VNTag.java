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

import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNTag
{
	private VNTag() {}
	
	static public final String VN_AGENT				= "agent";
	static public final String VN_ASSET				= "asset";
	static public final String VN_ATTRIBUTE			= "attribute";
	static public final String VN_BENEFICIARY		= "beneficiary";
	static public final String VN_CAUSE				= "cause";
	static public final String VN_CO_AGENT			= "co-agent";
	static public final String VN_CO_PATIENT		= "co-patient";
	static public final String VN_CO_THEME			= "co-theme";
	static public final String VN_DESTINATION		= "destination";
	static public final String VN_EXPERIENCER		= "experiencer";
	static public final String VN_EXTENT			= "extent";
	static public final String VN_GOAL				= "goal";
	static public final String VN_INITIAL_LOCATION	= "initial_location";
	static public final String VN_INSTRUMENT		= "instrument";
	static public final String VN_LOCATION			= "location";
	static public final String VN_MATERIAL			= "material";
	static public final String VN_PATIENT			= "patient";
	static public final String VN_PIVOT				= "pivot";
	static public final String VN_PREDICATE			= "predicate";
	static public final String VN_PRODUCT			= "product";
	static public final String VN_RECIPIENT			= "recipient";
	static public final String VN_REFLEXIVE			= "reflexive";
	static public final String VN_RESULT			= "result";
	static public final String VN_SOURCE			= "source";
	static public final String VN_STIMULUS			= "stimulus";
	static public final String VN_THEME				= "theme";
	static public final String VN_TIME				= "time";
	static public final String VN_TOPIC				= "topic";
	static public final String VN_TRAJECTORY		= "trajectory";
	static public final String VN_VALUE				= "value";

	static private final Set<String> TAG_SET = DSUtils.getFieldSet(new VNTag().getClass());
	static public boolean contains(String tag) {return TAG_SET.contains(tag);}
	static public Set<String> getTagSet() {return TAG_SET;}
}