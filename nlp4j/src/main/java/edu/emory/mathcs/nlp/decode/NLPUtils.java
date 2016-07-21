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
package edu.emory.mathcs.nlp.decode;

import java.io.InputStream;
import java.io.ObjectInputStream;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	static public String FEAT_POS_2ND   = "pos2";
	static public String FEAT_PREDICATE = "pred";

	static public String toStringLine(NLPNode[] nodes, String delim, Field field)
	{
		return Joiner.join(nodes, delim, 1, nodes.length, n -> n.getValue(field));
	}
	
	static public Tokenizer createTokenizer(Language language)
	{
		return new EnglishTokenizer();
	}
	
	@SuppressWarnings("unchecked")
	static public <N extends AbstractNLPNode<N>,S extends NLPState<N>>NLPComponent<N> getComponent(InputStream in)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		OnlineComponent<N,S> component = null;
		
		try
		{
			component = (OnlineComponent<N,S>)oin.readObject();
			component.setFlag(NLPFlag.DECODE);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}

		return component;
	}
}
