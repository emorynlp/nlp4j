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
package edu.emory.mathcs.nlp.dev;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.emorynlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ModelShrink
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-m", usage="model file (required)", required=true, metaVar="<filename>")
	public String model_file;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	@Option(name="-r", usage="rate", required=false, metaVar="<string>")
	public float rate = 0.001f;
	
	public <N,S>ModelShrink() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState<NLPNode>>ModelShrink(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		NLPOnlineComponent<NLPNode,S> component = (NLPOnlineComponent<NLPNode,S>)in.readObject();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		StringModel model = component.getModel();
		List<String> inputFiles = FileUtils.getFileList(input_path, input_ext);
		
		float f = 0f;
		evaluate(inputFiles, component, f);
		
		for (f=rate; f<=rate*10; f+=rate)
		{
			if (f > 0) model.shrink(f);
			evaluate(inputFiles, component, f);
		}
		
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(model_file+"."+rate);
		fout.writeObject(component);
		fout.close();
	}
	
	public <S extends NLPState<NLPNode>>void evaluate(List<String> inputFiles, NLPOnlineComponent<NLPNode,S> component, float rate) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		NLPNode[] nodes;
		
		component.setFlag(NLPFlag.EVALUATE);
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			while ((nodes = reader.next(NLPNode::new)) != null)
				component.process(nodes);
			
			reader.close();
		}
		
		System.out.println(String.format("%5.4f: %s -> %d", rate, component.getEval().toString(), component.getModel().getWeightVector().countNonZeroWeights()));
	}
	
	static public void main(String[] args)
	{
		try
		{
			new ModelShrink(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
