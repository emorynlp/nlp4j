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
package edu.emory.mathcs.nlp.learning.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.learning.vector.SparseItem;
import edu.emory.mathcs.nlp.learning.vector.SparseVector;
import edu.emory.mathcs.nlp.learning.vector.StringItem;
import edu.emory.mathcs.nlp.learning.vector.StringVector;
import edu.emory.mathcs.nlp.learning.vector.WeightVector;
import edu.emory.mathcs.nlp.learning.vector.WeightVectorDynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringModelMap extends StringModel
{
	private static final long serialVersionUID = 8254121942296507359L;
	private FeatureMap feature_map;
	
//	=================================== CONSTRUCTORS ===================================

	/** Bias = 0. */
	public StringModelMap(WeightVector vector)
	{
		this(vector, 0);
	}
	
	/** @param bias the default bias value. */
	public StringModelMap(WeightVector vector, float bias)
	{
		super(vector, bias);
		feature_map = new FeatureMap();
	}
	
//	=================================== BYTE ===================================

	@Override
	public byte[] toByteArray()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bos));
			out.writeObject(weight_vector);
			out.writeObject(feature_map);
			out.writeObject(label_map);
			out.writeFloat (bias);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
		return bos.toByteArray();
	}

	@Override
	public void fromByteArray(byte[] array)
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(array);
		try
		{
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
			weight_vector = (WeightVector)in.readObject();
			feature_map   = (FeatureMap)in.readObject();
			label_map     = (LabelMap)in.readObject();
			bias          = in.readFloat();
			in.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
//	=================================== LABEL/FEATURE ===================================
	
	@Override
	public int getFeatureSize()
	{
		return feature_map.size();
	}
	
	@Override
	public void addFeatures(StringVector features)
	{
		for (StringItem f : features)
			feature_map.add(f.getType(), f.getValue());
	}
	
//	=================================== CONVERSION ===================================
	
	@Override
	public SparseVector toSparseVector(StringVector features)
	{
		SparseVector x = new SparseVector();
		int index;
		
		if (bias > 0)
			x.add(new SparseItem(0, bias));
		
		for (StringItem f : features)
		{
			index = feature_map.index(f.getType(), f.getValue());
			if (index > 0) x.add(index, f.getWeight());
		}
		
		x.sort();
		return x;
	}
	
//	=================================== SHRINK ===================================
	
	public int shrink(float threshold)
	{
		int[] indexMap = new int[getFeatureSize()];
		int i, j, k, l, count = 1;	// bias
		float max, min;
		
		for (i=1; i<getFeatureSize(); i++)
		{
			k = i * getLabelSize();
			max = weight_vector.get(k);
			min = weight_vector.get(k);
			
			for (j=1; j<getLabelSize(); j++)
			{
				max = Math.max(max, weight_vector.get(k+j));
				min = Math.min(min, weight_vector.get(k+j));
			}
			
			if (Math.abs(max - min) >= threshold)
				indexMap[i] = count++;
		}
		
		WeightVector newWeights = new WeightVectorDynamic(weight_vector.getLabelSize(), count, weight_vector.getActivationFunction());
		ObjectIterator<Entry<String,Integer>> it;
		int oldIndex, newIndex;
		Entry<String,Integer> e;
		
		// bias weights
		for (j=0; j<getLabelSize(); j++)
			newWeights.set(j, weight_vector.get(j));
		
		for (Object2IntMap<String> map : feature_map.getIndexMaps())
		{
			it = map.entrySet().iterator();
			
			while (it.hasNext())
			{
				e = it.next();
				oldIndex = e.getValue();
				newIndex = indexMap[oldIndex];
				
				if (newIndex > 0)
				{
					e.setValue(newIndex);
					k = oldIndex * getLabelSize();
					l = newIndex * getLabelSize();
					
					for (j=0; j<getLabelSize(); j++)
						newWeights.set(l+j, weight_vector.get(k+j));
				}
				else
					it.remove();
			}
		}
		
		weight_vector = newWeights;
		feature_map.setSize(count);
		return count;
	}
}
