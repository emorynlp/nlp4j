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

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.ner.features.NERFeatureTemplate0;
import edu.emory.mathcs.nlp.component.zzz.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.zzz.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;
import edu.emory.mathcs.nlp.component.zzz.train.NLPOnlineTrainer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTrainer extends NLPOnlineTrainer<NERState>
{
	@Override
	protected NLPOnlineComponent<NERState> createComponent(InputStream config)
	{
		return new NERTagger(config);
	}

	@Override
	protected void collect(NLPOnlineComponent<NERState> component, List<String> inputFiles) {}
	
	@Override
	protected FeatureTemplate<NERState> createFeatureTemplate(int id)
	{
		switch (id)
		{
		case  0: return new NERFeatureTemplate0();
		default: throw new IllegalArgumentException("Unknown feature template: "+id);
		}
	}
	
	@Override
	protected NLPNode createNode()
	{
		return new NLPNode();
	}
}
