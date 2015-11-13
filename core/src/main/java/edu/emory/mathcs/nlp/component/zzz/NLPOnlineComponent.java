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
package edu.emory.mathcs.nlp.component.zzz;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.emory.mathcs.nlp.component.zzz.config.NLPConfig;
import edu.emory.mathcs.nlp.component.zzz.eval.Eval;
import edu.emory.mathcs.nlp.component.zzz.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;
import edu.emory.mathcs.nlp.component.zzz.state.NLPState;
import edu.emory.mathcs.nlp.component.zzz.train.TrainInfo;
import edu.emory.mathcs.nlp.component.zzz.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.learning.model.StringModel;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.prediction.StringPrediction;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.vector.SparseVector;
import edu.emory.mathcs.nlp.learning.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPOnlineComponent<S extends NLPState> implements NLPComponent, Serializable
{
	private static final long serialVersionUID = 59819173578703335L;
	protected FeatureTemplate<S> feature_template;
	protected OnlineOptimizer[] optimizers;
	protected TrainInfo[]       train_info;
	protected StringModel[]     models;
	protected NLPConfig         config;
	protected NLPFlag           flag;
	protected Eval              eval;

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
		feature_template = (FeatureTemplate<S>)in.readObject();
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
		SparseInstance inst;
		StringModel model;
		int[] Z, labels;
		TrainInfo info;
		SparseVector x;
		float[] scores;
		int ydot, yhat;
		int modelID;

		while (!state.isTerminate())
		{
			modelID = getModelID(state);
			model = models[modelID];
			x = extractFeatures(state, model);
			labels = state.getLabelCandidates();
			
			if (isTrain())
			{
				optimizer = optimizers[modelID];
				info = train_info[modelID];
				Z = state.getZeroCostLabels(model);
				optimizer.expand(model.getLabelSize(), model.getFeatureSize());
				scores = model.scores(x, labels);
				inst = new SparseInstance(Z, x, scores);
				ydot = inst.getGoldLabel();
				yhat = optimizer.setPredictedLabel(inst);
				optimizer.train(inst);
				if (info.chooseGold()) yhat = ydot;
			}
			else
			{
				scores = model.scores(x, labels);
				yhat = MLUtils.argmax(scores, model.getLabelSize());
			}
			
			state.next(new StringPrediction(model.getLabel(yhat), scores[yhat]));
		}
	
		if (isDecode() || isEvaluate())
		{
			postProcess(state);
			if (isEvaluate()) state.evaluate(eval);
		}
	}
	
//	============================== HELPERS ==============================
	
	/** @return the vector consisting of all features extracted from the state. */
	protected SparseVector extractFeatures(S state, StringModel model)
	{
		StringVector vector = feature_template.extractFeatures(state);
		if (isTrain()) model.addFeatures(vector);
		return model.toSparseVector(vector);
	}
	
	/** @return the index of the current statistical model to be used. */
	protected int getModelID(S state) { return 0; }
	
	/** Post-processes if necessary. */
	protected void postProcess(S state) {}
}
