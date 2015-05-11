/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
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
 
package server;

import gui.WebServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import protocol.HttpRequestFactory;
import protocol.HttpResponseFactory;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ProtocolException;
import protocol.User;

/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	private String backupLocation;
	private int backupNumber;
	private String backupFolder;
	private String[] systemFiles = { "index.html", "upload.html" };
	
	private long connections;
	private long serviceTime;
	private Map<String,User> users;
	
	// Cache Format: Filename, [String body of file, String representation of a long for time last accessed]
	private Map<String, ArrayList<String>> cacheDictionary;
	
	// Access Format: Filename, [String representation of a long for time last accessed, String representation of times accessed]
	private static Map<String, ArrayList<String>> accessTracker;
	
	private WebServer window;
	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port, WebServer window) {
		this.rootDirectory = rootDirectory;
		this.backupLocation = rootDirectory + System.getProperty("file.separator") + "backup";
		this.backupNumber = 1;
		this.backupFolder = rootDirectory + System.getProperty("file.separator") + "web";
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
		this.window = window;
	
		users = new HashMap<String, User>();
		cacheDictionary = new HashMap<String, ArrayList<String>>();
		accessTracker = new HashMap<String, ArrayList<String>>();
		
		// Schedule the backups for every three hours and run the first backup
		ScheduledExecutorService backupSES = Executors.newSingleThreadScheduledExecutor();
		backupSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Backing up");
				backup();
			}
		}, 0, 3, TimeUnit.HOURS);
		
		// Schedule the change/deleteion of system files to run every 20 sec and delay the first run by 20 secs
		ScheduledExecutorService fileCheckerSES = Executors.newSingleThreadScheduledExecutor();
		fileCheckerSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Checking System Files");
				for (String file : systemFiles){
					checkFileChangeOrDelete(file);
				}
			}
		}, 20, 20, TimeUnit.SECONDS);
		
		// Schedule the cache cleaning for every 5 seconds to attempt to capture the cache not being used for 10 seconds. Delayed by 5 seconds.
		ScheduledExecutorService cacheCleanerSES = Executors.newSingleThreadScheduledExecutor();
		cacheCleanerSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Cleaning Cache");
				cleanCache();
			}
		}, 5, 5, TimeUnit.SECONDS);
		
		// Schedule the cache cleaning for every 5 seconds to attempt to capture the cache not being used for 10 seconds. Delayed by 5 seconds.
		ScheduledExecutorService accessCleanerSES = Executors.newSingleThreadScheduledExecutor();
		accessCleanerSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				cleanAccess();
			}
		}, 10, 10, TimeUnit.SECONDS);
		
		// Schedule running additional requests every .1 seconds. Delayed by 1 second.
		ScheduledExecutorService requestQueueSES = Executors.newSingleThreadScheduledExecutor();
		accessCleanerSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				while(handleNextRequest());
			}
		}, 1000, 100, TimeUnit.MILLISECONDS);
		
		// Schedule running additional requests every 1 minute. Delayed by 1 minute.
		ScheduledExecutorService clearUserHistorySES = Executors.newSingleThreadScheduledExecutor();
		accessCleanerSES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				clearUserHistory();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}


	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns connections serviced per second. 
	 * Synchronized to be used in threaded environment.
	 * 
	 * @return
	 */
	public synchronized double getServiceRate() {
		if(this.serviceTime == 0)
			return Long.MIN_VALUE;
		double rate = this.connections/(double)this.serviceTime;
		rate = rate * 1000;
		return rate;
	}
	
	/**
	 * Increments number of connection by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementConnections(long value) {
		this.connections += value;
	}
	
	/**
	 * Increments the service time by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementServiceTime(long value) {
		this.serviceTime += value;
	}

	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(port);
			
			// Now keep welcoming new connections until stop flag is set to true
			while(true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				
				// Come out of the loop if the stop flag is set
				if(this.stop)
					break;
				
				
				
				//New functionality from connectionHandler
				OutputStream outStream = null;
				InputStream inStream = null;
				try {
					inStream = connectionSocket.getInputStream();
					outStream = connectionSocket.getOutputStream();
				}
				catch(Exception e) {
					// Cannot do anything if we have exception reading input or output stream
					// May be have text to log this for further analysis?
					e.printStackTrace();
					
					// Increment number of connections by 1
					this.incrementConnections(1);
					// Get the end time
					long end = System.currentTimeMillis();
					return;
				}
				
				IHttpRequest request = null;
				HttpRequestFactory requestFactory = new HttpRequestFactory(this);
				try {
					
					request = requestFactory.read(inStream);
					System.out.println(request);
				}
				catch(ProtocolException pe) {
					// We have some sort of protocol exception. Get its status code and create response
					// We know only two kind of exception is possible inside fromInputStream
					// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
					int status = pe.getStatus();
					if(status == Protocol.BAD_REQUEST_CODE) {
						HttpResponseFactory responseFactory = new HttpResponseFactory(this);
						IHttpResponse response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
						try {
							response.write(outStream);
//							System.out.println(response);
						}
						catch(Exception e){
							// We will ignore this exception
							e.printStackTrace();
						}

						// Increment number of connections by 1
						this.incrementConnections(1);
						return;
					}
					// TODO: Handle version not supported code as well
				}
				////////////////////////////
			
				// Create a handler for this incoming connection and start the handler in a new thread
				ConnectionHandler handler = new ConnectionHandler(this, connectionSocket, request);
				
				//System.out.println("IP:"+connectionSocket.getInetAddress().toString());
				String ip = connectionSocket.getInetAddress().toString();
				System.out.println(ip);
				if(users.containsKey(ip)){
					System.out.println("old user");
					User oldUser = users.get(ip);
					if(oldUser.addToQueue(handler)){
						oldUser.incrementRequest();
						System.out.println("Old user numRequests:" + oldUser.getRequests());
					}
					else{
						//exceeded queue size
						HttpResponseFactory responseFactory = new HttpResponseFactory(this);
						IHttpResponse response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
						try {
							response.write(outStream);
						}
						catch(Exception e){
							// We will ignore this exception
							e.printStackTrace();
						}

						// Increment number of connections by 1
						this.incrementConnections(1);
						return;
					}
				}
				else{
					System.out.println("new user");
					User newUser = new User(ip);
					System.out.println("check");
					newUser.addToQueue(handler);
					newUser.incrementRequest();
					
					users.put(ip, newUser);
					
				}
				//Execute handler
				//new Thread(handler).start();
				//handleNextRequest();
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			window.showSocketException(e);
		}
	}
	
	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if(this.stop)
			return;
		
		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept() method 
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);
			
			// We do not have any other job for this socket so just close it
			socket.close();
		}
		catch(Exception e){}
	}
	
	/**
	 * Checks if the server is stopeed or not.
	 * @return
	 */
	public boolean isStoped() {
		if(this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}
	
	private void backup(){
		// Get the current directory to back up and the directory to back up to
		File folderToBackup = new File(this.backupFolder);
		File backupFolder = new File(this.backupLocation + this.backupNumber);
		
		// Increment backup number so that the next time it stores it in the next backup folder
		// There are 3 total backup folders
		this.backupNumber++;
		if (this.backupNumber > 3){
			this.backupNumber = 1;
		}

		// Copy the current directory to the backup location.
		try {
		    FileUtils.copyDirectory(folderToBackup, backupFolder);
		} catch (IOException e) {
			System.out.println("Error in backup: " + e.getMessage());
		}
	}
	
	private void checkFileChangeOrDelete(String fileName){
		String filepath = backupFolder + System.getProperty("file.separator") + fileName;
		File currentFile = new File(filepath);
		
		// Get the backed up version
		int num = this.backupNumber;
		num--;
		if (num < 1){
			num = 3;
		}
		
		// Get the backed up file
		File lastBackup = new File(this.backupLocation + num + System.getProperty("file.separator") + fileName);
		
		// Check if the current system file exists
		if(currentFile.exists()){
			// Get the last modified dates of each file
			Long currentLastModified = currentFile.lastModified();
			Long lastLastModified = lastBackup.lastModified();
			
			// If the current file has been modified restore it
			if(currentLastModified > lastLastModified){
				System.out.println("Detected change in: " + fileName);
				restoreChangedOrDeletedFile(currentFile, lastBackup);
			}
		} else {
			// It was deleted so restore it
			System.out.println("Detected deletion of: " + fileName);
			restoreChangedOrDeletedFile(currentFile, lastBackup);
		}
		
		
	}
	
	private void restoreChangedOrDeletedFile(File currentFile, File backupFile){
		// Check to see if the currentFile is null (
		if (currentFile.exists()){
			currentFile.delete();
		}
		// Get the directory to restore to
		File currentDirectory = new File(currentFile.getPath());
		try {
			// Copy the backup file to the current directory
			FileUtils.copyFile(backupFile, currentDirectory);
			System.out.println("Restored file: " + currentFile.getPath());
		} catch (IOException e) {
			System.out.println("Error in restoring file: " + e.getMessage());
		}
	}
	
	private void cleanCache(){
		for (String key: cacheDictionary.keySet()){
			ArrayList<String> values = cacheDictionary.get(key);
			Long lastAccessed = Long.parseLong(values.get(1));
			Long currentTime = System.currentTimeMillis();
			int differenceInSeconds = (int)((currentTime - lastAccessed)/1000);
			if(differenceInSeconds >= 10){
				System.out.println("Removed from cache: " + key);
				cacheDictionary.remove(key);
			}
		}
	}
	
	private void cleanAccess(){
		for (String key: accessTracker.keySet()){
			ArrayList<String> values = accessTracker.get(key);
			Long lastAccessed = Long.parseLong(values.get(0));
			Long currentTime = System.currentTimeMillis();
			int differenceInSeconds = (int)((currentTime - lastAccessed)/1000);
			if(differenceInSeconds >= 10){
				accessTracker.remove(key);
			}
		}
	}
	
	public void addToCache(String filename, String body){
		ArrayList<String> values = new ArrayList<String>();
		values.add(body);
		values.add(System.currentTimeMillis() + "");
		cacheDictionary.put(filename, values);
		System.out.println("Added key to cache: " + filename);
	}
	
	public Map<String, ArrayList<String>> getAccessTracker(){
		return accessTracker;
	}
	
	public void updateAccessTracker(Map<String, ArrayList<String>> map){
		accessTracker = map;
		System.out.println(accessTracker);
	}
	
	public Boolean isCached(String filename){
		return cacheDictionary.containsKey(filename);
	}
	
	public String getBodyFromCache(String filename){
		ArrayList<String> attributes = cacheDictionary.get(filename);
		String body = attributes.get(0);
		return body;
	}
	
	private boolean handleNextRequest(){
		if(users.size() < 1)
			return false;
		//System.out.println("checking for next user");
		
		
		int min=1000;
		User best = null;
		
		for (User nextUser: users.values())
		{
			int num = nextUser.getNumRequestsQueued();
			if(num < min && !nextUser.isOverLimit()){
				best = nextUser;
				min = num;
			}
		}
		
		if(best==null)
			return false;
		ConnectionHandler handler = best.getNextRequest();
		if(handler == null)
			return false;
		new Thread(handler).start();
		return true;
	}
	
	private void clearUserHistory(){
		for (User nextUser: users.values())
		{
			nextUser.clearRequests();
		}
	}
}
