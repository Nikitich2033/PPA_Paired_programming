import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals from the African Savannah: Leopards, Cheetahs,
 * Boars, Impalas and Rhinos and also generic planst
 * 
 * @author David J. Barnes, Michael Kölling and Nikita Lyakhovoy
 */
public class Simulator
{   

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 180;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 140;
    //
    private static final double LEOPARD_CREATION_PROBABILITY = 0.02;
    private static final double CHEETAH_CREATION_PROBABILITY = 0.02;
    private static final double BOAR_CREATION_PROBABILITY = 0.08;
    private static final double IMPALA_CREATION_PROBABILITY = 0.07;
    private static final double RHINO_CREATION_PROBABILITY = 0.09;


    private static final double GRASS_CREATION_PROBABILITY = 0.60;

    // List of animals in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current time of day of the simulation.
    private Time timeOfDay;
    // The current weather of the simulation.
    private Weather weather;

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
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        timeOfDay = new Time();
        weather = new Weather();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

        view.setColor(Cheetah.class,Color.RED);
        view.setColor(Leopard.class,Color.YELLOW);
        view.setColor(Boar.class,Color.BLUE);
        view.setColor(Impala.class,Color.MAGENTA);
        view.setColor(Rhino.class,Color.ORANGE);
        view.setColor(Plant.class,Color.GREEN);
        
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
            delay(15);   // uncomment this to run more slowly
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

        //Set a new time of day every 3 steps
        if (step % 3 == 0 ){timeOfDay.incrementTimeOfDay();}
        //New weather every 12 steps/ a day
        if (step % 12 == 0){weather.setRandomWeather();}

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, timeOfDay.getTimeOfDay(), weather);
            if(! organism.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born animals and plants to the main lists.
        organisms.addAll(newOrganisms);

        //show the current state of the simulation in the view
        view.showStatus(step,timeOfDay.getTimeOfDay(), weather.getCurrentWeather() ,field);
    }

        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        timeOfDay.reset();
        weather.reset();
        organisms.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step,timeOfDay.getTimeOfDay(),weather.getCurrentWeather(),field);
    }
    
    /**
     * Randomly populate the field with organisms.
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
                    organisms.add(leopard);
                }
                else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cheetah cheetah = new Cheetah(true, field, location);
                    organisms.add(cheetah);
                }
                else if(rand.nextDouble() <= BOAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Boar boar = new Boar(true, field, location);
                    organisms.add(boar);
                }
                else if(rand.nextDouble() <= IMPALA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Impala impala = new Impala(true, field, location);
                    organisms.add(impala);
                }
                else if(rand.nextDouble() <= RHINO_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rhino rhino = new Rhino(true, field, location);
                    organisms.add(rhino);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location);
                    organisms.add(plant);
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
