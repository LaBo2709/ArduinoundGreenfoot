import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.JMRI;
import de.wwu.jmrigreenfootinterface.items.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.json.JSONObject;
import java.io.*;

/**
 * A world in which the scheme of a model railway (panel) can be imitated.
 * It also provides buttons to control the railway.
 * 
 * @author Leonard Bienbeck, Lars Bockstette
 * @version 1.0.1 für Zugerkennung mit Arduino
 */
public class PanelWorld extends World
{
    
    /**
     * Counts how often the Act method is called to perform certain functionality
     * only every n such calls.
     */
    int tickCounter = -1;
    /**
     * Constructs the world with a fixed number of cells visually delineated by a faint
     * greyish background grid. In addition, the objects placed in the world are initialised.
     */
    public PanelWorld()
    {    
        // Create a new world with 16x12 cells with a cell size of 64x64 pixels.
        super(16, 12, 64);
        paintBackgroundGrid();
        prepare();

    }
    
    @Override
    public void started() {
        // Uncomment the following block to display the state of the turnouts even before starting the
        // scenario via the Play or Act button. Attention: A connection with JMRI will then be
        // attempted even before such a click. If this fails, error messages are displayed before
        // the scenario has been started.
        
        /*
        getObjects(Turnout.class).forEach(t -> {
            TurnoutState s = t.getState();
            t.updateImage();
        });
        repaint();
        */
    }
    
    @Override
    public void act() {        
        // Clicks on turnouts
        if(Greenfoot.getMouseInfo() != null) {
            Actor focusedActor = Greenfoot.getMouseInfo().getActor();
            if(Greenfoot.mousePressed(focusedActor)) {
                if(focusedActor != null) {
                    if(focusedActor instanceof Turnout) {
                        Turnout clickedSwitch = (Turnout) focusedActor;
                        clickedSwitch.toggleState();
                        clickedSwitch.updateImage();
                    } else if(focusedActor instanceof Button) {
                        Button clickedButton = (Button) focusedActor;
                        clickedButton.onClick();
                    }
                }
            }
        }
        
        // Update layout block activation (every n ticks)
        if((++tickCounter) % 16 == 0) {
            getObjects(Curve.class).forEach(t -> t.updateImage());
            getObjects(TrackStraight.class).forEach(t -> t.updateImage());
            getObjects(Turnout.class).forEach(t -> t.updateImage());
                        try
            {
                // Write names of trains onto layout blocks
                displayTrainIds();
 

            }
            catch (org.json.JSONException jsone)
            {
                jsone.printStackTrace();
            }
            //System.out.println(JMRI.getInterface().getTrainOnLayoutBlock("IRreporter1"));
        }
        
    }
    
    /**
     * Write the names of the trains near the layout blocks they currently occupy.
     */
    private void displayTrainIds() throws org.json.JSONException {
        // collect all kinds of tracks in a single list
        List<TrackStraight> tracksA = getObjects(TrackStraight.class);
        List<Curve>         tracksB = getObjects(Curve.class);
        List<Turnout>       tracksC = getObjects(Turnout.class);
        
        List<Track> allTracks = new ArrayList<>();
        allTracks.addAll(tracksA);
        allTracks.addAll(tracksB);
        allTracks.addAll(tracksC);

        
        // generate set of distinct layout blocks associated with the tracks
        Set<String> layoutBlockNames = new HashSet<>();
        for(Track t : allTracks) {
            if(t != null && t.getLayoutBlock() != null) {
                layoutBlockNames.add(t.getLayoutBlock());
            }
        }
        
        // clear background (delete old train names) before drawing the new names
        clearBackground();
        
        // for every layout block...
        for(String layoutBlockName : layoutBlockNames) {
            // 1. query name of occupying train

            
            JSONObject queryResult = JMRI.getInterface().getTrainOnLayoutBlock(layoutBlockName);
           
            // no such train on this layout block?
            // --> continue with next layout block
            if(queryResult == null  || !queryResult.has("data") || !queryResult.getJSONObject("data").has("name")) {
                continue;
            } // else (implicit)
            String trainName = queryResult.getJSONObject("data").getString("name");
            //Rückmeldung in Konsole
            //System.out.println("Der Zug: " + trainName + " befindet sich auf Block: " + layoutBlockName);
            // 2. get all the tracks belonging to that layout block
            List<Track> tracksBelongingToBlock = new ArrayList<>();
            for(Track t : allTracks) {
                if(t.getLayoutBlock()==(layoutBlockName)) {
                    tracksBelongingToBlock.add(t);
                }

            }
            
            // 3. calculate centroid of all the tracks belonging to the layout block
            Centroid centroid = calculateCentroid(tracksBelongingToBlock);
            // 4. display train name as string at centroid
            getBackground().drawString(trainName, (int) centroid.x*64, (int) centroid.y*64);
        }
    }
    
    /**
     * Calculates the 2D geometric centre of the tracks from the given list and returns the x and y coordinates as a Centroid object.
     * 
     * @param tracks A list of tracks whose centre point is to be calculated
     * 
     * @return The x and y coordinates of the 2D geometric centre of the tracks from the given list as a Centroid object
     */
    private Centroid calculateCentroid(List<Track> tracks) {
        double centroidX = 0.0d;
        double centroidY = 0.0d;
        for(Track t : tracks) {
            centroidX += t.getX();
            centroidY += t.getY();
        }
        return new Centroid(centroidX / (double) tracks.size(), centroidY / (double) tracks.size());
    }
    
    /**
     * Represents a point in two-dimensional space whose x and y coordinates have a double resolution.
     */
    private class Centroid {
        /**
         * The x coordinate of the Centroid
         */
        double x;
        /**
         * The y coordinate of the Centroid
         */
        double y;
        
        /**
         * Constructs a centroid object, i.e. a point in two-dimensional space determined by the given x and y coordinates.
         * 
         * @param x The x coordinate of the Centroid
         * @param y The y coordinate of the Centroid
         */
        protected Centroid(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Draws a greyish background grid indicating the boundaries of the cells into which the world is divided.
     */
    private void paintBackgroundGrid() {
        GreenfootImage bg = getBackground();

        bg.setColor(Color.GRAY);
        for(int x = 1; x < getWidth(); x++) {
            bg.drawLine(x * getCellSize(), 0, x * getCellSize(), getHeight() * getCellSize());
        }
        for(int y = 1; y < getWidth(); y++) {
            bg.drawLine(0, y * getCellSize(), getWidth() * getCellSize(), y * getCellSize());
        }
    }
    
    /**
     * Initialises the objects to be displayed in the world and places them in it.
     */
    private void prepare() {
        TrackStraight trackStraight0 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight0,4,1); 
        TrackStraight trackStraight1 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight1,5,1);        
        TrackStraight trackStraight2 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight2,6,1);
        TrackStraight trackStraight3 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight3,7,1);
        TrackStraight trackStraight4 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight4,8,1);
        TrackStraight trackStraight5 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight5,9,1);
        TrackStraight trackStraight6 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight6,10,1);
        TrackStraight trackStraight7 = new TrackStraight(StraightType.HORIZONTAL, "Oben");
        addObject(trackStraight7,11,1);

        TrackStraight trackStraight20 = new TrackStraight(StraightType.HORIZONTAL,"Links");
        addObject(trackStraight20,4,6); 
        //TrackStraight trackStraight21 = new TrackStraight(StraightType.HORIZONTAL);
        //addObject(trackStraight21,5,6);        
        TrackStraight trackStraight22 = new TrackStraight(StraightType.HORIZONTAL,"Unten");
        addObject(trackStraight22,6,5);
        TrackStraight trackStraight23 = new TrackStraight(StraightType.HORIZONTAL,"Unten");
        addObject(trackStraight23,7,5);
        TrackStraight trackStraight24 = new TrackStraight(StraightType.HORIZONTAL,"Unten");
        addObject(trackStraight24,8,5);
        TrackStraight trackStraight25 = new TrackStraight(StraightType.HORIZONTAL,"Unten");
        addObject(trackStraight25,9,5);
        //TrackStraight trackStraight26 = new TrackStraight(StraightType.HORIZONTAL);
        //addObject(trackStraight26,10,6);
        TrackStraight trackStraight27 = new TrackStraight(StraightType.HORIZONTAL,"Unten");
        addObject(trackStraight27,11,6);

        TrackStraight trackStraight30 = new TrackStraight(StraightType.VERTICAL,"Links");
        addObject(trackStraight30,3,2);
        TrackStraight trackStraight31 = new TrackStraight(StraightType.VERTICAL,"Links");
        addObject(trackStraight31,3,3);
        TrackStraight trackStraight32 = new TrackStraight(StraightType.VERTICAL,"Links");
        addObject(trackStraight32,3,4);
        TrackStraight trackStraight33 = new TrackStraight(StraightType.VERTICAL,"Links");
        addObject(trackStraight33,3,5);

        TrackStraight trackStraight40 = new TrackStraight(StraightType.VERTICAL,"Rechts");
        addObject(trackStraight40,12,2);
        TrackStraight trackStraight41 = new TrackStraight(StraightType.VERTICAL,"Rechts");
        addObject(trackStraight41,12,3);
        TrackStraight trackStraight42 = new TrackStraight(StraightType.VERTICAL,"Rechts");
        addObject(trackStraight42,12,4);
        TrackStraight trackStraight43 = new TrackStraight(StraightType.VERTICAL,"Rechts");
        addObject(trackStraight43,12,5);

        TrackStraight trackStraight52 = new TrackStraight(StraightType.HORIZONTAL,"Weiche");
        addObject(trackStraight52,6,7);
        TrackStraight trackStraight53 = new TrackStraight(StraightType.HORIZONTAL,"Weiche");
        addObject(trackStraight53,7,7);
        TrackStraight trackStraight54 = new TrackStraight(StraightType.HORIZONTAL,"Weiche");
        addObject(trackStraight54,8,7);
        TrackStraight trackStraight55 = new TrackStraight(StraightType.HORIZONTAL,"Weiche");
        addObject(trackStraight55,9,7);

        Curve curve = new Curve(CurveType.BOTTOM_RIGHT);
        addObject(curve,3,1);
        Curve curve2 = new Curve(CurveType.BOTTOM_LEFT);
        addObject(curve2,12,1);
        Curve curve3 = new Curve(CurveType.TOP_RIGHT);
        addObject(curve3,3,6);
        Curve curve4 = new Curve(CurveType.TOP_LEFT);
        addObject(curve4,12,6);
        Turnout turnout = new Turnout(TurnoutType.LEFT, "DT2");
        addObject(turnout,5,6);
        Turnout turnout2 = new Turnout(TurnoutType.RIGHT, "DT4");
        addObject(turnout2,10,6);
        Curve curve5 = new Curve(CurveType.BOTTOM_RIGHT);
        addObject(curve5,5,5);
        Curve curve6 = new Curve(CurveType.BOTTOM_LEFT);
        addObject(curve6,10,5);
        Curve curve7 = new Curve(CurveType.TOP_RIGHT);
        addObject(curve7,5,7);
        Curve curve8 = new Curve(CurveType.TOP_LEFT);
        addObject(curve8,10,7);
        ButtonGo buttonGo = new ButtonGo("S3");
        addObject(buttonGo,1,9);
        ButtonStop buttonStop = new ButtonStop("S3");
        addObject(buttonStop,3,9);
        ButtonChangeDirections buttonChangeDirections = new ButtonChangeDirections("S3");
        addObject(buttonChangeDirections,2,9);
        ButtonHorn buttonHorn = new ButtonHorn("S3");
        addObject(buttonHorn,5,9);

        ButtonScript buttonScript = new ButtonScript();
        addObject(buttonScript,5,10);
        
    }
    
    /**
     * Erases the displayed background of the world so that any drawn strings become invisible and redraws
     * the background grid. The placed objects should also be visible again (or still visible) afterwards.
     */
    private void clearBackground() {
        Color oldColor = getBackground().getColor();
        getBackground().clear();
        getBackground().setColor(Color.WHITE);
        getBackground().fill();
        paintBackgroundGrid();
        getBackground().setColor(oldColor);
    }
       
}
