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
package edu.emory.mathcs.nlp.component.doc;

import java.io.InputStream;

import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCConfig extends NLPConfig
{
	private String feat_key;
	public DOCConfig() {}
	
	public DOCConfig(InputStream in)
	{
		super(in);
		setFeatKey(XMLUtils.getTextContentFromFirstElementByTagName(xml, "feat_key"));
	}
	
	public String getFeatKey()
	{
		return feat_key;
	}
	
	public void setFeatKey(String key)
	{
		feat_key = key;
	}
}
