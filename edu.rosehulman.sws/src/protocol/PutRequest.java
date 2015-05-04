/*
 * GetRequest.java
 * Apr 24, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
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
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package protocol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import server.Server;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PutRequest extends HttpRequest{
	//String method="PUT";
	String uri;
	String version;
	//Map<String, String> header;
	//char[] body;
	
	public PutRequest(){
		this.method = "PUT";
	}

	@Override
	public IHttpResponse execute(Server server) {
		IHttpResponse response; 
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		//String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		System.out.print("body in execute: ");
		for (int i=0;i<body.length;i++)
			System.out.print(body[i]);
		System.out.println();
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					//if the file exists, append to end of the file
					try{
						FileWriter writer = new FileWriter(rootDirectory + uri,true);
						writer.write(body);
						writer.close();
					}
					catch(IOException e){
						response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
					}
					
					// Lets create 200 OK response
					response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
				}
				else {
					try{
						FileWriter writer = new FileWriter(rootDirectory + uri,false);
						writer.write(body);
						writer.close();
					}
					catch(IOException e){
						response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
					}
					// File does not exist so lets create 404 file not found code
					response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
				}
			}
			else { // Its a file
				
				//if the file exists, append to end of the file
				try{
					FileWriter writer = new FileWriter(rootDirectory + uri,true);
					writer.write(body);
					writer.close();
				}
				catch(IOException e){
					response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
				}
				
				// Lets create 200 OK response
				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
			}
		}
		else {//File doesn't exist
			try{
				file.createNewFile();
				FileWriter writer = new FileWriter(rootDirectory + uri,false);
				writer.write(body);
				writer.close();
			}
			catch(IOException e){
				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
			}
			// File does not exist so lets create 404 file not found code
			response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
		}
		return response;
	}
}
