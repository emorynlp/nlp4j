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
package edu.emory.mathcs.nlp.common.treebank;

import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSLibEn implements POSTagEn
{
	static private final Set<String> S_PUNCTUATION = DSUtils.toHashSet(POS_COLON, POS_COMMA, POSTagEn.POS_PERIOD, POS_LQ, POS_RQ, POS_LRB, POS_RRB, POS_HYPH, POS_NFP, POS_SYM, POS_PUNC);
	static private final Set<String> S_RELATIVIZER = DSUtils.toHashSet(POS_WDT, POS_WP, POS_WPS, POS_WRB);
	
	private POSLibEn() {}
	
	static public boolean isNoun(String posTag)
	{
		return posTag.startsWith(POS_NN) || posTag.equals(CTLibEn.POS_PRP) || posTag.equals(CTLibEn.POS_WP);
	}
	
	static public boolean isCommonOrProperNoun(String posTag)
	{
		return posTag.startsWith(POS_NN);
	}
	
	static public boolean isPronoun(String posTag)
	{
		return posTag.equals(CTLibEn.POS_PRP) || posTag.equals(CTLibEn.POS_PRPS);
	}
	
	static public boolean isVerb(String posTag)
	{
		return posTag.startsWith(POS_VB);
	}
	
	static public boolean isAdjective(String posTag)
	{
		return posTag.startsWith(POS_JJ);
	}
	
	static public boolean isAdverb(String posTag)
	{
		return posTag.startsWith(POS_RB) || posTag.equals(POS_WRB);
	}
	
	static public boolean isRelativizer(String posTag)
	{
		return S_RELATIVIZER.contains(posTag);
	}
	
	static public boolean isPunctuation(String posTag)
	{
		return S_PUNCTUATION.contains(posTag);
	}
}