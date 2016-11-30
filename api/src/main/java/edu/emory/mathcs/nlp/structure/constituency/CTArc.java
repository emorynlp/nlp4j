/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.structure.constituency;

import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.util.Arc;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTArc extends Arc<CTNode>
{
	private static final long serialVersionUID = -7336832085506640932L;

	public CTArc(CTArc arc)
	{
		this(arc.node, arc.label);
	}
	
	public CTArc(CTNode node, String label)
	{
		super(node, label);
	}
	
	@Override
	public String toString()
	{
		return node.getTerminalID() + PBArgument.DELIM + label;
	}
}
