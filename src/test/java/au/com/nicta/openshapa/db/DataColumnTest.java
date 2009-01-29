package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public final class DataColumnTest {

    private Database db = null;
    private long f_mve0ID = DBIndex.INVALID_ID;
    private long f_mve1ID = DBIndex.INVALID_ID;
    private long f_mve2ID = DBIndex.INVALID_ID;
    private long i_mve0ID = DBIndex.INVALID_ID;
    private long i_mve1ID = DBIndex.INVALID_ID;
    private long m_mve0ID = DBIndex.INVALID_ID;
    private long m_mve1ID = DBIndex.INVALID_ID;
    private long n_mve0ID = DBIndex.INVALID_ID;
    private long n_mve1ID = DBIndex.INVALID_ID;
    private long p_mve0ID = DBIndex.INVALID_ID;
    private long p_mve1ID = DBIndex.INVALID_ID;
    private long t_mve0ID = DBIndex.INVALID_ID;
    private long t_mve1ID = DBIndex.INVALID_ID;
    private FormalArgument farg = null;
    private MatrixVocabElement f_mve0 = null;
    private MatrixVocabElement f_mve1 = null;
    private MatrixVocabElement f_mve2 = null;
    private MatrixVocabElement i_mve0 = null;
    private MatrixVocabElement i_mve1 = null;
    private MatrixVocabElement m_mve0 = null;
    private MatrixVocabElement m_mve1 = null;
    private MatrixVocabElement n_mve0 = null;
    private MatrixVocabElement n_mve1 = null;
    private MatrixVocabElement p_mve0 = null;
    private MatrixVocabElement p_mve1 = null;
    private MatrixVocabElement t_mve0 = null;
    private MatrixVocabElement t_mve1 = null;
    private DataColumn f_col0 = null;
    private DataColumn f_col1 = null;
    private DataColumn i_col0 = null;
    private DataColumn i_col1 = null;
    private DataColumn m_col0 = null;
    private DataColumn m_col1 = null;
    private DataColumn n_col0 = null;
    private DataColumn n_col1 = null;
    private DataColumn p_col0 = null;
    private DataColumn p_col1 = null;
    private DataColumn t_col0 = null;
    private DataColumn t_col1 = null;
    private DataColumn dc = null;

    /**
     * Create the test case.
     *
     * @param testName name of the test case
     */
    public DataColumnTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     */
    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        f_mve0 = new MatrixVocabElement(db, "f_col0");
        f_mve0.setType(MatrixVocabElement.MatrixType.FLOAT);
        farg = new FloatFormalArg(db);
        f_mve0.appendFormalArg(farg);
        db.vl.addElement(f_mve0);
        f_mve0ID = f_mve0.getID();
        f_col0 = new DataColumn(db, "f_col0", false, true, f_mve0ID);

        f_mve1 = new MatrixVocabElement(db, "f_col1");
        f_mve1.setType(MatrixVocabElement.MatrixType.FLOAT);
        farg = new FloatFormalArg(db);
        f_mve1.appendFormalArg(farg);
        db.vl.addElement(f_mve1);
        f_mve1ID = f_mve1.getID();
        f_col1 = new DataColumn(db, "f_col1", true, false, f_mve1ID);

        /* we use f_mve2 & f_mve2ID for failures tests */
        f_mve2 = new MatrixVocabElement(db, "f_col2");
        f_mve2.setType(MatrixVocabElement.MatrixType.FLOAT);
        farg = new FloatFormalArg(db);
        f_mve2.appendFormalArg(farg);
        db.vl.addElement(f_mve2);
        f_mve2ID = f_mve2.getID();


        i_mve0 = new MatrixVocabElement(db, "i_col0");
        i_mve0.setType(MatrixVocabElement.MatrixType.INTEGER);
        farg = new IntFormalArg(db);
        i_mve0.appendFormalArg(farg);
        db.vl.addElement(i_mve0);
        i_mve0ID = i_mve0.getID();
        i_col0 = new DataColumn(db, "i_col0", false, true, i_mve0ID);

        i_mve1 = new MatrixVocabElement(db, "i_col1");
        i_mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
        farg = new IntFormalArg(db);
        i_mve1.appendFormalArg(farg);
        db.vl.addElement(i_mve1);
        i_mve1ID = i_mve1.getID();
        i_col1 = new DataColumn(db, "i_col1", true, false, i_mve1ID);


        m_mve0 = new MatrixVocabElement(db, "m_col0");
        m_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
        farg = new UnTypedFormalArg(db, "<arg>");
        m_mve0.appendFormalArg(farg);
        db.vl.addElement(m_mve0);
        m_mve0ID = m_mve0.getID();
        m_col0 = new DataColumn(db, "m_col0", false, true, m_mve0ID);

        m_mve1 = new MatrixVocabElement(db, "m_col1");
        m_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
        farg = new UnTypedFormalArg(db, "<arg>");
        m_mve1.appendFormalArg(farg);
        m_mve1.setVarLen(true);
        db.vl.addElement(m_mve1);
        m_mve1ID = m_mve1.getID();
        m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);

        n_mve0 = new MatrixVocabElement(db, "n_col0");
        n_mve0.setType(MatrixVocabElement.MatrixType.NOMINAL);
        farg = new NominalFormalArg(db);
        n_mve0.appendFormalArg(farg);
        db.vl.addElement(n_mve0);
        n_mve0ID = n_mve0.getID();
        n_col0 = new DataColumn(db, "n_col0", false, true, n_mve0ID);

        n_mve1 = new MatrixVocabElement(db, "n_col1");
        n_mve1.setType(MatrixVocabElement.MatrixType.NOMINAL);
        farg = new NominalFormalArg(db);
        n_mve1.appendFormalArg(farg);
        db.vl.addElement(n_mve1);
        n_mve1ID = n_mve1.getID();
        n_col1 = new DataColumn(db, "n_col1", true, false, n_mve1ID);

        p_mve0 = new MatrixVocabElement(db, "p_col0");
        p_mve0.setType(MatrixVocabElement.MatrixType.PREDICATE);
        farg = new PredFormalArg(db);
        p_mve0.appendFormalArg(farg);
        db.vl.addElement(p_mve0);
        p_mve0ID = p_mve0.getID();
        p_col0 = new DataColumn(db, "p_col0", false, true, p_mve0ID);

        p_mve1 = new MatrixVocabElement(db, "p_col1");
        p_mve1.setType(MatrixVocabElement.MatrixType.PREDICATE);
        farg = new PredFormalArg(db);
        p_mve1.appendFormalArg(farg);
        db.vl.addElement(p_mve1);
        p_mve1ID = p_mve1.getID();
        p_col1 = new DataColumn(db, "p_col1", true, false, p_mve1ID);

        t_mve0 = new MatrixVocabElement(db, "t_col0");
        t_mve0.setType(MatrixVocabElement.MatrixType.TEXT);
        farg = new TextStringFormalArg(db);
        t_mve0.appendFormalArg(farg);
        db.vl.addElement(t_mve0);
        t_mve0ID = t_mve0.getID();
        t_col0 = new DataColumn(db, "t_col0", false, true, t_mve0ID);

        t_mve1 = new MatrixVocabElement(db, "t_col1");
        t_mve1.setType(MatrixVocabElement.MatrixType.TEXT);
        farg = new TextStringFormalArg(db);
        t_mve1.appendFormalArg(farg);
        db.vl.addElement(t_mve1);
        t_mve1ID = t_mve1.getID();
        t_col1 = new DataColumn(db, "t_col1", true, false, t_mve1ID);
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

    /**
     * Tests the three argument constructor for the DataColoumn class.
     *
     * @throws SystemErrorException If unable to create DataColumns.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        Database db0 = new ODBCDatabase();

        DataColumn f_col = new DataColumn(db0, "f_col",
                                          MatrixVocabElement.MatrixType.FLOAT);
        DataColumn i_col = new DataColumn(db0, "i_col",
                                          MatrixVocabElement.MatrixType
                                                            .INTEGER);
        DataColumn m_col = new DataColumn(db0, "m_col",
                                          MatrixVocabElement.MatrixType.MATRIX);
        DataColumn n_col = new DataColumn(db0, "n_col",
                                          MatrixVocabElement.MatrixType
                                                            .NOMINAL);
        DataColumn p_col = new DataColumn(db0, "p_col",
                                          MatrixVocabElement.MatrixType
                                                            .PREDICATE);
        DataColumn t_col = new DataColumn(db0, "t_col",
                                          MatrixVocabElement.MatrixType.TEXT);

        assertTrue(db0 != null);
        assertTrue(f_col != null);
        assertTrue(f_col.getDB() == db0);
        assertTrue(f_col.hidden == false);
        assertTrue(f_col.readOnly == false);
        assertTrue(f_col.numCells == 0);
        assertTrue(f_col.name.equals("f_col"));
        assertTrue(f_col.getItsCells() == null);
        assertTrue(f_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .FLOAT);

        assertTrue(i_col != null);
        assertTrue(i_col.getDB() == db0);
        assertTrue(i_col.hidden == false);
        assertTrue(i_col.readOnly == false);
        assertTrue(i_col.numCells == 0);
        assertTrue(i_col.name.equals("i_col"));
        assertTrue(i_col.getItsCells() == null);
        assertTrue(i_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .INTEGER);

        assertTrue(m_col != null);
        assertTrue(m_col.getDB() == db0);
        assertTrue(m_col.hidden == false);
        assertTrue(m_col.readOnly == false);
        assertTrue(m_col.numCells == 0);
        assertTrue(m_col.name.equals("m_col"));
        assertTrue(m_col.getItsCells() == null);
        assertTrue(m_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .MATRIX);

        assertTrue(n_col != null);
        assertTrue(n_col.getDB() == db0);
        assertTrue(n_col.hidden == false);
        assertTrue(n_col.readOnly == false);
        assertTrue(n_col.numCells == 0);
        assertTrue(n_col.name.equals("n_col"));
        assertTrue(n_col.getItsCells() == null);
        assertTrue(n_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .NOMINAL);

        assertTrue(p_col != null);
        assertTrue(p_col.getDB() == db0);
        assertTrue(p_col.hidden == false);
        assertTrue(p_col.readOnly == false);
        assertTrue(p_col.numCells == 0);
        assertTrue(p_col.name.equals("p_col"));
        assertTrue(p_col.getItsCells() == null);
        assertTrue(p_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .PREDICATE);

        assertTrue(t_col != null);
        assertTrue(t_col.getDB() == db0);
        assertTrue(t_col.hidden == false);
        assertTrue(t_col.readOnly == false);
        assertTrue(t_col.numCells == 0);
        assertTrue(t_col.name.equals("t_col"));
        assertTrue(t_col.getItsCells() == null);
        assertTrue(t_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .TEXT);
    }

    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        dc = new DataColumn(null, "f_col",
                            MatrixVocabElement.MatrixType.FLOAT);
    }

    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        dc = new DataColumn(db, "", MatrixVocabElement.MatrixType.FLOAT);
    }

    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        dc = new DataColumn(db, " invalid ",
                            MatrixVocabElement.MatrixType.FLOAT);
    }

    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        DataColumn fc = new DataColumn(db, "f_col",
                                       MatrixVocabElement.MatrixType.FLOAT);
        long fc_ID = db.addColumn(fc);
        fc = db.getDataColumn(fc_ID);
        long f_mveID = fc.getItsMveID();
        MatrixVocabElement f_mve = db.getMatrixVE(f_mveID);

        dc = new DataColumn(db, "f_col", MatrixVocabElement.MatrixType.FLOAT);
    }

    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure4() throws SystemErrorException {
        dc = new DataColumn(db, "valid",
                            MatrixVocabElement.MatrixType.UNDEFINED);
    }

    @Test
    public void test5ArgConstructor() throws SystemErrorException {
        assertTrue(db != null);

        assertTrue(f_mve0 != null);
        assertTrue(f_mve0ID != DBIndex.INVALID_ID);
        assertTrue(f_col0 != null);
        assertTrue(f_col0.hidden == false);
        assertTrue(f_col0.readOnly == true);
        assertTrue(f_col0.numCells == 0);
        assertTrue(f_col0.name.equals("f_col0"));
        assertTrue(f_col0.getDB() == db);
        assertTrue(f_col0.getItsCells() == null);
        assertTrue(f_col0.getItsMveID() == f_mve0ID);
        assertTrue(f_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .FLOAT);
        assertTrue(f_col0.getVarLen() == f_mve0.getVarLen());

        assertTrue(f_mve1 != null);
        assertTrue(f_mve1ID != DBIndex.INVALID_ID);
        assertTrue(f_col1 != null);
        assertTrue(f_col1.hidden == true);
        assertTrue(f_col1.readOnly == false);
        assertTrue(f_col1.numCells == 0);
        assertTrue(f_col1.name.equals("f_col1"));
        assertTrue(f_col1.getDB() == db);
        assertTrue(f_col1.getItsCells() == null);
        assertTrue(f_col1.getItsMveID() == f_mve1ID);
        assertTrue(f_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .FLOAT);
        assertTrue(f_col1.getVarLen() == f_mve1.getVarLen());

        assertTrue(f_mve2 != null);
        assertTrue(f_mve2ID != DBIndex.INVALID_ID);

        assertTrue(i_mve0 != null);
        assertTrue(i_mve0ID != DBIndex.INVALID_ID);
        assertTrue(i_col0 != null );
        assertTrue(i_col0.hidden == false);
        assertTrue(i_col0.readOnly == true);
        assertTrue(i_col0.numCells == 0);
        assertTrue(i_col0.name.equals("i_col0"));
        assertTrue(i_col0.getDB() == db);
        assertTrue(i_col0.getItsCells() == null);
        assertTrue(i_col0.getItsMveID() == i_mve0ID);
        assertTrue(i_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .INTEGER);
        assertTrue(i_col0.getVarLen() == i_mve0.getVarLen());

        assertTrue(i_mve1 != null);
        assertTrue(i_mve1ID != DBIndex.INVALID_ID);
        assertTrue(i_col1 != null);
        assertTrue(i_col1.hidden == true);
        assertTrue(i_col1.readOnly == false);
        assertTrue(i_col1.numCells == 0);
        assertTrue(i_col1.name.equals("i_col1"));
        assertTrue(i_col1.getDB() == db);
        assertTrue(i_col1.getItsCells() == null);
        assertTrue(i_col1.getItsMveID() == i_mve1ID);
        assertTrue(i_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .INTEGER);
        assertTrue(i_col1.getVarLen() == i_mve1.getVarLen());

        assertTrue(m_mve0 != null);
        assertTrue(m_mve0ID != DBIndex.INVALID_ID);
        assertTrue(m_col0 != null);
        assertTrue(m_col0.hidden == false);
        assertTrue(m_col0.readOnly == true);
        assertTrue(m_col0.numCells == 0);
        assertTrue(m_col0.name.equals("m_col0"));
        assertTrue(m_col0.getDB() == db);
        assertTrue(m_col0.getItsCells() == null);
        assertTrue(m_col0.getItsMveID() == m_mve0ID);
        assertTrue(m_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .MATRIX);
        assertTrue(m_col0.getVarLen() == m_mve0.getVarLen());

        assertTrue(m_mve1 != null);
        assertTrue(m_mve1ID != DBIndex.INVALID_ID);
        assertTrue(m_col1 != null);
        assertTrue(m_col1.hidden == true);
        assertTrue(m_col1.readOnly == false);
        assertTrue(m_col1.numCells == 0);
        assertTrue(m_col1.name.equals("m_col1"));
        assertTrue(m_col1.getDB() == db);
        assertTrue(m_col1.getItsCells() == null);
        assertTrue(m_col1.getItsMveID() == m_mve1ID);
        assertTrue(m_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .MATRIX);
        assertTrue(m_col1.getVarLen() == m_mve1.getVarLen());

        assertTrue(n_mve0 != null);
        assertTrue(n_mve0ID != DBIndex.INVALID_ID);
        assertTrue(n_col0 != null);
        assertTrue(n_col0.hidden == false);
        assertTrue(n_col0.readOnly == true);
        assertTrue(n_col0.numCells == 0);
        assertTrue(n_col0.name.equals("n_col0"));
        assertTrue(n_col0.getDB() == db);
        assertTrue(n_col0.getItsCells() == null);
        assertTrue(n_col0.getItsMveID() == n_mve0ID);
        assertTrue(n_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .NOMINAL);
        assertTrue(n_col0.getVarLen() == n_mve0.getVarLen());

        assertTrue(n_mve1 != null);
        assertTrue(n_mve1ID != DBIndex.INVALID_ID);
        assertTrue(n_col1 != null);
        assertTrue(n_col1.hidden == true);
        assertTrue(n_col1.readOnly == false);
        assertTrue(n_col1.numCells == 0);
        assertTrue(n_col1.name.equals("n_col1"));
        assertTrue(n_col1.getDB() == db);
        assertTrue(n_col1.getItsCells() == null);
        assertTrue(n_col1.getItsMveID() == n_mve1ID);
        assertTrue(n_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .NOMINAL);
        assertTrue(n_col1.getVarLen() == n_mve1.getVarLen());

        assertTrue(p_mve0 != null);
        assertTrue(p_mve0ID != DBIndex.INVALID_ID);
        assertTrue(p_col0 != null);
        assertTrue(p_col0.hidden == false);
        assertTrue(p_col0.readOnly == true);
        assertTrue(p_col0.numCells == 0);
        assertTrue(p_col0.name.equals("p_col0"));
        assertTrue(p_col0.getDB() == db);
        assertTrue(p_col0.getItsCells() == null);
        assertTrue(p_col0.getItsMveID() == p_mve0ID);
        assertTrue(p_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .PREDICATE);
        assertTrue(p_col0.getVarLen() == p_mve0.getVarLen());

        assertTrue(p_mve1 != null);
        assertTrue(p_mve1ID != DBIndex.INVALID_ID);
        assertTrue(p_col1 != null);
        assertTrue(p_col1.hidden == true);
        assertTrue(p_col1.readOnly == false);
        assertTrue(p_col1.numCells == 0);
        assertTrue(p_col1.name.equals("p_col1"));
        assertTrue(p_col1.getDB() == db);
        assertTrue(p_col1.getItsCells() == null);
        assertTrue(p_col1.getItsMveID() == p_mve1ID);
        assertTrue(p_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .PREDICATE);
        assertTrue(p_col1.getVarLen() == p_mve1.getVarLen());

        assertTrue(t_mve0 != null);
        assertTrue(t_mve0ID != DBIndex.INVALID_ID);
        assertTrue(t_col0 != null);
        assertTrue(t_col0.hidden == false);
        assertTrue(t_col0.readOnly == true);
        assertTrue(t_col0.numCells == 0);
        assertTrue(t_col0.name.equals("t_col0"));
        assertTrue(t_col0.getDB() == db);
        assertTrue(t_col0.getItsCells() == null);
        assertTrue(t_col0.getItsMveID() == t_mve0ID);
        assertTrue(t_col0.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .TEXT);
        assertTrue(t_col0.getVarLen() == t_mve0.getVarLen());

        assertTrue(t_mve1 != null);
        assertTrue(t_mve1ID != DBIndex.INVALID_ID);
        assertTrue(t_col1 != null);
        assertTrue(t_col1.hidden == true);
        assertTrue(t_col1.readOnly == false);
        assertTrue(t_col1.numCells == 0);
        assertTrue(t_col1.name.equals("t_col1"));
        assertTrue(t_col1.getDB() == db);
        assertTrue(t_col1.getItsCells() == null);
        assertTrue(t_col1.getItsMveID() == t_mve1ID);
        assertTrue(t_col1.getItsMveType() == MatrixVocabElement.MatrixType
                                                               .TEXT);
        assertTrue(t_col1.getVarLen() == t_mve1.getVarLen());
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure0() throws SystemErrorException {
        dc = new DataColumn(null, "f_col2", false, true, f_mve2ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure1() throws SystemErrorException {
        dc = new DataColumn(db, null, false, true, f_mve2ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure2() throws SystemErrorException {
        dc = new DataColumn(db, "", false, true, f_mve2ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure3() throws SystemErrorException {
        dc = new DataColumn(db, " invalid ", false, true, f_mve2ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure4() throws SystemErrorException {
        dc = new DataColumn(db, "f_col3", false, true, f_mve2ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure5() throws SystemErrorException {
        dc = new DataColumn(db, "f_col2", false, true, DBIndex.INVALID_ID);
    }

    @Test (expected = SystemErrorException.class)
    public void test5ArgConstructorFailure6() throws SystemErrorException {
        dc = new DataColumn(db, "f_col2", false, true, f_mve2ID + 1);
    }

    public void testAccessors() throws SystemErrorException {
        // Build the first data cell.
        DataColumn f_col = new DataColumn(db, "f_col",
                                          MatrixVocabElement.MatrixType.FLOAT);
        long f_col0ID = db.addColumn(f_col);
        f_col0 = (DataColumn) db.cl.getColumn(f_col0ID);
        f_mve0ID = f_col0.getItsMveID();
        f_mve0 = db.getMatrixVE(f_mve0ID);

        TimeStamp f_onset0 = new TimeStamp(db.getTicks(), 60);
        TimeStamp f_offset0 = new TimeStamp(db.getTicks(), 120);
        long fargID = f_mve0.getFormalArg(0).getID();
        Vector<DataValue> f_arg_list0 = new Vector<DataValue>();
        FloatDataValue arg = new FloatDataValue(db, fargID, 0.0);
        f_arg_list0.add(arg);

        Matrix f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
        f_mve0 = db.getMatrixVE(f_col0.getItsMveID());

        DataCell f_cell0 = new DataCell(db, "f_cell0", f_col0ID,
                                        f_mve0.getID(),
                                        f_onset0, f_offset0, f_matrix0);

        // Build the second data cell.
        TimeStamp f_onset1 = new TimeStamp(db.getTicks(), 180);
        TimeStamp f_offset1 = new TimeStamp(db.getTicks(), 240);
        Vector<DataValue> f_arg_list1 = new Vector<DataValue>();
        arg = new FloatDataValue(db, fargID, 1.0);
        f_arg_list1.add(arg);
        Matrix f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
        DataCell f_cell1 = new DataCell(db, "f_cell1", f_col0ID,
                                        f_mve0.getID(),
                                        f_onset1, f_offset1, f_matrix1);

        // Build the third data cell
        TimeStamp f_onset2 = new TimeStamp(db.getTicks(), 300);
        TimeStamp f_offset2 = new TimeStamp(db.getTicks(), 360);
        Vector<DataValue> f_arg_list2 = new Vector<DataValue>();
        arg = new FloatDataValue(db, fargID, 2.0);
        f_arg_list2.add(arg);
        Matrix f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
        DataCell f_cell2 = new DataCell(db, "f_cell2", f_col0ID,
                                        f_mve0.getID(),
                                        f_onset2, f_offset2, f_matrix2);

        f_col0.appendCell(f_cell2);
        f_col0.appendCell(f_cell1);
        f_col0.appendCell(f_cell0);

        assertTrue(f_col0.numCells == 3);

        Vector<DataCell> saved_f_col0_cells = f_col0.getItsCells();

        f_col0.setItsCells(null);
        assertTrue(f_col0.getItsCells() == null);
        assertTrue(f_col0.getNumCells() == 0);

        f_col0.setItsCells(saved_f_col0_cells);
        assertTrue(saved_f_col0_cells == f_col0.getItsCells());
        assertTrue(f_col0.getNumCells() == 3);
    }

    public void testAccessorFailure0() {
        try {
            f_col0.setItsMveID(p_mve0.getID());
            fail("Accessor should have thrown SystemErrorException.");
        } catch (SystemErrorException e) {
            // Do nothing - pass the test.
        }
    }

    public void testAccessorFailure1() {
        try {
            DataColumn newCol = new DataColumn(db, "newCol0",
                                               MatrixVocabElement.MatrixType
                                                                 .FLOAT);
            newCol.setItsMveID(DBIndex.INVALID_ID);
            fail("Accessor should have thrown SystemErrorException.");
        } catch (SystemErrorException e) {
            // Do nothing - pass the test.
        }
    }

    public void testAccessorFailure2() {
        try {
            DataColumn newCol = new DataColumn(db, "newCol1",
                                               MatrixVocabElement.MatrixType
                                                                 .FLOAT);

            i_mve1 = new MatrixVocabElement(db, "newCol1");
            i_mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
            i_mve1.appendFormalArg(new IntFormalArg(db, "<int0>"));
            db.vl.addElement(i_mve1);
            i_mve1 = db.vl.getMatrixVocabElement("newCol1");

            newCol.setItsMveID(i_mve1.getID());

            fail("Accessor should have thrown SystemErrorException.");
        } catch (SystemErrorException e) {
            // Do nothing - pass the test.
        }
    }

    public void testAccessorFailure3() {
        try {
            DataColumn newCol = new DataColumn(db, "newCol2",
                                               MatrixVocabElement.MatrixType
                                                                 .FLOAT);

            long f_col0ID = db.addColumn(newCol);
            f_col0 = (DataColumn) db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);

            TimeStamp f_onset0 = new TimeStamp(db.getTicks(), 60);
            TimeStamp f_offset0 = new TimeStamp(db.getTicks(), 120);
            long fargID = f_mve0.getFormalArg(0).getID();
            Vector<DataValue> f_arg_list0 = new Vector<DataValue>();
            FloatDataValue arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);

            Matrix f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_mve0 = db.getMatrixVE(f_col0.getItsMveID());

            DataCell f_cell0 = new DataCell(db, "f_cell0", f_col0ID,
                                            f_mve0.getID(),
                                            f_onset0, f_offset0, f_matrix0);
            newCol.setItsMveID(f_cell0.getID());

            fail("Accessor should have thrown SystemErrorException.");
        } catch (SystemErrorException e) {
            // Do nothing - pass the test.
        }
    }

    public void testToStringMethods() throws SystemErrorException {
        f_col0 = new DataColumn(db, "f_col",
                                MatrixVocabElement.MatrixType.FLOAT);
        long f_col0ID = db.addColumn(f_col0);
        f_col0 = (DataColumn) db.cl.getColumn(f_col0ID);
        f_mve0ID = f_col0.getItsMveID();
        f_mve0 = db.getMatrixVE(f_mve0ID);

        TimeStamp f_onset0 = new TimeStamp(db.getTicks(), 60);
        TimeStamp f_offset0 = new TimeStamp(db.getTicks(), 120);
        Vector<DataValue> f_arg_list0 = new Vector<DataValue>();
        long fargID = f_mve0.getFormalArg(0).getID();
        FloatDataValue arg = new FloatDataValue(db, fargID, 0.0);
        f_arg_list0.add(arg);
        Matrix f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
        DataCell f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID,
                                        f_onset0, f_offset0, f_matrix0);

        TimeStamp f_onset1 = new TimeStamp(db.getTicks(), 180);
        TimeStamp f_offset1 = new TimeStamp(db.getTicks(), 240);
        Vector<DataValue> f_arg_list1 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 1.0);
        f_arg_list1.add(arg);
        Matrix f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
        DataCell f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID,
                                        f_onset1, f_offset1, f_matrix1);

        TimeStamp f_onset2 = new TimeStamp(db.getTicks(), 300);
        TimeStamp f_offset2 = new TimeStamp(db.getTicks(), 360);
        Vector<DataValue> f_arg_list2 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 2.0);
        f_arg_list2.add(arg);
        Matrix f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
        DataCell f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID,
                                        f_onset2, f_offset2, f_matrix2);

        TimeStamp f_onset3 = new TimeStamp(db.getTicks(), 420);
        TimeStamp f_offset3 = new TimeStamp(db.getTicks(), 480);
        Vector<DataValue> f_arg_list3 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 3.0);
        f_arg_list3.add(arg);
        Matrix f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
        DataCell f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID,
                                        f_onset3, f_offset3, f_matrix3);

        TimeStamp f_onset4 = new TimeStamp(db.getTicks(), 540);
        TimeStamp f_offset4 = new TimeStamp(db.getTicks(), 600);
        Vector<DataValue> f_arg_list4 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 4.0);
        f_arg_list4.add(arg);
        Matrix f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
        DataCell f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID,
                                        f_onset4, f_offset4, f_matrix4);

        TimeStamp f_onset5 = new TimeStamp(db.getTicks(), 660);
        TimeStamp f_offset5 = new TimeStamp(db.getTicks(), 720);
        Vector<DataValue> f_arg_list5 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 5.0);
        f_arg_list5.add(arg);
        Matrix f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
        DataCell f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID,
                                        f_onset5, f_offset5, f_matrix5);

        TimeStamp f_onset6 = new TimeStamp(db.getTicks(), 780);
        TimeStamp f_offset6 = new TimeStamp(db.getTicks(), 840);
        Vector<DataValue> f_arg_list6 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 6.0);
        f_arg_list6.add(arg);
        Matrix f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
        DataCell f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID,
                                       f_onset6, f_offset6, f_matrix6);

        TimeStamp f_onset7 = new TimeStamp(db.getTicks(), 900);
        TimeStamp f_offset7 = new TimeStamp(db.getTicks(), 960);
        Vector<DataValue> f_arg_list7 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 7.0);
        f_arg_list7.add(arg);
        Matrix f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
        DataCell f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID,
                                        f_onset7, f_offset7, f_matrix7);

        TimeStamp f_onset8 = new TimeStamp(db.getTicks(), 900);
        TimeStamp f_offset8 = new TimeStamp(db.getTicks(), 960);
        Vector<DataValue> f_arg_list8 = new Vector<DataValue>();
        fargID = f_mve0.getFormalArg(0).getID();
        arg = new FloatDataValue(db, fargID, 8.0);
        f_arg_list8.add(arg);
        Matrix f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
        DataCell f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID,
                                        f_onset8, f_offset8, f_matrix8);

        f_col0.appendCell(f_cell0);
        f_col0.appendCell(f_cell1);
        f_col0.appendCell(f_cell2);
        f_col0.appendCell(f_cell3);
        f_col0.appendCell(f_cell4);
        f_col0.appendCell(f_cell5);
        f_col0.appendCell(f_cell6);
        f_col0.appendCell(f_cell7);
        f_col0.appendCell(f_cell8);

        assertTrue(f_col0.getNumCells() == 9);

        //System.out.printf(f_col0.toString());
        //System.out.printf(f_col0.toDBString());
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) Three argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs to the
     *         three argument constructor.  Verify that all fields of the
     *         DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the three argument constructor fails on invalid
     *         input.
     *
     * 2) Four argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs along with
     *         a comment to the four argument constructor.  Verify that all
     *         fields of the DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the constructor fails when passed invalid data.
     *
     * 3) Seven argument constructor:
     *
     *      As per four argument constructor, save that an onset, offset and
     *      a cell value value are supplied to the constructor.  Verify that
     *      the onset, offset, and value appear in the DataCell.
     *
     *      Verify that the constructor fails when passed invalid data.
     *
     * 4) Copy constructor:
     *
     *      a) Construct DataCells of all types using the 3, 4, and 7 argument
     *         constructors with a variety of values.
     *
     *         Now use the copy constructor to create a copies of the
     *         DataCells, and verify that the copies are correct.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that can be tested unless we go an manualy break some
     *         DataCells created with the other constructors.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsMveID(), getItsMveType() getOnset(),
     *      getOffset(), getVal(), setOnset(), setOffset(), and setVal()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the Cell.TestAccessorMethods()
     *      method.
     *
     *      Verify that the accessors fail on invalid data.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * TestClassDataColumn()
     *
     * Main routine for tests of class DataColumn.
     *
     *                                      JRM -- 12/25/07
     *
     * Changes:
     *
     *    - Non.
     */
    @Test
    public void TestClassDataColumn() throws SystemErrorException {
        PrintStream outStream = System.out;
        boolean verbose = true;

        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class DataColumn:\n");

        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestCellManagement(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class DataCell.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class DataCell.\n\n");
        }

        assertTrue(pass);

    } /* DataCell::TestClassDataColumn() */

    /**
     * TestCellManagement()
     *
     * Run a battery of tests on the cell management methods of the class.
     *
     * With the exception of the validCell() method, the cell management methods
     * don't care about the type of the column.  Thus for simplicity, most of
     * tests are performed on a float column.
     *
     *                                              JRM -- 12/31/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCellManagement(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing cell management methods for class DataColumn             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long f_col0ID = DBIndex.INVALID_ID;
        long i_col0ID = DBIndex.INVALID_ID;
        long m_col0ID = DBIndex.INVALID_ID;
        long n_col0ID = DBIndex.INVALID_ID;
        long p_col0ID = DBIndex.INVALID_ID;
        long t_col0ID = DBIndex.INVALID_ID;
        long fargID;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement t_mve0 = null;
        DataColumn f_col0 = null;
        DataColumn i_col0 = null;
        DataColumn m_col0 = null;
        DataColumn n_col0 = null;
        DataColumn p_col0 = null;
        DataColumn t_col0 = null;
        DataColumn dc = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell3 = null;
        DataCell f_cell4 = null;
        DataCell f_cell5 = null;
        DataCell f_cell6 = null;
        DataCell f_cell7 = null;
        DataCell f_cell8 = null;
        DataCell f_cell0_c = null;
        DataCell f_cell1_c = null;
        DataCell f_cell2_c = null;
        DataCell f_cell3_c = null;
        DataCell f_cell4_c = null;
        DataCell f_cell5_c = null;
        DataCell f_cell6_c = null;
        DataCell f_cell7_c = null;
        DataCell f_cell8_c = null;
        DataCell i_cell0 = null;
        DataCell m_cell0 = null;
        DataCell n_cell0 = null;
        DataCell p_cell0 = null;
        DataCell t_cell0 = null;
        TimeStamp f_onset0 = null;
        TimeStamp f_onset1 = null;
        TimeStamp f_onset2 = null;
        TimeStamp f_onset3 = null;
        TimeStamp f_onset4 = null;
        TimeStamp f_onset5 = null;
        TimeStamp f_onset6 = null;
        TimeStamp f_onset7 = null;
        TimeStamp f_onset8 = null;
        TimeStamp f_offset0 = null;
        TimeStamp f_offset1 = null;
        TimeStamp f_offset2 = null;
        TimeStamp f_offset3 = null;
        TimeStamp f_offset4 = null;
        TimeStamp f_offset5 = null;
        TimeStamp f_offset6 = null;
        TimeStamp f_offset7 = null;
        TimeStamp f_offset8 = null;
        Vector<DataValue> f_arg_list0 = null;
        Vector<DataValue> f_arg_list1 = null;
        Vector<DataValue> f_arg_list2 = null;
        Vector<DataValue> f_arg_list3 = null;
        Vector<DataValue> f_arg_list4 = null;
        Vector<DataValue> f_arg_list5 = null;
        Vector<DataValue> f_arg_list6 = null;
        Vector<DataValue> f_arg_list7 = null;
        Vector<DataValue> f_arg_list8 = null;
        Matrix f_matrix0 = null;
        Matrix f_matrix1 = null;
        Matrix f_matrix2 = null;
        Matrix f_matrix3 = null;
        Matrix f_matrix4 = null;
        Matrix f_matrix5 = null;
        Matrix f_matrix6 = null;
        Matrix f_matrix7 = null;
        Matrix f_matrix8 = null;
        FormalArgument farg = null;
        DataValue arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First, allocate a selection of columns and cells to work with. After
         * we insert the columns in the database, we use db.cl.getColumn()
         * to get a reference to the actual column in the database.
         */
        try
        {
            db = new ODBCDatabase();


            f_col0 = new DataColumn(db, "f_col0",
                                    MatrixVocabElement.MatrixType.FLOAT);
            f_col0ID = db.addColumn(f_col0);
            f_col0 = (DataColumn)db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);


            i_col0 = new DataColumn(db, "i_col0",
                                    MatrixVocabElement.MatrixType.INTEGER);
            i_col0ID = db.addColumn(i_col0);
            i_col0 = (DataColumn)db.cl.getColumn(i_col0ID);
            i_mve0ID = i_col0.getItsMveID();
            i_mve0 = db.getMatrixVE(i_mve0ID);


            m_col0 = new DataColumn(db, "m_col0",
                                    MatrixVocabElement.MatrixType.MATRIX);
            m_col0ID = db.addColumn(m_col0);
            m_col0 = (DataColumn)db.cl.getColumn(m_col0ID);
            m_mve0ID = m_col0.getItsMveID();
            m_mve0 = db.getMatrixVE(m_mve0ID);


            n_col0 = new DataColumn(db, "n_col0",
                                    MatrixVocabElement.MatrixType.NOMINAL);
            n_col0ID = db.addColumn(n_col0);
            n_col0 = (DataColumn)db.cl.getColumn(n_col0ID);
            n_mve0ID = n_col0.getItsMveID();
            n_mve0 = db.getMatrixVE(n_mve0ID);


            p_col0 = new DataColumn(db, "p_col0",
                                    MatrixVocabElement.MatrixType.PREDICATE);
            p_col0ID = db.addColumn(p_col0);
            p_col0 = (DataColumn)db.cl.getColumn(p_col0ID);
            p_mve0ID = p_col0.getItsMveID();
            p_mve0 = db.getMatrixVE(p_mve0ID);


            t_col0 = new DataColumn(db, "t_col0",
                                    MatrixVocabElement.MatrixType.TEXT);
            t_col0ID = db.addColumn(t_col0);
            t_col0 = (DataColumn)db.cl.getColumn(t_col0ID);
            t_mve0ID = t_col0.getItsMveID();
            t_mve0 = db.getMatrixVE(t_mve0ID);


            f_onset0 = new TimeStamp(db.getTicks(), 60);
            f_offset0 = new TimeStamp(db.getTicks(), 120);
            f_arg_list0 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);
            f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID,
                                       f_onset0, f_offset0, f_matrix0);

            f_onset1 = new TimeStamp(db.getTicks(), 180);
            f_offset1 = new TimeStamp(db.getTicks(), 240);
            f_arg_list1 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            f_arg_list1.add(arg);
            f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
            f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID,
                                       f_onset1, f_offset1, f_matrix1);

            f_onset2 = new TimeStamp(db.getTicks(), 300);
            f_offset2 = new TimeStamp(db.getTicks(), 360);
            f_arg_list2 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 2.0);
            f_arg_list2.add(arg);
            f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
            f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID,
                                       f_onset2, f_offset2, f_matrix2);

            f_onset3 = new TimeStamp(db.getTicks(), 420);
            f_offset3 = new TimeStamp(db.getTicks(), 480);
            f_arg_list3 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 3.0);
            f_arg_list3.add(arg);
            f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
            f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID,
                                       f_onset3, f_offset3, f_matrix3);

            f_onset4 = new TimeStamp(db.getTicks(), 540);
            f_offset4 = new TimeStamp(db.getTicks(), 600);
            f_arg_list4 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 4.0);
            f_arg_list4.add(arg);
            f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
            f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID,
                                       f_onset4, f_offset4, f_matrix4);

            f_onset5 = new TimeStamp(db.getTicks(), 660);
            f_offset5 = new TimeStamp(db.getTicks(), 720);
            f_arg_list5 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 5.0);
            f_arg_list5.add(arg);
            f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
            f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID,
                                       f_onset5, f_offset5, f_matrix5);

            f_onset6 = new TimeStamp(db.getTicks(), 780);
            f_offset6 = new TimeStamp(db.getTicks(), 840);
            f_arg_list6 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 6.0);
            f_arg_list6.add(arg);
            f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
            f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID,
                                       f_onset6, f_offset6, f_matrix6);

            f_onset7 = new TimeStamp(db.getTicks(), 900);
            f_offset7 = new TimeStamp(db.getTicks(), 960);
            f_arg_list7 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 7.0);
            f_arg_list7.add(arg);
            f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
            f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID,
                                       f_onset7, f_offset7, f_matrix7);

            f_onset8 = new TimeStamp(db.getTicks(), 900);
            f_offset8 = new TimeStamp(db.getTicks(), 960);
            f_arg_list8 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 8.0);
            f_arg_list8.add(arg);
            f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
            f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID,
                                       f_onset8, f_offset8, f_matrix8);

            i_cell0 = new DataCell(db, "i_cell0", i_col0ID, i_mve0ID);
            m_cell0 = new DataCell(db, "m_cell0", m_col0ID, m_mve0ID);
            n_cell0 = new DataCell(db, "n_cell0", n_col0ID, n_mve0ID);
            p_cell0 = new DataCell(db, "p_cell0", p_col0ID, p_mve0ID);
            t_cell0 = new DataCell(db, "t_cell0", t_col0ID, t_mve0ID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( f_col0ID == DBIndex.INVALID_ID ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve0 == null ) ||
             ( i_col0ID == DBIndex.INVALID_ID ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve0 == null ) ||
             ( m_col0ID == DBIndex.INVALID_ID ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve0 == null ) ||
             ( n_col0ID == DBIndex.INVALID_ID ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve0 == null ) ||
             ( p_col0ID == DBIndex.INVALID_ID ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve0 == null ) ||
             ( t_col0ID == DBIndex.INVALID_ID ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve0 == null ) ||
             ( f_onset0 == null ) ||
             ( f_offset0 == null ) ||
             ( f_cell0 == null ) ||
             ( f_onset1 == null ) ||
             ( f_offset1 == null ) ||
             ( f_cell1 == null ) ||
             ( f_onset2 == null ) ||
             ( f_offset2 == null ) ||
             ( f_cell2 == null ) ||
             ( f_onset3 == null ) ||
             ( f_offset3 == null ) ||
             ( f_cell3 == null ) ||
             ( f_onset4 == null ) ||
             ( f_offset4 == null ) ||
             ( f_cell4 == null ) ||
             ( f_onset5 == null ) ||
             ( f_offset5 == null ) ||
             ( f_cell5 == null ) ||
             ( f_onset6 == null ) ||
             ( f_offset6 == null ) ||
             ( f_cell6 == null ) ||
             ( f_onset7 == null ) ||
             ( f_offset7 == null ) ||
             ( f_cell7 == null ) ||
             ( i_cell0 == null ) ||
             ( m_cell0 == null ) ||
             ( n_cell0 == null ) ||
             ( p_cell0 == null ) ||
             ( t_cell0 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( f_col0ID == DBIndex.INVALID_ID ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) ||
                     ( f_mve0 == null ) )
                {
                    outStream.printf("f_col0 alloc failed.  f_col0ID = %d, " +
                            "f_mve0ID = %d\n", f_col0ID, f_mve0ID);
                }

                if ( ( i_col0ID == DBIndex.INVALID_ID ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ||
                     ( i_mve0 == null ) )
                {
                    outStream.printf("i_col0 alloc failed.  i_col0ID = %d, " +
                            "f_mve0ID = %d\n", i_col0ID, i_mve0ID);
                }

                if ( ( m_col0ID == DBIndex.INVALID_ID ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) ||
                     ( m_mve0 == null ) )
                {
                    outStream.printf("m_col0 alloc failed.  m_col0ID = %d, " +
                            "f_mve0ID = %d\n", m_col0ID, m_mve0ID);
                }

                if ( ( n_col0ID == DBIndex.INVALID_ID ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) ||
                     ( n_mve0 == null ) )
                {
                    outStream.printf("n_col0 alloc failed.  n_col0ID = %d, " +
                            "f_mve0ID = %d\n", n_col0ID, n_mve0ID);
                }

                if ( ( p_col0ID == DBIndex.INVALID_ID ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) ||
                     ( p_mve0 == null ) )
                {
                    outStream.printf("p_col0 alloc failed.  p_col0ID = %d, " +
                            "f_mve0ID = %d\n", p_col0ID, p_mve0ID);
                }

                if ( ( t_col0ID == DBIndex.INVALID_ID ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) ||
                     ( t_mve0 == null ) )
                {
                    outStream.printf("t_col0 alloc failed.  t_col0ID = %d, " +
                            "f_mve0ID = %d\n", t_col0ID, t_mve0ID);
                }

                if ( ( f_onset0 == null ) ||
                     ( f_offset0 == null ) ||
                     ( f_cell0 == null ) )
                {
                    outStream.printf("f_cell0 alloc failed.\n");
                }

                if ( ( f_onset1 == null ) ||
                     ( f_offset1 == null ) ||
                     ( f_cell1 == null ) )
                {
                    outStream.printf("f_cell1 alloc failed.\n");
                }

                if ( ( f_onset2 == null ) ||
                     ( f_offset2 == null ) ||
                     ( f_cell2 == null ) )
                {
                    outStream.printf("f_cell2 alloc failed.\n");
                }

                if ( ( f_onset3 == null ) ||
                     ( f_offset3 == null ) ||
                     ( f_cell3 == null ) )
                {
                    outStream.printf("f_cell3 alloc failed.\n");
                }

                if ( ( f_onset4 == null ) ||
                     ( f_offset4 == null ) ||
                     ( f_cell4 == null ) )
                {
                    outStream.printf("f_cell4 alloc failed.\n");
                }

                if ( ( f_onset5 == null ) ||
                     ( f_offset5 == null ) ||
                     ( f_cell5 == null ) )
                {
                    outStream.printf("f_cell5 alloc failed.\n");
                }

                if ( ( f_onset6 == null ) ||
                     ( f_offset6 == null ) ||
                     ( f_cell6 == null ) )
                {
                    outStream.printf("f_cell6 alloc failed.\n");
                }

                if ( ( f_onset7 == null ) ||
                     ( f_offset7 == null ) ||
                     ( f_cell7 == null ) )
                {
                    outStream.printf("f_cell7 alloc failed.\n");
                }

                if ( i_cell0 == null )
                {
                    outStream.printf("i_cell0 alloc failed.\n");
                }

                if ( m_cell0 == null )
                {
                    outStream.printf("m_cell0 alloc failed.\n");
                }

                if ( n_cell0 == null )
                {
                    outStream.printf("n_cell0 alloc failed.\n");
                }

                if ( p_cell0 == null )
                {
                    outStream.printf("p_cell0 alloc failed.\n");
                }

                if ( t_cell0 == null )
                {
                    outStream.printf("t_cell0 alloc failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else {
            try {
                Object f_col0itsCells = PrivateAccessor.getField(f_col0, "itsCells");
                Object i_col0itsCells = PrivateAccessor.getField(i_col0, "itsCells");
                Object m_col0itsCells = PrivateAccessor.getField(m_col0, "itsCells");
                Object n_col0itsCells = PrivateAccessor.getField(n_col0, "itsCells");
                Object p_col0itsCells = PrivateAccessor.getField(p_col0, "itsCells");
                Object t_col0itsCells = PrivateAccessor.getField(t_col0, "itsCells");

                if ( ( f_col0itsCells == null ) ||
                      ( i_col0itsCells == null ) ||
                      ( m_col0itsCells == null ) ||
                      ( n_col0itsCells == null ) ||
                      ( p_col0itsCells == null ) ||
                      ( t_col0itsCells == null ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "one or more column itsCells fields not initialized.\n");
                    }
                }
            } catch (Throwable th) {
                outStream.printf("Problem with .getField of x_col0.\n");
                failures++;
            }
        }

        /* test append cell */
        if ( failures == 0 )
        {
            String expectedString =
                    "((1, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:01:000, 00:00:02:000, (0.0)))";
            String expectedDBString =
                    "(itsCells " +
                        "((DataCell (id 43) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:05:000)) " +
                            "(offset (60,00:00:06:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 44) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 43) " +
                                            "(itsValue 2.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 45) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:03:000)) " +
                            "(offset (60,00:00:04:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 46) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 45) " +
                                            "(itsValue 1.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 47) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:01:000)) " +
                            "(offset (60,00:00:02:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 48) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 47) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
                f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
                f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 3 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 3 )
                    {
                        outStream.printf("f_col0 = %d (3 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString.compareTo(f_col0.itsCellsToString())
                         != 0 )
                    {
                        outStream.printf(
                                "Unexpected f_col0.itsCellsToString(1): \"%s\"\n",
                                f_col0.itsCellsToString());
                    }

                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString())
                         != 0 )
                    {
                        outStream.printf(
                                "Unexpected f_col0.itsCellsToDBString(1): \"%s\"\n",
                                f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "appendCell() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("appendCell() test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* remove the existing cells in preparation for the next test */
        if ( failures == 0 )
        {
            String expectedString = "()";
            String expectedDBString = "(itsCells ())";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.removeCell(3, f_cell0_c.getID());
                f_col0.removeCell(2, f_cell1_c.getID());
                f_col0.removeCell(1, f_cell2_c.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0numCells = %d (0 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString.compareTo(f_col0.itsCellsToString())
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToString(2): \"%s\"\n",
                            f_col0.itsCellsToString());
                    }

                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString())
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToDBString(2): \"%s\"\n",
                            f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "removeCell() test 1 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("removeCell() test 1 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }


        /* test insert cell */
        if ( failures == 0 )
        {
            String expectedString =
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(3, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(4, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedDBString =
                    "(itsCells " +
                        "((DataCell (id 51) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:09:000)) " +
                            "(offset (60,00:00:10:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 52) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 51) " +
                                            "(itsValue 4.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 49) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:07:000)) " +
                            "(offset (60,00:00:08:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 50) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 49) " +
                                            "(itsValue 3.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 55) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:13:000)) " +
                            "(offset (60,00:00:14:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 56) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 55) " +
                                            "(itsValue 6.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 53) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 4) " +
                            "(onset (60,00:00:11:000)) " +
                            "(offset (60,00:00:12:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 54) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 53) " +
                                            "(itsValue 5.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(f_cell3_c = new DataCell(f_cell3), 1);
                f_col0.insertCell(f_cell4_c = new DataCell(f_cell4), 1);
                f_col0.insertCell(f_cell5_c = new DataCell(f_cell5), 3);
                f_col0.insertCell(f_cell6_c = new DataCell(f_cell6), 3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 4 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 4 )
                    {
                        outStream.printf("f_col0 = %d (4 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString.compareTo(f_col0.itsCellsToString())
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToString(3): \"%s\"\n",
                            f_col0.itsCellsToString());
                    }

                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString())
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToDBString(3): \"%s\"\n",
                            f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "insertCell() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("insertCell() test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }


        /* remove cells again -- test removeCell() more fully in passing */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String expectedString0 =
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(3, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(4, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedString1 =
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(3, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedString2 =
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:13:000, 00:00:14:000, (6.0)))";
            String expectedString3 =
                    "((1, 00:00:13:000, 00:00:14:000, (6.0)))";
            String expectedString4 = "()";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.removeCell(2, f_cell3_c.getID());
                testString1 = f_col0.itsCellsToString();
                f_col0.removeCell(3, f_cell5_c.getID());
                testString2 = f_col0.itsCellsToString();
                f_col0.removeCell(1, f_cell4_c.getID());
                testString3 = f_col0.itsCellsToString();
                f_col0.removeCell(1, f_cell6_c.getID());
                testString4 = f_col0.itsCellsToString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0 = %d (0 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n",
                            testString0);
                    }

                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n",
                            testString1);
                    }

                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n",
                            testString2);
                    }

                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n",
                            testString3);
                    }

                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n",
                            testString4);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "removeCell() test 2 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("removeCell() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* next, test the replaceCell() and getCellCopy() methods. Deal with
         * the singleton cell case first...
         */
        if ( failures == 0 )
        {
            String testString0 = "";
            String testString1 = "";
            String testString2 = "";
            String testString3 = "";
            String expectedString0 =
                    "()";
            String expectedString1 =
                    "((1, 00:00:01:000, 00:00:02:000, (0.0)))";
            String expectedString2 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)))";
            String expectedString3 =
                    "(1, 00:00:01:000, 00:00:02:000, (0.0))";
            DataCell f_cell0a = null;
            DataCell expected_old_cell = null;
            DataCell old_cell = null;
            Matrix m = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
                testString1 = f_col0.itsCellsToString();
                expected_old_cell = f_col0.getCell(1);
                f_cell0a = f_col0.getCellCopy(1);
                m = f_cell0a.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(10.0);
                f_cell0a.setVal(m);
                old_cell = f_col0.replaceCell(f_cell0a, 1);
                testString2 = f_col0.itsCellsToString();
                testString3 = old_cell.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 1 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( old_cell != expected_old_cell ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 1 )
                    {
                        outStream.printf("f_col0 = %d (1 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n",
                            testString0);
                    }

                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n",
                            testString1);
                    }

                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n",
                            testString2);
                    }

                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n",
                            testString3);
                    }

                    if ( old_cell != expected_old_cell )
                    {
                        outStream.printf("old_cell != expected_old_cell\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "replaceCell() test 1 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("replaceCell() test 1 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* ...and then with multiple entries.
         */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String testString5 = null;
            String testString6 = null;
            String testString7 = null;
            String expectedString0 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)))";
            String expectedString1 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)))";
            String expectedString2 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString3 =
                    "(3, 00:00:05:000, 00:00:06:000, (2.0))";
            String expectedString4 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (40.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString5 =
                    "(2, 00:00:03:000, 00:00:04:000, (1.0))";
            String expectedString6 =
                    "((1, 00:00:01:000, 00:00:02:000, (50.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (40.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString7 =
                    "(1, 00:00:01:000, 00:00:02:000, (10.0))";
            DataCell cell = null;
            DataCell expected_old_cell0 = null;
            DataCell expected_old_cell1 = null;
            DataCell expected_old_cell2 = null;
            DataCell old_cell0 = null;
            DataCell old_cell1 = null;
            DataCell old_cell2 = null;
            Matrix m = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
                testString1 = f_col0.itsCellsToString();

                expected_old_cell0 = f_col0.getCell(3);
                cell = f_col0.getCellCopy(3);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(30.0);
                cell.setVal(m);
                old_cell0 = f_col0.replaceCell(cell, 3);
                testString2 = f_col0.itsCellsToString();
                testString3 = old_cell0.toString();

                expected_old_cell1 = f_col0.getCell(2);
                cell = f_col0.getCellCopy(2);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(40.0);
                cell.setVal(m);
                old_cell1 = f_col0.replaceCell(cell, 2);
                testString4 = f_col0.itsCellsToString();
                testString5 = old_cell1.toString();

                expected_old_cell2 = f_col0.getCell(1);
                cell = f_col0.getCellCopy(1);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(50.0);
                cell.setVal(m);
                old_cell2 = f_col0.replaceCell(cell, 1);
                testString6 = f_col0.itsCellsToString();
                testString7 = old_cell2.toString();

                /* tidy up for the next test */
                f_col0.removeCell(3, f_cell2_c.getID());
                f_col0.removeCell(2, f_cell1_c.getID());
                f_col0.removeCell(1, f_cell0_c.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( expectedString5.compareTo(testString5) != 0 ) ||
                 ( expectedString6.compareTo(testString6) != 0 ) ||
                 ( expectedString7.compareTo(testString7) != 0 ) ||
                 ( old_cell0 != expected_old_cell0 ) ||
                 ( old_cell1 != expected_old_cell1 ) ||
                 ( old_cell2 != expected_old_cell2 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0.numCells = %d (0 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n",
                            testString0);
                    }

                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n",
                            testString1);
                    }

                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n",
                            testString2);
                    }

                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n",
                            testString3);
                    }

                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n",
                            testString4);
                    }

                    if ( expectedString5.compareTo(testString5) != 0 )
                    {
                        outStream.printf("Unexpected testString5: \"%s\"\n",
                            testString5);
                    }

                    if ( expectedString6.compareTo(testString6) != 0 )
                    {
                        outStream.printf("Unexpected testString6: \"%s\"\n",
                            testString6);
                    }

                    if ( expectedString7.compareTo(testString7) != 0 )
                    {
                        outStream.printf("Unexpected testString7: \"%s\"\n",
                            testString7);
                    }

                    if ( old_cell0 != expected_old_cell0 )
                    {
                        outStream.printf("old_cell0 != expected_old_cell0\n");
                    }

                    if ( old_cell1 != expected_old_cell1 )
                    {
                        outStream.printf("old_cell1 != expected_old_cell1\n");
                    }

                    if ( old_cell2 != expected_old_cell2 )
                    {
                        outStream.printf("old_cell2 != expected_old_cell2\n");
                    }


                    if ( ! completed )
                    {
                        outStream.printf(
                                "replaceCell() test 2 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("replaceCell() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* finally, test sortCells */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String testString5 = null;
            String testString6 = null;
            String testString7 = null;
            String expectedString0 =
                    "((1, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString1 =
                    "((1, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString2 =
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:15:000, 00:00:16:000, (7.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)))";
            String expectedString3 =
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString4 =
                    "((1, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString5 =
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString6 =
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)), " +
                     "(5, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(6, 00:00:09:000, 00:00:10:000, (4.0)))";
            String expectedString7 =
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(4, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(5, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(6, 00:00:15:000, 00:00:16:000, (7.0)))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                /* do a sort on an empty column to make sure there are
                 * no problems.
                 */
                f_col0.sortCells();

                f_col0.appendCell(f_cell7_c = new DataCell(f_cell7));

                testString0 = f_col0.itsCellsToString();

                f_col0.sortCells();

                testString1 = f_col0.itsCellsToString();

                f_col0.insertCell(f_cell1_c = new DataCell(f_cell1), 1);
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));

                testString2 = f_col0.itsCellsToString();

                f_col0.sortCells();

                testString3 = f_col0.itsCellsToString();

                f_col0.insertCell(f_cell8_c = new DataCell(f_cell8), 1);

                testString4 = f_col0.itsCellsToString();

                f_col0.sortCells();

                testString5 = f_col0.itsCellsToString();

                f_col0.appendCell(f_cell3 = new DataCell(f_cell3));
                f_col0.appendCell(f_cell4 = new DataCell(f_cell4));

                testString6 = f_col0.itsCellsToString();

                f_col0.sortCells();

                testString7 = f_col0.itsCellsToString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0.numCells != 6 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( expectedString5.compareTo(testString5) != 0 ) ||
                 ( expectedString6.compareTo(testString6) != 0 ) ||
                 ( expectedString7.compareTo(testString7) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 6 )
                    {
                        outStream.printf("f_col0 = %d (6 expected).\n",
                                         f_col0.numCells);
                    }

                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n",
                            testString0);
                    }

                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n",
                            testString1);
                    }

                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n",
                            testString2);
                    }

                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n",
                            testString3);
                    }

                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n",
                            testString4);
                    }

                    if ( expectedString5.compareTo(testString5) != 0 )
                    {
                        outStream.printf("Unexpected testString5: \"%s\"\n",
                            testString5);
                    }

                    if ( expectedString6.compareTo(testString6) != 0 )
                    {
                        outStream.printf("Unexpected testString6: \"%s\"\n",
                            testString6);
                    }

                    if ( expectedString7.compareTo(testString7) != 0 )
                    {
                        outStream.printf("Unexpected testString7: \"%s\"\n",
                            testString7);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "sortCells() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("sortCells() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }



        /* Now verify that the cell management methods fail on invalid input */

        /* verify appendCell fails on null */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(null);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("appendCell(null) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendCell(null) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify appendCell fails when fed a cell configured
         * for another column.
         *
         * One could argue that I should run this test on all cell type /
         * coplumn type pairs.  However, this would be overkill, as while
         * the validCell() method does check these issues, it does so only
         * after verifying the cells itsColID and itsMveID fields match those
         * of the target column.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(i_cell0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("appendCell(i_cell0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendCell(i_cell0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on null */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(null, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(null, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(null, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a cell configured for a different column */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(i_cell0, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(i_cell0, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(i_cell0, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(f_cell3, 0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(f_cell3, 0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(f_cell3, 0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a ord  that is larger than the number
         * of cells in column plus 1.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 2;
                f_col0.insertCell(f_cell3, bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(f_cell3, bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "insertCell(f_cell3, bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.getCell(0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("getCell(0) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCell(0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCell(0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCell fails on a ord greater than the number of
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.getCell(bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "getCell(bogus_ord) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCell(bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCell(bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCellCopy fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.getCellCopy(0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("getCellCopy(0) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCellCopy(0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCellCopy(0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCellCopy fails on a ord greater than the number of
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.getCellCopy(bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "getCellCopy(bogus_ord) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCellCopy(bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCellCopy(bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.removeCell(0, f_cell0.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("removeCell(0, f_cell0.getID()) " +
                                "returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "removeCell(0, f_cell0.getID()) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "removeCell(0, f_cell0.getID()) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a ord greater than the number of
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.removeCell(bogus_ord, f_cell0.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a bogus cell ID.
         */
        if ( failures == 0 )
        {
            long bogusID;
            DataCell targetCell = null;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                targetCell = f_col0.getCell(1);
                bogusID = targetCell.getID() + 1;
                testCell = f_col0.removeCell(1, bogusID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "removeCell(1, bogusID) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("removeCell(1, bogusID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("removeCell(1, bogusID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(f_cell6, 0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("replaceCell(f_cell6, 0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(f_cell6, 0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on greater than the number of
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                f_col0.replaceCell(f_cell6, bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "replaceCell(f_cell6, bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "replaceCell(f_cell6, bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on null new cell.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(null, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("replaceCell(null, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(null, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on col/cell mismatch.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(i_cell0, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "replaceCell(i_cell0, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(i_cell0, 1) failed to " +
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

        return pass;

    } /* DataCell::TestCellManagement() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 12/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing copy argument constructor for class DataColumn           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long f_mve1ID = DBIndex.INVALID_ID;
        long f_mve2ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long i_mve1ID = DBIndex.INVALID_ID;
        long i_mve2ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long m_mve2ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long n_mve1ID = DBIndex.INVALID_ID;
        long n_mve2ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long p_mve1ID = DBIndex.INVALID_ID;
        long p_mve2ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long t_mve1ID = DBIndex.INVALID_ID;
        long t_mve2ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        MatrixVocabElement f_mve1 = null;
        MatrixVocabElement f_mve2 = null;
        MatrixVocabElement i_mve1 = null;
        MatrixVocabElement i_mve2 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement m_mve2 = null;
        MatrixVocabElement n_mve1 = null;
        MatrixVocabElement n_mve2 = null;
        MatrixVocabElement p_mve1 = null;
        MatrixVocabElement p_mve2 = null;
        MatrixVocabElement t_mve1 = null;
        MatrixVocabElement t_mve2 = null;
        DataColumn f_col0 = null;
        DataColumn f_col1 = null;
        DataColumn f_col2 = null;
        DataColumn i_col0 = null;
        DataColumn i_col1 = null;
        DataColumn i_col2 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn m_col2 = null;
        DataColumn n_col0 = null;
        DataColumn n_col1 = null;
        DataColumn n_col2 = null;
        DataColumn p_col0 = null;
        DataColumn p_col1 = null;
        DataColumn p_col2 = null;
        DataColumn t_col0 = null;
        DataColumn t_col1 = null;
        DataColumn t_col2 = null;
        DataColumn f_col0_copy = null;
        DataColumn f_col1_copy = null;
        DataColumn f_col2_copy = null;
        DataColumn i_col0_copy = null;
        DataColumn i_col1_copy = null;
        DataColumn i_col2_copy = null;
        DataColumn m_col0_copy = null;
        DataColumn m_col1_copy = null;
        DataColumn m_col2_copy = null;
        DataColumn n_col0_copy = null;
        DataColumn n_col1_copy = null;
        DataColumn n_col2_copy = null;
        DataColumn p_col0_copy = null;
        DataColumn p_col1_copy = null;
        DataColumn p_col2_copy = null;
        DataColumn t_col0_copy = null;
        DataColumn t_col1_copy = null;
        DataColumn t_col2_copy = null;
        DataColumn dc = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First allocate a selection of data columns to test the
         * the copy constructor on.  Note that in no case do we create
         * a set of cells for the base columns as the cells vector should
         * not be copied.  We will test this at the Database level, where
         * we will be constructing sets of cells for the columns already.
         */
        try
        {
            db = new ODBCDatabase();


            f_col0 = new DataColumn(db, "f_col0",
                                    MatrixVocabElement.MatrixType.FLOAT);

            f_mve1 = new MatrixVocabElement(db, "f_col1");
            f_mve1.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve1.appendFormalArg(farg);
            db.vl.addElement(f_mve1);
            f_mve1ID = f_mve1.getID();
            f_col1 = new DataColumn(db, "f_col1", true, false, f_mve1ID);

            f_mve2 = new MatrixVocabElement(db, "f_col2");
            f_mve2.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve2.appendFormalArg(farg);
            db.vl.addElement(f_mve2);
            f_mve2ID = f_mve2.getID();
            f_col2 = new DataColumn(db, "f_col2", false, true, f_mve2ID);


            i_col0 = new DataColumn(db, "i_col0",
                                    MatrixVocabElement.MatrixType.INTEGER);

            i_mve1 = new MatrixVocabElement(db, "i_col1");
            i_mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve1.appendFormalArg(farg);
            db.vl.addElement(i_mve1);
            i_mve1ID = i_mve1.getID();
            i_col1 = new DataColumn(db, "i_col1", true, false, i_mve1ID);

            i_mve2 = new MatrixVocabElement(db, "i_col2");
            i_mve2.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve2.appendFormalArg(farg);
            db.vl.addElement(i_mve2);
            i_mve2ID = i_mve2.getID();
            i_col2 = new DataColumn(db, "i_col2", false, true, i_mve2ID);


            m_col0 = new DataColumn(db, "m_col0",
                                    MatrixVocabElement.MatrixType.MATRIX);

            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);

            m_mve2 = new MatrixVocabElement(db, "m_col2");
            m_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve2.appendFormalArg(farg);
            db.vl.addElement(m_mve2);
            m_mve2ID = m_mve2.getID();
            m_col2 = new DataColumn(db, "m_col2", false, true, m_mve2ID);


            n_col0 = new DataColumn(db, "n_col0",
                                    MatrixVocabElement.MatrixType.NOMINAL);

            n_mve1 = new MatrixVocabElement(db, "n_col1");
            n_mve1.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve1.appendFormalArg(farg);
            db.vl.addElement(n_mve1);
            n_mve1ID = n_mve1.getID();
            n_col1 = new DataColumn(db, "n_col1", true, false, n_mve1ID);

            n_mve2 = new MatrixVocabElement(db, "n_col2");
            n_mve2.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve2.appendFormalArg(farg);
            db.vl.addElement(n_mve2);
            n_mve2ID = n_mve2.getID();
            n_col2 = new DataColumn(db, "n_col2", false, true, n_mve2ID);


            p_col0 = new DataColumn(db, "p_col0",
                                    MatrixVocabElement.MatrixType.PREDICATE);

            p_mve1 = new MatrixVocabElement(db, "p_col1");
            p_mve1.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve1.appendFormalArg(farg);
            db.vl.addElement(p_mve1);
            p_mve1ID = p_mve1.getID();
            p_col1 = new DataColumn(db, "p_col1", true, false, p_mve1ID);

            p_mve2 = new MatrixVocabElement(db, "p_col2");
            p_mve2.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve2.appendFormalArg(farg);
            db.vl.addElement(p_mve2);
            p_mve2ID = p_mve2.getID();
            p_col2 = new DataColumn(db, "p_col2", false, true, p_mve2ID);


            t_col0 = new DataColumn(db, "t_col0",
                                    MatrixVocabElement.MatrixType.TEXT);

            t_mve1 = new MatrixVocabElement(db, "t_col1");
            t_mve1.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve1.appendFormalArg(farg);
            db.vl.addElement(t_mve1);
            t_mve1ID = t_mve1.getID();
            t_col1 = new DataColumn(db, "t_col1", true, false, t_mve1ID);

            t_mve2 = new MatrixVocabElement(db, "t_col2");
            t_mve2.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve2.appendFormalArg(farg);
            db.vl.addElement(t_mve2);
            t_mve2ID = t_mve2.getID();
            t_col2 = new DataColumn(db, "t_col2", false, true, t_mve2ID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( f_col0 == null ) ||
             ( f_mve1 == null ) ||
             ( f_mve1ID == DBIndex.INVALID_ID ) ||
             ( f_col1 == null ) ||
             ( f_mve2 == null ) ||
             ( f_mve2ID == DBIndex.INVALID_ID ) ||
             ( f_col2 == null ) ||
             ( f_mve2ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve1 == null ) ||
             ( i_mve1ID == DBIndex.INVALID_ID ) ||
             ( i_col1 == null ) ||
             ( i_mve2 == null ) ||
             ( i_mve2ID == DBIndex.INVALID_ID ) ||
             ( i_col2 == null ) ||
             ( m_col0 == null ) ||
             ( m_mve1 == null ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( m_mve2 == null ) ||
             ( m_mve2ID == DBIndex.INVALID_ID ) ||
             ( m_col2 == null ) ||
             ( n_col0 == null ) ||
             ( n_mve1 == null ) ||
             ( n_mve1ID == DBIndex.INVALID_ID ) ||
             ( n_col1 == null ) ||
             ( n_mve2 == null ) ||
             ( n_mve2ID == DBIndex.INVALID_ID ) ||
             ( n_col2 == null ) ||
             ( p_col0 == null ) ||
             ( p_mve1 == null ) ||
             ( p_mve1ID == DBIndex.INVALID_ID ) ||
             ( p_col1 == null ) ||
             ( p_mve2 == null ) ||
             ( p_mve2ID == DBIndex.INVALID_ID ) ||
             ( p_col2 == null ) ||
             ( t_col0 == null ) ||
             ( t_mve1 == null ) ||
             ( t_mve1ID == DBIndex.INVALID_ID ) ||
             ( t_col1 == null ) ||
             ( t_mve2 == null ) ||
             ( t_mve2ID == DBIndex.INVALID_ID ) ||
             ( t_col2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( f_col0 == null )
                {
                    outStream.print("f_col0 allocation failed.\n");
                }

                if ( ( f_mve1 == null ) ||
                     ( f_mve1ID == DBIndex.INVALID_ID ) ||
                     ( f_col1 == null ) )
                {
                    outStream.print("f_col1 allocation failed.\n");
                }

                if ( ( f_mve2 == null ) ||
                     ( f_mve2ID == DBIndex.INVALID_ID ) ||
                     ( f_col2 == null ) )
                {
                    outStream.print("f_col2 allocation failed.\n");
                }


                if ( i_col0 == null )
                {
                    outStream.print("i_col0 allocation failed.\n");
                }

                if ( ( i_mve1 == null ) ||
                     ( i_mve1ID == DBIndex.INVALID_ID ) ||
                     ( i_col1 == null ) )
                {
                    outStream.print("i_col1 allocation failed.\n");
                }

                if ( ( i_mve2 == null ) ||
                     ( i_mve2ID == DBIndex.INVALID_ID ) ||
                     ( i_col2 == null ) )
                {
                    outStream.print("i_col2 allocation failed.\n");
                }


                if ( m_col0 == null )
                {
                    outStream.print("m_col0 allocation failed.\n");
                }

                if ( ( m_mve1 == null ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) )
                {
                    outStream.print("m_col1 allocation failed.\n");
                }

                if ( ( m_mve2 == null ) ||
                     ( m_mve2ID == DBIndex.INVALID_ID ) ||
                     ( m_col2 == null ) )
                {
                    outStream.print("m_col2 allocation failed.\n");
                }


                if ( n_col0 == null )
                {
                    outStream.print("n_col0 allocation failed.\n");
                }

                if ( ( n_mve1 == null ) ||
                     ( n_mve1ID == DBIndex.INVALID_ID ) ||
                     ( n_col1 == null ) )
                {
                    outStream.print("n_col1 allocation failed.\n");
                }

                if ( ( n_mve2 == null ) ||
                     ( n_mve2ID == DBIndex.INVALID_ID ) ||
                     ( n_col2 == null ) )
                {
                    outStream.print("n_col2 allocation failed.\n");
                }


                if ( p_col0 == null )
                {
                    outStream.print("p_col0 allocation failed.\n");
                }

                if ( ( p_mve1 == null ) ||
                     ( p_mve1ID == DBIndex.INVALID_ID ) ||
                     ( p_col1 == null ) )
                {
                    outStream.print("p_col1 allocation failed.\n");
                }

                if ( ( p_mve2 == null ) ||
                     ( p_mve2ID == DBIndex.INVALID_ID ) ||
                     ( p_col2 == null ) )
                {
                    outStream.print("p_col2 allocation failed.\n");
                }


                if ( t_col0 == null )
                {
                    outStream.print("t_col0 allocation failed.\n");
                }

                if ( ( t_mve1 == null ) ||
                     ( t_mve1ID == DBIndex.INVALID_ID ) ||
                     ( t_col1 == null ) )
                {
                    outStream.print("t_col1 allocation failed.\n");
                }

                if ( ( t_mve2 == null ) ||
                     ( t_mve2ID == DBIndex.INVALID_ID ) ||
                     ( t_col2 == null ) )
                {
                    outStream.print("t_col2 allocation failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
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
                f_col0_copy = new DataColumn(f_col0);
                f_col1_copy = new DataColumn(f_col1);
                f_col2_copy = new DataColumn(f_col2);

                i_col0_copy = new DataColumn(i_col0);
                i_col1_copy = new DataColumn(i_col1);
                i_col2_copy = new DataColumn(i_col2);

                m_col0_copy = new DataColumn(m_col0);
                m_col1_copy = new DataColumn(m_col1);
                m_col2_copy = new DataColumn(m_col2);

                n_col0_copy = new DataColumn(n_col0);
                n_col1_copy = new DataColumn(n_col1);
                n_col2_copy = new DataColumn(n_col2);

                p_col0_copy = new DataColumn(p_col0);
                p_col1_copy = new DataColumn(p_col1);
                p_col2_copy = new DataColumn(p_col2);

                t_col0_copy = new DataColumn(t_col0);
                t_col1_copy = new DataColumn(t_col1);
                t_col2_copy = new DataColumn(t_col2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_col0_copy == null ) ||
                 ( f_col1_copy == null ) ||
                 ( f_col2_copy == null ) ||
                 ( i_col0_copy == null ) ||
                 ( i_col1_copy == null ) ||
                 ( i_col2_copy == null ) ||
                 ( m_col0_copy == null ) ||
                 ( m_col1_copy == null ) ||
                 ( m_col2_copy == null ) ||
                 ( n_col0_copy == null ) ||
                 ( n_col1_copy == null ) ||
                 ( n_col2_copy == null ) ||
                 ( p_col0_copy == null ) ||
                 ( p_col1_copy == null ) ||
                 ( p_col2_copy == null ) ||
                 ( t_col0_copy == null ) ||
                 ( t_col1_copy == null ) ||
                 ( t_col2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0_copy == null )
                    {
                        outStream.print("f_col0_copy allocation failed.\n");
                    }

                    if ( f_col1_copy == null )
                    {
                        outStream.print("f_col1_copy allocation failed.\n");
                    }

                    if ( f_col1_copy == null )
                    {
                        outStream.print("f_col2_copy allocation failed.\n");
                    }

                    if ( i_col0_copy == null )
                    {
                        outStream.print("i_col0_copy allocation failed.\n");
                    }

                    if ( i_col1_copy == null )
                    {
                        outStream.print("i_col1_copy allocation failed.\n");
                    }

                    if ( i_col1_copy == null )
                    {
                        outStream.print("i_col2_copy allocation failed.\n");
                    }

                    if ( m_col0_copy == null )
                    {
                        outStream.print("m_col0_copy allocation failed.\n");
                    }

                    if ( m_col1_copy == null )
                    {
                        outStream.print("m_col1_copy allocation failed.\n");
                    }

                    if ( m_col1_copy == null )
                    {
                        outStream.print("m_col2_copy allocation failed.\n");
                    }

                    if ( n_col0_copy == null )
                    {
                        outStream.print("n_col0_copy allocation failed.\n");
                    }

                    if ( n_col1_copy == null )
                    {
                        outStream.print("n_col1_copy allocation failed.\n");
                    }

                    if ( n_col1_copy == null )
                    {
                        outStream.print("n_col2_copy allocation failed.\n");
                    }

                    if ( p_col0_copy == null )
                    {
                        outStream.print("p_col0_copy allocation failed.\n");
                    }

                    if ( p_col1_copy == null )
                    {
                        outStream.print("p_col1_copy allocation failed.\n");
                    }

                    if ( p_col1_copy == null )
                    {
                        outStream.print("p_col2_copy allocation failed.\n");
                    }

                    if ( t_col0_copy == null )
                    {
                        outStream.print("t_col0_copy allocation failed.\n");
                    }

                    if ( t_col1_copy == null )
                    {
                        outStream.print("t_col1_copy allocation failed.\n");
                    }

                    if ( t_col1_copy == null )
                    {
                        outStream.print("t_col2_copy allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "copy constructor test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("copy constructor test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyDataColumnCopy(f_col0, f_col0_copy,
                        outStream, verbose, "f_col0", "f_col0_copy");

                failures += VerifyDataColumnCopy(f_col1, f_col1_copy,
                        outStream, verbose, "f_col1", "f_col1_copy");

                failures += VerifyDataColumnCopy(f_col2, f_col2_copy,
                        outStream, verbose, "f_col2", "f_col2_copy");


                failures += VerifyDataColumnCopy(i_col0, i_col0_copy,
                        outStream, verbose, "i_col0", "i_col0_copy");

                failures += VerifyDataColumnCopy(i_col1, i_col1_copy,
                        outStream, verbose, "i_col1", "i_col1_copy");

                failures += VerifyDataColumnCopy(f_col2, f_col2_copy,
                        outStream, verbose, "i_col2", "i_col2_copy");


                failures += VerifyDataColumnCopy(m_col0, m_col0_copy,
                        outStream, verbose, "m_col0", "m_col0_copy");

                failures += VerifyDataColumnCopy(m_col1, m_col1_copy,
                        outStream, verbose, "m_col1", "m_col1_copy");

                failures += VerifyDataColumnCopy(m_col2, m_col2_copy,
                        outStream, verbose, "m_col2", "m_col2_copy");


                failures += VerifyDataColumnCopy(n_col0, n_col0_copy,
                        outStream, verbose, "n_col0", "n_col0_copy");

                failures += VerifyDataColumnCopy(n_col1, n_col1_copy,
                        outStream, verbose, "n_col1", "n_col1_copy");

                failures += VerifyDataColumnCopy(n_col2, n_col2_copy,
                        outStream, verbose, "n_col2", "n_col2_copy");


                failures += VerifyDataColumnCopy(p_col0, p_col0_copy,
                        outStream, verbose, "p_col0", "p_col0_copy");

                failures += VerifyDataColumnCopy(p_col1, p_col1_copy,
                        outStream, verbose, "p_col1", "p_col1_copy");

                failures += VerifyDataColumnCopy(p_col2, p_col2_copy,
                        outStream, verbose, "p_col2", "p_col2_copy");


                failures += VerifyDataColumnCopy(t_col0, t_col0_copy,
                        outStream, verbose, "t_col0", "t_col0_copy");

                failures += VerifyDataColumnCopy(t_col1, t_col1_copy,
                        outStream, verbose, "t_col1", "t_col1_copy");

                failures += VerifyDataColumnCopy(t_col2, t_col2_copy,
                        outStream, verbose, "t_col2", "t_col2_copy");
            }
        }

        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on null */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf(
                                "new DataColumn(null) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(null) failed to " +
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

        return pass;

    } /* DataCell::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods of the class.
     *
     *                                              JRM -- 12/31/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long f_col0ID = DBIndex.INVALID_ID;
        long i_col0ID = DBIndex.INVALID_ID;
        long m_col0ID = DBIndex.INVALID_ID;
        long m_col1ID = DBIndex.INVALID_ID;
        long n_col0ID = DBIndex.INVALID_ID;
        long p_col0ID = DBIndex.INVALID_ID;
        long t_col0ID = DBIndex.INVALID_ID;
        long fargID;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement t_mve0 = null;
        DataColumn f_col0 = null;
        DataColumn i_col0 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn n_col0 = null;
        DataColumn p_col0 = null;
        DataColumn t_col0 = null;
        DataColumn dc = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell3 = null;
        DataCell f_cell4 = null;
        DataCell f_cell5 = null;
        DataCell f_cell6 = null;
        DataCell f_cell7 = null;
        DataCell f_cell8 = null;
        DataCell i_cell0 = null;
        DataCell m_cell0 = null;
        DataCell n_cell0 = null;
        DataCell p_cell0 = null;
        DataCell t_cell0 = null;
        DataCell f_cell0_c = null;
        DataCell f_cell1_c = null;
        DataCell f_cell2_c = null;
        DataCell f_cell3_c = null;
        DataCell f_cell4_c = null;
        DataCell f_cell5_c = null;
        DataCell f_cell6_c = null;
        DataCell f_cell7_c = null;
        DataCell f_cell8_c = null;
        DataCell i_cell0_c = null;
        DataCell m_cell0_c = null;
        DataCell n_cell0_c = null;
        DataCell p_cell0_c = null;
        DataCell t_cell0_c = null;
        TimeStamp f_onset0 = null;
        TimeStamp f_onset1 = null;
        TimeStamp f_onset2 = null;
        TimeStamp f_onset3 = null;
        TimeStamp f_onset4 = null;
        TimeStamp f_onset5 = null;
        TimeStamp f_onset6 = null;
        TimeStamp f_onset7 = null;
        TimeStamp f_onset8 = null;
        TimeStamp f_offset0 = null;
        TimeStamp f_offset1 = null;
        TimeStamp f_offset2 = null;
        TimeStamp f_offset3 = null;
        TimeStamp f_offset4 = null;
        TimeStamp f_offset5 = null;
        TimeStamp f_offset6 = null;
        TimeStamp f_offset7 = null;
        TimeStamp f_offset8 = null;
        Vector<DataValue> f_arg_list0 = null;
        Vector<DataValue> f_arg_list1 = null;
        Vector<DataValue> f_arg_list2 = null;
        Vector<DataValue> f_arg_list3 = null;
        Vector<DataValue> f_arg_list4 = null;
        Vector<DataValue> f_arg_list5 = null;
        Vector<DataValue> f_arg_list6 = null;
        Vector<DataValue> f_arg_list7 = null;
        Vector<DataValue> f_arg_list8 = null;
        Matrix f_matrix0 = null;
        Matrix f_matrix1 = null;
        Matrix f_matrix2 = null;
        Matrix f_matrix3 = null;
        Matrix f_matrix4 = null;
        Matrix f_matrix5 = null;
        Matrix f_matrix6 = null;
        Matrix f_matrix7 = null;
        Matrix f_matrix8 = null;
        FormalArgument farg = null;
        DataValue arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First, allocate a selection of columns and cells to work with. After
         * we insert the columns in the database, we use db.cl.getColumn()
         * to get a reference to the actual column in the database.
         */
        try
        {
            db = new ODBCDatabase();


            f_col0 = new DataColumn(db, "f_col0",
                                    MatrixVocabElement.MatrixType.FLOAT);
            f_col0ID = db.addColumn(f_col0);
            f_col0 = (DataColumn)db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);


            i_col0 = new DataColumn(db, "i_col0",
                                    MatrixVocabElement.MatrixType.INTEGER);
            i_col0ID = db.addColumn(i_col0);
            i_col0 = (DataColumn)db.cl.getColumn(i_col0ID);
            i_mve0ID = i_col0.getItsMveID();
            i_mve0 = db.getMatrixVE(i_mve0ID);


            m_col0 = new DataColumn(db, "m_col0",
                                    MatrixVocabElement.MatrixType.MATRIX);
            m_col0ID = db.addColumn(m_col0);
            m_col0 = (DataColumn)db.cl.getColumn(m_col0ID);
            m_mve0ID = m_col0.getItsMveID();
            m_mve0 = db.getMatrixVE(m_mve0ID);


            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);
            db.cl.addColumn(m_col1);
            m_col1ID = m_col1.getID();


            n_col0 = new DataColumn(db, "n_col0",
                                    MatrixVocabElement.MatrixType.NOMINAL);
            n_col0ID = db.addColumn(n_col0);
            n_col0 = (DataColumn)db.cl.getColumn(n_col0ID);
            n_mve0ID = n_col0.getItsMveID();
            n_mve0 = db.getMatrixVE(n_mve0ID);


            p_col0 = new DataColumn(db, "p_col0",
                                    MatrixVocabElement.MatrixType.PREDICATE);
            p_col0ID = db.addColumn(p_col0);
            p_col0 = (DataColumn)db.cl.getColumn(p_col0ID);
            p_mve0ID = p_col0.getItsMveID();
            p_mve0 = db.getMatrixVE(p_mve0ID);


            t_col0 = new DataColumn(db, "t_col0",
                                    MatrixVocabElement.MatrixType.TEXT);
            t_col0ID = db.addColumn(t_col0);
            t_col0 = (DataColumn)db.cl.getColumn(t_col0ID);
            t_mve0ID = t_col0.getItsMveID();
            t_mve0 = db.getMatrixVE(t_mve0ID);


            f_onset0 = new TimeStamp(db.getTicks(), 60);
            f_offset0 = new TimeStamp(db.getTicks(), 120);
            f_arg_list0 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);
            f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID,
                                       f_onset0, f_offset0, f_matrix0);

            f_onset1 = new TimeStamp(db.getTicks(), 180);
            f_offset1 = new TimeStamp(db.getTicks(), 240);
            f_arg_list1 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            f_arg_list1.add(arg);
            f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
            f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID,
                                       f_onset1, f_offset1, f_matrix1);

            f_onset2 = new TimeStamp(db.getTicks(), 300);
            f_offset2 = new TimeStamp(db.getTicks(), 360);
            f_arg_list2 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 2.0);
            f_arg_list2.add(arg);
            f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
            f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID,
                                       f_onset2, f_offset2, f_matrix2);

            f_onset3 = new TimeStamp(db.getTicks(), 420);
            f_offset3 = new TimeStamp(db.getTicks(), 480);
            f_arg_list3 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 3.0);
            f_arg_list3.add(arg);
            f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
            f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID,
                                       f_onset3, f_offset3, f_matrix3);

            f_onset4 = new TimeStamp(db.getTicks(), 540);
            f_offset4 = new TimeStamp(db.getTicks(), 600);
            f_arg_list4 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 4.0);
            f_arg_list4.add(arg);
            f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
            f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID,
                                       f_onset4, f_offset4, f_matrix4);

            f_onset5 = new TimeStamp(db.getTicks(), 660);
            f_offset5 = new TimeStamp(db.getTicks(), 720);
            f_arg_list5 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 5.0);
            f_arg_list5.add(arg);
            f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
            f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID,
                                       f_onset5, f_offset5, f_matrix5);

            f_onset6 = new TimeStamp(db.getTicks(), 780);
            f_offset6 = new TimeStamp(db.getTicks(), 840);
            f_arg_list6 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 6.0);
            f_arg_list6.add(arg);
            f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
            f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID,
                                       f_onset6, f_offset6, f_matrix6);

            f_onset7 = new TimeStamp(db.getTicks(), 900);
            f_offset7 = new TimeStamp(db.getTicks(), 960);
            f_arg_list7 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 7.0);
            f_arg_list7.add(arg);
            f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
            f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID,
                                       f_onset7, f_offset7, f_matrix7);

            f_onset8 = new TimeStamp(db.getTicks(), 900);
            f_offset8 = new TimeStamp(db.getTicks(), 960);
            f_arg_list8 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 8.0);
            f_arg_list8.add(arg);
            f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
            f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID,
                                       f_onset8, f_offset8, f_matrix8);

            i_cell0 = new DataCell(db, "i_cell0", i_col0ID, i_mve0ID);
            m_cell0 = new DataCell(db, "m_cell0", m_col0ID, m_mve0ID);
            n_cell0 = new DataCell(db, "n_cell0", n_col0ID, n_mve0ID);
            p_cell0 = new DataCell(db, "p_cell0", p_col0ID, p_mve0ID);
            t_cell0 = new DataCell(db, "t_cell0", t_col0ID, t_mve0ID);

            f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
            f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
            f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
            f_col0.appendCell(f_cell3_c = new DataCell(f_cell3));
            f_col0.appendCell(f_cell4_c = new DataCell(f_cell4));
            f_col0.appendCell(f_cell5_c = new DataCell(f_cell5));
            f_col0.appendCell(f_cell6_c = new DataCell(f_cell6));
            f_col0.appendCell(f_cell7_c = new DataCell(f_cell7));
            f_col0.appendCell(f_cell8_c = new DataCell(f_cell8));

            i_col0.appendCell(i_cell0_c = new DataCell(i_cell0));
            m_col0.appendCell(m_cell0_c = new DataCell(m_cell0));
            n_col0.appendCell(n_cell0_c = new DataCell(n_cell0));
            p_col0.appendCell(p_cell0_c = new DataCell(p_cell0));
            t_col0.appendCell(t_cell0_c = new DataCell(t_cell0));

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( f_col0ID == DBIndex.INVALID_ID ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve0 == null ) ||
             ( i_col0ID == DBIndex.INVALID_ID ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve0 == null ) ||
             ( m_col0ID == DBIndex.INVALID_ID ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve0 == null ) ||
             ( m_col1ID == DBIndex.INVALID_ID ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( m_mve1 == null ) ||
             ( n_col0ID == DBIndex.INVALID_ID ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve0 == null ) ||
             ( p_col0ID == DBIndex.INVALID_ID ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve0 == null ) ||
             ( t_col0ID == DBIndex.INVALID_ID ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve0 == null ) ||
             ( f_onset0 == null ) ||
             ( f_offset0 == null ) ||
             ( f_cell0 == null ) ||
             ( f_onset1 == null ) ||
             ( f_offset1 == null ) ||
             ( f_cell1 == null ) ||
             ( f_onset2 == null ) ||
             ( f_offset2 == null ) ||
             ( f_cell2 == null ) ||
             ( f_onset3 == null ) ||
             ( f_offset3 == null ) ||
             ( f_cell3 == null ) ||
             ( f_onset4 == null ) ||
             ( f_offset4 == null ) ||
             ( f_cell4 == null ) ||
             ( f_onset5 == null ) ||
             ( f_offset5 == null ) ||
             ( f_cell5 == null ) ||
             ( f_onset6 == null ) ||
             ( f_offset6 == null ) ||
             ( f_cell6 == null ) ||
             ( f_onset7 == null ) ||
             ( f_offset7 == null ) ||
             ( f_cell7 == null ) ||
             ( f_onset8 == null ) ||
             ( f_offset8 == null ) ||
             ( f_cell8 == null ) ||
             ( i_cell0 == null ) ||
             ( m_cell0 == null ) ||
             ( n_cell0 == null ) ||
             ( p_cell0 == null ) ||
             ( t_cell0 == null ) ||
             ( f_col0.getNumCells() != 9 ) ||
             ( i_col0.getNumCells() != 1 ) ||
             ( m_col0.getNumCells() != 1 ) ||
             ( m_col1.getNumCells() != 0 ) ||
             ( n_col0.getNumCells() != 1 ) ||
             ( p_col0.getNumCells() != 1 ) ||
             ( t_col0.getNumCells() != 1 ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( f_col0ID == DBIndex.INVALID_ID ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) ||
                     ( f_mve0 == null ) )
                {
                    outStream.printf("f_col0 alloc failed.  f_col0ID = %d, " +
                            "f_mve0ID = %d\n", f_col0ID, f_mve0ID);
                }

                if ( ( i_col0ID == DBIndex.INVALID_ID ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ||
                     ( i_mve0 == null ) )
                {
                    outStream.printf("i_col0 alloc failed.  i_col0ID = %d, " +
                            "i_mve0ID = %d\n", i_col0ID, i_mve0ID);
                }

                if ( ( m_col0ID == DBIndex.INVALID_ID ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) ||
                     ( m_mve0 == null ) )
                {
                    outStream.printf("m_col0 alloc failed.  m_col0ID = %d, " +
                            "m_mve0ID = %d\n", m_col0ID, m_mve0ID);
                }

                if ( ( m_col1ID == DBIndex.INVALID_ID ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) ||
                     ( m_mve1 == null ) )
                {
                    outStream.printf("m_col1 alloc failed.  m_col1ID = %d, " +
                            "m_mve1ID = %d\n", m_col1ID, m_mve1ID);
                }

                if ( ( n_col0ID == DBIndex.INVALID_ID ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) ||
                     ( n_mve0 == null ) )
                {
                    outStream.printf("n_col0 alloc failed.  n_col0ID = %d, " +
                            "n_mve0ID = %d\n", n_col0ID, n_mve0ID);
                }

                if ( ( p_col0ID == DBIndex.INVALID_ID ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) ||
                     ( p_mve0 == null ) )
                {
                    outStream.printf("p_col0 alloc failed.  p_col0ID = %d, " +
                            "p_mve0ID = %d\n", p_col0ID, p_mve0ID);
                }

                if ( ( t_col0ID == DBIndex.INVALID_ID ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) ||
                     ( t_mve0 == null ) )
                {
                    outStream.printf("t_col0 alloc failed.  t_col0ID = %d, " +
                            "t_mve0ID = %d\n", t_col0ID, t_mve0ID);
                }

                if ( ( f_onset0 == null ) ||
                     ( f_offset0 == null ) ||
                     ( f_cell0 == null ) )
                {
                    outStream.printf("f_cell0 alloc failed.\n");
                }

                if ( ( f_onset1 == null ) ||
                     ( f_offset1 == null ) ||
                     ( f_cell1 == null ) )
                {
                    outStream.printf("f_cell1 alloc failed.\n");
                }

                if ( ( f_onset2 == null ) ||
                     ( f_offset2 == null ) ||
                     ( f_cell2 == null ) )
                {
                    outStream.printf("f_cell2 alloc failed.\n");
                }

                if ( ( f_onset3 == null ) ||
                     ( f_offset3 == null ) ||
                     ( f_cell3 == null ) )
                {
                    outStream.printf("f_cell3 alloc failed.\n");
                }

                if ( ( f_onset4 == null ) ||
                     ( f_offset4 == null ) ||
                     ( f_cell4 == null ) )
                {
                    outStream.printf("f_cell4 alloc failed.\n");
                }

                if ( ( f_onset5 == null ) ||
                     ( f_offset5 == null ) ||
                     ( f_cell5 == null ) )
                {
                    outStream.printf("f_cell5 alloc failed.\n");
                }

                if ( ( f_onset6 == null ) ||
                     ( f_offset6 == null ) ||
                     ( f_cell6 == null ) )
                {
                    outStream.printf("f_cell6 alloc failed.\n");
                }

                if ( ( f_onset7 == null ) ||
                     ( f_offset7 == null ) ||
                     ( f_cell7 == null ) )
                {
                    outStream.printf("f_cell7 alloc failed.\n");
                }

                if ( ( f_onset8 == null ) ||
                     ( f_offset8 == null ) ||
                     ( f_cell8 == null ) )
                {
                    outStream.printf("f_cell8 alloc failed.\n");
                }

                if ( i_cell0 == null )
                {
                    outStream.printf("i_cell0 alloc failed.\n");
                }

                if ( m_cell0 == null )
                {
                    outStream.printf("m_cell0 alloc failed.\n");
                }

                if ( n_cell0 == null )
                {
                    outStream.printf("n_cell0 alloc failed.\n");
                }

                if ( p_cell0 == null )
                {
                    outStream.printf("p_cell0 alloc failed.\n");
                }

                if ( t_cell0 == null )
                {
                    outStream.printf("t_cell0 alloc failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }

                if ( f_col0.getNumCells() != 9 )
                {
                    outStream.printf(
                            "f_col0.getNumCells() = %d (9 expected).\n",
                            f_col0.getNumCells());
                }

                if ( i_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "i_col0.getNumCells() = %d (1 expected).\n",
                            i_col0.getNumCells());
                }

                if ( m_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "m_col0.getNumCells() = %d (1 expected).\n",
                            m_col0.getNumCells());
                }

                if ( m_col1.getNumCells() != 0 )
                {
                    outStream.printf(
                            "m_col1.getNumCells() = %d (0 expected).\n",
                            m_col1.getNumCells());
                }

                if ( n_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "n_col0.getNumCells() = %d (1 expected).\n",
                            n_col0.getNumCells());
                }

                if ( p_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "p_col0.getNumCells() = %d (1 expected).\n",
                            p_col0.getNumCells());
                }

                if ( t_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "t_col0.getNumCells() = %d (1 expected).\n",
                            t_col0.getNumCells());
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else {
            try {
                Object f_col0itsCells = PrivateAccessor.getField(f_col0, "itsCells");
                Object i_col0itsCells = PrivateAccessor.getField(i_col0, "itsCells");
                Object m_col0itsCells = PrivateAccessor.getField(m_col0, "itsCells");
                Object m_col1itsCells = PrivateAccessor.getField(m_col1, "itsCells");
                Object n_col0itsCells = PrivateAccessor.getField(n_col0, "itsCells");
                Object p_col0itsCells = PrivateAccessor.getField(p_col0, "itsCells");
                Object t_col0itsCells = PrivateAccessor.getField(t_col0, "itsCells");

                if ( ( f_col0itsCells == null ) ||
                      ( i_col0itsCells == null ) ||
                      ( m_col0itsCells == null ) ||
                      ( m_col1itsCells == null ) ||
                      ( n_col0itsCells == null ) ||
                      ( p_col0itsCells == null ) ||
                      ( t_col0itsCells == null ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "one or more column itsCells fields not initialized.\n");
                    }
                }
            } catch (Throwable th) {
                outStream.printf("Problem with .getField of x_col0.\n");
                failures++;
            }
        }

        if ( failures == 0 )
        {
            String f_col0_string = null;
            String f_col0_DBstring = null;
            String i_col0_string = null;
            String i_col0_DBstring = null;
            String m_col0_string = null;
            String m_col0_DBstring = null;
            String m_col1_string = null;
            String m_col1_DBstring = null;
            String n_col0_string = null;
            String n_col0_DBstring = null;
            String p_col0_string = null;
            String p_col0_DBstring = null;
            String t_col0_string = null;
            String t_col0_DBstring = null;
            String expected_f_col0_string =
                "(f_col0, ((1, 00:00:01:000, 00:00:02:000, (0.0)), " +
                          "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                          "(3, 00:00:05:000, 00:00:06:000, (2.0)), " +
                          "(4, 00:00:07:000, 00:00:08:000, (3.0)), " +
                          "(5, 00:00:09:000, 00:00:10:000, (4.0)), " +
                          "(6, 00:00:11:000, 00:00:12:000, (5.0)), " +
                          "(7, 00:00:13:000, 00:00:14:000, (6.0)), " +
                          "(8, 00:00:15:000, 00:00:16:000, (7.0)), " +
                          "(9, 00:00:15:000, 00:00:16:000, (8.0))))";
            String expected_f_col0_DBstring =
                "(DataColumn " +
                    "(name f_col0) " +
                    "(id 7) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 1) " +
                    "(itsMveType FLOAT) " +
                    "(varLen false) " +
                    "(numCells 9) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 50) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:01:000)) " +
                            "(offset (60,00:00:02:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 51) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 50) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 52) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:03:000)) " +
                            "(offset (60,00:00:04:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 53) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 52) " +
                                            "(itsValue 1.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 54) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:05:000)) " +
                            "(offset (60,00:00:06:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 55) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 54) " +
                                            "(itsValue 2.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 56) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 4) " +
                            "(onset (60,00:00:07:000)) " +
                            "(offset (60,00:00:08:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 57) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 56) " +
                                            "(itsValue 3.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 58) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 5) " +
                            "(onset (60,00:00:09:000)) " +
                            "(offset (60,00:00:10:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 59) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 58) " +
                                            "(itsValue 4.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 60) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 6) " +
                            "(onset (60,00:00:11:000)) " +
                            "(offset (60,00:00:12:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 61) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 60) " +
                                            "(itsValue 5.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 62) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 7) " +
                            "(onset (60,00:00:13:000)) " +
                            "(offset (60,00:00:14:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 63) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 62) " +
                                            "(itsValue 6.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 64) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 8) " +
                            "(onset (60,00:00:15:000)) " +
                            "(offset (60,00:00:16:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 65) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 64) " +
                                            "(itsValue 7.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 66) " +
                            "(itsColID 7) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 9) " +
                            "(onset (60,00:00:15:000)) " +
                            "(offset (60,00:00:16:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 67) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 66) " +
                                            "(itsValue 8.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))))";
            String expected_i_col0_string =
                "(i_col0, ((1, 00:00:00:000, 00:00:00:000, (0))))";
            String expected_i_col0_DBstring =
                "(DataColumn " +
                    "(name i_col0) " +
                    "(id 14) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 8) " +
                    "(itsMveType INTEGER) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 68) " +
                            "(itsColID 14) " +
                            "(itsMveID 8) " +
                            "(itsMveType INTEGER) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 8) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue " +
                                            "(id 69) " +
                                            "(itsFargID 9) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 68) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))))))";
            String expected_m_col0_string =
                "(m_col0, ((1, 00:00:00:000, 00:00:00:000, (<val>))))";
            String expected_m_col0_DBstring =
                "(DataColumn " +
                    "(name m_col0) " +
                    "(id 21) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 15) " +
                    "(itsMveType MATRIX) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 70) " +
                            "(itsColID 21) " +
                            "(itsMveID 15) " +
                            "(itsMveType MATRIX) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 15) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue " +
                                            "(id 71) " +
                                            "(itsFargID 16) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 70) " +
                                            "(itsValue <val>) " +
                                            "(subRange false))))))))))))";
            String expected_m_col1_string =
                "(m_col1, ())";
            String expected_m_col1_DBstring =
                "(DataColumn " +
                    "(name m_col1) " +
                    "(id 28) " +
                    "(hidden true) " +
                    "(readOnly false) " +
                    "(itsMveID 22) " +
                    "(itsMveType MATRIX) " +
                    "(varLen true) " +
                    "(numCells 0) " +
                    "(itsCells ())))";
            String expected_n_col0_string =
                "(n_col0, ((1, 00:00:00:000, 00:00:00:000, ())))";
            String expected_n_col0_DBstring =
                "(DataColumn " +
                    "(name n_col0) " +
                    "(id 35) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 29) " +
                    "(itsMveType NOMINAL) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 72) " +
                            "(itsColID 35) " +
                            "(itsMveID 29) " +
                            "(itsMveType NOMINAL) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 29) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((NominalDataValue " +
                                            "(id 73) " +
                                            "(itsFargID 30) " +
                                            "(itsFargType NOMINAL) " +
                                            "(itsCellID 72) " +
                                            "(itsValue <null>) " +
                                            "(subRange false))))))))))))";
            String expected_p_col0_string =
                "(p_col0, ((1, 00:00:00:000, 00:00:00:000, (()))))";
            String expected_p_col0_DBstring =
                "(DataColumn " +
                    "(name p_col0) " +
                    "(id 42) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 36) " +
                    "(itsMveType PREDICATE) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 74) " +
                            "(itsColID 42) " +
                            "(itsMveID 36) " +
                            "(itsMveType PREDICATE) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 36) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((PredDataValue " +
                                            "(id 75) " +
                                            "(itsFargID 37) " +
                                            "(itsFargType PREDICATE) " +
                                            "(itsCellID 74) " +
                                            "(itsValue ()) " +
                                            "(subRange false))))))))))))";
            String expected_t_col0_string =
                "(t_col0, ((1, 00:00:00:000, 00:00:00:000, ())))";
            String expected_t_col0_DBstring =
                "(DataColumn " +
                    "(name t_col0) " +
                    "(id 49) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 43) " +
                    "(itsMveType TEXT) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                    "((DataCell " +
                        "(id 77) " +
                        "(itsColID 49) " +
                        "(itsMveID 43) " +
                        "(itsMveType TEXT) " +
                        "(ord 1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val " +
                            "(Matrix " +
                                "(mveID 43) " +
                                "(varLen false) " +
                                "(argList " +
                                    "((TextStringDataValue " +
                                        "(id 78) " +
                                        "(itsFargID 44) " +
                                        "(itsFargType TEXT) " +
                                        "(itsCellID 77) " +
                                        "(itsValue <null>) " +
                                        "(subRange false))))))))))))";

            f_col0_string = f_col0.toString();
            f_col0_DBstring = f_col0.toDBString();

            i_col0_string = i_col0.toString();
            i_col0_DBstring = i_col0.toDBString();

            m_col0_string = m_col0.toString();
            m_col0_DBstring = m_col0.toDBString();

            m_col1_string = m_col1.toString();
            m_col1_DBstring = m_col1.toDBString();

            n_col0_string = n_col0.toString();
            n_col0_DBstring = n_col0.toDBString();

            p_col0_string = p_col0.toString();
            p_col0_DBstring = p_col0.toDBString();

            t_col0_string = t_col0.toString();
            t_col0_DBstring = t_col0.toDBString();

            if ( expected_f_col0_string.compareTo(f_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_col0.toString(): \"%s\".\n",
                                     f_col0.toString());
                }
            }

            if ( expected_f_col0_DBstring.compareTo(f_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_col0.toDBString(): \"%s\".\n",
                                     f_col0.toDBString());
                }
            }


            if ( expected_i_col0_string.compareTo(i_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_col0.toString(): \"%s\".\n",
                                     i_col0.toString());
                }
            }

            if ( expected_i_col0_DBstring.compareTo(i_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_col0.toDBString(): \"%s\".\n",
                                     i_col0.toDBString());
                }
            }


            if ( expected_m_col0_string.compareTo(m_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_col0.toString(): \"%s\".\n",
                                     m_col0.toString());
                }
            }

            if ( expected_m_col0_DBstring.compareTo(m_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_col0.toDBString(): \"%s\".\n",
                                     m_col0.toDBString());
                }
            }


            if ( expected_m_col1_string.compareTo(m_col1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_col1.toString(): \"%s\".\n",
                                     m_col1.toString());
                }
            }

            if ( expected_m_col1_DBstring.compareTo(m_col1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_col1.toDBString(): \"%s\".\n",
                                     m_col1.toDBString());
                }
            }


            if ( expected_n_col0_string.compareTo(n_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_col0.toString(): \"%s\".\n",
                                     n_col0.toString());
                }
            }

            if ( expected_n_col0_DBstring.compareTo(n_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_col0.toDBString(): \"%s\".\n",
                                     n_col0.toDBString());
                }
            }


            if ( expected_p_col0_string.compareTo(p_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_col0.toString(): \"%s\".\n",
                                     p_col0.toString());
                }
            }

            if ( expected_p_col0_DBstring.compareTo(p_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_col0.toDBString(): \"%s\".\n",
                                     p_col0.toDBString());
                }
            }


            if ( expected_t_col0_string.compareTo(t_col0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_col0.toString(): \"%s\".\n",
                                     t_col0.toString());
                }
            }

            if ( expected_t_col0_DBstring.compareTo(t_col0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_col0.toDBString(): \"%s\".\n",
                                     t_col0.toDBString());
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

        return pass;

    } /* DataCell::TestToStringMethods() */

    /**
     * VerifyDataColumnCopy()
     *
     * Verify that the supplied instances of DataColumn are distinct, that they
     * contain no common references (other than db), and that with the exception
     * of the itsCells field, they have the same value (Recall that the copy
     * construtor for DataColumn specifically does not copy itsCells from the
     * base instance.  Instead itsCells in the copy is always set to null,
     * regardless of the value of itsCells in the base instance).
     *
     *                                              JRM -- 12/30/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyDataColumnCopy(DataColumn base,
                                           DataColumn copy,
                                           java.io.PrintStream outStream,
                                           boolean verbose,
                                           String baseDesc,
                                           String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDataColumnCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDataColumnCopy: %s null on entry.\n",
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

        failures += ColumnTest.VerifyColumnCopy((Column)base, (Column)copy,
                                            outStream, verbose,
                                            baseDesc, copyDesc);

        try {
            Object copyitsCells = PrivateAccessor.getField(copy, "itsCells");
            Object baseitsMveID = PrivateAccessor.getField(base, "itsMveID");
            Object copyitsMveID = PrivateAccessor.getField(copy, "itsMveID");
            Object baseitsMveType = PrivateAccessor.getField(base, "itsMveType");
            Object copyitsMveType = PrivateAccessor.getField(copy, "itsMveType");
            Object basevarLen = PrivateAccessor.getField(base, "varLen");
            Object copyvarLen = PrivateAccessor.getField(copy, "varLen");

            if ( copyitsCells != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.itsCells != null.\n", copyDesc);
                }
            }

            if ( !baseitsMveID.equals(copyitsMveID) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.itsMveID == %s != %s.itsMveID == %s.\n",
                                     baseDesc, baseitsMveID,
                                     copyDesc, copyitsMveID);
                }
            }

            if ( !baseitsMveType.equals(copyitsMveType) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.itsMveType == %s != %s.itsMveType == %s.\n",
                                     baseDesc, baseitsMveType.toString(),
                                     copyDesc, copyitsMveType.toString());
                }
            }

            if ( !basevarLen.equals(copyvarLen) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.varLen == %s != %s.itsMveID == %s.\n",
                                     baseDesc, basevarLen,
                                     copyDesc, copyvarLen);
                }
            }
        } catch (Throwable th) {
            outStream.printf("Problem with .getField of copy or base.\n");
            failures++;
        }
        return failures;

    } /* DataColumn::VerifyDataColumnCopy() */

}
