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
package edu.emory.mathcs.nlp.common.util;

import java.util.Set;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ENUtils
{
	static private final Set<String> S_BE		= DSUtils.toHashSet("be","been","being","am","is","was","are","were","'m","'s","'re");
	static private final Set<String> S_BECOME	= DSUtils.toHashSet("become","becomes","became","becoming");
	static private final Set<String> S_GET		= DSUtils.toHashSet("get","gets","got","gotten","getting");

	static public final Set<String> S_NEGATION = DSUtils.toHashSet("never","not","n't","'nt","no","neither","non");
	static public final Set<String> S_RELATIVIZER = DSUtils.toHashSet("how","however","that","what","whatever","whatsoever","when","whenever","where","whereby","wherein","whereupon","wherever","which","whichever","whither","who","whoever","whom","whose","why");
	static public final Set<String> S_RELATIVIZER_LINK = DSUtils.toHashSet("0","that","when","where","whereby","wherein","whereupon","which","who","whom","whose");
	static public final Set<String> S_CORRELATIVE_CONJUNCTION = DSUtils.toHashSet("either","neither","whether","both");
	
//	static private final Set<String> S_DO		= DSUtils.toHashSet("do","does","did","done","doing");
//	static private final Set<String> S_HAVE		= DSUtils.toHashSet("have","has","had","having","'ve","'d");
//	static private final Set<String> S_BE_FINITE = DSUtils.toHashSet("am","is","was","are","were");

	/** @return {@code true} if the specific word form is either "be", "become", or "get". */
	static public boolean isPassiveAuxiliaryVerb(String form)
	{
		form = StringUtils.toLowerCase(form);
		return S_BE.contains(form) || S_BECOME.contains(form) || S_GET.contains(form);
	}
	
	static public boolean isNegation(String form)
	{
		form = StringUtils.toLowerCase(form);
		return S_NEGATION.contains(form);
	}
	
	static public boolean isRelativizer(String form)
	{
		form = StringUtils.toLowerCase(form);
		return S_RELATIVIZER.contains(form);
	}
	
	static public boolean isLinkingRelativizer(String form)
	{
		form = StringUtils.toLowerCase(form);
		return S_RELATIVIZER_LINK.contains(form);
	}
	
	static public boolean isCorrelativeConjunction(String form)
	{
		form = StringUtils.toLowerCase(form);
		return S_CORRELATIVE_CONJUNCTION.contains(form);
	}
}