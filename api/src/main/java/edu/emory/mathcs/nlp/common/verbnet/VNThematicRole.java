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

import java.io.Serializable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNThematicRole implements Serializable
{
	private static final long serialVersionUID = 8897477637710084762L;
	
	private String  s_thematicRole;
	private boolean b_plural;
	
	public VNThematicRole(Element element, String role)
	{
		setThematicRole(role);
		initSynrestr(element);
	}
	
	private void initSynrestr(Element element)
	{
		NodeList list = element.getElementsByTagName(VNXml.E_SYNRESTR);
		int i, size = list.getLength();
		String value, type;
		Element eSynrestr;
		
		for (i=0; i<size; i++)
		{
			eSynrestr = (Element)list.item(i);
			type  = XMLUtils.getTrimmedAttribute(eSynrestr, VNXml.A_TYPE);
			value = XMLUtils.getTrimmedAttribute(eSynrestr, VNXml.A_VALUE_CAP);
			
			setPlural(type.equals(VNXml.SYNRESTR_TYPE_PLURAL) && value.equals(StringConst.PLUS));
		}
	}
	
	public String getThematicRole()
	{
		return s_thematicRole;
	}
	
	public boolean isPlural()
	{
		return b_plural;
	}

	public void setThematicRole(String role)
	{
		s_thematicRole = role;
	}
	
	public void setPlural(boolean plural)
	{
		b_plural = plural;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_thematicRole);
		if (b_plural) build.append(StringConst.PLUS);
		
		return build.toString();
	}
}