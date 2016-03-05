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
package edu.emory.mathcs.nlp.component.template;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.config.NLPConfig;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class OnlineComponent<S extends NLPState> implements NLPComponent, Serializable
{
	private static final long serialVersionUID = 59819173578703335L;
	protected FeatureTemplate<S> feature_template;
	protected OnlineOptimizer    optimizer;
	protected boolean            document_based;
	
	protected transient HyperParameter hyper_parameter;
	protected transient NLPConfig      config;
	protected transient NLPFlag        flag;
	protected transient Eval           eval;

//	============================== CONSTRUCTORS ==============================
	
	public OnlineComponent(boolean document)
	{
		setDocumentBased(document);
	}
	
	public OnlineComponent(boolean document, InputStream configuration)
	{
		this(document);
		setConfiguration(configuration);
	}

//	============================== GETTERS/SETTERS ==============================
	
	public OnlineOptimizer getOptimizer()
	{
		return optimizer;
	}
	
	public void setOptimizer(OnlineOptimizer optimizer)
	{
		this.optimizer = optimizer;
	}
	
	public HyperParameter getHyperParameter()
	{
		return hyper_parameter;
	}

	public void setHyperParameter(HyperParameter hyperparameter)
	{
		hyper_parameter = hyperparameter;
	}
	
	public FeatureTemplate<S> getFeatureTemplate()
	{
		return feature_template;
	}

	public void setFeatureTemplate(FeatureTemplate<S> template)
	{
		feature_template = template;
	}
	
	/** {@link #config} and {@link #hyper_parameter} must not be null. */
	public void initFeatureTemplate()
	{
		feature_template = new FeatureTemplate<>(config.getFeatureTemplateElement(), getHyperParameter());
	}
	
	public Eval getEval()
	{
		return eval;
	}
	
	public void setEval(Eval eval)
	{
		this.eval = eval;
	}
	
	public NLPFlag getFlag()
	{
		return flag;
	}
	
	public void setFlag(NLPFlag flag)
	{
		this.flag = flag;
		
		if (flag == NLPFlag.EVALUATE && eval == null)
			setEval(createEvaluator());
	}
	
	public NLPConfig getConfiguration()
	{
		return config;
	}
	
	public void setConfiguration(NLPConfig config)
	{
		this.config = config;
	}
	
	public NLPConfig setConfiguration(InputStream in)
	{
		NLPConfig config = new NLPConfig(in);
		setConfiguration(config);
		return config;
	}
	
	public boolean isDocumentBased()
	{
		return document_based;
	}
	
	public void setDocumentBased(boolean document)
	{
		document_based = document;
	}
	
//	============================== FLAGS ==============================
	
//	public boolean isCollect()
//	{
//		return flag == NLPFlag.COLLECT;
//	}
	
	public boolean isTrain()
	{
		return flag == NLPFlag.TRAIN;
	}
	
	public boolean isDecode()
	{
		return flag == NLPFlag.DECODE;
	}
	
	public boolean isEvaluate()
	{
		return flag == NLPFlag.EVALUATE;
	}
	
//	============================== PROCESS ==============================
	
	@Override
	public void process(NLPNode[] nodes)
	{
		process(initState(nodes));
	}
	
	@Override
	public void process(List<NLPNode[]> document)
	{
		if (document_based) process(initState(document));
		else for (NLPNode[] nodes : document) process(nodes);
	}
	
	/** Process the sequence of the nodes given the state. */
	public void process(S state)
	{
		if (!isDecode() && !state.saveOracle()) return;
		int[] top2 = {0,-1};
		Instance instance;
		FeatureVector x;
		float[] scores;
		String label;

		while (!state.isTerminate())
		{
			x = feature_template.createFeatureVector(state, isTrain());
			
			if (isTrain())
			{
				label = state.getOracle();
				instance = new Instance(label, x);
				optimizer.train(instance);
				scores = instance.getScores();
				putLabel(instance.getStringLabel(), instance.getGoldLabel());
				top2[0] = hyper_parameter.getLOLS().chooseGold() ? instance.getGoldLabel() : getPrediction(state, scores)[0];
			}
			else
			{
				scores = optimizer.scores(x);
				top2 = getPrediction(state, scores);
			}
			
			state.next(optimizer.getLabelMap(), top2, scores);
		}
		
		if (isDecode() || isEvaluate())
		{
			postProcess(state);
			if (isEvaluate()) state.evaluate(eval);
		}
	}
	
//	============================== HELPERS ==============================

	protected int[] getPrediction(S state, float[] scores)
	{
		return MLUtils.argmax2(scores);
	}
	
	public abstract Eval createEvaluator();
	
	/** @return the processing state for the input nodes. */
	protected abstract S initState(NLPNode[] nodes);
	
	/** @return the processing state for the input document. */
	protected abstract S initState(List<NLPNode[]> document);
	
	/** Post-processes if necessary. */
	protected abstract void postProcess(S state);
	
	protected void putLabel(String label, int index) {}
}
