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
package edu.emory.mathcs.nlp.component.doc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCState<N extends AbstractNLPNode<N>> extends NLPState<N>
{
	protected N         key_node;
	protected String    feat_key;
	protected String    oracle;
	protected boolean   terminate;
	protected List<N[]> non_stopwords;
	protected float[]   prediction_scores;
	protected float[][] ensemble_scores;
	
	public DOCState(List<N[]> document, String key)
	{
		super(document);
		feat_key = key;
		key_node = document.get(0)[1];
		non_stopwords = getNonStopWords(document);
		reinit();
	}
	
	@SuppressWarnings("unchecked")
	public List<N[]> getNonStopWords(List<N[]> document)
	{
		List<N[]> nonstop = new ArrayList<>();
		N node;
		
		for (N[] nodes : document)
		{
			List<N> sen = new ArrayList<>();
			
			for (int i=1; i<nodes.length; i++)
			{
				node = nodes[i];
				if (!node.isStopWord()) sen.add(node);
			}
			
			if (!sen.isEmpty())
			{
				N[] snodes = (N[])Array.newInstance(key_node.getClass(), sen.size()+1);
				snodes[0] = nodes[0];
				
				for (int i=1; i<snodes.length; i++)
					snodes[i] = sen.get(i-1);
				
				nonstop.add(snodes);
			}
		}
		
		return nonstop;
	}
	
	public void reinit()
	{
		terminate = false;
		prediction_scores = null;
	}
	
//	============================== ORACLE ==============================
	
	@Override
	public boolean saveOracle()
	{
		oracle = key_node.removeFeat(feat_key);
		return oracle != null;
	}
	
	@Override
	public void resetOracle()
	{
		setLabel(oracle);
	}

	@Override
	public String getOracle()
	{
		return oracle;
	}
	
//	============================== GETTERS/SETTERS ==============================
	
	public List<N[]> getDocument(boolean excludeStopwords)
	{
		return excludeStopwords ? non_stopwords : getDocument();
	}
	
	public String getLabel()
	{
		return key_node.getFeat(feat_key);
	}
	
	public void setLabel(String label)
	{
		key_node.putFeat(feat_key, label);
	}
	
	public float[] getPredictionScores()
	{
		return prediction_scores;
	}

	public void setPredictionScores(float[] scores)
	{
		this.prediction_scores = scores;
	}
	
	public float[][] getEnsembleScores()
	{
		return ensemble_scores;
	}

	public void setEnsembleScores(float[][] scores)
	{
		this.ensemble_scores = scores;
	}
	
	public N getKeyNode()
	{
		return key_node;
	}
	
	@Override
	public N getNode(FeatureItem item)
	{
		return null;
	}

//	============================== TRANSITION ==============================
	
	@Override
	public void next(LabelMap map, int[] top2, float[] scores)
	{
		setLabel(map.getLabel(top2[0]));
		setPredictionScores(scores);
		terminate = true;
	}

	@Override
	public boolean isTerminate()
	{
		return terminate;
	}

	@Override
	public void evaluate(Eval eval)
	{
		int correct = oracle.equals(getLabel()) ? 1 : 0;
		((AccuracyEval)eval).add(correct, 1);
	}
}
