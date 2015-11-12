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
package edu.emory.mathcs.nlp.common.constant;

import java.util.regex.Pattern;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PatternConst
{
	Pattern COMMA		= Pattern.compile(StringConst.COMMA);
	Pattern COLON		= Pattern.compile(StringConst.COLON);
	Pattern HYPHEN		= Pattern.compile(StringConst.HYPHEN);
	Pattern SEMICOLON	= Pattern.compile(StringConst.SEMICOLON);
	Pattern UNDERSCORE	= Pattern.compile(StringConst.UNDERSCORE);
	
	Pattern SPACE		= Pattern.compile(StringConst.SPACE);
	Pattern TAB			= Pattern.compile(StringConst.TAB);
	Pattern WHITESPACES	= Pattern.compile("\\s+");

	Pattern PUNCT	    = Pattern.compile("\\p{Punct}");
	Pattern PUNCT_ONLY  = Pattern.compile("^\\p{Punct}+$");

	Pattern DIGITS		= Pattern.compile("\\d+");
	Pattern DIGITS_ONLY	= Pattern.compile("^\\d+$");

	
	Pattern PUNCT_FINALS     = Pattern.compile("(\\.|\\?|\\!){2,}");
	Pattern PUNCT_SEPARATORS = Pattern.compile("\\*{2,}|-{2,}|={2,}|~{2,}|,{2,}|`{2,}|'{2,}");

	Pattern NUMBER = Pattern.compile("(-|\\+|\\.)?\\d+(,\\d{3})*(\\.\\d+)?");
	
	Pattern HTML_TAG = Pattern.compile("&([#]?\\p{Alnum}{2,}?);", Pattern.CASE_INSENSITIVE);
	Pattern TWITTER_HASH_TAG = Pattern.compile("^\\p{Alpha}[\\p{Alnum}_]{1,138}$");
	Pattern TWITTER_USER_ID  = Pattern.compile("^\\p{Alpha}[\\p{Alnum}_]{1,19}$");
}