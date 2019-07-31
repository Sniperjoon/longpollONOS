package org.onosproject;

import org.json.JSONObject;

public interface PushEventListener {

	void eventNotification(String deviceID, JSONObject sendMsg);
	
}
