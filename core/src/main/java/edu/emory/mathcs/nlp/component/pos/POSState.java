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

import edu.emory.mathcs.nlp.component.common.node.NLPNode;
import edu.emory.mathcs.nlp.component.common.state.L2RState;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState extends L2RState
{
	private String[] ambiguity_classes;
	
	public POSState(NLPNode[] nodes, String[] ambiguityClasses)
	{
		super(nodes);
		setAmbiguityClasses(ambiguityClasses);
	}
	
	@Override
	protected String getLabel(NLPNode node)
	{
		return node.getPartOfSpeechTag();
	}
	
	@Override
	protected String setLabel(NLPNode node, String label)
	{
		String s = node.getPartOfSpeechTag();
		node.setPartOfSpeechTag(label);
		return s;
	}
	
	public String getAmbiguityClass(NLPNode node)
	{
		return ambiguity_classes[node.getID()];
	}
	
	public String[] getAmbiguityClasses()
	{
		return ambiguity_classes;
	}
	
	public void setAmbiguityClasses(String[] classes)
	{
		ambiguity_classes = classes;
	}
}
