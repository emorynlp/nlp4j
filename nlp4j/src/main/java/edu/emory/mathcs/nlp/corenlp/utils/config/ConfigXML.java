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
package edu.emory.mathcs.nlp.corenlp.component.config;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface ConfigXML
{
	String LANGUAGE		= "language";
	String MODEL		= "model";
	
//	========================== TSV READER ==========================

	String TSV		= "tsv";
	String COLUMN	= "column";
	String FIELD	= "field";
	String INDEX	= "index";

	String FIELD_ID		= "id";
	String FIELD_FORM	= "form";
	String FIELD_LEMMA	= "lemma";
	String FIELD_POS 	= "pos";
	String FIELD_FEATS 	= "feats";
	String FIELD_HEADID	= "headID";
	String FIELD_DEPREL	= "deprel";
	String FIELD_SHEADS	= "sheads";
	String FIELD_NAMENT	= "nament";
	String FIELD_COREF	= "coref";
	String FIELD_XHEADS	= "xheads";

//	========================== TRAINER ==========================

	String OPTIMIZER		= "optimizer";
	String AGGREGATE		= "aggregate";
	
	String ALGORITHM		= "algorithm";
	String LABEL_CUTOFF		= "label_cutoff";
	String FEATURE_CUTOFF	= "feature_cutoff";
	String RESET_WEIGHTS	= "reset_weights";
	String AVERAGE			= "average";
	
	String LEARNING_RATE	= "learning_rate";
	String DECAYING_RATE	= "decaying_rate";
	String BATCH_RATIO		= "batch_ratio";
	String BIAS				= "bias";
	String RIDGE			= "ridge";
	String COST				= "cost";
	String TOLERANCE_DELTA	= "tolerance_delta";
	String MAX_TOLERANCE	= "max_tolerance";
	String LOSS_TYPE		= "loss_type";
	String THREAD_SIZE		= "thread_size";
	
//	========================== ALGORITHMS ==========================
	
	String PERCEPTRON			= "perceptron";
	String ADAGRAD				= "adagrad";
	String ADAGRAD_MINI_BATCH	= "adagrad-mini-batch";
	String ADADELTA_MINI_BATCH	= "adadelta-mini-batch";
	String LIBLINEAR_L2_SVC		= "liblinear-l2-svc";
}
