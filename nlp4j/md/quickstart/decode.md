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
* `-format` specifies the format of the input file: `raw`, `sen`, or `tsv` (see [data format](../supplements/data-format.md)).
* `-threads` specifies the number of threads to be used. When multi-threads are used, each file is assigned to an individual thread.

## Example

The following command takes [`emorynlp.txt`](../../src/main/resources/dat/emorynlp.txt) and generates [`emorynlp.txt.nlp`](../../src/main/resources/dat/emorynlp.txt.nlp) using [`config-decode-general.xml`](../../src/main/resources/configuration/config-decode-general.xml).

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
* The output file is generated in the `tsv` format (see [data format](../supplements/data-format.md#tab-separated-values-format)).
* Add [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to your classpath (see [log4j](http://logging.apache.org/log4j/)).

## Configuration

The following shows the content in [`config-decode-general.xml`](../../src/main/resources/configuration/config-decode-general.xml).  More configuration files can be found [here](../../src/main/resources/configuration/).

```xml
<configuration>
    <tsv>
        <column index="1" field="form"/>
    </tsv>

    <lexica>
        <ambiguity_classes field="word_form_simplified">en-ambiguity-classes-simplified.xz</ambiguity_classes>
        <word_clusters field="word_form_simplified_lowercase">en-brown-clusters-simplified-lowercase.xz</word_clusters>
        <named_entity_gazetteers field="word_form_simplified">en-named-entity-gazetteers-simplified.xz</named_entity_gazetteers>
        <word_embeddings field="word_form_undigitalized">en-word-embeddings-undigitalized.xz</word_embeddings>
    </lexica>

    <models>
    	<pos>en-general-pos.xz</pos>
    	<ner>en-general-ner.xz</ner>
    	<dep>en-general-dep.xz</dep>
    </models>
</configuration>
```

* `<tsv>`: see [`configuration#tsv`](train.md#configuration). This does not need to be specified when `raw` or `sen` is used. When `tsv` is used, only the `form` column must be specified in the configuration file.
* `<lexica>`: see [`configuration#lexica`](train.md#configuration).
* `<models>` specifies the statistical models (e.g., [english-models](../supplements/english-models.md)).
 * `<pos>`: part-of-speech tagging.
 * `<ner>`: named entity recognition.
 * `<dep>`: dependency parsing.
 * `<srl>`: semantic role labeling.
