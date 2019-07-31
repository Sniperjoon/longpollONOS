package org.onosproject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;

public class EventResponder implements PushEventListener {

	private HttpExchange he;
	private String deviceID;

	public EventResponder(HttpExchange he, String deviceID) {
		setHe(he);
		setDeviceID(deviceID);
	}

	public HttpExchange getHe() {
		return he;
	}

	public void setHe(HttpExchange he) {
		this.he = he;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public void eventNotification(String deviceID, JSONObject sendMsg) {
		// TODO Auto-generated method stub

		final Logger log = LoggerFactory.getLogger(getClass());
		
		if (deviceID.equals(this.deviceID)) {

			log.info("Message to : " + deviceID + " ConText : " + sendMsg);
			HttpExchange he = Global.registeredDevice.get(this.deviceID);
			String responseBody = sendMsg.toString();

			try {
				he.sendResponseHeaders(200, responseBody.length());
				OutputStream os = he.getResponseBody();
				os.write(responseBody.getBytes());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (deviceID.equals("all")) {
			
			System.out.println(Global.registeredDevice.keySet());
			
			Iterator<String> it = Global.registeredDevice.keySet().iterator();
				
			while(it.hasNext()){
			
				deviceID = it.next();
				HttpExchange he = Global.registeredDevice.get(this.deviceID);
				String responseBody = sendMsg.toString();
	
				try {
					he.sendResponseHeaders(200, responseBody.length());
					OutputStream os = he.getResponseBody();
					os.write(responseBody.getBytes());
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}		
	}
}

