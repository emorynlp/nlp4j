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

import java.util.List;

import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;
import edu.emory.mathcs.nlp.learning.util.LabelMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DOCState extends NLPState
{
	protected NLPNode         key_node;
	protected String          feat_key;
	protected String          oracle;
	protected boolean         terminate;
	protected List<NLPNode[]> non_stopwords;
	protected float[]         prediction_scores;
	protected float[][]       ensemble_scores;
	
	public DOCState(List<NLPNode[]> document, String key)
	{
		super(document);
		feat_key = key;
		key_node = document.get(0)[1];
		non_stopwords = NLPUtils.getNonStopWords(document);
		reinit();
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
	
	public List<NLPNode[]> getDocument(boolean excludeStopwords)
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
	
	public NLPNode getKeyNode()
	{
		return key_node;
	}
	
	@Override
	public NLPNode getNode(FeatureItem item)
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
