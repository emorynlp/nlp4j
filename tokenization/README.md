# Tokenization

Our tokenizer takes a raw text and splits tokens by their morphological aspects. It also groups tokens into sentences. Our tokenizer is based on the [LDC](https://www.ldc.upenn.edu/) tokenizer used for creating English Treebanks although it uses more robust heuristics. Here are some key features about our tokenizer.

* Emoticons are recognized as one unit (e.g., `:-)`, `^_^`).
* Hyperlinks are recognized as one unit (`emory.edu`, `jinho@emory.edu`, `index.html`).
* Numbers consisting of punctuation are recognized as one unit (e.g., `0.1`, `2/3`).
* Repeated punctuation are grouped together (e.g., `---`, `...`).
* Abbreviations are recognized as one unit (e.g., `Prof.`, `Ph.D`).
* File extensions are not tokenized (e.g., `clearnlp.zip`, `tokenizer.doc`).
* Units are tokenized (e.g., `1 kg`, `2 cm`).
* Usernames including periods are recognized as one unit (e.g., `jinho.choi`).

## API

[`TokenizerDemo`](src/test/java/edu/emory/mathcs/nlp/tokenization/TokenizerDemo.java) shows how the tokenizer can be used in APIs.