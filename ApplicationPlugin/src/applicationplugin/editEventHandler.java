package applicationplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

import protocol.HttpResponseFactory;
import protocol.IHandler;
import protocol.IHttpRequest;
import protocol.IHttpResponse;
import protocol.Protocol;
import protocol.ServletHandlerResponse;
import server.Server;

public class editEventHandler implements IHandler {

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
		String original_title = "";
		String original_host = "";
		try{
			title = request.getHeader().get("Title");
			time = request.getHeader().get("Time");
			day = request.getHeader().get("Day");
			location = request.getHeader().get("Location");
			eventHost = request.getHeader().get("Event_host");
			original_title = request.getHeader().get("Orig_title");
			original_host = request.getHeader().get("Orig_host");
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
		
		File file = new File(path + original_host.toString().toLowerCase() + original_title.toString().toLowerCase());
//		
		try{
			if (file.exists()){
				FileWriter writer = new FileWriter(file,false);
				writer.write(event.toJSONString());
				writer.close();
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
