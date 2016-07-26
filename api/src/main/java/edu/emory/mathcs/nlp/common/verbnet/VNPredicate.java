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
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNPredicate implements Serializable
{
	private static final long serialVersionUID = -2630032273928451395L;
	
	private List<VNArgument> l_arguments;
	private String           s_value;
	private boolean          b_negation;
	
	public VNPredicate(Element ePredicate)
	{
		init(ePredicate);
	}
	
	private void init(Element ePredicate)
	{
		initAttributes(ePredicate);
		initArguments (ePredicate);
	}
	
	private void initAttributes(Element ePredicate)
	{
		setValue(XMLUtils.getTrimmedAttribute(ePredicate, VNXml.A_VALUE));
		
		String bool = XMLUtils.getTrimmedAttribute(ePredicate, VNXml.A_BOOL);
		setNegation(bool.equals(StringConst.EXCLAMATION));
	}
	
	private void initArguments(Element ePredicate)
	{
		NodeList list = ePredicate.getElementsByTagName(VNXml.E_ARG);
		int i, size = list.getLength();
		Element eArgument;
		
		l_arguments = new ArrayList<>();
		
		for (i=0; i<size; i++)
		{
			eArgument = (Element)list.item(i);
			addArgument(new VNArgument(eArgument));
		}
	}
	
	public String getValue()
	{
		return s_value;
	}
	
	public List<VNArgument> getArgumentList()
	{
		return l_arguments;
	}
	
	public VNArgument getArgument(int index)
	{
		return l_arguments.get(index);
	}
	
	public int getArgumentSize()
	{
		return l_arguments.size();
	}
	
	public void setValue(String value)
	{
		s_value = value;
	}
	
	public void setNegation(boolean negation)
	{
		b_negation = negation;
	}
	
	public void addArgument(VNArgument argument)
	{
		l_arguments.add(argument);
	}
	
	public boolean isNegation()
	{
		return b_negation;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (VNArgument argument: l_arguments)
		{
			build.append(StringConst.COMMA);
			build.append(argument.getValue());
		}
		
		String s = s_value + StringConst.LRB + build.substring(1) + StringConst.RRB;
		if (b_negation) s = "not("+s+")";
		
		return s;
	}
}