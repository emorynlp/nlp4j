/**
 * 
 */
package edu.emory.mathcs.nlp.learning.activation;

/**
 * @author amit-deshmane
 *
 */
public class RectifiedLinearUnitFunction implements ActivationFunction {

	private static final long serialVersionUID = 2776457895707438981L;

	public RectifiedLinearUnitFunction() {
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.learning.activation.ActivationFunction#apply(float[])
	 */
	@Override
	public void apply(float[] scores) {
		for(int index = 0; index < scores.length; index++){
			if(scores[index] < 0){
				scores[index] = 0;
			}
		}

	}
	@Override
	public String toString()
	{
		return "Relu";
	}
}
