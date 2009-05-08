package org.openshapa.db;

import java.io.PrintStream;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the ColPred class.
 *
 * @author cfreeman
 */
public class ColPredTest {

    Database db;
    long floatMveId;
    long intMveId;
    PrintStream outStream;
    boolean verbose;

    public ColPredTest() {
    }

    @Before
    public void setUp() throws SystemErrorException, LogicErrorException {
        db = new ODBCDatabase();
        
        MatrixVocabElement floatMVE = new MatrixVocabElement(db, "floatMVE");
        floatMVE.setType(MatrixVocabElement.MatrixType.FLOAT);
        FloatFormalArg farg = new FloatFormalArg(db);
        floatMVE.appendFormalArg(farg);
        db.vl.addElement(floatMVE);
        floatMveId = floatMVE.getID();

        MatrixVocabElement intMVE = new MatrixVocabElement(db, "intMVE");
        intMVE.setType(MatrixVocabElement.MatrixType.INTEGER);
        IntFormalArg iarg = new IntFormalArg(db);
        intMVE.appendFormalArg(iarg);
        db.vl.addElement(intMVE);
        intMveId = intMVE.getID();

        outStream = System.out;
        verbose = true;
    }

    /**
     * Test of clone method, of class ColPred.
     */
    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = (ColPred) value0.clone();

        assertEquals(value0, value1);
    }

    /**
     * Test of hashCode method, of class ColPred.
     */
    @Test
    public void testHashCode() throws SystemErrorException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = new ColPred(db, floatMveId);
        ColPred value2 = new ColPred(db, intMveId);

        // Hashcode
        assertTrue(value0.hashCode() == value1.hashCode());
        assertTrue(value0.hashCode() != value2.hashCode());
    }

    /**
     * Test of equals method, of class ColPred.
     */
    @Test
    public void testEquals() throws SystemErrorException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = new ColPred(db, floatMveId);
        ColPred value2 = new ColPred(db, floatMveId);
        ColPred value3 = new ColPred(db, intMveId);

        // Reflexive
        assertTrue(value0.equals(value0));

        // Symmetric
        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));

        // Transitive
        assertTrue(value0.equals(value1));
        assertTrue(value0.equals(value2));
        assertTrue(value1.equals(value2));
        // Consistent not tested

        // Null
        assertFalse(value0.equals(null));        

        // Not equals tests
        assertFalse(value0.equals(value3));
    }

    /**
     * expectedResult:  Private enumerated type used to specify the expected
     *      result of a test.
     */
    private enum ExpectedResult { succeed, system_error, return_null };

    // TODO: Must add tests to verify corrct management of undefined data values
    //       and query variables.  A lot of this will be tested in MacSHAPA file
    //       save reload, and query language -- so perhaps I can get away with
    //       holding off for a while.
    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Verify that constructor completes when passed a valid instance
     *         of Database, and returns an instance of ColPred.  Verify that:
     *
     *              colPred.db matches supplied value
     *              colPred.mveID == DBIndex.INVALID_ID
     *              colPred.mveName == ""
     *              colPred.argList == NULL
     *              colPred.varLen == false
     *
     *      b) Verify that constructor fails when passed an invalid db.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element).
     *         Insert the mve in the database, and make note of the id
     *         assigned to the mve.  Construct a ColPred passing a reference
     *         to the database and the id of the mve.  Verify that:
     *
     *              colPred.db matches the suplied value
     *              colPred.mveID matches the supplied value
     *              colPred.mveName matches the name of the pve
     *              pred.argList reflects the column predidate formal argument
     *                  list of the mve
     *              colPred.varLen matches the varLen field of the mve.
     *
     *          Do this with various types of mves (i.e. TEXT, INTEGER, MATRIX,
     *          etc.) and with both a fixed length and a variable length mve's.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element).
     *         Insert the mve in the database, and make note of the id
     *         assigned to the mve.  Construct an argument list with valuse
     *         matching the column predicate formal arg list of the mve.
     *         Construct a ColPred, passing the db, the id of the mve, and the
     *         arg list.  Verify that:
     *
     *              colPred.db matches the suplied value
     *              colPred.mveID matches the supplied value
     *              colPred.mveName matches the name of the pve
     *              colpred.argList reflects both the column predicate formal
     *                  argument list of the mve and the supplied argument list.
     *              colPred.varLen matches the varLen field of the mve.
     *
     *          Do this with various types of mves (i.e. TEXT, INTEGER, MATRIX,
     *          etc.) and with both a fixed length and a variable length mve's.
     *
     *      b) Verify that the constructor fails when passed an invalid db,
     *         an invalid mve id, or an invalid argument list.  Note that
     *         we must test argument lists that are null, too short, too long,
     *         and which contain type mis-matches.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and several mve's (matrix vocab
     *         elements). Insert the mve's in the database, and make note
     *         of the id's assigned to the mve's.  Using these mve's, construct
     *         a selection of column predicates with and without argument lists,
     *         and with and without initializations to arguments.
     *
     *         Now use the copy constructor to make copies of these predicates.
     *         Verify that the copies are correct.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getMveID(), setMveID(), getDB(), getNumArgs(),
     *      getVarLen(), and getMveName() methods perform correctly.
     *
     *      Do this by creating a database and a selection of matrix vocab
     *      elements.  Then create a selection of column predicates, and verify
     *      that the get methods return the expected values.  Then use
     *      setveID() to change the mve ID associated with the column predicates,
     *      and verify that values returned by the get methods have changed
     *      accordingly.
     *
     *      Verify that setMveID() fails when given invalid input.
     *
     *      lookupMatrixVE() is an internal method that has been exercised
     *      already.  Verify that it fails on invalid input.
     *
     * 6) ArgList management:
     *
     *      Verify that argument lists are converted properly when the mveID
     *      is changed.  If salvage is true, must convert values to fit new
     *      formal argument list if possible.
     *
     *      Verify that the getArg() and replaceArg() methods perform as
     *      expected.
     *
     *      Verify that getArg() and replaceArg() methods fail on invalid
     *      input.
     *
     * 7) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 9/10/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        ColPred cp = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        cp = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            cp = new ColPred(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( cp == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( cp == null )
                {
                    outStream.print(
                            "new ColPred(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new ColPred(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("db not initialized correctly.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getMveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of mveID: %ld.\n",
                            cp.getMveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getMveName().compareTo("") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of mveName: \"%s\".\n",
                            cp.getMveName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.argList != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            cp = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cp = new ColPred((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cp != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new ColPred(null) returned.\n");
                    }

                    if ( cp != null )
                    {
                        outStream.print(
                                "new ColPred(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPred(null) failed to throw " +
                                        "a system error exception.\n");
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

    } /* ColPred::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 9/10/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 2 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.
        if ( failures == 0 )
        {
            String float_cp_string =
                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000)";
            String int_cp_string =
                "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String matrix_cp0_string =
                "matrix_mve0(0, 00:00:00:000, 00:00:00:000, " +
                    "0.000000, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String matrix_cp1_string =
                "matrix_mve1(0, 00:00:00:000, 00:00:00:000, " +
                    "<arg1>, <arg2>, <arg3>)";
            String matrix_cp2_string =
                "matrix_mve2(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String nominal_cp_string =
                "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String pred_cp_string =
                "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String text_cp_string =
                "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String float_cp_DBstring =
                "(colPred (id 0) (mveID 1) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 3) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 4) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 5) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 6) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 1) " +
//                    "(mveName float_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 3) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 4) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 5) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(FloatDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 6) " +
//                            "(itsFargType FLOAT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0.0) " +
//                            "(subRange false) " +
//                            "(minVal 0.0) " +
//                            "(maxVal 0.0))))))";
            String int_cp_DBstring =
                "(colPred (id 0) (mveID 7) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 9) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 11) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 7) " +
//                    "(mveName int_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 9) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 10) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 11) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(IntDataValue (id 0) " +
//                            "(itsFargID 12) " +
//                            "(itsFargType INTEGER) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0) " +
//                            "(subRange false) " +
//                            "(minVal 0) " +
//                            "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                "(colPred (id 0) (mveID 13) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 13) " +
//                    "(mveName matrix_mve0) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 21) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 22) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 23) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(FloatDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 24) " +
//                            "(itsFargType FLOAT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0.0) " +
//                            "(subRange false) " +
//                            "(minVal 0.0) " +
//                            "(maxVal 0.0)), " +
//                        "(IntDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 25) " +
//                            "(itsFargType INTEGER) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0) " +
//                            "(subRange false) " +
//                            "(minVal 0) " +
//                            "(maxVal 0)), " +
//                        "(NominalDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 26) " +
//                            "(itsFargType NOMINAL) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false)), " +
//                        "(PredDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 27) " +
//                            "(itsFargType PREDICATE) " +
//                            "(itsCellID 0) " +
//                            "(itsValue ()) " +
//                            "(subRange false)), " +
//                        "(QuoteStringDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 28) " +
//                            "(itsFargType QUOTE_STRING) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false)), " +
//                        "(TimeStampDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 29) " +
//                            "(itsFargType TIME_STAMP) " +
//                            "(itsCellID 0) " +
//                            "(itsValue (60,00:00:00:000)) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 30) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <untyped>) " +
//                            "(subRange false))))))";
            String matrix_cp1_DBstring =
                "(colPred (id 0) (mveID 31) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 35) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 36) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 37) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 31) " +
//                    "(mveName matrix_mve1) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 35) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 36) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue (id 0) " +
//                            "(itsFargID 37) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 38) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg1>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 39) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg2>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 40) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg3>) " +
//                            "(subRange false))))))";
            String matrix_cp2_DBstring =
                "(colPred (id 0) (mveID 41) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 43) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 44) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 45) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 41) " +
//                    "(mveName matrix_mve2) " +
//                    "(varLen true) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 43) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 44) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 45) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 46) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg1>) " +
//                            "(subRange false))))))";
            String nominal_cp_DBstring =
                "(colPred (id 0) (mveID 47) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 49) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 50) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 51) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 52) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 47) " +
//                    "(mveName nominal_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 49) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 50) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 51) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(NominalDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 52) " +
//                            "(itsFargType NOMINAL) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false))))))";
            String pred_cp_DBstring =
                "(colPred (id 0) (mveID 53) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 55) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 56) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 57) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 58) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 53) " +
//                    "(mveName pred_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 55) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 56) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 57) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(PredDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 58) " +
//                            "(itsFargType PREDICATE) " +
//                            "(itsCellID 0) " +
//                            "(itsValue ()) " +
//                            "(subRange false))))))";
            String text_cp_DBstring =
                "(colPred (id 0) (mveID 59) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 61) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 62) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 63) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 64) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 59) " +
//                    "(mveName text_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 61) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 62) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 63) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(TextStringDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 64) " +
//                            "(itsFargType TEXT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp = new ColPred(db, float_mve_ID);
                int_cp = new ColPred(db, int_mve_ID);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID);
                nominal_cp = new ColPred(db, nominal_mve_ID);
                pred_cp = new ColPred(db, pred_mve_ID);
                text_cp = new ColPred(db, text_mve_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf("allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test ColPreds failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "ColPred creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred(null, float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * matrix vocab element ID.
         */

        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred(new ODBCDatabase(), float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print("new ColPred(new ODBCDatabase(), " +
                                    "float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new ColPred(new ODBCDatabase(), " +
                                    "float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new ColPred(new ODBCDatabase(), float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
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

    } /* ColPred::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 9/11/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> float_cp_arg_list1 = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list1 = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list1 = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list1 = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list1 = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list1 = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list1 = null;
        Vector<DataValue> text_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list1 = null;
        Vector<DataValue> quote_string_arg_list = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // column predicates.  Use toString and toDBString to verify that they
        // are initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, " +
                                "pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList ((IntDataValue (id 0) " +
//                                                    "(itsFargID 11) " +
//                                                    "(itsFargType INTEGER) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue 22) " +
//                                                    "(subRange false) " +
//                                                    "(minVal 0) " +
//                                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test column preds failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "col pred creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_c[.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }

            /* Now repeat the above test, only without setting the fargIDs
             * on the entries in the argument list passed to the constructor.
             */
            float_cp_arg_list1 = null;
            float_cp = null;
            int_cp_arg_list1 = null;
            int_cp = null;
            matrix_cp0_arg_list1 = null;
            matrix_cp0 = null;
            matrix_cp1_arg_list1 = null;
            matrix_cp1 = null;
            matrix_cp2_arg_list1 = null;
            matrix_cp2 = null;
            nominal_cp_arg_list1 = null;
            nominal_cp = null;
            pred_cp_arg_list1 = null;
            pred_cp = null;
            text_cp_arg_list1 = null;
            text_cp = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(11);
                float_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list1.add(arg);
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(11.0);
                float_cp_arg_list1.add(arg);
                float_cp = new ColPred(db, float_mve_ID,
                                       float_cp_arg_list1);


                int_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(22);
                int_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(22);
                int_cp_arg_list1.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list1);


                matrix_cp0_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(33);
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list1.add(arg);
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(1.0);
                matrix_cp0_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(2);
                matrix_cp0_arg_list1.add(arg);
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("a_nominal");
                matrix_cp0_arg_list1.add(arg);
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                matrix_cp0_arg_list1.add(arg);
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue("q-string");
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list1.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list1);


                matrix_cp1_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(34);
                matrix_cp1_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list1.add(arg);
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue(" a q string ");
                matrix_cp1_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(88);
                matrix_cp1_arg_list1.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list1);


                matrix_cp2_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(35);
                matrix_cp2_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve2.getFormalArg(0).getFargName());
                matrix_cp2_arg_list1.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list1);


                nominal_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(44);
                nominal_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list1.add(arg);
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("another_nominal");
                nominal_cp_arg_list1.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list1);


                pred_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(55);
                pred_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list1.add(arg);
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                pred_cp_arg_list1.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list1);


                text_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(66);
                text_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list1.add(arg);
                arg = new TextStringDataValue(db);
                ((TextStringDataValue)arg).setItsValue("a text string");
                text_cp_arg_list1.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list1 == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list1 == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list1 == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list1 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list1 == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list1 == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list1 == null ) ||
                 ( text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed(2).\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf(
                                "allocation of float_cp failed(2).\n");
                    }

                    if ( int_cp_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of int_cp_arg_list failed(2).\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf(
                                "allocation of int_cp failed(2).\n");
                    }

                    if ( matrix_cp0_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp0_arg_list failed(2).\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed(2).\n");
                    }

                    if ( matrix_cp1_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp1_arg_list failed(2).\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed(2).\n");
                    }

                    if ( matrix_cp2_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp2_arg_list failed(2).\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed(2).\n");
                    }

                    if ( nominal_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "nominal_cp_arg_list failed(2).\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed(2).\n");
                    }

                    if ( pred_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "pred_cp_arg_list failed(2).\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp failed(2).\n");
                    }

                    if ( text_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "text_cp_arg_list failed(2).\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf(
                                "allocation of text_cp failed(2).\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test col preds " +
                                        "failed to complete(2).");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred creation threw a " +
                                "SystemErrorException(2): %s.\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString()(2): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString()(2): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString()(2): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString()(2): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString()(2): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString()(2): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString()(2): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString()(2): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString()(2): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString()(2): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString()(2): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString()(2): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString()(2): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString()(2): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString()(2): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString()(2): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        failures += Verify3ArgConstructorFailure(null,
                                                 float_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "null",
                                                 "float_mve_ID",
                                                 "float_cp_arg_list");

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */
        failures += Verify3ArgConstructorFailure(new ODBCDatabase(),
                                                 float_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "new ODBCDatabase()",
                                                 "float_mve_ID",
                                                 "float_cp_arg_list");



        // finally, verify that the constructor fails when passed an invalid
        // arg list.  Many possibilities...
        //
        // In the following, we do the obvious and try to construct instances
        // of all the mve's defined above, but using all the wrong arg lists.
        // All these attempts should fail when the farg ID mis-matches are
        // detected.
        //
        // In theory, there is also the possiblility of a type mis-match
        // between the formal argument and a datavalue in the argument list.
        // However, the datavalues should throw a system error if a datavalue
        // is created for a formal argument that doesn't match the type of that
        // formarl argument.
        //
        // Even with this, one could suppose that an datavalue was created,
        // and then the type of the formal argument was changed out from under
        // it.  However, in this case, we should be assigning a new ID to the
        // formal argument, causing a farg ID mismatch failure.
        //
        // Assuming we do our part in the rest of the library, the following
        // tests should be sufficient.
        //
        // Start with a float mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose an int mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose a 7 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "quote_string_arg_list");


        // Now choose a 3 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "quote_string_arg_list");


        // Now choose a 1 argument matrix mve as the target.  Since its only
        // argument is untyped, one would expect few possible failures.
        // However in this case, the farg IDs don't match, so we get the
        // usual failures.
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "quote_string_arg_list");


        // Now choose a nominal mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a predicate mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a text mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "quote_string_arg_list");

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

    } /* ColPred::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     *                                      JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class ColPred accessors                                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        Vector<DataValue> quote_string_arg_list = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // having set up a selection of test mve's, now allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, " +
                            "pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 22) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                "(itsFargID 35) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue  a q string ) " +
//                                "(subRange false)), " +
//                            "(UndefinedDataValue (id 0) " +
//                                "(itsFargID 36) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue <arg2>) " +
//                                "(subRange false)), " +
//                            "(IntDataValue (id 0) " +
//                                "(itsFargID 37) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue 88) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                 arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getCPFormalArg(9).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getCPFormalArg(4).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getCPFormalArg(3).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                            nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }


        // Verify that getDB() works as expected.  There is not much to
        // do here, as the db field is set on creation and never changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getDB() != db ) ||
                 ( int_cp.getDB() != db ) ||
                 ( matrix_cp0.getDB() != db ) ||
                 ( matrix_cp1.getDB() != db ) ||
                 ( matrix_cp2.getDB() != db ) ||
                 ( nominal_cp.getDB() != db ) ||
                 ( pred_cp.getDB() != db ) ||
                 ( text_cp.getDB() != db ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "*_cp.getDB() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getMveID() works as expected.  There is not much to
        // do here either, as the mveID field is set on creation and never
        // changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getMveID() != float_mve_ID ) ||
                 ( int_cp.getMveID() != int_mve_ID ) ||
                 ( matrix_cp0.getMveID() != matrix_mve0_ID ) ||
                 ( matrix_cp1.getMveID() != matrix_mve1_ID ) ||
                 ( matrix_cp2.getMveID() != matrix_mve2_ID ) ||
                 ( nominal_cp.getMveID() != nominal_mve_ID ) ||
                 ( pred_cp.getMveID() != pred_mve_ID ) ||
                 ( text_cp.getMveID() != text_mve_ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getMveID() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getNumArgs() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getNumArgs() != 4 ) ||
                 ( int_cp.getNumArgs() != 4 ) ||
                 ( matrix_cp0.getNumArgs() != 10 ) ||
                 ( matrix_cp1.getNumArgs() != 6 ) ||
                 ( matrix_cp2.getNumArgs() != 4 ) ||
                 ( nominal_cp.getNumArgs() != 4 ) ||
                 ( pred_cp.getNumArgs() != 4 ) ||
                 ( text_cp.getNumArgs() != 4 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getVarLen() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getVarLen() != false ) ||
                 ( int_cp.getVarLen() != false ) ||
                 ( matrix_cp0.getVarLen() != false ) ||
                 ( matrix_cp1.getVarLen() != false ) ||
                 ( matrix_cp2.getVarLen() != true ) ||
                 ( nominal_cp.getVarLen() != false ) ||
                 ( pred_cp.getVarLen() != false ) ||
                 ( text_cp.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // finally, verify that lookupMatrixVE() throws a system error on
        // invalid input.  Start with the valid id that does not refer to a
        // matrix vocab element

        threwSystemErrorException = false;
        completed = false;
        fargID = DBIndex.INVALID_ID;
        mve = null;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

             //   mve = float_cp.lookupMatrixVE(fargID);

                Object obj = PrivateAccessor.invoke(float_cp,
                        "lookupMatrixVE",
                        new Class[]{long.class}, new Object[]{fargID});
                if (obj != null) {
                    mve = (MatrixVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("float_cp.invoke threw unexpectedly");
                failures++;
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (2).\n");
                    }

                    if ( mve != null )
                    {
                        outStream.printf("mve != null (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "float_cp.lookupMatrixVE(fargID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "float_cp.lookupPredicateVE(fargID) " +
                                "failed to thow a system error.\n");
                    }
                }
            }
        }

        // now try an unused ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
             //   mve = int_cp.lookupMatrixVE(500);

                Object obj = PrivateAccessor.invoke(int_cp,
                        "lookupMatrixVE",
                        new Class[]{long.class}, new Object[]{500});
                if (obj != null) {
                    mve = (MatrixVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("int_cp.invoke threw unexpectedly");
                failures++;
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "int_cp.lookupMatrixVE(500) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("int_cp.lookupMatrixVE(500) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, try the invalid ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
             //   mve = matrix_cp0.lookupMatrixVE(DBIndex.INVALID_ID);

                Object obj = PrivateAccessor.invoke(matrix_cp0,
                        "lookupMatrixVE",
                        new Class[]{long.class},
                        new Object[]{DBIndex.INVALID_ID});
                if (obj != null) {
                    mve = (MatrixVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("matrix_cp0.invoke threw unexpectedly");
                failures++;
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (3)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("matrix_cp0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("matrix_cp0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) " +
                                         "failed to thow a system error.\n");
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
    } /* ColPred::TestAccessors() */



    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the arg list management facilities.
     *
     *                                              JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestArgListManagement() throws SystemErrorException {
        String testBanner =
            "Testing class ColPred argument list management                   ";
        String passBanner = "PASSED\n";
        String failBanner  = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long matrix_mve3_ID = DBIndex.INVALID_ID;
        long matrix_mve4_ID = DBIndex.INVALID_ID;
        long matrix_mve5_ID = DBIndex.INVALID_ID;
        long matrix_mve6_ID = DBIndex.INVALID_ID;
        long matrix_mve7_ID = DBIndex.INVALID_ID;
        long matrix_mve8_ID = DBIndex.INVALID_ID;
        long matrix_mve9_ID = DBIndex.INVALID_ID;
        long matrix_mve10_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement matrix_mve3 = null;
        MatrixVocabElement matrix_mve4 = null;
        MatrixVocabElement matrix_mve5 = null;
        MatrixVocabElement matrix_mve6 = null;
        MatrixVocabElement matrix_mve7 = null;
        MatrixVocabElement matrix_mve8 = null;
        MatrixVocabElement matrix_mve9 = null;
        MatrixVocabElement matrix_mve10 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue goodArg = null;
        DataValue badArg = null;
        FloatDataValue floatArg = null;
        IntDataValue intArg = null;
        NominalDataValue nomArg = null;
        PredDataValue predArg = null;
        QuoteStringDataValue qsArg = null;
        TextStringDataValue textArg = null;
        TimeStampDataValue tsArg = null;
        UndefinedDataValue undefArg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp16_arg_list = null;
        Vector<DataValue> matrix_cp17_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred float_cp1 = null;
        ColPred int_cp0 = null;
        ColPred int_cp1 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred matrix_cp3 = null;
        ColPred matrix_cp4 = null;
        ColPred matrix_cp5 = null;
        ColPred matrix_cp6 = null;
        ColPred matrix_cp7 = null;
        ColPred matrix_cp8 = null;
        ColPred matrix_cp9 = null;
        ColPred matrix_cp10 = null;
        ColPred matrix_cp11 = null;
        ColPred matrix_cp12 = null;
        ColPred matrix_cp13 = null;
        ColPred matrix_cp14 = null;
        ColPred matrix_cp15 = null;
        ColPred matrix_cp16 = null;
        ColPred matrix_cp17 = null;
        ColPred nominal_cp0 = null;
        ColPred nominal_cp1 = null;
        ColPred pred_cp0 = null;
        ColPred pred_cp1 = null;
        ColPred text_cp0 = null;
        ColPred text_cp1 = null;
        ColPred m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve1.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve1.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve1.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve1.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve1.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve2.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve2.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve2.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve2.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve2.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            matrix_mve3 = new MatrixVocabElement(db, "matrix_mve3");
            matrix_mve3.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve3.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve3.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve3.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve3.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve3.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve3.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve3.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve3.appendFormalArg(farg);
            db.vl.addElement(matrix_mve3);
            matrix_mve3_ID = matrix_mve3.getID();

            matrix_mve4 = new MatrixVocabElement(db, "matrix_mve4");
            matrix_mve4.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve4.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve4.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve4.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve4.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve4.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve4.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve4.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve4.appendFormalArg(farg);
            db.vl.addElement(matrix_mve4);
            matrix_mve4_ID = matrix_mve4.getID();

            matrix_mve5 = new MatrixVocabElement(db, "matrix_mve5");
            matrix_mve5.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve5.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve5.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve5.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve5.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve5.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve5.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve5.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve5.appendFormalArg(farg);
            db.vl.addElement(matrix_mve5);
            matrix_mve5_ID = matrix_mve5.getID();

            matrix_mve6 = new MatrixVocabElement(db, "matrix_mve6");
            matrix_mve6.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve6.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve6.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve6.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve6.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve6.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve6.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve6.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve6.appendFormalArg(farg);
            db.vl.addElement(matrix_mve6);
            matrix_mve6_ID = matrix_mve6.getID();

            matrix_mve7 = new MatrixVocabElement(db, "matrix_mve7");
            matrix_mve7.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve7.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve7.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve7.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve7.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve7.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve7.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve7.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve7.appendFormalArg(farg);
            db.vl.addElement(matrix_mve7);
            matrix_mve7_ID = matrix_mve7.getID();

            matrix_mve8 = new MatrixVocabElement(db, "matrix_mve8");
            matrix_mve8.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve8.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve8.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve8.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve8.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve8.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve8.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve8.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve8.appendFormalArg(farg);
            db.vl.addElement(matrix_mve8);
            matrix_mve8_ID = matrix_mve8.getID();


            matrix_mve9 = new MatrixVocabElement(db, "matrix_mve9");
            matrix_mve9.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve9.appendFormalArg(farg);
            db.vl.addElement(matrix_mve9);
            matrix_mve9_ID = matrix_mve9.getID();

            matrix_mve10 = new MatrixVocabElement(db, "matrix_mve10");
            matrix_mve10.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve10.appendFormalArg(farg);
            matrix_mve10.setVarLen(true);
            db.vl.addElement(matrix_mve10);
            matrix_mve10_ID = matrix_mve10.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 8 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 8 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve3 == null ) ||
             ( matrix_mve3.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve3.getNumFormalArgs() != 8 ) ||
             ( matrix_mve3_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve4 == null ) ||
             ( matrix_mve4.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve4.getNumFormalArgs() != 8 ) ||
             ( matrix_mve4_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve5 == null ) ||
             ( matrix_mve5.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve5.getNumFormalArgs() != 8 ) ||
             ( matrix_mve5_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve6 == null ) ||
             ( matrix_mve6.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve6.getNumFormalArgs() != 8 ) ||
             ( matrix_mve6_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve7 == null ) ||
             ( matrix_mve7.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve7.getNumFormalArgs() != 8 ) ||
             ( matrix_mve7_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve8 == null ) ||
             ( matrix_mve8.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve8.getNumFormalArgs() != 8 ) ||
             ( matrix_mve8_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve9 == null ) ||
             ( matrix_mve9.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve9.getNumFormalArgs() != 3 ) ||
             ( matrix_mve9_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve10 == null ) ||
             ( matrix_mve10.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve10.getNumFormalArgs() != 1 ) ||
             ( matrix_mve10_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
                }


                if ( matrix_mve3 == null )
                {
                    outStream.print("creation of matrix_mve3 failed.\n");
                }
                else if ( matrix_mve3.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve3.getType().\n");
                }
                else if ( matrix_mve3.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve3.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve3.getNumFormalArgs());
                }

                if ( matrix_mve3_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve3_ID == INVALID_ID.\n");
                }


                if ( matrix_mve4 == null )
                {
                    outStream.print("creation of matrix_mve4 failed.\n");
                }
                else if ( matrix_mve4.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve4.getType().\n");
                }
                else if ( matrix_mve4.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve4.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve4.getNumFormalArgs());
                }

                if ( matrix_mve4_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve4_ID == INVALID_ID.\n");
                }


                if ( matrix_mve5 == null )
                {
                    outStream.print("creation of matrix_mve5 failed.\n");
                }
                else if ( matrix_mve5.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve5.getType().\n");
                }
                else if ( matrix_mve5.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve5.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve5.getNumFormalArgs());
                }

                if ( matrix_mve5_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve5_ID == INVALID_ID.\n");
                }


                if ( matrix_mve6 == null )
                {
                    outStream.print("creation of matrix_mve6 failed.\n");
                }
                else if ( matrix_mve6.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve6.getType().\n");
                }
                else if ( matrix_mve6.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve6.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve6.getNumFormalArgs());
                }

                if ( matrix_mve6_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve6_ID == INVALID_ID.\n");
                }


                if ( matrix_mve7 == null )
                {
                    outStream.print("creation of matrix_mve7 failed.\n");
                }
                else if ( matrix_mve7.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve7.getType().\n");
                }
                else if ( matrix_mve7.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve7.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve7.getNumFormalArgs());
                }

                if ( matrix_mve7_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve7_ID == INVALID_ID.\n");
                }


                if ( matrix_mve8 == null )
                {
                    outStream.print("creation of matrix_mve8 failed.\n");
                }
                else if ( matrix_mve8.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve8.getType().\n");
                }
                else if ( matrix_mve8.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve8.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve8.getNumFormalArgs());
                }

                if ( matrix_mve8_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve8_ID == INVALID_ID.\n");
                }


                if ( matrix_mve9 == null )
                {
                    outStream.print("creation of matrix_mve9 failed.\n");
                }
                else if ( matrix_mve9.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve9.getType().\n");
                }
                else if ( matrix_mve9.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve9.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve9.getNumFormalArgs());
                }

                if ( matrix_mve9_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve9_ID == INVALID_ID.\n");
                }


                if ( matrix_mve10 == null )
                {
                    outStream.print("creation of matrix_mve10 failed.\n");
                }
                else if ( matrix_mve10.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve10.getType().\n");
                }
                else if ( matrix_mve10.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve10.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve10.getNumFormalArgs());
                }

                if ( matrix_mve10_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve10 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // Start with a set of tests to verify that an argument list is
        // converted properly when the mveID of an instance of ColPred
        // is changed.
        //
        // Start by creating the necessary set of test instances of ColPred.
        // In passing, also create instances of ColPred for later use.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, " +
                                "a_nominal, pve0(<arg>), \"q-string\", " +
                                "00:00:01:000, <untyped>, " +
                                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
//                                "a_nominal, pve0(<arg>), \"q-string\", " +
//                                "00:00:01:000, <untyped>, " +
//                                "float_mve(<ord>, <onset>, <offset>, 0.0))";
            String matrix_cp16_string =
                    "matrix_mve9(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp17_string =
                    "matrix_mve10(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 6) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 7) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 12) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 14) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 15) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 16) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 17) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 12) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 15) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 16) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 17) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 12) " +
//                            "(varLen false) " +
//                            "(argList ((IntDataValue (id 0) " +
//                                                    "(itsFargID 13) " +
//                                                    "(itsFargType INTEGER) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue 22) " +
//                                                    "(subRange false) " +
//                                                    "(minVal 0) " +
//                                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 27) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 30) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 31) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 32) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 33) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 34) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 35) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 36) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 37) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 27) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 30) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 31) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 32) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 33) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 34) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 35) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 36) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 37) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 27) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 29) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 30) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 31) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 32) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 33) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 34) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 35) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 18) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 24) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 25) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp16_DBstring =
                    "(colPred (id 0) (mveID 198) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 202) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 203) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 204) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 205) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 206) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 207) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 198) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 202) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 203) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 204) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 205) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 206) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 207) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 36) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 44) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 45) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 36) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 38) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 39) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp17_DBstring =
                    "(colPred (id 0) (mveID 208) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 210) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 211) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 212) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 213) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 208) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 210) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 211) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 212) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 213) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 46) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 50) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 51) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 46) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 47) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 214) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 216) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 217) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 218) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 219) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 214) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 216) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 217) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 218) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 219) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 52) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 55) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 56) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 57) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 52) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 53) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 220) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 222) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 223) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 224) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 225) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 220) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 222) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 223) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 224) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 225) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 58) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 61) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 62) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 63) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 58) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 59) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                "(colPred (id 0) (mveID 226) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 228) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 229) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 230) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 231) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                "(colPred (id 0) (mveID 226) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 228) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 229) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 230) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 231) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                "(colPred (id 0) (mveID 64) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 67) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 68) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 69) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 64) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 65) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);
                float_cp1 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp0 = new ColPred(db, int_mve_ID, int_cp_arg_list);
                int_cp1 = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp1 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp2 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp3 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp4 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp5 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp6 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp7 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp8 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp9 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp10 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp11 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp12 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp13 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp14 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp15 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);


                matrix_cp16_arg_list = new Vector<DataValue>();
                fargID = matrix_mve9.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve9.getFormalArg(1).getFargName());
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp16_arg_list.add(arg);
                matrix_cp16 = new ColPred(db, matrix_mve9_ID,
                                         matrix_cp16_arg_list);


                matrix_cp17_arg_list = new Vector<DataValue>();
                fargID = matrix_mve10.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve10.getFormalArg(0).getFargName());
                matrix_cp17_arg_list.add(arg);
                matrix_cp17 = new ColPred(db, matrix_mve10_ID,
                                          matrix_cp17_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);
                nominal_cp1 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp0 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);
                pred_cp1 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp0 = new ColPred(db, text_mve_ID, text_cp_arg_list);
                text_cp1 = new ColPred(db, text_mve_ID, text_cp_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( float_cp1 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( int_cp1 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( matrix_cp3 == null ) ||
                 ( matrix_cp4 == null ) ||
                 ( matrix_cp5 == null ) ||
                 ( matrix_cp6 == null ) ||
                 ( matrix_cp7 == null ) ||
                 ( matrix_cp8 == null ) ||
                 ( matrix_cp9 == null ) ||
                 ( matrix_cp10 == null ) ||
                 ( matrix_cp11 == null ) ||
                 ( matrix_cp12 == null ) ||
                 ( matrix_cp13 == null ) ||
                 ( matrix_cp14 == null ) ||
                 ( matrix_cp15 == null ) ||
                 ( matrix_cp16_arg_list == null ) ||
                 ( matrix_cp16 == null ) ||
                 ( matrix_cp17_arg_list == null ) ||
                 ( matrix_cp17 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( nominal_cp1 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( pred_cp1 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
                 ( text_cp1 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( ( float_cp0 == null ) || ( float_cp1 == null ) )
                    {
                        outStream.printf("allocation of float_cp? failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( ( int_cp0 == null ) || ( int_cp1 == null ) )
                    {
                        outStream.printf("allocation of int_cp? failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( ( matrix_cp0 == null ) ||
                         ( matrix_cp1 == null ) ||
                         ( matrix_cp2 == null ) ||
                         ( matrix_cp3 == null ) ||
                         ( matrix_cp4 == null ) ||
                         ( matrix_cp5 == null ) ||
                         ( matrix_cp6 == null ) ||
                         ( matrix_cp7 == null ) ||
                         ( matrix_cp8 == null ) ||
                         ( matrix_cp9 == null ) ||
                         ( matrix_cp10 == null ) ||
                         ( matrix_cp11 == null ) ||
                         ( matrix_cp12 == null ) ||
                         ( matrix_cp13 == null ) ||
                         ( matrix_cp14 == null ) ||
                         ( matrix_cp15 == null ) )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0-15 failed.\n");
                    }

                    if ( matrix_cp16_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp16_arg_list failed.\n");
                    }

                    if ( matrix_cp16 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp16 failed.\n");
                    }

                    if ( matrix_cp17_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp17_arg_list failed.\n");
                    }

                    if ( matrix_cp17 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp17 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( ( nominal_cp0 == null ) || ( nominal_cp1 == null ) )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( ( pred_cp0 == null ) || ( pred_cp1 == null ) )
                    {
                        outStream.printf("allocation of pred_cp? failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( ( text_cp0 == null ) || ( text_cp1 == null ) )
                    {
                        outStream.printf("allocation of text_cp? failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test matricies failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp0.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( float_cp1.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp0.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( int_cp1.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp3.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp4.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp5.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp6.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp7.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp8.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp9.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp10.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp11.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp12.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp13.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp14.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp15.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp16.toString().
                        compareTo(matrix_cp16_string) != 0 ) ||
                      ( matrix_cp17.toString().
                        compareTo(matrix_cp17_string) != 0 ) ||
                      ( nominal_cp0.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( nominal_cp1.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp0.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( pred_cp1.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp0.toString().
                        compareTo(text_cp_string) != 0 ) ||
                      ( text_cp1.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( float_cp0.toString().
                           compareTo(float_cp_string) != 0 ) ||
                         ( float_cp1.toString().
                           compareTo(float_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected float_cp?.toString(): %s\n",
                                float_cp0.toString());
                    }

                    if ( ( int_cp0.toString().
                           compareTo(int_cp_string) != 0 ) ||
                         ( int_cp1.toString().
                           compareTo(int_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected int_cp?.toString(): %s\n",
                                int_cp0.toString());
                    }

                    if ( ( matrix_cp0.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp1.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp2.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp3.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp4.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp5.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp6.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp7.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp8.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp9.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp10.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp11.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp12.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp13.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp14.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp15.toString().
                            compareTo(matrix_cp0_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0-15.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp16.toString().
                         compareTo(matrix_cp16_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp16.toString(): %s\n",
                                matrix_cp16.toString());
                    }

                    if ( matrix_cp17.toString().
                         compareTo(matrix_cp17_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp17.toString(): %s\n",
                                matrix_cp17.toString());
                    }

                    if ( ( nominal_cp0.toString().
                           compareTo(nominal_cp_string) != 0 ) ||
                         ( nominal_cp1.toString().
                           compareTo(nominal_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected nominal_cp?.toString(): %s\n",
                                nominal_cp0.toString());
                    }

                    if ( ( pred_cp0.toString().
                           compareTo(pred_cp_string) != 0 ) ||
                         ( pred_cp1.toString().
                           compareTo(pred_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected pred_cp?.toString(): %s\n",
                                pred_cp0.toString());
                    }

                    if ( ( text_cp0.toString().
                           compareTo(text_cp_string) != 0 ) ||
                         ( text_cp1.toString().
                           compareTo(text_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected text_cp?.toString(): %s\n",
                                text_cp0.toString());
                    }
                }
            }
            else if ( ( float_cp0.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( float_cp1.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp0.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( int_cp1.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp3.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp4.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp5.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp6.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp7.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp8.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp9.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp10.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp11.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp12.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp13.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp14.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp15.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp16.toDBString().
                        compareTo(matrix_cp16_DBstring) != 0 ) ||
                      ( matrix_cp17.toDBString().
                        compareTo(matrix_cp17_DBstring) != 0 ) ||
                      ( nominal_cp0.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( nominal_cp1.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp0.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( pred_cp1.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp0.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) ||
                      ( text_cp1.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( float_cp0.toDBString().
                           compareTo(float_cp_DBstring) != 0 ) ||
                         ( float_cp1.toDBString().
                           compareTo(float_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected float_cp?.toDBString(): %s\n",
                                float_cp0.toDBString());
                    }

                    if ( ( int_cp0.toDBString().
                           compareTo(int_cp_DBstring) != 0 ) ||
                         ( int_cp1.toDBString().
                           compareTo(int_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp0.toDBString());
                    }

                    if ( ( matrix_cp0.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp1.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp2.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp3.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp4.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp5.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp6.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp7.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp8.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp9.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp10.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp11.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp12.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp13.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp14.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp15.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0-15.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp16.toDBString().
                         compareTo(matrix_cp16_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp16.toDBString(): %s\n",
                                matrix_cp16.toDBString());
                    }

                    if ( matrix_cp17.toDBString().
                         compareTo(matrix_cp17_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp17.toDBString(): %s\n",
                                matrix_cp17.toDBString());
                    }

                    if ( ( nominal_cp0.toDBString().
                           compareTo(nominal_cp_DBstring) != 0 ) ||
                         ( nominal_cp1.toDBString().
                           compareTo(nominal_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected nominal_cp?.toDBString(): %s\n",
                                nominal_cp0.toDBString());
                    }

                    if ( ( pred_cp0.toDBString().
                           compareTo(pred_cp_DBstring) != 0 ) ||
                         ( pred_cp1.toDBString().
                           compareTo(pred_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected pred_cp?.toDBString(): %s\n",
                                pred_cp0.toDBString());
                    }

                    if ( ( text_cp0.toDBString().
                           compareTo(text_cp_DBstring) != 0 ) ||
                         ( text_cp1.toDBString().
                           compareTo(text_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected text_cp?.toDBString(): %s\n",
                                text_cp0.toDBString());
                    }
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString0 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, " +
                                "a_nominal, pve0(<arg>), \"q-string\", " +
                                "00:00:01:000, <untyped>, " +
                                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
//                                "a_nominal, pve0(<arg>), \"q-string\", " +
//                                "00:00:01:000, <untyped>, " +
//                                "float_mve(<ord>, <onset>, <offset>, 0.0))";
            // untyped, float, int, nominal, pred, q-string, timestamp
            String testString1 =
                    "matrix_mve1(33, 00:00:00:033, 00:00:33:000, 1, , (), \"\", 00:00:00:000, 00:00:01:000, (), 0.000000)";
//                    "pve5(1.0, 2.0, 0, , (), \"\", 00:00:00:000)";
            // timestamp, untyped, float, int, nominal, pred, q-string
            String testString2 =
                    "matrix_mve2(33, 00:00:00:033, 00:00:33:000, , (), \"a_nominal\", 00:00:00:000, \"q-string\", (), 0.000000, 0)";
//                    "pve6(00:00:00:000, 2, 0.0, 0, q-string, (), \"\")";
            // q-string, timestamp, untyped, float, int, nominal, pred
            String testString3 =
                    "matrix_mve3(33, 00:00:00:033, 00:00:33:000, (), \"\", 00:00:00:000, pve0(<arg>), (), 0.000000, 0, )";
//                    "pve7(\"\", 00:00:00:002, a_nominal, 0.0, 0, , ())";
            // pred, q-string, timestamp, untyped, float, int, nominal
            String testString4 =
                    "matrix_mve4(33, 00:00:00:033, 00:00:33:000, \"\", 00:00:00:002, a_nominal, (), 0.000000, 0, , ())";
//                    "pve8((), \"\", 00:00:00:000, pve0(<arg1>, <arg2>), 0.0, 0, )";
            // nominal, pred, q-string, timestamp, untyped, float, int
            String testString5 =
                    "matrix_mve5(33, 00:00:00:033, 00:00:33:000, 00:00:00:000, 2, (), 0.000000, 0, , (), \"\")";
//                    "pve9(, (), \"a_nominal\", 00:00:00:000, \"q-string\", 0.0, 0)";
            // int, nominal, pred, q-string, timestamp, untyped, float
            String testString6 =
                    "matrix_mve6(33, 00:00:00:033, 00:00:33:000, 1.000000, (), 0.000000, 0, q-string, (), \"\", 00:00:00:000)";
//                    "pve10(1, , (), \"\", 00:00:00:000, 00:00:00:000, 0.0)";
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString7 =
                    "matrix_mve7(33, 00:00:00:033, 00:00:33:000, (), 2.000000, 0, , (), \"\", 00:00:00:000, float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                    "matrix_mve7(33, 00:00:00:033, 00:00:33:000, (), 2.0, 0, , (), \"\", 00:00:00:000, float_mve(<ord>, <onset>, <offset>, 0.0))";
//                    "pve11(1.0, 2, a_nominal, pve0(<arg1>, <arg2>), " +
//                          "\"q-string\", 00:00:00:000, <untyped>)";
            String testString8 =
                    "matrix_mve8(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                    "matrix_mve8(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
//                    "pve3(1.0)";
            String testString9 = "matrix_mve9(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, a_nominal)";
            String testString10 = "matrix_mve10(33, 00:00:00:033, 00:00:33:000, 1.000000)";
            String testString11 = "float_mve(33, 00:00:00:033, 00:00:33:000, 1.000000)";
            String testString12 = "int_mve(33, 00:00:00:033, 00:00:33:000, 1)";
            String testString13 = "nominal_mve(33, 00:00:00:033, 00:00:33:000, )";
            String testString14 = "pred_mve(33, 00:00:00:033, 00:00:33:000, ())";
            String testString15 = "text_mve(33, 00:00:00:033, 00:00:33:000, )";
            String testString16 =
                    "matrix_mve9(11, 00:00:00:011, 00:00:11:000, 11.000000, <arg2>, <arg3>)";
            String testString17 =
                    "matrix_mve9(22, 00:00:00:022, 00:00:22:000, 22, <arg2>, <arg3>)";
            String testString18 =
                    "matrix_mve9(44, 00:00:00:044, 00:00:44:000, another_nominal, <arg2>, <arg3>)";
            String testString19 =
                    "matrix_mve9(55, 00:00:00:055, 00:00:55:000, pve0(<arg>), <arg2>, <arg3>)";
            String testString20 =
                    "matrix_mve9(66, 00:00:01:006, 00:01:06:000, \"a text string\", <arg2>, <arg3>)";

            try
            {
                matrix_cp0.setMveID(matrix_mve0_ID, true);
                matrix_cp1.setMveID(matrix_mve1_ID, true);
                matrix_cp2.setMveID(matrix_mve2_ID, true);
                matrix_cp3.setMveID(matrix_mve3_ID, true);
                matrix_cp4.setMveID(matrix_mve4_ID, true);
                matrix_cp5.setMveID(matrix_mve5_ID, true);
                matrix_cp6.setMveID(matrix_mve6_ID, true);
                matrix_cp7.setMveID(matrix_mve7_ID, true);
                matrix_cp8.setMveID(matrix_mve8_ID, true);

                matrix_cp9.setMveID(matrix_mve9_ID, true);
                matrix_cp10.setMveID(matrix_mve10_ID, true);
                matrix_cp11.setMveID(float_mve_ID, true);
                matrix_cp12.setMveID(int_mve_ID, true);
                matrix_cp13.setMveID(nominal_mve_ID, true);
                matrix_cp14.setMveID(pred_mve_ID, true);
                matrix_cp15.setMveID(text_mve_ID, true);

                float_cp1.setMveID(matrix_mve9_ID, true);
                int_cp1.setMveID(matrix_mve9_ID, true);
                nominal_cp1.setMveID(matrix_mve9_ID, true);
                pred_cp1.setMveID(matrix_mve9_ID, true);
                text_cp1.setMveID(matrix_mve9_ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test of setMveID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test of setMveID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            } else if ( ( matrix_cp0.toString().compareTo(testString0) != 0 ) ||
                        ( matrix_cp1.toString().compareTo(testString1) != 0 ) ||
                        ( matrix_cp2.toString().compareTo(testString2) != 0 ) ||
                        ( matrix_cp3.toString().compareTo(testString3) != 0 ) ||
                        ( matrix_cp4.toString().compareTo(testString4) != 0 ) ||
                        ( matrix_cp5.toString().compareTo(testString5) != 0 ) ||
                        ( matrix_cp6.toString().compareTo(testString6) != 0 ) ||
                        ( matrix_cp7.toString().compareTo(testString7) != 0 ) ||
                        ( matrix_cp8.toString().compareTo(testString8) != 0 ) ||
                        ( matrix_cp9.toString().compareTo(testString9) != 0 ) ||
                        ( matrix_cp10.toString().compareTo(testString10) != 0 ) ||
                        ( matrix_cp11.toString().compareTo(testString11) != 0 ) ||
                        ( matrix_cp12.toString().compareTo(testString12) != 0 ) ||
                        ( matrix_cp13.toString().compareTo(testString13) != 0 ) ||
                        ( matrix_cp14.toString().compareTo(testString14) != 0 ) ||
                        ( matrix_cp15.toString().compareTo(testString15) != 0 ) ||
                        ( float_cp1.toString().compareTo(testString16) != 0 ) ||
                        ( int_cp1.toString().compareTo(testString17) != 0 ) ||
                        ( nominal_cp1.toString().compareTo(testString18) != 0 ) ||
                        ( pred_cp1.toString().compareTo(testString19) != 0 ) ||
                        ( text_cp1.toString().compareTo(testString20) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( matrix_cp0.toString().compareTo(testString0) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp0.toString(1): \"%s\".\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp1.toString(1): \"%s\".\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp2.toString(1): \"%s\".\n",
                                matrix_cp2.toString());
                    }

                    if ( matrix_cp3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp3.toString(1): \"%s\".\n",
                                matrix_cp3.toString());
                    }

                    if ( matrix_cp4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp4.toString(1): \"%s\".\n",
                                matrix_cp4.toString());
                    }

                    if ( matrix_cp5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp5.toString(1): \"%s\".\n",
                                matrix_cp5.toString());
                    }

                    if ( matrix_cp6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp6.toString(1): \"%s\".\n",
                                matrix_cp6.toString());
                    }

                    if ( matrix_cp7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matix_cp7.toString(1): \"%s\".\n",
                                matrix_cp7.toString());
                    }

                    if ( matrix_cp8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp8.toString(1): \"%s\".\n",
                                matrix_cp8.toString());
                    }

                    if ( matrix_cp9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp9.toString(1): \"%s\".\n",
                                matrix_cp9.toString());
                    }

                    if ( matrix_cp10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp10.toString(1): \"%s\".\n",
                                matrix_cp10.toString());
                    }


                    if ( matrix_cp11.toString().compareTo(testString11) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp11.toString(1): \"%s\".\n",
                                matrix_cp11.toString());
                    }


                    if ( matrix_cp12.toString().compareTo(testString12) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp12.toString(1): \"%s\".\n",
                                matrix_cp12.toString());
                    }


                    if ( matrix_cp13.toString().compareTo(testString13) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp13.toString(1): \"%s\".\n",
                                matrix_cp13.toString());
                    }


                    if ( matrix_cp14.toString().compareTo(testString14) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp14.toString(1): \"%s\".\n",
                                matrix_cp14.toString());
                    }


                    if ( matrix_cp15.toString().compareTo(testString15) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp15.toString(1): \"%s\".\n",
                                matrix_cp15.toString());
                    }

                    if ( float_cp1.toString().compareTo(testString16) != 0 )
                    {
                        outStream.printf(
                                "Unexpected float_cp1.toString(1): \"%s\".\n",
                                float_cp1.toString());
                    }

                    if ( int_cp1.toString().compareTo(testString17) != 0 )
                    {
                        outStream.printf(
                                "Unexpected int_cp1.toString(1): \"%s\".\n",
                                int_cp1.toString());
                    }

                    if ( nominal_cp1.toString().compareTo(testString18) != 0 )
                    {
                        outStream.printf(
                                "Unexpected nominal_cp1.toString(1): \"%s\".\n",
                                nominal_cp1.toString());
                    }

                    if ( pred_cp1.toString().compareTo(testString19) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred_cp1.toString(1): \"%s\".\n",
                                pred_cp1.toString());
                    }

                    if ( text_cp1.toString().compareTo(testString20) != 0 )
                    {
                        outStream.printf(
                                "Unexpected text_cp1.toString(1): \"%s\".\n",
                                text_cp1.toString());
                    }
                }
            }
        }


        // set matrix_cp1-8 to a variety of mve id's -- all with salvage
        // set to FALSE.  Should get empty column predicates of type congruent
        // with the mve id supplied.  Don't touch matrix_cp0, as we will need
        // it later for other tests.
        //
        // set matrix_cp9-15 back to the original mve id -- some data
        // should be lost, but not all in all cases.
        //
        // set float_cp1, int_cp1, nominal_cp1, pred_cp1, and text_cp1 back
        // to their original mve IDs.  All should be as it was.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString1 =
                    "matrix_mve9(0, 00:00:00:000, 00:00:00:000, <arg1>, <arg2>, <arg3>)";
            String testString2 =
                    "matrix_mve10(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String testString3 =
                    "float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000)";
            String testString4 =
                    "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String testString5 =
                    "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String testString6 =
                    "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String testString7 =
                    "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String testString8 =
                    "matrix_mve8(0, 00:00:00:000, 00:00:00:000, 0.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString9 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, a_nominal, (), \"\", 00:00:00:000, <untyped>, ())";
            String testString10 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString11 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString12 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString13 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString14 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString15 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.000000, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString16 =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String testString17 =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String testString18 =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String testString19 =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String testString20 =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";

            try
            {
                matrix_cp1.setMveID(matrix_mve9_ID, false);
                matrix_cp2.setMveID(matrix_mve10_ID, false);
                matrix_cp3.setMveID(float_mve_ID, false);
                matrix_cp4.setMveID(int_mve_ID, false);
                matrix_cp5.setMveID(nominal_mve_ID, false);
                matrix_cp6.setMveID(pred_mve_ID, false);
                matrix_cp7.setMveID(text_mve_ID, false);
                matrix_cp8.setMveID(matrix_mve8_ID, false);

                matrix_cp9.setMveID(matrix_mve0_ID, true);
                matrix_cp10.setMveID(matrix_mve0_ID, true);
                matrix_cp11.setMveID(matrix_mve0_ID, true);
                matrix_cp12.setMveID(matrix_mve0_ID, true);
                matrix_cp13.setMveID(matrix_mve0_ID, true);
                matrix_cp14.setMveID(matrix_mve0_ID, true);
                matrix_cp15.setMveID(matrix_mve0_ID, true);

                float_cp1.setMveID(float_mve_ID, true);
                int_cp1.setMveID(int_mve_ID, true);
                nominal_cp1.setMveID(nominal_mve_ID, true);
                pred_cp1.setMveID(pred_mve_ID, true);
                text_cp1.setMveID(text_mve_ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test 2 of setMveID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test 2 of setMveID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            } else if ( ( matrix_cp1.toString().compareTo(testString1) != 0 ) ||
                        ( matrix_cp2.toString().compareTo(testString2) != 0 ) ||
                        ( matrix_cp3.toString().compareTo(testString3) != 0 ) ||
                        ( matrix_cp4.toString().compareTo(testString4) != 0 ) ||
                        ( matrix_cp5.toString().compareTo(testString5) != 0 ) ||
                        ( matrix_cp6.toString().compareTo(testString6) != 0 ) ||
                        ( matrix_cp7.toString().compareTo(testString7) != 0 ) ||
                        ( matrix_cp8.toString().compareTo(testString8) != 0 ) ||
                        ( matrix_cp9.toString().compareTo(testString9) != 0 ) ||
                        ( matrix_cp10.toString().compareTo(testString10) != 0 ) ||
                        ( matrix_cp11.toString().compareTo(testString11) != 0 ) ||
                        ( matrix_cp12.toString().compareTo(testString12) != 0 ) ||
                        ( matrix_cp13.toString().compareTo(testString13) != 0 ) ||
                        ( matrix_cp14.toString().compareTo(testString14) != 0 ) ||
                        ( matrix_cp15.toString().compareTo(testString15) != 0 ) ||
                        ( float_cp1.toString().compareTo(testString16) != 0 ) ||
                        ( int_cp1.toString().compareTo(testString17) != 0 ) ||
                        ( nominal_cp1.toString().compareTo(testString18) != 0 ) ||
                        ( pred_cp1.toString().compareTo(testString19) != 0 ) ||
                        ( text_cp1.toString().compareTo(testString20) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( matrix_cp1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp1.toString(2): \"%s\".\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp2.toString(2): \"%s\".\n",
                                matrix_cp2.toString());
                    }

                    if ( matrix_cp3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp3.toString(1): \"%s\".\n",
                                matrix_cp3.toString());
                    }

                    if ( matrix_cp4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp4.toString(2): \"%s\".\n",
                                matrix_cp4.toString());
                    }

                    if ( matrix_cp5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp5.toString(2): \"%s\".\n",
                                matrix_cp5.toString());
                    }

                    if ( matrix_cp6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp6.toString(2): \"%s\".\n",
                                matrix_cp6.toString());
                    }

                    if ( matrix_cp7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matix_cp7.toString(2): \"%s\".\n",
                                matrix_cp7.toString());
                    }

                    if ( matrix_cp8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp8.toString(2): \"%s\".\n",
                                matrix_cp8.toString());
                    }

                    if ( matrix_cp9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp9.toString(2): \"%s\".\n",
                                matrix_cp9.toString());
                    }

                    if ( matrix_cp10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp10.toString(2): \"%s\".\n",
                                matrix_cp10.toString());
                    }


                    if ( matrix_cp11.toString().compareTo(testString11) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp11.toString(2): \"%s\".\n",
                                matrix_cp11.toString());
                    }


                    if ( matrix_cp12.toString().compareTo(testString12) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp12.toString(2): \"%s\".\n",
                                matrix_cp12.toString());
                    }


                    if ( matrix_cp13.toString().compareTo(testString13) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp13.toString(2): \"%s\".\n",
                                matrix_cp13.toString());
                    }


                    if ( matrix_cp14.toString().compareTo(testString14) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp14.toString(2): \"%s\".\n",
                                matrix_cp14.toString());
                    }


                    if ( matrix_cp15.toString().compareTo(testString15) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp15.toString(2): \"%s\".\n",
                                matrix_cp15.toString());
                    }

                    if ( float_cp1.toString().compareTo(testString16) != 0 )
                    {
                        outStream.printf(
                                "Unexpected float_cp1.toString(2): \"%s\".\n",
                                float_cp1.toString());
                    }

                    if ( int_cp1.toString().compareTo(testString17) != 0 )
                    {
                        outStream.printf(
                                "Unexpected int_cp1.toString(2): \"%s\".\n",
                                int_cp1.toString());
                    }

                    if ( nominal_cp1.toString().compareTo(testString18) != 0 )
                    {
                        outStream.printf(
                                "Unexpected nominal_cp1.toString(2): \"%s\".\n",
                                nominal_cp1.toString());
                    }

                    if ( pred_cp1.toString().compareTo(testString19) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred_cp1.toString(2): \"%s\".\n",
                                pred_cp1.toString());
                    }

                    if ( text_cp1.toString().compareTo(testString20) != 0 )
                    {
                        outStream.printf(
                                "Unexpected text_cp1.toString(2): \"%s\".\n",
                                text_cp1.toString());
                    }
                }
            }
        }


        /* Now do a battery of tests of getArgCopy() -- objective is to
         * verify that output of getArgCopy() is a valid copy of the target
         * argument, or that the method fails appropriately if the target
         * doesn't exist.
         */

        failures += TestGetArgCopy(float_cp0, -1, 1,
                ExpectedResult.system_error, "float_cp0", outStream, verbose);
        failures += TestGetArgCopy(float_cp0,  0, 2,
                ExpectedResult.succeed, "float_cp0", outStream, verbose);
        failures += TestGetArgCopy(float_cp0,  1, 3,
                ExpectedResult.succeed, "float_cp0", outStream, verbose);
        failures += TestGetArgCopy(float_cp0,  2, 4,
                ExpectedResult.succeed, "float_cp0", outStream, verbose);
        failures += TestGetArgCopy(float_cp0,  3, 5,
                ExpectedResult.succeed, "float_cp0", outStream, verbose);
        failures += TestGetArgCopy(float_cp0,  4, 6,
                ExpectedResult.return_null, "float_cp0", outStream, verbose);

        failures += TestGetArgCopy(int_cp0, -1, 100,
                ExpectedResult.system_error, "int_cp0", outStream, verbose);
        failures += TestGetArgCopy(int_cp0,  0, 101,
                ExpectedResult.succeed, "int_cp0", outStream, verbose);
        failures += TestGetArgCopy(int_cp0,  1, 102,
                ExpectedResult.succeed, "int_cp0", outStream, verbose);
        failures += TestGetArgCopy(int_cp0,  2, 103,
                ExpectedResult.succeed, "int_cp0", outStream, verbose);
        failures += TestGetArgCopy(int_cp0,  3, 104,
                ExpectedResult.succeed, "int_cp0", outStream, verbose);
        failures += TestGetArgCopy(int_cp0,  4, 105,
                ExpectedResult.return_null, "int_cp0", outStream, verbose);

        failures += TestGetArgCopy(matrix_cp0, -1, 200,
                ExpectedResult.system_error, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  0, 201,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  1, 202,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  2, 203,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  3, 204,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  4, 205,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  5, 206,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  6, 207,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  7, 208,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  8, 209,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  9, 210,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  10, 211,
                ExpectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp0,  11, 212,
                ExpectedResult.return_null, "matrix_cp0", outStream, verbose);

        failures += TestGetArgCopy(matrix_cp8, -1, 300,
                ExpectedResult.system_error, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  0, 301,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  1, 302,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  2, 303,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  3, 304,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  4, 305,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  5, 306,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  6, 307,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  7, 308,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  8, 309,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  9, 310,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  10, 311,
                ExpectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp8,  11, 312,
                ExpectedResult.return_null, "matrix_cp8", outStream, verbose);

        failures += TestGetArgCopy(matrix_cp16, -1, 400,
                ExpectedResult.system_error, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  0, 401,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  1, 402,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  2, 403,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  3, 404,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  4, 405,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  5, 406,
                ExpectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp16,  6, 407,
                ExpectedResult.return_null, "matrix_cp16", outStream, verbose);

        failures += TestGetArgCopy(matrix_cp17, -1, 500,
                ExpectedResult.system_error, "matrix_cp17", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp17,  0, 501,
                ExpectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp17,  1, 502,
                ExpectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp17,  2, 503,
                ExpectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp17,  3, 504,
                ExpectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += TestGetArgCopy(matrix_cp17,  4, 505,
                ExpectedResult.return_null, "matrix_cp17", outStream, verbose);

        failures += TestGetArgCopy(nominal_cp0, -1, 600,
                ExpectedResult.system_error, "nominal_cp0", outStream, verbose);
        failures += TestGetArgCopy(nominal_cp0,  0, 601,
                ExpectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += TestGetArgCopy(nominal_cp0,  1, 602,
                ExpectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += TestGetArgCopy(nominal_cp0,  2, 603,
                ExpectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += TestGetArgCopy(nominal_cp0,  3, 604,
                ExpectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += TestGetArgCopy(nominal_cp0,  4, 605,
                ExpectedResult.return_null, "nominal_cp0", outStream, verbose);

        failures += TestGetArgCopy(pred_cp0, -1, 700,
                ExpectedResult.system_error, "pred_cp0", outStream, verbose);
        failures += TestGetArgCopy(pred_cp0,  0, 701,
                ExpectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += TestGetArgCopy(pred_cp0,  1, 702,
                ExpectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += TestGetArgCopy(pred_cp0,  2, 703,
                ExpectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += TestGetArgCopy(pred_cp0,  3, 704,
                ExpectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += TestGetArgCopy(pred_cp0,  4, 705,
                ExpectedResult.return_null, "pred_cp0", outStream, verbose);

        failures += TestGetArgCopy(text_cp0, -1, 800,
                ExpectedResult.system_error, "text_cp0", outStream, verbose);
        failures += TestGetArgCopy(text_cp0,  0, 801,
                ExpectedResult.succeed, "text_cp0", outStream, verbose);
        failures += TestGetArgCopy(text_cp0,  1, 802,
                ExpectedResult.succeed, "text_cp0", outStream, verbose);
        failures += TestGetArgCopy(text_cp0,  2, 803,
                ExpectedResult.succeed, "text_cp0", outStream, verbose);
        failures += TestGetArgCopy(text_cp0,  3, 804,
                ExpectedResult.succeed, "text_cp0", outStream, verbose);
        failures += TestGetArgCopy(text_cp0,  4, 805,
                ExpectedResult.return_null, "text_cp0", outStream, verbose);


        /* Now test argument replacement.
         *
         * Start with attempts to replace the value of a float column pred.
         */

        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 0.0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(float_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "float_cp0",
                                                    "floatArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(float_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "float_cp0",
                                                    "arg");
            }
        }


        /* now the value of an int column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(idv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "floatArg");

                failures += VerifyArgListAssignment(int_cp0,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "int_cp0",
                                                    "intArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(int_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "int_cp0",
                                                    "arg");
            }
        }


        /* now the value of a nominal column */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "whatever");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(ndv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "intArg");

                failures += VerifyArgListAssignment(nominal_cp0,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "nominal_cp0",
                                                    "nomArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(nominal_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "nominal_cp0",
                                                    "arg");
            }
        }


        /* now the value of a predicate column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID,
                        new Predicate(db, pve0_ID));
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(pdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "nomArg");

                failures += VerifyArgListAssignment(pred_cp0,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(pred_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred_cp0",
                                                    "arg");
            }
        }


        /* now the value of a text column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID,
                                              "yet another text string");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(tsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "predArg");

                failures += VerifyArgListAssignment(text_cp0,
                                                    textArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "text_cp0",
                                                    "textArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(text_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "text_cp0",
                                                    "arg");
            }
        }


        /* we have save matrix column predicates for last -- in theory
         * only need to test the single entry case below.  However,
         * we will start with that, and then do some spot checks on
         * multi-entry matrix column predicates.
         *
         * First use new args without fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp17,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp17,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp17",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    qsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    tsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "tsArg");
            }
        }

        /* repeat the above test, only with fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve10.getCPFormalArg(3).getID();

                floatArg = new FloatDataValue(db, fargID, 1066.0);
                intArg = new IntDataValue(db, fargID, 1903);
                nomArg = new NominalDataValue(db, fargID, "yan");
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db, fargID, "yats");
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 60));
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve10.getFormalArg(0).getFargName());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp17,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp17,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp17",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    qsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    tsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "tsArg");
            }
        }


        /* finally, do some spot checks of replaceArg()/getArg() on column
         * predicates implied by vme's of type matrix and with length greater
         * than one -- in the first pass, we will not assign fargIDs.
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    intArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    nomArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    predArg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp0,
                                                      textArg,
                                                      7,
                                                      outStream,
                                                      verbose,
                                                      "text_cp",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    qsArg,
                                                    7,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    tsArg,
                                                    8,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "tsArg");
            }
        }

        /* and a simlar test, with fargIDs set */

        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                floatArg = new FloatDataValue(db, fargID, 1066.0);

                fargID = matrix_mve0.getCPFormalArg(4).getID();
                intArg = new IntDataValue(db, fargID, 1903);

                fargID = matrix_mve0.getCPFormalArg(5).getID();
                nomArg = new NominalDataValue(db, fargID, "yan");

                fargID = matrix_mve0.getCPFormalArg(6).getID();
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                textArg = new TextStringDataValue(db, fargID, "yats");

                fargID = matrix_mve0.getCPFormalArg(7).getID();
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");

                fargID = matrix_mve0.getCPFormalArg(8).getID();
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 360));

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve0.getCPFormalArg(9).getFargName());

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new FloatDataValue(db, fargID, 10.0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( arg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    intArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp1",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    nomArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    predArg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp0,
                                                      textArg,
                                                      9,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    qsArg,
                                                    7,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    tsArg,
                                                    8,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "tsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    arg,
                                                    9,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "arg");
            }
        }

        /* we have now tested replaceArg() and getArgCopy() against all
         * type combinations.  Must now go through the rest of the
         * cases in which failures are expected.
         */

        /* verify failure on a farg ID mismatch. */

        if ( failures == 0 )
        {
            goodArg = null;
            badArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve9.getCPFormalArg(3).getID();
                goodArg = new NominalDataValue(db, fargID, "good_fargID");

                fargID = matrix_mve9.getCPFormalArg(4).getID();
                badArg = new NominalDataValue(db, fargID, "bad_fargID");

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( badArg == null ) ||
                 ( goodArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fID)";

                    if ( goodArg == null )
                    {
                        outStream.printf("%s: Allocation of goodArg failed.\n",
                                         testTag);
                    }

                    if ( badArg == null )
                    {
                        outStream.printf("%s: Allocation of badArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp16,
                                                    goodArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp16",
                                                    "goodArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp16,
                                                      badArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp16",
                                                      "badArg");

                failures += VerifyArgListAssignment(matrix_cp16,
                                                    badArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp16",
                                                    "badArg");
            }
        }


        /* next, verify that getArg() and replaceArg() fail when supplied
         * invalid indexes.
         */
        /* replaceArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                float_cp0.replaceArg(-1, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx0)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: float_cp0.replaceArg(-1, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: float_cp0.replaceArg(-1, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* replaceArg() with index too big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                matrix_cp16.replaceArg(6, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx1)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_cp16.replaceArg(6, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_cp16.replaceArg(6, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = matrix_cp16.getArg(-1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx2)";

                    if ( arg != null )
                    {
                        outStream.printf(
                                "%s: matrix_cp16.getArg(-1) returned.\n",
                                testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_cp16.getArg(-1) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_cp16.getArg(-1) " +
                                "failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with index to big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = float_cp0.getArg(4);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx3)";

                    if ( arg != null )
                    {
                        outStream.printf("%s: float_cp0.getArg(4) " +
                                "returned non-null.\n", testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                            "%s: float_cp0.getArg(4) failed to complete.\n",
                            testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s: float_cp0.getArg(4) " +
                                "threw a system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
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
    } /* ColPred::TestArgListManagement() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        String testBanner =
            "Testing copy constructor for class ColPred                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp = null;
        ColPred float_cp_copy = null;
        ColPred empty_float_cp = null;
        ColPred empty_float_cp_copy = null;
        ColPred int_cp = null;
        ColPred int_cp_copy = null;
        ColPred empty_int_cp = null;
        ColPred empty_int_cp_copy = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp0_copy = null;
        ColPred empty_matrix_cp0 = null;
        ColPred empty_matrix_cp0_copy = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp1_copy = null;
        ColPred empty_matrix_cp1 = null;
        ColPred empty_matrix_cp1_copy = null;
        ColPred matrix_cp2 = null;
        ColPred matrix_cp2_copy = null;
        ColPred empty_matrix_cp2 = null;
        ColPred empty_matrix_cp2_copy = null;
        ColPred nominal_cp = null;
        ColPred nominal_cp_copy = null;
        ColPred empty_nominal_cp = null;
        ColPred empty_nominal_cp_copy = null;
        ColPred pred_cp = null;
        ColPred pred_cp_copy = null;
        ColPred empty_pred_cp = null;
        ColPred empty_pred_cp_copy = null;
        ColPred text_cp = null;
        ColPred text_cp_copy = null;
        ColPred empty_text_cp = null;
        ColPred empty_text_cp_copy = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 22) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                            "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";
            String empty_float_cp_string =
                    "float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000)";
            String empty_int_cp_string =
                    "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String empty_matrix_cp0_string =
                    "matrix_mve0(0, 00:00:00:000, 00:00:00:000, " +
                            "0.000000, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String empty_matrix_cp1_string =
                    "matrix_mve1(0, 00:00:00:000, 00:00:00:000, " +
                            "<arg1>, <arg2>, <arg3>)";
            String empty_matrix_cp2_string =
                    "matrix_mve2(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String empty_nominal_cp_string =
                    "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String empty_pred_cp_string =
                    "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String empty_text_cp_string =
                    "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String empty_float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 5) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0))))))";
            String empty_int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String empty_matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue ()) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:00:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String empty_matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg3>) " +
//                                    "(subRange false))))))";
            String empty_matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((UndefinedDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String empty_nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false))))))";
            String empty_pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue ()) " +
//                                    "(subRange false))))))";
            String empty_text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getCPFormalArg(9).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getCPFormalArg(4).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getCPFormalArg(3).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);


                empty_float_cp   = new ColPred(db, float_mve_ID);
                empty_int_cp     = new ColPred(db, int_mve_ID);
                empty_matrix_cp0 = new ColPred(db, matrix_mve0_ID);
                empty_matrix_cp1 = new ColPred(db, matrix_mve1_ID);
                empty_matrix_cp2 = new ColPred(db, matrix_mve2_ID);
                empty_nominal_cp = new ColPred(db, nominal_mve_ID);
                empty_pred_cp    = new ColPred(db, pred_mve_ID);
                empty_text_cp    = new ColPred(db, text_mve_ID);


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( empty_float_cp == null ) ||
                 ( empty_int_cp == null ) ||
                 ( empty_matrix_cp0 == null ) ||
                 ( empty_matrix_cp1 == null ) ||
                 ( empty_matrix_cp2 == null ) ||
                 ( empty_nominal_cp == null ) ||
                 ( empty_pred_cp == null ) ||
                 ( empty_text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed*.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( empty_float_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_float_cp failed.\n");
                    }

                    if ( empty_int_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_int_cp failed.\n");
                    }

                    if ( empty_matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp0 failed.\n");
                    }

                    if ( empty_matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp1 failed.\n");
                    }

                    if ( empty_matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp2 failed.\n");
                    }

                    if ( empty_nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_nominal_cp failed.\n");
                    }

                    if ( empty_pred_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_pred_cp failed.\n");
                    }

                    if ( empty_text_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_text_cp failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test col preds failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred creation threw a " +
                                         "SystemErrorException: %s.\n",
                                         systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
            else if ( ( empty_float_cp.toString().
                        compareTo(empty_float_cp_string) != 0 ) ||
                      ( empty_int_cp.toString().
                        compareTo(empty_int_cp_string) != 0 ) ||
                      ( empty_matrix_cp0.toString().
                        compareTo(empty_matrix_cp0_string) != 0 ) ||
                      ( empty_matrix_cp1.toString().
                        compareTo(empty_matrix_cp1_string) != 0 ) ||
                      ( empty_matrix_cp2.toString().
                        compareTo(empty_matrix_cp2_string) != 0 ) ||
                      ( empty_nominal_cp.toString().
                        compareTo(empty_nominal_cp_string) != 0 ) ||
                      ( empty_pred_cp.toString().
                        compareTo(empty_pred_cp_string) != 0 ) ||
                      ( empty_text_cp.toString().
                        compareTo(empty_text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_cp.toString().
                         compareTo(empty_float_cp_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_cp.toString(): %s\n",
                            empty_float_cp.toString());
                    }

                    if ( empty_int_cp.toString().
                         compareTo(empty_int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_cp.toString(): %s\n",
                                empty_int_cp.toString());
                    }

                    if ( empty_matrix_cp0.toString().
                         compareTo(empty_matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp0.toString(): %s\n",
                            empty_matrix_cp0.toString());
                    }

                    if ( empty_matrix_cp1.toString().
                         compareTo(empty_matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp1.toString(): %s\n",
                            empty_matrix_cp1.toString());
                    }

                    if ( empty_matrix_cp2.toString().
                         compareTo(empty_matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp2.toString(): %s\n",
                            empty_matrix_cp2.toString());
                    }

                    if ( empty_nominal_cp.toString().
                         compareTo(empty_nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_cp.toString(): %s\n",
                             empty_nominal_cp.toString());
                    }

                    if ( empty_pred_cp.toString().
                         compareTo(empty_pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_cp.toString(): %s\n",
                                empty_pred_cp.toString());
                    }

                    if ( empty_text_cp.toString().
                         compareTo(empty_text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_cp.toString(): %s\n",
                                empty_text_cp.toString());
                    }
                }
            }
            else if ( ( empty_float_cp.toDBString().
                        compareTo(empty_float_cp_DBstring) != 0 ) ||
                      ( empty_int_cp.toDBString().
                        compareTo(empty_int_cp_DBstring) != 0 ) ||
                      ( empty_matrix_cp0.toDBString().
                        compareTo(empty_matrix_cp0_DBstring) != 0 ) ||
                      ( empty_matrix_cp1.toDBString().
                        compareTo(empty_matrix_cp1_DBstring) != 0 ) ||
                      ( empty_matrix_cp2.toDBString().
                        compareTo(empty_matrix_cp2_DBstring) != 0 ) ||
                      ( empty_nominal_cp.toDBString().
                        compareTo(empty_nominal_cp_DBstring) != 0 ) ||
                      ( empty_pred_cp.toDBString().
                        compareTo(empty_pred_cp_DBstring) != 0 ) ||
                      ( empty_text_cp.toDBString().
                        compareTo(empty_text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_cp.toDBString().
                         compareTo(empty_float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_cp.toDBString(): %s\n",
                            empty_float_cp.toDBString());
                    }

                    if ( empty_int_cp.toDBString().
                         compareTo(empty_int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_cp.toDBString(): %s\n",
                                empty_int_cp.toDBString());
                    }

                    if ( empty_matrix_cp0.toDBString().
                         compareTo(empty_matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp0.toDBString(): %s\n",
                            empty_matrix_cp0.toDBString());
                    }

                    if ( empty_matrix_cp1.toDBString().
                         compareTo(empty_matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp1.toDBString(): %s\n",
                            empty_matrix_cp1.toDBString());
                    }

                    if ( empty_matrix_cp2.toDBString().
                         compareTo(empty_matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp2.toDBString(): %s\n",
                            empty_matrix_cp2.toDBString());
                    }

                    if ( empty_nominal_cp.toDBString().
                         compareTo(empty_nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_cp.toDBString(): %s\n",
                             empty_nominal_cp.toDBString());
                    }

                    if ( empty_pred_cp.toDBString().
                         compareTo(empty_pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_cp.toDBString(): %s\n",
                                empty_pred_cp.toDBString());
                    }

                    if ( empty_text_cp.toDBString().
                         compareTo(empty_text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_cp.toDBString(): %s\n",
                                empty_text_cp.toDBString());
                    }
                }
            }
        }

        // setup is complete -- now try to make the copies
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_float_cp_copy   = new ColPred(empty_float_cp);
                empty_int_cp_copy     = new ColPred(empty_int_cp);
                empty_matrix_cp0_copy = new ColPred(empty_matrix_cp0);
                empty_matrix_cp1_copy = new ColPred(empty_matrix_cp1);
                empty_matrix_cp2_copy = new ColPred(empty_matrix_cp2);
                empty_nominal_cp_copy = new ColPred(empty_nominal_cp);
                empty_pred_cp_copy    = new ColPred(empty_pred_cp);
                empty_text_cp_copy    = new ColPred(empty_text_cp);

                float_cp_copy   = new ColPred(float_cp);
                int_cp_copy     = new ColPred(int_cp);
                matrix_cp0_copy = new ColPred(matrix_cp0);
                matrix_cp1_copy = new ColPred(matrix_cp1);
                matrix_cp2_copy = new ColPred(matrix_cp2);
                nominal_cp_copy = new ColPred(nominal_cp);
                pred_cp_copy    = new ColPred(pred_cp);
                text_cp_copy    = new ColPred(text_cp);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_float_cp_copy == null ) ||
                 ( empty_int_cp_copy == null ) ||
                 ( empty_matrix_cp0_copy == null ) ||
                 ( empty_matrix_cp1_copy == null ) ||
                 ( empty_matrix_cp2_copy == null ) ||
                 ( empty_nominal_cp_copy == null ) ||
                 ( empty_pred_cp_copy == null ) ||
                 ( empty_text_cp_copy == null ) ||
                 ( float_cp_copy == null ) ||
                 ( int_cp_copy == null ) ||
                 ( matrix_cp0_copy == null ) ||
                 ( matrix_cp1_copy == null ) ||
                 ( matrix_cp2_copy == null ) ||
                 ( nominal_cp_copy == null ) ||
                 ( pred_cp_copy == null ) ||
                 ( text_cp_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( empty_float_cp_copy == null )
                {
                    outStream.printf(
                            "empty_float_cp_copy allocation failed.\n");
                }

                if ( empty_int_cp_copy == null )
                {
                    outStream.printf(
                            "empty_int_cp_copy allocation failed.\n");
                }

                if ( empty_matrix_cp0_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp0_copy allocation failed.\n");
                }

                if ( empty_matrix_cp1_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp1_copy allocation failed.\n");
                }

                if ( empty_matrix_cp2_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp2_copy allocation failed.\n");
                }

                if ( empty_nominal_cp_copy == null )
                {
                    outStream.printf(
                            "empty_nominal_cp_copy allocation failed.\n");
                }

                if ( empty_pred_cp_copy == null )
                {
                    outStream.printf(
                            "empty_pred_cp_copy allocation failed.\n");
                }

                if ( empty_text_cp_copy == null )
                {
                    outStream.printf(
                            "empty_text_cp_copy allocation failed.\n");
                }

                if ( float_cp_copy == null )
                {
                    outStream.printf(
                            "float_cp_copy allocation failed.\n");
                }

                if ( int_cp_copy == null )
                {
                    outStream.printf(
                            "int_cp_copy allocation failed.\n");
                }

                if ( matrix_cp0_copy == null )
                {
                    outStream.printf(
                            "matrix_cp0_copy allocation failed.\n");
                }

                if ( matrix_cp1_copy == null )
                {
                    outStream.printf(
                            "matrix_cp1_copy allocation failed.\n");
                }

                if ( matrix_cp2_copy == null )
                {
                    outStream.printf(
                            "matrix_cp2_copy allocation failed.\n");
                }

                if ( nominal_cp_copy == null )
                {
                    outStream.printf(
                            "nominal_cp_copy allocation failed.\n");
                }

                if ( pred_cp_copy == null )
                {
                    outStream.printf(
                            "pred_cp_copy allocation failed.\n");
                }

                if ( text_cp_copy == null )
                {
                    outStream.printf(
                            "text_cp_copy allocation failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of copies failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "matrix copy threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // if failures == 0, check to see if the copies are valid */
        if ( failures == 0 )
        {
            failures += VerifyColPredCopy(empty_float_cp,
                                          empty_float_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_float_cp",
                                          "empty_float_cp_copy");

            failures += VerifyColPredCopy(empty_int_cp,
                                          empty_int_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_int_cp",
                                          "empty_int_cp_copy");

            failures += VerifyColPredCopy(empty_matrix_cp0,
                                          empty_matrix_cp0_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp0",
                                          "empty_matrix_cp0_copy");

            failures += VerifyColPredCopy(empty_matrix_cp1,
                                          empty_matrix_cp1_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp1",
                                          "empty_matrix_cp1_copy");

            failures += VerifyColPredCopy(empty_matrix_cp2,
                                          empty_matrix_cp2_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp2",
                                          "empty_matrix_cp2_copy");

            failures += VerifyColPredCopy(empty_nominal_cp,
                                          empty_nominal_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_nominal_cp",
                                          "empty_nominal_cp_copy");

            failures += VerifyColPredCopy(empty_pred_cp,
                                          empty_pred_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_pred_cp",
                                          "empty_pred_cp_copy");

            failures += VerifyColPredCopy(empty_text_cp,
                                          empty_text_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_text_cp",
                                          "empty_text_cp_copy");

            failures += VerifyColPredCopy(float_cp,
                                          float_cp_copy,
                                          outStream,
                                          verbose,
                                          "float_cp",
                                          "float_cp_copy");

            failures += VerifyColPredCopy(int_cp,
                                          int_cp_copy,
                                          outStream,
                                          verbose,
                                          "int_cp",
                                          "int_cp_copy");

            failures += VerifyColPredCopy(matrix_cp0,
                                          matrix_cp0_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp0",
                                          "matrix_cp0_copy");

            failures += VerifyColPredCopy(matrix_cp1,
                                          matrix_cp1_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp1",
                                          "matrix_cp1_copy");

            failures += VerifyColPredCopy(matrix_cp2,
                                          matrix_cp2_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp2",
                                          "matrix_cp2_copy");

            failures += VerifyColPredCopy(nominal_cp,
                                          nominal_cp_copy,
                                          outStream,
                                          verbose,
                                          "nominal_cp",
                                          "nominal_cp_copy");

            failures += VerifyColPredCopy(pred_cp,
                                          pred_cp_copy,
                                          outStream,
                                          verbose,
                                          "pred_cp",
                                          "pred_cp_copy");

            failures += VerifyColPredCopy(text_cp,
                                          text_cp_copy,
                                          outStream,
                                          verbose,
                                          "text_cp",
                                          "text_cp_copy");
        }

        /* now verify that the copy constructor fails when passed an invalid
         * reference to a ColPred.  For now, this just means passing in a
         * null.
         */
        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred((ColPred)null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print("new ColPred(null) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new ColPred(null) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new ColPred(null) " +
                                    "didn't throw a SystemErrorException.\n");
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
    } /* ColPred::TestCopyConstructor() */


    /**
     * TestGetArgCopy()
     *
     * Given a ColPred, and an argument number, verify that getArgCopy()
     * returns a copy of the target argument if the argNum parameter refers
     * to a parameter, returns null if argNum is greater than the number
     * of parameters, and fails with a system error is argNum is negative.
     *
     * Return the number of failures detected.
     *
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestGetArgCopy(ColPred cp,
                                     int argNum,
                                     int testNum,
                                     ExpectedResult er,
                                     String cpName,
                                     java.io.PrintStream outStream,
                                     boolean verbose)
        throws SystemErrorException
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue copy = null;

        try
        {
            copy = cp.getArgCopy(argNum);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( argNum < 0 )
        {
            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) completed.\n",
                                         testNum, cpName, argNum);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) failed to throw " +
                                "a system error exception.\n",
                                testNum, cpName, argNum);
                    }
                }
            }
            else if ( er != ExpectedResult.system_error )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            ExpectedResult.system_error.toString());
                }
            }
        }
        else if ( argNum >= cp.getNumArgs() )
        {
            if ( ( copy != null ) ||
                 ( ! completed  ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copy != null )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to return null.\n",
                                testNum, cpName, argNum);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to completed.\n",
                                testNum, cpName, argNum);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "%d: %s.getArgCopy(%d >= numArgs) threw " +
                            "an unexpected system error exception: \"%s\".\n",
                            testNum, cpName, argNum, systemErrorExceptionString);
                    }
                }
            }
            else if ( er != ExpectedResult.return_null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            ExpectedResult.return_null.toString());
                }
            }
        }
        else
        {
            failures += DataValueTest.VerifyDVCopy(cp.argList.get(argNum),
                                               copy,
                                               outStream,
                                               verbose,
                                               cpName + "(" + argNum + ")",
                                               cpName + "(" + argNum + ") copy");

            if ( er != ExpectedResult.succeed )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            ExpectedResult.succeed.toString());
                }
            }
        }

        return failures;

    } /* ColPred::TestGetArgCopy() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the to string methods for this
     * class.
     *
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods() {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures                 = 0;
        Database db                  = null;
        PredicateVocabElement pve    = null;
        MatrixVocabElement mve0      = null;
        MatrixVocabElement mve1      = null;
        long fargID                  = DBIndex.INVALID_ID;
        long pveID                   = DBIndex.INVALID_ID;
        long mve0ID                  = DBIndex.INVALID_ID;
        long mve1ID                  = DBIndex.INVALID_ID;
        FormalArgument farg          = null;
        Vector<DataValue> argList0   = null;
        Vector<DataValue> argList1   = null;
        DataValue arg                = null;
        FloatDataValue floatArg0     = null;
        FloatDataValue floatArg1     = null;
        IntDataValue intArg0         = null;
        IntDataValue intArg1         = null;
        IntDataValue intArg2         = null;
        NominalDataValue nominalArg0 = null;
        NominalDataValue nominalArg1 = null;
        PredDataValue predArg0       = null;
        PredDataValue predArg1       = null;
        QuoteStringDataValue qsArg0  = null;
        QuoteStringDataValue qsArg1  = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        ColPred cp0                  = null;
        ColPred cp1                  = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, mve's and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve = new PredicateVocabElement(db, "pve");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve.appendFormalArg(farg);
            pveID = db.addPredVE(pve);

            // get a copy of the databases version of pve with ids assigned
            pve = db.getPredVE(pveID);

            mve0 = new MatrixVocabElement(db, "mve0");
            mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            mve0.appendFormalArg(farg);
            mve0.setVarLen(true);
            db.vl.addElement(mve0);
            mve0ID = mve0.getID();


            mve1 = new MatrixVocabElement(db, "mve1");
            mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db, "<arg>");
            mve1.appendFormalArg(farg);
            db.vl.addElement(mve1);
            mve1ID = mve1.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve == null ) ||
             ( pveID == DBIndex.INVALID_ID ) ||
             ( mve0 == null ) ||
             ( mve0ID == DBIndex.INVALID_ID ) ||
             ( mve1 == null ) ||
             ( mve1ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve == null )
                {
                    outStream.print("creation of pve failed.\n");
                }

                if ( pveID == DBIndex.INVALID_ID )
                {
                    outStream.print("pveID not initialized.\n");
                }

                if ( mve0 == null )
                {
                    outStream.print("creation of mve0 failed.\n");
                }

                if ( mve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve0ID not initialized.\n");
                }

                if ( mve1 == null )
                {
                    outStream.print("creation of mve1 failed.\n");
                }

                if ( mve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve1ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("mve or pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Setup the matricies that we will used for the toString and
        // toDBString tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 =
                    "mve0(1, 00:00:00:001, 00:00:01:000, 1.000000, 2, a_nominal, " +
                            "pve(<arg>), \"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                    "(colPred (id 0) (mveID 3) (mveName mve0) (varLen true) (argList ((IntDataValue (id 100) (itsFargID 11) (itsFargType INTEGER) (itsCellID 500) (itsValue 1) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 101) (itsFargID 12) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:001)) (subRange false)), (TimeStampDataValue (id 102) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:01:000)) (subRange false)), (FloatDataValue (id 103) (itsFargID 14) (itsFargType FLOAT) (itsCellID 500) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 104) (itsFargID 15) (itsFargType INTEGER) (itsCellID 500) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 105) (itsFargID 16) (itsFargType NOMINAL) (itsCellID 500) (itsValue a_nominal) (subRange false)), (PredDataValue (id 106) (itsFargID 17) (itsFargType PREDICATE) (itsCellID 500) (itsValue (predicate (id 0) (predID 1) (predName pve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 107) (itsFargID 18) (itsFargType QUOTE_STRING) (itsCellID 500) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 108) (itsFargID 19) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 109) (itsFargID 20) (itsFargType UNTYPED) (itsCellID 500) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 3) (mveName mve0) (varLen true) (argList ((IntDataValue (id 100) (itsFargID 11) (itsFargType UNTYPED) (itsCellID 500) (itsValue 1) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 101) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 500) (itsValue (60,00:00:00:001)) (subRange false)), (TimeStampDataValue (id 102) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 500) (itsValue (60,00:00:01:000)) (subRange false)), (FloatDataValue (id 103) (itsFargID 14) (itsFargType FLOAT) (itsCellID 500) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 104) (itsFargID 15) (itsFargType INTEGER) (itsCellID 500) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 105) (itsFargID 16) (itsFargType NOMINAL) (itsCellID 500) (itsValue a_nominal) (subRange false)), (PredDataValue (id 106) (itsFargID 17) (itsFargType PREDICATE) (itsCellID 500) (itsValue (predicate (id 0) (predID 1) (predName pve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 107) (itsFargID 18) (itsFargType QUOTE_STRING) (itsCellID 500) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 108) (itsFargID 19) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 109) (itsFargID 20) (itsFargType UNTYPED) (itsCellID 500) (itsValue <untyped>) (subRange false))))))";
//                "(Matrix (mveID 3) " +
//                        "(varLen true) " +
//                        "(argList " +
//                            "((FloatDataValue (id 100) " +
//                                "(itsFargID 4) " +
//                                "(itsFargType FLOAT) " +
//                                "(itsCellID 500) " +
//                                "(itsValue 1.0) " +
//                                "(subRange false) " +
//                                "(minVal 0.0) " +
//                                "(maxVal 0.0)), " +
//                            "(IntDataValue (id 101) " +
//                                "(itsFargID 5) " +
//                                "(itsFargType INTEGER) " +
//                                "(itsCellID 500) " +
//                                "(itsValue 2) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0)), " +
//                            "(NominalDataValue (id 102) " +
//                                "(itsFargID 6) " +
//                                "(itsFargType NOMINAL) " +
//                                "(itsCellID 500) " +
//                                "(itsValue a_nominal) " +
//                                "(subRange false)), " +
//                            "(PredDataValue (id 103) " +
//                                "(itsFargID 7) " +
//                                "(itsFargType PREDICATE) " +
//                                "(itsCellID 500) " +
//                                "(itsValue " +
//                                    "(predicate (id 0) " +
//                                        "(predID 1) " +
//                                        "(predName pve) " +
//                                        "(varLen false) " +
//                                        "(argList " +
//                                            "((UndefinedDataValue (id 0) " +
//                                                "(itsFargID 2) " +
//                                                "(itsFargType UNTYPED) " +
//                                                "(itsCellID 0) " +
//                                                "(itsValue <arg>) " +
//                                                "(subRange false))))))) " +
//                                "(subRange false)), " +
//                            "(QuoteStringDataValue (id 104) " +
//                                "(itsFargID 8) " +
//                                "(itsFargType QUOTE_STRING) " +
//                                "(itsCellID 500) " +
//                                "(itsValue q-string) " +
//                                "(subRange false)), " +
//                            "(TimeStampDataValue (id 105) " +
//                                "(itsFargID 9) " +
//                                "(itsFargType TIME_STAMP) " +
//                                "(itsCellID 500) " +
//                                "(itsValue (60,00:00:00:000)) " +
//                                "(subRange false)), " +
//                            "(UndefinedDataValue (id 106) " +
//                                "(itsFargID 10) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 500) " +
//                                "(itsValue <untyped>) " +
//                                "(subRange false))))))";

            String testString1 = "mve1(2, 00:00:00:002, 00:00:02:000, 99)";
            String testDBString1 =
                "(colPred (id 0) (mveID 21) (mveName mve1) (varLen false) (argList ((IntDataValue (id 110) (itsFargID 23) (itsFargType INTEGER) (itsCellID 501) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 111) (itsFargID 24) (itsFargType TIME_STAMP) (itsCellID 501) (itsValue (60,00:00:00:002)) (subRange false)), (TimeStampDataValue (id 112) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 501) (itsValue (60,00:00:02:000)) (subRange false)), (IntDataValue (id 113) (itsFargID 26) (itsFargType INTEGER) (itsCellID 501) (itsValue 99) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(colPred (id 0) (mveID 21) (mveName mve1) (varLen false) (argList ((IntDataValue (id 110) (itsFargID 23) (itsFargType UNTYPED) (itsCellID 501) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 111) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 501) (itsValue (60,00:00:00:002)) (subRange false)), (TimeStampDataValue (id 112) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 501) (itsValue (60,00:00:02:000)) (subRange false)), (IntDataValue (id 113) (itsFargID 26) (itsFargType INTEGER) (itsCellID 501) (itsValue 99) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(Matrix (mveID 21) " +
//                        "(varLen false) " +
//                        "(argList " +
//                            "((IntDataValue (id 107) " +
//                                "(itsFargID 22) " +
//                                "(itsFargType INTEGER) " +
//                                "(itsCellID 501) " +
//                                "(itsValue 99) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 1);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 1));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 1 * db.getTicks()));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pveID));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                             mve0.getCPFormalArg(9).getFargName());
                argList0.add(arg);

                cp0 = new ColPred(db, mve0ID, argList0);

                // set argument IDs to dummy values to test toDBString()
                cp0.argList.get(0).setID(100);
                cp0.argList.get(1).setID(101);
                cp0.argList.get(2).setID(102);
                cp0.argList.get(3).setID(103);
                cp0.argList.get(4).setID(104);
                cp0.argList.get(5).setID(105);
                cp0.argList.get(6).setID(106);
                cp0.argList.get(7).setID(107);
                cp0.argList.get(8).setID(108);
                cp0.argList.get(9).setID(109);

                // set argument cellIDs to dummy values to test toDBString()
                cp0.argList.get(0).itsCellID = 500;
                cp0.argList.get(1).itsCellID = 500;
                cp0.argList.get(2).itsCellID = 500;
                cp0.argList.get(3).itsCellID = 500;
                cp0.argList.get(4).itsCellID = 500;
                cp0.argList.get(5).itsCellID = 500;
                cp0.argList.get(6).itsCellID = 500;
                cp0.argList.get(7).itsCellID = 500;
                cp0.argList.get(8).itsCellID = 500;
                cp0.argList.get(9).itsCellID = 500;

                argList1 = new Vector<DataValue>();
                fargID = mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 2));
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 2 * db.getTicks()));
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 99);
                argList1.add(arg);

                cp1 = new ColPred(db, mve1ID, argList1);

                // set argument IDs to dummy values to test toDBString()
                cp1.argList.get(0).setID(110);
                cp1.argList.get(1).setID(111);
                cp1.argList.get(2).setID(112);
                cp1.argList.get(3).setID(113);

                // set argument cellIDs to dummy values to test toDBString()
                cp1.argList.get(0).itsCellID = 501;
                cp1.argList.get(1).itsCellID = 501;
                cp1.argList.get(2).itsCellID = 501;
                cp1.argList.get(3).itsCellID = 501;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 10 ) ||
                 ( cp0 == null ) ||
                 ( argList1 == null ) ||
                 ( argList1.size() != 4 ) ||
                 ( cp1 == null ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 10 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (10).\n",
                                         argList0.size());
                    }

                    if ( argList1 == null )
                    {
                        outStream.print("argList1 allocation failed.\n");
                    }
                    else if ( argList1.size() != 4 )
                    {
                        outStream.printf("unexpected argList1.size(): %d (4).\n",
                                         argList1.size());
                    }

                    if ( ( cp0 == null ) ||
                         ( cp1 == null ) )
                    {
                        outStream.print("one or more ColPred allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test col pred allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test col pred allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( cp0.argList.get(0).getID() != 100 ) ||
                      ( cp0.argList.get(1).getID() != 101 ) ||
                      ( cp0.argList.get(2).getID() != 102 ) ||
                      ( cp0.argList.get(3).getID() != 103 ) ||
                      ( cp0.argList.get(4).getID() != 104 ) ||
                      ( cp0.argList.get(5).getID() != 105 ) ||
                      ( cp0.argList.get(6).getID() != 106 ) ||
                      ( cp0.argList.get(7).getID() != 107 ) ||
                      ( cp0.argList.get(8).getID() != 108 ) ||
                      ( cp0.argList.get(9).getID() != 109 ) ||
                      ( cp1.argList.get(0).getID() != 110 ) ||
                      ( cp1.argList.get(1).getID() != 111 ) ||
                      ( cp1.argList.get(2).getID() != 112 ) ||
                      ( cp1.argList.get(3).getID() != 113 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cp?.argList arg ID(s): " +
                            "%d %d %d %d %d %d %d %d %d %d - %d %d %d %d\n",
                            cp0.argList.get(0).getID(),
                            cp0.argList.get(1).getID(),
                            cp0.argList.get(2).getID(),
                            cp0.argList.get(3).getID(),
                            cp0.argList.get(4).getID(),
                            cp0.argList.get(5).getID(),
                            cp0.argList.get(6).getID(),
                            cp0.argList.get(7).getID(),
                            cp0.argList.get(8).getID(),
                            cp0.argList.get(9).getID(),
                            cp1.argList.get(0).getID(),
                            cp1.argList.get(1).getID(),
                            cp1.argList.get(2).getID(),
                            cp1.argList.get(3).getID());
                }
            }
            else if ( ( cp0.toString().compareTo(testString0) != 0 ) ||
                      ( cp1.toString().compareTo(testString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cp0.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected cp0.toString)(): \"%s\"\n",
                                         cp0.toString());
                    }

                    if ( cp1.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf("Unexpected cp1.toString)(): \"%s\"\n",
                                         cp1.toString());
                    }
                }
            }
            else if ( ( cp0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( cp1.toDBString().compareTo(testDBString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cp0.toDBString().compareTo(testDBString0) != 0 )
                    {
                       outStream.printf(
                               "Unexpected cp0.toDBString)(): \"%s\"\n",
                               cp0.toDBString());
                    }

                    if ( cp1.toDBString().compareTo(testDBString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected cp1.toDBString)(): \"%s\"\n",
                               cp1.toDBString());
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

    } /* ColPred::TestToStringMethods() */


    /**
     * Verify3ArgConstructorFailure()
     *
     * Verify that the three argument constructor for this class fails with
     * a system error when supplied the given parameters.
     *
     * Return 0 if the constructor fails as expected, and 1 if it does not.
     *
     *                                              JRM -- 9/30/08
     *
     * Changes:
     *
     *    - None.
     */

    public static int Verify3ArgConstructorFailure(Database db,
                                                   long mve_id,
                                                   Vector<DataValue> arg_list,
                                                   java.io.PrintStream outStream,
                                                   boolean verbose,
                                                   String db_desc,
                                                   String mve_id_desc,
                                                   String arg_list_desc)
    {
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        ColPred cp0 = null;

        try
        {
            cp0 = new ColPred(db, mve_id, arg_list);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.printf("new ColPred(%s, %s, %s) != null.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( completed )
                {
                    outStream.printf("new ColPred(%s, %s, %s) completed.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("new ColPred(%s, %s, %s) didn't throw " +
                                     "a SystemErrorException.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }
            }
        }

        return failures;

    } /* ColPred::Verify3ArgConstructorFailure() */


    /**
     * VerifyArgListAssignment()
     *
     * Verify that the specified replacement of an argument list
     * entry succeeds.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    private static int VerifyArgListAssignment(ColPred target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int progress = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            progress++;

            target.replaceArg(idx, newArg);

            progress++;

            new_dv = target.getArg(idx);

            progress++;
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( old_dv.getItsFargID() == DBIndex.INVALID_ID ) ||
             ( new_dv == null ) ||
             ( new_dv != newArg ) ||
             ( old_dv.getItsFargID() != new_dv.getItsFargID() ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "initial %s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( old_dv.getItsFargID() == DBIndex.INVALID_ID )
                {
                    outStream.printf("initial %s.getArg(%d).getItsFargID() " +
                            "returned INVALID_ID.\n",
                            targetDesc, idx);
                }

                if ( new_dv == null )
                {
                    outStream.printf("%s.replaceArg(%d, %s) failed to " +
                            "complete (progress = %d).\n",
                            targetDesc, idx, newArgDesc, progress);
                }

                if ( new_dv != newArg )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( old_dv.getItsFargID() != new_dv.getItsFargID() )
                {
                    outStream.printf("unexpected itsFargID after %s.replace" +
                            "Arg(%d, %s). old = %d, new = %d\n",
                            targetDesc, idx, newArgDesc,
                            old_dv.getItsFargID(), new_dv.getItsFargID());
                }

                if ( ! completed )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test failed to " +
                            "complete (progress = %d).\n",
                            targetDesc, idx, newArgDesc, progress);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                            "system error(1): \"%s\"\n",
                            targetDesc, idx, newArgDesc,
                            systemErrorExceptionString);

                }
            }
        }

        if ( new_dv instanceof UndefinedDataValue )
        {
            long target_mve_ID = DBIndex.INVALID_ID;
            String old_dv_val = null;
            String new_dv_val = null;
            String farg_name = null;
            MatrixVocabElement target_mve = null;

            try
            {
                if ( old_dv instanceof UndefinedDataValue )
                {
                    old_dv_val = ((UndefinedDataValue)old_dv).getItsValue();
                }
                new_dv_val = ((UndefinedDataValue)new_dv).getItsValue();
                target_mve_ID = target.getMveID();
                target_mve = target.getDB().getMatrixVE(target_mve_ID);
                farg_name = target_mve.getFormalArg(idx).getFargName();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                                "system error(2): \"%s\"\n",
                                targetDesc, idx, newArgDesc,
                                systemErrorExceptionString);
            }

            if ( ( old_dv instanceof UndefinedDataValue ) &&
                 ( old_dv_val == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( new_dv_val == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) && ( old_dv_val == new_dv_val ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args sharing a string.\n",
                        targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) &&
                 ( old_dv_val.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args with different " +
                        "values: \"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, old_dv_val, new_dv_val);
                }
            }

            if ( farg_name == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test couldn't " +
                            "get untyped arg name.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( farg_name != null ) &&
                 ( old_dv_val != null ) &&
                 ( farg_name.compareTo(old_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, old_dv_val);
                }
            }

            if ( ( farg_name != null ) &&
                 ( farg_name.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, new_dv_val);
                }
            }
        }

        return failures;

    } /* ColPred::VerifyArgListAssignment() */


    /**
     * VerifyArgListAsgnmntFails()
     *
     * Verify that the specified replacement of an argument list
     * entry fails.
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None
     */

    private static int VerifyArgListAsgnmntFails(ColPred target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test completed.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test " +
                            "failed to throw a system error.\n",
                            targetDesc, idx, newArgDesc);

                }
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( new_dv == null ) ||
             ( new_dv != old_dv ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( new_dv != old_dv )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.getArg(%d) test failed to complete.\n",
                        targetDesc, idx);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.getArg(%d) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx,
                            systemErrorExceptionString);

                }
            }
        }

        return failures;

    } /* ColPred::VerifyArgListAsgnmntFails() */


    /**
     * VerifyColPredCopy()
     *
     * Verify that the supplied instances of ColPred are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyColPredCopy(ColPred base,
                                        ColPred copy,
                                        java.io.PrintStream outStream,
                                        boolean verbose,
                                        String baseDesc,
                                        String copyDesc)
    {
        int failures = 0;
        int i;

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
        else if ( base == copy )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.getDB() != copy.getDB() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.mveID != copy.mveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.mveID == %d != %s.mveID == %d.\n",
                                 baseDesc, base.mveID, copyDesc, copy.mveID);
            }
        }
        else if ( ( base.mveName == copy.mveName ) &&
                  ( base.mveName != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                     "%s.mveName and %s.mveName refer to the same string.\n",
                      baseDesc, copyDesc);
            }
        }
        else if ( base.varLen != copy.varLen )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.varLen != %s.varLen.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList == copy.argList ) &&
                  ( base.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share an argList.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList == null ) &&
                  ( copy.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList != null ) &&
                  ( copy.argList == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  copyDesc, baseDesc);
            }
        }
        else if ( base.argList != null )
        {
            i = 0;
            while ( ( i < base.argList.size() ) && ( failures == 0 ) )
            {
                failures += DataValueTest.VerifyDVCopy(base.argList.get(i),
                                          copy.argList.get(i),
                                          outStream,
                                          verbose,
                                          baseDesc + "argList.get(" + i + ")",
                                          copyDesc + "argList.get(" + i + ")");
                i++;
            }
        }

        return failures;

    } /* ColPred::VerifyColPredCopy() */

}