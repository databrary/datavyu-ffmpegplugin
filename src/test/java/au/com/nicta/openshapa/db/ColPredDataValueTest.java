package au.com.nicta.openshapa.db;

import java.util.Vector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
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

    

    public ColPredDataValueTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();

        matrix_mve = new MatrixVocabElement(db, "matrix_mve");
        matrix_mve.setType(MatrixVocabElement.matrixType.MATRIX);

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
        float_mve.setType(MatrixVocabElement.matrixType.FLOAT);
        farg = new FloatFormalArg(db);
        float_mve.appendFormalArg(farg);
        db.vl.addElement(float_mve);
    }

    @After
    public void tearDown() {
    }

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

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue((Database) null,
                                                     untypedFarg.getID());
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue(db, DBIndex.INVALID_ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        ColPredDataValue cpdv = new ColPredDataValue(db, matrix_mve.getID());
    }

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

        ColPredDataValue float_cpdv0 = new ColPredDataValue(db, colPredFarg.getID(), float_cp0);
        
    }

    /**
     * Test of getItsValueBlind method, of class ColPredDataValue.
     */
    @Test
    public void testGetItsValueBlind() throws Exception {
        System.out.println("getItsValueBlind");
        ColPredDataValue instance = null;
        ColPred expResult = null;
        ColPred result = instance.getItsValueBlind();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getItsValueMveID method, of class ColPredDataValue.
     */
    @Test
    public void testGetItsValueMveID() throws Exception {
        System.out.println("getItsValueMveID");
        ColPredDataValue instance = null;
        long expResult = 0L;
        long result = instance.getItsValueMveID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setItsValue method, of class ColPredDataValue.
     */
    @Test
    public void testSetItsValue() throws Exception {
        System.out.println("setItsValue");
        ColPred value = null;
        ColPredDataValue instance = null;
        instance.setItsValue(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearID method, of class ColPredDataValue.
     */
    @Test
    public void testClearID() throws Exception {
        System.out.println("clearID");
        ColPredDataValue instance = null;
        instance.clearID();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insertInIndex method, of class ColPredDataValue.
     */
    @Test
    public void testInsertInIndex() throws Exception {
        System.out.println("insertInIndex");
        long DCID = 0L;
        ColPredDataValue instance = null;
        instance.insertInIndex(DCID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFromIndex method, of class ColPredDataValue.
     */
    @Test
    public void testRemoveFromIndex() throws Exception {
        System.out.println("removeFromIndex");
        long DCID = 0L;
        ColPredDataValue instance = null;
        instance.removeFromIndex(DCID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of replaceInIndex method, of class ColPredDataValue.
     */
    @Test
    public void testReplaceInIndex() throws Exception {
        System.out.println("replaceInIndex");
        DataValue old_dv = null;
        long DCID = 0L;
        boolean cascadeMveMod = false;
        boolean cascadeMveDel = false;
        long cascadeMveID = 0L;
        boolean cascadePveMod = false;
        boolean cascadePveDel = false;
        long cascadePveID = 0L;
        ColPredDataValue instance = null;
        instance.replaceInIndex(old_dv, DCID, cascadeMveMod, cascadeMveDel, cascadeMveID, cascadePveMod, cascadePveDel, cascadePveID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class ColPredDataValue.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        ColPredDataValue instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toDBString method, of class ColPredDataValue.
     */
    @Test
    public void testToDBString() {
        System.out.println("toDBString");
        ColPredDataValue instance = null;
        String expResult = "";
        String result = instance.toDBString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForMVEDefChange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForMVEDefChange() throws Exception {
        System.out.println("updateForMVEDefChange");
        Database db = null;
        long pveID = 0L;
        boolean nameChanged = false;
        String oldName = "";
        String newName = "";
        boolean varLenChanged = false;
        boolean oldVarLen = false;
        boolean newVarLen = false;
        boolean fargListChanged = false;
        long[] n2o = null;
        long[] o2n = null;
        boolean[] fargNameChanged = null;
        boolean[] fargSubRangeChanged = null;
        boolean[] fargRangeChanged = null;
        boolean[] fargDeleted = null;
        boolean[] fargInserted = null;
        Vector<FormalArgument> oldFargList = null;
        Vector<FormalArgument> newFargList = null;
        long[] cpn2o = null;
        long[] cpo2n = null;
        boolean[] cpFargNameChanged = null;
        boolean[] cpFargSubRangeChanged = null;
        boolean[] cpFargRangeChanged = null;
        boolean[] cpFargDeleted = null;
        boolean[] cpFargInserted = null;
        Vector<FormalArgument> oldCPFargList = null;
        Vector<FormalArgument> newCPFargList = null;
        ColPredDataValue instance = null;
        instance.updateForMVEDefChange(db, pveID, nameChanged, oldName, newName, varLenChanged, oldVarLen, newVarLen, fargListChanged, n2o, o2n, fargNameChanged, fargSubRangeChanged, fargRangeChanged, fargDeleted, fargInserted, oldFargList, newFargList, cpn2o, cpo2n, cpFargNameChanged, cpFargSubRangeChanged, cpFargRangeChanged, cpFargDeleted, cpFargInserted, oldCPFargList, newCPFargList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForMVEDeletion method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForMVEDeletion() throws Exception {
        System.out.println("updateForMVEDeletion");
        Database db = null;
        long mveID = 0L;
        ColPredDataValue instance = null;
        instance.updateForMVEDeletion(db, mveID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForPVEDefChange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForPVEDefChange() throws Exception {
        System.out.println("updateForPVEDefChange");
        Database db = null;
        long pveID = 0L;
        boolean nameChanged = false;
        String oldName = "";
        String newName = "";
        boolean varLenChanged = false;
        boolean oldVarLen = false;
        boolean newVarLen = false;
        boolean fargListChanged = false;
        long[] n2o = null;
        long[] o2n = null;
        boolean[] fargNameChanged = null;
        boolean[] fargSubRangeChanged = null;
        boolean[] fargRangeChanged = null;
        boolean[] fargDeleted = null;
        boolean[] fargInserted = null;
        Vector<FormalArgument> oldFargList = null;
        Vector<FormalArgument> newFargList = null;
        ColPredDataValue instance = null;
        instance.updateForPVEDefChange(db, pveID, nameChanged, oldName, newName, varLenChanged, oldVarLen, newVarLen, fargListChanged, n2o, o2n, fargNameChanged, fargSubRangeChanged, fargRangeChanged, fargDeleted, fargInserted, oldFargList, newFargList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForPVEDeletion method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForPVEDeletion() throws Exception {
        System.out.println("updateForPVEDeletion");
        Database db = null;
        long pveID = 0L;
        ColPredDataValue instance = null;
        instance.updateForPVEDeletion(db, pveID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateForFargChange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
        System.out.println("updateForFargChange");
        boolean fargNameChanged = false;
        boolean fargSubRangeChanged = false;
        boolean fargRangeChanged = false;
        FormalArgument oldFA = null;
        FormalArgument newFA = null;
        ColPredDataValue instance = null;
        instance.updateForFargChange(fargNameChanged, fargSubRangeChanged, fargRangeChanged, oldFA, newFA);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSubRange method, of class ColPredDataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
        System.out.println("updateSubRange");
        FormalArgument fa = null;
        ColPredDataValue instance = null;
        instance.updateSubRange(fa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of coerceToRange method, of class ColPredDataValue.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        System.out.println("coerceToRange");
        ColPred value = null;
        ColPredDataValue instance = null;
        ColPred expResult = null;
        ColPred result = instance.coerceToRange(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deregisterPreds method, of class ColPredDataValue.
     */
    @Test
    public void testDeregisterPreds() throws Exception {
        System.out.println("deregisterPreds");
        boolean cascadeMveDel = false;
        long cascadeMveID = 0L;
        boolean cascadePveDel = false;
        long cascadePveID = 0L;
        ColPredDataValue instance = null;
        instance.deregisterPreds(cascadeMveDel, cascadeMveID, cascadePveDel, cascadePveID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerPreds method, of class ColPredDataValue.
     */
    @Test
    public void testRegisterPreds() throws Exception {
        System.out.println("registerPreds");
        ColPredDataValue instance = null;
        instance.registerPreds();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ColPredDataValuesAreLogicallyEqual method, of class ColPredDataValue.
     */
    @Test
    public void testColPredDataValuesAreLogicallyEqual() throws Exception {
        System.out.println("ColPredDataValuesAreLogicallyEqual");
        ColPredDataValue cpdv0 = null;
        ColPredDataValue cpdv1 = null;
        boolean expResult = false;
        boolean result = ColPredDataValue.ColPredDataValuesAreLogicallyEqual(cpdv0, cpdv1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Construct method, of class ColPredDataValue.
     */
    @Test
    public void testConstruct() throws Exception {
        System.out.println("Construct");
        Database db = null;
        ColPred cp = null;
        ColPredDataValue expResult = null;
        ColPredDataValue result = ColPredDataValue.Construct(db, cp);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}