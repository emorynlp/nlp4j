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

import org.w3c.dom.Element;

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
	protected FeatureTemplate<NERState> createFeatureTemplate(Element eFeatures)
	{
		return new NERFeatureTemplate(eFeatures);
	}
}
