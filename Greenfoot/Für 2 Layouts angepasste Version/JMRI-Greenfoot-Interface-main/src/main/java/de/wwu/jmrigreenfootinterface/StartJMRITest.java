package de.wwu.jmrigreenfootinterface;

import java.io.IOException;

public class StartJMRITest {

	public static void main(String[] args) throws IOException, InterruptedException {
//		WiThrottleClient client = new WiThrottleClient("127.0.0.1", "12090");
//		System.out.println("Connecting to WiThrottleServer...");
//		client.connect();
//		System.out.println("Connected to WiThrottleServer.\n");
//		
//		System.out.println("Sending command...");
//		client.send("HUtestid");
//		System.out.println("Sent.\n");
//		
//		System.out.println("Receiving...");
//		String[] result = client.receive();
//		System.out.println(Arrays.toString(result));
//		System.out.println("Received.\n");
//		
//		client.sendHeartBeat();
		
		JMRI.getInterface().addLocomotive("S3", "S3");
		JMRI.getInterface().setSpeed("S3", 50);
		System.out.println(JMRI.getInterface().getSpeed("S3"));
		
//		Thread.sleep(20000);
//		
//		client.disconnect();
	}

}
