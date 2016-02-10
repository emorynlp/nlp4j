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
package edu.emory.mathcs.nlp.common.collection.obj;


/**
 * @author Amit-Deshmane
 * This class tracks the value of index variable.
 */
public class Index {
	int val;
	/**
	 * 
	 */
	public Index() {
		
	}
	public Index(int val){
		this.val = val;
	}
	public int getVal(){
		return val;
	}
	public void setVal(int val){
		this.val = val;
	}
	public String toString(){
		return Integer.toString(val);
	}
	public boolean equals(Object obj){
		if(!Index.class.isInstance(obj)){
			return false;
		}
		else{
			Index input = (Index)obj;
			if(input.getVal() == val){
				return true;
			}
		}
		return false;
	}
	public int hashCode(){
		int prime = 31;
		int result = 1;
		result = result*prime + val;
		return result;
	}
}
