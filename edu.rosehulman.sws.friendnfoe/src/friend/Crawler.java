/*
 * Crawler.java
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

package friend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import util.PathUtilities;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Crawler {
	private String host;
	private int port;
	private String seedURI;
	private int threadPool;
	
	private LinkedBlockingQueue<String> queue;
	private HashSet<String> setOfUri;

	private static final String STOP = "STOP";
	private AtomicInteger threadCount;
	
	private ArrayList<CrawlListener> listeners;
	
	/**
	 * @param threadPool
	 * @param seedURI
	 */
	public Crawler(String host, int port, String seedURI, int threadPool) {
		this.host = host;
		this.port = port;
		this.seedURI = seedURI;
		this.threadPool = threadPool;
		
		queue = new LinkedBlockingQueue<String>();
		setOfUri = new HashSet<String>();
		
		threadCount = new AtomicInteger(0);
		listeners = new ArrayList<CrawlListener>();
	}
	
	public boolean addCrawlListener(CrawlListener l) {
		return listeners.add(l);
	}
	
	public boolean removeCrawlListener(CrawlListener l) {
		return listeners.remove(l);
	}
	
	public Set<String> crawl() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(this.threadPool);
	    this.addURI(seedURI);
	    String uri = queue.take();
	    executor.execute(new Spider(uri));
	    
	    while(!queue.isEmpty() || threadCount.get() > 0) {
	    	uri = queue.take();
	    	if(uri != STOP)
	    		executor.execute(new Spider(uri));
	    }
	    
	    executor.shutdown();
	    while (!executor.isTerminated()) {
	    	try {
	    		executor.awaitTermination(500, TimeUnit.MILLISECONDS);
	    	}
	    	catch(Exception e){}
	    }
	    
	    return this.setOfUri;
	}
	
	private boolean addURI(String uri) {
		if(!setOfUri.add(uri))
			return false;

		// If we want to filter - We accept everything now
//		if(uri.endsWith(".htm") || uri.endsWith(".html") || uri.endsWith(".xhtml") || uri.endsWith("/")) {
//			queue.add(uri);
//		}
		
		queue.add(uri);
		
		for(CrawlListener l : listeners) {
			l.directoryAdded(this, uri);
		}
		return true;
	}

	private class Spider implements Runnable {
		private String uri;
		private String dirPath;
		
		public Spider(String uri) {
			this.uri = uri;
			threadCount.incrementAndGet();
		}

		@Override
		public void run() {
			try {
				this.dirPath = PathUtilities.extractDirectoryPath(uri);
				String page = this.getPage();
				this.processPage(page);
			}
			catch(Exception e) {
				for(CrawlListener l : listeners) {
					l.exceptionDetected(Crawler.this, e);
				}
			}
			
			// Lets add dummy STOP to the queue
			if(threadCount.decrementAndGet() == 0) {
				queue.add(STOP);
			}
		}
		
		private String getPage() throws Exception {
			// Open socket connection to the server
			Socket socket = new Socket(host, port);

			// Prepare the request buffer
			StringBuffer buffer = new StringBuffer();
			buffer.append("GET " + uri + " HTTP/1.1");
			buffer.append("\r\n");
			buffer.append("connection: close");
			buffer.append("\r\n");
			buffer.append("accept-language: en-us,en;q=0.5");
			buffer.append("\r\n");
			buffer.append("host: "+ host);
			buffer.append("\r\n");
			buffer.append("accept-charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			buffer.append("\r\n");
			buffer.append("accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			buffer.append("\r\n");
			buffer.append("\r\n");

			// Write the request to the socket
			OutputStream outStream = socket.getOutputStream();
			PrintStream printStream = new PrintStream(outStream);
			printStream.print(buffer.toString());
			printStream.flush();

			// Read and ignore the request
			InputStream inStream = socket.getInputStream();
			InputStreamReader inReader = new InputStreamReader(inStream);
			BufferedReader reader = new BufferedReader(inReader);
			buffer = new StringBuffer();

			String line = reader.readLine();
			if(!line.toLowerCase().contains("200 ok")) {
				socket.close();
				throw new Exception(uri + " => " + line);
			}
			
			buffer.append(line + "\n");
			while((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}

			// Close the socket
			socket.close();
			return buffer.toString();
		}
		
		public void processPage(String page) {
			String lcPage = page.toLowerCase(); // Page in lower case
			int index = 0; // position in page
			int iEndAngle, ihref, iURL, iCloseQuote, iHatchMark, iEnd;
			while ((index = lcPage.indexOf("<a", index)) != -1) {
				iEndAngle = lcPage.indexOf(">", index);
				ihref = lcPage.indexOf("href", index);
				if (ihref != -1) {
					iURL = lcPage.indexOf("\"", ihref) + 1;
					if ((iURL != -1) && (iEndAngle != -1) && (iURL < iEndAngle)) {
						iCloseQuote = lcPage.indexOf("\"", iURL);
						iHatchMark = lcPage.indexOf("#", iURL);
						if ((iCloseQuote != -1) && (iCloseQuote < iEndAngle)) {
							iEnd = iCloseQuote;
							if ((iHatchMark != -1) && (iHatchMark < iCloseQuote))
								iEnd = iHatchMark;
							String newUrlString = page.substring(iURL, iEnd);
							String newURI = PathUtilities.getLocalDomainURI(host, this.dirPath, newUrlString);
							if(newURI != null) {
								addURI(newURI);
							}
						}
					}
				}
				index = iEndAngle;
			}
		}
	}
}
