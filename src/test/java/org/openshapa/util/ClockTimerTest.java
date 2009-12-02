/*
 */

package org.openshapa.util;

import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openshapa.util.ClockTimer.ClockListener;

import static org.junit.Assert.*;

/**
 *
 */
public class ClockTimerTest {

    //--------------------------------------------------------------------------
    //
    //

    private ClockTimer instance;

    private long msStartTime;
    private long nanoStartTime;


    //--------------------------------------------------------------------------
    //
    //

    public ClockTimerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        instance = new ClockTimer();
    }

    @After
    public void tearDown() {
    }

    //--------------------------------------------------------------------------
    // [Tests]
    //

//    @Test
    public void testTicks() throws InterruptedException {
        System.out.println("testing 'ticks'");
        instance.registerListener(new ClockTimer.ClockListener() {
                public void clockTick(long time) {
                    testTicksReport("", time);
                }
                public void clockStart(long time) {
                    testTicksReport("start", time);
                }
                public void clockStop(long time) {
                    testTicksReport("stop", time);
                }
                public void clockRate(float rate) {
                    System.out.format("%f => rate%n", rate);
                }
                public void clockStep(long time) {
                    testTicksReport("step", time);
                }
            });
        msStartTime = System.currentTimeMillis();
        nanoStartTime = System.nanoTime();
        instance.start();
        Thread.sleep(10 * 1000 - 1); // seconds
        instance.stop();
        Thread.sleep(500); // seconds

       System.out.println("'ticks' tested");
    }

    private void testTicksReport(final String msg, final long time) {
        double clock = Double.NaN;
        try {
            clock = (Double) PrivateAccessor.getField(instance, "time");
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        System.out.format(
                "(%f) %d => %d (%d) : %s%n",
                clock,
                time,
                System.currentTimeMillis() - msStartTime,
                System.nanoTime() - nanoStartTime,
                msg
            );
    }


    /**
     * Test of setTime method, of class ClockTimer.
     */
    //@Test
    public void testSetTime() {
        System.out.println("setTime");
        long newTime = 0L;
        ClockTimer instance = new ClockTimer();
        instance.setTime(newTime);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTime method, of class ClockTimer.
     */
    //@Test
    public void testGetTime() {
        System.out.println("getTime");
        ClockTimer instance = new ClockTimer();
        long expResult = 0L;
        long result = instance.getTime();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRate method, of class ClockTimer.
     */
    //@Test
    public void testSetRate() {
        System.out.println("setRate");
        float newRate = 0.0F;
        ClockTimer instance = new ClockTimer();
        instance.setRate(newRate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRate method, of class ClockTimer.
     */
    //@Test
    public void testGetRate() {
        System.out.println("getRate");
        ClockTimer instance = new ClockTimer();
        float expResult = 0.0F;
        float result = instance.getRate();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class ClockTimer.
     */
    @Test
    public void testStart() {
        System.out.println("start|stop|isStopped");
        assertTrue(instance.isStopped());
        instance.start();
        assertFalse(instance.isStopped());
        instance.stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail("Problem sleeping!");
        }
        assertTrue(instance.isStopped());
    }

    /**
     * Test of stop method, of class ClockTimer.
     */
    //@Test
    public void testStop() {
        System.out.println("stop");
        ClockTimer instance = new ClockTimer();
        instance.stop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stepTime method, of class ClockTimer.
     */
    //@Test
    public void testStepTime() {
        System.out.println("stepTime");
        long ms = 0L;
        ClockTimer instance = new ClockTimer();
        instance.stepTime(ms);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isStopped method, of class ClockTimer.
     */
    //@Test
    public void testIsStopped() {
        System.out.println("isStopped");
        ClockTimer instance = new ClockTimer();
        boolean expResult = false;
        boolean result = instance.isStopped();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerListener method, of class ClockTimer.
     */
    //@Test
    public void testRegisterListener() {
        System.out.println("registerListener");
        ClockListener listener = null;
        ClockTimer instance = new ClockTimer();
        instance.registerListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}