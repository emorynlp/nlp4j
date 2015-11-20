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
package edu.emory.mathcs.nlp.component.doc.features;

import edu.emory.mathcs.nlp.component.doc.DOCFeatureTemplate;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.feature.Field;

/**
 * Minimum features.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCFeatureTemplate0 extends DOCFeatureTemplate
{
	private static final long serialVersionUID = -5167979527994866026L;

	@Override
	protected void init()
	{
		// bag-of-word features
//		addSet(new FeatureItem<>(0, Field.bag_of_words, false));
		addSetWeighted(new FeatureItem<>(0, Field.bag_of_words_count, false));
	}
}
