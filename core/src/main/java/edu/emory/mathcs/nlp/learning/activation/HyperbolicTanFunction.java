/**
 * 
 */
package edu.emory.mathcs.nlp.learning.activation;

/**
 * @author amit-deshmane
 *
 */
public class HyperbolicTanFunction implements ActivationFunction {

	private static final long serialVersionUID = 6581919225914864529L;

	public HyperbolicTanFunction() {
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.activation.ActivationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		for(int index = 0; index < scores.length; index++){
			scores[index] = (float)Math.tanh(scores[index]);
		}
	}
	@Override
	public String toString()
	{
		return "Tanh";
	}
}
