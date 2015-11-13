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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NEGazetteerCreate
{
	Tokenizer tokenizer;
	
	public NEGazetteerCreate()
	{
		tokenizer = new EnglishTokenizer();
	}
	
	public Set<String> union(InputStream wiki, InputStream redirect, boolean skipColon, boolean single) throws Exception
	{
		Set<String> set = new HashSet<>();
		read(wiki, set, false, skipColon, single);
		read(redirect, set, true, skipColon, single);
		return set;
	}
	
	public void read(InputStream in, Set<String> set, boolean redirect, boolean skipColon, boolean single) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		List<String> tokens;
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			if (skipColon && (line.contains(":") || line.contains(" of "))) continue;
			if (redirect) line = splitRedirect(line);
			tokens = tokenizer.tokenize(line);
			concatPeriod(tokens);
			trimTokens(tokens, single);
			if (!tokens.isEmpty()) set.add(Joiner.join(tokens, " "));
		}
		
		System.out.println(set.size());
		reader.close();
	}
	
	private void concatPeriod(List<String> tokens)
	{
		if (tokens.size() == 2 && tokens.get(1).equals(".") && tokens.get(0).contains("."))
		{
			tokens.set(0, tokens.get(0)+".");
			tokens.remove(1);
		}
	}
	
	public String splitRedirect(String s)
	{
		if (s.contains(StringConst.SPACE)) return s;
		StringBuilder build = new StringBuilder();
		char[] cs = s.toCharArray();
		int i, len = cs.length;
		
		for (i=0; i<len; i++)
		{
			if (0 < i&&i < len-1 && CharUtils.isLowerCase(cs[i-1]) && CharUtils.isUpperCase(cs[i]))
				build.append(StringConst.SPACE);
			
			build.append(cs[i]);
		}
		
		return build.toString();
	}
	
	public void trimTokens(List<String> tokens, boolean single)
	{
		Iterator<String> it = tokens.iterator();
		int i, bIdx = -1;
		String s;
		
		for (i=0; i<tokens.size(); i++)
		{
			if (tokens.get(i).equals(StringConst.LRB))
				bIdx = i;
			else if (tokens.get(i).equals(StringConst.RRB) && bIdx >= 0)
			{
				tokens.subList(bIdx, i+1).clear();
				break;
			}
		}
		
		while (it.hasNext())
		{
			s = it.next();
			
			if (StringUtils.containsPunctuationOnly(s))
				it.remove();
			else
				break;
		}
		
		for (i=tokens.size()-1; i>=0; i--)
		{
			if (StringUtils.containsPunctuationOnly(tokens.get(i)))
				tokens.remove(i);
			else
				break;
		}
		
		if (tokens.size() == 1 && ((single && !tokens.get(0).contains(".")) || StringUtils.containsDigitOnly(tokens.get(0))))
			tokens.clear();
		
//		if (tokens.size() == 1) System.out.println(tokens.get(0));
	}
	
	public void print(OutputStream out, Set<String> set)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		List<String> list = new ArrayList<>(set);
		Collections.sort(list);
		
		for (String s : list)
			fout.println(s);
		
		fout.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		final String DIR = args[0];
		
		NEGazetteerCreate dict = new NEGazetteerCreate();
		Set<String> set;
		String path;
		
		path = DIR+"/WikiArtWork";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), false, true);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiFilms";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), false, true);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiSongs";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), false, true);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiManMadeObjectNames";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), false, false);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiCompetitionsBattlesEvents";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), false, false);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiLocations";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), true, false);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiOrganizations";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), true, false);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
		path = DIR+"/WikiPeople";
		System.out.println(path);
		set= dict.union(IOUtils.createFileInputStream(path+".txt"), IOUtils.createFileInputStream(path+"Redirects.txt"), true, false);
		dict.print(IOUtils.createFileOutputStream(path+".union"), set);
		
	}
}
