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
package edu.emory.mathcs.nlp.learning.instance;

import java.io.Serializable;

import edu.emory.mathcs.nlp.learning.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringInstance implements Serializable
{
	private static final long serialVersionUID = 1704398147440599696L;
	private StringVector vector;
	private String label;
	
	public StringInstance(String label, StringVector vector)
	{
		set(label, vector);
	}
	
	public String getLabel()
	{
		return label;
	}

	public StringVector getVector()
	{
		return vector;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setVector(StringVector vector)
	{
		this.vector = vector;
	}
	
	public void set(String label, StringVector vector)
	{
		setLabel(label);
		setVector(vector);
	}
	
	public boolean isLabel(String label)
	{
		return label.equals(this.label);
	}
	
	@Override
	public String toString()
	{
		return label+" "+vector.toString();
	}
}
