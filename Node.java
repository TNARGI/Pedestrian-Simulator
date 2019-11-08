/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps_main;
public class Node
{
    private int[] location;
    private Node parent;
    private int heuristicCost;
    private int finalCost;
    private boolean solution;
    private boolean reachedByPedestrian;
    //private double charge = 3e-5; // worked
    private double charge = 2e-5;
    
    public Node(int x, int y)
    {
        this.location = new int[]{x,y};
    }
    
    @Override
    public String toString()
    {
        return "[" + location[0] + ", " + location[1] + "]";
    }

    public int[] getLocation()
    {
        return location;
    }

    public void setLocation(int[] location)
    {
        this.location = location;
    }

    public Node getParent()
    {
        return parent;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public int getHeuristicCost()
    {
        return heuristicCost;
    }

    public void setHeuristicCost(int heuristicCost)
    {
        this.heuristicCost = heuristicCost;
    }

    public int getFinalCost()
    {
        return finalCost;
    }

    public void setFinalCost(int finalCost)
    {
        this.finalCost = finalCost;
    }

    public boolean getSolution()
    {
        return solution;
    }

    public void setSolution(boolean solution)
    {
        this.solution = solution;
    }

    public boolean isReachedByPedestrian()
    {
        return reachedByPedestrian;
    }

    public void setReachedByPedestrian(boolean reachedByPedestrian)
    {
        this.reachedByPedestrian = reachedByPedestrian;
    }

    public double getCharge()
    {
        return charge;
    }

    public void setCharge(double charge)
    {
        this.charge = charge;
    }
    
    
    
    
}


