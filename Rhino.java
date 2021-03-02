import java.util.Iterator;
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
    // number of steps a Rhino can go before it has to eat again.
    private static final int FOOD_VALUE = 17;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Rhino's age.
    private int age;

    private Boolean gender;

    // The Rhino's food level, which is increased by eating animals.
    private int foodLevel;

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
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else{
            age = 0;
            foodLevel = FOOD_VALUE;
        }

        gender = rand.nextBoolean();
    }

    /**
     * This is what the Rhino does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newRhinos A list to return newly born Rhinos.
     */
    public void act(List<Organism> newRhinos, String timeOfDayString, Weather weather)
    {
        incrementAge();
        incrementHunger();

        if (isAlive()){
            if (weather.getIsDrought() == true){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 10) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 14) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 17) setDead();
                }
            }
        }

        if(isAlive()) {

            giveBirth(newRhinos);


            if (timeOfDayString.equals("Morning") || timeOfDayString.equals("Day")){

                Location newLocation = findFood();
                if(newLocation == null) {
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }

            }

        }
    }

    /**
     * Make this Impala more hungry. This could result in the Leopard's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first grass patch is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {

            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Grass) {
                Grass grass = (Grass) organism;
                if(grass.isAlive()) {
                    grass.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }

        }
        return null;
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
