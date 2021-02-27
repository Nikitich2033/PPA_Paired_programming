
/**
 * Write a description of class Time here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Time
{
    // instance variables - replace the example below with your own
    private String[] statesOfTime = {"Morning", "Day", "Evening", "Night"};
    private String timeOfDay;
    /**
     * Constructor for objects of class Time
     */
    public Time()
    {
        timeOfDay = "Day";
    }

    /**
     * Method to get the current timeOfDay;
     */
    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void updateTimeOfDay(){

    }
}
