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
package edu.emory.mathcs.nlp.emorynlp.pos;

import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.emorynlp.utils.feature.Field;
import edu.emory.mathcs.nlp.emorynlp.utils.node.FeatMap;
import edu.emory.mathcs.nlp.emorynlp.utils.node.NLPNode;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSNode extends NLPNode
{
	private static final long serialVersionUID = -8563108117037742010L;
	protected String pos_tag;
	protected String lemma;
	
	public POSNode() {}
	
	public POSNode(String form)
	{
		super(form);
	}
	
	public POSNode(String form, String tag)
	{
		super(form);
		setPOSTag(tag);
	}
	
	public POSNode(int id, String form, String lemma, String tag, FeatMap map)
	{
		super(id, form, map);
		setLemma(lemma);
		setPOSTag(tag);
	}
	
//	============================== POS TAG ==============================
	
	public String getPOSTag()
	{
		return pos_tag;
	}

	/** @return the previous pos-tag. */
	public String setPOSTag(String tag)
	{
		String t = pos_tag;
		pos_tag = tag;
		return t;
	}
	
	public boolean isPOSTag(String tag)
	{
		return tag.equals(pos_tag);
	}
	
	public boolean isPOSTag(Pattern pattern)
	{
		return pattern.matcher(pos_tag).find();
	}
	
//	============================== LEMMA ==============================
	
	public String getLemma()
	{
		return lemma;
	}

	/** @return the previous lemma. */
	public String setLemma(String lemma)
	{
		String t = lemma;
		this.lemma = lemma;
		return t;
	}
	
	public boolean isLemma(String lemma)
	{
		return lemma.equals(this.lemma);
	}
	
//	============================== HELPERS ==============================
	
	@Override
	public String getValue(Field field)
	{
		switch (field)
		{
		case lemma  : return getLemma();
		case pos_tag: return getPOSTag();
		default: return super.getValue(field);
		}
	}
	
	@Override
	public String toString() 
	{
		return word_form+"\t"+pos_tag;
	}
}
