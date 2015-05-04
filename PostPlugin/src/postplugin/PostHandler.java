package postplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class PostHandler implements IHandler{

	public void handle(HttpRequest request, ServletHandlerResponse servlet, Server server){
		
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		HttpResponse response = (HttpResponse) responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
		servlet.setResponse(response);
		try {
			servlet.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		IHttpResponse response; 
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);

		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		String uri = request.getUri().substring(request.getUri().indexOf("/", 1));
		File file = new File(rootDirectory + System.getProperty("file.separator") +  "web" + uri);

		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + request.getUri() + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					//if the file exists, append to end of the file
					try{
						
						FileWriter writer = new FileWriter(rootDirectory + System.getProperty("file.separator") +  "web" + uri,false);
						writer.write(request.getBody());
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
						FileWriter writer = new FileWriter(rootDirectory + System.getProperty("file.separator") +  "web" + uri,false);
						writer.write(request.getBody());
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
					FileWriter writer = new FileWriter(rootDirectory + System.getProperty("file.separator") +  "web" + uri,false);
					writer.write(request.getBody());
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
				FileWriter writer = new FileWriter(rootDirectory + System.getProperty("file.separator") +  "web" + uri,false);
				writer.write(request.getBody());
				writer.close();
			}
			catch(IOException e){
				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
			}
			// File does not exist so lets create 404 file not found code
			response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
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
