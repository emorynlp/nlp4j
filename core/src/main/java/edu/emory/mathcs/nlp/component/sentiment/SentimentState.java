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
package edu.emory.mathcs.nlp.component.sentiment;

import java.util.List;

import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.DOCState;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentState extends DOCState
{
	public SentimentState(List<NLPNode[]> document)
	{
		super(document, NLPUtils.FEAT_SENTIMENT);
	}

	@Override
	public void evaluate(Eval eval)
	{
		if (!((SentimentEval)eval).add(getOracle(), getLabel()))
			super.evaluate(eval);
	}
}
