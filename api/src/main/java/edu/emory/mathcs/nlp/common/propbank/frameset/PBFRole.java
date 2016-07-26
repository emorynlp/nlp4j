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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.common.verbnet.VNLib;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBFRole implements Serializable, Comparable<PBFRole>
{
	private static final long serialVersionUID = 6664227730485311810L;
	
	private Map<String,String> m_vnroles;
	private String             s_argumentNumber;
	private String             s_functionTag;
	private String             s_description;
	
	public PBFRole(Element eRole)
	{
		init(eRole);
	}
	
	private void init(Element eRole)
	{
		m_vnroles = new HashMap<>();
		
		setArgumentNumber(XMLUtils.getTrimmedAttribute(eRole, PBFXml.A_N));
		setFunctionTag(XMLUtils.getTrimmedAttribute(eRole, PBFXml.A_F));
		setDescription(XMLUtils.getTrimmedAttribute(eRole, PBFXml.A_DESCR));
		initVNRoles(eRole.getElementsByTagName(PBFXml.E_VNROLE));
	}
	
	private void initVNRoles(NodeList list)
	{
		int i, size = list.getLength();
		String vncls, vntheta;
		Element eVNRole;
		
		for (i=0; i<size; i++)
		{
			eVNRole = (Element)list.item(i);
			vncls   = XMLUtils.getTrimmedAttribute(eVNRole, PBFXml.A_VNCLS);
			vntheta = XMLUtils.getTrimmedAttribute(eVNRole, PBFXml.A_VNTHETA);
			
			if (!vncls.equals(StringConst.EMPTY) && !vntheta.equals(StringConst.EMPTY))
				putVNRole(vncls, vntheta);
		}
	}
	
	public String getArgumentNumber()
	{
		return s_argumentNumber;
	}

	public String getFunctionTag()
	{
		return s_functionTag;
	}

	public String getDescription()
	{
		return s_description;
	}
	
	public Set<String> getVNClasseSet()
	{
		return m_vnroles.keySet();
	}
	
	/** @return {@code null} if not exist. */
	public String getVNTheta(String vncls)
	{
		return m_vnroles.get(vncls);
	}

	public void setArgumentNumber(String argNumber)
	{
		s_argumentNumber = StringUtils.toUpperCase(argNumber);
	}

	public void setFunctionTag(String functionTag)
	{
		s_functionTag = StringUtils.toUpperCase(functionTag);
	}

	public void setDescription(String description)
	{
		s_description = description;
	}
	
	public void putVNRole(String vncls, String vntheta)
	{
		if (vncls.length() > 1)
			m_vnroles.put(VNLib.stripVerbNetClassName(vncls), StringUtils.toLowerCase(vntheta));
	}
	
	public boolean isArgumentNumber(String number)
	{
		return s_argumentNumber.equals(number);
	}
	
	public boolean isFunctionTag(String tag)
	{
		return s_functionTag.equals(tag);
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(getArgKey());
		build.append(": ");
		build.append(s_description);
		
		for (String vncls : m_vnroles.keySet())
		{
			build.append(", ");
			build.append(vncls);
			build.append(":");
			build.append(m_vnroles.get(vncls));
		}
		
		return build.toString();
	}
	
	public String getArgKey()
	{
		StringBuilder build = new StringBuilder();
		build.append(s_argumentNumber);
		
		if (!s_functionTag.equals(StringConst.EMPTY))
		{
			build.append("-");
			build.append(s_functionTag);
		}
		
		return build.toString();
	}
	
	@Override
	public int compareTo(PBFRole role)
	{
		int n = s_argumentNumber.compareTo(role.s_argumentNumber);
		return (n != 0) ? n : s_functionTag.compareTo(role.s_functionTag);
	}
}