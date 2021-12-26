import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import java.net.ConnectException;

/**
 * Represents a track curve as a special type of track.
 * 
 * @author Leonard Bienbeck 
 * @version 1.0.0
 */
public class Curve extends Track
{
    
    /**
     * Creates a track curve depending on the given type of curve
     * and specifying a layout block to which the curve belongs.
     * 
     * @param curveType The type of curve
     * @param layoutBlockId The layout block to which the curve belongs
     */
    public Curve(CurveType curveType, String layoutBlockId) {
        super(curveType.getImageBaseName(), layoutBlockId);
    }
    
    /**
     * Creates a track curve depending on the given type of curve
     * but without specifying a layout block to which the curve belongs.
     * 
     * @param curveType The type of curve
     */
    public Curve(CurveType curveType) {
        super(curveType.getImageBaseName());
    }
    
    @Override
    public void updateImage() {
        String activationModifierString = super.isBlockActive() ? "1" : "0";
        this.setImage(this.imageBaseName + activationModifierString + ".png");
    }    
}
