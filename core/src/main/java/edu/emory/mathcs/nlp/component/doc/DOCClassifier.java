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
package edu.emory.mathcs.nlp.component.doc;

import java.io.InputStream;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCClassifier extends OnlineComponent<DOCState>
{
	private static final long serialVersionUID = 87807440372806016L;
	private String feat_key;
	
	public DOCClassifier() {}
	
	public DOCClassifier(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public NLPConfig setConfiguration(InputStream in)
	{
		DOCConfig config = new DOCConfig(in);
		feat_key = config.getFeatKey();
		setConfiguration(config);
		return config;
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new AccuracyEval();
	}
	
	@Override
	protected DOCState initState(NLPNode[] nodes)
	{
		return new DOCState(nodes, feat_key);
	}
}
