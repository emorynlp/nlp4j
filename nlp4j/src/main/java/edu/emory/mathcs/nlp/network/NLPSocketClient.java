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
package edu.emory.mathcs.nlp.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPSocketClient
{
	private final String SERVER_ADDRESS;
	private final int    SERVER_PORT;
	
	public NLPSocketClient(String address, int port)
	{
		SERVER_ADDRESS = address;
		SERVER_PORT    = port;
	}
	
	public String decode(String text, String format)
	{
		StringBuilder build = new StringBuilder();
		
		try
		{
			String data = format+":"+text+NLPSocketServer.END;
			Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			InputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			OutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			out.write(data.getBytes());
			out.flush();
			
			byte[] buffer = new byte[2048];
			int i;
			
			while ((i = in.read(buffer, 0, buffer.length)) >= 0)
			{
				build.append(new String(buffer, 0, i));
				if (build.toString().endsWith(NLPSocketServer.END)) break;
			}
			
			socket.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return build.toString();
	}
	
	static public void main(String[] args)
	{
		NLPSocketClient client = new NLPSocketClient("127.0.0.1", 8000);
		System.out.println(client.decode("UN peacekeepers abuse children", "raw"));
	}
}
