
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentX; // Current X position of the robot
    int currentY; // Current Y position of the robot

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;

    // Declare policy variable at the class level
    int[][] policy;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);

        // Initialize policy
        policy = new int[mundo.width][mundo.height];
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            // Getting p_m and p_s from command line, respectively
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }
    
    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        
        // Create a copy of current probabilities to store updated beliefs
        double[][] updatedBeliefs = new double[mundo.width][mundo.height];
        
        // Update beliefs using Bayes filter algorithm
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                double sum = 0.0;
                // Update beliefs based on transition model
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        // Check if new position is within bounds of the world
                        if (newX >= 0 && newX < mundo.width && newY >= 0 && newY < mundo.height) {
                            // Compute probability of moving to new position based on action
                            double transitionProb = computeTransitionProbability(action, dx, dy);
                            // Update belief for new position based on transition model
                            sum += transitionProb * probs[newX][newY];
                        }
                    }
                }
                // Update beliefs based on sensor model
                double observationProb = computeObservationProbability(sonars, x, y);
                updatedBeliefs[x][y] = observationProb * sum;
            }
        }
        
        // Normalize updated beliefs to form a legal probability distribution
        double sumBeliefs = 0.0;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                sumBeliefs += updatedBeliefs[x][y];
            }
        }
        // Normalize beliefs
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                updatedBeliefs[x][y] /= sumBeliefs;
            }
        }
        
        // Update probabilities with the normalized beliefs
        probs = updatedBeliefs;

        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }
    

    // Helper method to compute the transition probability based on the action and movement direction
    double computeTransitionProbability(int action, int dx, int dy) {
        // Initialize transition probability
        double transitionProb = 0.0;
        
        // Check if the action corresponds to intended movement direction (dx, dy)
        switch (action) {
            case NORTH:
                transitionProb = (dx == 0 && dy == 1) ? moveProb : (1 - moveProb) / 3;
                break;
            case SOUTH:
                transitionProb = (dx == 0 && dy == -1) ? moveProb : (1 - moveProb) / 3;
                break;
            case EAST:
                transitionProb = (dx == -1 && dy == 0) ? moveProb : (1 - moveProb) / 3;
                break;
            case WEST:
                transitionProb = (dx == 1 && dy == 0) ? moveProb : (1 - moveProb) / 3;
                break;
            case STAY:
                transitionProb = moveProb;
                break;
        }
        
        return transitionProb;
    }

    // Helper method to compute the observation probability based on the sonar readings
    double computeObservationProbability(String sonars, int x, int y) {
        // Initialize observation probability
        double observationProb = 1.0;
        
        // Parse sonar readings
        boolean northWall = sonars.charAt(0) == '1';
        boolean southWall = sonars.charAt(1) == '1';
        boolean eastWall = sonars.charAt(2) == '1';
        boolean westWall = sonars.charAt(3) == '1';
        
        // Calculate observation probability based on sensor accuracy and wall presence
        // Check boundaries before accessing neighboring cells
        if (y > 0) {
            if (northWall && mundo.grid[x][y - 1] != 1) {
                observationProb *= (1 - sensorAccuracy);
            } else if (!northWall && mundo.grid[x][y - 1] == 1) {
                observationProb *= (1 - sensorAccuracy);
            }
        } else {
            // Handle boundary condition: if robot is at the top edge of the maze
            if (northWall) {
                observationProb *= (1 - sensorAccuracy);
            }
        }

        if (y < mundo.height - 1) {
            if (southWall && mundo.grid[x][y + 1] != 1) {
                observationProb *= (1 - sensorAccuracy);
            } else if (!southWall && mundo.grid[x][y + 1] == 1) {
                observationProb *= (1 - sensorAccuracy);
            }
        } else {
            if (southWall) {
                observationProb *= (1 - sensorAccuracy);
            }
        }
        
        if (x < mundo.width - 1){
            if (eastWall && mundo.grid[x + 1][y] != 1) {
                observationProb *= (1 - sensorAccuracy);
            } else if (!eastWall && mundo.grid[x + 1][y] == 1) {
                observationProb *= (1 - sensorAccuracy);
            }
        } else {
            if (eastWall) {
                observationProb *= (1 - sensorAccuracy);
            }
        }

        if (x > 0){
            if (westWall && mundo.grid[x - 1][y] != 1) {
                observationProb *= (1 - sensorAccuracy);
            } else if (!westWall && mundo.grid[x - 1][y] == 1) {
                observationProb *= (1 - sensorAccuracy);
            }
        } else {
            if (westWall) {
                observationProb *= (1 - sensorAccuracy);
            }
        }
        
        return observationProb;
    }


    // Perform value iteration to compute the optimal value function and derive the policy
    void valueIteration() {
        if (Vs == null) {
            // Initialize value function only if it's null
            Vs = new double[mundo.width][mundo.height];
        }

        // Initialize probabilities of being in each state
        initializeProbabilities();

        double gamma = 0.99; // Discount factor
        double epsilon = 0.01; // Convergence threshold

        double[][] updatedVs = new double[mundo.width][mundo.height];

        // Perform value iteration until convergence
        while (true) {
            double delta = 0.0; // Initialize change in value function

            // Iterate over all states
            for (int x = 0; x < mundo.width; x++) {
                for (int y = 0; y < mundo.height; y++) {
                    // Skip obstacle states
                    if (mundo.grid[x][y] == 1) {
                        continue;
                    }

                    // Compute the value of the current state using Bellman equation
                    double maxActionValue = Double.NEGATIVE_INFINITY;
                    int[] actions = {NORTH, SOUTH, EAST, WEST, STAY};

                    // Find the action with the maximum value
                    for (int action : actions) {
                        double value = 0.0;

                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int newX = x + dx;
                                int newY = y + dy;

                                // Skip invalid states
                                if (newX < 0 || newX >= mundo.width || newY < 0 || newY >= mundo.height || mundo.grid[newX][newY] == 1) {
                                    continue;
                                }

                                // Calculate transition probability
                                double transitionProb = computeTransitionProbability(action, dx, dy);
                                System.out.println("transition prob: " + transitionProb);
                                // Update value using Bellman equation
                                value += transitionProb * (probs[newX][newY] * (mundo.grid[newX][newY] == 3 ? 100.0 : 0.0) + (1.0 - probs[newX][newY]) * (Vs[newX][newY]));
                                // System.out.println("Value: " + value);
                            }
                        }

                        if (value > maxActionValue) {
                            maxActionValue = value;
                            policy[x][y] = action; // Update policy
                        }
                    }

                    // Update change in value function
                    delta = Math.max(delta, Math.abs(maxActionValue - Vs[x][y]));

                    // Update value function
                    updatedVs[x][y] = maxActionValue;
                }
            }

            // Update value function
            for (int x = 0; x < mundo.width; x++) {
                System.arraycopy(updatedVs[x], 0, Vs[x], 0, mundo.height);
            }

            // Check for convergence
            if (delta < epsilon) {
                break;
            }
        }
    }



    // Update the automaticAction() method to use the derived policy
    int automaticAction() {

        // Call valueIteration() if not already done
        valueIteration();
        
        // Get action using the derived policy
        System.out.println("Policy: " + policy[currentX][currentY]);
        return policy[currentX][currentY];
    }


    // Determine the current position based on probabilities
    void determineCurrentPosition() {
        double maxProb = 0.0;
        int maxX = 0, maxY = 0;

        // Iterate through probabilities to find the position with the highest probability
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProb) {
                    maxProb = probs[x][y];
                    maxX = x;
                    maxY = y;
                }
            }
        }

        // Update current position
        currentX = maxX;
        currentY = maxY;
    }
    
    void doStuff() {
        int action;
        
        valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map

        // Initialize current position if known
        if (knownPosition) {
            currentX = startX;
            currentY = startY;
        }
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); 
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); 
                
                // Determine the current position based on probabilities
                determineCurrentPosition();

                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                    
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}