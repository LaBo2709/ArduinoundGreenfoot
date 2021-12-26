package de.wwu.jmrigreenfootinterface;

import org.json.JSONArray;
import org.json.JSONObject;

import de.wwu.jmrigreenfootinterface.items.MovingDirection;

/**
 * The interface to access JMRI functionality. The actual realisation of
 * communication with JMRI depends on the concrete implementation of this
 * interface.<br>
 * This class combines such functions that can be provided by JMRI via WebServer
 * using a JSON interface and such functions that can be provided by the
 * WiThrottle interface.
 * 
 * @author Leonard Bienbeck
 */
public interface JMRIInterface {

	// JMRI json functions

	/**
	 * Returns a list of types supported by JMRI, e.g. Turnout(s) or layoutBlock(s),
	 * as JSONArray.
	 * 
	 * @return A list of types supported by JMRI as JSONArray
	 */
	public JSONArray listTypes();

	/**
	 * Returns a list of entities of the given type as a JSONArray that are created
	 * in JMRI, e.g. the specific turnouts created.
	 * 
	 * @param type The given type of the entities
	 * @returna A List of entities of the given type as a JSONArray
	 */
	public JSONArray getType(String type);

	/**
	 * Returns the entity of the given type with the given name as JSONObject.
	 * 
	 * @param type     The given type of the entity
	 * @param itemName The given name of the entity
	 * @return The entity of the given type with the given name
	 */
	public JSONObject getItem(String type, String itemName);

	/**
	 * Returns the value of the given property of the entity of the given type with
	 * the given name as JSONObject.
	 * 
	 * @param type         The given type of the entity
	 * @param itemName     The given name of the entity
	 * @param propertyName The given name of the entitiy's property
	 * @return the value of the given property
	 */
	public Object getProperty(String type, String itemName, String propertyName);

	/**
	 * Sets the value of the given attribute of the entity of the given type with
	 * the given name.
	 * 
	 * @param type         The given type of the entity
	 * @param itemName     The given name of the entity
	 * @param propertyName The given name of the entitiy's property
	 * @param value        The value that the property should have
	 * @return true if the property has actually taken the given value at the end of
	 *         the call (this is checked by a short query); false otherwise
	 */
	public boolean setProperty(String type, String itemName, String propertyName, Object value);

	/**
	 * Attempts to identify the train that is currently on the given layout block.
	 * If this fails, for example because there currently is no train on that block
	 * or for other reasons, null is returned.
	 * 
	 * @param layoutBlockName The name of the layout block
	 * @return The data of the train currently on the layout block as JSONObject;
	 *         null if no such train can be identified.
	 */
	public JSONObject getTrainOnLayoutBlock(String layoutBlockName);
	
	// JMRI WiThrottle functions

	/**
	 * Registers a locomotive for control via the WiThrottle protocol. A repeated,
	 * identical call of this method is harmless.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled,
	 *                            which is to be used in subsequent method calls
	 *                            (reference)
	 * @param address             The DCC address of the locomotive with a
	 *                            distinction for short and long addresses, e.g. "S"
	 *                            for short and "L" for long addresses
	 */
	public void addLocomotive(String locomotiveReference, String address);

	/**
	 * Deregisters a locomotive from the control through the WiThrottle protocol.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 */
	public void removeLocomotive(String locomotiveReference);

	/**
	 * Deregisters all locomotives from the control through the WiThrottle protocol.
	 */
	public void removeAllLocomotives();

	/**
	 * Sets a function key as pressed or released
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @param functionKeyNumber   The number of the function key, e.g. 1 or 12
	 * @param pressed             true if the key is to be set as pressed; false if
	 *                            it is to be set as released
	 */
	public void setFunctionKeyPressed(String locomotiveReference, int functionKeyNumber, boolean pressed);

	/**
	 * Determines whether a certain function key is a so-called 'locking' function
	 * key, i.e. whether it should remain pressed by itself after being pressed
	 * until it is explicitly released.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @param functionKeyNumber   The number of the function key, e.g. 1 or 12
	 * @param locking             true, if the function key is to remain pressed by
	 *                            itself after a press until it is explicitly
	 *                            released again; false, if it is allowed to change
	 *                            to the released state again by itself
	 */
	public void setFunctionKeyLocking(String locomotiveReference, int functionKeyNumber, boolean locking);

	/**
	 * Sets the direction of travel. The two possible directions of travel are
	 * provided by the MovingDirection enum.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @param movingDirection     The desired direction of travel that the
	 *                            locomotive should follow after calling this
	 *                            function.
	 */
	public void setMovingDirection(String locomotiveReference, MovingDirection movingDirection);

	/**
	 * Sets the speed of the locomotive. The allowed values depend on the hardware
	 * used and the settings in JMRI.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @param speed               The speed that the locomotive should have.
	 */
	public void setSpeed(String locomotiveReference, int speed);

	/**
	 * Returns the current driving direction of the locomotive.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @return The current driving direction of the locomotive
	 */
	public MovingDirection getMovingDirection(String locomotiveReference);

	/**
	 * Reverses the current direction of travel of the locomotive.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 */
	public void invertMovingDirection(String locomotiveReference);

	/**
	 * Returns the current speed of the locomotive.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 * @return The current speed of the locomotive
	 */
	public int getSpeed(String locomotiveReference);

	/**
	 * Performs an emergency braking of the locomotive.
	 * 
	 * @param locomotiveReference The identifier of the locomotive to be controlled
	 *                            (reference)
	 */
	public void doEmergencyStop(String locomotiveReference);

}
