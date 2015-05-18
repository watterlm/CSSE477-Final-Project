package applicationplugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.XML;

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
			//ArrayList<JSONObject> events = new ArrayList<JSONObject>();
			//String eventsList = "{\"events\": [";
			String eventsList = "";
			for (int i=0; i< listFiles.length; i++){
				
				try {
					Object obj = parser.parse(new FileReader(listFiles[i]));
					JSONObject jsonObject = (JSONObject) obj;
					String thisEvent = "";
					//String xml = XML.toString(jsonObject);
					
					thisEvent += "Title: " + jsonObject.get("Title") + "<br/>";
					thisEvent += "Host: " + jsonObject.get("Host") + "<br/>";
					thisEvent += "Location: " + jsonObject.get("Location") + "<br/>";
					thisEvent += "Time: " + jsonObject.get("Time") + "<br/>";
					thisEvent += "Day: " + jsonObject.get("Day") + "<br/>";
					thisEvent += "<br/>";
					System.out.println("Event " + i);
					/*
					if(i>0)
						eventsList += ",";
					eventsList += jsonObject.toString();
					*/
					eventsList += thisEvent;
					//events.add(jsonObject);
					System.out.println("JSON:" + jsonObject.toString());
				}catch(Exception e){
					System.out.println("Error in getEventsHandler:" + e.toString());
				}
			}
			//eventsList += "]}";
			System.out.println("EVENTS:" + eventsList);
			
			response = responseFactory.createResponseFromCache(eventsList.toString(), Protocol.CLOSE, Protocol.OK_CODE);
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