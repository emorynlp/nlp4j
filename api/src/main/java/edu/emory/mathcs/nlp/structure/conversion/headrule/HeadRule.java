/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.structure.conversion.headrule;

import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HeadRule
{
	/** The delimiter between head tagsets ({@code ";"}). */
	static final public String DELIM_TAGSETS = StringConst.SEMICOLON;
	/** The left-to-right search direction ({@code "l"}). */
	static final public String DIR_LEFT_TO_RIGHT = "l";
	/** The right-to-left search direction ({@code "r"}). */
	static final public String DIR_RIGHT_TO_LEFT = "r";

	static final private Pattern P_TAGSETS = PatternConst.SEMICOLON;

	protected HeadTagSet[] tagsets;
	protected boolean right_to_left;
	
	public HeadRule(String dir)
	{
		this(dir, ".*");
	}
	
	/**
	 * Constructs a new headrule by decoding the specific head tagsets.
	 * @param dir {@link HeadRule#DIR_LEFT_TO_RIGHT} or {@link HeadRule#DIR_RIGHT_TO_LEFT}.
	 * If {@link HeadRule#DIR_LEFT_TO_RIGHT}, searches the head from left to right.
	 * If {@link HeadRule#DIR_RIGHT_TO_LEFT}, searches the head from right to left. 
	 * @param rule e.g., "NP|-SBJ;PP|IN".
	 */
	public HeadRule(String dir, String rule)
	{
		String[] tmp = P_TAGSETS.split(rule);
		int i, size = tmp.length;
		
		right_to_left = dir.equals(DIR_RIGHT_TO_LEFT);
		tagsets = new HeadTagSet[size];
		
		for (i=0; i<size; i++)
			tagsets[i] = new HeadTagSet(tmp[i]);
		
	}
	
	/** @return {@code true} if the search direction is right-to-left. */
	public boolean isRightToLeft()
	{
		return right_to_left;
	}
	
	public HeadTagSet[] getHeadTags() 
	{
		return tagsets;
	}

	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (HeadTagSet tag : tagsets)
		{
			build.append(DELIM_TAGSETS);
			build.append(tag.toString());
		}

		return build.substring(DELIM_TAGSETS.length());
	}
}