package applicationplugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
		
		
		String title = "";
		String time = "";
		String day = "";
		String location = "";
		String eventHost = "";
		try{
			title = request.getHeader().get("title");
			time = request.getHeader().get("time");
			day = request.getHeader().get("day");
			location = request.getHeader().get("location");
			eventHost = request.getHeader().get("event_host");
		}
		catch(Exception e){
			
			System.out.println("Error getting header!");
		}
		
		JSONObject event = new JSONObject();
		event.put("Title", title);
		event.put("Time", time);
		event.put("Day", day);
		event.put("Location", location);
		event.put("Event_host", eventHost);
		
		File file = new File(path + eventHost.toString().toLowerCase() + title.toString().toLowerCase());
//		
		try{
			if (file.exists()){
				file.delete();
			}
			file.createNewFile();
			FileWriter writer = new FileWriter(file,false);
			writer.write(event.toJSONString());
			writer.close();
			
			path = server.getRootDirectory() + System.getProperty("file.separator") + "web" + System.getProperty("file.separator");
			file = new File(path + "events");
			System.out.println(file.getPath());
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
						eventsList += thisEvent;
						System.out.println("JSON:" + jsonObject.toString());
					}catch(Exception e){
						}
				}
				System.out.println("EVENTS:" + eventsList);
				
				response = responseFactory.createResponseFromCache(eventsList.toString(), Protocol.CLOSE, Protocol.OK_CODE);
			}else{				
				response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.OK_CODE);
			}
		}
		catch(IOException e){
			System.out.println(e.getMessage());
			response = responseFactory.createResponse(null, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
		}
		
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
