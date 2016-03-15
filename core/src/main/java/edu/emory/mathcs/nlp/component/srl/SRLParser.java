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
package edu.emory.mathcs.nlp.component.srl;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLParser extends OnlineComponent<SRLState>
{
	private static final long serialVersionUID = 6802441378426099565L;
	private int max_depth;
	private int max_height;
	
	public SRLParser() {super(false);}
	
	public SRLParser(InputStream configuration)
	{
		super(false, configuration);
		setMaxDepth (config.getIntegerTextContent("max_depth"));
		setMaxHeight(config.getIntegerTextContent("max_height"));
	}

//	============================== ABSTRACT ==============================

	@Override
	public Eval createEvaluator()
	{
		return null;
	}

	@Override
	protected SRLState initState(NLPNode[] nodes)
	{
		return new SRLState(nodes, max_depth, max_height);
	}
	
	@Override
	protected SRLState initState(List<NLPNode[]> document)
	{
		return null;
	}

	@Override
	protected void postProcess(SRLState state) {}
	

//	====================================== GETTERS/SETTERS ======================================

	public int getMaxDepth()
	{
		return max_depth;
	}

	public void setMaxDepth(int maxDepth)
	{
		this.max_depth = maxDepth;
	}

	public int getMaxHeight()
	{
		return max_height;
	}

	public void setMaxHeight(int maxHeight)
	{
		this.max_height = maxHeight;
	}
}
