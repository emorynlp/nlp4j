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
package edu.emory.mathcs.nlp.emorynlp.component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.emorynlp.component.train.TrainInfo;
import edu.emory.mathcs.nlp.emorynlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.machine_learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;
import edu.emory.mathcs.nlp.machine_learning.util.MLUtils;
import edu.emory.mathcs.nlp.machine_learning.vector.SparseVector;
import edu.emory.mathcs.nlp.machine_learning.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPOnlineComponent<N extends NLPNode,S extends NLPState<N>> implements NLPComponent<N>
{
	private static final long serialVersionUID = 59819173578703335L;
	protected FeatureTemplate<N,S> feature_template;
	protected OnlineOptimizer[]    optimizers;
	protected TrainInfo[]          train_info;
	protected StringModel[]        models;
	protected NLPConfig<N>         config;
	protected NLPFlag              flag;
	protected Eval                 eval;
	
//	============================== CONSTRUCTORS ==============================
	
	public NLPOnlineComponent() {}
	
	public NLPOnlineComponent(InputStream configuration)
	{
		setConfiguration(configuration);
	}
	
//	============================== SERIALIZATION ==============================
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		feature_template = (FeatureTemplate<N,S>)in.readObject();
		models = (StringModel[])in.readObject();
		readLexicons(in);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(feature_template);
		out.writeObject(models);
		writeLexicons(out);
	}
	
	protected abstract void readLexicons (ObjectInputStream in)   throws IOException, ClassNotFoundException;
	protected abstract void writeLexicons(ObjectOutputStream out) throws IOException;
	
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
	
	public StringModel getModel()
	{
		return models[0];
	}
	
	public void setModel(StringModel model)
	{
		this.models = new StringModel[]{model};
	}
	
	public StringModel[] getModels()
	{
		return models;
	}
	
	public void setModels(StringModel[] model)
	{
		this.models = model;
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
	
	public FeatureTemplate<N,S> getFeatureTemplate()
	{
		return feature_template;
	}

	public void setFeatureTemplate(FeatureTemplate<N,S> template)
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
	
	public NLPConfig<N> getConfiguration()
	{
		return config;
	}
	
	public void setConfiguration(NLPConfig<N> config)
	{
		this.config = config;
	}
	
	public abstract Eval createEvaluator();
	public abstract void setConfiguration(InputStream in);
	
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
	protected abstract S initState(N[] nodes);

	@Override
	public void process(N[] nodes)
	{
		process(nodes, initState(nodes));
	}
	
	/** Process the sequence of the nodes given the state. */
	public void process(N[] nodes, S state)
	{
		feature_template.setState(state);
		if (!isDecode()) state.saveOracle();
		
		OnlineOptimizer optimizer;
		SparseInstance inst;
		StringModel model;
		TrainInfo info;
		SparseVector x;
		float[] scores;
		int ydot, yhat;
		int modelID;
		int[] Z;
		
		while (!state.isTerminate())
		{
			modelID = getModelID(state);
			model = models[modelID];
			x = extractFeatures(state, model);
			
			if (isTrain())
			{
				optimizer = optimizers[modelID];
				info = train_info[modelID];
				Z = getZeroCostLabels(state, model);
				optimizer.expand(model.getLabelSize(), model.getFeatureSize());
				scores = model.scores(x);
				inst = new SparseInstance(Z, x, scores);
				ydot = inst.getGoldLabel();
				yhat = optimizer.setPredictedLabel(inst);
				optimizer.train(inst);
				if (info.chooseGold()) yhat = ydot;
			}
			else
			{
				scores = model.scores(x);
				yhat = MLUtils.argmax(scores);
			}
			
			state.next(new StringPrediction(model.getLabel(yhat), scores[yhat]));
		}
	
		if (isEvaluate()) state.evaluate(eval);
	}
	
//	============================== HELPERS ==============================
	
	/** @return the vector consisting of all features extracted from the state. */
	protected SparseVector extractFeatures(S state, StringModel model)
	{
		StringVector vector = feature_template.extractFeatures();
		if (isTrain()) model.addFeatures(vector);
		return model.toSparseVector(vector);
	}
	
	protected int[] getZeroCostLabels(S state, StringModel model)
	{
		Set<String> set = state.getZeroCost();
		model.addLabels(set);
		return model.getLabelIndexArray(set);
	}
	
	/** @return the index of the current statistical model to be used. */
	protected int getModelID(S state)
	{
		return 0;
	}
}
