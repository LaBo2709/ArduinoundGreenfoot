import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)

/**
 * Represents a track that is horizontal or in the world
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class TrackStraight extends Track
{
    
    /**
     * Creates a straight track depending on the given type of
     * straight track and specifying a layout block to which
     * the straight track belongs.
     * 
     * @param straightType The type of straight track
     * @param layoutBlockId The layout block to which the straight track belongs
     */
    public TrackStraight(StraightType straightType, String layoutBlockId) {
        super(straightType.getImageBaseName(), layoutBlockId);
    }
        
    /**
     * Creates a straight track depending on the given type of
     * straight track but without specifying a layout block to which
     * the straight track belongs.
     * 
     * @param straightType The type of straight track
     */
    public TrackStraight(StraightType straightType) {
        super(straightType.getImageBaseName());
    }
    
    @Override
    public void updateImage() {
        String activationModifierString = super.isBlockActive() ? "1" : "0";
        this.setImage(this.imageBaseName + activationModifierString + ".png");
    }    
}
