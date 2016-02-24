# Decode

## Command-Line

The following command runs the NLP pipeline for tokenization, part-of-speech tagging, morphological analysis, named entity recognition, dependency parsing, and semantic role labeling:

```bash
java edu.emory.mathcs.nlp.bin.NLPDecode -c <filename> -i <filepath> [-ie <string> -oe <string> -format <string> -threads <integer>]

-c       <filename> : configuration filename (required)
-i       <filepath> : input path (required)
-ie      <string>   : input file extension (default: *)
-oe      <string>   : output file extension (default: nlp)
-format  <string>   : format of the input data (raw|line|tsv; default: raw)
-threads <integer>  : number of threads (default: 2)
```

* `-c` specifies the configuration file (see [configuration](#configuration)).
* `-i` specifies the input path pointing to either a file or a directory. When the path points to a file, only the specific file is processed. When the path points to a directory, all files with the file extension `-ie` under the specific directory are processed.
* `-ie` specifies the input file extension. The default value `*` implies files with any extension. This option is used only when the input path `-i` points to a directory.
* `-oe` specifies the output file extension appended to each input filename. The corresponding output file, consisting of the NLP output, will be generated.
* `-format` specifies the format of the input file: `raw`, `line`, or `tsv` (see [data format](../supplements/data-format.md)).
* `-threads` specifies the number of threads to be used. When multi-threads are used, each file is assigned to an individual thread.

## Example

The following command takes [`emorynlp.txt`](../../src/test/resources/dat/emorynlp.txt) and generates [`emorynlp.txt.nlp`](../../src/test/resources/dat/emorynlp.txt.nlp) using [`config-decode-en.xml`](../../src/main/resources/edu/emory/mathcs/nlp/configuration/config-decode-en.xml).

```bash
$ java -Xmx8g -XX:+UseConcMarkSweepGC edu.emory.mathcs.nlp.bin.NLPDecode -c config-decode-general.xml -i emorynlp.txt
Loading ambiguity classes: 408397
Loading word clusters: 594491
Loading word embeddings: 1067831
Loading named entity gazetteers
Loading tokenizer
Loading part-of-speech tagger
Loading morphological analyzer
Loading named entity recognizer
Loading dependency parser
Loading semantic role labeler

emorynlp.txt
```

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* The `-Dlog4j.configuration` option specifies the configuration file for [log4j](http://logging.apache.org/log4j/) (e.g., [`log4j.properties`](../../src/main/resources/edu/emory/mathcs/nlp/configuration/log4j.properties)).
* The output file is generated in the `tsv` format (see [data format](../supplements/data-format.md#tab-separated-values-format)).

## Configuration

Sample configuration files for decoding can be found here: [`config-decode-*`](../../src/main/resources/edu/emory/mathcs/nlp/configuration/).

```xml
<configuration>
    <tsv>
        <column index="0" field="form"/>
    </tsv>

    <lexica>
        <ambiguity_classes field="word_form_simplified_lowercase">en-ambiguity-classes-simplified-lowercase.xz</ambiguity_classes>
        <word_clusters field="word_form_simplified_lowercase">en-brown-clusters-simplified-lowercase.xz</word_clusters>
        <named_entity_gazetteers field="word_form_simplified">en-named-entity-gazetteers-simplified.xz</named_entity_gazetteers>
        <word_embeddings field="word_form_undigitalized">en-word-embeddings-undigitalized.xz</word_embeddings>
    </lexica>

    <models>
    	<pos>en-pos.xz</pos>
    	<ner>en-ner.xz</ner>
    	<dep>en-dep.xz</dep>
    	<srl>en-srl.xz</srl>
    </models>
</configuration>
```

* `<tsv>`: see [`configuration#tsv`](train.md#configuration). This does not need to be specified when `raw` or `sen` is used.
* `<lexica>`: see [`configuration#lexica`](train.md#configuration).
* `<models>` specifies the statistical model for each component (e.g., [english models](../supplements/english-lexica-models.md#models); see [`NLPMode`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/util/NLPMode.java)).