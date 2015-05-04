package putplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.InternalErrorResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class myHandler implements IHandler {

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		IHttpResponse response;
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);

		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		String uri = request.getUri().substring(request.getUri().indexOf("/", 1));
		String directory = rootDirectory + System.getProperty("file.separator") +  "web" + uri;
		
		File file = new File(directory);
		System.out.println("Looking for file at:");
		System.out.println(file.getPath());
		System.out.println();

		for (int i = 0; i < request.getBody().length; i++)
			System.out.print(request.getBody()[i]);
		System.out.println();
		// Check if the file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = directory + System.getProperty("file.separator")
						+ Protocol.DEFAULT_FILE;
				file = new File(location);
				if (file.exists()) {
					// if the file exists, append to end of the file
					try {
						FileWriter writer = new FileWriter(location,
								true);
						writer.write(request.getBody());
						writer.close();
					} catch (IOException e) {
						response = responseFactory.createResponse(null,
								Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
					}

					// Lets create 200 OK response
					response = responseFactory.createResponse(null,
							Protocol.CLOSE, Protocol.OK_CODE);
				} else {
					try {
						FileWriter writer = new FileWriter(location,
								false);
						writer.write(request.getBody());
						writer.close();
					} catch (IOException e) {
						response = responseFactory.createResponse(null,
								Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
					}
					// File does not exist so lets create 404 file not found
					// code
					response = responseFactory.createResponse(null,
							Protocol.CLOSE, Protocol.OK_CODE);
				}
			} else { // Its a file

				// if the file exists, append to end of the file
				try {
					FileWriter writer = new FileWriter(directory,
							true);
					writer.write(request.getBody());
					writer.close();
				} catch (IOException e) {
					response = responseFactory.createResponse(null,
							Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
				}

				// Lets create 200 OK response
				response = responseFactory.createResponse(null, Protocol.CLOSE,
						Protocol.OK_CODE);
			}
		} else {// File doesn't exist
			try {
				file.createNewFile();
				FileWriter writer = new FileWriter(directory, false);
				writer.write(request.getBody());
				writer.close();
			} catch (IOException e) {
				response = responseFactory.createResponse(null, Protocol.CLOSE,
						Protocol.BAD_REQUEST_CODE);
			}
			// File does not exist so lets create 404 file not found code
			response = responseFactory.createResponse(null, Protocol.CLOSE,
					Protocol.OK_CODE);
		}

		servlet.setResponse(response);
		try {
			servlet.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
