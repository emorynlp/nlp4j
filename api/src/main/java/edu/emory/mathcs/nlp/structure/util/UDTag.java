/**
 * Copyright 2016, Emory University
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
public interface UDTag
{
	/** nominal subject */
	String NSUBJ      = "nsubj";
	/** passive nominal subject */
	String NSUBJPASS  = "nsubjpass";
	/** clausal subject */
	String CSUBJ      = "csubj";
	/** clausal passive subject */
	String CSUBJPASS  = "csubjpass";

	/** direct object */
	String DOBJ       = "dobj";
	/** indirect object */
	String IOBJ       = "iobj";
	/** clausal complement */
	String CCOMP      = "ccomp";
	/** open clausal complement */
	String XCOMP      = "xcomp";

	/** auxiliary */
	String AUX        = "aux";
	/** passive auxiliary */
	String AUXPASS    = "auxpass";
	/** copula */
	String COP        = "cop";

	/** coordination */
	String CC         = "cc";
	/** conjunct */
	String CONJ       = "conj";
	/** preconjunct */
	String PRECONJ    = "preconj";

	/** determiner */
	String DET        = "det";
	/** predeterminer */
	String PREDET     = "predet";

	/** clausal modifier of noun */
	String ACL        = "acl";
	/** relative clause modifier */
	String RELCL      = "relcl";
	/** appositional modifier */
	String APPOS      = "appos";

	/** adverbial clause modifier */
	String ADVCL      = "advcl";
	/** adverbial modifier */
	String ADVMOD     = "advmod";
	/** adjectival modifier */
	String AMOD       = "amod";
	/** case marking */
	String CASE       = "case";
	/** compound */
	String COMPOUND   = "compound";
	/** phrasal verb particle */
	String PRT        = "prt";
	/** dependent */
	String DEP        = "dep";
	/** discourse element */
	String DISCOURSE  = "discourse";
	/** dislocated elements */
	String DISLOCATED = "dislocated";
	/** expletive */
	String EXPL       = "expl";
	/** foreign words */
	String FOREIGN    = "foreign";
	/** goes with */
	String GOESWITH   = "goeswith";
	/** list */
	String LIST       = "list";
	/** marker */
	String MARK       = "mark";
	/** multi-word expression */
	String MWE        = "mwe";
	/** name */
	String NAME       = "name";
	/** negation modifier */
	String NEG        = "neg";
	/** nominal modifier */
	String NMOD       = "nmod";
	/** noun phrase as adverbial modifier */
	String NPMOD      = "npmod";
	/** possessive nominal modifier */
	String POSS       = "poss";
	/** temporal modifier */
	String TMOD       = "tmod";
	/** numeric modifier */
	String NUMMOD     = "nummod";
	/** parataxis */
	String PARATAXIS  = "parataxis";
	/** punctuation */
	String PUNCT      = "punct";
	/** remnant in ellipsis */
	String REMNANT    = "remnant";
	/** overridden disfluency */
	String REPARANDUM = "reparandum";
	/** root */
	String ROOT       = "root";
	/** vocative */
	String VOCATIVE   = "vocative";
}
