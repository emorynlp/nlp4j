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
package edu.emory.mathcs.nlp.component.it;

import java.util.List;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ItFeatureTemplate<N extends AbstractNLPNode<N>> extends FeatureTemplate<N,ItState<N>>
{
	private static final long serialVersionUID = 2899139489408851889L;
	protected List<Field> feature_list_type;
	
	public ItFeatureTemplate(Element eFeatures, HyperParameter hp)
	{
		super(eFeatures, hp);
	}
	
//	@Override
//	public SparseVector createSparseVector(ItState<N> state, boolean isTrain)
//	{
//		SparseVector x = super.createSparseVector(state, isTrain);
//		int type = feature_set.size() + feature_list.size();
//		String value;
//		
//		value = getPreviousIt(state);
//		add(x, type++, value, 1, isTrain);
//		return x;
//	}
	
	@Override
	public float[] createDenseVector(ItState<N> state)
	{
		float[] v = new float[3];
		
		v[0] = (float)MathUtils.divide(state.getNodeID(), state.getNodes().length-1);
		v[1] = (float)MathUtils.divide(state.getTreeID(), state.getDocument().size());
		v[2] = distanceToPredecessor(state, "NN");
		
		return v;
	}
	
	public float distanceToPredecessor(ItState<N> state, String pos)
	{
		N[] nodes = state.getNodes();
		
		for (int i=state.getNodeID()-1; i>0; i--)
		{
			N node = nodes[i];
			if (node.getPartOfSpeechTag().startsWith(pos))
				return (float)MathUtils.divide(state.getNodeID()-i, nodes.length-1);
		}
		
		return 0f;
	}
	
	public float distanceToSuccessor(ItState<N> state, String pos)
	{
		N[] nodes = state.getNodes();
		
		for (int i=state.getNodeID()+1; i<nodes.length; i++)
		{
			N node = nodes[i];
			if (node.getPartOfSpeechTag().startsWith(pos))
				return (float)MathUtils.divide(i-state.getNodeID(), nodes.length-1);
		}
		
		return 0f;
	}
	
//	public String getPreviousIt(ItState<N> state)
//	{
//		List<N[]> document = state.getDocument();
//		int start;
//		
//		for (int i=state.getTreeID(); i>=0; i--)
//		{
//			N[] nodes = document.get(i);
//			start = (i == state.getTreeID()) ? state.getNodeID() : nodes.length;
//
//			for (int j=start-1; j>0; j--)
//			{
//				N node = nodes[j];
//				if (state.isIt(node)) return node.getFeat(ItState.FEAT_KEY);
//			}
//		}
//
//		return null;
//	}
	
//	public float[] averageEmbeddings(ItState<N> state)
//	{
//		N[] nodes = state.getNodes();
//		float[] emb = null, v;
//		int t = 0;
//		
//		for (int i=state.getNodeID()+1; i<nodes.length && i<state.getNodeID()+3; i++)
//		{
//			N node = nodes[i];
//			
//			if (node.hasWordEmbedding())
//			{
//				t++;
//				v = node.getWordEmbedding();
//				if (emb == null) emb = Arrays.copyOf(v, v.length);
//				else MathUtils.add(emb, v);
//			}
//		}
//		
//		if (emb != null)
//		{
//			for (int i=0; i<emb.length; i++)
//				emb[i] /= t;
//		}
//		
//		return emb;
//	}
}
