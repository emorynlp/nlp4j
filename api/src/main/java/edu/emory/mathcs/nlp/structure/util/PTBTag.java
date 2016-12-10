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
package edu.emory.mathcs.nlp.structure.util;

import edu.emory.mathcs.nlp.structure.constituency.CTTag;

/**
 * Penn English Treebank Tags.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PTBTag extends CTTag
{
	/** The clausal tag for declaratives. */
	String C_S     = "S";
	/** The clausal tag for subordinating clauses. */
	String C_SBAR  = "SBAR";
	/** The clausal tag for wh-questions. */
	String C_SBARQ = "SBARQ";
	/** The clausal tag for inverted declaratives. */
	String C_SINV  = "SINV";
	/** The clausal tag for polar questions. */
	String C_SQ    = "SQ";
	
	/** The phrasal tag for adjective phrases. */
	String C_ADJP    = "ADJP";
	/** The phrasal tag for adverb phrases. */
	String C_ADVP    = "ADVP";
	/** The phrasal tag for captions. */
	String C_CAPTION = "CAPTION";
	/** The phrasal tag for citations. */
	String C_CIT     = "CIT";
	/** The phrasal tag for conjunction phrases. */
	String C_CONJP   = "CONJP";
	/** The phrasal tag for edited phrases. */
	String C_EDITED  = "EDITED";
	/** The phrasal tag for embedded phrases. */
	String C_EMBED   = "EMBED";
	/** The phrasal tag for fragments. */
	String C_FRAG    = "FRAG";
	/** The phrasal tag for headings. */
	String C_HEADING = "HEADING";
	/** The phrasal tag for interjections. */
	String C_INTJ    = "INTJ";
	/** The phrasal tag for list markers. */
	String C_LST     = "LST";
	/** The phrasal tag for meta phrases. */
	String C_META    = "META";
	/** The phrasal tag for "not a constituent". */
	String C_NAC     = "NAC";
	/** The phrasal tag for nominal phrases. */
	String C_NML     = "NML";
	/** The phrasal tag for noun phrases. */
	String C_NP      = "NP";
	/** The phrasal tag for complex noun phrases. */
	String C_NX      = "NX";
	/** The phrasal tag for prepositional phrases. */
	String C_PP      = "PP";
	/** The phrasal tag for parenthetical phrases. */
	String C_PRN     = "PRN";
	/** The phrasal tag for particles. */
	String C_PRT     = "PRT";
	/** The phrasal tag for quantifier phrases. */
	String C_QP      = "QP";
	/** The phrasal tag for reduced relative clauses. */
	String C_RRC     = "RRC";
	/** The phrasal tag for titles. */
	String C_TITLE   = "TITLE";
	/** The phrasal tag for types. */
	String C_TYPO    = "TYPO";
	/** The phrasal tag for unlike coordinated phrases. */
	String C_UCP     = "UCP";
	/** The phrasal tag for verb phrases. */
	String C_VP      = "VP"; 
	/** The phrasal tag for wh-adjective phrases. */
	String C_WHADJP  = "WHADJP";
	/** The phrasal tag for wh-adverb phrases. */
	String C_WHADVP  = "WHADVP";
	/** The phrasal tag for wh-noun phrases. */
	String C_WHNP    = "WHNP";
	/** The phrasal tag for wh-prepositional phrases. */
	String C_WHPP    = "WHPP";
	/** The phrasal tag for unknown phrases. */
	String C_X       = "X";

	/** The part-of-speech tag for colons. */
    String P_COLON  = ":";
    /** The part-of-speech tag for commas. */
    String P_COMMA  = ",";
    /** The part-of-speech tag for dollar signs. */
    String P_DOLLAR = "$";
    /** The part-of-speech tag for periods. */
    String P_PERIOD = ".";
    /** The part-of-speech tag for left quotes. */
    String P_LQ     = "``";
    /** The part-of-speech tag for right quotes. */
    String P_RQ     = "''";
    /** The part-of-speech tag for left round brackets. */
    String P_LRB    = "-LRB-";
    /** The part-of-speech tag for right round brackets. */
    String P_RRB    = "-RRB-";
    /** The part-of-speech tag for symbols. */
    String P_SYM    = "SYM";
    /** The part-of-speech tag for emails and urls. */
    String P_ADD  = "ADD";
	/** The part-of-speech tag for affixes. */
	String P_AFX  = "AFX";
	/** The part-of-speech tag for coordinating conjunctions. */
	String P_CC   = "CC";
	/** The part-of-speech tag for cardinal numbers. */
	String P_CD   = "CD";
	/** The part-of-speech tag for codes. */
	String P_CODE = "CODE";
	/** The part-of-speech tag for determiners. */
	String P_DT   = "DT";
	/** The part-of-speech tag for emoticons. */
	String P_EMO  = "EMO";
	/** The part-of-speech tag for existentials. */
	String P_EX   = "EX";
	/** The part-of-speech tag for foreign words. */
	String P_FW   = "FW";
	/** The part-of-speech tag for goes-with. */
	String P_GW   = "GW";
	/** The part-of-speech tag for hyphens. */
	String P_HYPH = "HYPH";
	/** The part-of-speech tag for prepositions or subordinating conjunctions. */
	String P_IN   = "IN";
	/** The part-of-speech tag for adjectives. */
	String P_JJ   = "JJ";
	/** The part-of-speech tag for comparative adjectives. */
	String P_JJR  = "JJR";
	/** The part-of-speech tag for superlative adjectives. */
	String P_JJS  = "JJS";
	/** The part-of-speech tag for list item markers. */
	String P_LS   = "LS";
	/** The part-of-speech tag for modals. */
	String P_MD   = "MD";
	/** The part-of-speech tag for superfluous punctuation. */
	String P_NFP  = "NFP";
	/** The part-of-speech tag for singular or mass nouns. */
	String P_NN   = "NN";
	/** The part-of-speech tag for singular proper nouns. */
	String P_NNP  = "NNP";
	/** The part-of-speech tag for plural proper nouns. */
	String P_NNPS = "NNPS";
	/** The part-of-speech tag for plural nouns. */
	String P_NNS  = "NNS";
	/** The part-of-speech tag for predeterminers. */
	String P_PDT  = "PDT";
	/** The part-of-speech tag for possessive endings. */
	String P_POS  = "POS";
	/** The part-of-speech tag for personal pronouns. */
	String P_PRP  = "PRP";
	/** The part-of-speech tag for possessive pronouns. */
	String P_PRPS = "PRP$";
	/** Punctuation. */
	String P_PUNC = "PUNC";
	/** The part-of-speech tag for adverbs. */
	String P_RB   = "RB";
	/** The part-of-speech tag for comparative adverbs. */
	String P_RBR  = "RBR";
	/** The part-of-speech tag for superlative adverbs. */
	String P_RBS  = "RBS";
	/** The part-of-speech tag for particles. */
	String P_RP   = "RP";
	/** The part-of-speech tag for "to". */
	String P_TO   = "TO";
	/** The part-of-speech tag for interjections. */
	String P_UH   = "UH";
	/** The part-of-speech tag for base form verbs. */
	String P_VB   = "VB";
	/** The part-of-speech tag for past tense verbs. */
	String P_VBD  = "VBD";
	/** The part-of-speech tag for gerunds. */
	String P_VBG  = "VBG";
	/** The part-of-speech tag for past participles. */
	String P_VBN  = "VBN";
	/** The part-of-speech tag for non-3rd person singular present verbs. */
	String P_VBP  = "VBP";
	/** The part-of-speech tag for 3rd person singular present verbs. */
	String P_VBZ  = "VBZ";
	/** The part-of-speech tag for wh-determiners. */
	String P_WDT  = "WDT";
	/** The part-of-speech tag for wh-pronouns. */
	String P_WP   = "WP";
	/** The part-of-speech tag for possessive wh-pronouns. */
	String P_WPS  = "WP$";
	/** The part-of-speech tag for wh-adverbs. */
	String P_WRB  = "WRB";
	/** The part-of-speech tag for unknown tokens. */
	String P_XX   = "XX";
	
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
	String E_EXP   = "*EXP*";
	/** The empty category representing ellipsed materials ({@code *?*}). */
	String E_ESM   = "*?*";
	/** The empty category representing "interpret constituent here" ({@code *ICH*}). */
	String E_ICH   = "*ICH*";
	/** The empty category representing anti-placeholders of gappings ({@code *NOT*}). */
	String E_NOT   = "*NOT*";
	/** The empty category representing null complementizers ({@code 0}). */
	String E_ZERO  = "0";
	/** The empty category representing "permanent predictable ambiguity" ({@code *PPA*}). */
	String E_PPA   = "*PPA*";
	/** The empty category representing subject/object controls ({@code *PRO*}). */
	String E_PRO   = "*PRO*";
	/** The empty category representing "right node raising" ({@code *RNR*}). */
	String E_RNR   = "*RNR*";
	/** The empty category representing passive nulls ({@code *}). */
	String E_NULL  = "*";
	/** The empty category representing traces ({@code *T*}). */
	String E_TRACE = "*T*";
	/** The empty category representing null units ({@code *U*}). */
	String E_UNIT  = "*U*";
}