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
public class QuoteStringDataValueTest extends DataValueTest {
    private Database db;
    private MatrixVocabElement qs_mve;
    private QuoteStringFormalArg qsfa;
    private QuoteStringDataValue qsdv;

    private MatrixVocabElement qs_mve2;
    private QuoteStringFormalArg qsfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

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
        qsdv = new QuoteStringDataValue(db);
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
     * Test 1 arg constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database)null,
                                                        qsfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qv
                            = new QuoteStringDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qv
                                = new QuoteStringDataValue(db, qs_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        QuoteStringDataValue qv = new QuoteStringDataValue((Database) null,
                                                        qsfa.getID(), "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        QuoteStringDataValue qv
                   = new QuoteStringDataValue(db, DBIndex.INVALID_ID, "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        QuoteStringDataValue qv
                       = new QuoteStringDataValue(db, qs_mve.getID(), "alpha");
    }

    /**
     * Test3 of 3 argument constructor failure, of class QuoteStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        QuoteStringDataValue qv
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        QuoteStringDataValue qv
                        = new QuoteStringDataValue((QuoteStringDataValue)null);
    }

    /**
     * Test of getItsValue method, of class QuoteStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        QuoteStringDataValue test = QuoteStringDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        QuoteStringDataValue value0 =
                                new QuoteStringDataValue(db, qsfa.getID(),
                                                                       "bravo");
        QuoteStringDataValue copy = (QuoteStringDataValue) value0.clone();

        assertEquals(value0, copy);
    }

    @Test
    @Override
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        super.testEquals();
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