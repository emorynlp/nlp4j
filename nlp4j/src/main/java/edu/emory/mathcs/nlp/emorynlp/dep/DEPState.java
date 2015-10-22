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
package edu.emory.mathcs.nlp.emorynlp.dep;

import java.util.Arrays;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState<N extends NLPNode> extends NLPState<N>
{
	static public final String LEFT_ARC  = "LA-";
	static public final String RIGHT_ARC = "RA-";
	static public final String SHIFT     = "S";
	static public final String REDUCE    = "R";
	
	private DEPArc[]     oracle;
	private IntArrayList stack;
	private int          input;
	
	public DEPState(N[] nodes)
	{
		super(nodes);
		stack = new IntArrayList();
		input = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================

	@Override
	public void saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> n.clearDependencies()).toArray(DEPArc[]::new);
	}
	
	@Override
	public Set<String> getZeroCost()
	{
		// left-arc: input is the head of stack
		DEPArc o = oracle[stack.topInt()];
		
		if (o.isNode(getInput()))
			return DSUtils.toHashSet(LEFT_ARC + o.getLabel());
		
		// right-arc: stack is the head of input
		o = oracle[input];
		
		if (o.isNode(getStack()))
			return DSUtils.toHashSet(RIGHT_ARC + o.getLabel());
		
		// reduce: stack has the head
		if (isOracleReduce())
			return DSUtils.toHashSet(REDUCE);
		
		return DSUtils.toHashSet(SHIFT);
	}
	
	private boolean isOracleReduce()
	{
		if (!getStack().hasDependencyHead()) return false;
		int s;
		
		for (int i=1; i<stack.size(); i++)
		{
			s = stack.peekInt(i);
			
			if (oracle[input].isNode(nodes[s]) || oracle[s].isNode(nodes[input]))
				return true;
		}
		
		return false;
	}
	
	boolean isOracleReduceEager()
	{
		N s = getStack();
		if (!s.hasDependencyHead()) return false;
		
		for (int i=input+1; i<nodes.length; i++)
		{
			if (oracle[i].isNode(s))
				return false;
		}
		
		return true;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(StringPrediction prediction)
	{
		String label = prediction.getLabel();
//		System.out.println(label+" "+stack.toString()+" "+input);
		
		if (label.startsWith(LEFT_ARC))
		{
			NLPNode s = getStack();
			NLPNode i = getInput();
			
			if (s != nodes[0] && !i.isDescendantOf(s))
			{
				s.setDependencyHead(i, label.substring(3));
				label = REDUCE;
			}
			else
				label = SHIFT;
		}
		else if (label.startsWith(RIGHT_ARC))
		{
			NLPNode s = getStack();
			NLPNode i = getInput();
			
			if (!s.isDescendantOf(i))
				i.setDependencyHead(s, label.substring(3));

			label = SHIFT;
		}
		else if (label.equals(REDUCE))
		{
			if (stack.size() == 1)
				label = SHIFT;
		}
		
		switch (label)
		{
		case SHIFT : shift();  break;
		case REDUCE: reduce(); break;
		}
	}
	
	public void shift()
	{
		stack.add(input++);
	}
	
	public void reduce()
	{
		stack.pop();
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	/**
	 * @return the window'th top of the stack if exists; otherwise, -1.
	 * @param window 0: top, 1: 2nd-top, so one.
	 */
	public N peekStack(int window)
	{
		return window < stack.size() ? nodes[stack.peekInt(window)] : null;
	}
	
	public N getStack(int window)
	{
		return getNode(stack.topInt(), window, true);
	}
	
	public N getStack()
	{
		return getStack(0);
	}
	
	public N getInput(int window)
	{
		return getNode(input, window, true);
	}
	
	public N getInput()
	{
		return getInput(0);
	}
	
//	====================================== EVALUATE ======================================

	@Override
	public void evaluate(Eval eval)
	{
		int las = 0, uas = 0;
		NLPNode node;
		DEPArc  gold;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes [i];
			gold = oracle[i];
			
			if (gold.isNode(node.getDependencyHead()))
			{
				uas++;
				if (gold.isLabel(node.getDependencyLabel())) las++;
			}
		}

		((DEPEval)eval).add(las, uas, nodes.length-1);
	}
	
//	============================== UTILITIES ==============================
	
	public boolean isFirst(N node)
	{
		return nodes[1] == node;
	}
	
	public boolean isLast(N node)
	{
		return nodes[nodes.length-1] == node;
	}

	@SuppressWarnings("unchecked")
	public N getNode(FeatureItem<?> item)
	{
		N node = null;
		
		switch (item.source)
		{
		case i: node = getStack (item.window); break;
		case j: node = getInput (item.window); break;
		case k: node = peekStack(item.window); break;
		}
		
		return (N)getNode(node, item);
	}
	
	protected NLPNode getNode(N node, FeatureItem<?> item)
	{
		if (node == null || item.relation == null)
			return node;
		
		switch (item.relation)
		{
		case h   : return node.getDependencyHead();
		case h2  : return node.getGrandDependencyHead();
		case lmd : return node.getLeftMostDependent();
		case lmd2: return node.getLeftMostDependent(1);
		case lnd : return node.getLeftNearestDependent();
		case lnd2: return node.getLeftNearestDependent(1);
		case lns : return node.getLeftNearestSibling();
		case lns2: return node.getLeftNearestSibling(1);
		case rmd : return node.getRightMostDependent();
		case rmd2: return node.getRightMostDependent(1);
		case rnd : return node.getRightNearestDependent();
		case rnd2: return node.getRightNearestDependent(1);
		case rns : return node.getRightNearestSibling();
		case rns2: return node.getRightNearestSibling(1);
		}
		
		return null;
	}
}
