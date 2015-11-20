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
package edu.emory.mathcs.nlp.component.ner;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.L2RState;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERState extends L2RState
{
	public NERState(NLPNode[] nodes)
	{
		super(nodes);
	}
	
	@Override
	protected String getLabel(NLPNode node)
	{
		return node.getNamedEntityTag();
	}
	
	@Override
	protected String setLabel(NLPNode node, String label)
	{
		String s = node.getNamedEntityTag();
		node.setNamedEntityTag(label);
		return s;
	}

	@Override
	public void evaluate(Eval eval)
	{
		Int2ObjectMap<ObjectIntIntTriple<String>> gMap = BILOU.collectNamedEntityMap(oracle, String::toString, 1, nodes.length);
		Int2ObjectMap<ObjectIntIntTriple<String>> sMap = BILOU.collectNamedEntityMap(nodes , this::getLabel  , 1, nodes.length);
		((F1Eval)eval).add(countCorrect(sMap, gMap), sMap.size(), gMap.size());
	}
	
	private int countCorrect(Int2ObjectMap<ObjectIntIntTriple<String>> map1, Int2ObjectMap<ObjectIntIntTriple<String>> map2)
	{
		ObjectIntIntTriple<String> s2;
		int count = 0;
		
		for (Entry<ObjectIntIntTriple<String>> p1 : map1.int2ObjectEntrySet())
		{
			s2 = map2.get(p1.getKey());
			if (s2 != null && s2.o.equals(p1.getValue().o)) count++; 
		}
		
		return count;
	}
}
