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
package edu.emory.mathcs.nlp.emorynlp.pos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.emorynlp.utils.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.utils.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.utils.train.TrainInfo;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTagger<N extends POSNode> extends NLPOnlineComponent<N,POSState<N>>
{
	private static final long serialVersionUID = -7926217238116337203L;
	private AmbiguityClassMap ambiguity_class_map;
	
	public POSTagger(OnlineOptimizer optimizer, StringModel model, TrainInfo info, FeatureTemplate<N,POSState<N>> template, Eval eval)
	{
		this(optimizer, model, info, template, eval, null);
	}
	
	public POSTagger(OnlineOptimizer optimizer, StringModel model, TrainInfo info, FeatureTemplate<N,POSState<N>> template, Eval eval, AmbiguityClassMap map)
	{
		super(new OnlineOptimizer[]{optimizer}, new StringModel[]{model}, new TrainInfo[]{info}, template, eval);
		setAmbiguityClassMap(map);
	}
	
//	============================== LEXICONS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ambiguity_class_map = (AmbiguityClassMap)in.readObject();
	}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException
	{
		out.writeObject(ambiguity_class_map);
	}
	
	public AmbiguityClassMap getAmbiguityClassMap()
	{
		return ambiguity_class_map;
	}
	
	public void setAmbiguityClassMap(AmbiguityClassMap map)
	{
		ambiguity_class_map = map;
	}
	
//	============================== PROCESS ==============================
	
	@Override
	protected POSState<N> initState(N[] nodes)
	{
		return new POSState<>(nodes, ambiguity_class_map);
	}
}
