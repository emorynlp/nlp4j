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
package edu.emory.mathcs.nlp.zzz;

import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TSVDecode
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-m", usage="model file (required)", required=true, metaVar="<filename>")
	public String model_file;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	@Option(name="-oe", usage="output file extension (default: nlp)", required=false, metaVar="<string>")
	public String output_ext = "nlp";
	
	public <N,S>TSVDecode() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState>TSVDecode(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		OnlineComponent<S> component = (OnlineComponent<S>)in.readObject();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		evaluate(FileUtils.getFileList(input_path, input_ext), component);
	}
	
	public <S extends NLPState>void evaluate(List<String> inputFiles, OnlineComponent<S> component) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		PrintStream fout;
		NLPNode[] nodes;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			fout = IOUtils.createBufferedPrintStream(inputFile+"."+output_ext);
			
			while ((nodes = reader.next()) != null)
			{
				GlobalLexica.assignGlobalLexica(nodes);
				component.process(nodes);
				fout.println(Joiner.join(nodes, "\n", 1)+"\n");
			}
			
			reader.close();
			fout.close();
		}
	}
	
	static public void main(String[] args)
	{
		try
		{
			new TSVDecode(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
