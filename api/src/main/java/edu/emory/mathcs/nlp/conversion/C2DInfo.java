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
package edu.emory.mathcs.nlp.conversion;

import edu.emory.mathcs.nlp.common.treebank.CTNode;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;

/**
 * Constituent to dependency information.
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class C2DInfo
{
	private CTNode  t_head;
	private CTNode  n_head;
	private boolean b_head;
	private String  s_label;
	private FeatMap d_feats;
	
	/** Initializes the constituent and dependency heads. */
	public C2DInfo(CTNode head)
	{
		s_label = null;
		b_head  = false;
		
		if (head.hasC2DInfo())
		{
			t_head = head.getC2DInfo().getTerminalHead();
			n_head = head;
		}
		else
		{
			t_head  = head;
			n_head  = null;
			d_feats = new FeatMap();
		}
	}
	
	public void setTerminalHead(CTNode head)
	{
		t_head = head;
	}
	
	public void setNonTerminalHead(CTNode head)
	{
		n_head = head;
	}
	
	public void setLabel(String label)
	{
		if (n_head == null)
			s_label = label;
		else
			t_head.getC2DInfo().setLabel(label);
	}
	
	/** Sets terminal heads for sibling constituents */
	public void setHead(CTNode head, String label)
	{
		setHead(head, label, false);
	}
	
	/** Sets terminal heads for sibling constituents */
	public void setHead(CTNode head, String label, boolean terminal)
	{
		if (terminal)
			t_head.getC2DInfo().setTerminalHead(head);
		else
			t_head.getC2DInfo().setTerminalHead(head.getC2DInfo().getTerminalHead());
		
		setLabel(label);
		b_head = true;
	}
	
	public String putFeat(String key, String value)
	{
		return t_head.getC2DInfo().d_feats.put(key, value);
	}
	
	public CTNode getTerminalHead()
	{
		return t_head;
	}
	
	public CTNode getNonTerminalHead()
	{
		return n_head;
	}
	
	public String getLabel()
	{
		return s_label;
	}
	
	public String getFeat(String key)
	{
		return d_feats.get(key);
	}
	
	public FeatMap getFeatMap()
	{
		return d_feats;
	}
	
	public boolean hasHead()
	{
		return b_head;
	}
}