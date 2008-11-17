package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class IntDataValue.
 *
 * @author cfreeman
 */
public class IntDataValueTest {
    private Database db;
    private MatrixVocabElement int_mve;
    private IntFormalArg ifa;

    private MatrixVocabElement int_mve2;
    private IntFormalArg ifa2;

    /**
     * Default test constructor.
     */
    public IntDataValueTest() {
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

        int_mve = new MatrixVocabElement(db, "int_mve");
        int_mve.setType(MatrixVocabElement.matrixType.INTEGER);
        ifa = new IntFormalArg(db);
        int_mve.appendFormalArg(ifa);
        db.vl.addElement(int_mve);

        int_mve2 = new MatrixVocabElement(db, "int_mve2");
        int_mve2.setType(MatrixVocabElement.matrixType.INTEGER);
        ifa2 = new IntFormalArg(db);
        ifa2.setRange(-100, 100);
        int_mve2.appendFormalArg(ifa2);
        db.vl.addElement(int_mve2);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test 1 arg constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException
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
     * @throws au.com.nicta.openshapa.db.SystemErrorException  on failure
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

    /**
     * Test of IntDataValuesAreLogicallyEqual method, of class IntDataValue.
     *
     * @throws au.com.nictaopenshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testIntDataValuesAreLogicallyEqual()
    throws SystemErrorException {
        IntDataValue int_value0 = new IntDataValue(db, ifa.getID(), 200);
        IntDataValue int_value1 = new IntDataValue(db, ifa.getID(), 50);
        IntDataValue int_copy = new IntDataValue(int_value0);

        assertTrue(IntDataValue.IntDataValuesAreLogicallyEqual(int_value0,
                                                               int_copy));
        assertFalse(IntDataValue.IntDataValuesAreLogicallyEqual(int_value0,
                                                                int_value1));
    }   
}