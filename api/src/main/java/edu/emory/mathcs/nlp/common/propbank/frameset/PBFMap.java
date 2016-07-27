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
package edu.emory.mathcs.nlp.common.propbank.frameset;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBFMap implements Serializable
{
	private static final long serialVersionUID = 1457732046110097472L;
	
	private Map<String,PBFFrameset> m_verbs;
	private Map<String,PBFFrameset> m_nouns;
	
	public PBFMap()
	{
		init();
	}
	
	/** @param framesDir the directory containing PropBank frame files. */
	public PBFMap(String framesDir)
	{
		init();
		addFramesets(framesDir);
	}
	
	private void init()
	{
		m_verbs = new HashMap<>();
		m_nouns = new HashMap<>();
	}
	
	private PBFType getType(String value)
	{
		if (PBFType.VERB.isValue(value))	return PBFType.VERB;
		if (PBFType.NOUN.isValue(value))	return PBFType.NOUN;
		
		return null;
	}
	
	/** @param framesDir the directory containing PropBank frame files. */
	public void addFramesets(String framesDir)
	{
		List<String> filelist = FileUtils.getFileList(framesDir, ".xml", false);
		
		try
		{
			for (String filename : filelist)
				addFrameset(filename);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	/** @throws Exception */
	public void addFrameset(String filename) throws Exception
	{
		InputStream in = new BufferedInputStream(new FileInputStream(filename));
		filename = FileUtils.getBaseName(filename);
		int idx = filename.length() - 6;
		String lemma;
		PBFType type;
		
		if (idx > 0)
		{
			lemma = filename.substring(0, idx);
			type  = getType(filename.substring(idx+1, idx+2));
			if (type != null) addFrameset(in, lemma, type);
		}
	}
	
	/**
	 * @param in the input-stream of a PropBank frame file.
	 * @param lemma the base lemma (e.g., "run", but not "run_out"). 
	 */
	public void addFrameset(InputStream in, String lemma, PBFType type)
	{
		try
		{
			Element eFrameset = XMLUtils.getDocumentElement(in);
			addFrameset(eFrameset, lemma, type);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	/** @param lemma the base lemma (e.g., "run", but not "run_out"). */
	public void addFrameset(Element eFrameset, String lemma, PBFType type)
	{
		addFrameset(new PBFFrameset(eFrameset, lemma), type);
	}
	
	public void addFrameset(PBFFrameset frameset, PBFType type)
	{
		     if (type == PBFType.VERB)
			m_verbs.put(frameset.getLemma(), frameset);
		else if (type == PBFType.NOUN)
			m_nouns.put(frameset.getLemma(), frameset);
	}
	
	/** @param lemma the base lemma (e.g., "run", but not "run_out"). */
	public PBFFrameset getFrameset(PBFType type, String lemma)
	{
		if (type == PBFType.VERB)	return m_verbs.get(lemma);
		if (type == PBFType.NOUN)	return m_nouns.get(lemma);
		
		return null;
	}
	
	public boolean hasFramset(String lemma)
	{
		return m_verbs.containsKey(lemma) || m_nouns.containsKey(lemma);
	}
	
	public Map<String,PBFFrameset> getFramesetMap(PBFType type)
	{
		if (type == PBFType.VERB)	return m_verbs;
		if (type == PBFType.NOUN)	return m_nouns;
		
		return null;
	}

	/** @param lemma the base lemma (e.g., "run", but not "run_out"). */
	public PBFRoleset getRoleset(PBFType type, String lemma, String rolesetID)
	{
		PBFFrameset frameset = getFrameset(type, lemma);
		
		if (frameset != null)
			return frameset.getRoleset(rolesetID);
		
		return null;
	}
}