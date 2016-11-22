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
package edu.emory.mathcs.nlp.common.treebank;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface DSRTag
{
	/** Auxiliary verb: default. */
	String AUX       = "aux";
	/** Auxiliary verb: passive. */
	String AUXPASS   = "auxpass";
	/** Auxiliary verb: perfect. */
	String AUXPERF   = "auxperf";
	/** Auxiliary verb: progressive. */
	String AUXPROG   = "auxprog";
	/** Modal verb. */
	String MODAL     = "modal";
	
	
	/** Clausal modifier of noun (1.2.0). */
	String ACL       = "acl";
	/** Adverbial clause (1.2.0). */
	String ADV       = "adv";
	/** Adverbial noun phrase (1.2.0). */
	String ADVCL     = "advcl";	
	/** Adverbial modifier (1.2.0). */
	String ADVNP     = "advnp";
	/** Apposition (1.2.0). */
	String APPOS     = "appos";
	/** Case marker (1.2.0). */
	String CASE      = "case";
	/** Coordinating conjunction (1.2.0). */
	String CC        = "cc";
	/** Clausal complement (1.2.0). */
	String CCOMP     = "ccomp";
	/** Compound word. */
	String COMPO     = "compo";
	/** Conjunct (1.2.0). */
	String CONJ      = "conj";
	/** Copula (1.2.0). */
	String COP       = "cop";
	/** Clausal subject (1.2.0). */
	String CSUBJ     = "csubj";
	/** Dative (1.2.0). */
	String DATV      = "datv";
	/** Unclassified dependency (1.2.0). */
	String DEP       = "dep";
	/** Determiner (1.2.0). */
	String DET       = "det";
	/** Discourse element (1.2.0). */
	String DISC      = "disc";
	/** Direct object (1.2.0). */
	String DOBJ      = "dobj";
	/** Expletive (1.2.0). */
	String EXPL      = "expl";
	/** Marker. */
	String MARK      = "mark";
	/** Meta element (1.2.0). */
	String META      = "meta";
	/** Multiword expression. */
	String MWE       = "mwe";
	/** Noun dependent (1.2.0). */
	String NDEP      = "ndep";
	/** Negation (1.2.0). */
	String NEG       = "neg";
	/** Nominal subject (1.2.0). */
	String NSUBJ     = "nsubj";
	/** Numeric modifier (1.2.0). */
	String NUM       = "num";
	/** Object predicate (1.2.0). */
	String OPRD      = "oprd";
	/** Parataxis (1.2.0). */
	String PARAT     = "parat";
	/** Preposition complement (1.2.0). */
	String PCOMP     = "pcomp";
	/** Preposition object (1.2.0). */
	String POBJ      = "pobj";
	/** Possessive modifier (1.2.0). */
	String POSS      = "poss";
	/** Preposition phrasal modifier (1.2.0). */
	String PPMOD     = "ppmod";
	/** Verb particle (1.2.0). */
	String PRT       = "prt";
	/** Punctuation (1.2.0). */
	String PUNCT     = "punct";
	/** Quantifier dependent (1.2.0). */
	String QDEP      = "qdep";
	/** Relative clause (1.2.0). */
	String RELCL     = "relcl";
	/** Root (1.2.0). */
	String ROOT      = "root";
	/** Vocative (1.2.0). */
	String VOC       = "voc";
	/** Open clausal complement (1.2.0). */
	String XCOMP     = "xcomp";
	
	
	
	
	
	
//	/** small clausal subject. */
//	String SSUBJ    = "ssubj";
//	/** relative clause. */
//	String DEP_RELCL    = "relcl";
//	/** The dependency label for passive. */
//	String DEP_PASS	= "pass";
//	/** The dependency label for subjects. */
//	String DEP_SUBJ	= "subj";
//	/** The dependency label for adjectival modifiers. */
//	String DEP_AMOD			= "amod";
//	/** Attribute. */
//	String DEP_ATTR     = "attr";
//	/** The dependency label for passive auxiliary verbs. */
//	String DEP_AUXPASS		= AUX+DEP_PASS;
//	/** The dependency label for complementizers. */
//	String DEP_COMPLM		= "complm";
//	/** The dependency label for clausal subjects. */
//	String DEP_CSUBJ		= "c"+DEP_SUBJ;
//	/** The dependency label for clausal passive subjects. */
//	String DEP_CSUBJPASS	= DEP_CSUBJ+DEP_PASS;
//	/** The dependency label for direct objects. */
//	String DEP_DOBJ 		= "dobj";
//	/** The dependency label for modifiers in hyphenation. */
//	String DEP_HMOD 		= "hmod";
//	/** The dependency label for hyphenation. */
//	String DEP_HYPH 		= "hyph";
//	/** The dependency label for indirect objects. */
//	String DEP_IOBJ 		= "iobj";
//	/** The dependency label for infinitival modifiers. */
//	String DEP_INFMOD		= "infmod";
//	/** The dependency label for noun compound modifiers. */
//	String DEP_NN			= "nn";
//	/** The dependency label for nominal subjects. */
//	String DEP_NSUBJ		= "n"+DEP_SUBJ;
//	/** The dependency label for nominal passive subjects. */
//	String DEP_NSUBJPASS	= DEP_NSUBJ+DEP_PASS;
//	/** The dependency label for elements of compound numbers. */
//	String DEP_NUMBER		= "number";
//	/** The dependency label for participial modifiers. */
//	String DEP_PARTMOD		= "partmod";
//	/** The dependency label for modifiers of prepositions. */
//	String DEP_PMOD 		= "pmod";
//	/** The dependency label for pre-conjuncts. */
//	String DEP_PRECONJ		= "preconj";
//	/** The dependency label for pre-determiners. */
//	String DEP_PREDET		= "predet";
//	/** The dependency label for prepositional modifiers. */
//	String DEP_PREP			= "prep";
//	/** The dependency label for quantifier phrase modifiers. */
//	String DEP_QMOD			= "qmod";
	
	/** The secondary dependency label for right node raising. */
	String DEP2_RNR		= "rnr";
	/** The secondary dependency label for referents. */
	String DEP2_REF		= "ref";
	/** The secondary dependency label for gapping relations. */
	String DEP2_GAP		= "gap";
	/** The dependency label for open clausal subjects. */
	String DEP2_XSUBJ	= "xsubj";
}