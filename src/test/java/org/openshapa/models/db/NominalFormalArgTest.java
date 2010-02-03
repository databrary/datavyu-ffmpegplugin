package org.openshapa.models.db;

import java.io.PrintStream;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class NominalFormalArgTest {

    private PrintStream outStream;
    private boolean verbose;

    public NominalFormalArgTest() {
    }

    @Before
    public void setUp() {
        outStream = System.out;
        verbose = true;
    }

    @After
    public void tearDown() {
    }

    /** Utility code for Private Access. */
    private static String approvedSetToStringPrivate(NominalFormalArg arg) {
        String st = "";
        try {
            st = (String) PrivateAccessor.invoke(arg, "approvedSetToString",
                                                                    null, null);
        } catch (Throwable e) {
            fail("Problem in approvedSetToStringPrivate.");
        }
        return st;
    }

    /*** TODO: Review test code. ***/

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
            "Testing class NominalFormalArg accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean testFinished = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        NominalFormalArg arg = null;

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
            arg = new NominalFormalArg(new ODBCDatabase());
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
                            "new NominalFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new NominalFormalArg(db) threw " +
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

        /* NominalFormalArg adds subRange and approvedSet.  We must test
         * the routines supporting these fields as well.
         */

        /* First verify correct initialization */
        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("\"arg.getSubRange()\" returned " +
                            "unexpected initial value(1): %b.\n",
                            arg.getSubRange());
                }
            }
            else if ( arg.approvedSet != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.approvedSet not initialized to null.\n");
                }
            }
        }

        /* now set subRange to true, and verify that approvedSet is allocated */
        if ( failures == 0 )
        {
            arg.setSubRange(true);

            if ( ( arg.subRange != true ) ||
                 ( arg.getSubRange() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(true)\" failed " +
                            "to set arg.subRange to true.\n");
                }
            }
            else if ( ( arg.approvedSet == null ) ||
                      ( arg.approvedSet.size() != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(true)\" failed " +
                            "to initialize arg.approvedSet correctly.\n");
                }
            }
        }

        /* now test the approvedSet management functions.  Start with just a
         * simple smoke check that verifies that the methods do more or
         * less as they should, and then verify that they fail when they
         * should.
         */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved("charlie");
                arg.addApproved("bravo");
                arg.addApproved("delta");
                arg.deleteApproved("bravo");
                arg.addApproved("alpha");
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in setup(1).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(1).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( approvedSetToStringPrivate(arg).
                    compareTo("(alpha, charlie, delta)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected arg.approvedSetToString() results(1): \"%s\".\n",
                        approvedSetToStringPrivate(arg));
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<String> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in call to " +
                            "arg.getApprovedVector()(1).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(2).\n");
                }
            }
            else if ( ( approvedVector.size() != 3 ) ||
                      ( approvedVector.get(0).compareTo("alpha") != 0 ) ||
                      ( approvedVector.get(1).compareTo("charlie") != 0 ) ||
                      ( approvedVector.get(2).compareTo("delta") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected approvedVector(1).\n");
                }
            }
        }

        /* try several invalid additions to the approved set */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(null);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.addApproved(null)\" failed to throw a" +
                            "SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.addApproved(null)\" returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(" invalid ");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.addApproved(\" invalid \")\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.addApproved(\" invalid \")\" returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved("charlie"); /* already in approved set */
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.addApproved(\"charlie\")\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.addApproved(\"charlie\")\" returned.\n");
                }
            }
        }

        /* try invalid calls to arg.approved() */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.approved(null);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.approved(null)\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.approved(null)\" returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.approved(" invalid ");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.approved(\" invalid \")\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.approved(\" invalid \")\" returned.\n");
                }
            }
        }

        /* try invalid deletions from the approved set */
         if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(null);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.deleteApproved(null)\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.deleteApproved(null)\" returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(" invalid ");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.deleteApproved(\" invalid \")\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.deleteApproved(\" invalid \")\" returned.\n");
                }
            }
        }


        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved("nonesuch");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.deleteApproved(\"nonesuch\")\" failed "
                            + " to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.deleteApproved(\"nonesuch\")\" returned.\n");
                }
            }
        }

        /* now reduce the size of the approved set to 1 */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved("alpha");
                arg.deleteApproved("charlie");
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in setup(2).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(3).\n");
                }
            }
        }

        /* verify that getApprovedVector() and getApprovedString() work as they
         * should with zero entries.
         */

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<String> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in call to " +
                            "arg.getApprovedVector()(2).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(4).\n");
                }
            }
            else if ( ( approvedVector.size() != 1 ) ||
                     ( approvedVector.get(0).compareTo("delta") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected approvedVector(2).\n");
                }
            }
        }


        if ( failures == 0 )
        {
            if ( approvedSetToStringPrivate(arg).compareTo("(delta)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "Unexpected arg.approvedSetToString() results(2).\n");
                }
            }
        }

        /* now reduce the size of the approved set to 0 */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved("delta");
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in setup(3).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(5).\n");
                }
            }
        }

        /* verify that getApprovedVector() and getApprovedString() work as they
         * should with no entries.
         */

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<String> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
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
                    outStream.print(
                            "SystemErrorException thrown in call to " +
                            "arg.getApprovedVector()(3).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(5).\n");
                }
            }
            else if ( approvedVector != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected approvedVector(3).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( approvedSetToStringPrivate(arg).compareTo("()") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "Unexpected arg.approvedSetToString() results(3).\n");
                }
            }
        }

        /* now set subRange back to false.  Verify that approvedSet is set to
         * null, and that all approved set manipulation methods thow a system
         * error if invoked.
         */

        if ( failures == 0 )
        {
            arg.setSubRange(false);

            if ( ( arg.subRange != false ) ||
                 ( arg.getSubRange() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(false)\" failed " +
                            "to set arg.subRange to false.\n");
                }
            }
            else if ( arg.approvedSet != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(false)\" failed " +
                            "to set arg.approvedSet to null.\n");
                }
            }
        }

        /* finally, verify that all the approved list management routines
         * flag a system error if invoked when subRange is false.
         */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.approved("alpha");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.approved() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "test completed with subrange == false (1)).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved("alpha");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.addApproved() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "test completed with subrange == false (2)).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved("alpha");
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.deleteApproved() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "test completed with subrange == false (3)).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.getApprovedVector();
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.getApprovedVector() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "test completed with subrange == false (4)).\n");
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
    } /* NominalFormalArg::TestAccessors() */


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
            "Testing class NominalFormalArg itsVocabElement accessors         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        NominalFormalArg arg = null;

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
            arg = new NominalFormalArg(new ODBCDatabase());
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
                            "new NominalFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new NominalFormalArg(db) threw " +
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
    } /* NominalFormalArg::TestVEAccessors() */


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
            "Testing 1 argument constructor for class NominalFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        NominalFormalArg arg = null;

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
            arg = new NominalFormalArg(new ODBCDatabase());
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
                            "new NominalFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new NominalFormalArg(db) threw " +
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

        /* Verify that the constructor fails if passed a bad db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new NominalFormalArg((Database)null);
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
                        outStream.print("new NominalFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                                "new NominalFormalArg(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalFormalArg(null) didn't " +
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
    } /* NominalFormalArg::Test1ArgConstructor() */

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
            "Testing 2 argument constructor for class NominalFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        NominalFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new NominalFormalArg(new ODBCDatabase(), "<valid>");
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
                        "new NominalFormalArg(db, \"<valid>\") returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new NominalFormalArg(db, \"<valid>\") " +
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
        threwSystemErrorException = false;

        try
        {
            arg = new NominalFormalArg(null, "<valid>");
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
                        "new NominalFormalArg(null, \"<alid>>\") != null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new NominalFormalArg(null, \"<valid>\") "
                                    + "didn't throw a SystemErrorException.\n");
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
            arg = new NominalFormalArg(new ODBCDatabase(), "<<invalid>>");
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
                        "new NominalFormalArg(db, \"<<valid>>\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new NominalFormalArg(db, \"<<invalid>>\") "
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
    } /* NominalFormalArg::Test2ArgConstructor() */


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
            "Testing copy constructor for class NominalFormalArg              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        NominalFormalArg arg = null;
        NominalFormalArg copyArg = null;
        NominalFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of NominalFormalArg to be copied: */
        threwSystemErrorException = false;

        try
        {
            arg = new NominalFormalArg(new ODBCDatabase(), "<copy_this>");
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
                        "new NominalFormalArg(\"<copy_this>\")\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new NominalFormalArg(\"<copy_this>\")\" " +
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
                copyArg = new NominalFormalArg(arg);
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
                            "new NominalFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new NominalFormalArg(arg)\" " +
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
                copyArg = new NominalFormalArg(copyArg);
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
                            "new NominalFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of NominalFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new NominalFormalArg(munged);
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
                            "new NominalFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalFormalArg(munged)\" " +
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
    } /* NominalFormalArg::TestCopyConstructor() */


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
            /* test  0 -- should return false */ " A Valid \t Text String ",
            /* test  1 -- should return false */ new Double(0.0),
            /* test  2 -- should return false */ new Long(0),
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ new TimeStamp(60),
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
            /* test  1 -- should return false */ "new Double(0.0)",
            /* test  2 -- should return false */ "new Long(0)",
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ "new TimeStamp(60)",
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
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ true,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
        };
        NominalFormalArg arg = null;

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
            arg = new NominalFormalArg(new ODBCDatabase());
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
                            "new NominalFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new NominalFormalArg(db) threw " +
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
    } /* NominalFormalArg::TestIsValidValue() */


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
        NominalFormalArg arg = null;

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
                arg = new NominalFormalArg(new ODBCDatabase(), "<test>");
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
                        outStream.print("new NominalFormalArg(db, \"<test>\")" +
                                         "returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new NominalFormalArg(db, \"<test>\") " +
                                         "threw a SystemErrorException.\n");
                    }
                }

                arg = null;
            }
        }

        if ( failures == 0 )
        {
            if ( arg != null )
            {
                if ( arg.toString().compareTo("<test>") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toString() returned unexpected value(1): \"%s\".\n",
                        arg.toString());
                }
            }

            if ( arg != null )
            {
                if ( arg.toDBString().compareTo(
                        "(NominalFormalArg 0 <test> false ())") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toDBString() returned unexpected value(1): \"%s\".\n",
                        arg.toDBString());
                }
            }
        }

        /* now set subRange, add some approved nominals, and verify that
         * this is reflected in the output from toDBString().
         */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                arg.setSubRange(true);
                arg.addApproved("foxtrot");
                arg.addApproved("bravo");
                arg.addApproved("delta");
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
                    outStream.print(
                            "SystemErrorException thrown in setup(1).\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg != null )
            {
                if ( arg.toString().compareTo("<test>") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toString() returned unexpected value(2): \"%s\".\n",
                        arg.toString());
                }
            }

            if ( arg != null )
            {
                if ( arg.toDBString().compareTo(
                        "(NominalFormalArg 0 <test> true (bravo, delta, foxtrot))") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toDBString() returned unexpected value(2): \"%s\".\n",
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
    } /* NominalFormalArg::TestToStringMethods() */

}