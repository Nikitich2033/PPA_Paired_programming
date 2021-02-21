import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{   
    //testing GitHub on bluej
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    //
    private static final double LEOPARD_CREATION_PROBABILITY = 0.02;
    private static final double CHEETAH_CREATION_PROBABILITY = 0.04;

    private static final double MEERKAT_CREATION_PROBABILITY = 0.09;
    private static final double IMPALA_CREATION_PROBABILITY = 0.08;
    private static final double RHINO_CREATION_PROBABILITY = 0.06;

    private static final double GRASS_CREATION_PROBABILITY = 0.20;

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

      //  view.setColor(Fox.class, Color.BLUE);
        view.setColor(Cheetah.class,Color.red);
        view.setColor(Leopard.class,Color.YELLOW);
        view.setColor(Meerkat.class,Color.BLUE);
        view.setColor(Impala.class,Color.MAGENTA);
        view.setColor(Rhino.class,Color.ORANGE);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= LEOPARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leopard leopard = new Leopard(true, field, location);
                    animals.add(leopard);
                }
                else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cheetah cheetah = new Cheetah(true, field, location);
                    animals.add(cheetah);
                }
                else if(rand.nextDouble() <= MEERKAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Meerkat meerkat = new Meerkat(true, field, location);
                    animals.add(meerkat);
                }
                else if(rand.nextDouble() <= IMPALA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Impala impala = new Impala(true, field, location);
                    animals.add(impala);
                }
                else if(rand.nextDouble() <= RHINO_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rhino rhino = new Rhino(true, field, location);
                    animals.add(rhino);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
