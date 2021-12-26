package de.wwu.jmrigreenfootinterface.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * A client for communication with the JMRI WiThrottle server
 * 
 * @author Leonard Bienbeck
 */
public class WiThrottleClient {

	private String host, port;
	private static final int CONNECT_TIMEOUT_MS = 5000;
	
	private String clientName;
	private UUID clientUuid;
			
	private Thread heartbeatThread;
	private boolean heartActive;
	private int heartBeatRate;
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	/**
	 * Creates an instance of this class with the given parameters characterising
	 * the connection and login to the WiThrottle server.
	 * 
	 * @param host       The host of the WiThrottle server
	 * @param port       The port of the WiThrottle server
	 * @param clientName The name with which the client should log on to the
	 *                   WiThrottle server. There it appears with this name in a
	 *                   list of connected clients.
	 */
	public WiThrottleClient(String host, String port, String clientName) {
		this.host = host;
		this.port = port;
		this.clientName = clientName;
		this.clientUuid = UUID.randomUUID();
	}

	/**
	 * Creates an instance of this class with the given parameters characterising
	 * the connection and login to the WiThrottle server. The name with which the
	 * client should log on to the WiThrottle server is created randomly. It can be
	 * specified using another constructor.
	 * 
	 * @param host The host of the WiThrottle server
	 * @param port The port of the WiThrottle server
	 */
	public WiThrottleClient(String host, String port) {
		this(host, port, "WiThrottle Device #" + new Random().nextInt(9999));
	}

	/**
	 * Establishes a connection with the WiThrottle server. The constant class
	 * variable CONNECT_TIMEOUT_MS serves as a timeout. The client tries to keep the
	 * established TCP/IP connection alive. The client is logged on to the server
	 * with the name specified or generated in the constructor. A generated, unique
	 * ID is sent to prevent confusion with other clients.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void connect() throws IOException {		
		// connect to TCP-Server @ host:port
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, Integer.parseInt(port)), CONNECT_TIMEOUT_MS);
		socket.setKeepAlive(true);
		
		// get and store input and output streams
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
		
		// send this client's name
		send("N" + clientName);
		String[] welcomeMsgLines = receive(500);
		// start heart for sending heartbeats at demanded rate
		startHeart(welcomeMsgLines);
		
		// send this client's unique ID
		send("HU" + clientUuid);
		receive(100);
	}

	/**
	 * Ensures that the connection is (re-)established if it has been lost or does not
	 * yet exist.
	 * 
	 * @throws IOException If something goes wrong
	 */
	private void maintainConnection() throws IOException {
		if(socket == null || (!socket.isConnected() || socket.isClosed())) {
			connect();
		}
	}
	
	/**
	 * Permanently terminates the connection with the server. The heartbeat
	 * transmitter is stopped, the connection is briefly established again - if
	 * necessary - to send the logoff command to the server according to the
	 * WiThrottle protocol. Afterwards, all I/O streams of the client into the
	 * network as well as the socket are closed.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void disconnect() throws IOException {
		// stop heartbeat sender
		stopHeart();
		
		// make sure connection is still alive
		maintainConnection();
		
		// send QUIT command
		send("Q");
		receive(10);
		
		// close streams and socket
		try {
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receives data from the WiThrottle server. If necessary, the connection is
	 * repaired beforehand or established first. By parameter, a short period of
	 * time can be set to wait afterwards (attention: blocking call) before the
	 * downstream is read buffered and line by line.
	 * 
	 * @param delayInMs A short period of time to wait before actually receiving
	 * @return The message(s) received from the server
	 * @throws IOException If something goes wrong
	 */
	public String[] receive(long delayInMs) throws IOException {
		maintainConnection();
		
		// give the server a chance to make calculations before expecting it to send something
		sleep(delayInMs); // milliseconds
		
		// allocate space to receive lines from server
		List<String> result = new ArrayList<>();
		
		// read lines and store them in the list
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		while(br.ready()) {
			// only add empty lines
			String line = br.readLine();
			if(!line.isEmpty()) {
				result.add(line);
			}
		}
		// return list as array of Strings
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Sends a message buffered to the WiThrottle server.
	 * @param s The message
	 * @throws IOException If something goes wrong
	 */
	public void send(String s) throws IOException {
		maintainConnection();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
		bw.write(s);
		bw.newLine();
		bw.flush();
	}
	
	/**
	 * Sends a single heart beat command to the WiThrottle server
	 * @throws IOException If something goes wrong
	 */
	public void sendHeartBeat() throws IOException {
		send("*");
	}
	
	/**
	 * Starts a heartbeat transmitter based on the interval expected given in the
	 * server's welcome message passed as a parameter. Default interval is 10
	 * seconds.
	 * 
	 * @param welcomeMsgLines The server's welcome message
	 */
	private void startHeart(String[] welcomeMsgLines) {
		for(int i = welcomeMsgLines.length - 1; i >= 0; i--) {
			String line = welcomeMsgLines[i];
			if(line.startsWith("*")) {
				this.heartBeatRate = Integer.parseInt(line.substring(1));
				break;
			}
		}
		
		if(heartBeatRate <= 0) {
			heartBeatRate = 10;
		}
		
		startHeart(heartBeatRate - 1);
	}
	
	/**
	 * Starts a heartbeat transmitter with the given interval on a new thread
	 * 
	 * @param interval The interval at which the heartbeat command is to be sent
	 */
	private void startHeart(int interval) {
		heartActive = true;
		
		// prevent a second heartbeat sender to start
		if(heartbeatThread != null && heartbeatThread.isAlive()) {
			return;
		}
		
		// create heartbeat sender on a separate thread
		heartbeatThread = new Thread(() -> {
			System.out.println("Heartbeat sender started @ 1 beat per " + interval + " seconds");
			do {
				try {
					// send heartbeat
					send("*");
					// sleep
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while(heartActive);
		});
		// start heartbeating
		heartbeatThread.start();
	}
	
	/**
	 * Stops the heartbeat transmitter. Afterwards, at most one more heartbeat is
	 * sent.
	 */
	private void stopHeart() {
		heartActive = false;
	}
	
	/**
	 * Lets the calling thread sleep for a while.
	 * 
	 * @param ms The number of milliseconds the calling thread should sleep
	 */
	private void sleep(long ms) {
		try { Thread.sleep(ms); } catch (InterruptedException e) {}
	}
	
}
