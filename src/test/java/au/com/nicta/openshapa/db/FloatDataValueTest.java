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
public class FloatDataValueTest {
    private final static double DELTA = 0.001;

    private Database db;
    private MatrixVocabElement float_mve;
    private FloatFormalArg ffa;

    private MatrixVocabElement float_mve2;
    private FloatFormalArg ffa2;

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

        float_mve = new MatrixVocabElement(db, "float_mve");
        float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
        ffa = new FloatFormalArg(db);
        float_mve.appendFormalArg(ffa);
        db.vl.addElement(float_mve);

        float_mve2 = new MatrixVocabElement(db, "float_mve2");
        float_mve2.setType(MatrixVocabElement.MatrixType.FLOAT);
        ffa2 = new FloatFormalArg(db);
        ffa2.setRange(-100.0, 100.0);
        float_mve2.appendFormalArg(ffa2);
        db.vl.addElement(float_mve2);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
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
        assertEquals(float_value.maxVal, 0.0, DELTA);
        assertEquals(float_value.minVal, 0.0, DELTA);
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
        assertNotNull(float_mve);
        assertNotNull(ffa);
        assertNotNull(float_mve2);
        assertNotNull(ffa2);

        assertEquals(float_value.getSubRange(), ffa.getSubRange());
        assertEquals(float_value.getItsValue(), float_value.ItsDefault, DELTA);
        assertEquals(float_value.maxVal, 0, DELTA);
        assertEquals(float_value.minVal, 0, DELTA);

        assertEquals(float_value2.getSubRange(), ffa2.getSubRange());
        assertEquals(float_value2.getItsValue(), float_value2.ItsDefault, DELTA);
        assertEquals(float_value2.maxVal, ffa2.getMaxVal());
        assertEquals(float_value2.minVal, ffa2.getMinVal());
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
        FloatDataValue float_value = new FloatDataValue(db, float_mve.getID());
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
        assertNotNull(float_mve);
        assertNotNull(ffa);
        assertNotNull(float_mve2);
        assertNotNull(ffa2);

        assertNotNull(f_value0);
        assertNotNull(f_value1);
        assertNotNull(f_value2);

        assertEquals(f_value0.getSubRange(), ffa.getSubRange());
        assertEquals(f_value0.itsValue, 200.0, DELTA);
        assertEquals(f_value0.maxVal, 0.0, DELTA);
        assertEquals(f_value0.minVal, 0.0, DELTA);

        assertEquals(f_value1.getSubRange(), ffa2.getSubRange());
        assertEquals(f_value1.itsValue, 1.0, DELTA);
        assertEquals(f_value1.maxVal, ffa2.getMaxVal(), DELTA);
        assertEquals(f_value1.minVal, ffa2.getMinVal(), DELTA);

        assertEquals(f_value2.getSubRange(), ffa2.getSubRange());
        assertEquals(f_value2.subRange, ffa2.getSubRange());
        assertEquals(f_value2.itsValue, ffa2.getMaxVal(), DELTA);
        assertEquals(f_value2.maxVal, ffa2.getMaxVal(), DELTA);
        assertEquals(f_value2.minVal, ffa2.getMinVal(), DELTA);
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
        FloatDataValue f_value = new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
    }

    /**
     * Test2 of 3Arg constructor failure, of class FloatDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, float_mve.getID(), 1.0);
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
    }

    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        FloatDataValue f_value0 = new FloatDataValue(db, ffa.getID(), 200.0);
        FloatDataValue f_copy = (FloatDataValue) f_value0.clone();

        //assertEquals(f_value0, f_copy);
    }
}