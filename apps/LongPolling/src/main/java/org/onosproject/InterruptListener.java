package org.onosproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.json.JSONObject;

public class InterruptListener extends Thread {

	public void run(JSONObject sendMsg) {
		// TODO Auto-generated method stub
		super.run();

		while (true) {

			System.out.println("Input Target DEVICE ID:");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String deviceID;
			try {
				deviceID = br.readLine();

				if (Global.registeredDevice.keySet().contains(deviceID)) {

					Global.initiator.eventOccur(deviceID, sendMsg);
					
				}else if(deviceID.equals("all")){
					Iterator<String> it = Global.registeredDevice.keySet().iterator();
					while(it.hasNext()){
						String temp = it.next();
						Global.initiator.eventOccur(temp, sendMsg);
					}
					
					System.out.println("Sending all devices");
					
				}else {
					System.out.println("Wrong Device ID");
					System.out.println("DEVICE ID format will be : DEVICE1");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
