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
package edu.emory.mathcs.nlp.common.verbnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.mathcs.nlp.structure.verbnet.VNClass;
import edu.emory.mathcs.nlp.structure.verbnet.VNFrame;
import edu.emory.mathcs.nlp.structure.verbnet.VNLib;
import edu.emory.mathcs.nlp.structure.verbnet.VNMap;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNLibTest
{
	@Test
	public void testGetVerbNetMap()
	{
		VNMap map = VNLib.getVerbNetMap("src/test/resources/verbnet", true);
		testGetVerbNetMap1(map);
		testGetVerbNetMap2(map);
	}
	
	private void testGetVerbNetMap1(VNMap map)
	{
		VNFrame frame;
		VNClass vn;
		
		vn = map.get("13.6");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("agent co-theme theme", frame.getSyntax().toString());
		assertEquals("has_possession(start(E),agent,theme) not(has_possession(start(E),agent,co-theme)) has_possession(end(E),agent,co-theme) not(has_possession(end(E),agent,theme)) transfer(during(E),theme) transfer(during(E),co-theme) cause(agent,E)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(1);
		assertEquals("agent+ theme+", frame.getSyntax().toString());
		assertEquals("has_possession(start(E),agent_i,theme_i) has_possession(end(E),agent_j,theme_i) has_possession(start(E),agent_j,theme_j) has_possession(end(E),agent_i,theme_j) transfer(during(E),theme_i) transfer(during(E),theme_j) cause(agent,E)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(2);
		assertEquals("agent theme+", frame.getSyntax().toString());
		assertEquals("has_possession(start(E),agent,theme_i) has_possession(end(E),agent,theme_j) transfer(during(E),theme_i) transfer(during(E),theme_j) cause(agent,E)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(3);
		assertEquals("agent co-agent theme+", frame.getSyntax().toString());
		assertEquals("has_possession(start(E),agent,theme_i) has_possession(end(E),co-agent,theme_i) has_possession(start(E),co-agent,theme_j) has_possession(end(E),agent,theme_j) transfer(during(E),theme_i) transfer(during(E),theme_j)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(4);
		assertEquals("agent theme", frame.getSyntax().toString());
		assertEquals("transfer(during(E),theme) cause(agent,E)", frame.getSemantics().toString(" "));
		
		vn = map.get("13.6-1");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("co-theme location theme", frame.getSyntax().toString());
		assertEquals("location(start(E),theme,location_i) location(end(E),theme,location_j) location(start(E),co-theme,location_j) location(end(E),co-theme,location_i)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(1);
		assertEquals("location theme+", frame.getSyntax().toString());
		assertEquals("location(start(E),theme_i,location_i) location(end(E),theme_i,location_j) location(start(E),theme_j,location_j) location(end(E),theme_j,location_i)", frame.getSemantics().toString(" "));
		
		vn = map.get("13.6-1-1");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("co-theme theme", frame.getSyntax().toString());
		assertEquals("transfer(during(E),theme) transfer(during(E),co-theme)", frame.getSemantics().toString(" "));
	}
	
	private void testGetVerbNetMap2(VNMap map)
	{
		VNFrame frame;
		VNClass vn;
		
		vn = map.get("26.7-1");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("agent theme", frame.getSyntax().toString());
		assertEquals("perform(during(E),agent,theme)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(1);
		assertEquals("agent", frame.getSyntax().toString());
		assertEquals("perform(during(E),agent,?theme)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(2);
		assertEquals("agent beneficiary theme", frame.getSyntax().toString());
		assertEquals("perform(during(E),agent,theme) benefit(E,beneficiary)", frame.getSemantics().toString(" "));
		
		vn = map.get("26.7-1-1");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("agent beneficiary theme", frame.getSyntax().toString());
		assertEquals("perform(during(E),agent,theme) benefit(E,beneficiary)", frame.getSemantics().toString(" "));
		
		vn = map.get("26.7-2");
		assertTrue(vn != null);
		
		frame = vn.getFrame(0);
		assertEquals("agent theme", frame.getSyntax().toString());
		assertEquals("not(exist(start(E),theme)) exist(result(E),theme) cause(agent,E)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(1);
		assertEquals("agent", frame.getSyntax().toString());
		assertEquals("not(exist(start(E),?theme)) exist(result(E),?theme) cause(agent,E)", frame.getSemantics().toString(" "));
		
		frame = vn.getFrame(2);
		assertEquals("agent beneficiary theme", frame.getSyntax().toString());
		assertEquals("not(exist(start(E),theme)) exist(result(E),theme) cause(agent,E) benefit(E,beneficiary)", frame.getSemantics().toString(" "));
		
		vn = map.get("26.7-2-1");
		assertTrue(vn != null);

		frame = vn.getFrame(0);
		assertEquals("agent beneficiary theme", frame.getSyntax().toString());
		assertEquals("not(exist(start(E),theme)) exist(result(E),theme) cause(agent,E) benefit(E,beneficiary)", frame.getSemantics().toString(" "));
	}
	
	@Test
	@Ignore
	public void printVerbNetMap()
	{
		VNMap map = VNLib.getVerbNetMap("src/test/resources/verbnet", true);
		List<String> ids = new ArrayList<>(map.keySet());
		Collections.sort(ids);
		VNClass vn;
		
		for (String id : ids)
		{
			System.out.println(id);
			vn = map.get(id);
			
			for (VNFrame frame : vn.getFrameList())
			{
				System.out.println(frame.getSyntax().toString());
				System.out.println(frame.getSemantics().toString());
			}
		}
	}
}