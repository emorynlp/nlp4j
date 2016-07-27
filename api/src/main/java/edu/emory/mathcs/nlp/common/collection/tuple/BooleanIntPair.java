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
package edu.emory.mathcs.nlp.common.collection.tuple;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BooleanIntPair implements Serializable
{
	private static final long serialVersionUID = -3606845926289267380L;
	public boolean b;
	public int i;
	
	public BooleanIntPair(boolean b, int i)
	{
		set(b, i);
	}
	
	public void set(boolean b, int i)
	{
		this.b = b;
		this.i = i;
	}
}