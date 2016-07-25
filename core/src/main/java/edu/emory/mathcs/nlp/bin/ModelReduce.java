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

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.lexicon.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ModelReduce extends NLPTrain
{
	private static final Logger LOG = LoggerFactory.getLogger(ModelReduce.class);
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>void reduce(String[] args)
	{
		BinUtils.initArgs(args, this);
		OnlineTrainer<N,S> trainer = createOnlineTrainer();
		
		List<String> developFiles  = FileUtils.getFileList(develop_path, develop_ext);
		GlobalLexica<N> lexica;
		try (InputStream lexicaStream = IOUtils.createFileInputStream(configuration_file)) {
			lexica = trainer.createGlobalLexica(lexicaStream);
		} catch (IOException e) {
			LOG.error("Failed to read lexica from " + configuration_file, e);
			throw new RuntimeException("Failed to read lexica", e);
		}


		LOG.info("Loading the model");
		OnlineComponent<N,S> component = null;
		try {
			component = readComponent(previous_model_file, configuration_file);
		} catch (IOException e) {
			LOG.error(String.format("Failed to read model %s or configuration %s", previous_model_file == null ? "none" : previous_model_file, configuration_file),
					e);
			throw new RuntimeException(String.format("Failed to read model %s or configuration %s", previous_model_file == null ? "none" : previous_model_file, configuration_file),
					e);
		}
		TSVReader<N> reader = trainer.createTSVReader(component.getConfiguration().getReaderFieldMap());
		trainer.reduceModel(reader, developFiles, component, lexica, previous_model_file, model_file);
	}
	
	@SuppressWarnings("unchecked")
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>OnlineComponent<N,S> readComponent(String modelPathname, String configPathname) throws IOException {
		OnlineComponent<N, S> component;
		try (ObjectInputStream oin = IOUtils.createArtifactObjectInputStream(modelPathname)) {
			component = (OnlineComponent<N, S>) oin.readObject();
			try (InputStream configStream = IOUtils.createArtifactInputStream(configPathname)) {
				component.setConfiguration(configStream);
			}
		} catch (ClassNotFoundException e) {
			LOG.error(String.format("Failed to read serialized configuration %s or model %s",
					configPathname, modelPathname),
					e);
			throw new RuntimeException("Failed to read serialized configuration or model", e);
		}

		return component;
	}
	
	static public void main(String[] args)
	{
		new ModelReduce().reduce(args);
	}
}
