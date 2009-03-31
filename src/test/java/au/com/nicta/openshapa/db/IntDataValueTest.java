package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class IntDataValue.
 *
 * @author cfreeman
 */
public class IntDataValueTest extends DataValueTest {
    private Database db;
    private MatrixVocabElement int_mve;
    private IntFormalArg ifa;
    private IntDataValue idv;

    private MatrixVocabElement int_mve2;
    private IntFormalArg ifa2;

    private PrintStream outStream;
    private boolean verbose;

    /**
     * Default test constructor.
     */
    public IntDataValueTest() {
    }

    @Override
    public DataValue getInstance() {
        return idv;
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Before
    public void setUp() throws SystemErrorException, LogicErrorException {
        db = new ODBCDatabase();

        int_mve = new MatrixVocabElement(db, "int_mve");
        int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
        ifa = new IntFormalArg(db);
        int_mve.appendFormalArg(ifa);
        db.vl.addElement(int_mve);

        int_mve2 = new MatrixVocabElement(db, "int_mve2");
        int_mve2.setType(MatrixVocabElement.MatrixType.INTEGER);
        ifa2 = new IntFormalArg(db);
        ifa2.setRange(-100, 100);
        int_mve2.appendFormalArg(ifa2);
        db.vl.addElement(int_mve2);
        idv = new IntDataValue(db);

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
     * Test of updateSubRange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateSubRange() throws Exception {
    }

    /**
     * Test 1 arg constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        IntDataValue intValue = new IntDataValue(db);

        assertNotNull(db);
        assertNotNull(intValue);

        assertEquals(intValue.getDB(), db);
        assertEquals(intValue.maxVal, 0);
        assertEquals(intValue.minVal, 0);
    }

    /**
     * Test 2 argument constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa.getID());
        IntDataValue int_value2 = new IntDataValue(db, ifa2.getID());

        assertNotNull(db);
        assertNotNull(int_mve);
        assertNotNull(ifa);
        assertNotNull(int_mve2);
        assertNotNull(ifa2);

        assertEquals(int_value.getSubRange(), ifa.getSubRange());
        assertEquals(int_value.getItsValue(), int_value.ItsDefault);
        assertEquals(int_value.maxVal, 0);
        assertEquals(int_value.minVal, 0);

        assertEquals(int_value2.getSubRange(), ifa2.getSubRange());
        assertEquals(int_value2.getItsValue(), int_value2.ItsDefault);
        assertEquals(int_value2.maxVal, ifa2.getMaxVal());
        assertEquals(int_value2.minVal, ifa2.getMinVal());
    }

    /**
     * Test0 of 2 arg constructor failre, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(null, ifa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, int_mve.getID());
    }

    /**
     * Test of 3 arg constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        IntDataValue int_value0 = new IntDataValue(db, ifa.getID(), 200);
        IntDataValue int_value1 = new IntDataValue(db, ifa2.getID(), 1);
        IntDataValue int_value2 = new IntDataValue(db, ifa2.getID(), 200);

        assertNotNull(db);
        assertNotNull(int_mve);
        assertNotNull(ifa);
        assertNotNull(int_mve2);
        assertNotNull(ifa2);

        assertNotNull(int_value0);
        assertNotNull(int_value1);
        assertNotNull(int_value2);

        assertEquals(int_value0.getSubRange(), ifa.getSubRange());
        assertEquals(int_value0.itsValue, 200);
        assertEquals(int_value0.maxVal, 0);
        assertEquals(int_value0.minVal, 0);

        assertEquals(int_value1.getSubRange(), ifa2.getSubRange());
        assertEquals(int_value1.itsValue, 1);
        assertEquals(int_value1.maxVal, ifa2.getMaxVal());
        assertEquals(int_value1.minVal, ifa2.getMinVal());

        assertEquals(int_value2.getSubRange(), ifa2.getSubRange());
        assertEquals(int_value2.subRange, ifa2.getSubRange());
        assertEquals(int_value2.itsValue, ifa2.getMaxVal());
        assertEquals(int_value2.maxVal, ifa2.getMaxVal());
        assertEquals(int_value2.minVal, ifa2.getMinVal());
    }

    /**
     * Test0 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(null, ifa.getID(), 1);
    }

    /**
     * Test1 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, DBIndex.INVALID_ID, 1);
    }

    /**
     * Test2 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, int_mve.getID(), 1);
    }

    /**
     * Test of copy constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa.getID(), 200);
        IntDataValue int_copy = new IntDataValue(int_value);

        assertNotSame(int_value, int_copy);
        assertEquals(int_value.getDB(), int_copy.getDB());
        assertEquals(int_value.itsFargID, int_copy.itsFargID);
        assertEquals(int_value.itsFargType, int_copy.itsFargType);
        assertEquals(int_value.subRange, int_copy.subRange);
        assertEquals(int_value.toString(), int_copy.toString());
        assertEquals(int_value.toDBString(), int_copy.toDBString());
        assertEquals(int_value.getClass(), int_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue((IntDataValue) null);
    }

    /**
     * Test of getItsValue method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa2.getID(), 50);

        assertEquals(int_value.getItsValue(), 50);
    }

    /**
     * Test of setItsValue method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa2.getID(), 50);

        int_value.setItsValue(3);
        assertEquals(int_value.getItsValue(), 3);
    }

    /**
     * Test of toString method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa2.getID(), 50);
        assertEquals(int_value.toString(), "50");
    }

    /**
     * Test of toDBString method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(IntDataValue (id 0) " +
                                "(itsFargID 8) " +
                                "(itsFargType INTEGER) " +
                                "(itsCellID 0) " +
                                "(itsValue 50) " +
                                "(subRange true) " +
                                "(minVal -100) " +
                                "(maxVal 100))";

        IntDataValue int_value = new IntDataValue(db, ifa2.getID(), 50);
        assertEquals(int_value.toDBString(), testDBString0);
    }

    /**
     * Test of coerceToRange method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, ifa2.getID(), 50);
        assertEquals(int_value.coerceToRange(101), 100);
        assertEquals(int_value.coerceToRange(100), 100);
        assertEquals(int_value.coerceToRange(99), 99);
        assertEquals(int_value.coerceToRange(47), 47);
        assertEquals(int_value.coerceToRange(-24), -24);
        assertEquals(int_value.coerceToRange(-99), -99);
        assertEquals(int_value.coerceToRange(-100), -100);
        assertEquals(int_value.coerceToRange(-101), -100);
    }

    /**
     * Test of Construct method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        IntDataValue test = IntDataValue.Construct(db, 50);
        assertEquals(test.getItsValue(), 50);
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        IntDataValue value0 = new IntDataValue(db, ifa.getID(), 300);
        IntDataValue value1 = new IntDataValue(db, ifa.getID(), 300);
        IntDataValue value2 = new IntDataValue(db, ifa.getID(), 100);

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        IntDataValue value0 = new IntDataValue(db, ifa.getID(), 300);
        IntDataValue value1 = new IntDataValue(db, ifa.getID(), 300);
        IntDataValue value2 = new IntDataValue(db, ifa.getID(), 300);
        IntDataValue value3 = new IntDataValue(db, ifa.getID(), 100);

        super.testEquals(value0, value1, value2, value3);
    }

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for IntDataValue.  Verify that all
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
     *         Construct a IntDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the IntDataValue's itsFargID,
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
     *      IntDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  IntDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         IntDataValue, and verify that the copy is correct.
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
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class IntDataValue            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        IntDataValue idv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        idv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            idv = new IntDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( idv == null ) ||
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

                if ( idv == null )
                {
                    outStream.print(
                            "new IntDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new IntDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db, idv, outStream,
                                                           verbose);

            if ( idv.itsValue != idv.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "idv.itsValue = %d != idv.ItsDefault = %d.\n",
                            idv.itsValue, idv.ItsDefault);
                }
            }

            if ( idv.maxVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of idv.maxVal: %d.\n",
                                     idv.maxVal);
                }
            }

            if ( idv.minVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of idv.minVal: %d.\n",
                                     idv.minVal);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(null) returned.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print(
                                "new IntDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntDataValue(null) failed to throw " +
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
    } /* IntDataValue::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 2 argument constructor for class IntDataValue            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement int_mve_sr = null;
        IntFormalArg ifa = null;
        IntFormalArg ifa_sr = null;
        IntDataValue idv = null;
        IntDataValue idv_sr = null;

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

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa = new IntFormalArg(db);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv = new IntDataValue(db, ifa.getID());

            int_mve_sr = new MatrixVocabElement(db, "int_mve_sr");
            int_mve_sr.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa_sr = new IntFormalArg(db);
            ifa_sr.setRange(-100, 100);
            int_mve_sr.appendFormalArg(ifa_sr);
            db.vl.addElement(int_mve_sr);

            idv_sr = new IntDataValue(db, ifa_sr.getID());

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv == null ) ||
             ( int_mve_sr == null ) ||
             ( ifa_sr == null ) ||
             ( idv_sr == null ) ||
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

                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }

                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.");
                }

                if ( idv == null )
                {
                    outStream.print(
                        "new IntDataValue(db, ifa.getID()) returned null.\n");
                }

                if ( int_mve_sr == null )
                {
                    outStream.print("allocation of int_mve_sr failed.\n");
                }

                if ( ifa_sr == null )
                {
                    outStream.print("allocation of ifa_sr failed.");
                }

                if ( idv_sr == null )
                {
                    outStream.print("new IntDataValue(db, ifa_sr.getID()) " +
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
                                                               ifa,
                                                               idv,
                                                               outStream,
                                                               verbose,
                                                              "idv");

            if ( idv.subRange != ifa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "idv.subRange doesn't match ifa.getSubRange().\n");
                }
            }

            if ( idv.itsValue != idv.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "idv.itsValue = %d != idv.ItsDefault = %d.\n",
                            idv.itsValue, idv.ItsDefault);
                }
            }

            if ( idv.maxVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv.maxVal: %d (0).\n",
                            idv.maxVal);
                }
            }

            if ( idv.minVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv.minVal: %d (0).\n",
                            idv.minVal);
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ifa_sr,
                                                               idv_sr,
                                                               outStream,
                                                               verbose,
                                                               "idv_sr");

            if ( idv_sr.subRange != ifa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv_sr.subRange doesn't match " +
                                     "ifa_sr.getSubRange().\n");
                }
            }

            if ( idv_sr.itsValue != idv_sr.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "idv_sr.itsValue = %d != idv_sr.ItsDefault = %d.\n",
                            idv_sr.itsValue, idv_sr.ItsDefault);
                }
            }

            if ( idv_sr.maxVal != ifa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr.maxVal: %d (%d).\n",
                            idv_sr.maxVal, ifa_sr.getMaxVal());
                }
            }

            if ( idv_sr.minVal != ifa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv_sr.minVal: %d (%d).\n",
                            idv_sr.minVal, ifa_sr.getMinVal());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue((Database)null, ifa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(null, " +
                                        "ifa.getID()) returned.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(null, " +
                                        "ifa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntDataValue(null, ifa.getID())" +
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
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntDataValue(db, INVALID_ID)" +
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
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue(db, int_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(db, " +
                                        "int_mve.getID()) returned.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(db, " +
                                "int_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new IntDataValue(db, int_mve.getID()) " +
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
    } /* IntDataValue::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class IntDataValue            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement int_mve_sr = null;
        IntFormalArg ifa = null;
        IntFormalArg ifa_sr = null;
        IntDataValue idv = null;
        IntDataValue idv_sr0 = null;
        IntDataValue idv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        idv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa = new IntFormalArg(db);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv = new IntDataValue(db, ifa.getID(), 200);

            int_mve_sr = new MatrixVocabElement(db, "int_mve_sr");
            int_mve_sr.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa_sr = new IntFormalArg(db);
            ifa_sr.setRange(-100, 100);
            int_mve_sr.appendFormalArg(ifa_sr);
            db.vl.addElement(int_mve_sr);

            idv_sr0 = new IntDataValue(db, ifa_sr.getID(), 1);
            idv_sr1 = new IntDataValue(db, ifa_sr.getID(), 200);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv == null ) ||
             ( int_mve_sr == null ) ||
             ( ifa_sr == null ) ||
             ( idv_sr0 == null ) ||
             ( idv_sr1 == null ) ||
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

                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }

                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.");
                }

                if ( idv == null )
                {
                    outStream.print("new IntDataValue(db, ifa.getID(), " +
                                    "200) returned null.\n");
                }

                if ( int_mve_sr == null )
                {
                    outStream.print("allocation of int_mve_sr failed.\n");
                }

                if ( ifa_sr == null )
                {
                    outStream.print("allocation of ifa_sr failed.");
                }

                if ( idv_sr0 == null )
                {
                    outStream.print("new IntDataValue(db, ifa_sr.getID(), " +
                                    "1) returned null.\n");
                }

                if ( idv_sr1 == null )
                {
                    outStream.print("new IntDataValue(db, ifa_sr.getID(), " +
                                    "200) returned null.\n");
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
                                                               ifa,
                                                               idv,
                                                               outStream,
                                                               verbose,
                                                               "idv");

            if ( idv.subRange != ifa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "idv.subRange doesn't match ifa.getSubRange().\n");
                }
            }

            if ( idv.itsValue != 200 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv.itsValue = %d != 200.\n",
                                     idv.itsValue);
                }
            }

            if ( idv.maxVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv.maxVal: %d (0).\n",
                            idv.maxVal);
                }
            }

            if ( idv.minVal != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv.minVal: %d (0).\n",
                            idv.minVal);
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ifa_sr,
                                                               idv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "idv_sr0");

            if ( idv_sr0.subRange != ifa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv_sr0.subRange doesn't match " +
                                     "ifa_sr.getSubRange().\n");
                }
            }

            if ( idv_sr0.itsValue != 1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv_sr.itsValue = %d != 1.\n",
                                     idv_sr0.itsValue);
                }
            }

            if ( idv_sr0.maxVal != ifa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv_sr0.maxVal: %d (%d).\n",
                            idv_sr0.maxVal, ifa_sr.getMaxVal());
                }
            }

            if ( idv_sr0.minVal != ifa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv_sr0.minVal: %d (%d).\n",
                            idv_sr0.minVal, ifa_sr.getMinVal());
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               ifa_sr,
                                                               idv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "idv_sr1");

            if ( idv_sr1.subRange != ifa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv_sr0.subRange doesn't match " +
                                     "ifa_sr.getSubRange().\n");
                }
            }

            if ( idv_sr1.itsValue != ifa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv_sr1.itsValue = %d != %d.\n",
                                     idv_sr1.itsValue, ifa_sr.getMaxVal());
                }
            }

            if ( idv_sr1.maxVal != ifa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv_sr1.maxVal: %d (%d).\n",
                            idv_sr1.maxVal, ifa_sr.getMaxVal());
                }
            }

            if ( idv_sr1.minVal != ifa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of idv_sr1.minVal: %d (%d).\n",
                            idv_sr1.minVal, ifa_sr.getMinVal());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue((Database)null, ifa.getID(), 1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(null, " +
                                "ifa.getID(), 1) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new IntDataValue(null, " +
                                        "ifa.getID(), 1) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new IntDataValue(null, ifa.getID(), 1) " +
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
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue(db, DBIndex.INVALID_ID, 1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(db, " +
                                "INVALID_ID, 1) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new IntDataValue(db, " +
                                        "INVALID_ID, 1) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new IntDataValue(db, INVALID_ID, 1) " +
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
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue(db, int_mve.getID(), 1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(db, " +
                                        "int_mve.getID(), 1) returned.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print("new IntDataValue(db, " +
                                "int_mve.getID(), 1) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new IntDataValue(db, int_mve.getID(), 1) " +
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
    } /* IntDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class IntDataValue accessors                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve = null;
        IntFormalArg ifa = null;
        UnTypedFormalArg ufa = null;
        IntDataValue idv0 = null;
        IntDataValue idv1 = null;
        IntDataValue idv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        idv0 = null;
        idv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa = new IntFormalArg(db);
            ifa.setRange(-1000, +1000);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv0 = new IntDataValue(db, ifa.getID(), 200);

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            idv1 = new IntDataValue(db, ufa.getID(), 2000);
            idv2 = new IntDataValue(db, ufa.getID(), 999);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( idv1 == null ) ||
             ( idv2 == null ) ||
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

                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }

                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.\n");
                }

                if ( idv0 == null )
                {
                    outStream.print("new IntDataValue(db, ifa.getID(), " +
                                    "200) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( idv1 == null )
                {
                    outStream.print("new IntDataValue(db, ufa.getID(), " +
                                    "2000) returned null.\n");
                }

                if ( idv2 == null )
                {
                    outStream.print("new IntDataValue(db, ufa.getID(), " +
                                    "999) returned null.\n");
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
            failures += DataValueTest.TestAccessors(db, ifa, matrix_mve, ufa,
                                                idv0, outStream, verbose);

            if ( idv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv0.getSubRange() != false");
                }
            }

            if ( idv0.getItsValue() != 200 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv.getItsValue() != 200\n");
                }
            }

            idv0.setItsValue(3);


            if ( idv0.getItsValue() != 3 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv0.getItsValue() != 3\n");
                }
            }

            /************************************/

            if ( idv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv1.getSubRange() != false\n");
                }
            }

            if ( idv1.getItsValue() != 2000 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != 2000\n");
                }
            }

            failures += DataValueTest.TestAccessors(db, ufa, int_mve, ifa,
                                                idv1, outStream, verbose);

            if ( idv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv1.getSubRange() != true\n");
                }
            }

            if ( idv1.getItsValue() != 1000 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != 1000\n");
                }
            }

            idv1.setItsValue(-50000);

            if ( idv1.getItsValue() != -1000 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != -1000\n");
                }
            }

            if ( ( idv1.coerceToRange(1001) != 1000 ) ||
                 ( idv1.coerceToRange(1000) != 1000 ) ||
                 ( idv1.coerceToRange(999) != 999 ) ||
                 ( idv1.coerceToRange(998) != 998 ) ||
                 ( idv1.coerceToRange(47) != 47 ) ||
                 ( idv1.coerceToRange(-25) != -25 ) ||
                 ( idv1.coerceToRange(-999) != -999 ) ||
                 ( idv1.coerceToRange(-1000) != -1000 ) ||
                 ( idv1.coerceToRange(-1001) != -1000 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from idv1.coerceToRange()\n");
                }
            }

            /************************************/

            failures += DataValueTest.TestAccessors(db, ufa, int_mve, ifa,
                                                idv2, outStream, verbose);

            if ( idv2.getItsValue() != 999 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("idv2.getItsValue() != 999\n");
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
    } /* IntDataValue::TestAccessors() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        String testBanner =
            "Testing copy constructor for class IntDataValue                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement int_mve_sr = null;
        IntFormalArg ifa = null;
        IntFormalArg ifa_sr = null;
        IntDataValue idv = null;
        IntDataValue idv_copy = null;
        IntDataValue idv_sr0 = null;
        IntDataValue idv_sr0_copy = null;
        IntDataValue idv_sr1 = null;
        IntDataValue idv_sr1_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        idv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa = new IntFormalArg(db);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv = new IntDataValue(db, ifa.getID(), 200);

            int_mve_sr = new MatrixVocabElement(db, "int_mve_sr");
            int_mve_sr.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa_sr = new IntFormalArg(db);
            ifa_sr.setRange(-100, 100);
            int_mve_sr.appendFormalArg(ifa_sr);
            db.vl.addElement(int_mve_sr);

            idv_sr0 = new IntDataValue(db, ifa_sr.getID(), 1);
            idv_sr1 = new IntDataValue(db, ifa_sr.getID(), 200);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv == null ) ||
             ( int_mve_sr == null ) ||
             ( ifa_sr == null ) ||
             ( idv_sr0 == null ) ||
             ( idv_sr1 == null ) ||
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

                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }

                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.");
                }

                if ( idv == null )
                {
                    outStream.print("new IntDataValue(db, ifa.getID(), " +
                                    "200) returned null.\n");
                }

                if ( int_mve_sr == null )
                {
                    outStream.print("allocation of int_mve_sr failed.\n");
                }

                if ( ifa_sr == null )
                {
                    outStream.print("allocation of ifa_sr failed.");
                }

                if ( idv_sr0 == null )
                {
                    outStream.print("new IntDataValue(db, ifa_sr.getID(), " +
                                    "1) returned null.\n");
                }

                if ( idv_sr1 == null )
                {
                    outStream.print("new IntDataValue(db, ifa_sr.getID(), " +
                                    "200) returned null.\n");
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
            idv_copy = null;
            idv_sr0_copy = null;
            idv_sr1_copy = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                idv_copy = new IntDataValue(idv);
                idv_sr0_copy = new IntDataValue(idv_sr0);
                idv_sr1_copy = new IntDataValue(idv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv_copy == null ) ||
                 ( idv_sr0_copy == null ) ||
                 ( idv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( idv_copy == null )
                    {
                        outStream.print(
                                "new IntDataValue(idv) returned null.\n");
                    }

                    if ( idv_sr0_copy == null )
                    {
                        outStream.print(
                                "new IntDataValue(idv_sr0) returned null.\n");
                    }

                    if ( idv_sr1_copy == null )
                    {
                        outStream.print(
                                "new IntDataValue(idv_sr1) returned null.\n");
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
            failures += DataValueTest.VerifyDVCopy(idv, idv_copy, outStream,
                                               verbose, "idv", "idv_copy");

            failures += DataValueTest.VerifyDVCopy(idv_sr0, idv_sr0_copy, outStream,
                                            verbose, "idv_sr0", "idv_sr0_copy");

            failures += DataValueTest.VerifyDVCopy(idv_sr1, idv_sr1_copy, outStream,
                                            verbose, "idv_sr1", "idv_sr1_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            idv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                idv = new IntDataValue((IntDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( idv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new IntDataValue(null) completed.\n");
                    }

                    if ( idv != null )
                    {
                        outStream.print(
                                "new IntDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntDataValue(null) " +
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
    } /* IntDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                              JRM -- 11/13/07
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
        String testString0 = "200";
        String testDBString0 = "(IntDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 500) " +
                                    "(itsValue 200) " +
                                    "(subRange true) " +
                                    "(minVal -1000) " +
                                    "(maxVal 1000))";
        String testString1 = "2000";
        String testDBString1 = "(IntDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue 2000) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve = null;
        IntFormalArg ifa = null;
        UnTypedFormalArg ufa = null;
        IntDataValue idv0 = null;
        IntDataValue idv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        idv0 = null;
        idv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            ifa = new IntFormalArg(db);
            ifa.setRange(-1000, +1000);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv0 = new IntDataValue(db, ifa.getID(), 200);
            idv0.setID(100);      // invalid value for print test
            idv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            idv1 = new IntDataValue(db, ufa.getID(), 2000);
            idv1.setID(101);      // invalid value for print test
            idv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( idv1 == null ) ||
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

                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }

                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.\n");
                }

                if ( idv0 == null )
                {
                    outStream.print("new IntDataValue(db, ifa.getID(), " +
                                    "200) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( idv1 == null )
                {
                    outStream.print("new IntDataValue(db, ufa.getID(), " +
                                    "100) returned null.\n");
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
            if ( idv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected idv0.toString(): \"%s\".\n",
                                     idv0.toString());
                }
            }

            if ( idv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected idv0.toDBString(): \"%s\".\n",
                                     idv0.toDBString());
                }
            }

            if ( idv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected idv1.toString(): \"%s\".\n",
                                     idv1.toString());
                }
            }

            if ( idv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected idv1.toDBString(): \"%s\".\n",
                                     idv1.toDBString());
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
    } /* IntDataValue::TestToStringMethods() */


    /**
     * VerifyIntDVCopy()
     *
     * Verify that the supplied instances of IntDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyIntDVCopy(IntDataValue base,
                                      IntDataValue copy,
                                      java.io.PrintStream outStream,
                                      boolean verbose,
                                      String baseDesc,
                                      String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyIntDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyIntDVCopy: %s null on entry.\n",
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
        else if ( base.itsValue != copy.itsValue )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsValue != %s.itsValue.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.maxVal != copy.maxVal )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.maxVal != %s.maxVal.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.minVal != copy.minVal )
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
            }
        }

        return failures;

    } /* IntDataValue::VerifyIntDVCopy() */

}