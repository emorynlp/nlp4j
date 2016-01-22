# Data Format

## Raw Format

The `raw` format accepts texts in any format.

```
I'd like to meet Dr. Choi. He's the founder of EmoryNLP.
```

## Line Format

The `line` format expects a sentence per line.

```
I'd like to meet Dr. Choi.
He's the founder of EmoryNLP.
```

## Tab Separated Values Format

In the `tsv` format, each column represents a different field and sentences are separated by an empty line.

```tsv
1    I           i           PRP    _             3    nsubj     3:A0;5:A0
2    'd          would       MD     _             3    aux       3:AM-MOD
3    like        like        VB     pb=like.02    0    root      _
4    to          to          TO     _             5    aux       _
5    meet        meet        VB     pb=meet.01    3    xcomp     3:A1
6    Dr.         dr.         NNP    _             7    nn        _
7    Choi        choi        NNP    _             5    dobj      5:A1
8    .           .           .      _             3    punct     _
 
1    He          he          PRP    _             2    nsubj    2:A1
2    's          be          VBZ    pb=be.01      0    root     _
3    the         the         DT     _             4    nn       _
4    founder     owner       NN     _             2    attr     2:A2
5    of          of          IN     _             4    prep     _
6    EmoryNLP    emory       NNP    _             5    pobj     _
7    .           .           .      _             .    punct    _
```

```
1	2	3
```

These columns are specified in the [configuration files](../../src/main/resources/configuration/) as follows:

```xml
<configuration>
    <tsv>
        <column index="1" field="form"/>
        <column index="2" field="lemma"/>
        <column index="3" field="pos"/>
        <column index="4" field="feats"/>
        <column index="5" field="dhead"/>
        <column index="6" field="deprel"/>
    </tsv>
</configuration>
```

* `id`: current token ID (starting at `0`).
* `form`: word form.
* `lemma`: lemma.
* `pos`: part-of-speech tag.
* `feats`: extra features; different features are delimited by `|`, keys and values are delimited by `=` (`_` indicates no feature).
* `headId`: head token ID.
* `deprel`: dependency label.
* `sheads`: semantic heads (`_` indicates no semantic head).
