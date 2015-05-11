package protocol;

import java.util.ArrayList;
import java.util.List;

import server.ConnectionHandler;

public class User {
	
	final int queueLimit = 100;
	final int perMinLimit = 200;
	private String ip;
	private int numRequests;
	private List<ConnectionHandler> queuedRequests;
	
	public User(){}
	
	public User(String ip){
		
		this.ip = ip;
		numRequests = 0;
		queuedRequests = new ArrayList<ConnectionHandler>();
	}
	
	public String getIP(){
		return ip;
	}
	
	public void incrementRequest(){
		//System.out.println("Incrementing request");
		numRequests++;
	}
	
	public void clearRequests(){
		numRequests = 0;
	}
	
	public int getRequests(){
		return numRequests;
	}
	
	public boolean isOverLimit(){
		return numRequests>perMinLimit;
	}
	
	public boolean addToQueue(ConnectionHandler c){
		System.out.println("adding handler to queue");
		if(queuedRequests.size() >= queueLimit)
		{
			System.out.println("Request from " + ip + " denied!");
			return false;
		}
		queuedRequests.add(c);
		return true;
	}
	
	public int getNumRequestsQueued(){
		return queuedRequests.size();
	}
	
	public ConnectionHandler getNextRequest(){
		if(queuedRequests.isEmpty()){
			return null;
		}
		return queuedRequests.remove(0);
	}
}