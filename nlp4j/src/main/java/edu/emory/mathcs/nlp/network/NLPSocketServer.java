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
package edu.emory.mathcs.nlp.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.decode.NLPDecoder;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPSocketServer
{
	static public final String END = "!E@N#D$"; 
	private NLPDecoder decoder;
	
	@SuppressWarnings("resource")
	public NLPSocketServer(InputStream configuration, int port, int threads) throws Exception
	{
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		ServerSocket server = new ServerSocket(port);
		Socket client;
		
		decoder = new NLPDecoder(configuration);
		System.out.println("Listening...");
		
		while (true)
		{
			client = server.accept();
			executor.submit(new NLPTask(client));
		}

//		executor.shutdown();
//		server.close();
	}
	
	class NLPTask implements Runnable 
	{
		OutputStream out;
		InputStream  in;
		Socket client;
		
		public NLPTask(Socket client)
		{
			try
			{
				in  = new DataInputStream (new BufferedInputStream (client.getInputStream()));
				out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
				this.client = client;
				System.out.println(client.getInetAddress().toString());
			}
			catch (IOException e) {e.printStackTrace();}
		}
		
		@Override
		public void run()
		{
			StringBuilder build = new StringBuilder();
			byte[] buffer = new byte[2048];
			String s, format;
			int i, idx;
			
			try
			{
				while ((i = in.read(buffer, 0, buffer.length)) >= 0)
				{
					build.append(new String(buffer, 0, i));
					
					if (build.toString().endsWith(END))
					{
						idx = build.indexOf(":");
						format = build.substring(0, idx);
						s = build.substring(idx+1, build.length()-END.length());
						out.write(decoder.decodeByteArray(s, format));
						out.close();
						in.close();
						break;
					}
				}
			}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		final String configFile = args[0];
		final int port = Integer.parseInt(args[1]);
		final int threads = Integer.parseInt(args[2]);
		new NLPSocketServer(IOUtils.createFileInputStream(configFile), port, threads);
	}
}
