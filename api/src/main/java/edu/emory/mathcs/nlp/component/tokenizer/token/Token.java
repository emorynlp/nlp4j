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
package edu.emory.mathcs.nlp.component.tokenizer.token;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Token
{
	protected String word_form;
	protected int    start_offset;
    protected int    end_offset;
    
    public Token(String form)
    {
    	this(form, -1, -1);
    }
    
    public Token(String form, int startOffset, int endOffset)
    {
    	setStartOffset(startOffset);
    	setEndOffset(endOffset);
    	setWordForm(form);
    }

    public String getWordForm()
	{
		return word_form;
	}
	
    public void setWordForm(String form)
	{
		this.word_form = form;
	}
	
    public int getStartOffset()
	{
		return start_offset;
	}
	
    public void setStartOffset(int offset)
	{
		this.start_offset = offset;
	}
	
    public int getEndOffset()
	{
		return end_offset;
	}
	
    public void setEndOffset(int offset)
	{
		this.end_offset = offset;
	}
    
    public void resetEndOffset()
    {
    	setEndOffset(start_offset+word_form.length());
    }
    
    public boolean isWordForm(String form)
    {
    	return word_form.equals(form);
    }
    
    @Override
    public String toString()
    {
    	return word_form;
    }
}
