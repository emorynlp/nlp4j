/**
 * Copyright 2016, Emory University
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

import java.util.Map.Entry;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTReader;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KoreanTreebank
{
	@Test
	public void run()
	{
		String path = "/Users/jdchoi/Documents/Data/penn/korean/parse/newswire";
		CTReader reader = new CTReader();
		CTTree tree;
		
		Object2IntMap<String> phraseTags = new Object2IntOpenHashMap<>();
		Object2IntMap<String> posTags = new Object2IntOpenHashMap<>();
		Object2IntMap<String> functionTags = new Object2IntOpenHashMap<>();
		Object2IntMap<String> emptyCategories = new Object2IntOpenHashMap<>();
		int wc = 0;
		
		for (String filename : FileUtils.getFileList(path, "parse"))
		{
			System.out.println(filename);
			reader.open(IOUtils.createFileInputStream(filename));
			
			while ((tree = reader.nextTree()) != null)
			{
				count(tree.getRoot(), phraseTags, posTags, functionTags, emptyCategories);
				wc += tree.getTokenList().size();
			}
			
			reader.close();
		}
		
		System.out.println("WC: "+wc);

		for (Entry<String,Integer> entry : phraseTags.entrySet())
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		
		System.out.println();
		
		for (Entry<String,Integer> entry : posTags.entrySet())
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		
		System.out.println();
		
		for (Entry<String,Integer> entry : functionTags.entrySet())
			System.out.println(entry.getKey()+"\t"+entry.getValue());

		System.out.println();
		
		for (Entry<String,Integer> entry : emptyCategories.entrySet())
			System.out.println(entry.getKey()+"\t"+entry.getValue());
	}

	void count(CTNode node, Object2IntMap<String> phraseTags, Object2IntMap<String> posTags, Object2IntMap<String> functionTags, Object2IntMap<String> emptyCategories)
	{
		if (node.isTerminal())
		{
			for (String tag : Splitter.splitPlus(node.getConstituentTag()))
				FastUtils.increment(posTags, tag);
			
			if (node.isEmptyCategory())
				FastUtils.increment(emptyCategories, Splitter.splitHyphens(node.getWordForm())[0]);
		}
		else
		{
			FastUtils.increment(phraseTags, node.getConstituentTag());
			
			for (String tag : node.getFunctionTagSet())
				FastUtils.increment(functionTags, tag);
			
			for (CTNode child : node.getChildrenList())
				count(child, phraseTags, posTags, functionTags, emptyCategories);
		}
	}
}
