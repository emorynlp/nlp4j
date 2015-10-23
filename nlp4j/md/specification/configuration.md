# Configuration

## Training

The following describes the common fields in the [configuration files](../../src/main/resources/configuration/) used for training.

| Field | Description |
| :-----: | :---------- |
| `<language>` | Specifies the [language](https://github.com/emorynlp/common/blob/master/src/main/java/edu/emory/mathcs/nlp/common/util/Language.java) of the input data. |
| `<tsv>` | Specifies the [Tab-Separated-Values](https://en.wikipedia.org/wiki/Tab-separated_values) format used in the input data. |
| `<column>` | Specifies the columns in TSV.<ul><li>`index` specifies the index of the field, starting at `0`.</li><li>`field` specifies the name of the field.</li>&#9702; `id`: node ID.<br>&#9702; `form`: word form.<br>&#9702; `lemma`: lemma.<br>&#9702; `pos`: part-of-speech tag.<br>&#9702; `feats`: pre-defined features.<br>&#9702; `headID`: head node ID.<br>&#9702; `deprel`: dependency label.<br>&#9702; `nament`: named entity tag.<br>&#9702; `sheads`: semantic heads.</ul> |
| `<optimizer>` | Specifies the optimizer and its parameters for training (see [below](#optimizers)).<ul><li>`algorithm`: the name of the optimization algorithm.</li><li>`label_cutoff`: discard labels appearing less than this cutoff.</li><li>`feature_cutoff`: discard features appearing less than this cutoff.</li><li>`reset_weights`: if `true`, reset the weight vector to `0` before self-training.</li><li>`thread_size`: the number of threads (for one-vs-all learning).</li><li>`average`: if `true`, return the averaged weight vector (for online learning).</li><li>`learning_rate`: the learning rate.</li><li>`bias`: the bias weight.</li><li>`batch_ratio`: the portion of each mini-batch (e.g., use every 10% as a mini-batch).</li></ul>| 
| `<aggregate>` | If set, use disjoint aggregation (DAGGER).<ul><li>`tolerance`: tolerance of termination criterion.</li></ul> | 

## Optimizers

### Perceptron

```
<optimizer>
    <algorithm>perceptron</algorithm>
    <label_cutoff>4</label_cutoff>
    <feature_cutoff>3</feature_cutoff>
    <reset_weights>false</reset_weights>
    <average>false</average>
    <learning_rate>0.01</learning_rate>
    <bias>0</bias>
</optimizer>
```
* [Discriminative Training Methods for Hidden Markov Models: Theory and Experiments with Perceptron Algorithms](http://www.aclweb.org/anthology/W02-1001), Michael Collins, EMNLP, 2002.

### AdaGrad

```
<optimizer>
    <algorithm>adagrad</algorithm>
    <label_cutoff>4</label_cutoff>
    <feature_cutoff>3</feature_cutoff>
    <reset_weights>false</reset_weights>
    <average>false</average>
    <learning_rate>0.01</learning_rate>
    <bias>0</bias>
</optimizer>
```
* [Adaptive Subgradient Methods for Online Learning and Stochastic Optimization](http://www.jmlr.org/papers/volume12/duchi11a/duchi11a.pdf), John Duchi et. al., JMLR, 2012.

### AdaGrad with Mini-Batch

```
<optimizer>
    <algorithm>adagrad-mini-batch</algorithm>
    <label_cutoff>4</label_cutoff>
    <feature_cutoff>3</feature_cutoff>
    <reset_weights>false</reset_weights>
    <average>false</average>
    <batch_ratio>0.1</batch_ratio>
    <learning_rate>0.1</learning_rate>
    <bias>0</bias>
</optimizer>
```

### AdaDelta with Mini-Batch

```
<optimizer>
    <algorithm>adadelta-mini-batch</algorithm>
    <label_cutoff>4</label_cutoff>
    <feature_cutoff>3</feature_cutoff>
    <reset_weights>false</reset_weights>
    <average>false</average>
    <batch_ratio>0.1</batch_ratio>
    <learning_rate>0.01</learning_rate>
    <decaying_rate>0.4</decaying_rate>
    <bias>0</bias>
</optimizer>
```

* [ADADELTA: An Adaptive Learning Rate Method](http://arxiv.org/pdf/1212.5701.pdf), Matthew D. Zeiler, arXiv:1212.5701, 2012.

### Liblinear L2-regularized Support Vector Classification

```
<optimizer>
    <algorithm>liblinear-l2-svc</algorithm>
    <label_cutoff>4</label_cutoff>
    <feature_cutoff>3</feature_cutoff>
    <reset_weights>false</reset_weights>
    <thread_size>2</thread_size>
    <loss_type>1</loss_type>
    <cost>0.1</cost>
    <tolerance>0.01</tolerance>
    <bias>0</bias>
</optimizer>
```

* [A Dual Coordinate Descent Method for Large-scale Linear SVM](http://icml2008.cs.helsinki.fi/papers/166.pdf), Cho-Jui Hsieh et. al., ICML, 2008.