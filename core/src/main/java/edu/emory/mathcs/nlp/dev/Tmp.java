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
package edu.emory.mathcs.nlp.dev;

import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.emory.mathcs.nlp.common.collection.tree.PrefixNode;
import edu.emory.mathcs.nlp.common.collection.tree.PrefixTree;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tmp
{
	static public void toList(PrefixNode<String,Set<String>> node, List<String> list, String prefix)
	{
		if (node.hasValue())
			list.add(prefix.trim()+"\t"+Joiner.join(node.getValue()," "));
		
		for (Entry<String,PrefixNode<String, Set<String>>> e : node.entrySet())
			toList(e.getValue(), list, prefix+" "+e.getKey());
	}
	
	@SuppressWarnings("unchecked")
	static public void main(String[] args) throws Exception
	{
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(args[0]);
		PrefixTree<String,Set<String>> tree = (PrefixTree<String,Set<String>>)in.readObject();
		in.close();
		
		List<String> list = new ArrayList<>();
		toList(tree.getRoot(), list, "");
		Collections.sort(list);
		
		PrintStream fout = IOUtils.createBufferedPrintStream(args[1]);
		for (String s : list) fout.println(s);
		fout.close();
	}
}
