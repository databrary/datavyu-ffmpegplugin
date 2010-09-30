package org.openshapa.models.db.legacy;

import java.io.PrintStream;

import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.IntFormalArg;
import org.openshapa.models.db.legacy.ODBCDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.TimeStamp;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class IntFormalArgTest {

    private PrintStream outStream;
    private boolean verbose;

    public IntFormalArgTest() {
    }

    @BeforeClass
    public void setUpClass() {
        outStream = System.out;
        verbose = true;
    }

    @AfterClass
    public void tearDownClass() {
    }

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessor methods for this class.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() {
        String testBanner =
            "Testing class IntFormalArg accessors                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures +=
                        FormalArgumentTest.TestAccessors(arg, outStream, verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("AbstractFormalArgument.TestAccessors." +
                            " threw a SystemErrorException.\n");
                }
            }
        }

        /* Now test accessors specific to IntFormalArg. */

        /* start by verifying the default values */
        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected subRange(1): %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected minVal(1): %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected maxVal(1): %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* now set the subRange... */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(1, 100);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( threwSystemErrorException ) ||
                  ( arg.getSubRange() != true ) ||
                  ( arg.getMinVal() != 1 ) ||
                  ( arg.getMaxVal() != 100 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(1, 100)\" threw a " +
                                          "SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != true )
                    {
                        outStream.printf("Unexpected subRange(2): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != 1 )
                    {
                        outStream.printf("Unexpected minVal(2): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != 100 )
                    {
                        outStream.printf("Unexpected maxVal(2): %d.\n",
                                          arg.getMaxVal());
                    }
                }
            }
        }

        /* ... and then set it back. */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(Long.MIN_VALUE, Long.MAX_VALUE);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != Long.MIN_VALUE ) ||
                  ( arg.getMaxVal() != Long.MAX_VALUE ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(MIN_VALUE, MAX_VALUE)\""
                                + " threw a SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(3): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != Long.MIN_VALUE )
                    {
                        outStream.printf("Unexpected minVal(3): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != Long.MAX_VALUE )
                    {
                        outStream.printf("Unexpected maxVal(3): %d.\n",
                                          arg.getMaxVal());
                    }
                }
            }
        }

        /* Now attempt to set an invalid subrange */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(0, 0);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( ! threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != Long.MIN_VALUE ) ||
                  ( arg.getMaxVal() != Long.MAX_VALUE ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(0, 0)\""
                                + " didn't throw a SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(4): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != Long.MIN_VALUE )
                    {
                        outStream.printf("Unexpected minVal(4): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != Long.MAX_VALUE )
                    {
                        outStream.printf("Unexpected maxVal(4): %d.\n",
                                          arg.getMaxVal());
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);

    } /* IntFormalArg::TestAccessors() */


    /**
     * TestVEAccessors()
     *
     * Run a battery of tests on the itsVocabElement and itsVocabElementID
     * accessor methods for this class.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestVEAccessors() {
        String testBanner =
            "Testing class IntFormalArg itsVocabElement accessors             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the itsVocabElement & itsVocabElementID accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures += FormalArgumentTest.TestVEAccessors(arg, outStream,
                                                           verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("FormalArgument.TestVEAccessors()" +
                            " threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::TestVEAccessors() */

    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if (failures == 0 )
        {
            if ( arg.getFargName().compareTo("<val>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* Verify that the constructor fails if passed a bad db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new IntFormalArg((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( methodReturned ) ||
                 ( arg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new IntFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                                "new IntFormalArg(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(null) didn't " +
                                         "throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::Test1ArgConstructor() */

    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() {
        String testBanner =
            "Testing 2 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<valid>\") returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\") " +
                                     "threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<valid>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(null, "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(null, \"<valid>\") != null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(null, \"<valid>\") " +
                                     "didn't throw  a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<<invalid>>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<<invalid>>\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\") " +
                                    "failed to throw a SystemErrorException.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::Test2ArgConstructor() */


    /**
     * Test4ArgConstructor()
     *
     * Run a battery of tests on the four argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test4ArgConstructor() {
        String testBanner =
            "Testing 4 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>", 0, 19);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 19) "
                                      + "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 19) "
                                     + "threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<valid>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName: \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != 19 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<<invalid>>", 0, 99);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\", " +
                            "0, 99) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\", " +
                            "0, 99) failed to throw a SystemErrorException.\n");
                }
            }
        }


        /* verify that the constructor fails when passed an invalid
         * minVal, maxVal pair.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>", 0, 0);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<valid>\", 0, 0) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 99)\""
                                  + " didn't throw a SystemErrorException.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::Test4ArgConstructor() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() {
        String testBanner =
            "Testing copy constructor for class IntFormalArg                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;
        IntFormalArg copyArg = null;
        IntFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of IntFormalArg to be copied: */
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<copy_this>", -10, 10);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print("new IntFormalArg(db, \"<copy_this>\", " +
                                     "-10, 10)\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<copy_this>\", -10, " +
                                     "10)\" threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                arg.setHidden(true);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.setHidden(true)\" threw a " +
                                     "SystemErrorException.\n");
                }
            }
            else if ( ! arg.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected value of arg.hidden.\n");
                }
            }
        }


        /* Now, try to make a copy of arg */

        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            try
            {
                copyArg = new IntFormalArg(arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg == null )
                    {
                        outStream.print(
                            "new IntFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(arg)\" " +
                                         "threw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the copy is good */

        if ( failures == 0 )
        {
            if ( arg == copyArg )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("(arg == copyArg) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo(copyArg.getFargName()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.fargName = \"%s\" != \" " +
                            "copyArg.fArgName = \"%s\".\n", arg.fargName,
                            copyArg.fargName);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != copyArg.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.hidden = %b != " +
                            "copyArg.hidden = %b.\n", arg.hidden,
                            copyArg.hidden);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != copyArg.getItsVocabElement() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.getItsVocabElement() != \" " +
                            "copyArg.getItsVocabElement().\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != copyArg.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.subRange = %b != " +
                            "copyArg.subRange = %b.\n", arg.subRange,
                            copyArg.subRange);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != copyArg.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.minVal = %d != " +
                            "copyArg.minVal = %d.\n", arg.minVal,
                            copyArg.minVal);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != copyArg.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.maxVal = %d != " +
                            "copyArg.maxnVal = %d.\n", arg.maxVal,
                            copyArg.maxVal);
                }
            }
        }

        /* now verify that we fail when we should */

        /* first ensure that the copy constructor failes when passed null */
        if ( failures == 0 )
        {
            munged = copyArg; /* save the copy for later */
            copyArg = null;
            threwSystemErrorException = false;

            try
            {
                copyArg = null;
                copyArg = new IntFormalArg(copyArg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of IntFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new IntFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(munged1)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(munged1)\" " +
                                "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the minVal & maxVal fields of an instance of IntFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<a_valid_name>";
            munged.minVal = 0;
            munged.maxVal = 0;

            try
            {
                copyArg = new IntFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(munged2)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(munged1)\" " +
                                     "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::TestCopyConstructor() */


    /**
     * TestIsValidValue()
     *
     * Verify that isValidValue() does more or less the right thing.
     *
     * Since isValidValue() uses the type tests defined in class Database,
     * and since those methods are tested extensively elsewhere, we only
     * need to verify that they are called correctly.
     *
     *                                           -- 3/11/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestIsValidValue() throws SystemErrorException {
        String testBanner =
            "Testing isValidValue()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean result;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 19;
        /* TODO -- must add predicates to this test */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ " A Valid \t Text String ",
            /* test  1 -- should return false */ new Double(0.0),
            /* test  2 -- should return true  */ new Long(0),
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ new TimeStamp(60),
            /* test  6 -- should return false */ new TimeStamp(30, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ new Long(-1),
            /* test 13 -- should return true  */ new Long(1),
            /* test 14 -- should return true  */ new Long(2),
            /* test 15 -- should return true  */ new Long(3),
            /* test 16 -- should return true  */ new Long(4),
            /* test 17 -- should return true  */ new Long(5),
            /* test 18 -- should return false */ new Long(6),
       };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ " A Valid Text String ",
            /* test  1 -- should return false */ "new Double(0.0)",
            /* test  2 -- should return true  */ "new Long(0)",
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ "new TimeStamp(60)",
            /* test  6 -- should return false */ "new TimeStamp(30, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ "new Long(-1)",
            /* test 13 -- should return true  */ "new Long(1)",
            /* test 14 -- should return true  */ "new Long(2)",
            /* test 15 -- should return true  */ "new Long(3)",
            /* test 16 -- should return true  */ "new Long(4)",
            /* test 17 -- should return true  */ "new Long(5)",
            /* test 18 -- should return false */ "new Long(6)",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
            /* test 12 should return */ false,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ false,
        };
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<arg>", 0, 5);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( arg == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new IntFormalArg()\" returned null.\n");
            }
        }

        if ( arg != null )
        {
            while ( testNum < numTestObjects )
            {
                if ( verbose )
                {
                    outStream.printf("test %d: arg.isValidValue(%s) --> %b: ",
                            testNum, testDesc[testNum],
                            expectedResult[testNum]);
                }

                threwSystemErrorException = false;
                result = false;

                try
                {
                    result = arg.isValidValue(testObjects[testNum]);
                }
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                }

                if ( ( threwSystemErrorException ) ||
                     ( result != expectedResult[testNum] ) )
                {
                    failures++;
                    if ( verbose )
                    {
                        if ( threwSystemErrorException )
                        {
                            outStream.print("failed -- unexpected exception.\n");
                        }
                        else
                        {
                            outStream.print("failed.\n");
                        }
                    }
                }
                else if ( verbose )
                {
                    outStream.print("passed.\n");
                }

                testNum++;
            }
        }

        /* Now verify that isValidValue() throws a system error when passed
         * a null.
         */

        if ( arg != null )
        {
            if ( verbose )
            {
                outStream.printf(
                        "test %d: arg.isValidValue(null) --> exception: ",
                        testNum);
            }

            methodReturned = false;
            threwSystemErrorException = false;
            result = false;

            try
            {
                result = arg.isValidValue(null);
                methodReturned = true;
            }
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( result != false ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else if ( methodReturned )
                    {
                        outStream.print("failed -- unexpected return.\n");
                    }
                    else
                    {
                        outStream.print("failed -- unexpected result.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::TestIsValidValue() */


    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *               -- 3/11/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods() throws SystemErrorException {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                arg = new IntFormalArg(new ODBCDatabase(), "<test>", 0, 10);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( arg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( arg == null )
                    {
                        outStream.print(
                                "new UnTypedFormalArg(db, \"<test>\", 0, 10) " +
                                "returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print(
                                "new UnTypedFormalArg(db, \"<test>\", 0, 10) " +
                                "threw a SystemErrorException.\n");
                    }
                }

                arg = null;
            }
        }

        if ( arg != null )
        {
            if ( arg.toString().compareTo("<test>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "arg.toString() returned unexpected value: \"%s\".\n",
                        arg.toString());
                }
            }
        }

        if ( arg != null )
        {
            if ( arg.toDBString().compareTo("(IntFormalArg 0 <test> true 0 10)")
                != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "arg.toDBString() returned unexpected value: \"%s\".\n",
                        arg.toDBString());
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* IntFormalArg::TestToStringMethods() */

}