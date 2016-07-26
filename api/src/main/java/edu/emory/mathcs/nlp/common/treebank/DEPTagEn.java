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
public interface DEPTagEn
{
	/** The dependency label for passive. */
	String DEP_PASS	= "pass";
	/** The dependency label for subjects. */
	String DEP_SUBJ	= "subj";
	
	/** The dependency label for finite and non-finite clausal modifier. */
	String DEP_ACL			= "acl";
	/** The dependency label for adjectival complements. */
	String DEP_ACOMP		= "acomp";
	/** The dependency label for adverbial clause modifiers. */
	String DEP_ADVCL		= "advcl";
	/** The dependency label for adverbial modifiers. */
	String DEP_ADVMOD		= "advmod";
	/** The dependency label for agents. */
	String DEP_AGENT		= "agent";
	/** The dependency label for adjectival modifiers. */
	String DEP_AMOD			= "amod";
	/** The dependency label for appositional modifiers. */
	String DEP_APPOS		= "appos";
	/** The dependency label for attributes. */
	String DEP_ATTR			= "attr";
	/** The dependency label for auxiliary verbs. */
	String DEP_AUX			= "aux";
	/** The dependency label for passive auxiliary verbs. */
	String DEP_AUXPASS		= DEP_AUX+DEP_PASS;
	/** The dependency label for case marker. */
	String DEP_CASE 		= "case";
	/** The dependency label for coordinating conjunctions. */
	String DEP_CC			= "cc";
	/** The dependency label for clausal complements. */
	String DEP_CCOMP		= "ccomp";
//	/** The dependency label for complementizers. */
//	String DEP_COMPLM		= "complm";
	/** The dependency label for compound nouns/numbers. */
	String DEP_COMPOUND		= "compound";
	/** The dependency label for conjuncts. */
	String DEP_CONJ			= "conj";
	/** The dependency label for clausal subjects. */
	String DEP_CSUBJ		= "c"+DEP_SUBJ;
	/** The dependency label for clausal passive subjects. */
	String DEP_CSUBJPASS	= DEP_CSUBJ+DEP_PASS;
	/** The dependency label for dative. */
	String DEP_DATIVE  		= "dative";
	/** The dependency label for unknown dependencies. */
	String DEP_DEP  		= "dep";
	/** The dependency label for determiners. */
	String DEP_DET			= "det";
	/** The dependency label for direct objects. */
	String DEP_DOBJ 		= "dobj";
	/** The dependency label for expletives. */
	String DEP_EXPL 		= "expl";
	/** The dependency label for modifiers in hyphenation. */
//	String DEP_HMOD 		= "hmod";
//	/** The dependency label for hyphenation. */
//	String DEP_HYPH 		= "hyph";
//	/** The dependency label for indirect objects. */
	String DEP_IOBJ 		= "iobj";
	/** The dependency label for interjections. */
	String DEP_INTJ			= "intj";
	/** The dependency label for markers. */
	String DEP_MARK			= "mark";
	/** The dependency label for meta modifiers. */
	String DEP_META			= "meta";
	/** The dependency label for negation modifiers. */
	String DEP_NEG			= "neg";
//	/** The dependency label for infinitival modifiers. */
//	String DEP_INFMOD		= "infmod";
	/** The dependency label for noun phrase modifiers. */
	String DEP_NOUNMOD 		= "nmod";
//	/** The dependency label for noun compound modifiers. */
//	String DEP_NN			= "nn";
	/** The dependency label for noun phrase as adverbial modifiers. */
	String DEP_NPADVMOD		= "npadvmod";
	/** The dependency label for nominal subjects. */
	String DEP_NSUBJ		= "n"+DEP_SUBJ;
	/** The dependency label for nominal passive subjects. */
	String DEP_NSUBJPASS	= DEP_NSUBJ+DEP_PASS;
	/** The dependency label for numeric modifiers. */
	String DEP_NUMMOD		= "nummod";
//	/** The dependency label for elements of compound numbers. */
//	String DEP_NUMBER		= "number";
	/** The dependency label for object predicates. */
	String DEP_OPRD			= "oprd";
	/** The dependency label for parataxis. */
	String DEP_PARATAXIS 	= "parataxis";
//	/** The dependency label for participial modifiers. */
//	String DEP_PARTMOD		= "partmod";
	/** The dependency label for modifiers of prepositions. */
	String DEP_PMOD 		= "pmod";
	/** The dependency label for prepositional complements. */
	String DEP_PCOMP 		= "pcomp";
	/** The dependency label for objects of prepositions. */
	String DEP_POBJ 		= "pobj";
	/** The dependency label for possession modifiers. */
	String DEP_POSS			= "poss";
	/** The dependency label for pre-conjuncts. */
	String DEP_PRECONJ		= "preconj";
	/** The dependency label for pre-determiners. */
	String DEP_PREDET		= "predet";
	/** The dependency label for prepositional modifiers. */
	String DEP_PREP			= "prep";
	/** The dependency label for particles. */
	String DEP_PRT 			= "prt";
	/** The dependency label for punctuation. */
	String DEP_PUNCT		= "punct";
	/** The dependency label for quantifier phrase modifiers. */
	String DEP_QUANTMOD		= "quantmod";
	/** The dependency label for relative clause modifiers. */
	String DEP_RELCL		= "relcl";
	/** The dependency label for roots. */
	String DEP_ROOT 		= "root";
	/** The dependency label for open clausal modifiers. */
	String DEP_XCOMP		= "xcomp";
	
	/** The secondary dependency label for right node raising. */
	String DEP2_RNR		= "rnr";
	/** The secondary dependency label for referents. */
	String DEP2_REF		= "ref";
	/** The secondary dependency label for gapping relations. */
	String DEP2_GAP		= "gap";
	/** The dependency label for open clausal subjects. */
	String DEP2_XSUBJ	= "xsubj";
}