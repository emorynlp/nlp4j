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


## Decoding

```
java edu.emory.mathcs.nlp.bin.Tokenize -i <filepath> [-l <language> -ie <string> -oe <string> -line]

-i       <filepath> : input path (required)
-l       <language> : language (default: english)
-ie      <regex>    : input file extension (default: *)
-oe      <string>   : output file extension (default: tok)
-line    <boolean>  : if set, treat each line as one sentence
-format  <string>   : format of the input data (raw|line; default: raw)
-threads <integer>  : number of threads (default: 2)
```
	 
* The input path `-i` can point to either a file or a directory. When the input path points to a file, only the specific file is processed. When the input path points to a directory, all files with the input file extension `-ie` under the specific directory are processed.
* The langauge `-l` specifies the input language. Currently, our tokenizer supports only English.
* The input file extension `-ie` specifies the extension of the input files. The default value `*` implies files with any extension. This option is used only when the input path `-i` points to a directory.
* The output file extension `-oe` is appended to each input filename, and generates the corresponding output file.
* The input format `-format` takes either the raw text (`raw`) or one sentence per line (`line`).
* When he number of threads `-threads > 1`, files are distributed into multiple threads. 

The following command takes the input file [`emorynlp-raw.txt`](src/test/resources/emorynlp-raw.txt), and generates the output file [`emorynlp-raw.txt.tok`](src/test/resources/emorynlp-raw.txt.tok), where each sentence is in a separate line.

```
java edu.emory.mathcs.nlp.bin.Tokenize -i emorynlp-raw.txt
```

## API

[`TokenizerDemo`](src/test/java/edu/emory/mathcs/nlp/tokenizer/TokenizerDemo.java) shows how the tokenizer can be used in APIs.