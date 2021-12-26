/**
 * Enumerates the types of straight tracks.
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public enum StraightType  
{
    
    /**
     * A straight, horizontal track
     */
    HORIZONTAL("panel_track_horizontal_"),
    /**
     * A straight, vertical track
     */
    VERTICAL("panel_track_vertical_");
    
    /**
     * The core part of the image file name of straight tracks of the
     * respective type, which is specific to the respective straight track type.
     */
    private String imageBaseName;
    
    /**
     * Creates a new straight track type with the given core part of the file
     * name of the image file that is to represent straight tracks of the new type.
     * 
     * @param imageBaseName The core part of the file name of the image file that
     * is to represent straight tracks of the new type.
     */
    StraightType(String imageBaseName) {
        this.imageBaseName = imageBaseName;
    }
    
    /**
     * Returns the core part of the image file name of straight tracks of the
     * respective type, which is specific to the respective straight track type.
     * 
     * @return The core part of the image file name of straight tracks of the
     * respective type, which is specific to the respective straight track type.
     */
    public String getImageBaseName() {
        return this.imageBaseName;
    }
    
}
