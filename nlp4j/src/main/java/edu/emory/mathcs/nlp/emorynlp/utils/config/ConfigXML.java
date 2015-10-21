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
package edu.emory.mathcs.nlp.emorynlp.utils.config;

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

	String OPTIMIZER	= "optimizer";
	String ALGORITHM	= "algorithm";
	String MAX_EPOCHS	= "max_epochs";
	String BATCH_SIZE	= "batch_size";
	String ROLL_IN		= "roll_in";
	String BIAS			= "bias";
	
	String L1_REGULARIZATION	= "l1_regularization";
	String LEARNING_RATE		= "learning_rate";
	String DECAYING_RATE		= "decaying_rate";
	String THREAD_SIZE			= "thread_size";
	
//	========================== ALGORITHMS ==========================
	
	String PERCEPTRON			= "perceptron";
	String SOFTMAX_REGRESSION	= "softmax-regression";
	String ADAGRAD				= "adagrad";
	String ADAGRAD_MINI_BATCH	= "adagrad-mini-batch";
	String ADAGRAD_REGRESSION	= "adagrad-regression";
	String ADADELTA_MINI_BATCH	= "adadelta-mini-batch";
}
