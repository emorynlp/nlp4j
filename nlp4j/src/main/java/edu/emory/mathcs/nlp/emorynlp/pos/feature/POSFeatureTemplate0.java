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
package edu.emory.mathcs.nlp.emorynlp.pos.feature;

import edu.emory.mathcs.nlp.emorynlp.utils.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.utils.feature.Field;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSFeatureTemplate0 extends POSFeatureTemplate
{
	private static final long serialVersionUID = 7072878555553683666L;

	@Override
	protected void init()
	{
		// 1-gram features 
		add(new FeatureItem<>(-2, Field.simplified_word_form));
		add(new FeatureItem<>(-1, Field.simplified_word_form));
		add(new FeatureItem<>( 0, Field.simplified_word_form));
		add(new FeatureItem<>( 1, Field.simplified_word_form));
		add(new FeatureItem<>( 2, Field.simplified_word_form));

		add(new FeatureItem<>(-1, Field.word_shape, 2));
		add(new FeatureItem<>( 0, Field.word_shape, 2));
		add(new FeatureItem<>( 1, Field.word_shape, 2));

		add(new FeatureItem<>(-3, Field.pos_tag));
		add(new FeatureItem<>(-2, Field.pos_tag));
		add(new FeatureItem<>(-1, Field.pos_tag));
		add(new FeatureItem<>( 0, Field.ambiguity_class));
		add(new FeatureItem<>( 1, Field.ambiguity_class));
		add(new FeatureItem<>( 2, Field.ambiguity_class));
		add(new FeatureItem<>( 3, Field.ambiguity_class));

		// 2-gram features
		add(new FeatureItem<>(-2, Field.uncapitalized_simplified_word_form), new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form));
		add(new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form));
		add(new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form));
		add(new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 2, Field.uncapitalized_simplified_word_form));
		add(new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form), new FeatureItem<>(+1, Field.uncapitalized_simplified_word_form));

		add(new FeatureItem<>(-2, Field.pos_tag)        , new FeatureItem<>(-1, Field.pos_tag));
		add(new FeatureItem<>(-1, Field.pos_tag)        , new FeatureItem<>( 1, Field.ambiguity_class));
		add(new FeatureItem<>( 1, Field.ambiguity_class), new FeatureItem<>( 2, Field.ambiguity_class));

		// 3-gram features
		add(new FeatureItem<>(-2, Field.pos_tag), new FeatureItem<>(-1, Field.pos_tag)        , new FeatureItem<>(0, Field.ambiguity_class));
		add(new FeatureItem<>(-2, Field.pos_tag), new FeatureItem<>(-1, Field.pos_tag)        , new FeatureItem<>(1, Field.ambiguity_class));
		add(new FeatureItem<>(-1, Field.pos_tag), new FeatureItem<>( 0, Field.ambiguity_class), new FeatureItem<>(1, Field.ambiguity_class));
		add(new FeatureItem<>(-1, Field.pos_tag), new FeatureItem<>( 1, Field.ambiguity_class), new FeatureItem<>(2, Field.ambiguity_class));

		// affix features
		add(new FeatureItem<>(0, Field.prefix, 2));
		add(new FeatureItem<>(0, Field.prefix, 3));
		add(new FeatureItem<>(0, Field.suffix, 1));
		add(new FeatureItem<>(0, Field.suffix, 2));
		add(new FeatureItem<>(0, Field.suffix, 3));
		add(new FeatureItem<>(0, Field.suffix, 4));
		
		// orthographic features
		addSet(new FeatureItem<>(0, Field.orthographic));
		
		// boolean features
		addSet(new FeatureItem<>(0, Field.binary));
	}
}
