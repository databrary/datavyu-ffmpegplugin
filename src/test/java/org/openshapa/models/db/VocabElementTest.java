package org.openshapa.models.db;

import java.io.PrintStream;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class VocabElementTest {

    public VocabElementTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void DummyTest() {
    }

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors defined in this class using
     * the instance of some subclass of VocabElement supplied in
     * the argument list.
     *
     * This method is intended to be called in the test code of the subclass
     * of VocabElement, and thus just returns the number of failures
     * unless the verbose parameter is true.
     *
     * Note that the method doesn't leave the supplied instance of some
     * subclass of VocabElement in the same condidtion it found
     * it in, so it is probably best to discard the instance on return.
     *
     *                                           -- 3/17/07
     *
     * Changes:
     *
     *    - Added the setNameOnSystemVEOK field, as I had forgotten that
     *      it was OK to change the name of a system MVE and needed to
     *      modify the test to allow it.
     *                                           -- 11/18/08
     */

    public static int TestAccessors(VocabElement ve,
                                    boolean setNameOnSystemVEOK,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
        final String mName = "VocabElement::TestAccessors(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }

        /***************************************/
        /* Start by testing accessors for name */
        /***************************************/

        /* verify that we have the default vocab element name */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("test") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(1) \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        /* now change it to another value.  Make it valid for
         * both predicates and matricies.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName("a_valid_name");
                methodReturned= true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("\"arg.setName(\"a_valid_name\")\""
                            + " failed to return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setName(\"a_valid_name\")\""
                            + " threw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change took */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("a_valid_name") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(2) \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        /* Most name validation is done at a higher level, so we can't do much
         * here.  However, we can verify that setName will throw a system error
         * if passed a null.
         */

        if ( failures == 0 )
        {
            String nullString = null;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName(nullString);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("\"arg.setName(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"arg.setName(null)\" failed to " +
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change didn't take */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("a_valid_name") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(3) \"%s\".\n",
                                       ve.getName());
                }
            }
        }


        /* Finally, verify that setName() either will or will not refuse to
         * change the name if the system flag is set depending on the value
         * of the setNameOnSystemVEOK.  Note that for the purposes of this
         * test, we change the system flag directly, as otherwise the setup
         * for this test would be much more involved.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve.system = true;

            try
            {
                ve.setName("another_valid_name");
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            ve.system = false;

            if ( setNameOnSystemVEOK )
            {
                if ( ( ! methodReturned ) ||
                     ( threwSystemErrorException ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( ! methodReturned )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "failed to return with system flag set.\n");
                        }

                        if ( ! threwSystemErrorException )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "threw a SystemErrorException " +
                                    "with system flag set.\n");
                        }
                    }
                }
            }
            else
            {
                if ( ( methodReturned ) ||
                     ( ! threwSystemErrorException ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( methodReturned )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "returned with system flag set.\n");
                        }

                        if ( ! threwSystemErrorException )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "failed to throw a SystemErrorException " +
                                    "with system flag set.\n");
                        }
                    }
                }
            }
        }


        /* again, verify that the change did or didn't take */

        if ( failures == 0 )
        {
            if ( ( ( setNameOnSystemVEOK ) &&
                   ( ve.getName().compareTo("another_valid_name") != 0 ) ) ||
                 ( ( ! setNameOnSystemVEOK ) &&
                   ( ve.getName().compareTo("a_valid_name") != 0 ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(4) \"%s\".\n",
                                       ve.getName());
                }
            }
        }


        /***********************************************/
        /* now test the accessors for the varLen field */
        /***********************************************/

        /* verify that valLen has its default value */

        if ( failures == 0 )
        {
            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(1): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* now try to set varLen to true, and verify that the change took */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(true);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "\"arg.setVarLen(true)\" did not return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(true)\" threw a " +
                                "SystemErrorException.\n");
                    }
                }
            }


            if ( ve.getVarLen() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(2): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* now try to set varLen back to false, and verify that the change took */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(false);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "\"arg.setVarLen(false)\" did not return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(false)\" threw a " +
                                "SystemErrorException.\n");
                    }
                }
            }


            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(3): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* Finally, verify that setVarLen will throw a system error if
         * invoked when the system flag is set.
         */

        if ( failures == 0 )
        {
            ve.system = true;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(true);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            ve.system = false;


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("\"arg.setVarLen(true)\" returned " +
                                "with system flag set.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(false)\" failed " +
                                "to throw a SystemErrorException with system " +
                                "flag set.\n");
                    }
                }
            }


            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(4): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /***********************************************/
        /* now test the accessors for the system field */
        /***********************************************/

        /* verify that system has its default value */

        if ( failures == 0 )
        {
            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(1): %b.\n",
                                       ve.getSystem());
                }
            }
        }


        /* now try to set system to true. This should fail as we don't have
         * any formal arguments defined.  Verify that system is still false.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                            "\"arg.setSystem()\" return when fArgList empty.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"arg.setSystem()\" failed to throw" +
                            " a SystemErrorException when fArgList empty.\n");
                    }
                }
            }


            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(2): %b.\n",
                                       ve.getSystem());
                }
            }
        }

        /* now add a formal argument and try to set system again.  Should
         * succeed.
         */
        if ( failures == 0 )
        {
            boolean appendFormalArgReturned = false;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB()));
                appendFormalArgReturned = true;
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }


            if ( ( ! appendFormalArgReturned ) ||
                 ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! appendFormalArgReturned )
                    {
                        outStream.print(
                            "\"arg.apendFormalArg()\" failed to return.\n");
                    }

                    if ( ! methodReturned )
                    {
                        outStream.print(
                            "\"arg.setSystem()\" failed to return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                             "\"arg.apendFormalArg()\" or \"arg.setSystem()\"" +
                             " threw a SystemErrorException: \"%s\"\n",
                             systemErrorExceptionString);
                    }
                }
            }


            if ( ve.getSystem() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(3): %b.\n",
                                       ve.getSystem());
                }
            }
        }

        return failures;

    } /* FormalArgument::TestAccessors() */

    /**
     * TestfArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * defined in this class using the instance of some subclass of
     * VocabElement supplied in the argument list.
     *
     * This method is intended to be called in the test code of subclasses
     * of VocabElement, and thus just returns the number of failures
     * unless the verbose parameter is true.
     *
     * While fArgLisToDBString(), fArgListToString(), and getNumFormalArgs()
     * are not tested systematically, they are used heavily it the tests for
     * the other formal argument list management routines.  Thus further
     * testing of these routines is probably not necessary.
     *
     * Note that the method doesn't leave the supplied instance of some
     * subclass of VocabElement in the same condition it found
     * it in, so it is probably best to discard the instance on return.
     *
     *                                           -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestfArgListManagement(VocabElement ve,
                                            java.io.PrintStream outStream,
                                            boolean verbose)
        throws SystemErrorException
    {
        final String mName = "VocabElement::TestfArgListManagement(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        FormalArgument arg0 = null;
        FormalArgument arg1 = null;
        FormalArgument arg2 = null;
        FormalArgument arg3 = null;
        FormalArgument arg4 = null;
        FormalArgument arg5 = null;
        FormalArgument arg6 = null;
        FormalArgument testArg0 = null;
        FormalArgument testArg1 = null;
        FormalArgument testArg2 = null;
        FormalArgument testArg3 = null;
        FormalArgument testArg4 = null;
        FormalArgument testArg5 = null;
        FormalArgument testArg6 = null;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( ve.fArgList.size() != 0 )
        {
            outStream.print(mName + "ve.fArgList.size() != 0 on entry.\n");

            throw new SystemErrorException(
                    mName + "ve.fArgList not empty on entry.");
        }

        failures = VerifyfNumFormalArgs(ve, 0, outStream, verbose, 0);


        /********************************/
        /*** testing AppendFormlArg() ***/
        /********************************/

        /* append several formal arguments and verify that they made it
         * into the formal argument list correctly.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB(), "<alpha>"));
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<bravo>"));
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<charlie>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to fArgListString " +
                                "failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(1): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    2);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 2);
        }

        /* Try to append a null formal argument.  Should fail with a system
         * error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                UnTypedFormalArg nullArg = null;
                ve.appendFormalArg(nullArg);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg(null) returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg(null) failed to "+
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    4);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 4);
         }


        /* Now set the system flag and verify that attempting to append a
         * valid formal argument will thow a system error.  Set the system
         * flag directly instead of using setSystem().
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(),
                                                            "<delta>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "appendFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg() with system set "+
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    6);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 6);
        }


        /* Now attempt to append a valid formal argument whose name is the
         * same as an existing formal argument.  This should throw a system
         * error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(),
                                                            "<alpha>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                            "appendFormalArg() with dup fArgName returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg() with dup fArgName" +
                                " failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    8);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 8);
        }


        /********************************/
        /*** testing InsertFormlArg() ***/
        /********************************/

        /* Insert a bunch of new formal arguments, and verify that the
         * insertions actually took place.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new TimeStampFormalArg(ve.getDB(), "<delta>"),
                                   0);
                ve.insertFormalArg(new QuoteStringFormalArg(ve.getDB(), "<echo>"),
                                   2);
                ve.insertFormalArg(new NominalFormalArg(ve.getDB(), "<foxtrot>"),
                                   5);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "insertFormalArg() failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(9): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    10);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 10);
        }

        /* there are lots of ways of getting insertFormalArg() to throw
         * a system error.  Work through them one by one.
         *
         * Start with sending it a null formal argument.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                UnTypedFormalArg nullArg = null;
                ve.insertFormalArg(nullArg, 2);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg(null) returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg(null) failed to "+
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    12);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 12);
        }


        /* Now set the system flag and verify that attempting to insert a
         * valid formal argument will thow a system error.  Set the system
         * flag directly instead of using setSystem().
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   2);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "insertFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with system set "+
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    14);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 14);
        }


        /* Next, try to insert an valid formal argument with a negative target
         * index.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   -1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                           "insertFormalArg() with negative index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with negative " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    16);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 16);
        }

        /* Next, try to insert an valid formal argument with a target
         * index that doesn't exist.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   7);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with " +
                                "non-existant index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with non-existant " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    18);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 18);
        }


        /* Next, try to insert an valid formal argument with a formal argument
         * name that already appears in the formal argument list.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<alpha>"),
                                   1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with " +
                                "duplicate fArgName returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with duplicate " +
                            "fArgName failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    20);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 20);
        }



        /********************************/
        /*** testing DeleteFormlArg() ***/
        /********************************/

        /* We have inserted a bunch of entries in the formal argument list.
         * Now lets delete some and verify that we get the expected result.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(5);
                ve.deleteFormalArg(3);
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "deleteFormalArg() failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(21): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    22);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 22);
        }


        /* deleteFormalArg() should fail with a system error if n is negative,
         * if the target entry doesn't exist, or if the system flag is set.
         * Verify this.
         */

        /* try to delete a formal argument when system is set.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "deleteFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with system set " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    24);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 24);
        }


        /* try to delete a formal argument with negative index.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(-1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                            "deleteFormalArg() with negative index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with negative index" +
                                " failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    26);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 26);
        }

        /* try to delete a formal argument that doesn't exist.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(3);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with non-existant " +
                                "index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with non-existant " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    28);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 28);
        }

        /* Delete all arguments in prep for next test. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(0);
                ve.deleteFormalArg(0);
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "deleteFormalArg() failed to complete(2).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(29): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "()",
                    null,
                    outStream,
                    verbose,
                    30);

            failures += VerifyfNumFormalArgs(ve, 0, outStream, verbose, 30);
        }


        /******************************/
        /*** testing getFormalArg() ***/
        /******************************/

        /* Start by setting up a formal argument list for us to test on. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg0 = new UnTypedFormalArg(ve.getDB(), "<hotel>");
                ve.insertFormalArg(arg0, 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to setup " +
                                "getFormalArg() tests failed to complete(31).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(31): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    32);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 32);
        }

        /* get the first (and only) formal argument, and verify that it is
         * a copy of arg0.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg0 = ve.getFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "getFormalArg(0) failed to return(33).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(33): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }

            if ( ( testArg0 != arg0 ) ||
                 ( ! ( testArg0 instanceof UnTypedFormalArg ) ) ||
                 ( arg0.getFargName().compareTo(testArg0.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(0) doesn't match arg0\n");
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    34);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 34);
        }


        /* Now attempt to get a formal argument with negative index.
         * Should fail with a systme error.
         */
        if ( failures == 0 )
        {
            FormalArgument testArg = null;

            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg = ve.getFormalArg(-1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "getFormalArg(-1) returned(35).\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg(-1) failed to throw " +
                            "a SystemErrorException.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    36);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 36);
        }


        /* Now attempt to get a formal argument that doesn't exist.
         * Should return null.
         */
        if ( failures == 0 )
        {
            FormalArgument testArg = null;

            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg = ve.getFormalArg(1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( testArg!= null ) ||
                 ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg() for non-existant " +
                                "entry failed to return(37).\n");
                    }
                }

                if ( testArg != null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg() for non-existant " +
                                "entry didn't return null(37).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(37): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    38);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 38);
        }

        /* finally, add entries of all available types, get them, and then
         * verify that the opbjects returned are not identical to the objects
         * inserted, and contain the same data.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                // arg0 = new UnTypedFormalArg("<hotel>");
                arg1 = new IntFormalArg(ve.getDB(), "<india>");
                arg2 = new FloatFormalArg(ve.getDB(), "<juno>");
                arg3 = new NominalFormalArg(ve.getDB(), "<kilo>");
                arg4 = new TimeStampFormalArg(ve.getDB(), "<lima>");
                arg5 = new QuoteStringFormalArg(ve.getDB(), "<mike>");
                arg6 = new PredFormalArg(ve.getDB(), "<nero>");
                ve.insertFormalArg(arg1, 1);
                ve.insertFormalArg(arg2, 2);
                ve.insertFormalArg(arg3, 3);
                ve.insertFormalArg(arg4, 4);
                ve.insertFormalArg(arg5, 5);
                ve.insertFormalArg(arg6, 6);
                testArg0 = ve.getFormalArg(0);
                testArg1 = ve.getFormalArg(1);
                testArg2 = ve.getFormalArg(2);
                testArg3 = ve.getFormalArg(3);
                testArg4 = ve.getFormalArg(4);
                testArg5 = ve.getFormalArg(5);
                testArg6 = ve.getFormalArg(6);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to setup & run " +
                                "getFormalArg() tests failed to complete(39).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(39): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }

            if ( ( testArg0 != arg0 ) ||
                 ( ! ( testArg0 instanceof UnTypedFormalArg ) ) ||
                 ( arg0.getFargName().compareTo(testArg0.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(0) doesn't match arg0(2)\n");
                }
            }

            if ( ( testArg1 != arg1 ) ||
                 ( ! ( testArg1 instanceof IntFormalArg ) ) ||
                 ( arg1.getFargName().compareTo(testArg1.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(1) doesn't match arg1\n");
                }
            }

            if ( ( testArg2 != arg2 ) ||
                 ( ! ( testArg2 instanceof FloatFormalArg ) ) ||
                 ( arg2.getFargName().compareTo(testArg2.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(2) doesn't match arg2\n");
                }
            }

            if ( ( testArg3 != arg3 ) ||
                 ( ! ( testArg3 instanceof NominalFormalArg ) ) ||
                 ( arg3.getFargName().compareTo(testArg3.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(3) doesn't match arg3\n");
                }
            }

            if ( ( testArg4 != arg4 ) ||
                 ( ! ( testArg4 instanceof TimeStampFormalArg ) ) ||
                 ( arg4.getFargName().compareTo(testArg4.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(4) doesn't match arg4\n");
                }
            }

            if ( ( testArg5 != arg5 ) ||
                 ( ! ( testArg5 instanceof QuoteStringFormalArg ) ) ||
                 ( arg5.getFargName().compareTo(testArg5.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(5) doesn't match arg5\n");
                }
            }

            if ( ( testArg6 != arg6 ) ||
                 ( ! ( testArg6 instanceof PredFormalArg ) ) ||
                 ( arg6.getFargName().compareTo(testArg6.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(6) doesn't match arg6\n");
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>, <india>, <juno>, <kilo>, <lima>, <mike>, <nero>)",
                    null,
                    outStream,
                    verbose,
                    40);

            failures += VerifyfNumFormalArgs(ve, 7, outStream, verbose, 40);
        }


        /**********************************/
        /*** testing replaceFormalArg() ***/
        /**********************************/

        /* replaceFormalArg() is implemented very simply with one call each to
         * removeFormalArg() and insertFormalArg().  As we have already tested
         * those routines, our testing here can be cursory.
         *
         * If the implementation of replaceFormalArg() is ever reworked
         * significantly, this decision should be revisited.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.replaceFormalArg((FormalArgument)
                            (new IntFormalArg(ve.getDB(), "<oscar>")), 0);
                ve.replaceFormalArg((FormalArgument)
                            (new UnTypedFormalArg(ve.getDB(), "<papa>")), 2);
                ve.replaceFormalArg((FormalArgument)
                             (new UnTypedFormalArg(ve.getDB(), "<quebec>")), 5);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to test " +
                                "replaceFormalArg() failed to complete(41).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(41): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<oscar>, <india>, <papa>, <kilo>, <lima>, <quebec>, <nero>)",
                    null,
                    outStream,
                    verbose,
                    42);

            failures += VerifyfNumFormalArgs(ve, 7, outStream, verbose, 42);
        }

        return failures;

    } /* VocabElement::TestfArgListManagement() */


    /**
     * VerifyfArgListContents()
     *
     * Verify the contents of the formal argument list by running
     * fArgListToString() and fArgListToDBString() and comparing the output
     * with the supplied strings.  If discrepencies are found, increment
     * the failure count, and (if verbose is true) generate a diagnostic
     * message
     *
     *                                           -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int VerifyfArgListContents(VocabElement ve,
                                             String expectedString,
                                             String expectedDBString,
                                             java.io.PrintStream outStream,
                                             boolean verbose,
                                             int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabElement::VerifyfArgListContents(): ";
        String fArgListString = null;
        String fArgListDBString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( ( expectedString == null ) && ( expectedDBString == null ) )
        {
            outStream.print(mName + "both expected strings null on entry.\n");

            throw new SystemErrorException(
                    mName + "both expected strings null on entry.");
        }

        try
        {
            fArgListString = ve.fArgListToString();
            fArgListDBString = ve.fArgListToDBString();
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }
        if ( ( fArgListString == null ) ||
             ( fArgListDBString == null ) ||
             ( threwSystemErrorException) )
        {
            if ( fArgListString == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fArgListString is null(%d)\n", testNum);
                }
            }

            if ( fArgListDBString == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fArgListDBString is null(%d)\n", testNum);
                }
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected SystemErrorException(%d): \"%s\"\n",
                        testNum, systemErrorExceptionString);
                }
            }
        }

        if ( ( expectedString != null ) &&
             ( fArgListString.compareTo(expectedString) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "Unexpected fArgListString(%d): \"%s\"\n",
                        testNum, fArgListString);
            }
        }

        if ( ( expectedDBString != null ) &&
             ( fArgListDBString.compareTo(expectedDBString) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "Unexpected fArgListDBString(%d): \"%s\"\n",
                        testNum, fArgListDBString);
            }
        }

        return failures;

    } /* VocabElement::VerifyfArgListContents() */


    /**
     * VerifyfNumFormalArgs()
     *
     * Verify the number of entries in the formal argument list by
     * running getNumFormalArgs() and conparing the result with the expected
     * value.  If a discrepency is found, increment the failure count, and
     * (if verbose is true) generate a diagnostic message
     *
     *                                           -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int VerifyfNumFormalArgs(VocabElement ve,
                                           int expectedNumFormalArgs,
                                           java.io.PrintStream outStream,
                                           boolean verbose,
                                           int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabElement::VerifyfNumFormalArgs(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int numArgs = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( expectedNumFormalArgs < 0 )
        {
            outStream.print(mName + "expectedNumFormalArgs < 0 ?!?\n");

            throw new SystemErrorException(
                    mName + "negative expected number of formal arguments.");
        }

        try
        {
            numArgs = ve.getNumFormalArgs();
        }

       catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( numArgs != expectedNumFormalArgs ) ||
             ( threwSystemErrorException) )
        {
            if ( numArgs != expectedNumFormalArgs )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected numFormalArgs(%d): %d (%d)\n",
                            testNum, numArgs, expectedNumFormalArgs);
                }
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected SystemErrorException(%d): \"%s\"\n",
                        testNum, systemErrorExceptionString);
                }
            }
        }

        return failures;

    } /* VocabElement::VerifyNumFormalArgs() */

}