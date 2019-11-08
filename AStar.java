/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps_main;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStar
{
    public static boolean testing = false;

    // movement costs
    public static final int DIAGONAL_COST = 20;
    public static final int V_OR_H_COST = 10;

    // map nodes
    private Node[][] map;

    // open set
    private PriorityQueue<Node> openNodes;

    // closed set
    private boolean[][] closedNodes;

    // starting node (not yet a node, just location)
    private int[] startCoordinates;

    // destination node (not yet a node, just location)
    private int[] destinationCoordinates;
    
    

    public AStar(
            int mapWidth,
            int mapHeight,
            int[] start,
            int[] destination,
            int[][] blocks
    )
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("AStar - AStar()");
        }
        map = new Node[mapWidth][mapHeight];
        closedNodes = new boolean[mapWidth][mapHeight];
        openNodes = new PriorityQueue<>((Node n1, Node n2) ->
        {
            return n1.getFinalCost() < n2.getFinalCost() ? -1 : n1.getFinalCost() > n2.getFinalCost() ? 1 : 0;
        });

        // set start and destination coordinates based on passed constructor parameters
        startCoordinates= start;
        destinationCoordinates = destination ;       

        // init nodes and heuristic
        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                map[j][i] = new Node(j, i);
                map[j][i].setHeuristicCost(Math.abs(j - destinationCoordinates[0]) + Math.abs(i - destinationCoordinates[1]));
                map[j][i].setSolution(false);
            }
        }

        map[startCoordinates[0]][startCoordinates[1]].setFinalCost(0);


        
        // put the blocks on the map
        for (int i = 0; i < blocks.length; i++)
        {
            addBlockOnNode(blocks[i][0], blocks[i][1]);
        }
    }

    
    // block a node - by making it null
    public void addBlockOnNode(int x, int y)
    {
        
        map[x][y] = null;
    }

    
    // set starting node/coordinates
    public void startCoordinates(int x, int y)
    {
        startCoordinates[0] = x;
        startCoordinates[1] = y;
    }

    
    // set destination node/coordinates
    public void destinationCoordinates(int x, int y)
    {
        destinationCoordinates[0] = x;
        destinationCoordinates[1] = y;
    }

    
    
    public void updateCostIfNeeded(Node current, Node t, int cost)
    {
        // if prospective node blocked or part of closed set (explored), exit method
        if (t == null || closedNodes[t.getLocation()[0]][t.getLocation()[1]])
        {
            return;
        }

        // calculate prospective cost and check if node is in open set
        int tFinalCost = t.getHeuristicCost() + cost;
        boolean isOpen = openNodes.contains(t);

        // if node not in open set or prospective cost is lower than existing cost,
        if (!isOpen || tFinalCost < t.getFinalCost())
        {
            t.setFinalCost(tFinalCost); // update final cost
            
            t.setParent(current); // set current node as parent of prospective node

            // if prospective node is not in the open set
            if (!isOpen)
            {
                openNodes.add(t); // add node to open set
            }
        }
        
       
    }

    //// CALLED 8th
    public void process()
    {     
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("AStar - process()");
        }
        
        // add the start location to the open set
        if(testing)
        {
            System.out.println("Start Coordinates: {" + startCoordinates[0] + ", " + startCoordinates[1] + "}");
        }
        
        try
        {
            openNodes.add(map[startCoordinates[0]][startCoordinates[1]]);
        }
        catch(Exception e) 
        {
            System.out.println("Cannot add " + "{" + startCoordinates[0] +"," + startCoordinates[1] + "}" + " to open nodes");
            System.out.println(map[startCoordinates[0]][startCoordinates[1]]);
        }
        
        Node current;
        
        while (true)
        {
            
            // grab first node in open set queue and set as the current node
            current = openNodes.poll();

            // if current node is blocked, break loop
            if (current == null)
            {
                break;
            }

            // ?????? update current node's status in closed set ??????
            closedNodes[current.getLocation()[0]][current.getLocation()[1]] = true;
   
            // if current node is the destination node, exit method
            if (current.equals(map[destinationCoordinates[0]][destinationCoordinates[1]]))
            {
                return;
            }

            
            // ?????? make an object for prospective nodes ??????
            Node t;

            /// NODES ABOVE CURRENT
            // if current node is not on the top edge of the map
            if (current.getLocation()[0] - 1 >= 0)
            {
                t = map[current.getLocation()[0] - 1][current.getLocation()[1]]; // set prospective node to the one above current
                updateCostIfNeeded(current, t, current.getFinalCost() + V_OR_H_COST); // calculate and set cost if necessary

                // if current node is not on the far left edge of the map
                if (current.getLocation()[1] - 1 >= 0) 
                {
                    t = map[current.getLocation()[0] - 1][current.getLocation()[1] - 1]; // set prospective node to the one above and left of current
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST); // calculate and set cost if necessary
                }

                // if current node is not the far right edge of the map
                if (current.getLocation()[1] + 1 < map[0].length)
                {
                    t = map[current.getLocation()[0] - 1][current.getLocation()[1] + 1]; // set prospective node to the one above and right of current
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST); // calculate and set cost if necessary
                }
            }

            
            /// NODES LEFT AND RIGHT OF CURRENT
            // if current node is not of the far left edge of the map
            if (current.getLocation()[1] - 1 >= 0)
            {
                t = map[current.getLocation()[0]][current.getLocation()[1] - 1]; // set prospective node to the one to the left of current
                updateCostIfNeeded(current, t, current.getFinalCost() + V_OR_H_COST); // calculate and set cost if necessary
            }
                        
            // if current node is not on the far right edge of the map
            if (current.getLocation()[1] + 1 < map[0].length)
            {
                t = map[current.getLocation()[0]][current.getLocation()[1] + 1]; // set prospective node to the one to the right of current
                updateCostIfNeeded(current, t, current.getFinalCost() + V_OR_H_COST); // calculate and set cost if necessary
            }
            
            
            /// NODES BELOW CURRENT
            // if current node is not on the bottom edge of the map
            if (current.getLocation()[0] + 1 < map.length)
            {
                t = map[current.getLocation()[0] + 1][current.getLocation()[1]]; // set prospective node to the one below curent
                updateCostIfNeeded(current, t, current.getFinalCost() + V_OR_H_COST); // calculate and set cost if necessary

                // if current node is not on the far left edge of the map
                if (current.getLocation()[1] - 1 >= 0)
                {
                    t = map[current.getLocation()[0] + 1][current.getLocation()[1] - 1]; // set prospective node to the one below and left of current
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST); // calculate and set cost if necessary
                }

                // if current node is not on far right edge of the map
                if (current.getLocation()[1] + 1 < map[0].length)
                {
                    t = map[current.getLocation()[0] + 1][current.getLocation()[1] + 1]; // set prospective node to the one below and right of current
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST); // calculate and set cost if necessary
                }
            }
        }
    }

    
    
    
    
    public void display()
    {
        System.out.println("map: ");

        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                if (j == startCoordinates[0] && i == startCoordinates[1])
                {
                    System.out.print("SO  "); // source node
                }
                else if (j == destinationCoordinates[0] && i == destinationCoordinates[1])
                {
                    System.out.print("DE  "); // destination node
                }
                else if (map[j][i] != null)
                {
                    System.out.printf("%-3d ", 0);
                }
                else
                {
                    System.out.print("BL  "); // block node
                }
            }

            System.out.println("");

        }

        System.out.println("");

    }
    
    
    
    public void displayNodes()
    {
        System.out.println("Node map: ");

        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                int x = map[j][i].getLocation()[0];
                int y = map[j][i].getLocation()[1];
                //System.out.println("{" + map[i][j].getLocation()[0] + "," + map[i][j].getLocation()[1] + "}");
            }

            System.out.println("");

        }

        System.out.println("");
    }

    public void displayScores()
    {
        System.out.println("\nScores for cells : ");

        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                if (map[j][i] != null)
                {
                    System.out.printf("%-3d ", map[j][i].getFinalCost());
                }
                else
                {
                    System.out.print("BL  ");
                }
            }

            System.out.println("");
        }

        System.out.println("");
    }

    
    //// CALLED 9th
    public void displaySolution()
    {
        if (closedNodes[destinationCoordinates[0]][destinationCoordinates[1]])
        {
            // track back the path
            System.out.println("");
            System.out.println("Path: ");
            Node current = map[destinationCoordinates[0]][destinationCoordinates[1]]; // set current node as destination node
            
            
            System.out.print("destination [" + current.getLocation()[0] + ", " + current.getLocation()[1] + "]");
            map[current.getLocation()[0]][current.getLocation()[1]].setSolution(true); // set the current, destination node as part of the solution

            // while the current node has a valid parent
            while (current.getParent() != null)
            {
                System.out.print(" <-- " + current.getParent()); // print parent
                map[current.getParent().getLocation()[0]][current.getParent().getLocation()[1]].setSolution(true); // grap parent location and set as part of the solution
                current = current.getParent(); // set parent as current
            }

            System.out.println("");
            System.out.println("");
            System.out.println("Map:");
            for (int i = 0; i < map.length; i++)
            {
                for (int j = 0; j < map[i].length; j++)
                {
                    // if node shares coordinates with start
                    if (j == startCoordinates[0] && i == startCoordinates[1])
                    {
                        System.out.print("SO  "); // print source node icon
                    }
                    // if node shares coordinates with destination
                    else if (j == destinationCoordinates[0] && i == destinationCoordinates[1])
                    {
                        System.out.print("DE  "); // print destination node icon
                    }
                    // if node is not null
                    else if (map[j][i] != null)
                    {
                        System.out.printf("%-3s ", map[j][i].getSolution() ? "X" : " ");
                    }
                    // if node is null
                    else
                    {
                        System.out.print("BL  "); // print block node icon
                    }
                }
                
                System.out.println("");
            }
            
            System.out.println("");
        }
        else
        {
            System.out.println("No possible solution");
        }
    }
    
    
    
    public Deque<Node> returnSolutionDeque()
    {
        System.out.println("Retracing solution path for Export");
        LinkedList<Node> path = new LinkedList<>();
        Stack stack = new Stack();
        Deque<Node> deque = new ArrayDeque<>();
        if (closedNodes[destinationCoordinates[0]][destinationCoordinates[1]])
        {
            Node current = map[destinationCoordinates[0]][destinationCoordinates[1]];
           
            map[current.getLocation()[0]][current.getLocation()[1]].setSolution(true);

            while (current.getParent() != null)
            {
                path.addFirst(current);
                stack.add(current);
                deque.add(current);
                map[current.getParent().getLocation()[0]][current.getParent().getLocation()[1]].setSolution(true);
                current = current.getParent();
            }
            
            path.addFirst(new Node(startCoordinates[0], startCoordinates[1]));
            stack.add(new Node(startCoordinates[0], startCoordinates[1]));
            deque.add(new Node(startCoordinates[0], startCoordinates[1]));
        }
        else
        {
            System.out.println("No possible solution");
        }
        return deque;
    }
    
    
    public LinkedList<Node> returnSolutionLinkedList()
    {
        if(testing)
        {
            System.out.println("\n----------");
            System.out.println("AStar - returnSolutionLinkedList()");
            System.out.println("Retracing solution path for Export");
        }
        LinkedList<Node> path = new LinkedList<>();
        Stack stack = new Stack();
        Deque<Node> deque = new ArrayDeque<>();
        if (closedNodes[destinationCoordinates[0]][destinationCoordinates[1]])
        {
            Node current = map[destinationCoordinates[0]][destinationCoordinates[1]];
           
            map[current.getLocation()[0]][current.getLocation()[1]].setSolution(true);

            while (current.getParent() != null)
            {
                path.addFirst(current);
                stack.add(current);
                deque.add(current);
                
                map[current.getParent().getLocation()[0]][current.getParent().getLocation()[1]].setSolution(true);
                current = current.getParent();
            }
            
            path.addFirst(new Node(startCoordinates[0], startCoordinates[1]));
            stack.add(new Node(startCoordinates[0], startCoordinates[1]));
            deque.add(new Node(startCoordinates[0], startCoordinates[1]));
        }
        else
        {
            System.out.println("No possible solution");
        }
        return path;
    }
    
    
    
    public void clearAStar()
    {
        setOpenNodes(null);
        setClosedNodes(null);
        setStartCoordinates(null);
        setDestinationCoordinates(null);
    }
    
    
    /*
    public static void main(String[] args)
    {
        aStar as = new aStar(5, 6, new int[]{0,0}, new int[]{3, 2}, 
        new int[][]
        {
            {0,4}, {2,2}, {3,1}, {3,3}, {2,1}, {2,3}
        });
        
        as.display();
        as.process(); // Apply algorithm
        //as.displayNodes();
        as.displayScores(); // Display scores on map
        as.displaySolution(); // Display solution path
        
        
        
    }
    */

    
    
    
    /// GETTERS AND SETTERS/||||||||||||
    public Node[][] getMap()
    {
        return map;
    }

    public void setMap(Node[][] map)
    {
        this.map = map;
    }

    public PriorityQueue<Node> getOpenNodes()
    {
        return openNodes;
    }

    public void setOpenNodes(PriorityQueue<Node> openNodes)
    {
        this.openNodes = openNodes;
    }

    public boolean[][] getClosedNodes()
    {
        return closedNodes;
    }

    public void setClosedNodes(boolean[][] closedNodes)
    {
        this.closedNodes = closedNodes;
    }

    public int[] getStartCoordinates()
    {
        return startCoordinates;
    }

    public void setStartCoordinates(int[] startCoordinates)
    {
        this.startCoordinates = startCoordinates;
    }

    public int[] getDestinationCoordinates()
    {
        return destinationCoordinates;
    }

    public void setDestinationCoordinates(int[] destinationCoordinates)
    {
        this.destinationCoordinates = destinationCoordinates;
    }
    
    
    
    

}
