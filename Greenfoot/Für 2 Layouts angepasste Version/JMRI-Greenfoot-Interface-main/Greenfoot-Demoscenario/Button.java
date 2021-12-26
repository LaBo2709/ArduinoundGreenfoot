import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)

/**
 * A button is an abstract and special type of control that has an onClick method.
 * It represents controls in the world that can be clicked and should react to that click.
 * 
 * @author Leonard Bienbeck
 * @version 1.0.0
 */
public abstract class Button extends Control
{
    
    /**
     * Abstract method to describe what happens when the button is clicked.
     */
    public abstract void onClick();
    
}
