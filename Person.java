package ps_main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Iterator;

public class Person
{
    private boolean testing = false;
    
    /// Properties
    private double[] location = {0,0}; // Might be better as a dictionary. eg. {"x": 100, "y": 200}
    //private double charge = 2e-5; // worked
    private double charge = 2e-5;
    private int mass = 80;
    private double[] finalDestination =
    {
        0, 0
    };
    private double[] immediateDestination =
    {
        0, 0
    }; // bad notation???
    private double impatience = 0.7; // used to be 0.7
    private boolean reachedDestination = false;
    private int interest = 0;
    private double averageDistanceFromOthers = 0;

    private boolean nextToWall = false;
    private boolean againstWall = false;

    /// Paths to destination
    private Deque<Node> pathDeque = new ArrayDeque<Node>();
    private LinkedList<Node> pathLL = new LinkedList<Node>();

    /// Knowledge of surroundings
    private ArrayList<PointOfInterest> poiCatalogue = new ArrayList<PointOfInterest>();

    /// Kinematics
    private double[] intentionalAcceleration = {0, 0};
    private double[] coulombAcceleration = {0, 0};
    private double[] maxAcceleration = {0, 0};
    private double maxAccelerationScalar = 1; // used to be 4
    private double[] resultantAcceleration = {0, 0};
    private double totalAcceleration = 0;
    
    private double[] velocity = {0, 0};
    private double[] maxVelocity = {0, 0}; // should be converted to max speed and vectors calculated
    private double maxVelocityScalar = 2; //used to be 6
    private double[] preferredVelocity = {0, 0};
    private double preferredSpeed = 1; // used to be 2

    /// Not yet implemented
    private int age = 30;
    private int personalSpaceRadius = 30;
    private int physicalRadius = 10;
    private String state = "calm"; // Could be calm or panicked
    private int urgency = 2; // Could be dependent on destination or panic state

    /// CONSTRUCTOR
    public Person(double[] location)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - Person()");
        }
        this.location = location;
    }

    //// CALLED 14th
    public void computePreferencesAndMaxima()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - computePreferencesAndMaxima()");
        
            System.out.println("Current Location: " + getLocation()[0] + "," + getLocation()[1]);
            System.out.println("Imeediate Destination: " + getImmediateDestination()[0] + "," + getImmediateDestination()[1]);
        }
        
        double dy = getImmediateDestination()[1] - getLocation()[1]; // calc. x-distance to immediate destination
        double dx = getImmediateDestination()[0] - getLocation()[0]; // calc. y-distance to immediate destination

        
        if(testing)
        {
            System.out.println("dy:  " + dy);
            System.out.println("dx:  " + dx);        
        }
        
        double gradient = dy / dx; // calc. dy/dx

        double angleOfTravel = Math.atan(gradient); // calc. angle

        // Convert angle from -0<x<0 to 0<x<360
        if(dx == 0 && dy < 0)
        {
            angleOfTravel += (2 * Math.PI);
        }
        if(dy == 0 && dx < 0)
        {
            angleOfTravel += Math.PI;
        }
        if (dx < 0 && dy > 0)
        {
            angleOfTravel += Math.PI;
        }
        if (dy < 0 && dx > 0)
        {
            angleOfTravel += (2 * Math.PI);
        }
        if (dx < 0 && dy < 0)
        {
            angleOfTravel += Math.PI;
        }

        if(testing)
        {
            System.out.println("ANGLE (degrees): " + angleOfTravel * (180 / Math.PI)); // print angle
        }
        
        double[] pref_vel = new double[]
        {
            getPreferredSpeed() * Math.cos(angleOfTravel), getPreferredSpeed() * Math.sin(angleOfTravel)
        }; // calc. preferred velocity

        // if x-distance is less than --- set preferred velocity to zero (MIGHT NEED TO REMOVE THESE)
        if (Math.abs(dx) < 0.5)
        {
            pref_vel[0] = 0;
        }
        // if y-distance is less than --- set preferred velocity to zero (MIGHT NEED TO REMOVE THESE)
        if (Math.abs(dy) < 0.5)
        {
            pref_vel[1] = 0;
        }
        setPreferredVelocity(pref_vel); // set preferred velocity
        if(testing)
        {
            System.out.println("Preferred Velocity: " + getPreferredVelocity()[0] + "," + getPreferredVelocity()[1]);
        }
        
        double[] max_vel = new double[]
        {
            getMaxVelocityScalar() * Math.cos(angleOfTravel), getMaxVelocityScalar() * Math.sin(angleOfTravel)
        }; // calc. max velocity

        // if x-distance is less than --- set max velocity to zero
        if (Math.abs(dx) < 0.5)
        {
            max_vel[0] = 0;
        }
        // if y-distance is less than --- set max velocity to zero
        if (Math.abs(dy) < 0.5)
        {
            max_vel[1] = 0;
        }
        setMaxVelocity(max_vel); // set max velocity
        if(testing)
        {
            System.out.println("Max Velocity: " + getMaxVelocity()[0] + "," + getMaxVelocity()[1]);
        }
        
        double[] max_acc = new double[]
        {
            getMaxAccelerationScalar() * Math.cos(angleOfTravel), getMaxAccelerationScalar() * Math.sin(angleOfTravel)
        }; // calc. max acceleration

        // if x-distance is less than --- set max acceleration to zero
        if (Math.abs(dx) < 0.5)
        {
            max_acc[0] = 0;
        }
        // if y-distance is less that --- set max acceleration to zero
        if (Math.abs(dy) < 0.5)
        {
            max_acc[1] = 0;
        }
        setMaxAcceleration(max_acc); // set max accleration
        if(testing)
        {
            System.out.println("Max Acceleration: " + getMaxAcceleration()[1]);
        }
    }

    //// CALLED 13th
    public void computeIntentionalAcceleration() // ERROR - y-component of int_acc might be calculated incorrectly
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - computeIntentionalAcceleration()");
        }
        //setFinalDestination(poiCatalogue.get(ThreadLocalRandom.current().nextInt(0, 20 + 1)).getLocation());

        /// a = 1/tau * (Vp - Vc)   Johansson
        double tau = this.impatience; // grab impatience
        double distanceX = getImmediateDestination()[0] - getLocation()[0]; // compute x-distance to immediate destination
        double distanceY = getImmediateDestination()[1] - getLocation()[1]; // compute y-distance to immediate destination
        if(testing)
        {
            System.out.println("Distance to Immediate Destination: " + distanceX + "," + distanceY);
        }
        // formula calculation of intentional acceleration
        double[] int_acc = new double[]
        {
            (1 / tau * (getPreferredVelocity()[0] - getVelocity()[0])),
            (1 / tau * (getPreferredVelocity()[1] - getVelocity()[1]))
        };
        

        // if x-distance to immediate destination is less than --- set intentional acceleration to zero (MIGHT NEED TO REMOVE THIS)
        if (Math.abs(distanceX) < 0.5)
        {
            int_acc[0] = 0;
        }
        // if x-distance to immediate destination is less than --- set intentional acceleration to zero (MIGHT NEED TO REMOVE THIS)
        if (Math.abs(distanceY) < 0.5)
        {
            int_acc[1] = 0;
        }
        setIntentionalAcceleration(int_acc); // set intentional acceleration
        //this.totalAcceleration += Math.abs(Math.sqrt(Math.pow(int_acc[0],2)+Math.pow(int_acc[1],2)));
        addAccelerationElement(int_acc);
        
        if(testing)
        {
            System.out.println("Intentional Acceleration: " + getIntentionalAcceleration()[0] + "," + getIntentionalAcceleration()[1]);
        }
        // if x and y distances to final destination are BOTH less than --- set intentional acceleration and velocity to zero
        if (Math.abs(this.finalDestination[0] - this.location[0]) < 1 && Math.abs(this.finalDestination[1] - this.location[1]) < 1)
        {
            double[] zero = new double[]
            {
                0, 0
            };
            setIntentionalAcceleration(zero);
            setVelocity(zero);
            if(testing)
            {
                System.out.println("Destination reached");
                System.out.print("reducing interest from ");
                System.out.print(interest);
            }
            interest--;
            if(testing)
            {
                System.out.println(" to " + interest);
            }
        }

    } // End of computeIntentionalAcceleration()

    //// CALLED 16th
    public void computeResultantAcceleration()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - computeResultantAcceleration()");
        }
        // calc. resultant acceleration
        double[] res_acc = new double[]
        {
            (getIntentionalAcceleration()[0] + getCoulombAcceleration()[0]),
            (getIntentionalAcceleration()[1] + getCoulombAcceleration()[1])
        };

        setResultantAcceleration(res_acc); // set resultant acceleration
        if(testing)
        {
            System.out.println("Coulomb: " + getCoulombAcceleration()[0] + "," + getCoulombAcceleration()[1]);
            System.out.println("Intentional: " + getIntentionalAcceleration()[0] + "," + getIntentionalAcceleration()[1]);
            System.out.println("Resultant: " + getResultantAcceleration()[0] + "," + getResultantAcceleration()[1]);
        }
    }

    //// CALLED 15th
    public void computeVelocity(double interval) // ERROR - y-component of vel might be calculated incorrectly
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - computeVelocity()");
        }
        
        // calc. velocity from formula ( v=u+at )
        double[] vel = new double[]
        {
            getVelocity()[0] + (getResultantAcceleration()[0] * interval / 1000),
            getVelocity()[1] + (getResultantAcceleration()[1] * interval / 1000)
        };
        setVelocity(vel); // set velocity
        if(testing)
        {
            System.out.println("Velocity: " + getVelocity()[0] + "," + getVelocity()[1]);
        }
        
    } // End of compute Velocity()

    //// CALLED 6.5th
    public void addCoulombAcceleration(double[] acc)
    {
        //System.out.println("Person - addCoulombAcceleration()");

        // calc. new coulomb acceleration
        double[] cou_acc = new double[]
        {
            getCoulombAcceleration()[0] + acc[0],
            getCoulombAcceleration()[1] + acc[1]
        };
        setCoulombAcceleration(cou_acc); // set coulomb acceleration
        //this.totalAcceleration += Math.abs(Math.sqrt(Math.pow(cou_acc[0],2)+Math.pow(cou_acc[1],2)));
        //addAccelerationElement(acc);

    } // End of addCoulombAcceleration()
    
    
    
    //// CALLED 6.5th
    public void addAccelerationElement(double[] acc)
    {
        double acceleration = Math.abs(Math.sqrt(Math.pow(acc[0], 2) + Math.pow(acc[1], 2)));
        
        //System.out.println("Adding: " + acceleration);
        
        setTotalAcceleration(getTotalAcceleration() + acceleration);
        
    } // End of addAccelerationElement()

    
    
    public void selectImmediateDestination()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - selectImmediateDestination()");
        
            System.out.println("Current Location: " + getLocation()[0] + "," + getLocation()[1]);
            System.out.println("Final Destination: " + getFinalDestination()[0] + "," + getFinalDestination()[1]);
        }
        
        //System.out.println("PathLL empty: " + pathLL.isEmpty());
        //for(Node n: pathDeque)
        //{
        //    System.out.println(n.getLocation()[0] +", " + n.getLocation()[1]);
        //}

        // if final destiantion IS null
        
        
        
        if (getFinalDestination() == null)
        {
            if(testing)
            {
                System.out.println("Trying to set immediate destination but final destination is null");
            }
            return; // exit method
        }

        // if x and y distances to final destination are BOTH less than ---
        if (Math.abs(getFinalDestination()[0] - getLocation()[0]) < 1.2 && Math.abs(getFinalDestination()[1] - getLocation()[1]) < 1.2)
        {
            setImmediateDestination(getFinalDestination()); // set final destination as immediate destination
            if(testing)
            {
                System.out.println("Final destination set as immediate");
            }
        }

        // if x and y distances to final destination are not both less than ---
        else
        {
            try // try to fetch the next waypoint and set as immediate destination
            {
                setImmediateDestination(new double[]
                {
                    (double) pathLL.getLast().getLocation()[0],
                    (double) pathLL.getLast().getLocation()[1]
                }
                );
                if(testing)
                {
                    System.out.println((double) pathLL.getLast().getLocation()[0] + "," + (double) pathLL.getLast().getLocation()[1] + " set as immediate destination");
                }
            }
            catch (Exception e)
            {
                System.out.println("Could not find element in pathDeque");
            }

            // if x and y distances to immediate destination are BOTH less than ---
            if (Math.abs(getImmediateDestination()[0] - getLocation()[0]) < 1 && Math.abs(getImmediateDestination()[1] - getLocation()[1]) < 1)
            {
                pathLL.removeLast(); // remove waypoint

                // set immediate destination as next waypoint
                setImmediateDestination(new double[]
                {
                    (double) pathLL.getLast().getLocation()[0],
                    (double) pathLL.getLast().getLocation()[1]
                }
                );
                if(testing)
                {
                    System.out.println((double) pathLL.getLast().getLocation()[0] + "," + (double) pathLL.getLast().getLocation()[1] + " set as immediate destination");
                }
                
                // if x and y distances to immediate destination are less than ---
                if (Math.abs(getImmediateDestination()[0] - getLocation()[0]) < 1 && Math.abs(getImmediateDestination()[1] - getLocation()[1]) < 1)
                {

                    pathLL.removeLast(); // remove waypoint

                    if(testing)
                    {
                        System.out.println("deque.size: " + pathLL.size());
                    }
                    
                    // set immediate destination as next waypoint
                    setImmediateDestination(new double[]
                    {
                        (double) pathLL.getLast().getLocation()[0],
                        (double) pathLL.getLast().getLocation()[1]
                    }
                    );
                    if(testing)
                    {
                        System.out.println((double) pathLL.getLast().getLocation()[0] + "," + (double) pathLL.getLast().getLocation()[1] + " set as immediate destination");
                    }
                }
            }
        }
        
        if(testing)
        {
            System.out.println("ImmediateDestination: " + getImmediateDestination()[0] + "," + getImmediateDestination()[1]); 
        }
    }
    
    
    
    
    public void findImmediateDestination()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - findImmediateDestination()");
        
            System.out.println("Current Location: " + getLocation()[0] + "," + getLocation()[1]);
            System.out.println("Final Destination: " + getFinalDestination()[0] + "," + getFinalDestination()[1]);
        }
        
        // if final destiantion IS null
        if (getFinalDestination() == null)
        {
            System.out.println("Trying to set immediate destination but final destination is null");
            return; // exit method
        }

        // if x and y distances to final destination are BOTH less than ---
        if (Math.abs(getFinalDestination()[0] - getLocation()[0]) < 1.2 && Math.abs(getFinalDestination()[1] - getLocation()[1]) < 1.2)
        {
            setImmediateDestination(getFinalDestination()); // set final destination as immediate destination
            if(testing)
            {
                System.out.println("Very close to final destintation");
                System.out.println("Final destination set as immediate");            
            }
        }

        // if x and y distances to final destination are not both less than ---
        else
        {
            try // try to fetch the next waypoint and set as immediate destination
            {
                setImmediateDestination(new double[]
                {
                    (double) pathLL.getFirst().getLocation()[0],
                    (double) pathLL.getFirst().getLocation()[1]
                }
                );
                if(testing)
                {
                    System.out.println("Not close to final destination");
                    System.out.println((double) pathLL.getFirst().getLocation()[0] + "," + (double) pathLL.getFirst().getLocation()[1] + " set as immediate destination");
                    //System.out.println("first on list woulld have been: " + (double) pathLL.getFirst().getLocation()[0] + "," + (double) pathLL.getFirst().getLocation()[1]);
                }
            }
            catch (Exception e)
            {
                System.out.println("Could not find element in pathDeque");
            }

            // if x and y distances to immediate destination are BOTH less than ---
            if (Math.abs(getImmediateDestination()[0] - getLocation()[0]) < 1 && Math.abs(getImmediateDestination()[1] - getLocation()[1]) < 1)
            {
            if(pathLL.size()>1)
            {
                pathLL.removeFirst(); // remove waypoint

                // set immediate destination as next waypoint
                setImmediateDestination(new double[]
                {
                    (double) pathLL.getFirst().getLocation()[0],
                    (double) pathLL.getFirst().getLocation()[1]
                }
                );
                if(testing)
                {
                    System.out.println("Already at immediate destination, remove and grab next");
                    System.out.println((double) pathLL.getFirst().getLocation()[0] + "," + (double) pathLL.getFirst().getLocation()[1] + " set as immediate destination");
                }
                
                // if x and y distances to immediate destination are less than ---
                if (Math.abs(getImmediateDestination()[0] - getLocation()[0]) < 1 && Math.abs(getImmediateDestination()[1] - getLocation()[1]) < 1)
                {
                if(pathLL.size()>1)
                {
                    pathLL.removeFirst(); // remove waypoint

                    if(testing)
                    {
                        System.out.println("deque.size: " + pathLL.size());
                    }
                    
                    // set immediate destination as next waypoint
                    setImmediateDestination(new double[]
                    {
                        (double) pathLL.getFirst().getLocation()[0],
                        (double) pathLL.getFirst().getLocation()[1]
                    }
                    );
                    if(testing)
                    {
                        System.out.println("AGAIN, already at immediate destination, remove and grab next");
                        System.out.println((double) pathLL.getFirst().getLocation()[0] + "," + (double) pathLL.getFirst().getLocation()[1] + " set as immediate destination");
                    }
                }
                }
            }
            }
        }
        
        if(testing)
        {
            System.out.println("ImmediateDestination: " + getImmediateDestination()[0] + "," + getImmediateDestination()[1]);
        }
    }
        
    
        
        
        
  
    public void move(double interval)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - move()");
        
            System.out.println("Moving from: " + getLocation()[0] + "," + getLocation()[1]);
        }
        
        /*
        selectImmediateDestination();
        
        //System.out.println("Loc: " + getLocation()[0] + ", " + getLocation()[1]);
        
        // if final destination is NOT null, print immediate an final destinations
        if(getFinalDestination() != null)
        {
            System.out.println("IDest: " + getImmediateDestination()[0] + ", " + getImmediateDestination()[1]);
            System.out.println("FDest: " + getFinalDestination()[0] + ", " + getFinalDestination()[1]);
        }
        
        System.out.println("");
        
        computePreferencesAndMaxima();
        //System.out.println("PREF Vel: " + getPreferredVelocity()[0] + ", " + getPreferredVelocity()[1]);
        //System.out.println("MAX Vel: " + getMaxVelocity()[0] + ", " + getMaxVelocity()[1]);
        //System.out.println("MAX Acc: " + getMaxAcceleration()[0] + ", " + getMaxAcceleration()[1]);
        //System.out.println("");
        
        computeIntentionalAcceleration();
        //System.out.println("IntAcc: " + getIntentionalAcceleration()[0] + ", " + getIntentionalAcceleration()[1]);
        //System.out.println("CouAcc: " + getCoulombAcceleration()[0] + ", " + getCoulombAcceleration()[1]);

        computeResultantAcceleration();
        //System.out.println("ResAcc: " + getResultantAcceleration()[0] + ", " + getResultantAcceleration()[1]);
        //System.out.println("");
        
        computeVelocity(interval);
        //System.out.println("Vel: " + getVelocity()[0] + ", " + getVelocity()[1]);


        */
        
        
        
        // calc. distance moved using formula ( s=vt-(at^2)/2 )
        double[] distanceMoved = {0,0};
        distanceMoved[0] = (getVelocity()[0] * interval/1000) - ( (getResultantAcceleration()[0] * Math.pow((interval/1000),2)) / 2); // compute x-movement
        distanceMoved[1] = (getVelocity()[1] * interval/1000) - ( (getResultantAcceleration()[1] * Math.pow((interval/1000),2)) / 2); // compute y-movement
                
        
        // set new location
        setLocation(new double[]
        {
            getLocation()[0] + distanceMoved[0], 
            getLocation()[1] + distanceMoved[1]}
        );
        
        if(testing)
        {
            System.out.println("Moving to: " + getLocation()[0] + "," + getLocation()[1]);
        }
        
    } // End of move()
    
    /*
    public void setNewFinalDestination()
    {
        System.out.println("");
        System.out.println("setNewFinalDestination");
        pathDeque.clear();
        
        System.out.println("current path");
        for(Node n: pathLL)
        {
            System.out.println(n.getLocation());
        }
        System.out.println("clearing pathLL");
        pathLL.clear();
        System.out.println("new path is");
        for(Node n: pathLL)
        {
            System.out.println(n.getLocation());
        }
        System.out.println("Path empty " + pathLL.isEmpty());
        
        
        
        
        setFinalDestination(poiCatalogue.get(ThreadLocalRandom.current().nextInt(0, poiCatalogue.size())).getLocation());
        //setInterest(ThreadLocalRandom.current().nextInt(3, 15));
        System.out.println("Interest refreshed to: " + getInterest());
    }
    */
    
    
    
    
    
    public void resetForces()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("Person - resetForces()");
        }
        double[] zero = {0, 0};
        
        setIntentionalAcceleration(zero);
        setCoulombAcceleration(zero);
        setResultantAcceleration(zero);
        
        //setTotalAcceleration(0);
        setAverageDistanceFromOthers(0);
    }
    
    /// GETTERS AND SETTERS
    public double[] getLocation()
    {
        return location;
    }

    public void setLocation(double[] location)
    {
        this.location = location;
    }

    public double getCharge()
    {
        return charge;
    }

    public void setCharge(double charge)
    {
        this.charge = charge;
    }

    public int getMass()
    {
        return mass;
    }

    public void setMass(int mass)
    {
        this.mass = mass;
    }

    public double[] getFinalDestination()
    {
        return finalDestination;
    }

    public void setFinalDestination(double[] finalDestination)
    {
        this.finalDestination = finalDestination;
    }

    public double[] getImmediateDestination()
    {
        return immediateDestination;
    }

    public void setImmediateDestination(double[] immediateDestination)
    {
        this.immediateDestination = immediateDestination;
    }

    public double getImpatience()
    {
        return impatience;
    }

    public void setImpatience(double impatience)
    {
        this.impatience = impatience;
    }

    public Deque<Node> getPathDeque()
    {
        return pathDeque;
    }

    public void setPathDeque(Deque<Node> pathDeque)
    {
        this.pathDeque = pathDeque;
    }

    public LinkedList<Node> getPathLL()
    {
        return pathLL;
    }

    public void setPathLL(LinkedList<Node> pathLL)
    {
        this.pathLL = pathLL;
    }
    
    public double[] getIntentionalAcceleration()
    {
        return intentionalAcceleration;
    }

    public void setIntentionalAcceleration(double[] intentionalAcceleration)
    {
        this.intentionalAcceleration = intentionalAcceleration;
    }

    public double[] getCoulombAcceleration()
    {
        return coulombAcceleration;
    }

    public void setCoulombAcceleration(double[] coulombAcceleration)
    {
        this.coulombAcceleration = coulombAcceleration;
    }

    public double[] getMaxAcceleration()
    {
        return maxAcceleration;
    }

    public void setMaxAcceleration(double[] maxAcceleration)
    {
        this.maxAcceleration = maxAcceleration;
    }

    public double getMaxAccelerationScalar()
    {
        return maxAccelerationScalar;
    }

    public void setMaxAccelerationScalar(double maxAccelerationScalar)
    {
        this.maxAccelerationScalar = maxAccelerationScalar;
    }

    public double[] getResultantAcceleration()
    {
        return resultantAcceleration;
    }

    public void setResultantAcceleration(double[] resultantAcceleration)
    {
        this.resultantAcceleration = resultantAcceleration;
    }

    public double[] getVelocity()
    {
        return velocity;
    }

    public void setVelocity(double[] velocity)
    {
        this.velocity = velocity;
    }

    public double[] getMaxVelocity()
    {
        return maxVelocity;
    }

    public void setMaxVelocity(double[] maxVelocity)
    {
        this.maxVelocity = maxVelocity;
    }

    public double getMaxVelocityScalar()
    {
        return maxVelocityScalar;
    }

    public void setMaxVelocityScalar(double maxVelocityScalar)
    {
        this.maxVelocityScalar = maxVelocityScalar;
    }

    public double[] getPreferredVelocity()
    {
        return preferredVelocity;
    }

    public void setPreferredVelocity(double[] preferredVelocity)
    {
        this.preferredVelocity = preferredVelocity;
    }

    public double getPreferredSpeed()
    {
        return preferredSpeed;
    }

    public void setPreferredSpeed(double preferredSpeed)
    {
        this.preferredSpeed = preferredSpeed;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getPersonalSpaceRadius()
    {
        return personalSpaceRadius;
    }

    public void setPersonalSpaceRadius(int personalSpaceRadius)
    {
        this.personalSpaceRadius = personalSpaceRadius;
    }

    public int getPhysicalRadius()
    {
        return physicalRadius;
    }

    public void setPhysicalRadius(int physicalRadius)
    {
        this.physicalRadius = physicalRadius;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public int getUrgency()
    {
        return urgency;
    }

    public void setUrgency(int urgency)
    {
        this.urgency = urgency;
    }

    public boolean isNextToWall()
    {
        return nextToWall;
    }

    public void setNextToWall(boolean nextToWall)
    {
        this.nextToWall = nextToWall;
    }

    public boolean isAgainstWall()
    {
        return againstWall;
    }

    public void setAgainstWall(boolean againstWall)
    {
        this.againstWall = againstWall;
    }

    public int getInterest()
    {
        return interest;
    }

    public void setInterest(int interest)
    {
        this.interest = interest;
    }

    public ArrayList<PointOfInterest> getPoiCatalogue()
    {
        return poiCatalogue;
    }

    public void setPoiCatalogue(ArrayList<PointOfInterest> poiCatalogue)
    {
        this.poiCatalogue = poiCatalogue;
    }

    public double getAverageDistanceFromOthers()
    {
        return averageDistanceFromOthers;
    }

    public void setAverageDistanceFromOthers(double averageDistanceFromOthers)
    {
        this.averageDistanceFromOthers = averageDistanceFromOthers;
    }

    public boolean isTesting()
    {
        return testing;
    }

    public void setTesting(boolean testing)
    {
        this.testing = testing;
    }

    public double getTotalAcceleration()
    {
        return totalAcceleration;
    }

    public void setTotalAcceleration(double totalAcceleration)
    {
        this.totalAcceleration = totalAcceleration;
    }

    
}
