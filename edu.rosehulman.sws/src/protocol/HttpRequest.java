/*
 * HttpRequest.java
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Represents a request object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public abstract class HttpRequest implements IHttpRequest {
	
	String method="";
	String uri;
	String version;
	Map<String, String> header;
	char[] body;
	
	public HttpRequest() {
		this.header = new HashMap<String, String>();
		this.body = new char[0];
	}
	
	/**
	 * The request method.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * The URI of the request object.
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * The version of the http request.
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	public char[] getBody() {
		return body;
	}

	/**
	 * The key to value mapping in the request header fields.
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifable view of the header map
		return Collections.unmodifiableMap(header);
	}

	/**
	 * Reads raw data from the supplied input stream and constructs a 
	 * <tt>HttpRequest</tt> object out of the raw data.
	 * 
	 * @param inputStream The input stream to read from.
	 * @return A <tt>HttpRequest</tt> object.
	 * @throws Exception Throws either {@link ProtocolException} for bad request or 
	 * {@link IOException} for socket input stream read errors.
	 */
	public abstract HttpRequest read(InputStream inputStream) throws Exception;
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------- Header ----------------\n");
		buffer.append(this.method);
		buffer.append(Protocol.SPACE);
		buffer.append(this.uri);
		buffer.append(Protocol.SPACE);
		buffer.append(this.version);
		buffer.append(Protocol.LF);
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		buffer.append("------------- Body ---------------\n");
		buffer.append(this.body);
		buffer.append("----------------------------------\n");
		return buffer.toString();
	}
}