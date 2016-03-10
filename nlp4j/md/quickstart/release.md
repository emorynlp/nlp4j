# Release Notes

## Version 1.1.0

* The sentiment analyzer for twits is added.
* The morphological analyzer lemmatizes `'s` into `be` and `have` now.
* Thanks to [Anatoly Vostryakov](https://github.com/avostryakov). [`adjective.exc`](https://github.com/emorynlp/nlp4j-morphology/blob/master/src/main/resources/edu/emory/mathcs/nlp/component/morph/english/adjective.exc) and [`averb.base`](https://github.com/emorynlp/nlp4j-morphology/blob/master/src/main/resources/edu/emory/mathcs/nlp/component/morph/english/adverb.base) are cleaned up.
* The POS tagger gives the 2nd-best predictions when the best predictions have low confidence (`pos2` in the extra feats).
* Global lexica are no longer static.

## Version 1.0.0 (02/24/2016)

* NLP4J is the successor of the widely used toolkit, [ClearNLP](https://github.com/clir/clearnlp), developed by the [NLP Research Group](http://nlp.mathcs.emory.edu) at Emory University. Please visit our [Github page](https://github.com/emorynlp/nlp4j) for more details about this project.
* This version supports tokenization, part-of-speech tagging, morphological analysis, named entity recognition, and dependency parsing. The next release (March, 2016) will include supports for semantic role labeling and sentiment analysis, and the following release (April, 2016) will include supports for coreference resolution.
* NLP4J makes it easy to train your own model. Please see [how to train](train.md) for more details about the training process.
* Calling the decoding API is easier than ever. See [NLPDemo](../../src/main/java/edu/emory/mathcs/nlp/bin/NLPDemo.java) for more details.
* The biggest difference between NLP4J and ClearNLP is in machine learning. NLP4J is capable of updating existing models with new training data, which is useful for domain adaptation. We also started implementing a deep learning package although we realized that the GPU support for Java is pretty limited and without a good GPU support, deep learning would make everything much slower. Please let us know if you'd like to contribute for this project.
* One could consider the NLP4J project is a more stabilized version of ClearNLP. I have been using this package for the NLP course I teach, and my students (including undergrads) were able to develop new NLP components without much effort using the built-in APIs in NLP4J. We are preparing a tutorial for developing NLP components using NLP4J.
* We do not expect our tools would work perfectly out of box. We now have a good team working on this project. Please let us know if you'd like to collaborate so we can make this project more robust for you.
* Please visit our [online demo](http://nlp.mathcs.emory.edu:8080/nlp4j). It parses 10K tokens with a couple of seconds and visualizes the dependency trees.
