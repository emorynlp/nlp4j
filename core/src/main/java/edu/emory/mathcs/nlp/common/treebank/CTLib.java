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

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTLib
{
	static public String toForms(List<CTNode> tokens, int beginIndex, int endIndex, String delim)
	{
		StringBuilder build = new StringBuilder();
		int i;
		
		for (i=beginIndex; i<endIndex; i++)
		{
			build.append(delim);
			build.append(tokens.get(i).getWordForm());
		}
		
		return build.substring(delim.length());
	}
	
	static public Predicate<CTNode> matchC(String constituentTag)
	{
		return node -> node.isConstituentTag(constituentTag);
	}
	
	static public Predicate<CTNode> matchCo(String... constituentTags)
	{
		return node -> node.isConstituentTagAny(constituentTags);
	}
	
	static public Predicate<CTNode> matchCo(Set<String> constituentTags)
	{
		return node -> constituentTags.contains(node.getConstituentTag());
	}
	
	static public Predicate<CTNode> matchCp(String constituentPrefix)
	{
		return node -> node.getConstituentTag().startsWith(constituentPrefix);
	}
	
	static public Predicate<CTNode> matchCF(String constituentTag, String functionTag)
	{
		return node -> node.isConstituentTag(constituentTag) && node.hasFunctionTag(functionTag);
	}
	
	static public Predicate<CTNode> matchCFa(String constituentTag, String... functionTags)
	{
		return node -> node.isConstituentTag(constituentTag) && node.hasFunctionTagAll(functionTags);
	}
	
	static public Predicate<CTNode> matchCFo(String constituentTag, String... functionTags)
	{
		return node -> node.isConstituentTag(constituentTag) && node.hasFunctionTagAny(functionTags);
	}
	
	static public Predicate<CTNode> matchF(String functionTag)
	{
		return node -> node.hasFunctionTag(functionTag);
	}
	
	static public Predicate<CTNode> matchFa(String... functionTags)
	{
		return node -> node.hasFunctionTagAll(functionTags);
	}
	
	static public Predicate<CTNode> matchFo(String... functionTags)
	{
		return node -> node.hasFunctionTagAny(functionTags);
	}
	
	static public Predicate<CTNode> matchP(Pattern constituentPattern)
	{
		return node -> node.matchesConstituentTag(constituentPattern);
	}
	
	static public Predicate<CTNode> matchPFa(Pattern constituentPattern, String... functionTags)
	{
		return node -> node.matchesConstituentTag(constituentPattern) && node.hasFunctionTagAll(functionTags);		
	}
	
	static public Predicate<CTNode> matchPFo(Pattern constituentPattern, String... functionTags)
	{
		return node -> node.matchesConstituentTag(constituentPattern) && node.hasFunctionTagAny(functionTags);		
	}
}
