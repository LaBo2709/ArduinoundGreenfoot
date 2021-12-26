import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;

/**
 * A button to stop a certain train.
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class ButtonStop extends Button
{

    /**
     * The name assigned to the locomotive in the JMRI system.
     */
    private String locomotiveSystemName = "";
    
    /**
     * Creates a button to stop the train identified by the given name.
     * 
     * @param locomotiveSystemName The name of the train
     */    
    public ButtonStop(String locomotiveSystemName) {
        this.locomotiveSystemName = locomotiveSystemName;
    }
    
    @Override
    public void onClick() {
        JMRI.getInterface().setSpeed(locomotiveSystemName, 0);
    }   
    
}
