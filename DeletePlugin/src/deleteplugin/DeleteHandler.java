package deleteplugin;

import java.io.File;
import java.io.IOException;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class DeleteHandler implements IHandler{

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		IHttpResponse response;
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		//String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		String uri = request.getUri().substring(request.getUri().indexOf("/", 1));
		String directory = rootDirectory + System.getProperty("file.separator") +  "web" + uri;
		File file = new File(directory);
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = directory + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					//Delete the file
					file.delete();
					// Lets create 200 OK response
					response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
				}
				else {
					// File does not exist so lets create 404 file not found code
					response = responseFactory.createResponse(null,Protocol.CLOSE, Protocol.NOT_FOUND_CODE);
				}
			}
			else { // Its a file
				//Delete the file
				file.delete();
				// Lets create 200 OK response
				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = responseFactory.createResponse(null,Protocol.CLOSE,Protocol.NOT_FOUND_CODE);
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
