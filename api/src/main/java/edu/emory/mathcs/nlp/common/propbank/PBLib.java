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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.CTReader;
import edu.emory.mathcs.nlp.common.treebank.CTTree;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBLib
{
	static public final String PREFIX_CONCATENATION	= "C-";
	static public final String PREFIX_REFERENT		= "R-";
	static public final String PREFIX_LINK			= "LINK-";
	static public final String DELIM_FUNCTION_TAG	= StringConst.HYPHEN;
	static public final Pattern P_R_ARG	= Pattern.compile("^"+PREFIX_REFERENT+"(.+)");
	static public final Pattern P_ARGN	= Pattern.compile("^(A|C-A|R-A)(RG)?(\\d|A)");
	static public final Pattern P_ARGM	= Pattern.compile("^A(RG)?M-(.+)");
	
	static private final Pattern P_LINK			= Pattern.compile("^"+PREFIX_LINK+"(.+)");
	static private final Pattern P_ARGN_CORE	= Pattern.compile("^A(RG)?(\\d|A)");
	
	private PBLib() {}
	
	static public List<CTTree> getTreeList(InputStream treebank, InputStream propbank)
	{
		return getTreeList(treebank, propbank, false);
	}
	
	static public List<CTTree> getTreeList(InputStream treebank, InputStream propbank, boolean normalize)
	{
		List<CTTree> trees = new CTReader(treebank).getTreeList();
		PBReader reader = new PBReader(propbank);
		PBInstance instance;
		CTTree tree;
		
		for (CTTree t : trees)
		{
			if (normalize) t.normalizeIndices();
			t.initPBLocations();
		}
		
		while ((instance = reader.nextInstance()) != null)
		{
			if (!isIllegalRolesetID(instance.getRolesetID()))
			{
				tree = trees.get(instance.getTreeID());
				tree.initPBInstance(instance);
			}
		}
		
		return trees;
	}
	
	/** @param out internally casted to {@code new PrintStream(new BufferedOutputStream(out))}. */
	static public void printInstances(List<PBInstance> instances, OutputStream out)
	{
		PrintStream stream = IOUtils.createBufferedPrintStream(out);
		
		for (PBInstance instance : instances)
			stream.println(instance.toString());
				
		stream.close();
	}

	static public boolean isNumberedArgument(String label)
	{
		return P_ARGN.matcher(label).find();
	}

	static public boolean isCoreNumberedArgument(String label)
	{
		return P_ARGN_CORE.matcher(label).find();
	}
	
	static public boolean isLinkArgument(String label)
	{
		return label.startsWith(PREFIX_LINK);
	}
	
	static public boolean isConcatenatedArgument(String label)
	{
		return label.startsWith(PREFIX_CONCATENATION);
	}
	
	static public boolean isReferentArgument(String label)
	{
		return label.startsWith(PREFIX_REFERENT);
	}
	
	static public boolean isModifier(String label)
	{
		return P_ARGM.matcher(label).find();
	}
	
	static public boolean isIllegalRolesetID(String rolesetID)
	{
		return StringUtils.endsWithAny(rolesetID, "ER","NN","IE","YY");
	}
	
	static public boolean isUndefinedLabel(String label)
	{
		return label.endsWith("UNDEF");
	}
	
	static public boolean isLightVerbRoleset(String rolesetID)
	{
		return rolesetID.endsWith("LV");
	}
	
	static public String getShortLabel(String label)
	{
		return PBTag.PB_REL.equals(label) ? PBTag.PB_C_V : "A"+label.substring(3);
	}
	
	/**
	 * @return the number of an numbered argument (e.g., "0", "A").
	 * If the label is not a numbered argument, returns {@code null}. 
	 */
	static public String getNumber(String label)
	{
		return PatternUtils.getGroup(P_ARGN, label, 3);
	}
	
	static public String getLinkType(String label)
	{
		return PatternUtils.getGroup(P_LINK, label, 1);
	}
	
	/**@return the type of the modifier if exists (e.g., "TMP", "LOC"); otherwise, {@code null}. */
	static public String getModifierType(String label)
	{
		return PatternUtils.getGroup(P_ARGM, label, 2);
	}
	
	/** @return the label discarding prefixes such as C- or R-. */
	static public String getBaseLabel(String label)
	{
		if (label.startsWith(PREFIX_CONCATENATION))
			return label.substring(PREFIX_CONCATENATION.length());
		else if (label.startsWith(PREFIX_REFERENT))
			return label.substring(PREFIX_REFERENT.length());
		else
			return label;
	}
	
//	static public void toReferentArgument(SRLArc arc)
//	{
//		String label = arc.getLabel();
//		
//		if (label.startsWith("A"))
//			arc.setLabel(PREFIX_REFERENT + label);
//		else if (label.startsWith(PREFIX_CONCATENATION))
//			arc.setLabel(PREFIX_REFERENT + label.substring(PREFIX_CONCATENATION.length()));
//	}
}