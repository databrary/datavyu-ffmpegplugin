package org.openshapa.models.db;

import java.io.PrintStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class PredicateVocabElementTest {
    private PrintStream outStream = System.out;
    private boolean verbose = true;

    public PredicateVocabElementTest() {
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
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class PredicateVocabElement accessors                    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredicateVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        ve = new PredicateVocabElement(new ODBCDatabase(), "test");

        if ( ve == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new PredicateVocabElement() returned null.\n");
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures += VocabElementTest.TestAccessors(ve, false,
                                                       outStream, verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("AbstractFormalArgument.TestAccessors()" +
                            " threw a SystemErrorException: \"%s\"\n",
                            SystemErrorExceptionString);
                }
            }
        }

        /* PredicateVocabElement adds no new fields, but it does do a little
         * extra error testing.  Verify that we flag errors as appropriate.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName("in valid");
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
                                "\"ve.setName(\"in valid\")\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "\"ve.setName(\"in valid\")\" failed to " +
                                "throw a SystemErrorException.\n");
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
    } /* PredicateVocabElement::TestAccessors() */

    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for this class.
     *                                           -- 3/18/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestArgListManagement() throws SystemErrorException {
        String testBanner =
            "Testing class PredicateVocabElement formal arg list management   ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredicateVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        ve = new PredicateVocabElement(new ODBCDatabase(), "test");

        if ( ve == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new PredicateVocabElement() returned null.\n");
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures += VocabElementTest.TestfArgListManagement(ve,
                                                                outStream,
                                                                verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "AbstractFormalArgument.TestfArgListManagement() " +
                            " threw a SystemErrorException: \"%s\"\n",
                            SystemErrorExceptionString);
                }
            }
        }

        /* PredicateVocabElement makes few changes to formal argument list
         * management methods, so we are almost done.  The only change is
         * the addition of the getNumElements() method, which is just another
         * name for getNumFormalArgs().  Thus it is probably sufficient to
         * just call getNumElements() and verify that it returns the expected
         * value.
         *
         * As it happens, the above inherited test routine leaves the predicate
         * vocab element with 7 arguments.  Call getNumElements() now and
         * verify that it returns 7.
         */

        if ( failures == 0 )
        {
            if ( ve.getNumElements() != 7 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "getNumElements() returned unexpected value: %d\n",
                            ve.getNumElements());
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
    } /* PredicateVocabElement::TestArgListManagement() */



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
            "Testing 2 argument constructor for class PredicateVocabElement   ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredicateVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            ve = new PredicateVocabElement(new ODBCDatabase(), "valid");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( ve == null )
                {
                    outStream.print("new PredicateVocabElement(db, \"valid\")"
                            + " returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new PredicateVocabElement(db, \"valid\")\""
                                     + " threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("valid") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial name \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of system: %b.\n",
                                       ve.getSystem());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen: %b.\n",
                                       ve.getVarLen());
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db. */
        ve = null;

        try
        {
            ve = new PredicateVocabElement(null, "valid");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( ve != null )
                {
                    outStream.print(
                        "new PredicateVocabElement(null, \"valid\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new PredicateVocabElement(null, " +
                        "\"valid\")\" didn't throw an SystemErrorException.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        ve = null;

        try
        {
            ve = new PredicateVocabElement(new ODBCDatabase(), "in valid");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( ve != null )
                {
                    outStream.print(
                        "new PredicateVocabElement(\"in valid\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new PredicateVocabElement(\"in valid\")\" "
                        + "didn't throw an SystemErrorException.\n");
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
    } /* PredicateVocabElement::Test2ArgConstructor() */


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
    public void TestCopyConstructor() throws SystemErrorException {
        final String mName = "PredicateVocabElement::TestCopyConstructor(): ";
        String testBanner =
            "Testing copy constructor for class PredicateVocabElement         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int i;
        int failures = 0;
        String s = null;
        IntFormalArg alpha = null;
        FloatFormalArg bravo = null;
        NominalFormalArg charlie = null;
        QuoteStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        PredicateVocabElement base_ve = null;
        PredicateVocabElement copy_ve = null;
        Database db = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            /* Start by creating a base predicate vocab element, and loading it
             * with a variety of formal arguments.
             */
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /** TODO: Add predicate formal arguments to this test when
                 *  they become available.
                 */

                db = new ODBCDatabase();

                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new FloatFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta = new QuoteStringFormalArg(db, "<delta>");
                echo = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");

                base_ve = new PredicateVocabElement(db, "valid");

                base_ve.appendFormalArg(alpha);
                base_ve.appendFormalArg(bravo);
                base_ve.appendFormalArg(charlie);
                base_ve.appendFormalArg(delta);
                base_ve.appendFormalArg(echo);
                base_ve.appendFormalArg(foxtrot);

                /* set other fields to non-default values just to make
                 * sure they get copied.
                 */
                base_ve.setLastModUID(2);
                base_ve.varLen = true;
                base_ve.system = true;

                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve);

                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( delta == null ) ||
                 ( echo == null ) ||
                 ( foxtrot == null ) ||
                 ( base_ve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(1).\n");
                    }

                    if ( ( db == null ) ||
                         ( alpha == null ) ||
                         ( bravo == null ) ||
                         ( charlie == null ) ||
                         ( delta == null ) ||
                         ( echo == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print("One or more classes not allocated(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(1): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now run the copy constructor on base_ve, and verify that the
         * result is a copy.
         */

        if ( failures == 0 )
        {
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                copy_ve = new PredicateVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(1).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(1):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(1).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(1).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(1)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName +
                                "unexpected return from getFormalArg() (1)");

                    }

                    if ( base_ve.fArgList == copy_ve.fArgList )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.fArgList == " +
                                             "copy_ve.fArgList)\n");
                        }
                    }

                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (1)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() !=
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(1).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 6 ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(1): %d\n", i);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            /* now create a base predicate vocab element, and loading it
             * with only one formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /** TODO: Add predicate formal arguments to this test when
                 *  they become available.
                 */

                db = new ODBCDatabase();

                foxtrot = new UnTypedFormalArg(db, "<foxtrot2>");

                base_ve = new PredicateVocabElement(db, "valid2");

                base_ve.appendFormalArg(foxtrot);

                base_ve.setLastModUID(4);
                base_ve.varLen = false;
                base_ve.system = true;

                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve);

                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                ( base_ve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(2).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(2): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now run the copy constructor on base_ve, and verify that the
         * result is a copy.
         */

        if ( failures == 0 )
        {
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                copy_ve = new PredicateVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(2).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(2):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(2).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(2).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(2)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName +
                                "unexpected return from getFormalArg() (2)");

                    }

                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (2)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() !=
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(2).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(2): %d\n", i);
                    }
                }
            }
        }


        if ( failures == 0 )
        {
            /* now create a base predicate vocab element, and don't load it
             * with any arguments.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                base_ve = new PredicateVocabElement(db, "valid3");

                base_ve.setLastModUID(6);
                base_ve.varLen = false;
                base_ve.system = false;

                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve);

                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( base_ve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(3).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(3): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now run the copy constructor on base_ve, and verify that the
         * result is a copy.
         */

        if ( failures == 0 )
        {
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                copy_ve = new PredicateVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(3).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(3):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(3).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(3).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(3)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName +
                                "unexpected return from getFormalArg() (3)");

                    }

                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (3)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() !=
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(3).\n", i);
                    }
                }

                if ( ( failures == 0 ) && ( i != 0 ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(3): %d\n", i);
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed a null predicate
         * vocab element.
         */

        base_ve = null;
        copy_ve = null;
        threwSystemErrorException = false;

        try
        {
            copy_ve = new PredicateVocabElement(base_ve);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( copy_ve != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( copy_ve != null )
                {
                    outStream.print(
                        "new PredicateVocabElement(null) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new PredicateVocabElement(null) " +
                        "didn't throw an SystemErrorException.\n");
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
    } /* PredicateVocabElement::TestCopyConstructor() */

    /**
     * TestIsWellFormedMethods()
     *
     * Test the isWellFormed method.
     *
     *               -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestIsWellFormed() throws SystemErrorException {
        String testBanner =
            "Testing isWellFormed()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean wellFormed0 = false;
        boolean wellFormed1 = false;
        boolean wellFormed2 = false;
        boolean wellFormed3 = false;
        boolean wellFormed4 = false;
        boolean wellFormed5 = false;
        boolean wellFormed6 = false;
        boolean wellFormed7 = false;
        boolean wellFormed8 = false;
        boolean wellFormed9 = false;
        int failures = 0;
        Database db = null;
        VocabList vl = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        UnTypedFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        UnTypedFormalArg mike = null;
        UnTypedFormalArg nero = null;
        UnTypedFormalArg oscar = null;
        UnTypedFormalArg papa = null;
        UnTypedFormalArg quebec = null;
        UnTypedFormalArg reno = null;
        UnTypedFormalArg sierra = null;
        UnTypedFormalArg tango = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p3dup = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        PredicateVocabElement p7 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* construct a selection of predicate vocab elements & run some
         * initial tests on isWellFormed()
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                vl = db.vl;

                alpha   = new UnTypedFormalArg(db, "<alpha>");
                bravo   = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta   = new UnTypedFormalArg(db, "<delta>");
                echo    = new UnTypedFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");
                golf    = new UnTypedFormalArg(db, "<golf>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new UnTypedFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                kilo    = new UnTypedFormalArg(db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new UnTypedFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");
                oscar   = new UnTypedFormalArg(db, "<oscar>");
                papa    = new UnTypedFormalArg(db, "<papa>");
                quebec  = new UnTypedFormalArg(db, "<quebec>");
                reno    = new UnTypedFormalArg(db, "<reno>");
                sierra  = new UnTypedFormalArg(db, "<sierra>");
                tango   = new UnTypedFormalArg(db, "<tango>");

                p0    = VocabListTest.ConstructTestPred(
                        db, "p0", alpha, bravo, charlie, delta);
                p1    = VocabListTest.ConstructTestPred(
                        db, "p1", echo, foxtrot, golf, null);
                p2    = VocabListTest.ConstructTestPred(
                        db, "p2", hotel, india, null, null);
                p3    = VocabListTest.ConstructTestPred(
                        db, "p3", juno, null, null, null);
                p3dup = VocabListTest.ConstructTestPred(
                        db, "p3", kilo, lima, null, null);
                p4    = VocabListTest.ConstructTestPred(
                        db, "p4", null, null, null, null);
                p5    = VocabListTest.ConstructTestPred(
                        db, "p5", reno, null, null, null);
                p6    = VocabListTest.ConstructTestPred(
                        db, "p6", sierra, null, null, null);
                p7    = VocabListTest.ConstructTestPred(
                        db, "p7", tango, null, null, null);

                wellFormed0 = p0.isWellFormed(true);
                wellFormed1 = p0.isWellFormed(false);
                wellFormed2 = p1.isWellFormed(true);
                wellFormed3 = p1.isWellFormed(false);
                wellFormed4 = p2.isWellFormed(true);
                wellFormed5 = p2.isWellFormed(false);
                wellFormed6 = p3.isWellFormed(true);
                wellFormed7 = p3.isWellFormed(false);
                wellFormed8 = p4.isWellFormed(true);
                wellFormed9 = p4.isWellFormed(false);

                db.vl.addElement(p0);
                db.vl.addElement(p1);
                db.vl.addElement(p2);
                db.vl.addElement(p3);

                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( alpha == null ) || ( bravo == null ) ||
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( golf == null ) || ( hotel == null ) ||
                 ( india == null ) || ( juno == null ) ||
                 ( kilo == null ) || ( lima == null ) ||
                 ( mike == null ) || ( nero == null ) ||
                 ( oscar == null ) || ( papa == null ) ||
                 ( quebec == null ) || ( reno == null ) ||
                 ( sierra == null ) || ( tango == null ) ||
                 ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                 ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) || ( p7 == null ) ||
                 ( wellFormed0 != true ) || ( wellFormed1 != false ) ||
                 ( wellFormed2 != true ) || ( wellFormed3 != false ) ||
                 ( wellFormed4 != true ) || ( wellFormed5 != false ) ||
                 ( wellFormed6 != true ) || ( wellFormed7 != false ) ||
                 ( wellFormed8 != false ) || ( wellFormed9 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(2).\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( golf == null ) || ( hotel == null ) ||
                         ( india == null ) || ( juno == null ) ||
                         ( kilo == null ) || ( lima == null ) ||
                         ( mike == null ) || ( nero == null ) ||
                         ( oscar == null ) || ( papa == null ) ||
                         ( quebec == null ) || ( reno == null ) ||
                         ( sierra == null ) || ( tango == null ) )
                    {
                        outStream.print("formal arg alloc(s) failed.\n");
                    }

                    if ( ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                         ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                         ( p6 == null ) || ( p7 == null ) )
                    {
                        outStream.print("predicate alloc(s) failed.\n");
                    }

                    if ( ( wellFormed0 != true ) || ( wellFormed1 != false ) ||
                         ( wellFormed2 != true ) || ( wellFormed3 != false ) ||
                         ( wellFormed4 != true ) || ( wellFormed5 != false ) ||
                         ( wellFormed6 != true ) || ( wellFormed7 != false ) ||
                         ( wellFormed8 != false ) || ( wellFormed9 != false ) )
                    {
                        outStream.print("Unexpected isWellFormed() results.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
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
    } /* PredicateVocabElement::TestIsWellFormed() */

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
        final String expectedString = "test(<a>, <b>, <c>, <d>, <e>, <f>)";
        final String expectedDBString =
            "((PredicateVocabElement: 0 test) " +
             "(system: true) " +
             "(varLen: true) " +
             "(fArgList: ((UnTypedFormalArg 0 <a>), " +
                          "(IntFormalArg 0 <b> false -9223372036854775808 " +
                                "9223372036854775807), " +
                          "(FloatFormalArg 0 <c> false " +
                                "-1.7976931348623157E308 " +
                                "1.7976931348623157E308), " +
                          "(TimeStampFormalArg 0 <d> false null null), " +
                          "(NominalFormalArg 0 <e> false ()), " +
                          "(QuoteStringFormalArg 0 <f>)))";
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        PredicateVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new PredicateVocabElement(new ODBCDatabase(), "test");
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB(), "<a>"));
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<b>"));
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<c>"));
                ve.appendFormalArg(new TimeStampFormalArg(ve.getDB(), "<d>"));
                ve.appendFormalArg(new NominalFormalArg(ve.getDB(), "<e>"));
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(), "<f>"));
                ve.setVarLen(true);
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                            "Setup for strings test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException in " +
                                "setup for strings test: \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }

                ve = null;
            }
        }

        if ( ve != null )
        {
            if ( ve.toString().compareTo(expectedString) != 0 )
            {
                failures++;
                outStream.printf(
                        "ve.toString() returned unexpected value: \"%s\".\n",
                        ve.toString());
            }
        }

        if ( ve != null )
        {
            if ( ve.toDBString().compareTo(expectedDBString) != 0 )
            {
                failures++;
                outStream.printf(
                        "ve.toDBString() returned unexpected value: \"%s\".\n",
                        ve.toDBString());
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
    } /* PredicateVocabElement::TestToStringMethods() */

}