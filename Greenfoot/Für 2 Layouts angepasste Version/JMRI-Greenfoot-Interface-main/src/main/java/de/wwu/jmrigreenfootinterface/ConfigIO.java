package de.wwu.jmrigreenfootinterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.json.JSONObject;

/**
 * Takes care of reading from and writing to the JSON configuration file located
 * in the resource directory. With the help of that configuration file and this
 * class, human-readable and editable parameters can be persistently set, which
 * can be loaded and processed by the programme.<br>
 * The class is designed as a singleton. The only instance of the class can be
 * addressed via the call <code>ConfigIO.getInstance()</code>. This prevents concurrent
 * access to the configuration file.
 * 
 * @author Leonard Bienbeck
 */
public class ConfigIO {
	
	// ---- Singleton ----
	
	public static ConfigIO instance;
	
	/**
	 * Private constructor. Instantiates the class.
	 */
	private ConfigIO() {
		readConfigFile();
	}

	/**
	 * This method can be used to call the only instance of this class. This
	 * prevents concurrent access to the configuration file. The instance is created
	 * the first time this method is called. This could take a short while, because
	 * I/O operations are being carried out then.
	 * 
	 * @return The only instance of this class
	 */
	public static ConfigIO getInstance() {
		if(instance == null) {
			instance = new ConfigIO();
		}
		return instance;
	}
	
	// ---- ---- ---- ----
	
	/**
	 * The JSONObject representation of the configuration file
	 */
	private JSONObject configJsonObject;
	
	/**
	 * Reads the contents of the JSON configuration file buffered and line by line
	 * to create a JSONObject from it.
	 */
	private void readConfigFile() {
		Scanner s = new Scanner(new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/config.json"))));
		StringBuilder builder = new StringBuilder();
		while(s.hasNextLine()) {
			builder.append(s.nextLine() + "\n");
		}
		configJsonObject = new JSONObject(builder.toString().trim());
		s.close();
	}
	
	/**
	 * Writes the current state of the internal JSONObject buffered to the JSON
	 * configuration file.
	 */
	public void writeConfigFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getClass().getResource("/config.json").getPath())));
			writer.write(configJsonObject.toString());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ---- ---- ---- ----
	
	/**
	 * Returns the value that is stored for the given key in the configuration.
	 * @param key The key
	 * @return The value that is stored for the given key in the configuration
	 */
	public Object get(String key) {
		return configJsonObject.get(key);
	}
	
	/**
	 * Stores the given object under the given key in the configuration.
	 * @param key The key
	 * @param obj The given object
	 */
	public void set(String key, Object obj) {
		configJsonObject.put(key, obj);
	}
	
	/**
	 * Removes the value that is stored for the given key from the configuration.
	 * @param key The key
	 */
	public void remove(String key) {
		configJsonObject.remove(key);
	}

}
