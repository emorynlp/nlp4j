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
public interface POSTagEn
{
	/** The pos tag for affixes. */
	String POS_AFX	= "AFX";
	/** The pos tag for coordinating conjunctions. */
	String POS_CC	= "CC";
	/** The pos tag for cardinal numbers. */
	String POS_CD	= "CD";
	/** The pos tag for codes. */
	String POS_CODE	= "CODE";
	/** The pos tag for colons. */	
	String POS_COLON	= ":";
	/** The pos tag for commas. */
	String POS_COMMA	= ",";
	/** The pos tag for dollar signs. */
	String POS_DOLLAR	= "$";
	/** The pos tag for determiners. */
	String POS_DT	= "DT";
	/** The pos tag for existentials. */
	String POS_EX	= "EX";
	/** The pos tag for foreign words. */
	String POS_FW	= "FW";
	/** The pos tag for hyphens. */
	String POS_HYPH	= "HYPH";
	/** The pos tag for prepositions or subordinating conjunctions. */
	String POS_IN	= "IN";
	/** The pos tag for adjectives. */
	String POS_JJ	= "JJ";
	/** The pos tag for comparative adjectives. */
	String POS_JJR	= "JJR";
	/** The pos tag for superlative adjectives. */
	String POS_JJS	= "JJS";
	/** The pos tag for left quotes. */
	String POS_LQ	= "``";
	/** The pos tag for left round brackets. */
	String POS_LRB	= "-LRB-";
	/** The pos tag for list item markers. */
	String POS_LS	= "LS";
	/** The pos tag for modals. */
	String POS_MD	= "MD";
	/** The pos tag for superfluous punctuation. */
	String POS_NFP	= "NFP";
	/** The pos tag for singular or mass nouns. */
	String POS_NN	= "NN";
	/** The pos tag for singular proper nouns. */
	String POS_NNP	= "NNP";
	/** The pos tag for plural proper nouns. */
	String POS_NNPS	= "NNPS";
	/** The pos tag for plural nouns. */
	String POS_NNS	= "NNS";
	/** The pos tag for predeterminers. */
	String POS_PDT	= "PDT";
	/** The pos tag for possessive endings. */
	String POS_POS	= "POS";
	/** The pos tag for personal pronouns. */
	String POS_PRP	= "PRP";
	/** The pos tag for possessive pronouns. */
	String POS_PRPS	= "PRP$";
	/** Punctuation. */
	String POS_PUNC	= "PUNC";
	/** The pos tag for adverbs. */
	String POS_RB	= "RB";
	/** The pos tag for comparative adverbs. */
	String POS_RBR	= "RBR";
	/** The pos tag for superlative adverbs. */
	String POS_RBS	= "RBS";
	/** The pos tag for particles. */
	String POS_RP	= "RP";
	/** The pos tag for right quotes. */
	String POS_RQ	= "''";
	/** The pos tag for right round brackets. */
	String POS_RRB	= "-RRB-";
	/** The pos tag for symbols. */
	String POS_SYM	= "SYM";
	/** The pos tag for "to". */
	String POS_TO	= "TO";
	/** The pos tag for interjections. */
	String POS_UH	= "UH";
	/** The pos tag for base form verbs. */
	String POS_VB	= "VB";
	/** The pos tag for past tense verbs. */
	String POS_VBD	= "VBD";
	/** The pos tag for gerunds. */
	String POS_VBG	= "VBG";
	/** The pos tag for past participles. */
	String POS_VBN	= "VBN";
	/** The pos tag for non-3rd person singular present verbs. */
	String POS_VBP	= "VBP";
	/** The pos tag for 3rd person singular present verbs. */
	String POS_VBZ	= "VBZ";
	/** The pos tag for wh-determiners. */
	String POS_WDT	= "WDT";
	/** The pos tag for wh-pronouns. */
	String POS_WP	= "WP";
	/** The pos tag for possessive wh-pronouns. */
	String POS_WPS	= "WP$";
	/** The pos tag for wh-adverbs. */
	String POS_WRB	= "WRB";
	/** The pos tag for unknown tokens. */
	String POS_XX	= "XX";

	/** The pos tag for periods. */
	String POS_PERIOD = ".";
	/** The pos tag for emails and urls. */
	String POS_ADD = "ADD";
}