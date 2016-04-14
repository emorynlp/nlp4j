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
package edu.emory.mathcs.nlp.common.propbank;

import java.io.Serializable;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBLocation implements Serializable, Comparable<PBLocation>
{
	private static final long serialVersionUID = -5141470453733767864L;
	
	static public  final String  DELIM = StringConst.COLON;
	static private final Pattern SPLIT = PatternConst.COLON;
	
	private int    i_terminalID;
	private int    i_height;
	private String s_type;
	
	public PBLocation(PBLocation loc, String type)
	{
		set(loc, type);
	}
	
	public PBLocation(int terminalID, int height, String type)
	{
		set(terminalID, height, type);
	}
	
	public PBLocation(int terminalID, int height)
	{
		set(terminalID, height, StringConst.EMPTY);
	}
	
	/**
	 * @param str "terminalId:height".
	 * @param type "|*|&|,|;".
	 */
	public PBLocation(String str, String type)
	{
		String[] loc = SPLIT.split(str);
		
		if (!StringUtils.containsDigitOnly(loc[0]) || !StringUtils.containsDigitOnly(loc[1]))
			throw new IllegalArgumentException(str);
		
		i_terminalID = Integer.parseInt(loc[0]);
		i_height     = Integer.parseInt(loc[1]);
		s_type       = type;
	}
	
	public int getTerminalID()
	{
		return i_terminalID;
	}
	
	public int getHeight()
	{
		return i_height;
	}
	
	public String getType()
	{
		return s_type;
	}

	public void set(int terminalID, int height, String type)
	{
		i_terminalID = terminalID;
		i_height     = height;
		s_type       = type;
	}
	
	public void set(PBLocation loc, String type)
	{
		set(loc.getTerminalID(), loc.getHeight(), type);
	}

	public void set(int terminalID, int height)
	{
		i_terminalID = terminalID;
		i_height     = height;
	}
	
	public void setHeight(int height)
	{
		i_height = height;
	}
	
	public void setType(String type)
	{
		s_type = type;
	}
	
	public void setLocation(PBLocation location)
	{
		i_terminalID = location.i_terminalID;
		i_height     = location.i_height;
		s_type       = location.s_type;
	}
	
	public boolean matches(int terminalID, int height)
	{
		return i_terminalID == terminalID && i_height == height;
	}
	
	public boolean isType(String type)
	{
		return s_type.equals(type);
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_type);
		build.append(i_terminalID);
		build.append(DELIM);
		build.append(i_height);
		
		return build.toString();
	}
	
	@Override
	public int compareTo(PBLocation locaction)
	{
		return i_terminalID == locaction.i_terminalID ? i_height - locaction.i_height : i_terminalID - locaction.i_terminalID;
	}
}