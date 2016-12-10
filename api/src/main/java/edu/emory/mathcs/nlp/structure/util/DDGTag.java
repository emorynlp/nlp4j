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
package edu.emory.mathcs.nlp.structure.util;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface DDGTag
{
	// subjects
	/** Expletive (2.0.0). */
	String EXPL = "expl";
	/** Clausal subject (2.0.0). */
	String CSBJ = "csbj";
	/** Nominal subject (2.0.0). */
	String NSBJ = "nsbj";
	
	// arguments
	/** Dative (2.0.0). */
	String DAT  = "dat";
	/** Object (2.0.0). */
	String OBJ  = "obj";
	/** Object predicate (2.0.0). */
	String OPRD = "oprd";
	/** Clausal complement (2.0.0). */
	String COMP = "comp";
	
	// auxiliaries
	/** Auxiliary verb (2.0.0). */
	String AUX   = "aux";
	/** Copula (2.0.0). */
	String COP   = "cop";
	/** Light verb (2.0.0). */
	String LV    = "lv";
	/** Modal adjective (2.0.0). */
	String MODAL = "modal";
	/** Raising verb (2.0.0). */
	String RAISE = "raise";

	// nominals/quantifiers
	/** Clausal modifier of noun (2.0.0). */
	String ACL   = "acl";
	/** Apposition (2.0.0). */
	String APPO  = "appo";
	/** Attribute (2.0.0). */
	String ATTR  = "attr";
	/** Determiner (2.0.0). */
	String DET   = "det";
	/** Numeral modifier (2.0.0). */
	String NUM   = "num";
	/** Relative clause (2.0.0). */
	String RELCL = "relcl";
	/** Possessive modifier (2.0.0). */
	String POSS  = "poss";
	
	// adverbials
	/** Adverbial (2.0.0). */
	String ADV   = "adv";
	/** Adverbial clause (2.0.0). */
	String ADVCL = "advcl";	
	/** Adverbial noun phrase (2.0.0). */
	String ADVNP = "advnp";
	/** Negation (2.0.0). */
	String NEG   = "neg";
	/** Preposition phrasal modifier (2.0.0). */
	String PPMOD = "ppmod";

	// particles
	/** Case marker (2.0.0). */
	String CASE = "case";
	/** Marker (2.0.0). */
	String MARK = "mark";
	/** Verb particle (2.0.0). */
	String PRT  = "prt";

	// coordination
	/** Coordinating conjunction (2.0.0). */
	String CC   = "cc";
	/** Conjunct (2.0.0). */
	String CONJ = "conj";
	
	// default
	/** Unclassified dependency (2.0.0). */
	String DEP  = "dep";
	/** Root (2.0.0). */
	String ROOT = "root";
	
	// function tags
	/** Closed related (2.0.0). */
	String CLR = "clr";
	/** Directional (2.0.0). */
	String DIR = "dir";
	/** Extent (2.0.0). */
	String EXT = "ext";
	/** Locative (2.0.0). */
	String LOC = "loc";
	/** Manner (2.0.0). */
	String MNR = "mnr";
	/** Purpose (2.0.0). */
	String PRP = "prp";
	/** Temporal (2.0.0). */
	String TMP = "tmp";
	/** Vocative (2.0.0). */
	String VOC  = "voc";
	
	// miscellaneous
	/** Compound word (2.0.0). */
	String COM = "com";
	/** Discourse element (2.0.0). */
	String DISC = "disc";
	/** Meta element (2.0.0). */
	String META = "meta";
	/** Punctuation (2.0.0). */
	String P    = "p";
	/** Parenthetical notation (2.0.0). */
	String PRN  = "prn";
	/** Relativizer (2.0.0). */
	String R     = "r-";
	
	String FEAT_SEM = "sem";
}