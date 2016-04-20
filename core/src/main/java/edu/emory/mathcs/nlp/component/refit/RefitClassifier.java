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
package edu.emory.mathcs.nlp.component.refit;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RefitClassifier<N extends AbstractNLPNode<N>> extends OnlineComponent<N,RefitState<N>>
{
	private static final long serialVersionUID = 3585863417135590906L;

	public RefitClassifier() {super(false);}
	
	public RefitClassifier(InputStream configuration)
	{
		super(false, configuration);
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.OnlineComponent#initState(edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode[])
	 */
	@Override
	protected RefitState<N> initState(N[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.OnlineComponent#initState(java.util.List)
	 */
	@Override
	protected RefitState<N> initState(List<N[]> document)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.OnlineComponent#createEvaluator()
	 */
	@Override
	public Eval createEvaluator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.OnlineComponent#postProcess(edu.emory.mathcs.nlp.component.template.state.NLPState)
	 */
	@Override
	protected void postProcess(RefitState<N> state)
	{
		// TODO Auto-generated method stub
		
	}
	
}
