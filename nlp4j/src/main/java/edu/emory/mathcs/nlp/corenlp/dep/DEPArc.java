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
package edu.emory.mathcs.nlp.corenlp.dep;

import edu.emory.mathcs.nlp.corenlp.component.node.AbstractArc;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPArc extends AbstractArc<DEPNode>
{
	private static final long serialVersionUID = -9099516205158258095L;

	public DEPArc(DEPNode node, String label)
	{
		set(node, label);
	}

	@Override
	public String toString()
	{
		return n_node.getID() + DELIM + s_label;
	}
	
	@Override
	public int compareTo(AbstractArc<DEPNode> arc)
	{
		return n_node.compareTo(arc.getNode());
	}
}