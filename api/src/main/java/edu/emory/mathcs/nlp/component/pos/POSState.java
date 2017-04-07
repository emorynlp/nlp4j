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

import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.structure.dependency.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState<N extends AbstractNLPNode<N>> extends NLPState<N>
{
	protected String[] golds;
	protected int input;
	
	public POSState(List<N> nodes, boolean save_gold)
	{
		super(nodes, save_gold);
		input = 1;
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public void saveGold(List<N> nodes)
	{
		golds = nodes.stream().map(n -> n.setSyntacticTag(null)).toArray(String[]::new);
	}
	
	@Override
	public String getGoldLabel()
	{
		return golds[input];
	}
	
//	============================== TRANSITION ==============================
	
	@Override
	public void next(LabelMap map, float[] scores)
	{
		String label = map.getLabel(MLUtils.argmax(scores));
		
		setLabel(nodes[input++], map.getLabel(top2[0]));
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	@Override
	public N getNode(FeatureItem item)
	{
		N node = getNode(input, item.window);
		return getRelativeNode(item, node);
	}
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		if (0 <= top2[1] && scores[top2[0]] - scores[top2[1]] < 1)
			getInput().putFeat(NLPUtils.FEAT_POS_2ND, map.getLabel(top2[1]));

		super.next(map, top2, scores);
	}
}
