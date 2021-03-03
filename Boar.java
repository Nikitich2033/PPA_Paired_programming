import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Boar extends Animal {
    // Characteristics shared by all Boars
    // (class variables).

    // The age at which a Boar can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Boar can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Boar breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // number of steps a Boar can go before it has to eat again.
    private static final int FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The boar's age.
    private int age;

    // The boar's food level, which is increased by eating animals.
    private int foodLevel;

    /**
     * Create a new boar. A Boar may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Boar will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Boar(boolean randomAge, Field field, Location location)
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
     * This is what the Boar does most of the time - it runs
     * around and eats. Sometimes it will breed or die of old age.
     * @param newBoars A list to return newly born Boar
     * @param timeOfDayString A string that represents the current time of day in the simulation.
     * @param weather An object that contains information on the current weather in the simulation.
     */
    public void act(List<Organism> newBoars, String timeOfDayString, Weather weather)
    {
        incrementAge();
        incrementHunger();

        //This IF statement represents a chance to die of dehydration in case of prolonged drought.
        if (isAlive()){
            if (weather.getIsDrought()){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 7) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 9) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 13) setDead();
                }
            }
        }

        if(isAlive()) {

            giveBirth(newBoars);

            //the conditions in what weather Boar moves around are specified here
            if (weather.getCurrentWeather().equals("Clear")
                    || weather.getCurrentWeather().equals("Cloudy")
                    || weather.getCurrentWeather().equals("Rain")
                    || weather.getCurrentWeather().equals("Fog")  ) {
                //the conditions at what time of day weather Boar moves around are specified here
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
     * Look for plants adjacent to the current location.
     * Only the first grass patch is eaten.
     * @return where food was found, or null if it wasn't.
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
     * Make this Boar more hungry. This could result in its' death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the age.
     * This could result in the boars' death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Boar is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBoars
     * A list to return newly born boars
     * .
     */
    private void giveBirth(List<Organism> newBoars)
    {
        // New boars
        // are born into adjacent locations.
        // Get a list of adjacent free locations.

        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());



        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Boar
                    && ((Boar) field.getObjectAt(location)).getGender() != getGender()){

                int births = breed();

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);

                    Boar young = new Boar(false, field, loc);
                    newBoars.add(young);
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
     * A Boar can breed if it has reached the breeding age.
     * @return true if the Boar can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
