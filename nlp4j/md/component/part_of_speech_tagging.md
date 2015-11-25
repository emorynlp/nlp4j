# Part-of-Speech Tagging

Our part-of-speech tagger processes about 68K tokens per second on an Intel Xeon 2.30GHz machine and shows state-of-the-art accuracy (97.5% on the OntoNotes Treebank).

* [Intrinsic and Extrinsic Evaluations of Word Embeddings](), Michael Zhai, Johnny Tan, Jinho D. Choi, Proceedings of the AAAI 2015 Student Program, Phoenix, AZ, 2015.

* [Fast and Robust Part-of-Speech Tagging Using Dynamic Model Selection](http://aclweb.org/anthology-new/P/P12/P12-2071.pdf), Jinho D. Choi, Martha Palmer, Proceedings of the 50th Annual Meeting of the Association for Computational Linguistics (ACL'12), 363-367, Jeju, Korea, 2012.

## English Tagset

### Words

| Tag | Description | Tag | Description |
|---|---|---|---|
| ADD | Email | POS | Possessive ending |
| AFX | Affix | PRP | Personal pronoun |
| CC | Coordinating conjunction | PRP$ | Possessive pronoun  |
| CD | Cardinal number | RB | Adverb |
| CODE | Code ID | RBR | Adverb, comparative |
| DT | Determiner | RBS | Adverb, superlative |
| EX | Existential there | RP | Particle |
| FW | Foreign word | TO | To |
| GW | Go with | UH | Interjection |
| IN | Preposition or subordinating conjunction | VB | Verb, base form |
| JJ | Adjective | VBD | Verb, past tense |
| JJR | Adjective, comparative | VBG | Verb, gerund or present participle |
| JJS | Adjective, superlative | VBN | Verb, past participle |
| LS | List item marker | VBP | Verb, non-3rd person singular present |
| MD | Modal | VBZ | Verb, 3rd person singular present |
| NN | Noun, singular or mass | WDT | *Wh*-determiner |
| NNS | Noun, plural | WP | *Wh*-pronoun |
| NNP | Proper noun, singular | WP$ | *Wh*-pronoun, possessive |
| NNPS | Proper noun, plural | WRB | *Wh*-adverb |
| PDT | Predeterminer | XX | Unknown |

### Symbols

| Tag | Description | Tag | Description |
|---|---|---|---|
| $ | Dollar | -LRB- | Left bracket |
| : | Colon | -RRB- | Right bracket |
| , | Comma | HYPH | Hyphen |
| . | Period | NFP | Superfluous punctuation |
| `` | Left quote | SYM | Symbol |
| '' | Right quote | PUNC | General punctuation |

