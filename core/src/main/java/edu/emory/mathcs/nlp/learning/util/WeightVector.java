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
package edu.emory.mathcs.nlp.learning.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WeightVector implements Serializable
{
	private static final long serialVersionUID = -3283251983046316463L;
	private ActivationFunction activation_function;
	private MajorVector sparse_weight_vector;	// column major
	private MajorVector dense_weight_vector;	// row major
	
//	=================================== CONSTRUCTORS ===================================

	public WeightVector()
	{
		this(null);
	}
	
	public WeightVector(ActivationFunction function)
	{
		setSparseWeightVector(new ColumnMajorVector());
		setDenseWeightVector (new RowMajorVector());
		setActivationFunction(function);
	}
	
//	=================================== GETTERS & SETTERS ===================================

	public MajorVector getMajorVector(boolean sparse)
	{
		return sparse ? sparse_weight_vector : dense_weight_vector;
	}
	
	public MajorVector getSparseWeightVector()
	{
		return sparse_weight_vector;
	}
	
	public void setSparseWeightVector(MajorVector vector)
	{
		sparse_weight_vector = vector;
	}
	
	public MajorVector getDenseWeightVector()
	{
		return dense_weight_vector;
	}
	
	public void setDenseWeightVector(MajorVector vector)
	{
		dense_weight_vector = vector;
	}
	
	public ActivationFunction getActivationFunction()
	{
		return activation_function;
	}
	
	public void setActivationFunction(ActivationFunction function)
	{
		activation_function = function;
	}
	
	public boolean hasActivationFunction()
	{
		return activation_function != null;
	}
	
	public int getLabelSize()
	{
		return sparse_weight_vector.getLabelSize();
	}
	
//	=================================== UTILITIES ===================================
	
	public boolean expand(int sparseFeatureSize, int denseFeatureSize, int labelSize)
	{
		return expand(sparseFeatureSize, denseFeatureSize, labelSize, null);
	}
	
	/**
	 * Expands the size of this weight vector using the new feature and label sizes.
	 * @return true if the size of this weight vector is expanded.
	 */
	public boolean expand(int sparseFeatureSize, int denseFeatureSize, int labelSize, WeightGenerator generator)
	{
		boolean b = false;
		b |= sparse_weight_vector.expand(labelSize, sparseFeatureSize, generator);
		b |= dense_weight_vector .expand(labelSize, denseFeatureSize , generator);
		return b;
	}
	
	/** @return a copy of this vector where all the values are initialized to 0. */
	public WeightVector createZeroVector()
	{
		WeightVector vector = new WeightVector(activation_function);
		vector.setSparseWeightVector(sparse_weight_vector.createZeroVector());
		vector.setDenseWeightVector (dense_weight_vector .createZeroVector());
		return vector;
	}
	
	public int countNonZeroWeights()
	{
		return sparse_weight_vector.countNonZeroWeights() + dense_weight_vector.countNonZeroWeights();
	}
	
	public String getTopFeatureCombination(FeatureVector x, int gold, int yhat)
	{
		SparseItem fst = new SparseItem(-1, 0);
		SparseItem snd = new SparseItem(-1, 0);
		SparseVector v = x.getSparseVector();
		float f;
		
		for (SparseItem p : v)
		{
			f = sparse_weight_vector.get(gold, p.getIndex()) - sparse_weight_vector.get(yhat, p.getIndex());
			
			if (f > fst.getValue())
			{
				snd.set(fst);
				fst.set(p.getIndex(), f);
			}
			else if (f > snd.getValue())
				snd.set(p.getIndex(), f);
		}
		
		return (fst.getIndex() < 0 || snd.getIndex() < 0) ? null : FeatureTemplate.getFeatureCombination(fst.getIndex(), snd.getIndex());
	}
	
//	public List<String> getTopFeatureCombinations(FeatureVector x, int gold, int yhat)
//	{
//		SparseItem max = new SparseItem(-1, 0);
//		SparseVector v = x.getSparseVector();
//		IntSet set = new IntOpenHashSet();
//		float f;
//		
//		for (SparseItem p : v)
//		{
//			f = sparse_weight_vector.get(gold, p.getIndex()) - sparse_weight_vector.get(yhat, p.getIndex());
//			
//			if (f > 0)
//			{
//				if (f > max.getValue()) max.set(p.getIndex(), f);
//				set.add(p.getIndex());
//			}
//		}
//		
//		List<String> list = new ArrayList<>();
//		
//		if (max.getIndex() > 0 && set.size() > 1)
//		{
//			set.remove(max.getIndex());
//			for (int j : set) list.add(FeatureTemplate.getCombo(max.getIndex(), j));
//		}
//		
//		return list;
//	}
	
	public List<String> getTopFeatureCombinations(FeatureVector x, int gold, int yhat)
	{
		SparseItem fst = new SparseItem(-1, 0);
		SparseItem snd = new SparseItem(-1, 0);
		SparseItem trd = new SparseItem(-1, 0);
		SparseVector v = x.getSparseVector();
		float f;
		
		for (SparseItem p : v)
		{
			f = sparse_weight_vector.get(gold, p.getIndex()) - sparse_weight_vector.get(yhat, p.getIndex());
			
			if (f > fst.getValue())
			{
				trd.set(snd);
				snd.set(fst);
				fst.set(p.getIndex(), f);
			}
			else if (f > snd.getValue())
			{
				trd.set(snd);
				snd.set(p.getIndex(), f);
			}
			else if (f > trd.getValue())
				trd.set(p.getIndex(), f);
		}
		
		List<String> list = new ArrayList<>();
		boolean b1 = fst.getIndex() >= 0;
		boolean b2 = snd.getIndex() >= 0;
		boolean b3 = trd.getIndex() >= 0;
		
		if (b1)
		{
			if (b2) list.add(FeatureTemplate.getFeatureCombination(fst.getIndex(), snd.getIndex()));
			if (b3) list.add(FeatureTemplate.getFeatureCombination(fst.getIndex(), trd.getIndex()));
//			if (b2 && b3) list.add(FeatureTemplate.getFeatureCombination(snd.getIndex(), trd.getIndex()));
		}
		
		return list;
	}
	
//	=================================== SCORES ===================================
	
	/** @return the scores of all labels given the specific feature vector. */
	public float[] scores(FeatureVector x)
	{
		float[] scores = new float[getLabelSize()];
		if (x.hasSparseVector())     sparse_weight_vector.addScores(x.getSparseVector(), scores);
		if (x.hasDenseVector())      dense_weight_vector .addScores(x.getDenseVector() , scores);
		if (hasActivationFunction()) activation_function.apply(scores);
		return scores;
	}
}
