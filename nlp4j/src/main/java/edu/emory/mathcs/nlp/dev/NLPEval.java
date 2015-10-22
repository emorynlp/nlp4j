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

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPEval
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-m", usage="model file (required)", required=true, metaVar="<filename>")
	public String model_file;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	
	public <N,S>NLPEval() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState<NLPNode>>NLPEval(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		NLPOnlineComponent<NLPNode,S> component = (NLPOnlineComponent<NLPNode,S>)in.readObject();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		evaluate(FileUtils.getFileList(input_path, input_ext), component);
	}
	
	public <S extends NLPState<NLPNode>>void evaluate(List<String> inputFiles, NLPOnlineComponent<NLPNode,S> component) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		long st, et, time = 0, tokens = 0, sentences = 0;
		NLPNode[] nodes;
		
		// warm-up
		component.setFlag(NLPFlag.DECODE);		
		reader.open(IOUtils.createFileInputStream(inputFiles.get(0)));
		for (int i=0; i<100 && (nodes = reader.next(NLPNode::new)) != null; i++) component.process(nodes);
		reader.close();
		
		component.setFlag(NLPFlag.EVALUATE);
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			while ((nodes = reader.next(NLPNode::new)) != null)
			{
				st = System.currentTimeMillis();
				component.process(nodes);
				et = System.currentTimeMillis();
				time += et - st;
				tokens += nodes.length - startIndex(nodes);
				sentences++;
			}
			
			reader.close();
		}
		
		System.out.println(component.getEval().toString());
		System.out.printf("Sent.  per sec: %5d\n", Math.round(1000d * sentences / time));
		System.out.printf("Tokens per sec: %5d\n", Math.round(1000d * tokens    / time));
	}
	
	private <N extends NLPNode>int startIndex(N[] nodes)
	{
		return nodes[0].getID() == 0 ? 1 : 0;
	}
	
	static public void main(String[] args)
	{
		try
		{
			new NLPEval(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
