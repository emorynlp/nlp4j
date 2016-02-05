# English Models

## Lexica

All lexica can be found [here](src/main/resources/edu/emory/mathcs/nlp/english/lexica):

* `en-ambiguity-classes-simplified-lowercase.xz`<br>: ambiguity classes using simplified lowercase word forms.
* `en-brown-clusters-simplified-lowercase.xz`<br>: brown clusters using simplified lowercase word forms. 
* `en-ner-gazetteers-simplified-uncapitalized.xz`<br>: gazetteers for named entity recognition using uncapitalized simplified word forms.
* `en-ner-gazetteers-simplified.xz`<br>: gazetteers for named entity recognition using simplified word forms.
* `en-stop-words-simplified-uncapitalized.xz`<br>: stop words using uncapitalized simplified word forms.

## Models

All models can be found [here](src/main/resources/edu/emory/mathcs/nlp/english/models):

* `en-general-pos.xz`<br>: part-of-speech tagging model.
* `en-general-pos-uncapitalized.xz`<br>: part-of-speech tagging model using uncapitalized word forms.


## General Models

The general models are trained on [OntoNotes 5.0](https://catalog.ldc.upenn.edu/LDC2013T19), [English Web Treebank](https://catalog.ldc.upenn.edu/LDC2012T13), and [QuestionBank](http://www.computing.dcu.ie/~jjudge/qtreebank/).

| OntoNotes 5.0              | Sentence Counts | Token Counts |
| -------------------------- | --------------: | -----------: |
| Broadcasting conversations | 10,822          | 171,101      |
| Broadcasting news          | 10,344          | 206,020      |
| News magazines             | 6,672           | 163,627      |
| Newswires                  | 34,434          | 875,800      |
| Religious texts            | 21,418          | 296,432      |
| Telephone conversations    | 8,963           | 85,444       |
| Web texts                  | 12,447          | 284,951      |

| Engilsh Web Treebank | Sentence Counts | Token Counts |
| -------------------- | --------------: | -----------: |
| Answers              | 2,699           | 43,916       |
| Email                | 2,983           | 44,168       |
| Newsgroup            | 1,995           | 37,714       |
| Reviews              | 2,915           | 44,337       |
| Weblog               | 1,753           | 38,770       |

| QuestionBank | Sentence Counts | Token Counts |
| ------------ | --------------: | -----------: |
| Questions    | 3,199           | 29,715       |

## Medical Domain

The medical models are trained on [MiPACQ](http://clear.colorado.edu/compsem/index.php?page=endendsystems&sub=mipacq), [SHARP](http://informatics.mayo.edu/sharp/index.php/Main_Page), and [THYME](http://clear.colorado.edu/compsem/index.php?page=endendsystems&sub=temporal) corpora.

| MiPACQ              | Sentence Counts | Token Counts |
| ------------------- | --------------: | -----------: |
| Clinical questions  | 1,600           |  30,138      |
| Medpedia articles   | 2,796           |  49,922      |
| Clinical notes      | 8,383           | 113,164      |
| Pathological notes  | 1,205           |  21,353      |

| SHARP                      | Sentence Counts | Token Counts |
| -------------------------- | --------------: | -----------: |
| Seattle group health notes |  7,205          |  94,474      |
| Clinical notes             |  6,807          |  93,914      |
| Stratified                 |  4,320          |  43,536      |
| Stratified SGH             | 13,668          | 139,424      |

| THYME                          | Sentence Counts | Token Counts |
| ------------------------------ | --------------: | -----------: |
| Clinical & patheological notes | 26,734          | 388,371      |
| Braincancer                    | 18,700          | 225,486      |
 
## Bioinformatics Domain

The bioinformaitcs models are trained on [CRAFT](http://bionlp-corpora.sourceforge.net/CRAFT/) Treebank.

| CRAFT          | Sentence Counts | Token Counts |
| -------------- | --------------: | -----------: |
| Training data  | 16,297          |  452,769     |
