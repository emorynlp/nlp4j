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

import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLState extends NLPState
{
	public SRLState(NLPNode[] nodes)
	{
		super(nodes);
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#saveOracle()
	 */
	@Override
	public void saveOracle()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#getOracle()
	 */
	@Override
	public String getOracle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#next(edu.emory.mathcs.nlp.learning.util.LabelMap, int, float[])
	 */
	@Override
	public void next(LabelMap map, int yhat, float[] scores)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#isTerminate()
	 */
	@Override
	public boolean isTerminate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#getNode(edu.emory.mathcs.nlp.component.template.feature.FeatureItem)
	 */
	@Override
	public NLPNode getNode(FeatureItem item)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#evaluate(edu.emory.mathcs.nlp.component.template.eval.Eval)
	 */
	@Override
	public void evaluate(Eval eval)
	{
		// TODO Auto-generated method stub
		
	}

}
