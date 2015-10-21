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
package edu.emory.mathcs.nlp.emorynlp.utils.feature;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.machine_learning.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class FeatureTemplate<N,S> implements Serializable
{
	private static final long serialVersionUID = -6755594173767815098L;
	protected List<FeatureItem<?>[]> feature_list;
	protected List<FeatureItem<?>>   feature_set;
	protected S state;

	public FeatureTemplate()
	{
		feature_list = new ArrayList<>();
		feature_set  = new ArrayList<>();
	}
	
//	============================== SERIALIZATION ==============================
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		feature_list = (List<FeatureItem<?>[]>)in.readObject();
		feature_set  = (List<FeatureItem<?>>)  in.readObject();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(feature_list);
		out.writeObject(feature_set);
	}

//	============================== INITIALIZATION ==============================

	public void add(FeatureItem<?>... items)
	{
		feature_list.add(items);
	}
	
	public void addSet(FeatureItem<?> items)
	{
		feature_set.add(items);
	}
	
//	============================== GETTERS & SETTERS ==============================
	
	public S getState()
	{
		return state;
	}

	public void setState(S state)
	{
		this.state = state;
	}
	
	
	public int size()
	{
		return feature_list.size() + feature_set.size();
	}
	
//	============================== EXTRACTOR ==============================
	
	public StringVector extractFeatures()
	{
		StringVector x = new StringVector();
		int i, type = 0;
		String[] t;
		String f;
		
		for (i=0; i<feature_list.size(); i++,type++)
		{
			f = getFeature(feature_list.get(i));
			if (f != null) x.add(type, f);
		}
		
		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getFeatures(feature_set.get(i));
			if (t != null) for (String s : t) x.add(type, s);
		}
		
		return x;
	}
	
	private String getFeature(FeatureItem<?>... items)
	{
		String f;
		
		if (items.length == 1)
			return getFeature(items[0]);
		else
		{
			StringJoiner join = new StringJoiner("_");
			
			for (FeatureItem<?> item : items)
			{
				f = getFeature(item);
				if (f == null) return null;
				join.add(f);
			}
			
			return join.toString();
		}
	}
	
	protected abstract String   getFeature (FeatureItem<?> item);
	protected abstract String[] getFeatures(FeatureItem<?> item);
}
