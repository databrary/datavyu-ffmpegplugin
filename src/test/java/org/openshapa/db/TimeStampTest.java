package org.openshapa.db;

import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author swhitcher
 */
public class TimeStampTest {

    public TimeStampTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Dummy test - so far only a holder for static utility method
     */
    @Test
    public void testTimeStamp() {
    }

     /**
     * VerifyTimeStampCopy()
     *
     * Verify that the supplied instances of TimeStamp are distinct,
     * that they contain no common references, and that they
     * represent the same value.
     *                                              JRM -- 12/1/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyTimeStampCopy(TimeStamp base,
                                          TimeStamp copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
                             copyDesc);
        }
        else {
            try {
                Object basetps = PrivateAccessor.getField(base, "tps");
                Object copytps = PrivateAccessor.getField(copy, "tps");
                Object baseticks = PrivateAccessor.getField(base, "ticks");
                Object copyticks = PrivateAccessor.getField(copy, "ticks");

                if ( base == copy )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s == %s.\n", baseDesc, copyDesc);
                    }
                }
                else if ( !basetps.equals(copytps) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s.tps == %s != %s.tps == %s.\n",
                                         baseDesc, basetps, copyDesc, copytps);
                    }
                }
                else if ( !baseticks.equals(copyticks) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s.ticks == %s != %s.ticks == %s.\n",
                                         baseDesc, baseticks, copyDesc, copyticks);
                    }
                }
            } catch (Throwable th) {
                outStream.printf("Problem with .getField of base or copy.\n");
                failures++;
            }
        }

        return failures;

    } /* TimeStamp::VerifyTimeStampCopy() */


}