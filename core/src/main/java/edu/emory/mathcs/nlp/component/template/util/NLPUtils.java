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
package edu.emory.mathcs.nlp.component.template.util;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	static public String joinWordForms(NLPNode[] nodes, String delim)
	{
		return Joiner.join(nodes, delim, 1, nodes.length, NLPNode::getWordForm);
	}
	
	static public Set<String> getBagOfWords(NLPNode[] nodes, Field... fields)
	{
		Set<String> set = new HashSet<>();
		StringJoiner join;
		
		for (int i=1; i<nodes.length; i++)
		{
			join = new StringJoiner("_");
			for (Field f : fields) join.add(nodes[i].getValue(f));
			set.add(join.toString());
		}

		return set;
	}
	
	static public Object2IntMap<String> getBagOfWordsCount(NLPNode[] nodes, Field... fields)
	{
		Object2IntMap<String> map = new Object2IntOpenHashMap<>();
		StringJoiner join;
		
		for (int i=1; i<nodes.length; i++)
		{
			join = new StringJoiner("_");
			for (Field f : fields) join.add(nodes[i].getValue(f));
			FastUtils.increment(map, join.toString());
		}
			
		return map;
	}
}
