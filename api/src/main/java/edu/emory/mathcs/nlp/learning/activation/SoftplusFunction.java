/**
 * 
 */
package edu.emory.mathcs.nlp.learning.activation;

/**
 * @author amit-deshmane
 *
 */
public class SoftplusFunction implements ActivationFunction {

	private static final long serialVersionUID = -3123516253479799668L;

	public SoftplusFunction() {
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.activation.ActivationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		for(int index = 0; index < scores.length; index++){
			scores[index] = (float)Math.log(1 + Math.exp(scores[index]));
		}

	}
	@Override
	public String toString()
	{
		return "Softplus";
	}
}
