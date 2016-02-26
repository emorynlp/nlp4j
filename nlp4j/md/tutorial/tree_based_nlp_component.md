# Develop a Tree-Based NLP Component

In this tutorial, we will create an NLP component that traverses every node in a dependency tree and classifies it into a specific type of [pleonastic-it](https://github.com/emorynlp/pleonastic-it). The goal of this tutorial is to give you a general overview of how to develop a tree-based NLP component in NLP4J. Let us begin by cloning the [nlp4j-core](https://github.com/emorynlp/nlp4j-core) repository.

```bash
git clone https://github.com/emorynlp/nlp4j-core.git
```

## Package

Create a package [`pleonastic`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic) under [`edu.emory.mathcs.nlp.component`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/).


## State

Create a class [`PleonasticState`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic/PleonasticState.java) extending [`NLPState`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/state/NLPState.java). Add the following member instances to the class (we will explain what those are later).

```java
public class PleonasticState extends NLPState
{
	static public final Pattern DEPREL = Pattern.compile("^(nsubj|nsubjpass|dobj)$");
	static public final String KEY = "it"; 
	private String[] oracle;
	private int input;
}
```

Define a constructor that takes an array of [`NLPNode`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java). `nodes[0]` is always the artificial root, `nodes[1]` represents the first token in a sentence, and so on. The member instance `input` indicates the index of the node to be processed. Let us initialize `input` to `0`.

```java
public PleonasticState(NLPNode[] nodes)
{
	super(nodes);
	input = 0;
	shift();
}
	
private void shift()
{
	for (input++; input<nodes.length; input++)
	{
		NLPNode node = nodes[input];
		
		if (node.isLemma("it") && node.isDependencyLabel(DEPREL))
			break;
	}
}
```

Extending `NLPState` requires overriding several abstract methods. First, we create a member instance `oracle` that is a type of string array.  We then override the method `saveOracle` to save the gold labels from all nodes to `oracle`.

```
private final String FEAT_KEY = "it"; 
private String[] oracle;

@Override
public boolean saveOracle()
{
	oracle = Arrays.stream(nodes).map(n -> n.removeFeat(FEAT_KEY)).toArray(String[]::new);
	return Arrays.stream(oracle).filter(o -> o != null).findFirst().isPresent();
}
```

Here, we assume that the gold label can be retrieved by taking the value of `FEAT_KEY` in [`FeatMap`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/FeatMap.java) under [`NLPNode`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java). Once the gold labels are saved to `oracle`, they need to be removed from the nodes. `saveOracle` returns `false` if no gold label is provided; otherwise, it returns `true`.


