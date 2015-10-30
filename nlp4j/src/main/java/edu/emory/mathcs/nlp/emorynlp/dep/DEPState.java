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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState<N extends NLPNode> extends NLPState<N> implements DEPTransition
{
	static public final int IS_ROOT         = 0;
	static public final int IS_DESC         = 1;
	static public final int IS_DESC_NO_HEAD = 2;
	static public final int NO_HEAD         = 3;
	static public final int LEFT_ARC        = 4;
	static public final int RIGHT_ARC       = 5;
	
	private List<PriorityQueue<DEPArc>> snd_heads;
	private DEPArc[]     oracle;
	private IntArrayList stack;
	private IntArrayList inter;
	private int          input;
	
	public DEPState(N[] nodes)
	{
		super(nodes);
		stack = new IntArrayList();
		inter = new IntArrayList();
		input = 0;
		shift();
		
		initSecondHeads();
	}
	
	private void initSecondHeads()
	{
		snd_heads = new ArrayList<>();
		
		for (int i=0; i<nodes.length; i++)
			snd_heads.add(new PriorityQueue<>(new DEPArcComparator()));
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
		DEPLabel label = getOracle();
		return Collections.singleton(label.toString());
	}
	
	public DEPLabel getOracle()
	{
		N stack = getStack();
		N input = getInput();
		DEPArc gold;
		String list;
		
		gold = oracle[stack.getID()];
		
		if (gold.isNode(input))
		{
			list = isOracleReduce(true) ? LIST_REDUCE : LIST_PASS;
			return new DEPLabel(ARC_LEFT, list, gold.getLabel());
		}
					
		gold = oracle[input.getID()];
		
		if (gold.isNode(stack))
		{
			list = isOracleShift() ? LIST_SHIFT : LIST_PASS;
			return new DEPLabel(ARC_RIGHT, list, gold.getLabel());
		}
		
		if      (isOracleShift())		list = LIST_SHIFT;
		else if (isOracleReduce(false))	list = LIST_REDUCE;
		else							list = LIST_PASS;
		
		return new DEPLabel(ARC_NO, list, StringConst.EMPTY);
	}
	
	/** Called by {@link #getGoldLabel()}. */
	private boolean isOracleShift()
	{
		// if head(input) < stack
		N stack = getStack();
		
		if (oracle[input].getNode().getID() < stack.getID())
			return false;
		
		// if child(input) < stack
		N input = getInput();
		int i = 1;

		while ((stack = peekStack(i++)) != null)
		{
			if (oracle[stack.getID()].isNode(input))
				return false;
		}
		
		return true;
	}
	
	/** Called by {@link #getGoldLabel()}. */
	private boolean isOracleReduce(boolean hasHead)
	{
		// if stack has no head
		N stack = getStack();
		
		if (!hasHead && !stack.hasDependencyHead())
			return false;
		
		// if child(input) > stack 
		for (int i=input+1; i<nodes.length; i++)
		{
			if (oracle[i].isNode(stack))
				return false;
		}
		
		return true;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(StringPrediction prediction)
	{
		DEPLabel label = new DEPLabel(prediction);
		N stack = getStack();
		N input = getInput();
		
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
		if (!inter.isEmpty())
		{
			for (int i=inter.size()-1; i>=0; i--) stack.push(inter.get(i));
			inter.clear();
		}
		
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
	
//	====================================== 2nd Heads ======================================

//	/** PRE: ps[0].isArc("NO"). */
//	public void save2ndHead(StringPrediction[] ps)
//	{
//		if (ps[0].getScore() - ps[1].getScore() < 1)
//		{
//			DEPLabel label = new DEPLabel(ps[1].getLabel());
//			if (label.isArc(ARC_NO)) return;
//			DEPNode curr, head;
//			
//			if (label.isArc(ARC_LEFT))
//			{
//				curr = getStack();
//				head = getInput();
//			}
//			else
//			{
//				head = getStack();
//				curr = getInput();
//			}
//			
//			snd_heads[curr.getID()].add(new ObjectDoublePair<DEPArc>(new DEPArc(head, label.getDeprel()), ps[1].getScore()));
//		}
//	}
//	
//	/** @param node has no head. */
//	public boolean find2ndHead(DEPNode node)
//	{
//		DEPArc head;
//		
//		for (ObjectDoublePair<DEPArc> p : snd_heads[node.getID()])
//		{
//			head = p.o;
//			
//			if (!head.getNode().isDescendantOf(node))
//			{
//				node.setHead(head.getNode(), head.getLabel());
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	class DEPArcComparator implements Comparator<DEPArc>
	{
		@Override
		public int compare(DEPArc o1, DEPArc o2)
		{
			return MathUtils.signum(o2.getWeight() - o1.getWeight());
		}
	}
	
//	====================================== EVALUATE ======================================

	@Override
	public void evaluate(Eval eval)
	{
		int las = 0, uas = 0;
		DEPArc gold;
		N node;
		
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
	
//	============================== INDICES ==============================

	static public int[][] initLabelIndices(String[] labels)
	{
		int i, size = labels.length;
		DEPLabel label;
		
		IntArrayList isRoot       = new IntArrayList();
		IntArrayList isDesc       = new IntArrayList();
		IntArrayList isDescNoHead = new IntArrayList();
		IntArrayList noHead       = new IntArrayList();
		IntArrayList leftArc      = new IntArrayList();
		IntArrayList rightArc     = new IntArrayList();
		
		for (i=0; i<size; i++)
		{
			label = new DEPLabel(labels[i]);
			
			if (label.isList(LIST_SHIFT))
				isRoot.add(i);
			
			if (label.isArc(ARC_NO))
			{
				isDesc.add(i);
				if (!label.isList(LIST_REDUCE)) isDescNoHead.add(i);
			}
			else if (label.isArc(ARC_LEFT))
				leftArc.add(i);
			else if (label.isArc(ARC_RIGHT))
				rightArc.add(i);
			
			if (!(label.isArc(ARC_NO) && label.isList(LIST_REDUCE)))
				noHead.add(i);
		}
		
		int[][] indices = new int[6][];
		
		initLabelIndices(indices, isRoot      , IS_ROOT);
		initLabelIndices(indices, isDesc      , IS_DESC);
		initLabelIndices(indices, isDescNoHead, IS_DESC_NO_HEAD);
		initLabelIndices(indices, noHead      , NO_HEAD);
		initLabelIndices(indices, leftArc     , LEFT_ARC);
		initLabelIndices(indices, rightArc    , RIGHT_ARC);
		
		return indices;
	}
	
	static private void initLabelIndices(int[][] indices, IntArrayList list, int index)
	{
		indices[index] = list.toIntArray();
		Arrays.sort(indices[index]);
	}
	
	public int[] getLabelIndices(int[][] indices)
	{
		N stack = getStack();
		N input = getInput();
		
		if (stack.getID() == 0)
			return indices[IS_ROOT];
		else if (stack.isDescendantOf(input))
			return indices[IS_DESC];
		else if (input.isDescendantOf(stack))
			return stack.hasDependencyHead() ? indices[IS_DESC] : indices[IS_DESC_NO_HEAD];
		else if (!stack.hasDependencyHead())
			return indices[NO_HEAD];
		else
			return null;
	}
}
