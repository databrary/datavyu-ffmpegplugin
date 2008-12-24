package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class FloatDataValue.
 *
 * @author cfreeman
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Before
    public void setUp() throws SystemErrorException {
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(null, ffa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, floatMve.getID());
    }

    /**
     * Test of 3 arg constructor, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(null, ffa.getID(), 1.0);
    }

    /**
     * Test1 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue f_value =
                                new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
    }

    /**
     * Test2 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, floatMve.getID(), 1.0);
    }

    /**
     * Test of copy constructor, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue((FloatDataValue) null);
    }

    /**
     * Test of getItsValue method, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);

        assertEquals(f_value.getItsValue(), 50.0, DELTA);
    }

    /**
     * Test of setItsValue method, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, ffa2.getID(), 50.0);
        assertEquals(f_value.toString(), "50.0");
    }

    /**
     * Test of toDBString method, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.cont.SystemErrorException on failure.
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
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
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
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        FloatDataValue test = FloatDataValue.Construct(db, 50.0);
        assertEquals(test.getItsValue(), 50.0);
    }

    /**
     * Test of FloatDataValuesAreLogicallyEqual method, of class FloatDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testFloatDataValuesAreLogicallyEqual()
    throws SystemErrorException {
        FloatDataValue f_value0 = new FloatDataValue(db, ffa.getID(), 200.0);
        FloatDataValue f_value1 = new FloatDataValue(db, ffa.getID(), 50.0);
        FloatDataValue f_copy = new FloatDataValue(f_value0);

        assertTrue(FloatDataValue.FloatDataValuesAreLogicallyEqual(f_value0,
                                                                   f_copy));
        assertFalse(FloatDataValue.FloatDataValuesAreLogicallyEqual(f_value0,
                                                                    f_value1));
        assertTrue(f_value0.equals(f_copy));
        assertFalse(f_value0.equals(f_value1));
    }

    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        FloatDataValue f_value0 = new FloatDataValue(db, ffa.getID(), 200.0);
        FloatDataValue f_copy = (FloatDataValue) f_value0.clone();

        assertEquals(f_value0, f_copy);
    }


    @Test
    @Override
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        super.testEquals();
        FloatDataValue f_value0 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue f_value1 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue f_value2 = new FloatDataValue(db, ffa.getID(), 300.003);
        FloatDataValue f_value3 = new FloatDataValue(db, ffa.getID(), 100.001);

        // Reflexive
        assertTrue(f_value0.equals(f_value0));
        // Symmetric
        assertTrue(f_value0.equals(f_value1));
        assertTrue(f_value1.equals(f_value0));
        // Transitive
        assertTrue(f_value0.equals(f_value1));
        assertTrue(f_value0.equals(f_value2));
        assertTrue(f_value1.equals(f_value2));
        // Consistent not tested
        // Null
        assertFalse(f_value0.equals(null));
        // Hashcode
        assertTrue(f_value0.hashCode() == f_value1.hashCode());

        // Not equals tests
        assertFalse(f_value0.equals(f_value3));
        assertTrue(f_value0.hashCode() != f_value3.hashCode());

        // modify f_value3
        double val = f_value3.getItsValue() * 3.0;
        f_value3.setItsValue(val);
        assertTrue(f_value0.equals(f_value3));
        assertTrue(f_value3.equals(f_value1));
        assertTrue(f_value2.hashCode() == f_value3.hashCode());
    }
}