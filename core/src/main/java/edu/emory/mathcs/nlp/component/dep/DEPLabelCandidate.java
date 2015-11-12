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
package edu.emory.mathcs.nlp.component.dep;

import java.io.Serializable;

import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;
import edu.emory.mathcs.nlp.component.zzz.util.LabelCandidate;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLabelCandidate implements Serializable, DEPTransition
{
	private static final long serialVersionUID = 5579863219050051216L;
	private LabelCandidate X_SHIFT;
	private LabelCandidate NO_X;
	private LabelCandidate NO_SHIFT_OR_PASS;
	private LabelCandidate NOT_NO_REDUCE;
	private LabelCandidate LEFT_ARC;
	private LabelCandidate RIGHT_ARC;
	
	public DEPLabelCandidate()
	{
		X_SHIFT          = new LabelCandidate();
		NO_X             = new LabelCandidate();
		NO_SHIFT_OR_PASS = new LabelCandidate();
		NOT_NO_REDUCE    = new LabelCandidate();
		LEFT_ARC         = new LabelCandidate();
		RIGHT_ARC        = new LabelCandidate();
	}

	public void add(DEPLabel label, int index)
	{
		if (label.isList(LIST_SHIFT))
			X_SHIFT.add(index);
		
		if (label.isArc(ARC_NO))
		{
			NO_X.add(index);
			if (!label.isList(LIST_REDUCE)) NO_SHIFT_OR_PASS.add(index);
		}
		else if (label.isArc(ARC_LEFT))
			LEFT_ARC.add(index);
		else if (label.isArc(ARC_RIGHT))
			RIGHT_ARC.add(index);
		
		if (!(label.isArc(ARC_NO) && label.isList(LIST_REDUCE)))
			NOT_NO_REDUCE.add(index);
	}
	
	public int[] get(NLPNode stack, NLPNode input)
	{
		if (stack.getID() == 0)
			return X_SHIFT.get();
		else if (stack.isDescendantOf(input))
			return NO_X.get();
		else if (input.isDescendantOf(stack))
			return stack.hasDependencyHead() ? NO_X.get() : NO_SHIFT_OR_PASS.get();
		else if (!stack.hasDependencyHead())
			return NOT_NO_REDUCE.get();
		else
			return null;
	}
	
	public int[] getLeftArcs()
	{
		return LEFT_ARC.get();
	}
	
	public int[] getRightArcs()
	{
		return RIGHT_ARC.get();
	}
}
