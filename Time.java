
/**
 * Time class stores the current time of day.
 *
 * @author Nikita Lyakhovoy
 */
public class Time
{
    // an array with all possible states of  time
    private String[] statesOfTime = {"Morning", "Day", "Evening", "Night"};
    //current time of day
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

    /**
     * Resets timeOfDay to morning
     */
    public void reset(){
        timeOfDay = 0;
    }
}
