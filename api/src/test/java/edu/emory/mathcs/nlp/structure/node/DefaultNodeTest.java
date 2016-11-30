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
package edu.emory.mathcs.nlp.structure.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.structure.node.DefaultNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultNodeTest
{
	@Test
	public void test()
	{
		DefaultNode p = new DefaultNode("P","p");
		DefaultNode a = new DefaultNode("A","a");
		DefaultNode b = new DefaultNode("B","b");
		DefaultNode c = new DefaultNode("C","c");
		DefaultNode d = new DefaultNode("D","d");
		DefaultNode e = new DefaultNode("E","e");
		DefaultNode f = new DefaultNode("F","f");
		
		p.addChild(b);
		p.addChild(0, a);
		p.addChild(d);
		p.addChild(2, c);
		
		List<DefaultNode> list = Lists.newArrayList(a, b, c, d);
		Set<String> set = Sets.newHashSet("B", "D");
		Pattern pattern = PatternUtils.createClosedORPattern("E|F");
		
		// p -> [a, b, c, d]
		assertEquals(a, p.getFirstChild());
		assertEquals(b, p.getFirstChild(1));
		assertEquals(b, p.getFirstChild(n -> n.isSyntacticTag(set)));
		
		assertEquals(d, p.getLastChild());
		assertEquals(c, p.getLastChild(1));
		assertEquals(d, p.getLastChild(n -> n.isSyntacticTag(set)));
		
		assertEquals(null, p.getChild(-1));
		assertEquals(null, p.getChild(4));
		assertEquals(null, p.getFirstChild(4));
		assertEquals(null, p.getFirstChild(n -> n.isSyntacticTag("E")));
		assertEquals(null, p.getLastChild(4));
		assertEquals(null, p.getLastChild(n -> n.isSyntacticTag("E")));
		
		assertTrue(p.hasChild());
		assertFalse(d.hasChild());
		
		for (int i=0; i<p.getChildrenSize(); i++)
		{
			DefaultNode child = p.getChild(i);

			assertTrue(p.isParentOf(child));
			assertTrue(child.isChildOf(p));
			assertTrue(child.hasParent());
			assertTrue(child.hasParent(n -> n.isSyntacticTag("P")));
			
			assertFalse(child.hasParent(n -> n.isSyntacticTag("N")));
			assertFalse(p.hasParent(n -> n.isSyntacticTag("P")));

			assertEquals(list.get(i), child);
			assertEquals(p, child.getParent());
			assertEquals(i, p.getChildIndex(child));
			assertEquals(p.getChild(i-1), child.getLeftNearestSibling());
			assertEquals(p.getChild(i-2), child.getLeftNearestSibling(1));
			assertEquals(p.getChild(i+1), child.getRightNearestSibling());
			assertEquals(p.getChild(i+2), child.getRightNearestSibling(1));
		}
		
		assertEquals(b, d.getLeftNearestSibling (n -> n.isSyntacticTag("B")));
		assertEquals(c, a.getRightNearestSibling(n -> n.isSyntacticTag("C")));
		
		assertEquals(null, d.getLeftNearestSibling (n -> n.isSyntacticTag("D")));
		assertEquals(null, a.getRightNearestSibling(n -> n.isSyntacticTag("A")));
		
		assertFalse(p.isParentOf(e));
		assertFalse(e.isChildOf(p));
		assertFalse(e.hasParent());
		assertTrue(p.containsChild(n -> n.isSyntacticTag("C")));
		
		assertTrue(b.hasLeftSibling());
		assertTrue(c.hasRightSibling());
		assertTrue(b.isSiblingOf(c));
		assertTrue(a.isLeftSiblingOf(d));
		assertTrue(a.isLeftSiblingOf(b));
		assertTrue(d.isRightSiblingOf(a));
		assertTrue(b.isRightSiblingOf(a));
		
		assertFalse(a.hasLeftSibling());
		assertFalse(d.hasRightSibling());
		assertFalse(b.isSiblingOf(f));
		assertFalse(b.isSiblingOf(p));
		assertFalse(b.isLeftSiblingOf(a));
		assertFalse(a.isRightSiblingOf(d));
		
		// p -> [a, b, e, d]
		assertEquals(c, p.setChild(2, e));
		
		assertTrue(p.isParentOf(e));
		assertTrue(e.isChildOf(p));
		assertTrue(e.hasParent());
		assertFalse(p.containsChild(n -> n.isSyntacticTag("C")));
		
		assertEquals(b, e.getLeftNearestSibling());
		assertEquals(e, b.getRightNearestSibling());
		
		// p -> [a, e, d]
		assertTrue(p.replaceChild(b, e));
		assertTrue(p.isParentOf(e));
		assertTrue(e.isChildOf(p));
		
		assertFalse(p.isParentOf(b));
		assertFalse(b.isChildOf(p));
		assertFalse(p.replaceChild(c, b));
		
		assertEquals(a, e.getLeftNearestSibling());
		assertEquals(e, d.getLeftNearestSibling());
		assertEquals(e, a.getRightNearestSibling());
		assertEquals(d, e.getRightNearestSibling());
		
		// p -> [a, f, d]
		assertTrue(p.replaceChild(e, f));
		assertTrue(p.isParentOf(f));
		assertTrue(f.isChildOf(p));
		
		assertEquals(a, f.getLeftNearestSibling());
		assertEquals(f, d.getLeftNearestSibling());
		assertEquals(f, a.getRightNearestSibling());
		assertEquals(d, f.getRightNearestSibling());
		
		// p -> [a, d]
		assertEquals(f, p.removeChild(f));
		assertEquals(a, d.getLeftNearestSibling());
		assertEquals(d, a.getRightNearestSibling());
		
		// p -> [a, b, c, d], b -> [e], c -> [f]
		list = Lists.newArrayList(a, b, c, d);
		p.addChild(1, c);
		p.addChild(1, b);
		e.setParent(b);
		f.setParent(c);
		
		assertEquals(4, p.getChildrenSize());
		assertEquals(list, p.getChildren());
		assertEquals(Lists.newArrayList(b, c, d), p.getChildren(1));
		assertEquals(Lists.newArrayList(b, c), p.getChildren(1, 3));
		assertEquals(Lists.newArrayList(b, d), p.getChildren(n -> n.isSyntacticTag(set)));
		assertEquals(Lists.newArrayList(e, f), p.getGrandChildren());
		assertEquals(e, p.getFirstDescendant(n -> n.isSyntacticTag(pattern)));
		assertEquals(e, p.getFirstLowestChainedDescendant(n -> n.isSyntacticTag(PatternUtils.createClosedORPattern("B|E"))));
		assertEquals(b, p.getFirstLowestChainedDescendant(n -> n.isSyntacticTag(PatternUtils.createClosedORPattern("B|F"))));
		
		assertTrue(f.isDescendantOf(c));
		assertTrue(f.isDescendantOf(p));
		assertTrue(p.isAncestorOf(f));
		assertTrue(c.isAncestorOf(f));
		
		assertFalse(f.isDescendantOf(b));
		assertFalse(b.isAncestorOf(f));
		
		assertEquals(p, f.getGrandParent());
		assertEquals(null, c.getGrandParent());
		assertEquals(p, e.getLowestAncestor(n -> n.isSyntacticTag("P")));
		assertEquals(b, e.getLowestAncestor(n -> n.isSyntacticTag(PatternUtils.createClosedORPattern("B|P"))));
		assertEquals(p, e.getHighestChainedAncestor(n -> n.isSyntacticTag(PatternUtils.createClosedORPattern("B|P"))));
		assertEquals(b, e.getHighestChainedAncestor(n -> n.isSyntacticTag(PatternUtils.createClosedORPattern("B|C"))));
	}
	
	
	
	
	
	
	
	
	
	

//	@Test
//	public void testDescendants()
//	{
//		DefaultNode a = new DefaultNode("a","A");
//		DefaultNode b = new DefaultNode("b","B");
//		DefaultNode c = new DefaultNode("c","C");
//		DefaultNode d = new DefaultNode("d","D");
//		DefaultNode e = new DefaultNode("e","E");
//		DefaultNode f = new DefaultNode("f","F");
//		DefaultNode g = new DefaultNode("g","G");
//		DefaultNode h = new DefaultNode("h","H");
//		
//		// a -> [b, c, d, e], c -> [f, g], d -> [h, a]
//		a.addChild(b);
//		a.addChild(c);
//		a.addChild(d);
//		a.addChild(e);
//		c.addChild(f);
//		c.addChild(g);
//		d.addChild(h);
//		d.addChild(a);
//		
//		List<DefaultNode> list = Lists.newArrayList(b, c, d, e);
//		Pattern t = PatternUtils.createClosedORPattern("C","D");
//		Set<String> s = Sets.newHashSet("C","D");
//		
//		assertEquals(b   , a.getLeftMostChild());
//		assertEquals(c   , a.getLeftMostChild(1));
//		assertEquals(c   , a.getLeftMostChild("C", DefaultNode::isSyntacticTag));
//		assertEquals(c   , a.getLeftMostChild(t  , DefaultNode::isSyntacticTag));
//		assertEquals(c   , a.getLeftMostChild(s  , DefaultNode::isSyntacticTag));
//		assertEquals(null, a.getLeftMostChild("A", DefaultNode::isSyntacticTag));
//		
//		assertEquals(e   , a.getRightMostChild());
//		assertEquals(d   , a.getRightMostChild(1));
//		assertEquals(d   , a.getRightMostChild("D", DefaultNode::isSyntacticTag));
//		assertEquals(d   , a.getRightMostChild(t  , DefaultNode::isSyntacticTag));
//		assertEquals(d   , a.getRightMostChild(s  , DefaultNode::isSyntacticTag));
//		assertEquals(null, a.getRightMostChild("A", DefaultNode::isSyntacticTag));
//		
//		assertEquals(list, a.getChildren());
//		assertEquals(Lists.newArrayList(f, g, h), a.getGrandChildren());
//
//		for (DefaultNode n : a.getChildren())
//			assertTrue(a.containsChild(n));
//		
//		assertTrue (a.containsChild(s  , DefaultNode::isSyntacticTag));
//		assertFalse(a.containsChild("A", DefaultNode::isSyntacticTag));
//		assertFalse(a.containsChild(h));
//		
//		assertTrue (c.hasChild());
//		assertFalse(h.hasChild());
//		
//		for (DefaultNode n : a.getChildren())
//			assertTrue(n.isChildOf(a));
//		
//		assertFalse(a.isChildOf(a));
//		assertFalse(g.isChildOf(a));
//		
//		assertTrue (h.isDescendantOf(a));
//		assertTrue (d.isDescendantOf(a));
//		assertTrue (a.isDescendantOf(d));
//		assertTrue (a.isDescendantOf(a));
//		assertFalse(a.isDescendantOf(c));
//		
//		assertTrue (a.removeChild(d));
//		assertFalse(a.removeChild(a));
//
//		assertFalse(h.isDescendantOf(a));
//		assertFalse(d.isDescendantOf(a));
//		assertFalse(a.isDescendantOf(a));
//		assertTrue (a.isDescendantOf(d));
//	}
//	
//	@Test
//	public void testSiblings()
//	{
//		DefaultNode a = new DefaultNode("A","a");
//		DefaultNode b = new DefaultNode("B","b");
//		DefaultNode c = new DefaultNode("C","c");
//		DefaultNode d = new DefaultNode("D","d");
//		DefaultNode e = new DefaultNode("E","e");
//		DefaultNode f = new DefaultNode("F","f");
//		
//		// a -> [b, c, d, e, f]
//		a.addChild(b);
//		a.addChild(c);
//		a.addChild(d);
//		a.addChild(e);
//		a.addChild(f);
//		
//		Set<String> bc = Sets.newHashSet("B","C");
//		Set<String> ef = Sets.newHashSet("E","F");
//		
//		assertEquals(c, d.getLeftNearestSibling());
//		assertEquals(b, d.getLeftNearestSibling(1));
//		assertEquals(null, d.getLeftNearestSibling(2));
//		assertEquals(b, d.getLeftNearestSibling(bc, DefaultNode::isSyntacticTag));
//		
//		assertEquals(e, d.getRightNearestSibling());
//		assertEquals(f, d.getRightNearestSibling(1));
//		assertEquals(null, d.getRightNearestSibling(2));
//		assertEquals(e, d.getRightNearestSibling(ef, DefaultNode::isSyntacticTag));
//		
//		assertEquals(b, d.getLeftMostSibling());
//		assertEquals(c, d.getLeftMostSibling(1));
//		assertEquals(null, d.getLeftMostSibling(2));
//		assertEquals(b, d.getLeftMostSibling(bc, DefaultNode::isSyntacticTag));
//		
//		assertEquals(f, d.getRightMostSibling());
//		assertEquals(e, d.getRightMostSibling(1));
//		assertEquals(null, d.getRightMostSibling(2));
//		assertEquals(f, d.getRightMostSibling(ef, DefaultNode::isSyntacticTag));
//		
//		assertEquals(Lists.newArrayList(b,c), d.getLeftSiblings());
//		assertEquals(Lists.newArrayList(b), d.getLeftSiblings("B", DefaultNode::isSyntacticTag));
//		
//		assertEquals(Lists.newArrayList(e,f), d.getRightSiblings());
//		assertEquals(Lists.newArrayList(e), d.getLeftSiblings("E", DefaultNode::isSyntacticTag));
//		
//		assertTrue (d.containsLeftSibling(c));
//		assertTrue (d.containsLeftSibling("B", DefaultNode::isSyntacticTag));
//		assertFalse(d.containsLeftSibling("A", DefaultNode::isSyntacticTag));
//		
//		assertTrue (d.containsRightSibling(f));
//		assertTrue (d.containsRightSibling("E", DefaultNode::isSyntacticTag));
//		assertFalse(d.containsRightSibling("A", DefaultNode::isSyntacticTag));
//		
//		assertTrue (d.hasLeftSibling());
//		assertFalse(b.hasLeftSibling());
//		
//		assertTrue (d.hasRightSibling());
//		assertFalse(f.hasRightSibling());
//		
//		assertTrue (b.isSiblingOf(d));
//		assertTrue (d.isSiblingOf(f));
//		assertTrue (a.removeChild(d));
//		assertFalse(b.isSiblingOf(d));
//		assertFalse(d.isSiblingOf(f));
//		
//		assertEquals(c, e.getLeftNearestSibling());
//		assertEquals(e, c.getRightNearestSibling());
//	}
}
