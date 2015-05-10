/*
 * DOSAttack.java
 * Oct 29, 2012
 *
 * FriendnFoe Attacker
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package foe;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public abstract class DOSAttack implements Runnable {
	protected String host;
	protected int port;
	protected String[] uris;
	protected int threadPool;
	protected int taskPerSecond;
	
	private ArrayList<DOSListener> listeners;
	private boolean stop;
	
	public DOSAttack(String host, int port, String[] uris, int threadPool, int taskPerSecond) {
		this.host = host;
		this.port = port;
		this.uris = uris;
		this.threadPool = threadPool;
		this.taskPerSecond = taskPerSecond;
		this.listeners = new ArrayList<DOSListener>();
		this.stop = false;
	}

	public boolean addDOSListener(DOSListener l) {
		return this.listeners.add(l);
	}
	
	public boolean removeDOSListener(DOSListener l) {
		return this.listeners.remove(l);
	}
	
	public int getTaskPerSecond() {
		return taskPerSecond;
	}

	public void setTaskPerSecond(int taskPerSecond) {
		this.taskPerSecond = taskPerSecond;
	}

	protected void fireDOSRateUpdateEvent(String type, String value) {
		for(DOSListener l : listeners) {
			l.rateUpdated(this, type, value);
		}
	}

	protected void fireDOSExceptionEvent(Exception e) {
		for(DOSListener l : listeners) {
			l.exceptionDetected(this, e);
		}
	}
	
	public void run() {
		ExecutorService executor = Executors.newCachedThreadPool();
		while(!stop) {
			long wait;
			if(this.taskPerSecond > 0) {
				long start = System.currentTimeMillis();
				long delayPerTask = 1000/this.taskPerSecond;

				executor.execute(this.getTask());

				long end = System.currentTimeMillis();
				long delta = end - start;
				wait = delayPerTask - delta;
			}
			else {
				wait = 1000;
			}

			if(wait > 0) {
				try {
					Thread.sleep(wait);
				}catch(Exception e){}
			}
		}
		
	    executor.shutdown();
	    while (!executor.isTerminated()) {
	    	try {
	    		executor.awaitTermination(500, TimeUnit.MILLISECONDS);
	    	}
	    	catch(Exception e){}
	    }
	}
	
	public void stop() {
		this.stop = true;
	}
	
	public abstract Runnable getTask();
}
