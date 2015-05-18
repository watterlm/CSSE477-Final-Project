package applicationplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class addEventHandler implements IHandler {

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		IHttpResponse response; 
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);

		String path = server.getRootDirectory() + System.getProperty("file.separator") + "web" + System.getProperty("file.separator") + "events" + System.getProperty("file.separator");
		
		System.out.println(request.getBody());
		File file = new File(path + "");

		
//			try{
//				if (file.exists()){
//					file.delete();
//				}
//				file.createNewFile();
//				FileWriter writer = new FileWriter(file,false);
//				writer.write(request.getBody());
//				writer.close();
//			}
//			catch(IOException e){
//				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
//			}
			// File does not exist so lets create 404 file not found code
			response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
		
		servlet.setResponse(response);
		try {
			servlet.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
