import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Cheetah extends Organism {
    // Characteristics shared by all Cheetaes (class variables).

    // The age at which a Cheeta can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Cheeta can live.
    private static final int MAX_AGE = 85;
    // The likelihood of a Cheeta breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a Cheetah can go before it has to eat again.
    private static final int FOOD_VALUE = 22;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The Cheeta's age.
    private int age;
    // The Cheeta's food level, which is increased by eating rabbits.
    private int foodLevel;

    private Boolean gender;
    /**
     * Create a Cheetah. A Cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Cheetah will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah (boolean randomAge, Field field, Location location)
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

        gender = rand.nextBoolean();
    }

    /**
     * This is what the Cheeta does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * //@param field The field currently occupied.
     * @param newCheetahs A list to return newly born Cheetahs.
     */
    public void act(List<Organism> newCheetahs, String timeOfDayString, Weather weather)
    {
        incrementAge();
        incrementHunger();

        if (isAlive()){
            //This IF statement represents a chance to die of dehydration in case of prolonged drought. (4 times a day)
            if (weather.getIsDrought() == true){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 2) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 6) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 11) setDead();
                }
            }
        }

        if(isAlive()) {
            giveBirth(newCheetahs);

            // Move towards a source of food if found.
            if (timeOfDayString.equals("Night") || timeOfDayString.equals("Evening")){
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
     * Increase the age. This could result in the Cheetah's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Cheetah more hungry. This could result in the Cheetah's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
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

            if(organism instanceof Meerkat) {
                Meerkat meerkat = (Meerkat) organism;
                if(meerkat.isAlive()) {
                    meerkat.setDead();
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
     * Check whether or not this Cheetah is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCheetahs A list to return newly born Cheetahs.
     */
    private void giveBirth(List<Organism> newCheetahs)
    {
        // New Cheetahs are born into adjacent locations.


        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> full = field.getFullAdjacentLocations(getLocation());

        int births = breed();

        for (Location location: full) {
            if ( field.getObjectAt(location) instanceof Cheetah
                    && ((Cheetah) field.getObjectAt(location)).gender != gender){

                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);

                    Cheetah young = new Cheetah(false, field, loc);
                    newCheetahs.add(young);
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
     * A Cheeta can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

}
