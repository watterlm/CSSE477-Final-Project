package applicationplugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class getEventsHandler implements IHandler {

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		System.out.println("get events handler");
		IHttpResponse response;
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		String path = server.getRootDirectory() + System.getProperty("file.separator") + "web" + System.getProperty("file.separator");
		File file = new File(path + "events");
		System.out.println(file.getPath());
		if(file.exists() && file.isDirectory()) {
			JSONParser parser = new JSONParser();
			File[] listFiles = file.listFiles();
			ArrayList<JSONObject> events = new ArrayList<JSONObject>();
			for (int i=0; i< listFiles.length; i++){
				try {
					Object obj = parser.parse(new FileReader(listFiles[i]));
					JSONObject jsonObject = (JSONObject) obj;
					events.add(jsonObject);
					
				}catch(Exception e){
					
				}
			}
			System.out.println("EVENTS:" + events.toString());
			
			response = responseFactory.createResponseFromCache(events.toString(), Protocol.CLOSE, Protocol.OK_CODE);
		}
		else {
			// File does not exist so lets create 404 file not found code
			System.out.println("File does not exist");
			file.mkdir();
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
