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
package edu.emory.mathcs.nlp.component.answer;

import java.io.InputStream;
import java.util.Arrays;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AnswerExtractor extends OnlineComponent<AnswerState>
{
	private static final long serialVersionUID = -324154755172000520L;
	
	public AnswerExtractor() {}
	
	public AnswerExtractor(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}

	@Override
	protected AnswerState initState(NLPNode[] nodes)
	{
		int index = separator(nodes);
		NLPNode[] answer   = Arrays.copyOf(nodes, index);
		NLPNode[] question = Arrays.copyOfRange(nodes, index, nodes.length);
		return new AnswerState(answer, question);
	}
	
	public int separator(NLPNode[] nodes)
	{
		for (int i=2; i<nodes.length; i++)
			if (nodes[i].isID(0))
				return i;
		
		return -1;
	}

	@Override
	protected void postProcess(AnswerState state)
	{
		state.postProcess();
	}
}
