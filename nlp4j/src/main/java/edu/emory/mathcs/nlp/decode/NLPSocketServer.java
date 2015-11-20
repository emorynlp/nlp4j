/**
// * Copyright 2015, Emory University
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
package edu.emory.mathcs.nlp.decode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.emory.mathcs.nlp.common.util.IOUtils;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPSocketServer
{
	static public final int PORT = 8080;
	static public final String END = "!E@N#D$"; 
	
	@SuppressWarnings("resource")
	public NLPSocketServer(InputStream configuration) throws Exception
	{
		NLPDecoder decoder = new NLPDecoder(configuration);
		ServerSocket server = new ServerSocket(PORT);
		StringBuilder build = new StringBuilder();
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream bout;
		ByteArrayInputStream bin;
		DataOutputStream out;
		DataInputStream in;
		String s, format;
		Socket client;
		int i;
		
		while (true)
		{
			client = server.accept();
			System.out.println(client.getInetAddress().toString());
			in  = new DataInputStream (new BufferedInputStream (client.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			build = new StringBuilder();
			
			while ((i = in.read(buffer, 0, buffer.length)) >= 0)
			{
				build.append(new String(buffer, 0, i));
				
				if (build.toString().endsWith(END))
				{
					format = build.substring(0, 3);
					s = build.substring(3, build.length()-END.length());
					bin  = new ByteArrayInputStream(s.getBytes());
					bout = new ByteArrayOutputStream();
					decoder.decode(bin, bout, format);
					out.write(bout.toByteArray());
					out.flush();
					build = new StringBuilder();
				}
			}
		}
		
//		server.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		final String configFile = args[0];
		new NLPSocketServer(IOUtils.createFileInputStream(configFile));
	}
}
