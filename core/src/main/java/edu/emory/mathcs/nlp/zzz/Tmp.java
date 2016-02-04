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
package edu.emory.mathcs.nlp.zzz;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.common.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	@SuppressWarnings("unchecked")
	public Tmp(String[] args) throws Exception
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(args[0]);
		Map<String,float[]> emb = (Map<String,float[]>)oin.readObject();
		oin.close();
		
		System.out.println(emb.size());
		Iterator<Entry<String, float[]>> it = emb.entrySet().iterator();
		Entry<String, float[]> e;
		
		while (it.hasNext())
		{
			e = it.next();
			
			if (skip(e.getKey()))
				it.remove();
		}
		
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(args[1]);
		out.writeObject(emb);
		out.close();
		
		System.out.println(emb.size());
	}
	
	boolean skip(String form)
	{
		char[] cs = form.toCharArray();
		if (cs.length < 3 || cs.length > 20) return true;
		
		for (int i=0; i<cs.length; i++)
		{
			if (cs[i] == '_' || cs[i] >= 128)
				return true;
		}
		
		return false;
	}
		
	static public void main(String[] args) throws Exception
	{
		new Tmp(args);
	}
}

