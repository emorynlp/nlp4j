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

import edu.emory.mathcs.nlp.component.ner.NERState;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AnswerState extends NERState
{
	private NLPNode[] question;
	
	public AnswerState(NLPNode[] answer, NLPNode[] question)
	{
		super(answer);
		setQuestion(question);
	}
	
//	============================== ORACLE ==============================

	@Override
	protected String getLabel(NLPNode node)
	{
		return node.getAnswerTag();
	}
	
	@Override
	protected String setLabel(NLPNode node, String label)
	{
		String s = node.getAnswerTag();
		node.setAnswerTag(label);
		return s;
	}
	
//	============================== QUESTION ==============================

	public NLPNode[] getQuestion()
	{
		return question;
	}
	
	public void setQuestion(NLPNode[] question)
	{
		this.question = question;
	}
}
