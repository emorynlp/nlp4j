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

import edu.emory.mathcs.nlp.structure.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPNode extends AbstractNLPNode<NLPNode>
{
	private static final long serialVersionUID = 5522467283393796925L;

	public NLPNode()
	{
		super();
	}
	
	public NLPNode(int id, String form)
	{
		super(id, form);
	}
	
	public NLPNode(int token_id, String word_form, String syntactic_tag)
	{
		super(token_id, word_form, syntactic_tag);
	}
	
	public NLPNode(int token_id, String word_form, String lemma, String syntactic_tag, FeatMap feat_map)
	{
		super(token_id, word_form, lemma, syntactic_tag, feat_map);
	}
	
	public NLPNode(int token_id, String word_form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		super(token_id, word_form, lemma, syntactic_tag, named_entity_tag, feat_map);
	}
	
	public NLPNode(int token_id, String word_form, String lemma, String syntactic_tag, FeatMap feat_map, NLPNode parent, String dependency_label)
	{
		super(token_id, word_form, lemma, syntactic_tag, feat_map, parent, dependency_label);
	}
	
	public NLPNode(int token_id, String word_form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map, NLPNode parent, String dependency_label)
	{
		super(token_id, word_form, lemma, syntactic_tag, named_entity_tag, feat_map, parent, dependency_label);
	}
	
	@Override
	public NLPNode self() {return this;}
}