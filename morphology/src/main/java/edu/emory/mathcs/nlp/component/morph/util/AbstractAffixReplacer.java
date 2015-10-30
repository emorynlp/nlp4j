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
package edu.emory.mathcs.nlp.component.morph.util;
import java.util.Map;
import java.util.Set;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractAffixReplacer
{
	protected String   s_basePOS;
	protected String   s_affixForm;
	protected String[] s_replacements;
	
	public AbstractAffixReplacer(String basePOS, String affixForm, String[] replacements)
	{
		s_basePOS      = basePOS;
		s_affixForm    = affixForm;
		s_replacements = replacements;
	}
	
	public String getBasePOS()
	{
		return s_basePOS;
	}
	
	/**
	 * Returns the base morpheme of the word form if exists; otherwise, {@code null}.
	 * @param form the word-form in lower-case.
	 * @return the base morpheme of the word form if exists; otherwise, {@code null}.
	 */
	abstract public String getBaseForm(Map<String,Set<String>> baseMap, String form);

	/**
	 * Returns the base morpheme of the word form if exists; otherwise, {@code null}.
	 * @param form the word-form in lower-case.
	 * @return the base morpheme of the word form if exists; otherwise, {@code null}.
	 */
	abstract public String getBaseForm(Set<String> baseSet, String form);
}
