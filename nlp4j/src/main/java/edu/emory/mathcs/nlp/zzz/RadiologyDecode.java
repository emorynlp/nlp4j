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

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RadiologyDecode
{
	static public void main(String[] args) throws Exception
	{
		final String configFile   = "/Users/jdchoi/Documents/EmoryNLP/nlp4j/src/main/resources/edu/emory/mathcs/nlp/configuration/config-decode-deident.xml";
		final String inputDir     = "/Users/jdchoi/Desktop/radiology/Q2";
		final String inputExt     = "txt";
		final String outputExt    = "tsv";
		final String outputFormat = NLPDecoder.FORMAT_LINE;
		
		NLPDecoder nlp4j = new NLPDecoder(IOUtils.createFileInputStream(configFile));
		
		for (String inputFile : FileUtils.getFileList(inputDir, inputExt))
		{
			System.out.println(inputFile);
			String outputFile = inputFile+"."+outputExt;
			nlp4j.decode(IOUtils.createFileInputStream(inputFile), IOUtils.createFileOutputStream(outputFile), outputFormat);	
		}
	}
}
