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
package edu.emory.mathcs.nlp.zzz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author 	Yu-Hsin (Henry) Chen ({@code yu-hsin.chen@emory.edu})
 */
public class Word2VecReader
{
	private DataInputStream din;
	private int demension;
	private boolean to_unit_vector;
	private long count, vocab_size; 
	private byte[] buff = new byte[4];
	
	public Word2VecReader(boolean toUnitVector){
		this.to_unit_vector = toUnitVector;
	}
	
	public Word2VecReader(InputStream in, boolean toUnitVector){
		this.to_unit_vector = toUnitVector;
		open(in);
	}

	public void open(InputStream in){
		count = 0;	din = new DataInputStream(in);
		
		try{
			char c; StringBuilder token = new StringBuilder();
			
			while((c = (char)din.read()) != ' ') token.append(c);
			vocab_size = Long.parseLong(token.toString());
			token.setLength(0);
			
			while((c = (char)din.read()) != '\n') token.append(c);
			demension = Integer.parseInt(token.toString());
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	public void close(){
		try{	din.close(); }
		catch (IOException e) {e.printStackTrace();}
	}
	
	public WordVector next() {
		if(din != null && count++ < vocab_size){
			try {
				char c; StringBuilder token = new StringBuilder();
				while((c = (char)din.read()) != ' ') {
					if(c == -1)		return null;
					else 			token.append(c);
				}
				
				int i; float f; double len = 0; 
				float[] array = new float[demension];
				for(i = 0; i < demension; i++){
					din.read(buff, 0, 4);
					f = ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					array[i] = f;	len += f * f;
				}
				
				if(to_unit_vector){
					Math.sqrt(len);	
					for(i = 0; i < demension; i++)	array[i] /= len;
				}
				
				din.read();	/* Read off NEXT_LINE */
				return new WordVector(token.toString(), array);
				
			} catch (Exception e) { e.printStackTrace(); }
		}
		return null;
	}
	
}