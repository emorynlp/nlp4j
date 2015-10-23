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
package edu.emory.mathcs.nlp.emorynlp.component.feature;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public enum Field
{
	// form features
	word_form,
	simplified_word_form,
	uncapitalized_simplified_word_form,
	word_shape,
	orthographic,	// set
	prefix,
	suffix,

	// part-of-speech tagging features
	lemma,
	feats,
	part_of_speech_tag,
	ambiguity_class,
	
	// named entity recognition
	named_entity_tag,
	
	// dependency parsing features
	dependency_label,
	distance,
	valency,
	
	// distributional semantics
	clusters,
	
	// more
	binary;	// set
}
