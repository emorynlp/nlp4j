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
package edu.emory.mathcs.nlp.zzz;

import java.util.regex.Pattern;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp(String[] args) throws Exception
	{
		final Pattern DEPREL = Pattern.compile("^(nsubj|nsubjpass|dobj)$");
		System.out.println(DEPREL.matcher("nsubj").find());
		System.out.println(DEPREL.matcher("nsubjpass").find());
		System.out.println(DEPREL.matcher("dobj").find());
		System.out.println(DEPREL.matcher("csubj").find());
		System.out.println(DEPREL.matcher("iobj").find());
	}
	
	boolean skip(String form)
	{
		char[] cs = form.toCharArray();
		if (cs.length < 3 || cs.length > 20) return true;
		
		for (int i=0; i<cs.length; i++)
		{
			if (cs[i] == '_' || cs[i] >= 128)
				return true;
		}
		
		return false;
	}
		
	static public void main(String[] args) throws Exception
	{
		new Tmp(args);
	}
}

