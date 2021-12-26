import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;

/**
 * A button that sets a train in motion
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class ButtonGo extends Button
{

    /**
     * The name assigned to the locomotive in the JMRI system.
     */
    private String locomotiveSystemName = "";
    
    /**
     * Creates a button to make the train identified by the given name move.
     * 
     * @param locomotiveSystemName The name of the train
     */
    public ButtonGo(String locomotiveSystemName) {
        this.locomotiveSystemName = locomotiveSystemName;
    }

    @Override
    public void onClick() {
        JMRI.getInterface().addLocomotive(locomotiveSystemName, locomotiveSystemName); // TODO outsource this line to an extra button
        JMRI.getInterface().setSpeed(locomotiveSystemName, 60);
    }
    
}
