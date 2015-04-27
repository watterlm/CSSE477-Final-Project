/*
 * HttpResponseFactory.java
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
 
package protocol;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a factory to produce various kind of HTTP responses.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponseFactory {
	private static Map<String, Object> classMap;
	
	public HttpResponseFactory(){
		classMap = new HashMap<String, Object>();
		classMap.put(Integer.toString(Protocol.OK_CODE), new OkResponse());
		classMap.put(Integer.toString(Protocol.MOVED_PERMANENTLY_CODE), new MovedPermanentlyResponse());
		classMap.put(Integer.toString(Protocol.NOT_MODIFIED_CODE), new NotModifiedResponse());
		classMap.put(Integer.toString(Protocol.BAD_REQUEST_CODE), new BadRequestResponse());
		classMap.put(Integer.toString(Protocol.NOT_FOUND_CODE), new NotFoundResponse());
		classMap.put(Integer.toString(Protocol.NOT_SUPPORTED_CODE), new NotSupportedResponse());
		classMap.put(Integer.toString(Protocol.INTERNAL_ERROR_CODE), new InternalErrorResponse());
	}
	
	/**
	 * Convenience method for adding general header to the supplied response object.
	 * 
	 * @param response The {@link HttpResponse} object whose header needs to be filled in.
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 */
	private static void fillGeneralHeader(IHttpResponse response, String connection) {
		// Lets add Connection header
		response.put(Protocol.CONNECTION, connection);

		// Lets add current date
		Date date = Calendar.getInstance().getTime();
		response.put(Protocol.DATE, date.toString());
		
		// Lets add server info
		response.put(Protocol.Server, Protocol.getServerInfo());

		// Lets add extra header with provider info
		response.put(Protocol.PROVIDER, Protocol.AUTHOR);
	}
	
	
	/**
	 * Creates a {@link HttpResponse} object for sending the supplied file with supplied connection
	 * parameter.
	 * 
	 * @param file The {@link File} to be sent.
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @param responseCode The Protocol response code for the current response.
	 * @return A {@link HttpResponse} object represent 200 status.
	 */	
	public static IHttpResponse createResponse(File file, String connection, int responseCode){
		IHttpResponse response = null;
		
		// Determine response to create based on response code. Default will return an internal error
		response = (IHttpResponse) classMap.get(Integer.toString(responseCode));
		if (response == null){
			response = new InternalErrorResponse();
		}
		
		// Update the file since all are initiated with a null file
		response.setFile(file);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		// Add response specific headers
		response.initiateSpecificHeaders();
				
		return response;
	}
}
