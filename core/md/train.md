# How to Train

## Command

The following shows the command to train an NLP component.

```
java edu.emory.mathcs.nlp.bin.NLPTrain -mode <string> -c <filename> -t <filepath> -d <filepath> [-f <integer> -m <filename> -p <filename> -te <string> -de <string>]

-mode <string> : component mode (required: pos|ner|dep|srl)
-c  <filename> : configuration file (required)
-f   <integer> : feature template ID (default: 0)
-m  <filename> : output model file (optional)
-p  <filename> : previously trained model file (optional)
-t  <filepath> : training path (required)
-d  <filepath> : development path (required)
-te   <string> : training file extension (default: *)
-de   <string> : development file extension (default: *)
```

* `-mode` specifies the kind of statistical model to be trained:
 * `pos`: part-of-speech tagging.
 * `ner`: named entity recognition.
 * `dep`: dependency parsing.
 * `srl`: semantic role labeling.
* `-c` specifies the configuration file (see [configuration](#configuration)).
* `-f` specifies the feature template ID (developers only; see [feature template](feature-template)).
* `-m` specifies the model file to be saved in the [xz](http://tukaani.org) format; if not specified, the model is not saved.
* `-p` specifies the previously trained model used as the seed model.
* `-t|d` specifies the training or development path pointing to either a file or a directory. When the path points to a file, only the specific file is trained. When the path points to a directory, all files with the file extension `-te|de` under the specific directory are trained.
* `-te|de` specifies the training or development file extension. The default value `*` implies files with any extension. This option is used only when the training or development path `-t|d` points to a directory.

The following command takes [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml) and [`sample.tsv`](../../src/main/resources/dat/sample.tsv) for both training and development, and saves the best statistical model to `pos.xz`.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode pos -c config_train_pos.xml -t sample.tsv -d sample.tsv -m pos.xz
```

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* Add the log4j configuration file ([`log4j.properties`](../../src/main/resources/configuration/log4j.properties)) to your classpath.

## Configuration

Sample configuration files can be found [here](../../src/main/resources/configuration/).

```
<configuration>
    <tsv>
        <column index="1" field="form"/>
        <column index="2" field="lemma"/>
        <column index="3" field="pos"/>
        <column index="4" field="feats"/>
        <column index="5" field="dhead"/>
        <column index="6" field="deprel"/>
        <column index="7" field="sheads"/>
        <column index="8" field="nament"/>
    </tsv>

    <lexica>
        <word_clusters field="uncapitalized_simplified_word_form">word_clusters_english.xz</word_clusters>
        <named_entity_gazetteers field="uncapitalized_simplified_word_form">named_entity_gazetteers_english.xz</named_entity_gazetteers>
    </lexica>

    <optimizer>
        <algorithm>adagrad-mini-batch</algorithm>
        <l1_regularization>0.00001</l1_regularization>
        <learning_rate>0.02</learning_rate>
        <max_epochs>40</max_epochs>
        <batch_size>5</batch_size>
        <roll_in>0.95</roll_in>
        <bias>0</bias>
    </optimizer>
</configuration>
```

* See [configuration](../specification/configuration.md#training) for details about the common fields.
* `ambiguity_class_threshold`: discard ambiguity classes whose likelihoods are less than or equal to this threshold.

## Feature Template

, `0`, is defined in [`POSFeatureTemplate`](../../src/main/java/edu/emory/mathcs/nlp/component/pos/POSFeatureTemplate.java). You can define your own feature templates and declare them in [`NLPTrain`](../../src/main/java/edu/emory/mathcs/nlp/bin/NLPTrain.java), which is useful for feature engineering

  The following shows the default configuration file: [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml).