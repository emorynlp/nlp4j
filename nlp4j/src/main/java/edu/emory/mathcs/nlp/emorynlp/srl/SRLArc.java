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
package edu.emory.mathcs.nlp.emorynlp.srl;

import edu.emory.mathcs.nlp.common.collection.arc.AbstractArc;
import edu.emory.mathcs.nlp.common.propbank.PBLib;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLArc extends AbstractArc<NLPNode>
{
	private static final long serialVersionUID = -5382621610169266179L;
	private String function_tag;
	
	public SRLArc(NLPNode node, String label)
	{
		this(node, label, null);
	}
	
	public SRLArc(NLPNode node, String label, String functionTag)
	{
		super(node, label);
		setFunctionTag(functionTag);
	}
	
	public SRLArc(SRLArc arc)
	{
		this(arc.getNode(), arc.getLabel(), arc.getFunctionTag());
	}
	
	public String getFunctionTag()
	{
		return function_tag;
	}
	
	public void setFunctionTag(String tag)
	{
		function_tag = tag;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(node.getID());
		build.append(ARC_DELIM);
		build.append(label);
		
		if (function_tag != null)
		{
			build.append(PBLib.DELIM_FUNCTION_TAG);
			build.append(function_tag);
		}
		
		return build.toString();
	}

	@Override
	public int compareTo(AbstractArc<NLPNode> arc)
	{
		return label.compareTo(arc.getLabel());
	}
}