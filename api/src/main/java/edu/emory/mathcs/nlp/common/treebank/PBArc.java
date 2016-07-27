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
package edu.emory.mathcs.nlp.common.treebank;

import edu.emory.mathcs.nlp.common.collection.arc.AbstractArc;
import edu.emory.mathcs.nlp.common.propbank.PBArgument;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBArc extends AbstractArc<CTNode>
{
	private static final long serialVersionUID = 8603308004980285093L;

	public PBArc(CTNode node, String label)
	{
		super(node, label);
	}

	@Override
	public String toString()
	{
		return node.getTerminalID() + PBArgument.DELIM + label;
	}
	
	@Override
	public int compareTo(AbstractArc<CTNode> arc)
	{
		return node.compareTo(arc.getNode());
	}
}