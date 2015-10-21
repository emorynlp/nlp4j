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
package edu.emory.mathcs.nlp.emorynlp.ner;

import edu.emory.mathcs.nlp.emorynlp.pos.POSNode;
import edu.emory.mathcs.nlp.emorynlp.utils.node.FeatMap;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERNode extends POSNode
{
	private static final long serialVersionUID = -2055782375001848433L;
	protected String named_entity_tag; 
	
//	====================================== Constructors ======================================
	
	public NERNode(int id, String form, String lemma, String posTag, FeatMap feats)
	{
		super(id, form, lemma, posTag, feats);
	}
	
	public NERNode(int id, String form, String lemma, String posTag, FeatMap feats, String namedEntityTag)
	{
		super(id, form, lemma, posTag, feats);
	}
	
//	============================== POS TAG ==============================
	
	public String getNamedEntityTag()
	{
		return named_entity_tag;
	}

	/** @return the previous named entity tag. */
	public String setNamedEntityTag(String tag)
	{
		String t = named_entity_tag;
		named_entity_tag = tag;
		return t;
	}
	
	public boolean isNamedEntityTag(String tag)
	{
		return tag.equals(named_entity_tag);
	}
}