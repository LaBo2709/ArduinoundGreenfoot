package de.wwu.jmrigreenfootinterface;

/**
 * Represents the Java Model Railroad Interface. This class does not offer any
 * functionality itself. It merely provides the reference for the implementation
 * of the interface via the getInterface() method.
 * 
 * @author Leonard Bienbeck
 */
public class JMRI {
	
	/**
	 * Private constructor to prevent instantiations of this class.
	 */
	private JMRI() {}

	/**
	 * The instance of the implementation of the interface.
	 */
	private static JMRIInterface jmriInterface;
	
	/**
	 * Allows access to an instance of the implementation of the interface. Use this
	 * method to access all kinds of functionality that JMRI offers.
	 * 
	 * @return an instance of the implementation of the interface
	 */
	public static JMRIInterface getInterface() {
		if(jmriInterface == null) {
			jmriInterface = new JMRIInterfaceImplementation();
		}
		return jmriInterface;
	}
	
}
