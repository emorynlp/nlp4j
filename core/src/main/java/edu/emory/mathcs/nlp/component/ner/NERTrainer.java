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

import edu.emory.mathcs.nlp.component.ner.feature.NERFeatureTemplate0;
import edu.emory.mathcs.nlp.component.ner.feature.NERFeatureTemplate1;
import edu.emory.mathcs.nlp.component.ner.feature.NERFeatureTemplate2;
import edu.emory.mathcs.nlp.component.ner.feature.NERFeatureTemplateCoNLL03;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTrainer extends OnlineTrainer<NERState>
{
	@Override
	protected OnlineComponent<NERState> createComponent(InputStream config)
	{
		return new NERTagger(config);
	}

	@Override
	protected void collect(OnlineComponent<NERState> component, List<String> inputFiles) {}
	
	@Override
	protected FeatureTemplate<NERState> createFeatureTemplate(int id, int dynamicFeatureSize, int embeddingWindowLeft, int embeddingWindowRight)
	{
		switch (id)
		{
		case  0: return new NERFeatureTemplate0(dynamicFeatureSize, embeddingWindowLeft, embeddingWindowRight);
		case  1: return new NERFeatureTemplate1(dynamicFeatureSize, embeddingWindowLeft, embeddingWindowRight);
		case  2: return new NERFeatureTemplate2(dynamicFeatureSize, embeddingWindowLeft, embeddingWindowRight);
		case  3: return new NERFeatureTemplateCoNLL03(dynamicFeatureSize, embeddingWindowLeft, embeddingWindowRight);
		default: throw new IllegalArgumentException("Unknown feature template: "+id);
		}
	}
}
