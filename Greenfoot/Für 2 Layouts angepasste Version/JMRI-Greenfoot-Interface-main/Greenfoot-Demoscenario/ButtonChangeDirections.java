import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;
import de.wwu.jmrigreenfootinterface.items.*;

/**
 * A button that changes the direction of travel of a train.
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class ButtonChangeDirections extends Button
{

    /**
     * The name assigned to the locomotive in the JMRI system.
     */
    private String locomotiveSystemName = "";
    
    /**
     * Creates a button to change the direction of travel of the train identified
     * by the given name.
     * 
     * @param locomotiveSystemName The name of the train
     */
    public ButtonChangeDirections(String locomotiveSystemName) {
        this.locomotiveSystemName = locomotiveSystemName;
    }
    
    @Override
    public void onClick() {
        doAsync(() -> {
            JMRI.getInterface().invertMovingDirection(locomotiveSystemName);
            /*
            // remember current speed, then break
            int oldSpeed = JMRI.getInterface().getSpeed(locomotiveSystemName);
            JMRI.getInterface().setSpeed(locomotiveSystemName, 0);
            // wait some seconds for the train to come to a stop
            try { Thread.sleep(5000); } catch(Exception e) { e.printStackTrace(); }
            
            // invert direction
            // TODO add a JMRI method for inverting the direction
            JMRI.getInterface().setMovingDirection(
                locomotiveSystemName,
                JMRI.getInterface().getMovingDirection(locomotiveSystemName) == MovingDirection.FORWARD ?
                    MovingDirection.REVERSE :
                    MovingDirection.FORWARD
               );
               
            // revert old speed
            JMRI.getInterface().setSpeed(locomotiveSystemName, oldSpeed);
            */
        });        
    }
    
}
