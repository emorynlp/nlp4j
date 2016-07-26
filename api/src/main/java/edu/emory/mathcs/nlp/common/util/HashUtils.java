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

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class HashUtils
{
	private static final long FNV_BASIS_64	= 0xcbf29ce484222325L;
	private static final long FNV_PRIME_64	= 0x100000001b3L;

	private static final int FNV_BASIS_32	= 0x811c9dc5;
	private static final int FNV_PRIME_32	= 0x01000193;
    
	public static int fnv1aHash32(final String s)
	{
		return fnv1aHash32(s, FNV_BASIS_32);
	}
	
	public static int fnv1aHash32(final String s, int basis)
	{
		char[] cs = s.toCharArray();
		int i, len = s.length();
		
		for (i=0; i<len; i++)
		{
			basis ^= cs[i];
			basis *= FNV_PRIME_32;
		}
		
		return basis;
    }
	
	public static long fnv1aHash64(String s)
	{
		return fnv1aHash64(s, FNV_BASIS_64);
	}

	public static long fnv1aHash64(String s, long basis)
	{
		char[] cs = s.toCharArray();
		int i, len = s.length();
		
		for(i=0; i<len; i++)
		{
			basis ^= cs[i];
			basis *= FNV_PRIME_64;
		}
		
		return basis;
    }
}
