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
package edu.emory.mathcs.nlp.component.pos;

import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.state.L2RState;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState<N extends AbstractNLPNode<N>> extends L2RState<N>
{
	public POSState(N[] nodes)
	{
		super(nodes);
	}
	
	@Override
	protected String getLabel(N node)
	{
		return node.getPartOfSpeechTag();
	}
	
	@Override
	protected String setLabel(N node, String label)
	{
		String s = node.getPartOfSpeechTag();
		node.setPartOfSpeechTag(label);
		return s;
	}
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		if (0 <= top2[1] && scores[top2[0]] - scores[top2[1]] < 1)
			getInput().putFeat(NLPUtils.FEAT_POS_2ND, map.getLabel(top2[1]));

		super.next(map, top2, scores);
	}
}
