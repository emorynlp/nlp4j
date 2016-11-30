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
package edu.emory.mathcs.nlp.structure.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.template.feature.Field;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultFields implements Serializable
{
	private static final long serialVersionUID = -1265329243225207527L;
	
	// default fields
	protected int     token_id;
	protected String  form;
	protected String  lemma;
	protected String  syntactic_tag;
	protected String  named_entity_tag;
	protected FeatMap feat_map;

	// inferred fields
	protected String form_lowercase;
	protected String form_simplified;
	protected String form_simplified_lowercase;
	
//	============================== Constructors ==============================
	
	public DefaultFields()
	{
		this(-1, null, null, null, null, new FeatMap());
	}
	
	public DefaultFields(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		init(token_id, form, lemma, syntactic_tag, named_entity_tag, feat_map);
	}
	
	public void init(int token_id, String form, String lemma, String syntactic_tag, String named_entity_tag, FeatMap feat_map)
	{
		setTokenID(token_id);
		setForm(form);
		setLemma(lemma);
		setSyntacticTag(syntactic_tag);
		setNamedEntityTag(named_entity_tag);
		setFeatMap(feat_map);
	}
	
	/** @return the value of the specific field. */
	public String get(Field field)
	{
		switch (field)
		{
		case form: return getForm();
		case form_lowercase: return getFormLowercase();
		case form_simplified: return getFormSimplified();
		case form_simplified_lowercase: return getFormSimplifiedLowercase();
		case lemma: return getLemma();
		case syntactic_tag: return getSyntacticTag();
		case named_entity_tag: return getNamedEntityTag();
		default: return null;
		}
	}

	public int getTokenID()
	{
		return token_id;
	}
	
	public String getForm()
	{
		return form;
	}
	
	public String getFormLowercase()
	{
		return form_lowercase;
	}
	
	/** @see StringUtils#toSimplifiedWordForm(String). */
	public String getFormSimplified()
	{
		return form_simplified;
	}
	
	public String getFormSimplifiedLowercase()
	{
		return form_simplified_lowercase;
	}
	
	public String getLemma()
	{
		return lemma;
	}
	
	public String getSyntacticTag()
	{
		return syntactic_tag;
	}
	
	public String getNamedEntityTag()
	{
		return named_entity_tag;
	}
	
	public FeatMap getFeatMap()
	{
		return feat_map;
	}
	
	public String getFeat(String key)
	{
		return feat_map.get(key);
	}
	
	public void setTokenID(int id)
	{
		this.token_id = id;
	}
	
	public void setForm(String form)
	{
		if (form == null)
			this.form = form_lowercase = form_simplified = form_simplified_lowercase = null;
		else
		{
			this.form                 = form;
			form_lowercase            = StringUtils.toLowerCase(form);
			form_simplified           = StringUtils.toSimplifiedForm(form);
			form_simplified_lowercase = StringUtils.toLowerCase(form_simplified);
		}
	}
	
	public void setLemma(String lemma)
	{
		this.lemma = lemma;
	}
	
	public void setSyntacticTag(String tag)
	{
		this.syntactic_tag = tag;
	}
	
	public void setNamedEntityTag(String tag)
	{
		this.named_entity_tag = tag;
	}
	
	public void setFeatMap(FeatMap map)
	{
		this.feat_map = map;
	}
	
	public String putFeat(String key, String value)
	{
		return feat_map.put(key, value);
	}
	
	public String removeFeat(String key)
	{
		return feat_map.remove(key);
	}
	
	public boolean isTokenID(int id)
	{
		return this.token_id == id;
	}
	
	public boolean isForm(String form)
	{
		return form.equals(this.form);
	}
	
	public boolean isForm(Pattern pattern)
	{
		return pattern.matcher(form).find();
	}
	
	public boolean isFormLowercase(String form)
	{
		return form.equals(form_lowercase);
	}
	
	public boolean isFormSimplified(String form)
	{
		return form.equals(form_simplified);
	}
	
	public boolean isFormSimplifiedLowercase(String form)
	{
		return form.equals(form_simplified_lowercase);
	}
	
	public boolean isLemma(String lemma)
	{
		return lemma.equals(this.lemma);
	}
	
	public boolean isSyntacticTag(String tag)
	{
		return tag.equals(syntactic_tag);
	}
	
	public boolean isSyntacticTag(Pattern pattern)
	{
		return pattern.matcher(syntactic_tag).find();
	}
	
	public boolean isSyntacticTag(Collection<String> set)
	{
		return set.contains(syntactic_tag);
	}
	
	public boolean isSyntacticTag(String... tags)
	{
		return Arrays.stream(tags).anyMatch(tag -> isSyntacticTag(tag));
	}
	
	public boolean isNamedEntityTag(String tag)
	{
		return tag.equals(named_entity_tag);
	}
}
