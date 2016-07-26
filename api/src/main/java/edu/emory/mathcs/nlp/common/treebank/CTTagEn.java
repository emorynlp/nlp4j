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
package edu.emory.mathcs.nlp.common.treebank;



/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface CTTagEn
{
	/** The special tag for the artificial top node. */
	String TOP  = "TOP";
	/** The special tag for empty categories. */
	String NONE = "-NONE-";
	
	/** The constituent tag for declaratives. */
	String C_S		= "S";
	/** The constituent tag for subordinating clauses. */
	String C_SBAR	= "SBAR";
	/** The constituent tag for wh-questions. */
	String C_SBARQ	= "SBARQ";
	/** The constituent tag for inverted declaratives. */
	String C_SINV	= "SINV";
	/** The constituent tag for polar questions. */
	String C_SQ	= "SQ";
	
	/** The constituent tag for adjective phrases. */
	String C_ADJP		= "ADJP";
	/** The constituent tag for adverb phrases. */
	String C_ADVP		= "ADVP";
	/** The constituent tag for captions. */
	String C_CAPTION	= "CAPTION";
	/** The constituent tag for citations. */
	String C_CIT		= "CIT";
	/** The constituent tag for conjunction phrases. */
	String C_CONJP		= "CONJP";
	/** The constituent tag for edited phrases. */
	String C_EDITED	= "EDITED";
	/** The constituent tag for embedded phrases. */
	String C_EMBED		= "EMBED";
	/** The constituent tag for fragments. */
	String C_FRAG		= "FRAG";
	/** The constituent tag for headings. */
	String C_HEADING	= "HEADING";
	/** The constituent tag for interjections. */
	String C_INTJ		= "INTJ";
	/** The constituent tag for list markers. */
	String C_LST		= "LST";
	/** The constituent tag for meta phrases. */
	String C_META		= "META";
	/** The constituent tag for "not a constituent". */
	String C_NAC		= "NAC";
	/** The constituent tag for nominal phrases. */
	String C_NML		= "NML";
	/** The constituent tag for noun phrases. */
	String C_NP		= "NP";
	/** The constituent tag for complex noun phrases. */
	String C_NX		= "NX";
	/** The constituent tag for prepositional phrases. */
	String C_PP		= "PP";
	/** The constituent tag for parenthetical phrases. */
	String C_PRN		= "PRN";
	/** The constituent tag for particles. */
	String C_PRT		= "PRT";
	/** The constituent tag for quantifier phrases. */
	String C_QP		= "QP";
	/** The constituent tag for reduced relative clauses. */
	String C_RRC		= "RRC";
	/** The constituent tag for titles. */
	String C_TITLE		= "TITLE";
	/** The constituent tag for types. */
	String C_TYPO		= "TYPO";
	/** The constituent tag for unlike coordinated phrases. */
	String C_UCP		= "UCP";
	/** The constituent tag for verb phrases. */
	String C_VP		= "VP"; 
	/** The constituent tag for wh-adjective phrases. */
	String C_WHADJP	= "WHADJP";
	/** The constituent tag for wh-adverb phrases. */
	String C_WHADVP	= "WHADVP";
	/** The constituent tag for wh-noun phrases. */
	String C_WHNP		= "WHNP";
	/** The constituent tag for wh-prepositional phrases. */
	String C_WHPP		= "WHPP";
	/** The constituent tag for unknown phrases. */
	String C_X			= "X";

	/** The function tag for adverbials. */
	String F_ADV = "ADV";
	/** The function tag for benefactives. */
	String F_BNF = "BNF";
	/** The function tag for clefts. */
	String F_CLF = "CLF";
	/** The function tag for closely related constituents. */
	String F_CLR = "CLR";
	/** The function tag for directions. */
	String F_DIR = "DIR";
	/** The function tag for datives. */
	String F_DTV = "DTV";
	/** The function tag for et cetera. */
	String F_ETC = "ETC";
	/** The function tag for extents. */
	String F_EXT = "EXT";
	/** The function tag for headlines. */
	String F_HLN = "HLN";
	/** The function tag for imperatives. */
	String F_IMP = "IMP";
	/** The function tag for interrogative. */
	String F_INT = "INT";
	/** The function tag for logical subjects. */
	String F_LGS = "LGS";
	/** The function tag for locatives. */
	String F_LOC = "LOC";
	/** The function tag for manners. */
	String F_MNR = "MNR";
	/** The function tag for nominalizations. */
	String F_NOM = "NOM";
	/** The function tag for predicates. */
	String F_PRD = "PRD";
	/** The function tag for purposes. */
	String F_PRP = "PRP";
	/** The function tag for the locative complement of "put". */
	String F_PUT = "PUT";
	/** The function tag for surface subjects. */
	String F_SBJ = "SBJ";
	/** The function tag for direct speeches. */
	String F_SEZ = "SEZ";
	/** The function tag for temporals. */
	String F_TMP = "TMP";
	/** The function tag for topicalizations. */
	String F_TPC = "TPC";
	/** The function tag for titles. */
	String F_TTL = "TTL";
	/** The function tag for unfinished constituents. */
	String F_UNF = "UNF";
	/** The function tag for vocatives. */
	String F_VOC = "VOC";

	/** The empty category representing expletives ({@code *EXP*}). */
	String E_EXP	= "*EXP*";
	/** The empty category representing ellipsed materials ({@code *?*}). */
	String E_ESM	= "*?*";
	/** The empty category representing "interpret constituent here" ({@code *ICH*}). */
	String E_ICH	= "*ICH*";
	/** The empty category representing anti-placeholders of gappings ({@code *NOT*}). */
	String E_NOT	= "*NOT*";
	/** The empty category representing null complementizers ({@code 0}). */
	String E_ZERO	= "0";
	/** The empty category representing "permanet predictable ambiguity" ({@code *PPA*}). */
	String E_PPA	= "*PPA*";
	/** The empty category representing subject/object controls ({@code *PRO*}). */
	String E_PRO	= "*PRO*";
	/** The empty category representing "right node raising" ({@code *RNR*}). */
	String E_RNR	= "*RNR*";
	/** The empty category representing passive nulls ({@code *}). */
	String E_NULL	= "*";
	/** The empty category representing traces ({@code *T*}). */
	String E_TRACE	= "*T*";
	/** The empty category representing null units ({@code *U*}). */
	String E_UNIT	= "*U*";
}