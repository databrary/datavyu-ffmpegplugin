package au.com.nicta.openshapa.db;

import java.util.Vector;
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
        assertTrue(f_col.db == db0);
        assertTrue(f_col.hidden == false);
        assertTrue(f_col.readOnly == false);
        assertTrue(f_col.numCells == 0);
        assertTrue(f_col.name.equals("f_col"));
        assertTrue(f_col.getItsCells() == null);
        assertTrue(f_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .FLOAT);

        assertTrue(i_col != null);
        assertTrue(i_col.db == db0);
        assertTrue(i_col.hidden == false);
        assertTrue(i_col.readOnly == false);
        assertTrue(i_col.numCells == 0);
        assertTrue(i_col.name.equals("i_col"));
        assertTrue(i_col.getItsCells() == null);
        assertTrue(i_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .INTEGER);

        assertTrue(m_col != null);
        assertTrue(m_col.db == db0);
        assertTrue(m_col.hidden == false);
        assertTrue(m_col.readOnly == false);
        assertTrue(m_col.numCells == 0);
        assertTrue(m_col.name.equals("m_col"));
        assertTrue(m_col.getItsCells() == null);
        assertTrue(m_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .MATRIX);

        assertTrue(n_col != null);
        assertTrue(n_col.db == db0);
        assertTrue(n_col.hidden == false);
        assertTrue(n_col.readOnly == false);
        assertTrue(n_col.numCells == 0);
        assertTrue(n_col.name.equals("n_col"));
        assertTrue(n_col.getItsCells() == null);
        assertTrue(n_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .NOMINAL);

        assertTrue(p_col != null);
        assertTrue(p_col.db == db0);
        assertTrue(p_col.hidden == false);
        assertTrue(p_col.readOnly == false);
        assertTrue(p_col.numCells == 0);
        assertTrue(p_col.name.equals("p_col"));
        assertTrue(p_col.getItsCells() == null);
        assertTrue(p_col.getItsMveType() == MatrixVocabElement.MatrixType
                                                              .PREDICATE);

        assertTrue(t_col != null);
        assertTrue(t_col.db == db0);
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
        assertTrue(f_col0.db == db);
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
        assertTrue(f_col1.db == db);
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
        assertTrue(i_col0.db == db);
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
        assertTrue(i_col1.db == db);
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
        assertTrue(m_col0.db == db);
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
        assertTrue(m_col1.db == db);
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
        assertTrue(n_col0.db == db);
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
        assertTrue(n_col1.db == db);
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
        assertTrue(p_col0.db == db);
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
        assertTrue(p_col1.db == db);
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
        assertTrue(t_col0.db == db);
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
        assertTrue(t_col1.db == db);
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
                                        f_mve0.id,
                                        f_onset0, f_offset0, f_matrix0);

        // Build the second data cell.
        TimeStamp f_onset1 = new TimeStamp(db.getTicks(), 180);
        TimeStamp f_offset1 = new TimeStamp(db.getTicks(), 240);
        Vector<DataValue> f_arg_list1 = new Vector<DataValue>();
        arg = new FloatDataValue(db, fargID, 1.0);
        f_arg_list1.add(arg);
        Matrix f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
        DataCell f_cell1 = new DataCell(db, "f_cell1", f_col0ID,
                                        f_mve0.id,
                                        f_onset1, f_offset1, f_matrix1);

        // Build the third data cell
        TimeStamp f_onset2 = new TimeStamp(db.getTicks(), 300);
        TimeStamp f_offset2 = new TimeStamp(db.getTicks(), 360);
        Vector<DataValue> f_arg_list2 = new Vector<DataValue>();
        arg = new FloatDataValue(db, fargID, 2.0);
        f_arg_list2.add(arg);
        Matrix f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
        DataCell f_cell2 = new DataCell(db, "f_cell2", f_col0ID,
                                        f_mve0.id,
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
                                            f_mve0.id,
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
}
