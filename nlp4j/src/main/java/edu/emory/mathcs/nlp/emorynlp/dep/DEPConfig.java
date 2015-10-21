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
package edu.emory.mathcs.nlp.emorynlp.dep;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.emorynlp.utils.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.utils.reader.TSVIndex;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPConfig extends NLPConfig<DEPNode>
{
	public DEPConfig() {super();}
	
	public DEPConfig(InputStream in)
	{
		super(in);
	}
	
	@Override
	public TSVIndex<DEPNode> getTSVIndex()
	{
		Element eReader = XMLUtils.getFirstElementByTagName(xml, TSV);
		Object2IntMap<String> map = getFieldMap(eReader);
		
		int form   = map.get(FIELD_FORM);
		int pos    = map.get(FIELD_POS);
		int lemma  = map.get(FIELD_LEMMA);
		int feats  = map.get(FIELD_FEATS);
		int headID = map.getOrDefault(FIELD_HEADID, -1);
		int deprel = map.getOrDefault(FIELD_DEPREL, -1);
		
		return new DEPIndex(form, lemma, pos, feats, headID, deprel);
	}
}
