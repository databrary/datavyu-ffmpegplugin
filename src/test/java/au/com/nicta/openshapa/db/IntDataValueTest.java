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
public class IntDataValueTest {

    private Database db;
    //private IntDataValue intValue;

    public IntDataValueTest() {
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
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        IntDataValue intValue = new IntDataValue(db);

        assertNotNull(db);
        assertNotNull(intValue);

        assertEquals(intValue.getDB(), db);
        assertNotSame(intValue.maxVal, 0);
        assertNotSame(intValue.minVal, 0);
    }

    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        MatrixVocabElement int_mve = new MatrixVocabElement(db, "int_mve");
        int_mve.setType(MatrixVocabElement.matrixType.INTEGER);
        IntFormalArg ifa = new IntFormalArg(db);
        int_mve.appendFormalArg(ifa);
        db.vl.addElement(int_mve);

        IntDataValue int_value = new IntDataValue(db, ifa.getID());

        MatrixVocabElement int_mve2 = new MatrixVocabElement(db, "int_mve2");
        int_mve2.setType(MatrixVocabElement.matrixType.INTEGER);
        IntFormalArg ifa2 = new IntFormalArg(db);
        ifa2.setRange(-100, 100);
        int_mve2.appendFormalArg(ifa2);
        db.vl.addElement(int_mve2);

        IntDataValue int_value2 = new IntDataValue(db, ifa2.getID());

        assertNotNull(db);
        assertNotNull(int_mve);
        assertNotNull(ifa);
        assertNotNull(int_mve2);
        assertNotNull(ifa2);

        assertSame(int_value.getSubRange(), ifa.getSubRange());
        assertSame(int_value.getItsValue(), int_value.ItsDefault);
        assertNotSame(int_value.maxVal, 0);
        assertNotSame(int_value.minVal, 0);

        assertSame(int_value2.getSubRange(), ifa2.getSubRange());
        assertSame(int_value2.getItsValue(), int_value2.ItsDefault);
        assertNotSame(int_value2.maxVal, 0);
        assertNotSame(int_value2.minVal, 0);
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure() throws SystemErrorException {
        IntFormalArg ifa = new IntFormalArg(db);        
        IntDataValue int_value = new IntDataValue(null, ifa.getID());
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        IntDataValue int_value = new IntDataValue(db, DBIndex.INVALID_ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        IntFormalArg ifa = new IntFormalArg(db);
        MatrixVocabElement int_mve = new MatrixVocabElement(db, "int_mve2");
        int_mve.setType(MatrixVocabElement.matrixType.INTEGER);
        int_mve.appendFormalArg(ifa);

        IntDataValue int_value = new IntDataValue(db, int_mve.getID());
    }

    /**
     * Test of getItsValue method, of class IntDataValue.
     */
    @Test
    public void testGetItsValue() {
        System.out.println("getItsValue");
        IntDataValue instance = null;
        long expResult = 0L;
        long result = instance.getItsValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setItsValue method, of class IntDataValue.
     */
    @Test
    public void testSetItsValue() {
        System.out.println("setItsValue");
        long value = 0L;
        IntDataValue instance = null;
        instance.setItsValue(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of constructEmptyArg method, of class IntDataValue.
     */
    @Test
    public void testConstructEmptyArg() throws Exception {
        System.out.println("constructEmptyArg");
        IntDataValue instance = null;
        DataValue expResult = null;
        DataValue result = instance.constructEmptyArg();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class IntDataValue.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        IntDataValue instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toDBString method, of class IntDataValue.
     */
    @Test
    public void testToDBString() {
        System.out.println("toDBString");
        IntDataValue instance = null;
        String expResult = "";
        String result = instance.toDBString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForFargChange method, of class IntDataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
        System.out.println("updateForFargChange");
        boolean fargNameChanged = false;
        boolean fargSubRangeChanged = false;
        boolean fargRangeChanged = false;
        FormalArgument oldFA = null;
        FormalArgument newFA = null;
        IntDataValue instance = null;
        instance.updateForFargChange(fargNameChanged, fargSubRangeChanged, fargRangeChanged, oldFA, newFA);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSubRange method, of class IntDataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
        System.out.println("updateSubRange");
        FormalArgument fa = null;
        IntDataValue instance = null;
        instance.updateSubRange(fa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of coerceToRange method, of class IntDataValue.
     */
    @Test
    public void testCoerceToRange() {
        System.out.println("coerceToRange");
        long value = 0L;
        IntDataValue instance = null;
        long expResult = 0L;
        long result = instance.coerceToRange(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Construct method, of class IntDataValue.
     */
    @Test
    public void testConstruct() throws Exception {
        System.out.println("Construct");
        Database db = null;
        long i = 0L;
        IntDataValue expResult = null;
        IntDataValue result = IntDataValue.Construct(db, i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of IntDataValuesAreLogicallyEqual method, of class IntDataValue.
     */
    @Test
    public void testIntDataValuesAreLogicallyEqual() throws Exception {
        System.out.println("IntDataValuesAreLogicallyEqual");
        IntDataValue idv0 = null;
        IntDataValue idv1 = null;
        boolean expResult = false;
        boolean result = IntDataValue.IntDataValuesAreLogicallyEqual(idv0, idv1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of TestClassIntDataValue method, of class IntDataValue.
     */
    @Test
    public void testTestClassIntDataValue() throws Exception {
        System.out.println("TestClassIntDataValue");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.TestClassIntDataValue(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Test1ArgConstructor method, of class IntDataValue.
     */
    @Test
    public void testTest1ArgConstructor() {
        System.out.println("Test1ArgConstructor");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.Test1ArgConstructor(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Test2ArgConstructor method, of class IntDataValue.
     */
    @Test
    public void testTest2ArgConstructor() throws Exception {
        System.out.println("Test2ArgConstructor");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.Test2ArgConstructor(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Test3ArgConstructor method, of class IntDataValue.
     */
    @Test
    public void testTest3ArgConstructor() throws Exception {
        System.out.println("Test3ArgConstructor");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.Test3ArgConstructor(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of TestAccessors method, of class IntDataValue.
     */
    @Test
    public void testTestAccessors() throws Exception {
        System.out.println("TestAccessors");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.TestAccessors(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of TestCopyConstructor method, of class IntDataValue.
     */
    @Test
    public void testTestCopyConstructor() throws Exception {
        System.out.println("TestCopyConstructor");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.TestCopyConstructor(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of TestToStringMethods method, of class IntDataValue.
     */
    @Test
    public void testTestToStringMethods() throws Exception {
        System.out.println("TestToStringMethods");
        PrintStream outStream = null;
        boolean verbose = false;
        boolean expResult = false;
        boolean result = IntDataValue.TestToStringMethods(outStream, verbose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of VerifyIntDVCopy method, of class IntDataValue.
     */
    @Test
    public void testVerifyIntDVCopy() {
        System.out.println("VerifyIntDVCopy");
        IntDataValue base = null;
        IntDataValue copy = null;
        PrintStream outStream = null;
        boolean verbose = false;
        String baseDesc = "";
        String copyDesc = "";
        int expResult = 0;
        int result = IntDataValue.VerifyIntDVCopy(base, copy, outStream, verbose, baseDesc, copyDesc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}