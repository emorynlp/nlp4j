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
package edu.emory.mathcs.nlp.component.template.train;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TrainInfo
{
	private RollIn  rollin;
	private int     batch_size;
	private int     max_epochs;
	private boolean save_last;
	
	public TrainInfo(int maxEpochs, int batchSize, RollIn rollin, boolean saveLast)
	{
		setMaxEpochs(maxEpochs);
		setBatchSize(batchSize);
		setRollIn(rollin);
		setSaveLast(saveLast);
	}
	
	public int getMaxEpochs()
	{
		return max_epochs;
	}
	
	public void setMaxEpochs(int epochs)
	{
		max_epochs = epochs;
	}
	
	public int getBatchSize()
	{
		return batch_size;
	}

	public void setBatchSize(int size)
	{
		batch_size = size;
	}
	
	public RollIn getRollIn()
	{
		return rollin;
	}
	
	public void setRollIn(RollIn rollin)
	{
		this.rollin = rollin;
	}
	
	public boolean isSaveLast()
	{
		return save_last;
	}
	
	public void setSaveLast(boolean last)
	{
		save_last = last;
	}
}
