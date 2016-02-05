# Train

## Command

The followings show the command to train an NLP component:

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

The following command takes [`sample_trn.tsv`](../../src/main/resources/dat/sample_trn.tsv) and [`sample_dev.tsv`](../../src/main/resources/dat/sample_dev.tsv), and trains a dependency parsing model with respect to [`config_train_sample.xml`](../../src/main/resources/configuration/config_train_sample.xml).

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* Add [`log4j.properties`](../../src/main/resources/configuration/log4j.properties) to your classpath (see [log4j](http://logging.apache.org/log4j/)).

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config_train_sample.xml -t sample_trn.tsv -d sample_dev.tsv
```

```
AdaGrad Mini-batch
- Max epoch: 5
- Mini-batch: 1
- Learning rate: 0.02
- LOLS: fixed = 0, decaying rate = 0.95
- RDA: 1.0E-5

Training:
    1: LAS = 26.92, UAS = 35.90, L = 29, SF = 1108, NZW =  835, N/S = 14182
    2: LAS = 40.38, UAS = 49.36, L = 29, SF = 1162, NZW = 3399, N/S = 39000
    3: LAS = 47.44, UAS = 54.49, L = 29, SF = 1199, NZW = 4804, N/S = 26000
    4: LAS = 46.15, UAS = 52.56, L = 29, SF = 1245, NZW = 6062, N/S = 39000
    5: LAS = 47.44, UAS = 55.13, L = 29, SF = 1301, NZW = 7215, N/S = 78000
 Best: 47.44, epoch = 3

```

Once you figure out the optimized set of hyper-parameters, modify the values in the configuration file. In this case, we would modify the max epoch to 3 (see [`config_train_sample_optimized.xml`](../../src/main/resources/configuration/config_train_sample_optimized.xml#L18)). The following command takes [`sample_trn.tsv`](../../src/main/resources/dat/sample_trn.tsv), trains a dependency parsing model, and saves the final model to `dep.xz`.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -mode dep -c config_train_sample_optimized.xml -t sample_trn.tsv -m dep.xz
```

```
AdaGrad Mini-batch
- Max epoch: 3
- Mini-batch: 1
- Learning rate: 0.02
- LOLS: fixed = 0, decaying rate = 0.95
- RDA: 1.0E-5

Training:
    1: L = 29, SF = 1108, NZW =  835
    2: L = 29, SF = 1162, NZW = 3399
    3: L = 29, SF = 1199, NZW = 4804
```

## Configuration

Sample configuration files can be found [here](../src/main/resources/configuration/).

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
        <word_clusters field="uncapitalized_simplified_word_form">en-brown-clusters-simplified-uncapitalized.xz</word_clusters>
        <named_entity_gazetteers field="uncapitalized_simplified_word_form">en-ner-gazetteers-simplified-uncapitalized.xz</named_entity_gazetteers>
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

* `<tsv>` specifies the configuration for our [TSV reader](../src/main/java/edu/emory/mathcs/nlp/component/template/reader/TSVReader.java). `index` specifies the index of the field, starting at 0. `field` specifies the name of the field (e.g., [`sample.tsv`](../src/main/resources/dat/sample.tsv)):
 * `form`&nbsp;&nbsp;&nbsp;&nbsp;: word form.
 * `lemma`&nbsp;&nbsp;: lemma.
 * `pos`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: part-of-speech tag.
 * `feats`&nbsp;&nbsp;: extra features.
 * `dhead`&nbsp;&nbsp;: dependency head ID.
 * `deprel`: dependency label.
 * `sheads`: semantic heads.
 * `nament`: named entity tag.

* `<lexica>` specifies the lexica used globally across multiple components (see [english-lexica](https://github.com/emorynlp/english-models)). `field` specifies the type of word forms used to generate these lexica (see [`NLPNode::getValue`](../src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java#L174)).
 * `word_clusters`: word clusters (e.g., brown clusters).
 * `named_entity_gazetteers`: gazetteers used for named entity recognition.

* `optimizer`specifies the optimizer to train a statistical model.
 * `algorithm`: perceptron, softmax, adagrad, agagrad-mini-batch, agadelta-mini-batch, agagrad-regression.
 * `l1_regularization`: the [RDA](http://www.jmlr.org/papers/volume11/xiao10a/xiao10a.pdf) regularization parameter used for `adagrad-*`.
 * `learning_rate`: the learning rate.
 * `max_epochs`: the maximum number of epochs to be used for training.
 * `batch_size`: the number of sentences used to train `*-mini-batch`.
 * `roll_in`: the rate of using zero-cost labels (see [this paper](http://jmlr.org/proceedings/papers/v37/changb15.pdf) for more details).
 * `bias`: the bias value.

### Part-of-Speech Tagging

* `<ambiguity_class_threshold>` ambiguity classes whose likelihoods are less than or equal to this threshold will be discarded during training.

## Feature Template

Each component comes with the default feature template indicated by the `-f 0` option. The followings show the steps to create and apply your own feature template.

1. Clone this repository: `git clone https://github.com/emorynlp/component.git`.
1. Go to the package for the component you want to create a feature template. Each component has its own package under [`component`](../src/main/java/edu/emory/mathcs/nlp/component). For instance, the part-of-speech tagger is implemented in [`component/pos`](../src/main/java/edu/emory/mathcs/nlp/component/pos/). Go to the feature package under the component (e.g., [`component/pos/feature`](../src/main/java/edu/emory/mathcs/nlp/component/pos/feature/)).
1. Create a feature template by copying one of the default templates (e.g., [`POSFeatureTemplate0`](../src/main/java/edu/emory/mathcs/nlp/component/pos/feature/POSFeatureTemplate0.java)). Modify the template to add or remove features.
1. Add the new feature template to the component's trainer (e.g, [`POSTrainer`](../src/main/java/edu/emory/mathcs/nlp/component/pos/POSTrainer.java)). Add your template to the `createFeatureTemplate(int)` method with a unique ID.
1. Run [`NLPTrain`](../src/main/java/edu/emory/mathcs/nlp/bin/NLPTrain.java) using the `-f ID` option, where `ID` is specified for the new feature template.