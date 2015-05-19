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

public class searchEventsHandler implements IHandler {

	@Override
	public void handle(IHttpRequest request, ServletHandlerResponse servlet,
			Server server) throws IOException {
		System.out.println("search events handler");
		
		//System.out.println("REQUEST: \n" + request.toString());
		String searchTerm = "";
		try{
			searchTerm = request.getHeader().get("searchterm");
		}
		catch(Exception e){
			searchTerm = "";
			System.out.println("Error getting search term!");
		}
		//System.out.println("Search Term: [" + searchTerm+ "]");
		
		IHttpResponse response;
		HttpResponseFactory responseFactory = new HttpResponseFactory(server);
		String path = server.getRootDirectory() + System.getProperty("file.separator") + "web" + System.getProperty("file.separator");
		File file = new File(path + "events");
		//System.out.println(file.getPath());
		if(file.exists() && file.isDirectory()) {
			JSONParser parser = new JSONParser();
			File[] listFiles = file.listFiles();
			String eventsList = "";
			for (int i=0; i< listFiles.length; i++){
				
				try {
					Object obj = parser.parse(new FileReader(listFiles[i]));
					JSONObject jsonObject = (JSONObject) obj;
					String thisEvent = "";
					
					
					thisEvent += "Title: " + jsonObject.get("Title") + "<br/>";
					thisEvent += "Host: " + jsonObject.get("Event_host") + "<br/>";
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
					if(thisEvent.toLowerCase().contains(searchTerm.toLowerCase()))
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
