package org.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class TimeStampDataValueTest extends DataValueTest {

    public TimeStampDataValueTest() {
    }

    Database db;

    TimeStampFormalArg tfa;
    TimeStampDataValue tsdv;

    TimeStampFormalArg tfa2;

    private PrintStream outStream;
    private boolean verbose;

    @Override
    public DataValue getInstance() {
        return tsdv;
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

        MatrixVocabElement matrix_mve0 = new MatrixVocabElement(db, "mve0");
        matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
        tfa = new TimeStampFormalArg(db, "<tfa>");
        tfa2 = new TimeStampFormalArg(db, "<tfa2>");
        matrix_mve0.appendFormalArg(tfa);
        matrix_mve0.appendFormalArg(tfa2);
        db.vl.addElement(matrix_mve0);
        
        tsdv = new TimeStampDataValue(db);

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
     * Test of updateForFargChange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateForFargChange() throws Exception {
    }

    /**
     * Test of updateSubRange method.
     */
    @Test
    @Override
    public void testUpdateSubRange() throws Exception {
    }

    /**
     * Test 1 arg constructor, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db);

        assertNotNull(db);
        assertNotNull(t_value);

        assertNotNull(t_value.ItsDefault);
        assertEquals(t_value.itsValue, t_value.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db, tfa.getID());

        assertNotNull(db);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertNotNull(t_value.ItsDefault);
        assertEquals(t_value.subRange, tfa.getSubRange());
        assertEquals(t_value.itsValue, t_value.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue((Database) null,
                                                              tfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db,
                                                            DBIndex.INVALID_ID);
    }

    /**
     * Test of 3 argument constructor, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db, tfa.getID(),
                                          new TimeStamp(60, 60));

        assertNotNull(db);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertEquals(t_value.subRange, tfa.getSubRange());
        assertNotNull(t_value.itsValue);
        assertEquals(t_value.itsValue, new TimeStamp(60, 60));
    }

    /**
     * Test0 of 3 argument constructor failure, of class TimeStampDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue((Database) null,
                                                              tfa.getID(),
                                                           new TimeStamp(1000));
    }

    /**
     * Test1 of 3 argument constructor failure, of class TimeStampDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db,
                                                             DBIndex.INVALID_ID,
                                                            new TimeStamp(1000));
    }

    /**
     * Test of copy constructor, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db, tfa.getID(),
                                                        new TimeStamp(60, 60));
        TimeStampDataValue t_copy = new TimeStampDataValue(t_value);

        assertNotSame(t_value, t_copy);
        assertEquals(t_value.getDB(), t_copy.getDB());
        assertEquals(t_value.itsFargID, t_copy.itsFargID);
        assertEquals(t_value.itsFargType, t_copy.itsFargType);
        assertEquals(t_value.subRange, t_copy.subRange);
        assertEquals(t_value.toString(), t_copy.toString());
        assertEquals(t_value.toDBString(), t_copy.toDBString());
        assertEquals(t_value.getClass(), t_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        TimeStampDataValue t_value =
                            new TimeStampDataValue((TimeStampDataValue) null);
    }

    /**
     * Test of getItsValue method, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db, tfa.getID(),
                                                         new TimeStamp(60, 60));

        assertEquals(t_value.getItsValue(), new TimeStamp(60, 60));
    }

    /**
     * Test of setItsValue method, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        TimeStampDataValue t_value = new TimeStampDataValue(db, tfa.getID(),
                                                         new TimeStamp(60, 60));

        t_value.setItsValue(new TimeStamp(60, 2000));
        assertEquals(t_value.getItsValue(), new TimeStamp(60, 2000));
    }

    /**
     * Test of toString method, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        TimeStampDataValue t_value0 = new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));

        TimeStampDataValue t_value1 = new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 2000));

        String t0 = t_value0.toString();
        String t1 = t_value1.toString();
        assertEquals(t0, "00:00:16:040");
        assertEquals(t1, "00:00:33:020");
    }

    /**
     * Test of Construct method, of class TimeStampDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        TimeStampDataValue test = TimeStampDataValue.Construct(db, 1000);
        assertEquals(test.getItsValue(), new TimeStamp(60, 1000));
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        TimeStampDataValue value0 =
                            new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));
        TimeStampDataValue value1 =
                            new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));
        TimeStampDataValue value2 =
                          new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 2000));

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        TimeStampDataValue value0 =
                            new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));
        TimeStampDataValue value1 =
                            new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));
        TimeStampDataValue value2 =
                            new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 1000));
        TimeStampDataValue value3 =
                          new TimeStampDataValue(db, tfa.getID(),
                                                       new TimeStamp(60, 2000));

        super.testEquals(value0, value1, value2, value3);
    }

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for TimeStampDataValue.  Verify that all
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
     *         Construct a TimeStampDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the TimeStampDataValue's itsFargID,
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
     *      TimeStampDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  TimeStampDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         TimeStampDataValue, and verify that the copy is correct.
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
    public void Test1ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 1 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        TimeStampDataValue tsdv_l = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        tsdv_l = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();
            tsdv_l = new TimeStampDataValue(db_l);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db_l == null ) ||
             ( tsdv_l == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( tsdv_l == null )
                {
                    outStream.print(
                            "new TimeStampDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new TimeStampDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TimeStampDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db_l, tsdv_l, outStream,
                                                           verbose);

            if ( ! tsdv_l.itsValue.eq(tsdv_l.ItsDefault) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != " +
                            "tsdv.ItsDefault = (%d,%d).\n",
                            tsdv_l.itsValue.getTPS(),
                            tsdv_l.itsValue.getTicks(),
                            tsdv_l.ItsDefault.getTPS(),
                            tsdv_l.ItsDefault.getTicks());
                }
            }

            if ( tsdv_l.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: %d.\n",
                                     tsdv_l.maxVal);
                }
            }

            if ( tsdv_l.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: %d.\n",
                                     tsdv_l.minVal);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new TimeStampDataValue(null) returned.\n");
                    }

                    if ( tsdv_l != null )
                    {
                        outStream.print("new TimeStampDataValue(null) " +
                                        "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null) failed " +
                                "to throw a system error exception.\n");
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
    } /* TimeStampDataValue::Test1ArgConstructor() */


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
            "Testing 2 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement ts_mve = null;
        MatrixVocabElement ts_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv = null;
        TimeStampDataValue tsdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            ts_mve = new MatrixVocabElement(db, "ts_mve");
            ts_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            ts_mve.appendFormalArg(tsfa);
            db.vl.addElement(ts_mve);

            tsdv = new TimeStampDataValue(db, tsfa.getID());

            ts_mve_sr = new MatrixVocabElement(db, "ts_mve_sr");
            ts_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db);
            tsfa_sr.setRange(new TimeStamp(db.getTicks(), 10 * db.getTicks()),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            ts_mve_sr.appendFormalArg(tsfa_sr);
            db.vl.addElement(ts_mve_sr);

            tsdv_sr = new TimeStampDataValue(db, tsfa_sr.getID());

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( ts_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv == null ) ||
             ( ts_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr == null ) ||
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

                if ( ts_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv == null )
                {
                    outStream.print("new TimeStampDataValue(db, tsfa.getID())" +
                                    "returned null.\n");
                }

                if ( ts_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr == null )
                {
                    outStream.print("new TimeStampDataValue(db, " +
                            "tsfa_sr.getID()) returned null.\n");
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
                                                               tsfa,
                                                               tsdv,
                                                               outStream,
                                                               verbose,
                                                              "tsdv");

            if ( tsdv.subRange != tsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv.subRange doesn't match tsfa.getSubRange().\n");
                }
            }

            if ( ! tsdv.itsValue.eq(tsdv.ItsDefault) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != " +
                            "tsdv.ItsDefault = (%d,%d).\n",
                            tsdv.itsValue.getTPS(),
                            tsdv.itsValue.getTicks(),
                            tsdv.ItsDefault.getTPS(),
                            tsdv.ItsDefault.getTicks());
                }
            }

            if ( tsdv.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.maxVal.getTPS(), tsdv.maxVal.getTicks());
                }
            }

            if ( tsdv.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.minVal.getTPS(), tsdv.minVal.getTicks());
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               tsfa_sr,
                                                               tsdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr");

            if ( tsdv_sr.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( tsdv_sr.itsValue.ne(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr.itsValue = (%d,%d) != " +
                            "tsfa_sr.getMinVal() = (%d,%d).\n",
                            tsdv_sr.itsValue.getTPS(),
                            tsdv_sr.itsValue.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }

            if ( tsdv_sr.maxVal.ne(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv_sr.maxVal: " +
                            "(%d,%d), (%d,%d) expected.\n",
                            tsdv_sr.maxVal.getTPS(),
                            tsdv_sr.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( tsdv_sr.minVal.ne(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv_sr.minVal: " +
                            "(%d,%d), (%d,%d) expected.\n",
                            tsdv_sr.minVal.getTPS(),
                            tsdv_sr.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue((Database)null, tsfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                        "tsfa.getID()) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                        "tsfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null, tsfa.getID())" +
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
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampDataValue(db, INVALID_ID)" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         *refer to a formal argument.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, ts_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "ts_mve.getID()) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "ts_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampDataValue(db, ts_mve.getID()) " +
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
    } /* TimeStampDataValue::Test2ArgConstructor() */


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
            "Testing 3 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement ts_mve = null;
        MatrixVocabElement ts_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv_l = null;
        TimeStampDataValue tsdv_sr0 = null;
        TimeStampDataValue tsdv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        tsdv_l = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();

            ts_mve = new MatrixVocabElement(db_l, "ts_mve");
            ts_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db_l);
            ts_mve.appendFormalArg(tsfa);
            db_l.vl.addElement(ts_mve);

            tsdv_l = new TimeStampDataValue(db_l, tsfa.getID(),
                                          new TimeStamp(db_l.getTicks(), 60));

            ts_mve_sr = new MatrixVocabElement(db_l, "ts_mve_sr");
            ts_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db_l);
            tsfa_sr.setRange(new TimeStamp(db_l.getTicks(), 0),
                         new TimeStamp(db_l.getTicks(), 60 * 60 * db_l.getTicks()));
            ts_mve_sr.appendFormalArg(tsfa_sr);
            db_l.vl.addElement(ts_mve_sr);

            tsdv_sr0 = new TimeStampDataValue(db_l, tsfa_sr.getID(),
                     new TimeStamp(db_l.getTicks(), 60 * db_l.getTicks()));
            tsdv_sr1 = new TimeStampDataValue(db_l, tsfa_sr.getID(),
                  new TimeStamp(db_l.getTicks(), (60 * 60 * db_l.getTicks()) + 1));

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( ts_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv_l == null ) ||
             ( ts_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr0 == null ) ||
             ( tsdv_sr1 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ts_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv_l == null )
                {
                    outStream.print("allocation of tsdv failed.\n");
                }

                if ( ts_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr0 == null )
                {
                    outStream.print("allocation of tsdv_sr0 failed.\n");
                }

                if ( tsdv_sr1 == null )
                {
                    outStream.print("allocation of tsdv_sr1 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test setup failed to complete.\n");
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
            failures += DataValueTest.Verify2PlusArgInitialization(db_l,
                                                               tsfa,
                                                               tsdv_l,
                                                               outStream,
                                                               verbose,
                                                               "tsdv");

            if ( tsdv_l.subRange != tsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv.subRange doesn't match tsfa.getSubRange().\n");
                }
            }

            if ( ! tsdv_l.itsValue.eq(new TimeStamp(db_l.getTicks(), 60)) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != (%d,60).\n",
                                     tsdv_l.itsValue.getTPS(),
                                     tsdv_l.itsValue.getTicks(),
                                     db_l.getTicks());
                }
            }

            if ( tsdv_l.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv_l.maxVal.getTPS(), tsdv_l.maxVal.getTicks());
                }
            }

            if ( tsdv_l.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv_l.minVal.getTPS(), tsdv_l.minVal.getTicks());
                }
            }

            /**************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db_l,
                                                               tsfa_sr,
                                                               tsdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr0");

            if ( tsdv_sr0.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr0.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( ! tsdv_sr0.itsValue.eq(new TimeStamp(db_l.getTicks(),
                                                      60 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr0.itsValue = (%d,%d) != (%d,%d).\n",
                                     tsdv_sr0.itsValue.getTPS(),
                                     tsdv_sr0.itsValue.getTicks(),
                                     db_l.getTicks(),
                                     60 * db_l.getTicks());
                }
            }

            if ( ! tsdv_sr0.maxVal.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr0.maxVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr0.maxVal.getTPS(),
                            tsdv_sr0.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr0.minVal.eq(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr0.minVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr0.minVal.getTPS(),
                            tsdv_sr0.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }

            /*************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db_l,
                                                               tsfa_sr,
                                                               tsdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr1");

            if ( tsdv_sr1.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr1.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( ! tsdv_sr1.itsValue.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr1.itsValue = (%d,%d) != (%d,%d).\n",
                                     tsdv_sr1.itsValue.getTPS(),
                                     tsdv_sr1.itsValue.getTicks(),
                                     tsfa_sr.getMaxVal().getTPS(),
                                     tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr1.maxVal.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr1.maxVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr1.maxVal.getTPS(),
                            tsdv_sr1.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr1.minVal.eq(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr1.minVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr1.minVal.getTPS(),
                            tsdv_sr1.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue((Database)null, tsfa.getID(),
                                              new TimeStamp(db_l.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv_l != null )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                                "0)) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                                "0)) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue(db_l, DBIndex.INVALID_ID,
                                              new TimeStamp(db_l.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv_l != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "INVALID_ID, new TimeStamp(db.getTicks(), 0))" +
                                " returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "INVALID_ID, new TimeStamp(db.getTicks(), 0))" +
                                " returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "INVALID_ID, new TimeStamp(db.getTicks(), 0)) " +
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
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue(db_l, ts_mve.getID(),
                                              new TimeStamp(db_l.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) returned.\n");
                    }

                    if ( tsdv_l != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), 0)) " +
                            "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when supplied an invalid initial
         * value.
         */
        if ( failures == 0 )
        {
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue(db_l, tsfa.getID(),
                                           new TimeStamp(db_l.getTicks() + 1, 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) returned.\n");
                    }

                    if ( tsdv_l != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) failed to throw a system error exception.\n");
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
    } /* TimeStampDataValue::Test3ArgConstructor() */


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
            "Testing class TimeStampDataValue accessors                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        TimeStampFormalArg tsfa = null;
        UnTypedFormalArg ufa = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv1 = null;
        TimeStampDataValue tsdv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();

            matrix_mve0 = new MatrixVocabElement(db_l, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db_l);
            tsfa.setRange(new TimeStamp(db_l.getTicks(), 10 * db_l.getTicks()),
                         new TimeStamp(db_l.getTicks(), 60 * 60 * db_l.getTicks()));
            matrix_mve0.appendFormalArg(tsfa);
            db_l.vl.addElement(matrix_mve0);

            tsdv0 = new TimeStampDataValue(db_l, tsfa.getID(),
                    new TimeStamp(db_l.getTicks(), 60 * db_l.getTicks()));

            matrix_mve1 = new MatrixVocabElement(db_l, "matrix_mve");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db_l, "<untyped>");
            matrix_mve1.appendFormalArg(ufa);
            db_l.vl.addElement(matrix_mve1);

            tsdv1 = new TimeStampDataValue(db_l, ufa.getID(),
                    new TimeStamp(db_l.getTicks(), (60 * 60 * db_l.getTicks()) + 1));
            tsdv2 = new TimeStampDataValue(db_l, ufa.getID(),
                              new TimeStamp(db_l.getTicks(), 60 * db_l.getTicks()));

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( matrix_mve0 == null ) ||
             ( tsfa == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve1 == null ) ||
             ( ufa == null ) ||
             ( tsdv1 == null ) ||
             ( tsdv2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( matrix_mve0 == null )
                {
                    outStream.print("allocation of matrix_mve0 failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve0 == null )
                {
                    outStream.print("allocation of matrix_mve0 failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
                }

                if ( tsdv2 == null )
                {
                    outStream.print("allocation of tsdv2 failed.\n");
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
            failures += DataValueTest.TestAccessors(db_l, tsfa, matrix_mve1, ufa,
                                                tsdv0, outStream, verbose);

            if ( tsdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv0.getSubRange() != false");
                }
            }

            if ( ! tsdv0.getItsValue().eq(new TimeStamp(db_l.getTicks(),
                                                        60 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv0.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv0.itsValue.getTPS(),
                            tsdv0.itsValue.getTicks(),
                            db_l.getTicks(),
                            60 * db_l.getTicks());
                }
            }

            tsdv0.setItsValue(new TimeStamp(db_l.getTicks(), 30 * db_l.getTicks()));


            if ( ! tsdv0.getItsValue().eq(new TimeStamp(db_l.getTicks(),
                                                        30 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv0.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv0.itsValue.getTPS(),
                            tsdv0.itsValue.getTicks(),
                            db_l.getTicks(),
                            60 * db_l.getTicks());
                }
            }

            /************************************/

            if ( tsdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv1.getSubRange() != false\n");
                }
            }

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db_l.getTicks(),
                                               (60 * 60 * db_l.getTicks()) + 1)) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db_l.getTicks(),
                            (60 * 60 * db_l.getTicks()) + 1);
                }
            }

            failures += DataValueTest.TestAccessors(db_l, ufa, matrix_mve0, tsfa,
                                                tsdv1, outStream, verbose);

            if ( tsdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv1.getSubRange() != true\n");
                }
            }

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db_l.getTicks(),
                                                     60 * 60 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db_l.getTicks(),
                            60 * 60 * db_l.getTicks());
                }
            }

            tsdv1.setItsValue(new TimeStamp(db_l.getTicks(), 9 * db_l.getTicks()));

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db_l.getTicks(),
                                                        10 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db_l.getTicks(),
                            10 * db_l.getTicks());
                }
            }

            if ( ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()) + 1)).ne(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()))).ne(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()) - 1)).ne(new TimeStamp(db_l.getTicks(),
                    (60 * 60 * db_l.getTicks()) - 1)) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (30 * 60 * db_l.getTicks()))).ne(new TimeStamp(db_l.getTicks(),
                    (30 * 60 * db_l.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()) + 1)).ne(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()) + 1)) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()))).ne(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()) - 1)).ne(new TimeStamp(db_l.getTicks(),
                    (10 * db_l.getTicks()))) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from tsdv1.coerceToRange()\n");
                }
            }

            /************************************/

            failures += DataValueTest.TestAccessors(db_l, ufa, matrix_mve0, tsfa,
                                                tsdv2, outStream, verbose);

            if ( tsdv2.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv2.getSubRange() != true\n");
                }
            }

            if ( tsdv2.getItsValue().ne(new TimeStamp(db_l.getTicks(),
                                                      60 * db_l.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv2.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv2.itsValue.getTPS(),
                            tsdv2.itsValue.getTicks(),
                            db_l.getTicks(),
                            60 * db_l.getTicks());
                }
            }
        }

        /* verivy that setItsValue() fails when provided an invalid value */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv2.setItsValue(new TimeStamp(db_l.getTicks() - 1,
                                                30 * db_l.getTicks()));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "tsdv2.setItsValue(invalid_ts) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("tsdv2.setItsValue(invalid_ts) " +
                                "failed to throw a system error.\n");
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
    } /* TimeStampDataValue::TestAccessors() */


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
            "Testing copy constructor for class TimeStampDataValue            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement matrix_mve = null;
        MatrixVocabElement matrix_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv_l = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv0_copy = null;
        TimeStampDataValue tsdv1 = null;
        TimeStampDataValue tsdv1_copy = null;
        TimeStampDataValue tsdv2 = null;
        TimeStampDataValue tsdv2_copy = null;
        TimeStampDataValue tsdv_sr0 = null;
        TimeStampDataValue tsdv_sr0_copy = null;
        TimeStampDataValue tsdv_sr1 = null;
        TimeStampDataValue tsdv_sr1_copy = null;
        TimeStampDataValue tsdv_sr2 = null;
        TimeStampDataValue tsdv_sr2_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db_l = new ODBCDatabase();

            tsdv0 = new TimeStampDataValue(db_l);

            matrix_mve = new MatrixVocabElement(db_l, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db_l);
            matrix_mve.appendFormalArg(tsfa);
            db_l.vl.addElement(matrix_mve);

            tsdv1 = new TimeStampDataValue(db_l, tsfa.getID());
            tsdv2 = new TimeStampDataValue(db_l, tsfa.getID(),
                    new TimeStamp(db_l.getTicks(), 24 * 60 * 60 * db_l.getTicks()));

            matrix_mve_sr = new MatrixVocabElement(db_l, "matrix_mve_sr");
            matrix_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db_l);
            tsfa.setRange(new TimeStamp(db_l.getTicks(), 10 * db_l.getTicks()),
                         new TimeStamp(db_l.getTicks(), 60 * 60 * db_l.getTicks()));
            matrix_mve_sr.appendFormalArg(tsfa_sr);
            db_l.vl.addElement(matrix_mve_sr);

            tsdv_sr0 = new TimeStampDataValue(db_l, tsfa_sr.getID());
            tsdv_sr1 = new TimeStampDataValue(db_l, tsfa_sr.getID(),
                    new TimeStamp(db_l.getTicks(), 12 * db_l.getTicks()));
            tsdv_sr2 = new TimeStampDataValue(db_l, tsfa_sr.getID(),
                    new TimeStamp(db_l.getTicks(), 12 * 60 * 60 * db_l.getTicks()));

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv1 == null ) ||
             ( tsdv2 == null ) ||
             ( matrix_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr0 == null ) ||
             ( tsdv_sr1 == null ) ||
             ( tsdv_sr2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
                }

                if ( tsdv2 == null )
                {
                    outStream.print("allocation of tsdv2 failed.\n");
                }

                if ( matrix_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr0 == null )
                {
                    outStream.print("allocation of tsdv_sr0 failed.\n");
                }

                if ( tsdv_sr2 == null )
                {
                    outStream.print("allocation of tsdv_sr1 failed.\n");
                }

                if ( tsdv_sr2 == null )
                {
                    outStream.print("allocation of tsdv_sr2 failed.\n");
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
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                tsdv0_copy = new TimeStampDataValue(tsdv0);
                tsdv1_copy = new TimeStampDataValue(tsdv1);
                tsdv2_copy = new TimeStampDataValue(tsdv2);
                tsdv_sr0_copy = new TimeStampDataValue(tsdv_sr0);
                tsdv_sr1_copy = new TimeStampDataValue(tsdv_sr1);
                tsdv_sr2_copy = new TimeStampDataValue(tsdv_sr2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv0_copy == null ) ||
                 ( tsdv1_copy == null ) ||
                 ( tsdv2_copy == null ) ||
                 ( tsdv_sr0_copy == null ) ||
                 ( tsdv_sr1_copy == null ) ||
                 ( tsdv_sr2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv0_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv0) returned null.\n");
                    }

                    if ( tsdv1_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv1) returned null.\n");
                    }

                    if ( tsdv2_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv2) returned null.\n");
                    }

                    if ( tsdv_sr0_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr0) returned null.\n");
                    }

                    if ( tsdv_sr1_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr1) returned null.\n");
                    }

                    if ( tsdv_sr2_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr2) returned null.\n");
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
            failures += DataValueTest.VerifyDVCopy(tsdv0, tsdv0_copy, outStream,
                                               verbose, "tsdv0", "tsdv0_copy");

            failures += DataValueTest.VerifyDVCopy(tsdv1, tsdv1_copy, outStream,
                                               verbose, "tsdv1", "tsdv1_copy");

            failures += DataValueTest.VerifyDVCopy(tsdv2, tsdv2_copy, outStream,
                                               verbose, "tsdv2", "tsdv2_copy");

            failures += DataValueTest.VerifyDVCopy(tsdv_sr0, tsdv_sr0_copy, outStream,
                                            verbose, "tsdv_sr0", "tsdv_sr0_copy");

            failures += DataValueTest.VerifyDVCopy(tsdv_sr1, tsdv_sr1_copy, outStream,
                                            verbose, "tsdv_sr1", "tsdv_sr1_copy");

            failures += DataValueTest.VerifyDVCopy(tsdv_sr2, tsdv_sr2_copy, outStream,
                                            verbose, "tsdv_sr2", "tsdv_sr2_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            tsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv_l = new TimeStampDataValue((TimeStampDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null) completed.\n");
                    }

                    if ( tsdv_l != null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null) " +
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
    } /* TimeStampDataValue::TestCopyConstructor() */


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
        String testString0 = "00:00:12:000";
        String testDBString0 = "(TimeStampDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 500) " +
                                    "(itsValue (60,00:00:12:000)) " +
                                    "(subRange true) " +
                                    "(minVal (60,00:00:10:000)) " +
                                    "(maxVal (60,01:00:00:000)))";
        String testString1 = "12:10:05:012";
        String testDBString1 = "(TimeStampDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue (60,12:10:05:012)) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement matrix_mve_sr = null;
        MatrixVocabElement matrix_mve = null;
        TimeStampFormalArg tsfa = null;
        UnTypedFormalArg ufa = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        tsdv0 = null;
        tsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();

            matrix_mve_sr = new MatrixVocabElement(db_l, "matrix_mve_sr");
            matrix_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db_l);
            tsfa.setRange(new TimeStamp(db_l.getTicks(), 10 * db_l.getTicks()),
                         new TimeStamp(db_l.getTicks(), 60 * 60 * db_l.getTicks()));
            matrix_mve_sr.appendFormalArg(tsfa);
            db_l.vl.addElement(matrix_mve_sr);

            tsdv0 = new TimeStampDataValue(db_l, tsfa.getID(),
                    new TimeStamp(db_l.getTicks(), 12 * db_l.getTicks()));
            tsdv0.setID(100);      // invalid value for print test
            tsdv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db_l, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db_l, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db_l.vl.addElement(matrix_mve);

            tsdv1 = new TimeStampDataValue(db_l, ufa.getID(),
                    new TimeStamp(db_l.getTicks(), 12 * 60 * 60 * db_l.getTicks()
                                                    + 10 * 60 * db_l.getTicks()
                                                          + 5 * db_l.getTicks()
                                                              + 12));
            tsdv1.setID(101);      // invalid value for print test
            tsdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( matrix_mve_sr == null ) ||
             ( tsfa == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( tsdv1 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( matrix_mve_sr == null )
                {
                    outStream.print("allocation of matrix_mve_sr failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
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
            if ( tsdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv0.toString(): \"%s\".\n",
                                     tsdv0.toString());
                }
            }

            if ( tsdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv0.toDBString(): \"%s\".\n",
                                     tsdv0.toDBString());
                }
            }

            if ( tsdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv1.toString(): \"%s\".\n",
                                     tsdv1.toString());
                }
            }

            if ( tsdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv1.toDBString(): \"%s\".\n",
                                     tsdv1.toDBString());
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
    } /* TimeStampDataValue::TestToStringMethods() */


    /**
     * VerifyTimeStampDVCopy()
     *
     * Verify that the supplied instances of TimeStampDataValue are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyTimeStampDVCopy(TimeStampDataValue base,
                                            TimeStampDataValue copy,
                                            java.io.PrintStream outStream,
                                            boolean verbose,
                                            String baseDesc,
                                            String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyTimeStampDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyTimeStampDVCopy: %s null on entry.\n",
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
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a value TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.itsValue == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( copy.itsValue == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ! base.itsValue.equals(copy.itsValue) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsValue and %s.itsValue are different.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal == copy.maxVal ) &&
                  ( base.maxVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a maxVal TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal == null ) &&
                  ( copy.maxVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.maxVal is null, and %s.maxVal isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal != null ) &&
                  ( copy.maxVal == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.maxVal is null, and %s.maxVal isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.maxVal != null ) &&
                  ( ! base.maxVal.equals(copy.maxVal) ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.maxVal and %s.maxVal are different.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal == copy.minVal ) &&
                  ( base.minVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a minVal TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal == null ) &&
                  ( copy.minVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.minVal is null, and %s.minVal isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal != null ) &&
                  ( copy.minVal == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.minVal is null, and %s.minVal isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.minVal != null ) &&
                  ( ! base.minVal.equals(copy.minVal) ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.minVal and %s.minVal are different.\n",
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

        return failures;

    } /* TimeStampDataValue::VerifyTimeStampDVCopy() */

}