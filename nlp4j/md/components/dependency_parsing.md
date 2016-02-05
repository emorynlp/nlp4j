# Dependency Parsing

Our dependency parser processes over 13K tokens per second on an Intel Xeon 2.30GHz machine, and shows state-of-the-art accuracy for greedy parsing (92.1% on the WSJ corpus).

* [It Depends: Dependency Parser Comparison Using A Web-based Evaluation Tool](http://www.aclweb.org/anthology/P15-1038.pdf), Jinho D. Choi, Amanda Stent, Joel Tetreault, Proceedings of the 53rd Annual Meeting of the Association for Computational Linguistics (ACL'15), 387–396, Beijing, China, 2015.
* [Transition-based Dependency Parsing with Selectional Branching](http://aclweb.org/anthology/P/P13/P13-1104.pdf), Jinho D. Choi, Andrew McCallum, Proceedings of the 51st Annual Meeting of the Association for Computational Linguistics (ACL'13), 1052-1062, Sofia, Bulgaria, 2013.
* [Getting the Most out of Transition-based Dependency Parsing](http://aclweb.org/anthology-new/P/P11/P11-2121.pdf), Jinho D. Choi, Martha Palmer, Proceedings of the 49th Annual Meeting of the Association for Computational Linguistics: Human Language Technologies (ACL:HLT'11), 687-692, Portland, Oregon, 2011.

## English Tagset

| Label         | Description                       | Since |
|:------------- |:----------------------------------|:-----:|
| ACL           | Clausal modifier of noun          | 1.0.0 |
| ACOMP         | Adjectival complement             | 1.0.0 |
| ADVCL         | Adverbial clause modifier         | 1.0.0 |
| ADVMOD        | Adverbial modifier                | 1.0.0 |
| AGENT         | Agent                             | 1.0.0 |
| AMOD          | Adjectival modifier               | 1.0.0 |
| APPOS         | Appositional modifier             | 1.0.0 |
| ATTR          | Attribute                         | 1.0.0 |
| AUX           | Auxiliary                         | 1.0.0 |
| AUXPASS       | Auxiliary (passive)               | 1.0.0 |
| CASE          | Case marker                       | 1.0.0 |
| CC            | Coordinating conjunction          | 1.0.0 |
| CCOMP         | Clausal complement                | 1.0.0 |
| COMPOUND      | Compound modifier                 | 1.0.0 |
| CONJ          | Conjunct                          | 1.0.0 |
| CSUBJ         | Clausal subject                   | 1.0.0 |
| CSUBJPASS     | Clausal subject (passive)         | 1.0.0 |
| DATIVE        | Dative                            | 1.0.0 |
| DEP           | Unclassified dependent            | 1.0.0 |
| DET           | Determiner                        | 1.0.0 |
| DOBJ          | Direct Object                     | 1.0.0 |
| EXPL          | Expletive                         | 1.0.0 |
| INTJ          | Interjection                      | 1.0.0 |
| MARK          | Marker                            | 1.0.0 |
| META          | Meta modifier                     | 1.0.0 |
| NEG           | Negation modifier                 | 1.0.0 |
| NOUNMOD       | Modifier of nominal               | 1.0.0 |
| NPMOD         | Noun phrase as adverbial modifier | 1.0.0 |
| NSUBJ         | Nominal subject                   | 1.0.0 |
| NSUBJPASS     | Nominal subject (passive)         | 1.0.0 |
| NUMMOD        | Number modifier                   | 1.0.0 |
| OPRD          | Object predicate                  | 1.0.0 |
| PARATAXIS     | Parataxis                         | 1.0.0 |
| PCOMP         | Complement of preposition         | 1.0.0 |
| POBJ          | Object of preposition             | 1.0.0 |
| POSS          | Possession modifier               | 1.0.0 |
| PRECONJ       | Pre-correlative conjunction       | 1.0.0 |
| PREDET        | Pre-determiner                    | 1.0.0 |
| PREP          | Prepositional modifier            | 1.0.0 |
| PRT           | Particle                          | 1.0.0 |
| PUNCT         | Punctuation                       | 1.0.0 |
| QUANTMOD      | Modifier of quantifier            | 1.0.0 |
| RELCL         | Relative clause modifier          | 1.0.0 |
| ROOT          | Root                              | 1.0.0 |
| XCOMP         | Open clausal complement           | 1.0.0 |