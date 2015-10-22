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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPParser<N extends NLPNode> extends NLPOnlineComponent<N,DEPState<N>>
{
	private static final long serialVersionUID = 7031031976396726276L;

	public DEPParser(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== ABSTRACT METHODS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException {}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException {}
	
//	============================== ABSTRACT ==============================

	@Override
	@SuppressWarnings("unchecked")
	public void setConfiguration(InputStream in)
	{
		setConfiguration((NLPConfig<N>)new DEPConfig(in));
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new DEPEval();
	}
	
	@Override
	protected DEPState<N> initState(N[] nodes)
	{
		return new DEPState<>(nodes);
	}
}
