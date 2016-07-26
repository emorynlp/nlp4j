# NLP Component

All components extend [`NLPComponent`](../../java/edu/emory/mathcs/nlp/component/util/NLPComponent.java), providing general methods for supervised NLP.  This class takes three genetic types `N`, `L`, and `S` representing the types of the input nodes, the label, and the [processing state](processing_state.md), respectively.

```java
public abstract class NLPComponent<N,L,S extends NLPState<N,L>> implements Serializable
```

This class contains several abstract methods:

```java
/** @return the processing state for the input nodes. */
protected abstract S createState(N[] nodes);

/** @return the gold-standard label for training; otherwise, the predicted label. */
protected abstract L getLabel(S state, StringVector vector);

/** Adds a training instance (label, x) to the statistical model. */
protected abstract void addInstance(L label, StringVector vector);

/** @return the vector consisting of all features extracted from the state. */
protected abstract StringVector extractFeatures(S state);
```

These abstract methods are used in the `process` method providing a genetic way for processing the NLP component.

```java
public void process(N[] nodes)
{
	S state = createState(nodes);
	if (!isDecode()) state.clearGoldLabels();
	
	while (!state.isTerminate())
	{
		StringVector vector = extractFeatures(state);
		if (isTrainOrBootstrap()) addInstance(state.getGoldLabel(), vector);
		L label = getLabel(state, vector);
		state.setLabel(label);
		state.next();
	}
	
	if (isEvaluate()) state.evaluate(eval);
}
```

* The `process` method takes an array of nodes with the genetic type `N`.

	```java
	public void process(N[] nodes)
	```

* It begins by creating a [processing state](processing_state.md).

	```java
	S state = createState(nodes);
	```

* It is important to clear out and save existing gold-standard labels before training; accidental usage of these labels can lead to inflated evaluation scores.

	```java
	if (!isDecode()) state.clearGoldLabels();
	```

* The method iterates through every state as defined in the [processing state](processing_state.md).

	```java
	while (!state.isTerminate())
	{
		...
		state.next();
	}
	```

* For each state, it creates a vector consisting of features extracted from the current state.

	```java
	StringVector vector = extractFeatures(state);
	```

* During training and bootstrapping, it adds the training instance to the statistical model.

	```java
	if (isTrainOrBootstrap()) addInstance(state.getGoldLabel(), vector);
	```

* Given the feature vector, it predicts the label of the current state either from the oracle or the statistical model.

	```java
	L label = getLabel(state, vector);
	```

* Finally, it assigns the label to the current state.

	```java
	state.setLabel(label);
	```

* During evaluation, the accuracy counts are updated to the evaluator.

	```java
	if (isEvaluate()) state.evaluate(eval);
	```

