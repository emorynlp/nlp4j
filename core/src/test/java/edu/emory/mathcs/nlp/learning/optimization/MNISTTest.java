/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.learning.optimization;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.DoubleIntPair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.learning.optimization.method.AdaGradRegression;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MLUtils;
import edu.emory.mathcs.nlp.learning.util.SparseVector;
import edu.emory.mathcs.nlp.learning.util.WeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MNISTTest
{
	@Test
	public void test() throws Exception
	{
		InputStream ftrn = IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/mnist/mnist_trn.txt");
		InputStream ftst = IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/mnist/mnist_tst.txt");
		boolean sparse = true;
		List<Instance> trn = read(ftrn, sparse);
		List<Instance> tst = read(ftst, sparse);
		WeightVector w = new WeightVector();
		OnlineOptimizer op;

		// best: 89.87 at 46 epoch
//		op = new Perceptron(w, 0.01f, 0);
//		develop(trn, tst, op, 1, sparse);
		
		// best: 90.64 at 41 epoch
//		op = new AdaGrad(w, 0.01f, 0);
//		develop(trn, tst, op, 1, sparse);
		
		// best: 90.60 at 26 epoch
//		op = new AdaGrad(w, 0.01f, 0, new RegularizedDualAveraging(w, 0.001f));
//		develop(trn, tst, op, 1, sparse);
		
		// best: 90.49 at 24 epoch
//		op = new AdaGradMiniBatch(w, 0.01f, 0);
//		develop(trn, tst, op, 5, sparse);
		
		// best: 90.63 at 45 epoch
//		op = new AdaGradMiniBatch(w, 0.01f, 0, new RegularizedDualAveraging(w, 0.001f));
//		develop(trn, tst, op, 5, sparse);
		
		// best: 89.12 at 24 epoch
//		op = new AdaDeltaMiniBatch(w, 0.01f, 0.4f, 0);
//		develop(trn, tst, op, 5, sparse);
		
		// best: 89.36 at 7 epoch
//		op = new AdaDeltaMiniBatch(w, 0.01f, 0.4f, 0, new RegularizedDualAveraging(w, 0.001f));
//		develop(trn, tst, op, 5, sparse);

		// best: 92.38 at 47 epoch
//		op = new SoftmaxRegression(w, 0.00000001f, 0);
//		develop(trn, tst, op, 1, sparse);
		
		// best: 92.66 at 46 epoch
		op = new AdaGradRegression(w, 0.0001f, 0);
		develop(trn, tst, op, 1, sparse);
		
//		ActivationFunction sigmoid = new SigmoidFunction();
//		op = new FeedForwardNeuralNetworkSoftmax(new int[]{300}, new ActivationFunction[]{sigmoid}, 0.0001f, 0, new RandomWeightGenerator(new XORShiftRandom(9), -0.2f, 0.2f));
//		develop(trn, tst, op, 1, sparse);
	}
	
	void develop(List<Instance> trn, List<Instance> tst, OnlineOptimizer op, int miniBatch, boolean sparse)
	{
		DoubleIntPair best = new DoubleIntPair(0d,0);
		int yhat, count;
		float[] scores;
		String label;
		double acc;
		
		for (int epoch=0; epoch<50; epoch++)
		{
			// training
			count = 0;
			
			for (Instance instance : trn)
			{
				op.train(instance);
				
				if (++count == miniBatch)
				{
					op.updateMiniBatch();
					count = 0;
				}
			}
			
			if (count > 0) op.updateMiniBatch();
			
			// evaluating
			count = 0;
			
			for (Instance instance : tst)
			{
				scores = op.scores(instance.getFeatureVector());
				yhat = MLUtils.argmax(scores);
				label = op.getLabel(yhat);
				if (instance.isStringLabel(label)) count++;
			}

			acc = MathUtils.accuracy(count, tst.size());
			System.out.printf("%4d: %5.2f\n", epoch, acc);
			if (best.d < acc) best.set(acc, epoch);
		}	
		
		System.out.printf("best: %5.2f at %d epoch\n", best.d, best.i);
	}
	
	List<Instance> read(InputStream in, boolean sparse) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		List<Instance> instances = new ArrayList<>();
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitSpace(line);
			instances.add(sparse ? toSparseInstance(t) : toDenseInstance(t));
		}
		
		return instances;
	}
	
	Instance toSparseInstance(String[] t) throws Exception
	{
		SparseVector v = new SparseVector();
		float f;
		
		for (int i=1; i<t.length; i++)
		{	
			f = Float.parseFloat(t[i]);
			if (f != 0) v.add(i-1, f);
		}

		return new Instance(t[0], v);
	}
	
	Instance toDenseInstance(String[] t) throws Exception
	{
		float[] v = new float[t.length-1];
		
		for (int i=1; i<t.length; i++)
			v[i-1] = Float.parseFloat(t[i]);
		
		return new Instance(t[0], v);
	}
}
