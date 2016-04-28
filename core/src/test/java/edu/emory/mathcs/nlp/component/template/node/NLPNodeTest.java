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
package edu.emory.mathcs.nlp.component.template.node;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.reader.NLPReader;
import edu.emory.mathcs.nlp.component.template.util.NLPLib;
import junit.framework.Assert;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPNodeTest
{
	@Test
	public void test() throws Exception
	{
		NLPReader reader = new NLPReader(1, 2, 3, 4, 5, 6, 7, 8);
		NLPNode[] nodes;
		
		reader.open(IOUtils.createFileInputStream("src/test/resources/dat/sample-dev.tsv"));
		nodes = reader.next();
		
		// root_0 -> selected_5 -> from_6 -> families_7 
		Assert.assertEquals(nodes[6], nodes[7].getDependencyHead());
		Assert.assertEquals(nodes[0], nodes[5].getDependencyHead());
		
		// root_0 -> selected_5 -> from_6 -> families_7
		Assert.assertEquals(nodes[5], nodes[7].getGrandDependencyHead());
		Assert.assertEquals(null    , nodes[5].getGrandDependencyHead());
		
		// Students_1 - will_2 - also_3 - be_4 <- selected_5 -> from_6
		Assert.assertEquals(nodes[4], nodes[5].getLeftNearestDependent());
		Assert.assertEquals(nodes[1], nodes[5].getLeftNearestDependent(3));
		Assert.assertNull(nodes[5].getLeftNearestDependent(4));
		Assert.assertNull(nodes[6].getLeftNearestDependent());
		
		// Students_1 - will_2 - also_3 - be_4 <- selected_5 -> from_6
		Assert.assertEquals(nodes[1], nodes[5].getLeftMostDependent());
		Assert.assertEquals(nodes[4], nodes[5].getLeftMostDependent(3));
		Assert.assertNull(nodes[5].getLeftMostDependent(4));
		Assert.assertNull(nodes[6].getLeftMostDependent());
		
		// selected_5 -> from_6 - ._21, whose_8 <- income_9
		Assert.assertEquals(nodes[ 6], nodes[5].getRightNearestDependent());
		Assert.assertEquals(nodes[21], nodes[5].getRightNearestDependent(1));
		Assert.assertNull(nodes[5].getRightNearestDependent(2));
		Assert.assertNull(nodes[9].getRightNearestDependent());
		
		// selected_5 -> from_6 - ._21, whose_8 <- income_9
		Assert.assertEquals(nodes[21], nodes[5].getRightMostDependent());
		Assert.assertEquals(nodes[ 6], nodes[5].getRightMostDependent(1));
		Assert.assertNull(nodes[5].getRightMostDependent(2));
		Assert.assertNull(nodes[9].getRightMostDependent());
		
		// Students_1 - will_2 - also_3 - be_4 <- selected_5 -> from_6
		Assert.assertEquals(nodes[4], nodes[6].getLeftNearestSibling());
		Assert.assertEquals(nodes[3], nodes[4].getLeftNearestSibling());
		Assert.assertEquals(nodes[1], nodes[4].getLeftNearestSibling(2));
		Assert.assertNull(nodes[4].getLeftNearestSibling(3));
		
		// income_9 <- is_10 -> below_11 - and_14 - with_15 
		Assert.assertEquals(nodes[11], nodes[ 9].getRightNearestSibling());
		Assert.assertEquals(nodes[14], nodes[11].getRightNearestSibling());
		Assert.assertEquals(nodes[15], nodes[11].getRightNearestSibling(1));
		Assert.assertNull(nodes[11].getLeftNearestSibling(2));
		
		System.out.println(NLPLib.join(nodes, " ", AbstractNLPNode::getWordForm));
	}

}
