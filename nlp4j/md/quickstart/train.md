# Train

## Command

The following shows the command for training an NLP component:

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
* `-m` specifies the output model file (saved in the [xz](http://tukaani.org) format). The model is not saved unless this option is specified.
* `-p` specifies the previously trained model file. If this option is specified, a new model is trained on top of the previous model.
* `-t|d` specifies the training or development path pointing to either a file or a directory. When the path points to a file, only the specific file is trained. When the path points to a directory, all files with the file extension `-te|de` under the directory are trained. Note that the training can be done without using a development set by not specifying the `-d` option (see the example below).
* `-te|de` specifies the training or development file extension. The default value `*` implies files with any extension. This option is used only when the training or development path `-t|d` points to a directory.
* `-mode` specifies the NLP component to be trained:
 * `pos`: part-of-speech tagging.
 * `ner`: named entity recognition.
 * `dep`: dependency parsing.
 * `srl`: semantic role labeling.

## Example

The following command takes [`sample_trn.tsv`](../../src/main/resources/dat/sample_trn.tsv) and [`sample_dev.tsv`](../../src/main/resources/dat/sample_dev.tsv), and trains a dependency parsing model with respect to [`config_train_sample.xml`](../../src/main/resources/configuration/config_train_sample.xml).

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config_train_sample.xml -t sample_trn.tsv -d sample_dev.tsv

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
* Add [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to your classpath (see [log4j](http://logging.apache.org/log4j/)).
 * `L`: number of labels.
 * `SF`: number of sparse features.
 * `NZW`: number of non-zero weights.
 * `N/S`: number of nodes processed per second. 

Once you figure out the optimized set of hyper-parameters, modify the values in the configuration file. In this case, we would modify the max epoch to `3` (see [`config_train_sample_optimized.xml`](../../src/main/resources/configuration/config_train_sample_optimized.xml#L18)). The following command takes [`sample_trn.tsv`](../../src/main/resources/dat/sample_trn.tsv), trains a dependency parsing model, and saves the final model to `dep.xz`.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config_train_sample_optimized.xml -t sample_trn.tsv -m dep.xz

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

## Configuration

Sample configuration files can be found [here](../../src/main/resources/configuration/):

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
        <column index="9" field="nament"/>
    </tsv>

    <lexica>
        <word_clusters field="word_form_simplified_lowercase">en-brown-clusters-simplified-lowercase.xz</word_clusters>
        <named_entity_gazetteers field="word_form_simplified">en-named-entity-gazetteers-simplified.xz</named_entity_gazetteers>
        <word_embeddings field="word_form_undigitalized">en-word-embeddings-undigitalized.xz</word_embeddings>
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

* `<tsv>` specifies the configuration for our [`TSVReader`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/util/TSVReader.java). `index` specifies the index of the field, starting at 0. `field` specifies the name of the field (e.g., [`sample_trn.tsv`](../src/main/resources/dat/sample_trn.tsv)):
 * `form`&nbsp;&nbsp;&nbsp;&nbsp;: word form.
 * `lemma`&nbsp;&nbsp;: lemma.
 * `pos`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: part-of-speech tag.
 * `feats`&nbsp;&nbsp;: extra features.
 * `dhead`&nbsp;&nbsp;: dependency head ID.
 * `deprel`: dependency label.
 * `sheads`: semantic heads.
 * `nament`: named entity tag.

* `<lexica>` specifies the lexica used globally across multiple components (e.g., [english-models](../supplements/english-models.md)). `field` specifies the type of word forms used to generate these lexica (see [`NLPNode::getValue`](https://github.com/emorynlp/corenlp/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java#L193)).
 * `word_clusters`: word clusters (e.g., brown clusters).
 * `named_entity_gazetteers`: gazetteers used for named entity recognition.
 * `word_embeddings`: word embeddings (e.g., [word2vec](http://word2vec.googlecode.com)).

* `optimizer`specifies the optimizer to train a statistical model.
 * `algorithm`: perceptron, softmax, adagrad, agagrad-mini-batch, agadelta-mini-batch, agagrad-regression.
 * `l1_regularization`: the [RDA](http://www.jmlr.org/papers/volume11/xiao10a/xiao10a.pdf) regularization parameter used for `adagrad-*`.
 * `learning_rate`: the learning rate.
 * `feature_cutoff`: features appearing less than or equal to this cutoff are discarded from training.
 * `lols`: [locally optimal learning to search](http://jmlr.org/proceedings/papers/v37/changb15.pdf). `fixed` - use only gold labels for this number of epochs, `decaying` - decay the use of gold labels by this rate for every epoch.
 * `max_epochs`: the maximum number of epochs to be used for training.
 * `batch_size`: the number of sentences used to train `*-mini-batch`.
 * `bias`: the bias value.

* `feature_template` specifies the features used during training.

    ```
    <feature( f#="source(&plusmn;window)?(_relation)?:field(:value)?")+/>
    ```

 * `f#`: `#` must start with `0`. When multiple features are joined, they must be in a consecutive order.
 * `source`: 

