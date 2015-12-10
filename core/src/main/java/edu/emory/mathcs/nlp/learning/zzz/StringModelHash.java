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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.util.LabelMap;
import edu.emory.mathcs.nlp.learning.util.SparseItem;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.StringItem;
import edu.emory.mathcs.nlp.learning.util.StringVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;
import net.openhft.hashing.LongHashFunction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringModelHash extends StringModel
{
	private static final long serialVersionUID = 5389704565910715157L;
	private LongHashFunction hash_function;
	private int feature_size;
	
//	=================================== CONSTRUCTORS ===================================

	/** Bias = 0. */
	public StringModelHash(WeightVector vector, int featureSize)
	{
		this(vector, featureSize, 0);
	}
	
	/** @param bias the default bias value. */
	public StringModelHash(WeightVector vector, int featureSize, float bias)
	{
		super(vector, bias);
		feature_size = featureSize;
		initHashFunction();
	}
	
	private void initHashFunction()
	{
		hash_function = LongHashFunction.xx_r39(serialVersionUID);
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
			out.writeObject(label_map);
			out.writeFloat (bias);
			out.writeInt   (feature_size);
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
			label_map     = (LabelMap)in.readObject();
			bias          = in.readFloat();
			feature_size  = in.readInt();
			initHashFunction();
			in.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
//	=================================== LABEL/FEATURE ===================================
	
	@Override
	public int getFeatureSize()
	{
		return feature_size;
	}
	
	@Override
	public void addFeatures(StringVector features) {}
	
//	=================================== CONVERSION ===================================
	
	@Override
	public SparseVector toSparseVector(StringVector features)
	{
		SparseVector x = new SparseVector();
		int index, div = feature_size - 1;
		char[] s;
		
		if (bias > 0)
			x.add(new SparseItem(0, bias));
		
		for (StringItem f : features)
		{
			s = (f.getType()+f.getValue()).toCharArray();
			index = MathUtils.modulus(hash_function.hashChars(s), div) + 1;
			x.add(index, f.getWeight());
		}
		
		x.sort();
		return x;
	}
	
//	=================================== SHRINK ===================================
	
//	public int trim(float threshold)
//	{
//		int i, j, k, count = 0;	// bias
//		float max, min;
//		
//		for (i=1; i<getFeatureSize(); i++)
//		{
//			k = i * getLabelSize();
//			max = weight_vector.get(k);
//			min = weight_vector.get(k);
//			
//			for (j=1; j<getLabelSize(); j++)
//			{
//				max = Math.max(max, weight_vector.get(k+j));
//				min = Math.min(min, weight_vector.get(k+j));
//			}
//			
//			if ((max != 0 || min != 0) && Math.abs(max - min) < threshold)
//			{
//				for (j=0; j<getLabelSize(); j++) weight_vector.set(k+j, 0);
//				count++;
//			}
//		}
//		
//		return count;
//	}
}
