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
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.decode.DecodeConfig;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import org.kohsuke.args4j.Option;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

/**
 * A command-line program that does LAS/UAS evaluation for dependency parsing.
 * By default, it allows NLP4J to predict the part of speech tags. Optionally,
 * it will use POS tags from the input TSV.
 */
public class DEPEvaluate {

    @Option(name="-c", usage="configuration filename (required)", required=true, metaVar="<filename>")
    public String configuration_file;
    @Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
    public String input_path;
    @Option(name="-goldPos", usage = "use gold pos tags")
    public boolean useGoldPos;

    private DEPEvaluate(String[] args) throws Exception {
        BinUtils.initArgs(args, this);
        List<String> filelist = FileUtils.getFileList(input_path, "*", false);
        Collections.sort(filelist);

        DecodeConfig decodeConfig;
        try (InputStream config = Files.newInputStream(Paths.get(configuration_file), StandardOpenOption.READ)) {
            decodeConfig = new DecodeConfig(config);
        }

        NLPDecoder decoder = new NLPDecoder(decodeConfig);

        List<NLPNode[]> sentences;
        try (InputStream is = Files.newInputStream(Paths.get(filelist.get(0)), StandardOpenOption.READ)) {
            TSVReader<NLPNode> reader = new TSVReader<NLPNode>(decodeConfig.getReaderFieldMap())
            {
                @Override
                protected NLPNode create() {return new NLPNode();}
            };

            reader.open(is);
            sentences = reader.readDocument();
        }

        int uas = 0;
        int las = 0;
        int pos = 0;
        int total = 0;

        for (NLPNode[] sentence : sentences) {
            int[] goldHeads = new int[sentence.length];
            String[] goldLabels = new String[sentence.length];
            String[] goldPos = new String[sentence.length];
            for (int x = 1; x < sentence.length; x++) {
                // capture gold and erase it so we recreate it in the decode.
                goldHeads[x] = sentence[x].getDependencyHead().getID();
                sentence[x].setDependencyHead(null);
                goldLabels[x] = sentence[x].getDependencyLabel();
                sentence[x].setDependencyLabel(null);
                // also forget the POS tag
                if (!useGoldPos) {
                    goldPos[x] = sentence[x].getPartOfSpeechTag();
                    sentence[x].setPartOfSpeechTag(null);
                }
            }
            decoder.decode(sentence);
            for (int x = 1; x < sentence.length; x++) {
                total++;
                if (!useGoldPos) {
                    if (goldPos[x].equals(sentence[x].getPartOfSpeechTag())) {
                        pos++;
                    }
                }

                if (goldHeads[x] == sentence[x].getDependencyHead().getID()) {
                    uas++;
                    if (goldLabels[x].equals(sentence[x].getDependencyLabel())) {
                        las++;
                    }
                }
            }
        }

        double uscore = ((double)uas)/total;
        double lscore = ((double)las)/total;
        if (!useGoldPos) {
            double posscore = ((double)pos)/total;
            System.out.format("UAS %.02f LAS %.02f POS %.02f total tokens %d%n", uscore, lscore, posscore, total);
        } else {
            System.out.format("UAS %.02f LAS %.02f total tokens %d%n", uscore, lscore, total);
        }
    }

    public static void main(String args[]) throws Exception {
        new DEPEvaluate(args);
    }
}
