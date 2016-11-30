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
package edu.emory.mathcs.nlp.common.grammar;

import edu.emory.mathcs.nlp.structure.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TVerb
{
	private String lemma;
	private Tense  tense;
	private Aspect aspect;
	private Voice  voice;
	
	public TVerb(String lemma)
	{
		setLemma(lemma);
		setTense(Tense.none);
		setAspect(Aspect.none);
	}
	
	public String getLemma()
	{
		return lemma;
	}

	public void setLemma(String lemma)
	{
		this.lemma = lemma;
	}

	public Tense getTense()
	{
		return tense;
	}
	
	public void setTense(Tense tense)
	{
		this.tense = tense;
	}
	
	public Aspect getAspect()
	{
		return aspect;
	}
	
	public void setAspect(Aspect aspect)
	{
		this.aspect = aspect;
	}

	public Voice getVoice()
	{
		return voice;
	}

	public void setVoice(Voice voice)
	{
		this.voice = voice;
	}
	
	public boolean isPresentTense()
	{
		return tense == Tense.present;
	}
	
	public boolean isPastTense()
	{
		return tense == Tense.past;
	}
	
	public boolean isFutureTense()
	{
		return tense == Tense.future;
	}
	
	public boolean isProgressiveAspect()
	{
		return aspect == Aspect.progressive;
	}
	
	public boolean isPerfectAspect()
	{
		return aspect == Aspect.perfect;
	}
	
	public boolean isPerfectProgressiveAspect()
	{
		return aspect == Aspect.perfect_progressive;
	}
	
	public boolean isActiveVoice()
	{
		return voice == Voice.active;
	}
	
	public boolean isPassiveVoice()
	{
		return voice == Voice.passive;
	}
	
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append("tense");
		build.append(FeatMap.DELIM_KEY_VALUE);
		build.append(tense);
		build.append(FeatMap.DELIM_FEATS);
		
		build.append("aspect");
		build.append(FeatMap.DELIM_KEY_VALUE);
		build.append(aspect);
		build.append(FeatMap.DELIM_FEATS);
		
		build.append("voice");
		build.append(FeatMap.DELIM_KEY_VALUE);
		build.append(voice);
		build.append(FeatMap.DELIM_FEATS);
		
		return build.toString();
	}
}

//private Tense  tense;
//private Aspect aspect;
//private Voice  voice;
//private Set<String> modals;
//private boolean negation;