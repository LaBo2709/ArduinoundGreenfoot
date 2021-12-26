import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;
import java.net.ConnectException;

/**
 * Represents a part of a track of a railway
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public abstract class Track extends Actor
{   
    /**
     * The name of the layout block to which this piece of track belongs.
     */
    protected String layoutBlockId = null;
    /**
     * The core part of the file name of the image to be drawn into the
     * Greenfoot world to represent this object.
     */
    protected String imageBaseName = null;
    protected String name = null;
    
    /**
     * Creates a track with the given core part of the file name of the
     * image representing it, but without assignment to a layout block.
     * 
     * @param imageBaseName The core part of the file name of the image
     * representing the track
     */
    public Track(String imageBaseName) {
        this(imageBaseName, null);
    }
    
    /**
     * Creates a track with the given core part of the file name of the
     * image representing it and assigns it the given layout block.
     * 
     * @param imageBaseName The core part of the file name of the image
     * representing the track
     * @param layoutBlockId The name of the layout block to which the
     * track belongs
     */
    public Track(String imageBaseName, String layoutBlockId) {
        this.imageBaseName = imageBaseName;
        this.layoutBlockId = layoutBlockId;
    }
    
    /**
     * Assigns the layout block with the given name to the track.
     * 
     * @param layoutBlockId The name of the layout block to which the
     * track belongs
     */
    public void setLayoutBlock(String layoutBlockId) {
        this.layoutBlockId = layoutBlockId;
    }
    
    /**
     * Returns the name of the layout block to which the track belongs.
     * 
     * @return The name of the layout block to which the track belongs
     */
    public String getLayoutBlock() {
        return layoutBlockId;
    }
    
    /**
     * Indicates whether the layout block to which the track belongs
     * is active, i.e. whether there is currently a train on it.
     * 
     * @return true if the block is active; false if the block is not
     * active or no block is assigned to the track
     */
    protected boolean isBlockActive() {
        if(layoutBlockId == null || layoutBlockId.isEmpty()) {
            return false;
        }
        
        try {
            int result = (int) JMRI.getInterface().getProperty("layoutBlocks", layoutBlockId, "state");
            return result == 2 ? true : false;
        } catch(Exception e) {
            return false;
        }
    }
    
    /**
     * Abstract method by which the subclasses determine how the
     * image representing the track is generated. This usually
     * takes into account the state of the track, for example
     * whether it is occupied or, in the case of turnouts, whether
     * they are thrown or closed.
     */
    public abstract void updateImage();
    
    @Override
    protected void addedToWorld(World world) {
        // Uncomment the following line to display the state of the tracks even before starting the
        // scenario via the Play or Act button. Attention: A connection with JMRI will then be
        // attempted even before such a click. If this fails, error messages are displayed before
        // the scenario has been started.
        
        //updateImage();
    }
    
    @Override
    public void act() 
    {
        // tracks do not act
    }  
    
    /**
     * A track does not move; therefore nothing happens when this method is called.
     */
    @Override
    public void move(int distance) {
        // tracks do not move
    }
}
