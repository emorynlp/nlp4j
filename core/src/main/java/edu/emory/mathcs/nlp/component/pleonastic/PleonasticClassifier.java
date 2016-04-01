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

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PleonasticClassifier extends OnlineComponent<PleonasticState>
{
	private static final long serialVersionUID = 3585863417135590906L;

	public PleonasticClassifier() {super(false);}
	
	public PleonasticClassifier(InputStream configuration)
	{
		super(false, configuration);
	}
	
	@Override
	protected PleonasticState initState(NLPNode[] nodes)
	{
		return new PleonasticState(nodes);
	}
	
	@Override
	protected PleonasticState initState(List<NLPNode[]> document)
	{
		return null;
	}

	@Override
	public Eval createEvaluator()
	{
		return new AccuracyEval();
	}

	@Override
	protected void postProcess(PleonasticState state) {}
}
