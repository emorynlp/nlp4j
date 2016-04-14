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
import edu.emory.mathcs.nlp.decode.AbstractNLPDecoder;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDecode
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
	private String format = AbstractNLPDecoder.FORMAT_RAW;
	@Option(name="-threads", usage="number of threads (default: 2)", required=false, metaVar="<integer>")
	protected int threads = 2;
	private NLPDecoder decoder;

//	======================================== CONSTRUCTORS ========================================
	
	public NLPDecode(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> filelist = FileUtils.getFileList(input_path, input_ext, false);
		Collections.sort(filelist);
		
		decoder = new NLPDecoder(IOUtils.createFileInputStream(configuration_file));
		decoder.decode(filelist, output_ext, format, threads);
	}
	
	static public void main(String[] args)
	{
		new NLPDecode(args);
	}
}
