
/**
 * Time class stores the current time of day.
 *
 */
public class Time
{
    // instance
    private String[] statesOfTime = {"Morning", "Day", "Evening", "Night"};
    private int timeOfDay;

    /**
     * Constructor for objects of class Time
     */
    public Time()
    {
        timeOfDay = 0;
    }

    /**
     * Method to get the current timeOfDay;
     */
    public String getTimeOfDay() {
        return statesOfTime[timeOfDay];
    }

    /**
     * Method that switches time of day to the next state;
     */
    public void incrementTimeOfDay(){
        if (timeOfDay < statesOfTime.length - 1){
            timeOfDay++;
        }
        else if(timeOfDay == statesOfTime.length - 1){
            timeOfDay = 0;
        }
    }

    public void reset(){
        timeOfDay = 0;
    }
}
