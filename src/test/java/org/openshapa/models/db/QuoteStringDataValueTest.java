package org.openshapa.models.db;

import java.io.PrintStream;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 * Junit test cases for the class QuoteStringDataValue.
 */
public class QuoteStringDataValueTest extends DataValueTest {
    private Database db;
    private MatrixVocabElement qs_mve;
    private QuoteStringFormalArg qsfa;
    private QuoteStringDataValue qsdv;

    private MatrixVocabElement qs_mve2;
    private QuoteStringFormalArg qsfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

    private PrintStream outStream;
    private boolean verbose;

    @Override
    public DataValue getInstance() {
        return qsdv;
    }

    /**
     * Default test constructor.
     */
    public QuoteStringDataValueTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @BeforeMethod
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        qs_mve = new MatrixVocabElement(db, "qs_mve");
        qs_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
        qsfa = new QuoteStringFormalArg(db);
        qs_mve.appendFormalArg(qsfa);
        db.vl.addElement(qs_mve);

        qs_mve2 = new MatrixVocabElement(db, "qs_mve2");
        qs_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
        qsfa2 = new QuoteStringFormalArg(db);
/*        qsfa2.setSubRange(true);
        qsfa2.addApproved("alpha");
        qsfa2.addApproved("bravo");
        qsfa2.addApproved("charlie");
*/
        qs_mve2.appendFormalArg(qsfa2);
        db.vl.addElement(qs_mve2);

        matrix_mve = new MatrixVocabElement(db, "matrix_mve");
        matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
        ufa = new UnTypedFormalArg(db, "<untyped>");
        matrix_mve.appendFormalArg(ufa);
        db.vl.addElement(matrix_mve);
        qsdv = new QuoteStringDataValue(db);

        outStream = System.out;
        verbose = true;
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @AfterMethod
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
     * Test 1 arg constructor, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue(db);

        // Could be in a DataValueTest.Verify1ArgInitialization method
        // common for PredDataValue, QuoteStringDataValue and TimeStampDataValue
        DataValueTest.verify1ArgInitialization(db, qv);

        // QuoteString specific checks
        assertNull(qv.ItsDefault);
        assertEquals(qv.itsValue, qv.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue(db, qsfa.getID());

        assertNotNull(db);
        assertNotNull(qs_mve);
        assertNotNull(qsfa);

        assertNotNull(qv);

        // Could be in a DataValueTest.Verify2PlusArgInitialization method
        // common for PredDataValue, ColPredDataValue QuoteStringDataValue
        // and TimeStampDataValue
        assertNotNull(db);
        assertNotNull(qv);
        assertNotNull(qsfa);
        assertTrue(qsfa.getID() != DBIndex.INVALID_ID);
        assertEquals(qv.getDB(), db);
        assertEquals(qv.getDB(), db);
        assertEquals(qv.getID(), DBIndex.INVALID_ID);
        assertEquals(qv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(qv.itsFargID, qsfa.getID());
        assertEquals(qv.itsFargType, qsfa.getFargType());
        assertEquals(qv.getLastModUID(), DBIndex.INVALID_ID);
        assertEquals(qv.subRange, qsfa.getSubRange());

        // QuoteString specific
        assertNull(qv.ItsDefault);
        assertEquals(qv.subRange, qsfa.subRange);
        assertEquals(qv.itsValue, qv.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database)null,
                                                        qsfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qv
                            = new QuoteStringDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qv
                                = new QuoteStringDataValue(db, qs_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qv =
                            new QuoteStringDataValue(db, qsfa.getID(), "echo");

        assertNotNull(db);
        assertNotNull(qs_mve);
        assertNotNull(qsfa);

        assertNotNull(qv);

        // Could be in a DataValueTest.Verify2PlusArgInitialization method
        // common for PredDataValue, ColPredDataValue QuoteStringDataValue
        // and TimeStampDataValue
        assertNotNull(db);
        assertNotNull(qv);
        assertNotNull(qsfa);
        assertTrue(qsfa.getID() != DBIndex.INVALID_ID);
        assertEquals(qv.getDB(), db);
        assertEquals(qv.getDB(), db);
        assertEquals(qv.getID(), DBIndex.INVALID_ID);
        assertEquals(qv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(qv.itsFargID, qsfa.getID());
        assertEquals(qv.itsFargType, qsfa.getFargType());
        assertEquals(qv.getLastModUID(), DBIndex.INVALID_ID);
        assertEquals(qv.subRange, qsfa.getSubRange());

        // QuoteString specific
        assertEquals(qv.subRange, qsfa.getSubRange());
        assertNotNull(qv.itsValue);
        assertEquals(qv.itsValue, "echo");
    }

    /**
     * Test0 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database) null,
                                                        qsfa.getID(), "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qv
                   = new QuoteStringDataValue(db, DBIndex.INVALID_ID, "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qv
                       = new QuoteStringDataValue(db, qs_mve.getID(), "alpha");
    }

    /**
     * Test3 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        QuoteStringDataValue qv
              = new QuoteStringDataValue(db, qsfa.getID(), "invalid \" string");
    }

    /**
     * TestAccessors()
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */

    @Test
    public void TestAccessors() throws SystemErrorException
    {
    }

    /**
     * Test of copy constructor, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue(db, qsfa.getID(),
                                                        "foxtrot");
        QuoteStringDataValue q_copy = new QuoteStringDataValue(qv);

        assertNotSame(qv, q_copy);
        assertEquals(qv.getDB(), q_copy.getDB());
        assertEquals(qv.itsFargID, q_copy.itsFargID);
        assertEquals(qv.itsFargType, q_copy.itsFargType);
        assertEquals(qv.subRange, q_copy.subRange);
        assertEquals(qv.toString(), q_copy.toString());
        assertEquals(qv.toDBString(), q_copy.toDBString());
        assertEquals(qv.getClass(), q_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qv
                        = new QuoteStringDataValue((QuoteStringDataValue)null);
    }

    /**
     * Test of getItsValue method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db,
                                                        qsfa.getID(), "bravo");

        assertEquals(n_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db,
                                                        qsfa.getID(), "bravo");

        n_value.setItsValue("echo");
        assertEquals(n_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue(db, qsfa.getID(),
                                                         "bravo");
        QuoteStringDataValue qv2 = new QuoteStringDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(qv.toString(), "\"bravo\"");
        assertEquals(qv2.toString(), "\"nero\"");
    }

    /**
     * Test of toDBString method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    /*
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue bravo) " +
                                    "(subRange true))";

        String testDBString1 = "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";

        QuoteStringDataValue n_value0 = new QuoteStringDataValue(db,
                                                    qsfa2.getID(), "bravo");

        QuoteStringDataValue n_value1 = new QuoteStringDataValue(db,
                                                    ufa.getID(), "nero");

        assertEquals(n_value0.toDBString(), testDBString0);
        assertEquals(n_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of coerceToRange method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db,
                                                    qsfa2.getID(), "bravo");

        assertEquals(n_value.coerceToRange("alpha"), "alpha");
        assertEquals(n_value.coerceToRange("bravo"), "bravo");
        assertEquals(n_value.coerceToRange("charlie"), "charlie");
        // TODO: what other tests?
    }

    /**
     * Test of Construct method, of class QuoteStringDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        QuoteStringDataValue test = QuoteStringDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        QuoteStringDataValue value0 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value1 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value2 =
                          new QuoteStringDataValue(db, qsfa.getID(), "charlie");

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        QuoteStringDataValue value0 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value1 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value2 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value3 =
                          new QuoteStringDataValue(db, qsfa.getID(), "charlie");

        super.testEquals(value0, value1, value2, value3);
    }

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for QuoteStringDataValue.  Verify that all
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
     *         Construct a QuoteStringDataValue for the formal argument of the
     *         mve by passing a reference to the database and the id of the
     *         formal argument.  Verify that the QuoteStringDataValue's
     *         itsFargID, and itsFargType fields matches those of the formal
     *         argument, and that all other fields are set to the expected
     *         defaults.
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
     *      QuoteStringDataValue.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  QuoteStringDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         QuoteStringDataValue, and verify that the copy is correct.
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), and setItsValue() methods perform
     *      correctly.  Verify that the inherited accessors function correctly
     *      via calls to the DataValue.TestAccessors() method.
     *
     *      Given compiler error checking, there isn't any way to feed
     *      invalid data to the getItsValue(), and setItsValue().
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
            "Testing 1 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        QuoteStringDataValue qsdv_l = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        qsdv_l = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();
            qsdv_l = new QuoteStringDataValue(db_l);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db_l == null ) ||
             ( qsdv_l == null ) ||
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

                if ( qsdv_l == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "new QuoteStringDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new QuoteStringDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db_l, qsdv_l, outStream,
                                                           verbose);

            if ( qsdv_l.ItsDefault != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(" qsdv.ItsDefault != null.\n");
                }
            }

            if ( qsdv_l.itsValue != qsdv_l.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    String s1;
                    String s2;

                    if ( qsdv_l.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = qsdv_l.itsValue;

                    if ( qsdv_l.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = qsdv_l.ItsDefault;

                    outStream.printf(
                            "qsdv.itsValue = %s != qsdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv_l = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv_l = new QuoteStringDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv_l != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null) returned.\n");
                    }

                    if ( qsdv_l != null )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null) failed to throw " +
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
    } /* QuoteStringDataValue::Test1ArgConstructor() */


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
            "Testing 2 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;

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

            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv = new QuoteStringDataValue(db, qsfa.getID());

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv == null ) ||
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

                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }

                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv == null )
                {
                    outStream.print("new QuoteStringDataValue(db, " +
                                    "qsfa.getID()) returned null.\n");
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
                                                               qsfa,
                                                               qsdv,
                                                               outStream,
                                                               verbose,
                                                               "qsdv");

            if ( qsdv.ItsDefault != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv.ItsDefault != null.\n");
                }
            }

            if ( qsdv.subRange != qsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "qsdv.subRange doesn't match qsfa.getSubRange().\n");
                }
            }

            if ( qsdv.itsValue != qsdv.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    String s1;
                    String s2;

                    if ( qsdv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = qsdv.itsValue;

                    if ( qsdv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = qsdv.ItsDefault;

                    outStream.printf(
                            "qsdv.itsValue = %s != qsdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((Database)null, qsfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID()) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                "qsfa.getID()) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(db, INVALID_ID) " +
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
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qs_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "qs_mve.getID()) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(db, qs_mve.getID()) " +
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
    } /* QuoteStringDataValue::Test2ArgConstructor() */


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
            "Testing 3 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        qsdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv = new QuoteStringDataValue(db, qsfa.getID(), "echo");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv == null ) ||
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

                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }

                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                            "\"echo\") returned null.\n");
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
                                                               qsfa,
                                                               qsdv,
                                                               outStream,
                                                               verbose,
                                                               "qsdv");

            if ( qsdv.subRange != qsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "qsdv.subRange doesn't match qsfa.getSubRange().\n");
                }
            }

            if ( ( qsdv.itsValue == null ) ||
                 ( qsdv.itsValue.compareTo("echo") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv.itsValue != \"echo\".\n");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((Database)null, qsfa.getID(),
                                                "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                "qsfa.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID(), \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null, qsfa.getID(), " +
                                "\"alpha\") failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, DBIndex.INVALID_ID, "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "INVALID_ID, \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID, \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(db, INVALID_ID, " +
                                "\"alpha\") failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qs_mve.getID(), "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID(), \"alpha\") returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID(), \"alpha\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid quote string.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qsfa.getID(),
                                                "invalid \" string");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qsfa.getID(), \"invalid \\\" string\") returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"invalid \\\" string\") returned " +
                            "non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"invalid \\\" string\") failed " +
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
    } /* QuoteStringDataValue::Test3ArgConstructor() */


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
    public void TestAccessors2() throws SystemErrorException {
        String testBanner =
            "Testing class QuoteStringDataValue accessors                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        QuoteStringFormalArg qsfa = null;
        UnTypedFormalArg ufa = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        qsdv0 = null;
        qsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            matrix_mve0.appendFormalArg(qsfa);
            db.vl.addElement(matrix_mve0);

            qsdv0 = new QuoteStringDataValue(db, qsfa.getID(), "bravo");

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve1);

            qsdv1 = new QuoteStringDataValue(db, ufa.getID(), "delta");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( matrix_mve0 == null ) ||
             ( qsfa == null ) ||
             ( qsdv0 == null ) ||
             ( matrix_mve1 == null ) ||
             ( ufa == null ) ||
             ( qsdv1 == null ) ||
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

                if ( matrix_mve0 == null )
                {
                    outStream.print("allocation of matrix_mve0 failed.\n");
                }

                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.\n");
                }

                if ( qsdv0 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, qsfa.getID(), " +
                                    "\"bravo\") returned null.\n");
                }

                if ( matrix_mve1 == null )
                {
                    outStream.print("allocation of matrix_mve1 failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( qsdv1 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, ufa.getID(), " +
                                    "\"delta\") returned null.\n");
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
            failures += DataValueTest.TestAccessors(db, qsfa, matrix_mve1, ufa,
                                                qsdv0, outStream, verbose);

            if ( qsdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv0.getSubRange() != false");
                }
            }

            if ( ( qsdv0.getItsValue() == null ) ||
                 ( qsdv0.getItsValue().compareTo("bravo") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv.getItsValue() != \"bravo\"\n");
                }
            }

            qsdv0.setItsValue("echo");


            if ( ( qsdv0.getItsValue() == null ) ||
                 ( qsdv0.getItsValue().compareTo("echo") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv0.getItsValue() != \"echo\"\n");
                }
            }

            /************************************/

            if ( qsdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv1.getSubRange() != false (1)\n");
                }
            }

            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"delta\"(1)\n");
                }
            }

            failures += DataValueTest.TestAccessors(db, ufa, matrix_mve0, qsfa,
                                                qsdv1, outStream, verbose);

            if ( qsdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv1.getSubRange() != false (2)\n");
                }
            }

            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"delta\"(2)\n");
                }
            }

            qsdv1.setItsValue("alpha");

            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("alpha") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"alpha\".\n");
                }
            }
        }

        /* verify that the setItsValue method fails when fed an invalid value */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv0.setItsValue("invalid \"quote\" string");
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
                        outStream.print("qsdv0.setItsValue(\"invalid " +
                                "\\\"quote\\\" string\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("qsdv0.setItsValue(\"invalid " +
                                "\\\"quote\\\" string\") failed " +
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
    } /* QuoteStringDataValue::TestAccessors() */


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
            "Testing copy constructor for class QuoteStringDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;
        QuoteStringDataValue qsdv2 = null;
        QuoteStringDataValue qsdv0_copy = null;
        QuoteStringDataValue qsdv1_copy = null;
        QuoteStringDataValue qsdv2_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            qsdv0 = new QuoteStringDataValue(db);

            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv1 = new QuoteStringDataValue(db, qsfa.getID());
            qsdv2 = new QuoteStringDataValue(db, qsfa.getID(), "foxtrot");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv0 == null ) ||
             ( qsdv1 == null ) ||
             ( qsdv2 == null ) ||
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

                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }

                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv0 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db) returned null.\n");
                }

                if ( qsdv1 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, " +
                            "qsfa.getID()) returned null.\n");
                }

                if ( qsdv2 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                             "\"foxtrot\") returned null.\n");
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
                qsdv0_copy = new QuoteStringDataValue(qsdv0);
                qsdv1_copy = new QuoteStringDataValue(qsdv1);
                qsdv2_copy = new QuoteStringDataValue(qsdv2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv0_copy == null ) ||
                 ( qsdv1_copy == null ) ||
                 ( qsdv2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv0_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv0) returned null.\n");
                    }

                    if ( qsdv1_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv1) returned null.\n");
                    }

                    if ( qsdv2_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv2) returned null.\n");
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
            failures += DataValueTest.VerifyDVCopy(qsdv0, qsdv0_copy, outStream,
                                               verbose, "qsdv0", "qsdv0_copy");

            failures += DataValueTest.VerifyDVCopy(qsdv1, qsdv1_copy, outStream,
                                               verbose, "qsdv1", "qsdv1_copy");

            failures += DataValueTest.VerifyDVCopy(qsdv2, qsdv2_copy, outStream,
                                               verbose, "qsdv2", "qsdv2_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((QuoteStringDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null) completed.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null) " +
                                "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null) " +
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
    } /* QuoteStringDataValue::TestCopyConstructor() */


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
        String testString0 = "\"bravo\"";
        String testDBString0 = "(QuoteStringDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 500) " +
                                    "(itsValue bravo) " +
                                    "(subRange false))";
        String testString1 = "\"nero\"";
        String testDBString1 = "(QuoteStringDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve_l = null;
        MatrixVocabElement matrix_mve_l = null;
        QuoteStringFormalArg qsfa_l = null;
        UnTypedFormalArg ufa_l = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        qsdv0 = null;
        qsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            qs_mve_l = new MatrixVocabElement(db, "qs_mve");
            qs_mve_l.setType(MatrixVocabElement.MatrixType.MATRIX);
            qsfa_l = new QuoteStringFormalArg(db);
            qs_mve_l.appendFormalArg(qsfa_l);
            db.vl.addElement(qs_mve_l);

            qsdv0 = new QuoteStringDataValue(db, qsfa_l.getID(), "bravo");
            qsdv0.setID(100);      // invalid value for print test
            qsdv0.itsCellID = 500; // invalid value for print test

            matrix_mve_l = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve_l.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa_l = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve_l.appendFormalArg(ufa_l);
            db.vl.addElement(matrix_mve_l);

            qsdv1 = new QuoteStringDataValue(db, ufa_l.getID(), "nero");
            qsdv1.setID(101);      // invalid value for print test
            qsdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( qs_mve_l == null ) ||
             ( qsfa_l == null ) ||
             ( qsdv0 == null ) ||
             ( matrix_mve_l == null ) ||
             ( ufa_l == null ) ||
             ( qsdv1 == null ) ||
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

                if ( qs_mve_l == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }

                if ( qsfa_l == null )
                {
                    outStream.print("allocation of qsfa failed.\n");
                }

                if ( qsdv0 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                            "\"bravo\") returned null.\n");
                }

                if ( matrix_mve_l == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa_l == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( qsdv1 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, ufa.getID(), " +
                            "\"nero\") returned null.\n");
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
            if ( qsdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv0.toString(): \"%s\".\n",
                                     qsdv0.toString());
                }
            }

            if ( qsdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv0.toDBString(): \"%s\".\n",
                                     qsdv0.toDBString());
                }
            }

            if ( qsdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv1.toString(): \"%s\".\n",
                                     qsdv1.toString());
                }
            }

            if ( qsdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv1.toDBString(): \"%s\".\n",
                                     qsdv1.toDBString());
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
    } /* QuoteStringDataValue::TestToStringMethods() */


    /**
     * VerifyQuoteStringDVCopy()
     *
     * Verify that the supplied instances of QuoteStringDataValue are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyQuoteStringDVCopy(QuoteStringDataValue base,
                                              QuoteStringDataValue copy,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              String baseDesc,
                                              String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyQuoteStringDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyQuoteStringDVCopy: %s null on entry.\n",
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
                outStream.printf("%s and %s share a string.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == null ) &&
                  ( copy.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( copy.itsValue == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( base.itsValue.compareTo(copy.itsValue) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%sitsValue and %s.itsValue represent different values.\n",
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

    } /* QuoteStringDataValue::VerifyQuoteStringDVCopy() */

}