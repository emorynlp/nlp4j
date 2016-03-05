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

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.common.treebank.DEPTagEn;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPParser extends OnlineComponent<DEPState>
{
	private static final long serialVersionUID = 7031031976396726276L;
	private DEPLabelCandidate label_candidates;

	public DEPParser()
	{
		super(false);
		label_candidates = new DEPLabelCandidate(); 
	}
	
	public DEPParser(InputStream configuration)
	{
		super(false, configuration);
		label_candidates = new DEPLabelCandidate();
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public Eval createEvaluator()
	{
		return new DEPEval();
	}
	
	@Override
	protected DEPState initState(NLPNode[] nodes)
	{
		return new DEPState(nodes);
	}
	
	@Override
	protected DEPState initState(List<NLPNode[]> document) {return null;}
	
//	============================== LABELS ==============================
	
	@Override
	protected void putLabel(String label, int index)
	{
		label_candidates.add(label, index);
	}
	
	@Override
	protected int[] getPrediction(DEPState state, float[] scores)
	{
		return label_candidates.getLabelIndices(state.getStack(), state.getInput(), scores);
	}
	
	public DEPLabelCandidate getLabelCandidates()
	{
		return label_candidates;
	}
	
//	============================== POST-PROCESS ==============================

	@Override
	protected void postProcess(DEPState state)
	{
		NLPNode[] nodes = state.getNodes();
		DEPTriple max;
		NLPNode   node;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes[i];
			
			if (!node.hasDependencyHead())
			{
				max = new DEPTriple();
				processHeadless(state, max, nodes, i, -1);
				processHeadless(state, max, nodes, i,  1);
				
				if (max.isNull())
					node.setDependencyHead(nodes[0], DEPTagEn.DEP_ROOT);
				else
					node.setDependencyHead(nodes[max.headId], new DEPLabel(optimizer.getLabel(max.yhat)).getDeprel());
			}
		}
	}

	void processHeadless(DEPState state, DEPTriple max, NLPNode[] nodes, int currID, int dir)
	{
		IntSet labels = (dir > 0) ? label_candidates.getLeftArcs() : label_candidates.getRightArcs();
		NLPNode head, node = nodes[currID];
		int yhat, window = 0;
		float[] scores;
		FeatureVector x;
		
		for (int headID=currID+dir; 0 <= headID&&headID < nodes.length; headID+=dir)
		{
			if (++window > 5) break;
			head = nodes[headID];

			if (!head.isDescendantOf(node))
			{
				if (dir > 0)	state.reset(currID, headID);
				else			state.reset(headID, currID);
				
				x = feature_template.createFeatureVector(state, isTrain());
				scores = optimizer.scores(x);
				yhat = MLUtils.argmax(scores, labels);
				if (max.score < scores[yhat]) max.set(headID, yhat, scores[yhat]);	
			}
		}
	}
	
	class DEPTriple
	{
		int    headId;
		int    yhat;
		double score;
		
		public DEPTriple()
		{
			set(-1, -1, -Double.MAX_VALUE);
		}
		
		public void set(int headID, int yhat, double score)
		{
			this.headId = headID;
			this.yhat   = yhat;
			this.score  = score;
		}
		
		public boolean isNull()
		{
			return headId < 0;
		}
	}
}
