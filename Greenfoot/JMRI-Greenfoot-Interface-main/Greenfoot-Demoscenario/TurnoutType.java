/**
 * Enumerates the types of turnouts.
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public enum TurnoutType  
{
    
    /**
     * A switch whose track segment, that is always in use regardless of its state,
     * touches the top of the cell in the Greenfoot world.
     */
    TOP("panel_track_branch_top_"),
    /**
     * A switch whose track segment, that is always in use regardless of its state,
     * touches the right of the cell in the Greenfoot world.
     */
    RIGHT("panel_track_branch_right_"),
    /**
     * A switch whose track segment, that is always in use regardless of its state,
     * touches the left of the cell in the Greenfoot world.
     */    
    LEFT("panel_track_branch_left_"),
    /**
     * A switch whose track segment, that is always in use regardless of its state,
     * touches the bottom of the cell in the Greenfoot world.
     */
    BOTTOM("panel_track_branch_bottom_");
    
    /**
     * The core part of the image file name of turnouts of the
     * respective type, which is specific to the respective turnout type.
     */
    private String imageBaseName;
    
    /**
     * Creates a new turnout type with the given core part of the file
     * name of the image file that is to represent turnouts of the new type.
     * 
     * @param imageBaseName The core part of the file name of the image file that
     * is to represent turnouts of the new type.
     */
    TurnoutType(String imageBaseName) {
        this.imageBaseName = imageBaseName;
    }
    
    /**
     * Returns the core part of the image file name of turnouts of the
     * respective type, which is specific to the respective turnout type.
     * 
     * @return The core part of the image file name of turnouts of the
     * respective type, which is specific to the respective turnout type.
     */
    public String getImageBaseName() {
        return this.imageBaseName;
    }
    
}
