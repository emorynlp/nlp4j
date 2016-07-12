/******************************************************************************
 ** This data and information is proprietary to, and a valuable trade secret
 ** of, Basis Technology Corp.  It is given in confidence by Basis Technology
 ** and may only be used as permitted under the license agreement under which
 ** it has been distributed, and in no other way.
 **
 ** Copyright (c) 2015 Basis Technology Corporation All rights reserved.
 **
 ** The technical data and information provided herein are provided with
 ** `limited rights', and the computer software provided herein is provided
 ** with `restricted rights' as those terms are defined in DAR and ASPR
 ** 7-104.9(a).
 ******************************************************************************/

package edu.emory.matchcs.nlp.decode;

import com.google.common.io.Resources;
import edu.emory.mathcs.nlp.bin.NLPDecode;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.decode.DecodeConfig;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

/**
 *
 */
public class AbstractNLPDecoderTest {

    @Test
    public void createTsv() throws Exception {
        URL configUrl = Resources.getResource("decoder-test-config.xml");
        DecodeConfig config;
        try (InputStream configStream = Resources.asByteSource(configUrl).openStream()) {
            config = new DecodeConfig(configStream);
        }

        NLPDecoder decoder = new NLPDecoder(config);
        TSVReader<NLPNode> reader = decoder.createTSVReader();
        URL tsvUrl = Resources.getResource("dat/sample-dev.tsv");
        try (InputStream tsvStream = Resources.asByteSource(tsvUrl).openStream()) {
            reader.open(tsvStream);
            reader.readDocument();
        }
    }
}
