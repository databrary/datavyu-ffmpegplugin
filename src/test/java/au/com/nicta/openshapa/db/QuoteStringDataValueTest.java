package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class QuoteStringDataValue.
 *
 * @author cfreeman
 */
public class QuoteStringDataValueTest {
    private Database db;
    private MatrixVocabElement qs_mve;
    private QuoteStringFormalArg qsfa;

    private MatrixVocabElement qs_mve2;
    private QuoteStringFormalArg qsfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

    /**
     * Default test constructor.
     */
    public QuoteStringDataValueTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Before
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
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test 1 arg constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue(db);

        // Could be in a DataValueTest.Verify1ArgInitialization method
        // common for PredDataValue, QuoteStringDataValue and TimeStampDataValue
        assertNotNull(db);
        assertNotNull(qsdv);
        assertEquals(qsdv.db, db);
        assertEquals(qsdv.getDB(), db);
        assertEquals(qsdv.id, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsFargID, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsFargType, FormalArgument.fArgType.UNDEFINED);
        assertEquals(qsdv.lastModUID, DBIndex.INVALID_ID);
        assertFalse(qsdv.subRange);

        // QuoteString specific checks
        assertNull(qsdv.ItsDefault);
        assertEquals(qsdv.itsValue, qsdv.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue(db, qsfa.getID());

        assertNotNull(db);
        assertNotNull(qs_mve);
        assertNotNull(qsfa);

        assertNotNull(qsdv);

        // Could be in a DataValueTest.Verify2PlusArgInitialization method
        // common for PredDataValue, ColPredDataValue QuoteStringDataValue and TimeStampDataValue
        assertNotNull(db);
        assertNotNull(qsdv);
        assertNotNull(qsfa);
        assertTrue(qsfa.getID() != DBIndex.INVALID_ID);
        assertEquals(qsdv.db, db);
        assertEquals(qsdv.getDB(), db);
        assertEquals(qsdv.id, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsFargID, qsfa.getID());
        assertEquals(qsdv.itsFargType, qsfa.getFargType());
        assertEquals(qsdv.lastModUID, DBIndex.INVALID_ID);
        assertEquals(qsdv.subRange, qsfa.getSubRange());

        // QuoteString specific
        assertNull(qsdv.ItsDefault);
        assertEquals(qsdv.subRange, qsfa.subRange);
        assertEquals(qsdv.itsValue, qsdv.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue((Database)null,
                                                        qsfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qsdv
                            = new QuoteStringDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qsdv
                                = new QuoteStringDataValue(db, qs_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        QuoteStringDataValue qsdv =
                            new QuoteStringDataValue(db, qsfa.getID(), "echo");

        assertNotNull(db);
        assertNotNull(qs_mve);
        assertNotNull(qsfa);

        assertNotNull(qsdv);

        // Could be in a DataValueTest.Verify2PlusArgInitialization method
        // common for PredDataValue, ColPredDataValue QuoteStringDataValue and TimeStampDataValue
        assertNotNull(db);
        assertNotNull(qsdv);
        assertNotNull(qsfa);
        assertTrue(qsfa.getID() != DBIndex.INVALID_ID);
        assertEquals(qsdv.db, db);
        assertEquals(qsdv.getDB(), db);
        assertEquals(qsdv.id, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(qsdv.itsFargID, qsfa.getID());
        assertEquals(qsdv.itsFargType, qsfa.getFargType());
        assertEquals(qsdv.lastModUID, DBIndex.INVALID_ID);
        assertEquals(qsdv.subRange, qsfa.getSubRange());

        // QuoteString specific
        assertEquals(qsdv.subRange, qsfa.getSubRange());
        assertNotNull(qsdv.itsValue);
        assertEquals(qsdv.itsValue, "echo");
    }

    /**
     * Test0 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue((Database) null,
                                                        qsfa.getID(), "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qsdv
                   = new QuoteStringDataValue(db, DBIndex.INVALID_ID, "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qsdv
                       = new QuoteStringDataValue(db, qs_mve.getID(), "alpha");
    }

    /**
     * Test3 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        QuoteStringDataValue qsdv
              = new QuoteStringDataValue(db, qsfa.getID(), "invalid \" string");
    }

    /**
     * TestAccessors()
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */

    @Test
    public void TestAccessors() throws SystemErrorException
    {
    }

    /**
     * Test of copy constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue(db, qsfa.getID(),
                                                        "foxtrot");
        QuoteStringDataValue q_copy = new QuoteStringDataValue(qsdv);

        assertNotSame(qsdv, q_copy);
        assertEquals(qsdv.getDB(), q_copy.getDB());
        assertEquals(qsdv.itsFargID, q_copy.itsFargID);
        assertEquals(qsdv.itsFargType, q_copy.itsFargType);
        assertEquals(qsdv.subRange, q_copy.subRange);
        assertEquals(qsdv.toString(), q_copy.toString());
        assertEquals(qsdv.toDBString(), q_copy.toDBString());
        assertEquals(qsdv.getClass(), q_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qsdv
                        = new QuoteStringDataValue((QuoteStringDataValue)null);
    }

    /**
     * Test of getItsValue method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db, qsfa.getID(),
                                                        "bravo");

        assertEquals(n_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db, qsfa.getID(),
                                                        "bravo");

        n_value.setItsValue("echo");
        assertEquals(n_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        QuoteStringDataValue qsdv = new QuoteStringDataValue(db, qsfa.getID(),
                                                         "bravo");
        QuoteStringDataValue qsdv2 = new QuoteStringDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(qsdv.toString(), "\"bravo\"");
        assertEquals(qsdv2.toString(), "\"nero\"");
    }

    /**
     * Test of toDBString method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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

        QuoteStringDataValue n_value0 = new QuoteStringDataValue(db, qsfa2.getID(),
                                                         "bravo");

        QuoteStringDataValue n_value1 = new QuoteStringDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(n_value0.toDBString(), testDBString0);
        assertEquals(n_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of coerceToRange method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        QuoteStringDataValue n_value = new QuoteStringDataValue(db, qsfa2.getID(),
                                                        "bravo");

        assertEquals(n_value.coerceToRange("alpha"), "alpha");
        assertEquals(n_value.coerceToRange("bravo"), "bravo");
        assertEquals(n_value.coerceToRange("charlie"), "charlie");
        // TODO: what other tests?
    }

    /**
     * Test of Construct method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        QuoteStringDataValue test = QuoteStringDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    /**
     * Test of QuoteStringDataValuesAreLogicallyEqual method, of class
     * QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testQuoteStringDataValuesAreLogicallyEqual()
    throws SystemErrorException {
        QuoteStringDataValue n_value0 = new QuoteStringDataValue(db, qsfa.getID(),
                                                         "bravo");
        QuoteStringDataValue n_value1 = new QuoteStringDataValue(db, ufa.getID(),
                                                         "nero");
        QuoteStringDataValue n_copy = new QuoteStringDataValue(n_value0);

        assertTrue(QuoteStringDataValue.
                   QuoteStringDataValuesAreLogicallyEqual(n_value0, n_copy));
        assertFalse(QuoteStringDataValue.
                    QuoteStringDataValuesAreLogicallyEqual(n_value0,n_value1));
    }


    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        QuoteStringDataValue value0 =
                                new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue copy = (QuoteStringDataValue) value0.clone();

        assertEquals(value0, copy);
    }


    @Test
    public void testEquals()
    throws SystemErrorException {
        QuoteStringDataValue value0 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value1 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value2 =
                            new QuoteStringDataValue(db, qsfa.getID(), "bravo");
        QuoteStringDataValue value3 =
                          new QuoteStringDataValue(db, qsfa.getID(), "charlie");

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
        // Hashcode
        assertTrue(value0.hashCode() == value1.hashCode());

        // Not equals tests
        assertFalse(value0.equals(value3));
        assertTrue(value0.hashCode() != value3.hashCode());

        // modify value3
        String val = value3.getItsValue();
        val = "bravo";
        value3.setItsValue(val);
        assertTrue(value0.equals(value3));
        assertTrue(value3.equals(value1));
        assertTrue(value2.hashCode() == value3.hashCode());
    }

}