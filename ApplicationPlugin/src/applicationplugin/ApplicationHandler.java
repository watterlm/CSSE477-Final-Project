package applicationplugin;

import java.io.File;
import java.io.IOException;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class ApplicationHandler implements IHandler {

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		IHttpResponse response;
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		//String path = ApplicationHandler.class.getResource("ApplicationHandler.class").toString();
		//path = path.replace("ApplicationHandler.class", "index.html");
		//System.out.println(path);
		File file = new File(server.getRootDirectory() + System.getProperty("file.separator") + "web" + System.getProperty("file.separator") + "index2.html");
		if(file.exists()) {
				response = responseFactory.createResponse(file, Protocol.CLOSE, Protocol.OK_CODE);
		}
		else {
			// File does not exist so lets create 404 file not found code
			System.out.println("File does not exist");
			response = responseFactory.createResponseFromCache("Hello World!", Protocol.CLOSE,Protocol.NOT_FOUND_CODE);
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
