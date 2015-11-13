# How to Train

## Command

The following shows the command to train an NLP component.

```
java edu.emory.mathcs.nlp.bin.NLPTrain -c <filename> -t <filepath> -d <filepath> [-f <integer> -m <filename> -te <string> -de <string>]

-c <filename> : configuration file (required)
-f <integer>  : feature template ID (default: 0)
-m <filename> : output model file (optional)
-p <filename> : previously trained model file (optional)
-t <filepath> : training path (required)
-d <filepath> : development path (required)
-te <string>  : training file extension (default: *)
-de <string>  : development file extension (default: *)
```

* `-c` The default configuration: [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml) (see [below](#configuration) for more details).
* `-f` The default feature template, `0`, is defined in [`POSFeatureTemplate`](../../src/main/java/edu/emory/mathcs/nlp/component/pos/POSFeatureTemplate.java). You can define your own feature templates and declare them in [`NLPTrain`](../../src/main/java/edu/emory/mathcs/nlp/bin/NLPTrain.java), which is useful for feature engineering (developers only).
* `-m` If specified, the best statistical model is saved to the file as a compressed Java object.
* `-p` If specified, a previously trained model is used as the seed model.
* `-t|d` The training or development path can point to either a file or a directory. When the path points to a file, only the specific file is trained. When the path points to a directory, all files with the file extension `-te|de` under the specific directory are trained.
* `-te|de` The training or development file extensions specifies the extensions of the training and development files. The default value `*` implies files with any extension. This option is used only when the training or development path `-t|d` points to a directory.

The following command takes [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml) and [`sample.tsv`](../../src/main/resources/dat/sample.tsv) for both training and development, and saves the best statistical model to `pos.xz`.

```
$ java -Xmx1g -XX:+UseConcMarkSweepGC java edu.emory.mathcs.nlp.bin.NLPTrain -c config_train_pos.xml -t sample.tsv -d sample.tsv -m pos.xz
```

* Use the [`-XX:+UseConcMarkSweepGC`](http://www.oracle.com/technetwork/java/tuning-139912.html) option for JVM, which reduces the memory usage into a half.
* Add the log4j configuration file ([`log4j.properties`](../../src/main/resources/configuration/log4j.properties)) to your classpath.

## Configuration

The following shows the default configuration file: [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml).

```
<configuration>
    <language>english</language>

	<tsv>
        <column index="2" field="form"/>
        <column index="4" field="pos"/>
        <column index="5" field="feats"/>
    </tsv>

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
    
	<aggregate tolerance_delta="0.01" max_tolerance="5"/>
	<ambiguity_class_threshold>0.4</ambiguity_class_threshold>
</configuration>
```

* See [configuration](../specification/configuration.md#training) for details about the common fields.
* `ambiguity_class_threshold`: discard ambiguity classes whose likelihoods are less than or equal to this threshold.
