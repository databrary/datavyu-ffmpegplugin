/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
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
    

    public FloatDataValueTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();

        float_mve = new MatrixVocabElement(db, "float_mve");
        float_mve.setType(MatrixVocabElement.matrixType.FLOAT);
        ffa = new FloatFormalArg(db);
        float_mve.appendFormalArg(ffa);
        db.vl.addElement(float_mve);

        float_mve2 = new MatrixVocabElement(db, "float_mve2");
        float_mve2.setType(MatrixVocabElement.matrixType.FLOAT);
        ffa2 = new FloatFormalArg(db);
        ffa2.setRange(-100.0, 100.0);
        float_mve2.appendFormalArg(ffa2);
        db.vl.addElement(float_mve2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db);

        assertNotNull(float_value);
        assertNotNull(db);

        assertEquals(float_value.getDB(), db);
        assertEquals(float_value.maxVal, 0.0, DELTA);
        assertEquals(float_value.minVal, 0.0, DELTA);
    }

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

        @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(null, ffa.getID());
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, DBIndex.INVALID_ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue float_value = new FloatDataValue(db, float_mve.getID());
    }


    /**
     * Test of 3 arg constructor, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException
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
     * Test0 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(null, ffa.getID(), 1.0);
    }

    /**
     * Test1 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
    }

    /**
     * Test2 of 3Arg constructor failure, of class IntDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        FloatDataValue f_value = new FloatDataValue(db, float_mve.getID(), 1.0);
    }

    /**
     * Test of getItsValue method, of class FloatDataValue.
     */
    @Test
    public void testGetItsValue() {
        System.out.println("getItsValue");
        FloatDataValue instance = null;
        double expResult = 0.0;
        double result = instance.getItsValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setItsValue method, of class FloatDataValue.
     */
    @Test
    public void testSetItsValue() {
        System.out.println("setItsValue");
        double value = 0.0;
        FloatDataValue instance = null;
        instance.setItsValue(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class FloatDataValue.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        FloatDataValue instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toDBString method, of class FloatDataValue.
     */
    @Test
    public void testToDBString() {
        System.out.println("toDBString");
        FloatDataValue instance = null;
        String expResult = "";
        String result = instance.toDBString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForFargChange method, of class FloatDataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
        System.out.println("updateForFargChange");
        boolean fargNameChanged = false;
        boolean fargSubRangeChanged = false;
        boolean fargRangeChanged = false;
        FormalArgument oldFA = null;
        FormalArgument newFA = null;
        FloatDataValue instance = null;
        instance.updateForFargChange(fargNameChanged, fargSubRangeChanged, fargRangeChanged, oldFA, newFA);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSubRange method, of class FloatDataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
        System.out.println("updateSubRange");
        FormalArgument fa = null;
        FloatDataValue instance = null;
        instance.updateSubRange(fa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of coerceToRange method, of class FloatDataValue.
     */
    @Test
    public void testCoerceToRange() {
        System.out.println("coerceToRange");
        double value = 0.0;
        FloatDataValue instance = null;
        double expResult = 0.0;
        double result = instance.coerceToRange(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Construct method, of class FloatDataValue.
     */
    @Test
    public void testConstruct() throws Exception {
        System.out.println("Construct");
        Database db = null;
        double f = 0.0;
        FloatDataValue expResult = null;
        FloatDataValue result = FloatDataValue.Construct(db, f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of FloatDataValuesAreLogicallyEqual method, of class FloatDataValue.
     */
    @Test
    public void testFloatDataValuesAreLogicallyEqual() throws Exception {
        System.out.println("FloatDataValuesAreLogicallyEqual");
        FloatDataValue fdv0 = null;
        FloatDataValue fdv1 = null;
        boolean expResult = false;
        boolean result = FloatDataValue.FloatDataValuesAreLogicallyEqual(fdv0, fdv1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}