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

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNSemantics implements Serializable
{
	private static final long serialVersionUID = 3804252052721753776L;
	
	private List<VNPredicate> l_predicates;
	
	public VNSemantics(Element eSemantics)
	{
		init(eSemantics);
	}
	
	private void init(Element eSemantics)
	{
		NodeList list = eSemantics.getElementsByTagName(VNXml.E_PRED);
		int i, size = list.getLength();
		Element ePredicate;
		
		l_predicates = new ArrayList<>();
		
		for (i=0; i<size; i++)
		{
			ePredicate = (Element)list.item(i);
			addPredicate(new VNPredicate(ePredicate));
		}
	}
	
	public List<VNPredicate> getPredicateList()
	{
		return l_predicates;
	}
	
	public VNPredicate getPredicdate(int index)
	{
		return l_predicates.get(index);
	}
	
	public int getPredicateSize()
	{
		return l_predicates.size();
	}
	
	public void addPredicate(VNPredicate predicate)
	{
		l_predicates.add(predicate);
	}
	
	@Override
	public String toString()
	{
		return toString(StringConst.SPACE);
	}
	
	public String toString(String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (VNPredicate predicate : l_predicates)
		{
			build.append(delim);
			build.append(predicate.toString());
		}
		
		return build.substring(delim.length());
	}
}