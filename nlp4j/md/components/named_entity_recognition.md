# Named Entity Recognition

Our named entity recognizer uses both sparse and dense features extracted from named entity gazetteers, word clusters, and word embeddings. It processes over 47K tokens per second on an Intel Xeon 2.30GHz machine and shows the state-of-the-art accuracy (91.0% on the CoNLL'03 corpus).

* [Intrinsic and Extrinsic Evaluations of Word Embeddings](), Michael Zhai, Johnny Tan, Jinho D. Choi, Proceedings of the AAAI 2015 Student Program, Phoenix, AZ, 2015.

## English Tags

| Tag            | Description | Version |
| -------------- | ----------- | ------- |
| `PERSON`       | People, including fictional | 1.0.0 || `NORP`         | Nationalities or religious or political groups | 1.0.0 || ￼`FACILITY`     | Buildings, airports, highways, bridges, etc. | 1.0.0 || `ORGANIZATION` | Companies, agencies, institutions, etc. | 1.0.0 || `GPE`          | Countries, cities, states | 1.0.0 || `LOCATION`     | Non-GPE locations, mountain ranges, bodies of water | 1.0.0 || `PRODUCT`      | Vehicles, weapons, foods, etc. (not services) | 1.0.0 || ￼`EVENT`        | Named hurricanes, battles, wars, sports events, etc. | 1.0.0 || `WORK OF ART`  | Titles of books, songs, etc. | 1.0.0 || `LAW`          | Named documents made into laws | 1.0.0 || `LANGUAGE`     | Any named language | 1.0.0 |
| `DATE`         | Absolute or relative dates or periods | 1.0.0 |
| `TIME`         | Times smaller than a day | 1.0.0 |
| `PERCENT`      | Percentage (including "%") | 1.0.0 |
| `MONEY`        | Monetary values, including unit | 1.0.0 |
| `QUANTITY`     | Measurements, as of weight or distance | 1.0.0 |
| `ORDINAL`      | Ordinals (e.g., "first", "1st") | 1.0.0 |
| `CARDINAL`     | Numerals that do not fall under another type | 1.0.0 |
