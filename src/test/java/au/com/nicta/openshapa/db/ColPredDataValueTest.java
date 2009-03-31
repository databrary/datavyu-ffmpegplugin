package au.com.nicta.openshapa.db;

import java.io.PrintStream;
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
public class ColPredDataValueTest extends DataValueTest {

    private Database db;

    private MatrixVocabElement matrix_mve;
    private FormalArgument farg;
    private long matrix_mve_ID;
    private FormalArgument untypedFarg;
    private FormalArgument colPredFarg;
    private MatrixVocabElement float_mve;

    private ColPredDataValue cpdv;

    private PrintStream outStream;
    private boolean verbose;

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
    public void setUp() throws SystemErrorException, LogicErrorException {
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

        cpdv = new ColPredDataValue(db, colPredFarg.getID());

        verbose = true;
        outStream = System.out;
    }

    @Override
    public DataValue getInstance() {
        return cpdv;
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
                     FormalArgument.FArgType.UNTYPED);


        ColPredDataValue cpdv0 = new ColPredDataValue(db, untypedFarg.getID());

        colPredFarg = matrix_mve.getFormalArg(7);
        assertEquals(colPredFarg.getFargType(),
                     FormalArgument.FArgType.COL_PREDICATE);

        ColPredDataValue cpdv1 = new ColPredDataValue(db, colPredFarg.getID());
        ColPred cp = new ColPred(db);

        assertNotNull(cpdv0);
        assertNotNull(cpdv1);
        assertNotNull(cp);

        assertNotNull(cpdv0.itsValue);
        assertEquals(cpdv0.getDB(), cp.getDB());
        assertEquals(cpdv0.getDB(), db);
        assertEquals(cpdv0.getID(), DBIndex.INVALID_ID);
        assertEquals(cpdv0.itsCellID, DBIndex.INVALID_ID);
        assertEquals(cpdv0.itsFargID, untypedFarg.getID());
        assertEquals(cpdv0.getLastModUID(), DBIndex.INVALID_ID);

        assertNotNull(cpdv1.itsValue);
        assertEquals(cpdv1.getDB(), cp.getDB());
        assertEquals(cpdv1.getDB(), db);
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
        ColPredDataValue cpdv2 = new ColPredDataValue((Database) null,
                                                     untypedFarg.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        ColPredDataValue cpdv2 = new ColPredDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class ColPredDataValue.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        ColPredDataValue cpdv2 = new ColPredDataValue(db, matrix_mve.getID());
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
        arg = new TimeStampDataValue(db, fargID,
                                            new TimeStamp(db.getTicks(), 11));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(2).getID();
        arg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 11 * db.getTicks()));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(3).getID();
        arg = new FloatDataValue(db, fargID, 11.0);
        float_cp_arg_list.add(arg);
        ColPred float_cp0 = new ColPred(db, float_mve.getID(),
                                                            float_cp_arg_list);

        ColPredDataValue float_cpdv0 = new ColPredDataValue(db,
                                                colPredFarg.getID(), float_cp0);
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
        arg = new TimeStampDataValue(db, fargID,
                                            new TimeStamp(db.getTicks(), 11));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(2).getID();
        arg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 11 * db.getTicks()));
        float_cp_arg_list.add(arg);
        fargID = float_mve.getCPFormalArg(3).getID();
        arg = new FloatDataValue(db, fargID, 11.0);
        float_cp_arg_list.add(arg);
        ColPred float_cp0 = new ColPred(db, float_mve.getID(),
                                                            float_cp_arg_list);
        cpdv.setItsValue(float_cp0);

        assertEquals(float_cp0, cpdv.getItsValue());
    }

    @Test
    @Override
    public void testGetItsFargType() {
        assertEquals(cpdv.itsFargType, FormalArgument.FArgType.COL_PREDICATE);
    }

    @Test
    @Override
    public void testGetItsFargID() {
        assertEquals(cpdv.itsFargID, 9);
    }

    /**
     * Test of hashCode method, of class ColPredDataValue.
     */
    @Test
    public void testHashCode() throws SystemErrorException {
        ColPredDataValue value0 = new ColPredDataValue(db, colPredFarg.getID());
        ColPredDataValue value1 = new ColPredDataValue(db, colPredFarg.getID());
        ColPredDataValue value2 = new ColPredDataValue(db, untypedFarg.getID());

        super.testHashCode(value0, value1, value2);
    }

    /**
     * Test of equals method, of class ColPredDataValue.
     */
    @Test
    public void testEquals() throws SystemErrorException {
        ColPredDataValue value0 = new ColPredDataValue(db, colPredFarg.getID());
        ColPredDataValue value1 = new ColPredDataValue(db, colPredFarg.getID());
        ColPredDataValue value2 = new ColPredDataValue(db, colPredFarg.getID());
        ColPredDataValue value3 = new ColPredDataValue(db, untypedFarg.getID());

        super.testEquals(value0, value1, value2, value3);
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for ColPredDataValue.  Verify that all
     *         fields are set to the expected defaults.
     *
     *      b) Verify that the one argument constructor fails on invalid
     *         input.  Given the compiler checks, this probably just means
     *         verifying that the constructor fails on null.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database, and a mve (matrix vocab element) with one
     *         formal argument.  Insert the mve into the database, and make
     *         note of the IDs assigned to them (including the formal argument).
     *
     *         Construct a ColPredDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the PredDataValue's itsFargID,
     *         itsFargType, and subRange fields match those of the formal
     *         argument, and that all other fields are set to the expected
     *         defaults.
     *
     *         Repeat for a variety of formal argument types and settings.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      As per two argument constructor, save that a value is supplied
     *      to the constructor.  Verify that this value appears in the
     *      PredDataValue -- perhaps after having been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a PredDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         PredDataValue, and verify that the copy is correct.
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), setItsValue() and coerceToRange()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the DataValue.TestAccessors()
     *      method.
     *
     *      Verify that setItsValue() and coerceToRange() fail on invalid
     *      input.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/10/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class ColPredDataValue        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred int_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp0 = null;
        ColPred pred_cp0 = null;
        ColPred text_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue int_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;
        ColPredDataValue matrix_cpdv1 = null;
        ColPredDataValue matrix_cpdv2 = null;
        ColPredDataValue nominal_cpdv0 = null;
        ColPredDataValue pred_cpdv0 = null;
        ColPredDataValue text_cpdv0 = null;


        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.
        //
        // For now, at least, the selection of mve's and cp's used in this
        // test is overkill.  But since I didn't figure this out until I had
        // already prepared them, I may as well leave them and use them all.
        // The day may come when they actually do something useful.

        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL)||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE )||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mv1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Create test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp0 = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getFormalArg(0).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp0 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp0 = new ColPred(db, text_mve_ID, text_cp_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp0 == null )
                    {
                        outStream.printf("allocation of int_cp0 failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp0 failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp0 == null )
                    {
                        outStream.printf("allocation of pred_cp0 failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp0 == null )
                    {
                        outStream.printf("allocation of text_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                          "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            String testString0 =
                    "()";
            String testString1 =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
            String testString2 =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String testString3 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
            String testString4 =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, \" a q string \", <arg2>, 88)";
            String testString5 =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String testString6 =
                 "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String testString7 =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String testString8 =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";

            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve0.getFormalArg(6);
                assertTrue( untyped_farg.getFargType() == FormalArgument.FArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assertTrue( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve0.getFormalArg(7);
                assertTrue( col_pred_farg.getFargType() ==
                        FormalArgument.FArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assertTrue( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                int_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, int_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp0);
                matrix_cpdv1 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp1);
                matrix_cpdv2 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp2);
                nominal_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, nominal_cp0);
                pred_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, pred_cp0);
                text_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, text_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( int_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( matrix_cpdv1 == null ) ||
                 ( matrix_cpdv2 == null ) ||
                 ( nominal_cpdv0 == null ) ||
                 ( pred_cpdv0 == null ) ||
                 ( text_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( int_cpdv0 == null )
                    {
                        outStream.print("allocation of int_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv1 == null )
                    {
                        outStream.print("allocation of matrix_cpdv1 failed.\n");
                    }

                    if ( matrix_cpdv2 == null )
                    {
                        outStream.print("allocation of matrix_cpdv2 failed.\n");
                    }

                    if ( nominal_cpdv0 == null )
                    {
                        outStream.print("allocation of nominal_cpdv0 failed.");
                    }

                    if ( pred_cpdv0 == null )
                    {
                        outStream.print("allocation of pred_cpdv0 failed.\n");
                    }

                    if ( text_cpdv0 == null )
                    {
                        outStream.print("allocation of text_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test " +
                                         "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( ( cpdv.toString().compareTo(testString0) != 0 ) ||
                      ( float_cpdv0.toString().compareTo(testString1) != 0 ) ||
                      ( int_cpdv0.toString().compareTo(testString2) != 0 ) ||
                      ( matrix_cpdv0.toString().compareTo(testString3) != 0 ) ||
                      ( matrix_cpdv1.toString().compareTo(testString4) != 0 ) ||
                      ( matrix_cpdv2.toString().compareTo(testString5) != 0 ) ||
                      ( nominal_cpdv0.toString().compareTo(testString6) != 0 ) ||
                      ( pred_cpdv0.toString().compareTo(testString7) != 0 ) ||
                      ( text_cpdv0.toString().compareTo(testString8) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected cpdv.toString)(): \"%s\"\n",
                                         cpdv.toString());
                    }

                    if ( float_cpdv0.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected float_cpdv0.toString)(): \"%s\"\n",
                               float_cpdv0.toString());
                    }

                    if ( int_cpdv0.toString().compareTo(testString2) != 0 )
                    {
                       outStream.printf(
                               "Unexpected int_cpdv0.toString)(): \"%s\"\n",
                               int_cpdv0.toString());
                    }

                    if ( matrix_cpdv0.toString().compareTo(testString3) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv0.toString)(): \"%s\"\n",
                               matrix_cpdv0.toString());
                    }

                    if ( matrix_cpdv1.toString().compareTo(testString4) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv1.toString)(): \"%s\"\n",
                               matrix_cpdv1.toString());
                    }

                    if ( matrix_cpdv2.toString().compareTo(testString5) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv2.toString)(): \"%s\"\n",
                               matrix_cpdv2.toString());
                    }

                    if ( nominal_cpdv0.toString().compareTo(testString6) != 0 )
                    {
                       outStream.printf(
                               "Unexpected nominal_cpdv0.toString)(): \"%s\"\n",
                               nominal_cpdv0.toString());
                    }

                    if ( pred_cpdv0.toString().compareTo(testString7) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred_cpdv0.toString)(): \"%s\"\n",
                               pred_cpdv0.toString());
                    }

                    if ( text_cpdv0.toString().compareTo(testString8) != 0 )
                    {
                       outStream.printf(
                               "Unexpected text_cpdv0.toString)(): \"%s\"\n",
                               text_cpdv0.toString());
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               cpdv,
                                                               outStream,
                                                               verbose,
                                                               "cpdv");

            if ( cpdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("cpdv.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(new ColPred(db),
                                                      cpdv.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "new ColPred(db)",
                                                      "cpdv.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               float_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "float_cpdv0");

            if ( float_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("float_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(float_cp0,
                                                      float_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "float_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               int_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "int_cpdv0");

            if ( int_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("int_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(int_cp0,
                                                      int_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "int_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               matrix_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv0");

            if ( matrix_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(matrix_cp0,
                                                      matrix_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "matrix_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               matrix_cpdv1,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv1");

            if ( matrix_cpdv1.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv1.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(matrix_cp1,
                                                      matrix_cpdv1.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp1",
                                                      "matrix_cpdv1.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               matrix_cpdv2,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv2");

            if ( matrix_cpdv2.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv2.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(matrix_cp2,
                                                      matrix_cpdv2.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp2",
                                                      "matrix_cpdv2.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               nominal_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "nominal_cpdv0");

            if ( nominal_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("nominal_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(nominal_cp0,
                                                      nominal_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "nominal_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               pred_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "pred_cpdv0");

            if ( pred_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pred_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(pred_cp0,
                                                      pred_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "pred_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               text_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "text_cpdv0");

            if ( text_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("text_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(text_cp0,
                                                      text_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "text_cpdv0.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue((Database)null, untyped_farg_ID,
                                            float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv != null )
                    {
                        outStream.print("new ColPredDataValue(null, " +
                                "untyped_farg_ID, float_cp0) returned " +
                                "non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID(), p4) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "untyped_farg_ID, float_cp0) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue(db, DBIndex.INVALID_ID, float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv != null )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "float_cp0) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, float_cp0) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "float_cp0) failed to throw a " +
                                        "system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue(db, pred_mve.getID(), float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), float_cp0) returned.\n");
                    }

                    if ( cpdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), float_cp0) returned " +
                                "non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, " +
                                 "pred_mve.getID(), float_cp0) failed to " +
                                 "throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* PredDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class ColPredDataValue accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        ColPred float_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;
        Database alt_db = null;
        long alt_float_mve0_ID = DBIndex.INVALID_ID;
        MatrixVocabElement alt_float_mve0 = null;
        Vector<DataValue> alt_float_cp0_arg_list = null;
        ColPred alt_float_cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.

        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();
            matrix_mve1 = db.getMatrixVE(matrix_mve1_ID);

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();
            matrix_mve2 = db.getMatrixVE(matrix_mve2_ID);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve1.getNumFormalArgs() != 1 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
                }


                if ( ! completed )
                {
                    outStream.print("Create test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix create threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve1.getFormalArg(0);
                assertTrue( untyped_farg.getFargType() == FormalArgument.FArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assertTrue( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve2.getFormalArg(0);
                assertTrue( col_pred_farg.getFargType() ==
                        FormalArgument.FArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assertTrue( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test "
                                        + "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               cpdv,
                                                               outStream,
                                                               verbose,
                                                               "cpdv");

            if ( cpdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("cpdv.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(new ColPred(db),
                                                      cpdv.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "new ColPred(db)",
                                                      "cpdv.itsValue");

                failures += DataValueTest.TestAccessors(db, untyped_farg,
                        matrix_mve2, col_pred_farg, cpdv, outStream, verbose);
            }

            if ( failures == 0 )
            {
                cpdv.setItsValue(float_cp0);

                if ( cpdv.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("cpdv.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPredTest.VerifyColPredCopy(float_cp0,
                                                          cpdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "float_cp0",
                                                          "cpdv.itsValue");
                }
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               float_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "float_cpdv0");

            if ( float_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("float_cpdv0.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(float_cp0,
                                                      float_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "float_cpdv0.itsValue");

                failures += DataValueTest.TestAccessors(db, col_pred_farg,
                        matrix_mve1, untyped_farg, float_cpdv0, outStream,
                        verbose);
            }

            if ( failures == 0 )
            {
                float_cpdv0.setItsValue(matrix_cp0);

                if ( float_cpdv0.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "float_cpdv0.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPredTest.VerifyColPredCopy(matrix_cp0,
                                                        float_cpdv0.itsValue,
                                                        outStream,
                                                        verbose,
                                                        "matrix_cp0",
                                                        "float_cpdv0.itsValue");
                }
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               matrix_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv0");

            if ( matrix_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv0.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPredTest.VerifyColPredCopy(matrix_cp0,
                                                      matrix_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "matrix_cpdv0.itsValue");

                failures += DataValueTest.TestAccessors(db, untyped_farg,
                        matrix_mve2, col_pred_farg, matrix_cpdv0, outStream,
                        verbose);
            }

            if ( failures == 0 )
            {
                matrix_cpdv0.setItsValue(null);

                if ( matrix_cpdv0.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "matrix_cpdv0.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPredTest.VerifyColPredCopy(new ColPred(db),
                                                       matrix_cpdv0.itsValue,
                                                       outStream,
                                                       verbose,
                                                       "new ColPred(db)",
                                                       "matrix_cpdv0.itsValue");
                }
            }
        }

        /* For now at least, there is no real need to test setItsValue with
         * invalid values.  The compiler requires that the supplied parameter
         * is an instance of ColPred, and the value supplied (if not null or
         * an empty ColPred) is passed through to the target formal arguments
         * isValidValue routine.  Since we already have tests for these
         * routines, there is no need to test them here.
         *
         * That said, against changes in the code, it is probably worth while
         * to pass through an invalid ColPred or two just to be sure.
         *
         * Start with setup for test:
         */

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                alt_db = new ODBCDatabase();

                alt_float_mve0 = new MatrixVocabElement(alt_db, "alt_float_mve0");
                alt_float_mve0.setType(MatrixVocabElement.MatrixType.FLOAT);
                farg = new FloatFormalArg(alt_db);
                alt_float_mve0.appendFormalArg(farg);
                alt_db.vl.addElement(alt_float_mve0);
                alt_float_mve0_ID = alt_float_mve0.getID();


                alt_float_cp0_arg_list = new Vector<DataValue>();
                fargID = alt_float_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(alt_db, fargID, 11);
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(alt_db, fargID,
                        new TimeStamp(alt_db.getTicks(), 11));
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(alt_db, fargID,
                        new TimeStamp(alt_db.getTicks(), 11 * alt_db.getTicks()));
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(alt_db, fargID, 11.0);
                alt_float_cp0_arg_list.add(arg);
                alt_float_cp0 = new ColPred(alt_db, alt_float_mve0_ID,
                                            alt_float_cp0_arg_list);

                completed = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            } catch (LogicErrorException le) {
                threwSystemErrorException = true;
                systemErrorExceptionString = le.toString();
            }

            if ( ( alt_db == null ) ||
                 ( alt_float_mve0 == null ) ||
                 ( alt_float_mve0_ID == DBIndex.INVALID_ID ) ||
                 ( alt_float_cp0_arg_list == null ) ||
                 ( alt_float_cp0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( alt_db == null )
                    {
                        outStream.print("creation of alt_db failedl.\n");
                    }

                    if ( alt_float_mve0 == null )
                    {
                        outStream.print("creation of alt_float_mve0 failed.\n");
                    }

                    if ( alt_float_mve0_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_float_mve0_ID not initialized.\n");
                    }

                    if ( alt_float_cp0_arg_list == null )
                    {
                        outStream.print(
                                "creation of alt_float_cp0_arg_list failed.\n");
                    }

                    if ( alt_float_cp0 == null )
                    {
                        outStream.print("creation of alt_float_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "alt float cp setup failed to complete (1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("alt float cp setup threw a " +
                                "SystemErrorException: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                cpdv.setItsValue(alt_float_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "cpdv.setItsValue(alt_float_cp0) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("cpdv.setItsValue(alt_float_cp0) " +
                                         "failed  to thow a system error.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);

    } /* PredDataValue::TestAccessors() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        String testBanner =
            "Testing copy constructor for class ColPredDataValue              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred int_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp0 = null;
        ColPred pred_cp0 = null;
        ColPred text_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue cpdv_copy = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue float_cpdv0_copy = null;
        ColPredDataValue int_cpdv0 = null;
        ColPredDataValue int_cpdv0_copy = null;
        ColPredDataValue matrix_cpdv0 = null;
        ColPredDataValue matrix_cpdv0_copy = null;
        ColPredDataValue matrix_cpdv1 = null;
        ColPredDataValue matrix_cpdv1_copy = null;
        ColPredDataValue matrix_cpdv2 = null;
        ColPredDataValue matrix_cpdv2_copy = null;
        ColPredDataValue nominal_cpdv0 = null;
        ColPredDataValue nominal_cpdv0_copy = null;
        ColPredDataValue pred_cpdv0 = null;
        ColPredDataValue pred_cpdv0_copy = null;
        ColPredDataValue text_cpdv0 = null;
        ColPredDataValue text_cpdv0_copy = null;


        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.
        //
        // For now, at least, the selection of mve's and cp's used in this
        // test is overkill.  But since I didn't figure this out until I had
        // already prepared them, I may as well leave them and use them all.
        // The day may come when they actually do something useful.

        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL)||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE )||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mv1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Create test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp0 = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getFormalArg(0).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp0 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp0 = new ColPred(db, text_mve_ID, text_cp_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp0 == null )
                    {
                        outStream.printf("allocation of int_cp0 failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp0 failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp0 == null )
                    {
                        outStream.printf("allocation of pred_cp0 failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp0 == null )
                    {
                        outStream.printf("allocation of text_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix create threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }


        // now create a set of column predicate data values for copying:
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve0.getFormalArg(6);
                assertTrue( untyped_farg.getFargType() == FormalArgument.FArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assertTrue( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve0.getFormalArg(7);
                assertTrue( col_pred_farg.getFargType() ==
                        FormalArgument.FArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assertTrue( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                int_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, int_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp0);
                matrix_cpdv1 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp1);
                matrix_cpdv2 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp2);
                nominal_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, nominal_cp0);
                pred_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, pred_cp0);
                text_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, text_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( int_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( matrix_cpdv1 == null ) ||
                 ( matrix_cpdv2 == null ) ||
                 ( nominal_cpdv0 == null ) ||
                 ( pred_cpdv0 == null ) ||
                 ( text_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( int_cpdv0 == null )
                    {
                        outStream.print("allocation of int_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv1 == null )
                    {
                        outStream.print("allocation of matrix_cpdv1 failed.\n");
                    }

                    if ( matrix_cpdv2 == null )
                    {
                        outStream.print("allocation of matrix_cpdv2 failed.\n");
                    }

                    if ( nominal_cpdv0 == null )
                    {
                        outStream.print("allocation of nominal_cpdv0 failed.");
                    }

                    if ( pred_cpdv0 == null )
                    {
                        outStream.print("allocation of pred_cpdv0 failed.\n");
                    }

                    if ( text_cpdv0 == null )
                    {
                        outStream.print("allocation of text_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation " +
                                         "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred data value allocation " +
                                "threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }


        // use the copy constructor to create copies of the col pred data values

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv_copy = new ColPredDataValue(cpdv);
                float_cpdv0_copy = new ColPredDataValue(float_cpdv0);
                int_cpdv0_copy = new ColPredDataValue(int_cpdv0);
                matrix_cpdv0_copy = new ColPredDataValue(matrix_cpdv0);
                matrix_cpdv1_copy = new ColPredDataValue(matrix_cpdv1);
                matrix_cpdv2_copy = new ColPredDataValue(matrix_cpdv2);
                nominal_cpdv0_copy = new ColPredDataValue(nominal_cpdv0);
                pred_cpdv0_copy = new ColPredDataValue(pred_cpdv0);
                text_cpdv0_copy = new ColPredDataValue(text_cpdv0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv_copy == null ) ||
                 ( float_cpdv0_copy == null ) ||
                 ( int_cpdv0_copy == null ) ||
                 ( matrix_cpdv0_copy == null ) ||
                 ( matrix_cpdv1_copy == null ) ||
                 ( matrix_cpdv2_copy == null ) ||
                 ( nominal_cpdv0_copy == null ) ||
                 ( pred_cpdv0_copy == null ) ||
                 ( text_cpdv0_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv_copy failed.\n");
                    }

                    if ( float_cpdv0_copy == null )
                    {
                        outStream.print("alloc of float_cpdv0_copy failed.");
                    }

                    if ( int_cpdv0_copy == null )
                    {
                        outStream.print("alloc of int_cpdv0_copy failed.\n");
                    }

                    if ( matrix_cpdv0_copy == null )
                    {
                        outStream.print("alloc of matrix_cpdv0_copy failed.\n");
                    }

                    if ( matrix_cpdv1_copy == null )
                    {
                        outStream.print("alloc of matrix_cpdv1_copy failed.\n");
                    }

                    if ( matrix_cpdv2_copy == null )
                    {
                        outStream.print("alloc of matrix_cpdv2_copy failed.\n");
                    }

                    if ( nominal_cpdv0_copy == null )
                    {
                        outStream.print("alloc of nominal_cpdv0_copy failed.");
                    }

                    if ( pred_cpdv0_copy == null )
                    {
                        outStream.print("alloc of pred_cpdv0_copy failed.\n");
                    }

                    if ( text_cpdv0_copy == null )
                    {
                        outStream.print("alloc of text_cpdv0_copy failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value copy constructor "
                                        + "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred data value copy constructor "
                                + "test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.VerifyDVCopy(cpdv, cpdv_copy,
                    outStream, verbose, "cpdv", "cpdv_copy");

            failures += DataValueTest.VerifyDVCopy(float_cpdv0, float_cpdv0_copy,
                    outStream, verbose, "float_cpdv0", "float_cpdv0_copy");

            failures += DataValueTest.VerifyDVCopy(int_cpdv0, int_cpdv0_copy,
                    outStream, verbose, "int_cpdv0", "int_cpdv0_copy");

            failures += DataValueTest.VerifyDVCopy(matrix_cpdv0, matrix_cpdv0_copy,
                    outStream, verbose, "matrix_cpdv0", "matrix_cpdv0_copy");

            failures += DataValueTest.VerifyDVCopy(matrix_cpdv1, matrix_cpdv1_copy,
                    outStream, verbose, "matrix_cpdv1", "matrix_cpdv1_copy");

            failures += DataValueTest.VerifyDVCopy(matrix_cpdv2, matrix_cpdv2_copy,
                    outStream, verbose, "matrix_cpdv2", "matrix_cpdv2_copy");

            failures += DataValueTest.VerifyDVCopy(nominal_cpdv0, nominal_cpdv0_copy,
                    outStream, verbose, "nominal_cpdv0", "nominal_cpdv0_copy");

            failures += DataValueTest.VerifyDVCopy(pred_cpdv0, pred_cpdv0_copy,
                    outStream, verbose, "pred_cpdv0", "pred_cpdv0_copy");

            failures += DataValueTest.VerifyDVCopy(text_cpdv0, text_cpdv0_copy,
                    outStream, verbose, "text_cpdv0", "text_cpdv0_copy");
        }

        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue((ColPredDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new ColPredDataValue(null) completed.\n");
                    }

                    if ( cpdv != null )
                    {
                        outStream.print(
                            "new ColPredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPredDataValue(null) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
        }


        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);
    } /* PredDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                              JRM -- 10/11/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods() throws SystemErrorException {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String testString0 = "()";
        String testDBString0 =
                "(ColPredDataValue (id 1000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 1001) (itsValue ()) (subRange false))";
        String testString1 = "float_mve(11, 00:00:00:011, 00:00:11:000, 11.000000)";
        String testDBString1 =
                "(ColPredDataValue (id 2000) (itsFargID 39) (itsFargType COL_PREDICATE) (itsCellID 2001) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))";
//                "(ColPredDataValue (id 2000) (itsFargID 39) (itsFargType COL_PREDICATE) (itsCellID 2001) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))";
        String testString2 =
                "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.000000, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.000000))";
//                "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
        String testDBString2 =
                "(ColPredDataValue (id 3000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 3001) (itsValue (colPred (id 0) (mveID 12) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 31) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))) (subRange false))";
//                "(ColPredDataValue (id 3000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 3001) (itsValue (colPred (id 0) (mveID 12) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 31) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))) (subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        ColPred float_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.

        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();
            matrix_mve1 = db.getMatrixVE(matrix_mve1_ID);

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();
            matrix_mve2 = db.getMatrixVE(matrix_mve2_ID);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve1.getNumFormalArgs() != 1 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX )||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned "
                                    + "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
                }


                if ( ! completed )
                {
                    outStream.print("Create test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix create threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve1.getFormalArg(0);
                assertTrue( untyped_farg.getFargType() == FormalArgument.FArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assertTrue( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve2.getFormalArg(0);
                assertTrue( col_pred_farg.getFargType() ==
                        FormalArgument.FArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assertTrue( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                cpdv.setID(1000);             // fake value for testing
                cpdv.itsCellID = 1001;        // fake value for testing
                cpdv.itsPredID = 1002;        // fake value for testing

                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                float_cpdv0.setID(2000);      // fake value for testing
                float_cpdv0.itsCellID = 2001; // fake value for testing
                float_cpdv0.itsPredID = 2002; // fake value for testing

                matrix_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp0);
                matrix_cpdv0.setID(3000);      // fake value for testing
                matrix_cpdv0.itsCellID = 3001; // fake value for testing
                matrix_cpdv0.itsPredID = 3002; // fake value for testing

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test "
                                        + "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cpdv.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cpdv.toString(): \"%s\".\n",
                                     cpdv.toString());
                }
            }

            if ( cpdv.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cpdv.toDBString(): \"%s\".\n",
                                     cpdv.toDBString());
                }
            }

            if ( float_cpdv0.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected float_cpdv0.toString(): \"%s\".\n",
                            float_cpdv0.toString());
                }
            }

            if ( float_cpdv0.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected float_cpdv0.toDBString(): \"%s\".\n",
                            float_cpdv0.toDBString());
                }
            }

            if ( matrix_cpdv0.toString().compareTo(testString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected matrix_cpdv0.toString(): \"%s\".\n",
                            matrix_cpdv0.toString());
                }
            }

            if ( matrix_cpdv0.toDBString().compareTo(testDBString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected matrix_cpdv0.toDBString(): \"%s\".\n",
                            matrix_cpdv0.toDBString());
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        assertTrue(pass);

    } /* ColPredDataValue::TestToStringMethods() */


    /**
     * VerifyColPredDVCopy()
     *
     * Verify that the supplied instances of ColPredDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyColPredDVCopy(ColPredDataValue base,
                                          ColPredDataValue copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyColPredDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyColPredDVCopy: %s null on entry.\n",
                             copyDesc);
        }
        else if ( base == copy )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.getDB() != copy.getDB() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a Predicate.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == null ) &&
                  ( copy.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( copy.itsValue == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( base.toString().compareTo(copy.toString()) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( base.itsValue != null )
        {
            failures += ColPredTest.VerifyColPredCopy(base.itsValue,
                                                  copy.itsValue,
                                                  outStream, verbose,
                                                  baseDesc + ".itsValue",
                                                  copyDesc + ".itsValue");
        }

        return failures;

    } /* ColPredDataValue::VerifyColPredDVCopy() */

}