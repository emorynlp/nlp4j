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
package edu.emory.mathcs.nlp.bin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.nlp.common.collection.tree.PrefixNode;
import edu.emory.mathcs.nlp.common.collection.tree.PrefixTree;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERPrefixTreeExtract
{
	static public PrefixTree<String,Set<String>> getNERPrefixTree(final String DIR) throws IOException
	{
		final String[] PATH = {
				"known_corporations.txt",
				"known_countries.txt",
				"known_currencies.txt",
				"known_jobs.txt",
				"known_names.txt",
				"known_nationalities.txt",
				"known_places.txt",
				"known_states.txt",
				"WikiArtWork.txt",
				"WikiCompetitionsBattlesEvents.txt",
				"WikiFilms.txt",
				"WikiLocations.txt",
				"WikiManMadeObjectNames.txt",
				"WikiOrganizations.txt",
				"WikiPeople.txt",
				"WikiSongs.txt"
		};
		
		PrefixTree<String,Set<String>> tree = new PrefixTree<>();
		
		for (int i=0; i<PATH.length; i++)
			populateNERPrefixTree(IOUtils.createFileInputStream(DIR+"/"+PATH[i]), tree, Integer.toString(i));
		
		return tree;
	}
	
	static private void populateNERPrefixTree(InputStream in, PrefixTree<String,Set<String>> tree, String type) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		PrefixNode<String,Set<String>> node;
		Set<String> set;
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitSpace(line.trim());
			if (t.length == 1 && t[0].length() == 1) continue;
			node = tree.add(t, 0, t.length, NERPrefixTreeExtract::toKey);
			set  = node.getValue();
			
			if (set == null)
			{
				set = new HashSet<>();
				node.setValue(set);
			}
			
			set.add(type);
		}
	}
	
	static private String toKey(String s)
	{
//		return StringUtils.toSimplifiedForm(s);
//		return StringUtils.toUndigitalizedForm(s);
		return StringUtils.toUndigitalizedForm(s, true);
//		return StringUtils.toLowerCase(StringUtils.toSimplifiedForm(s));
	}
	
	static public void main(String[] args)
	{
		final String dir = args[0];
		final String outputFile = args[1];
		
		try
		{
			PrefixTree<String,Set<String>> tree = NERPrefixTreeExtract.getNERPrefixTree(dir);
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(outputFile);
			out.writeObject(tree);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}