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

import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	protected String configuration_file;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	protected String train_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	protected String train_ext = "*";
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	protected String develop_path;
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	protected String develop_ext = "*";
	@Option(name="-m", usage="output model file (optional)", required=false, metaVar="<filename>")
	protected String model_file = null;
	@Option(name="-p", usage="previously trained model file (optional)", required=false, metaVar="<filename>")
	protected String previous_model_file = null;
	@Option(name="-mode", usage="mode (required: pos|ner|dep|srl|doc)", required=true, metaVar="<string>")
	protected String mode = null;
	
	public NLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> trainFiles   = FileUtils.getFileList(train_path  , train_ext);
		List<String> developFiles = FileUtils.getFileList(develop_path, develop_ext);
		OnlineTrainer<?> trainer = new OnlineTrainer<>();
		
		Collections.sort(trainFiles);
		Collections.sort(developFiles);
		
		trainer.train(NLPMode.valueOf(mode), trainFiles, developFiles, configuration_file, model_file, previous_model_file);
	}
	
	static public void main(String[] args)
	{
		new NLPTrain(args);
	}
}
