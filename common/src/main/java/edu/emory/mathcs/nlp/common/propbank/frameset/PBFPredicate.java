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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBFPredicate implements Serializable, Comparable<PBFPredicate>
{
	private static final long serialVersionUID = -8166301199851458202L;
	
	private Map<String,PBFRoleset> m_rolesets;
	private Set<String>            s_rolesetIDs;
	private String                 s_lemma;
	
	public PBFPredicate(Element ePredicate)
	{
		init(ePredicate);
	}
	
	private void init(Element ePredicate)
	{
		m_rolesets   = new HashMap<>();
		s_rolesetIDs = new HashSet<>();
		
		setLemma(XMLUtils.getTrimmedAttribute(ePredicate, PBFXml.A_LEMMA));
		initRolesets(ePredicate.getElementsByTagName(PBFXml.E_ROLESET));
	}
	
	private void initRolesets(NodeList list)
	{
		int i, size = list.getLength();
		
		for (i=0; i<size; i++)
			addRoleset((Element)list.item(i));
	}
	
	private void addRoleset(Element eRoleset)
	{
		addRoleset(new PBFRoleset(eRoleset));
	}
	
	public Collection<PBFRoleset> getRolesets()
	{
		return m_rolesets.values();
	}
	
	/** @return {@code null} if not exist. */
	public PBFRoleset getRoleset(String id)
	{
		return m_rolesets.get(id);
	}
	
	public Set<String> getRolesetIDSet()
	{
		return s_rolesetIDs;
	}
	
	/** @return the specific lemma of this predicate (e.g., "run_out"). */
	public String getLemma()
	{
		return s_lemma;
	}
	
	public List<PBFRoleset> getRolesetListFromVerbNet(String vncls, boolean polysemousOnly)
	{
		List<PBFRoleset> rolesets = new ArrayList<>();
		Set<String> set;
		
		for (PBFRoleset roleset : m_rolesets.values())
		{
			set = roleset.getVerbNetClasseSet();
			
			if (set.contains(vncls) && (set.size() > 1 || !polysemousOnly))
				rolesets.add(roleset);
		}
		
		return rolesets;
	}
	
	/** @param lemma the specific lemma of this predicate (e.g., "run_out"). */
	public void setLemma(String lemma)
	{
		s_lemma = lemma;
	}
	
	/** @param lemma the specific lemma of this predicate (e.g., "run_out"). */
	public boolean isLemma(String lemma)
	{
		return s_lemma.equals(lemma);
	}
	
	public void addRoleset(PBFRoleset roleset)
	{
		String id = roleset.getID();
		s_rolesetIDs.add(id);
		
		if (m_rolesets.put(id, roleset) != null)
			System.err.printf("Duplicated roleset: %s\n", id);
	}
	
	@Override
	public String toString()
	{
		List<PBFRoleset> list = new ArrayList<>(getRolesets());
		StringBuilder build = new StringBuilder();
		build.append("===== "+s_lemma+" =====");
		Collections.sort(list);
		
		for (PBFRoleset roleset : list)
		{
			build.append(StringConst.NEW_LINE);
			build.append(roleset.toString());
		}
		
		return build.toString();
	}

	@Override
	public int compareTo(PBFPredicate predicate)
	{
		return s_lemma.compareTo(predicate.s_lemma);
	}
}