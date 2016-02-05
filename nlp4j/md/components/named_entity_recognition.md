# Named Entity Recognition

Our named entity recognizer processes about 50K tokens per second on an Intel Xeon 2.30GHz machine and shows state-of-the-art accuracy (91.0% on the CoNLL'03 corpus).

* [Intrinsic and Extrinsic Evaluations of Word Embeddings](), Michael Zhai, Johnny Tan, Jinho D. Choi, Proceedings of the AAAI 2015 Student Program, Phoenix, AZ, 2015.

## Tagset

### Names

| Tag | Description |
|---|---|
| PERSON       | People, including fictional || NORP         | Nationalities or religious or political groups || ￼FACILITY     | Buildings, airports, highways, bridges, etc. || ORGANIZATION | Companies, agencies, institutions, etc. || GPE          | Countries, cities, states || LOCATION     | Non-GPE locations, mountain ranges, bodies of water || PRODUCT      | Vehicles, weapons, foods, etc. (not services) || ￼EVENT        | Named hurricanes, battles, wars, sports events, etc. || WORK OF ART  | Titles of books, songs, etc. || LAW          | Named documents made into laws || LANGUAGE     | Any named language

### Others

| Tag | Description |
|---|---|
| DATE     | Absolute or relative dates or periods |
| TIME     | Times smaller than a day |
| PERCENT  | Percentage (including "%") |
| MONEY    | Monetary values, including unit |
| QUANTITY | Measurements, as of weight or distance |
| ORDINAL  | Ordinals (e.g., "first", "1st") |
| CARDINAL | Numerals that do not fall under another type |
