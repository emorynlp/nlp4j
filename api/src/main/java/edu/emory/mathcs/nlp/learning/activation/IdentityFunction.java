/**
 * 
 */
package edu.emory.mathcs.nlp.learning.activation;

/**
 * @author amit-deshmane
 *
 */
public class IdentityFunction implements ActivationFunction {

	private static final long serialVersionUID = 797900453250163148L;

	public IdentityFunction() {
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.activation.ActivationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		return;

	}
	@Override
	public String toString()
	{
		return "Identity";
	}
}
