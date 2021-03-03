import java.util.List;
import java.util.Random;

/**
 * A class that represents plants.
 *
 * @author Nikita Lyakhovoy
 */
public class Plant extends Organism
{
    //the age at which grass can produce seeds and reproduce
    private static final int POLLINATION_AGE = 5;
    // The age to which a grass patch can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a grass patch to pollinate.
    private static final double POLLINATION_PROBABILITY = 0.70;
    // The maximum number of offsprings.
    private static final int MAX_OFFSPRING_NUM = 4;

    //plant's age
    private int age;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant at location in field
     *
     * @param randomAge If true set age to a randmo value less than the MAX_AGE, else set it to 0
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(field,location);

        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }

    }


    /**
     * Plants do not move, they can only produce offspring that will be place in the nearby locations.
     * Plants can also die of age or during drought.
     * @param newGrass A list to return newly born Impalas.
     * @param timeOfDayString A string that represents the current time of day in the simulation.
     * @param weather An object that contains information on the current weather in the simulation.
     */
    public void act(List<Organism> newGrass, String timeOfDayString, Weather weather)
    {
        incrementAge();

        if (isAlive()){
            //This IF statement represents a chance to die of dehydration in case of prolonged drought. (4 times a day)
            if (weather.getIsDrought() == true){
                int randDieNum = rand.nextInt(100);
                if (weather.getDaysSinceRain() <= 6){
                    if (randDieNum <= 3) setDead();
                }
                else if(weather.getDaysSinceRain() > 6 && weather.getDaysSinceRain() <= 10) {
                    if (randDieNum <= 9) setDead();
                }
                else if(weather.getDaysSinceRain() > 10) {
                    if (randDieNum <= 18) setDead();
                }
            }
        }

        if(isAlive()) {

            //Plants can only reproduce during morning or day.
            if (timeOfDayString.equals("Morning") || timeOfDayString.equals("Day")){
                reproduce(newGrass);
            }


        }
    }


    /**
     * Increase the age.
     * This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Produce new offspring.
     * @param newPlants list of newly grown plants.
     */
    private void reproduce(List<Organism> newPlants)
    {
        // New plants grow in adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        int births = makeOffsprings();

        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            if (!(field.getObjectAt(loc) instanceof Plant)){
                Plant sprout = new Plant(false, field, loc);
                newPlants.add(sprout);
            }
        }


    }


    /**
     * Generate a number representing the number of new offsprings,
     * if it can pollinate.
     * @return The number of new offsprings (may be zero).
     */
    private int makeOffsprings()
    {
        int offsprings = 0;
        if(canReproduce() && rand.nextDouble() <= POLLINATION_PROBABILITY) {
            offsprings = rand.nextInt(MAX_OFFSPRING_NUM) + 1;
        }
        return offsprings;
    }

    /**
     * A plant can produce offsprings if it has reached the pollination age.
     * @return true if the plant is old enough to pollinate.
     */
    private boolean canReproduce()
    {
        return age >= POLLINATION_AGE;
    }
}
