import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;

/**
 * A button that lets the horn of a train play.
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class ButtonHorn extends Button
{

    /**
     * The name assigned to the locomotive in the JMRI system.
     */
    private String locomotiveSystemName = "";
    
    /**
     * Creates a button to play the horn of the train identified
     * by the given name.
     * 
     * @param locomotiveSystemName The name of the train
     */
    public ButtonHorn(String locomotiveSystemName) {
        this.locomotiveSystemName = locomotiveSystemName;
    }

    @Override
    public void onClick() {
        JMRI.getInterface().setFunctionKeyPressed(locomotiveSystemName, 8, true);
    }
    
}
