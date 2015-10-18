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
package edu.emory.mathcs.nlp.corenlp.dep;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.corenlp.OnlineComponent;
import edu.emory.mathcs.nlp.corenlp.learn.model.StringModel;
import edu.emory.mathcs.nlp.corenlp.learn.util.StringInstance;
import edu.emory.mathcs.nlp.corenlp.learn.util.StringPrediction;
import edu.emory.mathcs.nlp.corenlp.learn.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPParser<N extends DEPNode> extends OnlineComponent<N,DEPState<N>>
{
	private static final long serialVersionUID = 7031031976396726276L;

	public DEPParser(StringModel model)
	{
		super(new StringModel[]{model});
	}
	
//	============================== LEXICONS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException {}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException {}
	
//	============================== PROCESS ==============================
	
	@Override
	protected DEPState<N> createState(N[] nodes)
	{
		return new DEPState<>(nodes);
	}

	@Override
	protected StringPrediction getModelPrediction(DEPState<N> state, StringVector vector)
	{
		return models[0].predictBest(vector);
	}

	@Override
	protected void addInstance(String label, StringVector vector)
	{
		models[0].learn(new StringInstance(label, vector));
	}
}
