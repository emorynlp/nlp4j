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
package edu.emory.mathcs.nlp.structure.dependency;

import java.util.Iterator;
import java.util.List;

import org.magicwerk.brownies.collections.GapList;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNLPGraph<N extends AbstractNLPNode<N>> implements Iterable<N>
{
	protected List<N> nodes;
	
	public AbstractNLPGraph()
	{
		nodes = new GapList<>();
		nodes.add(createRoot());
	}
	
	protected abstract N createRoot();
	
	public N getRoot()
	{
		return nodes.get(0);
	}
	
	/** @return the index'th node if exists; otherwise, null. */
	public N get(int index)
	{
		return DSUtils.isRange(nodes, index) ? nodes.get(index) : null;
	}
	
	public void add(N node)
	{
		nodes.add(node);
	}
	
	public int size()
	{
		return nodes.size();
	}
	
	public List<N> getNodes()
	{
		return nodes;
	}
	
	@Override
	public String toString()
	{
		return Joiner.join(nodes, "\n", 1);
	}

	@Override
	public Iterator<N> iterator()
	{
		Iterator<N> it = new Iterator<N>()
		{
			private int current_index = 1;
			
			@Override
			public boolean hasNext()
			{
				return current_index < size();
			}
			
			@Override
			public N next()
			{
				return get(current_index++);
			}
			
			@Override
			public void remove()
			{
				nodes.remove(--current_index);
			}
		};
		
		return it;
	}
}
