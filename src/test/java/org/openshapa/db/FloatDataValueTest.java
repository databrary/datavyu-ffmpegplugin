package org.openshapa.db;

import java.io.PrintStream;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class FloatDataValue.
 */
public class FloatDataValueTest extends DataValueTest {
    /** tolerance for double comparisons in tests. */
    private static final double DELTA = 0.001;

    /** Database for tests. */
    private Database db;
    /** MatrixVocabElement for tests. */
    private MatrixVocabElement floatMve;
    /** FloatFormalArg for tests. */
    private FloatFormalArg ffa;
    /** A basic fload data value. */
    private FloatDataValue fdv;

    /** MatrixVocalElement 2 for tests. */
    private MatrixVocabElement floatMve2;
    /** FloatFormalArg 2 for tests. */
    private FloatFormalArg ffa2;

    private PrintStream outStream;
    private boolean verbose;

    @Override
    public DataValue getInstance() {
        return fdv;
    }

    /**
     * Default test constructor.
     */
    public FloatDataValueTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Before
    public void setUp() throws SystemErrorException, LogicErrorException {
        db = new ODBCDatabase();

        floatMve = new MatrixVocabElement(db, "float_mve");
        floatMve.setType(MatrixVocabElement.MatrixType.FLOAT);

        ffa = new FloatFormalArg(db);
        floatMve.appendFormalArg(ffa);
        db.vl.addElement(floatMve);

        floatMve2 = new MatrixVocabElement(db, "float_mve2");
        floatMve2.setType(MatrixVocabElement.MatrixType.FLOAT);

        ffa2 = new FloatFormalArg(db);
        ffa2.setRange(-100.0, 100.0);
        floatMve2.appendFormalArg(ffa2);
        db.vl.addElement(floatMve2);
        fdv = new FloatDataValue(db);

        outStream = System.out;
        verbose = true;
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of updateForFargChange method, of class FloatDataValue.
     */
    @Test
    @Override
    public void testUpdateForFargChange() throws Exception {
    }


    /**
     * Test of updateSubRange method, of class FloatDataValue.
     */
    @Test
    @Override
    public void testUpdateSubRange() throws Exception {
    }

    /**
     * Test 1 arg constructor, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db);

        assertNotNull(float_value);
        assertNotNull(db);

        assertEquals(float_value.getDB(), db);
        assertEquals(float_value.getMaxVal(), 0.0, DELTA);
        assertEquals(float_value.getMinVal(), 0.0, DELTA);
    }

    /**
     * Test 2 argument constructor, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, ffa.getID());
        FloatDataValue float_value2 = new FloatDataValue(db, ffa2.getID());

        assertNotNull(db);
        assertNotNull(floatMve);
        assertNotNull(ffa);
        assertNotNull(floatMve2);
        assertNotNull(ffa2);

        assertEquals(float_value.getSubRange(), ffa.getSubRange());
        assertEquals(float_value.getItsValue(),
                                               float_value.getDefault(), DELTA);
        assertEquals(float_value.getMaxVal(), 0, DELTA);
        assertEquals(float_value.getMinVal(), 0, DELTA);

        assertEquals(float_value2.getSubRange(), ffa2.getSubRange());
        assertEquals(float_value2.getItsValue(),
                                              float_value2.getDefault(), DELTA);
        assertEquals(float_value2.getMaxVal(), ffa2.getMaxVal());
        assertEquals(float_value2.getMinVal(), ffa2.getMinVal());
    }

    /**
     * Test0 of 2 arg constructor failre, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(null, ffa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, floatMve.getID());
    }

    /**
     * Test of 3 arg constructor, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        FloatDataValue f_value0 = new FloatDataValue(db, ffa.getID(), 200.0);
        FloatDataValue f_value1 = new FloatDataValue(db, ffa2.getID(), 1.0);
        FloatDataValue f_value2 = new FloatDataValue(db, ffa2.getID(), 200.0);

        assertNotNull(db);
        assertNotNull(floatMve);
        assertNotNull(ffa);
        assertNotNull(floatMve2);
        assertNotNull(ffa2);

        assertNotNull(f_value0);
        assertNotNull(f_value1);
        assertNotNull(f_value2);

        assertEquals(f_value0.getSubRange(), ffa.getSubRange());
        assertEquals(f_value0.getItsValue(), 200.0, DELTA);
        assertEquals(f_value0.getMaxVal(), 0.0, DELTA);
        assertEquals(f_value0.getMinVal(), 0.0, DELTA);

        assertEquals(f_value1.getSubRange(), ffa2.getSubRange());
        assertEquals(f_value1.getItsValue(), 1.0, DELTA);
        assertEquals(f_value1.getMaxVal(), ffa2.getMaxVal(), DELTA);
        assertEquals(f_value1.getMinVal(), ffa2.getMinVal(), DELTA);

        assertEquals(f_value2.getSubRange(), ffa2.getSubRange());
        assertEquals(f_value2.subRange, ffa2.getSubRange());
        assertEquals(f_value2.getItsValue(), ffa2.getMaxVal(), DELTA);
        assertEquals(f_value2.getMaxVal(), ffa2.getMaxVal(), DELTA);
        assertEquals(f_value2.getMinVal(), ffa2.getMinVal(), DELTA);
    }

    /**
     * Test0 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(null, ffa.getID(), 1.0);
    }

    /**
     * Test1 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue f_value =
                                new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
    }

    /**
     * Test2 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, floatMve.getID(), 1.0);
    }

    /**
     * Test of copy constructor, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa.getID(), 200.0);
        FloatDataValue f_copy = new FloatDataValue(f_value);

        assertNotSame(f_value, f_copy);
        assertEquals(f_value.getDB(), f_copy.getDB());
        assertEquals(f_value.itsFargID, f_copy.itsFargID);
        assertEquals(f_value.itsFargType, f_copy.itsFargType);
        assertEquals(f_value.subRange, f_copy.subRange);
        assertEquals(f_value.toString(), f_copy.toString());
        assertEquals(f_value.toDBString(), f_copy.toDBString());
        assertEquals(f_value.getClass(), f_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class FloatDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue((FloatDataValue) null);
    }

    /**
     * Test of getItsValue method, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);

        assertEquals(f_value.getItsValue(), 50.0, DELTA);
    }

    /**
     * Test of setItsValue method, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);

        f_value.setItsValue(3.0);
        assertEquals(f_value.getItsValue(), 3.0, DELTA);
    }

    /**
     * Test of toString method, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);
        assertEquals(f_value.toString(), "50.000000");
    }

    /**
     * Test of toDBString method, of class FloatDataValue.
     *
     * @throws org.openshapa.cont.SystemErrorException on failure.
     */
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(FloatDataValue (id 0) " +
                                "(itsFargID 8) " +
                                "(itsFargType FLOAT) " +
                                "(itsCellID 0) " +
                                "(itsValue 50.0) " +
                                "(subRange true) " +
                                "(minVal -100.0) " +
                                "(maxVal 100.0))";

        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);
        assertEquals(f_value.toDBString(), testDBString0);
    }

    /**
     * Test of coerceToRange method, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws SystemErrorException {
        FloatDataValue int_value = new FloatDataValue(db, ffa2.getID(), 50.0);
        assertEquals(int_value.coerceToRange(100.0001), 100.0, DELTA);
        assertEquals(int_value.coerceToRange(100.0), 100.0, DELTA);
        assertEquals(int_value.coerceToRange(99.9999), 99.999, DELTA);
        assertEquals(int_value.coerceToRange(47.0), 47.0, DELTA);
        assertEquals(int_value.coerceToRange(-25.5), -25.5, DELTA);
        assertEquals(int_value.coerceToRange(-99.999), -99.999, DELTA);
        assertEquals(int_value.coerceToRange(-100.0), -100.0, DELTA);
        assertEquals(int_value.coerceToRange(-100.0001), -100.0, DELTA);
    }

    /**
     * Test of Construct method, of class FloatDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        FloatDataValue test = FloatDataValue.Construct(db, 50.0);
        assertEquals(test.getItsValue(), 50.0);
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        FloatDataValue value0 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue value1 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue value2 = new FloatDataValue(db, ffa.getID(), 100.001);

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        FloatDataValue value0 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue value1 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue value2 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue value3 = new FloatDataValue(db, ffa.getID(), 100.001);

        super.testEquals(value0, value1, value2, value3);
    }

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for FloatDataValue.  Verify that all
     *         fields are set to the expected defaults.
     *
     *      b) Verify that the one argument constructor fails on invalid
     *         input.  Given the compiler checks, this probably just means
     *         verifying that the constructor fails on null.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database, and a mve (matrix vocab element) with one
     *         formal argument.  Insert the mve into the database, and make
     *         note of the IDs assigned to them (including the formal argument).
     *
     *         Construct a FloatDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the FloatDataValue's itsFargID,
     *         itsFargType, subRange, minVal, and maxVal fields matches
     *         thos of the formal argument, and that all other fields are set
     *         to the expected defaults.
     *
     *         Repeat for a variety of formal argument types and settings.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      As per two argument constructor, save that a value is supplied
     *      to the constructor.  Verify that this value appears in the
     *      FloatDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  FloatDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         FloatDataValue, and verify that the copy is correct.
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), setItsValue() and coerceToRange()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the DataValue.TestAccessors()
     *      method.
     *
     *      Given compiler error checking, there isn't any way to feed
     *      invalid data to the getItsValue(), setItsValue() and coerceToRange()
     *
     * 6) toString methods:
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
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        FloatDataValue fdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            fdv = new FloatDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( fdv == null ) ||
             ( ! completed ) ||
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

                if ( fdv == null )
                {
                    outStream.print(
                            "new FloatDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new FloatDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new FloatDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db, fdv, outStream,
                                                           verbose);

            try {
                Double fdvitsValue = (Double) PrivateAccessor.getField(fdv, "itsValue");            
                Double fdvItsDefault = (Double) PrivateAccessor.getField(fdv, "ItsDefault");
                Double fdvmaxVal = (Double) PrivateAccessor.getField(fdv, "maxVal");            
                Double fdvminVal = (Double) PrivateAccessor.getField(fdv, "minVal");            

                if ( !FloatDataValue.closeEnough(fdvitsValue, fdvItsDefault))
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "fdv.itsValue = %f != fdv.ItsDefault = %f.\n",
                                fdvitsValue, fdvItsDefault);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvmaxVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("bad initial value of fdv.maxVal: %f.\n",
                                         fdvmaxVal);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvminVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("bad initial value of fdv.minVal: %f.\n",
                                         fdvminVal);
                    }
                }
            } catch (Throwable th) {
                failures++;
                if ( verbose ) {
                    outStream.printf("Problem with PrivateAccessor and fdv.");
                }                
            }

        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print(
                                "new FloatDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null) failed to throw " +
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

    } /* FloatDataValue::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 2 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID());

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr = new FloatDataValue(db, ffa_sr.getID());

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr == null ) ||
             ( ! completed ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print(
                        "new FloatDataValue(db, ffa.getID()) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID()) " +
                                    "returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ffa,
                                                               fdv,
                                                               outStream,
                                                               verbose,
                                                              "fdv");

            if ( fdv.subRange != ffa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.subRange doesn't match ffa.getSubRange().\n");
                }
            }

            try {
                Double fdvitsValue = (Double) PrivateAccessor.getField(fdv, "itsValue");
                Double fdvItsDefault = (Double) PrivateAccessor.getField(fdv, "ItsDefault");
                Double fdvmaxVal = (Double) PrivateAccessor.getField(fdv, "maxVal");
                Double fdvminVal = (Double) PrivateAccessor.getField(fdv, "minVal");

                if ( !FloatDataValue.closeEnough(fdvitsValue, fdvItsDefault) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "fdv.itsValue = %f != fdv.ItsDefault = %f.\n",
                                fdvitsValue, fdvItsDefault);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvmaxVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv.maxVal: %f (0.0).\n",
                                fdvmaxVal);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvminVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv.minVal: %f (0.0).\n",
                                fdvminVal);
                    }
                }
            } catch (Throwable th) {
                failures++;
                if ( verbose ) {
                    outStream.printf("Problem with PrivateAccessor and fdv.");
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr");

            if ( fdv_sr.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            try {
                Double fdv_sritsValue = (Double) PrivateAccessor.getField(fdv_sr, "itsValue");
                Double fdv_srItsDefault = (Double) PrivateAccessor.getField(fdv_sr, "ItsDefault");
                Double fdv_srmaxVal = (Double) PrivateAccessor.getField(fdv_sr, "maxVal");
                Double fdv_srminVal = (Double) PrivateAccessor.getField(fdv_sr, "minVal");

                if ( !FloatDataValue.closeEnough(fdv_sritsValue, fdv_srItsDefault) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "fdv_sr.itsValue = %f != fdv_sr.ItsDefault = %f.\n",
                                fdv_sritsValue, fdv_srItsDefault);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_srmaxVal, ffa_sr.getMaxVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr.maxVal: %f (%f).\n",
                                fdv_srmaxVal, ffa_sr.getMaxVal());
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_srminVal, ffa_sr.getMinVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr.minVal: %f (%f).\n",
                                fdv_srminVal, ffa_sr.getMinVal());
                    }
                }
            } catch (Throwable th) {
                failures++;
                if (verbose) {
                    outStream.printf("Problem with PrivateAccessor and fdv.");
                }
            }
        }
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null, ffa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID()) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null, ffa.getID())" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(db, INVALID_ID)" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, float_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "float_mve.getID()) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "float_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(db, float_mve.getID()) " +
                                "failed to throw a system error exception.\n");
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

    } /* FloatDataValue::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_sr0 = null;
        FloatDataValue fdv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID(), 200.0);

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr0 = new FloatDataValue(db, ffa_sr.getID(), 1.0);
            fdv_sr1 = new FloatDataValue(db, ffa_sr.getID(), 200.0);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr0 == null ) ||
             ( fdv_sr1 == null ) ||
             ( ! completed ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "1.0) returned null.\n");
                }

                if ( fdv_sr1 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ffa,
                                                               fdv,
                                                               outStream,
                                                               verbose,
                                                               "fdv");

            if ( fdv.subRange != ffa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.subRange doesn't match ffa.getSubRange().\n");
                }
            }

            try {
                Double fdvitsValue = (Double) PrivateAccessor.getField(fdv, "itsValue");
                Double fdvItsDefault = (Double) PrivateAccessor.getField(fdv, "ItsDefault");
                Double fdvmaxVal = (Double) PrivateAccessor.getField(fdv, "maxVal");
                Double fdvminVal = (Double) PrivateAccessor.getField(fdv, "minVal");

                if ( !FloatDataValue.closeEnough(fdvitsValue, 200.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("fdv.itsValue = %f != 200.0.\n",
                                         fdvitsValue);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvmaxVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv.maxVal: %f (0.0).\n",
                                fdvmaxVal);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdvminVal, 0.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv.minVal: %f (0.0).\n",
                                fdvminVal);
                    }
                }
            } catch (Throwable th) {
                failures++;
                if (verbose) {
                    outStream.printf("Problem with PrivateAccessor and fdv.");
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr0");

            if ( fdv_sr0.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr0.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            try {
                Double fdv_sr0itsValue = (Double) PrivateAccessor.getField(fdv_sr0, "itsValue");
                Double fdv_sr0ItsDefault = (Double) PrivateAccessor.getField(fdv_sr0, "ItsDefault");
                Double fdv_sr0maxVal = (Double) PrivateAccessor.getField(fdv_sr0, "maxVal");
                Double fdv_sr0minVal = (Double) PrivateAccessor.getField(fdv_sr0, "minVal");

                if ( !FloatDataValue.closeEnough(fdv_sr0itsValue, 1.0) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("fdv_sr.itsValue = %f != 1.0.\n",
                                         fdv_sr0itsValue);
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_sr0maxVal, ffa_sr.getMaxVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr0.maxVal: %f (%f).\n",
                                fdv_sr0maxVal, ffa_sr.getMaxVal());
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_sr0minVal, ffa_sr.getMinVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr0.minVal: %f (%f).\n",
                                fdv_sr0minVal, ffa_sr.getMinVal());
                    }
                }
            } catch (Throwable th) {
                failures++;
                if (verbose) {
                    outStream.printf("Problem with PrivateAccessor and fdv_sr.");
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr1");

            if ( fdv_sr1.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr0.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            try {
                Double fdv_sr1itsValue = (Double) PrivateAccessor.getField(fdv_sr1, "itsValue");
                Double fdv_sr1maxVal = (Double) PrivateAccessor.getField(fdv_sr1, "maxVal");
                Double fdv_sr1minVal = (Double) PrivateAccessor.getField(fdv_sr1, "minVal");

                if ( !FloatDataValue.closeEnough(fdv_sr1itsValue, ffa_sr.getMaxVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("fdv_sr1.itsValue = %f != %f.\n",
                                         fdv_sr1itsValue, ffa_sr.getMaxVal());
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_sr1maxVal, ffa_sr.getMaxVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr1.maxVal: %f (%f).\n",
                                fdv_sr1maxVal, ffa_sr.getMaxVal());
                    }
                }

                if ( !FloatDataValue.closeEnough(fdv_sr1minVal, ffa_sr.getMinVal()) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "bad initial value of fdv_sr1.minVal: %f (%f).\n",
                                fdv_sr1minVal, ffa_sr.getMinVal());
                    }
                }
            } catch (Throwable th) {
                failures++;
                if (verbose) {
                    outStream.printf("Problem with PrivateAccessor and fdv_sr1.");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null, ffa.getID(), 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID(), 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                "ffa.getID(), 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(null, ffa.getID(), 1.0) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID, 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "INVALID_ID, 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(db, INVALID_ID, 1.0) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, float_mve.getID(), 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "float_mve.getID(), 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "float_mve.getID(), 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new FloatDataValue(db, float_mve.getID(), 1.0) " +
                            "failed to throw a system error exception.\n");
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

    } /* FloatDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class FloatDataValue accessors                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve = null;
        FloatFormalArg ffa = null;
        UnTypedFormalArg ufa = null;
        FloatDataValue fdv0 = null;
        FloatDataValue fdv1 = null;
        FloatDataValue fdv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv0 = null;
        fdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            ffa.setRange(-1000.0, +1000.0);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv0 = new FloatDataValue(db, ffa.getID(), 200.0);

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            fdv1 = new FloatDataValue(db, ufa.getID(), 2000.0);
            fdv2 = new FloatDataValue(db, ufa.getID(), 999.999);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( fdv1 == null ) ||
             ( fdv2 == null ) ||
             ( ! completed ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.\n");
                }

                if ( fdv0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( fdv1 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "2000.0) returned null.\n");
                }

                if ( fdv2 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "999.999) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }


        if ( failures == 0 )
        {
            failures += DataValueTest.TestAccessors(db, ffa, matrix_mve, ufa,
                                                fdv0, outStream, verbose);


            if ( fdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv0.getSubRange() != false");
                }
            }

            if ( fdv0.getItsValue() != 200.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv.getItsValue() != 200.0\n");
                }
            }

            fdv0.setItsValue(3.14159);


            if ( fdv0.getItsValue() != 3.14159 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv0.getItsValue() != 3.14159\n");
                }
            }

            /************************************/

            if ( fdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getSubRange() != false\n");
                }
            }

            if ( fdv1.getItsValue() != 2000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != 2000.0\n");
                }
            }


            failures += DataValueTest.TestAccessors(db, ufa, float_mve, ffa,
                                                fdv1, outStream, verbose);

            if ( fdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getSubRange() != true\n");
                }
            }

            if ( fdv1.getItsValue() != 1000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != 1000.0\n");
                }
            }

            fdv1.setItsValue(-50000.0);

            if ( fdv1.getItsValue() != -1000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != -1000.0\n");
                }
            }

            if ( ( fdv1.coerceToRange(1000.0001) != 1000.0 ) ||
                 ( fdv1.coerceToRange(1000.0) != 1000.0 ) ||
                 ( fdv1.coerceToRange(999.9999) != 999.9999 ) ||
                 ( fdv1.coerceToRange(999.0) != 999.0 ) ||
                 ( fdv1.coerceToRange(47.0) != 47.0 ) ||
                 ( fdv1.coerceToRange(-25.5) != -25.5 ) ||
                 ( fdv1.coerceToRange(-999.999) != -999.999 ) ||
                 ( fdv1.coerceToRange(-1000.0) != -1000.0 ) ||
                 ( fdv1.coerceToRange(-1000.00001) != -1000.0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from fdv1.coerceToRange()\n");
                }
            }

            /************************************/

            failures += DataValueTest.TestAccessors(db, ufa, float_mve, ffa,
                                                fdv2, outStream, verbose);

            if ( fdv2.getItsValue() != 999.999 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv2.getItsValue() != 999.999\n");
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

    } /* FloatDataValue::TestAccessors() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        String testBanner =
            "Testing copy constructor for class FloatDataValue                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_copy = null;
        FloatDataValue fdv_sr0 = null;
        FloatDataValue fdv_sr0_copy = null;
        FloatDataValue fdv_sr1 = null;
        FloatDataValue fdv_sr1_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID(), 200.0);

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr0 = new FloatDataValue(db, ffa_sr.getID(), 1.0);
            fdv_sr1 = new FloatDataValue(db, ffa_sr.getID(), 200.0);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr0 == null ) ||
             ( fdv_sr1 == null ) ||
             ( ! completed ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "1.0) returned null.\n");
                }

                if ( fdv_sr1 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            fdv_copy = null;
            fdv_sr0_copy = null;
            fdv_sr1_copy = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                fdv_copy = new FloatDataValue(fdv);
                fdv_sr0_copy = new FloatDataValue(fdv_sr0);
                fdv_sr1_copy = new FloatDataValue(fdv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv_copy == null ) ||
                 ( fdv_sr0_copy == null ) ||
                 ( fdv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fdv_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv) returned null.\n");
                    }

                    if ( fdv_sr0_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv_sr0) returned null.\n");
                    }

                    if ( fdv_sr1_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv_sr1) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.VerifyDVCopy(fdv, fdv_copy, outStream,
                                               verbose, "fdv", "fdv_copy");

            failures += DataValueTest.VerifyDVCopy(fdv_sr0, fdv_sr0_copy, outStream,
                                            verbose, "fdv_sr0", "fdv_sr0_copy");

            failures += DataValueTest.VerifyDVCopy(fdv_sr1, fdv_sr1_copy, outStream,
                                            verbose, "fdv_sr1", "fdv_sr1_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((FloatDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null) completed.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print(
                            "new FloatDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null) " +
                                "failed to throw a system error exception.\n");
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
    } /* FloatDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                               -- 11/13/07
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
        String testString0 = "200.000000";
        String testDBString0 = "(FloatDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 500) " +
                                    "(itsValue 200.0) " +
                                    "(subRange true) " +
                                    "(minVal -1000.0) " +
                                    "(maxVal 1000.0))";
        String testString1 = "2000.000000";
        String testDBString1 = "(FloatDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue 2000.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve = null;
        FloatFormalArg ffa = null;
        UnTypedFormalArg ufa = null;
        FloatDataValue fdv0 = null;
        FloatDataValue fdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv0 = null;
        fdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            ffa.setRange(-1000.0, +1000.0);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv0 = new FloatDataValue(db, ffa.getID(), 200.0);
            fdv0.setID(100);      // invalid value for print test
            fdv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            fdv1 = new FloatDataValue(db, ufa.getID(), 2000.0);
            fdv1.setID(101);        // invalid value for print test
            fdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( fdv1 == null ) ||
             ( ! completed ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.\n");
                }

                if ( fdv0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( fdv1 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "100.0) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( fdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv0.toString(): \"%s\".\n",
                                     fdv0.toString());
                }
            }

            if ( fdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv0.toDBString(): \"%s\".\n",
                                     fdv0.toDBString());
                }
            }

            if ( fdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv1.toString(): \"%s\".\n",
                                     fdv1.toString());
                }
            }

            if ( fdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv1.toDBString(): \"%s\".\n",
                                     fdv1.toDBString());
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

    } /* FloatDataValue::TestToStringMethods() */


    /**
     * VerifyFloatDVCopy()
     *
     * Verify that the supplied instances of FloatDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyFloatDVCopy(FloatDataValue base,
                                        FloatDataValue copy,
                                        java.io.PrintStream outStream,
                                        boolean verbose,
                                        String baseDesc,
                                        String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyFloatDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyFloatDVCopy: %s null on entry.\n",
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
        else {
            try {
                Double baseitsValue = (Double) PrivateAccessor.getField(base, "itsValue");
                Double copyitsValue = (Double) PrivateAccessor.getField(copy, "itsValue");
                Double basemaxVal = (Double) PrivateAccessor.getField(base, "maxVal");
                Double copymaxVal = (Double) PrivateAccessor.getField(copy, "maxVal");
                Double baseminVal = (Double) PrivateAccessor.getField(base, "minVal");
                Double copyminVal = (Double) PrivateAccessor.getField(copy, "minVal");

                if ( !FloatDataValue.closeEnough(baseitsValue, copyitsValue) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s.itsValue != %s.itsValue.\n",
                                          baseDesc, copyDesc);
                    }
                }
                else if ( !FloatDataValue.closeEnough(basemaxVal, copymaxVal) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s.maxVal != %s.maxVal.\n",
                                          baseDesc, copyDesc);
                    }
                }
                else if ( !FloatDataValue.closeEnough(baseminVal, copyminVal) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("%s.minVal != %s.minVal.\n",
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
                        outStream.printf(
                                "%s.toDBString() = \"%s\".\n%s.toDBString() = \"%s\".\n",
                                baseDesc, base.toDBString(),
                                copyDesc, copy.toDBString());
                    }
                }
            } catch (Throwable th) {
                failures++;
                if (verbose) {
                    outStream.printf("Problem with PrivateAccessor and base or copy.");
                }
            }
        }

        return failures;

    } /* FloatDataValue::VerifyFloatDVCopy() */

}
