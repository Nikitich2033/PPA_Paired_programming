import java.util.ArrayList;
import java.util.Random;

/**
 * Weather class that represents the weather inside the simulation.
 *
 * @author Nikita Lyakhovoy
 */
public class Weather
{
    // an array with all the possible types of weather in the simulation
    private String[] statesOfWeather = {"Clear","Heatwave","Fog","Cloudy", "Rain","Thunderstorm"};
    //Stores the current state of weather
    private String currentWeather;
    //Indicates where there isa drought or not at this point in time.
    private Boolean IsDrought;
    //Stores the number of days that have passed since the last rain occured
    private int daysSinceRain;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        setRandomWeather();
        IsDrought = false;
    }

    /**
     * Set weather to a random state
     */
    public void setRandomWeather()
    {
        currentWeather = statesOfWeather[rand.nextInt(statesOfWeather.length - 1)];
        if (currentWeather != "Rain" && currentWeather != "Thunderstorm" ){
            daysSinceRain++;
        }

        else{
            daysSinceRain = 0;
            removeDrought();
        }
        setIsDrought();
    }

    /**
     * @return the current state of weather.
     */
    public String getCurrentWeather() {
        return currentWeather;
    }

    private void setIsDrought(){

        if (daysSinceRain > 7){
            currentWeather = currentWeather + " (DROUGHT)";
            IsDrought = true;
        }
        else if(daysSinceRain <= 7){
            IsDrought = false;
            removeDrought();

        }

    }

    /**
     * Removes the drought keyword from the current state of weather.
     */
    private void removeDrought(){

        String[] words = currentWeather.split(" ");
        currentWeather = "";
        for (String word:
             words) {
            if (word != "(DROUGHT)"){currentWeather = currentWeather + word;}
        }

    }

    /*
    *@return true if there is a drought at his point in time.
    */
    public Boolean getIsDrought() {
        return IsDrought;
    }

    /**
     * @return days since lats rain
     */
    public int getDaysSinceRain() {
        return daysSinceRain;
    }

    public void reset(){
        IsDrought = false;
        daysSinceRain = 0;
        currentWeather = statesOfWeather[rand.nextInt(statesOfWeather.length - 1)];
    }
}
