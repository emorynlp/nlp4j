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

import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.component.morph.util.AbstractAffixMatcher;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishDerivation
{
	List<AbstractAffixMatcher> suffix_matchers;
	
	public EnglishDerivation(List<AbstractAffixMatcher> affixMatchers)
	{
		init(affixMatchers);
	}
	
	private void init(List<AbstractAffixMatcher> affixMatchers)
	{
		suffix_matchers = affixMatchers;
		
		if (suffix_matchers == null)
			throw new IllegalArgumentException("The suffix matcher list must not be null.");
	}
	
	public List<AbstractAffixMatcher> getSuffixMatchers()
	{
		return suffix_matchers;
	}
	
	public String getBaseForm(String lemma, Set<String> baseSet)
	{
		String base;
		
		for (AbstractAffixMatcher matcher : suffix_matchers)
		{
			base = matcher.getBaseForm(baseSet, lemma);
			if (base != null) return base;
		}
		
		return null;
	}
}
