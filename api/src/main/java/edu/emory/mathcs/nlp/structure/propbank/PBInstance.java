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
package edu.emory.mathcs.nlp.structure.propbank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.util.PBTag;
import edu.emory.mathcs.nlp.structure.util.PTBLib;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBInstance implements Serializable, Comparable<PBInstance>
{
	private static final long serialVersionUID = 5564480917905255460L;
	
	static public final String  DELIM = StringConst.SPACE;
	static public final Pattern SPLIT = PatternConst.SPACE;
	
	private String				tree_path;
	private int					tree_id;
	private int					predicate_id;
	private String				annotator;
	private String				predicate_type;
	private String				frame_id;
	private String				aspects;
	private List<PBArgument>	arguments;
	private CTTree				tree = null;
	
	public PBInstance()
	{
		arguments = new ArrayList<>();
	}
	
	/** @param str {@code <treePath><treeId><predId><annotator><type><roleset><aspects>(<argument>)+}. */
	public PBInstance(String str)
	{
		String[] tmp = SPLIT.split(str);
		
		if (tmp.length < 7 || !StringUtils.containsDigitOnly(tmp[1]) || !StringUtils.containsDigitOnly(tmp[2]))
			throw new IllegalArgumentException(str);
		
		tree_path      = tmp[0];
		tree_id        = Integer.parseInt(tmp[1]);
		predicate_id   = Integer.parseInt(tmp[2]);
		annotator      = tmp[3];
		predicate_type = tmp[4];
		frame_id       = tmp[5];
		aspects        = tmp[6];
		
		arguments = new ArrayList<>();
		int i, size = tmp.length;
		
		for (i=7; i<size; i++)
		{
			addArgument(new PBArgument(tmp[i]));
		}
	}
	
	public String getTreePath()
	{
		return tree_path;
	}
	
	public int getTreeID()
	{
		return tree_id;
	}
	
	public int getPredicateID()
	{
		return predicate_id;
	}
	
	public String getAnnotator()
	{
		return annotator;
	}
	
	public String getPredicateType()
	{
		return predicate_type;
	}
	
	public String getFrameID()
	{
		return frame_id;
	}
	
	public String getAspects()
	{
		return aspects;
	}
	
	/** @return the list of all arguments of this instance. */
	public List<PBArgument> getArguments()
	{
		return arguments;
	}
	
	/** @return the index'th argument of this instance if exists; otherwise, {@code null}. */
	public PBArgument getArgument(int index)
	{
		return DSUtils.get(arguments, index);
	}
	
	/** @return the first argument with the specific PropBank label. */
	public PBArgument getFirstArgument(String label)
	{
		return arguments.stream().filter(arg -> arg.isLabel(label)).findFirst().orElse(null);
	}
	
	public void setTreePath(String path)
	{
		tree_path = path;
	}
	
	public void setTreeID(int id)
	{
		tree_id = id;
	}
	
	public void setPredicateID(int id)
	{
		predicate_id = id;
	}
	
	public void setAnnotator(String annotator)
	{
		this.annotator = annotator;
	}
	
	public void setPredicateType(String type)
	{
		predicate_type = type;
	}
	
	public void setFrameID(String id)
	{
		frame_id = id;
	}
	
	public void getAspects(String aspects)
	{
		this.aspects = aspects;
	}
	
	public void addArguments(Collection<PBArgument> args)
	{
		arguments.addAll(args);
	}
	
	public void addArgument(PBArgument argument)
	{
		arguments.add(argument);
	}
	
	public void removeArguments(Collection<PBArgument> args)
	{
		arguments.removeAll(args);
	}
	
	/** Removes all argument with the specific label. */
	public void removeArguments(String label)
	{
		Iterator<PBArgument> it = arguments.iterator();
		
		while (it.hasNext())
		{
			PBArgument arg = it.next();
			
			if (arg.isLabel(label))
				it.remove();
		}
	}

	public void sortArguments()
	{
		arguments.forEach(a -> a.sortLocations());
		Collections.sort(arguments);
	}

	public int getArgumentSize()
	{
		return arguments.size();
	}
	
	public CTTree getTree()
	{
		return tree;
	}
	
	/** @return {@link PBInstance#tree_path}+"_"+{@link PBInstance#tree_id}+"_"+{@link PBInstance#predicate_id}. */
	public String getKey()
	{
		return getKey(predicate_id);
	}
	
	/** @return {@link PBInstance#tree_path}+"_"+{@link PBInstance#tree_id}+"_"+{@code predicateID}. */
	public String getKey(int predicateID)
	{
		StringBuilder build = new StringBuilder();
		
		build.append(tree_path);
		build.append(StringConst.UNDERSCORE);
		build.append(tree_id);
		build.append(StringConst.UNDERSCORE);
		build.append(predicateID);
		
		return build.toString();
	}
	
	public void setTree(CTTree tree)
	{
		this.tree = tree;
	}
	
	public boolean isTreePath(String path)
	{
		return tree_path.equals(path);
	}
	
	/** @return {@code true} if the roleset ID of this instance is {@code ER|NN|IE|YY}. */
	public boolean isExperimental()
	{
		return StringUtils.endsWithAny(frame_id, "ER","NN","IE","YY");
	}
	
	/** @return {@code true} if the predicate of this instance is a verb. */
	public boolean isVerbalPredicate()
	{
		return predicate_type.endsWith("-v");
	}
	
	/** @return {@code true} if the predicate of this instance is a noun. */
	public boolean isNominalPredicate()
	{
		return predicate_type.endsWith("-n");
	}
	
	/** @return {@code true} if the predicate of this instance is an adjective. */
	public boolean isAdjectivalPredicate()
	{
		return predicate_type.endsWith("-j");
	}
	
	/** @return {@code true} if the predicate of this instance is a compound noun of a light verb. */
	public boolean isNominalPredicateLightVerb(CTTree tree, Language lang)
	{
		if (lang == Language.ENGLISH && isNominalPredicate())
		{
			PBArgument rel = getFirstArgument(PBTag.REL);
			if (rel != null) return rel.getLocations().stream().anyMatch(loc -> PTBLib.isVerb(tree.getNode(loc)));
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(tree_path);		build.append(DELIM);
		build.append(tree_id);			build.append(DELIM);
		build.append(predicate_id);		build.append(DELIM);
		build.append(annotator);		build.append(DELIM);
		build.append(predicate_type);	build.append(DELIM);
		build.append(frame_id);			build.append(DELIM);
		build.append(aspects);
		
		for (PBArgument arg : arguments)
		{
			build.append(DELIM);
			build.append(arg.toString());
		}
		
		return build.toString();
	}
	
	@Override
	public int compareTo(PBInstance instance)
	{
		int cmp;
		
		if ((cmp = tree_path.compareTo(instance.tree_path)) != 0) return cmp;
		if ((cmp = tree_id - instance.tree_id) != 0) return cmp;
		if ((cmp = predicate_id - instance.predicate_id) != 0) return cmp;
		return frame_id.compareTo(instance.frame_id);
	}
}