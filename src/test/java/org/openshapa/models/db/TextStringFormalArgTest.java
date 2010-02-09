package org.openshapa.models.db;

import java.io.PrintStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class TextStringFormalArgTest {

    private PrintStream outStream;
    private boolean verbose;

    public TextStringFormalArgTest() {
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
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() {
        String testBanner =
            "Testing class TextStringFormalArg accessors                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TextStringFormalArg arg = null;

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
            arg = new TextStringFormalArg(new ODBCDatabase());
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
                            "new TextStringFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TextStringFormalArg(db) threw " +
                                      "system error exception: \"%s\".\n",
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

        /* TextStringFormalArg adds only subRange, and does not allow its
         * value to be modified.  Thus all we need to do is verify that
         * arg.getSubRange() is false, and we are done.
         */
        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "\"arg.getSubRange()\" returned unexpected value: %b.\n",
                        arg.getSubRange());
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
    } /* TextStringFormalArg::TestAccessors() */


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
            "Testing class TextStringFormalArg itsVocabElement accessors      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TextStringFormalArg arg = null;

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
            arg = new TextStringFormalArg(new ODBCDatabase());
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
                            "new TextStringFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TextStringFormalArg(db) threw " +
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
    } /* TextStringFormalArg::TestVEAccessors() */


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
            "Testing 1 argument constructor for class TextStringFormalArg     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean pass = true;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        TextStringFormalArg arg = null;

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
            arg = new TextStringFormalArg(new ODBCDatabase());
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
                            "new TextStringFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TextStringFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
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

        /* verify that the constructor fails with an invalid db */

        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new TextStringFormalArg((Database)null);
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
                        outStream.print(
                                "new TextStringFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print("new TextStringFormalArg(null) "+
                                         "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringFormalArg(null) " +
                                "failed to throw system error exception.\n");
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
    } /* TextStringFormalArg::Test1ArgConstructor() */


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
            "Testing copy constructor for class TextStringFormalArg           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        TextStringFormalArg arg = null;
        TextStringFormalArg copyArg = null;
        TextStringFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of TextStringFormalArg to be copied: */
        threwSystemErrorException = false;

        try
        {
            /* Text String formal arguments normally don't have any need
             * for a formal argument name, so there is no costructor that
             * sets the name.  For this test, we need to change something
             * about the formal argument so we can verify that we have
             * copied the right formal argument.  Thus we create the new
             * TextStringFormalArgument, and then set it name.
             */
            arg = new TextStringFormalArg(new ODBCDatabase());

            if ( arg != null )
            {
                arg.setFargName("<copy_this>");
            }
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
                        "\"new TextStringFormalArg()\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    /* it is also possible that the call to arg.setFargName()
                     * threw the SystemErrorException.
                     */
                    outStream.print("new TextStringFormalArg()\" " +
                                     "threw a SystemErrorException.\n");
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
                copyArg = new TextStringFormalArg(arg);
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
                            "new TextStringFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new TextStringFormalArg(arg)\" " +
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
                copyArg = new TextStringFormalArg(copyArg);
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
                            "new TextStringFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of TextStringFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new TextStringFormalArg(munged);
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
                            "new TextStringFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringFormalArg(munged)\" " +
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
    } /* TextStringFormalArg::TestCopyConstructor() */


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
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean result;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 12;
        /* TODO -- must add predicates to this test */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ " A Valid \t Text String ",
            /* test  1 -- should return false */ new Double(0.0),
            /* test  2 -- should return false */ new Long(0),
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return false */ new TimeStamp(60),
            /* test  6 -- should return false */ new TimeStamp(30, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return true */ " An Invalid \t Quote string ",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ " A Valid Text String ",
            /* test  1 -- should return false */ "new Double(0.0)",
            /* test  2 -- should return false */ "new Long(0)",
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return false */ "new TimeStamp(60)",
            /* test  6 -- should return false */ "new TimeStamp(30, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return true  */ " An Invalid \t Quote string ",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ true,
            /* test  4 should return */ true,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ true,
        };
        TextStringFormalArg arg = null;

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
            arg = new TextStringFormalArg(new ODBCDatabase());
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
                            "new TextStringFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TextStringFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
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
    } /* TextStringFormalArg::TestIsValidValue() */


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
        TextStringFormalArg arg = null;

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
                /* Text String formal arguments normally don't have any need
                 * for a formal argument name, so there is no costructor that
                 * sets the name.  For this test, it is useful to change the
                 * fargName so we can verify that we are grabbing the correct
                 * data when we construct a string version of the  formal
                 * argument.  Thus we create the new TextStringFormalArgument,
                 * and then set itsname.
                 */
                arg = new TextStringFormalArg(new ODBCDatabase());

                if ( arg != null )
                {
                    arg.setFargName("<test>");
                }
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
                            "\"new TextStringFormalArg()\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        /* it is also possible that the call to
                         * arg.setFargName() threw the SystemErrorException.
                         */
                        outStream.print("\"new TextStringFormalArg()\" " +
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
                outStream.printf(
                        "arg.toString() returned unexpected value: \"%s\".\n",
                        arg.toString());
            }
        }

        if ( arg != null )
        {
            if ( arg.toDBString().compareTo("(TextStringFormalArg 0 <test>)")
                 != 0 )
            {
                failures++;
                outStream.printf(
                        "arg.toDBString() returned unexpected value: \"%s\".\n",
                        arg.toDBString());
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
    } /* TextStringFormalArg::TestToStringMethods() */

}