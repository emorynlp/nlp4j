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
package edu.emory.mathcs.nlp.component.pleonastic;

import java.util.Arrays;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PleonasticState extends NLPState
{
	static public final Pattern DEPREL = Pattern.compile("^(nsubj|nsubjpass|dobj)$");
	static public final String KEY = "it"; 
	private String[] oracle;
	private int input;
	
	public PleonasticState(NLPNode[] nodes)
	{
		super(nodes);
		input = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================
	
	@Override
	public boolean saveOracle()
	{
		oracle = Arrays.stream(nodes).map(n -> n.removeFeat(KEY)).toArray(String[]::new);
		return Arrays.stream(oracle).filter(o -> o != null).findFirst().isPresent();
	}

	@Override
	public String getOracle()
	{
		// TODO Auto-generated method stub
		return null;
	}

//	====================================== TRANSITION ======================================
	
	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.component.template.state.NLPState#next(edu.emory.mathcs.nlp.learning.util.LabelMap, int, float[])
	 */
	@Override
	public void next(LabelMap map, int yhat, float[] scores)
	{
		// TODO Auto-generated method stub
		
	}
	
	private void shift()
	{
		for (input++; input<nodes.length; input++)
		{
			NLPNode node = nodes[input];
			
			if (node.isLemma("it") && node.isDependencyLabel(DEPREL))
				break;
		}
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
