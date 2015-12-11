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
import edu.emory.mathcs.nlp.component.template.train.TrainInfo;
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
	protected OnlineOptimizer[]  optimizers;
	
	protected transient TrainInfo[] train_info;
	protected transient NLPConfig   config;
	protected transient NLPFlag     flag;
	protected transient Eval        eval;

//	============================== CONSTRUCTORS ==============================
	
	public OnlineComponent() {}
	
	public OnlineComponent(InputStream configuration)
	{
		setConfiguration(configuration);
	}

//	============================== GETTERS/SETTERS ==============================
	
	public OnlineOptimizer getOptimizer()
	{
		return optimizers[0];
	}
	
	public void setOptimizer(OnlineOptimizer optimizer)
	{
		this.optimizers = new OnlineOptimizer[]{optimizer};
	}
	
	public OnlineOptimizer[] getOptimizers()
	{
		return optimizers;
	}
	
	public void setOptimizers(OnlineOptimizer[] optimizers)
	{
		this.optimizers = optimizers;
	}
	
	public TrainInfo getTrainInfo()
	{
		return train_info[0];
	}

	public void setTrainInfo(TrainInfo info)
	{
		train_info = new TrainInfo[]{info};
	}
	
	public TrainInfo[] getTrainInfos()
	{
		return train_info;
	}

	public void setTrainInfos(TrainInfo[] info)
	{
		train_info = info;
	}
	
	public FeatureTemplate<S> getFeatureTemplate()
	{
		return feature_template;
	}

	public void setFeatureTemplate(FeatureTemplate<S> template)
	{
		feature_template = template;
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
	
	public abstract Eval createEvaluator();
	public abstract NLPConfig setConfiguration(InputStream in);
	
//	============================== FLAGS ==============================
	
	public boolean isCollect()
	{
		return flag == NLPFlag.COLLECT;
	}
	
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
	
	/** @return the processing state for the input nodes. */
	protected abstract S initState(NLPNode[] nodes);

	@Override
	public void process(NLPNode[] nodes)
	{
		process(nodes, initState(nodes));
	}
	
	/** Process the sequence of the nodes given the state. */
	public void process(NLPNode[] nodes, S state)
	{
		if (!isDecode()) state.saveOracle();
		OnlineOptimizer optimizer;
		int modelID, gold, yhat;
		Instance instance;
		FeatureVector x;
		List<String> p;
		String label;
		float[] scores;

		while (!state.isTerminate())
		{
			modelID = getModelID(state);
			optimizer = optimizers[modelID];
			x = feature_template.createFeatureVector(state, isTrain());
			
			if (isTrain())
			{
				label = state.getOracle();
				instance = new Instance(label, x);
				optimizer.train(instance);
				scores = instance.getScores();
				gold = instance.getGoldLabel();
				yhat = instance.getPredictedLabel();
				
				if (feature_template.useDynamicFeatureInduction() && gold != yhat)
				{
					p = optimizer.getWeightVector().getTopFeatureCombinations(x, gold, yhat);
					feature_template.addDynamicFeatures(p);
				}
				
				if (train_info[modelID].chooseGold()) yhat = gold;
			}
			else
			{
				scores = optimizer.scores(x);
				yhat = MLUtils.argmax(scores, optimizer.getLabelSize());
			}
			
			state.next(optimizer.getLabelMap(), yhat, scores);
		}
		
		if (isDecode() || isEvaluate())
		{
			postProcess(state);
			if (isEvaluate()) state.evaluate(eval);
		}
	}
	
//	============================== HELPERS ==============================
	
	/** @return the index of the current statistical model to be used. */
	protected int getModelID(S state) { return 0; }
	
	/** Post-processes if necessary. */
	protected void postProcess(S state) {}
}
