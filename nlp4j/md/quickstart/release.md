# Release Notes

## Version 1.1.2 (06/29/2016)

* Bugfixes: [tokenization-issue-7](https://github.com/emorynlp/nlp4j-tokenization/issues/7)
* Features: [tokenization-issue-6](https://github.com/emorynlp/nlp4j-tokenization/issues/6)
* The tokenizer does not tokenize left/right brackets where the content inside is a single character or all numbers (e.g., `(a)`,`[12]`).

## Version 1.1.1 (04/29/2016)

* Bugfixes: [core-pull-7](https://github.com/emorynlp/nlp4j-core/pull/7).
* Features: [issue-3](https://github.com/emorynlp/nlp4j/issues/3/), [issue-6](https://github.com/emorynlp/nlp4j/issues/6).
* [NLPNode](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java) comes with several useful constructors.
* The `segmentize` method in [Tokenizer](https://github.com/emorynlp/nlp4j-tokenization/blob/master/src/main/java/edu/emory/mathcs/nlp/tokenization/Tokenizer.java) takes the generic type of [Token](https://github.com/emorynlp/nlp4j-tokenization/blob/master/src/main/java/edu/emory/mathcs/nlp/tokenization/Token.java).

## Version 1.1.0 (04/20/2016)

* All the statistical models are about twice smaller than the previous ones without compromising accuracy. The whole pipeline can be run in 4GB of RAM now.
* [Training](train.md) automatically saves the best model in a single pass (no need to run training twice any more to save the best model).
* The [nlp4j-common](https://github.com/emorynlp/nlp4j-common) project is separated out from the [nlp4j-core](https://github.com/emorynlp/nlp4j-core) project.
* [GlobalLexica](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/util/GlobalLexica.java) is no longer static, so it does not get conflicted by another process.
* [NLPNode](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java) extends [AbstractNLPNode](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/AbstractNLPNode.java), which allows to create your own custom node. Generics are added all over for this change (e.g., [NLPState](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/state/NLPState.java), [OnlineComponent](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/OnlineComponent.java)).
* The part-of-speech tagger gives the 2nd-best predictions when the best predictions have low confidence (`pos2` in the extra feats).
* Thanks to [Anatoly Vostryakov](https://github.com/avostryakov): [`adjective.exc`](https://github.com/emorynlp/nlp4j-morphology/blob/master/src/main/resources/edu/emory/mathcs/nlp/component/morph/english/adjective.exc) and [`adverb.base`](https://github.com/emorynlp/nlp4j-morphology/blob/master/src/main/resources/edu/emory/mathcs/nlp/component/morph/english/adverb.base) are cleaned up.
* Thanks to [spraynasal](https://github.com/spraynasal): some bugs in tokenization are fixed [5](https://github.com/emorynlp/nlp4j-tokenization/pull/5).

## Version 1.0.0 (02/24/2016)

* NLP4J is the successor of the widely used toolkit, [ClearNLP](https://github.com/clir/clearnlp), developed by the [NLP Research Group](http://nlp.mathcs.emory.edu) at Emory University. Please visit our [Github page](https://github.com/emorynlp/nlp4j) for more details about this project.
* This version supports tokenization, part-of-speech tagging, morphological analysis, named entity recognition, and dependency parsing. The next release (March, 2016) will include supports for semantic role labeling and sentiment analysis, and the following release (April, 2016) will include supports for coreference resolution.
* NLP4J makes it easy to train your own model. Please see [how to train](train.md) for more details about the training process.
* Calling the decoding API is easier than ever. See [NLPDemo](../../src/main/java/edu/emory/mathcs/nlp/bin/NLPDemo.java) for more details.
* The biggest difference between NLP4J and ClearNLP is in machine learning. NLP4J is capable of updating existing models with new training data, which is useful for domain adaptation. We also started implementing a deep learning package although we realized that the GPU support for Java is pretty limited and without a good GPU support, deep learning would make everything much slower. Please let us know if you'd like to contribute for this project.
* One could consider the NLP4J project is a more stabilized version of ClearNLP. I have been using this package for the NLP course I teach, and my students (including undergrads) were able to develop new NLP components without much effort using the built-in APIs in NLP4J. We are preparing a tutorial for developing NLP components using NLP4J.
* We do not expect our tools would work perfectly out of box. We now have a good team working on this project. Please let us know if you'd like to collaborate so we can make this project more robust for you.
* Please visit our [online demo](http://nlp.mathcs.emory.edu:8080/nlp4j). It parses 10K tokens with a couple of seconds and visualizes the dependency trees.
