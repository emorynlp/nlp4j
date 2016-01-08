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
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	@SuppressWarnings("unchecked")
	public Tmp(String[] args) throws Exception
	{
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(args[0]);
		Map<String,List<String>> ambi = (Map<String,List<String>>)in.readObject();
		in.close();
		
		TSVReader reader = new TSVReader();
		reader.form = 1;
		
		int cover = 0, total = 0;
		NLPNode[] nodes;
		
		for (String filename : FileUtils.getFileList(args[1], "pos"))
		{
			reader.open(IOUtils.createFileInputStream(filename));
			
			while ((nodes = reader.next()) != null)
			{
				for (int i=1; i<nodes.length; i++)
					if (ambi.containsKey(nodes[i].getSimplifiedWordForm()))
						cover++;
				
				total += nodes.length - 1;
			}
			
			reader.close();
		}
		
		System.out.printf("%5.2f (%d/%d)", 100d*cover/total, cover, total);
		
		
//		LongHashFunction h = LongHashFunction.xx_r39(12345);
//		int len = 50, size = 4000000, iter = 1000, c, labels = 100;
//		List<Integer> list;
//		long st, et;
//		float d = 0;
//		float[] a = new float[size*labels];
//		boolean[] b = new boolean[size];
//		Random rand = new Random();
//		for (int i=0; i<a.length; i++)
//			a[i] = rand.nextFloat();
//		
//		for (int i=0; i<b.length; i++)
//			if (rand.nextFloat() <= 0.025) b[i] = true;
//		
//		st = System.currentTimeMillis();
//		for (int k=0; k<iter; k++)
//		{
//			list = new ArrayList<>();
//
////			for (int i=0; i<len; i++)
////				for (int j=0; j<len; j++)
////					list.add(MathUtils.modulus(h.hashLong(rand.nextInt()*3000000+rand.nextInt()), size));
////
////			d = 0;
////			for (int j=0; j<labels; j++)
////				for (int i=0; i<list.size(); i++)
////					d += a[list.get(i)+j*size];
//			
//			for (int i=0; i<len; i++)
//				for (int j=0; j<len; j++)
//				{
//					c = MathUtils.modulus(h.hashLong(rand.nextInt()*size+rand.nextInt()), size);
//					if (b[c]) list.add(c);
//				}
//			
////			Collections.sort(list);
//			d = 0;
//			for (int j=0; j<labels; j++)
//				for (int i=0; i<list.size(); i++)
//					d += a[list.get(i)+j*size];
//		}
//		
//		et = System.currentTimeMillis();
////		System.out.println(et-st);
//		System.out.println(MathUtils.divide(et-st, iter));
	}
		
	static public void main(String[] args) throws Exception
	{
		new Tmp(args);
	}
}

