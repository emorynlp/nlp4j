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
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.component.state.NLPState;
import edu.emory.mathcs.nlp.emorynlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.model.StringModelHash;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrim
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
	@Option(name="-tolerance", usage="accuracy tolerance (default: 0.02)", required=false, metaVar="<float>")
	public float tolerance = 0.02f;
	@Option(name="-id", usage="model id (default: 0)", required=false, metaVar="<integer>")
	public int model_id = 0;
	
	public <N,S>NLPTrim() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState<NLPNode>>NLPTrim(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		NLPOnlineComponent<NLPNode,S> component = (NLPOnlineComponent<NLPNode,S>)in.readObject();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		List<String> inputFiles = FileUtils.getFileList(input_path, input_ext);
		StringModelHash model = (StringModelHash)component.getModels()[model_id];
		
		int count = 0;
		byte[] prevModel = model.toByteArray();
		double score = evaluate(inputFiles, component, model, 0, count), currScore;
		
		for (float f=start; ; f+=increment)
		{
			count += model.trim(f);
			currScore = evaluate(inputFiles, component, model, f, count);
			
			if (currScore + tolerance >= score)
				prevModel = model.toByteArray();
			else
				break;
		}
		
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(model_file+"."+output_ext);
		model.fromByteArray(prevModel);
		fout.writeObject(component);
		fout.close();
	}
	
	public <S extends NLPState<NLPNode>>double evaluate(List<String> inputFiles, NLPOnlineComponent<NLPNode,S> component, StringModel model, float rate, int count) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		NLPNode[] nodes;
		
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			while ((nodes = reader.next(NLPNode::new)) != null)
				component.process(nodes);
			
			reader.close();
		}
		
		System.out.println(String.format("%5.4f: %s -> %5.2f", rate, eval.toString(), MathUtils.accuracy(count, model.getFeatureSize())));
		return eval.score();
	}
	
	static public void main(String[] args)
	{
		try
		{
			new NLPTrim(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
