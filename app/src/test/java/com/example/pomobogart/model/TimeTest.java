package com.example.pomobogart.model;

import junit.framework.TestCase;

public class TimeTest extends TestCase {
    Time time;
    public void setUp() throws Exception {
        super.setUp();
        time = new Time(10_000, 5_000);
    }

    public void testSetMilliSeconds() {
        time.setMilliSeconds(10_000);
        assertEquals(10_000, time.getMilliSeconds());
    }

    public void testGetMilliSeconds() {
        time.setMilliSeconds(25_000);
        assertEquals(25_000, time.getMilliSeconds());
    }

    public void testGetWorkingTime() {
        assertEquals(10_000, time.getWorkingTime());
    }

    public void testGetBreakTime() {
        assertEquals(5_000, time.getBreakTime());
    }

    public void testGetSeconds() {
        time.setMilliSeconds(25_000);
        assertEquals(25, time.getSeconds());
    }

    public void testGetMinutes() {
        time.setMilliSeconds(25_000_000);
        assertEquals(416, time.getMinutes());

    }
}