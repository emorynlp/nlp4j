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

import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLabelCandidate implements Serializable
{
	private static final long serialVersionUID = 5579863219050051216L;
	
	private IntSet X_SHIFT;
	private IntSet NO_X;
	private IntSet NO_SHIFT_OR_PASS;
	private IntSet NOT_NO_REDUCE;
	private IntSet LEFT_ARC;
	private IntSet RIGHT_ARC;
	
	public DEPLabelCandidate()
	{
		X_SHIFT          = new IntOpenHashSet();
		NO_X             = new IntOpenHashSet();
		NO_SHIFT_OR_PASS = new IntOpenHashSet();
		NOT_NO_REDUCE    = new IntOpenHashSet();
		LEFT_ARC         = new IntOpenHashSet();
		RIGHT_ARC        = new IntOpenHashSet();
	}

	public void add(String label, int index)
	{
		DEPLabel lb = new DEPLabel(label);
		
		if (lb.isList(DEPState.LIST_SHIFT))
			X_SHIFT.add(index);
		
		if (lb.isArc(DEPState.ARC_LEFT))
			LEFT_ARC.add(index);
		else if (lb.isArc(DEPState.ARC_RIGHT))
			RIGHT_ARC.add(index);
		else
		{
			NO_X.add(index);
			if (!lb.isList(DEPState.LIST_REDUCE)) NO_SHIFT_OR_PASS.add(index);
		}
		
		if (!(lb.isArc(DEPState.ARC_NO) && lb.isList(DEPState.LIST_REDUCE)))
			NOT_NO_REDUCE.add(index);
	}
	
	public IntSet get(NLPNode stack, NLPNode input)
	{
		if (stack.getID() == 0)
			return X_SHIFT;
		
		if (stack.isDescendantOf(input))
			return NO_X;
		
		if (input.isDescendantOf(stack))
			return stack.hasDependencyHead() ? NO_X : NO_SHIFT_OR_PASS;
		
		if (!stack.hasDependencyHead())
			return NOT_NO_REDUCE;

		return null;
	}
	
	public int getLabelIndex(NLPNode stack, NLPNode input, float[] scores)
	{
		IntSet candidates = get(stack, input);
		return MLUtils.argmax(scores, candidates);
	}
	
	public IntSet getLeftArcs()
	{
		return LEFT_ARC;
	}
	
	public IntSet getRightArcs()
	{
		return RIGHT_ARC;
	}
}
