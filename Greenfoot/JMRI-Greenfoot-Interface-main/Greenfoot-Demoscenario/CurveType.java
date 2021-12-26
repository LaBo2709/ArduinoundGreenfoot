/**
 * Enumerates the types of track curves.
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public enum CurveType  
{
    
    /**
     * A curve connecting the left and top edges of a cell.
     */
    TOP_LEFT("panel_track_curve_top_left_"),
    /**
     * A curve connecting the right and top edges of a cell.
     */
    TOP_RIGHT("panel_track_curve_top_right_"),
    /**
     * A curve connecting the left and bottom edges of a cell.
     */
    BOTTOM_LEFT("panel_track_curve_bottom_left_"),
    /**
     * A curve connecting the right and bottom edges of a cell.
     */
    BOTTOM_RIGHT("panel_track_curve_bottom_right_");
    
    /**
     * The core part of the image file name of curves of the
     * respective type, which is specific to the respective curve type.
     */
    private String imageBaseName;
    
    /**
     * Creates a new curve type with the given core part of the file
     * name of the image file that is to represent curves of the new type.
     * 
     * @param imageBaseName The core part of the file name of the image
     * file that is to represent curves of the new type.
     */
    CurveType(String imageBaseName) {
        this.imageBaseName = imageBaseName;
    }
    
    /**
     * Returns the core part of the image file name of curves of the
     * respective type, which is specific to the respective curve type.
     * 
     * @return The core part of the image file name of curves of the
     * respective type, which is specific to the respective curve type.
     */
    public String getImageBaseName() {
        return this.imageBaseName;
    }
    
}
