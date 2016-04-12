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
import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.component.dep.DEPParser;
import edu.emory.mathcs.nlp.component.doc.DOCAnalyzer;
import edu.emory.mathcs.nlp.component.ner.NERTagger;
import edu.emory.mathcs.nlp.component.pos.POSTagger;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.NLPReader;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	protected String configuration_file;
	@Option(name="-m", usage="output model file (optional)", required=false, metaVar="<filename>")
	protected String model_file = null;
	@Option(name="-p", usage="previously trained model file (optional)", required=false, metaVar="<filename>")
	protected String previous_model_file = null;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	protected String train_path;
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	protected String develop_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	protected String train_ext = "*";
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	protected String develop_ext = "*";
	@Option(name="-mode", usage="mode (required: pos|ner|dep)", required=true, metaVar="<string>")
	protected String mode = null;
	@Option(name="-preserve_last", usage="if set, preserve the last model", required=false, metaVar="<boolean>")
	protected boolean preserve_last = false;
	
	// model reduction
	@Option(name="-reduce_start", usage="starting reduce rate (default: 0)", required=false, metaVar="<float>")
	public float reduce_start = 0f;
	@Option(name="-reduce_inc", usage="incremental reduce rate (default: 0.01)", required=false, metaVar="<float>")
	public float reduce_inc = 0.01f;
	
	public void train(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> trainFiles    = FileUtils.getFileList(train_path  , train_ext);
		List<String> developFiles  = FileUtils.getFileList(develop_path, develop_ext);
		OnlineTrainer<?,?> trainer = createOnlineTrainer();
		
		Collections.sort(trainFiles);
		Collections.sort(developFiles);
		trainer.train(NLPMode.valueOf(mode), trainFiles, developFiles, configuration_file, model_file, previous_model_file, preserve_last, reduce_start, reduce_inc);
	}
	
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>OnlineTrainer<N,S> createOnlineTrainer()
	{
		return new OnlineTrainer<N,S>()
		{
			@Override
			@SuppressWarnings("unchecked")
			public OnlineComponent<N,S> createComponent(NLPMode mode, InputStream config)
			{
				switch (mode)
				{
				case pos: return (OnlineComponent<N,S>)new POSTagger<>(config);
				case ner: return (OnlineComponent<N,S>)new NERTagger<>(config);
				case dep: return (OnlineComponent<N,S>)new DEPParser<>(config);
				case doc: return (OnlineComponent<N,S>)new DOCAnalyzer<>(config);
//				case srl: return (OnlineComponent<N,S>)new SRLParser(config);
				default : throw new IllegalArgumentException("Unsupported mode: "+mode);
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public TSVReader<N> createTSVReader(Object2IntMap<String> map)
			{
				return (TSVReader<N>)new NLPReader(map);
			}

			@Override
			public GlobalLexica<N> createGlobalLexica(InputStream config)
			{
				return new GlobalLexica<>(config);
			}
		};
	}
	
	static public void main(String[] args)
	{
		new NLPTrain().train(args);
	}
}
