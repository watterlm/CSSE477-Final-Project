/*
 * iHttpRequest.java
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

import java.io.InputStream;
import java.util.Map;

import server.Server;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public interface IHttpRequest {
	

		/**
		 * The request method.
		 * 
		 * @return the method
		 */
		public String getMethod();
		/**
		 * The URI of the request object.
		 * 
		 * @return the uri
		 */
		public String getUri();

		/**
		 * The version of the http request.
		 * @return the version
		 */
		public String getVersion();
		
		public char[] getBody();

		/**
		 * The key to value mapping in the request header fields.
		 * 
		 * @return the header
		 */
		public Map<String, String> getHeader();

		/**
		 * Reads raw data from the supplied input stream and constructs a 
		 * <tt>HttpRequest</tt> object out of the raw data.
		 * 
		 * @param inputStream The input stream to read from.
		 * @return A <tt>HttpRequest</tt> object.
		 * @throws Exception Throws either {@link ProtocolException} for bad request or 
		 * {@link IOException} for socket input stream read errors.
		 */
		//public HttpRequest read(InputStream inputStream) throws Exception;
		
		
		@Override
		public String toString();
		
		public HttpResponse execute(Server server);
	}
