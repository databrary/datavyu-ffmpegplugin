package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class TextStringDataValue.
 *
 * @author cfreeman
 */
public class TextStringDataValueTest {
    Database db;

    MatrixVocabElement txt_mve;
    TextStringFormalArg tfa;

    MatrixVocabElement txt_mve2;
    TextStringFormalArg tfa2;

    /**
     * Default test constructor.
     */
    public TextStringDataValueTest() {
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

        txt_mve = new MatrixVocabElement(db, "txt_mve");
        txt_mve.setType(MatrixVocabElement.MatrixType.TEXT);
        tfa = new TextStringFormalArg(db);
        txt_mve.appendFormalArg(tfa);
        db.vl.addElement(txt_mve);

        txt_mve2 = new MatrixVocabElement(db, "txt_mve2");
        txt_mve2.setType(MatrixVocabElement.MatrixType.TEXT);
        tfa2 = new TextStringFormalArg(db);
        txt_mve2.appendFormalArg(tfa2);
        db.vl.addElement(txt_mve2);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test 1 arg constructor, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db);

        assertNotNull(db);
        assertNotNull(t_value);

        assertNull(t_value.ItsDefault);
        assertEquals(t_value.itsValue, t_value.ItsDefault);        
    }

    /**
     * Test 1 argument constructor failure, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID());

        assertNotNull(db);
        assertNotNull(txt_mve);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertNull(t_value.ItsDefault);
        assertEquals(t_value.subRange, tfa.getSubRange());
        assertEquals(t_value.itsValue, t_value.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null,
                                                              tfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                            DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              txt_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "echo");

        assertNotNull(db);
        assertNotNull(txt_mve);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertEquals(t_value.subRange, tfa.getSubRange());
        assertNotNull(t_value.itsValue);
        assertEquals(t_value.itsValue, "echo");        
    }

    /**
     * Test0 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null,
                                                              tfa.getID(),
                                                              "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                             DBIndex.INVALID_ID,
                                                             "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              txt_mve.getID(),
                                                              "alpha");
    }

    /**
     * Test3 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              tfa.getID(),
                                                      "invalid \b text string");
    }

    /**
     * Test of copy constructor, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                               "foxtrot");
        TextStringDataValue t_copy = new TextStringDataValue(t_value);

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
     * Test of copy constructor failure, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        TextStringDataValue t_value = 
                            new TextStringDataValue((TextStringDataValue) null);
    }

    /**
     * Test of getItsValue method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");
        
        assertEquals(t_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");

        t_value.setItsValue("echo");
        assertEquals(t_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        TextStringDataValue t_value0 = new TextStringDataValue(db, tfa.getID(),
                                                               "bravo");

        TextStringDataValue t_value1 = new TextStringDataValue(db, tfa.getID(),
                                                               "nero");

        assertEquals(t_value0.toString(), "bravo");
        assertEquals(t_value1.toString(), "nero");
    }

    /**
     * Test of toDBString method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    /*
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(TextStringDataValue (id 0) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue bravo) " +
                                    "(subRange false))";

        String testDBString1 = "(TextStringDataValue (id 0) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";

        TextStringDataValue t_value0 = new TextStringDataValue(db, tfa.getID(),
                                                               "bravo");

        TextStringDataValue t_value1 = new TextStringDataValue(db, tfa.getID(),
                                                               "nero");

        assertEquals(t_value0.toDBString(), testDBString0);
        assertEquals(t_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of updateSubRange method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testUpdateSubRange() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");
        assertEquals(t_value.getSubRange(), false);
    }

    /**
     * Test of Construct method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        TextStringDataValue test = TextStringDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    /**
     * Test of TextStringDataValuesAreLogicallyEqual method, of class TextStringDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testTextStringDataValuesAreLogicallyEqual()
    throws SystemErrorException {
        TextStringDataValue n_value0 = new TextStringDataValue(db, tfa.getID(),
                                                               "bravo");
        TextStringDataValue n_value1 = new TextStringDataValue(db, tfa2.getID(),
                                                               "nero");
        TextStringDataValue n_copy = new TextStringDataValue(n_value0);

        assertTrue(TextStringDataValue.
                   TextStringDataValuesAreLogicallyEqual(n_value0, n_copy));
        assertFalse(TextStringDataValue.
                    TextStringDataValuesAreLogicallyEqual(n_value0, n_value1));
    }


    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        TextStringDataValue value0 =
                             new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue copy = (TextStringDataValue) value0.clone();

        assertEquals(value0, copy);
    }


    @Test
    public void testEquals()
    throws SystemErrorException {
        TextStringDataValue value0 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value1 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value2 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value3 =
                          new TextStringDataValue(db, tfa.getID(), "charlie");

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