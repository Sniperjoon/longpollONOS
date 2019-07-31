package org.onosproject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventInitiater {
	private List<PushEventListener> listeners = new ArrayList<PushEventListener>();
	private ConcurrentHashMap<String, String> dup = new ConcurrentHashMap<>();
	
	final Logger log = LoggerFactory.getLogger(getClass());
	
	public void addListener(PushEventListener toAdd) {

		EventResponder input = (EventResponder) toAdd;
		String inputID = input.getDeviceID();
		
		if(!dup.containsKey(inputID)) {
			dup.put(inputID, inputID);
			listeners.add(toAdd);
		}
	}

	public void eventOccur(String deviceID, JSONObject sendMsg) {
		log.info("Logging in the event Occur before for: device ID : " + deviceID + " sendMsg " + sendMsg.toString());
		for (PushEventListener hl : listeners) {
			log.info("Logging in the event Occur during for : device ID : " + deviceID + " sendMsg " + sendMsg.toString());
			hl.eventNotification(deviceID,sendMsg);	
		}
	}
}