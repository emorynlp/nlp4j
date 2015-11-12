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
package edu.emory.mathcs.nlp.component.ner;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.component.zzz.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.zzz.config.NLPConfig;
import edu.emory.mathcs.nlp.component.zzz.eval.Eval;
import edu.emory.mathcs.nlp.component.zzz.eval.F1Eval;
import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTagger extends NLPOnlineComponent<NERState>
{
	private static final long serialVersionUID = 87807440372806016L;

	public NERTagger() {}
	
	public NERTagger(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== LEXICONS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException
	{
		
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public NLPConfig setConfiguration(InputStream in)
	{
		NLPConfig config = (NLPConfig)new NERConfig(in);
		setConfiguration(config);
		return config;
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}
	
	@Override
	protected NERState initState(NLPNode[] nodes)
	{
		return new NERState(nodes);
	}
}
