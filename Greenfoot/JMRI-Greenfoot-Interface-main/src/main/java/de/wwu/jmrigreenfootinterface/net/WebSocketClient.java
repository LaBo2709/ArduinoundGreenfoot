package de.wwu.jmrigreenfootinterface.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * A client for communication with the JMRI WebServer
 * 
 * @author Leonard Bienbeck
 */
public class WebSocketClient {

	private String host, port;

	/**
	 * Creates an instance of the client with given host and port of the JMRI
	 * WebServer
	 * 
	 * @param host The host of the JMRI WebServer
	 * @param port The Port of the JMRI WebServer
	 */
	public WebSocketClient(String host, String port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Creates an HttpURLConnection to the given JSON endpoint of the JMRI WebServer
	 * 
	 * @param endpoint The endpoint
	 * @return The created HttpURLConnection
	 * @throws IOException If something goes wrong
	 */
	private HttpURLConnection connect(String endpoint) throws IOException {
		URL url = new URL("http://" + host + ":" + port + "/json/" + endpoint);
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * Sends a request to the JMRI WebServer. This will use the given HTTP request
	 * method and the given endpoint to establish the connection to. The given
	 * message is sent as Content-Type: application/json;charset=utf-8.
	 * 
	 * @param requestMethod The HTTP request method, e.g. PUT, POST or GET
	 * @param endpoint      The given endpoint, e.g. some type supported by JMRI
	 * @param message       The JSON message
	 * @return The server's response
	 * @throws IOException If something goes wrong
	 */
	public String doRequest(String requestMethod, String endpoint, String message) throws IOException {
		// connect to JSON protocol endpoint
		HttpURLConnection conn = connect(endpoint);

		conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		conn.setRequestMethod(requestMethod);
		conn.setDoOutput(true);

		// if message is not empty, send message as request body
		if (!message.isEmpty()) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			bw.write(message);
			bw.flush();
		}

		// receive response
		int code = conn.getResponseCode();
		// if HTTP status code is not 200, return with error
		if (code != 200) {
			System.err.println("Response code: " + code);
			return null;
		}

		// else (HTTP status code is 200), read response line by line and return as
		// String
		Scanner s = new Scanner(new BufferedReader(new InputStreamReader(conn.getInputStream())));
		StringBuilder builder = new StringBuilder();
		while (s.hasNextLine()) {
			builder.append(s.nextLine() + "\n");
		}
		s.close();
		return builder.toString().trim();
	}

}
