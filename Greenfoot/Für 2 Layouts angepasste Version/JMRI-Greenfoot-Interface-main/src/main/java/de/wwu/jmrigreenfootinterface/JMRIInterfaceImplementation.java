package de.wwu.jmrigreenfootinterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wwu.jmrigreenfootinterface.items.MovingDirection;
import de.wwu.jmrigreenfootinterface.items.OccupationState;
import de.wwu.jmrigreenfootinterface.net.WebSocketClient;
import de.wwu.jmrigreenfootinterface.net.WiThrottleClient;

/**
 * Die Implementierung des JMRIInterface
 * 
 * @author Leonard Bienbeck
 */
public class JMRIInterfaceImplementation implements JMRIInterface {

	public static String WEBSERVER_HOST;
	public static String WEBSERVER_PORT;
	
	public static String WITHROTTLESERVER_HOST;
	public static String WITHROTTLESERVER_PORT;
	
	private WebSocketClient webClient;
	private WiThrottleClient throttleClient;
	
	/**
	 * Initialisation of one client for each of the JMRI WebServer and the JMRI
	 * WiThrottleServer in order to be able to exchange data with them later.
	 */
	public JMRIInterfaceImplementation() {
		loadNetworkConfig();
		
		webClient = new WebSocketClient(WEBSERVER_HOST, WEBSERVER_PORT);
		throttleClient = new WiThrottleClient(WITHROTTLESERVER_HOST, WITHROTTLESERVER_PORT);
	}
	
	/**
	 * Loads the network configuration via the ConfigIO class from the JSON
	 * configuration file in the resource directory
	 */
	private void loadNetworkConfig() {
		JSONObject networkConfig = (JSONObject) ConfigIO.getInstance().get("network");
		JSONObject webserverConfig = networkConfig.getJSONObject("webserver");
		JSONObject withrottleConfig = networkConfig.getJSONObject("withrottleserver");
		
		WEBSERVER_HOST = webserverConfig.getString("host");
		WEBSERVER_PORT = webserverConfig.getString("port");
		WITHROTTLESERVER_HOST = withrottleConfig.getString("host");
		WITHROTTLESERVER_PORT = withrottleConfig.getString("port");
		
		System.out.println("WebServer config is " + WEBSERVER_HOST + ":" + WEBSERVER_PORT);
		System.out.println("WiThrottle server config is " + WITHROTTLESERVER_HOST + ":" + WITHROTTLESERVER_PORT);
	}
	
	// ============ JMRI json functions section ============
	
	@Override
	public JSONArray listTypes() {
		try {
			String response = webClient.doRequest("GET", "type", "");
			if(response != null) {
				return new JSONArray(response);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONArray getType(String type) {
		try {
			String response = webClient.doRequest("GET", type, "");
			if(response != null) {
				return new JSONArray(response);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject getItem(String type, String itemName) {
		try {
			String response = webClient.doRequest("GET", type + "/" + itemName, "");
			if(response != null) {
				return new JSONObject(response);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getProperty(String type, String itemName, String propertyName) {
		JSONObject item = getItem(type, itemName);
		JSONObject itemData = item.getJSONObject("data");
		return itemData.get(propertyName);
	}

	private boolean setPropertyString(String type, String itemName, String propertyName, String value) {
		try {
			webClient.doRequest("POST", type + "/" + itemName, "{\"" + propertyName + "\":\"" + String.valueOf(value) + "\"}");
			return getProperty(type, itemName, propertyName).equals(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean setProperty(String type, String itemName, String propertyName, Object value) {
		// if value is of type String, use private helper method to add quotation marks
		if(value instanceof String) {
			return setPropertyString(type, itemName, propertyName, String.valueOf(value));
		}
		
		try {
			webClient.doRequest("POST", type + "/" + itemName, "{\"" + propertyName + "\":" + String.valueOf(value) + "}");
			return getProperty(type, itemName, propertyName).equals(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Thread layoutBlockListenerThread = null;
	private HashMap<String, String> layoutBlockOccupations = null;
	
	@Override
	public JSONObject getTrainOnLayoutBlock(String layoutBlockName) {

		// initialize layout block occupation map, if necessary

			if (layoutBlockOccupations == null) {
				layoutBlockOccupations = new HashMap<>();
			}



		// start layout block listener thread to stay informed about layout block
		// occupation updated, if necessary
		if (layoutBlockListenerThread == null || !layoutBlockListenerThread.isAlive()) {
			startLayoutBlockOccupationListenerThread();
		}


			// return current occupation information, if known
			if(!layoutBlockOccupations.containsKey(layoutBlockName)) {
				return null;
			}
			return getItem("rosterEntry", layoutBlockOccupations.get(layoutBlockName));

	}



	/**
	 * Starts a thread that polls the reporters of all layout blocks at short
	 * intervals about their current state so that trains can be identified and
	 * located on the track system.
	 */
	private void startLayoutBlockOccupationListenerThread() {
		layoutBlockListenerThread = new Thread(() -> {
			do {
				// request layout block reporters
				JSONArray reporters = getType("reporters");
				// for every reporter...
				String blockName="";
				for(int i = 0; i < reporters.length(); i++) {
					// request its name and report
					if(reporters.getJSONObject(i).getJSONObject("data").getString("name").equals("IR100")){
						 blockName="Links";

					}else if(reporters.getJSONObject(i).getJSONObject("data").getString("name").equals("IR101")){
						 blockName="Oben";

					}else if(reporters.getJSONObject(i).getJSONObject("data").getString("name").equals("IR102")){
						 blockName="Rechts";

					}else if(reporters.getJSONObject(i).getJSONObject("data").getString("name").equals("IR103")){
						 blockName="Unten";

					}else{
						 blockName="Weiche";

					}



					//System.out.println(reporters.getJSONObject(i).getJSONObject("data").getString("name"));
					String report = "";
					if(!reporters.getJSONObject(i).getJSONObject("data").isNull("report")) {
						report = reporters.getJSONObject(i).getJSONObject("data").getString("report");
					}

					// set occupation state depending on report
					OccupationState state = report.isEmpty() ? OccupationState.UNKNOWN : (report.endsWith("exits") ? OccupationState.UNOCCUPIED : OccupationState.OCCUPIED);

					// query train name depending on occupation state
					if(state == OccupationState.OCCUPIED) {
						String dccAddress = report.substring(0, report.indexOf(" "));
						// get all roster entries
						JSONArray rosterEntries = getType("rosterEntry");
						// filter using reported DCC address
						for(int k = 0; k < rosterEntries.length(); k++) {
							// if the dcc address of the iterated train matches the reported address, add the trains customized name to the layoutBlockOccupations map
							if(rosterEntries.getJSONObject(k).getJSONObject("data").getString("address").equals(dccAddress)) {
									// add name to occupations map
								synchronized (layoutBlockOccupations) {
									String trainName = rosterEntries.getJSONObject(k).getJSONObject("data").getString("name");
									//layoutBlockOccupations.clear();
									if (layoutBlockOccupations.containsKey(blockName)) {
										layoutBlockOccupations.replace(blockName, trainName);
									}else layoutBlockOccupations.put(blockName, trainName);

									System.out.println(blockName+ "" + trainName);
									// remove the same train from other blocks, because trains can only occupy one block at a time

									for (String key : layoutBlockOccupations.keySet()) {
										if (!key.equals(blockName)) {
											if (layoutBlockOccupations.get(key).equals(trainName)) {
												layoutBlockOccupations.remove(key); //HIER STAND blockName.. MUSS key SEIN
												// TODO Check if this is a concurrent modification
											}
										}
									}
								}
								
								break; // there should only be one train with a matching dcc address
							}
						}
					}					
				}
				
				// sleep
				try {
					Thread.sleep(200); // TODO make this configurable using the config file
				} catch (InterruptedException e) {} 
			} while(true);
		});
		layoutBlockListenerThread.start();
	}
	
	// ============ JMRI WiThrottle functions section ============

	private final String THROTTLE_ID = "T";
	private HashMap<String, Integer> lastReceivedTrainSpeeds = new HashMap<>();
	private HashMap<String, MovingDirection> lastReceivedTrainDirections = new HashMap<>();
	
	@Override
	public void addLocomotive(String locomotiveReference, String address) {
		sendCommand("+", locomotiveReference, "<;>", address);
	}

	@Override
	public void removeLocomotive(String locomotiveReference) {
		sendCommand("-", locomotiveReference, "<;>", "r");
	}

	@Override
	public void removeAllLocomotives() {
		removeLocomotive("*");
	}

	@Override
	public void setFunctionKeyPressed(String locomotiveReference, int functionKeyNumber, boolean pressed) {
		sendCommand("A", locomotiveReference, "<;>", "F", pressed ? "1" : "0", String.valueOf(functionKeyNumber));
	}

	@Override
	public void setFunctionKeyLocking(String locomotiveReference, int functionKeyNumber, boolean locking) {
		sendCommand("A", locomotiveReference, "<;>", "m", locking ? "1" : "0", String.valueOf(functionKeyNumber));		
	}

	@Override
	public void setMovingDirection(String locomotiveReference, MovingDirection movingDirection) {
		sendCommand("A", locomotiveReference, "<;>", "R", movingDirection == MovingDirection.FORWARD ? "1" : "0");
	}

	@Override
	public void setSpeed(String locomotiveReference, int speed) {
		sendCommand("A", locomotiveReference, "<;>", "V", String.valueOf(speed));
	}

	@Override
	public MovingDirection getMovingDirection(String locomotiveReference) {
		// the prefix of the expected result string
		String queryResultPrefix = "M" + THROTTLE_ID + "A" + locomotiveReference;
		// send the command, receive the results
		String[] queryResults = sendCommand("A", locomotiveReference, "<;>", "qR");
		// process all the results
		processQueryResults(queryResults, queryResultPrefix);
		// return the relevant result
		return lastReceivedTrainDirections.get(queryResultPrefix);
	}

	@Override
	public int getSpeed(String locomotiveReference) {
		// the prefix of the expected result string
		String queryResultPrefix = "M" + THROTTLE_ID + "A" + locomotiveReference;
		// send the command, receive the results
		String[] queryResults = sendCommand("A", locomotiveReference, "<;>", "qV");
		// process all the results
		processQueryResults(queryResults, queryResultPrefix);
		// return the relevant result
		return lastReceivedTrainSpeeds.get(queryResultPrefix);
	}

	@Override
	public void doEmergencyStop(String locomotiveReference) {
		sendCommand("A", locomotiveReference, "<;>", "X");
	}

	@Override
	public void invertMovingDirection(String locomotiveReference) {
		MovingDirection oldDirection = getMovingDirection(locomotiveReference);
		setMovingDirection(locomotiveReference, oldDirection == MovingDirection.FORWARD ? MovingDirection.REVERSE : MovingDirection.FORWARD);
	}
	
	/**
	 * Sends a command to the WiThrottle server and receives its response.
	 * 
	 * @param commandStrings The strings from which the command is composed
	 * @return The (possibly multi-part) response of the server
	 */
	private String[] sendCommand(String... commandStrings) {
		// build command string
		StringBuilder builder = new StringBuilder();
		for(String s : commandStrings) {
			builder.append(s);
		}
		try {
//			throttleClient.send("M" + THROTTLE_ID + builder.toString());
//			return throttleClient.receive(500);
			
			// \/ BEGIN OF DEBUG \/
			String cmd = "M" + THROTTLE_ID + builder.toString();
			System.out.println("\n>>" + cmd);
			throttleClient.send(cmd);
			String debugStrings[] = throttleClient.receive(500);
			System.out.println("<< " + Arrays.toString(debugStrings) + "\n");
			return debugStrings;
			// /\ END OF DEBUG /\
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Processes the given (possibly multi-part) response from the server. If
	 * information on the direction of travel or speed of a specific locomotive
	 * identified by the prefix is included, this information is cached.
	 * 
	 * @param queryResults  The (possibly multi-part) response from the server
	 * @param messagePrefix A prefix used to identify the locomotive to which
	 *                      information is being received.
	 */
	private void processQueryResults(String[] queryResults, String messagePrefix) {
		// for each part of the (possibly) multi-part result...
		for(String singleResult : queryResults) {
			
			// read and store direction information
			if(singleResult.contains(messagePrefix + "<;>R")) {
				lastReceivedTrainDirections.put(messagePrefix, singleResult.substring(singleResult.indexOf("<;>R") + 4).equalsIgnoreCase("0") ? MovingDirection.REVERSE : MovingDirection.FORWARD);
				
			// read and store speed information
			} else if(singleResult.contains(messagePrefix + "<;>V")) {
				lastReceivedTrainSpeeds.put(messagePrefix, Integer.valueOf(singleResult.substring(singleResult.indexOf("<;>V") + 4)));
				
			}
			
		}
	}
}
