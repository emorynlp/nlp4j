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
package edu.emory.mathcs.nlp.component.pos;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.component.pos.feature.POSFeatureTemplate0;
import edu.emory.mathcs.nlp.component.pos.feature.POSFeatureTemplate1;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrainer extends OnlineTrainer<POSState>
{
	@Override
	protected OnlineComponent<POSState> createComponent(InputStream config)
	{
		return new POSTagger(config);
	}

	@Override
	protected void collect(OnlineComponent<POSState> component, List<String> inputFiles)
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
	protected FeatureTemplate<POSState> createFeatureTemplate(int id)
	{
		switch (id)
		{
		case 0: return new POSFeatureTemplate0();
		case 1: return new POSFeatureTemplate1();
		default: throw new IllegalArgumentException("Unknown feature template: "+id);
		}
	}
}
