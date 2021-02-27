import java.util.List;
import java.util.Random;

public class Rhino extends Organism {
    // Characteristics shared by all Rhinos (class variables).

    // The age at which a Rhino can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Rhino can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a Rhino breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Rhino's age.
    private int age;

    private Boolean gender;

    /**
     * Create a new Rhino. A Rhino may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Rhino will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rhino(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }

        gender = rand.nextBoolean();
    }

    /**
     * This is what the Rhino does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newRhinos A list to return newly born Rhinos.
     */
    public void act(List<Organism> newRhinos)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newRhinos);
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Rhino's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Rhino is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRhinos A list to return newly born Rhinos.
     */
    private void giveBirth(List<Organism> newRhinos)
    {
        // New Rhinos are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());

        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Rhino
                    && ((Rhino) field.getObjectAt(location)).gender != gender){

                int births = breed();

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Rhino young = new Rhino(false, field, loc);
                    newRhinos.add(young);
                }
                break;
            }
        }

    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Rhino can breed if it has reached the breeding age.
     * @return true if the Rhino can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
