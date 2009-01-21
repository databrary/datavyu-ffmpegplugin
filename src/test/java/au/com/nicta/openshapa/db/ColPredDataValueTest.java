package au.com.nicta.openshapa.db;

import java.util.Vector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class ColPredDataValue.
 *
 * @author cfreeman
 */
public class ColPredDataValueTest {

    private Database db;

    private MatrixVocabElement matrix_mve;
    private FormalArgument farg;
    private long matrix_mve_ID;
    private FormalArgument untypedFarg;
    private FormalArgument colPredFarg;
    private MatrixVocabElement float_mve;

    /**
     * Default test constructor.
     */
    public ColPredDataValueTest() {
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

        matrix_mve = new MatrixVocabElement(db, "matrix_mve");
        matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);

        farg = new FloatFormalArg(db, "<float>");
        matrix_mve.appendFormalArg(farg);
        farg = new IntFormalArg(db, "<int>");
        matrix_mve.appendFormalArg(farg);
        farg = new NominalFormalArg(db, "<nominal>");
        matrix_mve.appendFormalArg(farg);
        farg = new PredFormalArg(db, "<pred>");
        matrix_mve.appendFormalArg(farg);
        farg = new QuoteStringFormalArg(db, "<qstring>");
        matrix_mve.appendFormalArg(farg);
        farg = new TimeStampFormalArg(db, "<timestamp>");
        matrix_mve.appendFormalArg(farg);
        farg = new UnTypedFormalArg(db, "<untyped>");
        matrix_mve.appendFormalArg(farg);
        farg = new ColPredFormalArg(db, "<colpred>");
        matrix_mve.appendFormalArg(farg);
        db.vl.addElement(matrix_mve);
        matrix_mve_ID = matrix_mve.getID();

        // get a copy of matrix_mve as insterted into the db
        matrix_mve = db.getMatrixVE(matrix_mve_ID);

        untypedFarg = matrix_mve.getFormalArg(6);
        colPredFarg = matrix_mve.getFormalArg(7);

        float_mve = new MatrixVocabElement(db, "float_mve");
        float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
        farg = new FloatFormalArg(db);
        float_mve.appendFormalArg(farg);
        db.vl.addElement(float_mve);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of updateForFargChange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
    }


    /**
     * Test of updateSubRange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
    }


    /**
     * Test 1 arg constructor, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue(db);

        assertNotNull(db);
        assertNotNull(cpdv);

        assertNotNull(cpdv.itsValue);
        assertEquals(cpdv.itsValue.getMveID(), DBIndex.INVALID_ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        ColPredDataValue cpdb = new ColPredDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {

        assertEquals(untypedFarg.getFargType(),
                     FormalArgument.fArgType.UNTYPED);


        ColPredDataValue cpdv0 = new ColPredDataValue(db, untypedFarg.getID());

        colPredFarg = matrix_mve.getFormalArg(7);
        assertEquals(colPredFarg.getFargType(),
                     FormalArgument.fArgType.COL_PREDICATE);

        ColPredDataValue cpdv1 = new ColPredDataValue(db, colPredFarg.getID());
        ColPred cp = new ColPred(db);

        assertNotNull(cpdv0);
        assertNotNull(cpdv1);
        assertNotNull(cp);

        assertNotNull(cpdv0.itsValue);
        assertEquals(cpdv0.db, cp.db);
        assertEquals(cpdv0.db, db);
        assertEquals(cpdv0.getID(), DBIndex.INVALID_ID);
        assertEquals(cpdv0.itsCellID, DBIndex.INVALID_ID);
        assertEquals(cpdv0.itsFargID, untypedFarg.getID());
        assertEquals(cpdv0.getLastModUID(), DBIndex.INVALID_ID);

        assertNotNull(cpdv1.itsValue);
        assertEquals(cpdv1.db, cp.db);
        assertEquals(cpdv1.db, db);
        assertEquals(cpdv1.getID(), DBIndex.INVALID_ID);
        assertEquals(cpdv1.itsCellID, DBIndex.INVALID_ID);
        assertEquals(cpdv1.itsFargID, colPredFarg.getID());
        assertEquals(cpdv1.getLastModUID(), DBIndex.INVALID_ID);
    }

    /**
     * Test0 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue((Database) null,
                                                     untypedFarg.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue(db, matrix_mve.getID());
    }

    /**
     * Test3 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        long fargID = matrix_mve.getCPFormalArg(10).getID();
        new ColPredDataValue(db, fargID, new ColPred(db, float_mve.getID()));

    }

    /**
     * Test of getItsValue method, of class ColPredDataValue.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        Vector<DataValue> float_cp_arg_list = new Vector<DataValue>();
        long fargID = float_mve.getCPFormalArg(0).getID();
        DataValue arg = new IntDataValue(db, fargID, 11);
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(1).getID();
        arg = new TimeStampDataValue(db, fargID, new TimeStamp(db.getTicks(), 11));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(2).getID();
        arg = new TimeStampDataValue(db, fargID, new TimeStamp(db.getTicks(), 11 * db.getTicks()));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(3).getID();
        arg = new FloatDataValue(db, fargID, 11.0);
        float_cp_arg_list.add(arg);
        ColPred float_cp0 = new ColPred(db, float_mve.getID(), float_cp_arg_list);

        ColPredDataValue float_cpdv0 = new ColPredDataValue(db, colPredFarg.getID(), float_cp0);
        ColPred source = float_cpdv0.getItsValue();

        assertEquals(source, float_cp0);
    }

    /**
     * Test of setItsValue method, of class ColPredDataValue.
     */
    @Test
    public void testSetItsValue() throws Exception {
        ColPredDataValue cpdv = new ColPredDataValue(db,
                                                     untypedFarg.getID(),
                                                     null);

        Vector<DataValue> float_cp_arg_list = new Vector<DataValue>();
        long fargID = float_mve.getCPFormalArg(0).getID();
        DataValue arg = new IntDataValue(db, fargID, 11);
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(1).getID();
        arg = new TimeStampDataValue(db, fargID, new TimeStamp(db.getTicks(), 11));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(2).getID();
        arg = new TimeStampDataValue(db, fargID, new TimeStamp(db.getTicks(), 11 * db.getTicks()));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(3).getID();
        arg = new FloatDataValue(db, fargID, 11.0);
        float_cp_arg_list.add(arg);
        ColPred float_cp0 = new ColPred(db, float_mve.getID(), float_cp_arg_list);
        cpdv.setItsValue(float_cp0);

        assertEquals(float_cp0, cpdv.getItsValue());
    }

    /**
     * Test of hashCode method, of class ColPredDataValue.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        ColPredDataValue instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class ColPredDataValue.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        ColPredDataValue instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}