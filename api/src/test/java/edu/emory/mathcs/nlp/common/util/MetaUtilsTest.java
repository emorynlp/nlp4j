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
package edu.emory.mathcs.nlp.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.MetaUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MetaUtilsTest
{
	@Test
	public void testContainsHyperlink()
	{
		String s;
		
		s = "http://www.clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "https://www-01.clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "wiki.clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "clearnlp.com:8080";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "clearnlp.co.kr";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com/wiki";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com:8080/wiki";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "id@clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "id:pw@clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "id:@clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "mailto:support@clearnlp.com";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "255.248.27.1";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "http://127.0.0.1";
		assertTrue(MetaUtils.containsHyperlink(s));
		
		s = "http://www.clearnlp.com/watch?v=IAaDVOd2sRQ";
		assertTrue(MetaUtils.containsHyperlink(s));
	}

	@Test
	public void testEndsWithFileExtension()
	{
		String s;
		
		s = "index.html";
		assertTrue(MetaUtils.endsWithFileExtension(s));
		
		s = "index.htm";
		assertTrue(MetaUtils.endsWithFileExtension(s));
		
		s = "html";
		assertFalse(MetaUtils.endsWithFileExtension(s));
	}
}
