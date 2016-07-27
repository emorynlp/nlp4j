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
package edu.emory.mathcs.nlp.common.propbank;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PBTag
{
	String PB_REL  = "rel";
	String PB_DSP  = "DSP";
	String PB_C_V  = "C-V";
	
	String PB_ARG0 = "ARG0";
	String PB_ARG1 = "ARG1";
	String PB_ARG2 = "ARG2";
	String PB_ARG3 = "ARG3";
	String PB_ARG4 = "ARG4";

	/** External causer. */
	String PB_ARGA = "ARGA";
	/** Adjectival. */
	String PB_ARGM_ADJ = "ARGM-ADJ";
	/** Adverbial. */
	String PB_ARGM_ADV = "ARGM-ADV";
	/** Cause. */
	String PB_ARGM_CAU = "ARGM-CAU";
	/** Comitative. */
	String PB_ARGM_COM = "ARGM-COM";
	/** Direction. */
	String PB_ARGM_DIR = "ARGM-DIR";
	/** Discourse. */
	String PB_ARGM_DIS = "ARGM-DIS";
	/** Goal. */
	String PB_ARGM_GOL = "ARGM-GOL";
	/** Extent. */
	String PB_ARGM_EXT = "ARGM-EXT";
	/** Location. */
	String PB_ARGM_LOC = "ARGM-LOC";
	/** Manner. */
	String PB_ARGM_MNR = "ARGM-MNR";
	/** Modal. */
	String PB_ARGM_MOD = "ARGM-MOD";
	/** Negation. */
	String PB_ARGM_NEG = "ARGM-NEG";
	/** Secondary predication. */
	String PB_ARGM_PRD = "ARGM-PRD";
	/** Purpose. */
	String PB_ARGM_PRP = "ARGM-PRP";
	/** Compound noun of light verb. */
	String PB_ARGM_PRR = "ARGM-PRR";
	/** Recipricol. */
	String PB_ARGM_REC = "ARGM-REC";
	/** Temporal. */
	String PB_ARGM_TMP = "ARGM-TMP";
	
	/** Link caused by reduced relative clauses. */
	String PB_LINK_SLC	= "LINK-SLC";
	/** Link caused by *PRO*. */
	String PB_LINK_PRO	= "LINK-PRO";
	/** Link caused by passive construtions. */
	String PB_LINK_PSV	= "LINK-PSV";
}