/**
 * Copyright 2014, Emory University
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
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.bin.util.BinUtils;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectIntIntTriple;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.FastUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.C2DConverter;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;
import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.propbank.PBInstance;
import edu.emory.mathcs.nlp.structure.propbank.PBLocation;
import edu.emory.mathcs.nlp.structure.propbank.PBReader;
import edu.emory.mathcs.nlp.structure.util.DDGTag;
import edu.emory.mathcs.nlp.structure.util.PBTag;
import edu.emory.mathcs.nlp.structure.util.PTBLib;
import edu.emory.mathcs.nlp.structure.util.PTBTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;


public class C2DConvert
{
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String input_path;
	@Option(name="-l", usage="language (default: english)", required=false, metaVar="<language>")
	private String language = Language.ENGLISH.toString();
	@Option(name="-parse_ext", usage="parse file extension (default: parse)", required=false, metaVar="<string>")
	private String parse_ext = "parse";
	@Option(name="-prop_ext", usage="propbank file extension (default: prop)", required=false, metaVar="<string>")
	private String prop_ext = "prop";
	@Option(name="-name_ext", usage="named entity file extension (default: name)", required=false, metaVar="<string>")
	private String name_ext = "name";
	@Option(name="-output_ext", usage="output file extension (default: dep)", required=false, metaVar="<string>")
	private String output_ext = "ddg";
	@Option(name="-normalize", usage="if set, normalize empty category indices", required=false, metaVar="<boolean>")
	private boolean normalize = false;
	@Option(name="-recursive", usage="if set, traverse parse files recursively", required=false, metaVar="<boolean>")
	private boolean recursive = false;

	public C2DConvert() {}
	
	public C2DConvert(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		List<String> parseFiles = FileUtils.getFileList(input_path, parse_ext, recursive);
		Language language = Language.getType(this.language);
		C2DConverter converter = NLPUtils.getC2DConverter(language);
		
		for (String parseFile : parseFiles)
		{
			int n = convert(converter, language, parseFile, parse_ext, prop_ext, name_ext, output_ext, normalize);
			System.out.printf("%s: %d trees\n", parseFile, n);
		}
	}
	
	protected int convert(C2DConverter converter, Language language, String parseFile, String parseExt, String propExt, String nameExt, String outputExt, boolean normalize) throws Exception
	{
		Int2ObjectMap<List<ObjectIntIntTriple<String>>> mName = getNamedEntityMap(parseFile, parseExt, nameExt);
		Int2ObjectMap<List<PBInstance>> mProp = getPBInstanceMap(parseFile, parseExt, propExt);
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parseFile), language);
		PrintStream fout = IOUtils.createBufferedPrintStream(parseFile+"."+outputExt);
		List<ObjectIntIntTriple<String>> names = null;
		List<PBInstance> instances = null;
		CTTree   cTree;
		NLPGraph dTree;
		int n;
		
		for (n=0; (cTree = reader.next()) != null; n++)
		{
			if (normalize)
				cTree.normalizeIndices();
			
			if (mProp != null && (instances = mProp.get(n)) != null)
				initPropBank(cTree, instances, language);
			
			if (mName != null && (names = mName.get(n)) != null)
				initNamedEntities(cTree, names);

			dTree = converter.toDependencyGraph(cTree);
			
			if (dTree != null)
				fout.println(dTree.toString()+"\n");
			else
				System.err.println("No token in the tree "+(n+1)+"\n"+cTree.toStringLine());
		}
		
		reader.close();
		fout.close();
		return n;
	}
	
	private Int2ObjectMap<List<PBInstance>> getPBInstanceMap(String parseFile, String parseExt, String propExt)
	{
		String filename = getFilename(parseFile, parseExt, propExt); 
		return filename != null ? new PBReader(IOUtils.createFileInputStream(filename)).getInstanceMap() : null;
	}
	
	private String getFilename(String parseFile, String parseExt, String otherExt)
	{
		if (parseExt == null || otherExt == null) return null;
		String filename = FileUtils.replaceExtension(parseFile, parseExt, otherExt);
		if (filename == null || !new File(filename).isFile()) return null;
		return filename;
	}
	
	private void initPropBank(CTTree tree, List<PBInstance> instances, Language language)
	{
		if (language == Language.ENGLISH)
		{
			Pair<List<CTNode>,CTNode> p0;
			Pair<CTNode,CTNode> p1;
			
			for (PBInstance instance : instances)
			{
				if (instance.isExperimental()) continue;
//				if (initPropBankLightVerb(tree, instance)) continue;
				
				for (PBArgument argument : instance.getArguments())
				{
					if (argument.isLabel(PBTag.LINK_PRO))
					{
						p0 = initPropBankLinkPRO(tree, argument);
						if (p0 != null) for (CTNode n : p0.o1) n.setAntecedent(p0.o2);
					}
					else if (argument.isLabel(PBTag.LINK_SLC))
					{
						p1 = initPropBankLinkSLC(tree, argument);
						if (p1 != null) p1.o1.setAntecedent(p1.o2);
					}
					else if (argument.isLabel(PBTag.LINK_PSV))
					{
						p1 = initPropBankLinkPSV(tree, argument);
						if (p1 != null) p1.o1.setAntecedent(p1.o2);
					}
				}
			}			
		}
	}
	
	boolean initPropBankLightVerb(CTTree tree, PBInstance instance)
	{
		if (instance.getFrameID().endsWith("LV"))
		{
			CTNode node = tree.getTerminal(instance.getPredicateID());
			node.addFunctionTag(DDGTag.LV);
			return true;
		}
		
		return false;
	}
	
	private Pair<List<CTNode>,CTNode> initPropBankLinkPRO(CTTree tree, PBArgument argument)
	{
		List<CTNode> pros = new ArrayList<>();
		CTNode ante = null;
		argument.sortLocations();
		
		for (PBLocation loc : argument.getLocations())
		{
			CTNode node = tree.getNode(loc);
			
			if (node.isEmptyCategoryPhrase())
			{
				CTNode tmp = node.getFirstTerminal();
				if (tmp.isForm(PTBTag.E_PRO)) pros.add(tmp);
			}
			else if (ante == null)
				ante = node;
		}
		
		return (!pros.isEmpty() && ante != null) ? new Pair<List<CTNode>,CTNode>(pros, ante) : null;
	}
	
	private Pair<CTNode,CTNode> initPropBankLinkSLC(CTTree tree, PBArgument argument)
	{
		CTNode relv = null, ante = null;
		argument.sortLocations();
		
		for (PBLocation loc : argument.getLocations())
		{
			CTNode node = tree.getNode(loc);
			
			if (node.getSyntacticTag().startsWith("WH"))
				relv = PTBLib.getRelativizer(node);
			else if (ante == null && !node.isEmptyCategoryPhrase())
				ante = node;
		}
		
		return (relv != null && ante != null) ? new Pair<CTNode,CTNode>(relv, ante) : null;
	}
	
	private Pair<CTNode,CTNode> initPropBankLinkPSV(CTTree tree, PBArgument argument)
	{
		CTNode psv = null, ante = null;
		argument.sortLocations();
		
		for (PBLocation loc : argument.getLocations())
		{
			CTNode node = tree.getNode(loc);
			
			if (node.isEmptyCategoryPhrase())
			{
				CTNode tmp = node.getFirstTerminal();
				if (tmp.isForm(PTBTag.E_NULL) && !tmp.hasCoIndex()) psv = tmp;
			}
			else if (ante == null)
				ante = node;
		}
		
		return (psv != null && ante != null) ? new Pair<CTNode,CTNode>(psv, ante) : null;
	}
	
	void count(CTTree tree, List<PBInstance> instances, Object2IntMap<String> count)
	{
		tree.flatten().forEach(node -> count(node, instances, count)); 
		Pair<List<CTNode>,CTNode> p0;
		Pair<CTNode,CTNode> p1;
		
		for (PBInstance instance : instances)
		{
			if (instance.isExperimental()) continue;
			
			for (PBArgument argument : instance.getArguments())
			{
				if (argument.isLabel(PBTag.LINK_PRO))
				{
					p0 = initPropBankLinkPRO(tree, argument);
					if (p0 != null) FastUtils.increment(count, "PRO-PB", p0.o1.size());
				}
				else if (argument.isLabel(PBTag.LINK_SLC))
				{
					p1 = initPropBankLinkSLC(tree, argument);
					
					if (p1 != null)
					{
						FastUtils.increment(count, "SLC-PB");
						
						if (p1.o1.hasAntecedent())
						{
							FastUtils.increment(count, "SLC-INTER");
							if (p1.o1.getAntecedent() == p1.o2) FastUtils.increment(count, "SLC-AGREE");
						}
					}
				}
				else if (argument.isLabel(PBTag.LINK_PSV))
				{
					p1 = initPropBankLinkPSV(tree, argument);
					
					if (p1 != null)
					{
						FastUtils.increment(count, "PSV-PB");
						
						if (p1.o1.hasAntecedent())
						{
							FastUtils.increment(count, "PSV-INTER");
							if (p1.o1.getAntecedent() == p1.o2) FastUtils.increment(count, "PSV-AGREE");
						}
					}
				}
			}
		}
	}
	
	private void count(CTNode node, List<PBInstance> instances, Object2IntMap<String> count)
	{
		CTNode tmp;
		
		if (node.isEmptyCategory())
		{
			if (node.isForm(PTBTag.E_PRO))
			{
				FastUtils.increment(count, "PRO-ALL");
				if (node.hasAntecedent()) FastUtils.increment(count, "PRO-TB");
			}
			else if (node.isForm(PTBTag.E_NULL) && !node.hasCoIndex())
			{
				FastUtils.increment(count, "PSV-ALL");
				if (node.hasAntecedent()) FastUtils.increment(count, "PSV-TB");
			}
		}
		else if ((tmp = PTBLib.getRelativizer(node)) != null)
		{
			FastUtils.increment(count, "SLC-ALL");
			if (tmp.hasAntecedent()) FastUtils.increment(count, "SLC-TB");
		}
	}
	
	private Int2ObjectMap<List<ObjectIntIntTriple<String>>> getNamedEntityMap(String parseFile, String parseExt, String nameExt) throws Exception
	{
		String filename = getFilename(parseFile, parseExt, nameExt);
		if (filename == null) return null;
		
		Int2ObjectMap<List<ObjectIntIntTriple<String>>> map = new Int2ObjectOpenHashMap<>();
		BufferedReader fin = IOUtils.createBufferedReader(filename);
		String[] tmp;
		String line;
		int treeID;
		
		while ((line = fin.readLine()) != null)
		{
			tmp    = Splitter.splitSpace(line);
			treeID = Integer.parseInt(tmp[1]);
			map.put(treeID, getNamedEntityList(tmp));
		}
		
		fin.close();
		return map;
	}
	
	private List<ObjectIntIntTriple<String>> getNamedEntityList(String[] names)
	{
		int i, bIdx, eIdx, size = names.length;
		List<ObjectIntIntTriple<String>> list = new ArrayList<>(size-2);
		String[] t0, t1;
		String ent;

		for (i=2; i<size; i++)
		{
			t0   = Splitter.splitHyphens(names[i]);
			t1   = Splitter.splitColons(t0[0]);
			ent  = t0[1];
			bIdx = Integer.parseInt(t1[0]);
			eIdx = Integer.parseInt(t1[1]);
			list.add(new ObjectIntIntTriple<>(ent, bIdx, eIdx));
		}
		
		return list;
	}
	
	private void initNamedEntities(CTTree cTree, List<ObjectIntIntTriple<String>> names)
	{
		if (names == null)	return;
		int i;
		
		for (CTNode node : cTree.getTerminals())
			node.setNamedEntityTag(BILOU.O.toString());
		
		for (ObjectIntIntTriple<String> t : names)
		{
			if (t.i1 == t.i2)
				cTree.getTerminal(t.i1).setNamedEntityTag(BILOU.U+"-"+t.o);
			else
			{
				cTree.getTerminal(t.i1).setNamedEntityTag(BILOU.B+"-"+t.o);
				cTree.getTerminal(t.i2).setNamedEntityTag(BILOU.L+"-"+t.o);
				
				for (i=t.i1+1; i<t.i2; i++)
					cTree.getTerminal(i).setNamedEntityTag(BILOU.I+"-"+t.o);
			}
		}
	}
	
	protected void propbank(MorphAnalyzer analyzer, String parseFile, String parseExt, String propExt, Object2IntMap<String> count) throws Exception
	{
		Int2ObjectMap<List<PBInstance>> mProp = getPBInstanceMap(parseFile, parseExt, propExt);
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parseFile));
		List<PBInstance> instances = null;
		CTTree tree;
		
		for (int n=0; (tree = reader.next()) != null; n++)
		{
			tree.getTokens().forEach(node -> analyzer.setLemma(node));
			if (mProp != null && (instances = mProp.get(n)) != null)
				comparePB(tree, instances, count);
		}
		
		reader.close();
	}
	
	void comparePB(CTTree tree, List<PBInstance> mProp, Object2IntMap<String> count)
	{
		for (PBInstance instance : mProp)
		{
			if (instance.isNominalPredicate())
			{
				PBArgument argument = instance.getFirstArgument(PBTag.REL);
				if (argument == null) continue;
				PBLocation loc_lv = argument.getLocations().stream().filter(l -> PTBLib.isVerb(tree.getNode(l))).findFirst().orElse(null);
				argument = instance.getFirstArgument(PBTag.ARG1);
				if (argument != null)
				{
					PBLocation loc_arg1 = argument.getLocations().stream().filter(l -> tree.getNode(l).isSyntacticTag("PP")).findFirst().orElse(null);
					
					if (loc_lv != null && loc_arg1 != null)
					{
						CTNode lv = tree.getNode(loc_lv);
						CTNode noun = tree.getTerminal(instance.getPredicateID());
						CTNode pp = tree.getNode(loc_arg1);
						
						CTNode in = pp.getFirstChild(n -> n.isSyntacticTag("IN"));
						if (in != null) FastUtils.increment(count, lv.getLemma()+"-"+noun.getLemma()+"-"+in.getLemma());
					}	
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			new C2DConvert(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}