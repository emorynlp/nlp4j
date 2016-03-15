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

import java.util.Arrays;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState extends NLPState
{
	public static final String ARC_LEFT    = "L";
	public static final String ARC_RIGHT   = "R";
	public static final String ARC_NO      = "N";
	public static final String LIST_SHIFT  = "S";
	public static final String LIST_REDUCE = "R";
	public static final String LIST_PASS   = "P";
	
	private DEPArc[]     oracle;
	private IntArrayList stack;
	private IntArrayList inter;
	private int          input;
	
	public DEPState(NLPNode[] nodes)
	{
		super(nodes);
		stack = new IntArrayList();
		inter = new IntArrayList();
		input = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================

	@Override
	public boolean saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> n.clearDependencies()).toArray(DEPArc[]::new);
		return Arrays.stream(oracle).filter(n -> n.getNode() != null).findFirst().isPresent();
	}
	
	@Override
	public void resetOracle()
	{
		for (int i=1; i<nodes.length; i++)
			nodes[i].clearDependencies();
		
		for (int i=1; i<nodes.length; i++)
		{
			DEPArc o = oracle[i];
			nodes[i].setDependencyHead(o.getNode(), o.getLabel());
		}
	}
	
	@Override
	public String getOracle()
	{
		DEPLabel label = getOracleLabel();
		return label.toString();
	}
	
	public DEPLabel getOracleLabel()
	{
		NLPNode stack = getStack();
		NLPNode input = getInput();
		DEPArc gold;
		String list;
		
		// left arc
		gold = oracle[stack.getID()];
		
		if (gold.isNode(input) && !input.isDescendantOf(stack))
		{
			list = isOracleReduce(true) ? LIST_REDUCE : LIST_PASS;
			return new DEPLabel(ARC_LEFT, list, gold.getLabel());
		}
				
		// right arc
		gold = oracle[input.getID()];
		
		if (gold.isNode(stack) && !stack.isDescendantOf(input))
		{
			list = isOracleShift() ? LIST_SHIFT : LIST_PASS;
			return new DEPLabel(ARC_RIGHT, list, gold.getLabel());
		}
		
		// no arc
		if      (isOracleShift())		list = LIST_SHIFT;
		else if (isOracleReduce(false))	list = LIST_REDUCE;
		else							list = LIST_PASS;
		
		return new DEPLabel(ARC_NO, list, StringConst.EMPTY);
	}
	
	/** Called by {@link #getOracleLabel()}. */
	private boolean isOracleShift()
	{
		// if head(input) < stack
		NLPNode stack = getStack();
		
		if (oracle[input].getNode().getID() < stack.getID())
			return false;
		
		// if child(input) < stack
		NLPNode input = getInput();
		int i = 0;

		while ((stack = peekStack(--i)) != null)
		{
			if (oracle[stack.getID()].isNode(input))
				return false;
		}
		
		return true;
	}
	
	/** Called by {@link #getOracleLabel()}. */
	private boolean isOracleReduce(boolean hasHead)
	{
		// if stack has no head
		NLPNode stack = getStack();
		
		if (!hasHead && !stack.hasDependencyHead())
			return false;
		
		// if child(stack) > input 
		for (int i=input+1; i<nodes.length; i++)
		{
			if (oracle[i].isNode(stack))
				return false;
		}
		
		return true;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		DEPLabel label = new DEPLabel(map.getLabel(top2[0]));
		NLPNode  stack = getStack();
		NLPNode  input = getInput();
		
		if (label.isArc(ARC_LEFT))
		{
			stack.setDependencyHead(input, label.getDeprel());
			if (label.isList(LIST_REDUCE)) reduce();
			else pass();
		}
		else if (label.isArc(ARC_RIGHT))
		{
			input.setDependencyHead(stack, label.getDeprel());
			if (label.isList(LIST_SHIFT)) shift();
			else pass();
		}
		else
		{
			if (label.isList(LIST_SHIFT)) shift();
			else if (label.isList(LIST_REDUCE)) reduce();
			else pass();
		}
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	private void shift()
	{
		while (!inter.isEmpty()) stack.push(inter.pop());
		stack.push(input++);
	}
	
	private void reduce()
	{
		stack.pop();
	}
	
	private void pass()
	{
		inter.push(stack.pop());
	}
	
//	====================================== NODE ======================================

	/**
	 * @return the window'th top of the stack if exists; otherwise, -1.
	 * @param window 0: top, 1: 2nd-top, so one.
	 */
	public NLPNode peekStack(int window)
	{
		if (window <= 0)
		{
			window *= -1;
			if (window < stack.size()) return nodes[stack.peekInt(window)];
		}
		else if (window <= inter.size())
			return nodes[inter.peekInt(window-1)];

		return null;
	}
	
	public NLPNode getStack(int window)
	{
		return getNode(stack.topInt(), window, true);
	}
	
	public NLPNode getInput(int window)
	{
		return getNode(input, window, true);
	}
	
	public NLPNode getStack()
	{
		return getStack(0);
	}
	
	public NLPNode getInput()
	{
		return getInput(0);
	}
	
	@Override
	public NLPNode getNode(FeatureItem item)
	{
		NLPNode node = null;
		
		switch (item.source)
		{
		case i: node = getStack (item.window); break;
		case j: node = getInput (item.window); break;
		case k: node = peekStack(item.window); break;
		}
		
		return getRelativeNode(item, node);
	}
	
//	====================================== EVALUATE ======================================

	@Override
	public void evaluate(Eval eval)
	{
		int las = 0, uas = 0;
		DEPArc  gold;
		NLPNode node;
		
		for (int i=1; i<nodes.length; i++)
		{
			gold = oracle[i];
			node = nodes [i];
			
			if (gold.isNode(node.getDependencyHead()))
			{
				uas++;
				if (gold.isLabel(node.getDependencyLabel())) las++;
			}
		}

		((DEPEval)eval).add(las, uas, nodes.length-1);
	}
	
	public void reset(int stackID, int inputID)
	{
		stack.clear();
		inter.clear();
		stack.push(stackID);
		input = inputID;
	}
}
