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
package edu.emory.mathcs.nlp.component.template.feature;

import java.util.Collection;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.collection.tuple.ObjectFloatPair;
import edu.emory.mathcs.nlp.component.template.state.DocumentState;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.learning.util.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DocumentFeatureTemplate<S extends DocumentState> extends FeatureTemplate<S>
{
	private static final long serialVersionUID = 8581842859392646419L;

	public DocumentFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}
	
	@Override
	public SparseVector createSparseVector(S state, boolean isTrain)
	{
		Collection<ObjectFloatPair<String>> t;
		SparseVector x = new SparseVector();
		int i, type = 0;
		
		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getWeightedFeatures(state, feature_set.get(i));
			if (t != null) for (ObjectFloatPair<String> s : t) add(x, type, s.o, s.f, isTrain);
		}
		
		return x;
	}
	
	protected Collection<ObjectFloatPair<String>> getWeightedFeatures(S state, FeatureItem item)
	{
		switch (item.field)
		{
		default: return null;
		}
	}
	
	@Override
	public float[] createDenseVector(S state)
	{
		return null;
	}
}
