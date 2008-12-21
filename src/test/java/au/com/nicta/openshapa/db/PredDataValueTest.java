package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class PredDataValue.
 *
 * @author cfreeman
 */
public class PredDataValueTest extends DataValueTest {
    private Database db;
    private MatrixVocabElement nom_mve;
    private NominalFormalArg nfa;
    private PredDataValue pdv;

    private MatrixVocabElement nom_mve2;
    private NominalFormalArg nfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

    @Override
    public DataValue getInstance() {
        return pdv;
    }

    /**
     * Default test constructor.
     */
    public PredDataValueTest() {
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
        pdv = new PredDataValue(db);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        PredDataValue value0 =
                                new PredDataValue(db);
        PredDataValue copy = (PredDataValue) value0.clone();

        assertEquals(value0, copy);
    }


    @Test
    @Override
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        super.testEquals();
        PredDataValue value0 = new PredDataValue(db);
        PredDataValue value1 = new PredDataValue(db);
        PredDataValue value2 = new PredDataValue(db);
        PredDataValue value3 = new PredDataValue(db); /* TODO: different args */

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
// TODO:      assertFalse(value0.equals(value3));
//        assertTrue(value0.hashCode() != value3.hashCode());

        // modify value3
/*        String val = value3.getItsValue();
        val = "bravo";
        value3.setItsValue(val);
        assertTrue(value0.equals(value3));
        assertTrue(value3.equals(value1));
        assertTrue(value2.hashCode() == value3.hashCode());
 * */
    }

}