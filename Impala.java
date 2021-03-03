import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Impala extends Animal {
    // Characteristics shared by all Impalas (class variables).

    // The age at which a Impala can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Impala can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a Impala breeding.
    private static final double BREEDING_PROBABILITY = 0.20;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // number of steps an Impala can go before it has to eat again.
    private static final int FOOD_VALUE = 17;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Impala's age.
    private int age;

    // The Impala's food level, which is increased by eating animals.
    private int foodLevel;

    /**
     * Create a new impala. A Impala may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the impala will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Impala(boolean randomAge, Field field, Location location)
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


    }

    /**
     * This is what the Impala does most of the time - it runs
     * around and eats. Sometimes it will breed or die of old age.
     * @param newImpalas A list to return newly born Impalas.
     * @param timeOfDayString A string that represents the current time of day in the simulation.
     * @param weather An object that contains information on the current weather in the simulation.
     */
    public void act(List<Organism> newImpalas, String timeOfDayString, Weather weather)
    {
        incrementAge();
        incrementHunger();

        if (isAlive()){

            //This IF statement represents a chance to die of dehydration in case of prolonged drought.
            if (weather.getIsDrought()){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 6) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 9) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 11) setDead();
                }
            }

        }

        if(isAlive()) {

            giveBirth(newImpalas);

            //the conditions in what weather Impala moves around are specified here
            if (weather.getCurrentWeather().equals("Clear")
                    || weather.getCurrentWeather().equals("Cloudy")
                    || weather.getCurrentWeather().equals("Fog")){

                //the conditions at what time of day Impala moves around are specified here
                if (timeOfDayString.equals("Day") || timeOfDayString.equals("Evening")){
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
    }

    /**
     * Make this Impala more hungry. This could result in the Impala's death.
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
     * Only the first plant is eaten.
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
            if(organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if(plant.isAlive()) {
                    plant.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }

        }
        return null;
    }


    /**
     * Increase the age.
     * This could result in the Impala's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Impala is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newImpalas A list to return newly born Impalas.
     */
    private void giveBirth(List<Organism> newImpalas)
    {
        // New Impalas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());

        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Impala
                    && ((Impala) field.getObjectAt(location)).getGender() != getGender()){

                int births = breed();

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);

                    Impala young = new Impala(false, field, loc);
                    newImpalas.add(young);
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
     * A Impala can breed if it has reached the breeding age.
     * @return true if the Impala can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
