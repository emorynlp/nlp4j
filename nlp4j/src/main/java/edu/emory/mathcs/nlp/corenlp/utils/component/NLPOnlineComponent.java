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
package edu.emory.mathcs.nlp.corenlp.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Set;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.corenlp.component.eval.Eval;
import edu.emory.mathcs.nlp.corenlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.corenlp.component.state.NLPState;
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
public abstract class NLPOnlineComponent<N,S extends NLPState<N>> implements NLPComponent<N>
{
	private static final long serialVersionUID = 59819173578703335L;
	protected FeatureTemplate<N,S> feature_template;
	protected OnlineOptimizer[]    optimizers;
	protected StringModel[]        models;
	protected NLPFlag              flag;
	protected Eval                 eval;

	// training
	protected double gold_dynamic_probability;
	protected Random rand;
	
//	============================== CONSTRUCTORS ==============================
	
	public NLPOnlineComponent() {}
	
	/** For training. */
	public NLPOnlineComponent(StringModel[] models, OnlineOptimizer[] optimizers, FeatureTemplate<N,S> template, Eval eval)
	{
		setEval(eval);
		setModels(models);
		setFlag(NLPFlag.TRAIN);
		setOptimizers(optimizers);
		setFeatureTemplate(template);
		gold_dynamic_probability = 1;
		rand = new XORShiftRandom(serialVersionUID);
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
	
	public OnlineOptimizer[] getOptimizers()
	{
		return optimizers;
	}
	
	public void setOptimizers(OnlineOptimizer[] optimizers)
	{
		this.optimizers = optimizers;
	}
	
	public StringModel[] getModels()
	{
		return models;
	}
	
	public void setModels(StringModel[] model)
	{
		this.models = model;
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
	}
	
	public double getGoldDynamicProbability()
	{
		return gold_dynamic_probability;
	}

	public void setGoldDynamicProbability(double referenceProbability)
	{
		gold_dynamic_probability = referenceProbability;
	}
	
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
		SparseVector x;
		float[] scores;
		int ydot, yhat;
		int[] Z;
		
		while (!state.isTerminate())
		{
			model = models[getModelIndex(state)];
			x = extractFeatures(state, model);
			
			if (isTrain())
			{
				Z = getZeroCostLabels(state, model);
				optimizer = optimizers[getModelIndex(state)];
				optimizer.expand(model.getLabelSize(), model.getFeatureSize());
				scores = model.scores(x);
				inst = new SparseInstance(Z, x, scores);
				ydot = inst.getGoldLabel();
				yhat = optimizer.setPredictedLabel(inst);
				optimizer.train(inst);
				if (chooseGold()) yhat = ydot;
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
	
	/** @return true if the gold label needs to be chosen for the next state. */
	protected boolean chooseGold()
	{
		return (gold_dynamic_probability > 0) && (gold_dynamic_probability >= 1 || gold_dynamic_probability > rand.nextDouble());		
	}
	
	/** @return the index of the current statistical model to be used. */
	protected int getModelIndex(S state)
	{
		return 0;
	}
}
