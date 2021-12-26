import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)

/**
 * Represents abstract control elements, for example buttons, with which inputs
 * can be made.
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public abstract class Control extends Actor
{
    /**
     * A thread on which asynchronous tasks can be performed that are started by
     * the control elements.
     */
    protected Thread controlThread = null;
    
    /**
     * Starts an asynchronous task by executing the given Runnable on a new thread.
     */
    protected void doAsync(Runnable runnable) {
        controlThread = new Thread(runnable);
        controlThread.start();
    }
    
    @Override
    public void act() 
    {
        // controls do not act
    }
    
    @Override
    public void move(int distance) {
        // controls do not move
    }
    
}
