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
package edu.emory.mathcs.nlp.component.morph.english;

import java.util.Map;
import java.util.Set;

import edu.emory.mathcs.nlp.component.morph.util.AbstractAffixReplacer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishSuffixReplacer extends AbstractAffixReplacer
{
	boolean b_doubleConsonants;
	
	public EnglishSuffixReplacer(String basePOS, String affixForm, String[] replacements, boolean doubleConsonants)
	{
		super(basePOS, affixForm, replacements);
		b_doubleConsonants = doubleConsonants;
	}
	
	@Override
	public String getBaseForm(Map<String,Set<String>> baseMap, String form)
	{
		return getBaseForm(baseMap.get(s_basePOS), form);
	}

	@Override
	public String getBaseForm(Set<String> baseSet, String form)
	{
		if (!form.endsWith(s_affixForm)) return null;
		
		int    subLen = form.length() - s_affixForm.length();
		String stem   = form.substring(0, subLen);
		String base   = getBaseFormAux(baseSet, stem);
		
		if (b_doubleConsonants && base == null && isDoubleConsonant(form, subLen))
		{
			stem = form.substring(0, subLen-1);
			base = getBaseFormAux(baseSet, stem);
		}
		
		return base;
	}
	
	private String getBaseFormAux(Set<String> baseSet, String stem)
	{
		String base;
		
		for (String replacement : s_replacements)
		{
			base = stem + replacement;
				
			if (baseSet.contains(base))
				return base;
		}
		
		return null;
	}
	
	private boolean isDoubleConsonant(String form, int subLen)
	{
		return subLen >= 4 && form.charAt(subLen-2) == form.charAt(subLen-1);
	}
}
