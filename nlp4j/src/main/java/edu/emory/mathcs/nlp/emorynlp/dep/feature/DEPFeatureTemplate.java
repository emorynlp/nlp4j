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
package edu.emory.mathcs.nlp.emorynlp.dep.feature;

import edu.emory.mathcs.nlp.emorynlp.component.feature.Direction;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.dep.DEPState;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class DEPFeatureTemplate<N extends NLPNode> extends FeatureTemplate<N,DEPState<N>>
{
	private static final long serialVersionUID = -2218894375050796569L;

	public DEPFeatureTemplate()	
	{
		init();
	}
	
	protected abstract void init();
	
//	========================= FEATURE EXTRACTORS =========================
	
	@Override
	protected String getFeature(FeatureItem<?> item)
	{
		NLPNode node = getNode(item);
		if (node == null) return null;
		
		switch (item.field)
		{
		case word_form: return node.getWordForm();
		case simplified_word_form: return node.getSimplifiedWordForm();
		case lemma: return node.getLemma();
		case part_of_speech_tag: return node.getPartOfSpeechTag();
		case feats: return node.getFeat((String)item.value);
		case dependency_label: return node.getDependencyLabel();
		case valency: return node.getValency((Direction)item.value);
		default: throw new IllegalArgumentException("Unsupported feature: "+item.field);
		}
	}
	
	@Override
	protected String[] getFeatures(FeatureItem<?> item)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	protected NLPNode getNode(FeatureItem<?> item)
	{
		NLPNode node = null;
		
		switch (item.source)
		{
		case i: node = state.getStack (item.window); break;
		case j: node = state.getInput (item.window); break;
		case k: node = state.peekStack(item.window); break;
		}
		
		return getNode(node, item);
	}
	
	protected NLPNode getNode(NLPNode node, FeatureItem<?> item)
	{
		if (node == null || item.relation == null)
			return node;
		
		switch (item.relation)
		{
		case h   : return node.getDependencyHead();
		case h2  : return node.getGrandDependencyHead();
		case lmd : return node.getLeftMostDependent();
		case lmd2: return node.getLeftMostDependent(1);
		case lnd : return node.getLeftNearestDependent();
		case lnd2: return node.getLeftNearestDependent(1);
		case lns : return node.getLeftNearestSibling();
		case lns2: return node.getLeftNearestSibling(1);
		case rmd : return node.getRightMostDependent();
		case rmd2: return node.getRightMostDependent(1);
		case rnd : return node.getRightNearestDependent();
		case rnd2: return node.getRightNearestDependent(1);
		case rns : return node.getRightNearestSibling();
		case rns2: return node.getRightNearestSibling(1);
		}
		
		return null;
	}
}
