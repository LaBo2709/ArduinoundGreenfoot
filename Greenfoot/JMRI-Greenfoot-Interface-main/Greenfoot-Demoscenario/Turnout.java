import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import de.wwu.jmrigreenfootinterface.*;
import de.wwu.jmrigreenfootinterface.items.*;

/**
 * Represents a turnout as a special type of track.
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class Turnout extends Track
{
    
    /**
     * The system name of the turnout in JMRI
     */
    private String jmriSystemName = "";
    
    /**
     * Creates a turnout with the given type, assigned to the layout block
     * with the given name and the given JMRI system name of the physical
     * turnout that the created object represents.
     * 
     * @param turnoutType The type of turnout
     * @param layoutBlockId The layout block to which the turnout belongs
     * @param jmriSystemName The name under which the turnout to be
     * represented is stored in JMRI.
     */
    public Turnout(TurnoutType turnoutType, String layoutBlockId, String jmriSystemName) {
        super(turnoutType.getImageBaseName(), layoutBlockId);
        this.imageBaseName = imageBaseName;
        this.jmriSystemName = jmriSystemName;
    }
    
    /**
     * Creates a turnout with the given type and the given JMRI system name
     * of the physical turnout that the created object represents but without
     * assignment to a layout block.
     * 
     * @param turnoutType The type of turnout
     * @param jmriSystemName The name under which the turnout to be
     * represented is stored in JMRI.
     */
    public Turnout(TurnoutType turnoutType, String jmriSystemName) {
        super(turnoutType.getImageBaseName());
        this.imageBaseName = imageBaseName;
        this.jmriSystemName = jmriSystemName;
    }
    
    /**
     * Sets the state of the turnout
     * 
     * @param state The state to be set
     */
    public void setState(TurnoutState state) {    
        JMRI.getInterface().setProperty("turnout", jmriSystemName, "state", state.getStateCode());
    }
    
    /**
     * Determines the condition of the turnout. For this purpose,
     * JMRI is contacted and the current status is queried.
     * 
     * @return The state of the turnout; may be TurnoutState.UNKNOWN if it
     * cannot be determined.
     */
    public TurnoutState getState() {
        try {
        // request current state from JMRI
            int stateCode = (int) JMRI.getInterface().getProperty("turnout", jmriSystemName, "state");
            return TurnoutState.fromCode(stateCode);
        } catch(Exception e) {
            System.err.println("Requesting turnout state from JMRI failed: " + e.getMessage() + "\nReturning UNKNOWN state instead.");
            return TurnoutState.UNKNOWN;
        }
    }
    
    /**
     * Reverses the state of the turnout, i.e. CLOSED becomes THROWN.
     * Attention: All other states, not only THROWN, become CLOSED.
     */
    public void toggleState() {
        setState(getState() == TurnoutState.CLOSED ? TurnoutState.THROWN : TurnoutState.CLOSED);
    }
    
    @Override
    public void updateImage() {
        TurnoutState state = getState();
        String stateModifierString = (state == TurnoutState.THROWN ? "thrown" : (state == TurnoutState.CLOSED ? "closed" : "unknown"));
        String activationModifierString = super.isBlockActive() ? "1" : "0";
        this.setImage(this.imageBaseName + stateModifierString + "_" + activationModifierString + ".png");
    }    
}
