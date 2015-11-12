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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.CTTree;
import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBArgument implements Serializable, Comparable<PBArgument>
{
	private static final long serialVersionUID = -5706844524751492352L;
	/** The delimiter between location and label ("-"). */
	static public final String DELIM = StringConst.HYPHEN;
	
	private List<PBLocation> l_locations;
	private String s_label;
	
	public PBArgument()
	{
		l_locations = new ArrayList<>();
	}
	
	/** @param str "<location>(<operator><location>)*-label". */
	public PBArgument(String str)
	{
		l_locations = new ArrayList<>();
		int idx = str.indexOf(DELIM);
		String type;
		
		if (idx == -1)
			throw new IllegalArgumentException(str);
		
		StringTokenizer tok = new StringTokenizer(str.substring(0, idx), "*&,;", true);
		s_label = str.substring(idx+1);
		
		if (!tok.hasMoreTokens())
			throw new IllegalArgumentException(str);
		
		addLocation(new PBLocation(tok.nextToken(), StringConst.EMPTY));
		
		while (tok.hasMoreTokens())
		{
			type = tok.nextToken();
		
			if (!tok.hasMoreTokens())
				throw new IllegalArgumentException(str);
			
			addLocation(new PBLocation(tok.nextToken(), type));
		}
	}
	
	public String getLabel()
	{
		return s_label;
	}
	
	/** @return the index'th location of this argument if exists; otherwise, {@code null}. */
	public PBLocation getLocation(int index)
	{
		return DSUtils.isRange(l_locations, index) ? l_locations.get(index) : null;
	}
	
	/** @return the first location matching the specific terminal ID and height in this argument. */
	public PBLocation getLocation(int terminalID, int height)
	{
		for (PBLocation loc : l_locations)
		{
			if (loc.matches(terminalID, height))
				return loc;
		}
		
		return null;
	}
	
	/** @return a list of locations of this argument. */
	public List<PBLocation> getLocationList()
	{
		return l_locations;
	}
	
	/** @return the number of locations in this argument. */
	public int getLocationSize()
	{
		return l_locations.size();
	}
	
	/**
	 * Returns a set of terminal IDs belonging to this argument given the specific tree.
	 * @param tree the constituent tree.
	 * @return a set of terminal IDs belonging to this argument.
	 */
	public Set<Integer> getTerminalIDSet(CTTree tree)
	{
		Set<Integer> set = new HashSet<>();
		
		for (PBLocation loc : l_locations)
			set.addAll(tree.getNode(loc).getTerminalIDSet());
		
		return set;
	}
		
	/** Adds the specific location to this argument. */
	public void addLocation(PBLocation location)
	{
		l_locations.add(location);
	}
	
	/** Adds the specific collection of locations to this argument. */
	public void addLocations(Collection<PBLocation> locations)
	{
		l_locations.addAll(locations);
	}
	
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	public void setLocations(List<PBLocation> locations)
	{
		l_locations = locations;
	}
	
	/** Removes the first location matching the specific terminal ID and height from this argument. */
	public void removeLocation(int terminalId, int height)
	{
		int i, size = l_locations.size();
		
		for (i=0; i<size; i++)
		{
			if (l_locations.get(i).matches(terminalId, height))
			{
				l_locations.remove(i);
				break;
			}
		}
	}
	
	/** Removes the specific collection of locations from this argument. */
	public void removeLocations(Collection<PBLocation> locations)
	{
		l_locations.removeAll(locations);
		if (!l_locations.isEmpty())	l_locations.get(0).setType(StringConst.EMPTY);
	}
	
	/**
	 * Sorts the locations of this argument by their terminal IDs and heights.
	 * @see PBLocation#compareTo(PBLocation)
	 */
	public void sortLocations()
	{
		if (l_locations.isEmpty())	return;
		
		Collections.sort(l_locations);
		PBLocation fst = l_locations.get(0), loc;
		
		if (!fst.isType(StringConst.EMPTY))
		{
			for (int i=1; i<l_locations.size(); i++)
			{
				loc = l_locations.get(i);
				
				if (loc.isType(StringConst.EMPTY))
				{
					loc.setType(fst.getType());
					break;
				}
			}
			
			fst.setType(StringConst.EMPTY);
		}
	}
	
	public boolean containsOperator(String operator)
	{
		for (PBLocation loc : l_locations)
		{
			if (loc.isType(operator))
				return true;
		}
		
		return false;
	}
	
	public boolean isLabel(String label)
	{
		return s_label.equals(label);
	}

	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (PBLocation loc : l_locations)
			build.append(loc.toString());
				
		build.append(DELIM);
		build.append(s_label);
		
		return build.toString();
	}
	
	@Override
	public int compareTo(PBArgument arg)
	{
		return getLocation(0).compareTo(arg.getLocation(0));
	}
}