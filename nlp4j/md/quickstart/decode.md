# Decode


## Command-Line

The following shows the command to run the NLP pipeline for tokenization, part-of-speech tagging, morphological analysis, named entity recognition, dependency parsing, and semantic role labeling:

```
java edu.emory.mathcs.nlp.bin.NLPDecode -c <filename> -i <filepath> [-ie <string> -oe <string>]

-c       <string>  : configuration filename (required)
-i       <string>  : input path (required)
-ie      <string>  : input file extension (default: *)
-oe      <string>  : output file extension (default: nlp)
-format  <string>  : format of the input data (raw|sen|tsv; default: raw)
-threads <integer> : number of threads (default: 2)
```

* `-c` specifies the configuration file (see [configuration](#configuration)).
* `-i` specifies the input path pointing to either a file or a directory. When the path points to a file, only the specific file is processed. When the path points to a directory, all files with the file extension `-ie` under the specific directory are processed.
* `-ie` specifies the input file extension. The default value `*` implies files with any extension. This option is used only when the input path `-i` points to a directory.
* `-oe` specifies the output file extension appended to each input filename. The corresponding output file consisting of the NLP output is generated.

The following command takes the input file ([clearnlp.txt](https://github.com/clir/clearnlp/blob/master/src/main/resources/samples/clearnlp.txt)) and the configuration file ([config\_decode_dep.xml](https://github.com/clir/clearnlp/blob/master/src/main/resources/configure/config_decode_dep.xml)), performs the NLP pipeline for dependency parsing (`dep`), and generates the output file ([clearnlp.txt.cnlp](https://github.com/clir/clearnlp/blob/master/src/main/resources/samples/clearnlp.txt.cnlp)).

```
$ java -Xmx5g -XX:+UseConcMarkSweepGC edu.emory.clir.clearnlp.bin.NLPDecode -mode dep -c config_decode_dep.xml -i sample-raw.txt
Loading distributional semantics.
Loading dependency parsing models.
Loading part-of-speech tagging models.
Decoding:
sample.txt
```

* Make sure to use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* If you want to run the sematic role labeling pipeline, use [config\_decode_srl.xml](https://github.com/clir/clearnlp/blob/master/src/main/resources/configure/config_decode_srl.xml) instead.
* If you want to run the named entity recognition pipeline, use [config\_decode_ner.xml](https://github.com/clir/clearnlp/blob/master/src/main/resources/configure/config_decode_ner.xml) instead, which takes about 9GB of RAM.
* Add the log4j configuration file ([log4j.properties](https://github.com/clir/clearnlp/blob/master/src/main/resources/configure/log4j.properties)) to your classpath.
* Use our [visualization tool](http://nlp.mathcs.emory.edu/clearnlp/demo/demo.html) to view the output.

-c src/main/resources/configuration/config_decode.xml -format tsv -i /Users/jdchoi/Documents/EmoryNLP/corenlp/src/main/resources/dat/sample.tsv -threads 1

## Configuration

The following describes the specifications of the [configuration files](https://github.com/clir/clearnlp/blob/master/src/main/resources/configure/).

| Element | Description |
| :-----: | :---------- |
| `<language>` | Specifies the language of the models.<ul><li>See [TLanguage](https://github.com/clir/clearnlp/blob/master/src/main/java/edu/emory/clir/clearnlp/util/lang/TLanguage.java) for all supported languages.</li></ul> |
| `<global>` | Specifies the lexicons used globally across different components.<ul><li>`distributional_semantics`: distributional semantics (e.g., brown clusters, word embeddings).</li><li>`named_entity_dictionary `: named entity dictionary.</li></ul> |
| `<model>` | Specifies the model file of each component.<ul><li>See [NLPMode](https://github.com/clir/clearnlp/blob/master/src/main/java/edu/emory/clir/clearnlp/component/utils/NLPMode.java) for all supported components.</li><li>See [How to add models](../quick_start/models.md) for more details about the model files.</li></ul> |
| `<reader>` | Specifies the data format of the input files.<ul><li>`type` specifies the type of the [data format](../formats/data_format.md):<br>&#9702; `raw`: accepts texts in any format.<br>&#9702; `line`: requires each sentence to be in one line.<br>&#9702; `tsv `: requires each field to be in one column delimited by tabs.</li><li>When `tsv ` is used, `<column>` must be specified.<br>&#9702; `index` specifies the index of the field, starting at 1.<br>&#9702; `field` specifies the name of the field.<br>&nbsp;&nbsp;&nbsp;&#8226; `form`: word form.</li></ul> |
| `<dep>` | Specifies the configuration of dependency parsing.<ul><li>`root_label`: label of the root node.</li><li>`beam_size`: beam size for selectional branching.</li></ul> |

## Run a Java class using Maven

* Specify the [JVM options](http://www.oracle.com/technetwork/articles/java/vmoptions-jsp-140102.html) in Maven.  If you are using [Bash](https://www.gnu.org/software/bash/), export `MAVEN_OPTS`:

   ```
   export MAVEN_OPTS='-Xmx8g -XX:+UseConcMarkSweepGC -XX:MaxPermSize=128m'
   ```

* Compile the Java project using Maven by running the following command from the top directory, where the [`pom.xml`](../../pom.xml) is located. The `target/classes` directory should be created after running this command if it does not already exist.

   ```
   mvn compile
   ```

* Copy [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to `target/classes` if it is not already specified in your path.

* Run an executable Java class using `mvn exec:java`.  For instance, the following command executes [`POSTrain`](../../src/main/java/edu/emory/mathcs/nlp/bin/POSTrain.java) (see [part-of-speech tagging](../component/part_of_speech_tagging.md#training) for more details about the command). Note that the base filenames are used in this example, but use the filenames with their absolute paths if they are not getting recognized.

   ```
   mvn exec:java -Dexec.mainClass="edu.emory.mathcs.nlp.bin.POSTrain" -Dexec.args="-c config_train_pos.xml -t wsj_0001.dep -d wsj_0001.dep"
   ```
