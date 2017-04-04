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
package edu.emory.mathcs.nlp.structure.dependency;

import edu.emory.mathcs.nlp.structure.util.Arc;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPArc<N extends AbstractNLPNode<N>> extends Arc<N>
{
	private static final long serialVersionUID = -9099516205158258095L;
	private double weight;
	
	public NLPArc(N node, String label)
	{
		super(node, label);
	}
	
	public double getWeight()
	{
		return weight;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	@Override
	public String toString()
	{
		return node.getTokenID() + LABEL_DELIM + label;
	}
	
	@Override
	public int compareTo(Arc<N> arc)
	{
		return node.compareTo(arc.getNode());
	}
}