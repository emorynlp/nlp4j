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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HeadRuleMap extends HashMap<String, HeadRule>
{
	private static final long serialVersionUID = -8495839532957709027L;
	/** The delimiter between columns ({@code "\t"}). */
	static final public String DELIM_COLUMN = StringConst.TAB;
	
	static final private Pattern P_COLUMN = PatternConst.TAB; 
	
	public HeadRuleMap() {}
	
	/**
	 * Constructs a headrule map from the specific reader.
	 * Each line indicates the headrule for a specific phrase.
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}.
	 */
	public HeadRuleMap(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String line, pTag, dir, rule;
		String[] tmp;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				tmp  = P_COLUMN.split(line);
				pTag = tmp[0];
				dir  = tmp[1];
				rule = tmp[2];
				
				put(pTag, new HeadRule(dir, rule));
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		List<String>   keys = new ArrayList<>();
		Collections.sort(keys);
		HeadRule rule;
		
		for (String key : keys)
		{
			build.append(key);
			build.append(DELIM_COLUMN);
			
			rule = get(key);
			if (rule.isRightToLeft())	build.append(HeadRule.DIR_RIGHT_TO_LEFT);
			else						build.append(HeadRule.DIR_LEFT_TO_RIGHT);
			
			build.append(DELIM_COLUMN);
			build.append(rule.toString());
	
			build.append(StringConst.NEW_LINE);
		}
		
		return build.toString().trim();
	}
}