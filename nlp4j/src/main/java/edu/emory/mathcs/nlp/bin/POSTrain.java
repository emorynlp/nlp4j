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

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.train.NLPOnlineTrain;
import edu.emory.mathcs.nlp.emorynlp.pos.AmbiguityClassMap;
import edu.emory.mathcs.nlp.emorynlp.pos.POSConfig;
import edu.emory.mathcs.nlp.emorynlp.pos.POSState;
import edu.emory.mathcs.nlp.emorynlp.pos.POSTagger;
import edu.emory.mathcs.nlp.emorynlp.pos.feature.POSFeatureTemplate0;
import edu.emory.mathcs.nlp.emorynlp.pos.feature.POSFeatureTemplate1;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrain extends NLPOnlineTrain<NLPNode,POSState<NLPNode>>
{
	public POSTrain(String[] args)
	{
		super(args);
	}
	
	@Override
	protected NLPOnlineComponent<NLPNode,POSState<NLPNode>> createComponent(InputStream config)
	{
		return new POSTagger<>(config);
	}

	@Override
	protected void initComponent(NLPOnlineComponent<NLPNode,POSState<NLPNode>> component, List<String> inputFiles)
	{
		initComponentSingleModel(component, inputFiles);
		
		AmbiguityClassMap map = createAmbiguityClasseMap(component.getConfiguration(), inputFiles);
		((POSTagger<NLPNode>)component).setAmbiguityClassMap(map);
	}
	
	@Override
	protected FeatureTemplate<NLPNode,POSState<NLPNode>> createFeatureTemplate()
	{
		switch (feature_template)
		{
		case 0: return new POSFeatureTemplate0<NLPNode>();
		case 1: return new POSFeatureTemplate1<NLPNode>();
		default: throw new IllegalArgumentException("Unknown feature template: "+feature_template);
		}
	}
	
	@Override
	protected NLPNode createNode()
	{
		return new NLPNode();
	}
	
	protected AmbiguityClassMap createAmbiguityClasseMap(NLPConfig<NLPNode> configuration, List<String> inputFiles)
	{
		BinUtils.LOG.info("Collecting lexicons:\n");
		AmbiguityClassMap ac = new AmbiguityClassMap();
		POSConfig config = (POSConfig)configuration;
		
		iterate(configuration.getTSVReader(), inputFiles, nodes -> ac.add(nodes));
		ac.expand(config.getAmbiguityClassThreshold());
		
		BinUtils.LOG.info(String.format("- # of ambiguity classes: %d\n", ac.size()));
		return ac;
	}
	
	static public void main(String[] args)
	{
		new POSTrain(args).train();
	}
}
