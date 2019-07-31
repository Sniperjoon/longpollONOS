package org.onosproject;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerHandler {
	public static int cnt = 0;

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static class RootHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange he) throws IOException {
			new exchangeHandle(he).start();
		}
	}
}

class exchangeHandle extends Thread {
	HttpExchange he;
	private final Logger log = LoggerFactory.getLogger(getClass());

	exchangeHandle(HttpExchange he) {
		this.he = he;
	}

	@Override
	public void run() {

		// TODO Auto-generated method stub

		// Parse Request(GET) URI in order to get deviceID
		String deviceID = he.getRemoteAddress().toString();
		log.info("Message Received from : " + deviceID);
		
		
		// Register Device id
		Global.registeredDevice.put(deviceID, he);

		// Register Event Listener
		EventResponder responder = new EventResponder(he, deviceID);
		Global.initiator.addListener(responder);

	}

}