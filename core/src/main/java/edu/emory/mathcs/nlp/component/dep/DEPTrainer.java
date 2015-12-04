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
package edu.emory.mathcs.nlp.component.dep;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate0;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate1;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate2;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate3;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate4;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate5;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate6;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate7;
import edu.emory.mathcs.nlp.component.dep.feature.DEPFeatureTemplate8;
import edu.emory.mathcs.nlp.component.template.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.template.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.template.train.NLPOnlineTrainer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPTrainer extends NLPOnlineTrainer<DEPState>
{
	@Override
	protected NLPOnlineComponent<DEPState> createComponent(InputStream config)
	{
		return new DEPParser(config);
	}

	@Override
	protected void collect(NLPOnlineComponent<DEPState> component, List<String> inputFiles) {}
	
	@Override
	protected FeatureTemplate<DEPState> createFeatureTemplate(int id)
	{
		switch (id)
		{
		case  0: return new DEPFeatureTemplate0();
		case  1: return new DEPFeatureTemplate1();
		case  2: return new DEPFeatureTemplate2();
		case  3: return new DEPFeatureTemplate3();
		case  4: return new DEPFeatureTemplate4();
		case  5: return new DEPFeatureTemplate5();
		case  6: return new DEPFeatureTemplate6();
		case  7: return new DEPFeatureTemplate7();
		case  8: return new DEPFeatureTemplate8();
		default: throw new IllegalArgumentException("Unknown feature template: "+id);
		}
	}
}
