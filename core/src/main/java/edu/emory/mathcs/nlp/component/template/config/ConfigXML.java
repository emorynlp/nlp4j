/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.component.template.config;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface ConfigXML
{
	String LANGUAGE		= "language";
	String MODELS		= "models";
	
//	========================== TSV READER ==========================

	String TSV			= "tsv";
	String COLUMN		= "column";
	String FIELD		= "field";
	String INDEX		= "index";

	String FIELD_ID		= "id";
	String FIELD_FORM	= "form";
	String FIELD_LEMMA	= "lemma";
	String FIELD_POS 	= "pos";
	String FIELD_NAMENT	= "nament";
	String FIELD_FEATS 	= "feats";
	String FIELD_DHEAD	= "dhead";
	String FIELD_DEPREL	= "deprel";
	String FIELD_SHEADS	= "sheads";
	String FIELD_COREF	= "coref";
	
	String DOCUMENT_BASED = "document_based";
	
//	========================== OPTIMIZER ==========================

	String OPTIMIZER			= "optimizer";
	String MAX_EPOCH			= "max_epoch";
	String BATCH_SIZE			= "batch_size";
	String FEATURE_CUTOFF		= "feature_cutoff";
	String LEARNING_RATE		= "learning_rate";
	String BIAS					= "bias";
	String L1_REGULARIZATION	= "l1_regularization";
	
	String DECAYING_RATE		= "decaying_rate";
	String THREAD_SIZE			= "thread_size";
	
//	========================== LOCALLY OPTIMAL LEARNING to SEARCH ==========================
	
	String LOLS		= "lols";
	String FIXED	= "fixed";
	String DECAYING	= "decaying";
	
//	========================== ALGORITHMS ==========================

	String ALGORITHM			= "algorithm";
	String PERCEPTRON			= "perceptron";
	String SOFTMAX_REGRESSION	= "softmax-regression";
	String ADAGRAD				= "adagrad";
	String ADAGRAD_MINI_BATCH	= "adagrad-mini-batch";
	String ADAGRAD_REGRESSION	= "adagrad-regression";
	String ADADELTA_MINI_BATCH	= "adadelta-mini-batch";

//	========================== NEURAL NETWORKS ==========================
	
	String FFNN_SOFTMAX	= "ffnn-softmax";

	String HIDDEN		= "hidden";
	String ACTIVATION	= "activation";
	String SOFTMAX		= "softmax";
	String SIGMOID		= "sigmoid";
	String RANDOM_BOUND	= "random_bound";

//	========================== FEATURE TEMPLATE ==========================
	
	String FEATURE_TEMPLATE = "feature_template";	
}
