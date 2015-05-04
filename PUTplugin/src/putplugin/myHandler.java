package putplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.InternalErrorResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;

public class myHandler implements IHandler{

	public void handle(IHttpRequest request, ServletHandlerResponse servlet) throws IOException{
		
		IHttpResponse response = new InternalErrorResponse();
		servlet.setResponse(response);
		servlet.write();
	}
}
