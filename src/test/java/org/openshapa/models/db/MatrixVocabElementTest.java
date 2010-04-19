package org.openshapa.models.db;

import org.openshapa.models.db.MatrixVocabElement.MatrixType;
import java.io.PrintStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 *
 */
public class MatrixVocabElementTest {

    private PrintStream outStream;
    private boolean verbose;

    public MatrixVocabElementTest() {
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
            "Testing class MatrixVocabElement accessors                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        ve = new MatrixVocabElement(new ODBCDatabase(), "test");

        if ( ve == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new MatrixVocabElement() returned null.\n");
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                ve.setType(MatrixType.MATRIX); /* test will fail otherwise */
                failures += VocabElementTest.TestAccessors(ve, true,
                                                       outStream, verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("AbstractFormalArgument.TestAccessors()" +
                            " threw a SystemErrorException: \"%s\"\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* MatrixVocabElement both adds new fields and does extensive error
         * checking.  Thus we have a lot more work to do.
         */

        /* the setName method adds test code to verify that the new name is a
         * valid spreadsheet variable name.  Run a quick test to verify that
         * the method will throw a system error if supplied an invalid
         * spreads sheet variable name.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName("in,valid");
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
                                "\"ve.setName(\"in,valid\")\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "\"ve.setName(\"in,valid\")\" failed to " +
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* the itsColumn field and the associated getItsColumn() and
         * setItsColumn() accessors in flux at present, so we will not test
         * them now.
         *
         * TODO: fix this as soon as the underlying design settles.
         */

        /* MatrixVocabElement adds the type field, which specifies the type of
         * the spreadsheet variable with which it is associated.  Since the
         * type is tightly bound to the types and numbers of formal arguments
         * permitted, this field is initializes to UNDEFINED, and most operations
         * on the instance of MatrixVocabElement are disabled until the type
         * has been specified.
         *
         * Here, we verify that setSystem() will fail if type has not been
         * specified.
         *
         * TODO: Also verify that setItSColumn() fails if the type has not
         *       been set.
         *
         * Methods modifying the formal argument list will not junction until
         * the type has been set, and the selected type constricts the number
         * and type of formal arguments.  However, we will test this in the
         * formal argument list management tests.
         *
         * Finally, once a type has been selected, it may not be changed.
         *
         * Tests follow:
         */

        /* start by allocating a fresh instance of MatrixVocabElement, and
         * verifying that the type field is initialized correctly.
         */
        if ( failures == 0 )
        {
            MatrixType initType = MatrixType.FLOAT;

            threwSystemErrorException = false;

            try
            {
                ve = null;
                ve = new MatrixVocabElement(new ODBCDatabase(), "test");
                initType = ve.getType();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ve == null ) ||
                 ( initType != MatrixType.UNDEFINED ) ||
                 ( ve.type != MatrixType.UNDEFINED ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ve == null )
                    {
                        outStream.print("Couldn't allocate instance of " +
                                "MatrixVocabElement for type init value test.\n");
                    }

                    if ( ( initType != MatrixType.UNDEFINED ) ||
                         ( ve.type != MatrixType.UNDEFINED ) )
                    {
                        outStream.print("Unexpected initial value of type.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(100): %s.\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now, try to set system to true with type still undefined.
         * Should fail.  We don't bother to test the other way round as
         * that has already been tested in VocabElement.TestAccessors()
         * above.
         */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            methodReturned = false;

            try
            {
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "setSystem() returned with type UNDEFINED.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("setSystem() with type undefined " +
                                "failed to throw a system error.\n");
                    }
                }
            }
        }

        if ( ( failures == 0 ) && ( ve.type != MatrixType.UNDEFINED ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(1).\n");
            }
        }

        /* Try to set the type to UNDEFINED.  Should fail with a system error. */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            methodReturned = false;

            try
            {
                ve.setType(MatrixType.UNDEFINED);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("setType(UNDEFINED) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("setType(UNDEFINED) failed to throw " +
                                "a system error.\n");
                    }
                }
            }
        }

        if ( ( failures == 0 ) && ( ve.type != MatrixType.UNDEFINED ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(2).\n");
            }
        }

        /* Finally, set type to some legal value, and then try to change it.
         * The first operation should succeed, the second should fail with
         * a system error.
         */
        if ( failures == 0 )
        {
            boolean secondMethodReturned = false;

            threwSystemErrorException = false;
            methodReturned = false;

            try
            {
                ve.setType(MatrixType.FLOAT);
                methodReturned = true;
                ve.setType(MatrixType.INTEGER);
                secondMethodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( secondMethodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "first call to setType() failed to return.\n");
                    }

                    if ( secondMethodReturned )
                    {
                        outStream.print("second call to setType() returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        if ( methodReturned )
                        {
                            outStream.print("second call to setType failed to " +
                                    "throw a system error.\n");
                        }
                        else
                        {
                            outStream.printf(
                                    "Unexpected system error exception " +
                                    "in first call to setType: \"%s\"\n",
                                    systemErrorExceptionString);
                        }
                    }
                }
            }
        }

        if ( ( failures == 0 ) && ( ve.type != MatrixType.FLOAT ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(3).\n");
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
    } /* MatrixVocabElement::TestAccessors() */

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
            "Testing class MatrixVocabElement formal arg list management      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        ve = new MatrixVocabElement(new ODBCDatabase(), "test");

        if ( ve == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new MatrixVocabElement() returned null.\n");
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                ve.setType(MatrixType.MATRIX); /* test will fail otherwise */
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
                    outStream.printf("FormalArgument.TestfArgListManagement()" +
                            " threw a SystemErrorException: \"%s\"\n",
                            SystemErrorExceptionString);
                }
            }
        }

        /* MatrixVocabElement makes a lot of changes to the formal argument
         * list management code, mostly in the area of error checking, as
         * most matrix types can only take one formal argument, and that of
         * a specified type.
         *
         * We will test these restrictions shortly.  However another change
         * is the addition of the getNumElements() method, which is just another
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

        /* Now on to testing the type and number of argument restrictions on
         * all types of MatrixVocabElements with the exception of
         * matrixType.MATRIX.  These tests are extensive, so I have put the
         * tests for each type in its own method.
         */

        if ( failures == 0 )
        {
            int progress = 0;

            try
            {
                failures += TestIntArgListManagement(outStream, verbose);
                progress++;
                failures += TestFloatArgListManagement(outStream, verbose);
                progress++;
                failures += TestNominalArgListManagement(outStream, verbose);
                progress++;
                failures += TestTextArgListManagement(outStream, verbose);
                progress++;
                failures += TestMatrixArgListManagement(outStream, verbose);
                progress++;
                failures += TestPredArgListManagement(outStream, verbose);
                progress = 10;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( progress < 10 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( progress < 10 )
                    {
                        outStream.printf("Typed arg list management tests " +
                                "did not complete.  Progress = %d\n", progress);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in typed " +
                                "arg list management tests: \"%s\"\n",
                                SystemErrorExceptionString);
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
    } /* MatrixVocabElement::TestArgListManagement() */


    /**
     * TestFloatArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for float instances of this class.
     *
     *                                           -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestFloatArgListManagement(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "float_test");
                ve.setType(MatrixType.FLOAT);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for float type " +
                                "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in float test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert non integer arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                                                    new IntFormalArg(ve.getDB()),
                                                    "float",
                                                    "IntFormalArg",
                                                    outStream,
                                                    verbose,
                                                    2);

                failures += VerifyTypeMisMatchError(ve,
                                                    new NominalFormalArg(ve.getDB()),
                                                    "float",
                                                    "NominalFormalArg",
                                                    outStream,
                                                    verbose,
                                                    3);

                failures += VerifyTypeMisMatchError(ve,
                                                    new QuoteStringFormalArg(ve.getDB()),
                                                    "float",
                                                    "QuoteStringFormalArg",
                                                    outStream,
                                                    verbose,
                                                    4);

                failures += VerifyTypeMisMatchError(ve,
                                                    new TextStringFormalArg(ve.getDB()),
                                                    "float",
                                                    "TextStringFormalArg",
                                                    outStream,
                                                    verbose,
                                                    5);

                failures += VerifyTypeMisMatchError(ve,
                                                    new TimeStampFormalArg(ve.getDB()),
                                                    "float",
                                                    "TimeStampFormalArg",
                                                    outStream,
                                                    verbose,
                                                    6);

                /*** TODO:  Add predicate formal arguments when available ***/

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
                        outStream.print("Type mismatch tests for float " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        /* try to append an float argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<float0>"));
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
                                "appendFormalArg(new FloatFormalArg(<float0>)) " +
                                "in a float matrix didn't return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }

        /* try to append a second integer argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<float1>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "appendFormalArg(new FloatFormalArg(<float1>)) " +
                                "in an float matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new FloatFormalArg(<float1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }

        /* Finally, try to insert a second float formal argument
         *                              -- should fail
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new FloatFormalArg(ve.getDB(), "<float2>"), 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "insertFormalArg(new FloatFormalArg(<float2>)) " +
                                "in an float matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new FloatFormalArg(<float2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }


        return failures;

    } /* MatrixVocabElement::TestFloatArgListManagement() */


    /**
     * TestIntArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for integer instances of this class.
     *
     *                                           -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestIntArgListManagement(java.io.PrintStream outStream,
                                               boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "int_test");
                ve.setType(MatrixType.INTEGER);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for integer type " +
                                "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Integer test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert non integer arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "integer",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "integer",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "integer",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "integer",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "integer",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);

                /*** TODO:  Add predicate formal arguments when available ***/

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
                        outStream.print("Type mismatch tests for integer " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        /* try to append an integer argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<int0>"));
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
                                "appendFormalArg(new IntFormalArg(<int0>)) " +
                                "in an integer matrix didn't return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }

        /* try to append a second integer argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<int1>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "appendFormalArg(new IntFormalArg(<int1>)) " +
                                "in an integer matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new IntFormalArg(<int1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }

        /* Finally, try to insert a second integer formal argument
         *                              -- should fail
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new IntFormalArg(ve.getDB(), "<int2>"), 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "insertFormalArg(new IntFormalArg(<int2>)) " +
                                "in an integer matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new IntFormalArg(<int2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }


        return failures;

    } /* MatrixVocabElement::TestIntArgListManagement() */


    /**
     * TestMatrixArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for matrix instances of this class.
     *
     * In this case, there isn't much to do, as VocabElement::
     * TestfArgListManagement() has tested almost everything we need to test
     * in matrix argument list management.  All we have to do here is verify
     * that attempts to append or insert text string formal arguments fail.
     *
     *                                           -- 3/26/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestMatrixArgListManagement(java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "matrix_test");
                ve.setType(MatrixType.NOMINAL);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for matrix type " +
                                "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in matrix test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert text stringl arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "matrix",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        2);

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
                        outStream.print("Type mismatch tests for matrix " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        return failures;

    } /* MatrixVocabElement::TestMatrixArgListManagement() */


    /**
     * TestNominalArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for Nominal instances of this class.
     *
     *                                           -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestNominalArgListManagement(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "nominal_test");
                ve.setType(MatrixType.NOMINAL);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for nominal type " +
                                "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Nominal test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert non nominal arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "nominal",
                        "IntFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "nominal",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "nominal",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "nominal",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "nominal",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);

                /*** TODO:  Add predicate formal arguments when available ***/

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
                        outStream.print("Type mismatch tests for nominal " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        /* try to append an nominal argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal0>"));
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
                                "appendFormalArg(new NominalFormalArg(<nominal0>)) " +
                                "in a nominal matrix didn't return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }

        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal1>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "appendFormalArg(new IntFormalArg(<nominal1>)) " +
                                "in a nominal matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new NominalFormalArg(<nominal1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }

        /* Finally, try to insert a second nominal formal argument
         *                              -- should fail
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal2>"), 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "insertFormalArg(new NominalFormalArg(<nominal2>)) " +
                                "in a nominal matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new NominalFormalArg(<nominal2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }


        return failures;

    } /* MatrixVocabElement::TestNominalArgListManagement() */


    /**
     * TestPredArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for Predicate instances of this class.
     *
     *                                           -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestPredArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(),
                                           "predicate_test");
                ve.setType(MatrixType.PREDICATE);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for predicate type " +
                                "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Predicate test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert non predicate arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "predicate",
                        "IntFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "predicate",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "predicate",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "predicate",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "predicate",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        6);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "predicate",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        7);

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
                        outStream.print("Type mismatch tests for predicate " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        /* try to append an predicate argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(
                        new PredFormalArg(ve.getDB(), "<pred0>"));
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
                                "appendFormalArg(new PredFormalArg(<pred0>)) " +
                                "in a predicate matrix didn't return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }

        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new PredFormalArg(ve.getDB(), "<pred1>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "appendFormalArg(new PredFormalArg(<pred1>)) " +
                                "in a predicate matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new PredFormalArg(<pred1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }

        /* Finally, try to insert a second predicate formal argument
         *                              -- should fail
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(
                        new PredFormalArg(ve.getDB(), "<pred2>"), 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "insertFormalArg(new PredFormalArg(<pred2>)) " +
                                "in a predicate matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new PredFormalArg(<pred2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }


        return failures;

    } /* MatrixVocabElement::TestPredArgListManagement() */


    /**
     * TestTextArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * for text string instances of this class.
     *
     *                                           -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestTextArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "text_test");
                ve.setType(MatrixType.TEXT);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for text " +
                                "type test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in text test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* try to append & insert non text string arguments -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "text",
                        "IntFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "text",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "text",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "text",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "text",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);

                /*** TODO:  Add predicate formal arguments when available ***/

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
                        outStream.print("Type mismatch tests for text " +
                                "matrix failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify that none of the appends / insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }

        /* try to append an text string argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new TextStringFormalArg(ve.getDB()));
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
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "in an text matrix didn't return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<val>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }

        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                /* set up an instance of TextStringFormalArg with a name
                 * other than the default name of "<val>".  Need to do this
                 * as using a duplicate name could cause a false negative in
                 * the test.
                 */
                FormalArgument t = new TextStringFormalArg(ve.getDB());
                t.setFargName("<arg1>");
                ve.appendFormalArg(t);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "in a text matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<val>)",
                                                            null,
                                                            outStream,
                                                            verbose,
                                                            15);
         }

        /* Finally, try to insert a second nominal formal argument
         *                              -- should fail
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                /* set up an instance of TextStringFormalArg with a name
                 * other than the default name of "<val>".  Need to do this
                 * as usning a duplicate name could cause a false negative in
                 * the test.
                 */
                FormalArgument t = new TextStringFormalArg(ve.getDB());
                t.setFargName("<arg2>");
                ve.insertFormalArg(t, 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "insertFormalArg(new TextStringFormalArg()) " +
                                "in a text matrix returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new TextStringFormalArg()) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElementTest.VerifyfArgListContents(ve,
                                                            "(<val>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
        }

        return failures;

    } /* MatrixVocabElement::TestTextArgListManagement() */


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
            "Testing 1 argument constructor for class MatrixVocabElement      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve = null;

            try
            {
                ve = new MatrixVocabElement((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( methodReturned ) ||
                 ( ve != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "new MatrixVocabElement(null) returned.\n");
                    }

                    if ( ve != null )
                    {
                        outStream.print(
                            "new MatrixVocabElement(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new MatrixVocabElement(null) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve = null;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase());
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( ve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new MatrixVocabElement(db) didn't return.\n");
                    }

                    if ( ve == null )
                    {
                        outStream.print(
                                "new MatrixVocabElement(db) returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new MatrixVocabElement(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("") != 0 )
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

        if ( failures == 0 )
        {
            if ( ve.getType() != MatrixType.UNDEFINED )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected initial value of type.\n");
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
    } /* MatrixVocabElement::Test1ArgConstructor() */


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
            "Testing 2 argument constructor for class MatrixVocabElement      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            ve = new MatrixVocabElement(new ODBCDatabase(), "valid");
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
                    outStream.print("new MatrixVocabElement(db, \"valid\") " +
                            "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(db, \"valid\")\"" +
                                     " threw a SystemErrorException.\n");
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

        if ( failures == 0 )
        {
            if ( ve.getType() != MatrixType.UNDEFINED )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected initial value of type.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db. */
        ve = null;

        try
        {
            ve = new MatrixVocabElement(null, "valid");
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
                        "new MatrixVocabElement(null, \"valid\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(null, \"valid\")\" "
                        + "didn't throw an SystemErrorException.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        ve = null;

        try
        {
            ve = new MatrixVocabElement(new ODBCDatabase(), " in valid ");
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
                        "new MatrixVocabElement(db, \" in valid \") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new MatrixVocabElement(db, \" in valid \")\" " +
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
    } /* MatrixVocabElement::Test2ArgConstructor() */


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
        final String mName = "MatrixVocabElement::TestCopyConstructor(): ";
        String testBanner =
            "Testing copy constructor for class MatrixVocabElement            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int i;
        int failures = 0;
        int progress = 0;
        String s = null;
        IntFormalArg alpha = null;
        FloatFormalArg bravo = null;
        NominalFormalArg charlie = null;
        QuoteStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        TextStringFormalArg golf = null;
        MatrixVocabElement base_ve = null;
        MatrixVocabElement copy_ve = null;
        Database db = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            /* Start by creating a base matrix vocab element, and loading it
             * with a variety of formal arguments.
             */
            progress = 0;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /** TODO: Add predicate formal arguments to this test when
                 *  they become available.
                 */

                db = new ODBCDatabase();

                progress++;

                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new FloatFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta = new QuoteStringFormalArg(db, "<delta>");
                echo = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");

                progress++;

                base_ve = new MatrixVocabElement(db, "matrix");

                progress++;

                base_ve.setType(MatrixType.MATRIX);

                progress++;

                base_ve.appendFormalArg(alpha);
                base_ve.appendFormalArg(bravo);
                base_ve.appendFormalArg(charlie);
                base_ve.appendFormalArg(delta);
                base_ve.appendFormalArg(echo);
                base_ve.appendFormalArg(foxtrot);

                progress++;

                /* set other fields to non-default values just to make
                 * sure they get copied.
                 */
                base_ve.setLastModUID(2);
                base_ve.varLen = true;
                base_ve.system = true;

                progress++;

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
                        outStream.printf("base_ve initialization didn't " +
                                "complete(1).  progress = %d\n", progress);
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
                copy_ve = new MatrixVocabElement(base_ve);
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
            /* now create a base matrix vocab element, and loading it
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

                base_ve = new MatrixVocabElement(db, "matrix2");

                base_ve.setType(MatrixType.MATRIX);

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
                copy_ve = new MatrixVocabElement(base_ve);
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
            /* now create a base matrix vocab element, and don't load it
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

                base_ve = new MatrixVocabElement(db, "matrix3");

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
                copy_ve = new MatrixVocabElement(base_ve);
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
            else if ( ( base_ve.fArgList != null ) &&
                      ( base_ve.fArgList.size() != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected number of formal " +
                                     "args(3): %d\n",
                                     base_ve.fArgList.size());
                }
            }
        }

        /* So far we have been testing matrix type matrix vocab elements.
         * Must also spot check the other types.  Will only do one or tow
         * as they are all pretty similar.
         */

        if ( failures == 0 )
        {
            /* Create a text string matrix vocab element, and loading it
             * with a text string formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            golf = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                golf = new TextStringFormalArg(db);

                base_ve = new MatrixVocabElement(db, "text4");

                base_ve.setType(MatrixType.TEXT);

                base_ve.appendFormalArg(golf);

                base_ve.setLastModUID(8);
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
                                "base_ve initialization didn't complete(4).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(4).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(4): \"%s\"\n",
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
                copy_ve = new MatrixVocabElement(base_ve);
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
                        outStream.print("copy constructor didn't complete(4).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(4):" +
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
                            "base_ve.toString() != copy_ve.toString()(4).\n" +
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
                            "base_ve.toDBString() != copy_ve.toDBString()(4).\n" +
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
                    outStream.print("base_ve == copy_ve(4)\n");
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
                                "unexpected return from getFormalArg() (4)");

                    }

                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (4)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() !=
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(4).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(4): %d\n", i);
                    }
                }
            }
        }


        if ( failures == 0 )
        {
            /* Create a nominal matrix vocab element, and load it
             * with a text string formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            golf = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                bravo = new FloatFormalArg(db, "<bravo5>");

                base_ve = new MatrixVocabElement(db, "float5");

                base_ve.setType(MatrixType.FLOAT);

                base_ve.appendFormalArg(bravo);

                base_ve.setLastModUID(10);
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
                                "base_ve initialization didn't complete(5).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(5).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(5): \"%s\"\n",
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
                copy_ve = new MatrixVocabElement(base_ve);
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
                        outStream.print("copy constructor didn't complete(5).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(5):" +
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
                            "base_ve.toString() != copy_ve.toString()(5).\n" +
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
                            "base_ve.toDBString() != copy_ve.toDBString()(5).\n" +
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
                    outStream.print("base_ve == copy_ve(5)\n");
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
                                "unexpected return from getFormalArg() (5)");

                    }

                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;

                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (5)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() !=
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(5).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(5): %d\n", i);
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed a null matrix
         * vocab element.
         */

        base_ve = null;
        copy_ve = null;
        threwSystemErrorException = false;

        try
        {
            copy_ve = new MatrixVocabElement(base_ve);
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
                        "new MatrixVocabElement(null) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(null) " +
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
    } /* MatrixVocabElement::TestCopyConstructor() */


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
            "((MatrixVocabElement: 0 test) " +
             "(system: true) " +
             "(type: MATRIX) " +
             "(varLen: true) " +
             "(fArgList: ((UnTypedFormalArg 0 <a>), " +
                          "(IntFormalArg 0 <b> false " +
                                "-9223372036854775808 " +
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
        MatrixVocabElement ve = null;

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
                ve = new MatrixVocabElement(new ODBCDatabase(), "test");
                ve.setType(MatrixType.MATRIX);
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
    } /* MatrixVocabElement::TestToStringMethods() */


    /**
     * VerifyTypeMisMatchError()
     *
     * Attempt to both append and insert the supplied formal argument
     * in the supplied instance of MatrixVocabElement.  Return the
     * number of failures.  If verbose, also issue a diabnostic message.
     *
     *                                   -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    public static int VerifyTypeMisMatchError(MatrixVocabElement ve,
                                              FormalArgument fArg,
                                              String veTypeString,
                                              String fArgTypeString,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              int testNum)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::VerifyTypeMisMatchError(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;

        if ( ( ve == null ) ||
             ( ! ( ve instanceof MatrixVocabElement ) ) ||
             ( fArg == null ) ||
             ( ! ( fArg instanceof FormalArgument ) ) ||
             ( veTypeString == null ) ||
             ( fArgTypeString == null ) ||
             ( testNum < 0 ) )
        {
            throw new SystemErrorException(mName + "bad param(s) on entry.");
        }

        if ( failures == 0 ) /* try to append the argument -- should fail */
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(fArg);
               methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.printf(
                                "appendFormalArg(new %s()) in an " +
                                "%s matrix returned.\n", fArgTypeString,
                                veTypeString);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendFormalArg(new %s()) in a %s " +
                            "matrix failed to throw a SystemErrorException.\n",
                            fArgTypeString, veTypeString);
                    }
                }
            }
        }


        if ( failures == 0 ) /* try to insert thel argument -- should fail */
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(fArg, 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.printf(
                                "insertFormalArg(new %s()) in an " +
                                "%s matrix returned.\n", fArgTypeString,
                                veTypeString);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertFormalArg(new %s()) in a %s " +
                            "matrix failed to throw a SystemErrorException.\n",
                            fArgTypeString, veTypeString);
                    }
                }
            }
        }

        return failures;

    } /* MatrixVocabElement::VerifyTypeMisMatchError() */

}