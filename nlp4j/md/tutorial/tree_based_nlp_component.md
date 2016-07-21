# Pleonastic It

In this tutorial, we will create an NLP component that traverses every node in a dependency tree, and classifies it into a specific type of [pleonastic-it](https://github.com/emorynlp/pleonastic-it). Let us begin by cloning the [nlp4j-core](https://github.com/emorynlp/nlp4j-core) repository (if you haven't already).

```bash
git clone https://github.com/emorynlp/nlp4j-core.git
```

## Package

Create a package [`pleonastic`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic) under [`edu.emory.mathcs.nlp.component`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/).


## State

Create a class [`PleonasticState`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic/PleonasticState.java) extending [`NLPState`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/state/NLPState.java). Add the following member instances to the class (you will see the use of these instances later).

```java
public class PleonasticState extends NLPState
{
	static public final String KEY = "it"; 
	private String[] oracle;
	private int input;
}
```

Define a constructor that takes an array of [`NLPNode`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java). `nodes[0]` is the artificial root, `nodes[1]` represents the first token in a sentence, and so on. `input` indicates the index of the node to be processed; it is initialized to `0`, pointing to the root node. `shift` finds the next node whose lemma is `it`.

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
		if (node.isLemma("it")) break;
	}
}
```

Extending `NLPState` requires overriding several abstract methods. First, override `saveOracle` to save all gold labels, which can be retrieved by taking the values of `FEAT_KEY` in [`NLPNode::feat_map`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/template/node/NLPNode.java). Once the gold labels are saved, they need to be removed from the nodes. This method returns `false` if no gold label is provided; otherwise, it returns `true`.
Then, override `getOracle`, which returns the gold label of the input node.

```java
@Override
public boolean saveOracle()
{
	oracle = Arrays.stream(nodes).map(n -> n.removeFeat(FEAT_KEY)).toArray(String[]::new);
	return Arrays.stream(oracle).filter(o -> o != null).findFirst().isPresent();
}

@Override
public String getOracle()
{
	return oracle[input];
}
```

Second, override `next`, which takes system or oracle predictions of the current state, applies the top prediction to the current state, and moves onto the next state. Then, override `isTerminal`, which returns `true` if no more state is available; in other words, no more input node is left to be processed.


```java
/**
 * @param map retrieves the string label from its index. 
 * @param yhat index of the top predicated label.
 * @param scores scores of all labels.
 */
@Override
public void next(LabelMap map, int yhat, float[] scores)
{
	String label = map.getLabel(yhat);
	nodes[input].putFeat(FEAT_KEY, label);
	shift();
}

@Override
public boolean isTerminate()
{
	return input >= nodes.length;
}
```

Third, override `getNode`, which takes [`FeatureItem`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/feature/FeatureItem.java) and returns a node indicated by the feature template.  Given the input node `nodes[input]`, the feature template specifies the window size and the dependency relation of the node to extract features from. For instance, if `window` is `-1` and the dependency relation is `lmd`, it returns `nodes[input-1].getLeftMostDependent()` if exist; otherwise, it returns `null`.

```java
@Override
public NLPNode getNode(FeatureItem item)
{
	NLPNode node = getNode(input, item.window);
	return getRelativeNode(item, node);
}
```

Finally, we override `evaluate`, which takes [`Eval`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/eval/Eval.java) and evaluates the predictions made for this tree. Here, we are using the built-in evaluator, [`AccuracyEval`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/eval/AccuracyEval.java), that measures the accuracy by counting the correct predictions.

```java
@Override
public void evaluate(Eval eval)
{
	int correct = 0, total = 0;
	
	for (int i=1; i<oracle.length; i++)
	{
		NLPNode n = nodes[i];
		String o = oracle[i];
		
		if (o != null)
		{
			if (o.equals(n.getFeat(FEAT_KEY))) correct++;
			total++;
		}
	}

	((AccuracyEval)eval).add(correct, total);
}
```

## Classifier

Create a class [`PleonasticClassifier`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic/PleonasticClassifier.java) extending [`OnlineComponent`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/OnlineComponent.java).

```java
public class PleonasticClassifier extends OnlineComponent<PleonasticState>
{
	private static final long serialVersionUID = 3585863417135590906L;
	
	public PleonasticClassifier() {}
	
	public PleonasticClassifier(InputStream configuration)
	{
		super(configuration);
	}
}
```

Override `initState` using [`PleonasticState`](https://github.com/emorynlp/nlp4j-core/tree/master/src/main/java/edu/emory/mathcs/nlp/component/pleonastic/PleonasticState.java).

```java
@Override
protected PleonasticState initState(NLPNode[] nodes)
{
	return new PleonasticState(nodes);
}
```

Override `createEvaluator` using [`AccuracyEval`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/eval/AccuracyEval.java).

```java
@Override
public Eval createEvaluator()
{
	return new AccuracyEval();
}
```

Override `postProcess` with an empty definition.

```java
@Override
protected void postProcess(PleonasticState state) {}
```

## NLPMode

Add the mode `pleonastic` to [`NLPMode`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/util/NLPMode.java).

```java
public enum NLPMode
{
	pos,			// part-of-speech tagging
	ner,			// named entity recognition
	dep,			// dependency parsing
	srl,			// semantic role labeling
	sentiment,		// sentiment analysis
	pleonastic;		// pleonastic-it classification
}
```

## Trainer

Add `pleonastic` to [`OnlineTrainer`](https://github.com/emorynlp/nlp4j-core/blob/master/src/main/java/edu/emory/mathcs/nlp/component/template/train/OnlineTrainer.java).

```java
protected OnlineComponent<S> createComponent(NLPMode mode, InputStream config)
{
	switch (mode)
	{
	case pos: return (OnlineComponent<S>)new POSTagger(config);
	case ner: return (OnlineComponent<S>)new NERTagger(config);
	case dep: return (OnlineComponent<S>)new DEPParser(config);
	case srl: return (OnlineComponent<S>)new SRLParser(config);
	case sentiment : return (OnlineComponent<S>)new SentimentAnalyzer(config);
	case pleonastic: return (OnlineComponent<S>)new PleonasticClassifier(config);
	default : throw new IllegalArgumentException("Unsupported mode: "+mode);
	}
}
```