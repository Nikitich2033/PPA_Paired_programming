import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Leopard extends Animal {
    // Characteristics shared by all Leopards (class variables).

    // The age at which a Leopard can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Leopard can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a Leopard breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single animal. In effect, this is the
    // number of steps a Leopard can go before it has to eat again.
    private static final int FOOD_VALUE = 24;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The Leopard's age.
    private int age;
    // The Leopard's food level, which is increased by eating animals.
    private int foodLevel;

    //Gender: False is Male, True is Female
    //private Boolean gender;

    /**
     * Create a Leopard. A Leopard can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Leopard will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge,Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }


    }

    /**
     * This is what the Leopard does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * //@param field The field currently occupied.
     * @param newLeopards A list to return newly born Leopards.
     * @param timeOfDayString A string that represents the current time of day in the simulation.
     * @param weather An object that contains information on the current weather in the simulation.
     */
    public void act(List<Organism> newLeopards, String timeOfDayString, Weather weather)
    {

        incrementAge();
        incrementHunger();

        //This IF statement represents a chance to die of dehydration in case of prolonged drought.
        if (isAlive()){
            if (weather.getIsDrought()){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 2) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 4) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 9) setDead();
                }
            }
        }


        if(isAlive()) {

            giveBirth(newLeopards);

            //the conditions in what weather Cheetah moves around are specified here
            if (weather.getCurrentWeather().equals("Clear")
                    || weather.getCurrentWeather().equals("Cloudy")
                    || weather.getCurrentWeather().equals("Fog")){

                //the conditions at which time of day Cheetah moves around are specified here
                if (timeOfDayString.equals("Night") || timeOfDayString.equals("Evening")){

                    // Move towards a source of food if found.
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
     * Increase the age. This could result in the Leopard's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Leopard more hungry. This could result in the Leopard's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal of the right type is eaten.
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

            if(organism instanceof Boar) {
                Boar boar = (Boar) organism;
                if(boar.isAlive()) {
                    boar.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(organism instanceof Impala) {
                Impala impala = (Impala) organism;
                if(impala.isAlive()) {
                    impala.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(organism instanceof Rhino) {
                Rhino rhino = (Rhino) organism;
                if(rhino.isAlive()) {
                    rhino.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }

        }
        return null;
    }

    /**
     * Check whether or not this Leopard is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLeopards A list to return newly born Leopards.
     */
    private void giveBirth(List<Organism> newLeopards)
    {
        // New Leopards are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());

        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Leopard
                    && ((Leopard) field.getObjectAt(location)).getGender() != getGender()){

                int births = breed();

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);

                    Leopard young = new Leopard(false, field, loc);
                    newLeopards.add(young);
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
     * A Leopard can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
