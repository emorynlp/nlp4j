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
package edu.emory.mathcs.nlp.component.it;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ItClassifier<N extends AbstractNLPNode<N>> extends OnlineComponent<N,ItState<N>>
{
	private static final long serialVersionUID = 3585863417135590906L;

	public ItClassifier() {super(true);}
	
	public ItClassifier(InputStream configuration)
	{
		super(true, configuration);
	}

	@Override
	protected ItState<N> initState(List<N[]> document)
	{
		return new ItState<>(document);
	}
	
	@Override
	public void initFeatureTemplate()
	{
		feature_template = new ItFeatureTemplate<>(config.getFeatureTemplateElement(), getHyperParameter());
	}

	@Override
	public Eval createEvaluator()
	{
		return new ItEval(4);
	}

	@Override
	protected void postProcess(ItState<N> state) {}

	@Override
	protected ItState<N> initState(N[] nodes) {return null;}
}
