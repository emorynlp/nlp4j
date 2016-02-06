# Dependency Parsing

Our dependency parser processes over 13K tokens per second on an Intel Xeon 2.30GHz machine, and shows the near state-of-the-art accuracy for greedy parsing (92.1% on the WSJ corpus).

* [It Depends: Dependency Parser Comparison Using A Web-based Evaluation Tool](http://www.aclweb.org/anthology/P15-1038.pdf), Jinho D. Choi, Amanda Stent, Joel Tetreault, Proceedings of the 53rd Annual Meeting of the Association for Computational Linguistics (ACL'15), 387–396, Beijing, China, 2015.
* [Transition-based Dependency Parsing with Selectional Branching](http://aclweb.org/anthology/P/P13/P13-1104.pdf), Jinho D. Choi, Andrew McCallum, Proceedings of the 51st Annual Meeting of the Association for Computational Linguistics (ACL'13), 1052-1062, Sofia, Bulgaria, 2013.
* [Getting the Most out of Transition-based Dependency Parsing](http://aclweb.org/anthology-new/P/P11/P11-2121.pdf), Jinho D. Choi, Martha Palmer, Proceedings of the 49th Annual Meeting of the Association for Computational Linguistics: Human Language Technologies (ACL:HLT'11), 687-692, Portland, Oregon, 2011.

## English Labels

| Label       | Description | Version |
| ----------- | ----------- | ------- |
| `acl`       | Clausal modifier of noun | 1.0.0 |
| `acomp`     | Adjectival complement | 1.0.0 |
| `advcl`     | Adverbial clause modifier | 1.0.0 |
| `advmod`    | Adverbial modifier | 1.0.0 |
| `agent`     | Agent (passive) | 1.0.0 |
| `appos`     | Appositional modifier | 1.0.0 |
| `attr`      | Attribute | 1.0.0 |
| `aux`       | Auxiliary verb | 1.0.0 |
| `auxpass`   | Auxiliary verb (passive) | 1.0.0 |
| `case`      | Case marker | 1.0.0 |
| `cc`        | Coordinating conjunction | 1.0.0 |
| `ccomp`     | Clausal complement | 1.0.0 |
| `compound`  | Compound word | 1.0.0 |
| `conj`      | Conjunct | 1.0.0 |
| `csubj`     | Clausal subject | 1.0.0 |
| `csubjpass` | Clausal subject (passive) | 1.0.0 |
| `dative`    | Dative | 1.0.0 |
| `dep`       | Unclassified dependent | 1.0.0 |
| `det`       | Determiner | 1.0.0 |
| `discourse` | Discourse element | 1.0.0 |
| `dobj`      | Direct Object | 1.0.0 |
| `expl`      | Expletive | 1.0.0 |
| `mark`      | Marker | 1.0.0 |
| `meta`      | Meta data | 1.0.0 |
| `neg`       | Negation modifier | 1.0.0 |
| `nmod`      | Modifier of nominal | 1.0.0 |
| `npadvmod`  | Noun phrase as adverbial modifier | 1.0.0 |
| `nsubj`     | Nominal subject | 1.0.0 |
| `nsubjpass` | Nominal subject (passive) | 1.0.0 |
| `oprd`      | Object predicate | 1.0.0 |
| `parataxis` | Parataxis | 1.0.0 |
| `pcomp`     | Preposition complement | 1.0.0 |
| `pobj`      | Preposition object | 1.0.0 |
| `poss`      | Possession modifier | 1.0.0 |
| `preconj`   | Precorrelative conjunction | 1.0.0 |
| `predet`    | Predeterminer | 1.0.0 |
| `prep`      | Prepositional modifier | 1.0.0 |
| `prt`       | Verb particle | 1.0.0 |
| `punct`     | Punctuation | 1.0.0 |
| `qmod`      | Modifier of quantifier | 1.0.0 |
| `relcl`     | Relative clause modifier | 1.0.0 |
| `root`      | Root | 1.0.0 |
| `vocative`  | Vocative modifier | 1.0.0 |
| `xcomp`     | Open clausal complement | 1.0.0 |