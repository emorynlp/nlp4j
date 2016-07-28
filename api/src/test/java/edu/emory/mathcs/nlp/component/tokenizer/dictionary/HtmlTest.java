/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.component.tokenizer.dictionary;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HtmlTest
{
	@Test
	public void test()
	{
		Html html = new Html();
		StringBuilder build;
		String s;
		
		s = "&quot;&amp;&lt;&gt;";
		assertEquals("\"&<>", html.replace(s));
		
		s = "&cent;&pound;&curren;&yen;&sect;&copy;&reg;&euro;";
		build = new StringBuilder();
		
		build.append((char)162);
		build.append((char)163);
		build.append((char)164);
		build.append((char)165);
		build.append((char)167);
		build.append((char)169);
		build.append((char)174);
		build.append((char)8364);

		assertEquals(build.toString(), html.replace(s));
		
		s = "&#33;&lt;&rand;&gt;&#123;";
		assertEquals("!<&rand;>{", html.replace(s));
	}
}
