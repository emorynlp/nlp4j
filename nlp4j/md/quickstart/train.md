# Train

## Command

The following command trains an NLP component:

```
java edu.emory.mathcs.nlp.bin.NLPTrain -mode <string> -c <filename> -t <filepath> -d <filepath> [-f <integer> -m <filename> -p <filename> -te <string> -de <string>]

-c  <filename> : configuration file (required)
-m  <filename> : output model file (optional)
-p  <filename> : previously trained model file (optional)
-t  <filepath> : training path (required)
-d  <filepath> : development path (optional)
-te   <string> : training file extension (default: *)
-de   <string> : development file extension (default: *)
-mode <string> : component mode (required: pos|ner|dep|srl)
```

* `-c` specifies the configuration file (see [configuration](#configuration)).
* `-m` specifies the output model file (saved in the [xz](http://tukaani.org) format). The model is not saved unless this option is set.
* `-p` specifies the previously trained model file. If this option is set, a new model is trained on top of the previous model.
* `-t|d` specifies the training or development path pointing to either a file or a directory. When the path points to a file, only the specific file is trained. When the path points to a directory, all files with the file extension `-te|de` under the specific directory are trained. It is possible to train a model without using a development set by not setting the `-d` option (see the example below).
* `-te|de` specifies the training or development file extension. The default value `*` implies files with any extension. This option is used only when the training or development path `-t|d` points to a directory.
* `-mode` specifies the NLP component to be trained:
 * `pos`: part-of-speech tagging.
 * `ner`: named entity recognition.
 * `dep`: dependency parsing.
 * `srl`: semantic role labeling.

## Example

The following command takes [`sample-trn.tsv`](../../src/main/resources/dat/sample-trn.tsv) and [`sample-dev.tsv`](../../src/main/resources/dat/sample-dev.tsv), and trains a dependency parsing model using [`config-train-sample.xml`](../../src/main/resources/configuration/config-train-sample.xml). Note that no model is saved after training because `-m` is not set.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config-train-sample.xml -t sample-trn.tsv -d sample-dev.tsv

AdaGrad Mini-batch
- Max epoch: 5
- Mini-batch: 1
- Learning rate: 0.02
- LOLS: fixed = 0, decaying rate = 0.95
- RDA: 1.0E-5

Training:
    1: LAS = 22.22, UAS = 26.98, L = 34, SF = 1300, NZW = 1867, N/S = 15750
    2: LAS = 34.92, UAS = 40.48, L = 34, SF = 1423, NZW = 4613, N/S = 42000
    3: LAS = 41.27, UAS = 46.03, L = 34, SF = 1449, NZW = 6297, N/S = 31500
    4: LAS = 38.10, UAS = 42.06, L = 34, SF = 1540, NZW = 7788, N/S = 31500
    5: LAS = 40.48, UAS = 45.24, L = 34, SF = 1597, NZW = 9087, N/S = 63000
 Best: 41.27, epoch = 3
```

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* Add [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to your classpath or put it under the directory you run (see [log4j](http://logging.apache.org/log4j/)).
 * `L`: number of labels.
 * `SF`: number of sparse features.
 * `NZW`: number of non-zero weights.
 * `N/S`: number of nodes processed per second. 

Once you figure out the optimized set of hyper-parameters, modify the values in the configuration file. In this case, we would modify the max epoch to `3` (see [`config-train-sample-optimized.xml`](../../src/main/resources/configuration/config-train-sample-optimized.xml#L18)). The following command takes [`sample-trn.tsv`](../../src/main/resources/dat/sample-trn.tsv), trains a dependency parsing model, and saves the final model to `dep.xz`. Note that the development set is not used for this training because `-d` is not set.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config-train-sample-optimized.xml -t sample-trn.tsv -m dep.xz

AdaGrad Mini-batch
- Max epoch: 3
- Mini-batch: 1
- Learning rate: 0.02
- LOLS: fixed = 0, decaying rate = 0.95
- RDA: 1.0E-5

Training:
    1: L = 34, SF = 1300, NZW = 1867
    2: L = 34, SF = 1423, NZW = 4613
    3: L = 34, SF = 1449, NZW = 6297
```

You should see the new file `dep.xz` created, which can be specified in the configuration file for dependency parsing (see [how to decode](decode.md)).

## Configuration

Sample configuration files for training can be found here: [`config-train-*`](../../src/main/resources/configuration/).

```xml
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
        <ambiguity_classes field="word_form_simplified_lowercase">en-ambiguity-classes-simplified-lowercase.xz</ambiguity_classes>
        <word_clusters field="word_form_simplified_lowercase">en-brown-clusters-simplified-lowercase.xz</word_clusters>
        <word_embeddings field="word_form_undigitalized">en-word-embeddings-undigitalized.xz</word_embeddings>
        <named_entity_gazetteers field="word_form_simplified">en-named-entity-gazetteers-simplified.xz</named_entity_gazetteers>
    </lexica>

    <optimizer>
        <algorithm>adagrad-mini-batch</algorithm>
        <l1_regularization>0.00001</l1_regularization>
        <learning_rate>0.02</learning_rate>
        <feature_cutoff>2</feature_cutoff>
        <lols fixed="0" decaying="0.95"/>
        <max_epochs>40</max_epochs>
        <batch_size>5</batch_size>
        <bias>0</bias>
    </optimizer>

    <feature_template>
        <feature f0="i:word_form"/>
        <feature f0="i+1:lemma"/>
        <feature f0="i-1:part_of_speech_tag"/>
        <feature f0="i_lmd:part_of_speech_tag"/>
        <feature f0="i-1:lemma" f1="i:lemma" f2="i+1:lemma"/>
    </feature_template>
</configuration>
```

* `<tsv>` specifies the configuration for [`TSVReader`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/util/TSVReader.java). `index` specifies the index of the field, starting at 0. `field` specifies the name of the field (e.g., [`sample-trn.tsv`](../../src/main/resources/dat/sample-trn.tsv)):
 * `form`&nbsp;&nbsp;&nbsp;&nbsp;: word form.
 * `lemma`&nbsp;&nbsp;: lemma.
 * `pos`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: part-of-speech tag.
 * `feats`&nbsp;&nbsp;: extra features.
 * `dhead`&nbsp;&nbsp;: dependency head ID.
 * `deprel`: dependency label.
 * `sheads`: semantic heads.
 * `nament`: named entity tag.

* `<lexica>` specifies the lexica used globally across multiple components (e.g., [english lexica](../supplements/english-lexica-models.md#lexica)). `field` specifies the type of word forms used to generate these lexica (see [`NLPNode::getValue`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java#L193)).
 * `ambiguity_classes`: ambiguity classes for part-of-speech tagging.
 * `word_clusters`: word clusters (e.g., brown clusters).
 * `word_embeddings`: word embeddings (e.g., [word2vec](http://word2vec.googlecode.com)).
 * `named_entity_gazetteers`: gazetteers for named entity recognition.

* `<optimizer>`specifies the optimizer to train a statistical model.
 * `algorithm`: perceptron, softmax, adagrad, agagrad-mini-batch, agadelta-mini-batch, agagrad-regression.
 * `l1_regularization`: the [RDA](http://www.jmlr.org/papers/volume11/xiao10a/xiao10a.pdf) regularization parameter used for `adagrad-*`.
 * `learning_rate`: the learning rate.
 * `feature_cutoff`: features appearing less than or equal to this cutoff are discarded from training.
 * `lols`: [locally optimal learning to search](http://jmlr.org/proceedings/papers/v37/changb15.pdf). <br>- `fixed`: use only gold labels for the specific number of epochs. <br>- `decaying`: decay the use of gold labels by the specific rate for every epoch.
 * `max_epochs`: the maximum number of epochs to be used for training.
 * `batch_size`: the number of sentences used to train `*-mini-batch`.
 * `bias`: the bias value.

* `<feature_template>` specifies the features used during training.

    ```xml
    <feature( f#="source(±window)?(_relation)?:field(:value)?")+/>
    ```

 * `f#`: `#` must start with 0. When multiple features are joined, they must be in a consecutive order.
 * `source`: see [`Source.java`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/feature/Source.java).
 * `window`: the context window with respect to the source.
 * `relation`: see [`Relation.java`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/feature/Relation.java).
 * `field`: see [`Field.java`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/feature/Field.java).
 * `value`: specifies the extra value of the field.