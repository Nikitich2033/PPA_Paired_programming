import java.util.List;
import java.util.Random;

/**
 * Write a description of class Grass here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Plant extends Organism
{
    //the age at which grass can produce seeds and reproduce
    private static final int POLLINATION_AGE = 5;
    // The age to which a grass patch can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a grass patch to pollinate.
    private static final double POLLINATION_PROBABILITY = 0.40;
    // The maximum number of offsprings.
    private static final int MAX_OFFSPRING_NUM = 4;

    private int age;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Grass
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
     * This is what the Impala does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newGrass A list to return newly born Impalas.
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

            if (timeOfDayString.equals("Morning") || timeOfDayString.equals("Day")){
                reproduce(newGrass);
            }


        }
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

    private void reproduce(List<Organism> newGrass)
    {
        // New Impalas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        int births = makeOffsprings();

        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            if (field.getObjectAt(loc) instanceof Plant == false){
                Plant sprout = new Plant(false, field, loc);
                newGrass.add(sprout);
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
