package com.example.pomobogart.model;

/**
 * This class serves as a Time object where it handles the minutes and seconds
 */
public class Time {
    private long milliSeconds;
    private long workingTime;
    private long breakTime;

    /**
     * This is a constructor that will ask for working time and break time once the class called
     * @param workingTime  default working time
     * @param breakTime - default break time
     */
    public Time(long workingTime, long breakTime ) {
        this.workingTime = workingTime;
        this.breakTime = breakTime;
    }

    /**
     * @param milliSeconds will return the default working time if it has been set to 0
     */
    public void setMilliSeconds(long milliSeconds) {
        if (milliSeconds == 0) {
            milliSeconds = this.workingTime;
        }
        this.milliSeconds = milliSeconds;
    }

    public long getMilliSeconds() { return  milliSeconds; }

    public long getWorkingTime() { return workingTime; }

    public long getBreakTime() { return breakTime; }

    /**
     *
     * @return converted milliseconds to regular seconds
     */
    public int getSeconds() {
        return (int) milliSeconds / 1000;
   }

    /**
     * @return converted seconds to minutes
     */
   public int getMinutes() {
        return getSeconds() / 60;
   }









}
