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

package edu.emory.mathcs.nlp.common.util;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IOUtilsTest {

    private static final String THIS_IS_THE_CEREAL_SHOT_FROM_GUNS = "This is the cereal shot from guns.\n";

    @Test
    public void fileNonStdFileSystem() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path testFile = fs.getPath("/foo.txt");
        try (Writer writer = Files.newBufferedWriter(testFile)) {
            writer.write(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS);
        }

        try (InputStream is = IOUtils.createArtifactInputStream(testFile)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        String uri = testFile.toUri().toString();
        try (InputStream is = IOUtils.createArtifactInputStream(uri)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        Path testFilexz = fs.getPath("/foo.txt.xz");
        try (Writer writer = new OutputStreamWriter(new XZOutputStream(Files.newOutputStream(testFilexz), new LZMA2Options()), UTF_8)) {
            writer.write(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS);
        }

        try (InputStream is = IOUtils.createArtifactInputStream(testFilexz)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        uri = testFilexz.toUri().toString();
        try (InputStream is = IOUtils.createArtifactInputStream(uri)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        Path testFilegz = fs.getPath("/foo.txt.gz");
        try (Writer writer = new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(testFilegz)), UTF_8)) {
            writer.write(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS);
        }

        try (InputStream is = IOUtils.createArtifactInputStream(testFilegz)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        uri = testFilegz.toUri().toString();
        try (InputStream is = IOUtils.createArtifactInputStream(uri)) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        // test plain old file system
        try (InputStream is = IOUtils.createArtifactInputStream("src/test/resources/a/test/some.txt")) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }

        // test classpath
        try (InputStream is = IOUtils.createArtifactInputStream("a/test/some.txt")) {
            String contents = org.apache.commons.io.IOUtils.toString(is, "utf-8");
            assertEquals(THIS_IS_THE_CEREAL_SHOT_FROM_GUNS, contents);
        }
    }
}
