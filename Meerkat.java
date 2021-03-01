import java.util.List;
import java.util.Random;

public class Meerkat extends Organism {
    // Characteristics shared by all Meerkats
    // (class variables).

    // The age at which a Meerkat can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Meerkat can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Meerkat breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The rabbit's age.
    private int age;

    private Boolean gender;
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
        }

        gender = rand.nextBoolean();
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
        if(isAlive()) {

            if (weather.getIsDrought() == true){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 50) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 65) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 85) setDead();
                }
            }

            giveBirth(newMeerkats);

            if (timeOfDayString.equals("Day") || timeOfDayString.equals("Evening")){

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
                    && ((Meerkat) field.getObjectAt(location)).gender != gender){

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
