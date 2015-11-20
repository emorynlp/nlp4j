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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.common.treebank.DEPTagEn;
import edu.emory.mathcs.nlp.component.template.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.learning.model.StringModel;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.vector.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPParser extends NLPOnlineComponent<DEPState>
{
	private static final long serialVersionUID = 7031031976396726276L;
	private DEPLabelCandidate label_indices;

	public DEPParser()
	{
		label_indices = new DEPLabelCandidate();
	}
	
	public DEPParser(InputStream configuration)
	{
		super(configuration);
		label_indices = new DEPLabelCandidate();
	}
	
//	============================== ABSTRACT METHODS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException {}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException {}
	
//	============================== ABSTRACT ==============================

	@Override
	public NLPConfig setConfiguration(InputStream in)
	{
		NLPConfig config = (NLPConfig)new DEPConfig(in);
		setConfiguration(config);
		return config;
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new DEPEval();
	}
	
	@Override
	protected DEPState initState(NLPNode[] nodes)
	{
		return new DEPState(nodes, label_indices);
	}
	
//	============================== POST-PROCESS ==============================

	@Override
	protected void postProcess(DEPState state)
	{
		postProcessHeadless(state);
	}
	
	void postProcessHeadless(DEPState state)
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
					node.setDependencyHead(nodes[max.headId], new DEPLabel(models[0].getLabel(max.yhat)).getDeprel());
			}
		}
	}

	void processHeadless(DEPState state, DEPTriple max, NLPNode[] nodes, int currID, int dir)
	{
		int[] labels = (dir > 0) ? label_indices.getLeftArcs() : label_indices.getRightArcs();
		NLPNode head, node = nodes[currID];
		StringModel model = models[0];
		float[] scores;
		SparseVector x;
		int yhat;
		
		for (int headID=currID+dir; 0 <= headID&&headID < nodes.length; headID+=dir)
		{
			head = nodes[headID];

			if (!head.isDescendantOf(node))
			{
				if (dir > 0)	state.reset(currID, headID);
				else			state.reset(headID, currID);
				
				x = extractFeatures(state, model);
				scores = model.scores(x, labels);
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
			set(-1, -1, Double.NEGATIVE_INFINITY);
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
