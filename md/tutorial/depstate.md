# DEPState

[`DEPState`](../../java/edu/emory/mathcs/nlp/component/dep/DEPState.java) implements the parsing algorithm and holds the parsing states processed by the algorithm.

## Initialization

```java
public DEPStateArcEager(N[] nodes)
{
	super(nodes);

	stack = new IntArrayList();
	input = 0;
}
```

## Save Oracle

The oracle is saved as a list of [`DEPArc`](../../java/edu/emory/mathcs/nlp/component/dep/DEPArc.java).  `oracle[0]` is preserved for the artificial root node and the rest holds the gold-standard head information for each node.


```java
@Override
public void saveOracle()
{
	oracle = Arrays.stream(nodes).map(n -> n.clearDependencies()).toArray(DEPArc[]::new);
}
```

# Arc-Eager

implements the arc-eager algorithm ([Nivre 2008](http://www.mitpressjournals.org/doi/pdf/10.1162/coli.07-056-R1-07-027), Section 4.2), that is the most widely used projective parsing algorithm.