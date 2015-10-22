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

import java.util.List;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.train.NLPOnlineTrain;
import edu.emory.mathcs.nlp.emorynlp.component.train.TrainInfo;
import edu.emory.mathcs.nlp.emorynlp.ner.NERState;
import edu.emory.mathcs.nlp.emorynlp.ner.NERTagger;
import edu.emory.mathcs.nlp.emorynlp.ner.features.NERFeatureTemplate0;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.vector.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTrain extends NLPOnlineTrain<NLPNode,NERState<NLPNode>>
{
	public NERTrain(String[] args)
	{
		super(args);
	}

	@Override
	protected NLPOnlineComponent<NLPNode,NERState<NLPNode>> createComponent(String configurationFile, List<String> inputFiles)
	{
		NERTagger<NLPNode> component = new NERTagger<>(IOUtils.createFileInputStream(configurationFile));
		NLPConfig<NLPNode> configuration = component.getConfiguration();
		
		WeightVector vector = new WeightVector();
		StringModel model = new StringModel(vector);
		TrainInfo info = configuration.getTrainInfo();
		OnlineOptimizer optimizer = configuration.getOptimizer(model);
		FeatureTemplate<NLPNode,NERState<NLPNode>> template = createFeatureTemplate();
		
		component.setModel(model);
		component.setTrainInfo(info);
		component.setOptimizer(optimizer);
		component.setFeatureTemplate(template);
		
		return component;
	}
	
	protected FeatureTemplate<NLPNode,NERState<NLPNode>> createFeatureTemplate()
	{
		switch (feature_template)
		{
		case  0: return new NERFeatureTemplate0<NLPNode>();
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
		new NERTrain(args).train();
	}
}
