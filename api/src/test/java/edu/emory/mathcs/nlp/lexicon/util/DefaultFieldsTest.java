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
package edu.emory.mathcs.nlp.lexicon.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.lexicon.util.DefaultFields;
import edu.emory.mathcs.nlp.lexicon.util.FeatMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultFieldsTest
{
	@Test
	public void test()
	{
		DefaultFields node = new DefaultFields(1, "John-123", "john", "NNP", "PERSON", new FeatMap("k1=v1|k2=v2"));
		
		assertEquals(1         , node.getTokenID());
		assertEquals("John-123", node.getForm());
		assertEquals("john-123", node.getFormLowercase());
		assertEquals("John-0"  , node.getFormSimplified());
		assertEquals("john-0"  , node.getFormSimplifiedLowercase());
		assertEquals("john"    , node.getLemma());
		assertEquals("NNP"     , node.getSyntacticTag());
		assertEquals("PERSON"  , node.getNamedEntityTag());
		
		assertTrue(node.isTokenID(1));
		assertTrue(node.isForm("John-123"));
		assertTrue(node.isForm(PatternUtils.createClosedORPattern("John-\\d+")));
		assertTrue(node.isFormLowercase("john-123"));
		assertTrue(node.isFormSimplified("John-0"));
		assertTrue(node.isFormSimplifiedLowercase("john-0"));
		assertTrue(node.isLemma("john"));
		assertTrue(node.isSyntacticTag("NNP"));
		assertTrue(node.isSyntacticTag(PatternUtils.createClosedPattern("NN.*")));
		assertTrue(node.isSyntacticTag(Sets.newHashSet("NNP","VBP")));
		assertTrue(node.isSyntacticTag("NNP","VBP"));
		assertTrue(node.isNamedEntityTag("PERSON"));

		assertFalse(node.isSyntacticTag(PatternUtils.createClosedPattern("VB.*")));
		assertFalse(node.isSyntacticTag(Sets.newHashSet("NN","VB")));
		assertFalse(node.isSyntacticTag("NN","VB"));
		
		assertEquals("v1", node.getFeat("k1"));
		assertEquals("v2", node.getFeat("k2"));
		assertEquals(null, node.getFeat("k3"));
		node.putFeat("k3", "v3");
		assertEquals("v3", node.getFeat("k3"));
		node.removeFeat("k2");
		assertEquals(null, node.getFeat("k2"));
	}
}
