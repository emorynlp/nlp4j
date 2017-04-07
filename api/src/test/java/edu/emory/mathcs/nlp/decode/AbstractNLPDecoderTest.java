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

package edu.emory.mathcs.nlp.decode;

import com.google.common.io.Resources;

import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.lexicon.NLPLexiconMapper;
import edu.emory.mathcs.nlp.component.template.lexicon.NLPLexicon;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;

import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.structure.dependency.NLPNode;

import org.junit.Test;
import org.tukaani.xz.XZInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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

    private <T> NLPLexicon<T> readLexiconItemFromStream(InputStream stream, Field field, String name) throws IOException, ClassNotFoundException {
        return NLPLexiconMapper.getGlobalLexicon(stream, field, name);
    }

    private InputStream openTestResourceFromTarget(String pathname) throws IOException {
        Path path = getPathForTestResource(pathname);
        // all these are sitting around XZ-compressed.
        return new XZInputStream(new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ)));
    }

    private Path getPathForTestResource(String pathname) {
        Path path = Paths.get("target/english");
        path = path.resolve(pathname);
        return path;
    }

    @Test
    public void initializeManually() throws Exception {
        List<NLPComponent<NLPNode>> components = new ArrayList<>();
        NLPLexiconMapper<NLPNode> lexica = new NLPLexiconMapper<>();
        lexica.setAmbiguityClasses(readLexiconItemFromStream(openTestResourceFromTarget("edu/emory/mathcs/nlp/lexica/en-ambiguity-classes-simplified-lowercase.xz"),
                Field.ambiguity_classes, "edu/emory/mathcs/nlp/lexica/en-ambiguity-classes-simplified-lowercase.xz"));
        lexica.setWordClusters(readLexiconItemFromStream(openTestResourceFromTarget("edu/emory/mathcs/nlp/lexica/en-brown-clusters-simplified-lowercase.xz"),
                Field.word_clusters, "edu/emory/mathcs/nlp/lexica/en-brown-clusters-simplified-lowercase.xz"));
        lexica.setWordEmbeddings(readLexiconItemFromStream(openTestResourceFromTarget("edu/emory/mathcs/nlp/lexica/en-word-embeddings-undigitalized.xz"),
                Field.word_embedding, "edu/emory/mathcs/nlp/lexica/en-word-embeddings-undigitalized.xz"));
        components.add(lexica);
        components.add(edu.emory.mathcs.nlp.common.util.NLPUtils.getComponent(getPathForTestResource("edu/emory/mathcs/nlp/models/en-pos.xz")));
        NLPDecoder decoder = new NLPDecoder();
        decoder.setComponents(components);
        decoder.setTokenizer(new EnglishTokenizer());
        NLPNode[] results = decoder.decode("My dog has fleas.");
        assertNotNull(results[1].getSyntacticTag());
    }
}
