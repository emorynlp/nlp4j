# Processing State

## NLPState

A state object is created for every input (e.g., a sentence), providing information about the current state of the component (e.g., a pointer to the input token).  Creating a separate state object for each input enables the component to be thread-safe.  All state objects extend [`NLPState`](../../java/edu/emory/mathcs/nlp/component/util/state/NLPState.java), which takes two genetic types `N` and `L` representing the types of the input node (e.g., [`NLPNode`](../../java/edu/emory/mathcs/nlp/component/util/NLPNode.java)) and the label (e.g., `String`), respectively.

```java
public abstract class NLPState<N,L>
```

This class contains several abstract methods:

```java
/** Clears and saves the gold-standard labels in the input nodes if available. */
public abstract void clearGoldLabels();

/** Moves onto the next state */
public abstract void next();

/** @return true if no more state can be processed; otherwise, false. */
public abstract boolean isTerminate();

/** @return the gold standard label for the current state. */
public abstract L getGoldLabel();

/** Assigns the specific label to the current state. */
public abstract void setLabel(L label);

/** Evaluates all predictions given the current input. */
public abstract void evaluate(Eval eval);
```

See [NLP component](nlp_component.md) for more details about how these abstract methods are used.

## L2RState

[`N2RState`](../../java/edu/emory/mathcs/nlp/component/util/state/N2RState.java) defines the left-to-right tagging strategy commonly used in NLP (e.g., part-of-speech tagging, named entity recognition).  It extends [`NLPState`](#nlpstate) and takes a genetic type `N` representing the type of the input node (e.g., [`POSNode`](../../java/edu/emory/mathcs/nlp/component/pos/POSNode.java)).

```java
public abstract class L2RState<N> extends NLPState<N,String>
```

This state keeps track of the pointer to the processing node, starting at `0`. It then moves onto the next state by incrementing the pointer to the next node. Finally, it terminates if there is no more node to process.

```java
protected int index = 0;

@Override
public void next()
{
	index++;
}
	
@Override
public boolean isTerminate()
{
	return index >= nodes.length;
}
```
See [`POSState`](../../java/edu/emory/mathcs/nlp/component/pos/POSState.java) for the example of a subclass inheriting this class.