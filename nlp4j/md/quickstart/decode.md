# Decode

## Command-Line

The following shows the command to run the NLP pipeline for tokenization, part-of-speech tagging, morphological analysis, named entity recognition, dependency parsing, and semantic role labeling:

```bash
java edu.emory.mathcs.nlp.bin.NLPDecode -c <filename> -i <filepath> [-ie <string> -oe <string> -format <string> -threads <integer>]

-c       <filename> : configuration filename (required)
-i       <filepath> : input path (required)
-ie      <string>   : input file extension (default: *)
-oe      <string>   : output file extension (default: nlp)
-format  <string>   : format of the input data (raw|sen|tsv; default: raw)
-threads <integer>  : number of threads (default: 2)
```

* `-c` specifies the configuration file (see [configuration](#configuration)).
* `-i` specifies the input path pointing to either a file or a directory. When the path points to a file, only the specific file is processed. When the path points to a directory, all files with the file extension `-ie` under the specific directory are processed.
* `-ie` specifies the input file extension. The default value `*` implies files with any extension. This option is used only when the input path `-i` points to a directory.
* `-oe` specifies the output file extension appended to each input filename. The corresponding output file, consisting of the NLP output, will be generated.
* `-format` specifies the format of the input file: `raw`, `sen`, or `tsv` (see [data format](data-format.md)).
* `-threads` specifies the number of threads to be used. When multi-threads are used, each file is assigned to an individual thread.

The following command takes [`config-decode-general.xml`](../../src/main/resources/configuration/config-decode-general.xml) and [emorynlp.txt](../../src/main/resources/dat/emorynlp.txt), and generates [emorynlp.txt.nlp](../../src/main/resources/dat/emorynlp.txt.nlp).

```bash
$ java -Xmx8g -XX:+UseConcMarkSweepGC edu.emory.mathcs.nlp.bin.NLPDecode -c config-decode-general.xml -i emorynlp.txt
Loading ambiguity classes: 408397
Loading word clusters: 594491
Loading word embeddings: 
Loading named entity gazetteers
Loading part-of-speech tagger
Loading morphological analyzer
Loading named entity recognizer
Loading dependency parser
Loading semantic role labeler

emorynlp.txt
```

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* The output file is generated in the `tsv` format (see [data format](data-format.md#tab-separated-values-format)).
* Add [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to your classpath (see [log4j](http://logging.apache.org/log4j/)).

## Configuration

The following shows the content in [`config-decode-general.xml`](../../src/main/resources/configuration/config-decode-general.xml).  More configuration files can be found [here](../../src/main/resources/configuration/).

```xml
<configuration>
	<tsv>
        <column index="0" field="form"/>
    </tsv>

    <lexica>
        <ambiguity_classes field="word_form_simplified">edu/emory/mathcs/nlp/english/lexica/en-ambiguity-classes-simplified-lowercase.xz</ambiguity_classes>
        <word_clusters field="word_form_simplified_lowercase">edu/emory/mathcs/nlp/english/lexica/en-brown-clusters-simplified-lowercase.xz</word_clusters>
        <word_embeddings field="word_form_simplified">edu/emory/mathcs/nlp/english/lexica/en-word2vec-embeddings-simplified.xz</word_embeddings>
        <named_entity_gazetteers field="word_form_simplified">edu/emory/mathcs/nlp/english/lexica/en-named-entity-gazetteers-simplified.xz</named_entity_gazetteers>
    </lexica>

    <models>
    	<pos>edu/emory/mathcs/nlp/english/models/en-general-pos.xz</pos>
    	<ner>edu/emory/mathcs/nlp/english/models/en-general-ner.xz</ner>
    	<dep>edu/emory/mathcs/nlp/english/models/en-general-dep.xz</dep>
    	<srl>edu/emory/mathcs/nlp/english/models/en-general-srl.xz</srl>
    </models>
</configuration>
```

* `<tsv>`:
 * When the `tsv` format is used, only the `form` column must be specified in the configuration file.
* `<lexica>`:
 * `<ambiguity_classes>`: 
 * `<word_clusters>`: 
 * `<word_embeddings>`: 
 * `<named_entity_gazetteers>`: 
* `<models>`:
 * `<pos>`: 
 * `<ner>`: 
 * `<dep>`: 
 * `<srl>`: 


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
