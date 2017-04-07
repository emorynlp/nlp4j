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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	public Tmp() throws Exception
	{
//		String[] filenames = {"ontonotes.tb","web.tb","question.tb","mipacq.tb","sharp.tb","thyme.tb","craft.tb"};
		String[] filenames = {"ontonotes.tb","web.tb","bolt.tb","question.tb"};
		CTReader reader = new CTReader();
		List<CTNode> tokens;
		CTTree tree;
		int sc, wc;
		
		int tsc = 0, twc = 0;
		
		for (String filename : filenames)
		{
			reader.open(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/english/"+filename));
			sc = wc = 0;
			
			while ((tree = reader.next()) != null)
			{
				 tokens = tree.getTokens();

				 if (!tokens.isEmpty())
				 {
					 sc++;
					 wc += tokens.size();					 
				 }
			}
			
			reader.close();
			System.out.printf("%15s%10d%10d\n", filename, sc, wc);
			tsc += sc;
			twc += wc;
		}
		
		System.out.printf("%15s%10d%10d\n", "total", tsc, twc);
	}
	
	static public void main(String[] args) throws Exception
	{
		String[] input_files = {"file1.txt", "file2.txt"};
		String output_file1 = "";
		
		Map<String, Object2FloatMap<String>> map1 = new HashMap<>();	// P(w_i | w_i-1, w_i-2)
		Pattern p = Pattern.compile(" ");
		String line;
		
		for (String input_file : input_files)
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input_file)));
			
			while ((line = reader.readLine()) != null)
			{
				String[] tmp = p.split(line);
				
				for (int i=0; i<tmp.length; i++)
				{
					if (i-2 >= 0)
					{
						String key1 = tmp[i];
						String key2 = tmp[i-2]+tmp[i-1];
						
						Object2FloatMap<String> m = map1.computeIfAbsent(key1, k -> new Object2FloatOpenHashMap<String>());
						m.merge(key2, 1f, (o, n) -> o + n);
					}
				}
			}
			
			reader.close();
		}
		
		// TODO: normalize to probablity
		
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(output_file1)));
		out.writeObject(map1);
		out.writeObject(map1);
		out.close();
		
		
		
		
//		String logFile = "/Users/jdchoi/Documents/EmoryNLP/nlp4j/README.md"; // Should be some file on your system
//	    SparkConf conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]").set("spark.executor.memory","1g");
//	    JavaSparkContext sc = new JavaSparkContext(conf);
//	    JavaRDD<String> logData = sc.textFile(logFile).cache();
//	    
//	    long numAs = logData.filter(new Function<String, Boolean>() {
//	      public Boolean call(String s) { return s.contains("t"); }
//	    }).count();
//
//	    long numBs = logData.filter(new Function<String, Boolean>() {
//	      public Boolean call(String s) { return s.contains("b"); }
//	    }).count();
//
//	    System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
//	    
//	    List<LabeledPoint> list = new ArrayList<LabeledPoint>();
//	    LabeledPoint zero = new LabeledPoint(0.0, Vectors.dense(1.0, 0.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0));
//	    LabeledPoint one = new LabeledPoint(1.0, Vectors.dense(8.0,7.0,6.0,4.0,5.0,6.0,1.0,2.0,3.0));
//	    list.add(zero);
//	    list.add(one);
//	    JavaRDD<LabeledPoint> data = sc.parallelize(list);
//	    
//	    
//	    sc.stop();
	}
}

