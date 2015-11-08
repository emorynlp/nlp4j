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
import edu.emory.mathcs.nlp.component.common.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.common.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.common.node.NLPNode;
import edu.emory.mathcs.nlp.component.common.train.NLPOnlineTrain;
import edu.emory.mathcs.nlp.component.pos.AmbiguityClassMap;
import edu.emory.mathcs.nlp.component.pos.POSConfig;
import edu.emory.mathcs.nlp.component.pos.POSState;
import edu.emory.mathcs.nlp.component.pos.POSTagger;
import edu.emory.mathcs.nlp.component.pos.feature.POSFeatureTemplate0;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrain extends NLPOnlineTrain<POSState>
{
	public POSTrain() {}
	
	public POSTrain(String[] args)
	{
		super(args);
	}
	
	@Override
	protected NLPOnlineComponent<POSState> createComponent(InputStream config)
	{
		return new POSTagger(config);
	}

	@Override
	protected void collect(NLPOnlineComponent<POSState> component, List<String> inputFiles)
	{
		POSTagger tagger = (POSTagger)component;
		AmbiguityClassMap map = tagger.getAmbiguityClassMap();
		POSConfig config = (POSConfig)component.getConfiguration();
		
		if (map == null)
		{
			map = new AmbiguityClassMap();
			tagger.setAmbiguityClassMap(map);
		}
		
		collectAmbiguityClasses(config, inputFiles, map);
	}
	
	protected void collectAmbiguityClasses(POSConfig config, List<String> inputFiles, AmbiguityClassMap map)
	{
		BinUtils.LOG.info("Collecting ambiguity classes: ");
		iterate(config.getTSVReader(), inputFiles, nodes -> map.add(nodes));
		map.expand(config.getAmbiguityClassThreshold());
		BinUtils.LOG.info(map.size()+"\n");
	}
	
	@Override
	protected FeatureTemplate<POSState> createFeatureTemplate()
	{
		switch (feature_template)
		{
		case 0: return new POSFeatureTemplate0();
		default: throw new IllegalArgumentException("Unknown feature template: "+feature_template);
		}
	}
	
	@Override
	protected NLPNode createNode()
	{
		return new NLPNode();
	}
	
	static public void main(String[] args)
	{
		new POSTrain(args).train();
	}
}
