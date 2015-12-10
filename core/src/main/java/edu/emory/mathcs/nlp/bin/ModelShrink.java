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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.zzz.StringModel;
import edu.emory.mathcs.nlp.learning.zzz.StringModelMap;

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
	@Option(name="-oe", usage="output file extension (default: shrink)", required=false, metaVar="<string>")
	public String output_ext = "sk";
	@Option(name="-start", usage="starting shrink rate (default: 0.05)", required=false, metaVar="<float>")
	public float start = 0.05f;
	@Option(name="-inc", usage="increment rate (default: 0.01)", required=false, metaVar="<float>")
	public float increment = 0.01f;
	@Option(name="-lower", usage="lower bound (required)", required=true, metaVar="<float>")
	public float lower_bound;
	@Option(name="-id", usage="model id (default: 0)", required=false, metaVar="<integer>")
	public int model_id = 0;
	
	public <N,S>ModelShrink() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState>ModelShrink(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		OnlineComponent<S> component = (OnlineComponent<S>)in.readObject(); in.close();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		List<String> inputFiles = FileUtils.getFileList(input_path, input_ext);
		StringModelMap model = (StringModelMap)component.getModels()[model_id];
		
		byte[] prevModel = model.toByteArray();
		double currScore;
		
		evaluate(inputFiles, component, model, 0f);
		
		for (float f=start; ; f+=increment)
		{
			model.shrink(f);
			currScore = evaluate(inputFiles, component, model, f);
			
			if (lower_bound < currScore)
				prevModel = model.toByteArray();
			else
				break;
		}
		
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(model_file+"."+output_ext);
		model.fromByteArray(prevModel);
		fout.writeObject(component);
		fout.close();
	}
	
	public <S extends NLPState>double evaluate(List<String> inputFiles, OnlineComponent<S> component, StringModel model, float rate) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		NLPNode[] nodes;
		
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			while ((nodes = reader.next()) != null)
			{
				GlobalLexica.assignGlobalLexica(nodes);
				component.process(nodes);
			}
			
			reader.close();
		}
		
		System.out.println(String.format("%5.4f: %s -> %d", rate, eval.toString(), model.getFeatureSize()));
		return eval.score();
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
