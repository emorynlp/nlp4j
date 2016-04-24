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
import java.io.ObjectInputStream;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ModelReduce extends NLPTrain
{
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>void reduce(String[] args)
	{
		BinUtils.initArgs(args, this);
		OnlineTrainer<N,S> trainer = createOnlineTrainer();
		
		List<String> developFiles  = FileUtils.getFileList(develop_path, develop_ext);
		GlobalLexica<N> lexica = trainer.createGlobalLexica(IOUtils.createFileInputStream(configuration_file));
		
		BinUtils.LOG.info("Loading the model\n");
		OnlineComponent<N,S> component = readComponent(IOUtils.createFileInputStream(previous_model_file), IOUtils.createFileInputStream(configuration_file));
		TSVReader<N> reader = trainer.createTSVReader(component.getConfiguration().getReaderFieldMap());
		trainer.reduceModel(reader, developFiles, component, lexica, previous_model_file, model_file);
	}
	
	@SuppressWarnings("unchecked")
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>OnlineComponent<N,S> readComponent(InputStream model, InputStream config)
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(model);
		OnlineComponent<N,S> component = null;
		
		try
		{
			component = (OnlineComponent<N,S>)oin.readObject();
			component.setConfiguration(config);
			oin.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return component;
	}
	
	static public void main(String[] args)
	{
		new ModelReduce().reduce(args);
	}
}
