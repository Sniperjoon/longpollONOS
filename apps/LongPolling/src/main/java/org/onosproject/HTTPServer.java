package org.onosproject;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;


public class HTTPServer {

	private static final int PORT = 12345;

	private HttpServer server;
	private final Logger log = LoggerFactory.getLogger(getClass());
	

	public void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		log.info("Log Message in HTTP Server : server is starting");

		
		for (int i = 0; i < 100; i++) {
			server.createContext("/sample/id=DEVICE" + i, new ServerHandler.RootHandler());
		}
		
		server.start();
	}

	public void stop() {
		server.stop(0);
	}

}
