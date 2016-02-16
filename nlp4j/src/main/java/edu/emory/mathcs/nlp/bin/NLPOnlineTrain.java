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
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.pos.POSState;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.component.template.train.LOLS;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGrad;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPOnlineTrain
{
	@Option(name="-c", usage="confinguration filename (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	@Option(name="-oe", usage="output file extension (default: nlp)", required=false, metaVar="<string>")
	public String output_ext = "nlp";
	@Option(name="-format", usage="format of the input data (raw|line|tsv; default: raw)", required=false, metaVar="<string>")
	private String format = NLPDecoder.FORMAT_RAW;
	@Option(name="-threads", usage="number of threads (default: 2)", required=false, metaVar="<integer>")
	protected int threads = 2;
	private NLPDecoder decoder;

//	======================================== CONSTRUCTORS ========================================
	
	@SuppressWarnings("unchecked")
	public NLPOnlineTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> filelist = FileUtils.getFileList(input_path, input_ext, false);
		Collections.sort(filelist);
		
		decoder = new NLPDecoder(IOUtils.createFileInputStream(configuration_file));
		System.out.println(decoder.decode("Republicans Close Ranks Over Scalia’s Replacement", "raw"));
		System.out.println(decoder.decode("Blocking Nominee Could be a Risk for McConnell", "raw"));
		System.out.println(decoder.decode("Some Economists See Huge Costs in Sanders’s Agenda", "raw"));
		
		HyperParameter hp = new HyperParameter();
		hp.setLearningRate(0.01f);
		hp.setLOLS(new LOLS(1, 0.95f));
		
		OnlineComponent<POSState> tagger = (OnlineComponent<POSState>)decoder.getComponents()[0];
		OnlineOptimizer optimizer = new AdaGrad(tagger.getOptimizer().getWeightVector(), hp.getLearningRate(), hp.getBias());
		optimizer.setLabelMap(tagger.getOptimizer().getLabelMap());
		tagger.setOptimizer(optimizer);
		tagger.setHyperParameter(hp);
				
		for (int i=0; i<10; i++)
		{
			tagger.setFlag(NLPFlag.TRAIN);
			tagger.process(createNodes());
			tagger.setFlag(NLPFlag.EVALUATE);
			tagger.getEval().clear();
			tagger.process(createNodes());
			tagger.setFlag(NLPFlag.DECODE);
			if (tagger.getEval().score() == 100) break;
		}

		System.out.println("======================");
		System.out.println(decoder.decode("Republicans close Ranks Over Scalia’s Replacement", "raw"));
		System.out.println(decoder.decode("Blocking Nominee Could be a Risk for McConnell", "raw"));
		System.out.println(decoder.decode("Some Economists See Huge Costs in Sanders’s Agenda", "raw"));
	}
	
	NLPNode[] createNodes()
	{
		NLPNode[] nodes = new NLPNode[5];
		nodes[0] = new NLPNode().toRoot();
		nodes[1] = new NLPNode(1, "UN", "NNP");
		nodes[2] = new NLPNode(2, "peacekeepers", "NNS");
		nodes[3] = new NLPNode(3, "abuse", "VBP");
		nodes[4] = new NLPNode(4, "children", "NNS");
		return nodes;
	}
	
	static public void main(String[] args)
	{
		new NLPOnlineTrain(args);
	}
}
