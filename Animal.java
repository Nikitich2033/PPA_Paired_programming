import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @author Nikita Lyakhovoy
 */
public abstract class Animal extends Organism
{

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
