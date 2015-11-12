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
package edu.emory.mathcs.nlp.common.propbank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.CTLibEn;
import edu.emory.mathcs.nlp.common.treebank.CTTree;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.StringUtils;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBInstance implements Serializable, Comparable<PBInstance>
{
	private static final long serialVersionUID = 5564480917905255460L;
	
	static public final String  DELIM = StringConst.SPACE;
	static public final Pattern SPLIT = PatternConst.SPACE;
	
	private String				s_treePath;
	private int					i_treeID;
	private int					i_predicateID;
	private String				s_annotator;
	private String				s_type;
	private String				s_rolesetID;
	private String				s_aspects;
	private List<PBArgument>	l_arguments;
	private CTTree				tree = null;
	
	public PBInstance()
	{
		l_arguments = new ArrayList<>();
	}
	
	/** @param str {@code <treePath><treeId><predId><annotator><type><roleset><aspects>(<argument>)+}. */
	public PBInstance(String str)
	{
		String[] tmp = SPLIT.split(str);
		
		if (tmp.length < 7 || !StringUtils.containsDigitOnly(tmp[1]) || !StringUtils.containsDigitOnly(tmp[2]))
			throw new IllegalArgumentException(str);
		
		s_treePath 		= tmp[0];
		i_treeID		= Integer.parseInt(tmp[1]);
		i_predicateID	= Integer.parseInt(tmp[2]);
		s_annotator		= tmp[3];
		s_type			= tmp[4];
		s_rolesetID		= tmp[5];
		s_aspects		= tmp[6];
		
		l_arguments = new ArrayList<>();
		int i, size = tmp.length;
		
		for (i=7; i<size; i++)
			addArgument(new PBArgument(tmp[i]));
	}
	
	public String getTreePath()
	{
		return s_treePath;
	}
	
	public int getTreeID()
	{
		return i_treeID;
	}
	
	public int getPredicateID()
	{
		return i_predicateID;
	}
	
	public String getAnnotator()
	{
		return s_annotator;
	}
	
	public String getType()
	{
		return s_type;
	}
	
	public String getRolesetID()
	{
		return s_rolesetID;
	}
	
	public String getAspects()
	{
		return s_aspects;
	}
	
	/** @return the list of all arguments of this instance. */
	public List<PBArgument> getArgumentList()
	{
		return l_arguments;
	}
	
	/** @return the index'th argument of this instance if exists; otherwise, {@code null}. */
	public PBArgument getArgument(int index)
	{
		return (0 <= index && index < l_arguments.size()) ? l_arguments.get(index) : null;
	}
	
	/** @return the first argument with the specific PropBank label. */
	public PBArgument getFirstArgument(String label)
	{
		for (PBArgument arg : l_arguments)
			if (arg.isLabel(label))
				return arg;
		
		return null;
	}
	
	public void setTreePath(String path)
	{
		s_treePath = path;
	}
	
	public void setTreeID(int id)
	{
		i_treeID = id;
	}
	
	public void setPredicateID(int id)
	{
		i_predicateID = id;
	}
	
	public void setAnnotator(String annotator)
	{
		s_annotator = annotator;
	}
	
	public void setType(String type)
	{
		s_type = type;
	}
	
	public void setRolesetID(String rolesetID)
	{
		s_rolesetID = rolesetID;
	}
	
	public void getAspects(String aspects)
	{
		s_aspects = aspects;
	}
	
	public void addArguments(Collection<PBArgument> args)
	{
		l_arguments.addAll(args);
	}
	
	public void addArgument(PBArgument argument)
	{
		l_arguments.add(argument);
	}
	
	public void removeArguments(Collection<PBArgument> args)
	{
		l_arguments.removeAll(args);
	}
	
	/** Removes all argument with the specific label. */
	public void removeArguments(String label)
	{
		List<PBArgument> remove = new ArrayList<>();
		
		for (PBArgument arg : l_arguments)
		{
			if (arg.isLabel(label))
				remove.add(arg);
		}
		
		l_arguments.removeAll(remove);
	}

	public void sortArguments()
	{
		for (PBArgument arg : l_arguments)
			arg.sortLocations();
				
		Collections.sort(l_arguments);
	}

	public int getArgumentSize()
	{
		return l_arguments.size();
	}
	
	public CTTree getTree()
	{
		return tree;
	}
	
	/** @return {@link PBInstance#s_treePath}+"_"+{@link PBInstance#i_treeID}+"_"+{@link PBInstance#i_predicateID}. */
	public String getKey()
	{
		return getKey(i_predicateID);
	}
	
	/** @return {@link PBInstance#s_treePath}+"_"+{@link PBInstance#i_treeID}+"_"+{@code predicateID}. */
	public String getKey(int predicateID)
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_treePath);
		build.append(StringConst.UNDERSCORE);
		build.append(i_treeID);
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
		return s_treePath.equals(path);
	}
	
	/** @return {@code true} if the roleset ID of this instance is {@code ER|NN|IE|YY}. */
	public boolean isTemporaryInstance()
	{
		return StringUtils.endsWithAny(s_rolesetID, "ER","NN","IE","YY");
	}
	
	/** @return {@code true} if the predicate of this instance is a verb. */
	public boolean isVerbPredicate()
	{
		return s_type.endsWith("-v");
	}
	
	/** @return {@code true} if the predicate of this instance is a noun. */
	public boolean isNounPredicate()
	{
		return s_type.endsWith("-n");
	}
	
	/** @return {@code true} if the predicate of this instance is a compound noun of a light verb. */
	public boolean isNounPredicateLightVerb(CTTree tree, Language lang)
	{
		if (isNounPredicate())
		{
			PBArgument rel = getFirstArgument(PBTag.PB_REL);
			if (rel == null) return false;
			
			for (PBLocation loc : rel.getLocationList())
			{
				if (lang == Language.ENGLISH && CTLibEn.isVerb(tree.getNode(loc)))
					return true;
			}			
		}
		
		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_treePath);		build.append(DELIM);
		build.append(i_treeID);			build.append(DELIM);
		build.append(i_predicateID);	build.append(DELIM);
		build.append(s_annotator);		build.append(DELIM);
		build.append(s_type);			build.append(DELIM);
		build.append(s_rolesetID);		build.append(DELIM);
		build.append(s_aspects);
		
		for (PBArgument arg : l_arguments)
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
		
		if ((cmp = s_treePath.compareTo(instance.s_treePath)) != 0) return cmp;
		if ((cmp = i_treeID - instance.i_treeID) != 0) return cmp;
		if ((cmp = i_predicateID - instance.i_predicateID) != 0) return cmp;
		
		return s_rolesetID.compareTo(instance.s_rolesetID);
	}
}