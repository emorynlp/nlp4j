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

The `tsv` format expects columns delimited by a tab and sentences separated by an empty line.

```tsv
1    I           i           PRP    _             3    nsubj    3:A0;5:A0    O
2    'd          would       MD     _             3    aux      3:AM-MOD     O
3    like        like        VB     pb=like.02    0    root     _            O
4    to          to          TO     _             5    aux      _            O
5    meet        meet        VB     pb=meet.01    3    xcomp    3:A1         O
6    Dr.         dr.         NNP    _             7    nn       _            B-PERSON
7    Choi        choi        NNP    _             5    dobj     5:A1         L-PERSON
8    .           .           .      _             3    punct    _            O
 
1    He          he          PRP    _             2    nsubj    2:A1         O
2    's          be          VBZ    pb=be.01      0    root     _            O
3    the         the         DT     _             4    nn       _            O
4    founder     owner       NN     _             2    attr     2:A2         O
5    of          of          IN     _             4    prep     _            O
6    EmoryNLP    emory       NNP    _             5    pobj     _            U-ORG
7    .           .           .      _             .    punct    _            O
```

The column fields are specified in the [configuration files](../../src/main/resources/configuration/) as follows:

```xml
<configuration>
    <tsv>
        <column index="1" field="form"/>
        <column index="2" field="lemma"/>
        <column index="3" field="pos"/>
        <column index="4" field="feats"/>
        <column index="5" field="dhead"/>
        <column index="6" field="deprel"/>
        <column index="7" field="sheads"/>
        <column index="8" field="nament"/>
    </tsv>
</configuration>
```

* `form`: word form.
* `lemma`: lemma.
* `pos`: part-of-speech tag.
* `feats`: extra features; features are delimited by `|`, and keys and values are delimited by `=` (e.g., `k1=v1|k2=v2`).
* `dhead`: dependency head token ID.
* `deprel`: dependency label.
* `sheads`: semantic heads; head IDs and labels are delimited by `:`.
