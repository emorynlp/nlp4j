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
package edu.emory.mathcs.nlp.learning.zzz;

import java.io.Serializable;
import java.util.Collection;

import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.StringVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class StringModel implements Serializable
{
	private static final long serialVersionUID = -8287696148995690222L;
	protected WeightVector weight_vector;
	protected LabelMap     label_map;
	protected float        bias;
	
//	=================================== CONSTRUCTORS ===================================

	/** Bias = 0. */
	public StringModel(WeightVector vector)
	{
		this(vector, 0);
	}
	
	/** @param bias the default bias value. */
	public StringModel(WeightVector vector, float bias)
	{
		label_map = new LabelMap();
		setWeightVector(vector);
		setBias(bias);
	}
	
//	=================================== SERIALIZATION ===================================

	public abstract byte[] toByteArray();
	public abstract void fromByteArray(byte[] array);
	
//	=================================== WEIGHT ===================================
	
	public WeightVector getWeightVector()
	{
		return weight_vector;
	}
	
	public void setWeightVector(WeightVector vector)
	{
		weight_vector = vector;
	}
	
//	=================================== BIAS ===================================
	
	/** @return the default bias value. */
	public float getBias()
	{
		return bias;
	}
	
	/** Sets the bias value. */
	public void setBias(float bias)
	{
		this.bias = bias;
	}
	
//	=================================== LABEL/FEATURE ===================================

	public String getLabel(int index)
	{
		return label_map.getLabel(index);
	}
	
	public int getLabelIndex(String label)
	{
		return label_map.index(label);
	}
	
	public int[] getLabelIndexArray(Collection<String> labels)
	{
		return labels.stream().mapToInt(s -> getLabelIndex(s)).toArray();
	}
	
	public int getLabelSize()
	{
		return label_map.size();
	}
	
	public void addLabel(String label)
	{
		label_map.add(label);
	}
	
	public void addLabels(Collection<String> labels)
	{
		for (String label : labels) addLabel(label);
	}
	
	public void add(StringInstance instance)
	{
		add(instance.getLabel(), instance.getVector());
	}
	
	public void add(String label, StringVector features)
	{
		addLabel(label);
		addFeatures(features);
	}
	
	public abstract int getFeatureSize();
	public abstract void addFeatures(StringVector features);
	
//	=================================== CONVERSION ===================================

	public abstract SparseVector toSparseVector(StringVector features);

	
//	/**
//	 * @return the best prediction given the input vector.
//	 * @param x the input vector.
//	 */
//	public StringPrediction predictBest(StringVector x)
//	{
//		float[] scores = scores(x);
//		int      yhat   = DSUtils.maxIndex(scores);
//		return new StringPrediction(getLabel(yhat), scores[yhat]);
//	}
//	
//	/**
//	 * @return the best prediction given the input vector.
//	 * @param x the input vector.
//	 * @param labels consider only these labels.
//	 */
//	public StringPrediction predictBest(StringVector x, int[] labels)
//	{
//		float[] scores = scores(x);
//		int      yhat   = DSUtils.maxIndex(scores, labels);
//		return new StringPrediction(getLabel(yhat), scores[yhat]);
//	}
//	
//	public StringPrediction[] predictTop2(StringVector x)
//	{
//		float[] scores = scores(x);
//		StringPrediction fst, snd;
//		
//		if (scores[0] < scores[1])
//		{
//			fst = new StringPrediction(getLabel(1), scores[1]);
//			snd = new StringPrediction(getLabel(0), scores[0]);
//		}
//		else
//		{
//			fst = new StringPrediction(getLabel(0), scores[0]);			
//			snd = new StringPrediction(getLabel(1), scores[1]);
//		}
//		
//		for (int i=2; i<label_map.size(); i++)
//		{
//			if (fst.getScore() < scores[i])
//			{
//				snd.copy(fst);
//				fst.set(getLabel(i), scores[i]);
//			}
//			else if (snd.getScore() < scores[i])
//				snd.set(getLabel(i), scores[i]);
//		}
//		
//		return new StringPrediction[]{fst, snd};
//	}
//	
//	public StringPrediction[] predictTop2(StringVector x, int[] labels)
//	{
//		int i, l0 = labels[0], l1 = labels[1];
//		float[] scores = scores(x);
//		StringPrediction fst, snd;
//		
//		if (scores[l0] < scores[l1])
//		{
//			fst = new StringPrediction(getLabel(l1), scores[l1]);
//			snd = new StringPrediction(getLabel(l0), scores[l0]);
//		}
//		else
//		{
//			fst = new StringPrediction(getLabel(l0), scores[l0]);			
//			snd = new StringPrediction(getLabel(l1), scores[l1]);
//		}
//		
//		for (int j=2; j<labels.length; j++)
//		{
//			i = labels[j];
//			
//			if (fst.getScore() < scores[i])
//			{
//				snd.copy(fst);
//				fst.set(getLabel(i), scores[i]);
//			}
//			else if (snd.getScore() < scores[i])
//				snd.set(getLabel(i), scores[i]);
//		}
//		
//		return new StringPrediction[]{fst, snd};
//	}
//	
//	/**
//	 * @return all predictions given the input vector.
//	 * @param x the input vector.
//	 */
//	public StringPrediction[] predictAll(StringVector x)
//	{
//		StringPrediction[] ps = new StringPrediction[label_map.size()];
//		float[] scores = scores(x);
//		
//		for (int i=0; i<ps.length; i++)
//			ps[i] = new StringPrediction(getLabel(i), scores[i]);
//		
//		return ps;
//	}
}
