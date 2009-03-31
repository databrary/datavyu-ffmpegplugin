package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author swhitcher
 */
public class UnTypedFormalArgTest {

    private PrintStream outStream;
    private boolean verbose;

    public UnTypedFormalArgTest() {
    }

    @Before
    public void setUp() {
        outStream = System.out;
        verbose = true;
    }

    @After
    public void tearDown() {
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
            "Testing class UnTypedFormalArg accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
            arg = null;

            try
            {
                arg = new UnTypedFormalArg(new ODBCDatabase());
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "new UnTypedFormalArg(db) returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new UnTypedFormalArg(db) " +
                                "threw unexpected system error exception: " +
                                "\"%s\".\n", systemErrorExceptionString);
                    }
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

        /* UnTypedFormalArgument adds no new fields, so we are done. */

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
    } /* UnTypedFormalArg::TestAccessors() */


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
            "Testing class UnTypedFormalArg itsVocabElement accessors         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

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
            arg = new UnTypedFormalArg(new ODBCDatabase());
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
                            "new UnTypedFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new UnTypedFormalArg(db) threw " +
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
    } /* UnTypedFormalArg::TestVEAccessors() */


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
            "Testing 1 argument constructor for class UnTypedFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            arg = null;
            systemErrorExceptionString = null;

            try
            {
                arg = new UnTypedFormalArg(new ODBCDatabase());
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! methodReturned ) ||
                 ( arg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new UntypedFormalArg(db) failed to return.\n");
                    }

                    if ( arg == null )
                    {
                        outStream.print(
                                "new UntypedFormalArg(db) returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new UntypedFormalArg(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
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


        /* Verify that the constructor fails when passed an invalid db. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            arg = null;
            systemErrorExceptionString = null;

            try
            {
                arg = new UnTypedFormalArg((Database)null);
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
                                "new UntypedFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                             "new UntypedFormalArg(null) returned non-null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UntypedFormalArg(db) failed to " +
                                "throw system error exception.\n");
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
    } /* UnTypedFormalArg::Test1ArgConstructor() */

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
            "Testing 2 argument constructor for class UnTypedFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<valid>");
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
                        "new UnTypedFormalArg(db, \"<valid>\")\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db, \"<valid>\")\" " +
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

        /* Verify that the constructor fails when passed an invalid db. */
        arg = null;
        systemErrorExceptionString = null;
        threwSystemErrorException = false;

        try
        {
            arg = new UnTypedFormalArg(null, "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
                        "new UnTypedFormalArg(null, \"<valid>\")\" != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(null, \"<valid>\")\""
                                   + " didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        systemErrorExceptionString = null;
        threwSystemErrorException = false;

        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<<invalid>>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
                        "new UnTypedFormalArg(db, \"<<valid>>\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db, \"<<invalid>>\") "
                        + "didn't throw a SystemErrorException.\n");
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
    } /* UnTypedFormalArg::Test2ArgConstructor() */


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
            "Testing copy constructor for class UnTypedFormalArg              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;
        UnTypedFormalArg copyArg = null;
        UnTypedFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of UnTypedFormalArg to be copied: */
        threwSystemErrorException = false;

        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<copy_this>");
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
                        "new UnTypedFormalArg(\"<copy_this>\")\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(\"<copy_this>\")\" " +
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
                copyArg = new UnTypedFormalArg(arg);
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
                            "new UnTypedFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(arg)\" " +
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
                copyArg = new UnTypedFormalArg(copyArg);
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
                            "new UnTypedFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of UnTypedFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new UnTypedFormalArg(munged);
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
                            "new UnTypedFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(munged)\" " +
                                "didn't throw an SystemErrorException.\n");
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
    } /* UnTypedFormalArg::TestCopyConstructor() */


    /**
     * TestIsValidValue()
     *
     * Verify that isValidValue() does more or less the right thing.
     *
     * Since isValidValue() uses the type tests defined in class Database,
     * and since those methods are tested extensively elsewhere, we only
     * need to verify that they are called correctly.
     *
     *                                          JRM -- 3/11/07
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
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean result;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 12;
        /* TODO -- must add predicates to this test */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ " A Valid \t Text String ",
            /* test  1 -- should return true  */ new Double(0.0),
            /* test  2 -- should return true  */ new Long(0),
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return true  */ new TimeStamp(60),
            /* test  6 -- should return false */ new TimeStamp(30, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ " A Valid Text String ",
            /* test  1 -- should return true  */ "new Double(0.0)",
            /* test  2 -- should return true  */ "new Long(0)",
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return true  */ "new TimeStamp(60)",
            /* test  6 -- should return false */ "new TimeStamp(30, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ true,
            /* test  2 should return */ true,
            /* test  3 should return */ true,
            /* test  4 should return */ true,
            /* test  5 should return */ true,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
        };
        long pveID = DBIndex.INVALID_ID;
        long alt_pveID = DBIndex.INVALID_ID;
        Database db = null;
        Database alt_db = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement alt_pve = null;
        FormalArgument farg = null;
        UnTypedFormalArg arg = null;
        Predicate p = null;
        Predicate alt_p = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;

        try
        {
            db = new ODBCDatabase();
            arg = new UnTypedFormalArg(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( db == null ) ||
             ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.printf("allocation of db failed.\n");
                }

                if ( arg == null )
                {
                    outStream.print(
                            "new UnTypedFormalArg(db)\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db) threw a system " +
                            "error exception.\n");
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

        /* we must verify that we handle predicates correctly as well --
         * start by allocating some test predicates, and then run the tests
         */
        if ( failures == 0 )
        {
            try
            {
                pve = new PredicateVocabElement(db, "pve");
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve.appendFormalArg(farg);

                pveID = db.addPredVE(pve);

                // get a copy of the databases version of pve0 with ids assigned
                pve = db.getPredVE(pveID);

                p = new Predicate(db, pveID);


                alt_db = new ODBCDatabase();


                alt_pve = new PredicateVocabElement(alt_db, "alt_pve");
                farg = new UnTypedFormalArg(alt_db, "<alt_pve>");
                alt_pve.appendFormalArg(farg);

                alt_pveID = alt_db.addPredVE(alt_pve);

                // get a copy of the alt_db's version of alt_pve with ids assigned
                alt_pve = db.getPredVE(alt_pveID);

                alt_p = new Predicate(alt_db, alt_pveID);


                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            } catch (LogicErrorException le) {
                threwSystemErrorException = true;
                systemErrorExceptionString = le.toString();
            }

            if ( ( pve == null ) ||
                 ( pveID == DBIndex.INVALID_ID ) ||
                 ( p == null ) ||
                 ( alt_db == null ) ||
                 ( alt_pve == null ) ||
                 ( alt_pveID == DBIndex.INVALID_ID ) ||
                 ( alt_p == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve == null )
                    {
                        outStream.printf("allocation of pve failed.\n");
                    }

                    if ( pveID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("pveID == DBIndex.INVALID_ID.\n");
                    }

                    if ( p == null )
                    {
                        outStream.printf("allocation of p failed.\n");
                    }

                    if ( alt_db == null )
                    {
                        outStream.printf("allocation of alt_db failed.\n");
                    }

                    if ( alt_pve == null )
                    {
                        outStream.printf("allocation of alt_pve failed.\n");
                    }

                    if ( alt_pveID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("alt_pveID == DBIndex.INVALID_ID.\n");
                    }

                    if ( alt_p == null )
                    {
                        outStream.printf("allocation of alt_p failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "pred test setup failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("pred test setup threw a " +
                                "SystemErrorException: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else
            {
                if ( ! arg.isValidValue(p) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("arg.isValidValue(p) is false.\n");
                    }
                }

                if ( arg.isValidValue(alt_p) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("arg.isValidValue(alt_p) is true.\n");
                    }
                }
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

            completed = false;
            threwSystemErrorException = false;
            result = false;

            try
            {
                result = arg.isValidValue(null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( result != false ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else if ( completed )
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
    } /* UnTypedFormalArg::TestIsValidValue() */


    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 3/11/07
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
        UnTypedFormalArg arg = null;

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
                arg = new UnTypedFormalArg(new ODBCDatabase(), "<test>");
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
                            "new UnTypedFormalArg(\"<test>\")\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(\"<test>\")\" " +
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
            if ( arg.toDBString().compareTo("(UnTypedFormalArg 0 <test>)")
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
    } /* UnTypedFormalArg::TestToStringMethods() */

}