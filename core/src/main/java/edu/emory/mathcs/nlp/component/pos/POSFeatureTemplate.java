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
package edu.emory.mathcs.nlp.component.pos;

import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class POSFeatureTemplate extends FeatureTemplate<POSState>
{
	public POSFeatureTemplate(int dynamicFeatureSize)
	{
		super(dynamicFeatureSize);
	}

	private static final long serialVersionUID = -243334323533999837L;
	
	@Override
	protected String getFeature(POSState state, FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		if (node == null) return null;
		
		switch (item.field)
		{
		case ambiguity_class: return state.getAmbiguityClass(node);
		default: return getFeature(item, node);
		}
	}
}