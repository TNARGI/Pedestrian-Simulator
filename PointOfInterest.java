/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps_main;

public class PointOfInterest
{
    private String id;
    private double[] location;
    private int gravity;
    private int radius;
    private int holdingPeriod;
    private int priority;

    
    public PointOfInterest(double[] location, String id)
    {
        this.id = id;
        this.location = location;
        this.gravity = 50;
        this.radius = 100;
        this.holdingPeriod = 20;
        this.priority = 1;
    }
    
    /*
    public PointOfInterest(double x, double y, int gravity, int radius, int holdingPeriod, int priority)
    {
        this.location[0] = x;
        this.location[1] = y;
        this.gravity = gravity;
        this.radius = radius;
        this.holdingPeriod = holdingPeriod;
        this.priority = priority;
    }
    */
    
    
    
    ////////////////////////////////// GETTERS AND SETTERS
    public double[] getLocation()
    {
        return location;
    }

    public void setLocation(double[] location)
    {
        this.location = location;
    }

    public int getGravity()
    {
        return gravity;
    }

    public void setGravity(int gravity)
    {
        this.gravity = gravity;
    }

    public int getRadius()
    {
        return radius;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getHoldingPeriod()
    {
        return holdingPeriod;
    }

    public void setHoldingPeriod(int holdingPeriod)
    {
        this.holdingPeriod = holdingPeriod;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    
}

