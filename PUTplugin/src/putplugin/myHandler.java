package putplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.rosehulman.sws.protocol.HttpResponseFactory;
import edu.rosehulman.sws.protocol.IHttpResponse;
import edu.rosehulman.sws.protocol.Protocol;
import edu.rosehulman.sws.protocol.HttpRequest;
import edu.rosehulman.sws.protocol.IHandler;
import edu.rosehulman.sws.protocol.ServletHandlerResponse;
import edu.rosehulman.sws.protocol.IHttpResponse;

public class myHandler implements IHandler{

	public void handle(HttpRequest request, ServletHandlerResponse servlet){
		
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);

		servlet.write(response);
	}
}
