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
package edu.emory.mathcs.nlp.structure.node;

import java.util.List;

import edu.emory.mathcs.nlp.structure.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultNode extends AbstractNode<DefaultNode>
{
	private static final long serialVersionUID = 893266647350424699L;

	public DefaultNode()
	{
		this(null, null);
	}
	
	public DefaultNode(String syntactic_tag, String form)
	{
		this(-1, form, null, syntactic_tag, null, new FeatMap());
	}
	
	public DefaultNode(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		super(token_id, form, lemma, syntactic_tag, named_entity_tag, feat_map);
	}

//	============================== Abstract Methods ==============================

	@Override
	public DefaultNode self() { return this; }
	
	@Override
	public int getChildIndex(DefaultNode node) { return children.indexOf(node); }
	
	@Override
	protected int getDefaultIndex(List<DefaultNode> list, DefaultNode node) { return list.size(); }
}
