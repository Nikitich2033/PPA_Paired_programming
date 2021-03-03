import java.util.Random;

/**
 * Abstract class Animal - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Animal extends Organism
{
    // The Leopard's age.
    private int age;

    private int foodLevel;

    private Boolean gender;

    private static final Random rand = Randomizer.getRandom();

    public  Animal(Field field, Location location){
        super(field,location);

        gender = rand.nextBoolean();

    }


    protected Boolean getGender() {
        return gender;
    }
}
