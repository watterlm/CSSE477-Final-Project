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
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import server.Server;

/**
 * This is a factory to produce various kind of HTTP responses.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponseFactory {
	private static Map<String, Map<String, IHandler>> classMap;
	private String pluginDirectory;

	// filter to identify files based on their extensions
	final FilenameFilter JAR_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(final File dir, final String name) {
			{
				if (name.endsWith(".jar")) {
					return (true);
				}
				return (false);
			}
		};
	};

	public HttpResponseFactory(Server server) {
		// TODO: Remove left for reference
		// classMap = new HashMap<Integer, Object>();
		// classMap.put(Protocol.OK_CODE, new OkResponse());
		// classMap.put(Protocol.MOVED_PERMANENTLY_CODE, new
		// MovedPermanentlyResponse());
		// classMap.put(Protocol.NOT_MODIFIED_CODE, new NotModifiedResponse());
		// classMap.put(Protocol.BAD_REQUEST_CODE, new BadRequestResponse());
		// classMap.put(Protocol.NOT_FOUND_CODE, new NotFoundResponse());
		// classMap.put(Protocol.NOT_SUPPORTED_CODE, new
		// NotSupportedResponse());
		// classMap.put(Protocol.INTERNAL_ERROR_CODE, new
		// InternalErrorResponse());
		String rootDirectory = server.getRootDirectory();
		pluginDirectory = rootDirectory + System.getProperty("file.separator")
				+ "plugin";

	}

	public void findPlugins() {
		File pluginFolder = new File(pluginDirectory);

		try {
			System.out.println("finding plugins");
			for (File f : pluginFolder.listFiles(JAR_FILTER)) {
				ClassLoader pluginLoader;
				System.out.println("Name:" + f.getName());
				pluginLoader = URLClassLoader.newInstance(new URL[] { f.toURI()
						.toURL() });

				JarFile jf = new JarFile(f.getPath());

				Manifest manifest = jf.getManifest();
				// Attributes att = manifest.getMainAttributes();
				// String pluginClassName = att.getValue("Plugin-Name");
				// System.out.println("Plugin Class Name  "+pluginClassName);
				// IPlugin plugin;
				//
				// plugin = (IPlugin) pluginLoader.loadClass(
				// pluginClassName).newInstance();
				// System.out.println("Plugin Title: "+plugin.getTitle());
				// plugins.add(plugin);
				// Platform p = this;
				// plugin.setPlatform(p);

			}
		} catch (Exception e) {

			System.out.println("Error in loadPluginList: " + e.getMessage());
		}
	}

	/**
	 * Convenience method for adding general header to the supplied response
	 * object.
	 * 
	 * @param response
	 *            The {@link HttpResponse} object whose header needs to be
	 *            filled in.
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 */
	private static void fillGeneralHeader(IHttpResponse response,
			String connection) {
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
	 * Creates a {@link HttpResponse} object for sending the supplied file with
	 * supplied connection parameter.
	 * 
	 * @param file
	 *            The {@link File} to be sent.
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 * @param responseCode
	 *            The Protocol response code for the current response.
	 * @return A {@link HttpResponse} object represent 200 status.
	 */
	public IHttpResponse createResponse(File file, String connection,
			int responseCode) {
		IHttpResponse response = null;

		// Determine response to create based on response code. Default will
		// return an internal error
		response = (IHttpResponse) classMap.get(responseCode);
		if (response == null) {
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


	
	
	
	public void handle(Server server, IHttpResponse response) {
		// TODO pass this response on the appropriate IHandler

	}
	
	
}
