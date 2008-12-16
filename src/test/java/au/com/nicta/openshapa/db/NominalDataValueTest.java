package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class NominalDataValue.
 * 
 * @author cfreeman
 */
public class NominalDataValueTest {
    private Database db;
    private MatrixVocabElement nom_mve;
    private NominalFormalArg nfa;

    private MatrixVocabElement nom_mve2;
    private NominalFormalArg nfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

    /**
     * Default test constructor.
     */
    public NominalDataValueTest() {
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
        nom_mve = new MatrixVocabElement(db, "nom_mve");
        nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
        nfa = new NominalFormalArg(db);
        nom_mve.appendFormalArg(nfa);
        db.vl.addElement(nom_mve);

        nom_mve2 = new MatrixVocabElement(db, "nom_mve2");
        nom_mve2.setType(MatrixVocabElement.MatrixType.NOMINAL);
        nfa2 = new NominalFormalArg(db);
        nfa2.setSubRange(true);
        nfa2.addApproved("alpha");
        nfa2.addApproved("bravo");
        nfa2.addApproved("charlie");
        nom_mve2.appendFormalArg(nfa2);
        db.vl.addElement(nom_mve2);

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
     * Test 1 arg constructor, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db);

        assertNotNull(db);
        assertNotNull(n_value);

        assertEquals(n_value.getDB(), db);
        assertEquals(n_value.itsValue, n_value.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID());
        NominalDataValue n_value1 = new NominalDataValue(db, nfa2.getID());

        assertNotNull(db);
        assertNotNull(nom_mve);
        assertNotNull(nfa);
        assertNotNull(nom_mve2);
        assertNotNull(nfa2);

        assertNotNull(n_value0);
        assertNotNull(n_value1);

        assertEquals(n_value0.ItsDefault, null);
        assertEquals(n_value0.subRange, nfa.subRange);
        assertEquals(n_value0.itsValue, n_value0.ItsDefault);

        assertEquals(n_value1.ItsDefault, null);
        assertEquals(n_value1.subRange, nfa2.getSubRange());
        assertEquals(n_value1.itsValue, n_value1.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database)null,
                                                        nfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nom_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID(),
                                                         "echo");
        NominalDataValue n_value1 = new NominalDataValue(db, nfa2.getID(),
                                                         "alpha");
        NominalDataValue n_value2 = new NominalDataValue(db, nfa2.getID(),
                                                         "delta");

        assertNotNull(db);
        assertNotNull(nom_mve);
        assertNotNull(nfa);
        assertNotNull(nom_mve2);
        assertNotNull(nfa2);

        assertNotNull(n_value0);
        assertNotNull(n_value1);
        assertNotNull(n_value2);

        assertEquals(n_value0.subRange, nfa.getSubRange());
        assertEquals(n_value0.itsValue, "echo");

        assertEquals(n_value1.subRange, nfa2.getSubRange());
        assertEquals(n_value1.itsValue, "alpha");

        assertEquals(n_value2.subRange, nfa2.getSubRange());
        assertNull(n_value2.itsValue);
    }

    /**
     * Test0 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database) null,
                                                        nfa.getID(), "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, DBIndex.INVALID_ID,
                                                        "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nom_mve.getID(),
                                                        "alpha");
    }

    /**
     * Test of copy constructor, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "foxtrot");
        NominalDataValue n_copy = new NominalDataValue(n_value);

        assertNotSame(n_value, n_copy);
        assertEquals(n_value.getDB(), n_copy.getDB());
        assertEquals(n_value.itsFargID, n_copy.itsFargID);
        assertEquals(n_value.itsFargType, n_copy.itsFargType);
        assertEquals(n_value.subRange, n_copy.subRange);
        assertEquals(n_value.toString(), n_copy.toString());
        assertEquals(n_value.toDBString(), n_copy.toDBString());
        assertEquals(n_value.getClass(), n_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((NominalDataValue)null);
    }

    /**
     * Test of getItsValue method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "bravo");

        assertEquals(n_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "bravo");

        n_value.setItsValue("echo");
        assertEquals(n_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID(),
                                                         "bravo");
        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(n_value0.toString(), "bravo");
        assertEquals(n_value1.toString(), "nero");
    }

    /**
     * Test of toDBString method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    /*
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(NominalDataValue (id 0) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue bravo) " +
                                    "(subRange true))";

        String testDBString1 = "(NominalDataValue (id 0) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";

        NominalDataValue n_value0 = new NominalDataValue(db, nfa2.getID(),
                                                         "bravo");

        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(n_value0.toDBString(), testDBString0);
        assertEquals(n_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of coerceToRange method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        NominalDataValue n_value = new NominalDataValue(db, nfa2.getID(),
                                                        "bravo");

        assertEquals(n_value.coerceToRange("alpha"), "alpha");
        assertEquals(n_value.coerceToRange("bravo"), "bravo");
        assertEquals(n_value.coerceToRange("charlie"), "charlie");
        assertNull(n_value.coerceToRange("echo"));
        assertNull(n_value.coerceToRange("alph"));
        assertNull(n_value.coerceToRange("alphaa"));
        assertNull(n_value.coerceToRange("charly"));
    }

    /**
     * Test of isQueryVar method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testIsQueryVar() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, ufa.getID(),
                                                         "?query_var");
        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "!?query_var");
        NominalDataValue n_value2 = new NominalDataValue(db, ufa.getID(), "?");

        assertEquals(n_value0.isQueryVar(), true);
        assertEquals(n_value1.isQueryVar(), false);
        assertEquals(n_value2.isQueryVar(), true);
    }

    /**
     * Test of Construct method, of class NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        NominalDataValue test = NominalDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    /**
     * Test of NominalDataValuesAreLogicallyEqual method, of class
     * NominalDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testNominalDataValuesAreLogicallyEqual()
    throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID(),
                                                         "bravo");
        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "nero");
        NominalDataValue n_copy = new NominalDataValue(n_value0);

        assertTrue(NominalDataValue.
                   NominalDataValuesAreLogicallyEqual(n_value0, n_copy));
        assertFalse(NominalDataValue.
                    NominalDataValuesAreLogicallyEqual(n_value0,n_value1));
    }
}