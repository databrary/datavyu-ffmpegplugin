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
    private static Database db;
    private MatrixVocabElement predMVE;
    private PredFormalArg pfa;
    private PredDataValue pdv;

    private MatrixVocabElement predMVE2;
    private PredFormalArg pfa2;

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
        predMVE = new MatrixVocabElement(db, "pred_mve");
        predMVE.setType(MatrixVocabElement.MatrixType.PREDICATE);
        pfa = new PredFormalArg(db);
        predMVE.appendFormalArg(pfa);
        db.vl.addElement(predMVE);

        predMVE2 = new MatrixVocabElement(db, "pred_mve2");
        predMVE2.setType(MatrixVocabElement.MatrixType.PREDICATE);
        pfa2 = new PredFormalArg(db);
        pfa2.setSubRange(true);

        PredicateVocabElement pve1 = new PredicateVocabElement(db, "pve1");
        pve1.appendFormalArg(new IntFormalArg(db, "<int>"));
        pve1.appendFormalArg(new UnTypedFormalArg(db, "<arg2>"));
        db.addPredVE(pve1);

        predMVE2.appendFormalArg(pfa2);
        db.vl.addElement(predMVE2);

        pdv = new PredDataValue(db, pfa.getID());

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

    @Test
    @Override
    public void testGetItsFargType() {
        assertEquals(pdv.itsFargType, FormalArgument.fArgType.PREDICATE);
    }

    @Test
    @Override
    public void testGetItsFargID() {
        assertEquals(pdv.itsFargID, 2);
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        PredDataValue value0 = new PredDataValue(db, pfa.getID());
        PredDataValue value1 = new PredDataValue(db, pfa.getID());
        PredDataValue value2 = new PredDataValue(db, pfa2.getID());

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        PredDataValue value0 = new PredDataValue(db, pfa.getID());
        PredDataValue value1 = new PredDataValue(db, pfa.getID());
        PredDataValue value2 = new PredDataValue(db, pfa.getID());
        PredDataValue value3 = new PredDataValue(db, pfa2.getID());

        super.testEquals(value0, value1, value2, value3);
    }

    /**
     * Test 1 arg constructor of class PredDataValue.
     *
     * @throws SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        PredDataValue value = new PredDataValue(db);

        assertNotNull(db);
        assertNotNull(value);

        assertEquals(value.getDB(), db);

        DataValueTest.verify1ArgInitialization(db, value);

        // When building a PredDataValue, at one point setItsValue(null)
        // is called - this actually sets it to an empty predicate
        assertNotNull(value.itsValue);
        assertTrue(value.itsValue.getPveID() == DBIndex.INVALID_ID);

    } /* PredDataValue::Test1ArgConstructor() */
}
