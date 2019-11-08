/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps_main;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Math;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class PS_Main
{
    private static int numberOfPeople = 5;
    private static int numberOfIterations = 10;
    private static int numberOfPois = 21;
    private static int deltaTime = 300;

    private static boolean testing = false;
    
    private static ArrayList<Person> peopleCatalogue = new ArrayList<Person>();
    private static ArrayList<PointOfInterest> poiCatalogue = new ArrayList<PointOfInterest>();
    private static String positionData = "";
    private static String metaData = "";
    private static String poiData = "";
    private static String blockData = "";
    
    /// Metric data
    private static String congestionData = "";
    private static String averageDistanceData = "";
    private static String averageVelocityData = "";
    private static String averageAccelerationData = "";
    private static String gridData = "";
    
    
    private static Date date = new Date();
    private static long startTime = date.getTime();
    private static long cycle = 0;
    
    
    private static Toolkit toolkit;
    private static Timer timer;
    private static TimerTask task;
    private static int seconds;
    private static int iterationCounter = 0;
    
    
    
    private static int mapWidth = 40;
    private static int mapHeight = 40;
    
    
    private static ArrayList<Node> blockedNodeCatalogue = new ArrayList<Node>();
    private static int[][] blockedNodes = new int[][]
    { 
    //    /* internal blocked nodes */ {10,5}, {10,6}, {10,7}, {10,8}, {10,9}, {10,10}, {10,11}, {10,12}, {10,13}, {10,14}, {10,15},
    //    /* more blocked nodes */ {10,16}, {10,17}, {10,18}, {10,19},
    //    /* even more blocked nodes */ {10,4}, {10,3}, {9,3}, {8,3}, {7,3}, {6,3}, {6,4}, {6,5}, {6,6}, {6,7}, {6,8}, {7,8}, {8,8}, {8,9}, 
    //    /* even more blocked nodes 2 */ {5,8}, {4,8}, {3,8}, {3,9}, {3,10}, {3,11}, {3,12}, {11,4}, {12,4}, {13,4}, {14,4}, {15,4}, {16,4}, 
    //    /* even more blocked nodes 3 */ {17,4}, {17,5}, {17,6}, {17,7}, {17,8}, {17,9}, {17,10}, {17,11}, {17,12}, {17,13}, {17,14}, 
        
    //    /* left edge wall 20x20 */ {0,0}, {0,1}, {0,2}, {0,3}, {0,4}, {0,5}, {0,6}, {0,7}, {0,8}, {0,9}, {0,10}, {0,11}, {0,12}, {0,13}, {0,14}, {0,15}, {0,16}, {0,17}, {0,18}, {0,19}, 
    //    /* right edge wall 20x20 */ {19,0}, {19,1}, {19,2}, {19,3}, {19,4}, {19,5}, {19,6}, {19,7}, {19,8}, {19,9}, {19,10}, {19,11}, {19,12}, {19,13}, {19,14}, {19,15}, {19,16}, {19,17}, {19,18}, {19,19}, 
    //    /* bottom edge wall 20x20 */ {1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {6,0}, {7,0}, {8,0}, {9,0}, {10,0}, {11,0}, {12,0}, {13,0}, {14,0}, {15,0}, {16,0}, {17,0}, {18,0},
    //    /* top edge wall 20x20 */ {1, 19}, {2, 19}, {3, 19}, {4, 19}, {5, 19}, {6, 19}, {7, 19}, {8, 19}, {9, 19}, {10, 19}, {11, 19}, {12, 19}, {13, 19}, {14, 19}, {15, 19}, {16, 19}, {17, 19}, {18, 19}
        
    };
    
    
    public static void main(String[] args) throws IOException, ParseException
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - main()");
        }
        //// Clear old data
        poiCatalogue.clear();
        peopleCatalogue.clear();
        blockedNodeCatalogue.clear();
        
        loadMap(2);
        createMapEdge(mapWidth, mapHeight);
        compileWalls();
        
        
        //// Add world objects
        //addOnePOI(35,1);
        addPOISet(1);
        
        
        //// Add pedestrians
        //addOneTestPedestrian_withCharge(37,34);
        //addOneTestPedestrian_withCharge(38,34);
        //addOneTestPedestrian_withoutCharge();
        //addFourTestPedestrians();
        addManyPedestrians(numberOfPeople);
        
        for(Person p: peopleCatalogue)
        {
            p.resetForces();
        }
        
        
        //// Build meta-data for export
        metaData += peopleCatalogue.size() + ",";
        metaData += numberOfIterations + ",";
        metaData += numberOfPois + ",";
        metaData += blockedNodes.length + "";
        
        //// Start simulation loop
        new PS_Main();
        
    } // End of main()
    
    
    //// CALLED 4th
    public PS_Main()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - PS_Main()");
        }
        timer = new Timer();
        
        timer.schedule(new RemindTask(), 
                       0,               //initial delay
                       1*deltaTime);    //subsequent rate
    }
 
    //// CALLED 5th
    class RemindTask extends TimerTask
    {       
        int iterations = numberOfIterations;
        public void run()
        {
            if(testing)
            {
                System.out.println("\n----------");
                System.out.println("PS_Main - RemindTask - run()");
            }
            /// MAIN SIMULATION LOOP
            if (iterations > 0)
            {
                if(testing)
                {
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                    System.out.println("#####       #####");
                    System.out.println("#               #");
                    System.out.println("# RUN           #");
                    System.out.println("# Iteration: " + iterationCounter + "  #");
                    System.out.println("#               #");
                    System.out.println("#####       #####");
                }
                else
                {
                    //System.out.println("Iteration: " + iterationCounter);
                }
                
                positionData += fetchPositionData(); // Store current location data
                
                //computeCoulombAcceleration();
                computeCoulombAcceleration_fromPeople();
                computeCoulombAcceleration_fromWalls();
                
                //System.out.println("Iteration: " + iterationCounter);
                
                //calculatePathIfNecessary_UsingDeque();
                //calculatePathIfNecessary_UsingLinkedList();
                
                //movePedestrians(deltaTime);
                for(Person p: peopleCatalogue)
                {
                    if(p.getInterest() <= 0)
                    {
                        p.setPathLL(null);
                        //p.setNewFinalDestination();
                        p.setFinalDestination(poiCatalogue.get(ThreadLocalRandom.current().nextInt(0, poiCatalogue.size())).getLocation());
                        p.setInterest(ThreadLocalRandom.current().nextInt(10, 100));
                        calculatePath(p);
                    }
                    
                    p.findImmediateDestination();
                    
                    p.computePreferencesAndMaxima();
                    p.computeIntentionalAcceleration();
                    p.computeResultantAcceleration();
                    p.computeVelocity(deltaTime);
                    
                    //p.selectImmediateDestination();
                    //p.findImmediateDestination();
                    p.move(deltaTime);
                    p.resetForces();
                }
                
                evaluateCongestion(1);
                evaluateCongestion(2);
                evaluateCongestion(3);
                evaluateCongestion(4);
                
                
                // Increment counters
                iterationCounter++;
                iterations--;
            }
            /// END OF SIMULATION LOOP - DATA EXPORT
            else
            {
                String filepath = "C:/Users/ExampleUser/Documents/";
                
                if(testing)
                {
                    System.out.println("exporting data");
                }
                /// Print data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "positionData.csv");                
                )
                {
                    byte[] contentToBytes = positionData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                /// Print metadata to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "metaData.csv");                
                )
                {
                    byte[] contentToBytes = metaData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                /// Print POI location data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "poiData.csv");                
                )
                {
                    byte[] contentToBytes = fetchPoiData().getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                
                /// Print blocked node location data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "blockData.csv");                
                )
                {
                    byte[] contentToBytes = fetchBlockData().getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                /// Print average distance data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "averageDistanceData.csv");                
                )
                {
                    byte[] contentToBytes = averageDistanceData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                /// Print average velocity data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "averageVelocityData.csv");                
                )
                {
                    byte[] contentToBytes = averageVelocityData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                
                /// Print average accleration data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "averageAccelerationData.csv");                
                )
                {
                    byte[] contentToBytes = averageAccelerationData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                
                
                
                /// Print section (4x4 grid) data to file
                try
                (
                    FileOutputStream outputStream = new FileOutputStream(filepath + "gridData.csv");                
                )
                {
                    byte[] contentToBytes = gridData.getBytes();
                    outputStream.write(contentToBytes);

                    outputStream.close();
                }
                catch(IOException e)
                {
                    System.out.println("EXCEPTION");
                }
                finally
                {
                    System.exit(0);
                }
            }
        }
    }
    
    
   
    
    //// CALLED 2nd
    public static void addFourTestPedestrians()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addFourTestPedestrians()");
        }
        Person p1 = new Person(new double[]{5,5});
        p1.setFinalDestination(null);
        peopleCatalogue.add(p1);
        
        Person p2 = new Person(new double[]{6,5});
        p2.setFinalDestination(null);
        peopleCatalogue.add(p2);
        
        Person p3 = new Person(new double[]{6,6});
        p3.setFinalDestination(null);
        peopleCatalogue.add(p3);
        
        Person p4 = new Person(new double[]{5,6});
        p4.setFinalDestination(null);
        peopleCatalogue.add(p4);    
    } // End of addTestPedestrians()
    
    
    //// CALLED 2nd
    public static void addOneTestPedestrian_withoutCharge(int locationX, int locationY)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addOneTestPedestrian_withoutCharge()");
        }
        Person newPerson = new Person(new double[]{locationX,locationY});
        double[] zero = {0,0};
        
        newPerson.setCharge(0);
        newPerson.setResultantAcceleration(zero);
        newPerson.setVelocity(zero);
        newPerson.setCoulombAcceleration(zero);
        newPerson.setPoiCatalogue(poiCatalogue);
        
        PointOfInterest destination = poiCatalogue.get(0);
        
        newPerson.setFinalDestination(destination.getLocation());
        
        peopleCatalogue.add(newPerson);
    } // End of addOneTestPedestrian_withoutCharge()
    
    
    //// CALLED 2nd
    public static void addOneTestPedestrian_withCharge(int locationX, int locationY)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addOneTestPedestrian_withCharge()");
        }
        Person newPerson = new Person(new double[]{locationX,locationY});
        double[] zero = {0,0};
        
        //newPerson.setCharge(1e-3);
        newPerson.setResultantAcceleration(zero);
        newPerson.setVelocity(zero);
        newPerson.setCoulombAcceleration(zero);
        
        PointOfInterest destination = poiCatalogue.get(0);
        
        newPerson.setFinalDestination(destination.getLocation());
        newPerson.setPoiCatalogue(poiCatalogue);
        
        peopleCatalogue.add(newPerson);
    } // End of addOneTestPedestrian_withCharge()
    
    
    
    
    
    
    
    
    /// Could maybe try an iterator
    //Person p = new Person(new double[]{ThreadLocalRandom.current().nextInt(1, 18 + 1), ThreadLocalRandom.current().nextInt(1, 18 + 1)});
    public static void addManyPedestrians(int number)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addManyPedestrians()");
            System.out.println("creating " + numberOfPeople + " pedestrian(s)");

        }
        /// Trying to find a way to create a location, loop through nodes to check if they are in the same location, then
        /// if any nodes hold the same location, create a new location and start checking again. If none are found, create
        /// the person and run again, until the peopleCatalogue is full
        while (peopleCatalogue.size() < number)
        {
            int x = ThreadLocalRandom.current().nextInt(2, mapWidth-1);
            int y = ThreadLocalRandom.current().nextInt(2, mapHeight-1);
            double[] loc = new double[]{x,y};
                    
            boolean validLocation = true;
            
            for (Node n : blockedNodeCatalogue)
            {
                if (n.getLocation()[0] == loc[0] && n.getLocation()[1] == loc[1])
                {
                    validLocation = false;
                }
            }
            
            if (validLocation)
            {
                //System.out.println("position " + loc[0] + "," + loc[1] + " is valid");
                Person p = new Person(loc);
                //p.setCharge(5e-5);
                
                int rand = ThreadLocalRandom.current().nextInt(0, 20 + 1);
                PointOfInterest destination = poiCatalogue.get(rand);
                //PointOfInterest destination = poiCatalogue.get(0);
                
                p.setPoiCatalogue(poiCatalogue);
                p.setFinalDestination(destination.getLocation());
                peopleCatalogue.add(p);
            }
        }
        
        
        /*
        int personNo = 1;
        // Print all the newly created people and their locations
        for(Person p: peopleCatalogue)
        {
            System.out.println("Person No." + personNo);
            
            System.out.println("Location: " + p.getLocation()[0] + ", " + p.getLocation()[1]);
            System.out.println("");
            personNo++;
        }
        */
        
    } // End of addManyPedestrians()



    
    
    //// CALLED 1st
    public static void addOnePOI(int locationX, int locationY)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addOnePOI()");
        }
        double[] testLocation = {locationX, locationY};

        String testID = "test";
        
        PointOfInterest testPOI = new PointOfInterest(testLocation, testID);
        
        poiCatalogue.add(testPOI);         
    } // End of addOnePOI()
    
    
    
    public static void addNewPOI(int locationX, int locationY, String id)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addNewPOI()");
        }
        double[] location = {locationX, locationY};

        String identity = id;
        
        PointOfInterest testPOI = new PointOfInterest(location, identity);
        
        poiCatalogue.add(testPOI);         
    } // End of addNewPOI()
    
    
    /*
    //// CALLED 10th
    public static void movePedestrians(double interval)
    {
        System.out.println("");
        System.out.println("### MOVE PEDESTRIANS ###");
        for(Person p: peopleCatalogue)
        {
            System.out.println("");
            System.out.println("");
            System.out.println("Moving person: " + p);
            System.out.println("FD: " + p.getFinalDestination()[0] + "," + p.getFinalDestination()[1]);
            p.move(p, interval);
            System.out.println("CALL IF 0: " + p.getInterest());
            if(p.getInterest() <= 0)
            {
                p.setNewFinalDestination();
            }
        }
    } // End of movePedestrians()
    */
    

    
    
    
    public static void calculatePath(Person p)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - calculatePath()");
        }
        
        //    if(p.getPathLL().isEmpty())
        //    {
        //        System.out.println("the path is empty for person " + p);
        //        System.out.println("calculating path for person " + p);
                
                
        
                AStar as = new AStar
                (
                    /*int mapWidth*/ mapWidth, 
                    /*int mapHeight*/ mapHeight, 
                //    /*int[] start*/ new int[]{ (int) Math.round(p.getLocation()[0]), (int) Math.round(p.getLocation()[1]) },
                    /*int[] start*/ new int[]{ (int) Math.round(p.getLocation()[0]), (int) Math.round(p.getLocation()[1]) }, 
                    /*int[] destination*/ new int[]{ (int) Math.round(p.getFinalDestination()[0]), (int) Math.round(p.getFinalDestination()[1]) }, 
                    /*blocked nodes*/ blockedNodes
                );
                
                if(testing)
                {
                    System.out.println("mapWidth: " + mapWidth);
                    System.out.println("mapHeight: " + mapHeight);
                    System.out.println("start (unrounded): " + p.getLocation()[0] + "," + p.getLocation()[1]);                
                    System.out.println("start (rounded): " + (int) Math.round(p.getLocation()[0]) + "," + (int) Math.round(p.getLocation()[1]));
                    System.out.println("final destination: " + (int) Math.round(p.getFinalDestination()[0]) + "," + (int) Math.round(p.getFinalDestination()[1]));
                }
                
                //System.out.println("blockedNodes:");
                //for(int[] i: blockedNodes)
                //{
                //    System.out.println(i[0] + "," + i[1]);
                //}
                
        
                as.process(); // Apply algorithm
                if(testing)
                {
                    as.displaySolution(); // Display solution path
                }
                
                p.setPathLL(as.returnSolutionLinkedList()); // set pedestrian's path to be A* solution path
                
                /*
                System.out.println("New path for person " + p + " is ") ;
                for(Node n : p.getPathLL())
                {
                    System.out.println(n.getLocation()[0] + ", " + n.getLocation()[1]);
                }
                */
                
                //as.clearAStar();
                
            //}
            /*
            else
            {    
                System.out.println("There's already a path for person " + p + "!!!!");
                System.out.println("The path for person " + p + " is ");
                for(Node n : p.getPathLL())
                {
                    System.out.println(n.getLocation()[0] + ", " + n.getLocation()[1]);
                }
                
                return;
            }
            */
        
    }
    
    
    
    
    
    
    public static void createRectInteriorBlockedNodes(int[] location, int width, int height)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - createRectInteriorBlockedNodes()");
            System.out.print("at ");
        }
        for(int i=location[0]; i<location[0] + width; i++)
        {
            for(int j=location[1]; j<location[1] + height; j++)
            {
                blockedNodeCatalogue.add(new Node(i, j));
                if(testing)
                {
                    System.out.print("[" + i + "," + j + "], ");
                }
            }
        }
    }
    
        
    
    
    public static void createMapEdge(int width, int height)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - createMapEdge()");
        }
        // left edge wall
        for(int i=0; i<height; i++)
        {
            blockedNodeCatalogue.add(new Node(0, i));
        }
        // right edge wall
        for(int i=0; i<height; i++)
        {
            blockedNodeCatalogue.add(new Node(width-1, i));
        }
        // bottom edge wall
        for(int i=1; i<width-1; i++)
        {
            blockedNodeCatalogue.add(new Node(i, 0));
        }
        // top edge wall
        for(int i=1; i<width-1; i++)
        {
            blockedNodeCatalogue.add(new Node(i, height-1));
        }
    }
    
    
    
    
    public static void compileWalls()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - compileWalls()");
        }
        //int[][] array = new int[ height*2 + ((width-2)*2) ][2];
        int[][] array = new int[blockedNodeCatalogue.size()][2];
        
        for(int i=0; i<blockedNodeCatalogue.size(); i++)
        {
            for(int j=0; j<2; j++)
            {
                //int x = blockedNodeCatalogue.get(i).getLocation()[0];
                //int y = blockedNodeCatalogue.get(i).getLocation()[1];

                array[i][j] = blockedNodeCatalogue.get(i).getLocation()[j];
            }
            //System.out.println("[" + array[i][0] + "," + array[i][1] + "]");
        }
        blockedNodes = array;
    }
    
    
   
    
    private static void loadMap(int mapNumber)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - loadMap()");
        }
        switch (mapNumber) {
            case 1:
                createRectInteriorBlockedNodes(new int[]{5,10},5,12);
                createRectInteriorBlockedNodes(new int[]{16,31},7,5);
                createRectInteriorBlockedNodes(new int[]{28,29}, 10, 6);
                createRectInteriorBlockedNodes(new int[]{28,15}, 3, 14);
                createRectInteriorBlockedNodes(new int[]{23,8}, 5, 5);
                createRectInteriorBlockedNodes(new int[]{13,6}, 7, 2);
                createRectInteriorBlockedNodes(new int[]{17,8}, 2, 19);
                createRectInteriorBlockedNodes(new int[]{10,31}, 3,8);
                createRectInteriorBlockedNodes(new int[]{1,25}, 13, 2);
                break;
            
            
            case 2:
                
                
                //createRectInteriorBlockedNodes(new int[]{8,1},2,7);  // pillar
                //createRectInteriorBlockedNodes(new int[]{11,7},2,1); // half of shop front
                //createRectInteriorBlockedNodes(new int[]{1,7},5,1); // full shop front
                
                // top side
                createRectInteriorBlockedNodes(new int[]{1,7},5,1);
                createRectInteriorBlockedNodes(new int[]{8,1},2,7);
                createRectInteriorBlockedNodes(new int[]{10,7},2,1);
                createRectInteriorBlockedNodes(new int[]{14,7},2,1);
                createRectInteriorBlockedNodes(new int[]{16,1},2,7);
                createRectInteriorBlockedNodes(new int[]{20,7},7,1);
                createRectInteriorBlockedNodes(new int[]{27,1},2,7);
                createRectInteriorBlockedNodes(new int[]{29,7},5,1);
                
                // bottom side
                createRectInteriorBlockedNodes(new int[]{1,32},2,1);
                createRectInteriorBlockedNodes(new int[]{7,32},2,1);
                createRectInteriorBlockedNodes(new int[]{9,32},2,7);
                createRectInteriorBlockedNodes(new int[]{11,32},1,1);
                createRectInteriorBlockedNodes(new int[]{13,32},2,1);
                createRectInteriorBlockedNodes(new int[]{15,32},2,7);
                createRectInteriorBlockedNodes(new int[]{17,32},2,1);
                createRectInteriorBlockedNodes(new int[]{22,32},3,1);
                createRectInteriorBlockedNodes(new int[]{25,32},2,7);
                createRectInteriorBlockedNodes(new int[]{27,32},7,1);
                createRectInteriorBlockedNodes(new int[]{36,32},3,1);
                
                // middle
                createRectInteriorBlockedNodes(new int[]{3,16},12,6);
                createRectInteriorBlockedNodes(new int[]{25,16},12,6);
                
                // kiosks
                createRectInteriorBlockedNodes(new int[]{18,18},4,2); // centre kiosk
                createRectInteriorBlockedNodes(new int[]{8,11},1,2);
                createRectInteriorBlockedNodes(new int[]{26,11},1,2);
                createRectInteriorBlockedNodes(new int[]{15,26},1,2);
                break;
            
                
            case 3:
                createRectInteriorBlockedNodes(new int[]{18,5}, 30, 3);
                break;
            
            default:
                createRectInteriorBlockedNodes(new int[]{3,3},2,4);
                break;
        }
    }
    
    
      public static void addPOISet(int setNumber)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - addPOISet()");
        }
        switch (setNumber)
        {
            case 1:
                addNewPOI(1, 2, "shop_1_a");
                addNewPOI(6, 1, "shop_1_b");

                addNewPOI(13, 2, "shop_2_a");

                addNewPOI(20, 2, "shop_3_a");
                addNewPOI(24, 2, "shop_3_b");
                addNewPOI(25, 5, "shop_3_c");

                addNewPOI(30, 3, "shop_4_a");
                addNewPOI(34, 2, "shop_4_b");

                addNewPOI(5, 35, "shop_5_a");

                addNewPOI(13, 37, "shop_6_a");

                addNewPOI(18, 38, "shop_7_a");
                addNewPOI(24, 36, "shop_7_b");

                addNewPOI(30, 37, "shop_8_a");
                addNewPOI(31, 38, "shop_8_b");

                addNewPOI(9, 12, "kiosk_1_a");

                addNewPOI(27, 11, "kiosk_2_a");

                addNewPOI(14, 27, "kiosk_3_a");

                addNewPOI(19, 20, "kiosk_main_a");
                addNewPOI(20, 20, "kiosk_main_b");
                addNewPOI(19, 17, "kiosk_main_c");
                addNewPOI(20, 17, "kiosk_main_d");
                break;
                
            default:
                break;
        }
    }


    public static void computeCoulombAcceleration_fromPeople()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - computeCoulombAcceleration_fromPeople()");
        }
        double person1X = 0;
        double person1Y = 0;
        
        double person2X = 0;
        double person2Y = 0;
        
        double person1Charge = 0;
        double person2Charge = 0;
        
        double distanceX = 0;
        double distanceY = 0;
        
        double forceX = 0;
        double forceY = 0;
        
        
        /// F = kqq/r2
        
        double alpha = 1/137;
        double hBar = 6.58e-16;
        double c = 3e8;
        double e = 1.6e-19;
              
        //double k = (alpha * hBar * c) / Math.pow(e,2);
        double k = 8.98e9;
        
        
        /// For each person, compute the influence of other objects
        for (Person p : peopleCatalogue)
        {
            p.setVelocity(new double[]{0,0}); // should not be here, only using to test impact of residual velocity
            //System.out.println("Calculating Coulomb Acceleration on Person: " + p);
            
            //System.out.println("person: " + p + " at " + p.getLocation()[0] + "," + p.getLocation()[1]); // print person's details
            
            person1X = p.getLocation()[0]; // grab (influencee) person x-position
            person1Y = p.getLocation()[1]; // grab (influencee) person y-position

            person1Charge = p.getCharge(); // grab (influencee) person charge
            
            if(testing)
            {
                System.out.println("Finding Repulsion from people");
            }
            
            /// Compute the influence from other people
            for (Person q : peopleCatalogue)
            {
                if (p == q)
                {
                    if(testing)
                    {
                        System.out.println("Cannot calculate force between same person");
                    }
                }
                else
                {    
                    person2X = q.getLocation()[0]; // grab (influencer) person x-position
                    person2Y = q.getLocation()[1]; // grab (influencer) person y-position

                    person2Charge = q.getCharge(); // grab (influencer) person charge

                    distanceX = person1X - person2X; // compute x-distance between influencer and influencee
                    distanceY = person1Y - person2Y; // compute y-distance between influencer and influencee
                    
                    
                    double totalDistance = Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2));

                    
                    if (distanceX == 0)
                    {
                        forceX = 0;
                    }
                    else
                    {
                        forceX = k * (person1Charge * person2Charge * distanceX) / Math.abs(Math.pow(totalDistance, 3));
                    }

                    if (distanceY == 0)
                    {
                        forceY = 0;
                    }
                    else
                    {
                        forceY = k * (person1Charge * person2Charge * distanceY) / Math.abs(Math.pow(totalDistance, 3));
                    }

                    double[] totalForce =
                    {
                        forceX, forceY
                    };
                    double[] totalAcceleration =
                    {
                        forceX / p.getMass(), forceY / p.getMass()
                    };

                    p.addCoulombAcceleration(totalAcceleration);
                    p.addAccelerationElement(totalAcceleration);
                    
                    //System.out.print("Coulomb Accelertation of " + "{" + totalAcceleration[0] + ", " + totalAcceleration[1] + "}" + " added");
                    //System.out.println(" from person" + q + " at " + q.getLocation()[0] + "," + q.getLocation()[1]);
                }
            }
        }
    } // End of computeCoulombAcceleration()
    
    
    
    
    
    public static void computeCoulombAcceleration_fromWalls()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - computeCoulombAcceleration_fromWalls()");
        }
        
        double personX = 0;
        double personY = 0;
        
        double nodeX = 0;
        double nodeY = 0;
        
        double personCharge = 0;
        double nodeCharge = 0;
        
        double distanceX = 0;
        double distanceY = 0;
        
        double forceX = 0;
        double forceY = 0;
        
        double alpha = 1/137;
        double hBar = 6.58e-16;
        double c = 3e8;
        double e = 1.6e-19;
              
        //double k = (alpha * hBar * c) / Math.pow(e,2);
        double k = 8.98e9;
        
        
        /// For each person, compute the influence of other objects
        for (Person p : peopleCatalogue)
        {           
            
            personX = p.getLocation()[0]; // grab (influencee) person x-position
            personY = p.getLocation()[1]; // grab (influencee) person y-position
           

            personCharge = p.getCharge(); // grab (influencee) person charge
            
            if(testing)
            {
                System.out.println("Finding repulsion from walls");
            }
            
            
            double smallestAbsDist = Double.POSITIVE_INFINITY;
            double secondSmallestAbsDist = Double.POSITIVE_INFINITY;
            double thirdSmallestAbsDist = Double.POSITIVE_INFINITY;
            double smallestX = Double.POSITIVE_INFINITY;
            double smallestY = Double.POSITIVE_INFINITY;
            double secondSmallestX = Double.POSITIVE_INFINITY;
            double secondSmallestY = Double.POSITIVE_INFINITY;
            double thirdSmallestX = Double.POSITIVE_INFINITY;
            double thirdSmallestY = Double.POSITIVE_INFINITY;
            
            for(Node n: blockedNodeCatalogue)
            {
                nodeX = n.getLocation()[0]; // grab (influencer) wall x-position
                nodeY = n.getLocation()[1]; // grab (influencer) wall y-position

                nodeCharge = n.getCharge(); // grab (influencer) wall charge

                distanceX = personX - nodeX; // compute x-distance between influencer and influencee
                distanceY = personY - nodeY; // compute y-distance between influencer and influencee
                
                double totalDistance = Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2)); // compute absolute distance between infulencer and influencee
                
                if(totalDistance < smallestAbsDist)
                {
                    smallestAbsDist = totalDistance;
                    smallestX = distanceX;
                    smallestY = distanceY;
                }
                else if(totalDistance < secondSmallestAbsDist)
                {
                    secondSmallestAbsDist = totalDistance;
                    secondSmallestX = distanceX;
                    secondSmallestY = distanceY;
                }
                else if(totalDistance < thirdSmallestAbsDist)
                {
                    thirdSmallestAbsDist = totalDistance;
                    thirdSmallestX = distanceX;
                    thirdSmallestY = distanceY;
                }
            }
            
            
            
            forceX = k * (personCharge * nodeCharge * smallestX) / Math.abs(Math.pow(smallestAbsDist, 3));
            forceX += k * (personCharge * nodeCharge * secondSmallestX) / Math.abs(Math.pow(secondSmallestAbsDist, 3));
            forceX += k * (personCharge * nodeCharge * thirdSmallestX) / Math.abs(Math.pow(thirdSmallestAbsDist, 3));

            forceY = k * (personCharge * nodeCharge * smallestY) / Math.abs(Math.pow(smallestAbsDist, 3));
            forceY += k * (personCharge * nodeCharge * secondSmallestY) / Math.abs(Math.pow(secondSmallestAbsDist, 3));
            forceY += k * (personCharge * nodeCharge * thirdSmallestY) / Math.abs(Math.pow(thirdSmallestAbsDist, 3));
            
            
            /*
            forceX = k * (personCharge * nodeCharge * smallestX) / Math.abs(Math.pow(smallestAbsDist, 3));
            forceX += k * (personCharge * nodeCharge * secondSmallestX) / Math.abs(Math.pow(secondSmallestAbsDist, 3));
            forceX += k * (personCharge * nodeCharge * thirdSmallestX) / Math.abs(Math.pow(thirdSmallestAbsDist, 3));

            forceY = k * (personCharge * nodeCharge * smallestY) / Math.abs(Math.pow(smallestAbsDist, 3));
            forceY += k * (personCharge * nodeCharge * secondSmallestY) / Math.abs(Math.pow(secondSmallestAbsDist, 3));
            forceY += k * (personCharge * nodeCharge * thirdSmallestY) / Math.abs(Math.pow(thirdSmallestAbsDist, 3));
            */
            
                    
            double[] totalForce =
            {
                forceX, forceY
            };
            double[] totalAcceleration =
            {
                forceX / p.getMass(), forceY / p.getMass()
            };

                
            p.addCoulombAcceleration(totalAcceleration);
            p.addAccelerationElement(totalAcceleration);
            
            
            
        }
    }
    
    
    /*
        public static void computeCoulombAcceleration_fromWalls()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - computeCoulombAcceleration_fromWalls()");
        }
        
        double personX = 0;
        double personY = 0;
        
        double nodeX = 0;
        double nodeY = 0;
        
        double personCharge = 0;
        double nodeCharge = 0;
        
        double distanceX = 0;
        double distanceY = 0;
        
        double forceX = 0;
        double forceY = 0;
        
        double alpha = 1/137;
        double hBar = 6.58e-16;
        double c = 3e8;
        double e = 1.6e-19;
              
        //double k = (alpha * hBar * c) / Math.pow(e,2);
        double k = 8.98e9;
        
        
        /// For each person, compute the influence of other objects
        for (Person p : peopleCatalogue)
        {           
            
            personX = p.getLocation()[0]; // grab (influencee) person x-position
            personY = p.getLocation()[1]; // grab (influencee) person y-position
           

            personCharge = p.getCharge(); // grab (influencee) person charge
            
            if(testing)
            {
                System.out.println("Finding repulsion from walls");
            }
            
            
            double smallestAbsDist = Double.POSITIVE_INFINITY;
            double smallestX = Double.POSITIVE_INFINITY;
            double smallestY = Double.POSITIVE_INFINITY;
            double secondSmallestX = Double.POSITIVE_INFINITY;
            double secondSmallestY = Double.POSITIVE_INFINITY;
            double thirdSmallestX = Double.POSITIVE_INFINITY;
            double thirdSmallestY = Double.POSITIVE_INFINITY;
            
            for(Node n: blockedNodeCatalogue)
            {
                nodeX = n.getLocation()[0]; // grab (influencer) wall x-position
                nodeY = n.getLocation()[1]; // grab (influencer) wall y-position

                nodeCharge = n.getCharge(); // grab (influencer) wall charge

                distanceX = personX - nodeX; // compute x-distance between influencer and influencee
                distanceY = personY - nodeY; // compute y-distance between influencer and influencee
                
                double totalDistance = Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2)); // compute absolute distance between infulencer and influencee
                
                if(totalDistance < smallestAbsDist)
                {
                    smallestAbsDist = totalDistance;
                    smallestX = distanceX;
                    smallestY = distanceY;
                }
            }
            
            
            //forceX = k * (personCharge * nodeCharge * smallestX) / Math.abs(Math.pow(smallestAbsDist, 8)); // worked
            forceX = k * (personCharge * nodeCharge * smallestX) / Math.abs(Math.pow(smallestAbsDist, 3));

            //forceY = k * (personCharge * nodeCharge * smallestY) / Math.abs(Math.pow(smallestAbsDist, 8)); // worked
            forceY = k * (personCharge * nodeCharge * smallestY) / Math.abs(Math.pow(smallestAbsDist, 3));

                    
            double[] totalForce =
            {
                forceX, forceY
            };
            double[] totalAcceleration =
            {
                forceX / p.getMass(), forceY / p.getMass()
            };

                
            p.addCoulombAcceleration(totalAcceleration);
            
            
            
        }
    }
    */
    
    
    /*
    //// CALLED 3rd
    public static void resetForces()
    {
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("### RESET FORCES ###");
        double[] zero = {0, 0};
        for(Person p: peopleCatalogue)
        {
            p.setIntentionalAcceleration(zero);
            p.setCoulombAcceleration(zero);
            p.setResultantAcceleration(zero);
            //System.out.println("resetForces() - resultant: " + p.getResultantAcceleration()[0]);
            //System.out.println("intentional: " + p.getIntentionalAcceleration()[0]);
            //System.out.println("coulomb: " + p.getCoulombAcceleration()[0]);
        }
    } // End of resetForces()
    */
    
    public static String fetchPositionData()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - fetchPositionData()");
        }
        String content = "";
            
            for(Person p: peopleCatalogue)
            {
                content += Double.toString(p.getLocation()[0]) + ",";
                content += Double.toString(p.getLocation()[1]) + ",";
            }
            return content;
    } // End of fetchPositionData()
    
    
    public static String fetchPoiData()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - fetchPoiData()");
        }
        String content = "";
            
            for(PointOfInterest p: poiCatalogue)
            {
                content += Double.toString(p.getLocation()[0]) + ",";
                content += Double.toString(p.getLocation()[1]) + ",";
            }
            return content;
    } // End of fetchPoiData()
    
    
    public static String fetchBlockData()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - fetchBlockData()");
        }
        String content = "";
            
            for(int[] b: blockedNodes)
            {
                content += Double.toString(b[0]) + ",";
                content += Double.toString(b[1]) + ",";
            }
            return content;
    } // End of fetchBlockData()
    
    
    
       
    public static void evaluateCongestion(int method)
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("PS_Main - evaluateCongestion()");
        }
        switch (method)
        {
            case 1:
                // density of people - divide map into squares and evaluate the number of people in each square
                //System.out.println("Evaluating average number of people per 1/16th of the map");
                      
                
                ArrayList<Person> area1 = new ArrayList<Person>();
                ArrayList<Person> area2 = new ArrayList<Person>();
                ArrayList<Person> area3 = new ArrayList<Person>();
                ArrayList<Person> area4 = new ArrayList<Person>();
                ArrayList<Person> area5 = new ArrayList<Person>();
                ArrayList<Person> area6 = new ArrayList<Person>();
                ArrayList<Person> area7 = new ArrayList<Person>();
                ArrayList<Person> area8 = new ArrayList<Person>();
                ArrayList<Person> area9 = new ArrayList<Person>();
                ArrayList<Person> area10 = new ArrayList<Person>();
                ArrayList<Person> area11 = new ArrayList<Person>();
                ArrayList<Person> area12 = new ArrayList<Person>();
                ArrayList<Person> area13 = new ArrayList<Person>();
                ArrayList<Person> area14 = new ArrayList<Person>();
                ArrayList<Person> area15 = new ArrayList<Person>();
                ArrayList<Person> area16 = new ArrayList<Person>();
                
                
                
                for(Person p: peopleCatalogue)
                {
                    if(p.getLocation()[0] < 10)
                    {
                        if(p.getLocation()[1] < 10)
                        {
                            area1.add(p);
                        }
                        else if(p.getLocation()[1] < 20)
                        {
                            area2.add(p);
                        }
                        else if(p.getLocation()[1] < 30)
                        {
                            area3.add(p);
                        }
                        else if(p.getLocation()[1] < 40)
                        {
                            area4.add(p);
                        }
                    }
                    
                    else if(p.getLocation()[0] < 20)
                    {
                        if(p.getLocation()[1] < 10)
                        {
                            area5.add(p);
                        }
                        else if(p.getLocation()[1] < 20)
                        {
                            area6.add(p);
                        }
                        else if(p.getLocation()[1] < 30)
                        {
                            area7.add(p);
                        }
                        else if(p.getLocation()[1] < 40)
                        {
                            area8.add(p);
                        }
                    }
                    
                    else if(p.getLocation()[0] < 30)
                    {
                        if(p.getLocation()[1] < 10)
                        {
                            area9.add(p);
                        }
                        else if(p.getLocation()[1] < 20)
                        {
                            area10.add(p);
                        }
                        else if(p.getLocation()[1] < 30)
                        {
                            area11.add(p);
                        }
                        else if(p.getLocation()[1] < 40)
                        {
                            area12.add(p);
                        }
                    }
                    
                    else if(p.getLocation()[0] < 40)
                    {
                        if(p.getLocation()[1] < 10)
                        {
                            area13.add(p);
                        }
                        else if(p.getLocation()[1] < 20)
                        {
                            area14.add(p);
                        }
                        else if(p.getLocation()[1] < 30)
                        {
                            area15.add(p);
                        }
                        else if(p.getLocation()[1] < 40)
                        {
                            area16.add(p);
                        }
                    }
                }
                
                
                
                
                if(testing)
                {
                    System.out.println("Number of people in area 1: " + area1.size());
                    System.out.println("Number of people in area 2: " + area2.size());
                    System.out.println("Number of people in area 3: " + area3.size());
                    System.out.println("Number of people in area 4: " + area4.size());
                    System.out.println("Number of people in area 5: " + area5.size());
                    System.out.println("Number of people in area 6: " + area6.size());
                    System.out.println("Number of people in area 7: " + area7.size());
                    System.out.println("Number of people in area 8: " + area8.size());
                    System.out.println("Number of people in area 9: " + area9.size());
                    System.out.println("Number of people in area 10: " + area10.size());
                    System.out.println("Number of people in area 11: " + area11.size());
                    System.out.println("Number of people in area 12: " + area12.size());
                    System.out.println("Number of people in area 13: " + area13.size());
                    System.out.println("Number of people in area 14: " + area14.size());
                    System.out.println("Number of people in area 15: " + area15.size());
                    System.out.println("Number of people in area 16: " + area16.size());
                }
                
                
                
                ArrayList<ArrayList<Person>> areas = new ArrayList<ArrayList<Person>>();
                areas.add(area1);
                areas.add(area2);
                areas.add(area3);
                areas.add(area4);
                areas.add(area5);
                areas.add(area6);
                areas.add(area7);
                areas.add(area8);
                areas.add(area9);
                areas.add(area10);
                areas.add(area11);
                areas.add(area12);
                areas.add(area13);
                areas.add(area14);
                areas.add(area15);
                areas.add(area16);
                
                
                
                int total = 0;
                int gridSectionCount = 0;
                int index = 0;
                
                while(total < peopleCatalogue.size()*0.5)
                {
                    double largest = 0;
                    for(ArrayList a: areas)
                    {
                        if(a.size() > largest)
                        {
                            largest = a.size();
                            index = areas.indexOf(a);
                            
                        }
                    }
                                        
                    areas.remove(index);
                    total += largest;
                    gridSectionCount++;
                    
                    //System.out.println("largest: " + largest);
                    //System.out.println("Index: " + index);
                    //System.out.println("Square count:" + gridSectionCount);
                }
                
                double metric = peopleCatalogue.size() / gridSectionCount;
                
                //System.out.println("Metric: " + metric);
                
                gridData += Double.toString(metric) + ",";
                
                break;
                
                
            case 2:
                // average distance to next nearest pedestrian
                
                double totalDistance = 0;
                
                for(Person p: peopleCatalogue)
                {
                    double nearest = Double.POSITIVE_INFINITY;
                    
                    for(Person q: peopleCatalogue)
                    {
                        if(p == q)
                        {
                           // do nothing 
                        }
                        else
                        {
                            double distX = q.getLocation()[0] - p.getLocation()[0];
                            double distY = q.getLocation()[1] - p.getLocation()[1];
                        
                            double dist = Math.sqrt(Math.pow(distX,2) + Math.pow(distY,2));
                            //System.out.println("Distance: " + dist);
                        
                            if(dist < nearest)
                            {
                                nearest = dist;
                            }
                        }
                    }
                    
                    //System.out.println("Nearest: " + nearest);
                    totalDistance += nearest;
                }
                
                double avgDistanceToNearestPedestrian = totalDistance / peopleCatalogue.size();
                //System.out.println("Average: " + avgDistanceToNearestPedestrian);
                averageDistanceData += Double.toString(avgDistanceToNearestPedestrian) + ",";
                break;
                         

            case 3:
                // average speed of individuals
                double totalSpeed = 0;
                double avgSpeed = 0;
                
                for(Person p: peopleCatalogue)
                {
                    double speed = Math.sqrt( Math.pow(p.getVelocity()[0], 2) + Math.pow(p.getVelocity()[1], 2) );
                    totalSpeed += speed;
                }
                
                avgSpeed = totalSpeed / peopleCatalogue.size();
                
                averageVelocityData += Double.toString(avgSpeed) + ",";
                
                break;
                
                
                
            case 4:
                // average force experienced by each person
                   
                double total_acc = 0;
                for(Person p: peopleCatalogue)
                {
                    total_acc += p.getTotalAcceleration();
                }
                
                double avgAcc = total_acc / peopleCatalogue.size();
                                
                averageAccelerationData += Double.toString(avgAcc) + ",";
                
                for(Person p: peopleCatalogue)
                {
                    p.setTotalAcceleration(0);
                }
                
                break;
                
            default:
                break;
        }
    } // End of evaluateCongestion()

    
    
    public static double euclideanDistance(double[] locationA, double[] locationB)
    {
        //System.out.println("\n----------");
        //System.out.println("Person - euclideanDistance()");
        double dx = locationB[0] - locationA[0];
        double dy = locationB[1] - locationA[1];
        
        double absDistance = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
                
        return absDistance;
    }
    
        
    
    
} // End of class
