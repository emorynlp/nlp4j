# English

## Lexica

All lexica can be found [here](https://bitbucket.org/emorynlp/nlp4j-english/src/fc6cf377142cb554ab74c7b6377eff6d28e43620/src/main/resources/edu/emory/mathcs/nlp/lexica/?at=master):

* `en-ambiguity-classes-simplified.xz`<br>: ambiguity classes for part-of-speech tagging with simplified word forms.
* `en-ambiguity-classes-simplified-lowercase.xz`<br>: ambiguity classes for part-of-speech tagging with simplified lowercase word forms.
* `en-brown-clusters-simplified-lowercase.xz`<br>: brown clusters with simplified lowercase word forms. 
* `en-named-entity-gazetteers-simplified.xz`<br>: gazetteers for named entity recognition with simplified word forms.
* `en-named-entity-gazetteers-simplified-lowercase.xz`<br>: gazetteers for named entity recognition with simplified lowercase word forms.
* `en-stop-words-simplified-lowercase.xz`<br>: stop words with simplified lowercase word forms.
* `en-word-embeddings-undigitalized.xz`<br>: word embeddings with undigitalized word forms.

## Models

All models can be found [here](https://bitbucket.org/emorynlp/nlp4j-english/src/fc6cf377142cb554ab74c7b6377eff6d28e43620/src/main/resources/edu/emory/mathcs/nlp/models/?at=master):

* `en-pos.xz`: part-of-speech tagging.
* `en-ner.xz`: named entity recognition.
* `en-dep.xz`: dependency parsing.
* `en-srl.xz`: semantic role labeling.

Models are trained on the following corpora.

| [OntoNotes 5.0](https://catalog.ldc.upenn.edu/LDC2013T19) | Sentences | Tokens | Names |
| -------------------------- | -----: | ------: | -----: |
| Broadcasting conversations | 10,822 | 171,101 |  9,771 |
| Broadcasting news          | 10,344 | 206,029 | 19,670 | 
| News magazines             |  6,672 | 163,627 | 10,736 |
| Newswires                  | 34,438 | 875,800 | 77,496 |
| Religious texts            | 21,418 | 296,432 |      0 |
| Telephone conversations    |  8,963 |  85,444 |  2,021 |
| Web texts                  | 12,448 | 284,951 |  8,170 |

| &nbsp;&nbsp;&nbsp;[English Web Treebank](https://catalog.ldc.upenn.edu/LDC2012T13)&nbsp;&nbsp;&nbsp; | Sentences | Tokens |
| --------- | ----: | -----: |
| Answers   | 2,699 | 43,916 |
| Email     | 2,983 | 44,168 |
| Newsgroup | 1,996 | 37,816 |
| Reviews   | 2,915 | 44,337 |
| Weblog    | 1,753 | 38,770 |

| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[QuestionBank](http://www.computing.dcu.ie/~jjudge/qtreebank/)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Sentences | Tokens |
| --------- | ----: | -----: |
| Questions | 3,198 | 29,704 |

| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[MiPACQ](http://clear.colorado.edu/compsem/index.php?page=endendsystems&sub=mipacq)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Sentences | Tokens |
| ------------------- | --------------: | -----------: |
| Clinical questions  | 1,600           |  30,138      |
| Medpedia articles   | 2,796           |  49,922      |
| Clinical notes      | 8,383           | 113,164      |
| Pathological notes  | 1,205           |  21,353      |

| [SHARP](http://informatics.mayo.edu/sharp/index.php/Main_Page) | Sentences | Tokens |
| -------------------------------------- | -----: | ------: |
| Seattle group health notes&nbsp;&nbsp; |  7,204 |  94,450 |
| Clinical notes                         |  6,807 |  93,914 |
| Stratified                             |  4,320 |  43,536 |
| Stratified SGH                         | 13,662 | 139,403 |

| [THYME](http://clear.colorado.edu/compsem/index.php?page=endendsystems&sub=temporal) | Sentences | Tokens |
| ----------------------------- | -----: | ------: |
| Clinical / pathological notes | 26,661 | 387,943 |
| Brain cancer                  | 18,722 | 225,899 |
