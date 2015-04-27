/*
 * IHttpResponse.java
 * Apr 26, 2015
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
import java.io.OutputStream;
import java.util.Map;

/**
 * 
 * @author Lindsey Watterson
 */

public interface IHttpResponse {
	
	/**
	 * Gets the version of the HTTP.
	 * 
	 * @return the version
	 */
	public String getVersion();
	
	/**
	 * Gets the status code of the response object.
	 * @return the status
	 */
	public int getStatus();
	
	/**
	 * Gets the status phrase of the response object.
	 * 
	 * @return the phrase
	 */
	public String getPhrase();
	
	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public File getFile();
	
	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public void setFile(File file);
	
	/**
	 * Returns the header fields associated with the response object.
	 * @return the header
	 */
	public Map<String, String> getHeader();
	
	/**
	 * Maps a key to value in the header map.
	 * @param key A key, e.g. "Host"
	 * @param value A value, e.g. "www.rose-hulman.edu"
	 */
	public void put(String key, String value);
	
	/**
	 * Automatically maps specific key to value in the header map.
	 */
	public void initiateSpecificHeaders();
	
	/**
	 * Writes the data of the http response object to the output stream.
	 * 
	 * @param outStream The output stream
	 * @throws Exception
	 */
	public void write(OutputStream outStream) throws Exception;
	
	@Override
	public String toString();
	
}
