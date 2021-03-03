import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Meerkat extends Animal {
    // Characteristics shared by all Meerkats
    // (class variables).

    // The age at which a Meerkat can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Meerkat can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Meerkat breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // number of steps an Impala can go before it has to eat again.
    private static final int FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The meerkat's age.
    private int age;

    // The meerkat's food level, which is increased by eating animals.
    private int foodLevel;

    /**
     * Create a new rabbit. A Meerkat may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Meerkat will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Meerkat(boolean randomAge, Field field, Location location)
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
     * This is what the Meerkat does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newMeerkats
     * A list to return newly born Meerkats
     * .
     */
    public void act(List<Organism> newMeerkats, String timeOfDayString, Weather weather)
    {
        incrementAge();
        incrementHunger();

        if (isAlive()){
            if (weather.getIsDrought() == true){
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

            giveBirth(newMeerkats);

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
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Meerkat is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMeerkats
     * A list to return newly born Meerkats
     * .
     */
    private void giveBirth(List<Organism> newMeerkats
    )
    {
        // New Meerkats
        // are born into adjacent locations.
        // Get a list of adjacent free locations.
        /* Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Meerkat young = new Meerkat(false, field, loc);
            newMeerkats
                    .add(young);
        }*/

        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());

        /* Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Cheetah young = new Cheetah(false, field, loc);
            newCheetahs.add(young);
        }*/

        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Meerkat
                    && ((Meerkat) field.getObjectAt(location)).getGender() != getGender()){

                int births = breed();

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Meerkat young = new Meerkat(false, field, loc);
                    newMeerkats.add(young);
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
     * A Meerkat can breed if it has reached the breeding age.
     * @return true if the Meerkat can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
