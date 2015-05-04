package putplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.Protocol;
import protocol.ServletHandlerResponse;

public class myHandler implements IHandler{

	public void handle(HttpRequest request, ServletHandlerResponse servlet){
		
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
		servlet.setResponse(response);
		servlet.write(response);
	}
}
