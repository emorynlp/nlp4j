/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.component.sentiment;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SentimentAnalyzer extends OnlineComponent<SentimentState>
{
	private static final long serialVersionUID = 2002182385845859658L;
	public static final String XML_POSITIVE = "sentiment_positive";
	public static final String XML_NEGATIVE = "sentiment_negative";
	private String positive_label;
	private String negative_label;

	public SentimentAnalyzer() {super(true);}
	
	public SentimentAnalyzer(InputStream configuration)
	{
		super(true, configuration);
		setPositiveLabel(config.getTextContent(XML_POSITIVE));
		setNegativeLabel(config.getTextContent(XML_NEGATIVE));
	}

//	============================== GETTERS/SETTERS ==============================
	
	public String getPositiveLabel()
	{
		return positive_label;
	}

	public void setPositiveLabel(String positiveLabel)
	{
		this.positive_label = positiveLabel;
	}

	public String getNegativeLabel()
	{
		return negative_label;
	}

	public void setNegativeLabel(String negativeLabel)
	{
		this.negative_label = negativeLabel;
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	protected SentimentState initState(List<NLPNode[]> document)
	{
		return new SentimentState(document);
	}
	
	@Override
	public void initFeatureTemplate()
	{
		feature_template = new SentimentFeatureTemplate(config.getFeatureTemplateElement(), getHyperParameter());
	}
	
	@Override
	public Eval createEvaluator()
	{
		return (positive_label != null && negative_label != null) ? new SentimentEval(positive_label, negative_label) : new AccuracyEval();
	}
	
	@Override
	protected void postProcess(SentimentState state) {}
	
	@Override
	protected SentimentState initState(NLPNode[] nodes) {return null;}
}
