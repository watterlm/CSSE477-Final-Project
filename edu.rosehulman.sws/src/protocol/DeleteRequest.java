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

import java.io.InputStream;
import java.util.Map;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class DeleteRequest extends HttpRequest{
	String method="";
	String uri;
	String version;
	Map<String, String> header;
	char[] body;
	
	public DeleteRequest(){
		
	}
	@Override
	public HttpRequest read(InputStream inputStream) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see protocol.IHttpRequest#getMethod()
	 */
	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see protocol.IHttpRequest#getUri()
	 */
	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see protocol.IHttpRequest#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see protocol.IHttpRequest#getBody()
	 */
	@Override
	public char[] getBody() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see protocol.IHttpRequest#getHeader()
	 */
	@Override
	public Map<String, String> getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
