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
package edu.emory.mathcs.nlp.component.template.feature;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public enum Field
{
	// form features
	word_form,
	word_form_undigitalized,
	word_form_simplified,
	word_form_simplified_lowercase,
	word_shape,
	word_shape_lowercase,
	orthographic,
	orthographic_lowercase,
	prefix,
	suffix,

	// part-of-speech tagging features
	lemma,
	feats,
	part_of_speech_tag,
	ambiguity_classes,
	
	// named entity recognition
	named_entity_tag,
	
	// dependency parsing features
	dependency_label,
	distance,
	valency,
	
	// lexica
	word_clusters,
	word_embedding,
	named_entity_gazetteers,
	
	// boolean
	positional,
	
	// document
	bag_of_words_1,
	bag_of_words_2,
	bag_of_words_3,
	bag_of_words_count_1,
	bag_of_words_count_2,
	bag_of_words_count_3,
	bag_of_words_norm_1,
	bag_of_words_norm_2,
	bag_of_words_norm_3,
	
	bag_of_words_stopwords_1,
	bag_of_words_stopwords_2,
	bag_of_words_stopwords_3,
	bag_of_words_stopwords_count_1,
	bag_of_words_stopwords_count_2,
	bag_of_words_stopwords_count_3,
	bag_of_words_stopwords_norm_1,
	bag_of_words_stopwords_norm_2,
	bag_of_words_stopwords_norm_3,
;}
