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

import java.io.InputStream;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTagger extends OnlineComponent<POSState>
{
	private static final long serialVersionUID = -7926217238116337203L;
	private AmbiguityClassMap ambiguity_class_map;
	
	public POSTagger() {}
	
	public POSTagger(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== LEXICONS ==============================
	
	public AmbiguityClassMap getAmbiguityClassMap()
	{
		return ambiguity_class_map;
	}
	
	public void setAmbiguityClassMap(AmbiguityClassMap map)
	{
		ambiguity_class_map = map;
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public NLPConfig setConfiguration(InputStream in)
	{
		NLPConfig config = (NLPConfig)new POSConfig(in);
		setConfiguration(config);
		return config;
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new AccuracyEval();
	}
	
	@Override
	protected POSState initState(NLPNode[] nodes)
	{
		String[] ambi = getAmbiguityClasses(nodes);
		return new POSState(nodes, ambi);
	}
	
	public String[] getAmbiguityClasses(NLPNode[] nodes)
	{
		String[] ambi = new String[nodes.length];
		for (int i=1; i<nodes.length; i++)
			ambi[i] = ambiguity_class_map.get(nodes[i]);
		return ambi;
	}
}
